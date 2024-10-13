package com.example.tttxtreme_android

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.tttxtreme_android.databinding.ActivityUserLandingBinding
import com.google.android.material.bottomnavigation.BottomNavigationMenuView
import com.google.android.material.bottomnavigation.BottomNavigationItemView

class UserLandingActivity : AppCompatActivity() {

    private lateinit var binding: ActivityUserLandingBinding
    private lateinit var customViews: MutableList<View>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Initialize the binding
        binding = ActivityUserLandingBinding.inflate(layoutInflater)

        // Set the content view using binding
        setContentView(binding.root)

        // Start with the PlayFragment
        replaceFragment(PlayFragment())

        // Setup custom menu items for BottomNavigationView
        customizeBottomNavigationView()

        // Set up the BottomNavigationView listener with color change for active item
        binding.bottomNavigationView.setOnItemSelectedListener { menuItem ->
            updateActiveMenuItem(menuItem.itemId)
            when (menuItem.itemId) {
                R.id.play -> replaceFragment(PlayFragment())
                R.id.scoreboard -> replaceFragment(ScoreboardFragment())
                R.id.friends -> replaceFragment(FriendsFragment())
                R.id.profile -> replaceFragment(ProfileFragment())
                else -> {}
            }
            true
        }
    }

    @SuppressLint("RestrictedApi")
    private fun customizeBottomNavigationView() {
        val bottomNavigationView = binding.bottomNavigationView
        customViews = mutableListOf()  // Store custom views for easy access

        // Loop through each menu item to set the custom layout
        for (i in 0 until bottomNavigationView.menu.size()) {
            val item = bottomNavigationView.menu.getItem(i)

            // Find the BottomNavigationMenuView and set custom view for each item
            val menuView = bottomNavigationView.getChildAt(0) as BottomNavigationMenuView
            val itemView = menuView.getChildAt(i) as BottomNavigationItemView

            // Inflate the custom layout
            val customView: View = LayoutInflater.from(this).inflate(R.layout.custom_menu_item, menuView, false)

            // Store custom views for reference
            customViews.add(customView)

            // Set the custom icon and label for the item
            val icon = customView.findViewById<ImageView>(R.id.icon)
            icon.setImageDrawable(item.icon)

            val label = customView.findViewById<TextView>(R.id.label)
            label.text = item.title

            // Replace the original view with the custom one
            itemView.removeAllViews()
            itemView.addView(customView)
        }

        // Set the first item as active initially
        updateActiveMenuItem(R.id.play)
    }

    private fun updateActiveMenuItem(selectedItemId: Int) {
        // Loop through each custom view and update its state
        for (i in 0 until customViews.size) {
            val customView = customViews[i]
            val frameLayout = customView.findViewById<FrameLayout>(R.id.customFrameLayout)  // Correctly referencing the FrameLayout

            // Check if this is the selected item
            val itemId = binding.bottomNavigationView.menu.getItem(i).itemId
            if (itemId == selectedItemId) {
                // Set active background
                frameLayout.setBackgroundResource(R.drawable.nav_item_active_background)  // Active background
            } else {
                // Set default background
                frameLayout.setBackgroundResource(R.drawable.nav_item_default_background)  // Default background
            }
        }
    }

    private fun replaceFragment(fragment: Fragment) {
        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.user_landing_frame_layout, fragment)
        fragmentTransaction.commit()
    }
}