package com.it2161.movieviewer

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.it2161.movieviewer.data.datastore.FavoritesDataStore
import com.it2161.movieviewer.data.local.database.AppDatabase
import com.it2161.movieviewer.ui.navigation.MovieViewerApp
import com.it2161.movieviewer.ui.theme.MovieViewerTheme
import com.it2161.movieviewer.utils.NetworkConnectivityObserver

class MainActivity : ComponentActivity() {
    private lateinit var database: AppDatabase
    private lateinit var favoritesDataStore: FavoritesDataStore
    private lateinit var networkConnectivityObserver: NetworkConnectivityObserver

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize database and data store
        database = AppDatabase.getDatabase(applicationContext)
        favoritesDataStore = FavoritesDataStore(applicationContext)
        networkConnectivityObserver = NetworkConnectivityObserver(applicationContext)

        setContent {
            MovieViewerTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    MovieViewerApp(
                        database = database,
                        favoritesDataStore = favoritesDataStore,
                        networkConnectivityObserver = networkConnectivityObserver
                    )
                }
            }
        }
    }
}
