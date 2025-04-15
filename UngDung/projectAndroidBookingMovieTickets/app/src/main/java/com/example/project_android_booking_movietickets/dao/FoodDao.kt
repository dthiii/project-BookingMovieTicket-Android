package com.example.project_android_booking_movietickets.dao

import android.content.ContentValues
import android.content.Context
import com.example.project_android_booking_movietickets.DBHelper.DatabaseHelper
import com.example.project_android_booking_movietickets.model.FoodItem
import com.example.project_android_booking_movietickets.model.Movie

class FoodDao(private val context: Context) {
    private val dbHelper = DatabaseHelper(context)

    fun getAllFoods(): List<FoodItem> {
        val db = dbHelper.readableDatabase
        val foodList = mutableListOf<FoodItem>()
        val cursor = db.rawQuery("SELECT * FROM DoAnKem", null)

        while (cursor.moveToNext()) {
            val food = FoodItem(
                cursor.getString(0), // MaDoAn
                cursor.getString(1), // TenDoAn
                cursor.getString(2), // MoTa
                cursor.getString(3), // AnhMinhHoa
                cursor.getInt(4)     // GiaBan
            )
            foodList.add(food)
        }
        cursor.close()
        db.close()
        return foodList
    }

    fun getIdByName(tenDoAn: String): String? {
        val db = dbHelper.readableDatabase
        val cursor = db.rawQuery("SELECT MaDoAn FROM DoAnKem WHERE TenDoAn = ?", arrayOf(tenDoAn))
        var maDoAn: String? = null
        if (cursor.moveToFirst()) {
            maDoAn = cursor.getString(0)
        }
        cursor.close()
        return maDoAn
    }

    fun generateNewId(): String {
        val db = dbHelper.readableDatabase
        var newId = "DA1"
        val cursor = db.rawQuery("SELECT MAX(MaDoAn) FROM DoAnKem", null)
        cursor.use {
            if (it.moveToFirst()) {
                val maxCode = it.getString(0)
                if (!maxCode.isNullOrEmpty()) {
                    val currentNumber = maxCode.substring(2).toInt()
                    val newNumber = currentNumber + 1
                    newId = "DA" + String.format("%01d", newNumber)
                }
            }
        }
        return newId
    }

    fun searchFood(query: String): List<FoodItem> {
        val foodList = mutableListOf<FoodItem>()
        val searchQuery = "%$query%"  // Thêm dấu % để tìm kiếm theo một phần của chuỗi
        val db = dbHelper.readableDatabase
        val cursor = db.rawQuery(
            "SELECT * FROM DoAnKem WHERE TenDoAn LIKE ?",
            arrayOf(searchQuery, searchQuery)
        )

        while (cursor.moveToNext()) {
            val food = FoodItem(
                cursor.getString(0), // MaDoAn
                cursor.getString(1), // TenDoAn
                cursor.getString(2), // MoTa
                cursor.getString(3), // AnhMinhHoa
                cursor.getInt(4)     // GiaBan
            )
            foodList.add(food)
        }
        cursor.close()
        db.close()
        return foodList
    }

    fun insertFood(food: FoodItem): Long {
        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put("MaDoAn", food.MaDoAn)
            put("TenDoAn", food.TenDoAn)
            put("MoTa", food.MoTa)
            put("AnhMinhHoa", food.AnhMinhHoa)
            put("GiaBan", food.GiaBan)
        }

        val result = db.insert("DoAnKem", null, values)
        db.close()
        return result
    }

    fun updateFood(food: FoodItem): Int {
        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put("TenDoAn", food.TenDoAn)
            put("MoTa", food.MoTa)
            put("AnhMinhHoa", food.AnhMinhHoa)
            put("GiaBan", food.GiaBan)
        }
        return db.update("DoAnKem", values, "MaDoAn = ?", arrayOf(food.MaDoAn))
    }

    fun getFoodById(maDoAn: String): FoodItem? {
        val db = dbHelper.readableDatabase
        val cursor = db.rawQuery("SELECT * FROM DoAnKem WHERE MaDoAn = ?", arrayOf(maDoAn))
        var food: FoodItem? = null

        if (cursor.moveToFirst()) {
            // Lấy các giá trị từ cursor
            val maDoAn = cursor.getString(cursor.getColumnIndexOrThrow("MaDoAn"))
            val name = cursor.getString(cursor.getColumnIndexOrThrow("TenDoAn"))
            val description = cursor.getString(cursor.getColumnIndexOrThrow("MoTa"))
            val image = cursor.getString(cursor.getColumnIndexOrThrow("AnhMinhHoa"))
            val price = cursor.getInt(cursor.getColumnIndexOrThrow("GiaBan"))

            // Tạo đối tượng FoodItem và gán dữ liệu cho nó
            food = FoodItem(
                MaDoAn = maDoAn,
                TenDoAn = name,
                MoTa = description,
                AnhMinhHoa = image,
                GiaBan = price
            )
        }
        cursor.close()
        return food
    }

    fun deleteFood(maDoAn: String) {
        val db = dbHelper.writableDatabase
        db.delete("DoAnKem", "MaDoAn = ?", arrayOf(maDoAn))
        db.close()
    }
}
