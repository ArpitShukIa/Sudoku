package com.arpit.sudoku.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun CorneredBox(
    showCorners: Boolean,
    modifier: Modifier = Modifier,
    borderColor: Color = Color.Black,
    contentBgColor: Color = Color.White,
    borderWidth: Dp = 1.dp,
    content: @Composable () -> Unit
) {
    if (!showCorners) {
        Box(modifier, contentAlignment = Alignment.Center) {
            content()
        }
    } else {
        Box(
            modifier
                .fillMaxSize()
                .padding(2.dp)
                .background(borderColor),
        ) {
            Box(
                modifier = Modifier
                    .height(borderWidth)
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp)
                    .background(contentBgColor)
                    .align(Alignment.TopCenter),
            )
            Box(
                modifier = Modifier
                    .height(borderWidth)
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp)
                    .background(contentBgColor)
                    .align(Alignment.BottomCenter),
            )
            Box(
                modifier = Modifier
                    .width(borderWidth)
                    .fillMaxHeight()
                    .padding(vertical = 8.dp)
                    .background(contentBgColor)
                    .align(Alignment.CenterStart),
            )
            Box(
                modifier = Modifier
                    .width(borderWidth)
                    .fillMaxHeight()
                    .padding(vertical = 8.dp)
                    .background(contentBgColor)
                    .align(Alignment.CenterEnd),
            )
            Box(
                modifier = Modifier
                    .padding(borderWidth)
                    .fillMaxSize()
                    .background(contentBgColor),
                contentAlignment = Alignment.Center
            ) {
                content()
            }
        }
    }
}

@Preview
@Composable
fun CorneredBoxPreview() {
    CorneredBox(
        showCorners = true,
        contentBgColor = Color.Cyan,
        modifier = Modifier
            .background(Color.Cyan)
            .size(40.dp)
    ) {
        Text(text = "5")
    }
}
