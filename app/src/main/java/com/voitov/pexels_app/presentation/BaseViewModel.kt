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
    initialUiState: ScreenState
) : ViewModel() {
    protected val _sideEffect = MutableSharedFlow<SideEffect>(extraBufferCapacity = 64)
    val sideEffect = _sideEffect.asSharedFlow()

    protected val _state =
        MutableStateFlow<ScreenState>(initialUiState)
    open val state = _state.asStateFlow()

    protected fun sendSideEffect(effect: SideEffect) {
        viewModelScope.launch {
            _sideEffect.emit(effect)
        }
    }

    protected fun reduceState(reducer: (ScreenState) -> ScreenState) {
        _state.update(reducer)
    }

    protected fun updateState(newState: ScreenState) = reduceState {
        newState
    }

    abstract fun onEvent(event: UserEvent)
}