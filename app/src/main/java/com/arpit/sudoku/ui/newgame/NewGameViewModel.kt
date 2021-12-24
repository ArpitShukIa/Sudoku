package com.arpit.sudoku.ui.newgame

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.arpit.sudoku.data.Cell
import com.arpit.sudoku.data.CellBorder
import com.arpit.sudoku.data.CellState
import com.arpit.sudoku.data.SudokuHelper
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NewGameViewModel @Inject constructor(
    private val sudokuHelper: SudokuHelper
) : ViewModel() {

    var sudokuCells by mutableStateOf(listOf<List<Cell>>())
        private set

    private var loading = true

    private var currentCell = -1 to -1

    init {
        viewModelScope.launch {
            val sudoku = sudokuHelper.generateSudokuWithEmptyCells(40)
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
                        onClick = {
                            if (!loading) {
                                currentCell = i to j
                                refreshSudoku()
                            }
                        }
                    )
                }
            }
            animateEntry(sudoku)
            loading = false
            for (i in 0..80) {
                val (x, y) = i / 9 to i % 9
                if (sudoku[x][y] == 0) {
                    currentCell = x to y
                    break
                }
            }
            refreshSudoku()
        }
    }

    private suspend fun animateEntry(sudoku: List<List<Int>>) {
        delay(500)
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
                    val text = sudoku[i][j].let { if (it == 0 || i > index + 2) "" else "$it" }
                    val state =
                        if (animatedRows[i] != null) animatedRows[i]!!
                        else CellState.Default
                    cell.copy(text = text, state = state)
                }
            }
            delay(50)
        }
    }

    private fun refreshSudoku() {
        sudokuCells = sudokuCells.mapIndexed { i, row ->
            row.mapIndexed { j, cell ->
                val (x, y) = currentCell
                val currText = sudokuCells[x][y].text
                val cellState =
                    if (i == x && j == y)
                        if (currText.isEmpty())
                            CellState.SelectedEmpty
                        else
                            CellState.SelectedNonEmpty
                    else
                        if ((x / 3 == i / 3 && y / 3 == j / 3) || x == i || y == j)
                            CellState.InRegionOfSelected
                        else if (currText.isNotEmpty() && cell.text == currText)
                            CellState.SelectedNonEmpty
                        else
                            CellState.Default
                cell.copy(state = cellState)
            }
        }
    }

    fun onNumberButtonClick(n: Int) {
        if (currentCell == -1 to -1)
            return
        val (x, y) = currentCell
        if (sudokuCells[x][y].text.isNotEmpty()) return
        val rawSudoku = sudokuCells.map { row -> row.map { it.text.toIntOrNull() ?: 0 } }
        if (sudokuHelper.checkIsSafe(rawSudoku, x, y, n)) {
            sudokuCells = sudokuCells.mapIndexed { i, row ->
                row.mapIndexed { j, cell ->
                    if (i to j != currentCell) cell
                    else cell.copy(text = "$n")
                }
            }
            refreshSudoku()
        }
    }
}