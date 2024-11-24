package com.example.myapplication

import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview

@Preview(showSystemUi = true)
@Composable
fun MyList(){
    val data = List(100000) { "Text $it "}
    LazyVerticalGrid(columns = GridCells.Fixed(3)) {
        items(data) {
            Text(text = it,
                textAlign = TextAlign.Center,
                )
        }
    }
}