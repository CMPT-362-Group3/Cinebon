package com.cmpt362.cinebon

import android.Manifest.permission.FOREGROUND_SERVICE
import android.Manifest.permission.FOREGROUND_SERVICE_DATA_SYNC
import android.Manifest.permission.POST_NOTIFICATIONS
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.app.ActivityCompat
import com.cmpt362.cinebon.ui.NavGraphs
import com.cmpt362.cinebon.ui.login.LoginScreen
import com.cmpt362.cinebon.ui.theme.CinebonTheme
import com.cmpt362.cinebon.utils.hasNotificationPerm
import com.ramcosta.composedestinations.DestinationsNavHost
import com.ramcosta.composedestinations.navigation.EmptyDestinationsNavigator

class MainActivity : ComponentActivity() {

    companion object {
        const val PERMISSION_REQUEST_CODE = 1
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        requestAppPermissions()

        setContent {
            CinebonTheme {
                DestinationsNavHost(navGraph = NavGraphs.root)
            }
        }
    }

    // function to request notification permissions:
    private fun requestAppPermissions() {
        val permissionList = if (Build.VERSION.SDK_INT >= 34) {
            mutableListOf(
                FOREGROUND_SERVICE,
                FOREGROUND_SERVICE_DATA_SYNC
            )
        } else {
            mutableListOf(
                FOREGROUND_SERVICE
            )
        }

        if (!hasNotificationPerm()) {
            permissionList.add(POST_NOTIFICATIONS)
        }
        if (permissionList.isNotEmpty()) {
            ActivityCompat.requestPermissions(
                this,
                permissionList.toTypedArray(),
                PERMISSION_REQUEST_CODE
            )
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