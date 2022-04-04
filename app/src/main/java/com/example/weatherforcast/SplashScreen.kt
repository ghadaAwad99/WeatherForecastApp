package com.example.weatherforcast

import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.WindowManager
import com.example.weatherforcast.home.view.HomeScreen
import java.util.*

class SplashScreen : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)



        // This is used to hide the status bar and make
        // the splash screen as a full screen activity.
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )

        // we used the postDelayed(Runnable, time) method
        // to send a message with a delayed time.
        Handler().postDelayed({
            val sharedPreferences = getSharedPreferences(getString(R.string.shared_prefs), MODE_PRIVATE)
            //set language as the last selected
            /* var lang = sharedPreferences.getString("LANGUAGE", "en")
             val config = resources.configuration

             val locale = Locale(lang)
             Locale.setDefault(locale)
             if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1)
                 config.setLocale(locale)
             else
                 config.locale = locale

             if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
                 createConfigurationContext(config)
             resources.updateConfiguration(config, resources.displayMetrics)*/

            val lang = sharedPreferences.getString("LANGUAGE", "en")

            val config = this.resources.configuration

            val locale = Locale(lang)
            Locale.setDefault(locale)
            config.setLocale(locale)

            this.createConfigurationContext(config)
            this.resources.updateConfiguration(config, this.resources.displayMetrics)
            if (!isOnline(this)) {
                this.startActivity(Intent(this, HomeScreen::class.java))
            }else{
                this.startActivity(Intent(this, MainActivity::class.java))
            }


            //val intent = Intent(this, MainActivity::class.java)
            //startActivity(intent)
            finish()
        }, 4000) // 3000 is the delayed time in milliseconds.
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
}