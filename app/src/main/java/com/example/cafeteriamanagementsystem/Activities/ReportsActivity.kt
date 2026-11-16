
package com.example.cafeteriamanagementsystem.activities

import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.view.Gravity
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.cafeteriamanagementsystem.R
import com.example.cafeteriamanagementsystem.models.Order
import com.example.cafeteriamanagementsystem.models.UserRole
import com.example.cafeteriamanagementsystem.utils.OrderManager
import com.example.cafeteriamanagementsystem.utils.SessionManager

class ReportsActivity : AppCompatActivity() {

    private lateinit var reportsContainer: LinearLayout
    private lateinit var categoryReportsContainer: LinearLayout

    private val green = Color.rgb(23, 107, 69)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Manager access only
        if (SessionManager.currentUser?.role != UserRole.MANAGER) {
            finish()
            return
        }

        setContentView(R.layout.activity_reports)

        // Initialize saved orders
        OrderManager.initialize(this)

        // Connect layout elements
        reportsContainer = findViewById(R.id.reportsContainer)

        categoryReportsContainer =
            findViewById(R.id.categoryReportsContainer)

        // Toolbar
        val toolbar =
            findViewById<com.google.android.material.appbar.MaterialToolbar>(
                R.id.toolbar
            )

        setSupportActionBar(toolbar)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        toolbar.setNavigationOnClickListener {
            finish()
        }

        displayReports()
    }

    override fun onResume() {
        super.onResume()

        if (::reportsContainer.isInitialized) {
            displayReports()
        }
    }

    // Display all reports
    private fun displayReports() {

        reportsContainer.removeAllViews()
        categoryReportsContainer.removeAllViews()

        val orders = OrderManager.orders.toList()

        // Calculate summary information
        val totalOrders = orders.size

        val totalRevenue = orders.sumOf {
            it.total
        }

        val pendingOrders = orders.count {
            it.status == "PENDING"
        }

        val completedOrders = orders.count {
            it.status == "COMPLETED"
        }

        // Display summary
        findViewById<TextView>(R.id.tvTotalOrders).text =
            "Total Orders: $totalOrders"

        findViewById<TextView>(R.id.tvTotalRevenue).text =
            "Total Revenue: R%.2f".format(totalRevenue)

        findViewById<TextView>(R.id.tvPendingOrders).text =
            "Pending Orders: $pendingOrders"

        findViewById<TextView>(R.id.tvCompletedOrders).text =
            "Completed Orders: $completedOrders"

        // Empty report
        if (orders.isEmpty()) {

            addText(
                reportsContainer,
                "No customer orders have been placed yet.",
                16f,
                Color.GRAY
            )

            addText(
                categoryReportsContainer,
                "No category sales available yet.",
                15f,
                Color.GRAY
            )

            return
        }

        // CATEGORY SALES REPORT
        val categorySales = mutableMapOf<String, Int>()
        val categoryRevenue = mutableMapOf<String, Double>()

        orders.forEach { order ->

            order.items.forEach { food ->

                val category = food.category.ifBlank {
                    "Uncategorized"
                }

                categorySales[category] =
                    (categorySales[category] ?: 0) + 1

                categoryRevenue[category] =
                    (categoryRevenue[category] ?: 0.0) + food.price
            }
        }

        categorySales.forEach { (category, quantity) ->

            val revenue = categoryRevenue[category] ?: 0.0

            val categoryCard = LinearLayout(this)

            categoryCard.orientation = LinearLayout.VERTICAL
            categoryCard.setPadding(20, 18, 20, 18)
            categoryCard.setBackgroundColor(Color.WHITE)
            categoryCard.elevation = 3f

            val params = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )

            params.setMargins(0, 5, 0, 12)

            categoryCard.layoutParams = params

            addText(
                categoryCard,
                category,
                18f,
                green,
                true
            )

            addText(
                categoryCard,
                "Items Sold: $quantity",
                15f,
                Color.DKGRAY
            )

            addText(
                categoryCard,
                "Category Revenue: R%.2f".format(revenue),
                15f,
                green,
                true
            )

            categoryReportsContainer.addView(categoryCard)
        }

        // INDIVIDUAL ORDER REPORTS
        orders.asReversed().forEach { order ->

            createOrderReport(order)
        }
    }

    // Create individual order report
    private fun createOrderReport(order: Order) {

        val card = LinearLayout(this)

        card.orientation = LinearLayout.VERTICAL
        card.setPadding(20, 20, 20, 20)
        card.setBackgroundColor(Color.WHITE)
        card.elevation = 3f

        val params = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )

        params.setMargins(0, 5, 0, 15)

        card.layoutParams = params

        // Order details
        addText(
            card,
            "ORDER: ${order.orderId}",
            19f,
            green,
            true
        )

        addText(
            card,
            "Customer: ${order.customerName}",
            15f,
            Color.DKGRAY
        )

        addText(
            card,
            "Student Number: ${order.studentNumber}",
            15f,
            Color.DKGRAY
        )

        addText(
            card,
            "Phone: ${order.phone}",
            15f,
            Color.DKGRAY
        )

        addText(
            card,
            "Date: ${order.dateTime}",
            14f,
            Color.GRAY
        )

        addText(
            card,
            "ORDERED ITEMS",
            16f,
            green,
            true
        )

        // Display individual items
        order.items.forEach { food ->

            addText(
                card,
                "${food.name} (${food.category}) - R%.2f".format(food.price),
                14f,
                Color.DKGRAY
            )
        }

        // Order total
        addText(
            card,
            "TOTAL: R%.2f".format(order.total),
            18f,
            green,
            true
        )

        // Order status
        addText(
            card,
            "STATUS: ${order.status}",
            15f,
            if (order.status == "COMPLETED") {
                green
            } else {
                Color.rgb(190, 130, 0)
            },
            true
        )

        reportsContainer.addView(card)
    }

    // Reusable text function
    private fun addText(
        parent: LinearLayout,
        message: String,
        size: Float,
        color: Int,
        bold: Boolean = false
    ) {

        val text = TextView(this)

        text.text = message
        text.textSize = size
        text.setTextColor(color)
        text.setPadding(0, 5, 0, 5)

        if (bold) {
            text.setTypeface(null, Typeface.BOLD)
        }

        parent.addView(text)
    }
}
