package com.example.project_android_booking_movietickets

import android.R
import android.content.Intent
import android.graphics.Paint
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.project_android_booking_movietickets.DBHelper.DatabaseHelper
import com.example.project_android_booking_movietickets.adapter.OrderAdapter
import com.example.project_android_booking_movietickets.dao.FoodDao
import com.example.project_android_booking_movietickets.dao.FoodTicketDao
import com.example.project_android_booking_movietickets.dao.MovieDao
import com.example.project_android_booking_movietickets.dao.SeatDao
import com.example.project_android_booking_movietickets.dao.SeatTicketDao
import com.example.project_android_booking_movietickets.dao.TicketDao
import com.example.project_android_booking_movietickets.databinding.ActivityBillInfoBinding
import com.example.project_android_booking_movietickets.model.Movie
import com.example.project_android_booking_movietickets.model.OrderItem


class BillInfoActivity : AppCompatActivity() {
    private lateinit var movieDao: MovieDao

    private val binding: ActivityBillInfoBinding by lazy {
        ActivityBillInfoBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        binding.imbBack.setOnClickListener {
            finish()
        }

        binding.tvTerms.paintFlags = binding.tvTerms.paintFlags or Paint.UNDERLINE_TEXT_FLAG


        movieDao = MovieDao(this)


        //Hiển thị các thông tin mua hàng
        val cinemaName = intent.getStringExtra("CINEMA_NAME")
        val showtime = intent.getStringExtra("SHOWTIME")
        val movieTitle = intent.getStringExtra("MOVIE_TITLE")
        val maSuatChieu = intent.getStringExtra("MA_SUAT_CHIEU")
        val totalPrice = intent.getIntExtra("TOTAL_PRICE", 0)

        val ListItem = intent.getParcelableArrayListExtra<OrderItem>("ListItem") ?: mutableListOf()


        val dsghe=SeatDao(this)
        val ghes = dsghe.getAllSeats()
        Log.d("BillInfoActivity", "Danh sách món ăn: $ghes")


        // Cập nhật UI
        binding.tvMovieTitle.text = movieTitle
        binding.tvCinemaName.text = cinemaName
        binding.tvShowtime.text = showtime
        binding.tvTotalPrice.text = totalPrice.toString() + "đ"

        val movie: Movie? = maSuatChieu?.let { movieDao.getMovieByShowtime(it) }
        if (movie != null) {
            binding.tvCatalogy.text = movie.PhanLoai

            // Load ảnh poster bằng Glide
            Glide.with(this)
                .load(movie.Poster)
                .into(binding.imgBanner)
        }

        //Hiển thị listOrder
        binding.recyclerViewOrderItems.layoutManager = LinearLayoutManager(this)
        val adapter = OrderAdapter(ListItem)
        binding.recyclerViewOrderItems.adapter = adapter

        val ticketDao = TicketDao(this)
        val tickets = ticketDao.getAllTickets()
        for (ticket in tickets) {
            Log.d("CheckVe", "Vé: $ticket")
        }

        val seatticketDao = SeatTicketDao(this)
        val seattickets = seatticketDao.getAllSeatTickets()

// Log dữ liệu vé để kiểm tra
        for (seatticket in seattickets) {
            Log.d("CheckVeGhe", "Vé: $seatticket")
        }




        binding.btnPayment.setOnClickListener {
            if (!binding.checkboxTerms.isChecked) {
                Toast.makeText(this, "Bạn phải đồng ý với điều khoản", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val ticketDao = TicketDao(this)
            val foodTicketDao = FoodTicketDao(this)
            val seatTicketDao = SeatTicketDao(this)
            val foodDao = FoodDao(this)

            val maTK = 123 // Giả sử lấy từ phiên đăng nhập
            val thoiGianDat = System.currentTimeMillis().toString()

            // Thêm vé vào bảng Ve
            val maVe = ticketDao.insertTicket(maSuatChieu ?: "", maTK, thoiGianDat, totalPrice.toInt())
            Log.d("BillInfoActivity", "Mã vé được tạo: $maVe")

            if (maVe == -1L) {
                Toast.makeText(this, "Lỗi khi tạo vé!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            for (item in ListItem) {
                if (item.ten.startsWith("Ghế")) {
                    // Loại bỏ chữ "Ghế " khỏi item.ten
                    val tenGhe = item.ten.replaceFirst("Ghế ", "")

                    // Lưu thông tin ghế vào bảng VeGhe
                    seatTicketDao.insertSeatTicket(maVe, tenGhe, item.donGia)
                } else {
                    // Lấy MaDoAn từ FoodDao
                    val maDoAn = foodDao.getIdByName(item.ten)
                    if (maDoAn != null) {
                        // Lưu vào bảng VeAn với MaDoAn
                        foodTicketDao.insertFoodTicket(maVe, maDoAn, item.soLuong, item.donGia)
                    } else {
                        Toast.makeText(this, "Lỗi: Không tìm thấy mã đồ ăn cho ${item.ten}", Toast.LENGTH_SHORT).show()
                    }
                }
            }

            val builder = AlertDialog.Builder(this)
            builder.setMessage("Bạn đã đặt vé thành công")
                .setPositiveButton("OK") { dialog, id ->
                    // Quay lại màn hình MainActivity khi nhấn "OK"
                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                    finish()  // Kết thúc Activity hiện tại và trở lại MainActivity
                }
                .create()
                .show()
        }
    }

    fun sendEmailWithTicketDetails(ticketDetails: String) {
        val emailIntent = Intent(Intent.ACTION_SENDTO).apply {
            data = Uri.parse("mailto:") // Chỉ định để mở ứng dụng email
            putExtra(Intent.EXTRA_EMAIL, arrayOf("email@example.com")) // Địa chỉ email nhận
            putExtra(Intent.EXTRA_SUBJECT, "Thông tin đặt vé") // Tiêu đề email
            putExtra(Intent.EXTRA_TEXT, ticketDetails) // Nội dung email là thông tin vé
        }

        // Kiểm tra xem có ứng dụng email nào không
        if (emailIntent.resolveActivity(packageManager) != null) {
            startActivity(emailIntent)
        } else {
            Toast.makeText(this, "Không thể gửi email", Toast.LENGTH_SHORT).show()
        }
    }
}