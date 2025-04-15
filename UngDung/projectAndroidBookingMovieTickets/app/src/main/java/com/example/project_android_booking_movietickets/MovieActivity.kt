package com.example.project_android_booking_movietickets

import android.content.Intent
import android.os.Bundle
import android.widget.ImageButton
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.project_android_booking_movietickets.R
import com.example.project_android_booking_movietickets.ShowtimeActivity
import com.example.project_android_booking_movietickets.dao.MovieDao
import com.example.project_android_booking_movietickets.model.Movie

class MovieActivity : AppCompatActivity() {
    private lateinit var maPhim: String
    private lateinit var imbBack: ImageButton
    private lateinit var movieDao: MovieDao

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_movie)

        imbBack = findViewById(R.id.imbBack)
        imbBack.setOnClickListener {
            finish()
        }

        // Nhận mã phim từ Intent
        maPhim = intent.getStringExtra("MaPhim") ?: return

        movieDao = MovieDao(this)
        val movie: Movie? = movieDao.getMovieById(maPhim)
        if (movie != null) {
            // Ánh xạ View
            val imgBanner: ImageView = findViewById(R.id.imgBanner)
            val tvTitle: TextView = findViewById(R.id.tvMovieTitle)
            val tvGenre: TextView = findViewById(R.id.tvGenre)
            val tvDuration: TextView = findViewById(R.id.tvDuration)
            val tvDescription: TextView = findViewById(R.id.tvDescription)
            val tvReleaseDate: TextView = findViewById(R.id.tvReleaseDate)
            val tvCategory: TextView = findViewById(R.id.tvCategory)
            val tvLanguage: TextView = findViewById(R.id.tvLanguage)
            val tvDirector: TextView = findViewById(R.id.tvDirector)
            val tvActor: TextView = findViewById(R.id.tvActor)

            // Cập nhật UI
            tvTitle.text = movie.TenPhim
            tvDuration.text = "\uD83D\uDD52 ${movie.ThoiLuong} phút"
            tvGenre.text = movie.TheLoai
            tvDescription.text = movie.MoTa
            tvReleaseDate.text = movie.NgayKhoiChieu
            tvCategory.text = movie.PhanLoai
            tvLanguage.text = movie.NgonNgu
            tvDirector.text = movie.DaoDien
            tvActor.text = movie.DienVien

            // Load ảnh poster bằng Glide
            Glide.with(this)
                .load(movie.Poster)
                .into(imgBanner)
        }

        val btnBooking: Button = findViewById(R.id.btnBooking)
        btnBooking.setOnClickListener {
            val intent = Intent(this, ShowtimeActivity::class.java)
            intent.putExtra("MaPhim", movie?.MaPhim)
            startActivity(intent)
        }
    }
}