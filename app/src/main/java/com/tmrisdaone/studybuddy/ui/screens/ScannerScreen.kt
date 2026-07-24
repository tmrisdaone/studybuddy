package com.tmrisdaone.studybuddy.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.DocumentScanner
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.tmrisdaone.studybuddy.ui.theme.TurboGradients
import com.tmrisdaone.studybuddy.ui.viewmodels.ScannerViewModel

@Composable
fun ScannerScreen(viewModel: ScannerViewModel) {
    val scannedText by viewModel.scannedText.collectAsStateWithLifecycle("")
    val isScanning by viewModel.isScanning.collectAsStateWithLifecycle(false)
    val error by viewModel.error.collectAsStateWithLifecycle<String?>(null)
    val colors = MaterialTheme.colorScheme
    var showText by remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxSize().background(colors.background)) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Header
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(TurboGradients.header)
                    .padding(horizontal = 20.dp, vertical = 18.dp)
            ) {
                Text("Scanner", fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = colors.primary)
                Text("Scan text from camera or photos", fontSize = 22.sp, fontWeight = FontWeight.Bold, color = colors.onSurface)
            }

            Column(
                modifier = Modifier.fillMaxSize().padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .background(
                            brush = TurboGradients.chip,
                            shape = RoundedCornerShape(20.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    if (scannedText.isNotBlank() && showText) {
                        Column(modifier = Modifier.padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("Scanned Text", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = colors.onSurface)
                            Spacer(Modifier.height(10.dp))
                            Text(scannedText, maxLines = 8, overflow = TextOverflow.Ellipsis, color = colors.onSurfaceVariant, fontSize = 14.sp)
                        }
                    } else {
                        Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(14.dp)) {
                            Icon(Icons.Filled.DocumentScanner, contentDescription = null, modifier = Modifier.size(58.dp), tint = colors.primary)
                            Text("Camera Scanner", fontSize = 18.sp, fontWeight = FontWeight.Medium, color = colors.onSurface)
                            Text("Point camera at text to scan", fontSize = 13.sp, color = colors.onSurfaceVariant)
                        }
                    }
                }

                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    GradientButton(
                        label = if (isScanning) "" else "Scan with Camera",
                        icon = Icons.Filled.CameraAlt,
                        loading = isScanning,
                        enabled = !isScanning
                    ) { viewModel.scanFromCamera() }

                    SecondaryButton(
                        label = "Pick from Gallery",
                        icon = Icons.Filled.PhotoLibrary,
                        enabled = !isScanning
                    ) { viewModel.scanFromGallery() }

                    if (scannedText.isNotBlank()) {
                        SecondaryButton(
                            label = if (showText) "Hide Result" else "View Result",
                            icon = null,
                            enabled = !isScanning
                        ) { showText = !showText }
                    }
                }

                error?.let { err ->
                    Surface(
                        color = colors.errorContainer,
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(err, color = colors.onErrorContainer, fontSize = 13.sp, modifier = Modifier.padding(14.dp))
                    }
                }

                Spacer(Modifier.height(8.dp))
                Text(
                    "Note: Camera integration requires CameraX + ML Kit setup",
                    fontSize = 12.sp,
                    color = colors.onSurfaceVariant.copy(alpha = 0.7f),
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

@Composable
private fun GradientButton(
    label: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    loading: Boolean,
    enabled: Boolean,
    onClick: () -> Unit
) {
    val colors = MaterialTheme.colorScheme
    Button(
        onClick = onClick,
        enabled = enabled,
        shape = RoundedCornerShape(16.dp),
        colors = ButtonDefaults.buttonColors(containerColor = colors.primary, contentColor = colors.onPrimary),
        modifier = Modifier.fillMaxWidth()
    ) {
        if (loading) {
            CircularProgressIndicator(modifier = Modifier.size(20.dp), color = colors.onPrimary, strokeWidth = 2.dp)
        } else {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(icon, contentDescription = null, modifier = Modifier.size(20.dp))
                Spacer(Modifier.width(10.dp))
                Text(label, fontWeight = FontWeight.SemiBold)
            }
        }
    }
}

@Composable
private fun SecondaryButton(
    label: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector?,
    enabled: Boolean,
    onClick: () -> Unit
) {
    val colors = MaterialTheme.colorScheme
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = colors.surfaceVariant,
        modifier = Modifier.fillMaxWidth()
    ) {
        Button(
            onClick = onClick,
            enabled = enabled,
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.Transparent,
                contentColor = colors.onSurface,
                disabledContainerColor = Color.Transparent,
                disabledContentColor = colors.onSurfaceVariant
            ),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                if (icon != null) {
                    Icon(icon, contentDescription = null, modifier = Modifier.size(20.dp))
                    Spacer(Modifier.width(10.dp))
                }
                Text(label, fontWeight = FontWeight.SemiBold)
            }
        }
    }
}
