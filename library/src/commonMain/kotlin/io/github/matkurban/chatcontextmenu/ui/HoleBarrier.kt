package io.github.matkurban.chatcontextmenu.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathFillType
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.IntOffset
import io.github.matkurban.chatcontextmenu.geometry.anchoredHoleShape
import kotlin.math.roundToInt

@Composable
fun HoleModalBarrier(
    color: Color,
    holeRect: Rect,
    holeShape: Shape?,
    modifier: Modifier = Modifier,
) {
    val density = LocalDensity.current
    val layoutDirection = LocalLayoutDirection.current
    val roundRect = anchoredHoleShape(
        holeLocalIntersectedViewport = holeRect,
        shape = holeShape,
        density = density,
        layoutDirection = layoutDirection,
    )
    Canvas(modifier = modifier.fillMaxSize()) {
        if (color.alpha == 0f) return@Canvas
        val background = Rect(Offset.Zero, size)
        val path = Path().apply {
            addRect(background)
            addRoundRect(roundRect)
            fillType = PathFillType.EvenOdd
        }
        drawPath(path, color)
    }
}

@Composable
fun HoleModalDismissScrim(
    holeRect: Rect,
    holeShape: Shape?,
    dismissible: Boolean,
    onDismiss: () -> Unit,
    useHolePassThrough: Boolean,
    viewportSize: Size,
    modifier: Modifier = Modifier,
) {
    if (!dismissible) return

    if (useHolePassThrough) {
        HoleDismissRegions(
            viewportSize = viewportSize,
            holeRect = holeRect,
            onDismiss = onDismiss,
            modifier = modifier,
        )
    } else {
        val density = LocalDensity.current
        val layoutDirection = LocalLayoutDirection.current
        val roundRect = anchoredHoleShape(
            holeLocalIntersectedViewport = holeRect,
            shape = holeShape,
            density = density,
            layoutDirection = layoutDirection,
        )
        Box(
            modifier = modifier
                .fillMaxSize()
                .pointerInput(roundRect) {
                    detectTapGestures { offset ->
                        if (!roundRect.contains(offset)) {
                            onDismiss()
                        }
                    }
                },
        )
    }
}

@Composable
fun HoleDismissRegions(
    viewportSize: Size,
    holeRect: Rect,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val density = LocalDensity.current
    Box(modifier = modifier.fillMaxSize()) {
        holeSurroundingRegions(viewportSize, holeRect).forEach { region ->
            if (region.width > 0f && region.height > 0f) {
                Box(
                    modifier = Modifier
                        .offset {
                            IntOffset(region.left.roundToInt(), region.top.roundToInt())
                        }
                        .size(
                            width = with(density) { region.width.toDp() },
                            height = with(density) { region.height.toDp() },
                        )
                        .pointerInput(Unit) {
                            detectTapGestures { onDismiss() }
                        },
                )
            }
        }
    }
}

fun holeSurroundingRegions(viewport: Size, hole: Rect): List<Rect> {
    return listOf(
        Rect(0f, 0f, viewport.width, hole.top.coerceAtLeast(0f)),
        Rect(0f, hole.bottom, viewport.width, viewport.height),
        Rect(0f, hole.top, hole.left.coerceAtLeast(0f), hole.bottom),
        Rect(hole.right, hole.top, viewport.width, hole.bottom),
    ).filter { it.width > 0f && it.height > 0f }
}