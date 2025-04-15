package com.example.project_android_booking_movietickets.dao

import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import com.example.project_android_booking_movietickets.DBHelper.DatabaseHelper
import com.example.project_android_booking_movietickets.model.Seat
import com.example.project_android_booking_movietickets.model.SeatType

class SeatTypeDao(context: Context) {
    private val dbHelper = DatabaseHelper(context)

    // Lấy giá ghế
    fun getAllSeatType(): List<SeatType> {
        val seatTypeList = mutableListOf<SeatType>()
        val db: SQLiteDatabase = dbHelper.readableDatabase
        val query = """
            SELECT LoaiGhe.* 
            FROM LoaiGhe 
            JOIN GheNgoi ON GheNgoi.MaLoaiGhe = LoaiGhe.MaGhe
        """
        val cursor: Cursor = db.rawQuery(query, null)

        cursor.use {
            while (it.moveToNext()) {
                val seatType = SeatType(
                    MaLoaiGhe = it.getString(it.getColumnIndexOrThrow("MaLoaiGhe")),
                    TenLoaiGhe = it.getString(it.getColumnIndexOrThrow("TenLoaiGhe")),
                    GiaLoaiGhe = it.getInt(it.getColumnIndexOrThrow("GiaLoaiGhe")),
                )
                seatTypeList.add(seatType)
            }
        }
        db.close()
        return seatTypeList
    }

    fun getPriceSeat(maLoaiGhe: String): Int {
        var price = 0
        val db: SQLiteDatabase = dbHelper.readableDatabase
        val query = "SELECT GiaLoaiGhe FROM LoaiGhe WHERE MaLoaiGhe = ?"
        val cursor: Cursor = db.rawQuery(query, arrayOf(maLoaiGhe))

        cursor.use {
            if (it.moveToFirst()) {
                price = it.getInt(it.getColumnIndexOrThrow("GiaLoaiGhe"))
            }
        }
        db.close()
        return price
    }

    fun getTotalPrice(selectedSeats: List<Seat>): Int {
        var totalPrice = 0
        val db: SQLiteDatabase = dbHelper.readableDatabase

        for (seat in selectedSeats) {
            val query = "SELECT GiaLoaiGhe FROM LoaiGhe WHERE MaLoaiGhe = ?"
            val cursor: Cursor = db.rawQuery(query, arrayOf(seat.MaLoaiGhe))

            cursor.use {
                if (it.moveToFirst()) {
                    totalPrice += it.getInt(it.getColumnIndexOrThrow("GiaLoaiGhe"))
                }
            }
        }
        db.close()
        return totalPrice
    }

}