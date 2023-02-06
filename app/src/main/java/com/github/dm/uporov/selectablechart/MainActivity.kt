package com.github.dm.uporov.selectablechart

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
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
                    MainScreen()
                }
            }
        }
    }
}

@Preview
@Composable
fun Preview() {
    MainScreen()
}

@Composable
fun MainScreen(
    modifier: Modifier = Modifier,
    mainViewModel: MainViewModel = viewModel()
) {
    val uiState by mainViewModel.uiState.collectAsState()

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
    ) {
        SelectablePieChart(
            segments = uiState.segments,
            indexOfSelectedSegment = uiState.selectedSegmentIndex,
            rotationDegrees = 180f,
            pointAtZeroDegreesClockwise = PointAtZeroDegreesClockwise.MIDDLE_OF_SELECTED_SEGMENT,
            modifier = Modifier
                .padding(16.dp)
        )
        Row {
            Button(onClick = mainViewModel::onPreviousClicked) {
                Text(text = "Select Previous".uppercase())
            }
            Spacer(modifier = Modifier.size(width = 8.dp, height = 0.dp))
            Button(onClick = mainViewModel::onNextClicked) {
                Text(text = "Select Next".uppercase())
            }
        }
        Row {
            Button(onClick = mainViewModel::onRandomClicked) {
                Text(text = "Select Random".uppercase())
            }
            Spacer(modifier = Modifier.size(width = 8.dp, height = 0.dp))
            Button(onClick = mainViewModel::onUnselectClicked) {
                Text(text = "Unselect".uppercase())
            }
        }

        Row {
            Button(
                onClick = mainViewModel::onRemoveClicked,
                enabled = uiState.possibleToRemove,
            ) {
                Text(text = "Remove".uppercase())
            }
            Spacer(modifier = Modifier.size(width = 8.dp, height = 0.dp))
            Button(
                onClick = mainViewModel::onAddClicked,
                enabled = uiState.possibleToAdd,
            ) {
                Text(text = "Add".uppercase())
            }
        }
    }
}
