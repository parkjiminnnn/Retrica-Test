package com.example.myapplication

class GrayScaleFilter : FrameProcessor {
    override suspend fun process(input: FrameData): FrameData {
        val uBuffer = input.uPlane
        val vBuffer = input.vPlane

        (0..uBuffer.capacity()).forEach {
            uBuffer.put(it, NEUTRAL_UV_VALUE.toByte())
        }
        (0..vBuffer.capacity()).forEach {
            vBuffer.put(it, NEUTRAL_UV_VALUE.toByte())
        }
        return input
    }

    companion object {
        private const val NEUTRAL_UV_VALUE: Int = 128
    }
}
