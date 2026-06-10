package com.matkurban.chatcontextmenu.ui

import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Shape

private data class HoleShapeElement(val shape: Shape) : Modifier.Element

fun Modifier.holeShape(shape: Shape): Modifier = this.then(HoleShapeElement(shape))

fun findHoleShape(modifier: Modifier): Shape? {
    var result: Shape? = null
    modifier.foldIn(Unit) { _, element ->
        if (element is HoleShapeElement) {
            result = element.shape
        }
    }
    return result
}
