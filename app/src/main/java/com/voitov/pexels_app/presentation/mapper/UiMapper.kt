package com.voitov.pexels_app.presentation.mapper

import com.voitov.pexels_app.domain.models.FeaturedCollection
import com.voitov.pexels_app.domain.models.Photo
import com.voitov.pexels_app.presentation.CuratedUiModel
import com.voitov.pexels_app.presentation.home_screen.models.FeaturedCollectionUiModel
import javax.inject.Inject

class UiMapper @Inject constructor() {
    fun mapDomainToUiModel(domain: Photo, minHeightInDp: Int, maxHeightInDp: Int): CuratedUiModel {
        return CuratedUiModel(
            id = domain.id,
            url = domain.url,
            height = CuratedUiModel.getHeightInRange(minHeightInDp, maxHeightInDp)
        )
    }
    fun mapDomainToUiModel(domain: FeaturedCollection): FeaturedCollectionUiModel {
        return FeaturedCollectionUiModel(id = domain.id, title = domain.title)
    }
}