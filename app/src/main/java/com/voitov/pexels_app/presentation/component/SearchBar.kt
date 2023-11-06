package com.voitov.pexels_app.presentation.component

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusState
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.voitov.pexels_app.R
import com.voitov.pexels_app.presentation.ui.theme.DarkGrayDarkShade
import com.voitov.pexels_app.presentation.ui.theme.DarkGrayLightShade
import com.voitov.pexels_app.presentation.ui.theme.Pexels_appTheme
import com.voitov.pexels_app.presentation.ui.theme.Red

@Composable
fun SearchBar(
    text: String,
    onValueChange: (String) -> Unit,
    onFocusChange: (FocusState) -> Unit,
    onSearch: (String) -> Unit,
    onClear: () -> Unit,
    modifier: Modifier = Modifier,
    textStyle: TextStyle = MaterialTheme.typography.bodyMedium,
    textColor: Color = MaterialTheme.colorScheme.onSecondary,
    hint: String = stringResource(id = R.string.search),
    shouldShowHint: Boolean = true,
    shouldShowClearIcon: Boolean = false,
    hintColor: Color = if (isSystemInDarkTheme()) DarkGrayDarkShade else DarkGrayLightShade,
    maxLines: Int = 1,
) {
    Row(
        modifier = modifier
            .clip(CircleShape)
            .background(MaterialTheme.colorScheme.secondary),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Spacer(modifier = Modifier.width(8.dp))
        IconButton(modifier = Modifier.size(40.dp),
            onClick = {
                onSearch(text)
            }) {
            Icon(
                modifier = Modifier.size(16.dp),
                tint = Red,
                imageVector = ImageVector.vectorResource(R.drawable.search_icon),
                contentDescription = stringResource(R.string.search)
            )
        }
        BasicTextField(
            modifier = Modifier
                .weight(1f)
                .onFocusChanged { onFocusChange(it) },
            value = if (shouldShowHint) hint else text,
            textStyle = textStyle.copy(color = if (shouldShowHint) hintColor else textColor),
            onValueChange = onValueChange,
            singleLine = true,
            maxLines = maxLines,
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
            keyboardActions = KeyboardActions(onSearch = {
                onSearch(text)
                defaultKeyboardAction(ImeAction.Search)
            }),
        )
        AnimatedVisibility(visible = shouldShowClearIcon) {
            IconButton(modifier = Modifier.size(40.dp),
                onClick = {
                    onClear()
                }) {
                Icon(
                    modifier = Modifier.size(16.dp),
                    tint = hintColor,
                    imageVector = ImageVector.vectorResource(R.drawable.clear),
                    contentDescription = stringResource(R.string.clear)
                )
            }
        }
        Spacer(modifier = Modifier.width(8.dp))
    }
}

@Preview
@Composable
private fun PreviewSearchBar_dark() {
    Pexels_appTheme(darkTheme = true) {
        SearchBar(
            modifier = Modifier.size(327.dp, 50.dp),
            text = "some_text some_text some_text some_text",
            onValueChange = {},
            onFocusChange = {},
            onSearch = {},
            onClear = {},
            hint = "Search",
            shouldShowHint = false,
            maxLines = 1,
            shouldShowClearIcon = true
        )
    }
}

@Preview
@Composable
private fun PreviewSearchBar_light() {
    Pexels_appTheme(darkTheme = false) {
        SearchBar(
            modifier = Modifier.size(327.dp, 50.dp),
            text = "some_text",
            onValueChange = {},
            onFocusChange = {},
            onSearch = {},
            onClear = {},
            hint = "Search",
            shouldShowHint = true,
            maxLines = 1
        )
    }
}