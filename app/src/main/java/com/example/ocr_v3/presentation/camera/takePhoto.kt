package com.example.ocr_v3.presentation.camera

import android.content.Context
import android.graphics.Bitmap
import android.util.Log
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.ImageProxy
import androidx.camera.view.LifecycleCameraController
import androidx.core.content.ContextCompat

fun takePhoto(controller : LifecycleCameraController ,
                      contextCam: Context,
              onPhotoTaken : (Bitmap) -> Unit
                      ) {

    Log.e("er","error 1 in take photo")

    controller.takePicture(
        ContextCompat.getMainExecutor(contextCam) ,
        object : ImageCapture.OnImageCapturedCallback() {
            override fun onCaptureSuccess(image: ImageProxy) {
                super.onCaptureSuccess(image)
                val bitmap = image.toBitmap()
                image.close()
                onPhotoTaken(bitmap)
            }

            override fun onError(exception: ImageCaptureException) {
                super.onError(exception)
                Log.e("Camera", " problem in taking the picture")
            }

        }
    )


}