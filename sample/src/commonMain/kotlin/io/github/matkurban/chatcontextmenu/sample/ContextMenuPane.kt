package io.github.matkurban.chatcontextmenu.sample

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.Forward
import androidx.compose.material.icons.automirrored.outlined.Reply
import androidx.compose.material.icons.outlined.ContentCopy
import androidx.compose.material.icons.outlined.DeleteOutline
import androidx.compose.material.icons.outlined.FormatQuote
import androidx.compose.material.icons.outlined.Forward
import androidx.compose.material.icons.outlined.MoreVert
import androidx.compose.material.icons.outlined.Reply
import androidx.compose.material.icons.outlined.SelectAll
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.layoutId
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.dp

@Composable
fun ContextMenuPane(onAnyAction: () -> Unit) {
    Layout(
        content = {
            ContextMenuRow(
                modifier = Modifier.layoutId("row1"),
                onAnyAction = onAnyAction,
                items = listOf(
                    MenuAction(Icons.AutoMirrored.Outlined.Reply, "Reply"),
                    MenuAction(Icons.Outlined.ContentCopy, "Copy"),
                    MenuAction(Icons.AutoMirrored.Outlined.Forward, "Forward"),
                    MenuAction(Icons.Outlined.DeleteOutline, "Delete"),
                    MenuAction(Icons.Outlined.FormatQuote, "Quote"),
                ),
            )
            Box(
                modifier = Modifier
                    .layoutId("divider")
                    .height(1.dp)
                    .background(MaterialTheme.colorScheme.outlineVariant),
            )
            ContextMenuRow(
                modifier = Modifier.layoutId("row2"),
                onAnyAction = onAnyAction,
                items = listOf(
                    MenuAction(Icons.Outlined.SelectAll, "Select"),
                    MenuAction(Icons.Outlined.MoreVert, "More"),
                ),
            )
        },
    ) { measurables, constraints ->
        val dividerHeight = 1.dp.roundToPx()
        val row1 = measurables.first { it.layoutId == "row1" }
            .measure(Constraints(maxWidth = constraints.maxWidth))
        val row2 = measurables.first { it.layoutId == "row2" }
            .measure(Constraints(maxWidth = constraints.maxWidth))
        val width = maxOf(row1.width, row2.width)
        val divider = measurables.first { it.layoutId == "divider" }
            .measure(Constraints.fixed(width = width, height = dividerHeight))
        val height = row1.height + divider.height + row2.height
        layout(width = width, height = height) {
            row1.place(0, 0)
            divider.place(0, row1.height)
            row2.place(0, row1.height + divider.height)
        }
    }
}

private data class MenuAction(
    val icon: androidx.compose.ui.graphics.vector.ImageVector,
    val label: String,
)

@Composable
private fun ContextMenuRow(
    modifier: Modifier = Modifier,
    onAnyAction: () -> Unit,
    items: List<MenuAction>,
) {
    Row(modifier = modifier) {
        items.forEach { item ->
            ContextMenuItem(item.icon, item.label, onAnyAction)
        }
    }
}

@Composable
private fun ContextMenuItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    onClick: () -> Unit,
) {
    Column(
        modifier = Modifier
            .padding(vertical = 4.dp)
            .widthIn(min = 54.dp)
            .pointerInput(Unit) { detectTapGestures { onClick() } },
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Icon(icon, contentDescription = label, modifier = Modifier.padding(bottom = 2.dp))
        Text(label, style = MaterialTheme.typography.labelSmall)
    }
}
