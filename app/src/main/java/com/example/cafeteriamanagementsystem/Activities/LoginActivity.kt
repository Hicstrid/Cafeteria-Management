package com.example.cafeteriamanagementsystem.activities

import android.app.Activity
import android.content.Intent
import android.credentials.CredentialManager
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.android.gms.tasks.Task
import com.example.cafeteriamanagementsystem.R
import com.example.cafeteriamanagementsystem.databinding.ActivityLoginBinding
import com.example.cafeteriamanagementsystem.models.User
import com.example.cafeteriamanagementsystem.utils.SessionManager
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.database.FirebaseDatabase

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var auth : FirebaseAuth
    private lateinit var googleSignInClient : GoogleSignInClient
    private lateinit var credentialManager: CredentialManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        auth = FirebaseAuth.getInstance()
        auth = Firebase.auth
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Login Button
        binding.btnLogin.setOnClickListener {

            val username = binding.edtUserId.text.toString().trim()
            val password = binding.edtPassword.text.toString().trim()

            if (username.isEmpty() || password.isEmpty()) {
                Toast.makeText(
                    this,
                    "Please enter your Student ID and Password",
                    Toast.LENGTH_SHORT
                ).show()
                return@setOnClickListener
            }

            val user = SessionManager.users.find {
                it.studentNumber == username &&
                        it.password == password
            }

            if (user != null) {

                SessionManager.currentUser = user

                Toast.makeText(
                    this,
                    "Welcome ${user.fullName}",
                    Toast.LENGTH_SHORT
                ).show()

                startActivity(Intent(this, DashboardActivity::class.java))
                finish()

            } else {

                Toast.makeText(
                    this,
                    "Invalid Student ID or Password",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        // Register
        binding.txtRegister.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }

        //CONTINUE WITH GOOGLE
        //
        auth = FirebaseAuth.getInstance()

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail().build()

        googleSignInClient = GoogleSignIn.getClient(this, gso)

        findViewById<Button>(R.id.btnGoogle).setOnClickListener {
            signInGoogle()
        }
    }
    //  open Google popup
    private fun signInGoogle(){
        val signInIntent = googleSignInClient.signInIntent
        launcher.launch(signInIntent)
    }

    //  get result back from Google
    private val launcher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
            result -> if(result.resultCode == Activity.RESULT_OK){
        val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
        handleResults(task)
    }
    }

    // check if Google sign-in was successful
    private fun handleResults(task: Task<GoogleSignInAccount>){
        if (task.isSuccessful){
            val account : GoogleSignInAccount? = task.result
            if (account != null){
                updateUI(account)
            }
        }else{
            Toast.makeText(this, task.exception.toString(), Toast.LENGTH_SHORT).show()
        }
    }

    //  login to Firebase with Google credential
    private fun updateUI(account: GoogleSignInAccount){
        val credential = GoogleAuthProvider.getCredential(account.idToken,null)
        auth.signInWithCredential(credential).addOnCompleteListener(this) {
            if (it.isSuccessful) {

                //  find user in SessionManager
                val user = SessionManager.users.find {
                    it.studentNumber == account.email
                }

                if (user != null) {
                    SessionManager.currentUser = user
                    startActivity(Intent(this, DashboardActivity::class.java))
                    finish()
                } else {
                    // YOUR LOGIC - no account found, must register first
                    Toast.makeText(
                        this,
                        "No account found here. Please register first",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            } else {
                Toast.makeText(this, it.exception.toString(), Toast.LENGTH_SHORT).show()
            }
        }
    }

    //  auto login if already signed in
    override fun onStart() {
        super.onStart()
        //Check to see if user is signed in (non-null) and update ui accordingly
        // FIXED: you had val currentUser twice, that gives red error. Now just 1 check.
        if (auth.currentUser != null && SessionManager.currentUser != null) {
            // FIXED: Toast.makeText() was empty, now has text
            Toast.makeText(this, "welcome back", Toast.LENGTH_SHORT).show()
            startActivity(Intent(this, DashboardActivity::class.java))
            finish()
        }
    }
}