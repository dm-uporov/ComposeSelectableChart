package com.github.dm.uporov.selectablechart

import androidx.compose.ui.graphics.Color

data class SimplePieChartSegmentData(
    override val weight: Double,
    override val color: Color,
) : PieChartSegmentData


interface PieChartSegmentData {
    val weight: Double
    val color: Color
}