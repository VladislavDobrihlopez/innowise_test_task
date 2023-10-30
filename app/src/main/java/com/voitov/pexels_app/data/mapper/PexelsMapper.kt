package com.voitov.pexels_app.data.mapper

import com.voitov.pexels_app.data.database.PhotoDetailsEntity
import com.voitov.pexels_app.data.network.dto.detailed_photo.PhotoDetailsDto
import com.voitov.pexels_app.data.network.dto.featured_collection.CollectionDto
import com.voitov.pexels_app.data.network.dto.photo.PhotoDto
import com.voitov.pexels_app.domain.models.FeaturedCollection
import com.voitov.pexels_app.domain.models.Photo
import com.voitov.pexels_app.domain.models.PhotoDetails
import javax.inject.Inject

class PexelsMapper @Inject constructor() {
    fun mapDtoToDomainModel(dto: CollectionDto): FeaturedCollection {
        return FeaturedCollection(id = dto.id, title = dto.title)
    }

    fun mapDtoToDomainModel(dto: PhotoDto): Photo {
        return Photo(id = dto.id, url = dto.source.url)
    }

    fun mapDtoToDomainModel(dto: PhotoDetailsDto): PhotoDetails {
        return PhotoDetails(id = dto.id, networkUrl = dto.urlHolder.url, author = dto.authorName)
    }

    fun mapDbEntityToDomainModel(dbEntity: PhotoDetailsEntity): PhotoDetails {
        return PhotoDetails(
            id = dbEntity.id,
            localUrl = dbEntity.localUrl,
            networkUrl = dbEntity.networkUrl,
            author = dbEntity.author
        )
    }
}