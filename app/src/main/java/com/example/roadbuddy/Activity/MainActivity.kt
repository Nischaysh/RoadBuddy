package com.example.roadbuddy.Activity

import android.os.Bundle
import android.view.View
import android.widget.ImageView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.example.roadbuddy.Fragment.ChatlistFragment
import com.example.roadbuddy.Fragment.HomeFragment
import com.example.roadbuddy.Fragment.ProfileFragment
import com.example.roadbuddy.Fragment.ServiceFragment
import com.example.roadbuddy.R
import com.google.firebase.auth.FirebaseAuth

class MainActivity : AppCompatActivity() {
    private lateinit var fragmentManager: FragmentManager
    private lateinit var auth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        auth = FirebaseAuth.getInstance()
        val rootView = findViewById<View>(R.id.fragmentContainer)

        // Handling insets to prevent overlapping with navigation bar
        ViewCompat.setOnApplyWindowInsetsListener(rootView) { view, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            // Apply padding to avoid being covered by the navigation bar
            view.updatePadding(bottom = systemBars.bottom)
            insets
        }



        val homeBtn = findViewById<ImageView>(R.id.home)
        val shopBtn = findViewById<ImageView>(R.id.shop)
        val serviceBtn = findViewById<ImageView>(R.id.service)
        val profileBtn = findViewById<ImageView>(R.id.profile)

        homeBtn.setColorFilter(getColor(R.color.red))


        homeBtn.setOnClickListener{
            goToFragment(HomeFragment())
            homeBtn.setColorFilter(getColor(R.color.red))
            serviceBtn.setColorFilter(getColor(R.color.black))
            shopBtn.setColorFilter(getColor(R.color.black))
            profileBtn.setColorFilter(getColor(R.color.black))
        }
        serviceBtn.setOnClickListener{
            goToFragment(ServiceFragment())
            homeBtn.setColorFilter(getColor(R.color.black))
            serviceBtn.setColorFilter(getColor(R.color.red))
            shopBtn.setColorFilter(getColor(R.color.black))
            profileBtn.setColorFilter(getColor(R.color.black))
        }
        shopBtn.setOnClickListener{
            goToFragment(ChatlistFragment())
            homeBtn.setColorFilter(getColor(R.color.black))
            serviceBtn.setColorFilter(getColor(R.color.black))
            shopBtn.setColorFilter(getColor(R.color.red))
            profileBtn.setColorFilter(getColor(R.color.black))
        }
        profileBtn.setOnClickListener{
            goToFragment(ProfileFragment())
            homeBtn.setColorFilter(getColor(R.color.black))
            serviceBtn.setColorFilter(getColor(R.color.black))
            shopBtn.setColorFilter(getColor(R.color.black))
            profileBtn.setColorFilter(getColor(R.color.red))
        }
    }

    private  fun goToFragment(fragment: Fragment){
        fragmentManager = supportFragmentManager
        fragmentManager.beginTransaction().replace(R.id.fragmentContainer,fragment).commit()
    }
}