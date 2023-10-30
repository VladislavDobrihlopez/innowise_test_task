package com.voitov.pexels_app.data.repository

data class RequestBatch(
    val query: String,
    val page: Int,
    val pagesInBatch: Int,
)
