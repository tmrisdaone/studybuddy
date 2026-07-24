package com.tmrisdaone.studybuddy.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

// Turbo-inspired palette: deep ink backgrounds, electric violet + cyan accents.
private val Ink = Color(0xFF0B0B12)
private val InkElevated = Color(0xFF14141F)
private val Surface = Color(0xFF1A1A28)
private val SurfaceHigh = Color(0xFF232336)

private val Violet = Color(0xFF7C5CFF)
private val VioletBright = Color(0xFF9A7BFF)
private val Cyan = Color(0xFF22D3EE)
private val Pink = Color(0xFFF472B6)

private val OnInk = Color(0xFFF5F5FA)
private val OnInkVariant = Color(0xFFA9A9BD)

private val DarkColors = darkColorScheme(
    primary = VioletBright,
    onPrimary = Color.White,
    primaryContainer = Surface,
    onPrimaryContainer = OnInk,
    secondary = Cyan,
    onSecondary = Color.Black,
    secondaryContainer = Color(0xFF10343B),
    onSecondaryContainer = Cyan,
    tertiary = Pink,
    onTertiary = Color.Black,
    tertiaryContainer = Color(0xFF39192C),
    onTertiaryContainer = Pink,
    background = Ink,
    onBackground = OnInk,
    surface = Surface,
    onSurface = OnInk,
    surfaceVariant = SurfaceHigh,
    onSurfaceVariant = OnInkVariant,
    surfaceContainer = InkElevated,
    surfaceContainerHigh = Surface,
    surfaceContainerHighest = SurfaceHigh,
    error = Color(0xFFFF6B6B),
    onError = Color.Black,
    errorContainer = Color(0xFF3A1414),
    onErrorContainer = Color(0xFFFFB4B4),
    outline = Color(0xFF3A3A52),
    outlineVariant = Color(0xFF262638)
)

private val LightColors = lightColorScheme(
    primary = Violet,
    onPrimary = Color.White,
    secondary = Color(0xFF0E7C8A),
    onSecondary = Color.White,
    tertiary = Color(0xFFC8378D),
    onTertiary = Color.White,
    background = Color(0xFFF6F6FB),
    onBackground = Color(0xFF14141F),
    surface = Color(0xFFFFFFFF),
    onSurface = Color(0xFF14141F),
    primaryContainer = Color(0xFFE8E1FF),
    onPrimaryContainer = Color(0xFF2A1A66),
    error = Color(0xFFC62828),
    onError = Color.White
)

@Composable
fun StudyBuddyTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColors else LightColors
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = Ink.toArgb()
            WindowCompat.getInsetsController(window, view).apply {
                isAppearanceLightStatusBars = false
                isAppearanceLightNavigationBars = false
            }
        }
    }
    MaterialTheme(colorScheme = colorScheme, content = content)
}

object TurboGradients {
    val accent: Brush
        @Composable get() = Brush.linearGradient(listOf(VioletBright, Cyan))
    val accentDiagonal: Brush
        @Composable get() = Brush.linearGradient(
            colors = listOf(Violet, Cyan),
            start = androidx.compose.ui.geometry.Offset(0f, 0f),
            end = androidx.compose.ui.geometry.Offset(Float.MAX_VALUE, Float.MAX_VALUE)
        )
    val header: Brush
        @Composable get() = Brush.verticalGradient(listOf(SurfaceHigh, Ink))
    val chip: Brush
        @Composable get() = Brush.linearGradient(listOf(Color(0xFF1E1E33), Color(0xFF1A2233)))
}

val TurboColors
    @Composable get() = MaterialTheme.colorScheme
