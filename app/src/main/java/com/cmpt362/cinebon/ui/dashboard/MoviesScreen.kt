package com.cmpt362.cinebon.ui.dashboard

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Tab
import androidx.compose.material3.TabPosition
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.cmpt362.cinebon.data.api.response.Movie
import com.cmpt362.cinebon.ui.destinations.MovieInfoScreenDestination
import com.cmpt362.cinebon.utils.SetStatusBarColor
import com.cmpt362.cinebon.viewmodels.MoviesViewModel
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import kotlinx.coroutines.launch

val tabs = listOf(
    "New",
    "Popular",
    "Upcoming"
)

@OptIn(ExperimentalFoundationApi::class)
@DashboardNavGraph(start = true)
@Destination
@Composable
fun MoviesScreen(navigator: DestinationsNavigator) {

    val pagerState = rememberPagerState(pageCount = { tabs.size })
    val scope = rememberCoroutineScope()
    val moviesViewModel = viewModel<MoviesViewModel>()

    SetStatusBarColor(statusBarColor = MaterialTheme.colorScheme.surfaceColorAtElevation(3.dp))

    Column(modifier = Modifier.fillMaxSize()) {
        TabRow(
            selectedTabIndex = pagerState.currentPage,
            divider = {
                Spacer(modifier = Modifier.height(5.dp))
            },
            indicator = { tabPositions: List<TabPosition> ->
                TabRowDefaults.Indicator(
                    modifier = Modifier
                        .width(10.dp)
                        .tabIndicatorOffset(tabPositions[pagerState.currentPage]),
                    height = 5.dp,
                    color = MaterialTheme.colorScheme.primary
                )
            },
            containerColor = MaterialTheme.colorScheme.surfaceColorAtElevation(3.dp),
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
        ) {
            tabs.forEachIndexed { index, tab ->
                Tab(selected = pagerState.currentPage == index,
                    onClick = {
                        scope.launch {
                            pagerState.animateScrollToPage(index)
                        }
                    },
                    text = {
                        Text(text = tab)
                    })
            }
        }

        HorizontalPager(state = pagerState, Modifier.fillMaxSize(), verticalAlignment = Alignment.Top) { index ->
            MoviesGrid(
                source = when (index) {
                    0 -> moviesViewModel.nowPlayingMovies.collectAsStateWithLifecycle().value
                    1 -> moviesViewModel.popularMovies.collectAsStateWithLifecycle().value
                    2 -> moviesViewModel.upcomingMovies.collectAsStateWithLifecycle().value
                    else -> emptyList()
                }, onClick = {
                    navigator.navigate(
                        MovieInfoScreenDestination(
                            movieId = it.id,
                        )
                    )
                }
            )
        }
    }
}

@Composable
fun MoviesGrid(source: List<Movie>, onClick: (Movie) -> Unit) {

    LazyVerticalStaggeredGrid(
        columns = StaggeredGridCells.Fixed(2),
        verticalItemSpacing = 16.dp,
        horizontalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterHorizontally),
        modifier = Modifier.padding(16.dp),
        content = {
            items(source.size) {
                MovieCard(movie = source[it], onClick)
            }
        })
}

@Composable
fun MovieCard(
    movie: Movie,
    onClick: (Movie) -> Unit,
    modifier: Modifier = Modifier,
    showQuickAdd: Boolean = false,
    onQuickAdd: (Movie) -> Unit = {}) {
    Card(modifier = modifier.fillMaxSize()) {
        Box(modifier = modifier.fillMaxSize()) {
            MovieImage(movie, onClick = { onClick(movie) })

            if (showQuickAdd) {
                MovieBookmarkIcon(movie, modifier = modifier.align(Alignment.TopEnd).clickable { onQuickAdd(movie) })
            }
        }
    }
}

