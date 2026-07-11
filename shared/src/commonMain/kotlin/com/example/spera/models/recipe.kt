package com.example.spera.models

data class Recipe(
    val id: String,
    val name: String,
    val date: String,
    val users: User,
    val description: String,
    val photo: String,
    val ingredients: List<IngredientQuantity>,
    val timePreparation: Int,
    val timeCooking: Int,
    val likes: List<User>,
    val comments: List<Comment<Recipe>>,
)

data class IngredientQuantity(val ingredient: Ingredient, val quantity: Int)