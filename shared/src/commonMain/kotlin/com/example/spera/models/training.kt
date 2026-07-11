package com.example.spera.models

data class Training (
    val id: String,
    val date: String,
    val name: String,
    val description: String,
    val photo: String,
    val users: User,
    val likes: List<User>,
    val comments: List<Comment<Training>>,
) {
}