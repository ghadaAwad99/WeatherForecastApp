package com.example.weatherforcast.home.view

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import android.os.Looper
import android.provider.Settings
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.lifecycle.ViewModelProvider
import com.example.weatherforcast.R
import com.example.weatherforcast.Utilities
import com.example.weatherforcast.alerts.view.AlertsActivity
import com.example.weatherforcast.database.ConcreteLocalSource
import com.example.weatherforcast.databinding.ActivityHomeScreenBinding
import com.example.weatherforcast.favorite.view.FavoriteActivity
import com.example.weatherforcast.home.viewModel.HomeViewModel
import com.example.weatherforcast.home.viewModel.HomeViewModelFactory
import com.example.weatherforcast.model.Repository
import com.example.weatherforcast.model.WeatherModel
import com.example.weatherforcast.network.WeatherService
import com.example.weatherforcast.settings.SettingsActivity
import com.google.android.gms.location.*
import java.text.SimpleDateFormat
import java.util.*


class HomeScreen : AppCompatActivity() {

    private val binding by lazy { ActivityHomeScreenBinding.inflate(layoutInflater) }

    private val homeDaysRecyclerAdapter by lazy { HomeDaysRecyclerAdapter() }
    private val homeHoursRecyclerAdapter by lazy { HomeHoursRecyclerAdapter() }

    private val viewModel by lazy { ViewModelProvider(
        this, factory = HomeViewModelFactory(
            Repository.getInstance(
                WeatherService.getInstance(), ConcreteLocalSource(this), this),this))
        .get(HomeViewModel::class.java) }

    private val sharedPreferences by lazy { getSharedPreferences(getString(R.string.shared_prefs), MODE_PRIVATE) }
    private val lang by lazy { sharedPreferences.getString("LANGUAGE", "en").toString() }
    private val sharedPrefsTemp by lazy { sharedPreferences.getString("TEMP", "celsius").toString() }
    private val chosenLocation by lazy { sharedPreferences.getString("LOCATION", getString(R.string.gps)).toString() }
    private val toggle by lazy { ActionBarDrawerToggle(this, binding.drawerLayout, R.string.open, R.string.close) }

    lateinit var currentAdders: String
    var lat: Double = 0.0
    var lon: Double = 0.0
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        binding.daysRecyclerView.adapter = homeDaysRecyclerAdapter
        binding.hoursRecyclerView.adapter = homeHoursRecyclerAdapter
        initSideDrawer()
        getWeatherInfo()
        observeWeather()
    }

    fun observeWeather(){
        viewModel.weatherLiveData.observe(this, {
            if (it != null) {
                it.id = 0
                viewModel.insertLastResponse(it)
                binding.tempCardView.visibility = View.VISIBLE
                binding.progressBar.visibility = View.GONE
                initHomeUi(it)
            }else{binding.noDataImage.visibility = View.VISIBLE
                binding.progressBar.visibility = View.GONE}
        })
    }

    fun getWeatherInfo(){
        if (chosenLocation == "GPS") {
            Log.i("TAG", "gps")
            getLastLocation()
        } else if (chosenLocation == "Map") {
            Log.i("TAG", " Map")
            lat = sharedPreferences.getFloat("MapLat", 0f).toDouble()
            lon = sharedPreferences.getFloat("MapLon", 0f).toDouble()
            currentAdders = sharedPreferences.getString("locality", "Unknown").toString()
            viewModel.getCurrentWeatherResponse(lat, lon, lang)
        }
    }

    fun initSideDrawer(){
        val actionBar: ActionBar = supportActionBar!!
        val colorDrawable = ColorDrawable(Color.parseColor("#5B86E5"))
        actionBar.setBackgroundDrawable(colorDrawable)

        binding.drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.navView.setNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.nav_home -> startActivity(Intent(this@HomeScreen, HomeScreen::class.java))
                R.id.nav_settings -> startActivity(Intent(this@HomeScreen, SettingsActivity::class.java))
                R.id.nav_favorite -> startActivity(Intent(this@HomeScreen, FavoriteActivity::class.java))
                R.id.nav_alerts -> startActivity(Intent(this@HomeScreen, AlertsActivity::class.java))
            }
            true
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (toggle.onOptionsItemSelected(item)) {
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    private fun getLastLocation() {
        if (checkPermission()) {
            if (isLocationEnabled()) {
                fusedLocationClient.lastLocation
                    .addOnSuccessListener { location: Location? ->
                        // Got last known location. In some rare situations this can be null.
                        if (location == null) {
                            requestNewLocationData()
                        } else {
                            lat = location.latitude
                            lon = location.longitude
                            currentAdders = Utilities.getAddress(lat, lon, lang, this)
                            Log.i("TAG", "before getCurrTemp")
                            viewModel.getCurrentWeatherResponse(lat, lon,lang)
                            sharedPreferences.edit().putFloat("GPSLat",lat.toFloat()).apply()
                            sharedPreferences.edit().putFloat("GPSLon",lon.toFloat()).apply()
                            Log.i("TAG", "getLastLocation lat is " + lat + "lon is " + lon + " and lang is " + lang)
                        }
                    }
            } else {
                Toast.makeText(this@HomeScreen, getString(R.string.turn_on_location), Toast.LENGTH_SHORT).show()
                val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                startActivity(intent)
                lat = sharedPreferences.getFloat("GPSLat", 31.200092f).toDouble()
                lon = sharedPreferences.getFloat("GPSLon", 29.918739f).toDouble()
                viewModel.getCurrentWeatherResponse(lat, lon,lang)

            }
        } else {
            ActivityCompat.requestPermissions(
                this, arrayOf(
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_FINE_LOCATION), 0)
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 0) {
            when {
                grantResults[0] == PackageManager.PERMISSION_GRANTED -> {
                    getLastLocation()
                }
                else -> {
                    Toast.makeText(this, getString(R.string.permission_required), Toast.LENGTH_LONG).show()
                    finish()
                }
            }
        }
    }

    private fun checkPermission(): Boolean {
        return ActivityCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun isLocationEnabled(): Boolean {
        val locationManager =
            getSystemService(LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
            LocationManager.NETWORK_PROVIDER
        )
    }

    @SuppressLint("MissingPermission")
    private fun requestNewLocationData() {
        val locationRequest: LocationRequest = LocationRequest.create()
        locationRequest.interval = 10000
        locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        locationRequest.fastestInterval = 5000
        locationRequest.numUpdates = 1
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        fusedLocationClient.requestLocationUpdates(
            locationRequest, locationCallback,
            Looper.myLooper()!!
        )
    }

    private val locationCallback: LocationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            super.onLocationResult(locationResult)
            val lastLocation: Location = locationResult.lastLocation
            lat = lastLocation.latitude
            lon = lastLocation.longitude

            currentAdders = Utilities.getAddress(lat, lon, lang, this@HomeScreen)
            viewModel.getCurrentWeatherResponse(lat, lon, lang)
            sharedPreferences.edit().putFloat("GPSLat",lat.toFloat()).apply()
            sharedPreferences.edit().putFloat("GPSLon",lon.toFloat()).apply()

            Log.i("TAG", "onLocationResult lat is " + lat + "lon is " + lon + " and lang is " + lang)
        }
    }

    private fun initHomeUi(it:WeatherModel){
            homeDaysRecyclerAdapter.setDaysList(it.daily)
            homeHoursRecyclerAdapter.setHoursList(it.hourly)

            currentAdders = Utilities.getAddress(it.lat, it.lon, lang, this)
            binding.cityName.text = currentAdders
            binding.currentMain.text = it.current.weather[0].description
            val unixSeconds: Long = it.current.dt.toLong()
            val date = Date(unixSeconds * 1000L)
            val dateFormat = SimpleDateFormat("EEE, d MMM", Locale(lang))
            lateinit var temp: String
            lateinit var unit: String
            if (lang == "en") {
                when(sharedPrefsTemp ){
                    "celsius" ->{ temp = it.current.temp.toString()
                        unit = " ºC"}
                    "fehrenheit" -> {
                        temp = (it.current.temp/2+30).toString()
                        unit = " ºF"
                    }
                    "kelvin" -> {
                        temp = (it.current.temp + 273).toInt().toString()
                        unit = " k"
                    }
                }

            } else if (lang == "ar") {
                when(sharedPrefsTemp ){
                    "celsius" ->{ temp = Utilities.convertToArabic(it.current.temp.toString())
                        unit = " ºC"}
                    "fehrenheit" -> {
                        temp = Utilities.convertToArabic((it.current.temp/2+30).toString())
                        unit = " ف"
                    }
                    "kelvin" -> {
                        temp = Utilities.convertToArabic((it.current.temp + 273).toString())
                        unit = " ك"
                    }
                }
            }
            dateFormat.timeZone = TimeZone.getTimeZone("GMT+2")
            val finalDate = dateFormat.format(date).trimIndent()
            binding.dateText.text = finalDate
            binding.currentTempText.text = temp + unit

            when (it.current.weather[0].main) {
                "Clouds" -> binding.currentWeatherIcon.setImageResource(R.drawable.current_cloudy)
                "Clear" -> binding.currentWeatherIcon.setImageResource(R.drawable.current_sun)
                "Thunderstorm" -> binding.currentWeatherIcon.setImageResource(R.drawable.cloudy_storm)
                "Drizzle" -> binding.currentWeatherIcon.setImageResource(R.drawable.current_rain)
                "Rain" -> binding.currentWeatherIcon.setImageResource(R.drawable.current_rain)
                "Snow" -> binding.currentWeatherIcon.setImageResource(R.drawable.current_snow)
                "Mist" -> binding.currentWeatherIcon.setImageResource(R.drawable.current_fog)
                "Smoke" -> binding.currentWeatherIcon.setImageResource(R.drawable.current_fog)
                "Haze" -> binding.currentWeatherIcon.setImageResource(R.drawable.current_fog)
                "Dust" -> binding.currentWeatherIcon.setImageResource(R.drawable.current_fog)
                "Fog" -> binding.currentWeatherIcon.setImageResource(R.drawable.current_fog)
                "Sand" -> binding.currentWeatherIcon.setImageResource(R.drawable.current_fog)
                "Ash" -> binding.currentWeatherIcon.setImageResource(R.drawable.current_fog)
                "Squall" -> binding.currentWeatherIcon.setImageResource(R.drawable.current_squall)
                "Tornado" -> binding.currentWeatherIcon.setImageResource(R.drawable.ic_tornado)
            }
        }
}
