package com.example.project_android_booking_movietickets.fragment.customer

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ListView
import androidx.fragment.app.Fragment
import com.example.project_android_booking_movietickets.R
import com.example.project_android_booking_movietickets.dao.CustomerDao
import com.example.project_android_booking_movietickets.model.Customer
import com.google.android.material.floatingactionbutton.FloatingActionButton

class CustomerListFragment : Fragment() {
    private lateinit var lvCustomers: ListView
    private lateinit var customerDao: CustomerDao
    private var customerList = mutableListOf<Customer>()
    private lateinit var adapter: ArrayAdapter<String>
    private lateinit var btnAdd: FloatingActionButton

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_customer_list, container, false)
        lvCustomers = view.findViewById(R.id.lvCustomers)
        btnAdd = view.findViewById(R.id.btnAdd)

        customerDao = CustomerDao(requireContext())

        loadCustomerList()

        lvCustomers.setOnItemClickListener { _, _, position, _ ->
            val selectedCustomer = customerList[position]
            Log.d("CustomerListFragment", "Item clicked: ${selectedCustomer.HoKH} ${selectedCustomer.TenKH}")
            val intent = Intent(requireContext(), CustomerUpdateActivity::class.java)
            intent.putExtra("MaKH", selectedCustomer.MaKH)
            startActivity(intent)
        }

        btnAdd.setOnClickListener {
            Log.d("CustomerListFragment", "Add customer button clicked.")
            val intent = Intent(activity, CustomerAddActivity::class.java)
            startActivity(intent)
        }

        return view
    }

    override fun onResume() {
        super.onResume()
        Log.d("CustomerListFragment", "onResume called, loading customer list.")
        loadCustomerList()
    }

    private fun loadCustomerList() {
        Log.d("CustomerListFragment", "Loading customer list.")
        customerList = customerDao.getAllCustomers().toMutableList()

        // Log the number of customers loaded
        Log.d("CustomerListFragment", "Loaded ${customerList.size} customers.")

        // Tạo danh sách tên khách hàng để hiển thị trong ListView
        val customerNames = customerList.map { "${it.HoKH} ${it.TenKH}" }

        // Log the customer names
        Log.d("CustomerListFragment", "Customer names: $customerNames")

        // Sử dụng ArrayAdapter để hiển thị tên khách hàng
        adapter = ArrayAdapter(requireContext(), R.layout.item_customer, customerNames)
        lvCustomers.adapter = adapter
    }

    fun updateCustomerList(newList: List<Customer>) {
        Log.d("CustomerListFragment", "Updating customer list.")
        customerList.clear()
        customerList.addAll(newList)

        // Log the new list size
        Log.d("CustomerListFragment", "New list size: ${newList.size}")

        // Cập nhật lại danh sách tên khách hàng
        val customerNames = newList.map { "${it.HoKH} ${it.TenKH}" }
        Log.d("CustomerListFragment", "Updated customer names: $customerNames")

        adapter.clear()
        adapter.addAll(customerNames)
        adapter.notifyDataSetChanged()
    }
}
