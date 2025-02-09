package com.example.mviimageeditor.ui.home.component

import android.annotation.SuppressLint
import android.content.Context
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.lerp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.text.HtmlCompat
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.example.mviimageeditor.R
import com.google.android.material.textview.MaterialTextView
import kotlin.math.absoluteValue

@SuppressLint("StringFormatMatches")
@OptIn(ExperimentalGlideComposeApi::class, ExperimentalMaterial3Api::class)
@Composable
fun ImageItem(
    item: com.example.mviimageeditor.model.CollectionModel,
    context: Context,
    onLikeImage: () -> Unit,
    onViewDetail: (String) -> Unit,
) {
    val photos = item.previewPhotos
    val pagerState = rememberPagerState(pageCount = { photos.size })
    val indicators =
        remember {
            derivedStateOf {
                pagerState.currentPage
            }
        }
    var isLike by rememberSaveable {
        mutableStateOf(item.isLiked)
    }

    val onLike: () -> Unit = {
        isLike = !isLike
        onLikeImage()
    }

    Row(
        modifier = Modifier.padding(top = 16.dp, start = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        GlideImage(
            item.user.profileImage.small,
            contentDescription = null,
            modifier =
                Modifier
                    .size(32.dp)
                    .clip(
                        CircleShape,
                    ),
        )
        Column(
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.padding(start = 10.dp),
        ) {
            Text(text = item.user.username)
            item.user.location?.let {
                Text(text = it)
            }
        }
    }

    HorizontalPager(state = pagerState, modifier = Modifier.padding(top = 4.dp)) { page ->
        GlideImage(
            model = photos[page].urls.regular,
            contentDescription = "",
            modifier =
                Modifier
                    .fillMaxWidth()
                    .height(375.dp)
                    .graphicsLayer {
                        // Calculate the absolute offset for the current page from the
                        // scroll position. We use the absolute value which allows us to mirror
                        // any effects for both directions
                        val pageOffset =
                            (
                                (pagerState.currentPage - page) +
                                    pagerState
                                        .currentPageOffsetFraction
                            ).absoluteValue

                        // We animate the alpha, between 50% and 100%
                        alpha =
                            lerp(
                                start = 0.2f,
                                stop = 1f,
                                fraction = 1f - pageOffset.coerceIn(0f, 1f),
                            )
                    }.clickable {
                        onViewDetail(photos[page].urls.regular)
                    },
            contentScale = ContentScale.Crop,
        )
    }
    Row(
        Modifier
            .wrapContentHeight()
            .fillMaxWidth()
            .padding(bottom = 8.dp),
        horizontalArrangement = Arrangement.Center,
    ) {
        photos.forEachIndexed { index, photo ->
            val color by animateColorAsState(
                if (index == indicators.value) Color.DarkGray else Color.LightGray,
                label = "",
            )
            Box(
                modifier =
                    Modifier
                        .padding(2.dp)
                        .clip(CircleShape)
                        .background(color)
                        .size(8.dp),
            )
        }
    }

    Row(modifier = Modifier.padding(start = 8.dp)) {
        Icon(
            imageVector = if (!isLike) Icons.Default.FavoriteBorder else Icons.Default.Favorite,
            contentDescription = "fav",
            modifier =
                Modifier.clickable {
                    onLike()
                },
        )

        AndroidView(
            modifier = Modifier,
            factory = { MaterialTextView(it) },
            update = {
                it.text =
                    HtmlCompat.fromHtml(
                        context.getString(
                            R.string.liked_by_others,
                            item.coverPhoto.likes,
                        ),
                        HtmlCompat.FROM_HTML_MODE_LEGACY,
                    )
            },
        )
    }
}
