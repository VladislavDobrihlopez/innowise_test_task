package com.voitov.pexels_app.domain

sealed class PexelsException(message: String): Throwable(message) {
    object UnexpectedError: PexelsException("It shouldn't have happened")
    object NoInternet: PexelsException("Seems like you don't have stable connection")
}