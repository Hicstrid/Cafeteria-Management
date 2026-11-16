package com.example.cafeteriamanagementsystem.activities

import android.app.Activity
import android.content.Intent
import android.credentials.CredentialManager
import android.os.Bundle
import android.widget.Toast
import android.widget.Button
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.example.cafeteriamanagementsystem.R
import com.example.cafeteriamanagementsystem.databinding.ActivityRegisterBinding
import com.example.cafeteriamanagementsystem.models.User
import com.example.cafeteriamanagementsystem.models.UserRole
import com.example.cafeteriamanagementsystem.utils.SessionManager
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.Firebase
import com.google.firebase.database.database

class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var googleSignInClient: GoogleSignInClient
    private lateinit var credentialManager: CredentialManager
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.txtLogin.setOnClickListener {
            startActivity(Intent(this, LoginActivity :: class.java))
            finish()
        }

        binding.btnCreateAccount.setOnClickListener {

            val fullName =
                binding.etFullName.text.toString().trim()

            val studentNumber =
                binding.etStudentNumber.text.toString().trim()

            val email =
                binding.etEmail.text.toString().trim()

            val phoneNumber =
                binding.etPhoneNumber.text.toString().trim()

            val password =
                binding.etPassword.text.toString()

            val confirmPassword =
                binding.etConfirmPassword.text.toString()

            // CHECK EMPTY FIELDS

            if (
                fullName.isEmpty() ||
                studentNumber.isEmpty() ||
                email.isEmpty() ||
                phoneNumber.isEmpty() ||
                password.isEmpty() ||
                confirmPassword.isEmpty()
            ) {

                Toast.makeText(
                    this,
                    "Please complete all fields",
                    Toast.LENGTH_SHORT
                ).show()

                return@setOnClickListener
            }

            // GMAIL VALIDATION

            if (
                !android.util.Patterns.EMAIL_ADDRESS
                    .matcher(email).matches() ||
                !email.endsWith("@gmail.com", ignoreCase = true)
            ) {

                binding.etEmail.error =
                    "Only valid @gmail.com addresses are allowed"

                binding.etEmail.requestFocus()

                return@setOnClickListener
            }

            // PHONE NUMBER VALIDATION

            if (!phoneNumber.matches(Regex("^0[0-9]{9}$"))) {

                binding.etPhoneNumber.error =
                    "Phone number must start with 0 and contain exactly 10 digits"

                binding.etPhoneNumber.requestFocus()

                return@setOnClickListener
            }

            // PASSWORD VALIDATION

            if (password.length < 8) {

                Toast.makeText(
                    this,
                    "Password must be at least 8 characters long",
                    Toast.LENGTH_LONG
                ).show()

                return@setOnClickListener
            }

            // AT LEAST ONE UPPERCASE LETTER

            if (!password.any { it.isUpperCase() }) {

                Toast.makeText(
                    this,
                    "Password must contain at least 1 uppercase letter",
                    Toast.LENGTH_LONG
                ).show()

                return@setOnClickListener
            }

            // AT LEAST ONE LOWERCASE LETTER

            if (!password.any { it.isLowerCase() }) {

                Toast.makeText(
                    this,
                    "Password must contain at least 1 lowercase letter",
                    Toast.LENGTH_LONG
                ).show()

                return@setOnClickListener
            }

            // AT LEAST ONE NUMBER

            if (!password.any { it.isDigit() }) {

                Toast.makeText(
                    this,
                    "Password must contain at least 1 number",
                    Toast.LENGTH_LONG
                ).show()

                return@setOnClickListener
            }

            // CONFIRM PASSWORD

            if (password != confirmPassword) {

                Toast.makeText(
                    this,
                    "Passwords do not match",
                    Toast.LENGTH_SHORT
                ).show()

                return@setOnClickListener
            }

            // CHECK USERNAME

            if (
                SessionManager.users.any {
                    it.studentNumber.equals(
                        studentNumber,
                        ignoreCase = true
                    )
                }
            ) {

                Toast.makeText(
                    this,
                    "Username already exists",
                    Toast.LENGTH_SHORT
                ).show()

                return@setOnClickListener
            }

            // CHECK EMAIL

            if (
                SessionManager.users.any {
                    it.email.equals(
                        email,
                        ignoreCase = true
                    )
                }
            ) {

                Toast.makeText(
                    this,
                    "Email address already registered",
                    Toast.LENGTH_SHORT
                ).show()

                return@setOnClickListener
            }
            //This is to store the users login info in the database

            // This is to add a user
            //Write message to the database
            val database = Firebase.database
            val myRef = database.getReference("Users")

            val user = User(
                fullName = fullName,
                studentNumber = studentNumber,
                email = email,
                phoneNumber = phoneNumber,
                password = password,
                confirmPassword = confirmPassword,
                role = UserRole.STUDENT
            )

            myRef.child(studentNumber).setValue(user)

            // CREATE STUDENT ACCOUNT

            SessionManager.users.add(user)
            SessionManager.currentUser = user

            // SUCCESS

            Toast.makeText(
                this,
                "Account created successfully",
                Toast.LENGTH_SHORT
            ).show()

            startActivity(Intent(this, DashboardActivity::class.java))
            finish()

        }

        //CONTINUE WITH GOOGLE

        auth = FirebaseAuth.getInstance()

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail().build()

        googleSignInClient = GoogleSignIn.getClient(this, gso)

        findViewById<Button>(R.id.btnGoogle).setOnClickListener {
            signInGoogle()
        }
    }

    private fun signInGoogle() {
        val signInIntent = googleSignInClient.signInIntent
        launcher.launch(signInIntent)
    }

    private val launcher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
                handleResults(task)
            }
        }

    private fun handleResults(task: Task<GoogleSignInAccount>) {
        if (task.isSuccessful) {
            val account: GoogleSignInAccount? = task.result
            if (account != null) {
                updateUI(account)
            }
        } else {
            Toast.makeText(this, task.exception.toString(), Toast.LENGTH_SHORT).show()
        }
    }

    private fun updateUI(account: GoogleSignInAccount) {
        val credential = GoogleAuthProvider.getCredential(account.idToken, null)
        auth.signInWithCredential(credential).addOnCompleteListener(this) {
            if (it.isSuccessful) {
                // SSO FIX for REGISTER screen:
                // On Register, Google should CREATE an account, not say "No account found"
                // Your top part uses username = student number, so we check by EMAIL here
                val existingUser = SessionManager.users.find {
                    it.email.equals(account.email, ignoreCase = true)
                }

                if (existingUser != null) {
                    // Already registered, just go to Login
                    SessionManager.currentUser = existingUser
                    Toast.makeText(this, "Account already exists, please login", Toast.LENGTH_SHORT)
                        .show()
                    startActivity(Intent(this, LoginActivity::class.java))
                    finish()
                } else {
                    // New Google user - create them like your teammate's CREATE STUDENT ACCOUNT does
                    val newUser = User(
                        fullName = account.displayName ?: "",
                        studentNumber = account.email
                            ?: "", // use email as username for Google users, since no student number
                        email = account.email ?: "",
                        password = "", // Google users have no password
                        role = UserRole.STUDENT
                    )
                    SessionManager.users.add(newUser)
                    SessionManager.currentUser = newUser

                    Toast.makeText(
                        this,
                        "Account created successfully with Google",
                        Toast.LENGTH_SHORT
                    ).show()
                    startActivity(Intent(this, LoginActivity::class.java))
                    finish()
                }
            } else {
                Toast.makeText(this, it.exception.toString(), Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onStart() {
        super.onStart()
        //Check to see if user is signed in (non-null) and update ui accordingly
        // SSO FIX: Don't auto-jump from Register to Login on every open,
        // or you can never stay on Register to create an account.
        // Only auto-jump if you already have a SessionManager user
        if (::auth.isInitialized && auth.currentUser != null && SessionManager.currentUser != null) {
            startActivity(Intent(this, DashboardActivity::class.java))
            finish()
        }
    }
}