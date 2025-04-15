package com.example.project_android_booking_movietickets.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.project_android_booking_movietickets.R
import com.example.project_android_booking_movietickets.dao.ShowtimeDao
import com.example.project_android_booking_movietickets.model.Cinema

class CinemaAdapter(
    private val context: Context,
    private val maPhim: String,
    private val cinemas: List<Cinema>,
    private val ngayChieu: String
) : RecyclerView.Adapter<CinemaAdapter.CinemaViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CinemaViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_cinema, parent, false)
        return CinemaViewHolder(view)
    }

    override fun onBindViewHolder(holder: CinemaViewHolder, position: Int) {
        val cinema = cinemas[position]
        holder.tvCinemaName.text = cinema.TenRap

        // Lấy danh sách suất chiếu từ ShowtimeDao
        val showtimeDao = ShowtimeDao(context)
        val showtimes = showtimeDao.getShowtimesByMovieAndCinema(maPhim, cinema.MaRap, ngayChieu)

        // Cấu hình RecyclerView hiển thị suất chiếu
        val showtimeAdapter = ShowtimeAdapter(showtimes)
        holder.recyclerShowtimes.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        holder.recyclerShowtimes.adapter = showtimeAdapter

        // Mặc định ẩn danh sách suất chiếu
        holder.recyclerShowtimes.visibility = View.GONE

        // Xử lý khi nhấn vào nút Expand để hiển thị giờ chiếu
        var isExpanded = false
        holder.btnExpand.setOnClickListener {
            isExpanded = !isExpanded
            holder.recyclerShowtimes.visibility = if (isExpanded) View.VISIBLE else View.GONE
            holder.btnExpand.setImageResource(if (isExpanded) R.drawable.up else R.drawable.down)
        }
    }

    override fun getItemCount(): Int = cinemas.size

    class CinemaViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvCinemaName: TextView = itemView.findViewById(R.id.tvCinemaName)
        val btnExpand: ImageView = itemView.findViewById(R.id.btnExpand)
        val recyclerShowtimes: RecyclerView = itemView.findViewById(R.id.recyclerShowtimes)
    }
}
