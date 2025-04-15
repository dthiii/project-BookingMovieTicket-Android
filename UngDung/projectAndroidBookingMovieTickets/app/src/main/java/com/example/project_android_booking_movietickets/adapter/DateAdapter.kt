package com.example.project_android_booking_movietickets.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.project_android_booking_movietickets.R
import java.text.SimpleDateFormat
import java.util.*

class DateAdapter(
    private val onDateSelected: (String) -> Unit
) : RecyclerView.Adapter<DateAdapter.DateViewHolder>() {

    private val calendar = Calendar.getInstance()
    private val dateList = mutableListOf<Date>()
    private var selectedPosition = -1
    private var todayPosition = -1  // Thêm biến toàn cục

    init {
        // Đưa về đầu tuần (Thứ 2)
        calendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY)
        for (i in 0..6) {
            dateList.add(calendar.time)
            calendar.add(Calendar.DAY_OF_MONTH, 1)
        }

        val today = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }

        // Tìm vị trí của ngày hiện tại trong dateList
        todayPosition = dateList.indexOfFirst { date ->
            val cal = Calendar.getInstance().apply { time = date }
            cal.get(Calendar.YEAR) == today.get(Calendar.YEAR) &&
                    cal.get(Calendar.DAY_OF_YEAR) == today.get(Calendar.DAY_OF_YEAR)
        }

        selectedPosition = if (todayPosition != -1) todayPosition else 0  // Mặc định chọn ngày hiện tại
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DateViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_date, parent, false)
        return DateViewHolder(view)
    }

    override fun onBindViewHolder(holder: DateViewHolder, position: Int) {
        val date = dateList[position]
        val dateFormat = SimpleDateFormat("dd", Locale.getDefault())
        val dayFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        val dateString = dayFormat.format(date)

        val calendar = Calendar.getInstance().apply { time = date }
        val dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK)

        holder.tvDate.text = dateFormat.format(date)
        holder.tvWeek.text = when (dayOfWeek) {
            Calendar.SUNDAY -> "CN"  // Chủ Nhật giữ nguyên
            else -> "T${dayOfWeek}" // Chỉnh lại thứ đúng chuẩn (T2 - T7)
        }

        val isToday = (position == todayPosition)
        val isSelected = (position == selectedPosition)

        when {
            isSelected -> {
                // Nếu là ngày được chọn, áp dụng nền được chọn
                holder.itemView.setBackgroundResource(R.drawable.bg_selected_item)
            }
            isToday -> {
                // Nếu là ngày hiện tại và chưa được chọn, áp dụng viền xám
                holder.itemView.setBackgroundResource(R.drawable.bg_today_date)
            }
            else -> {
                // Mặc định cho các ngày khác
                holder.itemView.setBackgroundResource(R.drawable.bg_unselected_date)
            }
        }

        holder.itemView.isSelected = selectedPosition == position

        holder.itemView.setOnClickListener {
            selectedPosition = position
            onDateSelected(dateString)
            notifyDataSetChanged()
        }
    }

    override fun getItemCount(): Int = dateList.size

    class DateViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvDate: TextView = view.findViewById(R.id.tvDate)
        val tvWeek: TextView = view.findViewById(R.id.tvWeek)
    }
}
