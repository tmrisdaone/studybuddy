package com.tmrisdaone.studybuddy.ui.screens

import android.Manifest
import android.graphics.Bitmap
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.concurrent.Executors

@Composable
fun ScannerScreen(onDocumentScanned: (String) -> Unit) {
    var hasCamera by remember { mutableStateOf(false) }
    var captured by remember { mutableStateOf<Bitmap?>(null) }
    var processing by remember { mutableStateOf(false) }
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    val cameraPermission = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted -> hasCamera = granted }

    LaunchedEffect(Unit) { cameraPermission.launch(Manifest.permission.CAMERA) }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Text("Scan Document", style = MaterialTheme.typography.headlineMedium)

        if (hasCamera && captured == null) {
            AndroidView(factory = { ctx ->
                val previewView = PreviewView(ctx)
                val cameraProvider = ProcessCameraProvider.getInstance(ctx).get()
                val preview = Preview.Builder().build().also { it.setSurfaceProvider(previewView.surfaceProvider) }
                cameraProvider.bindToLifecycle(
                    ctx as androidx.lifecycle.LifecycleOwner,
                    CameraSelector.DEFAULT_BACK_CAMERA,
                    preview
                )
                previewView
            }, modifier = Modifier.weight(1f).fillMaxWidth())
        }

        if (captured != null) {
            processing = true
            LaunchedEffect(captured) {
                scope.launch {
                    val text = withContext(Dispatchers.IO) {
                        // TODO: use ML Kit OCR or Tesseract
                        "[OCR text from ${captured?.width}x${captured?.height} bitmap — integrate ML Kit]"
                    }
                    processing = false
                    onDocumentScanned(text)
                }
            }
        }

        Button(
            onClick = {
                hasCamera = false
                cameraPermission.launch(Manifest.permission.CAMERA)
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(Icons.Default.CameraAlt, null)
            Spacer(Modifier.width(8.dp))
            Text("Open Camera")
        }

        if (processing) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
        }
    }
}
