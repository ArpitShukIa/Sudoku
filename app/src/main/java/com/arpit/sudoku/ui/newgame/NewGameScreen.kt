package com.arpit.sudoku.ui.newgame

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.arpit.sudoku.components.BorderValues
import com.arpit.sudoku.components.BorderedBox
import com.arpit.sudoku.data.Cell
import com.arpit.sudoku.vibrate
import kotlinx.coroutines.launch

@Composable
fun NewGameScreen(viewModel: NewGameViewModel) {
    val sudokuCells = viewModel.sudokuCells
    Column {
        IconButton(onClick = { /*TODO*/ }, modifier = Modifier.padding(8.dp)) {
            Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "back")
        }
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.SpaceEvenly
        ) {
            SudokuBoard(sudokuCells)
            NumberButtons(viewModel::onNumberButtonClick)
            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
private fun SudokuBoard(sudokuCells: List<List<Cell>>, modifier: Modifier = Modifier) {
    val cellSize = (LocalConfiguration.current.screenWidthDp.dp - 32.dp) / 9
    Column(modifier = modifier.padding(16.dp)) {
        repeat(9) { i ->
            Row {
                repeat(9) { j ->
                    val cell = sudokuCells[i][j]
                    BorderedBox(
                        size = cellSize,
                        borderValues = BorderValues(
                            cell.border.top,
                            cell.border.bottom,
                            cell.border.start,
                            cell.border.end,
                        ),
                        contentBgColor = cell.bgColor
                    ) {
                        SudokuCell(cell = cell, modifier = Modifier.fillMaxSize())
                    }
                }
            }
        }
    }
}

@Composable
fun SudokuCell(cell: Cell, modifier: Modifier = Modifier) {
    Box(
        modifier
            .clickable(
                onClick = cell.onClick,
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = cell.text,
            color = cell.textColor,
            fontSize = 20.sp,
        )
    }
}

@Composable
fun NumberButtons(onButtonClick: (Int) -> Unit, modifier: Modifier = Modifier) {
    Row(modifier = modifier.padding(8.dp)) {
        for (i in 1..9) {
            NumberButton(
                number = "$i",
                onClick = { onButtonClick(i) },
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
fun NumberButton(number: String, onClick: () -> Unit, modifier: Modifier = Modifier) {

    val scale = remember { Animatable(1f) }
    val scope = rememberCoroutineScope()
    var animate by remember { mutableStateOf(false) }
    val context = LocalContext.current

    LaunchedEffect(animate) {
        if (animate) {
            scope.launch {
                animate = false
                scale.snapTo(1f)
                scale.animateTo(1.25f, animationSpec = tween(100))
                scale.animateTo(1f, animationSpec = tween(100))
            }
        }
    }

    Card(
        shape = RoundedCornerShape(12.dp),
        elevation = 4.dp,
        modifier = modifier
            .padding(horizontal = 4.dp)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) {
                vibrate(context = context, duration = 25)
                animate = true
                onClick()
            }
            .graphicsLayer {
                scaleX = scale.value
                scaleY = scale.value
            }
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.clip(RoundedCornerShape(12.dp)),
        ) {
            Text(
                text = number,
                fontSize = 28.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(vertical = 4.dp)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun NewGameScreenPreview() {
    val cell = Cell(
        text = "5",
        textState = CellTextState.Prefilled,
        border = CellBorder(1.dp, 1.dp, 1.dp, 1.dp),
        state = CellState.Default,
        preFilled = true,
        onClick = {}
    )
    val sudokuCells = List(9) { List(9) { cell } }
    NewGameScreen(sudokuCells = sudokuCells, onNumberButtonClick = {}, onBackPressed = {})
}