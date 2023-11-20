package com.cmpt362.cinebon.dashboard

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import com.ramcosta.composedestinations.annotation.Destination

@DashboardNavGraph
@Destination
@Composable
fun ProfileScreen() {
    Text(text = "Profile screen")
}