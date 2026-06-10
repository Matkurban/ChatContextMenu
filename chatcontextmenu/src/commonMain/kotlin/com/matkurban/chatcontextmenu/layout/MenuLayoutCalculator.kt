package com.matkurban.chatcontextmenu.layout

import com.matkurban.chatcontextmenu.model.ArrowHorizontalDirection
import com.matkurban.chatcontextmenu.model.ArrowVerticalDirection
import kotlin.math.max

data class VerticalMenuLayoutResult(
    val positionX: Float,
    val positionY: Float,
    val childWidth: Float,
    val childHeight: Float,
    val arrowOffset: Float?,
    val arrowDirection: ArrowVerticalDirection,
    val maxHeight: Float?,
)

data class VerticalMenuLayoutInput(
    val widgetRect: FloatRect,
    val pointerRect: FloatRect?,
    val childWidth: Float,
    val childHeight: Float,
    val screenWidth: Float,
    val screenHeight: Float,
    val paddingTop: Float,
    val paddingBottom: Float,
    val viewInsetsBottom: Float,
    val arrowHeight: Float,
    val spacing: Float,
    val arrowWidth: Float,
    val horizontalMargin: Float,
    val borderRadiusMax: Float,
    val topPadding: Float,
    val layoutMaxHeight: Float?,
)

data class FloatRect(
    val left: Float,
    val top: Float,
    val right: Float,
    val bottom: Float,
) {
    val centerX: Float get() = (left + right) / 2f
    val centerY: Float get() = (top + bottom) / 2f
}

fun calculateVerticalMenuLayout(input: VerticalMenuLayoutInput): VerticalMenuLayoutResult {
    val widgetRect = input.widgetRect
    val topLimit = input.paddingTop + input.topPadding
    val screenBottomLimit = input.screenHeight - (input.paddingBottom + input.viewInsetsBottom)
    val availableHeightFromConstraints = input.layoutMaxHeight?.takeIf { it.isFinite() }
        ?: (screenBottomLimit - topLimit)
    val bottomLimit = topLimit + availableHeightFromConstraints

    val availableHeight = bottomLimit - topLimit
    val maxChildHeight = max(0f, availableHeight - input.arrowHeight)
    var constrainedChildHeight = input.childHeight
    var effectiveSpacing = input.spacing

    if (constrainedChildHeight > maxChildHeight) {
        constrainedChildHeight = maxChildHeight
        effectiveSpacing = 0f
    }

    val menuHeight = constrainedChildHeight + input.arrowHeight
    val totalHeight = menuHeight + effectiveSpacing
    val widgetBottomSpace = bottomLimit - widgetRect.bottom
    val widgetTopSpace = widgetRect.top - topLimit
    val fitsWithWidgetAnchor = widgetTopSpace >= totalHeight || widgetBottomSpace >= totalHeight
    val usePointerAnchor = input.pointerRect != null && !fitsWithWidgetAnchor

    val anchorRect = if (usePointerAnchor) input.pointerRect else widgetRect
    val bottomSpace = bottomLimit - anchorRect.bottom
    val topSpace = anchorRect.top - topLimit

    var isArrowUp = ArrowVerticalDirection.Up
    var y = anchorRect.bottom + effectiveSpacing

    if (y + menuHeight > bottomLimit) {
        if (topSpace > totalHeight) {
            y = anchorRect.top - constrainedChildHeight - input.arrowHeight - effectiveSpacing
            isArrowUp = ArrowVerticalDirection.Down
        } else {
            if (topSpace > bottomSpace) {
                y = anchorRect.top - constrainedChildHeight - input.arrowHeight - effectiveSpacing
                isArrowUp = ArrowVerticalDirection.Down
            } else {
                if (y + totalHeight > bottomLimit) {
                    val maxY = bottomLimit - constrainedChildHeight - input.arrowHeight
                    if (maxY <= anchorRect.bottom) {
                        y =
                            anchorRect.top - constrainedChildHeight - input.arrowHeight - effectiveSpacing
                        isArrowUp = ArrowVerticalDirection.Down
                    } else {
                        y = max(maxY, anchorRect.bottom + effectiveSpacing)
                    }
                }
            }
        }
    }

    val maxY = bottomLimit - menuHeight
    if (y < topLimit) y = topLimit
    if (y > maxY) y = maxY

    var x = anchorRect.centerX - input.childWidth / 2f
    if (x < input.horizontalMargin) x = input.horizontalMargin
    if (x + input.childWidth > input.screenWidth - input.horizontalMargin) {
        x = input.screenWidth - input.childWidth - input.horizontalMargin
    }

    var arrowOffset = anchorRect.centerX - x
    val safeMargin = input.borderRadiusMax + input.arrowWidth / 2f
    if (arrowOffset < safeMargin) arrowOffset = safeMargin
    if (arrowOffset > input.childWidth - safeMargin) {
        arrowOffset = input.childWidth - safeMargin
    }

    val maxHeight = if (constrainedChildHeight < input.childHeight) constrainedChildHeight else null

    return VerticalMenuLayoutResult(
        positionX = x,
        positionY = y,
        childWidth = input.childWidth,
        childHeight = constrainedChildHeight,
        arrowOffset = arrowOffset,
        arrowDirection = isArrowUp,
        maxHeight = maxHeight,
    )
}

data class HorizontalMenuLayoutResult(
    val positionX: Float,
    val positionY: Float,
    val childWidth: Float,
    val childHeight: Float,
    val arrowOffset: Float?,
    val arrowDirection: ArrowHorizontalDirection,
)

data class HorizontalMenuLayoutInput(
    val widgetRect: FloatRect,
    val childWidth: Float,
    val childHeight: Float,
    val screenWidth: Float,
    val screenHeight: Float,
    val paddingLeft: Float,
    val paddingRight: Float,
    val paddingTop: Float,
    val paddingBottom: Float,
    val viewInsetsBottom: Float,
    val arrowHeight: Float,
    val spacing: Float,
    val arrowWidth: Float,
    val horizontalMargin: Float,
    val borderRadiusMax: Float,
    val topPadding: Float,
)

fun calculateHorizontalMenuLayout(input: HorizontalMenuLayoutInput): HorizontalMenuLayoutResult {
    val widgetRect = input.widgetRect
    val leftLimit = input.paddingLeft + input.horizontalMargin
    val rightLimit = input.screenWidth - input.paddingRight - input.horizontalMargin
    val topLimit = input.paddingTop + input.topPadding
    val bottomLimit = input.screenHeight - (input.paddingBottom + input.viewInsetsBottom)

    val menuTotalWidth = input.childWidth + input.arrowHeight
    val rightSpace = rightLimit - widgetRect.right - input.spacing
    val leftSpace = widgetRect.left - leftLimit - input.spacing

    val arrowDirection: ArrowHorizontalDirection
    val x: Float

    val fitsRight = rightSpace >= menuTotalWidth
    val fitsLeft = leftSpace >= menuTotalWidth

    if (fitsRight) {
        x = widgetRect.right + input.spacing
        arrowDirection = ArrowHorizontalDirection.Left
    } else if (fitsLeft) {
        x = widgetRect.left - input.spacing - input.childWidth - input.arrowHeight
        arrowDirection = ArrowHorizontalDirection.Right
    } else {
        if (leftSpace > rightSpace) {
            var leftX = widgetRect.left - input.spacing - input.childWidth - input.arrowHeight
            arrowDirection = ArrowHorizontalDirection.Right
            if (leftX < leftLimit) leftX = leftLimit
            x = leftX
        } else {
            var rightX = widgetRect.right + input.spacing
            arrowDirection = ArrowHorizontalDirection.Left
            if (rightX + menuTotalWidth > rightLimit) {
                rightX = rightLimit - menuTotalWidth
            }
            x = rightX
        }
    }

    val widgetCenterY = widgetRect.centerY
    val safeMargin = input.borderRadiusMax + input.arrowWidth / 2f
    val topAlignedY = widgetRect.top
    val fitsWithTopAlign = topAlignedY + input.childHeight <= bottomLimit - input.horizontalMargin

    val y = if (fitsWithTopAlign) {
        maxOf(topAlignedY, topLimit + input.horizontalMargin)
    } else {
        maxOf(widgetRect.bottom - input.childHeight, topLimit + input.horizontalMargin)
    }

    var arrowOffset = widgetCenterY - y
    if (arrowOffset < safeMargin) arrowOffset = safeMargin
    if (arrowOffset > input.childHeight - safeMargin) {
        arrowOffset = input.childHeight - safeMargin
    }

    return HorizontalMenuLayoutResult(
        positionX = x,
        positionY = y,
        childWidth = input.childWidth,
        childHeight = input.childHeight,
        arrowOffset = arrowOffset,
        arrowDirection = arrowDirection,
    )
}

fun maxBorderRadius(
    topStart: Float,
    topEnd: Float,
    bottomStart: Float,
    bottomEnd: Float,
): Float = maxOf(topStart, topEnd, bottomStart, bottomEnd)
