package com.matkurban.chatcontextmenu.geometry

import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.LayoutDirection

fun anchoredHoleShape(
    holeLocalIntersectedViewport: Rect,
    shape: Shape?,
    density: Density,
    layoutDirection: LayoutDirection = LayoutDirection.Ltr,
): RoundRect {
    if (!holeLocalIntersectedViewport.isFinite || holeLocalIntersectedViewport.width <= 0f || holeLocalIntersectedViewport.height <= 0f) {
        return RoundRect(
            rect = Rect.Zero,
            topLeft = androidx.compose.ui.geometry.CornerRadius.Zero,
            topRight = androidx.compose.ui.geometry.CornerRadius.Zero,
            bottomRight = androidx.compose.ui.geometry.CornerRadius.Zero,
            bottomLeft = androidx.compose.ui.geometry.CornerRadius.Zero,
        )
    }
    if (shape == null) {
        return RoundRect(
            rect = holeLocalIntersectedViewport,
            topLeft = androidx.compose.ui.geometry.CornerRadius.Zero,
            topRight = androidx.compose.ui.geometry.CornerRadius.Zero,
            bottomRight = androidx.compose.ui.geometry.CornerRadius.Zero,
            bottomLeft = androidx.compose.ui.geometry.CornerRadius.Zero,
        )
    }
    return shape.toBodyRoundRect(holeLocalIntersectedViewport, density, layoutDirection)
}
