package com.example.clearsky.retrofit

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherService {
    @GET("data/2.5/weather?units=metric")
    suspend fun getCurrentWeather(@Query("q") city: String, @Query("appid") apiKey: String): Response<WeatherResponse>
}