package com.example.roadbuddy.Fragment

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.location.Location
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.roadbuddy.databinding.FragmentServiceBinding
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.android.gms.location.LocationServices
import com.google.android.gms.tasks.OnSuccessListener

class ServiceFragment : Fragment() {

    private var _binding: FragmentServiceBinding? = null
    private val binding get() = _binding!!

    private lateinit var db: FirebaseFirestore
    private lateinit var auth: FirebaseAuth
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private var userLocation: Location? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        db = Firebase.firestore
        auth = FirebaseAuth.getInstance() // Initialize Firebase Auth
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentServiceBinding.inflate(inflater, container, false)

        binding.skipbtn.setOnClickListener {
            binding.requestloading.visibility = View.GONE
        }
        // Add click listeners for service buttons
        binding.flatTyreBtn.setOnClickListener {
            showConfirmationDialog("Flat Tyres")
        }
        binding.fuelBtn.setOnClickListener {
            showConfirmationDialog("Fuel Emergency")
        }
        binding.batteryJumpBtn.setOnClickListener {
            showConfirmationDialog("Battery Jumpstart")
        }
        binding.startIssueBtn.setOnClickListener {
            showConfirmationDialog("Start Issue")
        }
        binding.keyProblemBtn.setOnClickListener {
            showConfirmationDialog("Key Problem")
        }
        binding.moderateProblemBtn.setOnClickListener {
            showConfirmationDialog("Moderate Problem")
        }
        binding.flatTowBtn.setOnClickListener {
            showConfirmationDialog("Flat Tow")
        }
        binding.towBtn.setOnClickListener {
            showConfirmationDialog("Tow")
        }
        binding.changeTyreBtn.setOnClickListener {
            showConfirmationDialog("Change Tyre")
        }
        binding.lubricantBtn.setOnClickListener {
            showConfirmationDialog("Lubricant Issue")
        }
        binding.lightChangeBtn.setOnClickListener {
            showConfirmationDialog("Light Change")
        }
        binding.interPartBtn.setOnClickListener {
            showConfirmationDialog("Internal Part Issue")
        }
        binding.hornBtn.setOnClickListener {
            showConfirmationDialog("Horn Issue")
        }
        binding.bookingBtn.setOnClickListener {
            showConfirmationDialog("Booking")
        }

        return binding.root
    }

    // Method to display confirmation dialog
    private fun showConfirmationDialog(service: String) {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Confirm Request")
        builder.setMessage("Are you sure you want to help for: $service?")

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
                                        binding.requestloading.visibility = View.VISIBLE
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
}
