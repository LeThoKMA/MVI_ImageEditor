package com.example.mviimageeditor.ui.detail.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.size
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp

@Suppress("ktlint:standard:function-naming")
@Composable
fun ItemOption(
    icon: Int,
    color: Color = Color.Black,
    onClick: () -> Unit,
) {
    IconButton(
        onClick = {
            onClick.invoke()
        },
    ) {
        Image(
            painter = painterResource(id = icon),
            contentDescription = "",
            modifier =
                Modifier.size(40.dp),
            colorFilter = ColorFilter.tint(color, blendMode = BlendMode.Difference),
        )
    }
}
