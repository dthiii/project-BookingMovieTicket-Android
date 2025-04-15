package com.example.project_android_booking_movietickets.fragment.showtime

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.project_android_booking_movietickets.dao.CinemaDao
import com.example.project_android_booking_movietickets.dao.MovieDao
import com.example.project_android_booking_movietickets.dao.ShowtimeDao
import com.example.project_android_booking_movietickets.databinding.ActivityShowtimeEditBinding
import com.example.project_android_booking_movietickets.model.Showtime
import java.util.Calendar

class ShowtimeUpdateActivity : AppCompatActivity() {
    private lateinit var binding: ActivityShowtimeEditBinding
    private lateinit var showtimeDao: ShowtimeDao
    private lateinit var movieDao: MovieDao
    private lateinit var cinemaDao: CinemaDao
    private lateinit var movieNames: List<String>


    private var showtimeId: String? = null
    private var movieName: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityShowtimeEditBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        showtimeDao = ShowtimeDao(this)
        movieDao = MovieDao(this)
        cinemaDao = CinemaDao(this)

        showtimeId = intent.getStringExtra("SHOWTIME_ID")
        movieName = intent.getStringExtra("MOVIE_NAME")
        if (showtimeId != null) {
            loadShowtimeDetails(showtimeId!!)
        }

        binding.edtShowtimeId.isEnabled = false

//        movieNames = movieDao.getAllMovieNames()
//        val adapter = ArrayAdapter(this, com.example.project_android_booking_movietickets.R.layout.spinner_item, movieNames)
//        adapter.setDropDownViewResource(com.example.project_android_booking_movietickets.R.layout.spinner_dropdown_item)
//        binding.spnMovieName.adapter = adapter

        binding.imvDate.setOnClickListener {
            val calendar = Calendar.getInstance()
            val datePicker = DatePickerDialog(
                this,
                { _, year, month, dayOfMonth ->
                    val dateStr = "%02d/%02d/%04d".format(dayOfMonth, month + 1, year)
                    binding.edtDate.setText(dateStr)
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
            )
            datePicker.show()
        }

        binding.imvTime.setOnClickListener {
            val calendar = Calendar.getInstance()
            val timePicker = TimePickerDialog(
                this,
                { _, hourOfDay, minute ->
                    val timeStr = "%02d:%02d".format(hourOfDay, minute)
                    binding.edtTime.setText(timeStr)
                },
                calendar.get(Calendar.HOUR_OF_DAY),
                calendar.get(Calendar.MINUTE),
                true
            )
            timePicker.show()
        }

        binding.btnEdit.text = "Cập nhật"
        binding.btnEdit.setOnClickListener {
            updateShowtime()
        }

        binding.imbBack.setOnClickListener {
            finish()
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

    private fun loadShowtimeDetails(showtimeId: String) {
        val showtime = showtimeDao.getShowtimeById(showtimeId)

        if (showtime != null) {
            binding.edtShowtimeId.setText(showtime.MaSuatChieu)
            binding.edtDate.setText(showtime.NgayChieu)
            binding.edtTime.setText(showtime.GioChieu)

            movieNames = movieDao.getAllMovieNames()
            movieNames = movieDao.getAllMovieNames()
            val adapter = ArrayAdapter(this, com.example.project_android_booking_movietickets.R.layout.spinner_item, movieNames)
            adapter.setDropDownViewResource(com.example.project_android_booking_movietickets.R.layout.spinner_dropdown_item)
            binding.spnMovieName.adapter = adapter

            val movieIndex = movieNames.indexOfFirst { it == movieName }
            Log.d("DEBUG", "Movie name from intent: $movieName")
            Log.d("DEBUG", "All movie names: $movieNames")


            if (movieIndex != -1) {
                binding.spnMovieName.setSelection(movieIndex)
            }
        }
    }

    private fun updateShowtime() {
        val showtimeId = binding.edtShowtimeId.text.toString()
        val movieName = binding.spnMovieName.selectedItem.toString()
        val date = binding.edtDate.text.toString()
        val time = binding.edtTime.text.toString()

        Log.d("ShowtimeUpdateActivity", "Showtime ID: $showtimeId")
        Log.d("ShowtimeUpdateActivity", "Selected Movie Name: $movieName")
        Log.d("ShowtimeUpdateActivity", "Date: $date")
        Log.d("ShowtimeUpdateActivity", "Time: $time")

        val movieId = movieDao.getMovieIdByName(movieName)
        Log.d("ShowtimeUpdateActivity", "Fetched Movie ID: $movieId")

        val sharedPreferences = this.getSharedPreferences("MyPreferences", Context.MODE_PRIVATE)
        val maNhanVien = sharedPreferences.getString("MANV", "") ?: ""

        val maRap = cinemaDao.getMaRapByEmployeeId(maNhanVien)

        Log.d("ShowtimeUpdateActivity", "Logged in Employee ID: $maNhanVien")
        Log.d("ShowtimeUpdateActivity", "Cinema ID: $maRap")

        if (validateFields()) {
            // Kiểm tra maRap và movieId có hợp lệ không trước khi tạo showtime
            if (maRap != null && movieId != null) {
                val showtime = Showtime(showtimeId, movieId, date, time, maRap)
                Log.d("ShowtimeUpdateActivity", "Updated Showtime Object: $showtime")

                val isSuccess = showtimeDao.updateShowtime(showtime)
                Log.d("ShowtimeUpdateActivity", "Update Showtime Success: $isSuccess")

                if (isSuccess) {
                    Toast.makeText(this, "Cập nhật suất chiếu thành công", Toast.LENGTH_SHORT).show()
                    finish()
                } else {
                    Toast.makeText(this, "Cập nhật suất chiếu thất bại", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "Thông tin không hợp lệ", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun validateFields(): Boolean {
        with(binding) {
            if (edtDate.text.isNullOrEmpty()) {
                edtDate.error = "Ngày không được để trống"
                edtDate.requestFocus()
                return false
            }
            if (edtTime.text.isNullOrEmpty()) {
                edtTime.error = "Thời gian không được để trống"
                edtTime.requestFocus()
                return false
            }
        }
        return true
    }
}
