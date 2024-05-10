package com.example.clearsky.room

import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import com.example.clearsky.retrofit.RetrofitClient
import com.example.clearsky.retrofit.WeatherResponse
import com.example.clearsky.retrofit.toWeatherEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

// Repository class handling data operations, providing a clean API to the rest of the app.
// For more details on Repository pattern: https://developer.android.com/jetpack/guide
class WeatherRepository private constructor(private val weatherDao: WeatherDao) {
    companion object {
        @Volatile
        private var instance: WeatherRepository? = null

        fun getInstance(weatherDao: WeatherDao): WeatherRepository =
            instance ?: synchronized(this) {
                instance ?: WeatherRepository(weatherDao).also { instance = it }
            }
    }

    // Fetches latest weather data from the database. This is executed in the IO dispatcher
    // for non-blocking database access. Reference: https://kotlinlang.org/docs/coroutines-basics.html#async-code
    suspend fun getLatestWeatherData(): WeatherEntity? {
        return withContext(Dispatchers.IO) {
            weatherDao.getLastWeatherData()
        }
    }

    // Refreshes weather data by making a network call and updating the local database.
    // Uses Retrofit for network calls: https://square.github.io/retrofit/
    suspend fun refreshWeatherData(city: String, apiKey: String): WeatherEntity? = withContext(Dispatchers.IO) {
        try {
            val response = RetrofitClient.instance.getCurrentWeather(city, apiKey)
            if (response.isSuccessful && response.body() != null) {
                val weather = response.body()!!.toWeatherEntity()
                weatherDao.insertWeather(weather)
                return@withContext weather
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return@withContext null
    }
}