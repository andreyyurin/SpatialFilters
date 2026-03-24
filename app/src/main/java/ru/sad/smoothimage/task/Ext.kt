package ru.sad.smoothimage.task

import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

private const val TAG = "SmoothImageLog"

suspend fun <T, R> T.runCatchingDefault(block: suspend T.() -> R): Result<R> = runCatching {
    withContext(Dispatchers.Default) {
        block()
    }
}

suspend fun <T, R> T.runCatchingIo(block: suspend T.() -> R): Result<R> = runCatching {
    withContext(Dispatchers.IO) {
        block()
    }
}

fun logE(text: String) {
    Log.e(TAG, text)
}

fun logD(text: String) {
    Log.d(TAG, text)
}