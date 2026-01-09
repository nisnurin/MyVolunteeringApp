package com.example.ict602my_vol.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val AppColorScheme = lightColorScheme(
    primary = BrandBlue,
    background = AppBackground,
    surface = AppSurface,
    onPrimary = AppBackground,
    onBackground = AppTextDark,
    onSurface = AppTextDark
)

@Composable
fun ICT602MY_VOLTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = AppColorScheme,
        typography = Typography,
        content = content
    )
}
