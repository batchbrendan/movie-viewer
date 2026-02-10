package com.it2161.movieviewer.data.repository

import com.it2161.movieviewer.data.local.dao.UserDao
import com.it2161.movieviewer.data.local.entities.UserEntity
import kotlinx.coroutines.flow.Flow

class UserRepository(private val userDao: UserDao) {
    
    suspend fun registerUser(user: UserEntity): Result<Unit> {
        return try {
            val existingUser = userDao.getUserById(user.userId)
            if (existingUser != null) {
                Result.failure(Exception("User ID already exists"))
            } else {
                userDao.insertUser(user)
                Result.success(Unit)
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun login(userId: String, password: String): Result<UserEntity> {
        return try {
            val user = userDao.login(userId, password)
            if (user != null) {
                Result.success(user)
            } else {
                Result.failure(Exception("Invalid credentials"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getUserById(userId: String): UserEntity? {
        return userDao.getUserById(userId)
    }

    fun getUserByIdFlow(userId: String): Flow<UserEntity?> {
        return userDao.getUserByIdFlow(userId)
    }

    suspend fun updateUser(user: UserEntity): Result<Unit> {
        return try {
            userDao.updateUser(user)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
