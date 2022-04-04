package com.example.weatherforcast.home.view

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.location.Location
import android.location.LocationManager
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Bundle
import android.os.Looper
import android.provider.Settings
import android.util.Log
import android.view.MenuItem
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import com.example.weatherforcast.R
import com.example.weatherforcast.Utilities
import com.example.weatherforcast.alerts.view.AlertsActivity
import com.example.weatherforcast.database.ConcreteLocalSource
import com.example.weatherforcast.favorite.view.FavoriteActivity
import com.example.weatherforcast.home.viewModel.HomeViewModel
import com.example.weatherforcast.home.viewModel.HomeViewModelFactory
import com.example.weatherforcast.model.Repository
import com.example.weatherforcast.model.WeatherModel
import com.example.weatherforcast.network.WeatherService
import com.example.weatherforcast.settings.SettingsActivity
import com.google.android.gms.location.*
import com.google.android.gms.maps.model.LatLng
import com.google.android.material.navigation.NavigationView
import java.text.SimpleDateFormat
import java.util.*


class HomeScreen : AppCompatActivity() {

    private lateinit var homeDaysRecyclerAdapter: HomeDaysRecyclerAdapter
    private lateinit var homeHoursRecyclerAdapter: HomeHoursRecyclerAdapter
    private lateinit var recyclerView: RecyclerView
    private lateinit var hoursRecyclerView: RecyclerView
    lateinit var viewModel: HomeViewModel
    private lateinit var cityName: TextView
    private lateinit var dateText: TextView
    private lateinit var currentTempText: TextView
    private lateinit var currentIcon: ImageView
    private lateinit var currentMain: TextView
    lateinit var currentAdders: String
    private lateinit var sharedPreferences: SharedPreferences
    lateinit var lang: String
    lateinit var temp: String
    private lateinit var choosenLocation: String
    var lat: Double = 0.0
    var lon: Double = 0.0
    private lateinit var toggle: ActionBarDrawerToggle
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    override fun onCreate(savedInstanceState: Bundle?) {
        Log.i("TAG", "Inside home screen activity")
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home_screen)

        sharedPreferences = getSharedPreferences(getString(R.string.shared_prefs), MODE_PRIVATE)
        lang = sharedPreferences.getString("LANGUAGE", "en").toString()
        temp = sharedPreferences.getString("TEMP", "").toString()
        choosenLocation = sharedPreferences.getString("LOCATION", getString(R.string.gps)).toString()

        val actionBar: ActionBar = supportActionBar!!
        val colorDrawable = ColorDrawable(Color.parseColor("#5B86E5"))
        actionBar.setBackgroundDrawable(colorDrawable)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        recyclerView = findViewById(R.id.days_recyclerView)
        hoursRecyclerView = findViewById(R.id.hours_recyclerView)
        cityName = findViewById(R.id.city_name)
        dateText = findViewById(R.id.date_text)
        currentIcon = findViewById(R.id.current_weather_icon)
        currentTempText = findViewById(R.id.current_temp_text)
        currentMain = findViewById(R.id.current_main)

        homeDaysRecyclerAdapter = HomeDaysRecyclerAdapter()
        homeHoursRecyclerAdapter = HomeHoursRecyclerAdapter()
        recyclerView.adapter = homeDaysRecyclerAdapter
        hoursRecyclerView.adapter = homeHoursRecyclerAdapter
        viewModel = ViewModelProvider(
            this, factory = HomeViewModelFactory(
                Repository.getInstance(
                    WeatherService.getInstance(), ConcreteLocalSource(this), this
                )
            )
        ).get(HomeViewModel::class.java)



        if (!isOnline(this)) {

            Toast.makeText(this, "you are offline", Toast.LENGTH_SHORT).show()
            viewModel.getLastResponseFromRoom().observe(this, {
                if (it != null) {
                    initHomeUi(it)
                }
            }
                )
        }else {
            if (choosenLocation == getString(R.string.gps)) {
                getLastLocation()
            } else if (choosenLocation == getString(R.string.map)) {
                val point: LatLng = intent.extras?.get("point") as LatLng
                Toast.makeText(this, "map is chosen and lat is " + point.latitude + "and lon is " + point.longitude,
                    Toast.LENGTH_SHORT).show()
                currentAdders = intent.extras?.get("locality") as String
                viewModel.getCurrTemp(point.latitude, point.longitude, Utilities.ApiKey, lang, "metric")
            }

            viewModel.weatherLiveData.observe(this, {
                it.id = 0
                viewModel.insertLastResponse(it)
                Log.d("TAG", "onCreate: ${it.daily}")
                initHomeUi(it)
            })
        }

        val drawerLayout: DrawerLayout = findViewById(R.id.drawerLayout)
        val navView: NavigationView = findViewById(R.id.nav_view)

        toggle = ActionBarDrawerToggle(this, drawerLayout, R.string.open, R.string.close)
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        navView.setNavigationItemSelectedListener {
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
        Log.i("TAG", "inside getLastLocation ")
        if (checkPermission()) {
            Log.i("TAG", "inside ifff ")
            if (isLocationEnabled()) {
                Log.i("TAG", "inside ifff second ")
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
                            viewModel.getCurrTemp(lat, lon,Utilities.ApiKey, lang,"metric")

                            Log.i("TAG", "getLastLocation lat is " + lat + "lon is " + lon + " and lang is " + lang)
                        }
                    }
            } else {
                Toast.makeText(this@HomeScreen, "Please turn on your location", Toast.LENGTH_SHORT).show()
                val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                startActivity(intent)
            }
        } else {
            ActivityCompat.requestPermissions(
                this, arrayOf(
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ), 0
            )

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
            viewModel.getCurrTemp(lat, lon, Utilities.ApiKey, lang, "metric")

            Log.i("TAG", "onLocationResult lat is " + lat + "lon is " + lon + " and lang is " + lang)
        }
    }

    fun isOnline(context: Context): Boolean {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        if (connectivityManager != null) {
            val capabilities =
                connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)
            if (capabilities != null) {
                if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)) {
                    Log.i("Internet", "NetworkCapabilities.TRANSPORT_CELLULAR")
                    return true
                } else if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)) {
                    Log.i("Internet", "NetworkCapabilities.TRANSPORT_WIFI")
                    return true
                } else if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)) {
                    Log.i("Internet", "NetworkCapabilities.TRANSPORT_ETHERNET")
                    return true
                }
            }
        }
        return false
    }

        fun initHomeUi(it:WeatherModel){
            homeDaysRecyclerAdapter.setDaysList(it.daily)
            homeHoursRecyclerAdapter.setHoursList(it.hourly)

            currentAdders = Utilities.getAddress(it.lat, it.lon, lang, this)
            cityName.text = currentAdders
            currentMain.text = it.current.weather[0].description
            val unix_seconds: Long = it.current.dt.toLong()
            val date = Date(unix_seconds * 1000L)
            lateinit var jdf: SimpleDateFormat
            lateinit var temp: String
            lateinit var unit: String
            if (lang == "en") {
                jdf = SimpleDateFormat("EEE, d MMM")
                temp = it.current.temp.toString()
                unit = " º"

            } else if (lang == "ar") {
                jdf = SimpleDateFormat("EEE, d MMM", Locale("ar"))
                temp = Utilities.convertToArabic(it.current.temp.toString())
                unit = " º"
            }
            jdf.timeZone = TimeZone.getTimeZone("GMT+2")
            val java_date = jdf.format(date).trimIndent()
            dateText.text = java_date
            currentTempText.text = temp + unit

            when (it.current.weather[0].main) {
                "Clouds" -> currentIcon.setImageResource(R.drawable.current_cloudy)
                "Clear" -> currentIcon.setImageResource(R.drawable.current_sun)
                "Thunderstorm" -> currentIcon.setImageResource(R.drawable.cloudy_storm)
                "Drizzle" -> currentIcon.setImageResource(R.drawable.current_rain)
                "Rain" -> currentIcon.setImageResource(R.drawable.current_rain)
                "Snow" -> currentIcon.setImageResource(R.drawable.current_snow)
                "Mist" -> currentIcon.setImageResource(R.drawable.current_fog)
                "Smoke" -> currentIcon.setImageResource(R.drawable.current_fog)
                "Haze" -> currentIcon.setImageResource(R.drawable.current_fog)
                "Dust" -> currentIcon.setImageResource(R.drawable.current_fog)
                "Fog" -> currentIcon.setImageResource(R.drawable.current_fog)
                "Sand" -> currentIcon.setImageResource(R.drawable.current_fog)
                "Ash" -> currentIcon.setImageResource(R.drawable.current_fog)
                "Squall" -> currentIcon.setImageResource(R.drawable.current_squall)
                "Tornado" -> currentIcon.setImageResource(R.drawable.ic_tornado)
            }
        }
        }

    fun convertToArabic(value: String): String {
        return (value + "")
            .replace("1".toRegex(), "١").replace("2".toRegex(), "٢")
            .replace("3".toRegex(), "٣").replace("4".toRegex(), "٤")
            .replace("5".toRegex(), "٥").replace("6".toRegex(), "٦")
            .replace("7".toRegex(), "٧").replace("8".toRegex(), "٨")
            .replace("9".toRegex(), "٩").replace("0".toRegex(), "٠")
    }

    /*fun EnglishNumToArabic(persianStr: String):String {
        var result = ""
        var en = '۰'
        for (ch in persianStr) {
            en = ch
            when (ch) {
                '0' -> en =  '۰'
                '1' -> en = '۱'
                '2' -> en =  '۲'
                '3'-> en =  '۳'
                '4' -> en = '۴'
                '5' -> en = '۵'
                '6' -> en = '۶'
                '7'  -> en = '۷'
                '8'-> en =  '۸'
                '9'-> en =  '۹'
            }
            result = "${result}$en"
        }
        return result
    }*/


/*if (intent.extras?.get("FAV LAT") != null && intent.extras?.get("FAV LON") != null) {
          lat = (intent.extras?.get("FAV LAT") as Double)
          lon = (intent.extras?.get("FAV LON") as Double)
          lang = (intent.extras?.get("FAV LANG") as String)
          currentAdders = (intent.extras?.get("FAV LOCALITY") as String)
          viewModel.getCurrTemp(
              lat,
              lon,
              "8bdc89e28e3ae5c674e20f1d16e70f7d",
              lang,
              "metric"
          )
      } else {*/

/*    fun replaceFragment(fragment: Fragment) {
        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragment.arguments = bundle
        fragmentTransaction.replace(R.id.frame, fragment)
        fragmentTransaction.commit()
    }*/
