
package com.example.cafeteriamanagementsystem.utils

import android.content.Context
import com.example.cafeteriamanagementsystem.models.Food
import com.example.cafeteriamanagementsystem.models.Order
import org.json.JSONArray
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object OrderManager {

    val orders = mutableListOf<Order>()

    private const val PREFS_NAME = "SmartServeOrders"
    private const val ORDERS_KEY = "saved_orders"

    private var initialized = false
    private var preferences: android.content.SharedPreferences? = null

    fun initialize(context: Context) {

        if (initialized) return

        preferences = context.applicationContext
            .getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

        loadOrders()

        initialized = true
    }

    fun placeOrder(
        customerName: String,
        studentNumber: String,
        phone: String,
        items: List<Food>,
        total: Double
    ): Order {

        val order = Order(
            orderId = "ORD-" + System.currentTimeMillis()
                .toString().takeLast(6),

            customerName = customerName,
            studentNumber = studentNumber,
            phone = phone,
            items = items.toList(),
            total = total,
            status = "PENDING",

            dateTime = SimpleDateFormat(
                "dd MMM yyyy, HH:mm",
                Locale.getDefault()
            ).format(Date())
        )

        orders.add(order)

        saveOrders()

        return order
    }

    fun updateStatus(orderId: String, status: String) {

        if (status != "PENDING" && status != "COMPLETED") {
            return
        }

        val order = orders.find {
            it.orderId == orderId
        }

        order?.let {
            it.status = status
            saveOrders()
        }
    }

    fun getCustomerOrders(studentNumber: String): List<Order> {

        return orders.filter {
            it.studentNumber.equals(
                studentNumber,
                ignoreCase = true
            )
        }.asReversed()
    }

    private fun saveOrders() {

        val jsonArray = JSONArray()

        orders.forEach { order ->

            val orderJson = JSONObject()

            orderJson.put("orderId", order.orderId)
            orderJson.put("customerName", order.customerName)
            orderJson.put("studentNumber", order.studentNumber)
            orderJson.put("phone", order.phone)
            orderJson.put("total", order.total)
            orderJson.put("status", order.status)
            orderJson.put("dateTime", order.dateTime)

            val itemsArray = JSONArray()

            order.items.forEach { food ->

                val foodJson = JSONObject()

                foodJson.put("id", food.id)
                foodJson.put("name", food.name)
                foodJson.put("description", food.description)
                foodJson.put("price", food.price)
                foodJson.put("imageResId", food.imageResId)

                itemsArray.put(foodJson)
            }

            orderJson.put("items", itemsArray)

            jsonArray.put(orderJson)
        }

        preferences?.edit()
            ?.putString(ORDERS_KEY, jsonArray.toString())
            ?.apply()
    }

    private fun loadOrders() {

        orders.clear()

        val savedData = preferences?.getString(
            ORDERS_KEY,
            "[]"
        ) ?: "[]"

        try {

            val jsonArray = JSONArray(savedData)

            for (i in 0 until jsonArray.length()) {

                val orderJson = jsonArray.getJSONObject(i)

                val itemsArray = orderJson.getJSONArray("items")

                val foodItems = mutableListOf<Food>()

                for (j in 0 until itemsArray.length()) {

                    val foodJson = itemsArray.getJSONObject(j)

                    foodItems.add(
                        Food(
                            id = foodJson.getInt("id"),
                            name = foodJson.getString("name"),
                            description = foodJson.getString("description"),
                            price = foodJson.getDouble("price"),
                            imageResId = foodJson.getInt("imageResId")
                        )
                    )
                }

                orders.add(
                    Order(
                        orderId = orderJson.getString("orderId"),
                        customerName = orderJson.getString("customerName"),
                        studentNumber = orderJson.getString("studentNumber"),
                        phone = orderJson.getString("phone"),
                        items = foodItems,
                        total = orderJson.getDouble("total"),
                        status = orderJson.optString("status", "PENDING"),
                        dateTime = orderJson.getString("dateTime")
                    )
                )
            }

        } catch (e: Exception) {

            orders.clear()
        }
    }
}
