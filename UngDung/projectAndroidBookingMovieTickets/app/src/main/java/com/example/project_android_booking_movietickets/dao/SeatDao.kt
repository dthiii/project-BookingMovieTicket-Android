package com.example.project_android_booking_movietickets.dao

import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.util.Log
import com.example.project_android_booking_movietickets.DBHelper.DatabaseHelper
import com.example.project_android_booking_movietickets.model.Seat

class SeatDao(context: Context) {
    private val dbHelper = DatabaseHelper(context)

    // Lấy toàn bộ danh sách ghế (không lọc theo suất chiếu)
    fun getAllSeats(): List<Seat> {
        val seatList = mutableListOf<Seat>()
        val db: SQLiteDatabase = dbHelper.readableDatabase
        val query = """
            SELECT GheNgoi.*, 
       CASE WHEN MAX(VeGhe.MaGhe) IS NOT NULL THEN 1 ELSE 0 END AS isBooked
FROM GheNgoi 
LEFT JOIN VeGhe ON GheNgoi.MaGhe = VeGhe.MaGhe
GROUP BY GheNgoi.MaGhe

        """
        val cursor: Cursor = db.rawQuery(query, null)

        cursor.use {
            while (it.moveToNext()) {
                val seat = Seat(
                    MaGhe = it.getString(it.getColumnIndexOrThrow("MaGhe")),
                    HangGhe = it.getString(it.getColumnIndexOrThrow("HangGhe")),
                    CotGhe = it.getString(it.getColumnIndexOrThrow("CotGhe")),
                    MaLoaiGhe = it.getString(it.getColumnIndexOrThrow("MaLoaiGhe")),
                    isBooked = it.getInt(it.getColumnIndexOrThrow("isBooked")) == 1,
                    isSelected = false
                )
                seatList.add(seat)
            }
        }
        db.close()
        return seatList
    }

    fun getPriceBySeatType(maLoaiGhe: String): Int {
        var price = 0
        val db: SQLiteDatabase = dbHelper.readableDatabase

        val query = "SELECT GiaLoaiGhe FROM LoaiGhe WHERE MaLoaiGhe = ?"
        val cursor: Cursor = db.rawQuery(query, arrayOf(maLoaiGhe))

        if (cursor.moveToFirst()) {
            price = cursor.getInt(0) // Lấy giá trị cột đầu tiên (GiaLoaiGhe)
        }

        cursor.close() // Đóng Cursor để tránh rò rỉ bộ nhớ
        db.close()     // Đóng database

        return price
    }


    fun getSeatsByShowtime(maSuatChieu: String): List<Seat> {
        val seatList = mutableListOf<Seat>()
        val db: SQLiteDatabase = dbHelper.readableDatabase
        val query = """
       SELECT DISTINCT GheNgoi.*, 
       CASE WHEN VeGhe.MaGhe IS NOT NULL THEN 1 ELSE 0 END AS isBooked
FROM GheNgoi
LEFT JOIN VeGhe ON GheNgoi.MaGhe = VeGhe.MaGhe
LEFT JOIN Ve ON Ve.MaVe = VeGhe.MaVe
WHERE Ve.MaSuatChieu = ? OR Ve.MaSuatChieu IS NULL

    """
        val cursor: Cursor = db.rawQuery(query, arrayOf(maSuatChieu))

        Log.d("DatabaseQuery", "Number of seats fetched: ${cursor.count}")

        cursor.use {
            while (it.moveToNext()) {
                val seat = Seat(
                    MaGhe = it.getString(it.getColumnIndexOrThrow("MaGhe")),
                    HangGhe = it.getString(it.getColumnIndexOrThrow("HangGhe")),
                    CotGhe = it.getString(it.getColumnIndexOrThrow("CotGhe")),
                    MaLoaiGhe = it.getString(it.getColumnIndexOrThrow("MaLoaiGhe")),
                    isBooked = it.getInt(it.getColumnIndexOrThrow("isBooked")) == 1,
                    isSelected = false
                )
                seatList.add(seat)
            }
        }
        Log.d("DatabaseQuery", "Fetched seats: $seatList")
        return seatList
    }
}