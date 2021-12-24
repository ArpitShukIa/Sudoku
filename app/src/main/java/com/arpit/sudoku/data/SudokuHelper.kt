package com.arpit.sudoku.data

import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SudokuHelper @Inject constructor() {

    fun checkIsSafe(sudoku: List<List<Int>>, x: Int, y: Int, num: Int): Boolean {
        for (i in 0..8) for (j in 0..8) {
            if (num == sudoku[i][j]) {
                if (i == x) return false
                if (j == y) return false
                if (i / 3 == x / 3 && j / 3 == y / 3) return false
            }
        }
        return true
    }

    private fun generateSudoku(): List<List<Int>> {
        val sudoku = List(9) { MutableList(9) { 0 } }
        val block1 = (1..9).shuffled().iterator()
        val block2 = (1..9).shuffled().iterator()
        val block3 = (1..9).shuffled().iterator()
        // fill diagonal blocks
        for (i in 0..2) for (j in 0..2) sudoku[i][j] = block1.next()
        for (i in 3..5) for (j in 3..5) sudoku[i][j] = block2.next()
        for (i in 6..8) for (j in 6..8) sudoku[i][j] = block3.next()

        fun backtrack(): Boolean {
            for (i in 0..8) for (j in 0..8) {
                if (sudoku[i][j] != 0) continue
                for (k in 1..9) {
                    if (checkIsSafe(sudoku, i, j, k)) {
                        sudoku[i][j] = k
                        if (backtrack()) return true
                        sudoku[i][j] = 0
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
        val newSudoku = List(9) { MutableList(9) { 0 } }
        for (i in 0..8) for (j in 0..8)
            if (i * 9 + j !in indicesToRemove)
                newSudoku[i][j] = sudoku[i][j]
        return newSudoku
    }

}