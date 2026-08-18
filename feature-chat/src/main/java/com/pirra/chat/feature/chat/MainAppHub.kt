package com.pirra.chat.feature.chat

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.pirra.chat.feature.chat.ui.ChatListScreen
import com.pirra.chat.feature.chat.ui.ChatViewModel
import com.pirra.chat.feature.chat.ui.ContactsScreen
import com.pirra.chat.feature.chat.ui.SearchUserScreen

@Composable
fun MainAppHub(
    rootNavController: NavHostController,
    viewModel: ChatViewModel,
    onLogout: () -> Unit
) {
    val bottomTabNavController = rememberNavController()
    val navBackStackEntry by bottomTabNavController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    Scaffold(
        bottomBar = {
            NavigationBar {
                NavigationBarItem(
                    icon = { Icon(Icons.Default.Email, contentDescription = "Chats") },
                    label = { Text("Chats") },
                    selected = currentRoute == Screen.ChatList.route,
                    onClick = {
                        if (currentRoute != Screen.ChatList.route) {
                            bottomTabNavController.navigate(Screen.ChatList.route) {
                                popUpTo(bottomTabNavController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    }
                )

                NavigationBarItem(
                    icon = { Icon(Icons.Default.Person, contentDescription = "Contacts") },
                    label = { Text("Contacts") },
                    selected = currentRoute == Screen.Contacts.route || currentRoute == Screen.SearchUser.route,
                    onClick = {
                        if (currentRoute != Screen.Contacts.route) {
                            bottomTabNavController.navigate(Screen.Contacts.route) {
                                popUpTo(bottomTabNavController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    }
                )

                NavigationBarItem(
                    icon = { Icon(Icons.Default.AccountCircle, contentDescription = "Settings") },
                    label = { Text("Settings") },
                    selected = currentRoute == Screen.Settings.route,
                    onClick = {
                        if (currentRoute != Screen.Settings.route) {
                            bottomTabNavController.navigate(Screen.Settings.route) {
                                popUpTo(bottomTabNavController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    }
                )
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = bottomTabNavController,
            startDestination = Screen.ChatList.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Screen.ChatList.route) {
                ChatListScreen(
                    viewModel = viewModel,
                    onChatSelected = { chatId, recipientName ->
                        rootNavController.navigate(
                            Screen.ChatRoom.createRoute(
                                chatId,
                                recipientName
                            )
                        )
                    }
                )
            }

            composable(Screen.Contacts.route) {
                ContactsScreen(
                    viewModel = viewModel,
                    onNavigateToSearch = { bottomTabNavController.navigate(Screen.SearchUser.route) },
                    onContactSelected = { contactUid, contactName ->
                        val currentUserId = viewModel.viewState.value.currentUser?.uid ?: ""
                        val combinedRoomId =
                            if (currentUserId < contactUid) "${currentUserId}_$contactUid" else "${contactUid}_$currentUserId"
                        rootNavController.navigate(
                            Screen.ChatRoom.createRoute(
                                combinedRoomId,
                                contactName
                            )
                        )
                    }
                )
            }

            composable(Screen.SearchUser.route) {
                SearchUserScreen(
                    viewModel = viewModel,
                    onNavigateBack = { bottomTabNavController.popBackStack() }
                )
            }

            composable(Screen.Settings.route) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Button(onClick = onLogout, modifier = Modifier.padding(16.dp)) {
                        Text("Log Out Session")
                    }
                }
            }
        }
    }
}
