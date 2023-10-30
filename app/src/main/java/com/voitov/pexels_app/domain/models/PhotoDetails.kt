package com.voitov.pexels_app.domain.models

data class PhotoDetails(
    val id: Int,
    val networkUrl: String,
    val localUrl: String? = null,
    val author: String,
)