package com.example.project_android_booking_movietickets.fragment.food

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.project_android_booking_movietickets.dao.FoodDao
import com.example.project_android_booking_movietickets.databinding.ActivityFoodEditBinding
import com.example.project_android_booking_movietickets.model.FoodItem

class FoodUpdateActivity : AppCompatActivity() {
    private lateinit var binding: ActivityFoodEditBinding
    private lateinit var foodDao: FoodDao
    private var foodId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFoodEditBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        foodDao = FoodDao(this)
        foodId = intent.getStringExtra("FOOD_ID")
        foodId?.let {
            loadFoodDetails(it)
        }

        binding.btnEdit.text = "Cập nhật"
        binding.btnEdit.setOnClickListener {
            updateFoodDetails()
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
            .setPositiveButton("Có") { _, _ -> super.onBackPressed() }
            .setNegativeButton("Không") { dialog, _ -> dialog.dismiss() }
            .show()
    }

    private fun loadFoodDetails(id: String) {
        val food = foodDao.getAllFoods().find { it.MaDoAn == id }
        if (food != null) {
            binding.edtFoodId.setText(food.MaDoAn)
            binding.edtFoodName.setText(food.TenDoAn)
            binding.edtDescription.setText(food.MoTa)
            binding.edtImage.setText(food.AnhMinhHoa)
            binding.edtPrice.setText(food.GiaBan.toString())
        } else {
            Toast.makeText(this, "Không tìm thấy món ăn", Toast.LENGTH_SHORT).show()
        }
    }

    private fun updateFoodDetails() {
        val foodId = binding.edtFoodId.text.toString()
        val foodName = binding.edtFoodName.text.toString()
        val description = binding.edtDescription.text.toString()
        val image = binding.edtImage.text.toString()
        val price = binding.edtPrice.text.toString().toIntOrNull() ?: 0

        if (validateFields()) {
            val food = FoodItem(foodId, foodName, description, image, price)
            val rowsAffected = foodDao.updateFood(food)
            if (rowsAffected > 0) {
                Toast.makeText(this, "Cập nhật món ăn thành công", Toast.LENGTH_SHORT).show()
                finish()
            } else {
                Toast.makeText(this, "Cập nhật thất bại", Toast.LENGTH_SHORT).show()
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
            } else if (edtPrice.text.toString().toIntOrNull() == null) {
                edtPrice.error = "Giá bán phải là số"
                edtPrice.requestFocus()
                return false
            }
            if (edtImage.text.isNullOrEmpty()) {
                edtImage.error = "Ảnh minh họa không được để trống"
                edtImage.requestFocus()
                return false
            }
        }
        return true
    }
}
