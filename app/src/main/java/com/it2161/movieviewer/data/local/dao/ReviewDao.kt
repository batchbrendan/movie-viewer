package com.it2161.movieviewer.data.local.dao

import androidx.room.*
import com.it2161.movieviewer.data.local.entities.ReviewEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ReviewDao {
    @Query("SELECT * FROM reviews WHERE movieId = :movieId")
    fun getReviewsByMovieId(movieId: Int): Flow<List<ReviewEntity>>

    @Query("SELECT * FROM reviews WHERE id = :reviewId")
    suspend fun getReviewById(reviewId: String): ReviewEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReview(review: ReviewEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReviews(reviews: List<ReviewEntity>)

    @Delete
    suspend fun deleteReview(review: ReviewEntity)

    @Query("DELETE FROM reviews WHERE movieId = :movieId")
    suspend fun deleteReviewsByMovieId(movieId: Int)
}
