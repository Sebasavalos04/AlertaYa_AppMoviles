package com.example.alertaya.datos.vistamodelo

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.alertaya.data.model.PronosticoPorHora
import com.example.alertaya.data.model.PronosticoRespuesta
import com.example.alertaya.datos.remoto.ClienteClima
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class PronosticoViewModel : ViewModel() {

    private val _pronostico = MutableStateFlow<List<PronosticoPorHora>>(emptyList())
    val pronostico: StateFlow<List<PronosticoPorHora>> = _pronostico

    fun cargarPronostico(ciudad: String, apiKey: String) {
        viewModelScope.launch {
            try {
                val respuesta: PronosticoRespuesta = ClienteClima.servicio.obtenerPronosticoPorCiudad(
                    ciudad = ciudad,
                    apiKey = apiKey
                )
                _pronostico.value = respuesta.listaPronostico
            } catch (e: Exception) {
                e.printStackTrace()
                _pronostico.value = emptyList()
            }
        }
    }
}