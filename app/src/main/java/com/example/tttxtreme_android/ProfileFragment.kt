package com.example.tttxtreme_android

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class ProfileFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_profile, container, false)

        // Reference the TextView in the inflated layout
        val textView = view.findViewById<TextView>(R.id.name)

        // Get the current Firebase user
        val auth = Firebase.auth
        val user = auth.currentUser

        // If the user is logged in, set their name in the TextView
        if (user != null) {
            val userName = user.displayName
            textView.text = "Welcome, $userName"
        }

        // Reference the logout button
        val logoutButton: Button = view.findViewById(R.id.btnLogout)

        // Set onClickListener for the logout button
        logoutButton.setOnClickListener {
            FirebaseAuth.getInstance().signOut() // Sign out from Firebase

            // Navigate back to the SignInActivity
            val intent = Intent(activity, SignInActivity::class.java)
            startActivity(intent)

            // Finish the current activity to prevent going back
            activity?.finish()

            Toast.makeText(activity, "Logged out successfully", Toast.LENGTH_SHORT).show()
        }

        return view
    }
}