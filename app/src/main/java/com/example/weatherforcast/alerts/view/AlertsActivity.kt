package com.example.weatherforcast.alerts.view

import android.app.AlertDialog
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
import android.view.Window
import android.widget.*
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.ViewModelProvider
import androidx.work.OneTimeWorkRequest
import androidx.work.WorkManager
import androidx.work.WorkRequest
import com.example.weatherforcast.R
import com.example.weatherforcast.Utilities
import com.example.weatherforcast.alerts.viewModel.AlertsViewModel
import com.example.weatherforcast.alerts.viewModel.AlertsViewModelFactory
import com.example.weatherforcast.database.ConcreteLocalSource
import com.example.weatherforcast.favorite.view.FavoriteActivity
import com.example.weatherforcast.favorite.viewModel.FavoriteViewModel
import com.example.weatherforcast.favorite.viewModel.FavoriteViewModelFactory
import com.example.weatherforcast.home.view.HomeScreen
import com.example.weatherforcast.model.Repository
import com.example.weatherforcast.model.UserAlarm
import com.example.weatherforcast.network.WeatherService
import com.example.weatherforcast.settings.SettingsActivity
import com.google.android.gms.maps.model.LatLng
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.navigation.NavigationView
import java.security.AccessController.getContext
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

class AlertsActivity : AppCompatActivity() {
    lateinit var startDate :TextView
    lateinit var endDate : TextView
    lateinit var alarmTime : TextView
    lateinit var toggle: ActionBarDrawerToggle
    lateinit var drawerLayout: DrawerLayout
    lateinit var navView: NavigationView
    lateinit var addButton: FloatingActionButton
    lateinit var  calendar : Calendar
    lateinit var saveAlarmButton : Button
    lateinit var alarmViewModel: AlertsViewModel
    var time: Long = 0
    var finalTime: Long = 0
    var currentTime: Long = 0
    var DefaultDay = 0
    var DefaultMonth = 0
    var DefaultYear = 0

    var checkedInDay = 0
    var checkedInMonth = 0
    var checkedInYear = 0

    var checkedOutDay = 0
    var checkedOutMonth = 0
    var checkedOutYear = 0
    var day = 0
    var month = 0
    var year = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_alerts)

        addButton = findViewById(R.id.add_fab)

        val actionBar: ActionBar = supportActionBar!!
        val colorDrawable = ColorDrawable(Color.parseColor("#5B86E5"))
        actionBar.setBackgroundDrawable(colorDrawable)


        drawerLayout = findViewById(R.id.drawerLayout)
        navView = findViewById(R.id.nav_view)

        toggle = ActionBarDrawerToggle(this, drawerLayout, R.string.open, R.string.close)
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        navView.setNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.nav_home -> startActivity(Intent(this@AlertsActivity, HomeScreen::class.java))
                R.id.nav_settings -> startActivity(Intent(this@AlertsActivity, SettingsActivity::class.java))
                R.id.nav_favorite -> startActivity(Intent(this@AlertsActivity, FavoriteActivity::class.java))
                R.id.nav_alerts -> startActivity(Intent(this@AlertsActivity, AlertsActivity::class.java))
            }
            true
        }

        alarmViewModel = ViewModelProvider(
            this, factory = AlertsViewModelFactory(
                Repository.getInstance(
                    WeatherService.getInstance(), ConcreteLocalSource(this), this))
        )
            .get(AlertsViewModel::class.java)

        addButton.setOnClickListener { showDialog() }
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (toggle.onOptionsItemSelected(item)) {
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    private fun showDialog() {

        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(false)
        dialog.setContentView(R.layout.alert_custom_dialog)
        startDate = dialog.findViewById(R.id.startDateText) as TextView
        endDate = dialog.findViewById(R.id.endDateText) as TextView
        alarmTime = dialog.findViewById(R.id.alarm_time_text) as TextView
        saveAlarmButton = dialog.findViewById(R.id.save_alarm_button)



        saveAlarmButton.setOnClickListener {
            calendar = Calendar.getInstance()
            var startDateInMills = Utilities.convertDateAndTimeToMills(startDate.text.toString())
            var endDateInMills = Utilities.convertDateAndTimeToMills(endDate.text.toString())
            var alarmTimeInMills = Utilities.convertDateAndTimeToMills(alarmTime.text.toString())
            var userAlarm = UserAlarm(0,startDateInMills, endDateInMills, alarmTimeInMills)
            alarmViewModel.insertAlarm(userAlarm)
        }

        setOnClick()
        dialog.show()


    }
    private fun setOnClick(){
        startDate.setOnClickListener{
            getDateTimeCalender()
            DatePickerDialog(this,checkInDatePicker,year,month,day).show()
        }
        endDate.setOnClickListener{
            getDateTimeCalender()
            DatePickerDialog(this,checkOutDatePicker,year,month,day).show()

        }
        alarmTime.setOnClickListener {

            calendar = Calendar.getInstance()

            TimePickerDialog(this, timeSetListener, calendar[Calendar.HOUR_OF_DAY], calendar[Calendar.MINUTE], false).show()
        }
    }

    private fun getDateTimeCalender() {
        val cal = Calendar.getInstance()
        DefaultDay = cal.get(Calendar.DAY_OF_MONTH)
        DefaultMonth = cal.get(Calendar.MONTH)
        DefaultYear = cal.get(Calendar.YEAR)
    }
    val checkInDatePicker = object: DatePickerDialog.OnDateSetListener{
        override fun onDateSet(view: DatePicker?, year: Int, month: Int, dayOfMonth: Int) {
            calendar = Calendar.getInstance()
        /*    checkedInDay = dayOfMonth
            checkedInMonth = month + 1
            checkedInYear = year*/
            val simpleDateFormat = SimpleDateFormat("dd-MM-yyyy HH:mm")
            calendar[Calendar.YEAR] = year
            calendar[Calendar.MONTH] = month
            calendar[Calendar.DAY_OF_MONTH] = dayOfMonth
            startDate.text = simpleDateFormat.format(calendar.getTime())
            //startDate.text = "$checkedInDay-$checkedInMonth-$checkedInYear"
        }
    }
    val checkOutDatePicker = object: DatePickerDialog.OnDateSetListener{
        override fun onDateSet(view: DatePicker?, year: Int, month: Int, dayOfMonth: Int) {
            checkedOutDay = dayOfMonth
            checkedOutMonth = month + 1
            checkedOutYear = year
            val simpleDateFormat = SimpleDateFormat("dd-MM-yyyy hh:mm")
            calendar[Calendar.YEAR] = year
            calendar[Calendar.MONTH] = month
            calendar[Calendar.DAY_OF_MONTH] = dayOfMonth
            endDate.text = simpleDateFormat.format(calendar.getTime())
           // endDate.text = "$checkedOutDay-$checkedOutMonth-$checkedOutYear"
        }
    }
    val timeSetListener =
        OnTimeSetListener { view, hourOfDay, minute ->
            calendar[Calendar.HOUR_OF_DAY] = hourOfDay
            calendar[Calendar.MINUTE] = minute
            val simpleDateFormat = SimpleDateFormat("dd-MM-yyyy HH:mm")
            alarmTime.setText(simpleDateFormat.format(calendar.getTime()));
            time = calendar.timeInMillis
            Log.i("selected time ", "selected Time $time")
            currentTime = Calendar.getInstance().timeInMillis
            finalTime = time - currentTime
            Log.i("finalTime ", "finalTime $finalTime")
            /*val downloadRequest: OneTimeWorkRequest =
                OneTimeWorkRequest.Builder(AlertsWorkManger::class.java) // .setInputData(data)
                    .setInitialDelay(finalTime, TimeUnit.MILLISECONDS)
                    .build()
            WorkManager.getInstance(this).enqueue(downloadRequest)*/
        }


    fun showDateTimeDialog() {
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
                            time = calendar.timeInMillis
                            Log.i("selected time ", "selected Time $time")
                            currentTime = Calendar.getInstance().timeInMillis
                             finalTime = time - currentTime
                            Log.i("finalTime ", "finalTime $finalTime")
                            val downloadRequest: OneTimeWorkRequest =
                                OneTimeWorkRequest.Builder(AlertsWorkManger::class.java) // .setInputData(data)
                                    .setInitialDelay(finalTime, TimeUnit.MILLISECONDS)
                                    .build()
                            WorkManager.getInstance(this).enqueue(downloadRequest)
                        }
                    TimePickerDialog(
                        this, timeSetListener,
                        calendar[Calendar.HOUR_OF_DAY],
                        calendar[Calendar.MINUTE], false
                    ).show()
                }
            DatePickerDialog(
                this, dateSetListener,
                calendar[Calendar.YEAR], calendar[Calendar.MONTH], calendar[Calendar.DAY_OF_MONTH]
            ).show()
    }
}
