package com.example.myapplication

interface FrameProcessor<T : Frame> {
    fun process(frame: T): T
}
