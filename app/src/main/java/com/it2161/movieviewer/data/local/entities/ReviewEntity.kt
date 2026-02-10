package com.it2161.movieviewer.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "reviews")
data class ReviewEntity(
    @PrimaryKey val id: String,
    val movieId: Int,
    val author: String,
    val content: String,
    val createdAt: String,
    val rating: Double?,
    val avatarPath: String?
)
