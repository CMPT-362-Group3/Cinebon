package com.cmpt362.cinebon.ui.dashboard

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SearchBar
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.cmpt362.cinebon.R
import com.cmpt362.cinebon.data.enums.DashboardNavItems
import com.cmpt362.cinebon.ui.NavGraphs
import com.cmpt362.cinebon.ui.appCurrentDestinationAsState
import com.cmpt362.cinebon.ui.destinations.MovieInfoScreenDestination
import com.cmpt362.cinebon.utils.AppLogo
import com.cmpt362.cinebon.viewmodels.DashBoardViewModel
import com.cmpt362.cinebon.viewmodels.MoviesSearchViewModel
import com.ramcosta.composedestinations.DestinationsNavHost
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.NavGraph
import com.ramcosta.composedestinations.annotation.RootNavGraph
import com.ramcosta.composedestinations.spec.DestinationSpec
import com.ramcosta.composedestinations.utils.startDestination

val sections = listOf(
    DashboardNavItems.Movies, DashboardNavItems.Lists, DashboardNavItems.Socials
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
        if (navController.appCurrentDestinationAsState().value?.route == DashboardNavItems.Movies.destination.route) {
            MoviesSearchTopBar(navController = navController)
        } else {
            DefaultTopBar(navController = navController)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MoviesSearchTopBar(navController: NavController) {

    var active by remember { mutableStateOf(false) }
    var searchQuery by remember { mutableStateOf("") }
    val searchVM = viewModel<MoviesSearchViewModel>()
    val searchResults by searchVM.searchResults.collectAsStateWithLifecycle()

    SearchBar(
        query = searchQuery,
        onQueryChange = {
            searchQuery = it
            searchVM.updateSearchResults(query = searchQuery, false)
        },
        onSearch = {
            searchQuery = searchQuery.trim()
            searchVM.updateSearchResults(query = searchQuery, false)
        },
        placeholder = { Text("Search movies") },
        active = active,
        onActiveChange = { active = it },
        leadingIcon = { AppLogo(modifier = Modifier.size(32.dp)) },
        trailingIcon = { //exit icon
            if (active) {
                Icon(
                    modifier = Modifier.clickable {
                        if (searchQuery.isNotEmpty()) {
                            searchQuery = ""
                            searchVM.resetSearchResults()
                        } else {
                            active = false
                        }
                    },
                    imageVector = ImageVector.vectorResource(R.drawable.close_icon),
                    contentDescription = "Exit search",
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        },
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        LazyColumn {
            items(searchResults.size) { index ->
                val movie = searchResults[index]

                MovieSearchItem(
                    movie = movie,
                    onClick = {
                        navController.navigate(
                            MovieInfoScreenDestination(
                                movieId = it.id,
                            ).route
                        )
                    },
                    enableToggle = false
                )
            }
        }
    }
}

@Composable
fun DefaultTopBar(navController: NavController) {
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
                    DashboardNavItems.Socials.destination.route -> R.string.socials
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

@NavGraph
annotation class DashboardNavGraph(
    val start: Boolean = false
)

@Preview
@Composable
fun DashboardNavPreview() {
    DashboardNav()
}