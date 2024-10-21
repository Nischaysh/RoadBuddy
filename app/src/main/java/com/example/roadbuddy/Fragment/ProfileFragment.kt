package com.example.roadbuddy.Fragment

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.example.roadbuddy.Activity.SigninActivity
import com.example.roadbuddy.Activity.SplashscreenActivity
import com.example.roadbuddy.R
import com.example.roadbuddy.databinding.FragmentHomeBinding
import com.example.roadbuddy.databinding.FragmentProfileBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.util.Locale

class ProfileFragment : Fragment() {

    private  lateinit var binding: FragmentProfileBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var db : FirebaseFirestore


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentProfileBinding.inflate(inflater, container, false)

        binding.logoutBtn.setOnClickListener{
                auth.signOut()
                startActivity(Intent(context , SplashscreenActivity::class.java))
        }

        val UserId = auth.currentUser?.uid
        if (UserId != null) {
            db.collection("users").document(UserId).get()
                .addOnSuccessListener { document ->
                    if (document != null) {
                        val userName = document.getString("name")!!.toUpperCase(Locale.ROOT)
                        val userRole = document.getString("role")
                        val userEmail = document.getString("email")
                        val userPhone = document.getString("phone")

                        if (userName != null||userRole != null||userPhone != null || userEmail!=null) {

                            binding.nameText.text = userName
                            binding.Name.text = userName
                            binding.roleText.text = userRole
                            binding.Email.text = userEmail
                            binding.Phone.text = userPhone

                        }
                    }
                    }
                }


        binding.profileBannerBtn.setOnClickListener{
            binding.profileBanner.visibility = View.VISIBLE
        }
        binding.aboutusBtn.setOnClickListener{
            binding.aboutus.visibility = View.VISIBLE
        }


        return binding.root
    }

}