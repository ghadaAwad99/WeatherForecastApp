package com.example.weatherforcast.home.view

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import android.os.Looper
import android.provider.Settings
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import com.example.weatherforcast.model.Repository
import com.example.weatherforcast.R
import com.example.weatherforcast.home.viewModel.HomeViewModel
import com.example.weatherforcast.home.viewModel.HomeViewModelFactory
import com.google.android.gms.location.*
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task


class HomeFragment : Fragment() {
    private var param1: String? = null
    private var param2: String? = null
    lateinit var homeDaysRecyclerAdapter: HomeDaysRecyclerAdapter
    lateinit var recyclerView: RecyclerView
    lateinit var viewModel: HomeViewModel
    var fusedLocationProviderClient: FusedLocationProviderClient? = null
    var bundle = Bundle()

    var lat : Double = 0.0
    var lon :  Double = 0.0
    private var mActivity: Activity? = null

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        Log.i("TAG", "Inside home fragment")
        //----
        // supportMapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.google_map);
        //----
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(mActivity!!)



        val bundle = arguments
       // var lat = bundle!!.getDouble("lat")
       // var lon = bundle!!.getDouble("long")
        Log.i("TAG", "inside home fragment lat is " + lat + " and lon is " + lon)
        val view: View = inflater.inflate(R.layout.fragment_home, container, false)
        //getLastLocation()
        recyclerView = view.findViewById(R.id.days_recyclerView)
        homeDaysRecyclerAdapter = HomeDaysRecyclerAdapter()
        recyclerView.adapter = homeDaysRecyclerAdapter
        viewModel = ViewModelProvider(this, HomeViewModelFactory(Repository.getInstance())).get(
            HomeViewModel::class.java)
        Log.i("TAG", "before getCurrTemp")
        viewModel.getCurrTemp(31.200092, 29.918739, "8bdc89e28e3ae5c674e20f1d16e70f7d")

       viewModel.weatherMutableLiveData.observe(viewLifecycleOwner, Observer {

            Log.d("TAG", "onCreate: ${it.daily}")

            homeDaysRecyclerAdapter.setDaysList(it.daily)
        })


        return view
    }

    companion object {

        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            HomeFragment().apply {
                arguments = Bundle().apply {
                    var lat = this.getDouble("lat")
                    Log.i("TAG", " arguments lat " + lat)
                }
            }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mActivity = context as Activity
    }

    private fun getLastLocation() {
        Log.i("TAG", "insode getLastLocation ")
        if (checkPermission()) {
            Log.i("TAG", "inside ifff ")
            if (isLocationEnabled()) {
                Log.i("TAG", "inside ifff second ")
                fusedLocationProviderClient!!.getLastLocation()
                    .addOnCompleteListener(object : OnCompleteListener<Location> {
                        override fun onComplete(task: Task<Location>) {
                            val location: Location = task.getResult()
                            if (location == null) {
                                Log.i("TAG", "inside location == null ")
                                requestNewLocationData()
                            } else {
                                lat = location.latitude
                                lon = location.longitude
                                bundle.putDouble("long",lon)
                                bundle.putDouble("lat", lat)
                                Log.i("TAG", "getLastLocation lat is " + lat + "lon is " + lon)
                                //latitudeText.setText(location.latitude.toString() + "")
                                //longitudeText.setText(location.longitude.toString() + "")
                            }
                        }
                    })
            } else {
                Log.i("TAG","ELSE")
               /* Toast.makeText(
                    this@HomeFragment,
                    "Please turn on your location",
                    Toast.LENGTH_SHORT
                ).show()*/
                val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                startActivity(intent)
            }
        } else {
            Log.i("TAG","ELSE")
            ActivityCompat.requestPermissions(
                mActivity!!, arrayOf(
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ), 0
            )
        }
    }

    private fun checkPermission(): Boolean {
        Log.i("TAG", "inside checkPermission")
        return ActivityCompat.checkSelfPermission(
            requireActivity().applicationContext
            ,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
            requireActivity().applicationContext,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun isLocationEnabled(): Boolean {
        Log.i("TAG", "inside isLocationEnabled ")
        val locationManager =
            context?.getSystemService(AppCompatActivity.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
            LocationManager.NETWORK_PROVIDER
        )
    }

    private fun requestNewLocationData() {
        val locationRequest: LocationRequest = LocationRequest.create()
        locationRequest.setInterval(5)
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
        locationRequest.setFastestInterval(0)
        locationRequest.setNumUpdates(1)
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(requireContext().applicationContext)
        Looper.myLooper()?.let {
            fusedLocationProviderClient!!.requestLocationUpdates(
                locationRequest,
                locationCallback,
                it
            )
        }
    }

    private val locationCallback: LocationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            super.onLocationResult(locationResult)
            val lastLocation: Location = locationResult.getLastLocation()
            lat =  lastLocation.latitude
            lon = lastLocation.longitude
            bundle.putDouble("long",lon)
            bundle.putDouble("lat", lat)
            Log.i("TAG", "onLocationResult lat is " + lat + "lon is " + lon)
            // latitudeText.setText(lastLocation.latitude.toString() + "")
            //longitudeText.setText(+lastLocation.longitude.toString() + "")
            // latitude = latitudeText.getText().toString()
            // longitude = longitudeText.getText().toString()
        }
    }
}