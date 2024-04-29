package com.example.clearsky.viewModel

import android.util.Log
import androidx.lifecycle.*
import com.example.clearsky.retrofit.RetrofitClient
import com.example.clearsky.retrofit.WeatherResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class WeatherViewModel(private val city: String, private val apiKey: String) : ViewModel() {
    private val tag = "WeatherApp"
    private val _weatherData = MutableLiveData<WeatherResponse?>()
    val weatherData: LiveData<WeatherResponse?>
        get() = _weatherData

    init {
        refreshWeatherData()
    }

    fun refreshWeatherData() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val response = RetrofitClient.instance.getCurrentWeather(city, apiKey)
                Log.d(tag, "API Call successful: ${response.isSuccessful}")
                if (response.isSuccessful) {
                    Log.d(tag, "Weather Data: ${response.body()}")
                    _weatherData.postValue(response.body())
                } else {
                    Log.d(tag, "API Call failed: ${response.errorBody()?.string()}")
                    _weatherData.postValue(null)
                }
            } catch (e: Exception) {
                Log.e(tag, "API Call error: $e")
                _weatherData.postValue(null)
            }
        }
    }

}
