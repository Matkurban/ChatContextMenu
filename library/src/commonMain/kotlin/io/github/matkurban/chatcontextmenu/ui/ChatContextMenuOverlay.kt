package io.github.matkurban.chatcontextmenu.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import io.github.matkurban.chatcontextmenu.geometry.maxCornerRadiusPx
import io.github.matkurban.chatcontextmenu.layout.FloatRect
import io.github.matkurban.chatcontextmenu.layout.HorizontalMenuLayoutInput
import io.github.matkurban.chatcontextmenu.layout.VerticalMenuLayoutInput
import io.github.matkurban.chatcontextmenu.layout.calculateHorizontalMenuLayout
import io.github.matkurban.chatcontextmenu.layout.calculateVerticalMenuLayout
import io.github.matkurban.chatcontextmenu.model.ArrowHorizontalDirection
import io.github.matkurban.chatcontextmenu.model.ArrowVerticalDirection
import io.github.matkurban.chatcontextmenu.model.ContextMenuContent
import kotlin.math.roundToInt

data class ChatContextMenuOverlayConfig(
    val widgetRect: Rect,
    val holeRect: Rect,
    val holeShape: Shape?,
    val pointerRect: Rect?,
    val menuModifier: Modifier = Modifier,
    val menuShape: Shape = RoundedCornerShape(8.dp),
    val barrierColor: Color = Color.Transparent,
    val arrowHeight: Dp = 8.dp,
    val arrowWidth: Dp = 12.dp,
    val spacing: Dp = 6.dp,
    val horizontalMargin: Dp = 10.dp,
    val topPadding: Dp = 56.dp,
    val layoutMaxHeight: Dp? = null,
    val axis: MenuAxis = MenuAxis.Vertical,
    val excludeAnchorFromBarrier: Boolean = false,
    val barrierDismissible: Boolean = true,
    val transitionDurationMillis: Int = 150,
)

enum class MenuAxis {
    Vertical,
    Horizontal,
}

@Composable
fun ChatContextMenuOverlay(
    config: ChatContextMenuOverlayConfig,
    dismissRequested: Boolean,
    onDismissRequestedConsumed: () -> Unit,
    onDismissComplete: () -> Unit,
    menuContent: ContextMenuContent,
) {
    val density = LocalDensity.current
    val layoutDirection = LocalLayoutDirection.current
    val safePadding = WindowInsets.safeDrawing.asPaddingValues()
    val paddingTop = safePadding.calculateTopPadding()
    val paddingBottom = safePadding.calculateBottomPadding()
    val paddingLeft = safePadding.calculateStartPadding(LayoutDirection.Ltr)
    val paddingRight = safePadding.calculateEndPadding(LayoutDirection.Ltr)

    var menuVisible by remember { mutableStateOf(true) }
    var transitionProgress by remember { mutableFloatStateOf(0f) }

    val requestDismiss: () -> Unit = {
        if (menuVisible) {
            menuVisible = false
        }
    }

    LaunchedEffect(dismissRequested) {
        if (dismissRequested) {
            requestDismiss()
            onDismissRequestedConsumed()
        }
    }

    val barrierDismissible = config.barrierDismissible && menuVisible
    val animatedBarrierColor = config.barrierColor.copy(
        alpha = config.barrierColor.alpha * transitionProgress,
    )

    BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
        val screenWidthPx = with(density) { maxWidth.toPx() }
        val screenHeightPx = with(density) { maxHeight.toPx() }
        val screenSize = IntSize(screenWidthPx.roundToInt(), screenHeightPx.roundToInt())
        val viewportSize = Size(screenWidthPx, screenHeightPx)

        val anchorCenter = config.pointerRect?.center ?: config.widgetRect.center
        val pivotFractionX = (anchorCenter.x / screenWidthPx).coerceIn(0f, 1f)
        val pivotFractionY = (anchorCenter.y / screenHeightPx).coerceIn(0f, 1f)

        if (config.excludeAnchorFromBarrier) {
            if (animatedBarrierColor.alpha > 0f) {
                HoleModalBarrier(
                    color = animatedBarrierColor,
                    holeRect = config.holeRect,
                    holeShape = config.holeShape,
                )
            }
            HoleModalDismissScrim(
                holeRect = config.holeRect,
                holeShape = config.holeShape,
                dismissible = barrierDismissible,
                onDismiss = requestDismiss,
                useHolePassThrough = true,
                viewportSize = viewportSize,
            )
        } else if (barrierDismissible || animatedBarrierColor.alpha > 0f) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .then(
                        if (barrierDismissible) {
                            Modifier.clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = null,
                                onClick = requestDismiss,
                            )
                        } else {
                            Modifier
                        },
                    ),
            ) {
                if (animatedBarrierColor.alpha > 0f) {
                    CanvasBarrier(color = animatedBarrierColor)
                }
            }
        }

        var menuSize by remember { mutableStateOf(IntSize.Zero) }
        var measured by remember { mutableStateOf(false) }

        if (!measured) {
            Box(
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .alpha(0f)
                    .wrapContentSize()
                    .onSizeChanged { size ->
                        if (size.width > 0 && size.height > 0) {
                            menuSize = size
                            measured = true
                        }
                    },
            ) {
                MenuSurface(
                    config = config,
                    onDismiss = requestDismiss,
                    menuContent = menuContent,
                    arrowOffset = null,
                    arrowVerticalDirection = ArrowVerticalDirection.Down,
                    arrowHorizontalDirection = ArrowHorizontalDirection.Left,
                    maxHeight = null,
                )
            }
        } else {
            val menuOffset = when (config.axis) {
                MenuAxis.Vertical -> {
                    val result = calculateVerticalMenuLayout(
                        buildVerticalInput(
                            config,
                            screenSize,
                            menuSize.width.toFloat(),
                            menuSize.height.toFloat(),
                            paddingTop,
                            paddingBottom,
                            density,
                            layoutDirection,
                        ),
                    )
                    IntOffset(
                        result.positionX.roundToInt(),
                        result.positionY.roundToInt(),
                    ) to result
                }

                MenuAxis.Horizontal -> {
                    val result = calculateHorizontalMenuLayout(
                        buildHorizontalInput(
                            config,
                            screenSize,
                            menuSize.width.toFloat(),
                            menuSize.height.toFloat(),
                            paddingTop,
                            paddingBottom,
                            paddingLeft,
                            paddingRight,
                            density,
                            layoutDirection,
                        ),
                    )
                    IntOffset(
                        result.positionX.roundToInt(),
                        result.positionY.roundToInt(),
                    ) to result
                }
            }

            val (offset, layoutResult) = menuOffset

            AnimatedMenuLayer(
                visible = menuVisible,
                transitionDurationMillis = config.transitionDurationMillis,
                pivotFractionX = pivotFractionX,
                pivotFractionY = pivotFractionY,
                onProgress = { transitionProgress = it },
                onExitComplete = onDismissComplete,
            ) {
                Box(
                    modifier = Modifier
                        .offset { offset }
                        .wrapContentSize(),
                ) {
                    when (config.axis) {
                        MenuAxis.Vertical -> {
                            val result =
                                layoutResult as io.github.matkurban.chatcontextmenu.layout.VerticalMenuLayoutResult
                            MenuSurface(
                                config = config,
                                onDismiss = requestDismiss,
                                menuContent = menuContent,
                                arrowOffset = result.arrowOffset,
                                arrowVerticalDirection = result.arrowDirection,
                                arrowHorizontalDirection = ArrowHorizontalDirection.Left,
                                maxHeight = result.maxHeight?.let { with(density) { it.toDp() } },
                            )
                        }

                        MenuAxis.Horizontal -> {
                            val result =
                                layoutResult as io.github.matkurban.chatcontextmenu.layout.HorizontalMenuLayoutResult
                            MenuSurface(
                                config = config,
                                onDismiss = requestDismiss,
                                menuContent = menuContent,
                                arrowOffset = result.arrowOffset,
                                arrowVerticalDirection = ArrowVerticalDirection.Down,
                                arrowHorizontalDirection = result.arrowDirection,
                                maxHeight = null,
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun MenuSurface(
    config: ChatContextMenuOverlayConfig,
    onDismiss: () -> Unit,
    menuContent: ContextMenuContent,
    arrowOffset: Float?,
    arrowVerticalDirection: ArrowVerticalDirection,
    arrowHorizontalDirection: ArrowHorizontalDirection,
    maxHeight: Dp?,
) {
    when (config.axis) {
        MenuAxis.Vertical -> {
            ChatContextMenuVerticalWidget(
                menuModifier = config.menuModifier,
                menuShape = config.menuShape,
                arrowOffset = arrowOffset,
                arrowDirection = arrowVerticalDirection,
                arrowHeight = config.arrowHeight,
                arrowWidth = config.arrowWidth,
                maxHeight = maxHeight,
            ) {
                menuContent(onDismiss)
            }
        }

        MenuAxis.Horizontal -> {
            ChatContextMenuHorizontalWidget(
                menuModifier = config.menuModifier,
                menuShape = config.menuShape,
                arrowOffset = arrowOffset,
                arrowDirection = arrowHorizontalDirection,
                arrowHeight = config.arrowHeight,
                arrowWidth = config.arrowWidth,
            ) {
                menuContent(onDismiss)
            }
        }
    }
}

@Composable
private fun CanvasBarrier(color: Color) {
    HoleModalBarrier(
        color = color,
        holeRect = Rect(0f, 0f, 0f, 0f),
        holeShape = null,
    )
}

private fun buildVerticalInput(
    config: ChatContextMenuOverlayConfig,
    screenSize: IntSize,
    childWidth: Float,
    childHeight: Float,
    paddingTop: Dp,
    paddingBottom: Dp,
    density: androidx.compose.ui.unit.Density,
    layoutDirection: LayoutDirection,
) = VerticalMenuLayoutInput(
    widgetRect = config.widgetRect.toFloatRect(),
    pointerRect = config.pointerRect?.toFloatRect(),
    childWidth = childWidth,
    childHeight = childHeight,
    screenWidth = screenSize.width.toFloat(),
    screenHeight = screenSize.height.toFloat(),
    paddingTop = with(density) { paddingTop.toPx() },
    paddingBottom = with(density) { paddingBottom.toPx() },
    viewInsetsBottom = 0f,
    arrowHeight = with(density) { config.arrowHeight.toPx() },
    spacing = with(density) { config.spacing.toPx() },
    arrowWidth = with(density) { config.arrowWidth.toPx() },
    horizontalMargin = with(density) { config.horizontalMargin.toPx() },
    borderRadiusMax = config.menuShape.maxCornerRadiusPx(density, layoutDirection),
    topPadding = with(density) { config.topPadding.toPx() },
    layoutMaxHeight = config.layoutMaxHeight?.let { with(density) { it.toPx() } },
)

private fun buildHorizontalInput(
    config: ChatContextMenuOverlayConfig,
    screenSize: IntSize,
    childWidth: Float,
    childHeight: Float,
    paddingTop: Dp,
    paddingBottom: Dp,
    paddingLeft: Dp,
    paddingRight: Dp,
    density: androidx.compose.ui.unit.Density,
    layoutDirection: LayoutDirection,
) = HorizontalMenuLayoutInput(
    widgetRect = config.widgetRect.toFloatRect(),
    childWidth = childWidth,
    childHeight = childHeight,
    screenWidth = screenSize.width.toFloat(),
    screenHeight = screenSize.height.toFloat(),
    paddingLeft = with(density) { paddingLeft.toPx() },
    paddingRight = with(density) { paddingRight.toPx() },
    paddingTop = with(density) { paddingTop.toPx() },
    paddingBottom = with(density) { paddingBottom.toPx() },
    viewInsetsBottom = 0f,
    arrowHeight = with(density) { config.arrowHeight.toPx() },
    spacing = with(density) { config.spacing.toPx() },
    arrowWidth = with(density) { config.arrowWidth.toPx() },
    horizontalMargin = with(density) { config.horizontalMargin.toPx() },
    borderRadiusMax = config.menuShape.maxCornerRadiusPx(density, layoutDirection),
    topPadding = with(density) { config.topPadding.toPx() },
)

private fun Rect.toFloatRect(): FloatRect = FloatRect(left, top, right, bottom)
