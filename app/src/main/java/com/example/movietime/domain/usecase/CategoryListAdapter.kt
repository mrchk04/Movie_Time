package com.example.movietime.domain.usecase

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.movietime.data.remote.dto.GenreDto
import com.example.movietime.databinding.ViewholderCategoryBinding


class CategoryListAdapter(private val genreList: List<GenreDto>) : RecyclerView.Adapter<CategoryListAdapter.ViewHolder>() {

    // Створення ViewHolder
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ViewholderCategoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    // Прив'язка даних до елементів UI
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val genre = genreList[position]

        // Встановлюємо назву жанру
        holder.binding.titleText.text = genre.name

        // Реакція на клік
        holder.binding.root.setOnClickListener {

        }
    }

    // Кількість елементів
    override fun getItemCount(): Int = genreList.size

    // ViewHolder для елементів RecyclerView
    class ViewHolder(val binding: ViewholderCategoryBinding) : RecyclerView.ViewHolder(binding.root)
}
