package com.voitov.pexels_app.data.network.dto.photo

import com.google.gson.annotations.SerializedName

data class PhotoUrlDto(
    @SerializedName("portrait") val url: String
)