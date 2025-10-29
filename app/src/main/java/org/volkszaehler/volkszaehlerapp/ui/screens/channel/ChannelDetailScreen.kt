package org.volkszaehler.volkszaehlerapp.ui.screens.channel

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import org.volkszaehler.volkszaehlerapp.domain.model.Channel
import org.volkszaehler.volkszaehlerapp.domain.model.ChannelData
import org.volkszaehler.volkszaehlerapp.domain.model.ChannelType
import java.text.SimpleDateFormat
import java.util.*

/**
 * Channel Detail Screen showing detailed information and data
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChannelDetailScreen(
    uuid: String,
    onNavigateBack: () -> Unit,
    viewModel: ChannelDetailViewModel = hiltViewModel()
) {
    val channel by viewModel.channel.collectAsState()
    val channelData by viewModel.channelData.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()

    LaunchedEffect(uuid) {
        viewModel.loadChannel(uuid)
        viewModel.loadChannelData(uuid)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(channel?.title ?: "Channel Details") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, "Back")
                    }
                }
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            when {
                isLoading -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
                error != null -> {
                    ErrorMessage(
                        error = error ?: "Unknown error",
                        onRetry = {
                            viewModel.loadChannel(uuid)
                            viewModel.loadChannelData(uuid)
                        },
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
                channel != null -> {
                    ChannelDetailContent(
                        channel = channel!!,
                        channelData = channelData,
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }
        }
    }
}

@Composable
private fun ChannelDetailContent(
    channel: Channel,
    channelData: ChannelData?,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier,
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Channel Info Card
        item {
            ChannelInfoCard(channel)
        }

        // Statistics Card
        if (channelData != null) {
            item {
                StatisticsCard(channelData)
            }
        }

        // Data Points
        if (channelData?.tuples?.isNotEmpty() == true) {
            item {
                Text(
                    text = "Recent Data",
                    style = MaterialTheme.typography.titleMedium
                )
            }
            items(channelData.tuples.take(20)) { tuple ->
                DataPointItem(
                    timestamp = tuple.timestamp,
                    value = tuple.value,
                    unit = channel.unit
                )
            }
        }
    }
}

@Composable
private fun ChannelInfoCard(channel: Channel) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = channel.title,
                style = MaterialTheme.typography.titleLarge
            )

            // ✅ KORRIGIERT: Konvertiere String zu ChannelType Enum
            InfoRow("Type", ChannelType.fromString(channel.type).displayName)
            InfoRow("UUID", channel.uuid)
            InfoRow("Unit", channel.unit)

            channel.description?.let {
                InfoRow("Description", it)
            }

            channel.cost?.let {
                InfoRow("Cost", "$it €/kWh")
            }
        }
    }
}

@Composable
private fun StatisticsCard(data: ChannelData) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = "Statistics",
                style = MaterialTheme.typography.titleMedium
            )

            data.min?.let { InfoRow("Minimum", it.toString()) }
            data.max?.let { InfoRow("Maximum", it.toString()) }
            data.average?.let { InfoRow("Average", it.toString()) }
            data.consumption?.let { InfoRow("Consumption", it.toString()) }

            InfoRow("Data Points", data.tuples.size.toString())
        }
    }
}

@Composable
private fun DataPointItem(
    timestamp: Long,
    value: Double,
    unit: String
) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = formatTimestamp(timestamp),
                style = MaterialTheme.typography.bodyMedium
            )
            Text(
                text = "$value $unit",
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

@Composable
private fun InfoRow(label: String, value: String) {
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
private fun ErrorMessage(
    error: String,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "Error: $error",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.error
        )
        Button(onClick = onRetry) {
            Text("Retry")
        }
    }
}

private fun formatTimestamp(timestamp: Long): String {
    val sdf = SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.getDefault())
    return sdf.format(Date(timestamp))
}
