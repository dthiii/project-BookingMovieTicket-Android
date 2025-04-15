package com.example.project_android_booking_movietickets.model

data class FoodItem(
    val MaDoAn: String,
    val TenDoAn: String,
    val MoTa: String,
    val AnhMinhHoa: String,
    val GiaBan: Int,
    var selectedQuantity: Int = 0

)

