package com.voitov.pexels_app.presentation.home_screen.components

import androidx.compose.foundation.shape.CornerBasedShape
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.voitov.pexels_app.R
import com.voitov.pexels_app.presentation.ui.theme.Pexels_appTheme

@Composable
fun PhotoCard(
    modifier: Modifier = Modifier,
    shape: CornerBasedShape = MaterialTheme.shapes.medium,
    imageUrl: String
) {
    Card(modifier = modifier, shape = shape) {
        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data(imageUrl)
                .crossfade(true)
                .error(R.drawable.splash_logo)
                .build(), contentDescription = ""
        )
    }
}

@Preview
@Composable
private fun PreviewPhotoCard() {
    Pexels_appTheme {
        PhotoCard(imageUrl = "https://www.pexels.com/photo/a-wagon-with-a-sign-that-says-fresh-produce-delivery-18878905/")
    }
}