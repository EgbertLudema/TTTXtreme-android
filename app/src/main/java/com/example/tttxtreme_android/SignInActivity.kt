package com.example.tttxtreme_android

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.style.ClickableSpan
import android.text.style.ForegroundColorSpan
import android.view.View
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import android.text.method.LinkMovementMethod

class SignInActivity : AppCompatActivity() {

    companion object {
        private const val RC_SIGN_IN = 9001
    }

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_in)

        auth = FirebaseAuth.getInstance()

        val currentUser = auth.currentUser
        if (currentUser != null) {
            // User already signed in, navigate to MainActivity
            startActivity(Intent(this, MainActivity::class.java))
            finish() // Finish this activity
        }

        // Set up clickable "Register now!" text
        setupRegisterLink()

        // Google Sign-In button
        val googleSignInButton = findViewById<ImageButton>(R.id.googleSignInButton)
        googleSignInButton.setOnClickListener {
            signIn()
        }
    }

    private fun setupRegisterLink() {
        val textView: TextView = findViewById(R.id.text_register)

        val fullText = "No account yet? Register now!"
        val spannableString = SpannableString(fullText)

        // Define the "Register now!" part as clickable
        val clickableSpan = object : ClickableSpan() {
            override fun onClick(widget: View) {
                // Navigate to the RegisterActivity
                val intent = Intent(this@SignInActivity, SignUpActivity::class.java)
                startActivity(intent)
            }
        }

        // Set the span for the "Register now!" part
        val start = fullText.indexOf("Register now!")
        val end = start + "Register now!".length
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

    private fun signIn() {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id)) // Ensure this is set in your strings.xml
            .requestEmail()
            .build()

        val googleSignInClient = GoogleSignIn.getClient(this, gso)
        val signInIntent = googleSignInClient.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val account = task.getResult(ApiException::class.java)
                firebaseAuthWithGoogle(account.idToken!!)
            } catch (e: ApiException) {
                Toast.makeText(this, "Google sign-in failed: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val user = auth.currentUser
                    Toast.makeText(this, "Signed in as ${user?.displayName}", Toast.LENGTH_SHORT).show()
                    startActivity(Intent(this, MainActivity::class.java))
                    finish()
                } else {
                    Toast.makeText(this, "Authentication failed", Toast.LENGTH_SHORT).show()
                }
            }
    }
}
