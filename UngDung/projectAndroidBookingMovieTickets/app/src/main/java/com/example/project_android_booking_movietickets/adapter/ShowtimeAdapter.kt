package com.example.project_android_booking_movietickets.adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.project_android_booking_movietickets.R
import com.example.project_android_booking_movietickets.SeatSelectionActivity
import com.example.project_android_booking_movietickets.model.Showtime

class ShowtimeAdapter(
    private val showtimes: List<Showtime>,
) : RecyclerView.Adapter<ShowtimeAdapter.ShowtimeViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ShowtimeViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_showtime, parent, false)
        return ShowtimeViewHolder(view)
    }

    override fun onBindViewHolder(holder: ShowtimeViewHolder, position: Int) {
        val showtime = showtimes[position]
        holder.tvShowtime.text = showtime.GioChieu

        holder.itemView.setOnClickListener {
            val intent = Intent(holder.itemView.context, SeatSelectionActivity::class.java)
            intent.putExtra("MA_SUAT_CHIEU", showtime.MaSuatChieu)
            holder.itemView.context.startActivity(intent)

        }
    }

    override fun getItemCount(): Int = showtimes.size

    class ShowtimeViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvShowtime: TextView = itemView.findViewById(R.id.tvShowtime)
    }
}
