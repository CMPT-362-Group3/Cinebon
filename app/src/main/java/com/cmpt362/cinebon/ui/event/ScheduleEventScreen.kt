package com.cmpt362.cinebon.ui.event

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
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.cmpt362.cinebon.R
import com.cmpt362.cinebon.ui.destinations.DashboardNavDestination
import com.cmpt362.cinebon.ui.destinations.LoginScreenDestination
import com.cmpt362.cinebon.ui.destinations.SignupScreenDestination
import com.cmpt362.cinebon.ui.signup.SignupScreen
import com.cmpt362.cinebon.ui.theme.CinebonTheme
import com.ramcosta.composedestinations.navigation.EmptyDestinationsNavigator
import com.ramcosta.composedestinations.navigation.popUpTo

@Composable
fun ScheduleEventScreen() {

    val scrollState = rememberScrollState()

    Surface(
        modifier = Modifier
            .scrollable(scrollState, Orientation.Vertical)
            .fillMaxSize(), color = MaterialTheme.colorScheme.background
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                stringResource(R.string.schedule_title),
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier
                    .padding(start = 16.dp, end = 16.dp, top = 32.dp, bottom = 32.dp)
            )

            @Composable
            // function for the icon and text pair layout
            fun IconTextPair(iconId: Int, text: String) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                    modifier = Modifier
                        .padding(horizontal = 8.dp, vertical = 16.dp)
                ) {
                    Icon(
                        imageVector = ImageVector.vectorResource(id = iconId),
                        contentDescription = text,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier
                            .size(54.dp)
                    )

                    Text(
                        text = text,
                        style = MaterialTheme.typography.headlineSmall,
                        modifier = Modifier
                            .padding(top = 8.dp)
                    )
                }
            }

            // Using the IconTextPair function for each icon and text pair
            IconTextPair(R.drawable.movie_icon, "Movie")
            IconTextPair(R.drawable.calendar_icon, "Date and Time")
            IconTextPair(R.drawable.location_icon, "Location")
            IconTextPair(R.drawable.friend_icon, "With")
            IconTextPair(R.drawable.alarm_icon, "Set Alarm")
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
                    Text("Save")
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
                    Text("Cancel")
                }
            }
        }

    }
}
@Preview(showBackground = true)
@Composable
fun SignupPreview() {
    CinebonTheme {
        ScheduleEventScreen()
    }
}