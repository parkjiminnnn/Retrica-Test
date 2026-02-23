package com.example.myapplication

import android.graphics.Color
import android.widget.ImageView
import androidx.core.graphics.createBitmap
import androidx.core.graphics.set

class YUVRenderer(
    private val imageView: ImageView,
) : Renderer<YUVFrame> {
    override fun render(frame: YUVFrame) {
        val width = frame.width
        val height = frame.height
        val bitmap = createBitmap(width, height)

        val y = frame.yPlane
        val u = frame.uPlane
        val v = frame.vPlane

        for (j in 0 until height) {
            for (i in 0 until width) {
                val yIndex = j * width + i
                val uvIndex = (j / 2) * (width / 2) + (i / 2)

                val yValue = y.get(yIndex).toInt() and 0xFF
                val uValue = (u.get(uvIndex).toInt() and 0xFF) - 128
                val vValue = (v.get(uvIndex).toInt() and 0xFF) - 128

                var r = (yValue + 1.402 * vValue).toInt()
                var g = (yValue - 0.344136 * uValue - 0.714136 * vValue).toInt()
                var b = (yValue + 1.772 * uValue).toInt()

                r = r.coerceIn(0, 255)
                g = g.coerceIn(0, 255)
                b = b.coerceIn(0, 255)

                bitmap[i, j] = Color.rgb(r, g, b)
            }
        }

        imageView.post { imageView.setImageBitmap(bitmap) }
    }
}
