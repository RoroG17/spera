package com.example.spera.models

data class User (
    val id: String,
    val name: String,
    val firstName: String,
    val pseudo: String,
    val mail: String,
    val height: Int,
    val weight: Int,
    val activities: List<Activity>,
    val followers: List<User>,
    val following: List<User>,
    val favoriteRecipes: List<Recipe>,
    val recipes: List<Recipe>,
    val trainings: List<Training>,
) {
}