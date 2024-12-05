package com.example.eventsproject.ui.theme

import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.material3.Typography
import com.example.eventsproject.R

val InterFontFamily = FontFamily(
    Font(R.font.inter_regular, FontWeight.Normal),
    Font(R.font.inter_bold, FontWeight.Bold),
    Font(R.font.inter_medium, FontWeight.Medium)
)

val AppTypography = Typography(
    displayLarge = Typography().displayLarge.copy(fontFamily = InterFontFamily),
    displayMedium = Typography().displayMedium.copy(fontFamily = InterFontFamily),
    displaySmall = Typography().displaySmall.copy(fontFamily = InterFontFamily),
    headlineLarge = Typography().headlineLarge.copy(fontFamily = InterFontFamily),
    headlineMedium = Typography().headlineMedium.copy(fontFamily = InterFontFamily),
    headlineSmall = Typography().headlineSmall.copy(fontFamily = InterFontFamily),
    bodyLarge = Typography().bodyLarge.copy(fontFamily = InterFontFamily),
    bodyMedium = Typography().bodyMedium.copy(fontFamily = InterFontFamily),
    bodySmall = Typography().bodySmall.copy(fontFamily = InterFontFamily),
    labelLarge = Typography().labelLarge.copy(fontFamily = InterFontFamily),
    labelMedium = Typography().labelMedium.copy(fontFamily = InterFontFamily),
    labelSmall = Typography().labelSmall.copy(fontFamily = InterFontFamily),
)
