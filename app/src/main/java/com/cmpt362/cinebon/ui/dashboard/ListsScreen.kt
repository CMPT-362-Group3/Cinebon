package com.cmpt362.cinebon.ui.dashboard

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Card
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.cmpt362.cinebon.R
import com.cmpt362.cinebon.data.api.response.Movie
import com.cmpt362.cinebon.data.entity.ResolvedListEntity
import com.cmpt362.cinebon.data.entity.UserEntity
import com.cmpt362.cinebon.ui.destinations.IndividualListScreenDestination
import com.cmpt362.cinebon.ui.destinations.MovieInfoScreenDestination
import com.cmpt362.cinebon.utils.SetStatusBarColor
import com.cmpt362.cinebon.viewmodels.NewListViewModel
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator

@DashboardNavGraph
@Destination
@Composable
fun ListsScreen(navigator: DestinationsNavigator) {
    val listViewModel = viewModel<NewListViewModel>()
    val userLists by listViewModel.userLists.collectAsStateWithLifecycle()
    val lazyListState = rememberLazyListState()

    SetStatusBarColor(statusBarColor = MaterialTheme.colorScheme.surfaceColorAtElevation(3.dp))

    Surface(modifier = Modifier.fillMaxSize()) {
        Box {
            LazyColumn(state = lazyListState) {
                items(userLists) { list ->
                    ListItem(
                        list = list,
                        onItemClick = {
                            navigator.navigate(
                                IndividualListScreenDestination(listId = it.listId)
                            )
                        },
                        onMovieClick = { movie ->
                            navigator.navigate(
                                MovieInfoScreenDestination(movieId = movie.id)
                            )
                        }
                    )
                }
            }

            FloatingActionButton(
                onClick = {
                    listViewModel.createEmptyNewList()
                },
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(24.dp)
            ) {
                Icon(painter = painterResource(id = R.drawable.playlist_plus), contentDescription = "Create new list")
            }
        }
    }
}

@Composable
fun ListItem(list: ResolvedListEntity, onItemClick: (ResolvedListEntity) -> Unit, onMovieClick: (Movie) -> Unit) {

    Card(modifier = Modifier.padding(16.dp).clickable { onItemClick(list) }) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = list.name,
                    style = MaterialTheme.typography.headlineSmall,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1,
                    modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 8.dp)
                )

                Text(
                    text = "Created by - ${list.owner.username}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                )
            }

            LazyRow(
                modifier = Modifier,
                contentPadding = PaddingValues(
                    start = 16.dp,
                    end = 16.dp,
                    top = 8.dp,
                    bottom = 16.dp
                ),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                items(list.movies) { movie ->
                    MovieCard(
                        movie = movie,
                        onClick = { onMovieClick(movie) },
                        modifier = Modifier
                    )
                }
            }
        }
    }
}

@Composable
@Preview
fun ListItemPreview() {
    ListItem(
        list = ResolvedListEntity(
            listId = "some list id",
            owner = UserEntity().apply {
                username = "some username"
            },
            name = "test list",
            movies = mutableListOf(),
            true
        ),
        onItemClick = { },
        onMovieClick = { }
    )
}