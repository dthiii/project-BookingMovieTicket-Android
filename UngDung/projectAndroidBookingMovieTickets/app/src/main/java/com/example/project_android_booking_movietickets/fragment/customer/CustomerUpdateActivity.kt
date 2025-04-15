package com.example.project_android_booking_movietickets.fragment.customer

import android.app.DatePickerDialog
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.project_android_booking_movietickets.dao.CustomerDao
import com.example.project_android_booking_movietickets.databinding.ActivityCustomerEditBinding
import com.example.project_android_booking_movietickets.model.Customer
import java.util.Calendar

class CustomerUpdateActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCustomerEditBinding
    private lateinit var customerId: String
    private lateinit var customerDao: CustomerDao

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCustomerEditBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Quay lại màn hình trước
        binding.imbBack.setOnClickListener {
            finish()
        }

        customerId = intent.getStringExtra("MaKH") ?: return

        customerDao = CustomerDao(this)
        val customer: Customer? = customerDao.getCustomerById(customerId)

        if (customer != null) {
            // Hiển thị thông tin khách hàng lên UI
            binding.edtCustomerId.setText(customer.MaKH)
            binding.edtFirstName.setText(customer.HoKH)
            binding.edtLastName.setText(customer.TenKH)
            binding.edtPhone.setText(customer.SdtKH)
            binding.edtEmail.setText(customer.EmailKH)
            binding.edtBirthday.setText(customer.NgaySinhKH)
            binding.edtAddress.setText(customer.DiaChiKH)

            if (customer.GioiTinhKH == "Nam") {
                binding.rdoNam.isChecked = true
            } else {
                binding.rdoNu.isChecked = true
            }
        }

        binding.btnEdit.text = "Sửa"
        binding.btnEdit.setOnClickListener {
            if (!validateFields()) return@setOnClickListener

            val gioiTinh = if (binding.rdoNam.isChecked) "Nam" else "Nữ"

            val updatedCustomer = Customer(
                MaKH = customer?.MaKH ?: return@setOnClickListener,
                HoKH = binding.edtLastName.text.toString(),
                TenKH = binding.edtFirstName.text.toString(),
                SdtKH = binding.edtPhone.text.toString(),
                EmailKH = binding.edtEmail.text.toString(),
                GioiTinhKH = gioiTinh,
                NgaySinhKH = binding.edtBirthday.text.toString(),
                DiaChiKH = binding.edtAddress.text.toString()
            )

            val success = customerDao.updateCustomer(updatedCustomer)
            if (success) {
                Toast.makeText(this, "Cập nhật thông tin thành công", Toast.LENGTH_SHORT).show()
                finish()
            } else {
                Toast.makeText(this, "Cập nhật thất bại", Toast.LENGTH_SHORT).show()
            }
        }

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
    }

    private fun validateFields(): Boolean {
        with(binding) {
            if (edtFirstName.text.isNullOrBlank()) {
                edtFirstName.error = "Tên không được để trống"
                edtFirstName.requestFocus()
                return false
            }
            if (edtLastName.text.isNullOrBlank()) {
                edtLastName.error = "Họ không được để trống"
                edtLastName.requestFocus()
                return false
            }
            if (edtPhone.text.isNullOrBlank()) {
                edtPhone.error = "Số điện thoại không được để trống"
                edtPhone.requestFocus()
                return false
            }
            if (edtBirthday.text.isNullOrBlank()) {
                edtBirthday.error = "Ngày sinh không được để trống"
                edtBirthday.requestFocus()
                return false
            }
            return true
        }
    }
}
