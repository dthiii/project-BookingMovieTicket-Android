package com.example.project_android_booking_movietickets.fragment.food

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.project_android_booking_movietickets.databinding.ActivityFoodEditBinding
import com.example.project_android_booking_movietickets.dao.FoodDao
import com.example.project_android_booking_movietickets.model.FoodItem

class FoodAddActivity : AppCompatActivity() {
    private lateinit var binding: ActivityFoodEditBinding
    private lateinit var foodDao: FoodDao
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFoodEditBinding.inflate(layoutInflater)
        setContentView(binding.root)
        Log.d("FoodAddActivity", "Layout inflated and set")

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        foodDao = FoodDao(this)
        binding.edtFoodId.setText(foodDao.generateNewId())

        binding.btnEdit.text = "Thêm"
        binding.btnEdit.setOnClickListener {
            addNewFood()
        }

        binding.imbBack.setOnClickListener {
            finish()
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    override fun onBackPressed() {
        AlertDialog.Builder(this)
            .setTitle("Xác nhận")
            .setMessage("Bạn có chắc chắn muốn quay lại không? Những thay đổi của bạn sẽ không được lưu.")
            .setPositiveButton("Có") { _, _ ->
                super.onBackPressed()
            }
            .setNegativeButton("Không") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

    private fun addNewFood() {
        val foodId = binding.edtFoodId.text.toString()
        val foodName = binding.edtFoodName.text.toString()
        val description = binding.edtDescription.text.toString()
        val price = binding.edtPrice.text.toString().toIntOrNull() ?: 0
        val image = binding.edtImage.text.toString()

        Log.d("FoodAddActivity", "Food Name: $foodName, Food Price: $price")

        if (validateFields()) {
            val food = FoodItem(foodId, foodName, description, image, price)
            val rowsAffected = foodDao.insertFood(food)
            if (rowsAffected > 0) {
                Toast.makeText(this, "Thêm món ăn thành công", Toast.LENGTH_SHORT).show()
                finish()
            } else {
                Toast.makeText(this, "Thêm món ăn thất bại", Toast.LENGTH_SHORT).show()
            }
        }
    }


    private fun validateFields(): Boolean {
        with(binding) {
            if (edtFoodName.text.isNullOrEmpty()) {
                edtFoodName.error = "Tên món ăn không được để trống"
                edtFoodName.requestFocus()
                return false
            }
            if (edtDescription.text.isNullOrEmpty()) {
                edtDescription.error = "Mô tả không được để trống"
                edtDescription.requestFocus()
                return false
            }
            if (edtPrice.text.isNullOrEmpty()) {
                edtPrice.error = "Giá bán không được để trống"
                edtPrice.requestFocus()
                return false
            } else {
                val price = edtPrice.text.toString().toIntOrNull()
                if (price == null || price < 0) {
                    edtPrice.error = "Giá bán không hợp lệ"
                    edtPrice.requestFocus()
                    return false
                }
            }
            if (edtImage.text.isNullOrEmpty()) {
                edtImage.error = "Link hình ảnh không được để trống"
                edtImage.requestFocus()
                return false
            }
        }
        return true
    }
}
