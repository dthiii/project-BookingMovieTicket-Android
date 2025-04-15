package com.example.project_android_booking_movietickets.adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.project_android_booking_movietickets.MovieActivity
import com.example.project_android_booking_movietickets.fragment.movie.MovieInfoActivity
import com.example.project_android_booking_movietickets.R
import com.example.project_android_booking_movietickets.model.Movie

class MovieAdapter(
    private val originalMovieList: List<Movie>,
    private val recyclerView: RecyclerView,
    private val onMovieSelected: (Movie) -> Unit
) : RecyclerView.Adapter<MovieAdapter.MovieViewHolder>() {

    private var selectedPosition = 0

    inner class MovieViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imvPoster: ImageView = itemView.findViewById(R.id.imvPoster)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MovieViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_movie, parent, false)
        return MovieViewHolder(view)
    }

    override fun onBindViewHolder(holder: MovieViewHolder, position: Int) {
        val actualPosition = position % originalMovieList.size
        val movie = originalMovieList[actualPosition]

        Glide.with(holder.itemView.context)
            .load(movie.Poster)
            .into(holder.imvPoster)

        val isSelected = actualPosition == selectedPosition

        val scaleFactor = if (isSelected) 0.5f else 0.4f
        holder.itemView.scaleX = scaleFactor
        holder.itemView.scaleY = scaleFactor

        holder.imvPoster.setOnClickListener {
            val context = holder.itemView.context
            val intent = Intent(context, MovieActivity::class.java)
            intent.putExtra("MaPhim", movie.MaPhim)
            context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int = Int.MAX_VALUE

    fun updateSelectedPosition() {
        val layoutManager = recyclerView.layoutManager as? LinearLayoutManager ?: return
        val firstVisible = layoutManager.findFirstVisibleItemPosition()
        val lastVisible = layoutManager.findLastVisibleItemPosition()
        if (firstVisible == RecyclerView.NO_POSITION || lastVisible == RecyclerView.NO_POSITION) return

        val centerPosition = (firstVisible + lastVisible) / 2
        selectedPosition = centerPosition % originalMovieList.size
        onMovieSelected(originalMovieList[selectedPosition])
        notifyDataSetChanged()
    }

    fun updateData(newMovies: List<Movie>) {
        (originalMovieList as ArrayList).clear()
        (originalMovieList as ArrayList).addAll(newMovies)
        notifyDataSetChanged()
    }

}
