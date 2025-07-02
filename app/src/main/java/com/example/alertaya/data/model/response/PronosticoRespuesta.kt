package com.example.alertaya.data.model.response

import com.google.gson.annotations.SerializedName

data class PronosticoRespuesta(
    @SerializedName("list")
    val listaPronostico: List<PronosticoPorHora>
)

data class PronosticoPorHora(
    @SerializedName("dt")
    val fechaUnix: Long,

    @SerializedName("main")
    val informacionPrincipal: ClimaPrincipal,

    @SerializedName("weather")
    val clima: List<ClimaDescripcion>,

    @SerializedName("wind")
    val viento: Viento,

    @SerializedName("rain")
    val lluvia: Lluvia?,

    @SerializedName("dt_txt")
    val fechaTexto: String
)

