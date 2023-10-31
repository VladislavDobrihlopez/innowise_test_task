package com.voitov.pexels_app.data.repository.helper

data class PhotoRequestBatch(
    val query: String,
    val page: Int,
    val pagesPerRequest: Int,
)
