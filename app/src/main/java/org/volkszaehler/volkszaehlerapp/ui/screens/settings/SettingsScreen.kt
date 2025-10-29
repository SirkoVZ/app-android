package org.volkszaehler.volkszaehlerapp.ui.screens.settings

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onNavigateBack: () -> Unit,
    viewModel: SettingsViewModel = hiltViewModel()
) {
    val settings by viewModel.settings.collectAsState()
    val fetchChannelsState by viewModel.fetchChannelsState.collectAsState()

    var serverUrl by remember { mutableStateOf(settings.serverUrl) }
    var username by remember { mutableStateOf(settings.username) }
    var password by remember { mutableStateOf(settings.password) }
    var tuples by remember { mutableStateOf(settings.tuples.toString()) }
    var privateChannelUUIDs by remember { mutableStateOf(settings.privateChannelUUIDs) }
    var zeroBasedYAxis by remember { mutableStateOf(settings.zeroBasedYAxis) }
    var autoReload by remember { mutableStateOf(settings.autoReload) }
    var sortChannelMode by remember { mutableStateOf(settings.sortChannelMode) }

    // Update local state when settings change
    LaunchedEffect(settings) {
        serverUrl = settings.serverUrl
        username = settings.username
        password = settings.password
        tuples = settings.tuples.toString()
        privateChannelUUIDs = settings.privateChannelUUIDs
        zeroBasedYAxis = settings.zeroBasedYAxis
        autoReload = settings.autoReload
        sortChannelMode = settings.sortChannelMode
    }

    // Show snackbar for fetch channels state
    val snackbarHostState = remember { SnackbarHostState() }
    LaunchedEffect(fetchChannelsState) {
        when (val state = fetchChannelsState) {
            is FetchChannelsState.Success -> {
                snackbarHostState.showSnackbar(state.message)
                viewModel.resetFetchChannelsState()
            }
            is FetchChannelsState.Error -> {
                snackbarHostState.showSnackbar("Error: ${state.message}")
                viewModel.resetFetchChannelsState()
            }
            else -> {}
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Settings") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Server Configuration Section
            Text(
                text = "Server Configuration",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary
            )

            OutlinedTextField(
                value = serverUrl,
                onValueChange = {
                    serverUrl = it
                    viewModel.updateServerUrl(it)
                },
                label = { Text("Server URL") },
                placeholder = { Text("https://demo.volkszaehler.org/middleware.php") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            OutlinedTextField(
                value = username,
                onValueChange = {
                    username = it
                    viewModel.updateUsername(it)
                },
                label = { Text("Username (optional)") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            OutlinedTextField(
                value = password,
                onValueChange = {
                    password = it
                    viewModel.updatePassword(it)
                },
                label = { Text("Password (optional)") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                visualTransformation = PasswordVisualTransformation()
            )

            Divider()

            // Channel Configuration Section
            Text(
                text = "Channel Configuration",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary
            )

            OutlinedTextField(
                value = privateChannelUUIDs,
                onValueChange = {
                    privateChannelUUIDs = it
                    viewModel.updatePrivateChannelUUIDs(it)
                },
                label = { Text("Private Channel UUIDs") },
                placeholder = { Text("uuid1, uuid2, uuid3") },
                modifier = Modifier.fillMaxWidth(),
                supportingText = { Text("Comma-separated list of UUIDs") }
            )

            // Sort Mode Dropdown
            var expandedSortMode by remember { mutableStateOf(false) }
            ExposedDropdownMenuBox(
                expanded = expandedSortMode,
                onExpandedChange = { expandedSortMode = !expandedSortMode }
            ) {
                OutlinedTextField(
                    value = when (sortChannelMode) {
                        "off" -> "No sorting"
                        "groups" -> "Sort by groups"
                        "plain" -> "Plain sorting"
                        else -> "No sorting"
                    },
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Sort Channel Mode") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedSortMode) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor()
                )
                ExposedDropdownMenu(
                    expanded = expandedSortMode,
                    onDismissRequest = { expandedSortMode = false }
                ) {
                    DropdownMenuItem(
                        text = { Text("No sorting") },
                        onClick = {
                            sortChannelMode = "off"
                            viewModel.updateSortChannelMode("off")
                            expandedSortMode = false
                        }
                    )
                    DropdownMenuItem(
                        text = { Text("Sort by groups") },
                        onClick = {
                            sortChannelMode = "groups"
                            viewModel.updateSortChannelMode("groups")
                            expandedSortMode = false
                        }
                    )
                    DropdownMenuItem(
                        text = { Text("Plain sorting") },
                        onClick = {
                            sortChannelMode = "plain"
                            viewModel.updateSortChannelMode("plain")
                            expandedSortMode = false
                        }
                    )
                }
            }

            // Fetch Channels Button
            Button(
                onClick = { viewModel.fetchChannels() },
                modifier = Modifier.fillMaxWidth(),
                enabled = fetchChannelsState !is FetchChannelsState.Loading
            ) {
                if (fetchChannelsState is FetchChannelsState.Loading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        strokeWidth = 2.dp
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                }
                Icon(Icons.Default.Refresh, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text(if (fetchChannelsState is FetchChannelsState.Loading) "Loading..." else "Fetch Channels")
            }

            Divider()

            // Display Configuration Section
            Text(
                text = "Display Configuration",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary
            )

            OutlinedTextField(
                value = tuples,
                onValueChange = {
                    tuples = it
                    it.toIntOrNull()?.let { value ->
                        viewModel.updateTuples(value)
                    }
                },
                label = { Text("Tuples") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                singleLine = true
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Zero-based Y-Axis")
                Switch(
                    checked = zeroBasedYAxis,
                    onCheckedChange = {
                        zeroBasedYAxis = it
                        viewModel.updateZeroBasedYAxis(it)
                    }
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Auto Reload")
                Switch(
                    checked = autoReload,
                    onCheckedChange = {
                        autoReload = it
                        viewModel.updateAutoReload(it)
                    }
                )
            }
        }
    }
}
