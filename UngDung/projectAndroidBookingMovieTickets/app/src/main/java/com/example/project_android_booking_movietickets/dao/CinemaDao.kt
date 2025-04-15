package com.example.project_android_booking_movietickets.dao

import android.content.Context
import android.database.Cursor
import com.example.project_android_booking_movietickets.DBHelper.DatabaseHelper
import com.example.project_android_booking_movietickets.model.Cinema
import com.example.project_android_booking_movietickets.model.Showtime
import com.example.project_android_booking_movietickets.model.ShowtimeDetails

class CinemaDao(context: Context) {

    private val databaseHelper = DatabaseHelper(context)

    // Lấy toàn bộ danh sách rạp
    fun getAllCinemas(): List<Cinema> {
        val cinemaList = mutableListOf<Cinema>()
        val db = databaseHelper.openDatabase() ?: return cinemaList

        val cursor: Cursor? = db.rawQuery("SELECT MaRap, TenRap, DiaChi, TinhThanh, SdtRap FROM Rap", null)
        cursor?.use {
            while (it.moveToNext()) {
                val maRap = it.getString(0)
                val tenRap = it.getString(1)
                val diaChi = it.getString(2)
                val tinhThanh = it.getString(3)
                val sdtRap = it.getString(4)
                cinemaList.add(Cinema(maRap, tenRap, diaChi, tinhThanh, sdtRap))
            }
        }
        db.close()
        return cinemaList
    }

    //Lấy danh sách các rạp theo Mã phim, địa điểm và ngày
    fun getCinemasByLocationAndDate(maPhim: String, location: String, date: String): List<Cinema> {
        val cinemas = mutableListOf<Cinema>()
        val db = databaseHelper.readableDatabase

        val query = """
            SELECT DISTINCT c.*
            FROM Rap c
            JOIN SuatChieu s ON c.MaRap = s.MaRap
            WHERE s.MaPhim = ? AND c.TinhThanh = ? AND s.NgayChieu = ?
        """
        val cursor = db.rawQuery(query, arrayOf(maPhim, location, date))

        if (cursor.moveToFirst()) {
            do {
                val maRap = cursor.getString(cursor.getColumnIndexOrThrow("MaRap"))
                val tenRap = cursor.getString(cursor.getColumnIndexOrThrow("TenRap"))
                val diaChi = cursor.getString(cursor.getColumnIndexOrThrow("DiaChi"))
                val tinhThanh = cursor.getString(cursor.getColumnIndexOrThrow("TinhThanh"))
                val sdtRap = cursor.getString(cursor.getColumnIndexOrThrow("SdtRap"))

                cinemas.add(Cinema(maRap, tenRap, diaChi, tinhThanh, sdtRap))
            } while (cursor.moveToNext())
        }
        cursor.close()
        db.close()
        return cinemas
    }

    fun getShowtimeById(maSuatChieu: String): ShowtimeDetails? {
        val db = databaseHelper.readableDatabase
        val query = """
        SELECT Phim.TenPhim, SuatChieu.NgayChieu, SuatChieu.GioChieu, Phim.ThoiLuong, Rap.TenRap
        FROM SuatChieu
        JOIN Phim ON SuatChieu.MaPhim = Phim.MaPhim
        JOIN Rap ON SuatChieu.MaRap = Rap.MaRap
        WHERE SuatChieu.MaSuatChieu = ?
    """
        val cursor = db.rawQuery(query, arrayOf(maSuatChieu))

        return if (cursor.moveToFirst()) {
            val tenPhim = cursor.getString(0)
            val ngayChieu = cursor.getString(1)
            val gioChieu = cursor.getString(2)
            val thoiLuong = cursor.getInt(3)
            val tenRap = cursor.getString(4)

            ShowtimeDetails(tenPhim, ngayChieu, gioChieu, thoiLuong, tenRap)
        } else {
            null
        }.also {
            cursor.close()
            db.close()
        }
    }

    fun getMaRapByEmployeeId(maNhanVien: String): String? {
        val db = databaseHelper.readableDatabase
        val query = """
        SELECT MaRap 
        FROM NhanVien
        WHERE MaNV = ?
    """
        val cursor = db.rawQuery(query, arrayOf(maNhanVien))

        return if (cursor.moveToFirst()) {
            cursor.getString(0)
        } else {
            null
        }.also {
            cursor.close()
            db.close()
        }
    }
}
