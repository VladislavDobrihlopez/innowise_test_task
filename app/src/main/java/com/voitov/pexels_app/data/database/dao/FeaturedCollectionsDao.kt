package com.voitov.pexels_app.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.voitov.pexels_app.data.database.entity.FeaturedCollectionsEntity

@Dao
interface FeaturedCollectionsDao {
    @Query("SELECT * FROM featured_collections")
    suspend fun getAll(): List<FeaturedCollectionsEntity>

    @Insert(entity = FeaturedCollectionsEntity::class, onConflict = OnConflictStrategy.IGNORE)
    suspend fun upsertAll(items: List<FeaturedCollectionsEntity>)

    @Query("DELETE FROM featured_collections")
    suspend fun removeAll()
}