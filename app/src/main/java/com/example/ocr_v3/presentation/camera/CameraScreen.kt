package com.example.ocr_v3.presentation.camera

import android.content.Context
import androidx.camera.view.CameraController
import androidx.camera.view.LifecycleCameraController
import androidx.camera.view.PreviewView
import androidx.camera.view.PreviewView.ImplementationMode
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlexDirection.Companion.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.navigation.NavController
import com.example.ocr_v3.presentation.Routes
import com.example.ocr_v3.presentation.scanner.ScannerViewModel

@Composable
fun CameraPreview(
    controller: LifecycleCameraController,
    modifier: Modifier
) {
    AndroidView(
        factory = { context ->
            PreviewView(context).apply {
                this.controller = controller
                implementationMode = ImplementationMode.COMPATIBLE
            }
        },
        modifier = modifier
    )
}

@Composable
fun CameraScreen(
    scannerViewModel: ScannerViewModel,
    navController: NavController
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    val controller = remember {
        LifecycleCameraController(context.applicationContext).apply {
            setEnabledUseCases(CameraController.IMAGE_CAPTURE)
        }
    }

    LaunchedEffect(lifecycleOwner) {
        controller.bindToLifecycle(lifecycleOwner)
    }

    Box(modifier = Modifier.fillMaxSize()) {

        /* ── 0. Camera feed ── */
        CameraPreview(
            controller = controller,
            modifier = Modifier.fillMaxSize()
        )

        /* ── 1. Dim overlay + corner brackets ── */
        MrzFrameOverlay(modifier = Modifier.fillMaxSize())

        /* ── 2. Back arrow (top-left) ── */
        IconButton(
            onClick = { navController.popBackStack() },
            modifier = Modifier
                .statusBarsPadding()
                .padding(16.dp)
                .size(48.dp)
                .background(Color.Black.copy(alpha = 0.4f), CircleShape)
        ) {
            Icon(
                imageVector = Icons.Default.ArrowBack,
                contentDescription = "Back",
                tint = Color.White
            )
        }

        /* ── 3. Instruction text (above the frame) ── */
        Column(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .statusBarsPadding()
                .padding(top = 100.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Scan the back of your card",
                color = Color.White,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = "Align the MRZ code inside the frame",
                color = Color.White.copy(alpha = 0.85f),
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Center
            )
        }

        /* ── 4. Capture button (bottom-center) ── */
        IconButton(
            onClick = {
                takePhoto(
                    controller = controller,
                    contextCam = context,
                    onPhotoTaken = { picBitmap ->
                        scannerViewModel.onPhotoTaken(picBitmap)
                        navController.navigate(Routes.ResultScreen)
                    }
                )
            },
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .navigationBarsPadding()
                .padding(bottom = 40.dp)
                .size(72.dp)
                .clip(CircleShape)
                .background(Color.White)
        ) {
            Icon(
                imageVector = Icons.Default.PhotoCamera,
                contentDescription = "Capture",
                tint = Color(0xFF2563EB),
                modifier = Modifier.size(32.dp)
            )
        }
    }
}
    /* ───────────────────────────────────────────────
   Overlay: darkens everything except the card area
   and draws four corner brackets.
   ─────────────────────────────────────────────── */
    @Composable
    fun MrzFrameOverlay(modifier: Modifier = Modifier) {
        val bracketColor = Color(0xFF3B82F6)
        val overlayColor = Color.Black.copy(alpha = 0.55f)


        Canvas(modifier = modifier) {
            // Card frame size (centered)
            val cardW = size.width * 0.88f
            val cardH = cardW / 1.586f          // ISO 7810 ID-1 aspect ratio
            val left = (size.width - cardW) / 2
            val top = (size.height - cardH) / 2
            val right = left + cardW
            val bottom = top + cardH

            val cornerLen = 36.dp.toPx()
            val stroke = 4.dp.toPx()

            // 1. Dim the outside
            drawRect(overlayColor, topLeft = Offset(0f, 0f), size = Size(size.width, top))
            drawRect(overlayColor, topLeft = Offset(0f, bottom), size = Size(size.width, size.height - bottom))
            drawRect(overlayColor, topLeft = Offset(0f, top), size = Size(left, cardH))
            drawRect(overlayColor, topLeft = Offset(right, top), size = Size(size.width - right, cardH))

            // 2. Corner brackets only (no full border)
            val cap = StrokeCap.Round
            // Top-Left
            drawLine(bracketColor, Offset(left, top + cornerLen), Offset(left, top), stroke, cap)
            drawLine(bracketColor, Offset(left, top), Offset(left + cornerLen, top), stroke, cap)
            // Top-Right
            drawLine(bracketColor, Offset(right, top + cornerLen), Offset(right, top), stroke, cap)
            drawLine(bracketColor, Offset(right, top), Offset(right - cornerLen, top), stroke, cap)
            // Bottom-Left
            drawLine(bracketColor, Offset(left, bottom - cornerLen), Offset(left, bottom), stroke, cap)
            drawLine(bracketColor, Offset(left, bottom), Offset(left + cornerLen, bottom), stroke, cap)
            // Bottom-Right
            drawLine(bracketColor, Offset(right, bottom - cornerLen), Offset(right, bottom), stroke, cap)
            drawLine(bracketColor, Offset(right, bottom), Offset(right - cornerLen, bottom), stroke, cap)
        }
    }

