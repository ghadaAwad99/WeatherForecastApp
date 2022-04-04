package com.example.weatherforcast.alerts.view

import android.app.DatePickerDialog
import android.app.DatePickerDialog.OnDateSetListener
import android.app.Dialog
import android.app.TimePickerDialog
import android.app.TimePickerDialog.OnTimeSetListener
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.*
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.drawerlayout.widget.DrawerLayout
import com.example.weatherforcast.R
import com.example.weatherforcast.favorite.view.FavoriteActivity
import com.example.weatherforcast.home.view.HomeScreen
import com.example.weatherforcast.settings.SettingsActivity
import com.google.android.material.navigation.NavigationView
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

class AlertsActivity : AppCompatActivity() {
    lateinit var toggle: ActionBarDrawerToggle
    lateinit var drawerLayout: DrawerLayout
    lateinit var navView: NavigationView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_alerts)

        val actionBar: ActionBar = supportActionBar!!
        val colorDrawable = ColorDrawable(Color.parseColor("#5B86E5"))
        actionBar.setBackgroundDrawable(colorDrawable)


        drawerLayout= findViewById(R.id.drawerLayout)
        navView= findViewById(R.id.nav_view)

        toggle = ActionBarDrawerToggle(this, drawerLayout, R.string.open, R.string.close)
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        navView.setNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.nav_home ->startActivity(Intent(this@AlertsActivity, HomeScreen::class.java))
                R.id.nav_settings -> startActivity(Intent(this@AlertsActivity, SettingsActivity::class.java))
                R.id.nav_favorite -> startActivity(Intent(this@AlertsActivity, FavoriteActivity::class.java))
                R.id.nav_alerts -> startActivity(Intent(this@AlertsActivity, AlertsActivity::class.java))
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
    }

/*fun showDateTimeDialog() {
    val dialog = Dialog(getContext())
    dialog.setContentView(R.layout.activity_schedule)

    dialog.show()
    reschedule.setOnClickListener(View.OnClickListener {
        val calendar = Calendar.getInstance()
        val dateSetListener =
            OnDateSetListener { view, year, month, dayOfMonth ->
                calendar[Calendar.YEAR] = year
                calendar[Calendar.MONTH] = month
                calendar[Calendar.DAY_OF_MONTH] = dayOfMonth
                val timeSetListener =
                    OnTimeSetListener { view, hourOfDay, minute ->
                        calendar[Calendar.HOUR_OF_DAY] = hourOfDay
                        calendar[Calendar.MINUTE] = minute
                        val simpleDateFormat = SimpleDateFormat("dd-MM-yyyy hh:mm")

                        // date_time_in.setText(simpleDateFormat.format(calendar.getTime()));
                        //time = calendar.timeInMillis
                        Log.i("selected time ", "selected Time $time")
                        //currentTime = Calendar.getInstance().timeInMillis
                       // finalTime = time - currentTime
                        val downloadRequest: OneTimeWorkRequest =
                            Builder(ScheduleWorkManger::class.java) // .setInputData(data)
                                .setInitialDelay(finalTime, TimeUnit.MILLISECONDS)
                                .build()
                        WorkManager.getInstance(getContext()).enqueue(downloadRequest)
                    }
                TimePickerDialog(
                    getContext(), timeSetListener,
                    calendar[Calendar.HOUR_OF_DAY],
                    calendar[Calendar.MINUTE], false
                ).show()
            }
        DatePickerDialog(
            getContext(), dateSetListener,
            calendar[Calendar.YEAR], calendar[Calendar.MONTH], calendar[Calendar.DAY_OF_MONTH]
        ).show()
        dialog.dismiss()
    })
    takeButton.setOnClickListener(View.OnClickListener {
        var medLeft: Int = medicine.getMedLeft()
        medLeft--
        val reference: DatabaseReference = FirebaseDatabase.getInstance().getReference("Users")
            .child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("medicineList")
            .child(medicine.getUid())
        reference.child("medLeft").setValue(medLeft)
    })
}*/
