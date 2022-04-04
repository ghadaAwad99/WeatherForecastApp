package com.example.weatherforcast.favorite.view

import android.content.SharedPreferences
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.weatherforcast.R
import com.example.weatherforcast.Utilities
import com.example.weatherforcast.database.ConcreteLocalSource
import com.example.weatherforcast.databinding.ActivityFavoriteItemDetailsBinding
import com.example.weatherforcast.favorite.viewModel.FavoriteViewModel
import com.example.weatherforcast.favorite.viewModel.FavoriteViewModelFactory
import com.example.weatherforcast.model.FavoriteModel
import com.example.weatherforcast.model.Repository
import com.example.weatherforcast.network.WeatherService
import java.text.SimpleDateFormat
import java.util.*

class FavoriteItemDetails : AppCompatActivity(), OnClickListener {
    private lateinit var binding: ActivityFavoriteItemDetailsBinding
    lateinit var sharedPreferences: SharedPreferences
    lateinit var lang: String
    lateinit var favoriteDaysAdapter: FavoriteDaysAdapter
    lateinit var favoriteHoursAdapter: FavoriteHoursAdapter
    lateinit var viewModel: FavoriteViewModel
    lateinit var currentAdders: String
    lateinit var jdf: SimpleDateFormat
    lateinit var temp: String
    lateinit var unit: String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val actionBar: ActionBar = supportActionBar!!
        val colorDrawable = ColorDrawable(Color.parseColor("#5B86E5"))
        actionBar.setBackgroundDrawable(colorDrawable)
        this.title = "Favorite Locations"
        binding = ActivityFavoriteItemDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        var favoriteModel = intent.extras?.get("favorite model") as FavoriteModel

        sharedPreferences = getSharedPreferences(getString(R.string.shared_prefs), MODE_PRIVATE)
        lang = sharedPreferences.getString("LANGUAGE", "en").toString()

        favoriteDaysAdapter = FavoriteDaysAdapter()
        favoriteHoursAdapter = FavoriteHoursAdapter()

        binding.hoursRecyclerView.adapter = favoriteHoursAdapter
        binding.daysRecyclerView.adapter = favoriteDaysAdapter

        viewModel = ViewModelProvider(
            this,
            FavoriteViewModelFactory(
                Repository.getInstance(
                    WeatherService.getInstance(),
                    ConcreteLocalSource(this),
                    this
                )
            )
        ).get(FavoriteViewModel::class.java)

        //Toast.makeText(this, "in display " + favoriteModel.lat + " lon is" +favoriteModel.lon + " " + lang , Toast.LENGTH_SHORT).show()


        currentAdders = Utilities.getAddress(favoriteModel.lat, favoriteModel.lon, lang, this)
        viewModel.getCurrTemp(favoriteModel.lat, favoriteModel.lon,Utilities.ApiKey, lang, "metric")
        viewModel.weatherLiveData.observe(this, {

            favoriteDaysAdapter.setFavDaysList(it.daily)
            favoriteHoursAdapter.setFavHoursList(it.hourly)
            binding.currentMain.text = it.current.weather[0].description
            binding.cityName.text = currentAdders

            val unix_seconds: Long = it.current.dt.toLong()
            val date = Date(unix_seconds * 1000L)
            jdf = SimpleDateFormat("EEE, d MMM", Locale(lang))

            if (lang == "en") {
                temp = it.current.temp.toString()
                unit = " k"

            } else if (lang == "ar") {
                temp = Utilities.convertToArabic(it.current.temp.toString())
                unit = " ك"
            }
            jdf.timeZone = TimeZone.getTimeZone("GMT+2")
            val java_date = jdf.format(date).trimIndent()

            binding.dateText.text = java_date
            binding.currentTempText.text = temp + unit

            Utilities.chooseWeatherIcon(it.current.weather[0].main, binding.currentWeatherIcon)
        })
    }

    override fun onDeleteClick(favoriteModel: FavoriteModel) {
        TODO("Not yet implemented")
    }

    override fun onDisplayClick(favoriteModel: FavoriteModel) {
        TODO("Not yet implemented")
    }
}