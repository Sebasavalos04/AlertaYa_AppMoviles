package com.example.alertaya.datos.remoto.auth
import com.example.alertaya.data.model.request.LoginRequest
import com.example.alertaya.data.model.response.LoginResponse
import com.example.alertaya.data.model.request.RegisterRequest

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

data class LoginRequest(
    val email: String,
    val password: String
)

data class AuthResponse(
    val status: String,
    val message: String,
    val data: Data?
)

data class Data(
    val token: String,
    val userId: Long
)

interface AuthApi {
    @POST("api/auth/login")
    suspend fun login(@Body input: LoginRequest): Response<LoginResponse>

    @POST("api/auth/register")
    suspend fun register(@Body request: RegisterRequest): Response<LoginResponse>
}
