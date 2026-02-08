package com.it2161.movieviewer.data.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "favorites")

class FavoritesDataStore(private val context: Context) {
    private val gson = Gson()

    companion object {
        private val FAVORITES_KEY = stringPreferencesKey("favorite_movies")
        private val CURRENT_USER_KEY = stringPreferencesKey("current_user_id")
    }

    val favoritesFlow: Flow<Set<Int>> = context.dataStore.data
        .map { preferences ->
            val json = preferences[FAVORITES_KEY] ?: "[]"
            val type = object : TypeToken<Set<Int>>() {}.type
            gson.fromJson(json, type) ?: emptySet()
        }

    suspend fun addFavorite(movieId: Int) {
        context.dataStore.edit { preferences ->
            val currentJson = preferences[FAVORITES_KEY] ?: "[]"
            val type = object : TypeToken<MutableSet<Int>>() {}.type
            val favorites: MutableSet<Int> = gson.fromJson(currentJson, type) ?: mutableSetOf()
            favorites.add(movieId)
            preferences[FAVORITES_KEY] = gson.toJson(favorites)
        }
    }

    suspend fun removeFavorite(movieId: Int) {
        context.dataStore.edit { preferences ->
            val currentJson = preferences[FAVORITES_KEY] ?: "[]"
            val type = object : TypeToken<MutableSet<Int>>() {}.type
            val favorites: MutableSet<Int> = gson.fromJson(currentJson, type) ?: mutableSetOf()
            favorites.remove(movieId)
            preferences[FAVORITES_KEY] = gson.toJson(favorites)
        }
    }

    suspend fun isFavorite(movieId: Int): Boolean {
        val favorites = favoritesFlow.map { it.contains(movieId) }
        var result = false
        favorites.collect { result = it }
        return result
    }

    val currentUserIdFlow: Flow<String?> = context.dataStore.data
        .map { preferences ->
            preferences[CURRENT_USER_KEY]
        }

    suspend fun setCurrentUserId(userId: String) {
        context.dataStore.edit { preferences ->
            preferences[CURRENT_USER_KEY] = userId
        }
    }

    suspend fun clearCurrentUserId() {
        context.dataStore.edit { preferences ->
            preferences.remove(CURRENT_USER_KEY)
        }
    }
}
