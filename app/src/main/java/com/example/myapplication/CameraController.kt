package com.example.myapplication

import android.content.Context
import android.util.Log
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import com.example.myapplication.ui.Frame
import java.util.concurrent.ExecutorService

class CameraController(
    private val context: Context,
    private val lifecycleOwner: LifecycleOwner,
    private val analysisExecutor: ExecutorService,
) {
    private var cameraSelector: CameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

    fun startCamera(
        previewView: PreviewView,
        analyzer: (Frame) -> Unit,
    ) {
        val future = ProcessCameraProvider.getInstance(context)

        future.addListener({
            try {
                val provider = future.get()
                provider.unbindAll()

                val preview = getPreview(previewView)
                val imageAnalysis = getImageAnalysis(analyzer)

                provider.bindToLifecycle(
                    lifecycleOwner,
                    cameraSelector,
                    preview,
                    imageAnalysis,
                )
            } catch (e: Exception) {
                Log.e("CameraController", "Bind 실패", e)
            }
        }, ContextCompat.getMainExecutor(context))
    }

    fun switchCamera(
        previewView: PreviewView,
        analyzer: (Frame) -> Unit,
    ) {
        cameraSelector =
            if (cameraSelector ==
                CameraSelector.DEFAULT_BACK_CAMERA
            ) {
                CameraSelector.DEFAULT_FRONT_CAMERA
            } else {
                CameraSelector.DEFAULT_BACK_CAMERA
            }

        startCamera(previewView, analyzer)
    }

    fun shutdown() {
        analysisExecutor.shutdown()
    }

    private fun getImageAnalysis(analyzer: (Frame) -> Unit): ImageAnalysis =
        ImageAnalysis
            .Builder()
            .setBackpressureStrategy(
                ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST,
            ).setOutputImageFormat(
                ImageAnalysis.OUTPUT_IMAGE_FORMAT_YUV_420_888,
            ).build()
            .also {
                it.setAnalyzer(analysisExecutor) { proxy ->
                    analyzer(proxy.toYUVFrame())
                    proxy.close()
                }
            }

    private fun getPreview(previewView: PreviewView): Preview =
        Preview
            .Builder()
            .build()
            .also {
                it.surfaceProvider = previewView.surfaceProvider
            }
}
