package com.voitov.pexels_app.presentation.component

import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.voitov.pexels_app.presentation.details_screen.composable.NavBackButton
import com.voitov.pexels_app.presentation.ui.theme.Grayish
import com.voitov.pexels_app.presentation.ui.theme.Pexels_appTheme
import com.voitov.pexels_app.presentation.ui.theme.White

@Composable
fun TopBar(
    titleText: String,
    modifier: Modifier = Modifier,
    textStyle: TextStyle = MaterialTheme.typography.titleMedium,
    textColor: Color = if (isSystemInDarkTheme()) White else Grayish,
    startSlot: @Composable (BoxScope.() -> Unit)? = null
) {
    Box(
        modifier = modifier
            .height(40.dp)
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.background)
    ) {
        startSlot?.invoke(this)
        Text(
            modifier = Modifier.align(Alignment.Center),
            maxLines = 1,
            text = titleText,
            style = textStyle,
            color = textColor,
        )
    }
}

@Preview
@Composable
private fun TopBar_light() {
    Pexels_appTheme(darkTheme = false) {
        TopBar(titleText = "Innowise office photo") {
            NavBackButton(
                modifier = Modifier
                    .align(Alignment.CenterStart),
                onClick = {}
            )
        }
    }
}

@Preview
@Composable
private fun TopBar_dark() {
    Pexels_appTheme(darkTheme = true) {
        TopBar(titleText = "Innowise office photo") {
            NavBackButton(
                modifier = Modifier
                    .align(Alignment.CenterStart),
                onClick = {}
            )
        }
    }
}

@Preview
@Composable
private fun TopBar_light_no_button() {
    Pexels_appTheme(darkTheme = false) {
        TopBar(titleText = "Innowise office photo")
    }
}