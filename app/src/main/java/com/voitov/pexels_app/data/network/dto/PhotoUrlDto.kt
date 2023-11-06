package com.voitov.pexels_app.data.network.dto

import com.google.gson.annotations.SerializedName

data class PhotoUrlDto(
    @SerializedName("medium") val url: String
)