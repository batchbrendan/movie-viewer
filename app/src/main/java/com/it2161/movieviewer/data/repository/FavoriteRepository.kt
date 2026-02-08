package com.it2161.movieviewer.data.repository

import com.it2161.movieviewer.data.datastore.FavoritesDataStore
import com.it2161.movieviewer.data.local.dao.MovieDao
import com.it2161.movieviewer.data.local.entities.MovieEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class FavoriteRepository(
    private val favoritesDataStore: FavoritesDataStore,
    private val movieDao: MovieDao
) {
    fun getFavoriteMovies(): Flow<List<MovieEntity>> {
        return favoritesDataStore.favoritesFlow.map { favoriteIds ->
            if (favoriteIds.isEmpty()) {
                emptyList()
            } else {
                movieDao.getMoviesByIds(favoriteIds.toList()).let { flow ->
                    var result = emptyList<MovieEntity>()
                    flow.collect { result = it }
                    result
                }
            }
        }
    }

    fun getFavoriteIds(): Flow<Set<Int>> {
        return favoritesDataStore.favoritesFlow
    }

    suspend fun addFavorite(movieId: Int) {
        favoritesDataStore.addFavorite(movieId)
    }

    suspend fun removeFavorite(movieId: Int) {
        favoritesDataStore.removeFavorite(movieId)
    }

    suspend fun isFavorite(movieId: Int): Boolean {
        return favoritesDataStore.isFavorite(movieId)
    }
}
