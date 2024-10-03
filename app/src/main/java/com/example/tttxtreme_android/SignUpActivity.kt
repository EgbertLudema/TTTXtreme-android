package com.example.tttxtreme_android

import android.content.Intent
import androidx.core.content.ContextCompat
import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.text.style.ForegroundColorSpan
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import android.app.AlertDialog
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.facebook.*
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FacebookAuthProvider
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.FirebaseFirestore

class SignUpActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore
    private lateinit var callbackManager: CallbackManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_sign_up)

        // Initialize Firebase Auth and Firestore
        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()

        // Initialize Facebook SDK
        callbackManager = CallbackManager.Factory.create()

        // Reference UI elements
        val usernameField: EditText = findViewById(R.id.username)
        val emailField: EditText = findViewById(R.id.email)
        val passwordField: EditText = findViewById(R.id.password)
        val registerButton: Button = findViewById(R.id.register_button)

        // Facebook login ImageButton (custom)
        val facebookLoginButton: ImageButton = findViewById(R.id.facebookSignInButton)
        facebookLoginButton.setOnClickListener {
            LoginManager.getInstance().logInWithReadPermissions(this, listOf("email", "public_profile"))
            LoginManager.getInstance().registerCallback(callbackManager, object : FacebookCallback<LoginResult> {
                override fun onSuccess(loginResult: LoginResult) {
                    handleFacebookAccessToken(loginResult.accessToken)
                }

                override fun onCancel() {
                    Toast.makeText(this@SignUpActivity, "Facebook login canceled.", Toast.LENGTH_SHORT).show()
                }

                override fun onError(error: FacebookException) {
                    Toast.makeText(this@SignUpActivity, "Facebook login failed: ${error.message}", Toast.LENGTH_SHORT).show()
                }
            })
        }

        // Google Sign-In button
        val googleSignInButton: ImageButton = findViewById(R.id.googleSignInButton)
        googleSignInButton.setOnClickListener {
            signInWithGoogle()
        }

        // Handle register button click for email/password registration
        registerButton.setOnClickListener {
            val username = usernameField.text.toString().trim()
            val email = emailField.text.toString().trim()
            val password = passwordField.text.toString().trim()

            if (email.isEmpty() || password.isEmpty() || username.isEmpty()) {
                Toast.makeText(this, "Please fill all fields.", Toast.LENGTH_SHORT).show()
            } else {
                registerUser(username, email, password)
            }
        }

        // Setup clickable "Login!" link to navigate to SignInActivity
        setupLoginLink()
    }

    private fun signInWithGoogle() {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        val googleSignInClient = GoogleSignIn.getClient(this, gso)
        googleSignInLauncher.launch(googleSignInClient.signInIntent)
    }

    // Initialize Google Sign-In result handler
    private val googleSignInLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
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
                    Toast.makeText(this, "Signed in with Google as ${user?.displayName}", Toast.LENGTH_SHORT).show()
                    startActivity(Intent(this, UserLandingActivity::class.java))
                    finish()
                } else {
                    Toast.makeText(this, "Google Authentication failed.", Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun registerUser(username: String, email: String, password: String) {
        // Password validation
        if (password.length < 6) {
            showPasswordTooShortDialog()
            return
        }

        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    Toast.makeText(this, "Registration successful", Toast.LENGTH_SHORT).show()
                    // Save username to Firestore
                    val user = auth.currentUser
                    user?.let {
                        saveUsernameToFirestore(it.uid, username)
                    }
                    startActivity(Intent(this, SignInActivity::class.java))
                } else {
                    val exception = task.exception
                    if (exception != null) {
                        if (exception.message?.contains("email address is already in use") == true) {
                            showEmailInUseDialog()
                        } else {
                            Toast.makeText(this, "Registration failed: ${exception.message}", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
    }

    private fun handleFacebookAccessToken(token: AccessToken) {
        val credential = FacebookAuthProvider.getCredential(token.token)
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Facebook sign-in successful
                    val user = auth.currentUser
                    Toast.makeText(this, "Signed in with Facebook as ${user?.displayName}", Toast.LENGTH_SHORT).show()
                    startActivity(Intent(this, UserLandingActivity::class.java))
                    finish()
                } else {
                    Toast.makeText(this, "Facebook Authentication failed.", Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun showPasswordTooShortDialog() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Password Too Short")
        builder.setMessage("Password must be at least 6 characters long. Please try again.")
        builder.setPositiveButton("OK") { dialog, _ -> dialog.dismiss() }
        val dialog: AlertDialog = builder.create()
        dialog.show()
    }

    private fun showEmailInUseDialog() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Email Already In Use")
        builder.setMessage("This email is already associated with an existing account. Would you like to sign in instead?")
        builder.setPositiveButton("Go to Login") { dialog, _ ->
            val intent = Intent(this, SignInActivity::class.java)
            startActivity(intent)
            dialog.dismiss()
        }
        builder.setNegativeButton("Cancel") { dialog, _ -> dialog.dismiss() }
        val dialog: AlertDialog = builder.create()
        dialog.show()
    }

    private fun saveUsernameToFirestore(userId: String, username: String) {
        val userMap = hashMapOf("username" to username)
        firestore.collection("users").document(userId)
            .set(userMap)
            .addOnSuccessListener {
                showRegistrationSuccessDialog()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Failed to save user data: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun showRegistrationSuccessDialog() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Registration Successful")
        builder.setMessage("Your account has been successfully created. You can now log in.")
        builder.setPositiveButton("Go to Login") { dialog, _ ->
            val intent = Intent(this, SignInActivity::class.java)
            startActivity(intent)
            dialog.dismiss()
        }
        val dialog: AlertDialog = builder.create()
        dialog.show()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        callbackManager.onActivityResult(requestCode, resultCode, data)
    }

    private fun setupLoginLink() {
        val textView: TextView = findViewById(R.id.text_login)
        val fullText = "Already have an account? Login!"
        val spannableString = SpannableString(fullText)

        val clickableSpan = object : ClickableSpan() {
            override fun onClick(widget: View) {
                val intent = Intent(this@SignUpActivity, SignInActivity::class.java)
                startActivity(intent)
            }
        }

        val redColor = ContextCompat.getColor(this, R.color.red)
        spannableString.setSpan(clickableSpan, fullText.indexOf("Login!"), fullText.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        spannableString.setSpan(ForegroundColorSpan(redColor), fullText.indexOf("Login!"), fullText.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)

        textView.text = spannableString
        textView.movementMethod = LinkMovementMethod.getInstance()
    }
}
