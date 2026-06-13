package io.github.matkurban.chatcontextmenu.geometry

import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.LayoutDirection
import io.github.matkurban.chatcontextmenu.layout.maxBorderRadius

private val referenceSize = Size(1000f, 1000f)

fun Shape.toBodyRoundRect(
    bodyRect: Rect,
    density: Density,
    layoutDirection: LayoutDirection,
): RoundRect {
    if (bodyRect.width <= 0f || bodyRect.height <= 0f) {
        return RoundRect(bodyRect, CornerRadius.Zero)
    }
    val outline = createOutline(
        size = Size(bodyRect.width, bodyRect.height),
        layoutDirection = layoutDirection,
        density = density,
    )
    return when (outline) {
        is Outline.Rounded -> {
            val source = outline.roundRect
            RoundRect(
                rect = bodyRect,
                topLeft = source.topLeftCornerRadius,
                topRight = source.topRightCornerRadius,
                bottomRight = source.bottomRightCornerRadius,
                bottomLeft = source.bottomLeftCornerRadius,
            )
        }

        else -> RoundRect(bodyRect, CornerRadius.Zero)
    }
}

fun Shape.maxCornerRadiusPx(
    density: Density,
    layoutDirection: LayoutDirection,
): Float {
    return when (val outline = createOutline(referenceSize, layoutDirection, density)) {
        is Outline.Rounded -> maxBorderRadius(
            outline.roundRect.topLeftCornerRadius.x,
            outline.roundRect.topRightCornerRadius.x,
            outline.roundRect.bottomLeftCornerRadius.x,
            outline.roundRect.bottomRightCornerRadius.x,
        )

        else -> 0f
    }
}
