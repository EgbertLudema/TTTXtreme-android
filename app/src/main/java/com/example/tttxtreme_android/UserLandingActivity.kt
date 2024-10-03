package com.example.tttxtreme_android

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.tttxtreme_android.databinding.ActivityUserLandingBinding

class UserLandingActivity : AppCompatActivity() {

    private lateinit var binding: ActivityUserLandingBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Initialize the binding
        binding = ActivityUserLandingBinding.inflate(layoutInflater)

        // Set the content view using binding
        setContentView(binding.root)

        // Start with the PlayFragment
        replaceFragment(PlayFragment())

        // Set up the BottomNavigationView listener
        binding.bottomNavigationView.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.play -> replaceFragment(PlayFragment())
                R.id.scoreboard-> replaceFragment(ScoreboardFragment())
                R.id.friends -> replaceFragment(FriendsFragment())
                R.id.profile -> replaceFragment(ProfileFragment())
                else -> {}
            }
            true
        }
    }

    private fun replaceFragment(fragment: Fragment) {
        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.user_landing_frame_layout, fragment)
        fragmentTransaction.commit()
    }
}