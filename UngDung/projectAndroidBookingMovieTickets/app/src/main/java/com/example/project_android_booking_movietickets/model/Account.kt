package com.example.project_android_booking_movietickets.model

import java.io.Serializable

data class Account(
    val MaTK: Int,
    val TenTK: String,
    var MatKhau: String,
    val LoaiNguoiDung: String,
    val MaNhanVien: String?
) : Serializable