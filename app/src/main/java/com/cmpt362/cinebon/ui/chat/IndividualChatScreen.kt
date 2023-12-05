package com.cmpt362.cinebon.ui.chat

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.cmpt362.cinebon.R
import com.cmpt362.cinebon.data.entity.ResolvedMessageEntity
import com.cmpt362.cinebon.ui.common.Border
import com.cmpt362.cinebon.ui.common.border
import com.cmpt362.cinebon.ui.dashboard.DashboardNavGraph
import com.cmpt362.cinebon.utils.SetStatusBarColor
import com.cmpt362.cinebon.viewmodels.IndividualChatViewModel
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator

@OptIn(ExperimentalMaterial3Api::class)
@DashboardNavGraph
@Destination
@Composable
fun ChatScreen(navigator: DestinationsNavigator, chatId: String) {

    val chatViewModel = viewModel<IndividualChatViewModel>(factory = IndividualChatViewModel.Factory(chatId))

    val currentChat = chatViewModel.currentChat.collectAsStateWithLifecycle()
    val lazyListState = rememberLazyListState()
    var newMessage by remember { mutableStateOf("") } //remember the value of the message written

    LaunchedEffect(key1 = currentChat.value?.messages?.size) {
        if ((currentChat.value?.messages?.size ?: 0) > 0) {
            Log.d("ChatScreen", "Scrolling to bottom")
            lazyListState.animateScrollToItem(currentChat.value!!.messages.size - 1)
        }
    }

    SetStatusBarColor(statusBarColor = MaterialTheme.colorScheme.surfaceColorAtElevation(3.dp))

    Scaffold(
        topBar = {
            Surface(color = MaterialTheme.colorScheme.surfaceColorAtElevation(3.dp)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically, modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    IconButton(onClick = { navigator.popBackStack() }) {
                        Icon(
                            imageVector = ImageVector.vectorResource(R.drawable.back_icon),
                            contentDescription = "back",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier
                                .size(24.dp),
                        )
                    }
                    Image(
                        painter = painterResource(id = R.drawable.defaultphoto),
                        contentDescription = null,
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape)
                    )
                    Text(
                        text = currentChat.value?.others?.joinToString { it.fname } ?: "Chat",
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp, horizontal = 16.dp)
                    )
                }
            }
        },
        bottomBar = {
            Surface {
                Row(
                    modifier = Modifier
                        .border(top = Border(1.dp, MaterialTheme.colorScheme.secondaryContainer))
                        .fillMaxWidth()
                        .padding(vertical = 16.dp)
                        .padding(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Input message
                    TextField(
                        value = newMessage,
                        onValueChange = { newMessage = it },
                        placeholder = { Text(text = "Send message") },
                        modifier = Modifier
                            .weight(1f)
                            .padding(start = 8.dp),
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Text,
                            imeAction = ImeAction.Default
                        ),
                        shape = MaterialTheme.shapes.extraLarge,
                        colors = TextFieldDefaults.colors(
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent,
                            disabledIndicatorColor = Color.Transparent,
                            unfocusedContainerColor = MaterialTheme.colorScheme.secondaryContainer,
                            focusedContainerColor = MaterialTheme.colorScheme.secondaryContainer,
                            focusedTextColor = MaterialTheme.colorScheme.onSecondaryContainer,
                            unfocusedTextColor = MaterialTheme.colorScheme.onSecondaryContainer,
                        )
                    )

                    // Send button
                    IconButton(
                        onClick = {
                            if (newMessage.isNotBlank()) {
                                chatViewModel.sendMessage(newMessage)
                                newMessage = ""
                            }
                        },
                        Modifier
                            .padding(start = 16.dp, end = 8.dp)
                            .background(MaterialTheme.colorScheme.inverseOnSurface, shape = CircleShape)
                    ) {
                        Icon(
                            imageVector = ImageVector.vectorResource(R.drawable.send_icon),
                            contentDescription = "Send",
                            tint = MaterialTheme.colorScheme.primary,
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        // Display chat messages
        LazyColumn(
            modifier = Modifier
                .padding(innerPadding),
            state = lazyListState,
        ) {
            items(currentChat.value?.messages ?: emptyList()) { message ->
                MessageItem(message)
            }
        }
    }
}

@Composable
fun MessageItem(message: ResolvedMessageEntity) {
    //Align current user messages to the end

    val alignment = if (message.isSelf) {
        Alignment.End
    } else {
        Alignment.Start
    }

    // Use different background colors for user and sender messages
    val backgroundColor = if (message.isSelf) {
        MaterialTheme.colorScheme.primary
    } else {
        MaterialTheme.colorScheme.secondaryContainer
    }

    // Use different text colors for user and sender messages
    val contentColor = if (message.isSelf) {
        MaterialTheme.colorScheme.onPrimary
    } else {
        MaterialTheme.colorScheme.onSecondaryContainer
    }

    // Display each message in a box
    Box(
        modifier = Modifier
            .padding(horizontal = 12.dp, vertical = 8.dp)
            .fillMaxWidth()
            .wrapContentWidth(align = alignment)
            .clip(
                RoundedCornerShape(
                    topStart = 48f,
                    topEnd = 48f,
                    bottomStart = if (message.isSelf) 48f else 0f,
                    bottomEnd = if (message.isSelf) 0f else 48f
                )
            )
            .background(color = backgroundColor)
            .padding(8.dp)

    ) {
        Text(
            text = message.text,
            color = contentColor,
            modifier = Modifier.padding(8.dp)
        )
    }
}