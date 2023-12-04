package com.cmpt362.cinebon.ui.dashboard

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.ramcosta.composedestinations.annotation.Destination

@DashboardNavGraph
@Destination
@Composable
fun ListsScreen() {
    Text("Lists screen")
}

@Composable
fun UserLists(usernames: List<String>, onItemClick: (String) -> Unit) {
    LazyColumn {
        items(usernames) { username ->
            ListItem(username = username, onItemClick = onItemClick)
        }
    }
}

@Composable
fun ListItem(username: String, onItemClick: (String) -> Unit) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onItemClick(username) }
            .padding(16.dp),
        color = MaterialTheme.colorScheme.background
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "$username's Movies List",
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Preview
@Composable
fun ListsPreview() {
    val dummyUsernames = listOf("Dardik", "Quantumcry", "Supritee", "Shabz")
    UserLists(usernames = dummyUsernames, onItemClick = { })
}