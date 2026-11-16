package com.example.cafeteriamanagementsystem.models

import com.google.android.gms.auth.api.identity.SignInPassword

data class User(
    var fullName: String? = null,
    var studentNumber: String? = null,
    val email:String? = null,
    val phoneNumber: String? = null,
    val password: String? = null,
    val confirmPassword: String? = null,
    val role: UserRole = UserRole.STUDENT
)