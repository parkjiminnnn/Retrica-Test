package com.example.myapplication

interface FrameProcessor {
    suspend fun process(input: FrameData): FrameData
}
