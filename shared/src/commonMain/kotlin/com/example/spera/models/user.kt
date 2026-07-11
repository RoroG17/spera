package com.example.spera.models

data class User (
    val id: String,
    val name: String,
    val firstName: String,
    val pseudo: String,
    val mail: String,
    val height: Int,
    val weight: Int,
    val activity: Activity,
    val followers: List<User>,
    val following: List<User>
) {
}