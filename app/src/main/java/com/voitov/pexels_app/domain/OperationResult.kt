package com.voitov.pexels_app.domain

sealed class OperationResult<in T, in E: Throwable> {
    data class Success<T, E: Throwable>(val data: T) : OperationResult<T, E>()
    data class Error<T, E: Throwable>(val throwable: E, val data: T? = null) : OperationResult<T, E>()
}