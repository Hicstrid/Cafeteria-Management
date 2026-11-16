package com.example.cafeteriamanagementsystem.activities

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.credentials.ClearCredentialStateRequest
import androidx.credentials.Credential
import android.util.Log
import android.credentials.ClearCredentialStateException
import android.credentials.CredentialManager
import androidx.lifecycle.lifecycleScope
import com.example.cafeteriamanagementsystem.databinding.ActivityProfileBinding
import com.example.cafeteriamanagementsystem.utils.SessionManager
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth

class ProfileActivity : AppCompatActivity() {

    private lateinit var binding: ActivityProfileBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var googleSignInClient: GoogleSignInClient
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)




        setSupportActionBar(binding.toolbar)

        supportActionBar?.title = "My Profile"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        binding.toolbar.setNavigationOnClickListener {
            finish()
        }


        // GET LOGGED-IN USER


        val user = SessionManager.currentUser

        if (user == null) {

            Toast.makeText(
                this,
                "No user is currently logged in",
                Toast.LENGTH_SHORT
            ).show()

            finish()
            return
        }


        // DISPLAY CURRENT INFORMATION


        binding.etFullName.setText(user.fullName)
        binding.etUsername.setText(user.studentNumber)

        binding.tvRole.text = when (user.role.name) {

            "STUDENT" -> "Student"

            "STAFF" -> "Cafeteria Staff"

            "MANAGER" -> "Manager"

            else -> "Unknown"
        }


        // SAVE PROFILE CHANGES


        binding.btnSaveProfile.setOnClickListener {

            val newFullName =
                binding.etFullName.text.toString().trim()

            val newUsername =
                binding.etUsername.text.toString().trim()

            // Check that the full name is not empty
            if (newFullName.isEmpty()) {

                binding.etFullName.error =
                    "Please enter your full name"

                binding.etFullName.requestFocus()

                return@setOnClickListener
            }

            // Check that the username is not empty
            if (newUsername.isEmpty()) {

                binding.etUsername.error =
                    "Please enter a username"

                binding.etUsername.requestFocus()

                return@setOnClickListener
            }

            // CHECK USERNAME AVAILABILITY

            val usernameAlreadyExists =
                SessionManager.users.any {

                    it !== user &&
                            it.studentNumber.equals(
                                newUsername,
                                ignoreCase = true
                            )
                }

            if (usernameAlreadyExists) {

                binding.etUsername.error =
                    "Username is already in use"

                binding.etUsername.requestFocus()

                Toast.makeText(
                    this,
                    "Please choose another username",
                    Toast.LENGTH_SHORT
                ).show()

                return@setOnClickListener
            }


            // UPDATE USER INFORMATION


            user.fullName = newFullName
            user.studentNumber = newUsername


            SessionManager.currentUser = user


            // SUCCESS MESSAGE


            Toast.makeText(
                this,
                "Profile updated successfully",
                Toast.LENGTH_SHORT
            ).show()

            // Close profile page
            finish()
        }


        // LOGOUT


        binding.btnLogout.setOnClickListener {

            SessionManager.currentUser = null

            val intent = Intent(
                this,
                LoginActivity::class.java
            )

            intent.flags =
                Intent.FLAG_ACTIVITY_NEW_TASK or
                        Intent.FLAG_ACTIVITY_CLEAR_TASK

            startActivity(intent)

            finish()
        }
    }

    //Copy and paste this is the profile part please
    private fun signOut() {
        binding.btnSignout.setOnClickListener {
            //This is for the Firebase sign out
            auth.signOut()
//When a user signs out, this is to clear the current user credentials from all credential providers
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)

            Toast.makeText(this, "Signed Out successfully", Toast.LENGTH_SHORT).show()
        }
    }
}