package com.github.dm.uporov.selectablechart

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import kotlin.math.cos
import kotlin.math.min
import kotlin.math.sin

private const val MAX_DEGREES = 360f

@Composable
fun SelectablePieChart(
    segments: List<PieChartSegmentData>,
    indexOfSelectedSegment: Int,
    modifier: Modifier = Modifier,
    segmentsThickness: Float = 100f,
    selectedSegmentsThickness: Float = 120f,
    rotationDegrees: Float = 0f,
    pointAtZeroDegreesClockwise: PointAtZeroDegreesClockwise = PointAtZeroDegreesClockwise.START_OF_FIRST_SEGMENT,
    spaceBetweenSegmentsDegree: Float = 3f,
    segmentsOffset: Dp = 0.dp,
    selectedSegmentsOffset: Dp = 12.dp,
    rotationAnimationSpec: AnimationSpec<Float> = defaultTweenRotationAnimationSpec
) {
    if (segments.isEmpty()) return

    val itemsSumWeight = segments.sumOf { it.weight }
    val pureSegmentsSpace = MAX_DEGREES - (segments.size * spaceBetweenSegmentsDegree)

    var selectedItemStartAngle: Float? = null
    var selectedItemSweepAngle: Float? = null
    var nextStartAngle = 0f
    val items: List<SegmentItem> = segments.mapIndexed { index, item ->
        val segmentSweepAngle = (item.weight / itemsSumWeight * pureSegmentsSpace).toFloat()

        val offset: Dp
        val segmentThickness: Float
        if (index == indexOfSelectedSegment) {
            offset = selectedSegmentsOffset
            segmentThickness = selectedSegmentsThickness
            selectedItemStartAngle = nextStartAngle
            selectedItemSweepAngle = segmentSweepAngle
        } else {
            offset = segmentsOffset
            segmentThickness = segmentsThickness
        }
        val startAngle = nextStartAngle
        nextStartAngle += segmentSweepAngle + spaceBetweenSegmentsDegree

        SegmentItem(
            startAngleDegrees = startAngle,
            sweepAngleDegrees = segmentSweepAngle,
            thickness = segmentThickness,
            color = item.color,
            offset = offset
        )
    }

    val rotation: Float = rotationDegrees + when (pointAtZeroDegreesClockwise) {
        PointAtZeroDegreesClockwise.START_OF_FIRST_SEGMENT -> 0f
        PointAtZeroDegreesClockwise.MIDDLE_OF_FIRST_SEGMENT -> -(items.first().sweepAngleDegrees / 2f)
        PointAtZeroDegreesClockwise.END_OF_FIRST_SEGMENT -> -(items.first().sweepAngleDegrees)
        PointAtZeroDegreesClockwise.START_OF_SELECTED_SEGMENT -> selectedItemStartAngle ?: 0f
        PointAtZeroDegreesClockwise.MIDDLE_OF_SELECTED_SEGMENT -> {
            val start = selectedItemStartAngle
            val sweep = selectedItemSweepAngle
            if (start == null || sweep == null) {
                0f
            } else {
                -start - (sweep / 2)
            }
        }
        PointAtZeroDegreesClockwise.END_OF_SELECTED_SEGMENT -> {
            val start = selectedItemStartAngle
            val sweep = selectedItemSweepAngle
            if (start == null || sweep == null) {
                0f
            } else {
                -start - sweep
            }
        }
    }

    val rotationState by animateFloatAsState(rotation, rotationAnimationSpec)
    Box(
        modifier = modifier
            .squareSizeByMinSide()
            .rotate(rotationState)
    ) {
        items.forEach {
            val offsetState by animateDpAsState(targetValue = it.offset)
            val thicknessState by animateFloatAsState(targetValue = it.thickness)
            PieChartSegment(
                startAngleDegrees = it.startAngleDegrees,
                sweepAngleDegrees = it.sweepAngleDegrees,
                thickness = thicknessState,
                color = it.color,
                modifier = Modifier.offset {
                    val offsetPx = offsetState.toPx()
                    val median = it.startAngleDegrees + (it.sweepAngleDegrees / 2.0)
                    val rad = Math.toRadians(median)
                    val x = cos(rad) * offsetPx
                    val y = sin(rad) * offsetPx
                    IntOffset(x.toInt(), y.toInt())
                }
            )
        }
    }
}

private val defaultTweenRotationAnimationSpec: AnimationSpec<Float> = tween(
    durationMillis = 500,
    easing = Ease
)

private data class SegmentItem(
    val startAngleDegrees: Float,
    val sweepAngleDegrees: Float,
    val thickness: Float,
    val color: Color,
    val offset: Dp,
)


enum class PointAtZeroDegreesClockwise {
    START_OF_FIRST_SEGMENT,
    MIDDLE_OF_FIRST_SEGMENT,
    END_OF_FIRST_SEGMENT,
    START_OF_SELECTED_SEGMENT,
    MIDDLE_OF_SELECTED_SEGMENT,
    END_OF_SELECTED_SEGMENT
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