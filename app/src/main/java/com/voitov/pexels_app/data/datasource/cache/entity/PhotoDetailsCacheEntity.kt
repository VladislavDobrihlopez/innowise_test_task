package com.voitov.pexels_app.data.datasource.cache.entity

data class PhotoDetailsCacheEntity(
    val id: Int,
    val query: String,
    val sourceUrl: String,
    val author: String? = null,
    val isBookmarked: Boolean = false,
)