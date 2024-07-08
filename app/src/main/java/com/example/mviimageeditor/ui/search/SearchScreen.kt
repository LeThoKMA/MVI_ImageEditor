package com.example.mviimageeditor.ui.search

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.rememberScrollableState
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.foundation.lazy.staggeredgrid.rememberLazyStaggeredGridState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.SearchBar
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.example.mviimageeditor.nav.BaseView
import com.example.mviimageeditor.nav.LocalAppNavigator
import com.example.mviimageeditor.nav.Screen
import com.example.mviimageeditor.use
import kotlinx.coroutines.flow.collectLatest
import org.koin.androidx.compose.koinViewModel

@Composable
fun SearchScreen(
    innerPaddingValues: PaddingValues,
    searchViewModel: SearchViewModel = koinViewModel(),
) {
    val navigator = LocalAppNavigator.current
    val (state, event, effect) = use(viewModel = searchViewModel)
    LaunchedEffect(key1 = Unit) {
        effect.collectLatest {
            when (it) {
                is SearchContract.Effect.OnViewDetail -> {
                    navigator.navigateToChild(Screen.Details(it.imageUrl))
                }

                else -> {

                }
            }
        }
    }
    BackHandler {
        navigator.navigateBack()
    }
    BaseView(innerPaddingValues, viewModel = searchViewModel) { SearchView(state, event) }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalGlideComposeApi::class)
@Composable
fun SearchView(state: SearchContract.State, event: (SearchContract.Event) -> Unit) {
    var query by remember { mutableStateOf("") }
    var searchBarActive by remember { mutableStateOf(false) }
    val stateGrid = rememberLazyGridState()

    val onSearch: (String) -> Unit = {
        event(SearchContract.Event.OnSearch(it))
        searchBarActive = false
    }
    val onClear: () -> Unit = {
        query = ""
    }
    Column() {
        SearchBar(
            query = query,
            onQueryChange = { query = it },
            onSearch = { onSearch(it) },
            active = searchBarActive,
            onActiveChange = { searchBarActive = it },
            leadingIcon = {
                Icon(Icons.Filled.Search, contentDescription = "search")
            },
            trailingIcon = {
                if (searchBarActive) Icon(
                    Icons.Filled.Clear,
                    contentDescription = "clear",
                    modifier = Modifier.clickable {
                        onClear()
                    })
            },
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
        ) {
        }

        LazyVerticalGrid(
            state = stateGrid,
            columns = GridCells.Fixed(2),
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            content = {
                items(state.images ?: emptyList(), key = { item -> item.id }) {
                    GlideImage(
                        model = it.urls.regular,
                        contentDescription = it.description,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp)
                            .clickable {
                                event(SearchContract.Event.OnViewDetail(it.urls.regular))
                            }
                    )
                }
            },
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 4.dp),
        )
    }
}