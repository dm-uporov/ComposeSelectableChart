package com.github.dm.uporov.selectablechart

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.animation.ToolingState
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.github.dm.uporov.selectablechart.ui.theme.ComposeCustomLayoutTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ComposeCustomLayoutTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    var selected by remember { mutableStateOf(false) }
                    val weight by animateFloatAsState(if (selected) 0.5f else 0.5f)
                    val indexOfSelected: MutableState<Int> = remember { mutableStateOf(0) }
                    val items = listOf(
                        PieChartSegmentSimpleData(0.2, Color.Red),
                        PieChartSegmentSimpleData(0.32, Color.Blue),
                        PieChartSegmentSimpleData(0.21, Color.Blue),
                        PieChartSegmentSimpleData(0.25, Color.Blue),
                        PieChartSegmentSimpleData(0.2, Color.Blue),
                        PieChartSegmentSimpleData(weight.toDouble(), Color.Cyan),
                        PieChartSegmentSimpleData(0.4, Color.Magenta),
                    )

                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        SelectablePieChart(
                            segments = items,
                            indexOfSelectedState = indexOfSelected,
                            rotationDegrees = 180f,
                            firstElementClockwise = FirstElementClockwise.MIDDLE_OF_SELECTED,
                            modifier = Modifier
                                .padding(16.dp)
                        )
                        Button(onClick = {
                            selected = !selected
                            val newIndex = indexOfSelected.value + 1
                            if (newIndex < items.size) {
                                indexOfSelected.value = newIndex
                            } else {
                                indexOfSelected.value = 0
                            }
                        }) {
                            Text(text = "Click me!")
                        }
                    }
                }
            }
        }
    }
}

@Preview
@Composable
fun Preview() {
    val items = listOf(
        PieChartSegmentSimpleData(0.1, Color.Red),
        PieChartSegmentSimpleData(0.3, Color.Blue),
        PieChartSegmentSimpleData(0.45, Color.Cyan),
        PieChartSegmentSimpleData(0.2, Color.Magenta),
    )
    SelectablePieChart(
        segments = items,
        modifier = Modifier.padding(16.dp),
        indexOfSelectedState = ToolingState(0),
        rotationDegrees = 180f,
        firstElementClockwise = FirstElementClockwise.MIDDLE_OF_SELECTED,
    )
}
