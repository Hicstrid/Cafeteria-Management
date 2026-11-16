
package com.example.cafeteriamanagementsystem.activities

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.cafeteriamanagementsystem.databinding.ActivityReceiptBinding
import com.example.cafeteriamanagementsystem.utils.CartManager
import com.example.cafeteriamanagementsystem.utils.OrderManager

class ReceiptActivity : AppCompatActivity() {

    private lateinit var binding: ActivityReceiptBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityReceiptBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initialize saved orders
        OrderManager.initialize(this)

        // Toolbar
        setSupportActionBar(binding.toolbar)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        binding.toolbar.setNavigationOnClickListener {
            goToDashboard()
        }

        // GET order ID
        val orderId = intent.getStringExtra("ORDER_ID")

        // Find the saved order
        val order = OrderManager.orders.find {
            it.orderId == orderId
        }

        // Check whether order exists
        if (order == null) {

            binding.tvCustomerName.text = "Name: Not available"
            binding.tvPhone.text = "Phone: Not available"
            binding.tvReceiptNumber.text = "Order No: Not found"
            binding.tvItems.text = "Unable to retrieve order details."
            binding.tvReceiptTotal.text = "TOTAL: R0.00"

            return
        }

        //  customer info
        binding.tvCustomerName.text =
            "Name: ${order.customerName}"

        binding.tvPhone.text =
            "Phone: ${order.phone}"

        //  receipt number
        binding.tvReceiptNumber.text =
            "Order No: ${order.orderId}"

        // Display ordered items
        val receipt = StringBuilder()

        if (order.items.isEmpty()) {

            receipt.append("No items in order.")

        } else {

            order.items.forEach { food ->

                receipt.append(food.name)
                receipt.append("\n")

                receipt.append(
                    String.format("R%.2f", food.price)
                )

                receipt.append("\n\n")
            }
        }

        binding.tvItems.text = receipt.toString()

        // Display actual saved order total
        binding.tvReceiptTotal.text = String.format(
            "TOTAL: R%.2f",
            order.total
        )

        // Done button
        binding.btnDone.setOnClickListener {

            CartManager.clearCart()

            goToDashboard()
        }
    }

    // Return to dashboard
    private fun goToDashboard() {

        val intent = Intent(
            this,
            DashboardActivity::class.java
        )

        intent.flags =
            Intent.FLAG_ACTIVITY_CLEAR_TOP or
                    Intent.FLAG_ACTIVITY_SINGLE_TOP

        startActivity(intent)

        finish()
    }
}
