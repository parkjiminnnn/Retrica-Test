package com.example.myapplication

import android.content.Context
import android.util.Log
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import java.util.concurrent.ExecutorService

class CameraController(
    private val context: Context,
    private val lifecycleOwner: LifecycleOwner,
    private val analyzer: (ImageProxy) -> Unit,
    private val analysisExecutor: ExecutorService,
) {
    private var cameraSelector: CameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

    fun startCamera(previewView: PreviewView) {
        val future = ProcessCameraProvider.getInstance(context)

        future.addListener({
            try {
                val provider = future.get()

                provider.unbindAll()

                val preview =
                    Preview
                        .Builder()
                        .build()
                        .also {
                            it.surfaceProvider = previewView.surfaceProvider
                        }

                val imageAnalysis =
                    ImageAnalysis
                        .Builder()
                        .setBackpressureStrategy(
                            ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST,
                        ).setOutputImageFormat(
                            ImageAnalysis.OUTPUT_IMAGE_FORMAT_YUV_420_888,
                        ).build()
                        .also {
                            it.setAnalyzer(analysisExecutor, analyzer)
                        }

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

    fun switchCamera(previewView: PreviewView) {
        cameraSelector =
            if (cameraSelector ==
                CameraSelector.DEFAULT_BACK_CAMERA
            ) {
                CameraSelector.DEFAULT_FRONT_CAMERA
            } else {
                CameraSelector.DEFAULT_BACK_CAMERA
            }

        return startCamera(previewView)
    }

    fun shutdown() {
        analysisExecutor.shutdown()
    }
}
