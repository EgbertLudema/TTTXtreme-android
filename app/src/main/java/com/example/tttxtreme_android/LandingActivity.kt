package com.example.tttxtreme_android

import android.content.Intent // Import Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import android.widget.Button

class LandingActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_landing)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Add click listeners for the buttons
        findViewById<Button>(R.id.btnLogin).setOnClickListener {
            // Create an Intent to start the SignIn activity
            val intent = Intent(this, SignInActivity::class.java)
            startActivity(intent) // Start the SignIn activity
        }

        findViewById<Button>(R.id.btnRegister).setOnClickListener {
            // Create an Intent to start the SignUp activity
            val intent = Intent(this, SignUpActivity::class.java)
            startActivity(intent) // Start the SignUp activity
        }
    }
}
