package com.example.ocr_v3.presentation.components

import android.content.ClipData
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ClipEntry
import androidx.compose.ui.platform.LocalClipboard
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ocr_v3.domain.model.ScanType
import com.example.ocr_v3.ui.icons.AppIcons
import com.example.ocr_v3.ui.theme.PrimaryPurple
import com.example.ocr_v3.ui.theme.TextDark
import com.example.ocr_v3.ui.theme.TextGray
import kotlinx.coroutines.launch

@Composable
fun InfoCard(
    label: String,
    value: String,
    onValueChange: ((String) -> Unit)? = null,
    isReadOnly: Boolean = false
) {
    val clipboard = LocalClipboard.current
    val scope = rememberCoroutineScope()
    
    Surface(
        color = Color.White,
        shape = RoundedCornerShape(16.dp),
        shadowElevation = 1.dp,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .padding(horizontal = 16.dp, vertical = 8.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = label,
                    fontSize = 12.sp,
                    color = TextGray,
                    fontWeight = FontWeight.Medium
                )
                if (isReadOnly || onValueChange == null) {
                    Text(
                        text = value.ifBlank { "—" },
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = TextDark,
                        modifier = Modifier.padding(vertical = 12.dp, horizontal = 16.dp)
                    )
                } else {
                    OutlinedTextField(
                        value = value,
                        onValueChange = onValueChange,
                        textStyle = TextStyle(
                            fontSize = 16.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = TextDark
                        ),
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color.Transparent,
                            unfocusedBorderColor = Color.Transparent,
                            cursorColor = PrimaryPurple
                        ),
                        singleLine = true
                    )
                }
            }

            IconButton(
                onClick = {
                    if (value.isNotEmpty()) {
                        scope.launch {
                            val clipData = ClipData.newPlainText(label, value)
                            clipboard.setClipEntry(ClipEntry(clipData))
                        }
                    }
                },
                modifier = Modifier.size(40.dp)
            ) {
                Icon(
                    imageVector = AppIcons.ContentCopy,
                    contentDescription = "Copy $label",
                    tint = PrimaryPurple.copy(alpha = 0.7f),
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}

@Composable
fun ScanTypeTag(scanType: ScanType) {
    val backgroundColor = if (scanType == ScanType.NFC) Color(0xFFE3F2FD) else Color(0xFFF3E5F5)
    val textColor = if (scanType == ScanType.NFC) Color(0xFF1976D2) else Color(0xFF7B1FA2)
    
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(backgroundColor)
            .padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
        Text(
            text = scanType.name,
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            color = textColor
        )
    }
}
