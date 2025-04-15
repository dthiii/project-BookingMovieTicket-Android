package com.example.project_android_booking_movietickets

import android.os.Bundle
import android.util.Log
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.project_android_booking_movietickets.adapter.CinemaAdapter
import com.example.project_android_booking_movietickets.adapter.DateAdapter
import com.example.project_android_booking_movietickets.adapter.LocationAdapter
import com.example.project_android_booking_movietickets.dao.CinemaDao
import com.example.project_android_booking_movietickets.dao.MovieDao
import com.example.project_android_booking_movietickets.model.Cinema
import com.example.project_android_booking_movietickets.model.Movie


class ShowtimeActivity : AppCompatActivity() {

    private var selectedDate: String = ""
    private var selectedLocation: String = ""
    private lateinit var maPhim: String

    private lateinit var imbBack: ImageButton
    private lateinit var recyclerViewDates: RecyclerView
    private lateinit var recyclerViewLocations: RecyclerView
    private lateinit var recyclerViewCinemas: RecyclerView
    private lateinit var movieDao: MovieDao
    private lateinit var cinemaDao: CinemaDao

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_showtime)

        imbBack = findViewById(R.id.imbBack)
        imbBack.setOnClickListener {
            finish()
        }

        // Nhận mã phim từ Intent
        maPhim = intent.getStringExtra("MaPhim") ?: return

        movieDao = MovieDao(this)
        cinemaDao = CinemaDao(this)

        val movie: Movie? = movieDao.getMovieById(maPhim)

        if (movie != null) {
            // Ánh xạ View
            val imgBanner: ImageView = findViewById(R.id.imgBanner)
            val tvTitle: TextView = findViewById(R.id.tvMovieTitle)
            val tvDuration: TextView = findViewById(R.id.tvDuration)
            val tvCategory: TextView = findViewById(R.id.tvTBC)

            // Cập nhật UI
            tvTitle.text = movie.TenPhim
            tvDuration.text = "\uD83D\uDD52 ${movie.ThoiLuong} phút"
            tvCategory.text = movie.PhanLoai

            // Load ảnh poster bằng Glide
            Glide.with(this)
                .load(movie.Poster)
                .into(imgBanner)
        }

        // Khởi tạo RecyclerView ngày chiếu
        recyclerViewDates = findViewById(R.id.recyclerViewDates)
        recyclerViewDates.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)

        val dateAdapter = DateAdapter { date ->
            selectedDate = date
            loadCinemas()
        }
        recyclerViewDates.adapter = dateAdapter


        // Danh sách địa điểm
        val locations = listOf("TPHCM", "Hà Nội", "Huế", "Long Khánh", "Phú Mỹ")

        // Khởi tạo RecyclerView địa điểm
        recyclerViewLocations = findViewById(R.id.recyclerViewLocations)
        recyclerViewLocations.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)

        val adapterLocations = LocationAdapter(locations) { location ->
            selectedLocation = location
            loadCinemas()
        }
        recyclerViewLocations.adapter = adapterLocations


        //Danh sách rạp
        recyclerViewCinemas = findViewById(R.id.recyclerViewCinemas)
        recyclerViewCinemas.layoutManager = LinearLayoutManager(this)
    }

    private fun loadCinemas() {
        if (!::cinemaDao.isInitialized) {
            Log.e("ShowtimeActivity", "cinemaDao chưa được khởi tạo!")
            return
        }

        if (selectedDate.isNotEmpty() && selectedLocation.isNotEmpty()) {
            val cinemas: List<Cinema> = cinemaDao.getCinemasByLocationAndDate(maPhim, selectedLocation, selectedDate)
            Log.e("ShowtimeActivity", "Số lượng rạp lấy được: ${cinemas.size}")

            val cinemaAdapter = CinemaAdapter(this, maPhim, cinemas, selectedDate)
            recyclerViewCinemas.adapter = cinemaAdapter
        } else {
            Log.e("ShowtimeActivity", "Không thể load rạp vì selectedDate hoặc selectedLocation rỗng")
        }
    }
}
