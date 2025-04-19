    package com.example.roadbuddy.Activity

    import android.content.Intent
    import android.os.Bundle
    import android.view.View
    import android.widget.Toast
    import androidx.activity.enableEdgeToEdge
    import androidx.appcompat.app.AppCompatActivity
    import com.example.roadbuddy.databinding.ActivitySigninBinding
    import com.google.firebase.auth.FirebaseAuth
    import com.google.firebase.auth.FirebaseUser
    import com.google.firebase.auth.FirebaseUserMetadata
    import com.google.firebase.firestore.FirebaseFirestore

    class SigninActivity : AppCompatActivity() {
        private lateinit var auth: FirebaseAuth
        private lateinit var db: FirebaseFirestore

        override fun onStart() {
            super.onStart()
            val currentUser : FirebaseUser? = auth.currentUser
            if(currentUser != null){
                startActivity(Intent(this , MainActivity::class.java))
            }
        }

        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            val binding = ActivitySigninBinding.inflate(layoutInflater)
            enableEdgeToEdge()
            setContentView(binding.root)

            // Initialize Firebase Auth and Firestore
            auth = FirebaseAuth.getInstance()
            db = FirebaseFirestore.getInstance()

            binding.button.setOnClickListener {
                val email = binding.editTextTextEmail.text.toString().trim()
                val name = binding.editTextTextName.text.toString().trim()
                val phone = binding.editTextPhone.text.toString().trim()
                val password = binding.editTextTextPassword3.text.toString().trim()

                if (email.isEmpty() || phone.isEmpty() || password.isEmpty() || name.isEmpty()) {
                    Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
                } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    Toast.makeText(this, "Invalid email format", Toast.LENGTH_SHORT).show()
                } else if (password.length < 6) {
                    Toast.makeText(this, "Password must be at least 6 characters", Toast.LENGTH_SHORT).show()
                } else {
                    binding.Loading.visibility = View.VISIBLE
                    // Register the user with email and password
                    auth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                // User successfully registered
                                val userId = auth.currentUser?.uid

                                // Create a user data map
                                val userData = hashMapOf(
                                    "email" to email,
                                    "name"  to name,
                                    "phone" to phone,
                                    "userId" to userId,
                                    "role" to "Owner"
                                )

                                // Store user data in Firestore under "users" collection
                                if (userId != null) {
                                    db.collection("users").document(userId)
                                        .set(userData)
                                        .addOnSuccessListener {
                                            binding.Loading.visibility = View.GONE
                                            Toast.makeText(this, "User data stored successfully", Toast.LENGTH_SHORT).show()
                                            startActivity(Intent(this, MainActivity::class.java))
                                            finish()
                                        }
                                        .addOnFailureListener {
                                            binding.Loading.visibility = View.GONE
                                            Toast.makeText(this, "Failed to store user data: ${it.message}", Toast.LENGTH_SHORT).show()
                                        }
                                }
                            } else {
                                binding.Loading.visibility = View.GONE
                                // Registration failed, show error message
                                task.exception?.message?.let { errorMessage ->
                                    Toast.makeText(this, "Registration failed: $errorMessage", Toast.LENGTH_LONG).show()
                                }
                            }
                        }
                }
            }

            binding.textView7.setOnClickListener{
                startActivity(Intent(this , LoginActivity::class.java))
            }
        }
    }