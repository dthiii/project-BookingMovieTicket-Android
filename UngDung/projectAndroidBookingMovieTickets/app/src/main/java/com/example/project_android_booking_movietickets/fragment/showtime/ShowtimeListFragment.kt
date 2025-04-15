package com.example.project_android_booking_movietickets.fragment.showtime

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.project_android_booking_movietickets.DBHelper.DatabaseHelper
import com.example.project_android_booking_movietickets.R
import com.example.project_android_booking_movietickets.adapter.ShowtimeInfoAdapter
import com.example.project_android_booking_movietickets.dao.ShowtimeDao
import com.example.project_android_booking_movietickets.model.FoodItem
import com.example.project_android_booking_movietickets.model.Movie
import com.example.project_android_booking_movietickets.model.Showtime
import com.google.android.material.floatingactionbutton.FloatingActionButton

class ShowtimeListFragment : Fragment() {
    private lateinit var showtimeList: ArrayList<Map<String, String>>  // Giữ Map
    private lateinit var showtimeAdapter: ShowtimeInfoAdapter
    private lateinit var databaseHelper: DatabaseHelper
    private lateinit var showtimeDao: ShowtimeDao
    private lateinit var rcvList: RecyclerView
    private lateinit var btnAdd: FloatingActionButton

    companion object {
        private const val REQUEST_CODE_SHOWTIME_DETAIL = 1
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_list, container, false)

        rcvList = view.findViewById(R.id.rcvList)
        btnAdd = view.findViewById(R.id.btnAdd)

        rcvList.layoutManager = LinearLayoutManager(context)
        rcvList.setHasFixedSize(true)

        databaseHelper = DatabaseHelper(requireContext())
        showtimeDao = ShowtimeDao(requireContext())

        val sharedPreferences = requireActivity().getSharedPreferences("MyPreferences", Context.MODE_PRIVATE)
        val maNhanVien = sharedPreferences.getString("MANV", "") ?: ""

        val rawShowtimes = showtimeDao.getShowtimesForEmployeeCinema(maNhanVien)
        showtimeList = ArrayList()

        // Chuyển đổi từ Showtime thành Map<String, String>
        for (raw in rawShowtimes) {
            val showtimeMap = mapOf(
                "MaSuatChieu" to (raw["MaSuatChieu"] ?: ""),
                "TenPhim" to (raw["TenPhim"] ?: ""),
                "NgayChieu" to (raw["NgayChieu"] ?: ""),
                "GioChieu" to (raw["GioChieu"] ?: ""),
                "ThoiLuong" to (raw["ThoiLuong"]?.toString() ?: ""),
                "MaRap" to (raw["MaRap"] ?: "")
            )
            showtimeList.add(showtimeMap)
        }

        showtimeAdapter = ShowtimeInfoAdapter(
            requireContext(),
            showtimeList,
            { showtime ->
                val intent = Intent(requireContext(), ShowtimeUpdateActivity::class.java)
                intent.putExtra("SHOWTIME_ID", showtime["MaSuatChieu"])  // Truyền MaSuatChieu
                intent.putExtra("MOVIE_NAME", showtime["TenPhim"])
                startActivity(intent)
            },
            { showtime ->
                showDeleteConfirmationDialog(showtime)
            }
        )

        rcvList.adapter = showtimeAdapter

        btnAdd.setOnClickListener {
            val intent = Intent(activity, ShowtimeAddActivity::class.java)
            startActivity(intent)
        }

        return view
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE_SHOWTIME_DETAIL && resultCode == Activity.RESULT_OK) {
            loadShowtimeList()
        }
    }

    override fun onResume() {
        super.onResume()
        loadShowtimeList()
    }

    private fun loadShowtimeList() {
        val sharedPreferences = requireActivity().getSharedPreferences("MyPreferences", Context.MODE_PRIVATE)
        val maNhanVien = sharedPreferences.getString("MANV", "") ?: ""
        val rawShowtimes = showtimeDao.getShowtimesForEmployeeCinema(maNhanVien)

        showtimeList.clear()

        // Chuyển đổi từ Showtime thành Map<String, String>
        for (raw in rawShowtimes) {
            val showtimeMap = mapOf(
                "MaSuatChieu" to (raw["MaSuatChieu"] ?: ""),
                "TenPhim" to (raw["TenPhim"] ?: ""),
                "NgayChieu" to (raw["NgayChieu"] ?: ""),
                "GioChieu" to (raw["GioChieu"] ?: ""),
                "ThoiLuong" to (raw["ThoiLuong"]?.toString() ?: ""),
                "MaRap" to (raw["MaRap"] ?: "")
            )
            showtimeList.add(showtimeMap)
        }

        showtimeAdapter.notifyDataSetChanged()
    }


    private fun showDeleteConfirmationDialog(showtime: Map<String, String>) {
        val builder = AlertDialog.Builder(requireContext())
        builder.setMessage("Bạn có chắc chắn muốn xóa suất chiếu này không?")
            .setCancelable(false)
            .setPositiveButton("Có") { dialog, id ->
                deleteShowtime(showtime)
            }
            .setNegativeButton("Không") { dialog, id ->
                dialog.dismiss()
            }

        val alert = builder.create()
        alert.show()
    }

    private fun deleteShowtime(showtime: Map<String, String>) {
        val maSuatChieu = showtime["MaSuatChieu"] ?: ""
        showtimeDao.deleteShowtime(maSuatChieu)

        loadShowtimeList()
    }
}
