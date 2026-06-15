package com.todayplay.app.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val LightColors = lightColorScheme(
    primary = CherryPressed,
    onPrimary = GalleryWhite,
    secondary = DustPink,
    onSecondary = InkBlack,
    tertiary = RoseGold,
    background = PaperWhite,
    onBackground = InkBlack,
    surface = GalleryWhite,
    onSurface = InkBlack,
    outline = LineBeige,
)

@Composable
fun TodayPlayTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    MaterialTheme(
        colorScheme = LightColors,
        typography = TodayPlayTypography,
        content = content,
    )
}
