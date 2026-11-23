package com.supershade.ui.theme

import androidx.compose.material3.ColorScheme
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

enum class ShadeColorTarget {
    PANEL_BACKGROUND,
    NOTIFICATION_BACKGROUND,
    ACTIVE_CONTROL,
    GENERAL_TEXT,
    BRIGHTNESS_SLIDER,
    NOTIFICATION_TEXT,
}

data class ShadeColorCustomization(
    val panelBackground: Color? = null,
    val notificationBackground: Color? = null,
    val activeControl: Color? = null,
    val generalText: Color? = null,
    val brightnessSlider: Color? = null,
    val notificationText: Color? = null,
) {
    fun colorFor(target: ShadeColorTarget): Color? = when (target) {
        ShadeColorTarget.PANEL_BACKGROUND -> panelBackground
        ShadeColorTarget.NOTIFICATION_BACKGROUND -> notificationBackground
        ShadeColorTarget.ACTIVE_CONTROL -> activeControl
        ShadeColorTarget.GENERAL_TEXT -> generalText
        ShadeColorTarget.BRIGHTNESS_SLIDER -> brightnessSlider
        ShadeColorTarget.NOTIFICATION_TEXT -> notificationText
    }

    fun withColor(target: ShadeColorTarget, color: Color?): ShadeColorCustomization = when (target) {
        ShadeColorTarget.PANEL_BACKGROUND -> copy(panelBackground = color)
        ShadeColorTarget.NOTIFICATION_BACKGROUND -> copy(notificationBackground = color)
        ShadeColorTarget.ACTIVE_CONTROL -> copy(activeControl = color)
        ShadeColorTarget.GENERAL_TEXT -> copy(generalText = color)
        ShadeColorTarget.BRIGHTNESS_SLIDER -> copy(brightnessSlider = color)
        ShadeColorTarget.NOTIFICATION_TEXT -> copy(notificationText = color)
    }
}

val LocalShadeColorCustomization = staticCompositionLocalOf { ShadeColorCustomization() }
val LocalShadeAmoledMode = staticCompositionLocalOf { false }

fun ColorScheme.withShadeCustomization(
    customization: ShadeColorCustomization,
    amoled: Boolean,
): ColorScheme {
    val panel = if (amoled) Color.Black else customization.panelBackground
    val blackSurfaces = Color.Black
    val active = customization.activeControl
    val text = customization.generalText
    return copy(
        background = panel ?: background,
        surface = panel ?: surface,
        surfaceVariant = if (amoled) blackSurfaces else panel ?: surfaceVariant,
        surfaceContainerLowest = if (amoled) blackSurfaces else panel ?: surfaceContainerLowest,
        surfaceContainerLow = if (amoled) blackSurfaces else panel ?: surfaceContainerLow,
        surfaceContainer = if (amoled) blackSurfaces else panel ?: surfaceContainer,
        surfaceContainerHigh = if (amoled) blackSurfaces else panel ?: surfaceContainerHigh,
        surfaceContainerHighest = if (amoled) blackSurfaces else panel ?: surfaceContainerHighest,
        primaryContainer = if (amoled) blackSurfaces else active ?: primaryContainer,
        secondaryContainer = if (amoled) blackSurfaces else secondaryContainer,
        tertiaryContainer = if (amoled) blackSurfaces else tertiaryContainer,
        errorContainer = if (amoled) blackSurfaces else errorContainer,
        primary = active ?: primary,
        onPrimary = active?.let { if (it.luminance() > 0.52f) Color.Black else Color.White } ?: onPrimary,
        onPrimaryContainer = if (amoled) text ?: Color.White
            else active?.let { if (it.luminance() > 0.52f) Color.Black else Color.White } ?: onPrimaryContainer,
        onSecondaryContainer = if (amoled) Color.White else onSecondaryContainer,
        onTertiaryContainer = if (amoled) Color.White else onTertiaryContainer,
        onErrorContainer = if (amoled) Color.White else onErrorContainer,
        onBackground = text ?: onBackground,
        onSurface = text ?: onSurface,
        onSurfaceVariant = text ?: onSurfaceVariant,
    )
}
