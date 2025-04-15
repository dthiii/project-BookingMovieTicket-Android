package com.example.project_android_booking_movietickets.activity

import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.example.project_android_booking_movietickets.DBHelper.DatabaseHelper
import com.example.project_android_booking_movietickets.R
import com.example.project_android_booking_movietickets.dao.MovieDao
import com.example.project_android_booking_movietickets.fragment.movie.MovieListFragment
import androidx.appcompat.widget.SearchView
import androidx.core.content.ContextCompat

class MovieSearchActivity : AppCompatActivity() {
    private lateinit var edtSearch: SearchView
    private lateinit var viewPager: ViewPager2
    private var movieListFragment: MovieListFragment? = null
    private lateinit var movieDao: MovieDao
    private lateinit var tvCancel: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        Log.d("MovieSearchActivity", "onCreate Called")

        edtSearch = findViewById(R.id.edtSearch)
        viewPager = findViewById(R.id.view_pager)
        tvCancel = findViewById(R.id.tvCancel)

        edtSearch.setOnQueryTextFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                val searchText = edtSearch.findViewById<android.widget.TextView>(androidx.appcompat.R.id.search_src_text)
                searchText?.setTextColor(ContextCompat.getColor(this, R.color.text))
                searchText?.setHintTextColor(ContextCompat.getColor(this, R.color.text_dark))
            }
        }


        // Khởi tạo MovieDao
        val databaseHelper = DatabaseHelper(this)
        movieDao = MovieDao(this)

        // Set up ViewPager2 với adapter
        val pagerAdapter = ViewPagerAdapter(this)
        viewPager.adapter = pagerAdapter

        // Xử lý tìm kiếm phim
        edtSearch.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                query?.let { performSearch(it) }
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                newText?.let { performSearch(it) }
                return true
            }
        })

        tvCancel.setOnClickListener {
            finish()
        }
    }

    // Adapter cho ViewPager2
    private inner class ViewPagerAdapter(activity: AppCompatActivity) : FragmentStateAdapter(activity) {
        override fun getItemCount(): Int = 1  // Chỉ có một fragment

        override fun createFragment(position: Int): MovieListFragment {
            val fragment = MovieListFragment()
            movieListFragment = fragment

            // Nếu bạn đã gõ gì đó trong ô tìm kiếm, gửi lại khi fragment tạo xong
            val currentQuery = edtSearch.query?.toString()
            if (!currentQuery.isNullOrEmpty()) {
                fragment.setInitialSearchQuery(currentQuery)
            }

            return fragment
        }
    }

    // Tìm kiếm phim trong database
    private fun performSearch(query: String) {
        val listSearch = movieDao.searchMovie(query)
        movieListFragment?.updateMovieList(listSearch)
    }
}

