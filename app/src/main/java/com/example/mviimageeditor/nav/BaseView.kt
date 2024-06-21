package com.example.mviimageeditor.nav

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.example.mviimageeditor.BaseViewModel

@Composable
fun BaseView(
    childView: @Composable () -> Unit,
    viewModel: BaseViewModel = androidx.lifecycle.viewmodel.compose.viewModel()
) {
    val state = viewModel.state.collectAsState()
    var isLoading by remember { mutableStateOf(viewModel.state.value.isLoading) }
    Box {
        childView()
        if (isLoading) {
            CircularProgressIndicator()
        }
    }

}