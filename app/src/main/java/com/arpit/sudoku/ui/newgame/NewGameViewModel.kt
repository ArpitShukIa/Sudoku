package com.arpit.sudoku.ui.newgame

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.unit.dp
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.arpit.sudoku.data.*
import com.arpit.sudoku.ui.Destinations
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.math.abs

@HiltViewModel
class NewGameViewModel @Inject constructor(
    private val sudokuHelper: SudokuHelper,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val newGameEventsChannel = Channel<NewGameEvents>()
    val newGameEvents = newGameEventsChannel.receiveAsFlow()

    private val difficulty = savedStateHandle.get<String>(Destinations.DIFFICULTY)!!

    var sudokuCells by mutableStateOf(listOf<List<Cell>>())
        private set

    private var currentCell = -1 to -1
    private var gameCompleted = false

    init {
        viewModelScope.launch {
            val emptyCells = when (Difficulty.valueOf(difficulty)) {
                Difficulty.EASY -> 20
                Difficulty.MEDIUM -> 35
                Difficulty.HARD -> 50
            }
            val sudoku = sudokuHelper.generateSudokuWithEmptyCells(emptyCells)
            sudokuCells = List(9) { i ->
                List(9) { j ->
                    val border = CellBorder(
                        top = if (i % 3 == 0) 2.dp else 0.5.dp,
                        bottom = if (i == 8) 2.dp else 0.dp,
                        start = if (j % 3 == 0) 2.dp else 0.5.dp,
                        end = if (j == 8) 2.dp else 0.dp
                    )
                    Cell(
                        text = "",
                        border = border,
                        state = CellState.Default,
                        textState = CellTextState.Prefilled,
                        preFilled = sudoku[i][j] != EMPTY_CELL,
                        onClick = {
                            if (currentCell != -1 to -1) {
                                currentCell = i to j
                                refreshSudoku()
                            }
                        }
                    )
                }
            }
            animateEntry(sudoku)
            currentCell = sudokuHelper.findFirstEmptyCell(sudoku)
            refreshSudoku()
        }
    }

    private suspend fun animateEntry(sudoku: List<List<Int>>) {
        delay(100)
        for (index in -2..9) {
            val cellStates = listOf(
                CellState.AnimationLight,
                CellState.AnimationMedium,
                CellState.AnimationDark,
                CellState.AnimationMedium,
                CellState.AnimationLight
            ).iterator()
            val animatedRows = List(5) { index + it }.associateWith { cellStates.next() }
            sudokuCells = sudokuCells.mapIndexed { i, row ->
                row.mapIndexed { j, cell ->
                    val text =
                        sudoku[i][j].let { if (it == EMPTY_CELL || i > index + 2) "" else "$it" }
                    val state = animatedRows[i] ?: CellState.Default
                    cell.copy(text = text, state = state)
                }
            }
            delay(50)
        }
    }

    private fun refreshSudoku() {
        val (x, y) = currentCell
        val inRegionOfSelected: (Int, Int) -> Boolean = { i, j ->
            i == x || j == y || i / 3 == x / 3 && j / 3 == y / 3
        }
        val rawSudoku = sudokuCells.map { row -> row.map { it.text.toIntOrNull() ?: EMPTY_CELL } }
        sudokuCells = sudokuCells.mapIndexed { i, row ->
            row.mapIndexed { j, cell ->
                val num = cell.text.toIntOrNull() ?: EMPTY_CELL
                val invalid = !sudokuHelper.checkIsSafe(rawSudoku, i, j, num)
                val textState = when {
                    cell.preFilled -> CellTextState.Prefilled
                    invalid -> CellTextState.Invalid
                    else -> CellTextState.Default
                }
                val cellState = when {
                    i == x && j == y && (num == EMPTY_CELL || (invalid && !cell.preFilled)) -> CellState.SelectedEmpty
                    (i != x || j != y) && invalid && inRegionOfSelected(i, j) &&
                            cell.text == sudokuCells[x][y].text -> CellState.InvalidInput
                    cell.text == sudokuCells[x][y].text && num != EMPTY_CELL -> CellState.SelectedNonEmpty
                    inRegionOfSelected(i, j) -> CellState.InRegionOfSelected
                    else -> CellState.Default
                }
                cell.copy(state = cellState, textState = textState)
            }
        }
        if (!gameCompleted)
            checkCompletion()
    }

    fun onNumberButtonClick(n: Int) {
        if (gameCompleted || currentCell == -1 to -1)
            return
        val (x, y) = currentCell
        if (sudokuCells[x][y].preFilled) return
        sudokuCells = sudokuCells.mapIndexed { i, row ->
            row.mapIndexed { j, cell ->
                if (i to j != currentCell) cell
                else cell.copy(text = if (cell.text == "$n") "" else "$n")
            }
        }
        refreshSudoku()
    }

    private fun checkCompletion() {
        val rawSudoku = sudokuCells.map { row -> row.map { it.text.toIntOrNull() ?: EMPTY_CELL } }
        for (i in 0..8) for (j in 0..8) {
            if (rawSudoku[i][j] == EMPTY_CELL ||
                !sudokuHelper.checkIsSafe(rawSudoku, i, j, rawSudoku[i][j])
            )
                return
        }
        viewModelScope.launch {
            celebrateVictory()
        }
    }

    private suspend fun celebrateVictory() {
        gameCompleted = true
        for (index in -4..9) {
            val cellStates = listOf(
                CellState.AnimationLight,
                CellState.AnimationMedium,
                CellState.AnimationMedium,
                CellState.AnimationLight,
            ).iterator()
            val animatedRows = List(4) { index + it }.associateWith { cellStates.next() }
            sudokuCells = sudokuCells.mapIndexed { i, row ->
                row.mapIndexed { j, cell ->
                    val dist = maxOf(abs(i - currentCell.first), abs(j - currentCell.second))
                    val state = animatedRows[dist] ?: CellState.Default
                    cell.copy(state = state)
                }
            }
            delay(100)
        }
        newGameEventsChannel.send(NewGameEvents.SignalGameCompleted)
    }

    sealed class NewGameEvents {
        object SignalGameCompleted : NewGameEvents()
    }
}