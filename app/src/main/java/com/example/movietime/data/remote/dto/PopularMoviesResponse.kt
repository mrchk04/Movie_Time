package com.example.movietime.data.remote.dto

import com.google.gson.annotations.SerializedName

data class PopularMoviesResponse(
    val results: List<Movie>
)

data class Movie(
    @SerializedName("backdrop_path") val backdropPath: String,
    val id: Int
)
