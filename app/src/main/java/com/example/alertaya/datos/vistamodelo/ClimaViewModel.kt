package com.example.alertaya.datos.vistamodelo

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.alertaya.data.model.response.ClimaRespuesta
import com.example.alertaya.datos.remoto.auth.ClienteClima
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ClimaViewModel : ViewModel() {

    private val _clima = MutableStateFlow<ClimaRespuesta?>(null)
    val clima: StateFlow<ClimaRespuesta?> = _clima

    fun cargarClima(ciudad: String, apiKey: String) {
        viewModelScope.launch {
            try {
                val respuesta = ClienteClima.servicio.obtenerClimaPorCiudad(
                    ciudad = ciudad,
                    apiKey = apiKey
                )
                _clima.value = respuesta
            } catch (e: Exception) {
                e.printStackTrace()
                _clima.value = null
            }
        }
    }
}