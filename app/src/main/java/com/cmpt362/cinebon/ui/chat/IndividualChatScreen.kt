package com.cmpt362.cinebon.ui.chat

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.cmpt362.cinebon.R
import com.cmpt362.cinebon.data.chats.Message
import com.cmpt362.cinebon.ui.theme.CinebonTheme

@Composable
fun ChatScreen(messages:List<Message>){
    val scrollState = rememberScrollState()
    var newMessage by remember { mutableStateOf("") } //remember the value of the message written
    Scaffold(
        topBar = {
            Surface(color = MaterialTheme.colorScheme.surfaceColorAtElevation(3.dp)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically, modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    IconButton(onClick = { /*TODO*/ }) {
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
                            .size(46.dp)
                            .clip(CircleShape)
                    )
                    Text(
                        "Friend Name",
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp)
                    )
                }
            }
        }
    ){innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(bottom = 8.dp)
        ){
            // Display chat messages
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(8.dp)
                    .verticalScroll(state = scrollState)
            ) {
                messages.forEach { message ->
                    MessageItem(message)
                }
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Input message
                OutlinedTextField(
                    value = newMessage,
                    onValueChange = { newMessage = it },
                    label = { Text("Aa") },
                    modifier = Modifier
                        .weight(1f),
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Text,
                        imeAction = ImeAction.Send
                    ),
                    keyboardActions = KeyboardActions(
                        onSend = {
                            if (newMessage.isNotBlank()) {
                                //onSendMessage(newMessage)
                                newMessage = ""
                            }
                        }
                    )
                )

                // Send button
                IconButton(
                    onClick = {
                        if (newMessage.isNotBlank()) {
                            //onSendMessage(newMessage)
                            newMessage = ""
                        }
                    }
                ) {
                    Icon(
                        imageVector = ImageVector.vectorResource(R.drawable.send_icon),
                        contentDescription = "Send",
                        tint = MaterialTheme.colorScheme.primary,)
                }
            }
        }
    }


}
@Composable
fun MessageItem(message: Message) {
    //Align current user messages to the end
    val alignment = if (message.sentByCurrentUser) {
        Alignment.End
    } else {
        Alignment.Start
    }

    // Use different background colors for user and sender messages
    val backgroundColor = if (message.sentByCurrentUser) {
        MaterialTheme.colorScheme.primary
    } else {
        MaterialTheme.colorScheme.inversePrimary
    }

    // Use different text colors for user and sender messages
    val contentColor = if (message.sentByCurrentUser) {
        MaterialTheme.colorScheme.onPrimary
    } else {
        Color.Black
    }

    // Display each message in a box
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentWidth(align=alignment)
            .padding(8.dp)
            .background(color=backgroundColor, shape = MaterialTheme.shapes.medium)
    ) {
        Text(
            text = message.content,
            color = contentColor,
            modifier = Modifier.padding(8.dp)
        )

    }
}


@Preview
@Composable
fun ChatPreview(){
    val messages = listOf(
        Message(id = "1", content = "This class ain't it", sentByCurrentUser = true),
        Message(id = "2", content = "Facts", sentByCurrentUser = false),
        Message(id = "1", content = "I wish we had more time :(", sentByCurrentUser = true),
    )
    CinebonTheme {
        ChatScreen(messages)
    }
}
