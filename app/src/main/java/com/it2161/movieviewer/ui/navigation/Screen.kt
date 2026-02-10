package com.it2161.movieviewer.ui.navigation

sealed class Screen(val route: String) {
    object Login : Screen("login")
    object Registration : Screen("registration")
    object MovieList : Screen("movie_list")
    object MovieDetail : Screen("movie_detail/{movieId}") {
        fun createRoute(movieId: Int) = "movie_detail/$movieId"
    }
    object Reviews : Screen("reviews/{movieId}") {
        fun createRoute(movieId: Int) = "reviews/$movieId"
    }
    object Profile : Screen("profile")
    object Favorites : Screen("favorites")
    object Search : Screen("search")
}
