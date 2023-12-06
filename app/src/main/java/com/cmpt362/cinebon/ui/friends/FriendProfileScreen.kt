package com.cmpt362.cinebon.ui.friends

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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.cmpt362.cinebon.R
import com.cmpt362.cinebon.data.enums.FriendRequestStatus
import com.cmpt362.cinebon.ui.dashboard.DashboardNavGraph
import com.cmpt362.cinebon.ui.destinations.IndividualListScreenDestination
import com.cmpt362.cinebon.ui.theme.CinebonTheme
import com.cmpt362.cinebon.viewmodels.ChatListViewModel
import com.cmpt362.cinebon.viewmodels.FriendViewModel
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import com.ramcosta.composedestinations.navigation.EmptyDestinationsNavigator

@DashboardNavGraph
@Destination
@Composable
fun FriendProfileScreen(navigator: DestinationsNavigator, userID: String) {
    val friendViewModel = viewModel<FriendViewModel>(factory = FriendViewModel.Factory(userID))
    val friendInfo by friendViewModel.friendInfo.collectAsStateWithLifecycle()
    val friendStatus by friendViewModel.requestStatus.collectAsStateWithLifecycle()

    val scrollState = rememberScrollState()
    val friendsCount by rememberSaveable { mutableIntStateOf(0) }
    val moviesWatched by rememberSaveable { mutableIntStateOf(0) }
    val lastWatched by rememberSaveable { mutableStateOf("") }

    val chatListViewModel = viewModel<ChatListViewModel>()
    val chatCreationStatus by chatListViewModel.openedChat.collectAsState()

    if (friendInfo == null) {
        return
    }

    if (chatCreationStatus != null) {
        navigator.navigate(IndividualListScreenDestination(chatCreationStatus!!.chatId))
        chatListViewModel.resetCreationStatus()
    }

    Surface(
        modifier = Modifier
            .scrollable(scrollState, Orientation.Vertical)
            .fillMaxSize(), color = MaterialTheme.colorScheme.background
    ) {
        Row(
            horizontalArrangement = Arrangement.Start, modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            IconButton(onClick = {
                navigator.popBackStack()
            }) {
                Icon(
                    imageVector = ImageVector.vectorResource(R.drawable.back_icon),
                    contentDescription = "back",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier
                        .size(48.dp)
                )
            }
        }
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxSize()
        ) {
            Icon(
                imageVector = ImageVector.vectorResource(R.drawable.profile_icon),
                contentDescription = "profile picture",
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier
                    .size(200.dp)
            )
            Text(
                text = friendInfo?.username ?: "",
                style = MaterialTheme.typography.displaySmall,
                modifier = Modifier
                    .padding(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 8.dp)
            )

            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier
                    .padding(horizontal = 8.dp)
            ) {
                Button(
                    onClick = {
                        chatListViewModel.startChatWithUser(userID)
                    },
                    colors = ButtonDefaults.buttonColors
                        (
                        contentColor = MaterialTheme.colorScheme.onPrimary,
                        containerColor = MaterialTheme.colorScheme.primary
                    ),
                    modifier = Modifier
                        .padding(vertical = 16.dp)
                        .padding(end = 4.dp)
                ) {
                    Text("Message", Modifier.padding(8.dp))
                }
                Button(
                    onClick = {
                        when (friendStatus) {
                            FriendRequestStatus.NONE -> {
                                friendViewModel.sendRequest(friendInfo!!)
                            }

                            FriendRequestStatus.SENT -> {
                                friendViewModel.rejectRequest(friendInfo!!)
                            }

                            FriendRequestStatus.RECEIVED -> {
                                friendViewModel.acceptRequest(friendInfo!!)
                            }

                            else -> {
                                friendViewModel.removeFriend(friendInfo!!)
                            }

                        }
                    },
                    colors = ButtonDefaults.buttonColors
                        (
                        contentColor = MaterialTheme.colorScheme.onPrimary,
                        containerColor = MaterialTheme.colorScheme.primary
                    ),
                    modifier = Modifier
                        .padding(vertical = 16.dp)
                        .padding(start = 4.dp)
                ) {
                    Text(
                        text = when (friendStatus) {
                            FriendRequestStatus.NONE -> "Add Friend"
                            FriendRequestStatus.SENT -> "Cancel Request"
                            FriendRequestStatus.RECEIVED -> "Accept Request"
                            else -> "Remove friend"
                        },
                        Modifier.padding(8.dp)
                    )
                }
            }

            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier
                    .padding(horizontal = 8.dp)
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center, modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Friends",
                        style = MaterialTheme.typography.headlineMedium,
                        modifier = Modifier
                            .padding(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 8.dp)
                    )

                    Divider(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 8.dp),
                        color = MaterialTheme.colorScheme.primary,
                        thickness = 4.dp
                    )

                    Text(
                        text = friendsCount.toString(),
                        style = MaterialTheme.typography.headlineMedium
                    )
                }

                Spacer(modifier = Modifier.width(32.dp))

                Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center, modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Watched",
                        style = MaterialTheme.typography.headlineMedium,
                        modifier = Modifier
                            .padding(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 8.dp)
                    )

                    Divider(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 8.dp),
                        color = MaterialTheme.colorScheme.primary,
                        thickness = 4.dp
                    )

                    Text(
                        text = moviesWatched.toString(),
                        style = MaterialTheme.typography.headlineMedium
                    )
                }
            }

            Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
                Text(
                    text = "Last Watched",
                    style = MaterialTheme.typography.headlineMedium,
                    modifier = Modifier
                        .padding(start = 16.dp, top = 16.dp, end = 16.dp, bottom = 8.dp)
                )

                Divider(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp),
                    color = MaterialTheme.colorScheme.primary,
                    thickness = 4.dp
                )
                Text(
                    text = lastWatched,
                    style = MaterialTheme.typography.headlineMedium,
                )
            }

            Button(
                onClick = {

                },
                colors = ButtonDefaults.buttonColors
                    (
                    contentColor = MaterialTheme.colorScheme.onPrimary,
                    containerColor = MaterialTheme.colorScheme.primary
                ),
                modifier = Modifier
                    .padding(vertical = 64.dp)
            ) {
                Text("${friendInfo?.fname}'s Movie List")
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun FriendProfilePreview() {
    CinebonTheme {
        FriendProfileScreen(EmptyDestinationsNavigator, "")
    }
}