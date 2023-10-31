package com.voitov.pexels_app.data.datasource.cache.entity

data class PhotoDetailsCacheEntity(
    val id: Int,
    val query: String,
    val sourceUrl: String, // coil use it as a key and decides the source of the photo receive
    val author: String? = null,
    val isBookmarked: Boolean = false,
)