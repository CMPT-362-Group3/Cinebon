package com.cmpt362.cinebon.ui.settings


import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.cmpt362.cinebon.R
import com.cmpt362.cinebon.ui.dashboard.DashboardNavGraph
import com.cmpt362.cinebon.ui.destinations.ProfileScreenDestination
import com.cmpt362.cinebon.ui.theme.CinebonTheme
import com.cmpt362.cinebon.viewmodels.UserAuthViewModel
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import com.ramcosta.composedestinations.navigation.EmptyDestinationsNavigator

@DashboardNavGraph
@Destination
@Composable
fun SettingsScreen(navigator: DestinationsNavigator) {
    val userAuthViewModel = viewModel<UserAuthViewModel>()
    val scrollState = rememberScrollState()

    val userInfo = userAuthViewModel.userFlow.collectAsStateWithLifecycle()

    userAuthViewModel.getSignedInUser()

    var username by rememberSaveable { mutableStateOf(userInfo.value?.username?: "") }
    var firstName by rememberSaveable { mutableStateOf(userInfo.value?.fname?: "") }
    var lastName by rememberSaveable { mutableStateOf(userInfo.value?.lname?: "") }
    var email by rememberSaveable { mutableStateOf(userInfo.value?.email?: "") }


    val context = LocalContext.current

    Surface(
        modifier = Modifier
            .scrollable(scrollState, Orientation.Vertical)
            .fillMaxSize(), color = MaterialTheme.colorScheme.background
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                 imageVector = ImageVector.vectorResource(id = R.drawable.profile_icon),
                contentDescription = "Profile Picture",
                modifier = Modifier
                    .size(175.dp)
            )
            // TODO: spacing here TBD later

            TextField(
                value = userInfo.value?.username ?: "",
                label = { Text("Username") },
                onValueChange = {
                                newUsername -> username = newUsername
                },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Text,
                    imeAction = ImeAction.Next
                ),
                keyboardActions = KeyboardActions.Default,
                modifier = Modifier
                    .padding(horizontal = 4.dp, vertical = 8.dp)
                    .background(color = Color.Transparent)
            )

            TextField(
                value = userInfo.value?.fname ?: "",
                label = { Text("First Name") },
                onValueChange = {
                                newFirstName -> firstName = newFirstName
                },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Text,
                    imeAction = ImeAction.Next
                ),
                keyboardActions = KeyboardActions.Default,
                modifier = Modifier
                    .padding(horizontal = 4.dp, vertical = 8.dp)
                    .background(color = Color.Transparent)
            )

            TextField(
                value = userInfo.value?.lname ?: "",
                label = { Text("Last Name") },
                onValueChange = {
                                newLastName -> lastName = newLastName
                },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Text,
                    imeAction = ImeAction.Next
                ),
                keyboardActions = KeyboardActions.Default,
                modifier = Modifier
                    .padding(horizontal = 4.dp, vertical = 8.dp)
                    .background(color = Color.Transparent)
            )

            TextField(
                value = userInfo.value?.email ?: "",
                label = { Text("Email Address") },
                onValueChange = {
                                newEmail -> email = newEmail
                },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Text,
                    imeAction = ImeAction.Next
                ),
                keyboardActions = KeyboardActions.Default,
                modifier = Modifier
                    .padding(horizontal = 4.dp, vertical = 8.dp)
                    .background(color = Color.Transparent)
            )

            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {

                Button(
                    onClick = {
                        // calls updateUserProfile from userAuthViewModel, which calls updateUserData in user repo
                              userAuthViewModel.updateUserProfile(
                                  username,
                                  firstName,
                                  lastName,
                                  email
                              ) { result ->
                                  if (result == null) {
                                      // success, toast and exit
                                      Toast.makeText(context, "Successfully updated profile", Toast.LENGTH_SHORT).show()
                                      navigator.navigate(ProfileScreenDestination)
                                  } else {
                                      // fail, inform that it failed
                                      Toast.makeText(context, "Failed to update profile", Toast.LENGTH_SHORT).show()
                                  }
                              }
                    },
                    colors = ButtonDefaults.buttonColors
                        (
                        contentColor = MaterialTheme.colorScheme.onPrimary,
                        containerColor = MaterialTheme.colorScheme.primary
                    ),
                    modifier = Modifier
                        .padding(vertical = 32.dp)
                        .padding(start = 16.dp, end = 8.dp)
                ) {
                    Text("Save", Modifier.padding(8.dp))
                }

                Button(
                    onClick = {
                        navigator.navigate(ProfileScreenDestination)
                    },
                    colors = ButtonDefaults.buttonColors
                        (
                        contentColor = MaterialTheme.colorScheme.onErrorContainer,
                        containerColor = MaterialTheme.colorScheme.errorContainer
                    ),
                    modifier = Modifier
                        .padding(vertical = 32.dp)
                        .padding(start = 8.dp, end = 16.dp)
                ) {
                    Text("Cancel", Modifier.padding(8.dp))
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun SettingsScreenPreview() {
    CinebonTheme {
        SettingsScreen(EmptyDestinationsNavigator)
    }
}