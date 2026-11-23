package com.supershade.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.dp

/** Notification containers keep their own geometry, independent of quick-tile shape settings. */
fun notificationCardShape(theme: ShadeTheme): Shape = when (theme) {
    is ShadeTheme.Cyberpunk -> ChamferedCornerShape(10.dp)
    is ShadeTheme.Nothing -> RoundedCornerShape(16.dp)
    is ShadeTheme.Pixel -> RoundedCornerShape(26.dp)
    else -> RoundedCornerShape(24.dp)
}

fun notificationChipShape(theme: ShadeTheme): Shape = when (theme) {
    is ShadeTheme.Cyberpunk -> ChamferedCornerShape(6.dp)
    is ShadeTheme.Nothing -> RoundedCornerShape(10.dp)
    is ShadeTheme.Pixel, is ShadeTheme.PureMaterial -> RoundedCornerShape(50)
    else -> RoundedCornerShape(14.dp)
}
