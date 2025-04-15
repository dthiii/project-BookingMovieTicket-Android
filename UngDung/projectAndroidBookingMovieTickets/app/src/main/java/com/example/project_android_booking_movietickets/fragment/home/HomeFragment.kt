package com.example.project_android_booking_movietickets.fragment.home

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.PagerSnapHelper
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.example.project_android_booking_movietickets.R
import com.example.project_android_booking_movietickets.ShowtimeActivity
import com.example.project_android_booking_movietickets.adapter.MovieAdapter
import com.example.project_android_booking_movietickets.adapter.MovieInfoAdapter
import com.example.project_android_booking_movietickets.dao.MovieDao
import com.example.project_android_booking_movietickets.model.Movie

class HomeFragment : Fragment() {
    private lateinit var viewPagerBanner: ViewPager2
    private lateinit var indicatorLayout: LinearLayout
    private val handler = Handler(Looper.getMainLooper())
    private var currentPage = 0
    private val bannerImages = listOf(
        R.drawable.banner1,
        R.drawable.banner2,
        R.drawable.banner3,
        R.drawable.banner4,
        R.drawable.banner5
    )
    private lateinit var recyclerViewMovies: RecyclerView
    private lateinit var movieAdapter: MovieAdapter
    private lateinit var tvTitle: TextView
    private lateinit var tvDuration: TextView
    private lateinit var btnBooking: Button
    private var currentMovie: Movie? = null


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Banner setup
        viewPagerBanner = view.findViewById(R.id.viewPagerBanner)
        indicatorLayout = view.findViewById(R.id.indicatorLayout)

        viewPagerBanner.adapter = object : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
            override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
                val imageView = ImageView(parent.context).apply {
                    layoutParams = ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT
                    )
                    adjustViewBounds = true
                    scaleType = ImageView.ScaleType.FIT_CENTER
                }
                return object : RecyclerView.ViewHolder(imageView) {}
            }

            override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
                (holder.itemView as ImageView).setImageResource(bannerImages[position])
            }

            override fun getItemCount(): Int = bannerImages.size
        }

        setupIndicators(bannerImages.size)
        updateIndicator(0)

        viewPagerBanner.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                updateIndicator(position)
                currentPage = position
            }
        })
        startAutoScroll()


        // RecyclerView setup
        recyclerViewMovies = view.findViewById(R.id.recyclerViewMovies)
        tvTitle = view.findViewById(R.id.tvTitle)
        tvDuration = view.findViewById(R.id.tvDuration)
        btnBooking = view.findViewById(R.id.btnBooking)

        recyclerViewMovies.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)

        val movieDao = MovieDao(requireContext())
        val movieList = movieDao.getAllMovies()

        if (movieList.isNotEmpty()) {
            updateMovieInfo(movieList[0])
        }

        movieAdapter = MovieAdapter(movieList, recyclerViewMovies) { selectedMovie ->
            updateMovieInfo(selectedMovie)
        }

        recyclerViewMovies.adapter = movieAdapter

        val snapHelper = PagerSnapHelper()
        snapHelper.attachToRecyclerView(recyclerViewMovies)

        recyclerViewMovies.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    movieAdapter.updateSelectedPosition()
                }
            }
        })


        //Xử lý sự kiện cho nút btnBooking
        btnBooking.setOnClickListener {
            currentMovie?.let { movie ->
                val intent = Intent(requireContext(), ShowtimeActivity::class.java)
                intent.putExtra("MaPhim", movie.MaPhim) // Truyền mã phim
                startActivity(intent)
            }
        }
    }

    private fun setupIndicators(count: Int) {
        indicatorLayout.removeAllViews()
        val indicators = Array(count) { ImageView(requireContext()) }
        val layoutParams = LinearLayout.LayoutParams(32, 32).apply { setMargins(8, 0, 8, 0) }
        for (i in indicators.indices) {
            indicators[i].layoutParams = layoutParams
            indicators[i].setImageDrawable(ContextCompat.getDrawable(requireContext(),
                R.drawable.indicator_inactive
            ))
            indicatorLayout.addView(indicators[i])
        }
    }

    private fun updateIndicator(position: Int) {
        if (!isAdded) return
        for (i in 0 until indicatorLayout.childCount) {
            val imageView = indicatorLayout.getChildAt(i) as ImageView
            val drawable = if (i == position) {
                ContextCompat.getDrawable(requireContext(), R.drawable.indicator_active)
            } else {
                ContextCompat.getDrawable(requireContext(), R.drawable.indicator_inactive)
            }
            imageView.setImageDrawable(drawable)
        }
    }

    private fun startAutoScroll() {
        val runnable = object : Runnable {
            override fun run() {
                if (viewPagerBanner.adapter != null) {
                    currentPage = (currentPage + 1) % bannerImages.size
                    viewPagerBanner.setCurrentItem(currentPage, true)
                    handler.postDelayed(this, 3000)
                }
            }
        }
        handler.postDelayed(runnable, 3000)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        handler.removeCallbacksAndMessages(null)
    }

    private fun updateMovieInfo(movie: Movie) {
        currentMovie = movie
        tvTitle.text = movie.TenPhim
        tvDuration.text = "${movie.ThoiLuong} phút"
    }
}