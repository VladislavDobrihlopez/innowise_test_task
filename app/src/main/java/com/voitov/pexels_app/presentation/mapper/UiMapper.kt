package com.voitov.pexels_app.presentation.mapper

import com.voitov.pexels_app.domain.model.FeaturedCollection
import com.voitov.pexels_app.domain.model.Photo
import com.voitov.pexels_app.domain.model.PhotoDetails
import com.voitov.pexels_app.presentation.bookmarks_screen.model.CuratedDetailedUiModel
import com.voitov.pexels_app.presentation.home_screen.model.CuratedUiModel
import com.voitov.pexels_app.presentation.home_screen.model.FeaturedCollectionUiModel
import com.voitov.pexels_app.presentation.utils.getHeightRelatedToId
import javax.inject.Inject

class UiMapper @Inject constructor() {
    fun mapDomainToUiModel(domain: Photo): CuratedUiModel {
        return CuratedUiModel(
            id = domain.id,
            url = domain.url,
            height = getHeightRelatedToId(domain.id)
        )
    }

    fun mapDomainToUiModel(domain: FeaturedCollection): FeaturedCollectionUiModel {
        return FeaturedCollectionUiModel(id = domain.id, title = domain.title)
    }

    fun mapDomainToUiModel(domain: PhotoDetails): CuratedDetailedUiModel {
        return CuratedDetailedUiModel(
            id = domain.id,
            url = domain.sourceUrl,
            author = domain.author,
            height = getHeightRelatedToId(domain.id)
        )
    }
}