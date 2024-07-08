package com.example.mviimageeditor.nav

import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.mviimageeditor.BaseViewModel
import com.example.mviimageeditor.MainActivity
import com.example.mviimageeditor.MyPreference
import com.example.mviimageeditor.ui.theme.GrayE0
import com.example.mviimageeditor.ui.authorize.AuthorizeActivity
import org.koin.androidx.compose.koinViewModel

@Composable
fun BaseView(
    innerPadding: PaddingValues = PaddingValues(0.dp),
    viewModel: BaseViewModel = koinViewModel(),
    childView: @Composable () -> Unit,
) {
    val state = viewModel.baseState.collectAsState()
    val isLoading = state.value.isLoading
    val context = LocalContext.current
    LaunchedEffect(key1 = Unit) {
        viewModel.baseEffect.collect {
            when (it) {
                is BaseViewModel.Effect.OnErrorAuthorize -> {
                    MyPreference(context).deleteToken()
                    context.startActivity(Intent(context, AuthorizeActivity::class.java))
                }

                else -> {}
            }
        }
    }
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(if (isLoading) GrayE0 else Color.White)
                .padding(innerPadding)
        ) {
            childView()
            if (isLoading) {
                Column(
                    modifier = Modifier
                        .size(150.dp)
                        .align(Alignment.Center)
                        .padding(16.dp)
                        .clip(
                            RoundedCornerShape(8.dp)
                        )
                        .background(Color.White),
                    verticalArrangement = Arrangement.Center,
                ) {
                    CircularProgressIndicator(
                        modifier = Modifier
                            .align(Alignment.CenterHorizontally)
                            .background(Color.White)
                    )
                    Text(
                        text = "Loading",
                        modifier = Modifier
                            .align(Alignment.CenterHorizontally)
                            .padding(top = 16.dp)
                    )
                }

            }
        }

    }