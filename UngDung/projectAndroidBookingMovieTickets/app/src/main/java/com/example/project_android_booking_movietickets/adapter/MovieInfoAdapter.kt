package com.example.project_android_booking_movietickets.adapter

import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.project_android_booking_movietickets.fragment.movie.MovieInfoActivity
import com.example.project_android_booking_movietickets.R
import com.example.project_android_booking_movietickets.model.Movie

class MovieInfoAdapter(
    private val movieList: MutableList<Movie>,
    private val recyclerView: RecyclerView,
    private val onMovieSelected: (Movie) -> Unit
) : RecyclerView.Adapter<MovieInfoAdapter.MovieViewHolder>() {

    private var selectedPosition = 0

    inner class MovieViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imgPoster: ImageView = itemView.findViewById(R.id.imgPoster)
        val tvMovieName: TextView = itemView.findViewById(R.id.tvMovieName)
        val tvGenre: TextView = itemView.findViewById(R.id.tvGenre)
        val tvDuration: TextView = itemView.findViewById(R.id.tvDuration)
        val tvShowtime: TextView = itemView.findViewById(R.id.tvShowtime)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MovieViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_movie_info, parent, false)
        return MovieViewHolder(view)
    }

    override fun onBindViewHolder(holder: MovieViewHolder, position: Int) {
        val movie = movieList[position]
        Log.d("MovieInfoAdapter", "Binding movie: ${movie.TenPhim}")

        holder.tvMovieName.text = movie.TenPhim
        holder.tvGenre.text = movie.TheLoai
        holder.tvDuration.text = movie.ThoiLuong.toString() + " phút"
        holder.tvShowtime.text = movie.NgayKhoiChieu

        Glide.with(holder.itemView.context)
            .load(movie.Poster)
            .into(holder.imgPoster)

        holder.itemView.setOnClickListener {
            val context = holder.itemView.context
            val intent = Intent(context, MovieInfoActivity::class.java)
            intent.putExtra("MaPhim", movie.MaPhim)
            context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int = movieList.size


    fun updateData(newMovies: List<Movie>) {
        movieList.clear()
        movieList.addAll(newMovies)
        notifyDataSetChanged()
    }
}
