package com.voitov.pexels_app.data.network.dto.photo

import com.google.gson.annotations.SerializedName

data class PhotosHolder(
    @SerializedName("page") val page: Int,
    @SerializedName("per_page") val perPage: Int,
    @SerializedName("photos") val photos: List<PhotoDto>,
)
