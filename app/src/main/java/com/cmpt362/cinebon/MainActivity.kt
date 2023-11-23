package com.cmpt362.cinebon

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.cmpt362.cinebon.ui.NavGraphs
import com.cmpt362.cinebon.ui.login.LoginScreen
import com.cmpt362.cinebon.ui.theme.CinebonTheme
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.ramcosta.composedestinations.DestinationsNavHost
import com.ramcosta.composedestinations.navigation.EmptyDestinationsNavigator

class MainActivity : ComponentActivity() {
    private lateinit var auth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        auth = Firebase.auth
        super.onCreate(savedInstanceState)
        setContent {
            CinebonTheme {
                DestinationsNavHost(navGraph = NavGraphs.root)
            }
        }
    }

    override fun onStart() {
        super.onStart()
        val currentUser = auth.currentUser
        if(currentUser != null)
            Log.d("debug", "User Logged In")
    }
}

@Preview(showBackground = true)
@Composable
fun MainPreview() {
    CinebonTheme {
        LoginScreen(EmptyDestinationsNavigator)
    }
}