package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

// --- THEME COLOR SCHEMES ---

// Theme 0: Lavender Mint
private val LavenderLightScheme = lightColorScheme(
    primary = LavenderLightPrimary,
    onPrimary = Color.White,
    primaryContainer = LavenderLightVariant,
    onPrimaryContainer = LavenderLightPrimary,
    secondary = LavenderLightSecondary,
    onSecondary = Color.White,
    secondaryContainer = LavenderLightVariant,
    onSecondaryContainer = LavenderLightSecondary,
    tertiary = LavenderLightTertiary,
    background = LavenderLightBg,
    onBackground = Color(0xFF1E1B4B),
    surface = LavenderLightSurface,
    onSurface = Color(0xFF1E1B4B),
    surfaceVariant = LavenderLightVariant,
    onSurfaceVariant = LavenderLightPrimary,
    outline = Color(0xFFC0A6FF)
)

private val LavenderDarkScheme = darkColorScheme(
    primary = LavenderDarkPrimary,
    onPrimary = Color(0xFF0F0E1E),
    primaryContainer = LavenderDarkVariant,
    onPrimaryContainer = LavenderDarkPrimary,
    secondary = LavenderDarkSecondary,
    onSecondary = Color(0xFF064E3B),
    secondaryContainer = LavenderDarkVariant,
    onSecondaryContainer = LavenderDarkSecondary,
    tertiary = LavenderDarkTertiary,
    background = LavenderDarkBg,
    onBackground = Color(0xFFECEBFA),
    surface = LavenderDarkSurface,
    onSurface = Color(0xFFECEBFA),
    surfaceVariant = LavenderDarkVariant,
    onSurfaceVariant = LavenderDarkPrimary,
    outline = Color(0xFF4A3E72)
)

// Theme 1: Peach Cream
private val PeachLightScheme = lightColorScheme(
    primary = PeachLightPrimary,
    onPrimary = Color.White,
    primaryContainer = PeachLightVariant,
    onPrimaryContainer = PeachLightPrimary,
    secondary = PeachLightSecondary,
    onSecondary = Color.White,
    secondaryContainer = PeachLightVariant,
    onSecondaryContainer = PeachLightSecondary,
    tertiary = PeachLightTertiary,
    background = PeachLightBg,
    onBackground = Color(0xFF3B1E05),
    surface = PeachLightSurface,
    onSurface = Color(0xFF3B1E05),
    surfaceVariant = PeachLightVariant,
    onSurfaceVariant = PeachLightPrimary,
    outline = Color(0xFFFFCC99)
)

private val PeachDarkScheme = darkColorScheme(
    primary = PeachDarkPrimary,
    onPrimary = Color(0xFF1E130B),
    primaryContainer = PeachDarkVariant,
    onPrimaryContainer = PeachDarkPrimary,
    secondary = PeachDarkSecondary,
    onSecondary = Color(0xFF3B1E05),
    secondaryContainer = PeachDarkVariant,
    onSecondaryContainer = PeachDarkSecondary,
    tertiary = PeachDarkTertiary,
    background = PeachDarkBg,
    onBackground = Color(0xFFFFF7ED),
    surface = PeachDarkSurface,
    onSurface = Color(0xFFFFF7ED),
    surfaceVariant = PeachDarkVariant,
    onSurfaceVariant = PeachDarkPrimary,
    outline = Color(0xFF5E3F27)
)

// Theme 2: Cherry Blossom
private val CherryLightScheme = lightColorScheme(
    primary = CherryLightPrimary,
    onPrimary = Color.White,
    primaryContainer = CherryLightVariant,
    onPrimaryContainer = CherryLightPrimary,
    secondary = CherryLightSecondary,
    onSecondary = Color.White,
    secondaryContainer = CherryLightVariant,
    onSecondaryContainer = CherryLightSecondary,
    tertiary = CherryLightTertiary,
    background = CherryLightBg,
    onBackground = Color(0xFF3E1119),
    surface = CherryLightSurface,
    onSurface = Color(0xFF3E1119),
    surfaceVariant = CherryLightVariant,
    onSurfaceVariant = CherryLightPrimary,
    outline = Color(0xFFFFC0CB)
)

private val CherryDarkScheme = darkColorScheme(
    primary = CherryDarkPrimary,
    onPrimary = Color(0xFF1F0E16),
    primaryContainer = CherryDarkVariant,
    onPrimaryContainer = CherryDarkPrimary,
    secondary = CherryDarkSecondary,
    onSecondary = Color(0xFF4C0519),
    secondaryContainer = CherryDarkVariant,
    onSecondaryContainer = CherryDarkSecondary,
    tertiary = CherryDarkTertiary,
    background = CherryDarkBg,
    onBackground = Color(0xFFFFF1F2),
    surface = CherryDarkSurface,
    onSurface = Color(0xFFFFF1F2),
    surfaceVariant = CherryDarkVariant,
    onSurfaceVariant = CherryDarkPrimary,
    outline = Color(0xFF6B3A50)
)

// Theme 3: Ocean Breeze
private val OceanLightScheme = lightColorScheme(
    primary = OceanLightPrimary,
    onPrimary = Color.White,
    primaryContainer = OceanLightVariant,
    onPrimaryContainer = OceanLightPrimary,
    secondary = OceanLightSecondary,
    onSecondary = Color.White,
    secondaryContainer = OceanLightVariant,
    onSecondaryContainer = OceanLightSecondary,
    tertiary = OceanLightTertiary,
    background = OceanLightBg,
    onBackground = Color(0xFF0F172A),
    surface = OceanLightSurface,
    onSurface = Color(0xFF0F172A),
    surfaceVariant = OceanLightVariant,
    onSurfaceVariant = OceanLightPrimary,
    outline = Color(0xFF99DEFF)
)

private val OceanDarkScheme = darkColorScheme(
    primary = OceanDarkPrimary,
    onPrimary = Color(0xFF0B1424),
    primaryContainer = OceanDarkVariant,
    onPrimaryContainer = OceanDarkPrimary,
    secondary = OceanDarkSecondary,
    onSecondary = Color(0xFF042F2E),
    secondaryContainer = OceanDarkVariant,
    onSecondaryContainer = OceanDarkSecondary,
    tertiary = OceanDarkTertiary,
    background = OceanDarkBg,
    onBackground = Color(0xFFF1F5F9),
    surface = OceanDarkSurface,
    onSurface = Color(0xFFF1F5F9),
    surfaceVariant = OceanDarkVariant,
    onSurfaceVariant = OceanDarkPrimary,
    outline = Color(0xFF2C4A70)
)

// Theme 4: Sage Garden
private val SageLightScheme = lightColorScheme(
    primary = SageLightPrimary,
    onPrimary = Color.White,
    primaryContainer = SageLightVariant,
    onPrimaryContainer = SageLightPrimary,
    secondary = SageLightSecondary,
    onSecondary = Color.White,
    secondaryContainer = SageLightVariant,
    onSecondaryContainer = SageLightSecondary,
    tertiary = SageLightTertiary,
    background = SageLightBg,
    onBackground = Color(0xFF042F2E),
    surface = SageLightSurface,
    onSurface = Color(0xFF042F2E),
    surfaceVariant = SageLightVariant,
    onSurfaceVariant = SageLightPrimary,
    outline = Color(0xFFA7F3D0)
)

private val SageDarkScheme = darkColorScheme(
    primary = SageDarkPrimary,
    onPrimary = Color(0xFF071B14),
    primaryContainer = SageDarkVariant,
    onPrimaryContainer = SageDarkPrimary,
    secondary = SageDarkSecondary,
    onSecondary = Color(0xFF1A2E05),
    secondaryContainer = SageDarkVariant,
    onSecondaryContainer = SageDarkSecondary,
    tertiary = SageDarkTertiary,
    background = SageDarkBg,
    onBackground = Color(0xFFECFDF5),
    surface = SageDarkSurface,
    onSurface = Color(0xFFECFDF5),
    surfaceVariant = SageDarkVariant,
    onSurfaceVariant = SageDarkPrimary,
    outline = Color(0xFF226E54)
)

@Composable
fun MyApplicationTheme(
    selectedThemeIndex: Int = 0,
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit,
) {
    val colorScheme = when (selectedThemeIndex) {
        0 -> if (darkTheme) LavenderDarkScheme else LavenderLightScheme
        1 -> if (darkTheme) PeachDarkScheme else PeachLightScheme
        2 -> if (darkTheme) CherryDarkScheme else CherryLightScheme
        3 -> if (darkTheme) OceanDarkScheme else OceanLightScheme
        4 -> if (darkTheme) SageDarkScheme else SageLightScheme
        else -> if (darkTheme) LavenderDarkScheme else LavenderLightScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
