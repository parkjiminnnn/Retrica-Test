package com.example.myapplication

import android.graphics.Bitmap
import android.graphics.Color
import android.widget.ImageView
import androidx.core.graphics.createBitmap
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.lang.ref.WeakReference

class YUVRenderer(
    imageView: ImageView,
) : Renderer<YUVFrame> {
    private val imageViewRef = WeakReference(imageView)

    private val scope = CoroutineScope(Job() + Dispatchers.Default)

    private val frameFlow = MutableSharedFlow<YUVFrame>(extraBufferCapacity = 1)

    private var bitmap: Bitmap? = null
    private var pixels: IntArray? = null

    init {
        startWorker()
    }

    override fun render(frame: YUVFrame) {
        frameFlow.tryEmit(frame)
    }

    fun release() {
        scope.cancel()
    }

    private fun startWorker() {
        scope.launch {
            frameFlow.collectLatest {
                processFrame(it)
            }
        }
    }

    private suspend fun processFrame(frame: YUVFrame) {
        val width = frame.width
        val height = frame.height

        val bmp =
            bitmap?.takeIf { it.width == width && it.height == height }
                ?: createBitmap(width, height).also { bitmap = it }

        val buffer =
            pixels?.takeIf { it.size == width * height }
                ?: IntArray(width * height).also { pixels = it }

        convertYUVToRGB(width, height, frame, buffer)

        if (!currentCoroutineContext().isActive) return

        bmp.setPixels(buffer, 0, width, 0, 0, width, height)

        withContext(Dispatchers.Main) {
            imageViewRef.get()?.setImageBitmap(bmp)
        }
    }

    private fun convertYUVToRGB(
        width: Int,
        height: Int,
        frame: YUVFrame,
        pixels: IntArray,
    ) {
        val y = frame.yPlane
        val u = frame.uPlane
        val v = frame.vPlane

        for (j in 0 until height) {
            val uvRow = (j shr 1) * (width shr 1)
            val yRow = j * width

            for (i in 0 until width) {
                val yIndex = yRow + i
                val uvIndex = uvRow + (i shr 1)

                val yValue = y[yIndex].toInt() and 0xFF
                val uValue = (u[uvIndex].toInt() and 0xFF) - 128
                val vValue = (v[uvIndex].toInt() and 0xFF) - 128

                var r = (yValue + 1.402f * vValue).toInt()
                var g = (yValue - 0.344136f * uValue - 0.714136f * vValue).toInt()
                var b = (yValue + 1.772f * uValue).toInt()

                r = r.coerceIn(0, 255)
                g = g.coerceIn(0, 255)
                b = b.coerceIn(0, 255)

                pixels[yIndex] = Color.rgb(r, g, b)
            }
        }
    }
}
