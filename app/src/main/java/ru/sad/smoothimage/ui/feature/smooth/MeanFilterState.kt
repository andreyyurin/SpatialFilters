package ru.sad.smoothimage.ui.feature.smooth

import android.net.Uri
import androidx.compose.runtime.Stable
import androidx.compose.ui.graphics.ImageBitmap


const val DEFAULT_SMOOTH_VALUE = 21
const val DEFAULT_CHUNK_VALUE = 500

@Stable
data class SmoothState(
    val initialImageUri: Uri? = null,
    val initialImage: ImageBitmap? = null,
    val resultImage: ImageBitmap? = null,
    val chunkValue: Int = DEFAULT_CHUNK_VALUE,
    val smoothValue: Int = DEFAULT_SMOOTH_VALUE,
    val time: Long = 0L
)