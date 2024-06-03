package com.example.clearsky.room

import androidx.lifecycle.LiveData
import com.example.clearsky.retrofit.RetrofitClient
import com.example.clearsky.retrofit.WeatherResponse
import com.example.clearsky.retrofit.toWeatherEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

// Experimentovanie s Jetpack
// https://developer.android.com/jetpack/guide
class WeatherRepository private constructor(private val weatherDao: WeatherDao) {
    companion object {
        @Volatile
        private var instance: WeatherRepository? = null

        fun getInstance(weatherDao: WeatherDao): WeatherRepository =
            instance ?: synchronized(this) {
                instance ?: WeatherRepository(weatherDao).also { instance = it }
            }
    }

    // https://kotlinlang.org/docs/coroutines-basics.html#async-code
    suspend fun getLatestWeatherData(): WeatherEntity? {
        return withContext(Dispatchers.IO) {
            weatherDao.getLastWeatherData()
        }
    }

    // https://square.github.io/retrofit/
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