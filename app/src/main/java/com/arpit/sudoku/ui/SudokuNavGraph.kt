package com.arpit.sudoku.ui

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.arpit.sudoku.ui.Destinations.NEW_GAME_ROUTE
import com.arpit.sudoku.ui.newgame.NewGameScreen

object Destinations {
    const val NEW_GAME_ROUTE = "new_game"
}

@Composable
fun SudokuNavGraph() {
    val navController = rememberNavController()
    NavHost(
        navController = navController,
        startDestination = NEW_GAME_ROUTE
    ) {
        composable(NEW_GAME_ROUTE) {
            NewGameScreen(viewModel = hiltViewModel())
        }
    }
}