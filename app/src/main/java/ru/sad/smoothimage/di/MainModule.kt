package ru.sad.smoothimage.di

import org.koin.android.ext.koin.androidContext
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module
import ru.sad.smoothimage.task.MeanFilterTask
import ru.sad.smoothimage.ui.feature.smooth.MeanFilterViewModel

val mainModule = module {
    factory { MeanFilterTask(androidContext()) }

    viewModelOf(::MeanFilterViewModel)
}