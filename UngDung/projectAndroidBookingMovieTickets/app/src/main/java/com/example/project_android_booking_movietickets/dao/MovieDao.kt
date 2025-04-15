package com.example.project_android_booking_movietickets.dao

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.util.Log
import com.example.project_android_booking_movietickets.DBHelper.DatabaseHelper
import com.example.project_android_booking_movietickets.model.Cinema
import com.example.project_android_booking_movietickets.model.Movie

class MovieDao(context: Context) {
    private val database: SQLiteDatabase = DatabaseHelper(context).writableDatabase

    //Lấy toàn bộ danh sách phim
    fun getAllMovies(): List<Movie> {
        val movieList = mutableListOf<Movie>()
        val cursor = database.rawQuery("SELECT * FROM Phim", null)

        if (cursor.moveToFirst()) {
            do {
                val id = cursor.getString(cursor.getColumnIndexOrThrow("MaPhim"))
                val title = cursor.getString(cursor.getColumnIndexOrThrow("TenPhim"))
                val genre = cursor.getString(cursor.getColumnIndexOrThrow("TheLoai"))
                val duration = cursor.getInt(cursor.getColumnIndexOrThrow("ThoiLuong"))
                val description = cursor.getString(cursor.getColumnIndexOrThrow("MoTa"))
                val posterUrl = cursor.getString(cursor.getColumnIndexOrThrow("Poster"))
                val releaseDate = cursor.getString(cursor.getColumnIndexOrThrow("NgayKhoiChieu"))
                val category = cursor.getString(cursor.getColumnIndexOrThrow("PhanLoai"))
                val language = cursor.getString(cursor.getColumnIndexOrThrow("NgonNgu"))
                val director = cursor.getString(cursor.getColumnIndexOrThrow("DaoDien"))
                val actors = cursor.getString(cursor.getColumnIndexOrThrow("DienVien"))

                movieList.add(Movie(id, title, genre, duration, description, posterUrl, releaseDate, category, language, director, actors))
            } while (cursor.moveToNext())
        }
        cursor.close()
        return movieList
    }


    //Tìm phim theo mã phim
    fun getMovieById(maPhim: String): Movie? {
        val cursor = database.rawQuery("SELECT * FROM Phim WHERE MaPhim = ?", arrayOf(maPhim))
        var movie: Movie? = null

        if (cursor.moveToFirst()) {
            val title = cursor.getString(cursor.getColumnIndexOrThrow("TenPhim"))
            val genre = cursor.getString(cursor.getColumnIndexOrThrow("TheLoai"))
            val duration = cursor.getInt(cursor.getColumnIndexOrThrow("ThoiLuong"))
            val description = cursor.getString(cursor.getColumnIndexOrThrow("MoTa"))
            val posterUrl = cursor.getString(cursor.getColumnIndexOrThrow("Poster"))
            val releaseDate = cursor.getString(cursor.getColumnIndexOrThrow("NgayKhoiChieu"))
            val category = cursor.getString(cursor.getColumnIndexOrThrow("PhanLoai"))
            val language = cursor.getString(cursor.getColumnIndexOrThrow("NgonNgu"))
            val director = cursor.getString(cursor.getColumnIndexOrThrow("DaoDien"))
            val actors = cursor.getString(cursor.getColumnIndexOrThrow("DienVien"))

            movie = Movie(maPhim, title, genre, duration, description, posterUrl, releaseDate, category, language, director, actors)
        }
        cursor.close()
        return movie
    }

    // Trả về mã phim (MaPhim) theo tên phim
    fun getMovieIdByName(tenPhim: String): String? {
        val cursor = database.rawQuery("SELECT MaPhim FROM Phim WHERE TenPhim = ?", arrayOf(tenPhim))
        var maPhim: String? = null

        if (cursor.moveToFirst()) {
            maPhim = cursor.getString(cursor.getColumnIndexOrThrow("MaPhim"))
        }

        cursor.close()
        return maPhim
    }


    //Tìm phim theo mã suất chiếu
    fun getMovieByShowtime(maSuatChieu: String): Movie? {
        val cursor = database.rawQuery("SELECT Phim.MaPhim, TenPhim, TheLoai, ThoiLuong, MoTa, Poster, NgayKhoiChieu, PhanLoai, NgonNgu, DaoDien, DienVien " +
                "FROM Phim JOIN SuatChieu ON Phim.MaPhim = SuatChieu.MaPhim " +
                "WHERE SuatChieu.MaSuatChieu = ?", arrayOf(maSuatChieu))
        var movie: Movie? = null

        cursor.use {
            if (it.moveToFirst()) {
                val maPhim = it.getString(0)
                val tenPhim = it.getString(1)
                val theLoai = it.getString(2)
                val thoiLuong = it.getInt(3)
                val moTa = it.getString(4)
                val poster = it.getString(5)
                val ngayKhoiChieu = it.getString(6)
                val phanLoai = it.getString(7)
                val ngonNgu = it.getString(8)
                val daoDien = it.getString(9)
                val dienVien = it.getString(10)

                movie = Movie(maPhim, tenPhim, theLoai, thoiLuong, moTa, poster, ngayKhoiChieu, phanLoai, ngonNgu, daoDien, dienVien)
            }
        }
        cursor.close()
        return movie
    }

    fun insertMovie(movie: Movie): Long {
        val values = ContentValues().apply {
            put("MaPhim", movie.MaPhim)
            put("TenPhim", movie.TenPhim)
            put("TheLoai", movie.TheLoai)
            put("ThoiLuong", movie.ThoiLuong)
            put("MoTa", movie.MoTa)
            put("NgayKhoiChieu", movie.NgayKhoiChieu)
            put("PhanLoai", movie.PhanLoai)
            put("NgonNgu", movie.NgonNgu)
            put("DaoDien", movie.DaoDien)
            put("DienVien", movie.DienVien)
            put("Poster", movie.Poster)
        }
        return database.insert("Phim", null, values)
    }

    fun generateNewId(): String {
        var newId = "P0001"
        val cursor = database.rawQuery("SELECT MAX(MaPhim) FROM Phim", null)
        cursor.use {
            if (it.moveToFirst()) {
                val maxCode = it.getString(0)
                if (!maxCode.isNullOrEmpty()) {
                    val currentNumber = maxCode.substring(1).toInt()
                    val newNumber = currentNumber + 1
                    newId = "P" + String.format("%04d", newNumber)
                }
            }
        }
        return newId
    }

    // Tìm phim theo tên hoặc thể loại
    fun searchMovie(query: String): List<Movie> {
        val movieList = mutableListOf<Movie>()
        val searchQuery = "%$query%"  // Thêm dấu % để tìm kiếm theo một phần của chuỗi
        val cursor = database.rawQuery(
            "SELECT * FROM Phim WHERE TenPhim LIKE ? OR TheLoai LIKE ?",
            arrayOf(searchQuery, searchQuery)
        )

        if (cursor.moveToFirst()) {
            do {
                val maPhim = cursor.getString(cursor.getColumnIndexOrThrow("MaPhim"))
                val tenPhim = cursor.getString(cursor.getColumnIndexOrThrow("TenPhim"))
                val theLoai = cursor.getString(cursor.getColumnIndexOrThrow("TheLoai"))
                val thoiLuong = cursor.getInt(cursor.getColumnIndexOrThrow("ThoiLuong"))
                val moTa = cursor.getString(cursor.getColumnIndexOrThrow("MoTa"))
                val poster = cursor.getString(cursor.getColumnIndexOrThrow("Poster"))
                val ngayKhoiChieu = cursor.getString(cursor.getColumnIndexOrThrow("NgayKhoiChieu"))
                val phanLoai = cursor.getString(cursor.getColumnIndexOrThrow("PhanLoai"))
                val ngonNgu = cursor.getString(cursor.getColumnIndexOrThrow("NgonNgu"))
                val daoDien = cursor.getString(cursor.getColumnIndexOrThrow("DaoDien"))
                val dienVien = cursor.getString(cursor.getColumnIndexOrThrow("DienVien"))

                movieList.add(Movie(maPhim, tenPhim, theLoai, thoiLuong, moTa, poster, ngayKhoiChieu, phanLoai, ngonNgu, daoDien, dienVien))
            } while (cursor.moveToNext())
        }
        cursor.close()
        return movieList
    }

    fun update(movie: Movie): Int {
        val contentValues = ContentValues().apply {
            put("MaPhim", movie.MaPhim)
            put("TenPhim", movie.TenPhim)
            put("TheLoai", movie.TheLoai)
            put("ThoiLuong", movie.ThoiLuong)
            put("MoTa", movie.MoTa)
            put("NgayKhoiChieu", movie.NgayKhoiChieu)
            put("PhanLoai", movie.PhanLoai)
            put("NgonNgu", movie.NgonNgu)
            put("DaoDien", movie.DaoDien)
            put("DienVien", movie.DienVien)
            put("Poster", movie.Poster)
        }

        return database.update("Phim", contentValues, "MaPhim = ?", arrayOf(movie.MaPhim))
        database.close()
    }

    fun getMovie(): List<String> {
        val movieNames = mutableListOf<String>()
        val cursor = database.rawQuery("SELECT MaPhim, TenPhim FROM Phim", null)
        if (cursor.moveToFirst()) {
            do {
                movieNames.add(cursor.getString(0))
            } while (cursor.moveToNext())
        }
        cursor.close()
        return movieNames
    }

    fun getAllMovieNames(): List<String> {
        val movieNames = mutableListOf<String>()
        val cursor = database.rawQuery("SELECT TenPhim FROM Phim", null)
        if (cursor.moveToFirst()) {
            do {
                movieNames.add(cursor.getString(0))
            } while (cursor.moveToNext())
        }
        cursor.close()
        return movieNames
    }
}
