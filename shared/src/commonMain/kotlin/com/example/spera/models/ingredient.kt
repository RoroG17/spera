package com.example.spera.models

data class Ingredient (
    val id: String,
    val name: String,
    val photo: String,
    val allergens: List<String>,
    val unit: String,
) {
}