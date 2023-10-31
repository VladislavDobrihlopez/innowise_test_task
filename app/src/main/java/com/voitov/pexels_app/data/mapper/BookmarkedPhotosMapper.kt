package com.voitov.pexels_app.data.mapper

import com.voitov.pexels_app.data.database.entity.PhotoDetailsEntity
import com.voitov.pexels_app.domain.model.PhotoDetails
import javax.inject.Inject

class BookmarkedPhotosMapper @Inject constructor() {
    fun mapDomainToDbEntity(domain: PhotoDetails, query: String): PhotoDetailsEntity {
        return PhotoDetailsEntity(
            id = domain.id,
            sourceUrl = domain.sourceUrl,
            author = domain.author,
            query = query,
            date = System.currentTimeMillis(),
            isBookmarked = domain.isBookmarked
        )
    }

    fun mapDbEntityToDomain(dbEntity: PhotoDetailsEntity): PhotoDetails {
        return PhotoDetails(
            id = dbEntity.id,
            sourceUrl = dbEntity.sourceUrl,
            author =
            requireNotNull(dbEntity.author),
            isBookmarked = dbEntity.isBookmarked
        )
    }
}