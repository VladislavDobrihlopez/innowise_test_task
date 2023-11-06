package com.voitov.pexels_app.data.network.dto.detailed_photo

import com.google.gson.annotations.SerializedName
import com.voitov.pexels_app.data.network.dto.PhotoUrlDto

data class PhotoDetailsDto(
    @SerializedName("id") val id: Int,
    @SerializedName("photographer") val authorName: String,
    @SerializedName("src") val urlHolder: PhotoUrlDto,
)