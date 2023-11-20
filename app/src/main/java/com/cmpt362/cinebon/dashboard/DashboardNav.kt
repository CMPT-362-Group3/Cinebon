package com.cmpt362.cinebon.dashboard

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.cmpt362.cinebon.NavGraphs
import com.cmpt362.cinebon.appCurrentDestinationAsState
import com.cmpt362.cinebon.data.enums.DashboardNavItems
import com.ramcosta.composedestinations.DestinationsNavHost
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.NavGraph
import com.ramcosta.composedestinations.spec.DestinationSpec
import com.ramcosta.composedestinations.utils.startDestination

val sections = listOf(
    DashboardNavItems.Movies, DashboardNavItems.Lists, DashboardNavItems.Profile
)

@Destination
@Composable
fun DashboardNav() {
    val navController = rememberNavController()

    Surface(
        modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background
    ) {
        Scaffold(bottomBar = { BottomBar(navController = navController) }) { innerPadding ->
            DestinationsNavHost(navController = navController, navGraph = NavGraphs.dashboard, modifier = Modifier.padding(innerPadding))
        }
    }
}

@Composable
private fun BottomBar(navController: NavController) {

    var navigationIndex by rememberSaveable {
        mutableIntStateOf(0)
    }

    val currentDestination: DestinationSpec<out Any?> = navController.appCurrentDestinationAsState().value ?: NavGraphs.dashboard.startDestination

    NavigationBar {
        sections.forEachIndexed { index, section ->
            NavigationBarItem(
                icon = { Icon(ImageVector.vectorResource(id = section.icon), contentDescription = section.name) },
                onClick = {
                    navigationIndex = index
                    navController.navigate(section.destination.route) {
                        launchSingleTop = true
                    }
                },
                selected = currentDestination == section.destination,
                label = { Text(section.name) }
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