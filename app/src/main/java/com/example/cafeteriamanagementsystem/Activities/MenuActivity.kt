
package com.example.cafeteriamanagementsystem.activities

import android.content.res.ColorStateList
import android.graphics.Color
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import com.example.cafeteriamanagementsystem.adapters.FoodAdapter
import com.example.cafeteriamanagementsystem.databinding.ActivityMenuBinding
import com.example.cafeteriamanagementsystem.utils.MenuManager
import com.example.cafeteriamanagementsystem.utils.CartManager

class MenuActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMenuBinding

    private var selectedCategory = "Food"

    private val selectedColor = Color.rgb(23, 107, 69)
    private val normalColor = Color.WHITE

    private val selectedTextColor = Color.WHITE
    private val normalTextColor = Color.rgb(23, 107, 69)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMenuBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        binding.toolbar.setNavigationOnClickListener {
            finish()
        }

        binding.recyclerFood.layoutManager =
            GridLayoutManager(this, 2)

        binding.btnFood.setOnClickListener {
            selectCategory("Food")
        }

        binding.btnDrinks.setOnClickListener {
            selectCategory("Drinks")
        }

        binding.btnAddOns.setOnClickListener {
            selectCategory("Add-ons")
        }

        // SHOPPING CART SHORTCUT

        binding.btnCartShortcut.setOnClickListener {

            if (CartManager.cartItems.isNotEmpty()) {

                val intent = Intent(this, CartActivity::class.java)
                startActivity(intent)

            }
        }

        selectCategory("Food")

        updateCartShortcut()
    }

    override fun onResume() {
        super.onResume()

        if (::binding.isInitialized) {
            loadMenu()
            updateCartShortcut()
        }
    }

    private fun updateCartShortcut() {

        val hasItems = CartManager.cartItems.isNotEmpty()

        binding.btnCartShortcut.isEnabled = hasItems

        binding.btnCartShortcut.alpha =
            if (hasItems) 1.0f else 0.5f

        binding.btnCartShortcut.backgroundTintList =
            ColorStateList.valueOf(
                if (hasItems) selectedColor else Color.LTGRAY
            )

    }

    private fun selectCategory(category: String) {

        selectedCategory = category

        updateCategoryButtons()

        when (category) {

            "Food" -> {
                binding.tvSectionTitle.text = "Popular Menu"
            }

            "Drinks" -> {
                binding.tvSectionTitle.text = "Available Drinks"
            }

            "Add-ons" -> {
                binding.tvSectionTitle.text = "Extra Add-ons"
            }
        }

        loadMenu()
    }

    private fun updateCategoryButtons() {

        val buttons = mapOf(
            "Food" to binding.btnFood,
            "Drinks" to binding.btnDrinks,
            "Add-ons" to binding.btnAddOns
        )

        buttons.forEach { (category, button) ->

            val isSelected = category == selectedCategory

            button.backgroundTintList =
                ColorStateList.valueOf(
                    if (isSelected) selectedColor else normalColor
                )

            button.setTextColor(
                if (isSelected) selectedTextColor
                else normalTextColor
            )
        }
    }

    private fun loadMenu() {

        val filteredItems = MenuManager.foodItems.filter {
            it.category == selectedCategory
        }

        binding.recyclerFood.adapter = FoodAdapter(filteredItems) {
            updateCartShortcut()
        }
    }
}
