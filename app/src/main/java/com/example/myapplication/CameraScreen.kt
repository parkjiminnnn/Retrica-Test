package com.example.myapplication

import androidx.camera.view.PreviewView
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.compose.LocalLifecycleOwner
import java.util.concurrent.Executors

@Composable
fun CameraScreen(modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val previewView = remember { PreviewView(context) }
    val analysisExecutor = remember { Executors.newSingleThreadExecutor() }
    val cameraController =
        remember {
            CameraController(
                context = context,
                lifecycleOwner = lifecycleOwner,
                analyzer = { it.close() },
                analysisExecutor = analysisExecutor,
            )
        }
    LaunchedEffect(Unit) {
        cameraController.startCamera(previewView)
    }
    DisposableEffect(Unit) {
        onDispose {
            cameraController.shutdown()
        }
    }
    AndroidView(
        modifier = modifier.fillMaxSize(),
        factory = { previewView },
    )
}

@Composable
@Preview
private fun CameraScreenPreview() {
    CameraScreen()
}
