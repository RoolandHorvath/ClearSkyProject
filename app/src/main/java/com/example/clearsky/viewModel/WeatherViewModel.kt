package com.example.clearsky.viewModel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.clearsky.room.WeatherEntity
import com.example.clearsky.room.WeatherRepository
import kotlinx.coroutines.launch

class WeatherViewModel(private val repository: WeatherRepository) : ViewModel() {
    private val _loading = MutableLiveData<Boolean>()
    val loading: LiveData<Boolean> = _loading
    val weatherData: LiveData<WeatherEntity?> = repository.getLatestWeatherData()

    fun refreshWeatherData() {
        _loading.postValue(true)
        viewModelScope.launch {
            try {
                repository.refreshWeatherData("Kosice, SK", "8eda2e31b68afa4c7f28c172514e642d")
            } catch (e: Exception) {
                Log.e("WeatherViewModel", "Error refreshing data", e)
            } finally {
                _loading.postValue(false)
            }
        }
    }
}