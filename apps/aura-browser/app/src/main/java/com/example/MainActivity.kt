package com.example

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.ViewGroup
import android.webkit.CookieManager
import android.webkit.WebChromeClient
import android.webkit.WebResourceRequest
import android.webkit.WebResourceResponse
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.material3.darkColorScheme
import androidx.compose.ui.viewinterop.AndroidView

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MaterialTheme(colorScheme = darkColorSchemeCustom()) {
                ChromeLikeBrowser()
            }
        }
    }
}

private fun darkColorSchemeCustom() = darkColorScheme(
    background = Color(0xFF0D1117),
    surface = Color(0xFF161B22),
    primary = Color(0xFF00E5FF),
    onBackground = Color.White,
    onSurface = Color.White,
    onSurfaceVariant = Color(0xFF8B949E)
)

@SuppressLint("SetJavaScriptEnabled")
@Composable
fun ChromeLikeBrowser() {
    val context = LocalContext.current

    val webView = remember {
        android.webkit.WebView(context).apply {
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
        }
    }

    val settings = remember { webView.settings }
    settings.javaScriptEnabled = true
    settings.domStorageEnabled = true
    settings.databaseEnabled = true
    settings.useWideViewPort = true
    settings.loadWithOverviewMode = true
    settings.builtInZoomControls = true
    settings.displayZoomControls = false
    settings.setSupportZoom(true)
    settings.mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
    settings.mediaPlaybackRequiresUserGesture = false
    settings.javaScriptCanOpenWindowsAutomatically = true
    CookieManager.getInstance().setAcceptCookie(true)
    CookieManager.getInstance().setAcceptThirdPartyCookies(webView, true)

    var inputText by remember { mutableStateOf("https://www.google.com") }
    var isLoading by remember { mutableStateOf(false) }

    remember {
        webView.webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest?): Boolean = false
            override fun shouldInterceptRequest(view: WebView?, request: WebResourceRequest?): WebResourceResponse? = null
            override fun onPageStarted(view: WebView?, url: String?, favicon: android.graphics.Bitmap?) {
                super.onPageStarted(view, url, favicon)
                isLoading = true
                url?.let { inputText = it }
            }
            override fun onPageFinished(view: WebView?, url: String?) {
                super.onPageFinished(view, url)
                isLoading = false
                url?.let { inputText = it }
            }
        }
        webView.webChromeClient = object : WebChromeClient() {
            override fun onProgressChanged(view: WebView?, newProgress: Int) {
                super.onProgressChanged(view, newProgress)
                isLoading = newProgress < 100
            }
        }
    }

    fun normalize(input: String): String {
        val t = input.trim()
        if (t.startsWith("http://") || t.startsWith("https://")) return t
        return if (t.contains(".") && !t.contains(" ") && t.length > 3) "https://" + t
        else "https://www.google.com/search?q=" + java.net.URLEncoder.encode(t, "UTF-8")
    }

    fun load(input: String) {
        val url = normalize(input)
        inputText = url
        webView.loadUrl(url)
    }

    BackHandler(enabled = true) {
        if (webView.canGoBack()) webView.goBack()
        else (context as? android.app.Activity)?.finish()
    }

    LaunchedEffect(Unit) {
        if (webView.url == null) webView.loadUrl("https://www.google.com")
    }

    Column(modifier = Modifier.fillMaxSize().background(Color(0xFF0D1117))) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(horizontal = 8.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = inputText,
                onValueChange = { inputText = it },
                modifier = Modifier.weight(1f),
                placeholder = { Text("Search or type URL", color = Color(0xFF8B949E), fontSize = 14.sp) },
                singleLine = true,
                shape = RoundedCornerShape(24.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(0xFF00E5FF),
                    unfocusedBorderColor = Color(0xFF30363D),
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                    cursorColor = Color(0xFF00E5FF)
                ),
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Go),
                keyboardActions = KeyboardActions(onGo = { if (inputText.isNotBlank()) load(inputText) })
            )
            androidx.compose.material3.TextButton(onClick = { if (inputText.isNotBlank()) load(inputText) }) {
                Text("GO", color = Color(0xFF00E5FF), fontSize = 16.sp)
            }
        }

        if (isLoading) {
            LinearProgressIndicator(modifier = Modifier.fillMaxWidth(), color = Color(0xFF00E5FF))
        }

        Box(modifier = Modifier.weight(1f)) {
            AndroidView(factory = { webView }, modifier = Modifier.fillMaxSize())
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFF161B22))
                .navigationBarsPadding()
                .padding(vertical = 6.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            androidx.compose.material3.IconButton(onClick = { if (webView.canGoBack()) webView.goBack() }) {
                androidx.compose.material3.Icon(
                    androidx.compose.material.icons.Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back", tint = Color.White
                )
            }
            androidx.compose.material3.IconButton(onClick = { if (webView.canGoForward()) webView.goForward() }) {
                androidx.compose.material3.Icon(
                    androidx.compose.material.icons.Icons.AutoMirrored.Filled.ArrowForward,
                    contentDescription = "Forward", tint = Color.White
                )
            }
            androidx.compose.material3.IconButton(onClick = { load("https://www.google.com") }) {
                androidx.compose.material3.Icon(
                    androidx.compose.material.icons.Icons.Default.Home,
                    contentDescription = "Home", tint = Color(0xFF00E5FF)
                )
            }
            androidx.compose.material3.IconButton(onClick = { webView.reload() }) {
                androidx.compose.material3.Icon(
                    androidx.compose.material.icons.Icons.Default.Refresh,
                    contentDescription = "Refresh", tint = Color.White
                )
            }
        }
    }
}
