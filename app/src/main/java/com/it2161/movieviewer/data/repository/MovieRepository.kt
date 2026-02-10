package com.it2161.movieviewer.data.repository

import com.google.gson.Gson
import com.it2161.movieviewer.data.local.dao.MovieDao
import com.it2161.movieviewer.data.local.entities.MovieEntity
import com.it2161.movieviewer.data.remote.api.TMDBApiService
import com.it2161.movieviewer.data.remote.models.Movie
import com.it2161.movieviewer.data.remote.models.MovieDetail
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class MovieRepository(
    private val apiService: TMDBApiService,
    private val movieDao: MovieDao
) {
    private val gson = Gson()

    suspend fun getPopularMovies(forceRefresh: Boolean = false): Flow<Result<List<MovieEntity>>> = flow {
        try {
            if (forceRefresh) {
                val response = apiService.getPopularMovies()
                val entities = response.results.map { it.toEntity("popular") }
                movieDao.insertMovies(entities)
            }
            movieDao.getMoviesByCategory("popular").collect { movies ->
                emit(Result.success(movies))
            }
        } catch (e: Exception) {
            // Try to get from local database
            movieDao.getMoviesByCategory("popular").collect { movies ->
                if (movies.isNotEmpty()) {
                    emit(Result.success(movies))
                } else {
                    emit(Result.failure(e))
                }
            }
        }
    }

    suspend fun getTopRatedMovies(forceRefresh: Boolean = false): Flow<Result<List<MovieEntity>>> = flow {
        try {
            if (forceRefresh) {
                val response = apiService.getTopRatedMovies()
                val entities = response.results.map { it.toEntity("top_rated") }
                movieDao.insertMovies(entities)
            }
            movieDao.getMoviesByCategory("top_rated").collect { movies ->
                emit(Result.success(movies))
            }
        } catch (e: Exception) {
            movieDao.getMoviesByCategory("top_rated").collect { movies ->
                if (movies.isNotEmpty()) {
                    emit(Result.success(movies))
                } else {
                    emit(Result.failure(e))
                }
            }
        }
    }

    suspend fun getNowPlayingMovies(forceRefresh: Boolean = false): Flow<Result<List<MovieEntity>>> = flow {
        try {
            if (forceRefresh) {
                val response = apiService.getNowPlayingMovies()
                val entities = response.results.map { it.toEntity("now_playing") }
                movieDao.insertMovies(entities)
            }
            movieDao.getMoviesByCategory("now_playing").collect { movies ->
                emit(Result.success(movies))
            }
        } catch (e: Exception) {
            movieDao.getMoviesByCategory("now_playing").collect { movies ->
                if (movies.isNotEmpty()) {
                    emit(Result.success(movies))
                } else {
                    emit(Result.failure(e))
                }
            }
        }
    }

    suspend fun getUpcomingMovies(forceRefresh: Boolean = false): Flow<Result<List<MovieEntity>>> = flow {
        try {
            if (forceRefresh) {
                val response = apiService.getUpcomingMovies()
                val entities = response.results.map { it.toEntity("upcoming") }
                movieDao.insertMovies(entities)
            }
            movieDao.getMoviesByCategory("upcoming").collect { movies ->
                emit(Result.success(movies))
            }
        } catch (e: Exception) {
            movieDao.getMoviesByCategory("upcoming").collect { movies ->
                if (movies.isNotEmpty()) {
                    emit(Result.success(movies))
                } else {
                    emit(Result.failure(e))
                }
            }
        }
    }

    suspend fun getMovieDetails(movieId: Int, forceRefresh: Boolean = false): Result<MovieEntity> {
        return try {
            if (forceRefresh) {
                val response = apiService.getMovieDetails(movieId)
                val entity = response.toEntity()
                movieDao.insertMovie(entity)
                Result.success(entity)
            } else {
                val cachedMovie = movieDao.getMovieById(movieId)
                if (cachedMovie != null && cachedMovie.runtime != null) {
                    Result.success(cachedMovie)
                } else {
                    val response = apiService.getMovieDetails(movieId)
                    val entity = response.toEntity()
                    movieDao.insertMovie(entity)
                    Result.success(entity)
                }
            }
        } catch (e: Exception) {
            val cachedMovie = movieDao.getMovieById(movieId)
            if (cachedMovie != null) {
                Result.success(cachedMovie)
            } else {
                Result.failure(e)
            }
        }
    }

    suspend fun searchMovies(query: String): Flow<Result<List<MovieEntity>>> = flow {
        try {
            val response = apiService.searchMovies(query)
            val entities = response.results.map { it.toEntity("search") }
            movieDao.insertMovies(entities)
            emit(Result.success(entities))
        } catch (e: Exception) {
            movieDao.searchMovies(query).collect { movies ->
                emit(Result.success(movies))
            }
        }
    }

    fun getMoviesByIds(ids: List<Int>): Flow<List<MovieEntity>> {
        return movieDao.getMoviesByIds(ids)
    }

    private fun Movie.toEntity(category: String): MovieEntity {
        return MovieEntity(
            id = id,
            title = title,
            originalTitle = originalTitle,
            overview = overview,
            posterPath = posterPath,
            backdropPath = backdropPath,
            releaseDate = releaseDate,
            voteAverage = voteAverage,
            voteCount = voteCount,
            adult = adult,
            originalLanguage = originalLanguage,
            popularity = popularity,
            genres = "[]",
            runtime = null,
            revenue = null,
            category = category
        )
    }

    private fun MovieDetail.toEntity(): MovieEntity {
        return MovieEntity(
            id = id,
            title = title,
            originalTitle = originalTitle,
            overview = overview,
            posterPath = posterPath,
            backdropPath = backdropPath,
            releaseDate = releaseDate,
            voteAverage = voteAverage,
            voteCount = voteCount,
            adult = adult,
            originalLanguage = originalLanguage,
            popularity = popularity,
            genres = gson.toJson(genres),
            runtime = runtime,
            revenue = revenue,
            category = "detail"
        )
    }
}
