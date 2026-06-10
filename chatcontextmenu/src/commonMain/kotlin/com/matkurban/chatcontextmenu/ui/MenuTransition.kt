package com.matkurban.chatcontextmenu.ui

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.graphicsLayer

@Composable
fun AnimatedMenuLayer(
    visible: Boolean,
    transitionDurationMillis: Int,
    pivotFractionX: Float,
    pivotFractionY: Float,
    onProgress: (Float) -> Unit = {},
    onExitComplete: () -> Unit = {},
    content: @Composable () -> Unit,
) {
    val progress = remember { Animatable(0f) }

    LaunchedEffect(visible) {
        progress.animateTo(
            targetValue = if (visible) 1f else 0f,
            animationSpec = tween(
                durationMillis = transitionDurationMillis,
                easing = FastOutSlowInEasing,
            ),
        )
        if (!visible) {
            onExitComplete()
        }
    }

    val currentProgress = progress.value
    SideEffect {
        onProgress(currentProgress)
    }

    Box(
        modifier = Modifier
            .wrapContentSize(align = Alignment.TopStart)
            .graphicsLayer {
                alpha = currentProgress
                scaleX = currentProgress
                scaleY = currentProgress
                transformOrigin = TransformOrigin(pivotFractionX, pivotFractionY)
            },
    ) {
        content()
    }
}
