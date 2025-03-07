package com.example.mviimageeditor.ui.home

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemKey
import com.example.mviimageeditor.model.CollectionModel
import com.example.mviimageeditor.nav.LocalAppNavigator
import com.example.mviimageeditor.nav.Screen
import com.example.mviimageeditor.ui.home.component.ImageItem
import com.example.mviimageeditor.use
import com.example.mviimageeditor.utils.reachedBottom
import kotlinx.coroutines.flow.collectLatest
import org.koin.androidx.compose.koinViewModel

@Suppress("ktlint:standard:function-naming")
@Composable
fun HomeScreen(
    innerPaddingValues: PaddingValues,
    homeViewModel: HomeViewModel = koinViewModel(),
) {
    val navigator = LocalAppNavigator.current
    val (state, event, effect) = use(homeViewModel)
    val pagingState = homeViewModel.pagingDataFlow.collectAsLazyPagingItems()
    LaunchedEffect(key1 = Unit) {
        effect.collectLatest {
            when (it) {
                is HomeContract.Effect.OnViewDetail -> {
                    navigator.navigate(Screen.Details(it.imageUrl))
                }

                else -> {
                }
            }
        }
    }
    HomeView(
        state,
        event,
        pagingState,
        modifier = Modifier.padding(paddingValues = innerPaddingValues)
    )
}

@Composable
fun HomeView(
    state: HomeContract.State,
    event: (HomeContract.Event) -> Unit,
    pagingData: LazyPagingItems<CollectionModel>,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    val lisState = rememberLazyListState(pagingData.itemCount)
    val reachedBottom by remember {
        derivedStateOf {
            lisState.reachedBottom()
        }
    }
    LaunchedEffect(key1 = reachedBottom) {
        if (reachedBottom) {
            event(HomeContract.Event.OnLoadMore)
        }
    }

    LazyColumn(
        state = lisState,
        modifier = modifier,
    ) {
        items(pagingData.itemCount, key = pagingData.itemKey { it.id }) {
            pagingData[it]?.let { it1 ->
                ImageItem(item = it1, context = context, onLikeImage = {
                    event.invoke(HomeContract.Event.OnLikeImage(it1))
                }, onViewDetail = {
                    event.invoke(HomeContract.Event.OnViewDetail(it))
                })
            }
        }
    }
    pagingData.apply {
        when {
            loadState.refresh is LoadState.Loading -> {
            }

            loadState.append is LoadState.Loading -> {
            }

            loadState.append is LoadState.Error -> {
            }
        }
    }
}
