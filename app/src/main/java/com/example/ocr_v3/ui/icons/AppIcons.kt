package com.example.ocr_v3.ui.icons

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

/**
 * Custom icon definitions to avoid the heavy material-icons-extended dependency.
 */
object AppIcons {

    val PhotoCamera: ImageVector
        get() = ImageVector.Builder(
            name = "PhotoCamera",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 24f,
            viewportHeight = 24f
        ).path(
            fill = SolidColor(Color.Black),
            strokeLineWidth = 0f
        ) {
            moveTo(12f, 8f)
            curveToRelative(-2.21f, 0f, -4f, 1.79f, -4f, 4f)
            reflectiveCurveToRelative(1.79f, 4f, 4f, 4f)
            reflectiveCurveToRelative(4f, -1.79f, 4f, -4f)
            reflectiveCurveToRelative(-1.79f, -4f, -4f, -4f)
            close()
            moveTo(20f, 4f)
            horizontalLineToRelative(-3.17f)
            lineTo(15f, 2f)
            lineTo(9f, 2f)
            lineTo(7.17f, 4f)
            lineTo(4f, 4f)
            curveToRelative(-1.1f, 0f, -2f, 0.9f, -2f, 2f)
            verticalLineToRelative(12f)
            curveToRelative(0f, 1.1f, 0.9f, 2f, 2f, 2f)
            horizontalLineToRelative(16f)
            curveToRelative(1.1f, 0f, 2f, -0.9f, 2f, -2f)
            lineTo(22f, 6f)
            curveToRelative(0f, -1.1f, -0.9f, -2f, -2f, -2f)
            close()
            moveTo(12f, 18f)
            curveToRelative(-3.31f, 0f, -6f, -2.69f, -6f, -6f)
            reflectiveCurveToRelative(2.69f, -6f, 6f, -6f)
            reflectiveCurveToRelative(6f, 2.69f, 6f, 6f)
            reflectiveCurveToRelative(-2.69f, 6f, -6f, 6f)
            close()
        }.build()

    val Delete: ImageVector
        get() = ImageVector.Builder(
            name = "Delete",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 24f,
            viewportHeight = 24f
        ).path(fill = SolidColor(Color.Black)) {
            moveTo(6f, 19f)
            curveToRelative(0f, 1.1f, 0.9f, 2f, 2f, 2f)
            horizontalLineToRelative(8f)
            curveToRelative(1.1f, 0f, 2f, -0.9f, 2f, -2f)
            verticalLineTo(7f)
            horizontalLineTo(6f)
            verticalLineToRelative(12f)
            close()
            moveTo(19f, 4f)
            horizontalLineToRelative(-3.5f)
            lineToRelative(-1f, -1f)
            horizontalLineToRelative(-5f)
            lineToRelative(-1f, 1f)
            horizontalLineTo(5f)
            verticalLineToRelative(2f)
            horizontalLineToRelative(14f)
            verticalLineTo(4f)
            close()
        }.build()

    val List: ImageVector
        get() = ImageVector.Builder(
            name = "List",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 24f,
            viewportHeight = 24f
        ).path(fill = SolidColor(Color.Black)) {
            moveTo(3f, 13f)
            horizontalLineToRelative(2f)
            verticalLineToRelative(-2f)
            horizontalLineTo(3f)
            verticalLineToRelative(2f)
            close()
            moveTo(3f, 17f)
            horizontalLineToRelative(2f)
            verticalLineToRelative(-2f)
            horizontalLineTo(3f)
            verticalLineToRelative(2f)
            close()
            moveTo(3f, 9f)
            horizontalLineToRelative(2f)
            verticalLineTo(7f)
            horizontalLineTo(3f)
            verticalLineToRelative(2f)
            close()
            moveTo(7f, 13f)
            horizontalLineToRelative(14f)
            verticalLineToRelative(-2f)
            horizontalLineTo(7f)
            verticalLineToRelative(2f)
            close()
            moveTo(7f, 17f)
            horizontalLineToRelative(14f)
            verticalLineToRelative(-2f)
            horizontalLineTo(7f)
            verticalLineToRelative(2f)
            close()
            moveTo(7f, 7f)
            verticalLineToRelative(2f)
            horizontalLineToRelative(14f)
            verticalLineTo(7f)
            horizontalLineTo(7f)
            close()
        }.build()

    val ContentCopy: ImageVector
        get() = ImageVector.Builder(
            name = "ContentCopy",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 24f,
            viewportHeight = 24f
        ).path(fill = SolidColor(Color.Black)) {
            moveTo(16f, 1f)
            horizontalLineTo(4f)
            curveToRelative(-1.1f, 0f, -2f, 0.9f, -2f, 2f)
            verticalLineToRelative(14f)
            horizontalLineToRelative(2f)
            verticalLineTo(3f)
            horizontalLineToRelative(12f)
            verticalLineTo(1f)
            close()
            moveTo(19f, 5f)
            lineTo(19f, 5f)
            horizontalLineTo(8f)
            curveToRelative(-1.1f, 0f, -2f, 0.9f, -2f, 2f)
            verticalLineToRelative(14f)
            curveToRelative(0f, 1.1f, 0.9f, 2f, 2f, 2f)
            horizontalLineToRelative(11f)
            curveToRelative(1.1f, 0f, 2f, -0.9f, 2f, -2f)
            verticalLineTo(7f)
            curveToRelative(0f, -1.1f, -0.9f, -2f, -2f, -2f)
            close()
            moveTo(19f, 21f)
            horizontalLineTo(8f)
            verticalLineTo(7f)
            horizontalLineToRelative(11f)
            verticalLineTo(21f)
            close()
        }.build()

    val RotateRight: ImageVector
        get() = ImageVector.Builder(
            name = "RotateRight",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 24f,
            viewportHeight = 24f
        ).path(fill = SolidColor(Color.Black)) {
            moveTo(15.55f, 5.55f)
            lineTo(11f, 1f)
            verticalLineToRelative(3.07f)
            curveTo(7.06f, 4.56f, 4f, 7.92f, 4f, 12f)
            reflectiveCurveToRelative(3.06f, 7.44f, 7f, 7.93f)
            verticalLineToRelative(-2.02f)
            curveToRelative(-2.84f, -0.48f, -5f, -2.94f, -5f, -5.91f)
            reflectiveCurveToRelative(2.16f, -5.43f, 5f, -5.91f)
            verticalLineTo(10f)
            lineTo(15.55f, 5.55f)
            close()
            moveTo(19.93f, 11f)
            curveToRelative(-0.17f, -1.39f, -0.72f, -2.73f, -1.62f, -3.89f)
            lineToRelative(-1.42f, 1.42f)
            curveToRelative(0.54f, 0.75f, 0.88f, 1.6f, 1.01f, 2.47f)
            horizontalLineTo(19.93f)
            close()
            moveTo(15.89f, 15.48f)
            lineToRelative(1.42f, 1.42f)
            curveToRelative(0.9f, -1.16f, 1.45f, -2.5f, 1.62f, -3.89f)
            horizontalLineToRelative(-2.02f)
            curveTo(16.77f, 13.88f, 16.43f, 14.73f, 15.89f, 15.48f)
            close()
        }.build()

    val Nfc: ImageVector
        get() = ImageVector.Builder(
            name = "Nfc",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 24f,
            viewportHeight = 24f
        ).path(fill = SolidColor(Color.Black)) {
            moveTo(4f, 20f)
            horizontalLineToRelative(16f)
            curveToRelative(1.1f, 0f, 2f, -0.9f, 2f, -2f)
            verticalLineTo(6f)
            curveToRelative(0f, -1.1f, -0.9f, -2f, -2f, -2f)
            horizontalLineTo(4f)
            curveToRelative(-1.1f, 0f, -2f, 0.9f, -2f, 2f)
            verticalLineToRelative(12f)
            curveToRelative(0f, 1.1f, 0.9f, 2f, 2f, 2f)
            close()
            moveTo(4f, 6f)
            horizontalLineToRelative(16f)
            verticalLineToRelative(12f)
            horizontalLineTo(4f)
            verticalLineTo(6f)
            close()
            moveTo(18f, 8f)
            horizontalLineToRelative(-2f)
            verticalLineToRelative(8f)
            horizontalLineToRelative(2f)
            verticalLineTo(8f)
            close()
            moveTo(9.25f, 8f)
            curveToRelative(-1.1f, 0f, -2f, 0.9f, -2f, 2f)
            verticalLineToRelative(4f)
            curveToRelative(0f, 1.1f, 0.9f, 2f, 2f, 2f)
            horizontalLineToRelative(3.5f)
            curveToRelative(1.1f, 0f, 2f, -0.9f, 2f, -2f)
            verticalLineToRelative(-4f)
            curveToRelative(0f, -1.1f, -0.9f, -2f, -2f, -2f)
            horizontalLineToRelative(-3.5f)
            close()
            moveTo(12.75f, 14f)
            horizontalLineToRelative(-3.5f)
            verticalLineToRelative(-4f)
            horizontalLineToRelative(3.5f)
            verticalLineToRelative(4f)
            close()
        }.build()
}
