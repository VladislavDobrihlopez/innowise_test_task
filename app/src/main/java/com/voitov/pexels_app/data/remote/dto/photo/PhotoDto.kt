package com.voitov.pexels_app.data.remote.dto.photo

import com.google.gson.annotations.SerializedName

data class PhotoDto(
    @SerializedName("id") val id: Int,
    @SerializedName("url") val url: String,
)
