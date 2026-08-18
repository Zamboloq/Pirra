package com.pirra.chat.feature.chat

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavType
import androidx.navigation.navArgument
import com.pirra.chat.feature.chat.ui.ChatScreen
import com.pirra.chat.feature.chat.ui.ChatViewEvent
import com.pirra.chat.feature.chat.ui.ChatViewModel
import com.pirra.chat.feature.chat.ui.LoginScreen
import com.pirra.chat.feature.chat.ui.RegisterScreen

@Composable
fun RootNavGraph(viewModel: ChatViewModel) {
    val navController = rememberNavController()
    val viewState by viewModel.viewState.collectAsStateWithLifecycle()
    LaunchedEffect(key1 = viewState.currentUser, key2 = viewState.isSessionChecked) {
        if (viewState.isSessionChecked) {
            if (viewState.currentUser != null) {
                navController.navigate(Screen.ChatList.route) {
                    popUpTo(0) { inclusive = true }
                }
            } else {
                navController.navigate(Screen.Login.route) {
                    popUpTo(0) { inclusive = true }
                }
            }
        }
    }

    if (!viewState.isSessionChecked) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
    } else {
        NavHost(
            navController = navController,
            startDestination = if (viewState.currentUser != null) Screen.ChatList.route else Screen.Login.route
        ) {
            composable(Screen.Login.route) {
                LoginScreen(
                    viewModel = viewModel,
                    onNavigateToRegister = { navController.navigate(Screen.Register.route) }
                )
            }

            composable(Screen.Register.route) {
                RegisterScreen(
                    viewModel = viewModel,
                    onNavigateToLogin = { navController.navigate(Screen.Login.route) }
                )
            }

            composable(Screen.ChatList.route) {
                MainAppHub(
                    rootNavController = navController,
                    viewModel = viewModel,
                    onLogout = { viewModel.onEvent(ChatViewEvent.TriggerSignOut) }
                )
            }

            composable(
                route = Screen.ChatRoom.route,
                arguments = listOf(
                    navArgument("chatId") { type = NavType.StringType },
                    navArgument("recipientName") { type = NavType.StringType }
                )
            ) { backStackEntry ->
                val chatId = backStackEntry.arguments?.getString("chatId") ?: ""
                val recipientName = backStackEntry.arguments?.getString("recipientName") ?: "User"
                val currentUserId = viewState.currentUser?.uid ?: ""

                ChatScreen(
                    chatId = chatId,
                    senderId = currentUserId,
                    recipientName = recipientName,
                    viewModel = viewModel,
                    onNavigateBack = { navController.popBackStack() }
                )
            }
        }
    }
}
