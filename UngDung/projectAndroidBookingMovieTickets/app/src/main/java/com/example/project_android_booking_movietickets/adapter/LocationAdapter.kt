package com.example.project_android_booking_movietickets.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.project_android_booking_movietickets.R

class LocationAdapter(
    private val locations: List<String>,
    private val onLocationSelected: (String) -> Unit
) : RecyclerView.Adapter<LocationAdapter.LocationViewHolder>() {

    private var selectedPosition = 0  // Mặc định chọn vị trí đầu tiên

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LocationViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_location, parent, false)
        return LocationViewHolder(view)
    }

    override fun onBindViewHolder(holder: LocationViewHolder, position: Int) {
        val location = locations[position]
        holder.tvLocation.text = location

        // Áp dụng trạng thái được chọn
        holder.itemView.isSelected = selectedPosition == position

        holder.itemView.setOnClickListener {
            selectedPosition = position
            onLocationSelected(location)
            notifyDataSetChanged()
        }
    }

    override fun getItemCount(): Int = locations.size

    class LocationViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvLocation: TextView = view.findViewById(R.id.tvLocation)
    }
}
