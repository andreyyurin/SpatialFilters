package ru.sad.smoothimage.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SliderState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SliderTrack(
    sliderState: SliderState,
    modifier: Modifier = Modifier,
    trackColor: Color = Color.LightGray,
    progressColor: Color = Color.DarkGray,
    height: Dp = 10.dp,
    shape: Shape = CircleShape
) {
    Box(
        modifier = modifier
            .track(height = height, shape = shape)
            .background(trackColor)
    ) {
        Box(
            modifier = Modifier
                .progress(
                    sliderState = sliderState,
                    height = height,
                    shape = shape
                )
                .background(progressColor)
        )
    }
}


@OptIn(ExperimentalMaterial3Api::class)
fun Modifier.progress(
    sliderState: SliderState,
    height: Dp = 10.dp,
    shape: Shape = CircleShape
) = this
    .fillMaxWidth(fraction = (sliderState.value - sliderState.valueRange.start) / (sliderState.valueRange.endInclusive - sliderState.valueRange.start))
    .heightIn(min = height)
    .clip(shape)

fun Modifier.track(
    height: Dp = 10.dp,
    shape: Shape = CircleShape
) = this
    .fillMaxWidth()
    .heightIn(min = height)
    .clip(shape)

@Composable
fun SliderThumb(size: Dp = 20.dp) {
    Box(
        modifier = Modifier
            .size(size)
            .clip(CircleShape)
            .background(Color.Green)
    )
}