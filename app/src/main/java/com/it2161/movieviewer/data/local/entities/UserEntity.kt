package com.it2161.movieviewer.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey val userId: String,
    val dateOfBirth: String,
    val password: String,
    val preferredName: String,
    val profilePicturePath: String?
)
