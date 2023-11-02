package com.sanjay.trackLocation.Adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.sanjay.trackLocation.Database.LocationObject
import com.sanjay.trackLocation.R

class LocationHistoryRVAdapter(
    private val context: Context,
    private val locationsList: List<LocationObject>,
    val selectedIndex: (Int) -> Unit
) : RecyclerView.Adapter<LocationHistoryRVAdapter.ViewHolder>() {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): LocationHistoryRVAdapter.ViewHolder {
        val itemView = LayoutInflater.from(context)
            .inflate(R.layout.location_history_rv_template, parent, false)
        return ViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val item = locationsList[position]
        holder.cityStateTV.text = item.city + ", " + item.state
        holder.pinCodeTV.text = item.pinCode
        holder.countryTV.text = item.country
        holder.dateTime.text = item.dateTime

        holder.cardItem.setOnClickListener { selectedIndex(position) }

    }

    override fun getItemCount(): Int {
        return locationsList.size
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val cardItem: CardView = itemView.findViewById(R.id.cardItem)
        val cityStateTV: TextView = itemView.findViewById(R.id.cityStateTV)
        val pinCodeTV: TextView = itemView.findViewById(R.id.pinCodeTV)
        val countryTV: TextView = itemView.findViewById(R.id.countryTV)
        val dateTime: TextView = itemView.findViewById(R.id.dateTimeTV)
    }

}