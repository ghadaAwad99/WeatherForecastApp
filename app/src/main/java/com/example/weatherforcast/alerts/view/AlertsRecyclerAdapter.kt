package com.example.weatherforcast.alerts.view

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.weatherforcast.Utilities
import com.example.weatherforcast.databinding.AlarmItemBinding
import com.example.weatherforcast.databinding.DayItemBinding
import com.example.weatherforcast.databinding.FavoriteItemBinding
import com.example.weatherforcast.favorite.view.FavViewHolder
import com.example.weatherforcast.model.UserAlarm

class AlertsRecyclerAdapter(val onDeleteListener: onDeleteListener) : RecyclerView.Adapter<AlertsViewHolder>() {

    private var alertsLisr = mutableListOf<UserAlarm>()

    fun setAlertsList(alertsLisr: List<UserAlarm>) {
        this.alertsLisr = alertsLisr.toMutableList()
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AlertsViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = AlarmItemBinding.inflate(inflater, parent, false)
        return AlertsViewHolder(binding)
    }

    override fun onBindViewHolder(holder: AlertsViewHolder, position: Int) {
        val alert = alertsLisr[position]

        var time = Utilities.convertTimeInMillesToMinutes(alert.alarmTime)
        var start = Utilities.convertDateInMillsToDate(alert.startDate)
        var end = Utilities.convertDateInMillsToDate(alert.endDate)
        holder.binding.localityText.text = time
        holder.binding.alarmDate.text = " $start - $end"
        holder.binding.deleteButton.setOnClickListener { onDeleteListener.onDeleteClick(alert) }

    }

    override fun getItemCount() = alertsLisr.size
}

class AlertsViewHolder(val binding: AlarmItemBinding) : RecyclerView.ViewHolder(binding.root)