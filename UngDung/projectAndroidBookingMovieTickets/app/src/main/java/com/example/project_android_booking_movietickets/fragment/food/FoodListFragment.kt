package com.example.project_android_booking_movietickets.fragment.food

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.project_android_booking_movietickets.DBHelper.DatabaseHelper
import com.example.project_android_booking_movietickets.R
import com.example.project_android_booking_movietickets.adapter.FoodInfoAdapter
import com.example.project_android_booking_movietickets.dao.FoodDao
import com.example.project_android_booking_movietickets.model.FoodItem
import com.google.android.material.floatingactionbutton.FloatingActionButton
import androidx.recyclerview.widget.RecyclerView
import com.example.project_android_booking_movietickets.model.Movie

class FoodListFragment : Fragment() {
    private lateinit var foodList: ArrayList<FoodItem>
    private lateinit var foodAdapter: FoodInfoAdapter
    private lateinit var databaseHelper: DatabaseHelper
    private lateinit var foodDao: FoodDao
    private lateinit var rcvFood: RecyclerView
    private lateinit var btnAdd: FloatingActionButton

    companion object {
        private const val REQUEST_CODE_FOOD_DETAIL = 1
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.fragment_list, container, false)

        rcvFood = view.findViewById(R.id.rcvList)
        btnAdd = view.findViewById(R.id.btnAdd)

        rcvFood.layoutManager = LinearLayoutManager(context)
        rcvFood.setHasFixedSize(true)

        databaseHelper = DatabaseHelper(requireContext())
        foodDao = FoodDao(requireContext())

        foodList = ArrayList(foodDao.getAllFoods())

        foodAdapter = FoodInfoAdapter(
            requireContext(),
            foodList.toMutableList(),
            { food ->
                // Xử lý click vào nút Edit
                val intent = Intent(requireContext(), FoodUpdateActivity::class.java)
                intent.putExtra("FOOD_ID", food.MaDoAn)
                startActivity(intent)
            },
            { food ->
                // Xử lý click vào nút Delete
                showDeleteConfirmationDialog(food)
            }
        )

        rcvFood.adapter = foodAdapter

        btnAdd.setOnClickListener {
            val intent = Intent(activity, FoodAddActivity::class.java)
            startActivity(intent)
        }

        return view
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE_FOOD_DETAIL && resultCode == Activity.RESULT_OK) {
            loadFoodList()
        }
    }

    override fun onResume() {
        super.onResume()
        loadFoodList()
    }

    private fun loadFoodList() {
        val foods = foodDao.getAllFoods()
        foodAdapter.updateData(foods)
    }

    fun updateFoodList(newList: List<FoodItem>) {
        foodList.clear()
        foodList.addAll(newList)
        foodAdapter.updateData(newList)
    }

    private fun showDeleteConfirmationDialog(food: FoodItem) {
        val builder = AlertDialog.Builder(requireContext())
        builder.setMessage("Bạn có chắc chắn muốn xóa món ăn này không?")
            .setCancelable(false)
            .setPositiveButton("Có") { dialog, id ->
                deleteFood(food)
            }
            .setNegativeButton("Không") { dialog, id ->
                dialog.dismiss()
            }

        val alert = builder.create()
        alert.show()
    }

    private fun deleteFood(food: FoodItem) {
        val maDoAn = food.MaDoAn
        foodDao.deleteFood(maDoAn)
        loadFoodList()
    }
}
