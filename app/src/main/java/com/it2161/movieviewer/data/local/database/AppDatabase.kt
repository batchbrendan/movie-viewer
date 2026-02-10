package com.it2161.movieviewer.data.local.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.it2161.movieviewer.data.local.dao.MovieDao
import com.it2161.movieviewer.data.local.dao.ReviewDao
import com.it2161.movieviewer.data.local.dao.UserDao
import com.it2161.movieviewer.data.local.entities.MovieEntity
import com.it2161.movieviewer.data.local.entities.ReviewEntity
import com.it2161.movieviewer.data.local.entities.UserEntity

@Database(
    entities = [UserEntity::class, MovieEntity::class, ReviewEntity::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun movieDao(): MovieDao
    abstract fun reviewDao(): ReviewDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "movie_viewer_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
