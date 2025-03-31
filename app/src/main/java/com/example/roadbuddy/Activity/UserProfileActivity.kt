package com.example.roadbuddy.Activity

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.roadbuddy.R
import com.example.roadbuddy.databinding.ActivityUserProfileBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class UserProfileActivity : AppCompatActivity() {
    private lateinit var binding: ActivityUserProfileBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore
    private var isEditMode = false
    private var currentRole: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUserProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        // Get current user ID
        val userId = auth.currentUser?.uid

        if (userId != null) {
            binding.progressBar.visibility = View.VISIBLE
            // Fetch user data from Firestore
            db.collection("users").document(userId).get()
                .addOnSuccessListener { document ->
                    binding.progressBar.visibility = View.GONE
                    if (document != null) {
                        // Basic Information
                        val userName = document.getString("name")
                        val userEmail = document.getString("email")
                        val userPhone = document.getString("phone")
                        currentRole = document.getString("role")

                        // Additional Information
                        val userAddress = document.getString("address")
                        val userVehicle = document.getString("vehicle")
                        val userEmergencyContact = document.getString("emergencyContact")
                        val userBloodGroup = document.getString("bloodGroup")

                        // Update UI with user data
                        binding.userNameText.setText(userName)
                        binding.userEmailText.setText(userEmail)
                        binding.userPhoneText.setText(userPhone)
                        binding.userRoleText.setText(currentRole)

                        // Update additional information
                        binding.userAddressText.setText(userAddress)
                        binding.userVehicleText.setText(userVehicle)
                        binding.userEmergencyContactText.setText(userEmergencyContact)
                        binding.userBloodGroupText.setText(userBloodGroup)
                    } else {
                        Toast.makeText(this, "User data not found", Toast.LENGTH_SHORT).show()
                    }
                }
                .addOnFailureListener { exception ->
                    binding.progressBar.visibility = View.GONE
                    Toast.makeText(this, "Error fetching data: ${exception.message}", Toast.LENGTH_SHORT).show()
                }
        } else {
            Toast.makeText(this, "User is not logged in", Toast.LENGTH_SHORT).show()
        }

        // Back button click listener
        binding.backButton.setOnClickListener {
            finish()
        }

        // Edit button click listener
        binding.editButton.setOnClickListener {
            toggleEditMode()
        }

        // Save button click listener
        binding.saveButton.setOnClickListener {
            saveUserData()
        }
    }

    private fun toggleEditMode() {
        isEditMode = !isEditMode
        
        // Enable/disable EditText fields (except role)
        binding.userNameText.isEnabled = isEditMode
        binding.userEmailText.isEnabled = isEditMode
        binding.userPhoneText.isEnabled = isEditMode
        binding.userRoleText.isEnabled = false // Role is never editable
        
        // Enable/disable additional information fields
        binding.userAddressText.isEnabled = isEditMode
        binding.userVehicleText.isEnabled = isEditMode
        binding.userEmergencyContactText.isEnabled = isEditMode
        binding.userBloodGroupText.isEnabled = isEditMode
        
        // Show/hide save button
        binding.saveButton.visibility = if (isEditMode) View.VISIBLE else View.GONE
        
        // Change edit button icon
        binding.editButton.setImageResource(
            if (isEditMode) android.R.drawable.ic_menu_close_clear_cancel
            else R.drawable.ic_edit
        )
    }

    private fun saveUserData() {
        val userId = auth.currentUser?.uid ?: return
        
        binding.progressBar.visibility = View.VISIBLE
        
        val updatedData = mapOf<String, Any>(
            // Basic Information
            "name" to binding.userNameText.text.toString(),
            "email" to binding.userEmailText.text.toString(),
            "phone" to binding.userPhoneText.text.toString(),
            
            // Additional Information
            "address" to binding.userAddressText.text.toString(),
            "vehicle" to binding.userVehicleText.text.toString(),
            "emergencyContact" to binding.userEmergencyContactText.text.toString(),
            "bloodGroup" to binding.userBloodGroupText.text.toString()
            // Role is not included in the update
        )

        db.collection("users").document(userId)
            .update(updatedData)
            .addOnSuccessListener {
                binding.progressBar.visibility = View.GONE
                Toast.makeText(this, "Profile updated successfully", Toast.LENGTH_SHORT).show()
                toggleEditMode()
            }
            .addOnFailureListener { exception ->
                binding.progressBar.visibility = View.GONE
                Toast.makeText(this, "Error updating profile: ${exception.message}", Toast.LENGTH_SHORT).show()
            }
    }
} 