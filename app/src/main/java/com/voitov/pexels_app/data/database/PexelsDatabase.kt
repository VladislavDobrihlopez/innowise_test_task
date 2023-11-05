package com.voitov.pexels_app.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.voitov.pexels_app.data.database.dao.FeaturedCollectionsDao
import com.voitov.pexels_app.data.database.dao.PhotosDao
import com.voitov.pexels_app.data.database.dao.UserPhotosDao
import com.voitov.pexels_app.data.database.entity.FeaturedCollectionsEntity
import com.voitov.pexels_app.data.database.entity.PhotoDetailsEntity

@Database(
    entities = [PhotoDetailsEntity::class, FeaturedCollectionsEntity::class],
    version = 1,
    exportSchema = false
)
abstract class PexelsDatabase : RoomDatabase() {
    companion object {
        private var instance: PexelsDatabase? = null
        private const val DB_NAME = "pexels_app.db"

        @Synchronized
        fun getInstance(context: Context): PexelsDatabase {
            instance?.let {
                return it
            }
            synchronized(this) {
                val db = Room.databaseBuilder(context, PexelsDatabase::class.java, DB_NAME)
                    .build()
                instance = db
                return db
            }
        }
    }

    abstract fun getPhotoDao(): PhotosDao
    abstract fun getUserPhotoDao(): UserPhotosDao
    abstract fun getFeaturedCollectionsDao(): FeaturedCollectionsDao
}