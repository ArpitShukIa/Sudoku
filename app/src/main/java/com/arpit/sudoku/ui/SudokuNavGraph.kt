package com.arpit.sudoku.ui

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.arpit.sudoku.ui.Destinations.DIFFICULTY
import com.arpit.sudoku.ui.Destinations.HOME_ROUTE
import com.arpit.sudoku.ui.Destinations.NEW_GAME_ROUTE
import com.arpit.sudoku.ui.home.HomeScreen
import com.arpit.sudoku.ui.newgame.NewGameScreen

object Destinations {
    const val HOME_ROUTE = "home"
    const val DIFFICULTY = "difficulty"
    const val NEW_GAME_ROUTE = "new_game/{$DIFFICULTY}"
}

@Composable
fun SudokuNavGraph() {
    val navController = rememberNavController()
    NavHost(
        navController = navController,
        startDestination = HOME_ROUTE
    ) {
        composable(HOME_ROUTE) {
            HomeScreen { difficulty ->
                val routeArg = "new_game/${difficulty.name}"
                navController.navigate(routeArg)
            }
        }
        composable(
            route = NEW_GAME_ROUTE,
            arguments = listOf(
                navArgument(DIFFICULTY) {
                    type = NavType.StringType
                }
            )
        ) {
            NewGameScreen(viewModel = hiltViewModel()) {
                navController.navigateUp()
            }
        }
    }
}