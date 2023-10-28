package com.voitov.pexels_app.data.network.dto.featured_collection

import com.google.gson.annotations.SerializedName

data class FeaturedCollectionsHolder(
    @SerializedName("collections") val collections: List<CollectionDto>,
)