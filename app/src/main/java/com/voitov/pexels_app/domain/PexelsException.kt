package com.voitov.pexels_app.domain

sealed class PexelsException(message: String): Throwable(message) {
//    object NoCachedData: PexelsException("It shouldn't have happened")
    object NoInternet: PexelsException("Seems like you don't have stable connection, but you've got cache")
    object InternetConnectionFailedAndNoCache: PexelsException("Everything is bad")
}