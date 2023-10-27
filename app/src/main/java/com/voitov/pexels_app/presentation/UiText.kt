package com.voitov.pexels_app.presentation

import android.content.Context

sealed class UiText {
    data class Runtime(val text: String) : UiText()
    data class Resource(val resId: Int) : UiText()

    fun getValue(context: Context) =
        when (this) {
            is Resource -> context.getString(this.resId)
            is Runtime -> this.text
        }
}