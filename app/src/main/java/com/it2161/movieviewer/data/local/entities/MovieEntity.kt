package com.it2161.movieviewer.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "movies")
data class MovieEntity(
    @PrimaryKey val id: Int,
    val title: String,
    val originalTitle: String,
    val overview: String,
    val posterPath: String?,
    val backdropPath: String?,
    val releaseDate: String,
    val voteAverage: Double,
    val voteCount: Int,
    val adult: Boolean,
    val originalLanguage: String,
    val popularity: Double,
    val genres: String, // JSON string of genre list
    val runtime: Int?,
    val revenue: Long?,
    val category: String // "popular", "top_rated", "now_playing", "upcoming", "search"
)
