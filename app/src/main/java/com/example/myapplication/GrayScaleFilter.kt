package com.example.myapplication

class GrayScaleFilter : FrameProcessor<YUVFrame> {
    override fun process(frame: YUVFrame): YUVFrame {
        val uBuffer = frame.uPlane
        val vBuffer = frame.vPlane

        (0 until uBuffer.capacity()).forEach {
            uBuffer.put(it, NEUTRAL_UV_VALUE.toByte())
        }
        (0 until vBuffer.capacity()).forEach {
            vBuffer.put(it, NEUTRAL_UV_VALUE.toByte())
        }
        return frame
    }

    companion object {
        private const val NEUTRAL_UV_VALUE: Int = 128
    }
}
