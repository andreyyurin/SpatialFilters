package ru.sad.smoothimage.ui.feature.smooth

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import ru.sad.smoothimage.task.MeanFilterTask
import ru.sad.smoothimage.task.logE

class MeanFilterViewModel(
    private val meanFilterTask: MeanFilterTask
) : ViewModel() {

    private val _stateSmoothImage = MutableStateFlow(SmoothState())
    val stateSmoothState = _stateSmoothImage
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(),
            initialValue = SmoothState()
        )

    private val _labelsSmoothImage =
        MutableSharedFlow<MeanFilterLabel>(replay = 0, extraBufferCapacity = 0)
    val labelsSmoothImage = _labelsSmoothImage.asSharedFlow()

    fun executeIntent(intent: MeanFilterIntent) {
        when (intent) {
            is MeanFilterIntent.PickImage -> handlePickImageIntent(intent.uri)
            is MeanFilterIntent.OnSmoothPickSliderPickChange -> handleSmoothSliderPickChange(intent.value)
            is MeanFilterIntent.OnChunksSliderPickChange -> handleChunksSliderPickChange(intent.value)
        }
    }

    private fun handleSmoothSliderPickChange(value: Int) {
        viewModelScope.launch {
            val imageUri = _stateSmoothImage.value.initialImageUri
            val chunkSize = _stateSmoothImage.value.chunkValue

            meanFilterTask(
                uri = imageUri,
                size = value,
                chunkSize = chunkSize
            )
                .onSuccess { (init, result, time) ->
                    _stateSmoothImage.emit(
                        SmoothState(
                            initialImage = init,
                            resultImage = result,
                            initialImageUri = imageUri,
                            smoothValue = value,
                            time = time
                        )
                    )
                }
                .onFailure {
                    logE(it.stackTraceToString())
                    _labelsSmoothImage.emit(MeanFilterLabel.ShowErrorToast(message = it.message.orEmpty()))
                }
        }
    }

    private fun handleChunksSliderPickChange(value: Int) {
        viewModelScope.launch {
            val imageUri = _stateSmoothImage.value.initialImageUri
            val smoothValue = _stateSmoothImage.value.smoothValue

            meanFilterTask(
                uri = imageUri,
                size = smoothValue,
                chunkSize = value
            )
                .onSuccess { (init, result, time) ->
                    _stateSmoothImage.emit(
                        SmoothState(
                            initialImage = init,
                            resultImage = result,
                            initialImageUri = imageUri,
                            chunkValue = value,
                            time = time
                        )
                    )
                }
                .onFailure {
                    logE(it.stackTraceToString())
                    _labelsSmoothImage.emit(MeanFilterLabel.ShowErrorToast(message = it.message.orEmpty()))
                }
        }
    }

    private fun handlePickImageIntent(
        uri: Uri?,
    ) {
        viewModelScope.launch {
            val chunkSize = _stateSmoothImage.value.chunkValue
            val smoothValue = _stateSmoothImage.value.smoothValue

            meanFilterTask(
                uri = uri,
                size = smoothValue,
                chunkSize = chunkSize
            )
                .onSuccess { (init, result, time) ->
                    _stateSmoothImage.emit(
                        SmoothState(
                            initialImage = init,
                            resultImage = result,
                            initialImageUri = uri,
                            time = time
                        )
                    )
                }
                .onFailure {
                    logE(it.stackTraceToString())
                    _labelsSmoothImage.emit(MeanFilterLabel.ShowErrorToast(it.message.orEmpty()))
                }
        }
    }
}