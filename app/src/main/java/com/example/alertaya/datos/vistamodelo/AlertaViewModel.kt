package com.example.alertaya.datos.vistamodelo

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.alertaya.datos.remoto.auth.ClienteClima
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class AlertaViewModel : ViewModel() {

    private val _nivelAlerta = MutableStateFlow("Cargando...")
    val nivelAlerta: StateFlow<String> = _nivelAlerta

    private val _recomendaciones = MutableStateFlow<List<String>>(emptyList())
    val recomendaciones: StateFlow<List<String>> = _recomendaciones

    fun cargarAlerta(ciudad: String, apiKey: String) {
        viewModelScope.launch {
            try {
                val respuesta = ClienteClima.servicio.obtenerPronosticoPorCiudad(
                    ciudad = ciudad,
                    apiKey = apiKey
                )

                val primerPronostico = respuesta.listaPronostico.firstOrNull()
                if (primerPronostico == null) {
                    _nivelAlerta.value = "Sin datos"
                    _recomendaciones.value = listOf("No se encontró información del pronóstico.")
                    return@launch
                }

                val lluvia = primerPronostico.lluvia?.cantidad3h ?: 8.0
                val nivel = getNivel(lluvia)

                _nivelAlerta.value = nivel
                _recomendaciones.value = getRecomendaciones(nivel)

            } catch (e: Exception) {
                Log.e("AlertaViewModel", "Error al obtener alerta", e)
                _nivelAlerta.value = "Error"
                _recomendaciones.value = listOf("No se pudo obtener la alerta.")
            }

        }
    }

    private fun getNivel(valor: Double): String {
        return when {
            valor <= 0.0 -> "Sin alerta"
            valor < 2.5 -> "Suave"
            valor <= 7.6 -> "Moderada"
            else -> "Fuerte"
        }
    }

    private fun getRecomendaciones(nivel: String): List<String> {
        return when (nivel) {
            "Fuerte" -> listOf(
                "Evita sembrar durante las próximas horas.",
                "Protege tus herramientas y maquinaria.",
                "Verifica el estado de los drenajes en los campos."
            )
            "Moderada" -> listOf(
                "Planifica con anticipación tareas sensibles.",
                "Supervisa cultivos vulnerables a la humedad."
            )
            "Suave" -> listOf(
                "Realiza tareas agrícolas con precaución.",
                "Monitorea los cambios en el clima."
            )
            "Sin alerta" -> listOf(
                "No se reportan lluvias intensas actualmente.",
                "Aprovecha el clima estable para labores de campo."
            )
            else -> listOf("No se pudo generar recomendaciones.")
        }
    }
}