package com.example.tttxtreme_android

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.widget.Button

class UserLandingActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_landing)

        // Access the buttons using their IDs
        val btnLocalCasual = findViewById<Button>(R.id.btnLocalCasual)
        val btnLocalExtreme = findViewById<Button>(R.id.btnLocalExtreme)
        val btnLocalOnly3 = findViewById<Button>(R.id.btnLocalOnly3)

        // Set click listeners for each button
        btnLocalCasual.setOnClickListener {
            // Start the LocalCasualActivity
            val intent = Intent(this, LocalCasualActivity::class.java)
            startActivity(intent)
        }

        btnLocalExtreme.setOnClickListener {
            // Start the LocalExtremeActivity
            val intent = Intent(this, LocalExtremeActivity::class.java)
            startActivity(intent)
        }

        btnLocalOnly3.setOnClickListener {
            // Start the LocalOnly3Activity
            val intent = Intent(this, LocalOnly3Activity::class.java)
            startActivity(intent)
        }
    }
}
