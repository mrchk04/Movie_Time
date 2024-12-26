package com.example.movietime.domain.usecase

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.example.movietime.data.remote.dto.MovieDto
import com.example.movietime.databinding.ViewholderFilmBinding
import com.example.movietime.ui.detail.DetailActivity

class FilmListAdapter(private val movieList: List<MovieDto>) : RecyclerView.Adapter<FilmListAdapter.ViewHolder>() {

    // Створення ViewHolder
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ViewholderFilmBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    // Прив'язка даних до елементів UI
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val movie = movieList[position]

        // Завантажуємо зображення за допомогою Glide
        Glide.with(holder.itemView.context)
            .load("https://image.tmdb.org/t/p/w500${movie.poster_path}") // Повний URL для зображення
            .apply(RequestOptions().transform(CenterCrop(), RoundedCorners(16))) // Заокруглення кутів
            .into(holder.binding.poster)

        // Встановлюємо назву фільму
        holder.binding.titleText.text = movie.title

        // Реакція на клік
        holder.binding.root.setOnClickListener {
            val context = holder.itemView.context
            val intent = Intent(context, DetailActivity::class.java).apply {
                putExtra("MOVIE_ID", movie.id) // Передаємо ID фільму
            }
            context.startActivity(intent)
        }
    }

    // Кількість елементів
    override fun getItemCount(): Int = movieList.size

    // ViewHolder для елементів RecyclerView
    class ViewHolder(val binding: ViewholderFilmBinding) : RecyclerView.ViewHolder(binding.root)
}
