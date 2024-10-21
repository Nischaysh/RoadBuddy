package com.example.roadbuddy.Activity

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.roadbuddy.databinding.ActivityLoginBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class LoginActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityLoginBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(binding.root)

        // Initialize Firebase Auth and Firestore
        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        binding.button2.setOnClickListener {
            val email = binding.editTextPhone.text.toString().trim()
            val password = binding.editTextTextPassword2.text.toString().trim()

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
            } else {
                // Show progress bar when login starts
                binding.Loading.visibility = View.VISIBLE

                // Attempt to login the user
                auth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            // Get the current user's ID
                            val userId = auth.currentUser?.uid

                            // Fetch user data from Firestore to check role
                            if (userId != null) {
                                db.collection("users").document(userId).get()
                                    .addOnSuccessListener { document ->
                                        if (document != null) {
                                            val role = document.getString("role")

                                            // Check if the role is "user"
                                            if (role == "Owner") {
                                                binding.Loading.visibility = View.GONE
                                                // Proceed to MainActivity if the role is "user"
                                                val intent = Intent(this, MainActivity::class.java)
                                                startActivity(intent)
                                                finish()
                                            } else {
                                                // If not a "user", show error and prevent login
                                                binding.Loading.visibility = View.GONE
                                                Toast.makeText(this, "Access denied: Only users can log in.", Toast.LENGTH_SHORT).show()
                                                auth.signOut() // Log out the helper if logged in
                                            }
                                        }
                                    }
                                    .addOnFailureListener { exception ->
                                        binding.Loading.visibility = View.GONE
                                        Toast.makeText(this, "Error fetching data: ${exception.message}", Toast.LENGTH_SHORT).show()
                                    }
                            }
                        } else {
                            binding.Loading.visibility = View.GONE
                            Toast.makeText(this, "Login failed: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                        }
                    }
            }
        }
    }
}
