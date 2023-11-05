package com.voitov.pexels_app.domain

sealed class PexelsException(message: String): Throwable(message) {
    object NoInternet: PexelsException("Seems like you don't have stable connection, but there is a cache")
    object InternetConnectionFailedAndNoCache: PexelsException("Zero available data")
}