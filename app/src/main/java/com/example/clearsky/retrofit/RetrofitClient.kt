package com.example.clearsky.retrofit

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

/**
 * Singleton to manage Retrofit operations for network requests.
 * Retrofit setup is guided by tutorials from CoderSee and various YouTube tutorials.
 * See https://codersee.com/retrofit-with-kotlin-the-ultimate-guide/
 * and https://www.youtube.com/watch?v=4JGvDUlfk7Y for basic setup.
 */
object RetrofitClient {
    private const val BASE_URL = "https://api.openweathermap.org/"

    // Lazy initialization of Retrofit to ensure it is created at its first usage
    val instance: WeatherService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())  // Use GSON for JSON parsing
            .build()
            .create(WeatherService::class.java)
    }
}
