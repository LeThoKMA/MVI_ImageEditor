package com.example.mviimageeditor.ui.authorize

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.preference.PreferenceManager
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
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
import com.example.mviimageeditor.MainActivity
import com.example.mviimageeditor.ui.authorize.ui.theme.MVIImageEditorTheme
import com.example.mviimageeditor.use
import com.example.mviimageeditor.utils.ACCESS_KEY
import com.example.mviimageeditor.utils.PREF_ACCESS_TOKEN
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
                val (state, event) = use(viewModel = viewModel)
                val context = LocalContext.current
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    AuthorizeScreen(
                        state, event, context,
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

@Composable
fun AuthorizeScreen(
    state: AuthorizeContract.State,
    event: (AuthorizeContract.Event) -> Unit,
    context: Context,
    modifier: Modifier = Modifier
) {
    LaunchedEffect(key1 = state.authorizeResponse) {
        if (state.authorizeResponse != null || PreferenceManager.getDefaultSharedPreferences(context)
                .getString(PREF_ACCESS_TOKEN, "")?.isNotBlank() == true
        ) {
            context.startActivity(Intent(context, MainActivity::class.java))
        }
    }
    AndroidView(
        factory = { context ->
            WebView(context).apply {
                webViewClient = object : WebViewClient() {
                    override fun shouldOverrideUrlLoading(
                        view: WebView?,
                        request: WebResourceRequest?,
                    ): Boolean {
                        request?.url?.let { url ->
                            if (url.toString().contains(SIGN_OF_AUTHORIZE)) {
                                event.invoke(
                                    AuthorizeContract.Event.OnAuthorize(
                                        url.toString().toAuthorizationCode()
                                    )
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
        }
    )
}
fun authorizeUrl(): String {
    return "https://unsplash.com/oauth/authorize" +
            "?client_id=" + ACCESS_KEY +
            "&redirect_uri=" + REDIRECT_URI +
            "&response_type=" + RESPONSE_TYPE +
            "&scope=" + SCOPE
}