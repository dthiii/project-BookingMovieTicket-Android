package com.example.project_android_booking_movietickets.model

data class Showtime (
    val MaSuatChieu: String,
    val MaPhim: String,
    val NgayChieu: String,
    val GioChieu: String,
    val MaRap: String,
    var isExpanded: Boolean = false
)