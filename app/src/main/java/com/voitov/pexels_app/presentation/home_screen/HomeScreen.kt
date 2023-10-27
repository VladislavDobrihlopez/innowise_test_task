package com.voitov.pexels_app.presentation.home_screen

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
fun HomeScreen(paddingValues: PaddingValues) {
    val viewModel: HomeViewModel = hiltViewModel()
    val scope = rememberCoroutineScope()

    val screenState by viewModel.state.collectAsStateWithLifecycle()
    HomeContent(paddingValues, screenState)
}