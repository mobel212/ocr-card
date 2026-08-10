package com.example.ocr_v3.presentation.camera

// ZoomableImage.kt
import android.graphics.Bitmap
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
@Composable
fun ZoomableImage(
    bitmap: Bitmap,
    modifier: Modifier = Modifier,
    onTransform: (Float, Offset) -> Unit = { _, _ -> }
) {
    var scale by remember { mutableStateOf(1f) }
    var offset by remember { mutableStateOf(Offset.Zero) }

    Box(
        modifier = modifier
            .fillMaxSize()
            .pointerInput(Unit) {
                detectTransformGestures { _, pan, zoom, _ ->
                    scale = (scale * zoom).coerceIn(1f, 5f)
                    offset += pan
                    onTransform(scale, offset)
                }
            }
    ) {
        androidx.compose.foundation.Canvas(modifier = Modifier.fillMaxSize()) {
            val image = bitmap.asImageBitmap()
            val canvasWidth = size.width
            val canvasHeight = size.height

            val imageAspect = bitmap.width.toFloat() / bitmap.height.toFloat()
            val canvasAspect = canvasWidth / canvasHeight

            var drawWidth = canvasWidth
            var drawHeight = canvasHeight

            if (imageAspect > canvasAspect) {
                drawHeight = canvasWidth / imageAspect
            } else {
                drawWidth = canvasHeight * imageAspect
            }

            val scaledWidth = drawWidth * scale
            val scaledHeight = drawHeight * scale

            val left = (canvasWidth - scaledWidth) / 2f + offset.x
            val top = (canvasHeight - scaledHeight) / 2f + offset.y

            drawImage(
                image = image,
                dstSize = androidx.compose.ui.unit.IntSize(
                    scaledWidth.toInt(),
                    scaledHeight.toInt()
                ),
                dstOffset = androidx.compose.ui.unit.IntOffset(
                    left.toInt(),
                    top.toInt()
                )
            )
        }
    }
}