package io.github.matkurban.chatcontextmenu.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier

class ChatContextMenuHostState {
    internal var overlay by mutableStateOf<(@Composable () -> Unit)?>(null)

    internal fun show(content: @Composable () -> Unit) {
        overlay = content
    }

    internal fun hide() {
        overlay = null
    }
}

val LocalChatContextMenuHost = compositionLocalOf<ChatContextMenuHostState?> { null }

@Composable
fun ChatContextMenuHost(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    val hostState = remember { ChatContextMenuHostState() }
    CompositionLocalProvider(LocalChatContextMenuHost provides hostState) {
        Box(modifier = modifier.fillMaxSize()) {
            content()
            hostState.overlay?.invoke()
        }
    }
}
