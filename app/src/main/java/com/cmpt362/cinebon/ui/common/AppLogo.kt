package com.cmpt362.cinebon.ui.common

import androidx.compose.foundation.Image
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import com.cmpt362.cinebon.R

// A simple composable to show the app logo
@Composable
fun AppLogo(modifier: Modifier = Modifier) {
    return Image(
        painter = painterResource(id = R.drawable.cinebon),
        contentDescription = "App logo",
        modifier = modifier
    )
}