package com.example.myapplication

import java.nio.ByteBuffer

data class FrameData(
    val y: ByteBuffer,
    val u: ByteBuffer,
    val v: ByteBuffer,
    val width: Int,
    val height: Int,
    val timestamp: Long,
)
