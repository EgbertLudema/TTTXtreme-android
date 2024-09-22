package com.example.tttxtreme_android

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlin.system.exitProcess

class MainActivity : AppCompatActivity() {

    private lateinit var mGoogleSignInClient: GoogleSignInClient
    private lateinit var mAuth: FirebaseAuth

    // Tic-Tac-Toe game variables
    var player1Count = 0
    var player2Count = 0
    var player1 = ArrayList<Int>()
    var player2 = ArrayList<Int>()
    var emptyCells = ArrayList<Int>()
    var activeUser = 1
    var playerTurn = true
    var singleUser = false // Flag for single-player mode (AI)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        mAuth = FirebaseAuth.getInstance()

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso)

        val textView = findViewById<TextView>(R.id.name)
        val auth = Firebase.auth
        val user = auth.currentUser

        if (user != null) {
            val userName = user.displayName
            textView.text = "Welcome, $userName"
        }

        // Logout button functionality
        val signOutButton = findViewById<Button>(R.id.logout_button)
        signOutButton.setOnClickListener {
            signOutAndStartSignInActivity()
        }

        // Reset button listener
        val resetButton = findViewById<Button>(R.id.button10)
        resetButton.setOnClickListener {
            reset()
        }
    }

    private fun signOutAndStartSignInActivity() {
        mAuth.signOut()

        mGoogleSignInClient.signOut().addOnCompleteListener(this) {
            val intent = Intent(this@MainActivity, SignInActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    // Handle Tic-Tac-Toe board clicks
    fun clickfun(view: View) {
        if (playerTurn) {
            val but = view as Button
            var cellID = 0
            when (but.id) {
                R.id.button -> cellID = 1
                R.id.button2 -> cellID = 2
                R.id.button3 -> cellID = 3
                R.id.button4 -> cellID = 4
                R.id.button5 -> cellID = 5
                R.id.button6 -> cellID = 6
                R.id.button7 -> cellID = 7
                R.id.button8 -> cellID = 8
                R.id.button9 -> cellID = 9
            }
            playerTurn = false
            Handler().postDelayed({ playerTurn = true }, 600)
            playNow(but, cellID)
        }
    }

    // Update game board after every move
    fun playNow(buttonSelected: Button, currCell: Int) {
        if (activeUser == 1) {
            buttonSelected.text = "X"
            buttonSelected.setTextColor(Color.parseColor("#FF5959"))
            player1.add(currCell)
            emptyCells.add(currCell)
            buttonSelected.isEnabled = false

            val checkWinner = checkWinner()
            if (checkWinner == 1) {
                Handler().postDelayed({ reset() }, 2000)
            } else {
                activeUser = 2
            }
        } else {
            buttonSelected.text = "O"
            buttonSelected.setTextColor(Color.parseColor("#63C6F7"))
            player2.add(currCell)
            emptyCells.add(currCell)
            buttonSelected.isEnabled = false

            val checkWinner = checkWinner()
            if (checkWinner == 1) {
                Handler().postDelayed({ reset() }, 4000)
            }
            activeUser = 1
        }
    }

    // Reset the game
    fun reset() {
        player1.clear()
        player2.clear()
        emptyCells.clear()
        activeUser = 1

        for (i in 1..9) {
            val buttonSelected: Button? = when (i) {
                1 -> findViewById<Button>(R.id.button)
                2 -> findViewById<Button>(R.id.button2)
                3 -> findViewById<Button>(R.id.button3)
                4 -> findViewById<Button>(R.id.button4)
                5 -> findViewById<Button>(R.id.button5)
                6 -> findViewById<Button>(R.id.button6)
                7 -> findViewById<Button>(R.id.button7)
                8 -> findViewById<Button>(R.id.button8)
                9 -> findViewById<Button>(R.id.button9)
                else -> findViewById<Button>(R.id.button)
            }
            buttonSelected?.isEnabled = true
            buttonSelected?.text = ""
        }

        val textView1 = findViewById<TextView>(R.id.textView)
        val textView2 = findViewById<TextView>(R.id.textView2)
        textView1.text = "Player1 : $player1Count"
        textView2.text = "Player2 : $player2Count"
    }

    // Check for the winner
    fun checkWinner(): Int {
        if (checkVictory(player1)) {
            player1Count += 1
            buttonDisable()
            disableReset()
            showAlertDialog("You have won the game..")
            return 1
        } else if (checkVictory(player2)) {
            player2Count += 1
            buttonDisable()
            disableReset()
            showAlertDialog("Opponent has won the game")
            return 1
        } else if (emptyCells.size == 9) {
            showAlertDialog("Nobody Wins")
            return 1
        }
        return 0
    }

    // Helper to check victory condition
    fun checkVictory(player: ArrayList<Int>): Boolean {
        return (player.contains(1) && player.contains(2) && player.contains(3)) ||
                (player.contains(1) && player.contains(4) && player.contains(7)) ||
                (player.contains(3) && player.contains(6) && player.contains(9)) ||
                (player.contains(7) && player.contains(8) && player.contains(9)) ||
                (player.contains(4) && player.contains(5) && player.contains(6)) ||
                (player.contains(1) && player.contains(5) && player.contains(9)) ||
                (player.contains(3) && player.contains(5) && player.contains(7)) ||
                (player.contains(2) && player.contains(5) && player.contains(8))
    }

    // Show alert dialog on win or draw
    fun showAlertDialog(message: String) {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Game Over")
        builder.setMessage("$message\n\nDo you want to play again?")
        builder.setPositiveButton("Ok") { _, _ ->
            reset()
        }
        builder.setNegativeButton("Exit") { _, _ ->
            exitProcess(1)
        }
        builder.show()
    }

    // Disable all buttons after game ends
    fun buttonDisable() {
        for (i in 1..9) {
            val buttonSelected: Button = when (i) {
                1 -> findViewById<Button>(R.id.button)
                2 -> findViewById<Button>(R.id.button2)
                3 -> findViewById<Button>(R.id.button3)
                4 -> findViewById<Button>(R.id.button4)
                5 -> findViewById<Button>(R.id.button5)
                6 -> findViewById<Button>(R.id.button6)
                7 -> findViewById<Button>(R.id.button7)
                8 -> findViewById<Button>(R.id.button8)
                9 -> findViewById<Button>(R.id.button9)
                else -> findViewById<Button>(R.id.button)
            }
            buttonSelected.isEnabled = false
        }
    }

    // Disable the reset button temporarily
    fun disableReset() {
        val resetButton = findViewById<Button>(R.id.button10)
        resetButton.isEnabled = false
        Handler().postDelayed({ resetButton.isEnabled = true }, 2200)
    }
}