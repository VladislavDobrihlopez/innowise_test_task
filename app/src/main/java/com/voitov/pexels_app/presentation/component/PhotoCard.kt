package com.voitov.pexels_app.presentation.component

import android.util.Log
import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.shape.CornerBasedShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.tooling.preview.Preview
import coil.compose.SubcomposeAsyncImage
import coil.request.ImageRequest
import coil.request.SuccessResult
import com.voitov.pexels_app.R
import com.voitov.pexels_app.presentation.ui.theme.DarkGrayDarkShade
import com.voitov.pexels_app.presentation.ui.theme.DarkGrayLightShade
import com.voitov.pexels_app.presentation.ui.theme.Pexels_appTheme

@Composable
fun PhotoCard(
    imageUrl: String,
    onRenderFailed: () -> Unit,
    modifier: Modifier = Modifier,
    @DrawableRes placeholder: Int = R.drawable.placeholder,
    placeholderTint: Color = if (isSystemInDarkTheme()) DarkGrayLightShade else DarkGrayDarkShade,
    shape: CornerBasedShape = MaterialTheme.shapes.medium,
    onBeingLoadedColor: Color = MaterialTheme.colorScheme.secondary,
    contentScale: ContentScale = ContentScale.Crop
) {
    val context = LocalContext.current

    SubcomposeAsyncImage(
        modifier = Modifier
            .clip(shape)
            .then(modifier),
        model = ImageRequest.Builder(context)
            .data(imageUrl)
            .crossfade(true)
            .listener(onSuccess = { request: ImageRequest, result: SuccessResult ->
                Log.d("COIL", result.diskCacheKey ?: "")
                Log.d("COIL", result.memoryCacheKey?.key ?: "")
            }, onError = { _, _ ->
                onRenderFailed()
            })
            .build(),
        contentScale = contentScale,
        contentDescription = stringResource(R.string.feed_image),
        loading = {
            Placeholder(
                shape = shape,
                modifier = modifier,
                placeholderTint = placeholderTint,
                onBeingLoadedColor = onBeingLoadedColor,
                placeholder = placeholder
            )
        },
        error = {
            Placeholder(
                shape = shape,
                modifier = modifier,
                placeholderTint = placeholderTint,
                onBeingLoadedColor = onBeingLoadedColor,
                placeholder = placeholder
            )
        }
    )
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
            .then(modifier)
            .background(onBeingLoadedColor),
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
            onRenderFailed = {})
    }
}