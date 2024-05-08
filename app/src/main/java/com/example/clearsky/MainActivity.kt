package com.example.clearsky

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ProgressBar
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.clearsky.retrofit.WeatherResponse
import com.example.clearsky.viewModel.ViewModelFactory
import com.example.clearsky.viewModel.WeatherViewModel
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity() {
    private val city: String = "kosice,sk"
    private val api: String = "8eda2e31b68afa4c7f28c172514e642d"
    private val tag = "WeatherApp"
    private lateinit var swipeContainer: SwipeRefreshLayout
    private lateinit var viewModel: WeatherViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        Log.d(tag, "onCreate: Initializing ViewModel")

        val factory = ViewModelFactory(city, api)
        viewModel = ViewModelProvider(this, factory).get(WeatherViewModel::class.java)

        swipeContainer = findViewById(R.id.swipeContainer)
        swipeContainer.setOnRefreshListener {
            viewModel.refreshWeatherData()
            // Don't set false here
            // swipeContainer.isRefreshing = false
        }

        viewModel.weatherData.observe(this, androidx.lifecycle.Observer { weatherResponse ->
            if (weatherResponse != null) {
                populateWeatherData(weatherResponse)
                updateBackground(weatherResponse)
                updateWeatherDescription(weatherResponse)
                swipeContainer.isRefreshing = false
            }
        })

        Log.d(tag, "onCreate: Calling refreshWeatherData")
        viewModel.refreshWeatherData()
    }

    private fun populateWeatherData(weatherResponse: WeatherResponse) {
        try {
            Log.d(tag, "populateWeatherData: Populating weather data")

            val address = "${weatherResponse.name}, ${weatherResponse.sys.country}"
            val updatedAtText = "Updated at: " + SimpleDateFormat("dd/MM/yyyy hh:mm a", Locale.ENGLISH).format(Date(weatherResponse.dt * 1000))
            val temp = "${weatherResponse.main.temp.toInt()}°C"
            // val tempMin = "Min Temp: ${weatherResponse.main.temp_min.toInt()}°C"
            // val tempMax = "Max Temp: ${weatherResponse.main.temp_max.toInt()}°C"
            val pressure = weatherResponse.main.pressure.toString()
            val humidity = "${weatherResponse.main.humidity}%"
            val sunrise = SimpleDateFormat("hh:mm a", Locale.ENGLISH).format(Date(weatherResponse.sys.sunrise * 1000))
            val sunset = SimpleDateFormat("hh:mm a", Locale.ENGLISH).format(Date(weatherResponse.sys.sunset * 1000))
            val windSpeed = "${weatherResponse.wind.speed} m/s"

            findViewById<TextView>(R.id.address).text = address
            findViewById<TextView>(R.id.updated_at).text = updatedAtText
            findViewById<TextView>(R.id.temp).text = temp
//            findViewById<TextView>(R.id.temp_min).text = tempMin
//            findViewById<TextView>(R.id.temp_max).text = tempMax
            findViewById<TextView>(R.id.pressure).text = pressure
            findViewById<TextView>(R.id.humidity).text = humidity
            findViewById<TextView>(R.id.sunrise).text = sunrise
            findViewById<TextView>(R.id.sunset).text = sunset
            findViewById<TextView>(R.id.wind).text = windSpeed

            findViewById<ProgressBar>(R.id.loader).visibility = View.GONE
            findViewById<RelativeLayout>(R.id.mainContainer).visibility = View.VISIBLE

            val weatherCondition = weatherResponse.weather.firstOrNull()?.main ?: "Default"

            val backgroundResource = when (weatherCondition.toLowerCase(Locale.getDefault())) {
                "clear" -> R.drawable.gradient_bg
                "clouds" -> R.drawable.gradient_cloudy
                "rain" -> R.drawable.gradient_rain
                "night" -> R.drawable.gradient_night
                else -> R.drawable.gradient_bg
            }

            findViewById<RelativeLayout>(R.id.mainContainer).setBackgroundResource(backgroundResource)

        } catch (e: Exception) {
            Log.e(tag, "populateWeatherData: Error populating weather data", e)
            showError()
        }
    }

    private fun showError() {
        Log.d(tag, "showError: Showing error")
        findViewById<ProgressBar>(R.id.loader).visibility = View.GONE
        findViewById<TextView>(R.id.errorText).visibility = View.VISIBLE
    }
    private fun updateBackground(weatherResponse: WeatherResponse) {
        val weatherResponse = viewModel.weatherData.value
        val backgroundResId = determineBackgroundResource(weatherResponse)

        swipeContainer.setBackgroundResource(backgroundResId)
        findViewById<RelativeLayout>(R.id.mainContainer).setBackgroundResource(backgroundResId)
    }

    private fun determineBackgroundResource(weatherResponse: WeatherResponse?): Int {
        return when {
            weatherResponse == null -> R.drawable.gradient_black
            isNightTime() && weatherResponse.weather.firstOrNull()?.main == "Rain" -> R.drawable.gradient_rain
            isNightTime() -> R.drawable.gradient_night
            else -> R.drawable.gradient_bg
        }
    }

    private fun isNightTime(): Boolean {
        val hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
        return hour < 6 || hour >= 21
    }

    private fun updateWeatherDescription(weatherResponse: WeatherResponse) {
        val weatherDescription = if (isNightTime() && weatherResponse.weather.firstOrNull()?.main == "Sunny") {
            "Clear"
        } else {
            weatherResponse.weather.firstOrNull()?.description ?: "Unknown"
        }
        findViewById<TextView>(R.id.status).text = weatherDescription.capitalize(Locale.getDefault())
    }

}