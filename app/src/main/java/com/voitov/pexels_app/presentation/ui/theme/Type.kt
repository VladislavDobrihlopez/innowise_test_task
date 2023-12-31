package com.voitov.pexels_app.presentation.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.voitov.pexels_app.R

private val fontFamily = FontFamily(
    listOf(
        Font(resId = R.font.mulish_bold_700, weight = FontWeight.W700),
        Font(resId = R.font.mulish_semi_bold_600, weight = FontWeight.W600),
        Font(resId = R.font.mulish_medium_500, weight = FontWeight.W500),
        Font(resId = R.font.mulish_regular_400, weight = FontWeight.W400),
    )
)
val Typography = Typography(
    bodyMedium = TextStyle(
        fontFamily = fontFamily,
        fontWeight = FontWeight.W400,
        fontSize = 14.sp,
    ),
    bodyLarge = TextStyle(
        fontFamily = fontFamily,
        fontWeight = FontWeight.W500,
        fontSize = 14.sp,
    ),
    titleSmall = TextStyle(
        fontFamily = fontFamily,
        fontWeight = FontWeight.W600,
        fontSize = 14.sp
    ),
    titleMedium = TextStyle(
        fontFamily = fontFamily,
        fontWeight = FontWeight.W700,
        fontSize = 18.sp
    ),
)