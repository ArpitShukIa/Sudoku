package com.arpit.sudoku.data

import javax.inject.Inject
import javax.inject.Singleton

const val EMPTY_CELL = 0

@Singleton
class SudokuHelper @Inject constructor() {

    fun checkIsSafe(sudoku: List<List<Int>>, x: Int, y: Int, num: Int): Boolean {
        if (num == EMPTY_CELL)
            return true
        for (i in 0..8) for (j in 0..8) {
            if (i == x && j == y) continue
            if (num == sudoku[i][j]) {
                if (i == x) return false
                if (j == y) return false
                if (i / 3 == x / 3 && j / 3 == y / 3) return false
            }
        }
        return true
    }

    private fun generateSudoku(): List<List<Int>> {
        val sudoku = List(9) { MutableList(9) { EMPTY_CELL } }
        val block1 = (1..9).shuffled().iterator()
        val block2 = (1..9).shuffled().iterator()
        val block3 = (1..9).shuffled().iterator()
        // fill diagonal blocks
        for (i in 0..2) for (j in 0..2) sudoku[i][j] = block1.next()
        for (i in 3..5) for (j in 3..5) sudoku[i][j] = block2.next()
        for (i in 6..8) for (j in 6..8) sudoku[i][j] = block3.next()

        fun backtrack(): Boolean {
            for (i in 0..8) for (j in 0..8) {
                if (sudoku[i][j] != EMPTY_CELL) continue
                for (k in 1..9) {
                    if (checkIsSafe(sudoku, i, j, k)) {
                        sudoku[i][j] = k
                        if (backtrack()) return true
                        sudoku[i][j] = EMPTY_CELL
                    }
                }
                return false
            }
            return true
        }
        require(backtrack()) { "No valid sudoku found" }
        return sudoku
    }

    fun generateSudokuWithEmptyCells(n: Int): List<List<Int>> {
        val sudoku = generateSudoku()
        require(n <= 81) { "n must be less than or equal to 81" }
        val indicesToRemove = (0..80).shuffled().take(n)
        val newSudoku = List(9) { MutableList(9) { EMPTY_CELL } }
        for (i in 0..8) for (j in 0..8)
            if (i * 9 + j !in indicesToRemove)
                newSudoku[i][j] = sudoku[i][j]
        return newSudoku
    }

    fun findFirstEmptyCell(sudoku: List<List<Int>>): Pair<Int, Int> {
        for (i in 0..80) {
            val (x, y) = i / 9 to i % 9
            if (sudoku[x][y] == EMPTY_CELL)
                return x to y
        }
        throw IllegalStateException()
    }
}