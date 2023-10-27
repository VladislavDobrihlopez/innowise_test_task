package com.voitov.pexels_app.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

abstract class BaseViewModel<SideEffect, ScreenState, UserEvent>(
    private val initialUiState: ScreenState
) : ViewModel() {
    private val _sideEffect = MutableSharedFlow<SideEffect>(extraBufferCapacity = 64)
    val sideEffect = _sideEffect.asSharedFlow()

    private val _state =
        MutableStateFlow<ScreenState>(initialUiState)
    val state = _state.asStateFlow()

    fun sendSideEffect(effect: SideEffect) {
        viewModelScope.launch {
            _sideEffect.emit(effect)
        }
    }

    fun reduceState(reducer: (ScreenState) -> ScreenState) {
        _state.update(reducer)
    }

    fun updateState(reducer: (ScreenState) -> ScreenState) {
        _state.update(reducer)
    }

    abstract fun onEvent(event: UserEvent)
}