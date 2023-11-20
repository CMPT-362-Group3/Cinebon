package com.cmpt362.cinebon.dashboard

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.cmpt362.cinebon.R
import com.cmpt362.cinebon.data.DummyMovieData.newMovies
import com.cmpt362.cinebon.data.DummyMovieData.popularMovies
import com.cmpt362.cinebon.data.DummyMovieData.upcomingMovies
import com.cmpt362.cinebon.data.MovieEntity
import com.cmpt362.cinebon.utils.SetStatusBarColor
import com.cmpt362.cinebon.utils.shimmerBrush
import com.ramcosta.composedestinations.annotation.Destination
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
fun MoviesScreen() {

    val pagerState = rememberPagerState(pageCount = { tabs.size })
    val scope = rememberCoroutineScope()

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
            when (index) {
                0 -> MoviesGrid(source = newMovies)
                1 -> MoviesGrid(source = popularMovies)
                2 -> MoviesGrid(source = upcomingMovies)
            }

        }
    }
}

@Composable
fun MoviesGrid(source: List<MovieEntity>) {
    LazyVerticalStaggeredGrid(
        columns = StaggeredGridCells.Fixed(2),
        verticalItemSpacing = 8.dp,
        horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally),
        modifier = Modifier.padding(8.dp),
        content = {
            items(source.size) {
                MovieCard(movie = source[it])
            }
        })
}

@Composable
fun MovieCard(movie: MovieEntity) {

    val showShimmer = remember { mutableStateOf(true) }

    Card(modifier = Modifier.fillMaxSize()) {
        AsyncImage(
            model = R.drawable.cinebon,
            contentDescription = movie.title,
            contentScale = ContentScale.FillWidth,
            onSuccess = { showShimmer.value = false },
            modifier = Modifier
                .background(shimmerBrush(targetValue = 1300f, showShimmer = showShimmer.value))
                .heightIn(min = 100.dp)
                .fillMaxSize()
        )
    }
}

