package com.arpit.sudoku.data

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import com.arpit.sudoku.ui.theme.*

data class Cell(
    val text: String,
    val textState: CellTextState,
    val border: CellBorder,
    val state: CellState,
    val preFilled: Boolean,
    val onClick: () -> Unit
) {
    val bgColor: Color
        @Composable get() = when (state) {
            CellState.AnimationLight -> AnimationColorLight
            CellState.AnimationMedium -> AnimationColorMedium
            CellState.AnimationDark -> AnimationColorDark
            CellState.SelectedEmpty -> EmptyCellColor
            CellState.SelectedNonEmpty -> NonEmptyCellColor
            CellState.InRegionOfSelected -> CellInRegionColor
            CellState.Default -> DefaultCellColor
            CellState.InvalidInput -> CellColorInvalid
        }

    val textColor: Color
        @Composable get() = when (textState) {
            CellTextState.Default -> DefaultTextColor
            CellTextState.Invalid -> TextColorInvalid
            CellTextState.Prefilled -> Color.Black
        }
}

data class CellBorder(
    val top: Dp,
    val bottom: Dp,
    val start: Dp,
    val end: Dp
)

enum class CellState {
    AnimationLight, AnimationMedium, AnimationDark,
    SelectedEmpty, SelectedNonEmpty, InRegionOfSelected, Default, InvalidInput
}

enum class CellTextState {
    Prefilled, Invalid, Default
}