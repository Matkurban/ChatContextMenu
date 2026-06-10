package com.matkurban.chatcontextmenu.ui.shape

import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathOperation
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.LayoutDirection
import com.matkurban.chatcontextmenu.geometry.toBodyRoundRect
import com.matkurban.chatcontextmenu.model.ArrowHorizontalDirection
import com.matkurban.chatcontextmenu.model.ArrowVerticalDirection

fun verticalMenuOuterPath(
    size: Size,
    arrowWidth: Float,
    arrowHeight: Float,
    arrowOffset: Float,
    arrowDirection: ArrowVerticalDirection,
    menuShape: Shape,
    density: Density,
    layoutDirection: LayoutDirection,
): Path {
    val left = 0f
    val right = size.width
    val top = if (arrowDirection == ArrowVerticalDirection.Up) arrowHeight else 0f
    val bottom =
        if (arrowDirection == ArrowVerticalDirection.Up) size.height else size.height - arrowHeight

    val rrect = menuShape.toBodyRoundRect(
        bodyRect = Rect(left, top, right, bottom),
        density = density,
        layoutDirection = layoutDirection,
    )

    val path = Path().apply { addRoundRect(rrect) }
    val arrowX = left + arrowOffset
    val arrowPath = Path().apply {
        when (arrowDirection) {
            ArrowVerticalDirection.Up -> {
                moveTo(arrowX - arrowWidth / 2f, top)
                lineTo(arrowX, 0f)
                lineTo(arrowX + arrowWidth / 2f, top)
            }

            ArrowVerticalDirection.Down -> {
                moveTo(arrowX - arrowWidth / 2f, bottom)
                lineTo(arrowX, size.height)
                lineTo(arrowX + arrowWidth / 2f, bottom)
            }
        }
        close()
    }
    return Path.combine(PathOperation.Union, path, arrowPath)
}

fun horizontalMenuOuterPath(
    size: Size,
    arrowWidth: Float,
    arrowHeight: Float,
    arrowOffset: Float,
    arrowDirection: ArrowHorizontalDirection,
    menuShape: Shape,
    density: Density,
    layoutDirection: LayoutDirection,
): Path {
    val left = if (arrowDirection == ArrowHorizontalDirection.Left) arrowHeight else 0f
    val right =
        if (arrowDirection == ArrowHorizontalDirection.Right) size.width - arrowHeight else size.width
    val top = 0f
    val bottom = size.height

    val rect = menuShape.toBodyRoundRect(
        bodyRect = Rect(left, top, right, bottom),
        density = density,
        layoutDirection = layoutDirection,
    )

    val path = Path().apply { addRoundRect(rect) }
    val arrowY = top + arrowOffset
    val arrowPath = Path().apply {
        when (arrowDirection) {
            ArrowHorizontalDirection.Left -> {
                moveTo(left, arrowY - arrowWidth / 2f)
                lineTo(0f, arrowY)
                lineTo(left, arrowY + arrowWidth / 2f)
            }

            ArrowHorizontalDirection.Right -> {
                moveTo(right, arrowY - arrowWidth / 2f)
                lineTo(size.width, arrowY)
                lineTo(right, arrowY + arrowWidth / 2f)
            }
        }
        close()
    }
    return Path.combine(PathOperation.Union, path, arrowPath)
}
