package com.example.otomaxposphotos.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.otomaxposphotos.model.CartPhotos

@Database(entities = [CartPhotos::class], version = 1, exportSchema = false)
abstract class PhotosDatabase : RoomDatabase() {

    abstract fun photosDao(): PhotosDao

    companion object {
        @Volatile
        private var INSTANCE: PhotosDatabase? = null
        fun getDatabase(context: Context): PhotosDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    PhotosDatabase::class.java,
                    "photos_database"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                return instance
            }
        }
    }
}