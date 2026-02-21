package com.example.myapplication

class FilterChain(
    private val filters: List<FrameProcessor>,
) : FrameProcessor {
    override suspend fun process(input: FrameData): FrameData {
        var current = input
        filters.forEach { filter ->
            current = filter.process(current)
        }
        return current
    }
}
