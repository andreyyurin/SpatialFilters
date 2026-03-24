package ru.sad.smoothimage.task.helper

class IntegralImage(private val width: Int, private val height: Int) {

    private val integralA: Array<LongArray> = Array(height + 1) { LongArray(width + 1) }
    private val integralR: Array<LongArray> = Array(height + 1) { LongArray(width + 1) }
    private val integralG: Array<LongArray> = Array(height + 1) { LongArray(width + 1) }
    private val integralB: Array<LongArray> = Array(height + 1) { LongArray(width + 1) }

    constructor(pixelArray: IntArray, width: Int, height: Int) : this(width, height) {
        for (y in 0 until height) {
            var rowSumA = 0L
            var rowSumR = 0L
            var rowSumG = 0L
            var rowSumB = 0L

            for (x in 0 until width) {
                val pixel = pixelArray[y * width + x]
                val a = (pixel shr 24 and 0xFF).toLong()
                val r = (pixel shr 16 and 0xFF).toLong()
                val g = (pixel shr 8 and 0xFF).toLong()
                val b = (pixel and 0xFF).toLong()

                rowSumA += a
                rowSumR += r
                rowSumG += g
                rowSumB += b

                integralA[y + 1][x + 1] = integralA[y][x + 1] + rowSumA
                integralR[y + 1][x + 1] = integralR[y][x + 1] + rowSumR
                integralG[y + 1][x + 1] = integralG[y][x + 1] + rowSumG
                integralB[y + 1][x + 1] = integralB[y][x + 1] + rowSumB
            }
        }
    }

    fun getWindowSum(centerX: Int, centerY: Int, radius: Int): Int {
        val left = maxOf(0, centerX - radius)
        val top = maxOf(0, centerY - radius)
        val right = minOf(width - 1, centerX + radius)
        val bottom = minOf(height - 1, centerY + radius)

        if (left > right || top > bottom) return 0

        val count = (right - left + 1) * (bottom - top + 1)

        val sumA = integralA[bottom + 1][right + 1] - integralA[top][right + 1] -
                integralA[bottom + 1][left] + integralA[top][left]

        val sumR = integralR[bottom + 1][right + 1] - integralR[top][right + 1] -
                integralR[bottom + 1][left] + integralR[top][left]

        val sumG = integralG[bottom + 1][right + 1] - integralG[top][right + 1] -
                integralG[bottom + 1][left] + integralG[top][left]

        val sumB = integralB[bottom + 1][right + 1] - integralB[top][right + 1] -
                integralB[bottom + 1][left] + integralB[top][left]

        val avgA = (sumA / count).toInt()
        val avgR = (sumR / count).toInt()
        val avgG = (sumG / count).toInt()
        val avgB = (sumB / count).toInt()

        return (avgA shl 24) or (avgR shl 16) or (avgG shl 8) or avgB
    }
}