package com.example.clearsky.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.liveData
import com.example.clearsky.retrofit.RetrofitClient
import kotlinx.coroutines.Dispatchers

class ViewModelFactory(private val city: String, private val apiKey: String) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(WeatherViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return WeatherViewModel(city, apiKey) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}