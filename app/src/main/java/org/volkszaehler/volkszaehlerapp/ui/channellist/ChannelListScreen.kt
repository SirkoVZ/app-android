package org.volkszaehler.volkszaehlerapp.ui.channellist

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import org.volkszaehler.volkszaehlerapp.data.model.Channel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChannelListScreen(
    onChannelClick: (String) -> Unit,
    viewModel: ChannelListViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val filteredChannels by viewModel.filteredChannels.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Volkszähler Kanäle") },
                actions = {
                    IconButton(onClick = { viewModel.loadChannels(forceRefresh = true) }) {
                        Icon(Icons.Default.Refresh, contentDescription = "Aktualisieren")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // Suchfeld
            SearchBar(
                query = uiState.searchQuery,
                onQueryChange = viewModel::updateSearchQuery,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            )

            when {
                uiState.isLoading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }

                uiState.error != null -> {
                    ErrorMessage(
                        message = uiState.error ?: "Unbekannter Fehler",
                        onRetry = { viewModel.loadChannels(forceRefresh = true) }
                    )
                }

                filteredChannels.isEmpty() -> {
                    EmptyState()
                }

                else -> {
                    ChannelList(
                        channels = filteredChannels,
                        onChannelClick = onChannelClick,
                        onChannelCheckedChange = viewModel::toggleChannelChecked
                    )
                }
            }
        }
    }
}

@Composable
fun SearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    OutlinedTextField(
        value = query,
        onValueChange = onQueryChange,
        modifier = modifier,
        placeholder = { Text("Kanal suchen...") },
        leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
        singleLine = true
    )
}

@Composable
fun ChannelList(
    channels: List<Channel>,
    onChannelClick: (String) -> Unit,
    onChannelCheckedChange: (Channel) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(channels, key = { it.uuid }) { channel ->
            ChannelItem(
                channel = channel,
                onClick = { onChannelClick(channel.uuid) },
                onCheckedChange = { onChannelCheckedChange(channel) }
            )
        }
    }
}

@Composable
fun ChannelItem(
    channel: Channel,
    onClick: () -> Unit,
    onCheckedChange: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = channel.title,
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = "${channel.type} • ${channel.unit}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Checkbox(
                checked = channel.isChecked,
                onCheckedChange = { onCheckedChange() }
            )
        }
    }
}

@Composable
fun ErrorMessage(
    message: String,
    onRetry: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
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

@Composable
fun EmptyState() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "Keine Kanäle gefunden",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}