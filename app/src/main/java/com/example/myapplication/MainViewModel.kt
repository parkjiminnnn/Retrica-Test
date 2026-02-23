package com.example.myapplication

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class MainViewModel<T : Frame>(
    private val frameProcessor: FrameProcessor<T>,
) : ViewModel() {
    private val _frameTime: MutableStateFlow<Long> = MutableStateFlow(0L)
    val frameTime: StateFlow<Long> = _frameTime.asStateFlow()

    private val _processedFrame: MutableStateFlow<T?> = MutableStateFlow(null)
    val processedFrame: StateFlow<T?> = _processedFrame.asStateFlow()

    fun analyzeFrame(frame: T) {
        viewModelScope.launch(Dispatchers.Default) {
            val startTime = System.nanoTime()
            val processed = frameProcessor.process(frame)
            _processedFrame.value = processed
            val elapsedTime = (System.nanoTime() - startTime) / NANOS_IN_MILLISECOND
            _frameTime.value = elapsedTime
        }
    }

    companion object {
        private const val NANOS_IN_MILLISECOND: Long = 1_000_000L
        val Factory: ViewModelProvider.Factory =
            viewModelFactory {
                initializer {
                    val filters = listOf(GrayScaleFilter())
                    val frameProcessor = FilterChain(filters)
                    MainViewModel(frameProcessor = frameProcessor)
                }
            }
    }
}
