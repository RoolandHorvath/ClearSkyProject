package com.example.clearsky.retrofit

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

// https://codersee.com/retrofit-with-kotlin-the-ultimate-guide/
// https://www.youtube.com/watch?v=4JGvDUlfk7Y
object RetrofitClient {
    private const val BASE_URL = "https://api.openweathermap.org/"

    val instance: WeatherService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(WeatherService::class.java)
    }
}
