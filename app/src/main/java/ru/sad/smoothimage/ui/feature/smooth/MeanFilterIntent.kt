package ru.sad.smoothimage.ui.feature.smooth

import android.net.Uri

sealed interface MeanFilterIntent {
    data class PickImage(
        val uri: Uri?,
    ) : MeanFilterIntent

    data class OnSmoothPickSliderPickChange(
        val value: Int
    ) : MeanFilterIntent

    data class OnChunksSliderPickChange(
        val value: Int
    ) : MeanFilterIntent
}