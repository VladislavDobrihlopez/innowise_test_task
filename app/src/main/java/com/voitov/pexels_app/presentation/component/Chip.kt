package com.voitov.pexels_app.presentation.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.voitov.pexels_app.presentation.ui.theme.Pexels_appTheme

@Composable
fun Chip(
    isSelected: Boolean,
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    selectedBackgroundColor: Color = MaterialTheme.colorScheme.primary,
    unselectedBackgroundColor: Color = MaterialTheme.colorScheme.secondary,
    textStyle: TextStyle = MaterialTheme.typography.bodyMedium,
    selectedTextColor: Color = MaterialTheme.colorScheme.onPrimary,
    unselectedTextColor: Color = MaterialTheme.colorScheme.onSecondary,
) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .clip(CircleShape)
            .background(if (isSelected) selectedBackgroundColor else unselectedBackgroundColor)
            .clickable(onClick = onClick)
            .padding(horizontal = 20.dp, vertical = 10.dp)
    ) {

        Text(
            text = text,
            style = textStyle,
            color = if (isSelected) selectedTextColor else unselectedTextColor
        )
    }
}

@Preview
@Composable
private fun PreviewChip_unselected() {
    Pexels_appTheme {
        Chip(
            isSelected = false,
            text = "Cats",
            onClick = {}
        )
    }
}

@Preview
@Composable
private fun PreviewChip_selected() {
    Pexels_appTheme {
        Chip(
            isSelected = true,
            text = "Cats",
            onClick = {}
        )
    }
}