package com.it2161.movieviewer.ui.screens.home

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import com.it2161.movieviewer.ui.components.*
import com.it2161.movieviewer.ui.viewmodels.MovieListState
import com.it2161.movieviewer.ui.viewmodels.MovieListViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MovieListScreen(
    viewModel: MovieListViewModel,
    onNavigateToDetail: (Int) -> Unit,
    onNavigateToSearch: () -> Unit,
    onOpenDrawer: () -> Unit,
    isOffline: Boolean
) {
    var selectedTabIndex by remember { mutableStateOf(0) }
    val movieListState by viewModel.movieListState.collectAsState()
    
    val categories = listOf(
        "popular" to "Popular",
        "top_rated" to "Top Rated",
        "now_playing" to "Now Playing",
        "upcoming" to "Upcoming"
    )

    LaunchedEffect(Unit) {
        viewModel.loadMovies(categories[0].first)
    }

    LaunchedEffect(isOffline) {
        viewModel.setOnlineStatus(!isOffline)
    }

    Scaffold(
        topBar = {
            Column {
                TopAppBar(
                    title = { Text("Movie Viewer") },
                    navigationIcon = {
                        IconButton(onClick = onOpenDrawer) {
                            Icon(Icons.Default.Menu, contentDescription = "Menu")
                        }
                    },
                    actions = {
                        IconButton(onClick = onNavigateToSearch) {
                            Icon(Icons.Default.Search, contentDescription = "Search")
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer,
                        titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                )
                OfflineBanner(isOffline = isOffline)
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            TabRow(
                selectedTabIndex = selectedTabIndex,
                containerColor = MaterialTheme.colorScheme.surface
            ) {
                categories.forEachIndexed { index, (_, label) ->
                    Tab(
                        selected = selectedTabIndex == index,
                        onClick = {
                            selectedTabIndex = index
                            viewModel.loadMovies(categories[index].first)
                        },
                        text = { Text(label) }
                    )
                }
            }

            when (val state = movieListState) {
                is MovieListState.Loading -> {
                    LoadingIndicator()
                }
                is MovieListState.Success -> {
                    SwipeRefresh(
                        state = rememberSwipeRefreshState(false),
                        onRefresh = { viewModel.refresh() }
                    ) {
                        LazyVerticalGrid(
                            columns = GridCells.Fixed(2),
                            contentPadding = PaddingValues(8.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.fillMaxSize()
                        ) {
                            items(state.movies) { movie ->
                                MovieCard(
                                    movie = movie,
                                    onClick = { onNavigateToDetail(movie.id) }
                                )
                            }
                        }
                    }
                }
                is MovieListState.Error -> {
                    ErrorMessage(
                        message = state.message,
                        onRetry = { viewModel.refresh() }
                    )
                }
            }
        }
    }
}
