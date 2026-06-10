package com.matkurban.chatcontextmenu.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.layout.SubcomposeLayout
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import com.matkurban.chatcontextmenu.geometry.toBodyRoundRect
import com.matkurban.chatcontextmenu.model.ArrowHorizontalDirection
import com.matkurban.chatcontextmenu.model.ArrowVerticalDirection
import com.matkurban.chatcontextmenu.ui.shape.horizontalMenuOuterPath
import com.matkurban.chatcontextmenu.ui.shape.verticalMenuOuterPath

@Composable
fun ChatContextMenuVerticalWidget(
    modifier: Modifier = Modifier,
    menuModifier: Modifier = Modifier,
    menuShape: Shape = RoundedCornerShape(8.dp),
    arrowOffset: Float?,
    arrowDirection: ArrowVerticalDirection,
    arrowHeight: Dp = 8.dp,
    arrowWidth: Dp = 12.dp,
    maxHeight: Dp? = null,
    content: @Composable () -> Unit,
) {
    MenuShapeContainer(
        modifier = modifier.then(
            if (maxHeight != null) Modifier.heightIn(max = maxHeight) else Modifier,
        ),
        menuModifier = menuModifier,
        menuShape = menuShape,
        arrowOffset = arrowOffset,
        arrowHeight = arrowHeight,
        arrowWidth = arrowWidth,
        verticalDirection = arrowDirection,
        horizontalDirection = null,
        content = content,
    )
}

@Composable
fun ChatContextMenuHorizontalWidget(
    modifier: Modifier = Modifier,
    menuModifier: Modifier = Modifier,
    menuShape: Shape = RoundedCornerShape(8.dp),
    arrowOffset: Float?,
    arrowDirection: ArrowHorizontalDirection,
    arrowHeight: Dp = 8.dp,
    arrowWidth: Dp = 12.dp,
    content: @Composable () -> Unit,
) {
    MenuShapeContainer(
        modifier = modifier,
        menuModifier = menuModifier,
        menuShape = menuShape,
        arrowOffset = arrowOffset,
        arrowHeight = arrowHeight,
        arrowWidth = arrowWidth,
        verticalDirection = null,
        horizontalDirection = arrowDirection,
        content = content,
    )
}

@Composable
private fun MenuShapeContainer(
    modifier: Modifier,
    menuModifier: Modifier,
    menuShape: Shape,
    arrowOffset: Float?,
    arrowHeight: Dp,
    arrowWidth: Dp,
    verticalDirection: ArrowVerticalDirection?,
    horizontalDirection: ArrowHorizontalDirection?,
    content: @Composable () -> Unit,
) {
    val density = LocalDensity.current
    val layoutDirection = LocalLayoutDirection.current
    var shape by remember { mutableStateOf<Shape?>(null) }
    val arrowHeightPx = with(density) { arrowHeight.toPx() }
    val arrowWidthPx = with(density) { arrowWidth.toPx() }

    SubcomposeLayout(
        modifier = modifier
            .wrapContentSize(align = Alignment.TopStart)
            .onSizeChanged { intSize ->
                val size = Size(intSize.width.toFloat(), intSize.height.toFloat())
                shape = if (arrowOffset != null && verticalDirection != null) {
                    pathShape {
                        verticalMenuOuterPath(
                            size = size,
                            arrowWidth = arrowWidthPx,
                            arrowHeight = arrowHeightPx,
                            arrowOffset = arrowOffset,
                            arrowDirection = verticalDirection,
                            menuShape = menuShape,
                            density = density,
                            layoutDirection = layoutDirection,
                        )
                    }
                } else if (arrowOffset != null && horizontalDirection != null) {
                    pathShape {
                        horizontalMenuOuterPath(
                            size = size,
                            arrowWidth = arrowWidthPx,
                            arrowHeight = arrowHeightPx,
                            arrowOffset = arrowOffset,
                            arrowDirection = horizontalDirection,
                            menuShape = menuShape,
                            density = density,
                            layoutDirection = layoutDirection,
                        )
                    }
                } else {
                    bodyShape(menuShape)
                }
            }
            .then(
                if (shape != null) Modifier.clip(shape!!) else Modifier,
            )
            .then(menuModifier),
    ) { constraints ->
        val placeable = subcompose("menu-content") {
            Box(
                modifier = Modifier
                    .wrapContentWidth(align = Alignment.Start)
                    .wrapContentHeight(),
            ) {
                content()
            }
        }.map { measurable ->
            measurable.measure(
                Constraints(
                    minWidth = 0,
                    maxWidth = constraints.maxWidth,
                    minHeight = 0,
                    maxHeight = constraints.maxHeight,
                ),
            )
        }.first()

        layout(placeable.width, placeable.height) {
            placeable.place(0, 0)
        }
    }
}

private fun pathShape(pathFactory: () -> Path): Shape = object : Shape {
    override fun createOutline(
        size: Size,
        layoutDirection: LayoutDirection,
        density: Density
    ): Outline {
        return Outline.Generic(pathFactory())
    }
}

private fun bodyShape(
    menuShape: Shape,
): Shape = object : Shape {
    override fun createOutline(
        size: Size,
        layoutDirection: LayoutDirection,
        density: Density
    ): Outline {
        return Outline.Rounded(
            menuShape.toBodyRoundRect(Rect(Offset.Zero, size), density, layoutDirection),
        )
    }
}
