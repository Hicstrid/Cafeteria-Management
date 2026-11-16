
package com.example.cafeteriamanagementsystem.utils

import com.example.cafeteriamanagementsystem.R
import com.example.cafeteriamanagementsystem.models.Food

object MenuManager {

    val foodItems = mutableListOf(

        // FOOD
        Food(1, "Chicken Burger", "Chicken burger with lettuce, cheese and sauce", 45.00, R.drawable.veggie_burger, "Food"),
        Food(2, "Pizza", "Cheesy pizza with tomato and mixed toppings", 65.00, R.drawable.pizza, "Food"),
        Food(3, "Chicken Meal", "Fried chicken served with chips", 55.00, R.drawable.shawarma, "Food"),
        Food(4, "French Fries", "Crispy golden potato chips", 25.00, R.drawable.chips, "Food"),
        Food(5, "Chicken Sandwich", "Fresh sandwich with chicken and lettuce", 35.00, R.drawable.burger, "Food"),
        Food(6, "Hotdog", "Fresh hotdog with sauce", 30.00, R.drawable.kota, "Food"),

        // DRINKS
        Food(7, "Coca-Cola", "Refreshing cold Coca-Cola", 15.00, R.drawable.cooldrinks, "Drinks"),
        Food(8, "Fanta", "Refreshing orange flavoured drink", 15.00, R.drawable.energy_drinks, "Drinks"),
        Food(9, "Sprite", "Refreshing lemon flavoured drink", 15.00, R.drawable.flavoured_water, "Drinks"),
        Food(10, "Water", "500ml bottled water", 12.00, R.drawable.spring_water, "Drinks"),
        Food(11, "Fruit Juice", "Refreshing fruit juice", 18.00, R.drawable.coffee, "Drinks"),
        Food(12, "Ice Tea", "Cold refreshing iced tea", 20.00, R.drawable.coffee, "Drinks"),

        // ADD-ONS
        Food(13, "Extra Cheese", "Additional cheese topping", 10.00, R.drawable.coffee, "Add-ons"),
        Food(14, "Extra Sauce", "Choice of additional sauce", 5.00, R.drawable.coffee, "Add-ons"),
        Food(15, "Extra Chips", "Additional portion of chips", 15.00, R.drawable.chips, "Add-ons"),
        Food(16, "Chicken Strips", "Crispy chicken strips", 20.00, R.drawable.coffee, "Add-ons"),
        Food(17, "Onion Rings", "Crispy golden onion rings", 18.00, R.drawable.coffee, "Add-ons")
    )

    private var nextId = 18

    fun addFood(
        name: String,
        description: String,
        price: Double,
        category: String = "Food"
    ) {
        foodItems.add(
            Food(
                id = nextId++,
                name = name,
                description = description,
                price = price,
                imageResId = R.drawable.coffee,
                category = category
            )
        )
    }

    fun updateFood(
        id: Int,
        name: String,
        description: String,
        price: Double
    ) {
        val index = foodItems.indexOfFirst {
            it.id == id
        }

        if (index != -1) {
            val oldFood = foodItems[index]

            foodItems[index] = oldFood.copy(
                name = name,
                description = description,
                price = price
            )
        }
    }

    fun deleteFood(id: Int) {
        foodItems.removeAll {
            it.id == id
        }
    }
}
