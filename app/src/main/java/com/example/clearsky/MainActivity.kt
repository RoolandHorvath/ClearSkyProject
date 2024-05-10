package com.example.clearsky

import android.os.Bundle
import android.view.View
import android.widget.ImageView
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


// Inšpirácia pre main activity dizajn od Coding With Evan - https://www.youtube.com/watch?v=gj0g1a75Lmo
// Lokalizácia podľa https://www.youtube.com/watch?v=XNFz97zqN-E
class MainActivity : AppCompatActivity() {
    private lateinit var swipeContainer: SwipeRefreshLayout
    private lateinit var viewModel: WeatherViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val database = AppDatabase.getInstance(this)
        val weatherDao = database.weatherDao()
        val repository = WeatherRepository.getInstance(weatherDao)
        val factory = ViewModelFactory(repository)
        viewModel = ViewModelProvider(this, factory).get(WeatherViewModel::class.java)

        val gearIcon = findViewById<ImageView>(R.id.gearIcon)
        gearIcon.setOnClickListener {
            toggleLanguage()
        }

        setupSwipeRefreshLayout()
        observeWeatherData()
        updateBackgroundBasedOnTimeOfDay()
    }

    private fun toggleLanguage() {
        val newLang = if (Locale.getDefault().language != "sk") "sk" else "en"
        setLocale(newLang)
        recreate()
    }

    private fun setLocale(languageCode: String) {
        val locale = Locale(languageCode)
        Locale.setDefault(locale)
        val config = resources.configuration
        config.setLocale(locale)
        resources.updateConfiguration(config, resources.displayMetrics)
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
            swipeContainer.isRefreshing = false
        }
    }

    private fun formatTimestamp(timestamp: Long): String {
        val date = Date(timestamp * 1000)
        val format = SimpleDateFormat("hh:mm a", Locale.ENGLISH)
        format.timeZone = TimeZone.getDefault()
        return format.format(date)
    }

    private fun populateWeatherData(weatherEntity: WeatherEntity) {
        val address = "${weatherEntity.cityName}"
        val updatedAtText = "Updated at: " + SimpleDateFormat("dd/MM/yyyy hh:mm a", Locale.ENGLISH).format(Date(weatherEntity.timestamp))
        val temp = "${weatherEntity.temperature.toInt()}°C"
        val sunriseTime = formatTimestamp(weatherEntity.sunrise)
        val sunsetTime = formatTimestamp(weatherEntity.sunset)
        val windSpeed = "${weatherEntity.windSpeed} m/s"
        val pressure = "${weatherEntity.pressure} hPa"
        val humidity = "${weatherEntity.humidity} %"

        findViewById<TextView>(R.id.address).text = address
        findViewById<TextView>(R.id.updated_at).text = updatedAtText
        findViewById<TextView>(R.id.temp).text = temp
        findViewById<TextView>(R.id.sunrise).text = sunriseTime
        findViewById<TextView>(R.id.sunset).text = sunsetTime
        findViewById<TextView>(R.id.wind).text = windSpeed
        findViewById<TextView>(R.id.pressure).text = pressure
        findViewById<TextView>(R.id.humidity).text = humidity
        findViewById<TextView>(R.id.status).text = weatherEntity.description

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
        val backgroundResId = if (hour in 6..18) R.drawable.gradient_bg else R.drawable.gradient_night
        swipeContainer.setBackgroundResource(backgroundResId)
    }

    private fun isNightTime(): Boolean {
        val hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
        return hour < 6 || hour >= 21
    }
}