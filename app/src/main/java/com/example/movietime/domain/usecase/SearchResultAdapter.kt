package com.example.movietime.domain.usecase

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.movietime.data.remote.dto.MovieDto
import com.example.movietime.databinding.ViewholderSearchItemBinding
import java.text.SimpleDateFormat
import java.util.Locale

class SearchResultAdapter(
    private val movies: List<MovieDto>,
    private val onItemClick: (MovieDto) -> Unit
) : RecyclerView.Adapter<SearchResultAdapter.SearchResultViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchResultViewHolder {
        val binding = ViewholderSearchItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return SearchResultViewHolder(binding)
    }

    override fun onBindViewHolder(holder: SearchResultViewHolder, position: Int) {
        val movie = movies[position]
        holder.bind(movie)
    }

    override fun getItemCount() = movies.size

    inner class SearchResultViewHolder(
        private val binding: ViewholderSearchItemBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(movie: MovieDto) {
            binding.movieTitle.text = movie.title
            binding.movieOverview.text = movie.overview

            // Отримуємо рік з дати
            movie.release_date?.let {
                try {
                    val inputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                    val date = inputFormat.parse(it)
                    val yearFormat = SimpleDateFormat("yyyy", Locale.getDefault())
                    binding.movieYear.text = date?.let { yearFormat.format(it) }
                } catch (e: Exception) {
                    binding.movieYear.text = it.split("-")[0]
                }
            } ?: run {
                binding.movieYear.text = "N/A"
            }

            // Відображаємо рейтинг
            binding.movieRating.text = movie.vote_average.toString()

            // Завантажуємо постер
            Glide.with(itemView.context)
                .load("https://image.tmdb.org/t/p/w200${movie.poster_path}")
                .centerCrop()
                .into(binding.moviePoster)

            // Клік по елементу
            itemView.setOnClickListener {
                onItemClick(movie)
            }
        }
    }
}