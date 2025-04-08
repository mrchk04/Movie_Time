package com.example.movietime.di

import android.app.Application
import androidx.room.Room
import com.example.movietime.data.local.movie.MovieDB
import com.example.movietime.data.remote.api.MovieAPI
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideApiService(): MovieAPI {
        return Retrofit.Builder()
            .baseUrl(MovieAPI.BASE_URL)
            .build()
            .create(MovieAPI::class.java)
    }

    @Provides
    @Singleton
    fun provideMovieDatabase(app: Application): MovieDB {
        return Room.databaseBuilder(
            app,
            MovieDB::class.java,
            "movie_db"
        ).build()
    }

}