# Animated Grid in Jetpack Compose

## :scroll: Description

This repository contains an example app and an article describing my adventure with animating a grid of elements in [Jetpack Compose](https://developer.android.com/jetpack/compose).

#### Introducing [AnimatedVerticalGrid](https://github.com/linean/AnimatedVerticalGrid/blob/main/app/src/main/java/com/example/animatedverticalgrid/AnimatedVerticalGrid.kt).

<img src="https://github.com/linean/AnimatedVerticalGrid/blob/main/assets/demo.gif?raw=true" height=480/>

## :bulb: Motivation and Context

Recently, in one of the apps I'm working on I came across a case where I needed to animate position changes in a grid of elements. My first idea was to try [animateItemPlacement](https://developer.android.com/reference/kotlin/androidx/compose/foundation/lazy/LazyItemScope#(androidx.compose.ui.Modifier).animateItemPlacement(androidx.compose.animation.core.FiniteAnimationSpec)) from [LazyItemScope](https://developer.android.com/reference/kotlin/androidx/compose/foundation/lazy/LazyItemScope) but unfortunately it's not available for LazyVerticalGrid in Compose 1.1.0. The only way to achieve it was to write something custom.

## :man_student: Thinking process

First, let's determine what data we need to display in the grid. For sure the number of columns, rows, and a list of items. We also need to be able to identify items so we can use a dedicated function - very similar to how LazyRow/Column works. With that we can create the following signature:

```
@Composable
fun <ITEM, KEY> AnimatedVerticalGrid(
    items: List<ITEM>,
    itemKey: (ITEM) -> KEY,
    columns: Int,
    rows: Int
) 
```

To implement an animated grid we can use simple [Box](https://developer.android.com/reference/kotlin/androidx/compose/foundation/layout/package-summary#Box(androidx.compose.ui.Modifier,androidx.compose.ui.Alignment,kotlin.Boolean,kotlin.Function1)) and calculate offsets manually. It will later allow us to easily animate specific offsets. Knowing how many columns and rows we want to display, we can calculate the desired item size based on the available space. The best component for this job is BoxWithConstrains. It gives us available `maxWidth` and `maxHeight` thanks to which we can calculate the maximum item size:

```
val itemSize = remember(columns, rows) {
    val itemWidth = (maxWidth) / rows
    val itemHeight = (maxHeight) / columns
    DpSize(itemWidth, itemHeight)
}
```

The next step is to calculate the required offset for every cell. We iterate over all columns and rows and create a flat list of `DpOffsets`.

```
val gridOffsets = remember(columns, rows, itemSize) {
    (0 until columns).map { column ->
        (0 until rows).map { row ->
            DpOffset(
                x = itemSize.width * row,
                y = itemSize.height * column,
            )
        }
    }.flatten()
}
 ```
 
To be able to animate `DpOffset` we can wrap it inside [Animatable](https://developer.android.com/jetpack/compose/animation#animatable). Let's introduce the type alias and custom factory to make it simpler.
 
 ```
typealias ItemOffset = Animatable<DpOffset, AnimationVector2D>
fun ItemOffset(offset: DpOffset): ItemOffset = Animatable(offset, DpOffset.VectorConverter)
```


Now is the time for the most difficult part. We need to somehow assign offsets to the items. When the grid is displayed for the first time it's easy, we just take the first offset for the first item, the second offset for the second item, and so on. The tricky part is to remember offsets in a way that we can reuse them. My idea for that is to associate offsets with item keys and recreate them every time keys change. During recreation, we can check if we already know the offset for a given key or not. If we do, we can reuse it, otherwise we have to create a new offset based on the index.

 ```
var itemsOffsets by remember { mutableStateOf(mapOf<KEY, ItemOffset>()) }
key(itemKeys) {
    itemsOffsets = items.mapIndexed { index, item ->
        val key = itemKey(item)
        key to when {
            itemsOffsets.containsKey(key) -> itemsOffsets.getValue(key)
            else -> ItemOffset(gridOffsets[index])
        }
    }.toMap()
}
```

Once we assigne offsets for all items, we can finally draw them on the screen. Nothing complicated here, we just iterate over and wrap each item inside Box with calculated size and offset.

```
items.forEach { item ->
    val offset = itemsOffsets.getValue(itemKey(item)).value
    Box(
        modifier = Modifier
            .size(itemSize)
            .offset(offset.x, offset.y)
    ) {
        itemContent(item)
    }
}
 ```
 
Now you may think: *wait but how does it animate? If we reuse item offsets together with creating new ones, we may end up with two items in the same position.* And you are right!

The last missing part in our puzzle is to animate all item offsets to the new positions using LaunchedEffect:

 ```
LaunchedEffect(itemKeys) {
    items.forEachIndexed { index, item ->
        val newOffset = gridOffsets[index]
        val itemOffset = itemsOffsets.getValue(itemKey(item))
        launch { itemOffset.animateTo(newOffset, animationSpec) }
    }
}
```

And that's it! Our complete solution takes only 50 lines of code :heart: You can find it [here](https://github.com/linean/AnimatedVerticalGrid/blob/main/app/src/main/java/com/example/animatedverticalgrid/AnimatedVerticalGrid.kt). It's not production-ready but with a few more tweaks it can be. 

If you want to play with it, make sure to check out [my example app](https://github.com/linean/AnimatedVerticalGrid/tree/main/app/src/main/java/com/example/animatedverticalgrid).

Stay inspired!
