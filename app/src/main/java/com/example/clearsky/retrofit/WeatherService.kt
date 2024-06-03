package com.example.clearsky.retrofit

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

// https://www.youtube.com/watch?v=5gFrXGbQsc8
// https://codersee.com/retrofit-with-kotlin-the-ultimate-guide/
interface WeatherService {
    @GET("data/2.5/weather")
    suspend fun getCurrentWeather(
        @Query("q") city: String,
        @Query("appid") apiKey: String,
        @Query("units") units: String = "metric"
    ): Response<WeatherResponse>
}
