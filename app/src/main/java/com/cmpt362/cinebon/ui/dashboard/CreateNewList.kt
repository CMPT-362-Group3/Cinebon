package com.cmpt362.cinebon.ui.dashboard

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SearchBar
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.ramcosta.composedestinations.annotation.Destination

@DashboardNavGraph
@Destination
@Composable
@Preview
fun NewListScreen() {

    var listName by rememberSaveable { mutableStateOf("") }
    var query by rememberSaveable { mutableStateOf("") }
    var active by rememberSaveable { mutableStateOf(false) }

    Surface(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {

            // TODO: Add a search bar here and then figure out the addition logic.

            OutlinedTextField(
                value = listName,
                onValueChange = {
                    listName = it
                },
                label = {
                    Text("Title")
                },
                placeholder = { Text("Marvel series?") },
                modifier = Modifier.padding(top = 8.dp)
            )

            Text(text = "Movies:", modifier = Modifier
                .padding(top = 32.dp)
                .padding(16.dp)
                .align(Alignment.Start))

            if (listName.isEmpty()) {
                Text(
                    text = "Whoops!\n",
                    style = MaterialTheme.typography.displayLarge,
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    modifier = Modifier
                        .padding(48.dp)
                        .align(Alignment.CenterHorizontally)
                )

                Text(
                    text = "Seems empty in here... You can search and add more movies!",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    modifier = Modifier
                        .padding(horizontal = 96.dp, vertical = 16.dp)
                        .align(Alignment.CenterHorizontally)
                )
            }
        }
    }
}