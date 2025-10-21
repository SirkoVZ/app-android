package org.volkszaehler.volkszaehlerapp.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import org.volkszaehler.volkszaehlerapp.ui.channellist.ChannelListScreen
import org.volkszaehler.volkszaehlerapp.ui.chart.ChartScreen

sealed class Screen(val route: String) {
    object ChannelList : Screen("channel_list")
    object Chart : Screen("chart/{channelUuid}") {
        fun createRoute(channelUuid: String) = "chart/$channelUuid"
    }
}

@Composable
fun VolkszaehlerNavHost(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController()
) {
    NavHost(
        navController = navController,
        startDestination = Screen.ChannelList.route,
        modifier = modifier
    ) {
        composable(Screen.ChannelList.route) {
            ChannelListScreen(
                onChannelClick = { channelUuid ->
                    navController.navigate(Screen.Chart.createRoute(channelUuid))
                }
            )
        }

        composable(
            route = Screen.Chart.route,
            arguments = listOf(
                navArgument("channelUuid") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val channelUuid = backStackEntry.arguments?.getString("channelUuid") ?: ""
            ChartScreen(
                channelUuid = channelUuid,
                onNavigateBack = { navController.navigateUp() }
            )
        }
    }
}