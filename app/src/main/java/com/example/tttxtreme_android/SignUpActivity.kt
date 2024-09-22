package com.example.tttxtreme_android

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.text.style.ForegroundColorSpan
import android.view.View
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class SignUpActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_sign_up)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Set up clickable "Register now!" text
        setupLoginLink()
    }

    private fun setupLoginLink() {
        val textView: TextView = findViewById(R.id.text_login)

        val fullText = "Already a account? Login!"
        val spannableString = SpannableString(fullText)

        // Define the "Login!" part as clickable
        val clickableSpan = object : ClickableSpan() {
            override fun onClick(widget: View) {
                // Navigate to the LoginActivity
                val intent = Intent(this@SignUpActivity, SignInActivity::class.java)
                startActivity(intent)
            }
        }

        // Set the span for the "Login!" part
        val start = fullText.indexOf("Login!")
        val end = start + "Login!".length
        spannableString.setSpan(clickableSpan, start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)

        // Optionally, change the color of the "Register now!" part
        spannableString.setSpan(
            ForegroundColorSpan(Color.BLUE),
            start,
            end,
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )

        // Set the spannable string to the TextView
        textView.text = spannableString
        textView.movementMethod = LinkMovementMethod.getInstance() // Makes the link clickable
    }
}