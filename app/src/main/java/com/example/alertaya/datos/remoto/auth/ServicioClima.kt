package com.example.alertaya.datos.remoto.auth

import com.example.alertaya.data.model.response.ClimaRespuesta
import com.example.alertaya.data.model.response.PronosticoRespuesta
import retrofit2.http.GET
import retrofit2.http.Query


interface ServicioClima {
    @GET("data/2.5/weather")
    suspend fun obtenerClimaPorCiudad(
        @Query("q") ciudad: String,
        @Query("units") unidades: String = "metric",
        @Query("appid") apiKey: String,
        @Query("lang") idioma: String = "es"
    ): ClimaRespuesta

    @GET("data/2.5/forecast")
    suspend fun obtenerPronosticoPorCiudad(
        @Query("q") ciudad: String,
        @Query("units") unidades: String = "metric",
        @Query("appid") apiKey: String,
        @Query("lang") idioma: String = "es"
    ): PronosticoRespuesta
}