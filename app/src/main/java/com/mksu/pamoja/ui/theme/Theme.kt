package com.mksu.pamoja.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

import com.mksu.pamoja.ui.theme.Background
import com.mksu.pamoja.ui.theme.BayOfMany
import com.mksu.pamoja.ui.theme.Black
import com.mksu.pamoja.ui.theme.OnBackground
import com.mksu.pamoja.ui.theme.OnPrimary
import com.mksu.pamoja.ui.theme.OnSecondary
import com.mksu.pamoja.ui.theme.OnSurface
import com.mksu.pamoja.ui.theme.SanguineBrown
import com.mksu.pamoja.ui.theme.Surface
import com.mksu.pamoja.ui.theme.Twine
import com.mksu.pamoja.ui.theme.Typography
import com.mksu.pamoja.ui.theme.White

private val DarkColorScheme = darkColorScheme(
    primary = BayOfMany,
    secondary = Twine,
    tertiary = SanguineBrown,
    background = Color(0xFF121212),
    surface = Color(0xFF1E1E1E),
    onPrimary = White,
    onSecondary = Black,
    onTertiary = White,
    onBackground = Color(0xFFFFFFFF),
    onSurface = Color(0xFFFFFFFF)
)

private val LightColorScheme = lightColorScheme(
    primary = BayOfMany,
    secondary = Twine,
    tertiary = SanguineBrown,
    background = Background,
    surface = Surface,
    onPrimary = OnPrimary,
    onSecondary = OnSecondary,
    onTertiary = OnPrimary,
    onBackground = OnBackground,
    onSurface = OnSurface
)

/**
 * MKSU Pamoja theme that configures the app's appearance.
 *
 * @param darkTheme Whether the theme should use a dark color scheme.
 * @param dynamicColor Whether to use dynamic color theming (Android 12+).
 * @param content The composable content to be displayed with this theme.
 */
@Composable
fun MKSUPamojaTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }
    
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.primary.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
