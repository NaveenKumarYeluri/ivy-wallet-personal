package com.ivy.design.system

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.ivy.design.system.colors.IvyColors

@Composable
fun IvyMaterial3Theme(
    isTrueBlack: Boolean,
    dark: Boolean = isSystemInDarkTheme(),
    accentColor: Color = Color(0xFF27AE60),
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = if (dark) ivyDarkColorScheme(isTrueBlack, accentColor) else ivyLightColorScheme(accentColor),
        content = content,
    )
}

private fun ivyLightColorScheme(accentColor: Color = Color(0xFF27AE60)): ColorScheme = ColorScheme(
    primary = accentColor,
    onPrimary = IvyColors.White,
    primaryContainer = accentColor.copy(alpha = 0.2f),
    onPrimaryContainer = accentColor,
    inversePrimary = accentColor,
    secondary = IvyColors.Green.primary,
    onSecondary = IvyColors.White,
    secondaryContainer = IvyColors.Green.light,
    onSecondaryContainer = IvyColors.White,
    tertiary = IvyColors.Green.primary,
    onTertiary = IvyColors.White,
    tertiaryContainer = IvyColors.Green.light,
    onTertiaryContainer = IvyColors.White,

    error = IvyColors.Red.primary,
    onError = IvyColors.White,
    errorContainer = IvyColors.Red.light,
    onErrorContainer = IvyColors.White,

    background = IvyColors.White,
    onBackground = IvyColors.Black,
    surface = IvyColors.White,
    onSurface = IvyColors.Black,
    surfaceVariant = IvyColors.ExtraLightGray,
    onSurfaceVariant = IvyColors.Black,
    surfaceTint = IvyColors.Black,
    inverseSurface = IvyColors.DarkGray,
    inverseOnSurface = IvyColors.White,

    outline = IvyColors.Gray,
    outlineVariant = IvyColors.DarkGray,
    scrim = IvyColors.ExtraDarkGray.copy(alpha = 0.8f)
)

private fun ivyDarkColorScheme(isTrueBlack: Boolean, accentColor: Color = Color(0xFF27AE60)): ColorScheme = ColorScheme(
    primary = accentColor,
    onPrimary = IvyColors.White,
    primaryContainer = accentColor.copy(alpha = 0.2f),
    onPrimaryContainer = IvyColors.White,
    inversePrimary = accentColor,
    secondary = IvyColors.Green.primary,
    onSecondary = IvyColors.White,
    secondaryContainer = IvyColors.Green.light,
    onSecondaryContainer = IvyColors.White,
    tertiary = IvyColors.Green.primary,
    onTertiary = IvyColors.White,
    tertiaryContainer = IvyColors.Green.light,
    onTertiaryContainer = IvyColors.White,

    error = IvyColors.Red.primary,
    onError = IvyColors.White,
    errorContainer = IvyColors.Red.light,
    onErrorContainer = IvyColors.White,

    background = if (isTrueBlack) IvyColors.TrueBlack else IvyColors.Black,
    onBackground = IvyColors.White,
    surface = if (isTrueBlack) IvyColors.TrueBlack else IvyColors.Black,
    onSurface = IvyColors.White,
    surfaceVariant = IvyColors.ExtraDarkGray,
    onSurfaceVariant = IvyColors.White,
    surfaceTint = IvyColors.White,
    inverseSurface = IvyColors.LightGray,
    inverseOnSurface = if (isTrueBlack) IvyColors.TrueBlack else IvyColors.Black,

    outline = IvyColors.Gray,
    outlineVariant = IvyColors.LightGray,
    scrim = IvyColors.ExtraLightGray.copy(alpha = 0.8f)
)
