package com.example.movietime.data.local.movie

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert

@Dao
interface MovieDao {
    @Upsert
    suspend fun upsertAll(movies: List<MovieEntity>)

    @Query("SELECT * FROM MovieEntity WHERE category = :category")
    suspend fun getMovies(category: String): List<MovieEntity>

    @Query("SELECT * FROM MovieEntity WHERE id = :id")
    suspend fun getMoviesById(id: Int): MovieEntity
}