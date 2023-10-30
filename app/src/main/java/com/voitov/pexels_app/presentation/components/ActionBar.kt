package com.voitov.pexels_app.presentation.components

import androidx.annotation.DrawableRes
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.voitov.pexels_app.R
import com.voitov.pexels_app.presentation.ui.theme.Pexels_appTheme
import com.voitov.pexels_app.presentation.ui.theme.Red
import com.voitov.pexels_app.presentation.ui.theme.White

@Composable
fun ActionBar(
    @DrawableRes icon: Int,
    onIconClick: () -> Unit,
    modifier: Modifier = Modifier,
    backgroundColor: Color = MaterialTheme.colorScheme.secondary,
    iconTintColor: Color = White,
    iconContainerColor: Color = Red,
    shouldShowLabel: Boolean = false,
    labelStyle: TextStyle = MaterialTheme.typography.titleSmall,
    label: String = "",
    labelColor: Color = MaterialTheme.colorScheme.onSecondary,
    iconContentDescription: String = ""
) {
    val backRoundedCornerModifier =
        modifier.clip(
            RoundedCornerShape(
                topStart = CircleShape.topStart,
                bottomStart = CircleShape.bottomStart,
                topEnd = if (shouldShowLabel) MaterialTheme.shapes.large.topEnd else CircleShape.topEnd,
                bottomEnd = if (shouldShowLabel) MaterialTheme.shapes.large.bottomEnd else CircleShape.bottomEnd,
            )
        )

    Row(
        modifier = Modifier
            .height(48.dp)
            .clip(MaterialTheme.shapes.large)
            .then(backRoundedCornerModifier.background(backgroundColor)),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .fillMaxHeight()
                .clip(CircleShape)
                .aspectRatio(1f)
                .background(iconContainerColor)
        ) {
            IconButton(onClick = onIconClick) {
                Icon(
                    imageVector = ImageVector.vectorResource(id = icon),
                    contentDescription = iconContentDescription,
                    tint = iconTintColor,
                )
            }
        }
        if (shouldShowLabel) {
            Text(
                color = labelColor,
                modifier = Modifier.fillMaxWidth(),
                text = label,
                style = labelStyle,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Preview
@Composable
private fun PreviewActionBar_shorten_light() {
    Pexels_appTheme(darkTheme = false) {
        ActionBar(
            icon = R.drawable.download,
            onIconClick = {},
            iconContentDescription = "download"
        )
    }
}

@Preview
@Composable
private fun PreviewActionBar_shorten_dark() {
    Pexels_appTheme(darkTheme = true) {
        ActionBar(
            icon = R.drawable.download,
            onIconClick = {},
            iconContentDescription = "download"
        )
    }
}

@Preview
@Composable
private fun PreviewActionBar_full_light() {
    Pexels_appTheme(darkTheme = false) {
        ActionBar(
            icon = R.drawable.download,
            modifier = Modifier.width(180.dp),
            shouldShowLabel = true,
            label = "Download",
            onIconClick = {}
        )
    }
}

@Preview
@Composable
private fun PreviewActionBar_full_dark() {
    Pexels_appTheme(darkTheme = true) {
        ActionBar(
            icon = R.drawable.download,
            modifier = Modifier.width(180.dp),
            shouldShowLabel = true,
            label = "Download",
            onIconClick = {}
        )
    }
}