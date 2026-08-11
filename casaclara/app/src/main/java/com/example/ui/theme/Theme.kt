package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val CasaClaraColorScheme = lightColorScheme(
    primary = CyanDark008B8B,                 // #008B8B Dark Cyan
    onPrimary = Color.White,
    primaryContainer = CyanAquaB2EBF2,        // #B2EBF2 Light Aqua
    onPrimaryContainer = DarkCyanText,
    secondary = CyanSoftSkyA6D7E5,           // #A6D7E5 Soft Sky Cyan
    onSecondary = DarkCyanText,
    secondaryContainer = CyanPowderB0E0E6,    // #B0E0E6 Powder Blue
    onSecondaryContainer = DarkCyanText,
    tertiary = CyanDark008B8B,               // #008B8B Dark Cyan
    onTertiary = Color.White,
    tertiaryContainer = CyanIceE0F7FA,        // #E0F7FA Icy Light Cyan
    onTertiaryContainer = DarkCyanText,
    background = CyanIceE0F7FA,               // #E0F7FA Soft Icy Cyan Background
    onBackground = DarkCyanText,              // Deep cyan text on background
    surface = BrandSurface,                   // #A6D7E5 Soft sky cyan cards
    onSurface = BrandOnSurface,               // Dark cyan text on cards
    surfaceVariant = BrandSurfaceVariant,     // #B0E0E6 Powder blue variant
    onSurfaceVariant = BrandOnSurfaceMuted
)

@Composable
fun CasaClaraTheme(
    darkTheme: Boolean = false,
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = CasaClaraColorScheme,
        typography = Typography,
        content = content
    )
}


