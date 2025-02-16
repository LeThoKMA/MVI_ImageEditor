package com.example.mviimageeditor.ui.detail.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun ImageFilterList(
    modifier: Modifier = Modifier,
    imageSource: Any?,
    colorFilters: List<ColorFilter>,
    onFilterSelected: (ColorFilter) -> Unit,
) {
    val imageModifier =
        Modifier
            .size(128.dp)
            .clip(RoundedCornerShape(16.dp))

    LazyRow(
        modifier
            .padding(bottom = 16.dp)
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        items(colorFilters) {
            if (imageSource is BitmapPainter) {
                Image(
                    painter = imageSource,
                    contentDescription = "",
                    colorFilter = it,
                    contentScale = ContentScale.Crop,
                    modifier =
                        imageModifier.clickable {
                            onFilterSelected.invoke(it)
                        },
                )
            } else {
                Box(
                    modifier =
                        imageModifier.clickable {
                            onFilterSelected(it)
                        },
                ) {
                    GlideImage(
                        model = imageSource,
                        contentDescription = "",
                        colorFilter = it,
                        contentScale = ContentScale.Crop,
                    )
                }
            }
        }
    }
}
