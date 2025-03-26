package com.example.roadbuddy.Fragment

import Request
import RequestAdapter
import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.transition.Visibility
import com.example.roadbuddy.Activity.MainActivity
import com.example.roadbuddy.Activity.SigninActivity
import com.example.roadbuddy.R
import com.example.roadbuddy.databinding.FragmentHomeBinding
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.tasks.OnSuccessListener
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query

class HomeFragment : Fragment(), OnMapReadyCallback {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private lateinit var db: FirebaseFirestore
    private lateinit var auth: FirebaseAuth
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var googleMap: GoogleMap
    private var userLocation: Location? = null
    private lateinit var bottomSheetBehavior: BottomSheetBehavior<LinearLayout>
    private lateinit var requestAdapter: RequestAdapter  // Adapter for RecyclerView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize Firebase Firestore and Auth
        db = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()

        // Initialize location client
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)



        // Initialize BottomSheetBehavior
        bottomSheetBehavior = BottomSheetBehavior.from(binding.bottomSheet)

        // Set bottom sheet to start at 1/4 of the screen and allow full expansion
        bottomSheetBehavior.peekHeight = resources.displayMetrics.heightPixels / 3
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED

        bottomSheetBehavior.addBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                when (newState) {
                    BottomSheetBehavior.STATE_EXPANDED -> { /* Fully Expanded */ }
                    BottomSheetBehavior.STATE_COLLAPSED -> { /* Collapsed to 1/4 screen */ }
                }
            }
            override fun onSlide(bottomSheet: View, slideOffset: Float) { /* Handle Sliding */ }
        })
        // Get the current user's ID
        val userId = auth.currentUser?.uid

        if (userId != null) {
            binding.Loading.visibility = View.VISIBLE
            // Fetch user data from Firestore
            db.collection("users").document(userId).get()
                .addOnSuccessListener { document ->
                    binding.Loading.visibility = View.GONE
                    if (document != null) {
                        // Get the user's name
                        val userName = document.getString("name")

                        // Display the name in a TextView
                        if (userName != null) {
                            binding.textView12.text = "Welcome " + userName.toUpperCase()
                        } else {
                            Toast.makeText(context, "Name not found", Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        Toast.makeText(context, "User not found", Toast.LENGTH_SHORT).show()
                    }
                }
                .addOnFailureListener { exception ->
                    binding.Loading.visibility = View.GONE
                    Toast.makeText(context, "Error fetching data: ${exception.message}", Toast.LENGTH_SHORT).show()
                }
        } else {
            Toast.makeText(context, "User is not logged in", Toast.LENGTH_SHORT).show()
        }

        // Get the map fragment and notify when it's ready
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        // Set up RecyclerView
        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        requestAdapter = RequestAdapter(listOf()) // Initialize empty adapter
        binding.recyclerView.adapter = requestAdapter

        // Fetch and display user requests
        fetchUserRequests()

        binding.skipbtn.setOnClickListener {
            binding.requestloading.visibility = View.GONE
        }


        binding.accidentscase.setOnClickListener {
            showConfirmationDialog("Accidental Help")
        }
        binding.emergencycab.setOnClickListener {
            showConfirmationDialog("Emergency cab")
        }

        return binding.root
    }

    override fun onMapReady(map: GoogleMap) {
        googleMap = map


        // Check for location permissions
        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED ||
            ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            googleMap.isMyLocationEnabled = true
            getUserLocation()
        } else {
            requestPermissions(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ),
                1000
            )
        }
    }

    private fun getUserLocation() {
        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }

        fusedLocationClient.lastLocation
            .addOnSuccessListener { location: Location? ->
                if (location != null) {
                    val userLatLng = LatLng(location.latitude, location.longitude)
//                    googleMap.addMarker(MarkerOptions().position(userLatLng).title("You are here"))
                    googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userLatLng, 16f))

                } else {
                    // Handle when location is null (could be due to disabled GPS)
                    Toast.makeText(requireContext(), "Unable to fetch location. Please enable GPS.", Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener {
                Toast.makeText(requireContext(), "Unable to fetch location", Toast.LENGTH_SHORT).show()
            }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (requestCode == 1000 && grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            getUserLocation()
        } else {
            Toast.makeText(requireContext(), "Location permission denied", Toast.LENGTH_SHORT).show()
        }
    }

    private fun fetchUserRequests() {
        val userId = auth.currentUser?.uid ?: return

        db.collection("requests")
            .whereEqualTo("userId", userId)
            .orderBy("timestamp", Query.Direction.DESCENDING) // Order by newest first
            .addSnapshotListener { documents, error -> // Listen for real-time updates
                if (error != null) {
                    Toast.makeText(requireContext(), "Error fetching requests: ${error.message}", Toast.LENGTH_SHORT).show()
                    return@addSnapshotListener
                }

                if (documents != null) {
                    val requests = documents.map { doc ->
                        val service = doc.getString("service") ?: "Unknown"
                        val helpername = doc.getString("helperName") ?: "Not assign"
                        val status = doc.getString("status") ?: "pending"
                        Request(service, status, helpername) // Map to Request data model
                    }
                    requestAdapter.updateRequests(requests) // Automatically updates UI
                }
            }
    }

    // Method to display confirmation dialog
    private fun showConfirmationDialog(service: String) {
        val builder = android.app.AlertDialog.Builder(requireContext())
        builder.setTitle("Confirm Request")
        builder.setMessage("Urgent need for: $service?")

        builder.setPositiveButton("Confirm") { dialog, _ ->
            sendRequest(service)
            dialog.dismiss() // Close the dialog after confirming
        }

        builder.setNegativeButton("Cancel") { dialog, _ ->
            dialog.dismiss() // Close the dialog if the user cancels
        }

        val alertDialog = builder.create()
        alertDialog.show()
    }

    @SuppressLint("MissingPermission")
    private fun sendRequest(service: String) {
        val userId = auth.currentUser?.uid

        if (userId != null) {
            fusedLocationClient.lastLocation.addOnSuccessListener(requireActivity(), OnSuccessListener { location ->
                if (location != null) {
                    // Fetch the user's name from Firestore
                    db.collection("users").document(userId).get()
                        .addOnSuccessListener { document ->
                            if (document != null) {
                                val userName = document.getString("name") ?: "Unknown User"
                                val email = document.getString("email") ?: "Unknown User"
                                val phone = document.getString("phone") ?: "Unknown User"
                                val userid = document.getString("userId") ?: "Unknown User"
                                userLocation = location

                                val requestData = hashMapOf(
                                    "service" to service,
                                    "userId" to userid,
                                    "latitude" to userLocation?.latitude,
                                    "longitude" to userLocation?.longitude,
                                    "timestamp" to System.currentTimeMillis(),
                                    "username" to userName,
                                    "email" to email,
                                    "phone" to phone,
                                    "status" to "pending"
                                )

                                // Save the request to Firestore
                                db.collection("requests")
                                    .add(requestData)
                                    .addOnSuccessListener {
                                        Toast.makeText(requireContext(), "Request sent successfully!", Toast.LENGTH_SHORT).show()
                                        binding.requestloading.visibility  = View.VISIBLE
                                        Handler(Looper.getMainLooper()).postDelayed({
                                            binding.requestloading.visibility = View.GONE
                                        },300000)
                                    }
                                    .addOnFailureListener { e ->
                                        Toast.makeText(requireContext(), "Error sending request: ${e.message}", Toast.LENGTH_SHORT).show()
                                    }
                            }
                        }

                } else {
                    Toast.makeText(requireContext(), "Unable to get location.", Toast.LENGTH_SHORT).show()
                }
            })
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

