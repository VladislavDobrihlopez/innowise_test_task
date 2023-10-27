package com.voitov.pexels_app.presentation.home_screen.models

data class FeaturedCollectionUiModel(
    val id: String,
    val title: String,
    val isSelected: Boolean = false
)