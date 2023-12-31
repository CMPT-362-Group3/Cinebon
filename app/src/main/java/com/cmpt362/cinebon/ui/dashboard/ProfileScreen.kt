package com.cmpt362.cinebon.ui.dashboard

import android.app.Activity
import android.content.Intent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.cmpt362.cinebon.MainActivity
import com.cmpt362.cinebon.R
import com.cmpt362.cinebon.ui.destinations.IndividualListScreenDestination
import com.cmpt362.cinebon.ui.destinations.RequestListScreenDestination
import com.cmpt362.cinebon.ui.destinations.SettingsScreenDestination
import com.cmpt362.cinebon.ui.theme.CinebonTheme
import com.cmpt362.cinebon.utils.SetStatusBarColor
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

    val userInfo by userAuthViewModel.userFlow.collectAsStateWithLifecycle()

    // Triggers the userViewModel to get the signed in user
    userAuthViewModel.getSignedInUser()

    SetStatusBarColor(statusBarColor = MaterialTheme.colorScheme.surfaceColorAtElevation(3.dp))

    if (userInfo == null) {
        LocalContext.current.startActivity(Intent(LocalContext.current, MainActivity::class.java))
        val context = LocalContext.current as Activity
        context.finish()
    } else {

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
                IconButton(
                    onClick = {
                        navigator.popBackStack()
                    }
                ) {
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
                verticalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector = ImageVector.vectorResource(id = R.drawable.profile_icon),
                    contentDescription = "profile picture",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier
                        .size(200.dp)
                )
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier
                        .padding(horizontal = 8.dp)
                ) {
                    Text(
                        text = userInfo?.username ?: "",
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
                Button(
                    onClick = {
                        navigator.navigate(RequestListScreenDestination)
                    },
                    colors = ButtonDefaults.buttonColors
                        (
                        contentColor = MaterialTheme.colorScheme.onSecondaryContainer,
                        containerColor = MaterialTheme.colorScheme.secondaryContainer
                    ),
                    modifier = Modifier.padding(8.dp)
                ) {
                    Text("Friend Requests")
                }
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier
                        .padding(horizontal = 8.dp)
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center,
                        modifier = Modifier.weight(1f)
                    ) {
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
                            text = (userInfo?.friends?.size ?: 0).toString(),
                            style = MaterialTheme.typography.headlineMedium
                        )
                    }
                }

                Button(
                    onClick = {
                        navigator.navigate(IndividualListScreenDestination(listId = userInfo?.defaultList?.id!!))
                    },
                    colors = ButtonDefaults.buttonColors
                        (
                        contentColor = MaterialTheme.colorScheme.onPrimary,
                        containerColor = MaterialTheme.colorScheme.primary
                    ),
                    modifier = Modifier
                        .padding(vertical = 64.dp)
                ) {
                    Text("${userInfo?.fname}'s Movie List")
                }

                Row(horizontalArrangement = Arrangement.End, modifier = Modifier.fillMaxWidth()) {
                    Button(
                        onClick = { userAuthViewModel.signOut() },
                        colors = ButtonDefaults.buttonColors
                            (
                            contentColor = MaterialTheme.colorScheme.onError,
                            containerColor = MaterialTheme.colorScheme.error
                        ),
                        modifier = Modifier
                            .padding(vertical = 12.dp, horizontal = 12.dp)
                    ) {
                        Text(text = "Sign Out")
                    }

                }
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