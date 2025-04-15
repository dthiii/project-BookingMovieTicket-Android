package com.example.project_android_booking_movietickets.dao

import android.content.ContentValues
import android.database.Cursor
import android.util.Log
import com.example.project_android_booking_movietickets.DBHelper.DatabaseHelper
import com.example.project_android_booking_movietickets.model.Account

class AccountDao(private val databaseHelper: DatabaseHelper) {

    fun generateNewId(): String {
        var newId = "TK00001"
        val db = databaseHelper.readableDatabase
        db.rawQuery("SELECT MAX(MaTK) FROM TaiKhoan", null).use { cursor ->
            if (cursor.moveToFirst()) {
                val maxCode = cursor.getString(0)
                if (!maxCode.isNullOrEmpty()) {
                    val currentNumber = maxCode.substring(1).toInt()
                    val newNumber = currentNumber + 1
                    newId = "TK" + String.format("%05d", newNumber)
                }
            }
        }
        db.close()
        return newId
    }

    fun insert(account: Account): Int {
        val db = databaseHelper.openDatabase()

        val contentValues = ContentValues().apply {
            put("MaTK", generateNewId())
            put("TenTK", account.TenTK)
            put("MatKhau", account.MatKhau)
            put("LoaiNguoiDung", account.LoaiNguoiDung)
        }
        val result = db?.insert("TaiKhoan", null, contentValues)
        db?.close()
        return if (result == -1L) 0 else 1
    }

    fun update(account: Account): Int {
        val db = databaseHelper.writableDatabase

        val contentValues = ContentValues().apply {
            put("TenTK", account.TenTK)
            put("MatKhau", account.MatKhau)
            put("LoaiNguoiDung", account.LoaiNguoiDung)
        }

        val rowsAffected = db.update("TaiKhoan", contentValues, "MaTK = ?", arrayOf(account.MaTK.toString()))
        db.close()
        return rowsAffected
    }

    fun delete(accountId: String): Int {
        val db = databaseHelper.writableDatabase
        db.execSQL("PRAGMA foreign_keys = ON;")
        val rowsAffected = db.delete("TaiKhoan", "MaTK = ?", arrayOf(accountId))
        db.close()
        return rowsAffected
    }

    private fun cursor(cursor: Cursor): Account {
        val accountId = cursor.getInt(cursor.getColumnIndexOrThrow("MaTK"))
        val username = cursor.getString(cursor.getColumnIndexOrThrow("TenTK"))
        val password = cursor.getString(cursor.getColumnIndexOrThrow("MatKhau"))
        val usertype = cursor.getString(cursor.getColumnIndexOrThrow("LoaiNguoiDung"))
        val employeeId = cursor.getString(cursor.getColumnIndexOrThrow("MaNV"))
        //val customerId = cursor.getString(cursor.getColumnIndexOrThrow("MaKH"))

        return Account(accountId, username, password, usertype, employeeId)
    }

    fun getAccount(username: String, password: String): Account? {
        val db = databaseHelper.openDatabase()
        val cursor = db?.rawQuery(
            "SELECT * FROM TaiKhoan WHERE TenTK = ? AND MatKhau = ?",
            arrayOf(username, password)
        )
        var account: Account? = null
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                account = cursor(cursor)
            }
        }
        if (cursor != null) {
            cursor.close()
        }
        db?.close()
        return account
    }

    fun getAccountById(accountId: String): Account? {
        val db = databaseHelper.openDatabase()
        val cursor = db?.rawQuery(
            "SELECT * FROM TaiKhoan WHERE MaTK = ?",
            arrayOf(accountId)
        )
        var account: Account? = null
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                account = cursor(cursor)
            }
        }
        if (cursor != null) {
            cursor.close()
        }
        db?.close()
        return account
    }

    fun getAllAccounts(): List<Account> {
        val db = databaseHelper.openDatabase()
        val accountList = mutableListOf<Account>()
        val cursor = db?.rawQuery("SELECT * FROM TaiKhoan", null)

        if (cursor != null) {
            while (cursor.moveToNext()) {
                val account = cursor(cursor)
                accountList.add(account)

                // Ghi log thông tin tài khoản
                Log.d("AccountDao", "MaTK: ${account.MaTK}, TenTK: ${account.TenTK}, LoaiNguoiDung: ${account.LoaiNguoiDung}")
            }
            cursor.close()
        }
        db?.close()
        return accountList
    }

}