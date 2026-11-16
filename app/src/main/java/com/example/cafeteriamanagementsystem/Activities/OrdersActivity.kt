
package com.example.cafeteriamanagementsystem.activities

import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.view.Gravity
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.cafeteriamanagementsystem.R
import com.example.cafeteriamanagementsystem.models.Food
import com.example.cafeteriamanagementsystem.models.Order
import com.example.cafeteriamanagementsystem.models.User
import com.example.cafeteriamanagementsystem.models.UserRole
import com.example.cafeteriamanagementsystem.utils.OrderManager
import com.example.cafeteriamanagementsystem.utils.SessionManager
import com.google.firebase.Firebase
import com.google.firebase.database.database

class OrdersActivity : AppCompatActivity() {

    private lateinit var ordersContainer: LinearLayout

    private val green = Color.rgb(23, 107, 69)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.orders)


        val toolbar =
            findViewById<com.google.android.material.appbar.MaterialToolbar>(
                R.id.toolbar
            )

        ordersContainer = findViewById(R.id.ordersContainer)

        setSupportActionBar(toolbar)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        toolbar.setNavigationOnClickListener {
            finish()
        }

        if (SessionManager.currentUser == null) {
            finish()
            return
        }

        displayOrders()
    }

    override fun onResume() {
        super.onResume()

        if (::ordersContainer.isInitialized) {
            displayOrders()
        }
    }

    private fun displayOrders() {

        ordersContainer.removeAllViews()

        val user = SessionManager.currentUser ?: return

        val isStaff =
            user.role == UserRole.STAFF ||
                    user.role == UserRole.MANAGER

        val displayedOrders = if (isStaff) {

            OrderManager.orders.asReversed()

        } else {

            OrderManager.getCustomerOrders(user.studentNumber ?: "")
        }

        if (displayedOrders.isEmpty()) {

            val emptyText = TextView(this)

            emptyText.text = if (isStaff) {
                "No customer orders have been placed yet."
            } else {
                "You have not placed any orders yet."
            }

            emptyText.textSize = 17f
            emptyText.gravity = Gravity.CENTER
            emptyText.setTextColor(Color.DKGRAY)
            emptyText.setPadding(20, 60, 20, 60)

            ordersContainer.addView(emptyText)

            return
        }


        displayedOrders.forEach { order ->
            createOrderView(order, isStaff)
        }


    }

    private fun createOrderView(
        order: Order,
        isStaff: Boolean
    ) {

        val layout = LinearLayout(this)

        layout.orientation = LinearLayout.VERTICAL
        layout.setPadding(25, 25, 25, 25)
        layout.setBackgroundColor(Color.WHITE)

        val params = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )

        params.setMargins(5, 10, 5, 15)

        layout.layoutParams = params

        // ORDER NUMBER

        val orderNumber = TextView(this)

        orderNumber.text = "Order: ${order.orderId}"
        orderNumber.textSize = 20f
        orderNumber.setTextColor(green)
        orderNumber.setTypeface(null, Typeface.BOLD)

        layout.addView(orderNumber)

        // CUSTOMER NAME

        addText(layout, "Customer: ${order.customerName}")

        // STUDENT NUMBER

        addText(layout, "Student Number: ${order.studentNumber}")

        // PHONE

        if (isStaff) {
            addText(layout, "Phone: ${order.phone}")
        }

        // DATE

        addText(layout, "Date: ${order.dateTime}")

        // ITEMS

        val itemsTitle = TextView(this)

        itemsTitle.text = "ORDER ITEMS"
        itemsTitle.textSize = 17f
        itemsTitle.setTypeface(null, Typeface.BOLD)
        itemsTitle.setPadding(0, 15, 0, 5)

        layout.addView(itemsTitle)

        order.items.forEach { food ->

            addText(
                layout,
                "${food.name} - R%.2f".format(food.price)
            )
        }

        // TOTAL

        val total = TextView(this)

        total.text = "TOTAL: R%.2f".format(order.total)
        total.textSize = 19f
        total.setTypeface(null, Typeface.BOLD)
        total.setTextColor(green)
        total.setPadding(0, 15, 0, 15)

        layout.addView(total)

        // STATUS

        val status = TextView(this)

        status.text = "ORDER STATUS: ${order.status}"
        status.textSize = 18f
        status.setTypeface(null, Typeface.BOLD)
        status.setTextColor(
            if (order.status == "COMPLETED") {
                green
            } else {
                Color.rgb(190, 130, 0)
            }
        )

        layout.addView(status)

        // STAFF AND MANAGER CONTROLS

        if (isStaff) {

            val pendingButton = Button(this)

            pendingButton.text = "MARK AS PENDING"
            pendingButton.setTextColor(Color.WHITE)
            pendingButton.backgroundTintList =
                android.content.res.ColorStateList.valueOf(green)

            pendingButton.setOnClickListener {

                OrderManager.updateStatus(
                    order.orderId,
                    "PENDING"
                )

                Toast.makeText(
                    this,
                    "Order marked as pending",
                    Toast.LENGTH_SHORT
                ).show()

                displayOrders()
            }

            layout.addView(pendingButton)

            val completedButton = Button(this)

            completedButton.text = "MARK AS COMPLETED"
            completedButton.setTextColor(Color.WHITE)
            completedButton.backgroundTintList =
                android.content.res.ColorStateList.valueOf(green)

            completedButton.setOnClickListener {

                OrderManager.updateStatus(
                    order.orderId,
                    "COMPLETED"
                )

                Toast.makeText(
                    this,
                    "Order marked as completed",
                    Toast.LENGTH_SHORT
                ).show()

                displayOrders()
            }

            layout.addView(completedButton)
        }

        ordersContainer.addView(layout)

    }


    private fun addText(
        parent: LinearLayout,
        message: String
    ) {

        val text = TextView(this)

        text.text = message
        text.textSize = 15f
        text.setTextColor(Color.DKGRAY)
        text.setPadding(0, 5, 0, 5)

        parent.addView(text)
    }
}

