package org.ks.chan.mi.updater.ui.theme

import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme

@RequiresApi(Build.VERSION_CODES.S)
private fun getDynamicColorScheme(isDarkTheme: Boolean) = when {
    isDarkTheme -> ::dynamicDarkColorScheme
    else -> ::dynamicLightColorScheme
}

@RequiresApi(Build.VERSION_CODES.S)
fun getDynamicColorScheme(
    context: Context,
    isDarkTheme: Boolean,
    dynamicColorScheme: (Context) -> ColorScheme =
        getDynamicColorScheme(isDarkTheme = isDarkTheme)
): ColorScheme = dynamicColorScheme(context)

private val LightColorScheme by lazy {
    lightColorScheme()
}

private val DarkColorScheme by lazy {
    darkColorScheme()
}

fun getColorScheme(isDarkTheme: Boolean) = when {
    isDarkTheme -> DarkColorScheme
    else -> LightColorScheme
}