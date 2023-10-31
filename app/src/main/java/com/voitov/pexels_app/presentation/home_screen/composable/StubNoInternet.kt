package com.voitov.pexels_app.presentation.home_screen.composable

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.tooling.preview.Preview
import com.voitov.pexels_app.R
import com.voitov.pexels_app.presentation.component.StubNoData
import com.voitov.pexels_app.presentation.ui.theme.Pexels_appTheme

@Composable
fun StubNoInternet(onTryAgainClick: () -> Unit) {
    StubNoData(
        modifier = Modifier.fillMaxSize(),
        actionText = stringResource(R.string.try_again),
        onTextButtonClick = onTryAgainClick
    ) {
        Image(
            imageVector = ImageVector.vectorResource(id = R.drawable.no_network_icon),
            contentDescription = stringResource(
                R.string.no_internet_try_again
            )
        )
    }
}

@Composable
@Preview
private fun PreviewStubNoInternet() {
    Pexels_appTheme {
        StubNoInternet {}
    }
}