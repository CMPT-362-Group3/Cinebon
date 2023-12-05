package com.cmpt362.cinebon.ui.friends

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
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import com.cmpt362.cinebon.data.entity.FriendUser
import com.cmpt362.cinebon.data.entity.ResolvedFriendRequest
import com.cmpt362.cinebon.ui.dashboard.DashboardNavGraph
import com.cmpt362.cinebon.ui.theme.CinebonTheme
import com.cmpt362.cinebon.viewmodels.FriendsViewModel
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import com.ramcosta.composedestinations.navigation.EmptyDestinationsNavigator

@DashboardNavGraph
@Destination
@Composable
fun RequestListScreen(navigator: DestinationsNavigator) {
    val friendsViewModel = viewModel<FriendsViewModel>()
    val requestList = friendsViewModel.receivedRequests.collectAsStateWithLifecycle()
    val scrollState = rememberScrollState()

    friendsViewModel.getRequestList()

    Scaffold(
        topBar = {
            Row(
                verticalAlignment = Alignment.CenterVertically, modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                IconButton(onClick = { navigator.popBackStack() }) {
                    Icon(
                        imageVector = ImageVector.vectorResource(R.drawable.back_icon),
                        contentDescription = "back",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier
                            .size(24.dp),

                        )
                }

                Text(
                    text = "Friend Requests",
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.primary,
                )

            }

        }

    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(state = scrollState)
        ) {
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(8.dp)
            ){

                RequestList(requestList = requestList.value, onItemClick = {})

            }
        }
    }
}

@Composable
fun RequestList(requestList: List<ResolvedFriendRequest>, onItemClick: (FriendUser) -> Unit){
    LazyColumn {
        items(requestList) { user ->
            RequestListItem(friendRequest = user, onItemClick = onItemClick)
        }
    }
}

@Composable
fun RequestListItem(friendRequest: ResolvedFriendRequest, onItemClick: (FriendUser) -> Unit){
    Column {
        Divider(
            color = MaterialTheme.colorScheme.primary,
            thickness = 0.5.dp,
            modifier = Modifier
                .fillMaxWidth()
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = friendRequest.sender.username,
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(8.dp)
            )
            Spacer(Modifier.weight(1f))

            Button(
                onClick = {
                    /*TODO*/
                },
                colors = ButtonDefaults.buttonColors
                    (
                    contentColor = MaterialTheme.colorScheme.onPrimary,
                    containerColor = MaterialTheme.colorScheme.primary
                ),
                modifier = Modifier.padding(8.dp)
            ) {
                Text("Accept")
            }

            Button(
                onClick = {
                    /*TODO*/
                },
                colors = ButtonDefaults.buttonColors
                    (
                    contentColor = MaterialTheme.colorScheme.onError,
                    containerColor = MaterialTheme.colorScheme.error
                ),
                modifier = Modifier.padding(8.dp)
            ) {
                Text("Reject")
            }
        }
        Text(
            text = friendRequest.sender.fname + " " + friendRequest.sender.lname,
            style = MaterialTheme.typography.bodyLarge,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.padding(start = 8.dp, bottom = 8.dp, end = 8.dp)
        )
    }
}

@Preview(showBackground = true)
@Composable
fun RequestListPreview() {
    CinebonTheme {
       RequestListScreen(EmptyDestinationsNavigator)
    }
}