package com.example.project_android_booking_movietickets.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.project_android_booking_movietickets.R
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class ShowtimeInfoAdapter(
    private val context: Context,
    private val showtimes: List<Map<String, String>>,
    private val onEditClick: (Map<String, String>) -> Unit,
    private val onDeleteClick: (Map<String, String>) -> Unit
) : RecyclerView.Adapter<ShowtimeInfoAdapter.ShowtimeViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ShowtimeViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_showtime_info, parent, false)
        return ShowtimeViewHolder(view)
    }

    override fun onBindViewHolder(holder: ShowtimeViewHolder, position: Int) {
        val showtime = showtimes[position]
        holder.bind(showtime)
    }

    override fun getItemCount(): Int = showtimes.size

    inner class ShowtimeViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvShowtimeId: TextView = itemView.findViewById(R.id.tvShowtimeId)
        private val tvMovieName: TextView = itemView.findViewById(R.id.tvMovieName)
        private val tvShowtime: TextView = itemView.findViewById(R.id.tvShowtime)

        fun bind(showtime: Map<String, String>) {
            tvShowtimeId.text = showtime["MaSuatChieu"]
            tvMovieName.text = showtime["TenPhim"]
            val ngayChieu = showtime["NgayChieu"]
            val gioChieu = showtime["GioChieu"] ?: ""
            val thoiLuong = showtime["ThoiLuong"]?.toInt() ?: 0

            val gioKetThuc = calculateEndTime(gioChieu, thoiLuong)

            tvShowtime.text = "$ngayChieu | $gioChieu - $gioKetThuc"

            itemView.findViewById<View>(R.id.imvEdit).setOnClickListener {
                onEditClick(showtime)
            }

            itemView.findViewById<View>(R.id.imvDelete).setOnClickListener {
                onDeleteClick(showtime)
            }
        }

    }

    private fun calculateEndTime(startTime: String, duration: Int): String {
        val sdf = SimpleDateFormat("HH:mm", Locale.getDefault())
        val date = sdf.parse(startTime)
        val calendar = Calendar.getInstance()
        date?.let {
            calendar.time = it
            calendar.add(Calendar.MINUTE, duration)
        }
        return sdf.format(calendar.time)
    }
}
