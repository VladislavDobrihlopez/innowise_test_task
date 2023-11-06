package com.voitov.pexels_app.data.repository.model

data class PhotoRequestBatch(
    val query: String,
    val page: Int,
    val pagesPerRequest: Int,
)
