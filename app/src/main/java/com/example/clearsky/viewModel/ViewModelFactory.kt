package com.example.clearsky.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.clearsky.room.WeatherRepository

/**
 * Factory for creating instances of ViewModels. Necessary for passing repositories to ViewModels.
 * This pattern is recommended by Android's ViewModel architecture to inject dependencies.
 * Documentation: https://developer.android.com/topic/libraries/architecture/viewmodel
 */
class ViewModelFactory(private val repository: WeatherRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(WeatherViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return WeatherViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}