package com.example.myapplication

class FilterChain<T : Frame>(
    private val filters: List<FrameProcessor<T>>,
) : FrameProcessor<T> {
    override fun process(frame: T): T {
        var current = frame
        filters.forEach { filter ->
            current = filter.process(current)
        }
        return current
    }
}
