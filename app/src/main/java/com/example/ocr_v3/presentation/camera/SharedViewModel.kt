package com.example.ocr_v3.presentation.camera

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import dagger.hilt.android.lifecycle.HiltViewModel
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import javax.inject.Inject

@HiltViewModel
class SharedViewModel @Inject constructor() : ViewModel() {
    // Initialize with a placeholder bitmap
    private val _photoBitmap = MutableStateFlow(createPlaceholderBitmap())
    val photoBitmap: StateFlow<Bitmap> = _photoBitmap.asStateFlow()

    fun setPhotoBitmap(bitmap: Bitmap) {
        _photoBitmap.value = bitmap
    }
}

// Create a placeholder bitmap
fun createPlaceholderBitmap(): Bitmap {
    return Bitmap.createBitmap(200, 200, Bitmap.Config.ARGB_8888).apply {
        val canvas = Canvas(this)
        canvas.drawColor(Color.LTGRAY)
        val paint = android.graphics.Paint().apply {
            color = Color.DKGRAY
            textSize = 30f
            textAlign = Paint.Align.CENTER
        }
        canvas.drawText("No Photo", 100f, 110f, paint)
    }
}