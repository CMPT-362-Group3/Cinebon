package com.cmpt362.cinebon.ui.chat

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.cmpt362.cinebon.R
import com.cmpt362.cinebon.data.entity.ResolvedChatEntity
import com.cmpt362.cinebon.ui.dashboard.DashboardNavGraph
import com.cmpt362.cinebon.viewmodels.ChatListViewModel
import com.ramcosta.composedestinations.annotation.Destination
import java.text.SimpleDateFormat
import java.util.Locale

data class ChatUser(val id: String, val name: String, val lastMessage: String, val profilePicture: Int, val lastDate: String)

@DashboardNavGraph
@Destination
@Composable
fun ChatListScreen() {
    val chatListVM = viewModel<ChatListViewModel>()
    val chatList = chatListVM.resolvedChats.collectAsStateWithLifecycle().value

    LazyColumn {
        items(chatList) { chatItem ->
            ChatListItem(chat = chatItem, onItemClick = {
                // TODO: Navigate to individual chat screen
            })
        }
    }

}

@Composable
fun ChatListItem(chat: ResolvedChatEntity, onItemClick: (ResolvedChatEntity) -> Unit) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onItemClick(chat) }
            .padding(16.dp), color = MaterialTheme.colorScheme.background
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(id = R.drawable.defaultphoto),
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
                    Text(
                        text = chat.others.joinToString { it.fname },
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Text(
                        text = "Replace date", // TODO: Replace with last message date
                        style = MaterialTheme.typography.bodySmall,
                    )
                }
                Text(
                    text = "{user.lastMessage}", // TODO: Replace with last message
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
