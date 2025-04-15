package com.example.project_android_booking_movietickets.dao

import android.content.ContentValues
import android.content.Context
import com.example.project_android_booking_movietickets.DBHelper.DatabaseHelper
import com.example.project_android_booking_movietickets.model.SeatTicket

class SeatTicketDao(context: Context) {
    private val dbHelper = DatabaseHelper(context)

//    fun insertVeGhe(veGhe: SeatTicket) {
//        val db = dbHelper.writableDatabase
//        val values = ContentValues().apply {
//            put("MaGhe", veGhe.MaGhe)
//            put("MaVe", veGhe.MaVe)
//            put("DonGia", veGhe.DonGia)
//        }
//        db.insert("VeGhe", null, values)
//        db.close()
//    }

    fun insertSeatTicket(maVe: Long, maGhe: String, donGia: Int): Long {
        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put("MaGhe", maGhe)
            put("MaVe", maVe)
            put("DonGia", donGia)
        }
        return db.insert("VeGhe", null, values)
    }

    fun getAllSeatTickets(): List<Map<String, Any>> {
        val db = dbHelper.readableDatabase
        val ticketList = mutableListOf<Map<String, Any>>()

        val cursor = db.rawQuery("SELECT * FROM VeGhe", null)
        if (cursor.moveToFirst()) {
            do {
                val ticket = mutableMapOf<String, Any>()
                for (i in 0 until cursor.columnCount) {
                    ticket[cursor.getColumnName(i)] = cursor.getString(i) ?: ""
                }
                ticketList.add(ticket)
            } while (cursor.moveToNext())
        }
        cursor.close()
        return ticketList
    }
}
