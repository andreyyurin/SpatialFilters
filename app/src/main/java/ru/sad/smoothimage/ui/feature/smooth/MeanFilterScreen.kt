package ru.sad.smoothimage.ui.feature.smooth

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import org.koin.compose.viewmodel.koinViewModel
import ru.sad.smoothimage.ui.component.ErrorView
import ru.sad.smoothimage.ui.component.SliderThumb
import ru.sad.smoothimage.ui.component.SliderTrack
import kotlin.math.roundToInt

@Composable
fun MeanFilterScreen(viewModel: MeanFilterViewModel = koinViewModel()) {
    val state = viewModel.stateSmoothState.collectAsStateWithLifecycle()
    val scope = rememberCoroutineScope()
    val showErrorToast = remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        viewModel.labelsSmoothImage.collect {
            when (it) {
                is MeanFilterLabel.ShowErrorToast -> {
                    scope.launch {
                        showErrorToast.value = true
                        delay(1000L)
                        showErrorToast.value = false
                    }
                }
            }
        }
    }

    ImageContent(state.value) {
        viewModel.executeIntent(it)
    }

    ErrorView(isShow = showErrorToast)
}

@Composable
private fun ImageContent(
    state: SmoothState,
    onIntent: (MeanFilterIntent) -> Unit
) {
    Box(
        Modifier
            .fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            Modifier
                .fillMaxHeight()
                .padding(vertical = 30.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            ButtonImagePick(onIntent = onIntent)
            ChunkSlider(
                onIntent = onIntent,
                isAvailable = state.initialImageUri != null
            )
            SmoothSlider(
                onIntent = onIntent,
                isAvailable = state.initialImageUri != null
            )

            Time(state.time)
            Column(
                modifier = Modifier
                    .padding(top = 20.dp)
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                ImageResult(state.initialImage)
                ImageResult(state.resultImage)
            }
        }
    }
}

@Composable
fun Time(time: Long) {
    if (time <= 0) return

    Text(modifier = Modifier.padding(top = 30.dp), text = "Completed for $time ms")
}

@OptIn(ExperimentalMaterial3Api::class, FlowPreview::class)
@Composable
fun SmoothSlider(
    onIntent: (MeanFilterIntent) -> Unit,
    isAvailable: Boolean
) {
    if (!isAvailable) return
    val sliderPosition = remember { mutableIntStateOf(DEFAULT_SMOOTH_VALUE) }
    LaunchedEffect(Unit) {
        snapshotFlow { sliderPosition.intValue }
            .distinctUntilChanged()
            .debounce(100)
            .onEach {
                onIntent.invoke(
                    MeanFilterIntent.OnSmoothPickSliderPickChange(
                        it,
                    )
                )
            }
            .launchIn(this)
    }

    Column(
        modifier = Modifier.padding(horizontal = 25.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Slider(
            value = sliderPosition.intValue.toFloat(),
            onValueChange = {
                val value = it.roundToInt()
                val oddValue = if (value % 2 == 0) {
                    value - 1
                } else {
                    value
                }
                sliderPosition.intValue = oddValue
            },
            valueRange = 3f..200f,
            thumb = {
                SliderThumb()
            },
            track = { state ->
                SliderTrack(
                    state,
                )
            }
        )
        Text(text = "Smooth ${sliderPosition.intValue}")
    }
}

@OptIn(ExperimentalMaterial3Api::class, FlowPreview::class)
@Composable
private fun ChunkSlider(
    onIntent: (MeanFilterIntent) -> Unit,
    isAvailable: Boolean
) {
    if (!isAvailable) return
    val sliderPosition = remember { mutableIntStateOf(DEFAULT_CHUNK_VALUE) }

    LaunchedEffect(Unit) {
        snapshotFlow { sliderPosition.intValue }
            .distinctUntilChanged()
            .debounce(100)
            .onEach {
                onIntent.invoke(
                    MeanFilterIntent.OnChunksSliderPickChange(
                        it,
                    )
                )
            }
            .launchIn(this)
    }

    Column(
        modifier = Modifier.padding(horizontal = 25.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Slider(
            value = sliderPosition.intValue.toFloat(),
            onValueChange = {
                val value = it.roundToInt()
                val evenValue = if (value % 2 != 0) {
                    value - 1
                } else {
                    value
                }
                sliderPosition.intValue = evenValue
            },
            valueRange = 20f..1000f,
            thumb = {
                SliderThumb()
            },
            track = { state ->
                SliderTrack(
                    state,
                )
            }
        )
        Text(text = "Chunks ${sliderPosition.intValue}")
    }
}

@Composable
private fun ButtonImagePick(
    onIntent: (MeanFilterIntent) -> Unit
) {
    val singlePhotoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = {
            onIntent.invoke(MeanFilterIntent.PickImage(it))
        }
    )

    val request = remember {
        PickVisualMediaRequest(
            mediaType = ActivityResultContracts.PickVisualMedia.ImageOnly
        )
    }

    TextButton(
        modifier = Modifier
            .background(
                shape = RoundedCornerShape(20.dp),
                color = Color.Black
            ),
        onClick = {
            singlePhotoPickerLauncher.launch(request)
        }
    ) {
        Text(
            text = "Pick image",
            color = Color.White
        )
    }
}

@Composable
private fun ColumnScope.ImageResult(bitmap: ImageBitmap?) {
    if (bitmap == null) return
    Image(
        bitmap = bitmap,
        contentDescription = "Image 1",
        modifier = Modifier
            .weight(1f)
            .fillMaxHeight(),
        contentScale = ContentScale.Fit
    )
}

