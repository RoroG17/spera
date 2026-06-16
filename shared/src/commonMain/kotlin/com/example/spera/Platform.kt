package com.example.spera

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform