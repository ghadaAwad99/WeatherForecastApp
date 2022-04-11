package com.example.weatherforcast.favorite.view

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.weatherforcast.R
import com.example.weatherforcast.Utilities
import com.example.weatherforcast.alerts.view.AlertsActivity
import com.example.weatherforcast.database.ConcreteLocalSource
import com.example.weatherforcast.databinding.ActivityFavoriteBinding
import com.example.weatherforcast.favorite.viewModel.FavoriteViewModel
import com.example.weatherforcast.favorite.viewModel.FavoriteViewModelFactory
import com.example.weatherforcast.home.view.HomeScreen
import com.example.weatherforcast.home.view.MapsActivity
import com.example.weatherforcast.model.FavoriteModel
import com.example.weatherforcast.model.Repository
import com.example.weatherforcast.network.WeatherService
import com.example.weatherforcast.settings.SettingsActivity
import com.google.android.gms.maps.model.LatLng
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.navigation.NavigationView
import com.google.android.material.snackbar.Snackbar

class FavoriteActivity : AppCompatActivity(), OnClickListener {
    private val binding by lazy { ActivityFavoriteBinding.inflate(layoutInflater) }

    private val toggle by lazy { ActionBarDrawerToggle(this, binding.drawerLayout, R.string.open, R.string.close) }
    private val favoriteViewModel by lazy { ViewModelProvider(
        this, factory = FavoriteViewModelFactory(
            Repository.getInstance(
                WeatherService.getInstance(), ConcreteLocalSource(this), this)))
        .get(FavoriteViewModel::class.java) }
    private val favoriteRecyclerAdapter by lazy { FavoriteRecyclerAdapter(this) }
    lateinit var outIntent: Intent

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        this.title = getString(R.string.fav_locations)

        initSideDrawer()

        binding.addFab.setOnClickListener {
            if (Utilities.isOnline(this)) {
                outIntent = Intent(this, MapsActivity::class.java)
                outIntent.putExtra("source", "FAV")
                startActivity(outIntent)
            }else{
                Snackbar.make(binding.addFab,getString(R.string.you_are_offline), Snackbar.LENGTH_LONG ).show()
            }
        }

        binding.daysRecyclerView.adapter = favoriteRecyclerAdapter

        if (intent.extras?.get("point") != null && intent.extras?.get("locality") != null){
            var point: LatLng = intent.extras!!.get("point") as LatLng
            var locality = intent.extras!!.get("locality") as String
            favoriteViewModel.insertFavorite(
                FavoriteModel(locality, point.latitude, point.longitude)
            )
        }

        favoriteViewModel.getAllFavorite().observe(this) {
            if (it != null) {favoriteRecyclerAdapter.setFavoriteList(it) }
        }

    }

    private fun initSideDrawer(){
        val actionBar: ActionBar = supportActionBar!!
        val colorDrawable = ColorDrawable(Color.parseColor("#5B86E5"))
        actionBar.setBackgroundDrawable(colorDrawable)

        binding.drawerLayout.addDrawerListener(toggle)
        toggle.syncState()
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.navView.setNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.nav_home -> startActivity(Intent(this@FavoriteActivity, HomeScreen::class.java))
                R.id.nav_settings -> startActivity(Intent(this@FavoriteActivity, SettingsActivity::class.java))
                R.id.nav_favorite -> startActivity(Intent(this@FavoriteActivity, FavoriteActivity::class.java))
                R.id.nav_alerts -> startActivity(Intent(this@FavoriteActivity, AlertsActivity::class.java))
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

    override fun onDeleteClick(favoriteModel: FavoriteModel) {
        favoriteViewModel.deleteFavorite(favoriteModel)
        Toast.makeText(this, getString(R.string.toast_deleted), Toast.LENGTH_SHORT).show()
    }

    override fun onDisplayClick(favoriteModel: FavoriteModel) {
        var favIntent = Intent(this, FavoriteItemDetails::class.java)
        favIntent.putExtra("favorite model", favoriteModel)
        startActivity(favIntent)
    }

}
