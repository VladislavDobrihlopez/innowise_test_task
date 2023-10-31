package com.voitov.pexels_app.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.voitov.pexels_app.data.database.entity.PhotoDetailsEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface UserPhotosDao {
    @Query("SELECT * FROM photo_details WHERE is_bookmarked = 1")
    fun getAllBookmarkedPhotos(): Flow<List<PhotoDetailsEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPhoto(item: PhotoDetailsEntity)

    @Query("DELETE FROM photo_details WHERE id=:id")
    suspend fun deletePhotoById(id: Int)

    @Query("SELECT EXISTS(SELECT * FROM photo_details WHERE id = :id AND is_bookmarked=1)")
    suspend fun isItemExists(id : Int) : Boolean
}