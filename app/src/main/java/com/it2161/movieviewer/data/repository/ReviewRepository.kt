package com.it2161.movieviewer.data.repository

import com.it2161.movieviewer.data.local.dao.ReviewDao
import com.it2161.movieviewer.data.local.entities.ReviewEntity
import com.it2161.movieviewer.data.remote.api.TMDBApiService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class ReviewRepository(
    private val apiService: TMDBApiService,
    private val reviewDao: ReviewDao
) {
    suspend fun getMovieReviews(movieId: Int, forceRefresh: Boolean = false): Flow<Result<List<ReviewEntity>>> = flow {
        try {
            if (forceRefresh) {
                val response = apiService.getMovieReviews(movieId)
                val entities = response.results.map { review ->
                    ReviewEntity(
                        id = review.id,
                        movieId = movieId,
                        author = review.author,
                        content = review.content,
                        createdAt = review.createdAt,
                        rating = review.authorDetails?.rating,
                        avatarPath = review.authorDetails?.avatarPath
                    )
                }
                reviewDao.insertReviews(entities)
            }
            reviewDao.getReviewsByMovieId(movieId).collect { reviews ->
                emit(Result.success(reviews))
            }
        } catch (e: Exception) {
            reviewDao.getReviewsByMovieId(movieId).collect { reviews ->
                if (reviews.isNotEmpty()) {
                    emit(Result.success(reviews))
                } else {
                    emit(Result.failure(e))
                }
            }
        }
    }
}
