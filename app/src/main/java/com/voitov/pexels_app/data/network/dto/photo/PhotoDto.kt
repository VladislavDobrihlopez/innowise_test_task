package com.voitov.pexels_app.data.network.dto.photo

import com.google.gson.annotations.SerializedName

data class PhotoDto(
    @SerializedName("id") val id: Int,
    @SerializedName("src") val source: PhotoUrlDto,
)
