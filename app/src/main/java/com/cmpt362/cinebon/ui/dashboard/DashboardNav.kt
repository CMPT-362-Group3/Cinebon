package com.cmpt362.cinebon.ui.dashboard

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.cmpt362.cinebon.R
import com.cmpt362.cinebon.data.enums.DashboardNavItems
import com.cmpt362.cinebon.ui.NavGraphs
import com.cmpt362.cinebon.ui.appCurrentDestinationAsState
import com.cmpt362.cinebon.utils.AppLogo
import com.cmpt362.cinebon.viewmodels.DashBoardViewModel
import com.ramcosta.composedestinations.DestinationsNavHost
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.NavGraph
import com.ramcosta.composedestinations.annotation.RootNavGraph
import com.ramcosta.composedestinations.spec.DestinationSpec
import com.ramcosta.composedestinations.utils.startDestination

val sections = listOf(
    DashboardNavItems.Movies, DashboardNavItems.Lists, DashboardNavItems.Profile
)

@RootNavGraph
@Destination
@Composable
fun DashboardNav() {

    val navController = rememberNavController()

    // This creates a dashboard VM instance which starts the chat service
    val dashboardVM = viewModel<DashBoardViewModel>()
    dashboardVM.ensureRunningNetworkService()

    Surface(
        modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background
    ) {
        Scaffold(bottomBar = { BottomBar(navController = navController) }, topBar = { TopBar(navController) }) { innerPadding ->
            DestinationsNavHost(navController = navController, navGraph = NavGraphs.dashboard, modifier = Modifier.padding(innerPadding))
        }
    }
}

@Composable
fun shouldHideBottomBar(navController: NavController): Boolean {
    return !sections.map { it.destination.route }.contains(
        navController.appCurrentDestinationAsState().value?.route ?: DashboardNavItems.Movies.destination.route
    )
}

@Composable
fun shouldHideTopBar(navController: NavController): Boolean {
    return shouldHideBottomBar(navController = navController)
}

@Composable
private fun BottomBar(navController: NavController) {

    if (shouldHideBottomBar(navController)) return


    val currentDestination: DestinationSpec<out Any?> = navController.appCurrentDestinationAsState().value ?: NavGraphs.dashboard.startDestination

    NavigationBar {
        sections.forEach { section ->
            NavigationBarItem(icon = { Icon(ImageVector.vectorResource(id = section.icon), contentDescription = section.name) }, onClick = {
                if (currentDestination == section.destination) return@NavigationBarItem
                navController.popBackStack()
                navController.navigate(section.destination.route) {
                    launchSingleTop = true
                }
            }, selected = currentDestination == section.destination, label = { Text(section.name) })
        }
    }
}

@Composable
private fun TopBar(navController: NavController) {

    if (shouldHideTopBar(navController)) return

    Surface(color = MaterialTheme.colorScheme.surfaceColorAtElevation(3.dp)) {
        Row(
            verticalAlignment = Alignment.CenterVertically, modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            AppLogo(modifier = Modifier.size(32.dp))

            Text(
                stringResource(
                    when (navController.appCurrentDestinationAsState().value?.route) {
                        DashboardNavItems.Movies.destination.route -> R.string.discover
                        DashboardNavItems.Lists.destination.route -> R.string.lists
                        DashboardNavItems.Profile.destination.route -> R.string.profile
                        else -> R.string.app_name
                    }
                ),
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
            )
        }

    }
}

@NavGraph
annotation class DashboardNavGraph(
    val start: Boolean = false
)

@Preview
@Composable
fun DashboardNavPreview() {
    DashboardNav()
}