package com.voitov.pexels_app.data.remote.dto.featured_collection

import com.google.gson.annotations.SerializedName

data class FeaturedCollectionsHolder(
    @SerializedName("collections") val collections: List<CollectionDto>,
)