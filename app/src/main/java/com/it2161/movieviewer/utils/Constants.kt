package com.it2161.movieviewer.utils

object Constants {
    const val BASE_URL = "https://api.themoviedb.org/3/"
    const val IMAGE_BASE_URL = "https://image.tmdb.org/t/p/"
    const val IMAGE_SIZE_W500 = "w500"
    const val IMAGE_SIZE_W780 = "w780"
    const val IMAGE_SIZE_ORIGINAL = "original"
    
    const val CATEGORY_POPULAR = "popular"
    const val CATEGORY_TOP_RATED = "top_rated"
    const val CATEGORY_NOW_PLAYING = "now_playing"
    const val CATEGORY_UPCOMING = "upcoming"
    const val CATEGORY_SEARCH = "search"
    
    fun getImageUrl(path: String?, size: String = IMAGE_SIZE_W500): String {
        return if (path != null) {
            "$IMAGE_BASE_URL$size$path"
        } else {
            ""
        }
    }
}
