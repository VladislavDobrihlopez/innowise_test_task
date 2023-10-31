package com.voitov.pexels_app.data.mapper

import com.voitov.pexels_app.data.database.entity.PhotoDetailsEntity
import com.voitov.pexels_app.data.network.dto.detailed_photo.PhotoDetailsDto
import com.voitov.pexels_app.data.network.dto.photo.PhotoDto
import com.voitov.pexels_app.domain.model.Photo
import com.voitov.pexels_app.domain.model.PhotoDetails
import javax.inject.Inject

class PhotosMapper @Inject constructor() {
    fun mapDtoToDomainModel(dto: PhotoDto): Photo {
        return Photo(id = dto.id, url = dto.source.url)
    }

    fun mapDtoToDomainModel(dto: PhotoDetailsDto): PhotoDetails {
        return PhotoDetails(
            id = dto.id,
            sourceUrl = dto.urlHolder.url,
            author = dto.authorName,
            isBookmarked = false
        )
    }


    fun mapDtoToEntity(dto: PhotoDto, query: String): PhotoDetailsEntity {
        return PhotoDetailsEntity(
            id = dto.id,
            sourceUrl = dto.source.url,
            date = System.currentTimeMillis(),
            query = query
        )
    }

    fun mapDbEntityToDomainModel(dbEntity: PhotoDetailsEntity): PhotoDetails {
        return PhotoDetails(
            id = dbEntity.id,
            sourceUrl = dbEntity.sourceUrl,
            author = dbEntity.author ?: "",
            isBookmarked = dbEntity.isBookmarked
        )
    }
}