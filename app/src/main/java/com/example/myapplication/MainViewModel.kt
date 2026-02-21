package com.example.myapplication

import android.util.Log
import androidx.camera.core.ImageProxy
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

class MainViewModel(
    private val frameProcessor: FrameProcessor,
) : ViewModel() {
    private val _frameTime: MutableStateFlow<Long> = MutableStateFlow(0L)
    val frameTime: StateFlow<Long> = _frameTime.asStateFlow()

    fun analyzeFrame(imageProxy: ImageProxy) {
        val frameData = imageProxy.toFrameData()

        viewModelScope.launch(Dispatchers.Default) {
            try {
                val startTime = System.nanoTime()
                frameProcessor.process(frameData)

                val elapsedTime = (System.nanoTime() - startTime) / NANOS_IN_MILLISECOND
                onFrameProcessed(elapsedTime)
            } finally {
                imageProxy.close()
            }
        }
    }

    private fun onFrameProcessed(frameTime: Long) {
        _frameTime.value = frameTime
        Log.d("Performance", "FrameTime: ${frameTime}ms")
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
