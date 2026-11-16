
package com.example.cafeteriamanagementsystem.models

data class Order(
    val orderId: String = "",
    val customerName: String = "",
    val studentNumber: String = "",
    val phone: String = "",
    val items: List<Food> = emptyList(),
    val total: Double = 0.0,
    var status: String = "PENDING",
    val dateTime: String = ""
)
