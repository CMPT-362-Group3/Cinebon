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
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.cmpt362.cinebon.R
import com.cmpt362.cinebon.ui.common.AppLogo
import com.cmpt362.cinebon.ui.destinations.ForgotPasswordScreenDestination
import com.cmpt362.cinebon.ui.destinations.LoginScreenDestination
import com.cmpt362.cinebon.ui.theme.CinebonTheme
import com.cmpt362.cinebon.utils.SetStatusBarColor
import com.cmpt362.cinebon.viewmodels.UserAuthViewModel
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import com.ramcosta.composedestinations.navigation.EmptyDestinationsNavigator
import com.ramcosta.composedestinations.navigation.popUpTo


@Destination
@Composable
fun ForgotPasswordScreen(navigator: DestinationsNavigator, modifier: Modifier = Modifier) {
    val userAuthViewModel = viewModel<UserAuthViewModel>()
    val scrollState = rememberScrollState()
    var email by rememberSaveable { mutableStateOf("") }
    var resetEmailSent by rememberSaveable { mutableStateOf(false) }
    val infiniteTransition = rememberInfiniteTransition(label = "login_inf_transition")
    val offsetAnimation by infiniteTransition.animateValue(
        initialValue = (-15).dp, targetValue = 0.dp, typeConverter = Dp.VectorConverter, animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = EaseInOutSine), repeatMode = RepeatMode.Reverse
        ), label = "login_logo_bounce"
    )

    SetStatusBarColor(statusBarColor = MaterialTheme.colorScheme.surface)

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

            if (resetEmailSent)
                Text(
                    text = "A reset link has been sent to your email",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(16.dp),
                )
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {

                if (!resetEmailSent) {
                    Button(
                        onClick = {
                            if (email != "") {
                                userAuthViewModel.sendResetPasswordEmail(email, onResult = {
                                    resetEmailSent = it == null
                                })
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
                        Text("Send Reset", modifier.padding(8.dp))
                    }
                } else {
                    Button(
                        onClick = {
                            navigator.navigate(LoginScreenDestination) {
                                popUpTo(ForgotPasswordScreenDestination) { inclusive = true }
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
                        Text("Back To Login", modifier.padding(8.dp))
                    }
                }

                if (!resetEmailSent) {
                    Button(
                        onClick = {
                            navigator.navigate(LoginScreenDestination) {
                                popUpTo(ForgotPasswordScreenDestination) { inclusive = true }
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
fun ForgotPasswordPreview() {
    CinebonTheme {
        LoginScreen(EmptyDestinationsNavigator)
    }
}