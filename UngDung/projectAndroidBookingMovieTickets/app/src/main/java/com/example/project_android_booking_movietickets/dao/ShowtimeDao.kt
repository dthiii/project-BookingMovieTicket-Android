package com.example.project_android_booking_movietickets.dao

import android.content.Context
import android.database.Cursor
import com.example.project_android_booking_movietickets.DBHelper.DatabaseHelper
import com.example.project_android_booking_movietickets.model.FoodItem
import com.example.project_android_booking_movietickets.model.Showtime

class ShowtimeDao(context: Context) {
    private val databaseHelper = DatabaseHelper(context)

    //Lấy suất chiếu theo Mã phim và mã rạp và ngayChieu
    fun getShowtimesByMovieAndCinema(maPhim: String, maRap: String, ngayChieu: String): List<Showtime> {
        val showtimes = mutableListOf<Showtime>()
        val db = databaseHelper.openDatabase() ?: return showtimes

        val query = """
            SELECT MaSuatChieu, MaPhim, NgayChieu, GioChieu, MaRap 
            FROM SuatChieu 
            WHERE MaPhim = ? AND MaRap = ? AND NgayChieu = ?
        """
        val cursor: Cursor = db.rawQuery(query, arrayOf(maPhim, maRap, ngayChieu))

        cursor.use {
            while (it.moveToNext()) {
                val maSuatChieu = it.getString(0)
                val ngayChieu = it.getString(2)
                val gioChieu = it.getString(3)

                showtimes.add(Showtime(maSuatChieu, maPhim, ngayChieu, gioChieu, maRap))
            }
        }
        db.close()
        return showtimes
    }

    fun getShowtimesForEmployeeCinema(maNhanVien: String): List<Map<String, String>> {
        val results = mutableListOf<Map<String, String>>()
        val db = databaseHelper.openDatabase() ?: return results

        val query = """
    SELECT sc.MaSuatChieu, p.TenPhim, sc.NgayChieu, sc.GioChieu, p.ThoiLuong, sc.MaRap
    FROM SuatChieu sc
    JOIN Phim p ON sc.MaPhim = p.MaPhim
    JOIN NhanVien nv ON sc.MaRap = nv.MaRap
    WHERE nv.MaNV = ?
    """

        val cursor = db.rawQuery(query, arrayOf(maNhanVien))

        cursor.use {
            while (it.moveToNext()) {
                val maSuatChieu = it.getString(0)
                val tenPhim = it.getString(1)
                val ngayChieu = it.getString(2)
                val gioChieu = it.getString(3)
                val thoiLuong = it.getInt(4)
                val maRap = it.getString(5)

                results.add(
                    mapOf(
                        "MaSuatChieu" to maSuatChieu,
                        "TenPhim" to tenPhim,
                        "NgayChieu" to ngayChieu,
                        "GioChieu" to gioChieu,
                        "ThoiLuong" to thoiLuong.toString(),
                        "MaRap" to maRap
                    )
                )
            }
        }
        db.close()
        return results
    }


    fun deleteShowtime(maSuatChieu: String) {
        val db = databaseHelper.openDatabase()
        db?.delete("SuatChieu", "MaSuatChieu = ?", arrayOf(maSuatChieu))
        db?.close()
    }

    fun generateNewId(): String {
        val db = databaseHelper.readableDatabase
        var newId = "SC0001"
        val cursor = db.rawQuery("SELECT MAX(MaSuatChieu) FROM SuatChieu", null)
        cursor.use {
            if (it.moveToFirst()) {
                val maxCode = it.getString(0)
                if (!maxCode.isNullOrEmpty()) {
                    val currentNumber = maxCode.substring(2).toInt()
                    val newNumber = currentNumber + 1
                    newId = "SC" + String.format("%04d", newNumber)
                }
            }
        }
        return newId
    }

    fun insertShowtime(showtime: Showtime): Boolean {
        val db = databaseHelper.writableDatabase
        val query = """
        INSERT INTO SuatChieu (MaSuatChieu, MaPhim, NgayChieu, GioChieu, MaRap) 
        VALUES (?, ?, ?, ?, ?)
    """
        return try {
            db.execSQL(
                query,
                arrayOf(
                    showtime.MaSuatChieu,
                    showtime.MaPhim,
                    showtime.NgayChieu,
                    showtime.GioChieu,
                    showtime.MaRap
                )
            )
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        } finally {
            db.close()
        }
    }

    // Phương thức lấy suất chiếu theo ID
    fun getShowtimeById(maSuatChieu: String): Showtime? {
        val db = databaseHelper.readableDatabase
        val cursor = db.rawQuery("SELECT * FROM SuatChieu WHERE MaSuatChieu = ?", arrayOf(maSuatChieu))
        var showtime: Showtime? = null

        if (cursor.moveToFirst()) {
            // Lấy các giá trị từ cursor
            val maSuatChieu = cursor.getString(cursor.getColumnIndexOrThrow("MaSuatChieu"))
            val maPhim = cursor.getString(cursor.getColumnIndexOrThrow("MaPhim"))
            val ngayChieu = cursor.getString(cursor.getColumnIndexOrThrow("NgayChieu"))
            val gioChieu = cursor.getString(cursor.getColumnIndexOrThrow("GioChieu"))
            val maRap = cursor.getString(cursor.getColumnIndexOrThrow("MaRap"))

            // Tạo đối tượng FoodItem và gán dữ liệu cho nó
            showtime = Showtime(
                MaSuatChieu = maSuatChieu,
                MaPhim = maPhim,
                NgayChieu = ngayChieu,
                GioChieu = gioChieu,
                MaRap = maRap
            )
        }
        cursor.close()
        return showtime
    }

    fun updateShowtime(showtime: Showtime): Boolean {
        val db = databaseHelper.writableDatabase

        // Cập nhật thông tin suất chiếu
        val query = """
        UPDATE SuatChieu
        SET MaPhim = ?, NgayChieu = ?, GioChieu = ?, MaRap = ?
        WHERE MaSuatChieu = ?
    """

        // Thực hiện câu lệnh UPDATE
        return try {
            db.execSQL(query, arrayOf(
                showtime.MaPhim,
                showtime.NgayChieu,
                showtime.GioChieu,
                showtime.MaRap,
                showtime.MaSuatChieu
            ))
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        } finally {
            db.close()
        }
    }


    fun getShowtimesByMovieName(tenPhim: String): List<Showtime> {
        val showtimes = mutableListOf<Showtime>()
        val db = databaseHelper.openDatabase() ?: return showtimes

        val query = """
        SELECT sc.MaSuatChieu, sc.MaPhim, sc.NgayChieu, sc.GioChieu, sc.MaRap
        FROM SuatChieu sc
        INNER JOIN Phim p ON sc.MaPhim = p.MaPhim
        WHERE p.TenPhim LIKE ?
    """
        val cursor: Cursor = db.rawQuery(query, arrayOf("%$tenPhim%"))

        cursor.use {
            while (it.moveToNext()) {
                val maSuatChieu = it.getString(0)
                val maPhim = it.getString(1)
                val ngayChieu = it.getString(2)
                val gioChieu = it.getString(3)
                val maRap = it.getString(4)

                showtimes.add(Showtime(maSuatChieu, maPhim, ngayChieu, gioChieu, maRap))
            }
        }
        db.close()
        return showtimes
    }

}
