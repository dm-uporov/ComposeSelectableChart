package com.github.dm.uporov.selectablechart

import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlin.random.Random

private fun generateSegmentsList(): List<PieChartSegmentData> = listOf(
    randomSegment(),
    randomSegment(),
    randomSegment(),
    randomSegment(),
    randomSegment(),
)

private fun randomSegment() = SimplePieChartSegmentData(
    weight = Random.nextDouble(from = 0.1, until = 1.0),
    color = randomColor()
)

private fun randomColor() = Color(
    red = Random.nextInt(256),
    green = Random.nextInt(256),
    blue = Random.nextInt(256),
)

private const val MAX_COUNT = 20
private const val MIN_COUNT = 2

class MainViewModel : ViewModel() {

    private var segments = generateSegmentsList()
    private val segmentsCount
        get() = segments.size

    private val _uiState = MutableStateFlow(
        PieChartState(
            segments = segments,
            selectedSegmentIndex = 0,
            possibleToAdd = true,
            possibleToRemove = true,
        )
    )
    val uiState: StateFlow<PieChartState> = _uiState.asStateFlow()

    private val selectedSegment
        get() = _uiState.value.selectedSegmentIndex

    fun onNextClicked() {
        var nextSegment = selectedSegment + 1
        if (nextSegment >= segmentsCount) {
            nextSegment = 0
        }
        _uiState.update {
            it.copy(selectedSegmentIndex = nextSegment)
        }
    }

    fun onPreviousClicked() {
        var previousSegment = selectedSegment - 1
        if (previousSegment < 0) {
            previousSegment = segmentsCount - 1
        }
        _uiState.update {
            it.copy(selectedSegmentIndex = previousSegment)
        }
    }

    fun onRandomClicked() {
        _uiState.update {
            it.copy(selectedSegmentIndex = Random.nextInt(segmentsCount))
        }
    }

    fun onUnselectClicked() {
        _uiState.update {
            it.copy(selectedSegmentIndex = -1)
        }
    }

    fun onRemoveClicked() {
        segments = segments.toMutableList().apply { removeLast() }
        _uiState.update {
            it.copy(
                segments = segments,
                possibleToRemove = segmentsCount > MIN_COUNT,
                possibleToAdd = true
            )
        }
    }

    fun onAddClicked() {
        segments = listOf(*segments.toTypedArray(), randomSegment())
        _uiState.update {
            it.copy(
                segments = segments,
                possibleToRemove = true,
                possibleToAdd = segmentsCount < MAX_COUNT
            )
        }
    }
}