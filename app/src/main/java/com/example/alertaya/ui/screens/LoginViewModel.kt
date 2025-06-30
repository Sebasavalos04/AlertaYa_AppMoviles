package com.example.alertaya.ui.screens

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.alertaya.data.model.request.LoginRequest
import com.example.alertaya.datos.remoto.auth.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

sealed class LoginState {
    object Idle : LoginState()
    object Loading : LoginState()
    data class Success(val token: String, val name: String) : LoginState()
    data class Error(val message: String) : LoginState()
}

class LoginViewModel : ViewModel() {

    private val repository = AuthRepository()

    private val _loginState = MutableStateFlow<LoginState>(LoginState.Idle)
    val loginState: StateFlow<LoginState> get() = _loginState

    fun login(email: String, password: String) {
        _loginState.value = LoginState.Loading

        viewModelScope.launch {
            try {
                val response = repository.login(LoginRequest(email, password))
                if (response.isSuccessful) {
                    val body = response.body()
                    val token = body?.token ?: ""
                    val name = body?.name ?: ""
                    Log.d("LoginViewModel", "Nombre recibido del backend: $name")
                    Log.d("LoginViewModel", "Nuevo estado: Success con name=$name")
                    _loginState.value = LoginState.Success(token, name)
                }
                else {
                    _loginState.value = LoginState.Error("Credenciales inválidas")
                }
            } catch (e: Exception) {
                _loginState.value = LoginState.Error("Error de red: ${e.localizedMessage}")
            }
        }
    }
}
