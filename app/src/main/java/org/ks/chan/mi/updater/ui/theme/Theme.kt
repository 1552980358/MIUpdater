package org.ks.chan.mi.updater.ui.theme

import android.app.Activity
import android.content.Context
import android.os.Build
import android.view.View
import android.view.Window
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val Typography = Typography()

@Composable
fun MIUpdaterTheme(
    isDarkTheme: Boolean = isSystemInDarkTheme(),
    applyDynamicColor: Boolean = true,
    colorScheme: ColorScheme =
        getColorScheme(isDarkTheme = isDarkTheme, applyDynamicColor = applyDynamicColor),
    content: @Composable () -> Unit,
) {

    ReleaseSideEffect { view, window ->
        WindowCompat.getInsetsController(window, view)
            .isAppearanceLightStatusBars = isDarkTheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}

private val isDynamicColorSupported: Boolean =
    Build.VERSION.SDK_INT >= Build.VERSION_CODES.S

@Composable
private fun getColorScheme(
    isDarkTheme: Boolean,
    applyDynamicColor: Boolean
): ColorScheme = when {
    applyDynamicColor && isDynamicColorSupported -> {
        getDynamicColorScheme(
            context = LocalContext.current,
            isDarkTheme = isDarkTheme
        )
    }
    else -> {
        getColorScheme(isDarkTheme = isDarkTheme)
    }
}

@Composable
private fun ReleaseSideEffect(
    view: View = LocalView.current,
    context: Context = LocalContext.current,
    sideEffect: (View, Window) -> Unit
) {
    if (!view.isInEditMode) {
        SideEffect {
            sideEffect(view, (context as Activity).window)
        }
    }
}