package com.example.weatherforcast.home.view

import android.app.Dialog
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.Window
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.weatherforcast.R
import com.example.weatherforcast.Utilities
import com.example.weatherforcast.databinding.ActivityMapsBinding
import com.example.weatherforcast.favorite.view.FavoriteActivity
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions

class MapsActivity : AppCompatActivity(), OnMapReadyCallback, GoogleMap.OnMapClickListener {
    lateinit var outIntent: Intent
    lateinit var sharedPreferences: SharedPreferences
    lateinit var lang: String

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMapsBinding
    private lateinit var marker: Marker


    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        sharedPreferences = getSharedPreferences(getString(R.string.shared_prefs), MODE_PRIVATE)
        lang = sharedPreferences.getString("LANGUAGE", "en").toString()

        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        // Add a marker in Sydney and move the camera
        val alex = LatLng(31.200092, 29.918739)
        marker = mMap.addMarker(MarkerOptions().position(alex))
        mMap.moveCamera(CameraUpdateFactory.newLatLng(alex))

        mMap.setOnMapClickListener(this)
    }

    override fun onMapClick(point: LatLng) {
        marker.remove()
        mMap.addMarker(MarkerOptions().position(point))

        val location = Utilities.getAddress(point.latitude, point.longitude, lang, this)


        when (intent.extras?.getString("source")) {
            "FAV" -> {
                outIntent = Intent(this, FavoriteActivity::class.java)
                outIntent.putExtra("point", point)
                outIntent.putExtra("locality",location)


            }
            "HOME" -> {
                outIntent = Intent(this, HomeScreen::class.java)
                outIntent.putExtra("point", point)
                outIntent.putExtra("locality",location)
                sharedPreferences.edit().putFloat("MapLat",point.latitude.toFloat()).apply()
                sharedPreferences.edit().putFloat("MapLon",point.longitude.toFloat()).apply()
                sharedPreferences.edit().putString("locality",location).apply()



            }
        }

        showDialog(getString(R.string.do_you_want_to_choose_this_place) + "\n" + location, point)
    }

    private fun showDialog(title: String, point: LatLng) {
        Toast.makeText(
            this,
            "inside map activity and lat is " + point.latitude + " and lon is " + point.longitude,
            Toast.LENGTH_SHORT
        ).show()

        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(false)
        dialog.setContentView(R.layout.dialog_custom_layout)
        val body = dialog.findViewById(R.id.body) as TextView
        body.text = title
        val yesBtn = dialog.findViewById(R.id.yesBtn) as Button
        val noBtn = dialog.findViewById(R.id.noBtn) as TextView
        yesBtn.setOnClickListener {
            dialog.dismiss()
            startActivity(outIntent)
        }
        noBtn.setOnClickListener { dialog.dismiss() }
        dialog.show()


    }
}

