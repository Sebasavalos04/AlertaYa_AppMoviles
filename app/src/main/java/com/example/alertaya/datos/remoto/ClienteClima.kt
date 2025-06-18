package com.example.alertaya.datos.remoto

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ClienteClima {
    private const val BASE_URL = "https://api.openweathermap.org/"

    val servicio: ServicioClima by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ServicioClima::class.java)
    }
}