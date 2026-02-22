package com.example.myapplication

import androidx.camera.core.ImageProxy
import com.example.myapplication.ui.Frame
import java.nio.ByteBuffer

data class YUVFrame(
    val yPlane: ByteBuffer,
    val uPlane: ByteBuffer,
    val vPlane: ByteBuffer,
    override val width: Int,
    override val height: Int,
    override val timestamp: Long,
) : Frame

fun ImageProxy.toYUVFrame(): YUVFrame {
    val yPlane = ByteBuffer.allocate(planes[0].buffer.capacity())
    yPlane.put(planes[0].buffer).rewind()
    val uPlane = ByteBuffer.allocate(planes[1].buffer.capacity())
    uPlane.put(planes[1].buffer).rewind()
    val vPlane = ByteBuffer.allocate(planes[2].buffer.capacity())
    vPlane.put(planes[2].buffer).rewind()
    return YUVFrame(yPlane, uPlane, vPlane, width, height, imageInfo.timestamp)
}
