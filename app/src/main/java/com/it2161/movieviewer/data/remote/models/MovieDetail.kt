package com.it2161.movieviewer.data.remote.models

import com.google.gson.annotations.SerializedName

data class MovieDetail(
    @SerializedName("id") val id: Int,
    @SerializedName("title") val title: String,
    @SerializedName("original_title") val originalTitle: String,
    @SerializedName("overview") val overview: String,
    @SerializedName("poster_path") val posterPath: String?,
    @SerializedName("backdrop_path") val backdropPath: String?,
    @SerializedName("release_date") val releaseDate: String,
    @SerializedName("vote_average") val voteAverage: Double,
    @SerializedName("vote_count") val voteCount: Int,
    @SerializedName("adult") val adult: Boolean,
    @SerializedName("original_language") val originalLanguage: String,
    @SerializedName("popularity") val popularity: Double,
    @SerializedName("genres") val genres: List<Genre>,
    @SerializedName("runtime") val runtime: Int?,
    @SerializedName("revenue") val revenue: Long?
)
