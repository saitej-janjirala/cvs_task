package com.saitejajanjirala.cvs_task.ui.util

sealed class Screen(val route : String) {
    object HomeScreen : Screen("home_screen")
    object DetailScreen : Screen("detail_screen")
}