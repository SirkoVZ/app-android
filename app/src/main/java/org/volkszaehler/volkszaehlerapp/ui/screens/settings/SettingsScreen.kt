package org.volkszaehler.volkszaehlerapp.ui.screens.settings

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onNavigateBack: () -> Unit,
    viewModel: SettingsViewModel = hiltViewModel()
) {
    val settings by viewModel.settings.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Einstellungen") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Zurück"
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Server Einstellungen
            item {
                SettingsSection(title = "Server")
            }

            item {
                ServerUrlSetting(
                    serverUrl = settings.serverUrl,
                    onServerUrlChange = viewModel::updateServerUrl
                )
            }

            // Diagramm Einstellungen
            item {
                Spacer(modifier = Modifier.height(16.dp))
                SettingsSection(title = "Diagramm")
            }

            item {
                DropdownSetting(
                    title = "Standard-Zeitraum",
                    selectedValue = settings.defaultPeriod,
                    options = listOf(
                        "hour" to "Stunde",
                        "day" to "Tag",
                        "week" to "Woche",
                        "month" to "Monat",
                        "year" to "Jahr"
                    ),
                    onValueChange = viewModel::updateDefaultPeriod
                )
            }

            item {
                DropdownSetting(
                    title = "Diagrammtyp",
                    selectedValue = settings.chartType,
                    options = listOf(
                        "line" to "Linie",
                        "bar" to "Balken",
                        "area" to "Fläche"
                    ),
                    onValueChange = viewModel::updateChartType
                )
            }

            item {
                SwitchSetting(
                    title = "Gitter anzeigen",
                    description = "Zeigt Gitterlinien im Diagramm an",
                    checked = settings.showGrid,
                    onCheckedChange = viewModel::updateShowGrid
                )
            }

            item {
                SwitchSetting(
                    title = "Legende anzeigen",
                    description = "Zeigt die Legende im Diagramm an",
                    checked = settings.showLegend,
                    onCheckedChange = viewModel::updateShowLegend
                )
            }

            // Cache Einstellungen
            item {
                Spacer(modifier = Modifier.height(16.dp))
                SettingsSection(title = "Cache")
            }

            item {
                SwitchSetting(
                    title = "Cache aktivieren",
                    description = "Speichert Daten lokal für schnelleren Zugriff",
                    checked = settings.enableCache,
                    onCheckedChange = viewModel::updateEnableCache
                )
            }

            if (settings.enableCache) {
                item {
                    SliderSetting(
                        title = "Cache-Dauer",
                        description = "${settings.cacheDuration} Sekunden",
                        value = settings.cacheDuration.toFloat(),
                        valueRange = 60f..3600f,
                        steps = 0,
                        onValueChange = { viewModel.updateCacheDuration(it.toInt()) }
                    )
                }
            }

            // Aktualisierung
            item {
                Spacer(modifier = Modifier.height(16.dp))
                SettingsSection(title = "Aktualisierung")
            }

            item {
                SwitchSetting(
                    title = "Automatische Aktualisierung",
                    description = "Aktualisiert Daten automatisch",
                    checked = settings.autoRefresh,
                    onCheckedChange = viewModel::updateAutoRefresh
                )
            }

            if (settings.autoRefresh) {
                item {
                    SliderSetting(
                        title = "Aktualisierungsintervall",
                        description = "${settings.refreshInterval} Sekunden",
                        value = settings.refreshInterval.toFloat(),
                        valueRange = 10f..300f,
                        steps = 0,
                        onValueChange = { viewModel.updateRefreshInterval(it.toInt()) }
                    )
                }
            }

            // Darstellung
            item {
                Spacer(modifier = Modifier.height(16.dp))
                SettingsSection(title = "Darstellung")
            }

            item {
                DropdownSetting(
                    title = "Theme",
                    selectedValue = settings.themeMode,
                    options = listOf(
                        "light" to "Hell",
                        "dark" to "Dunkel",
                        "system" to "System"
                    ),
                    onValueChange = viewModel::updateThemeMode
                )
            }

            item {
                SwitchSetting(
                    title = "Dynamische Farben",
                    description = "Verwendet Systemfarben (Android 12+)",
                    checked = settings.useDynamicColors,
                    onCheckedChange = viewModel::updateUseDynamicColors
                )
            }
        }
    }
}

@Composable
private fun SettingsSection(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleMedium,
        color = MaterialTheme.colorScheme.primary,
        modifier = Modifier.padding(vertical = 8.dp)
    )
}

@Composable
private fun SwitchSetting(
    title: String,
    description: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyLarge
                )
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Switch(
                checked = checked,
                onCheckedChange = onCheckedChange
            )
        }
    }
}

@Composable
private fun ServerUrlSetting(
    serverUrl: String,
    onServerUrlChange: (String) -> Unit
) {
    var text by remember(serverUrl) { mutableStateOf(serverUrl) }
    var isEditing by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = "Server URL",
                style = MaterialTheme.typography.bodyLarge
            )
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = text,
                onValueChange = {
                    text = it
                    isEditing = true
                },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("https://demo.volkszaehler.org") },
                singleLine = true
            )
            if (isEditing) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = {
                        text = serverUrl
                        isEditing = false
                    }) {
                        Text("Abbrechen")
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(onClick = {
                        onServerUrlChange(text)
                        isEditing = false
                    }) {
                        Text("Speichern")
                    }
                }
            }
        }
    }
}

@Composable
private fun DropdownSetting(
    title: String,
    selectedValue: String,
    options: List<Pair<String, String>>,
    onValueChange: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier.fillMaxWidth(),
        onClick = { expanded = true }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge
            )
            Text(
                text = options.find { it.first == selectedValue }?.second ?: selectedValue,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                options.forEach { (value, label) ->
                    DropdownMenuItem(
                        text = { Text(label) },
                        onClick = {
                            onValueChange(value)
                            expanded = false
                        },
                        leadingIcon = if (value == selectedValue) {
                            {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = null
                                )
                            }
                        } else null
                    )
                }
            }
        }
    }
}

@Composable
private fun SliderSetting(
    title: String,
    description: String,
    value: Float,
    valueRange: ClosedFloatingPointRange<Float>,
    steps: Int,
    onValueChange: (Float) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge
            )
            Text(
                text = description,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Slider(
                value = value,
                onValueChange = onValueChange,
                valueRange = valueRange,
                steps = steps,
                modifier = Modifier.padding(top = 8.dp)
            )
        }
    }
}
