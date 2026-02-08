package com.it2161.movieviewer.ui.screens.detail

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.it2161.movieviewer.ui.components.ErrorMessage
import com.it2161.movieviewer.ui.components.LoadingIndicator
import com.it2161.movieviewer.ui.components.OfflineBanner
import com.it2161.movieviewer.ui.viewmodels.MovieDetailState
import com.it2161.movieviewer.ui.viewmodels.MovieDetailViewModel
import com.it2161.movieviewer.utils.Constants
import com.it2161.movieviewer.utils.DateUtils
import org.json.JSONArray

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MovieDetailScreen(
    viewModel: MovieDetailViewModel,
    movieId: Int,
    onNavigateBack: () -> Unit,
    onNavigateToReviews: (Int) -> Unit,
    isOffline: Boolean
) {
    val movieDetailState by viewModel.movieDetailState.collectAsState()
    val scrollState = rememberScrollState()

    LaunchedEffect(movieId) {
        viewModel.loadMovieDetails(movieId)
    }

    LaunchedEffect(isOffline) {
        viewModel.setOnlineStatus(!isOffline)
    }

    Scaffold(
        topBar = {
            Column {
                TopAppBar(
                    title = { Text("Movie Details") },
                    navigationIcon = {
                        IconButton(onClick = onNavigateBack) {
                            Icon(Icons.Default.ArrowBack, contentDescription = "Back")
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
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when (val state = movieDetailState) {
                is MovieDetailState.Loading -> {
                    LoadingIndicator()
                }
                is MovieDetailState.Success -> {
                    val movie = state.movie
                    val isFavorite = state.isFavorite
                    
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(scrollState)
                    ) {
                        Box {
                            AsyncImage(
                                model = Constants.getImageUrl(
                                    movie.backdropPath, 
                                    Constants.IMAGE_SIZE_W780
                                ),
                                contentDescription = movie.title,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(200.dp),
                                contentScale = ContentScale.Crop
                            )
                        }

                        Column(
                            modifier = Modifier.padding(16.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.Top
                            ) {
                                Row(
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Card(
                                        modifier = Modifier
                                            .width(120.dp)
                                            .height(180.dp),
                                        shape = RoundedCornerShape(8.dp),
                                        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                                    ) {
                                        AsyncImage(
                                            model = Constants.getImageUrl(movie.posterPath),
                                            contentDescription = movie.title,
                                            modifier = Modifier.fillMaxSize(),
                                            contentScale = ContentScale.Crop
                                        )
                                    }
                                    
                                    Spacer(modifier = Modifier.width(16.dp))
                                    
                                    Column(
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        Text(
                                            text = movie.title,
                                            style = MaterialTheme.typography.headlineSmall,
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.onBackground
                                        )
                                        
                                        if (movie.originalTitle != movie.title) {
                                            Spacer(modifier = Modifier.height(4.dp))
                                            Text(
                                                text = movie.originalTitle,
                                                style = MaterialTheme.typography.bodyMedium,
                                                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
                                            )
                                        }
                                        
                                        Spacer(modifier = Modifier.height(8.dp))
                                        
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Text(
                                                text = "⭐ ${String.format("%.1f", movie.voteAverage)}",
                                                style = MaterialTheme.typography.titleMedium,
                                                color = MaterialTheme.colorScheme.onBackground
                                            )
                                            Spacer(modifier = Modifier.width(8.dp))
                                            Text(
                                                text = "(${movie.voteCount} votes)",
                                                style = MaterialTheme.typography.bodySmall,
                                                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
                                            )
                                        }
                                    }
                                }
                                
                                IconButton(
                                    onClick = { viewModel.toggleFavorite(movieId) }
                                ) {
                                    Icon(
                                        imageVector = if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                                        contentDescription = if (isFavorite) "Remove from favorites" else "Add to favorites",
                                        tint = if (isFavorite) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurface
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(16.dp)
                            ) {
                                Column {
                                    Text(
                                        text = "Release Date",
                                        style = MaterialTheme.typography.labelMedium,
                                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
                                    )
                                    Text(
                                        text = DateUtils.formatDate(movie.releaseDate),
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onBackground
                                    )
                                }
                                
                                movie.runtime?.let { runtime ->
                                    Column {
                                        Text(
                                            text = "Runtime",
                                            style = MaterialTheme.typography.labelMedium,
                                            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
                                        )
                                        Text(
                                            text = "${runtime} min",
                                            style = MaterialTheme.typography.bodyMedium,
                                            color = MaterialTheme.colorScheme.onBackground
                                        )
                                    }
                                }
                                
                                Column {
                                    Text(
                                        text = "Language",
                                        style = MaterialTheme.typography.labelMedium,
                                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
                                    )
                                    Text(
                                        text = movie.originalLanguage.uppercase(),
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onBackground
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            // Parse and display genres
                            val genresList = remember(movie.genres) {
                                try {
                                    val genresArray = JSONArray(movie.genres)
                                    val list = mutableListOf<String>()
                                    for (i in 0 until genresArray.length()) {
                                        val genreObj = genresArray.getJSONObject(i)
                                        list.add(genreObj.getString("name"))
                                    }
                                    list
                                } catch (e: Exception) {
                                    emptyList()
                                }
                            }
                            
                            if (genresList.isNotEmpty()) {
                                Text(
                                    text = "Genres",
                                    style = MaterialTheme.typography.labelMedium,
                                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = genresList.joinToString(", "),
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onBackground
                                )
                                Spacer(modifier = Modifier.height(16.dp))
                            }

                            Text(
                                text = "Overview",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onBackground
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = movie.overview,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onBackground
                            )

                            movie.revenue?.let { revenue ->
                                if (revenue > 0) {
                                    Spacer(modifier = Modifier.height(16.dp))
                                    Text(
                                        text = "Revenue",
                                        style = MaterialTheme.typography.labelMedium,
                                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
                                    )
                                    Text(
                                        text = "$${String.format("%,d", revenue)}",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onBackground
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(16.dp))
                            
                            Text(
                                text = "Popularity: ${String.format("%.1f", movie.popularity)}",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
                            )

                            Spacer(modifier = Modifier.height(24.dp))

                            Button(
                                onClick = { onNavigateToReviews(movieId) },
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text("View Reviews")
                            }

                            Spacer(modifier = Modifier.height(16.dp))
                        }
                    }
                }
                is MovieDetailState.Error -> {
                    ErrorMessage(
                        message = state.message,
                        onRetry = { viewModel.loadMovieDetails(movieId) }
                    )
                }
            }
        }
    }
}
