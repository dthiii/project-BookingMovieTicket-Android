package com.example.project_android_booking_movietickets.fragment.customer

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.project_android_booking_movietickets.R
import com.example.project_android_booking_movietickets.dao.CinemaDao
import com.example.project_android_booking_movietickets.dao.CustomerDao
import com.example.project_android_booking_movietickets.databinding.ActivityCustomerEditBinding
import com.example.project_android_booking_movietickets.model.Customer
import com.example.project_android_booking_movietickets.model.Showtime
import java.util.Calendar

class CustomerAddActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCustomerEditBinding
    private lateinit var customerDao: CustomerDao


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCustomerEditBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        customerDao = CustomerDao(this)
        binding.edtCustomerId.setText(customerDao.generateNewId())

        binding.imvBirthday.setOnClickListener {
            val calendar = Calendar.getInstance()
            val datePicker = DatePickerDialog(
                this,
                { _, year, month, dayOfMonth ->
                    val dateStr = "%02d/%02d/%04d".format(dayOfMonth, month + 1, year)
                    binding.edtBirthday.setText(dateStr)
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
            )
            datePicker.show()
        }

        binding.btnEdit.text = "Thêm"
        binding.btnEdit.setOnClickListener {
            addNewCustomer()
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

    private fun addNewCustomer() {
        val maKH = binding.edtCustomerId.text.toString().trim()
        val ho = binding.edtFirstName.text.toString().trim()
        val ten = binding.edtLastName.text.toString().trim()
        val soDienThoai = binding.edtPhone.text.toString().trim()
        val email = binding.edtEmail.text.toString().trim()
        val ngaySinh = binding.edtBirthday.text.toString().trim()
        val diaChi = binding.edtAddress.text.toString().trim()

        val selectedGenderId = binding.rgGender.checkedRadioButtonId
        val gioiTinh = when (selectedGenderId) {
            R.id.rdoNam -> "Nam"
            R.id.rdoNu -> "Nữ"
            else -> ""
        }

        if (maKH.isBlank() || ho.isBlank() || ten.isBlank() || gioiTinh.isBlank()) {
            Toast.makeText(this, "Vui lòng điền đầy đủ thông tin bắt buộc", Toast.LENGTH_SHORT).show()
            return
        }

        val newCustomer = Customer(
            MaKH = maKH,
            HoKH = ho,
            TenKH = ten,
            SdtKH = soDienThoai,
            EmailKH = email,
            NgaySinhKH = ngaySinh,
            DiaChiKH = diaChi,
            GioiTinhKH = gioiTinh
        )

        val isSuccess = customerDao.addCustomer(newCustomer)
        if (isSuccess) {
            Toast.makeText(this, "Thêm khách hàng thành công", Toast.LENGTH_SHORT).show()
            finish()
        } else {
            Toast.makeText(this, "Thêm khách hàng thất bại", Toast.LENGTH_SHORT).show()
        }
    }


    private fun validateFields(): Boolean {
        with(binding) {
            if (edtFirstName.text.isNullOrEmpty()) {
                edtFirstName.error = "Họ không được để trống"
                edtFirstName.requestFocus()
                return false
            }
            if (edtLastName.text.isNullOrEmpty()) {
                edtLastName.error = "Tên không được để trống"
                edtLastName.requestFocus()
                return false
            }
            if (edtLastName.text.isNullOrEmpty()) {
                edtLastName.error = "Tên không được để trống"
                edtLastName.requestFocus()
                return false
            }
            if (edtPhone.text.isNullOrEmpty()) {
                edtPhone.error = "Số điện thoại không được để trống"
                edtPhone.requestFocus()
                return false
            }
        }
        return true
    }
}