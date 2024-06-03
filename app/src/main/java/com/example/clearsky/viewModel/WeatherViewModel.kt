package com.example.clearsky.viewModel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.clearsky.room.WeatherEntity
import com.example.clearsky.room.WeatherRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

// https://developer.android.com/topic/libraries/architecture/viewmodel
// https://developer.android.com/topic/libraries/architecture/livedata
class WeatherViewModel(private val repository: WeatherRepository) : ViewModel() {
    private val _weatherData = MutableLiveData<WeatherEntity?>()
    val weatherData: LiveData<WeatherEntity?> = _weatherData
    private val _loading = MutableLiveData<Boolean>()
    val loading: LiveData<Boolean> = _loading

    init {
        loadInitialData()
    }

    private fun loadInitialData() {
        viewModelScope.launch(Dispatchers.IO) {
            _weatherData.postValue(repository.getLatestWeatherData())
        }
    }

    fun refreshWeatherData() {
        _loading.value = true
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val refreshedData = repository.refreshWeatherData("Kosice, SK", "8eda2e31b68afa4c7f28c172514e642d")
                refreshedData?.let {
                    _weatherData.postValue(it)
                }
            } catch (e: Exception) {
                Log.e("WeatherViewModel", "Error refreshing data", e)
            } finally {
                _loading.postValue(false)
            }
        }
    }

}