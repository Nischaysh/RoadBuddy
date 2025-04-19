package com.example.roadbuddy.Model

data class Message(
    val text: String = "",
    val senderId: String = "",
    val senderName: String = "",
    val timestamp: Long = System.currentTimeMillis(),
    val seen: Boolean = false
) 