package com.voitov.pexels_app.domain

sealed class OperationResult<out T> {
    class Success<T>(val data: T) : OperationResult<T>()
    class Error(val messageText: String) : OperationResult<Nothing>()
}