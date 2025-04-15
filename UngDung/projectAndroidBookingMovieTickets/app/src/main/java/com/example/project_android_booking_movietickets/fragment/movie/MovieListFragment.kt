package com.example.project_android_booking_movietickets.fragment.movie

import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.project_android_booking_movietickets.DBHelper.DatabaseHelper
import com.example.project_android_booking_movietickets.R
import com.example.project_android_booking_movietickets.adapter.MovieInfoAdapter
import com.example.project_android_booking_movietickets.dao.MovieDao
import com.example.project_android_booking_movietickets.model.Customer
import com.example.project_android_booking_movietickets.model.Movie
import com.google.android.material.floatingactionbutton.FloatingActionButton

class MovieListFragment : Fragment() {
    private lateinit var rcvMovie: RecyclerView
    private lateinit var movieList: ArrayList<Movie>
    private lateinit var movieAdapter: MovieInfoAdapter
    private lateinit var databaseHelper: DatabaseHelper
    private lateinit var movieDao: MovieDao

    companion object {
        private const val REQUEST_CODE_MOVIE_DETAIL = 1
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_list, container, false)

        rcvMovie = view.findViewById(R.id.rcvList)
        rcvMovie.layoutManager = LinearLayoutManager(context)
        rcvMovie.setHasFixedSize(true)

        databaseHelper = DatabaseHelper(requireContext())
        movieDao = MovieDao(requireContext())

        movieList = ArrayList(movieDao.getAllMovies())
        Log.d("MovieListFragment", "Loaded movies: ${movieList.size}")

        movieAdapter = MovieInfoAdapter(movieList.toMutableList(), rcvMovie) { movie ->
            val intent = Intent(context, MovieInfoActivity::class.java)
            intent.putExtra("MOVIE_ID", movie.MaPhim)
            startActivityForResult(intent, REQUEST_CODE_MOVIE_DETAIL)

        }

        rcvMovie.adapter = movieAdapter
        loadMovieList()

        val btnAdd = view.findViewById<FloatingActionButton>(R.id.btnAdd)
        btnAdd.setOnClickListener {
            val intent = Intent(activity, MovieAddActivity::class.java)
            startActivity(intent)
        }

        return view
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE_MOVIE_DETAIL && resultCode == Activity.RESULT_OK) {
            loadMovieList()
        }
    }

    override fun onResume() {
        super.onResume()
        pendingQuery?.let {
            val results = movieDao.searchMovie(it)
            updateMovieList(results)
        }
        //loadMovieList()
    }

    private fun loadMovieList() {
        val movies = movieDao.getAllMovies()  // Lấy danh sách phim mới từ database
        Log.d("MovieListFragment", "Movies loaded: ${movies.size}")
        movieAdapter.updateData(movies)  // Cập nhật lại dữ liệu cho adapter
    }

    fun updateMovieList(newList: List<Movie>) {
        movieAdapter.updateData(newList)  // Cập nhật lại adapter với danh sách phim mới
    }


    private var pendingQuery: String? = null

    fun setInitialSearchQuery(query: String) {
        pendingQuery = query
        if (this::movieDao.isInitialized && this::movieAdapter.isInitialized) {
            val results = movieDao.searchMovie(query)
            updateMovieList(results)
        }
    }

}