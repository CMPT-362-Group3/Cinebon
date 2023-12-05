package com.cmpt362.cinebon.ui.dashboard

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SearchBar
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.cmpt362.cinebon.R
import com.cmpt362.cinebon.data.api.response.DummyMovie
import com.cmpt362.cinebon.data.api.response.Movie
import com.cmpt362.cinebon.data.api.toRuntimeString
import com.cmpt362.cinebon.ui.destinations.MovieInfoScreenDestination
import com.cmpt362.cinebon.utils.SetStatusBarColor
import com.cmpt362.cinebon.utils.UNICODE_DOT
import com.cmpt362.cinebon.viewmodels.IndividualListViewModel
import com.cmpt362.cinebon.viewmodels.MoviesSearchViewModel
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator

@OptIn(ExperimentalMaterial3Api::class)
@DashboardNavGraph
@Destination
@Composable
fun IndividualListScreen(navigator: DestinationsNavigator, listId: String) {

    val listViewModel = viewModel<IndividualListViewModel>(factory = IndividualListViewModel.Factory(listId))
    val list by listViewModel.currentList.collectAsStateWithLifecycle()
    val searchViewModel = viewModel<MoviesSearchViewModel>()
    val searchResults by searchViewModel.searchResults.collectAsStateWithLifecycle()

    // If the list is null, then we don't want to display anything.
    if (list == null) {
        Text("Whoops! Error loading list.")
        return
    }

    var listName by rememberSaveable { mutableStateOf(list?.name ?: "") }
    var query by rememberSaveable { mutableStateOf("") }
    var active by rememberSaveable { mutableStateOf(false) }
    val focusManager = LocalFocusManager.current
    val focusRequester = remember { FocusRequester() }
    var textFieldLoaded by remember { mutableStateOf(false) }

    SetStatusBarColor(statusBarColor = MaterialTheme.colorScheme.surface)

    Surface(
        modifier = Modifier
            .fillMaxSize()
    ) {
        Box {
            Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {

                SearchBar(
                    query = query,
                    onQueryChange = {
                        query = it
                        searchViewModel.updateSearchResults(query)
                    },
                    onSearch = { },
                    active = active,
                    onActiveChange = {
                        active = it
                        if (!active) {
                            focusManager.clearFocus(true)
                            textFieldLoaded = false
                        }
                    },
                    placeholder = { Text("Search for movies") },
                    leadingIcon = { //search icon
                        Icon(
                            imageVector = ImageVector.vectorResource(R.drawable.search_icon),
                            contentDescription = "Search",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    },
                    trailingIcon = { //exit icon
                        if (active) {
                            Icon(
                                modifier = Modifier.clickable {
                                    if (query.isNotEmpty()) {
                                        query = ""
                                        searchViewModel.resetSearchResults()
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
                        .focusRequester(focusRequester)
                        .onGloballyPositioned {
                            if (!textFieldLoaded && active) {
                                focusRequester.requestFocus() // IMPORTANT
                                textFieldLoaded = true // stop cyclic recompositions
                            }
                        }
                ) {
                    LazyColumn {
                        items(searchResults.size) { index ->
                            val movie = searchResults[index]

                            MovieSearchItem(
                                movie = movie,
                                onClick = {
                                    navigator.navigate(
                                        MovieInfoScreenDestination(
                                            movieId = it.id,
                                        )
                                    )
                                },
                                enableToggle = list!!.isSelf,
                                onToggle = {
                                    if (it) {
                                        listViewModel.addMovieToList(movie.id)
                                    } else {
                                        listViewModel.removeMovieFromList(movie.id)
                                    }
                                    searchViewModel.updateSearchResults(query, false)
                                },
                                isToggled = list?.movies?.any { it.id == movie.id } ?: false
                            )
                        }
                    }
                }

                if (list!!.isSelf) {
                    OutlinedTextField(
                        value = listName,
                        onValueChange = {
                            listName = it
                        },
                        label = {
                            Text("Title")
                        },
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Password,
                            imeAction = ImeAction.Done
                        ),
                        keyboardActions = KeyboardActions(onDone = {
                            if (listName.isNotEmpty()) {
                                listViewModel.updateListName(listName.trim())
                            }
                            focusManager.clearFocus(true)
                        }),
                        placeholder = { Text("Hint: Marvel collection?") },
                        modifier = Modifier.padding(top = 16.dp)
                    )

                    if (listName.isEmpty()) {
                        Text(
                            text = "Title cannot be blank",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.errorContainer,
                            modifier = Modifier
                                .padding(horizontal = 16.dp, vertical = 16.dp)
                                .background(
                                    color = MaterialTheme.colorScheme.error,
                                    shape = MaterialTheme.shapes.medium
                                )
                                .padding(horizontal = 16.dp, vertical = 8.dp)
                        )
                    }
                } else {
                    Text(
                        text = listName,
                        style = MaterialTheme.typography.headlineSmall,
                        color = MaterialTheme.colorScheme.onSurface,
                        maxLines = 1,
                        modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 16.dp)
                    )

                    Text(
                        text = "by - ${list!!.owner.username}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface,
                        maxLines = 1,
                        modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 8.dp)
                    )
                }

                Text(
                    text = "Movies:", modifier = Modifier
                        .padding(top = 32.dp)
                        .padding(16.dp)
                        .align(Alignment.Start)
                )

                if (list!!.movies.isEmpty()) {
                    Text(
                        text = if (list!!.isSelf) "Seems empty in here... You can search and add more movies!" else "This list is empty",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        modifier = Modifier
                            .padding(horizontal = 96.dp, vertical = 8.dp)
                            .align(Alignment.CenterHorizontally)
                    )
                } else {
                    MoviesGrid(source = list!!.movies, onClick = {
                        navigator.navigate(
                            MovieInfoScreenDestination(
                                movieId = it.id,
                            )
                        )
                    }
                    )
                }
            }

            AnimatedVisibility(
                visible = !active, modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(24.dp)
            ) {
                FloatingActionButton(onClick = { active = true }) {
                    Icon(painter = painterResource(id = R.drawable.book_search_outline), contentDescription = "Add new movie")
                }
            }
        }
    }
}

@Composable
fun MovieSearchItem(
    movie: Movie,
    onClick: (Movie) -> Unit,
    enableToggle: Boolean = false,
    onToggle: (Boolean) -> Unit = {},
    isToggled: Boolean = false,
) {
    Row(
        modifier = Modifier
            .padding(24.dp)
            .clickable { onClick(movie) },
        verticalAlignment = Alignment.CenterVertically
    ) {

        MovieImage(
            movie,
            onClick = { onClick(movie) },
            modifier = Modifier
                .size(48.dp)
                .clip(CircleShape)
        )

        Column(
            modifier = Modifier
                .weight(0.5f)
                .padding(8.dp)
                .fillMaxWidth()
        ) {
            Text(text = movie.title, modifier = Modifier.padding(start = 8.dp))
            Text(
                text = "${movie.originalLanguage} $UNICODE_DOT ${movie.runtime.toRuntimeString()}",
                modifier = Modifier.padding(start = 8.dp),
                style = MaterialTheme.typography.bodySmall
            )
        }

        if (enableToggle) {
            Box(modifier = Modifier
                .clip(CircleShape)
                .size(48.dp)
                .background(MaterialTheme.colorScheme.secondaryContainer)
                .clickable { onToggle(!isToggled) }) {
                Icon(
                    painter = painterResource(id = if (isToggled) R.drawable.check else R.drawable.plus),
                    contentDescription = "Add movie",
                    modifier = Modifier
                        .padding(8.dp)
                        .align(Alignment.Center)
                )
            }
        }
    }
}

@Composable
@Preview
fun MovieSearchItemPreview() {
    MovieSearchItem(movie = DummyMovie, onClick = {})
}

@Composable
@Preview
fun MovieSearchItemAddPreview() {
    MovieSearchItem(movie = DummyMovie, onClick = {}, enableToggle = true)
}