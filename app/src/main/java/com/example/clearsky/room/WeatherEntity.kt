package com.example.clearsky.room

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.clearsky.retrofit.WeatherResponse

// Entity class represents a table within the database. Room uses this for database operations.
@Entity(tableName = "weather_table")
data class WeatherEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    @ColumnInfo(name = "city_name") val cityName: String,
    @ColumnInfo(name = "temperature") val temperature: Double,
    @ColumnInfo(name = "description") val description: String,
    @ColumnInfo(name = "timestamp") val timestamp: Long = System.currentTimeMillis(),
    @ColumnInfo(name = "sunrise") val sunrise: Long,
    @ColumnInfo(name = "sunset") val sunset: Long,
    @ColumnInfo(name = "wind_speed") val windSpeed: Double,
    @ColumnInfo(name = "pressure") val pressure: Int,
    @ColumnInfo(name = "humidity") val humidity: Int
)

// Extension function to map network response to database entity. Inspired by
// Retrofit and Room integration guide on Android developers.
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