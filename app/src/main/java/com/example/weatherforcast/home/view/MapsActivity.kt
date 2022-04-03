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

class MapsActivity : AppCompatActivity(), OnMapReadyCallback, /*GoogleMap.OnMarkerDragListener,
    GoogleMap.OnMarkerClickListener*/  GoogleMap.OnMapClickListener {
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

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)


    }

    private fun showDialog(title: String, point: LatLng) {

        when (intent.extras?.getString("source")) {
            "FAV" -> {
                outIntent = Intent(this, FavoriteActivity::class.java)
                outIntent.putExtra("point", point)
            }
            "HOME" -> {
                outIntent = Intent(this, HomeScreen::class.java)
                outIntent.putExtra("lat", point.latitude)
                outIntent.putExtra("lon", point.longitude)
            }
        }


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

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        // Add a marker in Sydney and move the camera
        val sydney = LatLng(-34.0, 151.0)
        marker = mMap.addMarker(MarkerOptions().position(sydney).title("Marker in Sydney"))
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney))

        mMap.setOnMapClickListener(this)
    }

    /* override fun onMarkerDragStart(p0: Marker?) {
         TODO("Not yet implemented")
     }

     override fun onMarkerDrag(p0: Marker?) {
         TODO("Not yet implemented")
     }

     override fun onMarkerDragEnd(p0: Marker?) {
         Toast.makeText(this, "marker drag ennnddd", Toast.LENGTH_SHORT).show()
     }

     override fun onMarkerClick(p0: Marker?): Boolean {
         Toast.makeText(this, "marker clicked", Toast.LENGTH_SHORT).show()
         return true
     }*/

    override fun onMapClick(point: LatLng) {
        marker.remove()
        mMap.addMarker(MarkerOptions().position(point))

        val location = Utilities.getAddress(point.latitude, point.longitude, lang, this)

        showDialog(getString(R.string.do_you_want_to_choose_this_place) + "\n" + location, point)


    }


}

/*
mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
    @Override
    public void onMapClick(LatLng latLng) {

        Toast.makeText(
            YourActivity.this,
            "Lat : " + latLng.latitude + " , "
                    + "Long : " + latLng.longitude,
            Toast.LENGTH_LONG).show();

    }
});

mGoogleMap.setOnInfoWindowClickListener(RegActivity.this);*/
