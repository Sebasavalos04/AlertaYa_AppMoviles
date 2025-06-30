package com.example.alertaya.datos.remoto.auth

import com.example.alertaya.data.model.request.LoginRequest
import com.example.alertaya.data.model.request.RegisterRequest
import com.example.alertaya.data.model.response.LoginResponse

import retrofit2.Response

class AuthRepository {

    private val api = RetrofitInstance.api

    suspend fun login(input: LoginRequest): Response<LoginResponse> {
        return api.login(input)
    }
    suspend fun register(input: RegisterRequest): Response<LoginResponse> {
        return api.register(input)
    }

}
