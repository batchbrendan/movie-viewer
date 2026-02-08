package com.it2161.movieviewer.ui.screens.detail

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.it2161.movieviewer.ui.components.ErrorMessage
import com.it2161.movieviewer.ui.components.LoadingIndicator
import com.it2161.movieviewer.ui.components.OfflineBanner
import com.it2161.movieviewer.ui.components.ReviewCard
import com.it2161.movieviewer.ui.viewmodels.ReviewState
import com.it2161.movieviewer.ui.viewmodels.ReviewViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReviewsScreen(
    viewModel: ReviewViewModel,
    movieId: Int,
    onNavigateBack: () -> Unit,
    isOffline: Boolean
) {
    val reviewState by viewModel.reviewState.collectAsState()

    LaunchedEffect(movieId) {
        viewModel.loadReviews(movieId)
    }

    LaunchedEffect(isOffline) {
        viewModel.setOnlineStatus(!isOffline)
    }

    Scaffold(
        topBar = {
            Column {
                TopAppBar(
                    title = { Text("Reviews") },
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
            when (val state = reviewState) {
                is ReviewState.Loading -> {
                    LoadingIndicator()
                }
                is ReviewState.Success -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(state.reviews) { review ->
                            ReviewCard(review = review)
                        }
                    }
                }
                is ReviewState.Empty -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.padding(16.dp)
                        ) {
                            Text(
                                text = "No reviews available",
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.onBackground
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Be the first to review this movie!",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
                            )
                        }
                    }
                }
                is ReviewState.Error -> {
                    ErrorMessage(
                        message = state.message,
                        onRetry = { viewModel.loadReviews(movieId) }
                    )
                }
            }
        }
    }
}
