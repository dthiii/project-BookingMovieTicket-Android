package com.example.project_android_booking_movietickets.fragment.food

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.project_android_booking_movietickets.R
import androidx.appcompat.widget.SearchView
import androidx.core.content.ContextCompat
import com.example.project_android_booking_movietickets.dao.FoodDao
import com.example.project_android_booking_movietickets.databinding.ActivitySearchBinding

class FoodSearchActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySearchBinding
    private lateinit var foodDao: FoodDao
    private var foodListFragment: FoodListFragment? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Inflate the layout using ViewBinding
        binding = ActivitySearchBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Khởi tạo FoodDao
        foodDao = FoodDao(this)

        // Set up ViewPager2 với adapter
        val pagerAdapter = ViewPagerAdapter(this)
        binding.viewPager.adapter = pagerAdapter

        // Xử lý tìm kiếm
        binding.edtSearch.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                query?.let { performSearch(it) }
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                newText?.let { performSearch(it) }
                return true
            }
        })

        binding.tvCancel.setOnClickListener {
            finish()
        }

        // Đổi màu text khi SearchView được focus
        binding.edtSearch.setOnQueryTextFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                val searchText = binding.edtSearch.findViewById<android.widget.TextView>(androidx.appcompat.R.id.search_src_text)
                searchText?.setTextColor(ContextCompat.getColor(this, R.color.text))
                searchText?.setHintTextColor(ContextCompat.getColor(this, R.color.text_dark))
            }
        }
    }

    // Adapter cho ViewPager2
    private inner class ViewPagerAdapter(activity: AppCompatActivity) : FragmentStateAdapter(activity) {
        override fun getItemCount(): Int = 1  // Chỉ có một fragment

        override fun createFragment(position: Int): FoodListFragment {
            val fragment = FoodListFragment()
            foodListFragment = fragment
            return fragment
        }
    }

    // Tìm kiếm đồ ăn trong database
    private fun performSearch(query: String) {
        val listSearch = foodDao.searchFood(query)
        foodListFragment?.updateFoodList(listSearch)
    }

}
