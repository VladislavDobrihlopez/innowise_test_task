package com.voitov.pexels_app.presentation.component

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.shape.CornerBasedShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.tooling.preview.Preview
import coil.compose.SubcomposeAsyncImage
import com.voitov.pexels_app.R
import com.voitov.pexels_app.presentation.ui.theme.DarkGrayDarkShade
import com.voitov.pexels_app.presentation.ui.theme.DarkGrayLightShade
import com.voitov.pexels_app.presentation.ui.theme.Pexels_appTheme

@Composable
fun PhotoCard(
    imageUrl: String,
    modifier: Modifier = Modifier,
    onRenderFailed: (() -> Unit)? = null,
    onLoadingInProgress: (() -> Unit)? = null,
    onClick: (() -> Unit)? = null,
    @DrawableRes placeholder: Int = R.drawable.placeholder,
    placeholderTint: Color = if (isSystemInDarkTheme()) DarkGrayLightShade else DarkGrayDarkShade,
    shape: CornerBasedShape = MaterialTheme.shapes.medium,
    onBeingLoadedColor: Color = MaterialTheme.colorScheme.secondary,
    contentScale: ContentScale = ContentScale.Crop,
    uiHoveredOverPhotoCard: @Composable (BoxScope.() -> Unit)? = null,
) {
    var isAllowedToOutcomeClick by rememberSaveable {
        mutableStateOf(false)
    }

    Box(
        modifier = Modifier
            .clip(shape)
            .clickable(enabled = isAllowedToOutcomeClick, onClick = { onClick?.invoke() })
    ) {
        SubcomposeAsyncImage(
            modifier = modifier,
            model = imageUrl,
            contentScale = contentScale,
            contentDescription = stringResource(R.string.feed_image),
            onSuccess = {
                isAllowedToOutcomeClick = true
            },
            onLoading = {
                onLoadingInProgress?.invoke()
            },
            onError = {
                onRenderFailed?.invoke()
            },
            loading = {
                Placeholder(
                    modifier = Modifier.shimmer(),
                    shape = shape,
                    placeholderTint = placeholderTint,
                    onBeingLoadedColor = onBeingLoadedColor,
                    placeholder = placeholder
                )
            },
            error = {
                Placeholder(
                    modifier = Modifier.shimmer(),
                    shape = shape,
                    placeholderTint = placeholderTint,
                    onBeingLoadedColor = onBeingLoadedColor,
                    placeholder = placeholder
                )
            }
        )
        uiHoveredOverPhotoCard?.invoke(this)
    }
}

@Composable
private fun Placeholder(
    modifier: Modifier = Modifier,
    shape: CornerBasedShape,
    placeholderTint: Color = if (isSystemInDarkTheme()) DarkGrayLightShade else DarkGrayDarkShade,
    onBeingLoadedColor: Color = MaterialTheme.colorScheme.secondary,
    @DrawableRes placeholder: Int = R.drawable.placeholder,
) {
    Box(
        modifier = Modifier
            .clip(shape)
            .background(onBeingLoadedColor)
            .then(modifier),
        contentAlignment = Alignment.Center
    ) {
        Image(
            imageVector = ImageVector.vectorResource(id = placeholder),
            contentDescription = stringResource(id = R.string.download),
            colorFilter = ColorFilter.tint(placeholderTint)
        )
    }
}

@Preview
@Composable
private fun PreviewPhotoCard() {
    Pexels_appTheme {
        PhotoCard(
            imageUrl = "https://www.pexels.com/photo/a-wagon-with-a-sign-that-says-fresh-produce-delivery-18878905/",
            onRenderFailed = {},
            onClick = {})
    }
}