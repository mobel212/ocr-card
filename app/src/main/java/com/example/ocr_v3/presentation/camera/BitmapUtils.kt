package com.example.ocr_v3.presentation.camera

// BitmapUtils.kt
import android.graphics.Bitmap
import android.graphics.Matrix
import androidx.compose.ui.geometry.Offset

fun rotateBitmap(bitmap: Bitmap, degrees: Float): Bitmap {
    val matrix = Matrix()
    matrix.postRotate(degrees)
    return Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
}

fun cropBitmap(bitmap: Bitmap, ratio: Float = 1.59f): Bitmap {
    val width = bitmap.width
    val height = bitmap.height

    var cropWidth = width
    var cropHeight = height

    if (width >= height * ratio) {
        cropWidth = (height * ratio).toInt()
        cropHeight = height
    } else {
        cropWidth = width
        cropHeight = (width / ratio).toInt()
    }

    val left = (width - cropWidth) / 2
    val top = (height - cropHeight) / 2

    return Bitmap.createBitmap(bitmap, left, top, cropWidth, cropHeight)
}

fun cropZoomedBitmap(
    original: Bitmap,
    scale: Float,
    offset: Offset,
    ratio: Float = 1.59f
): Bitmap {
    val w = original.width.toFloat()
    val h = original.height.toFloat()

    // Visible area in original coordinates
    val visW = w / scale
    val visH = h / scale

    // Center of visible area
    val cx = w / 2f - offset.x / scale
    val cy = h / 2f - offset.y / scale

    // Calculate crop with ratio
    var cropW = visW
    var cropH = visH

    if (cropW >= cropH * ratio) {
        cropW = cropH * ratio
    } else {
        cropH = cropW / ratio
    }

    val left = (cx - cropW / 2f).coerceIn(0f, w - cropW)
    val top = (cy - cropH / 2f).coerceIn(0f, h - cropH)

    return Bitmap.createBitmap(
        original,
        left.toInt(),
        top.toInt(),
        cropW.toInt(),
        cropH.toInt()
    )
}