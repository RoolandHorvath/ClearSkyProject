package com.example.clearsky.retrofit

import com.example.clearsky.room.WeatherEntity

// Inšpirované podľa https://developer.android.com/topic/architecture
// Parsovanie JSON odpovede z API
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

// Konvertovanie WeatherResponse modelu do databázovej entity WeatherEntity
fun WeatherResponse.toWeatherEntity(): WeatherEntity {
    return WeatherEntity(
        cityName = this.name,
        temperature = this.main.temp,
        description = this.weather.firstOrNull()?.description ?: "No description",
        timestamp = System.currentTimeMillis(),
        sunrise = this.sys.sunrise,
        sunset = this.sys.sunset,
        windSpeed = this.wind.speed,
        pressure = this.main.pressure,
        humidity = this.main.humidity
    )
}