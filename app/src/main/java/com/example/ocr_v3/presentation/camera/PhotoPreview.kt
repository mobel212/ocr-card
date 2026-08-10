package com.example.ocr_v3.presentation.camera

import android.graphics.Bitmap
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.ocr_v3.presentation.navigation.Routes
import com.example.ocr_v3.presentation.scanner.ScannerViewModel
import com.example.ocr_v3.ui.icons.AppIcons
import com.example.ocr_v3.ui.theme.BackgroundLight
import com.example.ocr_v3.ui.theme.PrimaryPurple

@Composable
fun PhotoPreview(
    onPhotoAccepted: (Bitmap) -> Unit,
    navController: NavController,
    sharedViewModel: SharedViewModel,
    scannerViewModel: ScannerViewModel,
) {
    val myBitmap by sharedViewModel.photoBitmap.collectAsState()
    val scannerState = scannerViewModel.theState

    // Image state
    var scale by remember { mutableFloatStateOf(1f) }
    var offset by remember { mutableStateOf(Offset.Zero) }
    var rotation by remember { mutableFloatStateOf(0f) }

    val bitmap = myBitmap

    Box(modifier = Modifier.fillMaxSize().background(BackgroundLight)) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Zoomable & Rotatable image with crop overlay
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(16.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .background(Color.Black)
            ) {
                // Zoomable & Rotatable image
                ZoomableImage(
                    bitmap = bitmap,
                    modifier = Modifier
                        .fillMaxSize()
                        .graphicsLayer(rotationZ = rotation),
                    onTransform = { newScale, newOffset ->
                        scale = newScale
                        offset = newOffset
                    }
                )

                // Rotation Button (top right)
                IconButton(
                    onClick = { rotation += 90f },
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(16.dp)
                        .background(Color.Black.copy(alpha = 0.5f), CircleShape)
                ) {
                    Icon(
                        imageVector = AppIcons.RotateRight,
                        contentDescription = "Rotate",
                        tint = Color.White
                    )
                }

                // Crop overlay
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val ratio = 1.59f
                    val canvasWidth = size.width
                    val canvasHeight = size.height

                    var cropWidth = canvasWidth
                    var cropHeight = canvasHeight

                    if (cropWidth >= (cropHeight * ratio)) {
                        cropWidth = cropHeight * ratio
                    } else {
                        cropHeight = cropWidth / ratio
                    }

                    val left = (canvasWidth - cropWidth) / 2f
                    val top = (canvasHeight - cropHeight) / 2f

                    val dimColor = Color.Black.copy(alpha = 0.5f)
                    drawRect(dimColor, topLeft = Offset(0f, 0f), size = Size(canvasWidth, top))
                    drawRect(dimColor, topLeft = Offset(0f, top + cropHeight), size = Size(canvasWidth, canvasHeight - top - cropHeight))
                    drawRect(dimColor, topLeft = Offset(0f, top), size = Size(left, cropHeight))
                    drawRect(dimColor, topLeft = Offset(left + cropWidth, top), size = Size(canvasWidth - left - cropWidth, cropHeight))

                    drawRect(
                        color = PrimaryPurple,
                        topLeft = Offset(left, top),
                        size = Size(cropWidth, cropHeight),
                        style = Stroke(width = 6f, cap = StrokeCap.Round)
                    )

                    drawContext.canvas.nativeCanvas.apply {
                        val paint = android.graphics.Paint().apply {
                            color = android.graphics.Color.WHITE
                            textSize = 28f
                            textAlign = android.graphics.Paint.Align.CENTER
                            isFakeBoldText = true
                        }
                        drawText(
                            "Ensure the card is in this area",
                            canvasWidth / 2,
                            top - 16f,
                            paint
                        )
                    }
                }
            }

            // Buttons
            Row(
                modifier = Modifier
                    .padding(30.dp)
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Button(
                    onClick = {
                        navController.navigate(Routes.CameraScreen)
                    },
                    enabled = !scannerState.isLoading,
                    modifier = Modifier.weight(1f).height(56.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.White,
                        contentColor = PrimaryPurple
                    ),
                    shape = RoundedCornerShape(16.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, PrimaryPurple.copy(alpha = 0.2f))
                ) {
                    Text("Retry")
                }

                Button(
                    onClick = {
                        // Apply rotation first
                        val rotated = if (rotation % 360 != 0f) {
                            rotateBitmap(bitmap, rotation % 360)
                        } else {
                            bitmap
                        }
                        
                        // Crop the zoomed and rotated image
                        val cropped = cropZoomedBitmap(
                            original = rotated,
                            scale = scale,
                            offset = offset,
                            ratio = 1.59f
                        )
                        onPhotoAccepted(cropped)
                    },
                    enabled = !scannerState.isLoading,
                    modifier = Modifier.weight(1f).height(56.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryPurple),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Text("Scan")
                }
            }
        }

        if (scannerState.isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.3f)),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = PrimaryPurple)
            }
        }
    }
}
