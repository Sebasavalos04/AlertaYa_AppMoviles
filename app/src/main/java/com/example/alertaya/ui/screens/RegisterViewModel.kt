package com.example.alertaya.ui.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.alertaya.data.model.request.RegisterRequest
import com.example.alertaya.datos.remoto.auth.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException

sealed class RegisterState {
    object Idle : RegisterState()
    object Loading : RegisterState()
    data class Success(val message: String) : RegisterState()
    data class Error(val error: String) : RegisterState()
}

class RegisterViewModel : ViewModel() {

    private val repository = AuthRepository()

    private val _state = MutableStateFlow<RegisterState>(RegisterState.Idle)
    val state = _state.asStateFlow()

    fun register(name: String, surname: String, email: String, password: String, role: String) {
        viewModelScope.launch {
            _state.value = RegisterState.Loading

            try {
                val request = RegisterRequest(
                    name = name,
                    surname = surname,
                    email = email,
                    password = password,
                    roleNames = listOf(role)
                )
                val response = repository.register(request)

                if (response.isSuccessful) {
                    _state.value = RegisterState.Success("Registro exitoso")
                } else if (response.code() == 409) {
                    _state.value = RegisterState.Success("Registro exitoso")
                } else {
                    _state.value = RegisterState.Error("Registro fallido: ${response.code()}")
                }


            } catch (e: Exception) {
                _state.value = RegisterState.Error("Error inesperado: ${e.message}")
            }
        }
    }

}
