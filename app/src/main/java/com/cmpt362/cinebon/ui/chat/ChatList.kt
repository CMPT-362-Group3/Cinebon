package com.cmpt362.cinebon.ui.chat

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
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.cmpt362.cinebon.R
import com.cmpt362.cinebon.data.entity.ResolvedChatEntity
import com.cmpt362.cinebon.ui.destinations.ChatScreenDestination
import com.cmpt362.cinebon.ui.destinations.FriendProfileScreenDestination
import com.cmpt362.cinebon.utils.SetStatusBarColor
import com.cmpt362.cinebon.utils.formatted
import com.cmpt362.cinebon.viewmodels.ChatListViewModel
import com.ramcosta.composedestinations.navigation.DestinationsNavigator

@Composable
fun ChatList(navigator: DestinationsNavigator) {
    val chatListVM = viewModel<ChatListViewModel>()
    val chatList = chatListVM.resolvedChats.collectAsStateWithLifecycle().value

    SetStatusBarColor(statusBarColor = MaterialTheme.colorScheme.surfaceColorAtElevation(3.dp))

    LazyColumn {
        items(chatList) { chatItem ->
            ChatListItem(
                chat = chatItem,
                onItemClick = {
                    navigator.navigate(ChatScreenDestination(chatId = it.chatId))
                },
                onProfileClick = {
                    navigator.navigate(FriendProfileScreenDestination(chatItem.others.first().userId))
                }
            )
        }
    }
}

@Composable
fun ChatListItem(chat: ResolvedChatEntity, onItemClick: (ResolvedChatEntity) -> Unit = {}, onProfileClick: () -> Unit = {}) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onItemClick(chat) }, color = MaterialTheme.colorScheme.background
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onProfileClick) {
                Icon(
                    imageVector = ImageVector.vectorResource(id = R.drawable.profile_icon),
                    contentDescription = "Profile picture",
                    tint = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                )
            }


            Spacer(modifier = Modifier.width(16.dp))

            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = chat.others.joinToString { it.fname },
                        style = MaterialTheme.typography.bodyMedium,
                        maxLines = 1
                    )

                    Text(
                        text = if (chat.messages.isEmpty()) "" else chat.messages.last().timestamp.formatted(),
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.weight(1f),
                        textAlign = TextAlign.End
                    )
                }
                Text(
                    text = if (chat.messages.isEmpty()) "" else chat.messages.last().text,
                    style = MaterialTheme.typography.bodySmall,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.padding(vertical = 4.dp)
                )
            }

        }
    }
}
