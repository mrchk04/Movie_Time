package com.example.movietime.data.repository

import android.net.http.HttpException
import android.os.Build
import androidx.annotation.RequiresExtension
import com.example.movietime.data.Resource
import com.example.movietime.data.local.movie.MovieDB
import com.example.movietime.data.remote.api.MovieAPI
import com.example.movietime.data.toMovie
import com.example.movietime.data.toMovieEntity
import com.example.movietime.domain.model.Movie
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.io.IOException
import javax.inject.Inject

class MovieListRepositoryImpl @Inject constructor(
    private val api: MovieAPI,
    private val db: MovieDB
) : MovieListRepository {

    @RequiresExtension(extension = Build.VERSION_CODES.S, version = 7)
    override suspend fun getMovieList(
        forceFetchFromRemote: Boolean,
        category: String,
        page: Int,
    ): Flow<Resource<List<Movie>>> {
        return flow {
            emit(Resource.Loading(true))
            val movieList = db.dao.getMovies(category)

            val shouldJustLoadFromCache = !forceFetchFromRemote && movieList.isNotEmpty()
            if (shouldJustLoadFromCache) {
                emit(Resource.Success(
                    data = movieList.map { movieEntity -> movieEntity.toMovie(category) }
                ))
                emit(Resource.Loading(false))
                return@flow
            }

            val movieListFromApi = try {
                api.getMovieList(category, page)
            } catch (e: IOException){
                e.printStackTrace()
                emit(Resource.Error("Couldn't load data"))
                return@flow
            } catch (e: HttpException){
                e.printStackTrace()
                emit(Resource.Error("Couldn't load data"))
                return@flow
            } catch (e: Exception){
                e.printStackTrace()
                emit(Resource.Error("Couldn't load data"))
                return@flow
            }

            val movieEntities = movieListFromApi.results.let {
                it.map { movieDto -> movieDto.toMovieEntity(category) }
            }

            db.dao.upsertAll(movieEntities)

            emit(Resource.Success(
                data = movieEntities.map { movieEntity -> movieEntity.toMovie(category) }
            ))
            emit(Resource.Loading(false))

        }
    }

    override suspend fun getMovieById(id: Int): Flow<Resource<Movie>> {
        return flow{
            emit(Resource.Loading(true))
            val movie = db.dao.getMoviesById(id)

            if (movie != null){
                emit(Resource.Success(
                    data = movie.toMovie(movie.category)
                ))
                emit(Resource.Loading(false))
                return@flow
            }

            emit(Resource.Error("Couldn't load data"))
            emit(Resource.Loading(false))
        }
    }
}
