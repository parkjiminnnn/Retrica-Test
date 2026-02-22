package com.example.myapplication

import com.example.myapplication.ui.Frame

interface FrameProcessor<T : Frame> {
    suspend fun process(frame: T): T
}
