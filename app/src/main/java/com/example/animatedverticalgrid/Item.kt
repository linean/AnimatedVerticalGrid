package com.example.animatedverticalgrid

import androidx.compose.ui.graphics.Color

data class Item(
    val id: Int,
    val color: Color
)

private val colors = listOf(
    Color(0xFFF44336),
    Color(0xFFE91E63),
    Color(0xFF9C27B0),
    Color(0xFF673AB7),
    Color(0xFF3F51B5),
    Color(0xFF2196F3),
    Color(0xFF03A9F4),
    Color(0xFF00BCD4),
    Color(0xFF009688),
    Color(0xFF4CAF50),
    Color(0xFF8BC34A),
    Color(0xFFCDDC39),
    Color(0xFFFFEB3B),
    Color(0xFFFFC107),
    Color(0xFFFF9800),
    Color(0xFFFF5722),
)

fun createItems(count: Int): List<Item> {
    return (1..count).map { Item(it, colors[it % colors.size]) }
}
