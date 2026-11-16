package com.example.cafeteriamanagementsystem.utils

import com.example.cafeteriamanagementsystem.models.User
import com.example.cafeteriamanagementsystem.models.UserRole

object SessionManager {

    val users = mutableListOf<User>()

    var currentUser: User? = null

    init {

        // Default manager account
        users.add(
            User(
                fullName = "Cafeteria Manager",
                studentNumber = "manager",
                password = "manager123",
                email = "",
                role = UserRole.MANAGER
            )
        )

        // Default staff account
        users.add(
            User(
                fullName = "Cafeteria Staff",
                studentNumber = "staff",
                password = "staff123",
                email="",
                role = UserRole.STAFF
            )
        )
    }
}