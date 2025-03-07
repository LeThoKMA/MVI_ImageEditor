package com.example.mviimageeditor.ui.search

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.SearchBar
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemKey
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.example.mviimageeditor.model.PhotoModel
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
    val pagingData = searchViewModel.pagingDataFlow.collectAsLazyPagingItems()
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
    SearchView(
        modifier = Modifier.padding(paddingValues = innerPaddingValues),
        state,
        event,
        pagingData
    )
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalGlideComposeApi::class)
@Composable
fun SearchView(
    modifier: Modifier,
    state: SearchContract.State,
    event: (SearchContract.Event) -> Unit,
    pagingData: LazyPagingItems<PhotoModel>,
) {
    var query by remember { mutableStateOf("") }
    val stateGrid = rememberLazyGridState(pagingData.itemCount)

    var expandSearchBar by rememberSaveable { mutableStateOf(false) }
    val onSearch: (String) -> Unit = {
        event(SearchContract.Event.OnSearch(it))
        expandSearchBar = false
    }
    val onClear: () -> Unit = {
        query = ""
    }
    Column {
        SearchBar(
            modifier =
            Modifier
                .padding(horizontal = 8.dp)
                .fillMaxWidth()
                .wrapContentHeight(),
            inputField = {
                SearchBarDefaults.InputField(
                    query = query,
                    onQueryChange = { query = it },
                    onExpandedChange = { expandSearchBar = it },
                    onSearch = { onSearch(it) },
                    expanded = expandSearchBar,
                    leadingIcon = {
                        Icon(Icons.Filled.Search, contentDescription = "search")
                    },
                    trailingIcon = {
                        if (expandSearchBar) {
                            Icon(
                                Icons.Filled.Clear,
                                contentDescription = "clear",
                                modifier =
                                Modifier.clickable {
                                    onClear()
                                },
                            )
                        }
                    },
                )
            },
            expanded = expandSearchBar,
            onExpandedChange = { expandSearchBar = it },
        ) {
        }

        LazyVerticalGrid(
            state = stateGrid,
            columns = GridCells.Fixed(2),
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            content = {
                items(pagingData.itemCount, key = pagingData.itemKey { it.id }) {
                    GlideImage(
                        modifier =
                        Modifier
                            .fillMaxWidth()
                            .height(200.dp)
                            .clickable {
                                event(SearchContract.Event.OnViewDetail(pagingData[it]?.urls?.regular))
                            }
                            .animateItem(),
                        model = pagingData[it]?.urls?.regular,
                        contentDescription = pagingData[it]?.description,
                        contentScale = ContentScale.Crop,
                    )
//                    GlideImageCustom(
//                        url = pagingData[it]?.urls?.regular,
//                        description = pagingData[it]?.description,
//                        event = {
//                            event(SearchContract.Event.OnViewDetail(pagingData[it]?.urls?.regular))
//                        },
//                    )
                }
            },
            modifier =
            Modifier
                .fillMaxSize()
                .padding(top = 4.dp),
        )
        pagingData.apply {
            when {
                loadState.refresh is androidx.paging.LoadState.Loading -> {
                    // You can add modifier to manage load state when first time response page
                }

                loadState.append is androidx.paging.LoadState.Loading -> {
                    // You can add modifier to manage load state when next response page
                }

                loadState.append is androidx.paging.LoadState.Error -> {
                    // You can use modifier to show error message
                    println(" loadState.append is androidx.paging.LoadState.Error")
                }

                loadState.refresh is androidx.paging.LoadState.Error -> {
                    println(" loadState.refresh is androidx.paging.LoadState.Error")
                }
            }
        }
    }
}
