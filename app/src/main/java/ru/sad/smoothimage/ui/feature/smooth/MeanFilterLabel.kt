package ru.sad.smoothimage.ui.feature.smooth

sealed interface MeanFilterLabel {
    data class ShowErrorToast(val message: String) : MeanFilterLabel
}