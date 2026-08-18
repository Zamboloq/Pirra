package com.pirra.chat.feature.chat.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchUserScreen(
    viewModel: ChatViewModel,
    onNavigateBack: () -> Unit
) {
    val viewState by viewModel.viewState.collectAsStateWithLifecycle()
    var emailQuery by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        viewModel.onEvent(ChatViewEvent.ClearSearchState)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Find New Contact") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            OutlinedTextField(
                value = emailQuery,
                onValueChange = { emailQuery = it },
                label = { Text("Search by Exact Email Address") },
                modifier = Modifier.fillMaxWidth(),
                trailingIcon = {
                    IconButton(onClick = {
                        if (emailQuery.isNotBlank()) {
                            viewModel.onEvent(ChatViewEvent.SearchUserByEmail(emailQuery.trim()))
                        }
                    }) {
                        Icon(Icons.Default.Search, contentDescription = "Execute Search")
                    }
                },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                singleLine = true
            )

            if (viewState.isLoading) {
                Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }

            viewState.searchError?.let { errorText ->
                Text(text = errorText, color = MaterialTheme.colorScheme.error, fontSize = 14.sp)
            }

            viewState.searchResult?.let { foundUser ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(text = foundUser.displayName, fontSize = 18.sp, style = MaterialTheme.typography.bodyLarge)
                            Text(text = foundUser.email, fontSize = 14.sp, color = Color.Gray)
                        }
                        Button(
                            onClick = {
                                viewModel.onEvent(ChatViewEvent.AddUserToContacts(foundUser.uid))
                                onNavigateBack()
                            }
                        ) {
                            Text("Add")
                        }
                    }
                }
            }
        }
    }
}
