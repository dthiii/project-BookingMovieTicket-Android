package com.example.project_android_booking_movietickets.fragment.movie

import android.app.Activity
import android.app.DatePickerDialog
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.ContextThemeWrapper
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.core.graphics.drawable.toBitmap
import com.example.project_android_booking_movietickets.DBHelper.DatabaseHelper
import com.example.project_android_booking_movietickets.R
import com.example.project_android_booking_movietickets.dao.MovieDao
import com.example.project_android_booking_movietickets.model.Movie
import com.google.android.material.textfield.TextInputEditText
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.util.Calendar

class MovieUpdateActivity : AppCompatActivity() {
    private lateinit var databaseHelper: DatabaseHelper
    private lateinit var movieDao: MovieDao

    private lateinit var edtMovieId: TextInputEditText
    private lateinit var edtMovieName: TextInputEditText
    private lateinit var spnGenre: Spinner
    private lateinit var edtDuration: TextInputEditText
    private lateinit var edtDescription: TextInputEditText
    private lateinit var edtReleaseDate: TextInputEditText
    private lateinit var edtLanguage: TextInputEditText
    private lateinit var edtDirector: TextInputEditText
    private lateinit var edtActor: TextInputEditText
    private lateinit var edtCatalogy: TextInputEditText
    private lateinit var edtPosterUrl: TextInputEditText
    private lateinit var btnBack: ImageButton
    private lateinit var imvReleaseDate: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_movie_edit)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val movieId = intent.getStringExtra("MOVIE_ID") ?: ""
        Log.d("MovieUpdateActivity", "Received MOVIE_ID: $movieId")

        databaseHelper = DatabaseHelper(this)
        movieDao = MovieDao(this)

        edtMovieId = findViewById(R.id.edtMovieId)
        edtMovieName = findViewById(R.id.edtMovieName)
        spnGenre = findViewById(R.id.spnGenre)
        edtDuration = findViewById(R.id.edtDuration)
        edtDescription = findViewById(R.id.edtDescript)
        edtReleaseDate = findViewById(R.id.edtReleaseDate)
        edtLanguage = findViewById(R.id.edtLanguage)
        edtDirector = findViewById(R.id.edtDirector)
        edtActor = findViewById(R.id.edtActor)
        edtCatalogy = findViewById(R.id.edtCatalogy)
        edtPosterUrl = findViewById(R.id.edtPosterUrl)
        btnBack = findViewById(R.id.imbBack)
        imvReleaseDate = findViewById(R.id.imvReleaseDate)

        loadMovieDetails(movieId)

        val adapter = ArrayAdapter.createFromResource(
            this,
            R.array.movie_genres,
            R.layout.spinner_item
        )
        adapter.setDropDownViewResource(R.layout.spinner_dropdown_item)
        spnGenre.adapter = adapter

        val btnSave = findViewById<Button>(R.id.btnEdit)
        btnSave.text = "Lưu thông tin"
        btnSave.setOnClickListener {
            saveMovieDetails()
        }

        btnBack.setOnClickListener{
            finish()
        }

        imvReleaseDate.setOnClickListener {
            val calendar = Calendar.getInstance()
            val datePicker = DatePickerDialog(
                ContextThemeWrapper(this, R.style.DarkDatePickerDialog),
                { _, year, month, dayOfMonth ->
                    val dateStr = "%02d/%02d/%04d".format(dayOfMonth, month + 1, year)
                    edtReleaseDate.setText(dateStr)
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
            )
            datePicker.show()
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    override fun onBackPressed() {
        AlertDialog.Builder(this)
            .setTitle("Xác nhận")
            .setMessage("Bạn có chắc chắn muốn quay lại không? Những thay đổi của bạn sẽ không được lưu.")
            .setPositiveButton("Có") { _, _ -> super.onBackPressed() }
            .setNegativeButton("Không") { dialog, _ -> dialog.dismiss() }
            .show()
    }

    private fun loadMovieDetails(movieId: String) {
        Log.d("MovieUpdateActivity", "Loading movie details for ID: $movieId")

        val movie = movieDao.getMovieById(movieId)

        if (movie != null) {
            Log.d("MovieUpdateActivity", "Movie found: ${movie.TenPhim}")
            edtMovieId.setText(movie.MaPhim)
            edtMovieName.setText(movie.TenPhim)

            // Chọn thể loại trong Spinner
            val genres = resources.getStringArray(R.array.movie_genres) // Danh sách thể loại phim
            val genreIndex = genres.indexOf(movie.TheLoai)
            if (genreIndex != -1) {
                spnGenre.setSelection(genreIndex)
            }

            edtDuration.setText(movie.ThoiLuong.toString())
            edtPosterUrl.setText((movie.Poster))
            edtDescription.setText(movie.MoTa)
            edtReleaseDate.setText(movie.NgayKhoiChieu)
            edtLanguage.setText(movie.NgonNgu)
            edtDirector.setText(movie.DaoDien)
            edtActor.setText(movie.DienVien)
            edtCatalogy.setText((movie.PhanLoai))

        } else {
            Toast.makeText(this, "Không tìm thấy phim", Toast.LENGTH_SHORT).show()
        }
    }

    private fun saveMovieDetails() {
        val movieId = edtMovieId.text.toString()
        val movieName = edtMovieName.text.toString()
        val genre = spnGenre.selectedItem.toString()
        val duration = edtDuration.text.toString().toIntOrNull() ?: 0
        val description = edtDescription.text.toString()
        val releaseDate = edtReleaseDate.text.toString()
        val catagory = edtCatalogy.text.toString()
        val language = edtLanguage.text.toString()
        val director = edtDirector.text.toString()
        val actor = edtActor.text.toString()
        val poster = edtPosterUrl.text.toString()

        if (validateFields()) {
            val movie = Movie(
                movieId, movieName, genre, duration, description, poster, releaseDate, catagory, language, director, actor)

            val rowsAffected = movieDao.update(movie)
            if (rowsAffected > 0) {
                Toast.makeText(this, "Cập nhật thông tin phim thành công", Toast.LENGTH_SHORT).show()
                val resultIntent = Intent()
                resultIntent.putExtra("MOVIE_ID", movie.MaPhim)
                setResult(RESULT_OK, resultIntent)
                finish()
            } else {
                Toast.makeText(this, "Cập nhật thông tin phim thất bại", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun validateFields(): Boolean {
        if (edtMovieName.text.isNullOrEmpty()) {
            edtMovieName.error = "Tên phim không được để trống"
            edtMovieName.requestFocus()
            return false
        }
        if (edtDuration.text.isNullOrEmpty()) {
            edtDuration.error = "Thời lượng không được để trống"
            edtDuration.requestFocus()
            return false
        } else {
            val duration = edtDuration.text.toString().toIntOrNull()
            if (duration == null || duration <= 0) {
                edtDuration.error = "Thời lượng không hợp lệ"
                edtDuration.requestFocus()
                return false
            }
        }
        if (edtReleaseDate.text.isNullOrEmpty()) {
            edtReleaseDate.error = "Ngày phát hành không được để trống"
            edtReleaseDate.requestFocus()
            return false
        }
        return true
    }
}
