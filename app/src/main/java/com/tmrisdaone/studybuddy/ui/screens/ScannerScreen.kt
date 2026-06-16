package com.tmrisdaone.studybuddy.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.spacer
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tmrisdaone.studybuddy.ui.theme.StudyBuddyTheme
import com.tmrisdaone.studybuddy.ui.viewmodels.ScannerViewModel

@Composable
fun ScannerScreen(
    viewModel: ScannerViewModel,
    onNavigateToChat: () -> Unit
) {
    val scannedText by viewModel.scannedText.observeAsState("")
    val isScanning by viewModel.isScanning.observeAsState(false)
    val error by viewModel.error.observeAsState<String?>(null)
    
    var showText by remember { mutableStateOf(false) }
    
    Column(modifier = Modifier.fillMaxSize()) {
        TopAppBar(
            title = { Text("Scanner", fontWeight = FontWeight.Bold) },
            colors = TopAppBarDefaults.topAppBarColors(containerColor = StudyBuddyTheme.colorScheme.surfaceContainer),
            navigationIcon = {
                IconButton(onClick = onNavigateToChat) {
                    Icon(
                        painter = androidx.compose.material.icons.Icons.Filled.ArrowBack,
                        contentDescription = "Back"
                    )
                }
            }
        )
        
        androidx.compose.foundation.layout.Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            androidx.compose.foundation.layout.Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1f)
                    .background(
                        color = StudyBuddyTheme.colorScheme.surfaceContainer,
                        shape = androidx.compose.foundation.shape.RoundedCornerShape(16.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                if (!scannedText.isBlank() && showText) {
                    androidx.compose.foundation.layout.Column(
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Scanned Text",
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp,
                            color = StudyBuddyTheme.colorScheme.onSurface
                        )
                        androidx.compose.foundation.layout.Spacer(modifier = Modifier.height(12.dp))
                        androidx.compose.material3.Text(
                            text = scannedText,
                            maxLines = 10,
                            overflow = androidx.compose.ui.text.TextOverflow.Ellipsis,
                            color = StudyBuddyTheme.colorScheme.onSurface
                        )
                    }
                } else {
                    androidx.compose.foundation.layout.Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Icon(
                            painter = androidx.compose.material.icons.Icons.Filled.DocumentScanner,
                            contentDescription = "",
                            modifier = Modifier.size(64.dp),
                            tint = StudyBuddyTheme.colorScheme.primary.copy(alpha = 0.7f)
                        )
                        Text(
                            text = "Camera Scanner",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Medium,
                            color = StudyBuddyTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "Point camera at text to scan",
                            fontSize = 14.sp,
                            color = StudyBuddyTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
            
            // Action Buttons
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Button(
                    onClick = { viewModel.scanFromCamera() },
                    enabled = !isScanning,
                    modifier = Modifier.fillMaxWidth().width(280.dp)
                ) {
                    if (isScanning) {
                        androidx.compose.material3.CircularProgressIndicator(
                            modifier = Modifier.size(20.dp),
                            color = StudyBuddyTheme.colorScheme.onPrimary
                        )
                    } else {
                        androidx.compose.foundation.layout.Row(
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                painter = androidx.compose.material.icons.Icons.Filled.CameraAlt,
                                contentDescription = ""
                            )
                            androidx.compose.foundation.layout.Spacer(modifier = Modifier.width(8.dp))
                            Text("Scan with Camera")
                        }
                    }
                }
                
                Button(
                    onClick = { viewModel.scanFromGallery() },
                    enabled = !isScanning,
                    modifier = Modifier.fillMaxWidth().width(280.dp)
                ) {
                    androidx.compose.foundation.layout.Row(
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            painter = androidx.compose.material.icons.Icons.Filled.PhotoLibrary,
                            contentDescription = ""
                        )
                        androidx.compose.foundation.layout.Spacer(modifier = Modifier.width(8.dp))
                        Text("Pick from Gallery")
                    }
                }
                
                Button(
                    onClick = { 
                        showText = !showText
                        if (showText && scannedText.isBlank()) {
                            viewModel.scanFromCamera()
                        }
                    },
                    enabled = !scannedText.isBlank() && !isScanning,
                    modifier = Modifier.fillMaxWidth().width(280.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = StudyBuddyTheme.colorScheme.secondaryContainer,
                        contentColor = StudyBuddyTheme.colorScheme.onSecondaryContainer
                    )
                ) {
                    Text(if (showText) "Hide Result" else "View Result")
                }
                
                if (scannedText.isNotBlank()) {
                    Button(
                        onClick = {
                            // TODO: Send scanned text to chat
                        },
                        modifier = Modifier.fillMaxWidth().width(280.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = StudyBuddyTheme.colorScheme.tertiaryContainer,
                            contentColor = StudyBuddyTheme.colorScheme.onTertiaryContainer
                        )
                    ) {
                        Text("Send to Chat")
                    }
                }
            }
            
            error?.let { err ->
                Card(
                    modifier = Modifier.fillMaxWidth().width(280.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = StudyBuddyTheme.colorScheme.errorContainer
                    )
                ) {
                    Text(
                        err,
                        color = StudyBuddyTheme.colorScheme.onErrorContainer,
                        modifier = Modifier.padding(16.dp)
                    )
                }
            }
            
            androidx.compose.foundation.layout.Spacer(modifier = Modifier.height(16.dp))
            
            Text(
                text = "Note: Camera integration requires CameraX + ML Kit setup",
                fontSize = 12.sp,
                color = StudyBuddyTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                textAlign = androidx.compose.ui.text.TextAlign.Center
            )
        }
    }
}