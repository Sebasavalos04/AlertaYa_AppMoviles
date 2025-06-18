package com.example.alertaya.data.model

import com.google.gson.annotations.SerializedName

data class ClimaRespuesta(
    @SerializedName("name")
    val nombreCiudad: String,

    @SerializedName("dt")
    val fechaUnix: Long,

    @SerializedName("weather")
    val listaClima: List<ClimaDescripcion>,

    @SerializedName("main")
    val informacionPrincipal: ClimaPrincipal,

    @SerializedName("wind")
    val viento: Viento
)

data class ClimaPrincipal(
    @SerializedName("temp")
    val temperatura: Double,

    @SerializedName("feels_like")
    val sensacionTermica: Double,

    @SerializedName("humidity")
    val humedad: Int,

    @SerializedName("pressure")
    val presion: Int
)

data class ClimaDescripcion(
    @SerializedName("description")
    val descripcion: String,

    @SerializedName("icon")
    val icono: String
)

data class Viento(
    @SerializedName("speed")
    val velocidad: Double
)