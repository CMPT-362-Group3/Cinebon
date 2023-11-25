package com.cmpt362.cinebon.ui.chat

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.cmpt362.cinebon.R
import java.text.SimpleDateFormat
import java.util.Locale

data class ChatUser(val id: String, val name: String, val lastMessage: String, val profilePicture: Int, val lastDate:String)

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

@Preview
@Composable
fun ChatListPreview() {
    val dummyChatList = listOf(
        ChatUser("1", "Maisha", "Is anyone alive? The chat function is killing me!!", R.drawable.defaultphoto, "2023-11-25"),
        ChatUser("2", "Tanish", "LGTM!", R.drawable.defaultphoto, "2023-11-23"),
        ChatUser("3", "Darrick", "I'm still sleeping.", R.drawable.defaultphoto, "2023-11-22"),
        ChatUser("3", "Shabbir", "SKILL ISSUE", R.drawable.defaultphoto, "2023-11-20"),

    )
    ChatList(chatList = dummyChatList, onItemClick = { //todo })
    })

}
private fun formatDate(dateString: String): String {
    val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    val date = dateFormat.parse(dateString)
    return date?.let {
        SimpleDateFormat("E", Locale.getDefault()).format(date)
    } ?: ""
}
