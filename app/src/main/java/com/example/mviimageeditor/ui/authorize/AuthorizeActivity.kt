package com.example.mviimageeditor.ui.authorize

import android.content.Intent
import android.os.Bundle
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import com.example.mviimageeditor.main.MainActivity
import com.example.mviimageeditor.ui.authorize.ui.theme.MVIImageEditorTheme
import com.example.mviimageeditor.use
import com.example.mviimageeditor.utils.ACCESS_KEY
import com.example.mviimageeditor.utils.REDIRECT_URI
import com.example.mviimageeditor.utils.RESPONSE_TYPE
import com.example.mviimageeditor.utils.SCOPE
import com.example.mviimageeditor.utils.SIGN_OF_AUTHORIZE
import com.example.mviimageeditor.utils.toAuthorizationCode
import org.koin.androidx.compose.koinViewModel

class AuthorizeActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MVIImageEditorTheme {
                val viewModel: AuthorizeViewModel = koinViewModel()
                val (state, event, effect) = use(viewModel = viewModel)
                val context = LocalContext.current
                LaunchedEffect(key1 = effect) {
                    effect.collect {
                        when (it) {
                            is AuthorizeContract.Effect.AuthorizeSuccess -> {
                                startActivity(
                                    Intent(
                                        this@AuthorizeActivity,
                                        MainActivity::class.java,
                                    ),
                                )
                            }

                            is AuthorizeContract.Effect.ShowToast -> {
                                Toast.makeText(context, it.message, Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                }
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    AuthorizeScreen(
                        modifier = Modifier.padding(innerPadding),
                        event,
                    )
                }
            }
        }
    }
}

@Composable
fun AuthorizeScreen(
    modifier: Modifier,
    event: (AuthorizeContract.Event) -> Unit,
) {
    AndroidView(
        modifier = modifier,
        factory = { context ->
            WebView(context).apply {
                webViewClient =
                    object : WebViewClient() {
                        override fun shouldOverrideUrlLoading(
                            view: WebView?,
                            request: WebResourceRequest?,
                        ): Boolean {
                            request?.url?.let { url ->
                                if (url.toString().contains(SIGN_OF_AUTHORIZE)) {
                                    event.invoke(
                                        AuthorizeContract.Event.OnAuthorize(
                                            url.toString().toAuthorizationCode(),
                                        ),
                                    )
                                }
                            }
                            return false
                        }
                    }
                loadUrl(authorizeUrl())
            }
        },
        update = { webView ->
            webView.loadUrl(authorizeUrl())
        },
    )
}

fun authorizeUrl(): String =
    "https://unsplash.com/oauth/authorize" +
        "?client_id=" + ACCESS_KEY +
        "&redirect_uri=" + REDIRECT_URI +
        "&response_type=" + RESPONSE_TYPE +
        "&scope=" + SCOPE
