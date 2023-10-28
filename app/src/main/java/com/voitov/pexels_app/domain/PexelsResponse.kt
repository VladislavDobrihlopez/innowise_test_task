package com.voitov.pexels_app.domain

sealed class PexelsResponse {
    object ClientError: PexelsResponse()
    object ServerError: PexelsResponse()
    data class Success<T>(val data: T): PexelsResponse()
}