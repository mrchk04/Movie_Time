package com.example.movietime.data.repository

import com.example.movietime.data.Resource
import com.example.movietime.domain.model.Movie
import kotlinx.coroutines.flow.Flow

interface MovieListRepository {
    suspend fun getMovieList(
        forceFetchFromRemote: Boolean,
        category: String,
        page: Int
        ): Flow<Resource<List<Movie>>>

    suspend fun getMovieById(id: Int): Flow<Resource<Movie>>
}