package com.example.clearsky

import android.content.res.Configuration
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ProgressBar
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.clearsky.room.AppDatabase
import com.example.clearsky.room.WeatherEntity
import com.example.clearsky.room.WeatherRepository
import com.example.clearsky.viewModel.ViewModelFactory
import com.example.clearsky.viewModel.WeatherViewModel
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity() {
    private lateinit var swipeContainer: SwipeRefreshLayout
    private lateinit var viewModel: WeatherViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Setup Database, Repository, ViewModelFactory, and ViewModel
        val database = AppDatabase.getInstance(this)
        val weatherDao = database.weatherDao()
        val repository = WeatherRepository.getInstance(weatherDao)
        val factory = ViewModelFactory(repository)
        viewModel = ViewModelProvider(this, factory).get(WeatherViewModel::class.java)

        // Setup SwipeRefreshLayout and observe weather data
        setupSwipeRefreshLayout()
        observeWeatherData()
        updateBackgroundBasedOnTimeOfDay()
    }

    private fun setupSwipeRefreshLayout() {
        swipeContainer = findViewById(R.id.swipeContainer)
        swipeContainer.setOnRefreshListener {
            viewModel.refreshWeatherData()
            updateBackgroundBasedOnTimeOfDay()
        }
    }

    private fun observeWeatherData() {
        viewModel.weatherData.observe(this) { weatherEntity ->
            weatherEntity?.let {
                populateWeatherData(it)
                updateBackgroundBasedOnWeather(it.description)
            }
            swipeContainer.isRefreshing = false  // Ensure this is always called
        }
    }

    private fun populateWeatherData(weatherEntity: WeatherEntity) {
        val address = "${weatherEntity.cityName}"
        val updatedAtText = "Updated at: " + SimpleDateFormat("dd/MM/yyyy hh:mm a", Locale.ENGLISH).format(Date(weatherEntity.timestamp))
        val temp = "${weatherEntity.temperature.toInt()}°C"

        findViewById<TextView>(R.id.address).text = address
        findViewById<TextView>(R.id.updated_at).text = updatedAtText
        findViewById<TextView>(R.id.temp).text = temp

        findViewById<ProgressBar>(R.id.loader).visibility = View.GONE
        findViewById<RelativeLayout>(R.id.mainContainer).visibility = View.VISIBLE
    }

    private fun updateBackgroundBasedOnWeather(description: String) {
        val backgroundResId = when {
            description.contains("rain", ignoreCase = true) -> R.drawable.gradient_rain
            isNightTime() -> R.drawable.gradient_night
            description.contains("clear", ignoreCase = true) -> R.drawable.gradient_bg
            else -> R.drawable.gradient_bg
        }
        findViewById<RelativeLayout>(R.id.mainContainer).setBackgroundResource(backgroundResId)
    }

    private fun updateBackgroundBasedOnTimeOfDay() {
        val hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
        val backgroundResId = if (hour in 6..18) {
            R.drawable.gradient_bg
        } else {
            R.drawable.gradient_night
        }
        swipeContainer.setBackgroundResource(backgroundResId)
    }

    private fun isNightTime(): Boolean {
        val hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
        return hour < 6 || hour >= 21
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            Log.d("MainActivity", "Landscape orientation")
        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
            Log.d("MainActivity", "Portrait orientation")
        }
        // Optionally refresh the layout based on new configuration
        updateUIBasedOnConfiguration(newConfig)
    }

    private fun updateUIBasedOnConfiguration(config: Configuration) {
        // Here, you can update the layout or any other element based on orientation
    }
}
