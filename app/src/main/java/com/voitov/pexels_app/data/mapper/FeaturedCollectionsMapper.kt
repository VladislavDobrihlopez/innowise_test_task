package com.voitov.pexels_app.data.mapper

import com.voitov.pexels_app.data.database.entity.FeaturedCollectionsEntity
import com.voitov.pexels_app.data.network.dto.featured_collection.CollectionDto
import com.voitov.pexels_app.domain.model.FeaturedCollection
import javax.inject.Inject

class FeaturedCollectionsMapper @Inject constructor() {
    fun mapDtoToDomainModel(dto: CollectionDto): FeaturedCollection {
        return FeaturedCollection(id = dto.id, title = dto.title)
    }

    fun mapDbEntitiesToDomainModel(dbEntity: FeaturedCollectionsEntity): FeaturedCollection {
        return FeaturedCollection(id = dbEntity.id, title = dbEntity.title)
    }

    fun mapDtoToDbEntity(dto: CollectionDto): FeaturedCollectionsEntity {
        return FeaturedCollectionsEntity(id = dto.id, title = dto.title)
    }
}