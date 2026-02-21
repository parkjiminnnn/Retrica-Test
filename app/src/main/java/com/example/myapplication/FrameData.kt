package com.example.myapplication

import androidx.camera.core.ImageProxy
import java.nio.ByteBuffer

data class FrameData(
    val yPlane: ByteBuffer,
    val uPlane: ByteBuffer,
    val vPlane: ByteBuffer,
    val width: Int,
    val height: Int,
    val timestamp: Long,
)

fun ImageProxy.toFrameData(): FrameData {
    val yPlane = planes[0]
    val uPlane = planes[1]
    val vPlane = planes[2]

    return FrameData(
        yPlane = yPlane.buffer,
        uPlane = uPlane.buffer,
        vPlane = vPlane.buffer,
        width = width,
        height = height,
        timestamp = imageInfo.timestamp,
    )
}
