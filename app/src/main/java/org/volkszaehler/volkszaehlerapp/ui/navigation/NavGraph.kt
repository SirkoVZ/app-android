package org.volkszaehler.volkszaehlerapp.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import org.volkszaehler.volkszaehlerapp.ui.screens.channel.ChannelDetailScreen
import org.volkszaehler.volkszaehlerapp.ui.screens.channel.ChannelListScreen
import org.volkszaehler.volkszaehlerapp.ui.screens.settings.SettingsScreen

/**
 * Navigation routes for the app
 */
object NavigationRoutes {
    const val CHANNEL_LIST = "channels"
    const val CHANNEL_DETAIL = "channel/{uuid}"
    const val SETTINGS = "settings"

    fun channelDetail(uuid: String) = "channel/$uuid"
}

/**
 * Main navigation graph for the app
 */
@Composable
fun NavGraph(
    navController: NavHostController,
    startDestination: String = NavigationRoutes.CHANNEL_LIST
) {
    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        // Channel List Screen
        composable(NavigationRoutes.CHANNEL_LIST) {
            ChannelListScreen(
                onChannelClick = { uuid ->
                    navController.navigate(NavigationRoutes.channelDetail(uuid))
                },
                onSettingsClick = {
                    navController.navigate(NavigationRoutes.SETTINGS)
                }
            )
        }

        // Channel Detail Screen
        composable(
            route = NavigationRoutes.CHANNEL_DETAIL,
            arguments = listOf(
                navArgument("uuid") {
                    type = NavType.StringType
                }
            )
        ) { backStackEntry ->
            val uuid = backStackEntry.arguments?.getString("uuid") ?: return@composable
            ChannelDetailScreen(
                uuid = uuid,
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }

        // Settings Screen
        composable(NavigationRoutes.SETTINGS) {
            SettingsScreen(
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }
    }
}
