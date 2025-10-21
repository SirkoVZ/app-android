package org.volkszaehler.volkszaehlerapp.ui.chart

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import co.yml.charts.axis.AxisData
import co.yml.charts.common.model.Point
import co.yml.charts.ui.linechart.LineChart
import co.yml.charts.ui.linechart.model.*
import org.volkszaehler.volkszaehlerapp.data.model.ChannelData
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChartScreen(
    channelUuid: String,
    onNavigateBack: () -> Unit,
    viewModel: ChartViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Diagramm") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Zurück")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
        ) {
            // Zeitbereich-Auswahl
            TimeRangeSelector(
                selectedRange = uiState.timeRange,
                onRangeSelected = { viewModel.loadData(it) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            )

            when {
                uiState.isLoading -> {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(400.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }

                uiState.error != null -> {
                    ErrorMessage(
                        message = uiState.error ?: "Unbekannter Fehler",
                        onRetry = { viewModel.loadData() }
                    )
                }

                uiState.channelData != null -> {
                    ChartContent(
                        channelData = uiState.channelData!!,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(400.dp)
                            .padding(16.dp)
                    )

                    // Statistiken
                    StatisticsCard(
                        channelData = uiState.channelData!!,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun TimeRangeSelector(
    selectedRange: TimeRange,
    onRangeSelected: (TimeRange) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        TimeRange.values().forEach { range ->
            FilterChip(
                selected = selectedRange == range,
                onClick = { onRangeSelected(range) },
                label = { Text(range.label) }
            )
        }
    }
}

@Composable
fun ChartContent(
    channelData: ChannelData,
    modifier: Modifier = Modifier
) {
    if (channelData.tuples.isEmpty()) {
        Box(
            modifier = modifier,
            contentAlignment = Alignment.Center
        ) {
            Text("Keine Daten verfügbar")
        }
        return
    }

    val points = channelData.tuples.mapIndexed { index, tuple ->
        Point(index.toFloat(), tuple.value.toFloat())
    }

    val xAxisData = AxisData.Builder()
        .axisStepSize(100.dp)
        .steps(points.size - 1)
        .labelData { index ->
            if (index < channelData.tuples.size) {
                val date = Date(channelData.tuples[index].timestamp)
                SimpleDateFormat("HH:mm", Locale.getDefault()).format(date)
            } else ""
        }
        .build()

    val yAxisData = AxisData.Builder()
        .steps(5)
        .labelData { index ->
            val max = points.maxOfOrNull { it.y } ?: 0f
            val min = points.minOfOrNull { it.y } ?: 0f
            val step = (max - min) / 5
            String.format("%.1f", min + (step * index))
        }
        .build()

    val lineChartData = LineChartData(
        linePlotData = LinePlotData(
            lines = listOf(
                Line(
                    dataPoints = points,
                    lineStyle = LineStyle(
                        color = MaterialTheme.colorScheme.primary
                    ),
                    intersectionPoint = IntersectionPoint(
                        color = MaterialTheme.colorScheme.primary
                    ),
                    selectionHighlightPoint = SelectionHighlightPoint(
                        color = MaterialTheme.colorScheme.primary
                    ),
                    shadowUnderLine = ShadowUnderLine(
                        alpha = 0.3f,
                        brush = androidx.compose.ui.graphics.Brush.verticalGradient(
                            colors = listOf(
                                MaterialTheme.colorScheme.primary.copy(alpha = 0.3f),
                                Color.Transparent
                            )
                        )
                    ),
                    selectionHighlightPopUp = SelectionHighlightPopUp()
                )
            )
        ),
        xAxisData = xAxisData,
        yAxisData = yAxisData,
        gridLines = GridLines()
    )

    Card(modifier = modifier) {
        LineChart(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            lineChartData = lineChartData
        )
    }
}

@Composable
fun StatisticsCard(
    channelData: ChannelData,
    modifier: Modifier = Modifier
) {
    Card(modifier = modifier) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = "Statistiken",
                style = MaterialTheme.typography.titleMedium
            )

            Divider()

            StatisticRow("Minimum", "${channelData.min} ${channelData.unit}")
            StatisticRow("Maximum", "${channelData.max} ${channelData.unit}")
            StatisticRow("Durchschnitt", "${channelData.average} ${channelData.unit}")
            StatisticRow("Verbrauch", "${channelData.consumption} ${channelData.unit}")
            StatisticRow("Datenpunkte", "${channelData.tuples.size}")
        }
    }
}

@Composable
fun StatisticRow(
    label: String,
    value: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium
        )
    }
}

@Composable
fun ErrorMessage(
    message: String,
    onRetry: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = message,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.error
        )
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = onRetry) {
            Text("Erneut versuchen")
        }
    }
}