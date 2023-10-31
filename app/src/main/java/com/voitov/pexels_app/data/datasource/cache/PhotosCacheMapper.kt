package com.voitov.pexels_app.data.datasource.cache

import com.voitov.pexels_app.data.database.entity.PhotoDetailsEntity
import com.voitov.pexels_app.data.datasource.cache.entity.PhotoDetailsCacheEntity
import com.voitov.pexels_app.domain.model.Photo
import com.voitov.pexels_app.domain.model.PhotoDetails
import javax.inject.Inject

class PhotosCacheMapper @Inject constructor() {
    fun mapDbEntityToCacheEntity(
        dbEntity: PhotoDetailsEntity,
    ): PhotoDetailsCacheEntity {
        return PhotoDetailsCacheEntity(
            id = dbEntity.id,
            query = dbEntity.query,
            sourceUrl = dbEntity.sourceUrl,
            author = dbEntity.author,
            isBookmarked = dbEntity.isBookmarked,
        )
    }

    fun mapCacheEntityToDomainModel(cacheEntity: PhotoDetailsCacheEntity): Photo {
        return Photo(id = cacheEntity.id, url = cacheEntity.sourceUrl)
    }

    fun mapCacheEntityToDomainModelDetails(cacheEntity: PhotoDetailsCacheEntity): PhotoDetails {
        return PhotoDetails(
            id = cacheEntity.id,
            sourceUrl = cacheEntity.sourceUrl,
            author = cacheEntity.author ?: ""
        )
    }
}