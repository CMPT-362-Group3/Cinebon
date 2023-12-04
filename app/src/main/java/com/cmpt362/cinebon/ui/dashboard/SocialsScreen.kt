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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.surfaceColorAtElevation
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
import androidx.navigation.compose.rememberNavController
import com.cmpt362.cinebon.R
import com.cmpt362.cinebon.data.chats.ChatUser
import com.cmpt362.cinebon.data.enums.DashboardNavItems
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
        Column{
            Row(
                verticalAlignment = Alignment.CenterVertically, modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
            ){
                IconButton(onClick = {navigator.navigate(DashboardNavItems.Profile.destination.route){
                    launchSingleTop = true //if profile screen is already at the top of the stack it will be reused instead of creating another instance of it
                } }) {
                    Icon(
                        imageVector = ImageVector.vectorResource(R.drawable.profile_icon),
                        contentDescription = "profile",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier
                            .size(48.dp),

                        )
                }
                Spacer(modifier = Modifier.weight(1f))
                IconButton(onClick = { /*TODO*/ }) {
                    Icon(
                        imageVector = ImageVector.vectorResource(R.drawable.addchat_icon),
                        contentDescription = "new chat",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier
                            .size(48.dp),

                        )
                }
                IconButton(onClick = { /*TODO*/ }) {
                    Icon(
                        imageVector = ImageVector.vectorResource(R.drawable.search_icon),
                        contentDescription = "search",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier
                            .size(48.dp)

                        )
                }
            }
            Text(
                text = "Chats",
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(8.dp)
            )
            ChatList(dummyChatList(), onItemClick = {/*TODO*/})
        }


    }
}
private fun dummyChatList():List<ChatUser>{
    val dummyChatList = listOf(
        ChatUser("1", "Maisha", "Is anyone alive? The chat function is killing me!!", "2023-11-25"),
        ChatUser("2", "Tanish", "LGTM!", "2023-11-23"),
        ChatUser("3", "Darrick", "I'm still sleeping.", "2023-11-22"),
        ChatUser("3", "Shabbir", "SKILL ISSUE", "2023-11-20"),

        )
    return dummyChatList
}
@Composable
fun ChatList(chatList: List<ChatUser>, onItemClick: (ChatUser) -> Unit) {
    LazyColumn {
        items(chatList) { user ->
            ChatListItem(user = user, onItemClick = onItemClick)
        }
    }
}

@Composable
fun ChatListItem(user: ChatUser, onItemClick: (ChatUser) -> Unit) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onItemClick(user) },
        color = MaterialTheme.colorScheme.background
    ) {
        Divider(
            color = MaterialTheme.colorScheme.primary,
            thickness = 0.5.dp,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp)
        )

        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = user.name,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
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
