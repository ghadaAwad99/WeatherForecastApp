package com.example.weatherforcast.home.view

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.location.Address
import android.location.Location
import android.location.LocationManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Looper
import android.provider.Settings
import android.util.Log
import android.view.MenuItem
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.core.app.ActivityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import com.example.weatherforcast.model.Repository
import com.example.weatherforcast.R
import com.example.weatherforcast.home.viewModel.HomeViewModel
import com.example.weatherforcast.home.viewModel.HomeViewModelFactory
import com.google.android.gms.location.*
import com.google.android.material.navigation.NavigationView
import java.text.SimpleDateFormat
import java.util.*
import android.location.Geocoder
import androidx.appcompat.app.ActionBar
import com.example.weatherforcast.Utilities
import com.example.weatherforcast.alerts.view.AlertsActivity
import com.example.weatherforcast.database.ConcreteLocalSource
import com.example.weatherforcast.favorite.view.FavoriteActivity
import com.example.weatherforcast.network.WeatherService
import com.example.weatherforcast.settings.SettingsActivity
import java.io.IOException
import java.lang.StringBuilder


class HomeScreen : AppCompatActivity() {
    lateinit var homeDaysRecyclerAdapter: HomeDaysRecyclerAdapter
    lateinit var homeHoursRecyclerAdapter: HomeHoursRecyclerAdapter
    lateinit var recyclerView: RecyclerView
    lateinit var hoursRecyclerView: RecyclerView
    lateinit var viewModel: HomeViewModel
    lateinit var cityName: TextView
    lateinit var dateText : TextView
    lateinit var currentTempText : TextView
    lateinit var currentIcon : ImageView
    lateinit var currentMain : TextView
    lateinit var currentAdders:String
    var bundle = Bundle()
    lateinit var sharedPreferences : SharedPreferences
    lateinit var lang:String
    lateinit var temp:String
    lateinit var choosenLocation:String

    var lat: Double = 0.0
    var lon: Double = 0.0
    lateinit var toggle: ActionBarDrawerToggle
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    override fun onCreate(savedInstanceState: Bundle?) {
        Log.i("TAG", "Inside home screen activity")
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home_screen)
        //replaceFragment(HomeFragment())
        sharedPreferences = getSharedPreferences(getString(R.string.shared_prefs), MODE_PRIVATE)
        lang = sharedPreferences.getString("LANGUAGE", "en").toString()
        temp = sharedPreferences.getString("TEMP","").toString()
        choosenLocation = sharedPreferences.getString("LOCATION", getString(R.string.gps)).toString()


        val actionBar: ActionBar = supportActionBar!!
        val colorDrawable = ColorDrawable(Color.parseColor("#FFFFFF"))
        actionBar.setBackgroundDrawable(colorDrawable)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        recyclerView = findViewById(R.id.favorite_recyclerView)
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
        viewModel = ViewModelProvider(this, HomeViewModelFactory(Repository.getInstance(
            WeatherService.getInstance(), ConcreteLocalSource(this), this
        ))).get(
            HomeViewModel::class.java
        )
        if (choosenLocation == getString(R.string.gps)){
            getLastLocation()
        }else if (choosenLocation == getString(R.string.map)){
            lat = intent.extras?.get("lat") as Double
            lon = intent.extras?.get("lon") as Double
            Toast.makeText(this,"map is choosen and lat is " + lat+ "and lon is " + lon , Toast.LENGTH_SHORT).show()
            currentAdders = Utilities.getAddress(lat, lon, lang, this)
            viewModel.getCurrTemp(lat, lon, "8bdc89e28e3ae5c674e20f1d16e70f7d", lang)
        }



        viewModel.weatherMutableLiveData.observe(this, {

            Log.d("TAG", "onCreate: ${it.daily}")

            homeDaysRecyclerAdapter.setDaysList(it.daily)
            homeHoursRecyclerAdapter.setHoursList(it.hourly)
            cityName.text = currentAdders/*it.timezone*/
            currentMain.text = it.current.weather[0].description

            //Unix seconds
            val unix_seconds: Long = it.current.dt.toLong()
            //convert seconds to milliseconds
            val date = Date(unix_seconds * 1000L)
            // format of the date
            lateinit var jdf: SimpleDateFormat
            lateinit var temp:String
            lateinit var unit:String
            if(lang == "en"){
                 jdf = SimpleDateFormat("EEE, d MMM")
                temp = it.current.temp.toString()
                unit = " k"

            }else if (lang == "ar") {
                 jdf = SimpleDateFormat("EEE, d MMM", Locale("ar"))
                 temp = convertToArabic(it.current.temp.toString())
                unit= " ك"
            }
            jdf.timeZone = TimeZone.getTimeZone("GMT+2")
            val java_date = jdf.format(date).trimIndent()
          /*  var num = convertToArabic(java_date)
            Toast.makeText(this, "num is " + num , Toast.LENGTH_SHORT).show()*/
            dateText.text = java_date


            currentTempText.text =  temp + unit

            when(it.current.weather[0].main){
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
        })


        val drawerLayout: DrawerLayout = findViewById(R.id.drawerLayout)
        val navView: NavigationView = findViewById(R.id.nav_view)

        toggle = ActionBarDrawerToggle(this, drawerLayout, R.string.open, R.string.close)
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        navView.setNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.nav_home ->startActivity(Intent(this@HomeScreen, HomeScreen::class.java))
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

    fun replaceFragment(fragment: Fragment) {
        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragment.arguments = bundle
        fragmentTransaction.replace(R.id.frame, fragment)
        fragmentTransaction.commit()
    }


    //----
    private fun getLastLocation() {
        Log.i("TAG", "insode getLastLocation ")
        if (checkPermission()) {
            Log.i("TAG", "inside ifff ")
            if (isLocationEnabled()) {
                Log.i("TAG", "inside ifff second ")
                fusedLocationClient.lastLocation
                    .addOnSuccessListener { location : Location? ->
                        // Got last known location. In some rare situations this can be null.
                        if (location == null) {
                            requestNewLocationData()
                        } else {

                           /* lat = -64.6240
                            lon = -70.1251*/
                            lat = location.latitude
                            lon =location.longitude
                            currentAdders = Utilities.getAddress(lat, lon, lang, this)
                            /* bundle.putDouble("long",lon)
                             bundle.putDouble("lat", lat)*/
                            Log.i("TAG", "before getCurrTemp")
                            viewModel.getCurrTemp(lat = lat, lon= lon, key = "8bdc89e28e3ae5c674e20f1d16e70f7d", language = lang)

                            Log.i("TAG", "getLastLocation lat is " + lat + "lon is " + lon + " and lang is " + lang.toString())
                        }
                    }
             /*   fusedLocationProviderClient?.getLastLocation()
                    ?.addOnCompleteListener(object : OnCompleteListener<Location> {
                        override fun onComplete(task: Task<Location>) {
                            val location: Location = task.getResult()
                            if (location == null) {
                                requestNewLocationData()
                            } else {
                                lat = location.latitude
                                lon = location.longitude
                                *//* bundle.putDouble("long",lon)
                                 bundle.putDouble("lat", lat)*//*
                                Log.i("TAG", "before getCurrTemp")
                                viewModel.getCurrTemp(lat, lon, "8bdc89e28e3ae5c674e20f1d16e70f7d")

                                Log.i("TAG", "getLastLocation lat is " + lat + "lon is " + lon)
                            }
                        }
                    })*/
            } else {
                Toast.makeText(
                    this@HomeScreen,
                    "Please turn on your location",
                    Toast.LENGTH_SHORT
                ).show()
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
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
        locationRequest.setFastestInterval(5000)
        locationRequest.setNumUpdates(1)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback,
            Looper.myLooper()!!
        )
      /*  Looper.myLooper()?.let {
            fusedLocationProviderClient!!.requestLocationUpdates(
                locationRequest,
                locationCallback,
                it
            )
        }*/
    }

    private val locationCallback: LocationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            super.onLocationResult(locationResult)
            val lastLocation: Location = locationResult.getLastLocation()
            lat = lastLocation.latitude
            lon = lastLocation.longitude


          /*  lat = -64.6240
            lon = -70.1251*/

            currentAdders = Utilities.getAddress(lat, lon,lang, this@HomeScreen)
            viewModel.getCurrTemp(lat, lon, "8bdc89e28e3ae5c674e20f1d16e70f7d", lang)

            /* bundle.putDouble("long",lon)
             bundle.putDouble("lat", lat)*/

            Log.i("TAG", "onLocationResult lat is " + lat + "lon is " + lon + " and lang is " + lang.toString())
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
}