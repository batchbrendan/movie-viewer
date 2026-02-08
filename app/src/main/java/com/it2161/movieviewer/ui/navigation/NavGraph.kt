package com.it2161.movieviewer.ui.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.it2161.movieviewer.data.datastore.FavoritesDataStore
import com.it2161.movieviewer.data.local.database.AppDatabase
import com.it2161.movieviewer.data.remote.api.TMDBApiService
import com.it2161.movieviewer.data.remote.interceptors.AuthInterceptor
import com.it2161.movieviewer.data.repository.*
import com.it2161.movieviewer.ui.components.OfflineBanner
import com.it2161.movieviewer.ui.screens.auth.LoginScreen
import com.it2161.movieviewer.ui.screens.auth.RegistrationScreen
import com.it2161.movieviewer.ui.screens.detail.MovieDetailScreen
import com.it2161.movieviewer.ui.screens.detail.ReviewsScreen
import com.it2161.movieviewer.ui.screens.favorites.FavoritesScreen
import com.it2161.movieviewer.ui.screens.home.MovieListScreen
import com.it2161.movieviewer.ui.screens.profile.ProfileScreen
import com.it2161.movieviewer.ui.screens.search.SearchScreen
import com.it2161.movieviewer.ui.viewmodels.*
import com.it2161.movieviewer.utils.NetworkConnectivityObserver
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

@Composable
fun MovieViewerApp(
    database: AppDatabase,
    favoritesDataStore: FavoritesDataStore,
    networkConnectivityObserver: NetworkConnectivityObserver
) {
    val navController = rememberNavController()
    val isOnline by networkConnectivityObserver.observeConnectivity().collectAsState(initial = true)

    // Create API service
    val apiService = remember {
        val okHttpClient = OkHttpClient.Builder()
            .addInterceptor(AuthInterceptor())
            .build()

        Retrofit.Builder()
            .baseUrl("https://api.themoviedb.org/3/")
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(TMDBApiService::class.java)
    }

    // Create repositories
    val userRepository = remember { UserRepository(database.userDao()) }
    val movieRepository = remember { MovieRepository(apiService, database.movieDao()) }
    val reviewRepository = remember { ReviewRepository(apiService, database.reviewDao()) }
    val favoriteRepository = remember { FavoriteRepository(favoritesDataStore, database.movieDao()) }

    // Create ViewModels
    val authViewModel = remember { AuthViewModel(userRepository, favoritesDataStore) }
    val movieListViewModel = remember { MovieListViewModel(movieRepository) }
    val movieDetailViewModel = remember { MovieDetailViewModel(movieRepository, favoritesDataStore) }
    val profileViewModel = remember { ProfileViewModel(userRepository) }
    val favoriteViewModel = remember { FavoriteViewModel(favoriteRepository) }
    val searchViewModel = remember { SearchViewModel(movieRepository) }
    val reviewViewModel = remember { ReviewViewModel(reviewRepository) }

    // Update online status in ViewModels
    LaunchedEffect(isOnline) {
        movieListViewModel.setOnlineStatus(isOnline)
        movieDetailViewModel.setOnlineStatus(isOnline)
        reviewViewModel.setOnlineStatus(isOnline)
    }

    val currentUserId by authViewModel.currentUserId.collectAsState()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    // Determine if we should show bottom navigation
    val showBottomBar = currentUserId != null && currentRoute !in listOf(
        Screen.Login.route,
        Screen.Registration.route,
        "movie_detail/{movieId}",
        "reviews/{movieId}"
    )

    Scaffold(
        topBar = {
            if (showBottomBar || currentRoute in listOf("movie_detail/{movieId}", "reviews/{movieId}")) {
                OfflineBanner(isOffline = !isOnline)
            }
        },
        bottomBar = {
            if (showBottomBar) {
                BottomNavigationBar(navController = navController, currentRoute = currentRoute)
            }
        }
    ) { paddingValues ->
        NavHost(
            navController = navController,
            startDestination = if (currentUserId != null) Screen.MovieList.route else Screen.Login.route,
            modifier = Modifier.padding(paddingValues)
        ) {
            composable(Screen.Login.route) {
                LoginScreen(
                    viewModel = authViewModel,
                    onNavigateToRegister = {
                        navController.navigate(Screen.Registration.route)
                    },
                    onLoginSuccess = {
                        navController.navigate(Screen.MovieList.route) {
                            popUpTo(Screen.Login.route) { inclusive = true }
                        }
                    }
                )
            }

            composable(Screen.Registration.route) {
                RegistrationScreen(
                    viewModel = authViewModel,
                    onNavigateToLogin = { navController.popBackStack() },
                    onRegistrationSuccess = {
                        navController.navigate(Screen.MovieList.route) {
                            popUpTo(Screen.Login.route) { inclusive = true }
                        }
                    }
                )
            }

            composable(Screen.MovieList.route) {
                MovieListScreen(
                    viewModel = movieListViewModel,
                    isOffline = !isOnline,
                    onNavigateToDetail = { movieId ->
                        navController.navigate(Screen.MovieDetail.createRoute(movieId))
                    },
                    onNavigateToSearch = {
                        navController.navigate(Screen.Search.route)
                    },
                    onOpenDrawer = { }
                )
            }

            composable(
                route = Screen.MovieDetail.route,
                arguments = listOf(navArgument("movieId") { type = NavType.IntType })
            ) { backStackEntry ->
                val movieId = backStackEntry.arguments?.getInt("movieId") ?: 0
                MovieDetailScreen(
                    movieId = movieId,
                    viewModel = movieDetailViewModel,
                    isOffline = !isOnline,
                    onNavigateBack = { navController.popBackStack() },
                    onNavigateToReviews = { id ->
                        navController.navigate(Screen.Reviews.createRoute(id))
                    }
                )
            }

            composable(
                route = Screen.Reviews.route,
                arguments = listOf(navArgument("movieId") { type = NavType.IntType })
            ) { backStackEntry ->
                val movieId = backStackEntry.arguments?.getInt("movieId") ?: 0
                ReviewsScreen(
                    movieId = movieId,
                    viewModel = reviewViewModel,
                    isOffline = !isOnline,
                    onNavigateBack = { navController.popBackStack() }
                )
            }

            composable(Screen.Profile.route) {
                currentUserId?.let { userId ->
                    ProfileScreen(
                        userId = userId,
                        viewModel = profileViewModel,
                        onLogout = {
                            authViewModel.logout()
                            navController.navigate(Screen.Login.route) {
                                popUpTo(0) { inclusive = true }
                            }
                        }
                    )
                }
            }

            composable(Screen.Favorites.route) {
                FavoritesScreen(
                    viewModel = favoriteViewModel,
                    onNavigateToDetail = { movieId ->
                        navController.navigate(Screen.MovieDetail.createRoute(movieId))
                    },
                    onOpenDrawer = { }
                )
            }

            composable(Screen.Search.route) {
                SearchScreen(
                    viewModel = searchViewModel,
                    onNavigateToDetail = { movieId ->
                        navController.navigate(Screen.MovieDetail.createRoute(movieId))
                    },
                    onNavigateBack = { navController.popBackStack() }
                )
            }
        }
    }
}

@Composable
fun BottomNavigationBar(
    navController: NavHostController,
    currentRoute: String?
) {
    NavigationBar {
        NavigationBarItem(
            icon = { Icon(Icons.Default.Home, contentDescription = "Home") },
            label = { Text("Home") },
            selected = currentRoute == Screen.MovieList.route,
            onClick = {
                navController.navigate(Screen.MovieList.route) {
                    popUpTo(navController.graph.findStartDestination().id) {
                        saveState = true
                    }
                    launchSingleTop = true
                    restoreState = true
                }
            }
        )
        NavigationBarItem(
            icon = { Icon(Icons.Default.Search, contentDescription = "Search") },
            label = { Text("Search") },
            selected = currentRoute == Screen.Search.route,
            onClick = {
                navController.navigate(Screen.Search.route) {
                    popUpTo(navController.graph.findStartDestination().id) {
                        saveState = true
                    }
                    launchSingleTop = true
                    restoreState = true
                }
            }
        )
        NavigationBarItem(
            icon = { Icon(Icons.Default.Favorite, contentDescription = "Favorites") },
            label = { Text("Favorites") },
            selected = currentRoute == Screen.Favorites.route,
            onClick = {
                navController.navigate(Screen.Favorites.route) {
                    popUpTo(navController.graph.findStartDestination().id) {
                        saveState = true
                    }
                    launchSingleTop = true
                    restoreState = true
                }
            }
        )
        NavigationBarItem(
            icon = { Icon(Icons.Default.Person, contentDescription = "Profile") },
            label = { Text("Profile") },
            selected = currentRoute == Screen.Profile.route,
            onClick = {
                navController.navigate(Screen.Profile.route) {
                    popUpTo(navController.graph.findStartDestination().id) {
                        saveState = true
                    }
                    launchSingleTop = true
                    restoreState = true
                }
            }
        )
    }
}
