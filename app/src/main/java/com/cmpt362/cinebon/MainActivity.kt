package com.cmpt362.cinebon

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.cmpt362.cinebon.login.LoginScreen
import com.cmpt362.cinebon.ui.theme.CinebonTheme
import com.ramcosta.composedestinations.DestinationsNavHost
import com.ramcosta.composedestinations.navigation.EmptyDestinationsNavigator

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            CinebonTheme {
                DestinationsNavHost(navGraph = NavGraphs.root)
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun MainPreview() {
    CinebonTheme {
        LoginScreen(EmptyDestinationsNavigator)
    }
}