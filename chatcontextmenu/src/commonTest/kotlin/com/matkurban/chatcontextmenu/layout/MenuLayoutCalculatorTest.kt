package com.matkurban.chatcontextmenu.layout

import com.matkurban.chatcontextmenu.model.ArrowHorizontalDirection
import com.matkurban.chatcontextmenu.model.ArrowVerticalDirection
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class MenuLayoutCalculatorTest {

    @Test
    fun verticalMenuPrefersBottomPlacement() {
        val result = calculateVerticalMenuLayout(
            VerticalMenuLayoutInput(
                widgetRect = FloatRect(100f, 200f, 200f, 240f),
                pointerRect = null,
                childWidth = 120f,
                childHeight = 80f,
                screenWidth = 400f,
                screenHeight = 800f,
                paddingTop = 0f,
                paddingBottom = 0f,
                viewInsetsBottom = 0f,
                arrowHeight = 8f,
                spacing = 6f,
                arrowWidth = 12f,
                horizontalMargin = 10f,
                borderRadiusMax = 8f,
                topPadding = 0f,
                layoutMaxHeight = null,
            ),
        )
        assertTrue(result.positionY >= 240f)
        assertEquals(ArrowVerticalDirection.Up, result.arrowDirection)
    }

    @Test
    fun horizontalMenuPrefersRightSide() {
        val result = calculateHorizontalMenuLayout(
            HorizontalMenuLayoutInput(
                widgetRect = FloatRect(100f, 200f, 200f, 260f),
                childWidth = 160f,
                childHeight = 100f,
                screenWidth = 400f,
                screenHeight = 800f,
                paddingLeft = 0f,
                paddingRight = 0f,
                paddingTop = 0f,
                paddingBottom = 0f,
                viewInsetsBottom = 0f,
                arrowHeight = 8f,
                spacing = 6f,
                arrowWidth = 12f,
                horizontalMargin = 10f,
                borderRadiusMax = 8f,
                topPadding = 0f,
            ),
        )
        assertTrue(result.positionX >= 200f)
        assertEquals(ArrowHorizontalDirection.Left, result.arrowDirection)
    }
}
