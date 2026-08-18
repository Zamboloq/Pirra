package com.pirra.chat.feature.chat

sealed class Screen(val route: String) {
    object Login : Screen("login")
    object Register : Screen("register")
    object ChatList : Screen("chat_list")
    object Contacts : Screen("contacts")
    object SearchUser : Screen("search_user")
    object Settings : Screen("settings")
    object ChatRoom : Screen("chat_room/{chatId}/{recipientName}") {
        fun createRoute(chatId: String, recipientName: String) = "chat_room/$chatId/$recipientName"
    }
}
