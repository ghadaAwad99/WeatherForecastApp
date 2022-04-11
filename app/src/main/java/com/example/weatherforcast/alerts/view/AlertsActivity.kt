package com.example.weatherforcast.alerts.view

import android.app.DatePickerDialog
import android.app.Dialog
import android.app.TimePickerDialog
import android.app.TimePickerDialog.OnTimeSetListener
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.Window
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.work.Data
import androidx.work.OneTimeWorkRequest
import androidx.work.WorkManager
import com.example.weatherforcast.R
import com.example.weatherforcast.Utilities
import com.example.weatherforcast.alerts.viewModel.AlertsViewModel
import com.example.weatherforcast.alerts.viewModel.AlertsViewModelFactory
import com.example.weatherforcast.database.ConcreteLocalSource
import com.example.weatherforcast.databinding.ActivityAlertsBinding
import com.example.weatherforcast.favorite.view.FavoriteActivity
import com.example.weatherforcast.home.view.HomeScreen
import com.example.weatherforcast.model.Repository
import com.example.weatherforcast.model.UserAlarm
import com.example.weatherforcast.network.WeatherService
import com.example.weatherforcast.settings.SettingsActivity
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

class AlertsActivity : AppCompatActivity(), onDeleteListener {
    private val binding by lazy { ActivityAlertsBinding.inflate(layoutInflater) }
    private val alarmViewModel by lazy { ViewModelProvider(
        this, factory = AlertsViewModelFactory(
            Repository.getInstance(
                WeatherService.getInstance(), ConcreteLocalSource(this), this
            )
        )
    ).get(AlertsViewModel::class.java) }

    private val alertAdapter by lazy { AlertsRecyclerAdapter(this) }

    lateinit var startDate: TextView
    lateinit var endDate: TextView
    lateinit var alarmTime: TextView
    lateinit var toggle: ActionBarDrawerToggle
    lateinit var calendar: Calendar
    lateinit var saveAlarmButton: Button

    var time: Long = 0
    var finalTime: Long = 0
    var currentTime: Long = 0

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        this.title = getString(R.string.alerts)

        binding.daysRecyclerView.adapter = alertAdapter

        initSideDrawer()
        binding.addFab.setOnClickListener { showDialog() }
        observeAlarms()
    }


    private fun observeAlarms(){
        alarmViewModel.getAllAlarms()

        alarmViewModel.alarmsLiveData.observe(this, {

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                setAlarmsListForManager(it)
                findNextAlarm()
                alertAdapter.setAlertsList(it)
            }
        })
    }

    private fun initSideDrawer() {
        val actionBar: ActionBar = supportActionBar!!
        val colorDrawable = ColorDrawable(Color.parseColor("#5B86E5"))
        actionBar.setBackgroundDrawable(colorDrawable)

        toggle = ActionBarDrawerToggle(this, binding.drawerLayout, R.string.open, R.string.close)
        binding.drawerLayout.addDrawerListener(toggle)
        toggle.syncState()
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.navView.setNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.nav_home -> startActivity(Intent(this@AlertsActivity, HomeScreen::class.java))
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

    @RequiresApi(Build.VERSION_CODES.O)
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
            val startDateInMills = Utilities.convertDateAndTimeToMills(startDate.text.toString())
            val endDateInMills = Utilities.convertDateAndTimeToMills(endDate.text.toString())
            val alarmTimeInMills = Utilities.convertDateAndTimeToMills(alarmTime.text.toString())
            val userAlarm = UserAlarm(0, startDateInMills, endDateInMills, alarmTimeInMills)
            alarmViewModel.insertAlarm(userAlarm)
            dialog.dismiss()
            finish()
            startActivity(Intent(this, this::class.java))

        }

        val cal = Calendar.getInstance()
        var myYEAR = cal.get(Calendar.YEAR)
        var myMonth = cal.get(Calendar.MONTH)
        var day = cal.get(Calendar.DAY_OF_MONTH)

        startDate.setOnClickListener {
            val datePickerDialog =
                DatePickerDialog(this, { datePicker, i, i2, i3 ->
                    val simpleDateFormat = SimpleDateFormat("dd-MM-yyyy HH:mm")
                    myYEAR = i
                    myMonth = i2
                    day = i3
                    cal.set(myYEAR, myMonth, day)
                    startDate.text = simpleDateFormat.format(cal.getTime())
                    val newDate: Calendar = Calendar.getInstance()
                    newDate.set(i, i2, i3)
                }, myYEAR, myMonth, day)
            datePickerDialog.show()
        }

        endDate.setOnClickListener {
            val datePickerDialog =
                DatePickerDialog(this, { datePicker, i, i2, i3 ->
                    val simpleDateFormat = SimpleDateFormat("dd-MM-yyyy HH:mm")
                    myYEAR = i
                    myMonth = i2
                    day = i3
                    cal.set(myYEAR, myMonth, day)
                    endDate.text = simpleDateFormat.format(cal.getTime())
                    val newDate: Calendar = Calendar.getInstance()
                    newDate.set(i, i2, i3)
                }, myYEAR, myMonth, day)
            datePickerDialog.show()
        }
        alarmTime.setOnClickListener {

            calendar = Calendar.getInstance()

            TimePickerDialog(this, timeSetListener, calendar[Calendar.HOUR_OF_DAY], calendar[Calendar.MINUTE],
                false).show()
        }
        dialog.show()
    }

    companion object {

        lateinit var alarmsList: List<UserAlarm>
        fun setAlarmsListForManager(alarmsList: List<UserAlarm>) {
            this.alarmsList = alarmsList
        }

        @RequiresApi(api = Build.VERSION_CODES.O)
        fun findNextAlarm() {
             //WorkManager.getInstance().cancelAllWorkByTag("alarms")
            val currentTime = Calendar.getInstance().timeInMillis
            Log.i("TAG", "current time$currentTime")
            var smallest = currentTime
            var scheduledAlarm: String? = null
            var timeInMills: Long = 0
            Log.i("TAG", "alarm list $alarmsList")
            for (alarm in alarmsList) {
                Log.i("TAG", " ")
                Log.i("TAG", " ")
                timeInMills = alarm.alarmTime
                Log.i(
                    "TAG",
                    "In side findRestMills " + timeInMills + " " + (timeInMills - currentTime)
                )
                Log.i("TAG", "current time$currentTime")
                if (timeInMills - currentTime >= 0 && timeInMills - currentTime < smallest) {
                    smallest = timeInMills - currentTime
                    scheduledAlarm = timeInMills.toString()
                    Log.i("TAG", "FinfResut If $scheduledAlarm")
                }
            }
            if (scheduledAlarm != null) {
                val currentTime = Calendar.getInstance().timeInMillis
                Log.i("TAG", "In side smallest reminder method")
                val finalTime = timeInMills - currentTime
                val reminderRequest = OneTimeWorkRequest.Builder(AlertsWorkManger::class.java)
                    .setInitialDelay(finalTime, TimeUnit.MILLISECONDS)
                    .build()
                WorkManager.getInstance().enqueue(reminderRequest)
            }

        }
    }

    val timeSetListener =
        OnTimeSetListener { view, hourOfDay, minute ->
            calendar[Calendar.HOUR_OF_DAY] = hourOfDay
            calendar[Calendar.MINUTE] = minute
            val simpleDateFormat = SimpleDateFormat("dd-MM-yyyy HH:mm")
            alarmTime.setText(simpleDateFormat.format(calendar.getTime()))
            time = calendar.timeInMillis
            Log.i("selected time ", "selected Time $time")
            currentTime = Calendar.getInstance().timeInMillis
            finalTime = time - currentTime
            Log.i("finalTime ", "finalTime $finalTime")
        }

    override fun onDeleteClick(alarm: UserAlarm) {
        alarmViewModel.deleteAlarm(alarm)
    }
}
