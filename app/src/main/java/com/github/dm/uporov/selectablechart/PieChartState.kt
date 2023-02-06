package com.github.dm.uporov.selectablechart

data class PieChartState(
    val segments: List<PieChartSegmentData>,
    val selectedSegmentIndex: Int,
    val possibleToRemove: Boolean,
    val possibleToAdd: Boolean,
)
