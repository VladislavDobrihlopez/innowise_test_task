package com.voitov.pexels_app.presentation.components

import androidx.annotation.DrawableRes
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.shape.CornerBasedShape
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.zIndex
import coil.compose.AsyncImage
import coil.imageLoader
import coil.request.ImageRequest
import com.voitov.pexels_app.R
import com.voitov.pexels_app.presentation.ui.theme.DarkGrayDarkShade
import com.voitov.pexels_app.presentation.ui.theme.DarkGrayLightShade
import com.voitov.pexels_app.presentation.ui.theme.Pexels_appTheme

@Composable
fun PhotoCard(
    imageUrl: String,
    modifier: Modifier = Modifier,
    @DrawableRes placeholder: Int = R.drawable.placeholder,
    placeholderTint: Color = if (isSystemInDarkTheme()) DarkGrayLightShade else DarkGrayDarkShade,
    shape: CornerBasedShape = MaterialTheme.shapes.medium,
    onBeingLoadedColor: Color = MaterialTheme.colorScheme.secondary,
) {
    val isImageDownloaded = rememberSaveable {
        mutableStateOf(false)
    }

    val context = LocalContext.current
    var imageRequest = ImageRequest.Builder(context).build()

    LaunchedEffect(key1 = Unit) {
        imageRequest = ImageRequest.Builder(context)
            .data(imageUrl)
            .crossfade(true)
            .listener(onSuccess = { _, _ ->
                isImageDownloaded.value = true
            }, onError = { _, _ ->
                isImageDownloaded.value = true

            })
            .error(R.drawable.splash_logo)
            .build()

        context.imageLoader.enqueue(imageRequest)
    }

    Card(modifier = modifier, shape = shape) {
        AnimatedContent(targetState = isImageDownloaded.value, label = "") {
            if (it) {
                AsyncImage(
                    modifier = Modifier
                        .fillMaxSize(),
                    contentScale = ContentScale.Crop,
                    model = imageRequest,
                    contentDescription = ""
                )
            } else {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(onBeingLoadedColor),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        imageVector = ImageVector.vectorResource(id = placeholder),
                        contentDescription = "donwloading",
                        colorFilter = ColorFilter.tint(placeholderTint)
                    )
                }
            }
        }
    }
}

@Preview
@Composable
private fun PreviewPhotoCard() {
    Pexels_appTheme {
        PhotoCard(imageUrl = "https://www.pexels.com/photo/a-wagon-with-a-sign-that-says-fresh-produce-delivery-18878905/")
    }
}