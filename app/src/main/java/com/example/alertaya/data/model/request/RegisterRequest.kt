package com.example.alertaya.data.model.request

data class RegisterRequest(
    val email: String,
    val password: String,
    val surname: String,
    val name: String,
    val roleNames: List<String>
)
