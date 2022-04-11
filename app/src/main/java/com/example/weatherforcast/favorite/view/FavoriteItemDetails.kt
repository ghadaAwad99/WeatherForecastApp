package com.example.weatherforcast.favorite.view

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.View
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
import com.example.weatherforcast.model.WeatherModel
import com.example.weatherforcast.network.WeatherService
import java.text.SimpleDateFormat
import java.util.*

class FavoriteItemDetails : AppCompatActivity(), OnClickListener {
    private  val binding by lazy { ActivityFavoriteItemDetailsBinding.inflate(layoutInflater)}
    private val sharedPreferences by lazy { getSharedPreferences(getString(R.string.shared_prefs), MODE_PRIVATE) }
    private val lang by lazy { sharedPreferences.getString("LANGUAGE", "en").toString() }
    private val favoriteDaysAdapter by lazy { FavoriteDaysAdapter() }
    private val favoriteHoursAdapter by lazy {FavoriteHoursAdapter()}
    private val viewModel by lazy {  ViewModelProvider(this, FavoriteViewModelFactory(Repository.getInstance(
                WeatherService.getInstance(),
                ConcreteLocalSource(this),
                this))).get(FavoriteViewModel::class.java)}
    private val sharedPrefsTemp by lazy { sharedPreferences.getString("TEMP", "celsius").toString() }
    lateinit var currentAdders: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val actionBar: ActionBar = supportActionBar!!
        val colorDrawable = ColorDrawable(Color.parseColor("#5B86E5"))
        actionBar.setBackgroundDrawable(colorDrawable)
        this.title = getString(R.string.fav_locations)
        setContentView(binding.root)

        var favoriteModel = intent.extras?.get("favorite model") as FavoriteModel

        binding.hoursRecyclerView.adapter = favoriteHoursAdapter
        binding.daysRecyclerView.adapter = favoriteDaysAdapter

        currentAdders = Utilities.getAddress(favoriteModel.lat, favoriteModel.lon, lang, this)
        viewModel.getCurrTemp(favoriteModel.lat, favoriteModel.lon,Utilities.ApiKey, lang, "metric")
        viewModel.weatherLiveData.observe(this, {

            binding.tempCardView.visibility = View.VISIBLE
            binding.progressBar.visibility = View.GONE

            favoriteDaysAdapter.setFavDaysList(it.daily)
            favoriteHoursAdapter.setFavHoursList(it.hourly)

            fillFavoriteData(it)

        })
    }

    private fun fillFavoriteData(it: WeatherModel){
        binding.currentMain.text = it.current.weather[0].description
        binding.cityName.text = currentAdders

        val unixSeconds: Long = it.current.dt.toLong()
        val date = Date(unixSeconds * 1000L)
        lateinit var dateFormat: SimpleDateFormat
        lateinit var temp: String
        lateinit var unit: String
        if (lang == "en") {
            dateFormat = SimpleDateFormat("EEE, d MMM")
            when(sharedPrefsTemp ){
                "celsius" ->{ temp = it.current.temp.toString()
                    unit = " ºC"}
                "fehrenheit" -> {
                    temp = (it.current.temp/2+30).toString()
                    unit = " ºF"
                }
                "kelvin" -> {
                    temp = (it.current.temp + 273.15).toString()
                    unit = " k"
                }
            }
        } else if (lang == "ar") {
            dateFormat = SimpleDateFormat("EEE, d MMM", Locale("ar"))
            when(sharedPrefsTemp ){
                "celsius" ->{ temp = Utilities.convertToArabic(it.current.temp.toString())
                    unit = " ºC"}
                "fehrenheit" -> {
                    temp = Utilities.convertToArabic((it.current.temp/2+30).toString())
                    unit = " ف"
                }
                "kelvin" -> {
                    temp = Utilities.convertToArabic((it.current.temp + 273.15).toString())
                    unit = " ك"
                }
            }
        }
        dateFormat.timeZone = TimeZone.getTimeZone("GMT+2")
        val finalDate = dateFormat.format(date).trimIndent()

        binding.dateText.text = finalDate
        binding.currentTempText.text = temp + unit

        Utilities.chooseWeatherIcon(it.current.weather[0].main, binding.currentWeatherIcon)
    }

    override fun onDeleteClick(favoriteModel: FavoriteModel) {
        TODO("Not yet implemented")
    }

    override fun onDisplayClick(favoriteModel: FavoriteModel) {
        TODO("Not yet implemented")
    }
}