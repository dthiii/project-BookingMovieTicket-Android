package com.example.project_android_booking_movietickets.model

data class Cinema(
    val MaRap: String,
    val TenRap: String,
    val DiaChi: String,
    val TinhThanh: String,
    val SdtRap: String,
    var isExpanded: Boolean = false
)