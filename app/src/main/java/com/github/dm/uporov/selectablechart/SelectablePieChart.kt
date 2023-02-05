package com.github.dm.uporov.selectablechart

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlin.math.min

@Composable
fun SelectablePieChart(
    segments: List<PieChartSegmentData>,
    modifier: Modifier = Modifier,
    indexOfSelectedState: State<Int>,
    segmentsThickness: Float = 100f,
    selectedSegmentsThickness: Float = 120f,
    rotationDegrees: Float = 0f,
    firstElementClockwise: FirstElementClockwise = FirstElementClockwise.START_OF_FIRST,
    spaceBetweenDegree: Float = 4f,
    segmentsPadding: Dp = 12.dp,
    selectedSegmentsPadding: Dp = 0.dp,
) {
    if (segments.isEmpty()) return

    val itemsSumWeight = segments.sumOf { it.weight }
    val pureSegmentsSpace = 360f - (segments.size * spaceBetweenDegree)
    val indexOfSelected = indexOfSelectedState.value

    var selectedItemStartAngle: Float? = null
    var selectedItemSweepAngle: Float? = null
    var nextStartAngle = 0f
    val items: List<SegmentItem> = segments.mapIndexed { index, item ->
        val segmentSweepAngle = (item.weight / itemsSumWeight * pureSegmentsSpace).toFloat()

        val padding: Dp
        val segmentThickness: Float
        if (index == indexOfSelected) {
            padding = selectedSegmentsPadding
            segmentThickness = selectedSegmentsThickness
            selectedItemStartAngle = nextStartAngle
            selectedItemSweepAngle = segmentSweepAngle
        } else {
            padding = segmentsPadding
            segmentThickness = segmentsThickness
        }
        val startAngle = nextStartAngle
        nextStartAngle += segmentSweepAngle + spaceBetweenDegree

        SegmentItem(
            startAngleDegrees = startAngle,
            sweepAngleDegrees = segmentSweepAngle,
            thickness = segmentThickness,
            color = item.color,
            padding = padding
        )
    }

    val rotation: Float = rotationDegrees + when (firstElementClockwise) {
        FirstElementClockwise.START_OF_FIRST -> 0f
        FirstElementClockwise.MIDDLE_OF_FIRST -> -(items.first().sweepAngleDegrees / 2f)
        FirstElementClockwise.END_OF_FIRST -> -(items.first().sweepAngleDegrees)
        FirstElementClockwise.START_OF_SELECTED -> selectedItemStartAngle ?: 0f
        FirstElementClockwise.MIDDLE_OF_SELECTED -> {
            val start = selectedItemStartAngle
            val sweep = selectedItemSweepAngle
            if (start == null || sweep == null) {
                0f
            } else {
                -start - (sweep / 2)
            }
        }
        FirstElementClockwise.END_OF_SELECTED -> {
            val start = selectedItemStartAngle
            val sweep = selectedItemSweepAngle
            if (start == null || sweep == null) {
                0f
            } else {
                -start - sweep
            }
        }
    }

    val rotationState by animateFloatAsState(targetValue = rotation)

    Box(
        modifier = modifier
            .squareSizeByMinSide()
            .rotate(rotationState)
    ) {
        items.forEach {
            val paddingState by animateDpAsState(targetValue = it.padding)
            val thicknessState by animateFloatAsState(targetValue = it.thickness)
            PieChartSegment(
                startAngleDegrees = it.startAngleDegrees,
                sweepAngleDegrees = it.sweepAngleDegrees,
                thickness = thicknessState,
                color = it.color,
                // TODO change to offset with lambda on performance reason
                modifier = Modifier.offset(paddingState)
            )
        }
    }
}

private data class SegmentItem(
    val startAngleDegrees: Float,
    val sweepAngleDegrees: Float,
    val thickness: Float,
    val color: Color,
    val padding: Dp,
)


enum class FirstElementClockwise {
    START_OF_FIRST,
    MIDDLE_OF_FIRST,
    END_OF_FIRST,
    START_OF_SELECTED,
    MIDDLE_OF_SELECTED,
    END_OF_SELECTED
}

@Composable
private fun PieChartSegment(
    startAngleDegrees: Float,
    sweepAngleDegrees: Float,
    thickness: Float,
    color: Color,
    modifier: Modifier = Modifier,
) {
    Canvas(modifier = modifier.fillMaxSize()) {
        val outerRadius = min(size.width, size.height) / 2f
        val innerRadius = outerRadius - thickness

        val path = Path().apply {
            arcTo(
                rect = Rect(
                    center = Offset(outerRadius, outerRadius),
                    radius = outerRadius
                ),
                startAngleDegrees = startAngleDegrees,
                sweepAngleDegrees = sweepAngleDegrees,
                forceMoveTo = true,
            )

            arcTo(
                rect = Rect(
                    center = Offset(outerRadius, outerRadius),
                    radius = innerRadius
                ),
                startAngleDegrees = startAngleDegrees + sweepAngleDegrees,
                sweepAngleDegrees = -sweepAngleDegrees,
                forceMoveTo = false,
            )
        }

        drawPath(path = path, color = color)
    }
}