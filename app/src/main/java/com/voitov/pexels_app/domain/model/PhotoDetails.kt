package com.voitov.pexels_app.domain.model

data class PhotoDetails(
    val id: Int,
    val sourceUrl: String,
    val author: String,
    val isBookmarked: Boolean
)