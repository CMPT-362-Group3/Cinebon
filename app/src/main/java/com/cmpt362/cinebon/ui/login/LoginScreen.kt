package com.cmpt362.cinebon.ui.login

import androidx.compose.animation.core.EaseInOutSine
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.VectorConverter
import androidx.compose.animation.core.animateValue
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.cmpt362.cinebon.R
import com.cmpt362.cinebon.ui.common.AppLogo
import com.cmpt362.cinebon.ui.destinations.DashboardNavDestination
import com.cmpt362.cinebon.ui.destinations.ForgotPasswordScreenDestination
import com.cmpt362.cinebon.ui.destinations.LoginScreenDestination
import com.cmpt362.cinebon.ui.destinations.SignupScreenDestination
import com.cmpt362.cinebon.ui.theme.CinebonTheme
import com.cmpt362.cinebon.utils.SetStatusBarColor
import com.cmpt362.cinebon.viewmodels.UserAuthViewModel
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.RootNavGraph
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import com.ramcosta.composedestinations.navigation.EmptyDestinationsNavigator
import com.ramcosta.composedestinations.navigation.popUpTo

@RootNavGraph(
    start = true
)
@Destination
@Composable
fun LoginScreen(navigator: DestinationsNavigator, modifier: Modifier = Modifier) {
    val userAuthViewModel = viewModel<UserAuthViewModel>()
    val scrollState = rememberScrollState()
    var email by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }
    var error by rememberSaveable { mutableStateOf(false) }
    val infiniteTransition = rememberInfiniteTransition(label = "login_inf_transition")
    val offsetAnimation by infiniteTransition.animateValue(
        initialValue = (-15).dp, targetValue = 0.dp, typeConverter = Dp.VectorConverter, animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = EaseInOutSine), repeatMode = RepeatMode.Reverse
        ), label = "login_logo_bounce"
    )

    SetStatusBarColor(statusBarColor = MaterialTheme.colorScheme.surface)

    if (userAuthViewModel.isSignedIn()) {
        navigator.navigate(DashboardNavDestination) {
            popUpTo(LoginScreenDestination) { inclusive = true }
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
                AppLogo(modifier = Modifier.offset(y = offsetAnimation))
                Text(
                    stringResource(R.string.app_name),
                    style = MaterialTheme.typography.headlineMedium,
                    modifier = Modifier
                        .padding(16.dp)
                )

                Spacer(modifier = Modifier.height(48.dp))

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
                        password = it
                    },
                    visualTransformation = PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Password,
                        imeAction = ImeAction.Go
                    ),
                    keyboardActions = KeyboardActions(onGo = {
                        if (email != "" && password.length >= 6) {
                            userAuthViewModel.signIn(email, password, onResult = {
                                if (it != null) {
                                    error = true
                                } else {
                                    navigator.navigate(DashboardNavDestination) {
                                        popUpTo(LoginScreenDestination) { inclusive = true }
                                    }
                                }
                            })
                        }
                    }),
                    modifier = Modifier.padding(16.dp)
                )


                if (error)
                    Text(
                        text = "There was an error signing in. Please try again.",
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(16.dp),
                        color = MaterialTheme.colorScheme.error
                    )

                Button(
                    onClick = {
                        if (email != "" && password.length >= 6) {
                            userAuthViewModel.signIn(email, password, onResult = {
                                if (it != null) {
                                    error = true
                                } else {
                                    navigator.navigate(DashboardNavDestination) {
                                        popUpTo(LoginScreenDestination) { inclusive = true }
                                    }
                                }
                            })
                        }
                    },
                    modifier.padding(32.dp)
                ) {
                    Text("Login", modifier.padding(8.dp))
                }

                TextButton(
                    onClick = {
                        navigator.navigate(ForgotPasswordScreenDestination) {
                            popUpTo(LoginScreenDestination) { inclusive = true }
                        }
                    },
                    modifier = Modifier.padding(16.dp, 16.dp, 16.dp, 2.dp)
                ) {
                    Text("Forgot Password?")
                }

                TextButton(
                    onClick = {
                        navigator.navigate(SignupScreenDestination) {
                            popUpTo(LoginScreenDestination) { inclusive = true }
                        }
                    },
                    modifier = Modifier.padding(16.dp, 2.dp, 16.dp, 16.dp)
                ) {
                    Text("Don't have an account? Sign Up")
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun LoginPreview() {
    CinebonTheme {
        LoginScreen(EmptyDestinationsNavigator)
    }
}