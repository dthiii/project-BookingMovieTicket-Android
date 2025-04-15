package com.example.project_android_booking_movietickets.dao

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import com.example.project_android_booking_movietickets.DBHelper.DatabaseHelper
import com.example.project_android_booking_movietickets.model.Customer
import com.example.project_android_booking_movietickets.model.FoodItem

class CustomerDao(context: Context) {

    private val databaseHelper = DatabaseHelper(context)

    fun findCustomersByName(name: String): List<Customer> {
        val db = databaseHelper.openDatabase()
        val query = "SELECT * FROM KhachHang WHERE HoKH || ' ' || TenKH LIKE ?"
        val cursor = db?.rawQuery(query, arrayOf("%$name%")) ?: return emptyList()

        val customers = mutableListOf<Customer>()
        cursor.use {
            while (it.moveToNext()) {
                customers.add(
                    Customer(
                        it.getString(it.getColumnIndexOrThrow("MaKH")),
                        it.getString(it.getColumnIndexOrThrow("HoKH")),
                        it.getString(it.getColumnIndexOrThrow("TenKH")),
                        it.getString(it.getColumnIndexOrThrow("SdtKH")) ?: "",
                        it.getString(it.getColumnIndexOrThrow("EmailKH")) ?: "",
                        it.getString(it.getColumnIndexOrThrow("GioiTinhKH")) ?: "",
                        it.getString(it.getColumnIndexOrThrow("NgaySinhKH")) ?: "",
                        it.getString(it.getColumnIndexOrThrow("DiaChiKH")) ?: "",
                    )
                )
            }
        }
        return customers
    }

    fun getAllCustomers(): List<Customer> {
        val customerList = mutableListOf<Customer>()
        val db = databaseHelper.readableDatabase
        val query = "SELECT * FROM KhachHang"  // Tên bảng là KhachHang

        val cursor = db.rawQuery(query, null)
        if (cursor.moveToFirst()) {
            do {
                val customer = Customer(

                    MaKH = cursor.getString(cursor.getColumnIndexOrThrow("MaKH")),
                    HoKH = cursor.getString(cursor.getColumnIndexOrThrow("HoKH")),
                    TenKH = cursor.getString(cursor.getColumnIndexOrThrow("TenKH")),
                    SdtKH = cursor.getString(cursor.getColumnIndexOrThrow("SdtKH")) ?: "",
                    EmailKH = cursor.getString(cursor.getColumnIndexOrThrow("EmailKH")) ?: "",
                    GioiTinhKH = cursor.getString(cursor.getColumnIndexOrThrow("GioiTinhKH")) ?: "",
                    NgaySinhKH = cursor.getString(cursor.getColumnIndexOrThrow("NgaySinhKH")) ?: "",
                    DiaChiKH = cursor.getString(cursor.getColumnIndexOrThrow("DiaChiKH")) ?: ""
                )
                customerList.add(customer)
            } while (cursor.moveToNext())
        }
        cursor.close()
        db.close()
        return customerList
    }

    fun addCustomer(customer: Customer): Boolean {
        val db = databaseHelper.readableDatabase
        val values = ContentValues().apply {
            put("MaKH", customer.MaKH)
            put("HoKH", customer.HoKH)
            put("TenKH", customer.TenKH)
            put("GioiTinhKH", customer.GioiTinhKH)
            put("SdtKH", customer.SdtKH)
            put("EmailKH", customer.EmailKH)
            put("NgaySinhKH", customer.NgaySinhKH)
            put("DiaChiKH", customer.DiaChiKH)
        }

        val result = db.insert("KhachHang", null, values)
        db.close()
        return result != -1L
    }

    fun generateNewId(): String {
        val db = databaseHelper.readableDatabase
        var newId = "KH01"
        val cursor = db.rawQuery("SELECT MAX(MaKH) FROM KhachHang", null)
        cursor.use {
            if (it.moveToFirst()) {
                val maxCode = it.getString(0)
                if (!maxCode.isNullOrEmpty()) {
                    val currentNumber = maxCode.substring(2).toInt()
                    val newNumber = currentNumber + 1
                    newId = "KH" + String.format("%02d", newNumber)
                }
            }
        }
        return newId
    }

    fun getCustomerById(customerId: String): Customer? {
        val db = databaseHelper.readableDatabase
        val cursor = db.rawQuery("SELECT * FROM KhachHang WHERE MaKH = ?", arrayOf(customerId))
        var customer: Customer? = null

        if (cursor.moveToFirst()) {
            customer = Customer(
                MaKH = cursor.getString(cursor.getColumnIndexOrThrow("MaKH")),
                HoKH = cursor.getString(cursor.getColumnIndexOrThrow("HoKH")),
                TenKH = cursor.getString(cursor.getColumnIndexOrThrow("TenKH")),
                SdtKH = cursor.getString(cursor.getColumnIndexOrThrow("SdtKH")) ?: "",
                EmailKH = cursor.getString(cursor.getColumnIndexOrThrow("EmailKH")) ?: "",
                GioiTinhKH = cursor.getString(cursor.getColumnIndexOrThrow("GioiTinhKH")) ?: "",
                NgaySinhKH = cursor.getString(cursor.getColumnIndexOrThrow("NgaySinhKH")) ?: "",
                DiaChiKH = cursor.getString(cursor.getColumnIndexOrThrow("DiaChiKH")) ?: ""
            )
        }

        cursor.close()
        db.close()
        return customer
    }

    fun updateCustomer(customer: Customer): Boolean {
        val db = databaseHelper.writableDatabase
        val values = ContentValues().apply {
            put("HoKH", customer.HoKH)
            put("TenKH", customer.TenKH)
            put("SdtKH", customer.SdtKH)
            put("EmailKH", customer.EmailKH)
            put("GioiTinhKH", customer.GioiTinhKH)
            put("NgaySinhKH", customer.NgaySinhKH)
            put("DiaChiKH", customer.DiaChiKH)
        }

        val result = db.update("KhachHang", values, "MaKH = ?", arrayOf(customer.MaKH))

        db.close()
        return result > 0
    }
}