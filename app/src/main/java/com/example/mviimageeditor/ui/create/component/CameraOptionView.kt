package com.example.mviimageeditor.ui.create.component

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.mviimageeditor.R

@Composable
fun CameraOptionView(
    modifier: Modifier,
    onCapture: () -> Unit = {},
    onFlash: () -> Unit = {},
    onSwitchCamera: () -> Unit = {},
) {
    Row(
        modifier
            .padding(16.dp)
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceAround,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        IconButton(
            onClick = onSwitchCamera,
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_change_camera),
                contentDescription = "changeCamera",
                tint = Color.White,
            )
        }

        CaptureView(onClick = {
            onCapture.invoke()
        })

        IconButton(
            onClick = onFlash,
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_light),
                contentDescription = "flash",
                tint = Color.White,
            )
        }
    }
}

@Composable
fun CaptureView(onClick: () -> Unit = {}) {
    Canvas(
        modifier =
        Modifier
            .size(100.dp)
            .clickable(enabled = true, onClick = {
                onClick.invoke()
            }),
    ) {
        // Gradient từ màu tối đến trắng
        val gradient = Brush.radialGradient(
            colors = listOf(Color(0xFFAAAAAA), Color.White),
            center = center,
            radius = 100f
        )

        // Vẽ vòng tròn với gradient
        drawCircle(
            brush = gradient,
            radius = 100f,
        )

        drawCircle(
            color = Color.White, // Màu viền
            radius = 100f,
            style = Stroke(width = 5f), // Độ dày viền
        )
    }
}