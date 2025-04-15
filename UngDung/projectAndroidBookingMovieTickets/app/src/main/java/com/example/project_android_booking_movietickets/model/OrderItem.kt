package com.example.project_android_booking_movietickets.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class OrderItem(
    val ten: String,
    var soLuong: Int,
    var donGia: Int,
    var thanhTien: Int = soLuong * donGia,
) : Parcelable
