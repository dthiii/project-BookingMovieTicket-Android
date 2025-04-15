package com.example.project_android_booking_movietickets

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import com.example.project_android_booking_movietickets.adapter.SeatAdapter
import com.example.project_android_booking_movietickets.dao.CinemaDao
import com.example.project_android_booking_movietickets.dao.SeatDao
import com.example.project_android_booking_movietickets.databinding.ActivitySeatSelectionBinding
import com.example.project_android_booking_movietickets.model.Seat
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import androidx.lifecycle.lifecycleScope
import com.example.project_android_booking_movietickets.model.OrderItem
import java.text.DecimalFormat
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class SeatSelectionActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySeatSelectionBinding
    private lateinit var seatAdapter: SeatAdapter
    private val seatList = mutableListOf<Seat>()
    private lateinit var cinemaDao: CinemaDao
    private var maSuatChieu: String? = null

    private val ListItem = mutableListOf<OrderItem>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySeatSelectionBinding.inflate(layoutInflater)
        setContentView(binding.root)

        cinemaDao = CinemaDao(this)
        maSuatChieu = intent.getStringExtra("MA_SUAT_CHIEU")
        Log.d("masc", "maSuatChieu: $maSuatChieu")

        setupRecyclerView()
        loadSeats()

        if (maSuatChieu != null) {
            loadShowtimeDetails(maSuatChieu!!)
        }

        binding.imbBack.setOnClickListener { finish() }


        val btnContinue = findViewById<Button>(R.id.btnContinue)
        btnContinue.setOnClickListener {
            val totalPrice = binding.tvTotalPrice.text.toString().replace("[^\\d]".toRegex(), "").toIntOrNull() ?: 0
            val intent = Intent(this, FoodSelectionActivity::class.java).apply {
                putExtra("CINEMA_NAME", binding.tvCinemaName.text.toString())
                putExtra("SHOWTIME", binding.tvShowtime.text.toString())
                putExtra("MOVIE_TITLE", binding.tvMovieTitle.text.toString())
                putExtra("SELECTED_SEATS", binding.tvTotalSeats.text.toString())
                putExtra("TOTAL_PRICE", totalPrice)
                putExtra("MA_SUAT_CHIEU", maSuatChieu)
                putParcelableArrayListExtra("ListItem", ArrayList(ListItem))
            }
            startActivity(intent)
        }
    }

    private fun setupRecyclerView() {
        val gridLayoutManager = GridLayoutManager(this, 8)
        gridLayoutManager.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
            override fun getSpanSize(position: Int): Int {
                return if (seatList.getOrNull(position)?.MaLoaiGhe == "C") 2 else 1
            }
        }
        binding.recyclerViewSeats.layoutManager = gridLayoutManager

        seatAdapter = SeatAdapter(this, seatList, ::onSeatSelected, ::onTotalPriceUpdated)
        binding.recyclerViewSeats.adapter = seatAdapter
    }

    private fun loadShowtimeDetails(maSuatChieu: String) {
        val showtime = cinemaDao.getShowtimeById(maSuatChieu)
        if (showtime != null) {
            binding.tvCinemaName.text = showtime.tenRap
            binding.tvShowtime.text = "${showtime.ngayChieu}  ${showtime.gioChieu} ~ ${calculateEndTime(showtime.gioChieu, showtime.thoiLuong)}"
            binding.tvMovieTitle.text = showtime.tenPhim
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

    private fun loadSeats() {
        val seatDao = SeatDao(this)
        lifecycleScope.launch(Dispatchers.IO) {
            if (maSuatChieu != null) {
                val seats = seatDao.getSeatsByShowtime(maSuatChieu.toString()).sortedWith(compareBy({ it.HangGhe }, { it.CotGhe }))
                // Cập nhật lại UI trong main thread
                withContext(Dispatchers.Main) {
                    seatList.clear()
                    seatList.addAll(seats)
                    seatAdapter.notifyDataSetChanged()
                }
            } else {
                Log.e("SeatSelectionActivity", "maSuatChieu is null")
            }

        }
    }

    private fun onTotalPriceUpdated(totalPrice: Int) {
        binding.tvTotalPrice.text = formatPrice(totalPrice)
    }

    // Hàm định dạng tiền tệ
    private fun formatPrice(price: Int): String {
        val formatter: NumberFormat = DecimalFormat("#,###đ")
        return formatter.format(price)
    }

    private fun onSeatSelected(seatCount: Int, selectedSeats: List<OrderItem>) {
        binding.tvTotalSeats.text = "Số ghế đã chọn: $seatCount"

        ListItem.clear()
        ListItem.addAll(selectedSeats)
    }
}
