package com.example.project_android_booking_movietickets.fragment.movie

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.ContextThemeWrapper
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.project_android_booking_movietickets.DBHelper.DatabaseHelper
import com.example.project_android_booking_movietickets.R
import com.example.project_android_booking_movietickets.dao.MovieDao
import com.example.project_android_booking_movietickets.model.Movie
import com.google.android.material.textfield.TextInputEditText
import java.util.Calendar


class MovieAddActivity : AppCompatActivity() {
    private lateinit var databaseHelper: DatabaseHelper
    private lateinit var movieDao: MovieDao

    private lateinit var edtMovieId: TextInputEditText
    private lateinit var edtMovieName: TextInputEditText
    private lateinit var spnGenre: Spinner
    private lateinit var edtDuration: TextInputEditText
    private lateinit var edtDescription: TextInputEditText
    private lateinit var edtReleaseDate: TextInputEditText
    private lateinit var edtCatalogy: TextInputEditText
    private lateinit var edtLanguage: TextInputEditText
    private lateinit var edtDirector: TextInputEditText
    private lateinit var edtActor: TextInputEditText
    private lateinit var edtPosterUrl: TextInputEditText
    private lateinit var btnBack: ImageButton
    private lateinit var imvReleaseDate: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_movie_edit)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        databaseHelper = DatabaseHelper(this)
        movieDao = MovieDao(this)

        edtMovieId = findViewById(R.id.edtMovieId)
        edtMovieName = findViewById(R.id.edtMovieName)
        spnGenre = findViewById(R.id.spnGenre)
        edtDuration = findViewById(R.id.edtDuration)
        edtDescription = findViewById(R.id.edtDescript)
        edtReleaseDate = findViewById(R.id.edtReleaseDate)
        edtCatalogy = findViewById(R.id.edtCatalogy)
        edtLanguage = findViewById(R.id.edtLanguage)
        edtDirector = findViewById(R.id.edtDirector)
        edtActor = findViewById(R.id.edtActor)
        edtPosterUrl = findViewById(R.id.edtPosterUrl)
        btnBack = findViewById(R.id.imbBack)
        imvReleaseDate = findViewById(R.id.imvReleaseDate)

        val adapter = ArrayAdapter.createFromResource(
            this,
            R.array.movie_genres,
            R.layout.spinner_item
        )
        adapter.setDropDownViewResource(R.layout.spinner_dropdown_item)
        spnGenre.adapter = adapter

        edtMovieId.setText(movieDao.generateNewId())

        val btnEdit = findViewById<Button>(R.id.btnEdit)
        btnEdit.text = "Thêm"
        btnEdit.setOnClickListener {
            addNewMovie()
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
            .setPositiveButton("Có") { _, _ ->
                super.onBackPressed()
            }
            .setNegativeButton("Không") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

    private fun addNewMovie() {
        val movieId = edtMovieId.text.toString()
        val title = edtMovieName.text.toString()
        val genre = spnGenre.selectedItem.toString()
        val duration = edtDuration.text.toString().toIntOrNull() ?: 0
        val description = edtDescription.text.toString()
        val releaseDate = edtReleaseDate.text.toString()
        val category = edtCatalogy.text.toString()
        val language = edtLanguage.text.toString()
        val director = edtDirector.text.toString()
        val actor = edtActor.text.toString()
        val posterUrl = edtPosterUrl.text.toString()

        if (validateFields()) {
            val movie = Movie(movieId, title, genre, duration, description, posterUrl, releaseDate, category, language, director, actor)
            val rowsAffected = movieDao.insertMovie(movie)
            if (rowsAffected > 0) {
                Toast.makeText(this, "Thêm phim thành công", Toast.LENGTH_SHORT).show()
                finish()
            } else {
                Toast.makeText(this, "Thêm phim thất bại", Toast.LENGTH_SHORT).show()
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
            edtReleaseDate.error = "Ngày khởi chiếu không được để trống"
            edtReleaseDate.requestFocus()
            return false
        }
        return true
    }
}