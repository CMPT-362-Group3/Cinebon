package com.cmpt362.cinebon.signup

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
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.cmpt362.cinebon.R
import com.cmpt362.cinebon.ui.theme.CinebonTheme
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.RootNavGraph

@RootNavGraph(
    start = false
)
@Destination
@Composable
fun SignupScreen(modifier: Modifier = Modifier) {

    val scrollState = rememberScrollState()
    var fName by rememberSaveable { mutableStateOf("") }
    var lName by rememberSaveable { mutableStateOf("") }
//    var username by rememberSaveable { mutableStateOf("") }
    var email by rememberSaveable { mutableStateOf("") }
//    var phone by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }
    val infiniteTransition = rememberInfiniteTransition(label = "signup_inf_transition")
    val offsetAnimation by infiniteTransition.animateValue(
        initialValue = (-15).dp, targetValue = 0.dp, typeConverter = Dp.VectorConverter, animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = EaseInOutSine), repeatMode = RepeatMode.Reverse
        ), label = "login_logo_bounce"
    )

    Surface(
        modifier = Modifier
            .scrollable(scrollState, Orientation.Vertical)
            .fillMaxSize(), color = MaterialTheme.colorScheme.background
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
            Image(
                painter = painterResource(id = R.drawable.cinebon),
                contentDescription = "App logo",
                modifier = Modifier
                    .offset(y = offsetAnimation)
                    .size(150.dp)
            )

            OutlinedTextField(
                value = fName,
                label = { Text("First Name") },
                onValueChange = {
                    fName = it.trim()
                },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text, imeAction = ImeAction.Next),
                keyboardActions = KeyboardActions.Default,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
            )

            OutlinedTextField(
                value = lName,
                label = { Text("Last Name") },
                onValueChange = {
                    lName = it.trim()
                },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text, imeAction = ImeAction.Next),
                keyboardActions = KeyboardActions.Default,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
            )

//            OutlinedTextField(
//                value = username,
//                label = { Text("Username") },
//                onValueChange = {
//                    username = it.trim()
//                },
//                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text, imeAction = ImeAction.Next),
//                keyboardActions = KeyboardActions.Default,
//                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
//            )

            OutlinedTextField(
                value = email,
                label = { Text("Email") },
                onValueChange = {
                    email = it.trim()
                },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email, imeAction = ImeAction.Next),
                keyboardActions = KeyboardActions.Default,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
            )

//            OutlinedTextField(
//                value = phone,
//                label = { Text("Phone Number") },
//                onValueChange = {
//                    phone = it.trim()
//                },
//                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone, imeAction = ImeAction.Next),
//                keyboardActions = KeyboardActions.Default,
//                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
//            )

            OutlinedTextField(
                value = password,
                label = { Text("Password") },
                onValueChange = {
                    password = it.trim()
                },
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password, imeAction = ImeAction.Go),
                keyboardActions = KeyboardActions(onGo = {
                    // TODO: firebase stuff?
                }),
                modifier = Modifier.padding(horizontal = 16.dp)
            )

            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {

                Button(
                    onClick = {

                    },
                    modifier.padding(32.dp)
                ) {
                    Text("Sign Up", modifier.padding(8.dp))
                }

                Button(
                    onClick = {

                    },
                    modifier.padding(32.dp)
                ) {
                    Text("Cancel", modifier.padding(8.dp))
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun SignupPreview() {
    CinebonTheme {
        SignupScreen()
    }
}