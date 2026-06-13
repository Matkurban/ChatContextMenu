package io.github.matkurban.chatcontextmenu.model

import androidx.compose.runtime.Composable

typealias ContextMenuWidgetContent = @Composable (
    showMenu: () -> Unit,
    hideMenu: () -> Unit,
) -> Unit

typealias ContextMenuContent = @Composable (
    hideMenu: () -> Unit,
) -> Unit
