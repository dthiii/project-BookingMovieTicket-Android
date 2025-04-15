package com.example.project_android_booking_movietickets.DBHelper

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream

class DatabaseHelper(private val context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "BookingTicketCinema.db"
        private const val DATABASE_VERSION = 1
    }

    private val dbPath = context.getDatabasePath(DATABASE_NAME).absolutePath

    init {
        Log.d("DatabaseCheck", "Database path: $dbPath")
        copyDatabaseFromAssets()
    }

    private fun copyDatabaseFromAssets() {
        val dbFile = File(dbPath)

        // XÓA database nếu đã tồn tại (ĐỂ CẬP NHẬT)
        if (dbFile.exists()) {
            Log.d("DatabaseCheck", "Xóa database cũ để cập nhật!")
            //dbFile.delete()
            return
        }

        try {
            val inputStream: InputStream = context.assets.open(DATABASE_NAME) // Đảm bảo tên đúng
            val outputStream: OutputStream = FileOutputStream(dbFile)

            val buffer = ByteArray(1024)
            var length: Int
            while (inputStream.read(buffer).also { length = it } > 0) {
                outputStream.write(buffer, 0, length)
            }

            outputStream.flush()
            outputStream.close()
            inputStream.close()

            dbFile.setWritable(true, false)
            dbFile.setReadable(true, false)
            dbFile.setExecutable(true, false)

            Log.d("DatabaseCheck", "Database copied & quyền ghi được cấp!")
        } catch (e: IOException) {
            Log.e("DatabaseCheck", "Lỗi sao chép database: ${e.message}")
        }

//            Log.d("DatabaseCheck", "Database copied thành công!")
//        } catch (e: IOException) {
//            Log.e("DatabaseCheck", "Lỗi sao chép database: ${e.message}")
//        }
    }


    fun openDatabase(): SQLiteDatabase? {
        val dbFile = File(dbPath)

        if (!dbFile.exists()) {
            Log.e("DatabaseCheck", "Database không tồn tại, hãy kiểm tra việc sao chép!")
            return null
        }

        return try {
            val db = SQLiteDatabase.openDatabase(dbPath, null, SQLiteDatabase.OPEN_READWRITE)
            Log.d("DatabaseCheckOpen", "Mở database thành công với quyền ghi!")
            db
        } catch (e: Exception) {
            Log.e("DatabaseCheckOpen", "Không thể mở database: ${e.message}")
            null
        }
    }

    override fun onCreate(db: SQLiteDatabase?) {
        // Không tạo bảng ở đây vì database đã có sẵn trong assets
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        Log.d("DatabaseCheck", "Database version thay đổi từ $oldVersion lên $newVersion")
    }
}

