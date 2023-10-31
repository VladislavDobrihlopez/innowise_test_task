package com.voitov.pexels_app.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.voitov.pexels_app.data.database.entity.PhotoDetailsEntity

@Dao
interface PhotosDao {
    @Query("SELECT * FROM photo_details")
    suspend fun getAllPhotos(): List<PhotoDetailsEntity>

    @Query("SELECT * FROM photo_details WHERE id=:photoId LIMIT 1")
    suspend fun getPhotoById(photoId: Int): PhotoDetailsEntity

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertAll(items: List<PhotoDetailsEntity>)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun addAll(items: List<PhotoDetailsEntity>)
}