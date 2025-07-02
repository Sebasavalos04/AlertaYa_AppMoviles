package com.example.alertaya.data.model.response

data class LoginResponse(
    val token: String,
    val name: String,
    val email: String,
    val userId: Long
)
