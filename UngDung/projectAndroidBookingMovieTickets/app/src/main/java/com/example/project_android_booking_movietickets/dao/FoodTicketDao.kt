package com.example.project_android_booking_movietickets.dao

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import com.example.project_android_booking_movietickets.DBHelper.DatabaseHelper

class FoodTicketDao(context: Context) {
    private val database: SQLiteDatabase = DatabaseHelper(context).readableDatabase


    fun addOrUpdateFoodTicket(maVe: Int, maDoAn: String, soLuong: Int, donGia: Int) {
        val db = database

        // Kiểm tra xem món ăn đã tồn tại trong vé chưa
        val cursor = db.rawQuery(
            "SELECT SoLuong FROM VeAn WHERE MaVe = ? AND MaDoAn = ?",
            arrayOf(maVe.toString(), maDoAn)
        )

        if (cursor.moveToFirst()) {
            val currentQuantity = cursor.getInt(0)
            val newQuantity = currentQuantity + soLuong

            if (newQuantity > 0) {
                // Cập nhật số lượng nếu lớn hơn 0
                val values = ContentValues().apply {
                    put("SoLuong", newQuantity)
                    put("DonGia", donGia)
                }
                db.update("VeAn", values, "MaVe = ? AND MaDoAn = ?", arrayOf(maVe.toString(), maDoAn))
            } else {
                // Xóa nếu số lượng = 0
                db.delete("VeAn", "MaVe = ? AND MaDoAn = ?", arrayOf(maVe.toString(), maDoAn))
            }
        } else {
            // Thêm mới nếu chưa tồn tại
            val values = ContentValues().apply {
                put("MaVe", maVe)
                put("MaDoAn", maDoAn)
                put("SoLuong", soLuong)
                put("DonGia", donGia)
            }
            db.insert("VeAn", null, values)
        }

        cursor.close()
        db.close()
    }

    fun insertFoodTicket(maVe: Long, maDoAn: String, soLuong: Int, donGia: Int): Long {
        val db = database
        val values = ContentValues().apply {
            put("MaVe", maVe)
            put("MaDoAn", maDoAn)
            put("SoLuong", soLuong)
            put("DonGia", donGia)
        }
        return db.insert("VeAn", null, values)
    }
}