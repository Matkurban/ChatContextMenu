package com.matkurban.chatcontextmenu.ui

import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.boundsInWindow
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.matkurban.chatcontextmenu.model.ContextMenuContent
import com.matkurban.chatcontextmenu.model.ContextMenuWidgetContent

@Composable
fun ChatContextMenuWrapper(
    widgetContent: ContextMenuWidgetContent,
    menuContent: ContextMenuContent,
    modifier: Modifier = Modifier,
    menuModifier: Modifier = Modifier,
    menuShape: Shape = RoundedCornerShape(8.dp),
    barrierColor: Color = Color.Transparent,
    arrowHeight: Dp = 8.dp,
    arrowWidth: Dp = 12.dp,
    spacing: Dp = 6.dp,
    transitionDurationMillis: Int = 150,
    onClose: (() -> Unit)? = null,
    horizontalMargin: Dp = 10.dp,
    layoutMaxHeight: Dp? = null,
    axis: MenuAxis = MenuAxis.Vertical,
    topPadding: Dp = 56.dp,
    excludeAnchorFromBarrier: Boolean = false,
    barrierAnchorModifier: Modifier = Modifier,
    barrierDismissible: Boolean = true,
) {
    val host = LocalChatContextMenuHost.current
        ?: error("ChatContextMenuWrapper requires ChatContextMenuHost ancestor")

    var isMenuVisible by remember { mutableStateOf(false) }
    var dismissRequested by remember { mutableStateOf(false) }
    var widgetRect by remember { mutableStateOf<Rect?>(null) }
    var holeRect by remember { mutableStateOf<Rect?>(null) }
    var pointerInWindow by remember { mutableStateOf<Offset?>(null) }
    val holeShape = findHoleShape(barrierAnchorModifier)

    val hideMenu: () -> Unit = {
        if (isMenuVisible) {
            dismissRequested = true
        }
    }

    val showMenu: () -> Unit = {
        val anchor = widgetRect
        val hole = holeRect
        if (anchor != null && hole != null && !isMenuVisible) {
            isMenuVisible = true
            dismissRequested = false
            host.show {
                ChatContextMenuOverlay(
                    config = ChatContextMenuOverlayConfig(
                        widgetRect = anchor,
                        holeRect = hole,
                        holeShape = holeShape,
                        pointerRect = pointerInWindow?.let { point ->
                            Rect(point.x, point.y, point.x + 1f, point.y + 1f)
                        },
                        menuModifier = menuModifier,
                        menuShape = menuShape,
                        barrierColor = barrierColor,
                        arrowHeight = arrowHeight,
                        arrowWidth = arrowWidth,
                        spacing = spacing,
                        horizontalMargin = horizontalMargin,
                        topPadding = topPadding,
                        layoutMaxHeight = layoutMaxHeight,
                        axis = axis,
                        excludeAnchorFromBarrier = excludeAnchorFromBarrier,
                        barrierDismissible = barrierDismissible,
                        transitionDurationMillis = transitionDurationMillis,
                    ),
                    dismissRequested = dismissRequested,
                    onDismissRequestedConsumed = { dismissRequested = false },
                    onDismissComplete = {
                        isMenuVisible = false
                        dismissRequested = false
                        host.hide()
                        onClose?.invoke()
                    },
                    menuContent = menuContent,
                )
            }
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            if (isMenuVisible) host.hide()
        }
    }

    Box(modifier = modifier) {
        Box(
            modifier = Modifier
                .then(barrierAnchorModifier)
                .onGloballyPositioned { coordinates ->
                    holeRect = coordinates.boundsInWindow()
                },
        ) {
            Box(
                modifier = Modifier
                    .onGloballyPositioned { coordinates ->
                        widgetRect = coordinates.boundsInWindow()
                    }
                    .pointerInput(Unit) {
                        detectTapGestures(
                            onPress = { local ->
                                val bounds = widgetRect
                                if (bounds != null) {
                                    pointerInWindow = Offset(
                                        bounds.left + local.x,
                                        bounds.top + local.y,
                                    )
                                }
                            },
                        )
                    },
            ) {
                widgetContent(showMenu, hideMenu)
            }
        }
    }
}
