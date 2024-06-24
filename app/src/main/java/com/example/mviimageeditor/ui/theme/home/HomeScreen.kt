package com.example.mviimageeditor.ui.theme.home

import android.annotation.SuppressLint
import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.toMutableStateList
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.text.HtmlCompat
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.example.mviimageeditor.ui.theme.model.CollectionModel
import com.example.mviimageeditor.R
import com.example.mviimageeditor.nav.BaseView
import com.example.mviimageeditor.use
import com.google.android.material.textview.MaterialTextView
import org.koin.androidx.compose.koinViewModel

@Composable
fun HomeScreen(modifier: Modifier = Modifier, homeViewModel: HomeViewModel = koinViewModel()) {
    val context = LocalContext.current
    val (state, event, effect) = use(homeViewModel)
    BaseView(childView = {
        HomeView(state, event, context)
    }, homeViewModel)
}

@Composable
fun HomeView(
    state: HomeContract.State,
    event: (HomeContract.Event) -> Unit,
    context: Context
) {
    val images = remember {
        state.images?.toMutableStateList() ?: emptyList()
    }
    LazyColumn(modifier = Modifier.fillMaxSize()) {
        items(state.images ?: emptyList(), key = { item ->
            item.id
        }) {
            ImageItem(item = it, context = context)
        }
    }
}

@SuppressLint("StringFormatMatches")
@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun ImageItem(item: CollectionModel, context: Context) {
    val photos = item.previewPhotos
    val pagerState = rememberPagerState(pageCount = { photos.size })
    Row() {
        GlideImage(
            item.user.profileImage.small,
            contentDescription = null,
            modifier = Modifier
                .size(32.dp)
                .clip(
                    CircleShape
                )
        )
        Column(
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.padding(start = 10.dp)
        ) {
            Text(text = item.user.username)
            Text(text = item.user.location.toString())
        }
    }
    HorizontalPager(state = pagerState) { page ->
        GlideImage(
            model = photos[page].urls.regular,
            contentDescription = "",
            modifier = Modifier
                .fillMaxWidth()
                .height(375.dp),
            contentScale = ContentScale.Crop
        )
    }
    Row(
        Modifier
            .wrapContentHeight()
            .fillMaxWidth()
            .padding(bottom = 8.dp),
        horizontalArrangement = Arrangement.Center
    ) {
        repeat(pagerState.pageCount) { iteration ->
            val color = if (pagerState.currentPage == iteration) Color.DarkGray else Color.LightGray
            Box(
                modifier = Modifier
                    .padding(2.dp)
                    .clip(CircleShape)
                    .background(color)
                    .size(8.dp)
            )
        }
    }

    Row {
        Icon(imageVector = Icons.Default.FavoriteBorder, contentDescription = "fav")

        AndroidView(
            modifier = Modifier,
            factory = { MaterialTextView(it) },
            update = {
                it.text = HtmlCompat.fromHtml(
                    context.getString(
                        R.string.liked_by_others,
                        item.coverPhoto.likes,
                    ),
                    HtmlCompat.FROM_HTML_MODE_LEGACY,
                )
            }
        )
    }

}