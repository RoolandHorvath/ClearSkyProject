package com.example.clearsky.retrofit

data class WeatherResponse(
    val coord: Coordinates,
    val weather: List<WeatherDescription>,
    val main: Main,
    val wind: Wind,
    val sys: SystemInfo,
    val name: String,
    val dt: Long,
    val weatherMain: String
)

data class Coordinates(val lon: Double, val lat: Double)
data class WeatherDescription(val id: Int, val main: String, val description: String, val icon: String)
data class Main(val temp: Double, val feels_like: Double, val temp_min: Double, val temp_max: Double, val pressure: Int, val humidity: Int)
data class Wind(val speed: Double, val deg: Int)
data class SystemInfo(val country: String, val sunrise: Long, val sunset: Long)
