package com.example.project_android_booking_movietickets.fragment.showtime

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.core.content.ContextCompat
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.project_android_booking_movietickets.R
import com.example.project_android_booking_movietickets.dao.ShowtimeDao
import com.example.project_android_booking_movietickets.databinding.ActivitySearchBinding

class ShowtimeSearchActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySearchBinding
    private lateinit var showtimeDao: ShowtimeDao
    private var showtimeListFragment: ShowtimeListFragment? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitySearchBinding.inflate(layoutInflater)
        setContentView(binding.root)

        showtimeDao = ShowtimeDao(this)

        val pagerAdapter = ViewPagerAdapter(this)
        binding.viewPager.adapter = pagerAdapter

        // Lắng nghe sự kiện tìm kiếm
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

        // Xử lý nút cancel
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

    private inner class ViewPagerAdapter(activity: AppCompatActivity) : FragmentStateAdapter(activity) {
        override fun getItemCount(): Int = 1

        override fun createFragment(position: Int): ShowtimeListFragment {
            val fragment = ShowtimeListFragment()
            showtimeListFragment = fragment
            return fragment
        }
    }

    // Tìm kiếm suất chiếu trong database theo tên phim
//    private fun performSearch(query: String) {
//        val listSearch = showtimeDao.getShowtimesByMovieName(query)
//        showtimeListFragment?.loadShowtimeList(listSearch)
//    }

    private fun performSearch(query: String) {
        // Lấy danh sách suất chiếu theo tên phim từ DAO
        val listSearch = showtimeDao.getShowtimesByMovieName(query)

        // Gọi phương thức loadShowtimeList trong ShowtimeListFragment để cập nhật dữ liệu
        //showtimeListFragment?.loadShowtimeList(listSearch)
    }
}
