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

        Log.d(tag, "onCreate: Initializing ViewModel and observing LiveData")

        // Initialize the ViewModel with the factory
        val factory = ViewModelFactory(city, api)
        viewModel = ViewModelProvider(this, factory).get(WeatherViewModel::class.java)

        swipeContainer = findViewById(R.id.swipeContainer)
        swipeContainer.setOnRefreshListener {
            viewModel.refreshWeatherData()
            swipeContainer.isRefreshing = false
        }


        viewModel.weatherData.observe(this, androidx.lifecycle.Observer { weatherResponse ->
            Log.d(tag, "Weather Data observed: $weatherResponse")
            weatherResponse?.let {
                populateWeatherData(it)
            } ?: showError()
        })


        // Load initial weather data
        Log.d(tag, "onCreate: Calling refreshWeatherData to load initial weather data")
        viewModel.refreshWeatherData()
    }

    private fun populateWeatherData(weatherResponse: WeatherResponse) {
        try {
            Log.d(tag, "populateWeatherData: Populating weather data...")

            val address = "${weatherResponse.name}, ${weatherResponse.sys.country}"
            val updatedAtText = "Updated at: " + SimpleDateFormat("dd/MM/yyyy hh:mm a", Locale.ENGLISH).format(Date(weatherResponse.dt * 1000))
            val temp = "${weatherResponse.main.temp.toInt()}°C"
            val tempMin = "Min Temp: ${weatherResponse.main.temp_min.toInt()}°C"
            val tempMax = "Max Temp: ${weatherResponse.main.temp_max.toInt()}°C"
            val pressure = weatherResponse.main.pressure.toString()
            val humidity = "${weatherResponse.main.humidity}%"
            val sunrise = SimpleDateFormat("hh:mm a", Locale.ENGLISH).format(Date(weatherResponse.sys.sunrise * 1000))
            val sunset = SimpleDateFormat("hh:mm a", Locale.ENGLISH).format(Date(weatherResponse.sys.sunset * 1000))
            val windSpeed = "${weatherResponse.wind.speed} m/s"

            // Now actually use these variables to update the UI
            findViewById<TextView>(R.id.address).text = address
            findViewById<TextView>(R.id.updated_at).text = updatedAtText
            findViewById<TextView>(R.id.temp).text = temp
            findViewById<TextView>(R.id.temp_min).text = tempMin
            findViewById<TextView>(R.id.temp_max).text = tempMax
            findViewById<TextView>(R.id.pressure).text = pressure
            findViewById<TextView>(R.id.humidity).text = humidity
            findViewById<TextView>(R.id.sunrise).text = sunrise
            findViewById<TextView>(R.id.sunset).text = sunset
            findViewById<TextView>(R.id.wind).text = windSpeed

            findViewById<ProgressBar>(R.id.loader).visibility = View.GONE
            findViewById<RelativeLayout>(R.id.mainContainer).visibility = View.VISIBLE

            // This line extracts the main weather description, such as "Clear" or "Rain".
            val weatherCondition = weatherResponse.weather.firstOrNull()?.main ?: "Default"

            // Match the weather condition to your gradient drawables
            val backgroundResource = when (weatherCondition.toLowerCase(Locale.getDefault())) {
                "clear" -> R.drawable.gradient_bg
                "clouds" -> R.drawable.gradient_cloudy
                "rain" -> R.drawable.gradient_rain
                "night" -> R.drawable.gradient_night
                else -> R.drawable.gradient_bg
            }

            // Set the background of your container view
            findViewById<RelativeLayout>(R.id.mainContainer).setBackgroundResource(backgroundResource)

        } catch (e: Exception) {
            Log.e(tag, "populateWeatherData: Error populating weather data", e)
            showError()
        }
    }

    private fun showError() {
        Log.d(tag, "showError: Showing error...")
        findViewById<ProgressBar>(R.id.loader).visibility = View.GONE
        findViewById<TextView>(R.id.errorText).visibility = View.VISIBLE
    }
}
