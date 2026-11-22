package com.supershade.ui.theme

import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.GenericShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.supershade.settings.TileShape

private val TeardropShape = GenericShape { size, _ ->
    val w = size.width
    val h = size.height
    moveTo(w * 0.5f, h * 0.98f)
    cubicTo(w * 0.42f, h * 0.79f, w * 0.08f, h * 0.53f, w * 0.08f, h * 0.34f)
    cubicTo(w * 0.08f, h * 0.15f, w * 0.25f, h * 0.02f, w * 0.5f, h * 0.02f)
    cubicTo(w * 0.75f, h * 0.02f, w * 0.92f, h * 0.15f, w * 0.92f, h * 0.34f)
    cubicTo(w * 0.92f, h * 0.53f, w * 0.58f, h * 0.79f, w * 0.5f, h * 0.98f)
    close()
}

private val OwlHeadShape = GenericShape { size, _ ->
    val w = size.width
    val h = size.height
    moveTo(w * 0.08f, h * 0.36f)
    cubicTo(w * 0.06f, h * 0.24f, w * 0.10f, h * 0.12f, w * 0.19f, h * 0.08f)
    lineTo(w * 0.35f, h * 0.27f)
    cubicTo(w * 0.40f, h * 0.20f, w * 0.45f, h * 0.16f, w * 0.50f, h * 0.16f)
    cubicTo(w * 0.55f, h * 0.16f, w * 0.60f, h * 0.20f, w * 0.65f, h * 0.27f)
    lineTo(w * 0.81f, h * 0.08f)
    cubicTo(w * 0.90f, h * 0.12f, w * 0.94f, h * 0.24f, w * 0.92f, h * 0.36f)
    cubicTo(w * 0.90f, h * 0.67f, w * 0.74f, h * 0.91f, w * 0.50f, h * 0.98f)
    cubicTo(w * 0.26f, h * 0.91f, w * 0.10f, h * 0.67f, w * 0.08f, h * 0.36f)
    close()
}

private fun polygonShape(points: List<Pair<Float, Float>>): Shape = GenericShape { size, _ ->
    points.forEachIndexed { index, point ->
        val x = size.width * point.first
        val y = size.height * point.second
        if (index == 0) moveTo(x, y) else lineTo(x, y)
    }
    close()
}

private val HexagonShape = polygonShape(
    listOf(0.25f to 0f, 0.75f to 0f, 1f to 0.5f, 0.75f to 1f, 0.25f to 1f, 0f to 0.5f),
)
private val PentagonShape = polygonShape(
    listOf(0.5f to 0f, 1f to 0.38f, 0.81f to 1f, 0.19f to 1f, 0f to 0.38f),
)

/**
 * Cohesive global shape tokens derived from the selected [TileShape].
 * Automatically shapes tiles, cards, container islands, sliders, chips, and pills across SuperShade.
 */
data class ShadeShapeScheme(
    val tile: Shape,
    val card: Shape,
    val container: Shape,
    val slider: Shape,
    val chip: Shape,
    val pill: Shape,
    val tilePaddingHorizontal: Dp = 8.dp,
    val tilePaddingVertical: Dp = 8.dp,
    val tileIconSizeModifier: Dp = 0.dp,
) {
    companion object {
        fun fromTileShape(tileShape: TileShape): ShadeShapeScheme = when (tileShape) {
            TileShape.SQUIRCLE -> ShadeShapeScheme(
                tile = RoundedCornerShape(22.dp),
                card = RoundedCornerShape(24.dp),
                container = RoundedCornerShape(26.dp),
                slider = RoundedCornerShape(22.dp),
                chip = RoundedCornerShape(14.dp),
                pill = RoundedCornerShape(50),
                tilePaddingHorizontal = 8.dp,
                tilePaddingVertical = 8.dp,
            )
            TileShape.ROUNDED -> ShadeShapeScheme(
                tile = RoundedCornerShape(16.dp),
                card = RoundedCornerShape(18.dp),
                container = RoundedCornerShape(20.dp),
                slider = RoundedCornerShape(16.dp),
                chip = RoundedCornerShape(12.dp),
                pill = RoundedCornerShape(50),
                tilePaddingHorizontal = 8.dp,
                tilePaddingVertical = 8.dp,
            )
            TileShape.CIRCLE -> ShadeShapeScheme(
                tile = CircleShape,
                card = RoundedCornerShape(30.dp),
                container = RoundedCornerShape(32.dp),
                slider = CircleShape,
                chip = CircleShape,
                pill = CircleShape,
                tilePaddingHorizontal = 10.dp,
                tilePaddingVertical = 6.dp, // Protect text from circular arc clipping
            )
            TileShape.CIRCLE_OUTLINE, TileShape.CIRCLE_DASH, TileShape.CIRCLE_SLICE,
            TileShape.CIRCLE_GRADIENT, TileShape.CIRCLE_HOLO -> ShadeShapeScheme(
                tile = CircleShape,
                card = RoundedCornerShape(30.dp),
                container = RoundedCornerShape(32.dp),
                slider = CircleShape,
                chip = CircleShape,
                pill = CircleShape,
                tilePaddingHorizontal = 10.dp,
                tilePaddingVertical = 6.dp,
            )
            TileShape.TEARDROP -> ShadeShapeScheme(
                tile = TeardropShape,
                card = RoundedCornerShape(24.dp),
                container = RoundedCornerShape(26.dp),
                slider = TeardropShape,
                chip = RoundedCornerShape(14.dp),
                pill = RoundedCornerShape(50),
                tilePaddingHorizontal = 10.dp,
                tilePaddingVertical = 8.dp,
            )
            TileShape.ROUNDED_RECTANGLE, TileShape.ROUNDED_RECTANGLE_OUTLINE -> ShadeShapeScheme(
                tile = RoundedCornerShape(12.dp),
                card = RoundedCornerShape(16.dp),
                container = RoundedCornerShape(20.dp),
                slider = RoundedCornerShape(12.dp),
                chip = RoundedCornerShape(10.dp),
                pill = RoundedCornerShape(50),
                tilePaddingHorizontal = 8.dp,
                tilePaddingVertical = 8.dp,
            )
            TileShape.RECTANGLE, TileShape.RECTANGLE_OUTLINE -> ShadeShapeScheme(
                tile = RectangleShape,
                card = RectangleShape,
                container = RectangleShape,
                slider = RectangleShape,
                chip = RectangleShape,
                pill = RectangleShape,
                tilePaddingHorizontal = 9.dp,
                tilePaddingVertical = 8.dp,
            )
            TileShape.HEXAGON -> ShadeShapeScheme(
                tile = HexagonShape,
                card = RoundedCornerShape(24.dp),
                container = RoundedCornerShape(26.dp),
                slider = HexagonShape,
                chip = RoundedCornerShape(14.dp),
                pill = RoundedCornerShape(50),
                tilePaddingHorizontal = 12.dp,
                tilePaddingVertical = 10.dp,
            )
            TileShape.PENTAGON -> ShadeShapeScheme(
                tile = PentagonShape,
                card = RoundedCornerShape(24.dp),
                container = RoundedCornerShape(26.dp),
                slider = PentagonShape,
                chip = RoundedCornerShape(14.dp),
                pill = RoundedCornerShape(50),
                tilePaddingHorizontal = 12.dp,
                tilePaddingVertical = 10.dp,
            )
            TileShape.OWL -> ShadeShapeScheme(
                tile = OwlHeadShape,
                card = OwlHeadShape,
                container = OwlHeadShape,
                slider = RoundedCornerShape(22.dp),
                chip = RoundedCornerShape(14.dp),
                pill = RoundedCornerShape(50),
                tilePaddingHorizontal = 12.dp,
                tilePaddingVertical = 11.dp,
            )
            TileShape.PILL -> ShadeShapeScheme(
                tile = RoundedCornerShape(28.dp),
                card = RoundedCornerShape(26.dp),
                container = RoundedCornerShape(28.dp),
                slider = RoundedCornerShape(28.dp),
                chip = RoundedCornerShape(50),
                pill = RoundedCornerShape(50),
                tilePaddingHorizontal = 9.dp,
                tilePaddingVertical = 8.dp,
            )
            TileShape.SOFT -> ShadeShapeScheme(
                tile = RoundedCornerShape(12.dp),
                card = RoundedCornerShape(14.dp),
                container = RoundedCornerShape(16.dp),
                slider = RoundedCornerShape(12.dp),
                chip = RoundedCornerShape(10.dp),
                pill = RoundedCornerShape(18.dp),
                tilePaddingHorizontal = 8.dp,
                tilePaddingVertical = 8.dp,
            )
            TileShape.LEAF -> ShadeShapeScheme(
                tile = RoundedCornerShape(topStart = 24.dp, bottomEnd = 24.dp, topEnd = 8.dp, bottomStart = 8.dp),
                card = RoundedCornerShape(topStart = 24.dp, bottomEnd = 24.dp, topEnd = 10.dp, bottomStart = 10.dp),
                container = RoundedCornerShape(topStart = 28.dp, bottomEnd = 28.dp, topEnd = 12.dp, bottomStart = 12.dp),
                slider = RoundedCornerShape(topStart = 20.dp, bottomEnd = 20.dp, topEnd = 8.dp, bottomStart = 8.dp),
                chip = RoundedCornerShape(topStart = 14.dp, bottomEnd = 14.dp, topEnd = 6.dp, bottomStart = 6.dp),
                pill = RoundedCornerShape(topStart = 20.dp, bottomEnd = 20.dp, topEnd = 8.dp, bottomStart = 8.dp),
                tilePaddingHorizontal = 8.dp,
                tilePaddingVertical = 7.dp,
            )
            TileShape.SHARP -> ShadeShapeScheme(
                tile = RoundedCornerShape(6.dp),
                card = RoundedCornerShape(8.dp),
                container = RoundedCornerShape(10.dp),
                slider = RoundedCornerShape(6.dp),
                chip = RoundedCornerShape(4.dp),
                pill = RoundedCornerShape(8.dp),
                tilePaddingHorizontal = 9.dp,
                tilePaddingVertical = 8.dp,
            )
            TileShape.CLOVER -> ShadeShapeScheme(
                tile = M3ExpressiveShapes.Clover4.toComposeShape(),
                card = RoundedCornerShape(24.dp),
                container = RoundedCornerShape(26.dp),
                slider = RoundedCornerShape(22.dp),
                chip = RoundedCornerShape(14.dp),
                pill = RoundedCornerShape(50),
                tilePaddingHorizontal = 10.dp,
                tilePaddingVertical = 8.dp,
            )
            TileShape.BURST -> ShadeShapeScheme(
                tile = M3ExpressiveShapes.SoftBurst8.toComposeShape(),
                card = RoundedCornerShape(24.dp),
                container = RoundedCornerShape(26.dp),
                slider = RoundedCornerShape(22.dp),
                chip = RoundedCornerShape(14.dp),
                pill = RoundedCornerShape(50),
                tilePaddingHorizontal = 10.dp,
                tilePaddingVertical = 8.dp,
            )
        }

        /**
         * Authentic Google Pixel (Material 3 Expressive) shape scheme for Android 15/16.
         * Features 28dp pill tiles, 26dp cards, 30dp sliders, and stadium pill chips.
         */
        val PixelExpressive = ShadeShapeScheme(
            tile = RoundedCornerShape(28.dp),
            card = RoundedCornerShape(26.dp),
            container = RoundedCornerShape(28.dp),
            slider = RoundedCornerShape(30.dp),
            chip = RoundedCornerShape(50),
            pill = RoundedCornerShape(50),
            tilePaddingHorizontal = 10.dp,
            tilePaddingVertical = 8.dp,
        )

        /**
         * Samsung One UI 8/9 squircle shape scheme.
         */
        val OneUiStandard = ShadeShapeScheme(
            tile = RoundedCornerShape(20.dp),
            card = RoundedCornerShape(24.dp),
            container = RoundedCornerShape(26.dp),
            slider = RoundedCornerShape(20.dp),
            chip = RoundedCornerShape(14.dp),
            pill = RoundedCornerShape(50),
            tilePaddingHorizontal = 8.dp,
            tilePaddingVertical = 8.dp,
        )

        /**
         * Nothing OS minimal geometric shape scheme with dot-matrix styling and stadium pills.
         */
        val NothingMinimal = ShadeShapeScheme(
            tile = RoundedCornerShape(20.dp),
            card = RoundedCornerShape(22.dp),
            container = RoundedCornerShape(24.dp),
            slider = RoundedCornerShape(16.dp),
            chip = RoundedCornerShape(50),
            pill = RoundedCornerShape(50),
            tilePaddingHorizontal = 8.dp,
            tilePaddingVertical = 8.dp,
        )

        /**
         * Cyberpunk high-tech sharp angular cuts and chamfered surfaces.
         */
        val CyberpunkTech = ShadeShapeScheme(
            tile = RoundedCornerShape(6.dp),
            card = RoundedCornerShape(10.dp),
            container = RoundedCornerShape(12.dp),
            slider = RoundedCornerShape(6.dp),
            chip = RoundedCornerShape(4.dp),
            pill = RoundedCornerShape(8.dp),
            tilePaddingHorizontal = 9.dp,
            tilePaddingVertical = 8.dp,
        )

        /**
         * Pure Material 3 / Android Expressive shape scheme.
         */
        val PureMaterialM3 = ShadeShapeScheme(
            tile = RoundedCornerShape(16.dp),
            card = RoundedCornerShape(24.dp),
            container = RoundedCornerShape(28.dp),
            slider = RoundedCornerShape(24.dp),
            chip = RoundedCornerShape(12.dp),
            pill = RoundedCornerShape(50),
            tilePaddingHorizontal = 8.dp,
            tilePaddingVertical = 8.dp,
        )

        fun forTheme(theme: ShadeTheme, tileShape: TileShape): ShadeShapeScheme {
            if (tileShape != TileShape.SQUIRCLE) {
                return fromTileShape(tileShape)
            }
            return when (theme) {
                ShadeTheme.Pixel -> PixelExpressive
                ShadeTheme.Nothing -> NothingMinimal
                ShadeTheme.Cyberpunk -> CyberpunkTech
                ShadeTheme.PureMaterial -> PureMaterialM3
                ShadeTheme.OneUI -> OneUiStandard
            }
        }
    }
}

val LocalShadeShapeScheme = staticCompositionLocalOf {
    ShadeShapeScheme.fromTileShape(TileShape.SQUIRCLE)
}
