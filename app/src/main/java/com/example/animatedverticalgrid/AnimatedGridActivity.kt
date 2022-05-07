package com.example.animatedverticalgrid

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Slider
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import kotlin.math.roundToInt

private const val MIN_SIZE = 2
private const val MAX_SIZE = 6

class AnimatedGridActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { AnimatedGridDemo() }
    }
}

@Composable
private fun AnimatedGridDemo() = MaterialTheme {
    var columns by rememberSaveable { mutableStateOf((MIN_SIZE + MAX_SIZE) / 2) }
    var rows by rememberSaveable { mutableStateOf((MIN_SIZE + MAX_SIZE) / 2) }
    var items by remember(columns, rows) { mutableStateOf(createItems(columns * rows)) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {

        AnimatedVerticalGrid(
            items = items,
            itemKey = Item::id,
            columns = columns,
            rows = rows,
            modifier = Modifier
                .fillMaxWidth()
                .weight(1F)
        ) {
            Item(it)
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {

            Button(
                onClick = { items = items.shuffled() },
                modifier = Modifier
                    .padding(vertical = 32.dp)
                    .align(Alignment.CenterHorizontally),
            ) {
                Text(text = "Shuffle")
            }

            Slider(
                label = "Columns",
                value = columns,
                onChanged = { columns = it }
            )
            Slider(
                label = "Rows",
                value = rows,
                onChanged = { rows = it }
            )
        }
    }
}


@Composable
private fun Slider(
    label: String,
    value: Int,
    onChanged: (Int) -> Unit
) {
    var sliderValue by remember { mutableStateOf(value.toFloat()) }

    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(text = "$label: $value")

        Slider(
            value = sliderValue,
            valueRange = MIN_SIZE.toFloat()..MAX_SIZE.toFloat(),
            steps = MAX_SIZE - MIN_SIZE - 1,
            onValueChange = {
                sliderValue = it
                onChanged(it.roundToInt())
            },
        )
    }
}

@Composable
private fun Item(item: Item) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .fillMaxSize()
            .padding(2.dp)
            .background(item.color)
    ) {
        Text(text = item.id.toString())
    }
}
