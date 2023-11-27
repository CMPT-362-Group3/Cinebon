package com.cmpt362.cinebon.ui.settings

import androidx.compose.foundation.Image
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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.cmpt362.cinebon.R
import com.cmpt362.cinebon.ui.theme.CinebonTheme
import com.ramcosta.composedestinations.annotation.Destination

@Destination
@Composable
fun SettingsScreen() {
    val scrollState = rememberScrollState()

    // TODO: user thing here
    val profilePicture = R.drawable.defaultphoto
    val username = "JohnDoe"
    val firstName = "John"
    val lastName = "Doe"
    val email = "johndoe@gmail.com"

    Surface(
        modifier = Modifier
            .scrollable(scrollState, Orientation.Vertical)
            .fillMaxSize(), color = MaterialTheme.colorScheme.background
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Image(
                painter = painterResource(id = profilePicture),
                contentDescription = "Profile Picture",
                modifier = Modifier
                    .size(175.dp)
            )
            // TODO: spacing here TBD later

            TextField(
                value = username,
                label = { Text("Username") },
                onValueChange = {
                    // TODO: insert functionality
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
                value = firstName,
                label = { Text("First Name") },
                onValueChange = {
                    // TODO: insert functionality
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
                value = lastName,
                label = { Text("Last Name") },
                onValueChange = {
                    // TODO: insert functionality
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
                value = email,
                label = { Text("Email Address") },
                onValueChange = {
                    // TODO: insert functionality
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
        SettingsScreen()
    }
}

