package com.example.mviimageeditor.custom

import android.graphics.drawable.Drawable
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun GlideImageCustom(
    url: String?,
    description: String?,
    event: () -> Unit? = {},
) {
    val visibleState = remember { MutableTransitionState(false) }
    AnimatedVisibility(visibleState = visibleState) {
        GlideImage(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .clickable {
                        event.invoke()
                    },
            model = url,
            contentDescription = description,
            contentScale = ContentScale.Crop,
            requestBuilderTransform = { requestBuilder ->
                requestBuilder.listener(
                    object : RequestListener<Drawable> {
                        override fun onResourceReady(
                            resource: Drawable,
                            model: Any,
                            target: com.bumptech.glide.request.target.Target<Drawable>?,
                            dataSource: DataSource,
                            isFirstResource: Boolean,
                        ): Boolean {
                            visibleState.targetState = true
                            return false
                        }

                        override fun onLoadFailed(
                            e: GlideException?,
                            model: Any?,
                            target: Target<Drawable>,
                            isFirstResource: Boolean,
                        ): Boolean {
                            visibleState.targetState = false
                            return false
                        }
                    },
                )
            },
        )
    }
}
