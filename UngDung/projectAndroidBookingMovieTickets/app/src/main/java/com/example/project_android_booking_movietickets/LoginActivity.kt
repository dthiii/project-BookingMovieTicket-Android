package com.example.project_android_booking_movietickets

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.project_android_booking_movietickets.DBHelper.DatabaseHelper
import com.example.project_android_booking_movietickets.dao.AccountDao
import com.example.project_android_booking_movietickets.databinding.ActivityLoginBinding
import com.google.android.material.snackbar.Snackbar

class LoginActivity : AppCompatActivity() {
    private val binding: ActivityLoginBinding by lazy {
        ActivityLoginBinding.inflate(layoutInflater)
    }

    private lateinit var databaseHelper: DatabaseHelper
    private lateinit var accountDao: AccountDao

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        databaseHelper = DatabaseHelper(this)
        accountDao = AccountDao(databaseHelper)


        val accounts = accountDao.getAllAccounts()
        if (accounts.isEmpty()) {
            Log.d("LoginActivity", "Không có tài khoản nào trong database.")
        } else {
            Log.d("LoginActivity", "Có ${accounts.size} tài khoản trong database.")
        }

        binding.btnLogin.setOnClickListener {
            val username = binding.edtUsername.text.toString()
            val password = binding.edtPassword.text.toString()
            val account = accountDao.getAccount(username, password)

            if (account != null) {
                val sharedPreferences = getSharedPreferences("MyPreferences", Context.MODE_PRIVATE)
                with (sharedPreferences.edit()) {
                    putString("USER_ROLE", account.LoaiNguoiDung)
                    putInt("ACCOUNT_ID", account.MaTK)
                    putString("MANV", account.MaNhanVien) // Lưu MaNhanVien để sau lấy MaRap
                    apply()
                }

                val intent = Intent(this, MainActivity::class.java)
                intent.putExtra("USER_ROLE", account.LoaiNguoiDung)
                startActivity(intent)
                finish()
                Log.d("LoginActivity", "Đăng nhập thành công! Chuyển sang MainActivity")

            } else {
                Snackbar.make(binding.root, "Tên đăng nhập hoặc mật khẩu không đúng", Snackbar.LENGTH_SHORT).show()
            }

        }
    }
}