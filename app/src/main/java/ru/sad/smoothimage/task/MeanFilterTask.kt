package ru.sad.smoothimage.task

import android.content.ContentResolver
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.core.graphics.createBitmap
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import ru.sad.smoothimage.task.helper.IntegralImage
import kotlin.math.min
import kotlin.system.measureTimeMillis

class MeanFilterTask(
    private val context: Context
) {

    suspend operator fun invoke(
        uri: Uri?,
        size: Int,
        chunkSize: Int
    ): Result<SmoothResult> = runCatchingIo {

        if (uri == null) error("Uri is null")

        val image = getBitmapFromUri(uri, context.contentResolver)
        val height = image.height
        val width = image.width

        val pixelsArray = IntArray(width * height)
        val resultPixelsArray = IntArray(width * height)

        image.getPixels(
            pixelsArray, 0, width, 0, 0, width, height
        )

        val resultBitmap = createBitmap(width, height)

        val time = measureTimeMillis {
            chunkInParallel(
                width = width,
                height = height,
                pixelsArray = pixelsArray,
                resultPixelsArray = resultPixelsArray,
                size = size,
                chunkSize = chunkSize
            )

        }
        resultBitmap.setPixels(resultPixelsArray, 0, width, 0, 0, width, height)

        SmoothResult(
            initialImage = image.asImageBitmap(),
            resultImage = resultBitmap.asImageBitmap(),
            time = time
        )
    }

    private suspend fun chunkInParallel(
        width: Int,
        height: Int,
        pixelsArray: IntArray,
        resultPixelsArray: IntArray,
        size: Int,
        chunkSize: Int,
    ) {
        val chunksX = (width + chunkSize - 1) / chunkSize
        val chunksY = (height + chunkSize - 1) / chunkSize
        val procs = Runtime.getRuntime().availableProcessors()

        val chunks = mutableListOf<Chunk>()
        for (iy in 0 until chunksY) {
            val chunkHeight = minOf(chunkSize, height - iy * chunkSize)
            for (ix in 0 until chunksX) {
                val chunkWidth = minOf(chunkSize, width - ix * chunkSize)
                chunks.add(Chunk(ix, iy, chunkWidth, chunkHeight))
            }
        }

        val groups = chunks.chunked((chunks.size + procs - 1) / procs)

        groups.map { group ->
            coroutineScope {
                async {
                    for (chunk in group) {
                        val (ix, iy, chunkWidth, chunkHeight) = chunk
                        val chunkPixelCount = chunkWidth * chunkHeight

                        val chunkPixels = IntArray(chunkPixelCount)
                        for (y in 0 until chunkHeight) {
                            val srcStart = (iy * chunkSize + y) * width + ix * chunkSize
                            System.arraycopy(
                                pixelsArray,
                                srcStart,
                                chunkPixels,
                                y * chunkWidth,
                                chunkWidth
                            )
                        }

                        val resultChunk = IntArray(chunkPixelCount)

                        chunkAndSmoothPixels(
                            resultPixelsArray = resultChunk,
                            pixelsArray = chunkPixels,
                            width = chunkWidth,
                            height = chunkHeight,
                            chunkSize = chunkSize,
                            size = size
                        )

                        for (y in 0 until chunkHeight) {
                            val dstStart = (iy * chunkSize + y) * width + ix * chunkSize
                            System.arraycopy(
                                resultChunk,
                                y * chunkWidth,
                                resultPixelsArray,
                                dstStart,
                                chunkWidth
                            )
                        }
                    }
                }
            }
        }.awaitAll()
    }

    private fun chunkAndSmoothPixels(
        width: Int,
        height: Int,
        pixelsArray: IntArray,
        resultPixelsArray: IntArray,
        size: Int,
        chunkSize: Int
    ) {
        val chunksX = (width + chunkSize - 1) / chunkSize
        val chunksY = (height + chunkSize - 1) / chunkSize

        for (iy in 0 until chunksY) {
            val chunkHeight = min(chunkSize, height - iy * chunkSize)

            for (ix in 0 until chunksX) {
                val chunkWidth = min(chunkSize, width - ix * chunkSize)
                val chunkPixelCount = chunkWidth * chunkHeight

                val chunkPixels = IntArray(chunkPixelCount)

                for (y in 0 until chunkHeight) {
                    val srcStart = (iy * chunkSize + y) * width + ix * chunkSize
                    System.arraycopy(pixelsArray, srcStart, chunkPixels, y * chunkWidth, chunkWidth)
                }

                val resultChunk = IntArray(chunkPixelCount)

                smoothPixelsIntegralImage(
                    resultPixelArray = resultChunk,
                    defaultPixelArray = chunkPixels,
                    width = chunkWidth,
                    height = chunkHeight,
                    windowSize = size
                )

                for (y in 0 until chunkHeight) {
                    val dstStart = (iy * chunkSize + y) * width + ix * chunkSize
                    System.arraycopy(
                        resultChunk,
                        y * chunkWidth,
                        resultPixelsArray,
                        dstStart,
                        chunkWidth
                    )
                }
            }
        }
    }


    private fun smoothPixelsIntegralImage(
        defaultPixelArray: IntArray,
        resultPixelArray: IntArray,
        width: Int,
        height: Int,
        windowSize: Int = 3
    ) {
        val image = IntegralImage(defaultPixelArray, width, height)

        logD("$width $height")

        for (index in defaultPixelArray.indices) {
            val x = index % width
            val y = index / width

            val newPixel = image.getWindowSum(x, y, windowSize)
            resultPixelArray[index] = newPixel
        }
    }

    private fun getBitmapFromUri(uri: Uri, contentResolver: ContentResolver): Bitmap {
        return contentResolver.openInputStream(uri)?.use { inputStream ->
            BitmapFactory.decodeStream(inputStream)
        } ?: error("Image is undefined")
    }

    private data class Chunk(
        val ix: Int,
        val iy: Int,
        val width: Int,
        val height: Int
    )

    companion object {
        data class SmoothResult(
            val initialImage: ImageBitmap,
            val resultImage: ImageBitmap,
            val time: Long
        )
    }
}