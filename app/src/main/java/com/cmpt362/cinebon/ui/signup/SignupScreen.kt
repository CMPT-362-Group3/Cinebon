package com.cmpt362.cinebon.ui.signup

import androidx.compose.animation.core.EaseInOutSine
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.VectorConverter
import androidx.compose.animation.core.animateValue
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.cmpt362.cinebon.R
import com.cmpt362.cinebon.ui.destinations.DashboardNavDestination
import com.cmpt362.cinebon.ui.destinations.LoginScreenDestination
import com.cmpt362.cinebon.ui.destinations.SignupScreenDestination
import com.cmpt362.cinebon.ui.theme.CinebonTheme
import com.cmpt362.cinebon.viewmodels.ListViewModel
import com.cmpt362.cinebon.viewmodels.UserAuthViewModel
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.RootNavGraph
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import com.ramcosta.composedestinations.navigation.EmptyDestinationsNavigator
import com.ramcosta.composedestinations.navigation.popUpTo

@RootNavGraph(
    start = false
)
@Destination
@Composable
fun SignupScreen(navigator: DestinationsNavigator, modifier: Modifier = Modifier) {
    val defaultImage = ImageBitmap.imageResource(R.drawable.defaultphoto).asAndroidBitmap()
    // I don't know if the above line is correct, but it's what I think it should be

    val userAuthViewModel = viewModel<UserAuthViewModel>()
    val listViewModel = viewModel<ListViewModel>()
    val scrollState = rememberScrollState()
    var username by rememberSaveable { mutableStateOf("") }
    var fName by rememberSaveable { mutableStateOf("") }
    var lName by rememberSaveable { mutableStateOf("") }
    var email by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }
    var error by rememberSaveable { mutableStateOf(false) }
    var invalidPassword by rememberSaveable { mutableStateOf(false) }
    val infiniteTransition = rememberInfiniteTransition(label = "signup_inf_transition")
    val offsetAnimation by infiniteTransition.animateValue(
        initialValue = (-15).dp, targetValue = 0.dp, typeConverter = Dp.VectorConverter, animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = EaseInOutSine), repeatMode = RepeatMode.Reverse
        ), label = "login_logo_bounce"
    )

    if (userAuthViewModel.isSignedIn()) {
        navigator.navigate(DashboardNavDestination) {
            popUpTo(SignupScreenDestination) { inclusive = true }
        }
    } else {
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
                    painter = painterResource(id = R.drawable.cinebon),
                    contentDescription = "App logo",
                    modifier = Modifier
                        .offset(y = offsetAnimation)
                        .size(64.dp)
                )

                Text(
                    stringResource(R.string.signup_title),
                    style = MaterialTheme.typography.headlineMedium,
                    modifier = Modifier
                        .padding(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 64.dp)
                )

                OutlinedTextField(
                    value = username,
                    label = { Text("Username") },
                    onValueChange = {
                        username = it.trim()
                    },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Text,
                        imeAction = ImeAction.Next
                    ),
                    keyboardActions = KeyboardActions.Default,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                )


                OutlinedTextField(
                    value = fName,
                    label = { Text("First Name") },
                    onValueChange = {
                        fName = it.trim()
                    },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Text,
                        imeAction = ImeAction.Next
                    ),
                    keyboardActions = KeyboardActions.Default,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                )

                OutlinedTextField(
                    value = lName,
                    label = { Text("Last Name") },
                    onValueChange = {
                        lName = it.trim()
                    },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Text,
                        imeAction = ImeAction.Next
                    ),
                    keyboardActions = KeyboardActions.Default,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                )

                OutlinedTextField(
                    value = email,
                    label = { Text("Email") },
                    onValueChange = {
                        email = it.trim()
                    },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Email,
                        imeAction = ImeAction.Next
                    ),
                    keyboardActions = KeyboardActions.Default,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                )

                OutlinedTextField(
                    value = password,
                    label = { Text("Password") },
                    onValueChange = {
                        password = it.trim()
                    },
                    visualTransformation = PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Password,
                        imeAction = ImeAction.Go
                    ),
                    keyboardActions = KeyboardActions(onGo = {
                        invalidPassword = password.length < 6
                        if (!invalidPassword)
                            userAuthViewModel.signUp(email, password, fName, lName,username,
                                defaultImage) {
                                if (it != null) {
                                    error = true
                                } else {
                                    listViewModel.createDefaultList()
                                    navigator.navigate(DashboardNavDestination) {
                                        popUpTo(SignupScreenDestination) { inclusive = true }
                                    }
                                }
                            }
                    }),
                    modifier = Modifier.padding(horizontal = 16.dp)
                )

                if (invalidPassword)
                    Text(
                        text = "Your password must contain at least 6 characters",
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(16.dp),
                        color = MaterialTheme.colorScheme.error
                    )

                if (error)
                    Text(
                        text = "There was an error signing up",
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(16.dp),
                        color = MaterialTheme.colorScheme.error
                    )

                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                ) {

                    Button(
                        onClick = {
                            invalidPassword = password.length < 6
                            if (!invalidPassword)
                                userAuthViewModel.signUp(email, password, fName, lName,
                                    username, defaultImage) {
                                    if (it != null) {
                                        error = true
                                    } else {
                                        listViewModel.createDefaultList()
                                        navigator.navigate(DashboardNavDestination) {
                                            popUpTo(SignupScreenDestination) { inclusive = true }
                                        }
                                    }
                                }
                        },
                        colors = ButtonDefaults.buttonColors
                            (
                            contentColor = MaterialTheme.colorScheme.onPrimary,
                            containerColor = MaterialTheme.colorScheme.primary
                        ),
                        modifier = modifier
                            .padding(vertical = 32.dp)
                            .padding(start = 16.dp, end = 8.dp)
                    ) {
                        Text("Sign Up", modifier.padding(8.dp))
                    }

                    Button(
                        onClick = {
                            navigator.navigate(LoginScreenDestination) {
                                popUpTo(SignupScreenDestination) { inclusive = true }
                            }
                        },
                        colors = ButtonDefaults.buttonColors
                            (
                            contentColor = MaterialTheme.colorScheme.onErrorContainer,
                            containerColor = MaterialTheme.colorScheme.errorContainer
                        ),
                        modifier = modifier
                            .padding(vertical = 32.dp)
                            .padding(start = 8.dp, end = 16.dp)
                    ) {
                        Text("Cancel", modifier.padding(8.dp))
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun SignupPreview() {
    CinebonTheme {
        SignupScreen(EmptyDestinationsNavigator)
    }
}