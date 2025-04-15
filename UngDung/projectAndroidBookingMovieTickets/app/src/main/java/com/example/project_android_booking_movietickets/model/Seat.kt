package com.example.project_android_booking_movietickets.model

data class Seat(
    val MaGhe: String,
    val HangGhe: String,
    val CotGhe: String,
    val MaLoaiGhe: String,
    var isBooked: Boolean = false,
    var isSelected: Boolean = false
)

