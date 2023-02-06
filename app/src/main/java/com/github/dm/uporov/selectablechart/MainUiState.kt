package com.github.dm.uporov.selectablechart

data class MainUiState(
    val segments: List<PieChartSegmentData>,
    val selectedSegmentIndex: Int,
    val possibleToRemove: Boolean,
    val possibleToAdd: Boolean,
)
