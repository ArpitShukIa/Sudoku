package com.arpit.sudoku.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp

@Immutable
class BorderValues(val top: Dp, val bottom: Dp, val start: Dp, val end: Dp)

@Composable
fun BorderedBox(
    size: Dp,
    borderValues: BorderValues,
    modifier: Modifier = Modifier,
    borderColor: Color = Color.Black,
    contentBgColor: Color = MaterialTheme.colors.background,
    content: @Composable () -> Unit
) {
    Box(
        modifier = modifier
            .size(size)
            .background(borderColor)
    ) {
        Box(
            modifier = Modifier
                .padding(
                    top = borderValues.top,
                    bottom = borderValues.bottom,
                    start = borderValues.start,
                    end = borderValues.end
                )
                .fillMaxSize()
                .background(contentBgColor),
            contentAlignment = Alignment.Center
        ) {
            content()
        }
    }
}
