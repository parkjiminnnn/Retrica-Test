package com.example.myapplication

import android.widget.ImageView
import androidx.camera.view.PreviewView
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.myapplication.ui.Frame
import java.util.concurrent.Executors

@Composable
fun CameraScreen(
    modifier: Modifier = Modifier,
    viewModel: MainViewModel<Frame> = viewModel(factory = MainViewModel.Factory),
) {
    val frameTime by viewModel.frameTime.collectAsStateWithLifecycle()
    val processedFrame by viewModel.processedFrame.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    val previewView = remember { PreviewView(context) }
    val analysisExecutor = remember { Executors.newSingleThreadExecutor() }
    val cameraController =
        remember { CameraController(context, lifecycleOwner, analysisExecutor) }

    val imageView = remember { ImageView(context) }
    val renderer = remember { YUVRenderer(imageView) }

    LaunchedEffect(Unit) { cameraController.startCamera(previewView) { viewModel.analyzeFrame(it) } }
    DisposableEffect(Unit) { onDispose { cameraController.shutdown() } }

    LaunchedEffect(processedFrame) {
        processedFrame?.let { renderer.render(it as YUVFrame) }
    }

    Column(
        modifier = modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        AndroidView(factory = { previewView }, modifier = Modifier.weight(1f))

        AndroidView(factory = { imageView }, modifier = Modifier.weight(1f))

        Text(text = "$frameTime ms", fontSize = 40.sp)
        Button(onClick = { cameraController.switchCamera(previewView) { viewModel.analyzeFrame(it) } }) {
            Text(text = "카메라 전환")
        }
    }
}

@Composable
@Preview(showBackground = true)
private fun CameraScreenPreview() {
    CameraScreen()
}
