package com.example.project_android_booking_movietickets.dao

import android.content.ContentValues
import android.content.Context
import android.util.Log
import com.example.project_android_booking_movietickets.DBHelper.DatabaseHelper

class TicketDao(context: Context) {
    private val databaseHelper = DatabaseHelper(context)

    fun insertTicket(maSuatChieu: String, maTK: Int, thoiGianDat: String, tongTien: Int): Long {
        val db = databaseHelper.writableDatabase
        val values = ContentValues().apply {
            put("MaSuatChieu", maSuatChieu)
            put("MaTK", maTK)
            put("ThoiGianDat", thoiGianDat)
            put("TongTien", tongTien)
        }
        val result = db.insert("Ve", null, values)

        if (result == -1L) {
            Log.e("TicketDao", "Lỗi khi thêm vé vào database!")
        } else {
            Log.d("TicketDao", "Vé đã được thêm vào database với MaVe = $result")
        }

        return result
    }

    fun getAllTickets(): List<Map<String, Any>> {
        val db = databaseHelper.readableDatabase
        val ticketList = mutableListOf<Map<String, Any>>()

        val cursor = db.rawQuery("SELECT * FROM Ve", null)
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