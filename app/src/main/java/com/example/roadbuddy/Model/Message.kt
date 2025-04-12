package com.example.roadbuddy.Model

import java.util.Date

data class Message(
    val text: String = "",
    val senderId: String = "",
    val senderName: String = "",
    val timestamp: Date = Date()
) 