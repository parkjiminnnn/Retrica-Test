package com.example.myapplication

interface FrameProcessor<T : Frame> {
    suspend fun process(frame: T): T
}
