package com.arpit.sudoku.ui.home

import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.arpit.sudoku.data.Difficulty

@Composable
fun HomeScreen(navigateToNewGame: (Difficulty) -> Unit) {
    Column {
        Text(
            text = "Sudoku",
            fontSize = 24.sp,
            modifier = Modifier.padding(vertical = 8.dp, horizontal = 16.dp)
        )
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "START A NEW GAME",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(top = 48.dp, bottom = 24.dp)
            )
            Difficulty.values().forEach { difficulty ->
                Button(
                    onClick = { navigateToNewGame(difficulty) },
                    modifier = Modifier
                        .width(150.dp)
                        .padding(top = 16.dp)
                ) {
                    Text(text = difficulty.name)
                }
            }
        }
    }
}