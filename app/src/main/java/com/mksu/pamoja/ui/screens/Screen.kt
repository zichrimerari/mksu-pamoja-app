package com.mksu.pamoja.ui.screens

sealed class Screen(val route: String) {
    object Login : Screen("login")
    object Home : Screen("home")
    object Counseling : Screen("counseling")
    object Resources : Screen("resources")
    object Forum : Screen("forum")
    object Profile : Screen("profile")
    
    // Add more screens as needed
}
