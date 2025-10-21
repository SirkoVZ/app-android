package org.volkszaehler.volkszaehlerapp.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Settings
import androidx.compose.ui.graphics.vector.ImageVector

sealed class Screen(val route: String, val title: String, val icon: ImageVector? = null) {
    data object Home : Screen("home", "Home", Icons.Default.Home)
    data object ChannelList : Screen("channels", "Channels", Icons.Default.List)
    data object Settings : Screen("settings", "Settings", Icons.Default.Settings)
    data object ChannelDetail : Screen("channel/{uuid}", "Channel Detail") {
        fun createRoute(uuid: String) = "channel/$uuid"
    }
}