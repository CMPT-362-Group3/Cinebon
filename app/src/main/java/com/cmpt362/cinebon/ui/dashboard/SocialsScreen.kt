package com.cmpt362.cinebon.ui.dashboard

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.cmpt362.cinebon.R
import com.cmpt362.cinebon.data.chats.ChatUser
import com.cmpt362.cinebon.ui.theme.CinebonTheme
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import com.ramcosta.composedestinations.navigation.EmptyDestinationsNavigator
import java.text.SimpleDateFormat
import java.util.Locale

@DashboardNavGraph
@Destination
@Composable
fun SocialScreen(navigator: DestinationsNavigator) {
    val scrollState = rememberScrollState()
    Surface(
        modifier = Modifier
            .scrollable(scrollState, Orientation.Vertical)
            .fillMaxSize(), color = MaterialTheme.colorScheme.background
    ){
        Row(
            verticalAlignment = Alignment.CenterVertically, modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ){
            IconButton(onClick = { /*TODO*/ }) {
                Icon(
                    imageVector = ImageVector.vectorResource(R.drawable.defaultphoto),
                    contentDescription = "profile",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(24.dp)
                )
            }
        }

    }
}

@Composable
fun ChatListItem(user: ChatUser, onItemClick: (ChatUser) -> Unit) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onItemClick(user) }
            .padding(16.dp), color = MaterialTheme.colorScheme.background
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(id = user.profilePicture),
                contentDescription = null,
                modifier = Modifier
                    .size(56.dp)
                    .clip(CircleShape) //making the profile picture show as a circle
            )

            Spacer(modifier = Modifier.width(16.dp))

            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = user.name, style = MaterialTheme.typography.bodyMedium)
                    Text(
                        text = formatDate(user.lastDate),
                        style = MaterialTheme.typography.bodySmall,
                    )
                }
                Text(
                    text = "${user.lastMessage}",
                    style = MaterialTheme.typography.bodySmall,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

        }
    }
}
private fun formatDate(dateString: String): String {
    val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    val date = dateFormat.parse(dateString)
    return date?.let {
        SimpleDateFormat("E", Locale.getDefault()).format(date)
    } ?: ""
}
@Preview(showBackground = true)
@Composable
fun SocialsPreview() {
    CinebonTheme {
        SocialScreen(EmptyDestinationsNavigator)
    }
}
