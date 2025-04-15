package com.example.project_android_booking_movietickets.adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.project_android_booking_movietickets.R
import com.example.project_android_booking_movietickets.dao.SeatTypeDao
import com.example.project_android_booking_movietickets.databinding.ItemSeatBinding
import com.example.project_android_booking_movietickets.model.OrderItem
import com.example.project_android_booking_movietickets.model.Seat

class SeatAdapter(
    private val context: Context,
    private val seatList: List<Seat>,
    private val onSeatSelected: (Int, List<OrderItem>) -> Unit,
    var onTotalPriceUpdated: ((Int) -> Unit)? = null
) : RecyclerView.Adapter<SeatAdapter.SeatViewHolder>() {

    private val selectedSeats = mutableSetOf<Int>()
    private val seatTypeDao = SeatTypeDao(context)
    private val selectedItemList = mutableSetOf<OrderItem>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SeatViewHolder {
        val binding = ItemSeatBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return SeatViewHolder(binding)
    }

    override fun onBindViewHolder(holder: SeatViewHolder, position: Int) {
        val seat = seatList[position]
        val isSelected = selectedSeats.contains(position)
        holder.bind(seat, isSelected)
    }

    override fun getItemCount(): Int = seatList.size

    inner class SeatViewHolder(private val binding: ItemSeatBinding) : RecyclerView.ViewHolder(binding.root) {
        private val tvSeatName: TextView  = itemView.findViewById(R.id.tvSeatName)

        fun bind(seat: Seat, isSelected: Boolean) {
            tvSeatName.text = seat.MaGhe

            val seatColor = when (seat.MaLoaiGhe) {
                "S" -> R.color.text_dark
                "V" -> R.color.pink
                "C" -> R.color.purple
                else -> R.color.text_dark
            }

            // Điều chỉnh kích thước nếu là ghế Couple
            val layoutParams = binding.seatView.layoutParams as ViewGroup.MarginLayoutParams
            if (seat.MaLoaiGhe == "C") {
                layoutParams.width = binding.root.context.resources.getDimensionPixelSize(R.dimen.couple_seat_width)
                layoutParams.leftMargin = 0
                layoutParams.rightMargin = 0
            } else {
                layoutParams.width = binding.root.context.resources.getDimensionPixelSize(R.dimen.normal_seat_width)
                layoutParams.leftMargin = 5
                layoutParams.rightMargin = 0
            }
            binding.seatView.layoutParams = layoutParams



            // Đổi màu ghế khi chọn
//            val finalColor = if (isSelected) R.color.text_highlight else seatColor
//            binding.seatView.backgroundTintList = ContextCompat.getColorStateList(context, finalColor)
//            //binding.seatView.backgroundTintList = ContextCompat.getColorStateList(context, finalColor)


            // Đổi màu ghế khi chọn, ghế đã đặt màu đỏ
            val finalColor = when {
                seat.isBooked -> R.color.red // Màu đỏ cho ghế đã đặt
                isSelected -> R.color.text_highlight // Màu cho ghế được chọn
                else -> seatColor // Màu mặc định cho ghế chưa chọn
            }

            binding.seatView.backgroundTintList = ContextCompat.getColorStateList(context, finalColor)


            // Xử lý sự kiện chọn ghế
            itemView.setOnClickListener {
                if (seat.isBooked) {
                    // Nếu ghế đã được đặt thì không làm gì cả
                    return@setOnClickListener
                }

                if (selectedSeats.contains(adapterPosition)) {
                    selectedSeats.remove(adapterPosition) // Bỏ chọn ghế
                } else {
                    selectedSeats.add(adapterPosition) // Chọn ghế
                }

                notifyItemChanged(adapterPosition) // Cập nhật màu ghế
                onSeatSelected(selectedSeats.size, getSelectedSeatsList()) // Truyền danh sách OrderItem

                val totalPrice = selectedSeats.sumOf { pos -> seatTypeDao.getPriceSeat(seatList[pos].MaLoaiGhe) }
                onTotalPriceUpdated?.invoke(totalPrice) // Gửi tổng tiền về Activity

                Log.d("SeatAdapter", "Danh sách ghế đã chọn: ${getSelectedSeatsList().joinToString { it.toString() }}")

            }
        }

        fun getSelectedSeatsList(): List<OrderItem> {
            return selectedSeats.map { pos ->
                val seat = seatList[pos]
                val price = seatTypeDao.getPriceSeat(seat.MaLoaiGhe)
                OrderItem(
                    ten = "Ghế ${seat.MaGhe}",
                    soLuong = 1,
                    donGia = price
                )
            }
        }
    }
}
