package io.github.matkurban.chatcontextmenu.geometry

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import kotlin.test.Test
import kotlin.test.assertEquals

class HoleCutoutTest {

    private val density = Density(1f)
    private val layoutDirection = LayoutDirection.Ltr

    @Test
    fun anchoredHoleShapeUsesRectangularHoleWhenShapeIsNull() {
        val hole = Rect(10f, 20f, 30f, 40f)
        val result = anchoredHoleShape(hole, shape = null, density, layoutDirection)
        assertEquals(hole.left, result.left)
        assertEquals(hole.top, result.top)
        assertEquals(hole.right, result.right)
        assertEquals(hole.bottom, result.bottom)
        assertEquals(0f, result.topLeftCornerRadius.x)
    }

    @Test
    fun anchoredHoleShapeUsesRoundedCornersFromShape() {
        val hole = Rect(0f, 0f, 100f, 50f)
        val result = anchoredHoleShape(
            hole,
            shape = RoundedCornerShape(8.dp),
            density,
            layoutDirection,
        )
        assertEquals(8f, result.topLeftCornerRadius.x)
        assertEquals(8f, result.topRightCornerRadius.x)
    }
}
