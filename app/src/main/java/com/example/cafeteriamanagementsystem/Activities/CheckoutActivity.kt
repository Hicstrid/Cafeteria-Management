
package com.example.cafeteriamanagementsystem.activities

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.cafeteriamanagementsystem.databinding.ActivityCheckoutBinding
import com.example.cafeteriamanagementsystem.utils.CartManager
import com.example.cafeteriamanagementsystem.utils.OrderManager
import com.example.cafeteriamanagementsystem.utils.SessionManager
import com.example.cafeteriamanagementsystem.models.UserRole

class CheckoutActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCheckoutBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityCheckoutBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initialize order storage
        OrderManager.initialize(this)

        // Toolbar
        setSupportActionBar(binding.toolbar)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        binding.toolbar.setNavigationOnClickListener {
            finish()
        }

        // Display order total
        binding.tvCheckoutTotal.text = String.format(
            "Order Total: R%.2f",
            CartManager.getTotal()
        )

        // Place order button
        binding.btnPlaceOrder.setOnClickListener {

            val name = binding.etName.text.toString().trim()

            val studentNumber =
                binding.etStudentNumber.text.toString().trim()

            val phone = binding.etPhone.text.toString().trim()

            // Validate required fields
            if (
                name.isEmpty() ||
                studentNumber.isEmpty() ||
                phone.isEmpty()
            ) {
                Toast.makeText(
                    this,
                    "Please complete all required fields",
                    Toast.LENGTH_SHORT
                ).show()

                return@setOnClickListener
            }

            // Validate registered student number
            val currentUser = SessionManager.currentUser

            if (
                currentUser != null &&
                currentUser.role == UserRole.STUDENT &&
                !studentNumber.equals(
                    currentUser.studentNumber,
                    ignoreCase = true
                )
            ) {
                binding.etStudentNumber.error =
                    "Enter your registered User ID"

                binding.etStudentNumber.requestFocus()

                return@setOnClickListener
            }

            // Validate phone number
            if (!phone.matches(Regex("^0[0-9]{9}$"))) {

                binding.etPhone.error =
                    "Phone number must start with 0 and must be exactly 10 numbers"

                binding.etPhone.requestFocus()

                return@setOnClickListener
            }

            // Check whether cart is empty
            if (CartManager.cartItems.isEmpty()) {

                Toast.makeText(
                    this,
                    "Your cart is empty",
                    Toast.LENGTH_SHORT
                ).show()

                return@setOnClickListener
            }

            // Make a copy of cart items
            val orderedItems = CartManager.cartItems.toList()

            // Calculate total
            val total = orderedItems.sumOf { it.price }

            // Save order permanently
            val order = OrderManager.placeOrder(
                customerName = name,
                studentNumber = studentNumber,
                phone = phone,
                items = orderedItems,
                total = total
            )

            // Clear shopping cart after successfully saving order
            CartManager.clearCart()

            // Open receipt
            val receiptIntent = Intent(
                this,
                ReceiptActivity::class.java
            )

            receiptIntent.putExtra(
                "ORDER_ID",
                order.orderId
            )

            startActivity(receiptIntent)

            Toast.makeText(
                this,
                "Order ${order.orderId} placed successfully",
                Toast.LENGTH_LONG
            ).show()

            finish()
        }
    }
}
