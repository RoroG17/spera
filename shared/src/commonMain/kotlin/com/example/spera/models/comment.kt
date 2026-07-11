package com.example.spera.models

data class Comment<T>(
    val id: String,
    val content: String,
    val date: String,
    val user: User,
    val type: T,
)