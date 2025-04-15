package com.example.project_android_booking_movietickets

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.project_android_booking_movietickets.adapter.FoodAdapter
import com.example.project_android_booking_movietickets.dao.FoodDao
import com.example.project_android_booking_movietickets.databinding.ActivityComboSelectionBinding
import com.example.project_android_booking_movietickets.model.OrderItem
import java.text.DecimalFormat
import java.text.NumberFormat

class FoodSelectionActivity : AppCompatActivity() {
    private lateinit var binding: ActivityComboSelectionBinding
    private lateinit var foodAdapter: FoodAdapter
    private lateinit var foodDao: FoodDao
    private var totalPrice: Int = 0
    private var maSuatChieu: String? = null
    private var orderList: MutableList<OrderItem> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityComboSelectionBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.imbBack.setOnClickListener { finish() }

        // Nhận dữ liệu từ Intent
        maSuatChieu = intent.getStringExtra("MA_SUAT_CHIEU")
        val cinemaName = intent.getStringExtra("CINEMA_NAME")
        val showtime = intent.getStringExtra("SHOWTIME")
        val movieTitle = intent.getStringExtra("MOVIE_TITLE")
        val selectedSeats = intent.getStringExtra("SELECTED_SEATS")
        val totalSeatPrice = intent.getIntExtra("TOTAL_PRICE", 0)

        // Nhận danh sách đã chọn từ Intent
        orderList = intent.getParcelableArrayListExtra<OrderItem>("ListItem")?.toMutableList() ?: mutableListOf()
        totalPrice = totalSeatPrice

        Log.d("FoodSelection", "Total Seat Price: $totalSeatPrice")
        Log.d("intentListItem", "Truyền intent chưa: ${orderList}")

        // Hiển thị dữ liệu lên TextView
        binding.tvCinemaName.text = cinemaName
        binding.tvShowtime.text = showtime
        binding.tvMovieTitle.text = movieTitle
        binding.tvTotalSeats.text = selectedSeats
        binding.tvTotalPrice.text = formatPrice(totalPrice)

        foodDao = FoodDao(this)
        setupRecyclerView()

        binding.btnPayment.setOnClickListener {
            val totalPrice = binding.tvTotalPrice.text.toString().replace("[^\\d]".toRegex(), "").toIntOrNull() ?: 0
            val intent = Intent(this, BillInfoActivity::class.java).apply {
                putExtra("CINEMA_NAME", binding.tvCinemaName.text.toString())
                putExtra("SHOWTIME", binding.tvShowtime.text.toString())
                putExtra("MOVIE_TITLE", binding.tvMovieTitle.text.toString())
                putExtra("SELECTED_SEATS", binding.tvTotalSeats.text.toString())
                putExtra("TOTAL_PRICE", totalPrice)
                putExtra("MA_SUAT_CHIEU", maSuatChieu)
                putParcelableArrayListExtra("ListItem", ArrayList(orderList))
            }
            startActivity(intent)
        }
    }

    private fun setupRecyclerView() {
        val foodList = foodDao.getAllFoods()
        foodAdapter = FoodAdapter(this, foodList, binding.tvTotalPrice, totalPrice, orderList) { newTotal ->
            updateTotalPrice(newTotal) // Cập nhật tổng tiền khi có thay đổi
        }
        binding.recyclerViewCombo.layoutManager = LinearLayoutManager(this)
        binding.recyclerViewCombo.adapter = foodAdapter
    }

    private fun updateTotalPrice(newTotal: Int) {
        totalPrice = newTotal
        binding.tvTotalPrice.text = formatPrice(totalPrice)
        Log.d("FoodSelection", "Total Price Display: ${binding.tvTotalPrice.text}")
    }

    // Hàm định dạng tiền tệ
    private fun formatPrice(price: Int): String {
        val formatter: NumberFormat = DecimalFormat("#,###đ")
        return formatter.format(price)
    }
}
