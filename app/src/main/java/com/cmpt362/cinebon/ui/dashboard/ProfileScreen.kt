package com.cmpt362.cinebon.ui.dashboard

import androidx.compose.foundation.Image
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.cmpt362.cinebon.R
import com.cmpt362.cinebon.ui.destinations.SettingsScreenDestination
import com.cmpt362.cinebon.ui.theme.CinebonTheme
import com.cmpt362.cinebon.viewmodels.UserAuthViewModel
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import com.ramcosta.composedestinations.navigation.EmptyDestinationsNavigator

@DashboardNavGraph
@Destination
@Composable
fun ProfileScreen(navigator: DestinationsNavigator) {
    val userAuthViewModel = viewModel<UserAuthViewModel>()

    val scrollState = rememberScrollState()
    // TODO: insert variables here

    val defaultImage = ImageBitmap.imageResource(R.drawable.defaultphoto).asAndroidBitmap()
    val userInfo = userAuthViewModel.userFlow.collectAsStateWithLifecycle()
    val friendsCount by rememberSaveable { mutableIntStateOf(0) }
    val moviesWatched by rememberSaveable { mutableIntStateOf(0) }
    val lastWatched by rememberSaveable { mutableStateOf("") }

    // Triggers the userViewModel to get the signed in user
    userAuthViewModel.getSignedInUser {}

    Surface(
        modifier = Modifier
            .scrollable(scrollState, Orientation.Vertical)
            .fillMaxSize(), color = MaterialTheme.colorScheme.background
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {

            // TODO: this is all fake data, fix in the future
            Image(
                bitmap = userInfo.value?.profilePicture?.asImageBitmap() ?: defaultImage.asImageBitmap(),
                contentDescription = "Profile Picture",
                modifier = Modifier
                    .size(175.dp)
                    .clip(CircleShape)
            )
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier
                    .padding(horizontal = 8.dp)
            ) {
                Text(
                    text = userInfo.value?.username ?: "",
                    style = MaterialTheme.typography.displaySmall,
                    modifier = Modifier
                        .padding(start = 16.dp, top = 8.dp, bottom = 8.dp, end = 8.dp)
                )

                Icon(
                    imageVector = ImageVector.vectorResource(id = R.drawable.edit_icon),
                    contentDescription = "Edit",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier
                        .size(54.dp)
                        .padding(top = 8.dp)
                        .clickable { navigator.navigate(SettingsScreenDestination) }
                )

            }
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier
                    .padding(horizontal = 8.dp)
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center, modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Friends",
                        style = MaterialTheme.typography.titleLarge,
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
                        style = MaterialTheme.typography.titleLarge,
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
                    text = "Last Watched: $lastWatched",
                    style = MaterialTheme.typography.titleLarge,
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
                Text("${userInfo.value?.fname}'s Movie List")
            }
        }

    }
}

@Preview(showBackground = true)
@Composable
fun ProfilePreview() {
    CinebonTheme {
        ProfileScreen(EmptyDestinationsNavigator)
    }
}