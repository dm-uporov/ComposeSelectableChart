package com.github.dm.uporov.selectablechart

import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.layout
import androidx.compose.ui.unit.IntOffset
import kotlin.math.min


fun Modifier.squareSizeByMinSide() = layout { measurable, constraints ->
    val size = min(constraints.maxWidth, constraints.maxHeight)
    val placeable =
        measurable.measure(
            constraints.copy(
                minWidth = size,
                minHeight = size,
                maxWidth = size,
                maxHeight = size
            )
        )

    layout(size, size) {
        placeable.place(IntOffset.Zero)
    }
}