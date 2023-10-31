package com.voitov.pexels_app.presentation.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import com.voitov.pexels_app.presentation.ui.LocalSpacing

@Composable
fun StubNoData(
    actionText: String,
    modifier: Modifier = Modifier,
    actionTextButtonStyle: TextStyle = MaterialTheme.typography.titleMedium,
    actionTextColor: Color = MaterialTheme.colorScheme.primary,
    onTextButtonClick: () -> Unit,
    upperUiItem: @Composable () -> Unit
) {
    val spacing = LocalSpacing.current
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        upperUiItem()
        Spacer(modifier = Modifier.height(spacing.spaceSmall))
        Text(
            modifier = Modifier.clickable(onClick = onTextButtonClick),
            text = actionText,
            style = actionTextButtonStyle,
            color = actionTextColor
        )
    }
}