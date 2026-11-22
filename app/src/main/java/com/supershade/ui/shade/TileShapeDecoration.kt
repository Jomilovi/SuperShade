package com.supershade.ui.shade

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import com.supershade.settings.TileShape

internal fun TileShape.isOutlineStyle(): Boolean = this == TileShape.CIRCLE_OUTLINE ||
    this == TileShape.CIRCLE_DASH ||
    this == TileShape.ROUNDED_RECTANGLE_OUTLINE ||
    this == TileShape.RECTANGLE_OUTLINE

@Composable
internal fun TileShapeDecoration(tileShape: TileShape, isActive: Boolean) {
    if (!isActive) return

    val colors = androidx.compose.material3.MaterialTheme.colorScheme
    when (tileShape) {
        TileShape.CIRCLE_GRADIENT -> Box(
            Modifier
                .fillMaxSize()
                .background(Brush.linearGradient(listOf(colors.primary, colors.secondary, colors.tertiary))),
        )
        TileShape.CIRCLE_HOLO -> Box(
            Modifier
                .fillMaxSize()
                .background(
                    Brush.radialGradient(
                        colors = listOf(colors.tertiary, colors.secondary, colors.primary),
                    ),
                ),
        )
        TileShape.CIRCLE_SLICE -> Canvas(Modifier.fillMaxSize()) {
            val side = size.minDimension
            drawArc(
                color = colors.secondary.copy(alpha = 0.45f),
                startAngle = -90f,
                sweepAngle = 132f,
                useCenter = true,
                topLeft = Offset((size.width - side) / 2f, (size.height - side) / 2f),
                size = Size(side, side),
            )
        }
        TileShape.CIRCLE_DASH -> Canvas(Modifier.fillMaxSize()) {
            val side = size.minDimension - 6.dp.toPx()
            if (side > 0f) {
                drawOval(
                    color = colors.primary,
                    topLeft = Offset((size.width - side) / 2f, (size.height - side) / 2f),
                    size = Size(side, side),
                    style = Stroke(
                        width = 2.dp.toPx(),
                        pathEffect = PathEffect.dashPathEffect(floatArrayOf(7.dp.toPx(), 5.dp.toPx())),
                    ),
                )
            }
        }
        else -> Unit
    }
}
