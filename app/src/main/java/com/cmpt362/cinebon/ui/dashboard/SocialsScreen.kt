package com.cmpt362.cinebon.ui.dashboard

import android.annotation.SuppressLint
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SearchBar
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.cmpt362.cinebon.R
import com.cmpt362.cinebon.data.objects.User
import com.cmpt362.cinebon.ui.chat.ChatList
import com.cmpt362.cinebon.ui.destinations.FriendProfileScreenDestination
import com.cmpt362.cinebon.ui.destinations.ProfileScreenDestination
import com.cmpt362.cinebon.ui.theme.CinebonTheme
import com.cmpt362.cinebon.viewmodels.SearchViewModel
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import com.ramcosta.composedestinations.navigation.EmptyDestinationsNavigator


@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@DashboardNavGraph
@Destination
@Composable
fun SocialScreen(navigator: DestinationsNavigator) {
    val scrollState = rememberScrollState()
    var searchQuery by rememberSaveable { mutableStateOf("") } //the query we will tyoe in search bar
    var active by rememberSaveable { //whether search bar is open or not
        mutableStateOf(false)
    }
    val searchViewModel = viewModel<SearchViewModel>()
    val searchResults by searchViewModel.searchResults.collectAsStateWithLifecycle() //observe the search results
    Scaffold(
        topBar = {
            Surface(color = MaterialTheme.colorScheme.background){
                SearchBar(
                    modifier = Modifier.fillMaxWidth(),
                    shape = MaterialTheme.shapes.large,
                    placeholder = { Text("Find Friends") },
                    leadingIcon = { //search icon
                        Icon(
                            imageVector = ImageVector.vectorResource(R.drawable.search_icon),
                            contentDescription = "Search",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    },
                    onQueryChange = {
                        searchQuery = it
                        println(searchQuery)
                        searchViewModel.searchUsers(searchQuery)},
                    onActiveChange = {active = it},
                    active = active,
                    query = searchQuery,
                    content = {//show the search results int he dropdown
                        if (searchQuery.isNotEmpty()) {
                            UserList(users = searchResults) { userId ->
                                navigator.navigate(FriendProfileScreenDestination(userID = userId ))
                            }
                        }
                    },
                    onSearch = {
                        active = false
                        },
                    trailingIcon = { //exit icon
                        if(active){
                            Icon(
                                modifier = Modifier.clickable{
                                    if(searchQuery.isNotEmpty()){
                                        searchQuery = ""
                                    }else{
                                        active = false
                                    }
                                    searchViewModel.resetSearchResults()
                                },
                                imageVector = ImageVector.vectorResource(R.drawable.close_icon),
                                contentDescription = "Exit search",
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                )
            }
        }
    ) {
        Column(
            modifier = Modifier
                .scrollable(scrollState, Orientation.Vertical)
                .fillMaxSize()
                .padding(top = 58.dp)
        ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                ) {

                    Text(
                        text = "Chats",
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(8.dp)
                    )

                    Spacer(Modifier.weight(1f))

                    IconButton(//profile button
                        onClick = {
                            navigator.navigate(ProfileScreenDestination)
                        }
                    ) {
                        Icon(
                            imageVector = ImageVector.vectorResource(R.drawable.profile_icon),
                            contentDescription = "profile",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier
                                .size(48.dp)
                        )
                    }

                }

            //display list of all chat
            ChatList(navigator = navigator)

            FloatingActionButton(//new chat button
                onClick = { /*TODO*/ },
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                modifier = Modifier
                    .padding(8.dp)
                    .align(Alignment.End)
            ) {
                Icon(
                    imageVector = ImageVector.vectorResource(R.drawable.addchat_icon),
                    contentDescription = "new chat",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier
                        .size(48.dp),
                )
            }
        }
    }

}

//UserList displays search results
@Composable
fun UserList(users: List<User>, onItemClick: (String) -> Unit) {
    LazyColumn {
        items(users) { user ->
            UserListItem(user = user, onItemClick = {onItemClick(user.userId)} )
        }
    }
}

// UserListItem displays each user in the list
@Composable
fun UserListItem(user: User, onItemClick: (String) -> Unit) {
            Column (
                modifier = Modifier
                    .padding(16.dp)
                    .clickable { onItemClick(user.userId) }
            ){
                Text(
                    text = user.username,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = user.fname + " "+ user.lname,
                    style = MaterialTheme.typography.bodySmall,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
}

@Preview(showBackground = true)
@Composable
fun SocialsPreview() {
    CinebonTheme {
        SocialScreen(EmptyDestinationsNavigator)
    }
}
