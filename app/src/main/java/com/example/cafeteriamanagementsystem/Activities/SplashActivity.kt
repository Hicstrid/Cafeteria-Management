package com.example.cafeteriamanagementsystem.activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import com.example.cafeteriamanagementsystem.R
import com.example.cafeteriamanagementsystem.databinding.ActivityMainBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.Firebase
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.database

class SplashActivity : AppCompatActivity() {

    lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)

/* //THIS IS PURELY FOR THE FIREBASE REALTIME DATABASE
 //Write message to the database
 val database = FirebaseDatabase.getInstance("https://cafeteriamanagementsystem-default-rtdb.firebaseio.com/")
 val myRef = database.getReference("message")
 val TAG = "FirebaseRead"
 myRef.setValue("Hello, World")

 // Read from the database
 myRef.addValueEventListener(object: ValueEventListener {

     override fun onDataChange(snapshot: DataSnapshot) {
         // This method is called once with the initial value and again
         // whenever data at this location is updated.
         val value = snapshot.getValue(String :: class.java)
         Log.d(TAG, "Value is: " + value)
     }

     override fun onCancelled(error: DatabaseError) {
         //This is to show that it failed to read the value
         Log.w(TAG, "Failed to read value.", error.toException())
     }

 })*/
        // Wait 3 seconds before opening Login Screen
        Handler(Looper.getMainLooper()).postDelayed({

            startActivity(Intent(this, LoginActivity::class.java))

            finish()

        },3000)
    }
}