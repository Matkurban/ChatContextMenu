package io.github.matkurban.chatcontextmenu.sample

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import io.github.matkurban.chatcontextmenu.ui.ChatContextMenuHost
import io.github.matkurban.chatcontextmenu.ui.ChatContextMenuWrapper
import io.github.matkurban.chatcontextmenu.ui.MenuAxis
import io.github.matkurban.chatcontextmenu.ui.holeShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CropFree
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.GroupAdd
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.outlined.MoreVert
import androidx.compose.material3.ListItem
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DemoApp() {
    MaterialTheme(
        colorScheme = if (isSystemInDarkTheme()) darkColorScheme() else lightColorScheme(),
    ) {
        ChatContextMenuHost {
            ChatContextMenuDemoScreen()
        }
    }
}

@Composable
private fun isSystemInDarkTheme(): Boolean = false

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ChatContextMenuDemoScreen() {
    val messages = rememberDemoMessages()
    val colorScheme = MaterialTheme.colorScheme
    val typography = MaterialTheme.typography

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Chat Context Menu") },
                actions = {
                    ChatContextMenuWrapper(
                        menuShape = RoundedCornerShape(8.dp),
                        menuModifier = Modifier.background(
                            color = Color.White,
                            shape = RoundedCornerShape(8.dp)
                        ).padding(8.dp),
                        spacing = 0.dp,
                        widgetContent = { showMenu, _ ->
                            IconButton(onClick = showMenu) {
                                Icon(Icons.Outlined.MoreVert, contentDescription = "Menu")
                            }
                        },
                        menuContent = { hideMenu ->
                            Column(
                                modifier = Modifier.width(IntrinsicSize.Max)
                            ) {
                                ListItem(
                                    leadingContent = {
                                        Icon(Icons.Filled.PersonAdd, contentDescription = null)
                                    },
                                    headlineContent = { Text(text = "添加朋友") },
                                )
                                ListItem(
                                    modifier = Modifier.fillMaxWidth(),
                                    leadingContent = {
                                        Icon(Icons.Filled.GroupAdd, contentDescription = null)
                                    },
                                    headlineContent = { Text(text = "添加群聊") },
                                )
                                ListItem(
                                    modifier = Modifier.fillMaxWidth(),
                                    leadingContent = {
                                        Icon(Icons.Filled.Group, contentDescription = null)
                                    },
                                    headlineContent = { Text(text = "创建群聊") },
                                )
                                ListItem(
                                    modifier = Modifier.fillMaxWidth(),
                                    leadingContent = {
                                        Icon(Icons.Filled.CropFree, contentDescription = null)
                                    },
                                    headlineContent = { Text(text = "扫一扫") },
                                )
                            }
                        },
                    )
                },
            )
        },
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
        ) {
            LazyColumn(
                modifier = Modifier.weight(1f),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                itemsIndexed(messages) { index, message ->
                    val isMe = index % 2 == 0
                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = if (isMe) Alignment.CenterEnd else Alignment.CenterStart,
                    ) {
                        ChatContextMenuWrapper(
                            menuShape = RoundedCornerShape(10.dp),
                            menuModifier = Modifier
                                .background(Color.White, RoundedCornerShape(10.dp))
                                .padding(8.dp),
                            barrierColor = Color.Black.copy(alpha = 0.26f),
                            excludeAnchorFromBarrier = true,
                            barrierAnchorModifier = Modifier.holeShape(RoundedCornerShape(8.dp)),
                            axis = MenuAxis.Vertical,
                            spacing = 2.dp,
                            transitionDurationMillis = 250,
                            widgetContent = { showMenu, _ ->
                                Box(
                                    modifier = Modifier
                                        .widthIn(max = 280.dp)
                                        .background(
                                            color = if (isMe) colorScheme.primary else colorScheme.surfaceContainerHighest,
                                            shape = RoundedCornerShape(8.dp),
                                        )
                                        .pointerInput(Unit) {
                                            detectTapGestures(onLongPress = { showMenu() })
                                        }
                                        .padding(horizontal = 12.dp, vertical = 8.dp),
                                ) {
                                    Text(
                                        text = message,
                                        style = typography.bodyLarge,
                                        color = if (isMe) colorScheme.onPrimary else colorScheme.onSurface,
                                    )
                                }
                            },
                            menuContent = { hideMenu ->
                                ContextMenuPane(onAnyAction = hideMenu)
                            },
                        )
                    }
                }
            }
            var draft by remember { mutableStateOf("") }
            TextField(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                value = draft,
                onValueChange = { draft = it },
                placeholder = { Text("Type a message...") },
            )
        }
    }
}

private fun rememberDemoMessages(): List<String> = listOf(
    "Hello!",
    "Hello!",
    "How are you?",
    "Im Fine hah",
    "and you?",
    "Im good too, thanks for asking.",
    "This is a long press context menu demo.",
    "Try long pressing on any message.",
    "You can see different options.",
    "Like Reply, Copy, Forward, Delete.",
    "It mimics the iOS style context menu.",
)
