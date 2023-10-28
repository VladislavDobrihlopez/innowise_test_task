package com.voitov.pexels_app.data.network.dto.featured_collection

import com.google.gson.annotations.SerializedName

data class CollectionDto(
    @SerializedName("id") val id: String,
    @SerializedName("title") val title: String,
)