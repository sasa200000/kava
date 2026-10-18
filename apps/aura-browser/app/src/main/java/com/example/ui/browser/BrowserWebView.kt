package com.example.ui.browser

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.util.Log
import android.view.ViewGroup
import android.webkit.ConsoleMessage
import android.webkit.CookieManager
import android.webkit.WebChromeClient
import android.webkit.WebResourceError
import android.webkit.WebResourceRequest
import android.webkit.WebResourceResponse
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import com.example.data.model.ActiveDownload
import com.example.data.model.BrowserTab
import com.example.downloader.DownloadHelper
import com.example.security.SecurityEngine

private const val DESKTOP_UA = "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/128.0.0.0 Safari/537.36"
private const val TAG = "AuraWebView"

@SuppressLint("SetJavaScriptEnabled")
@Composable
fun BrowserWebView(
    tab: BrowserTab,
    navigationRequest: String?,
    adBlockerEnabled: Boolean,
    forceHttps: Boolean,
    onNavigationHandled: () -> Unit,
    onPageStarted: (String) -> Unit,
    onPageFinished: (String, String?) -> Unit,
    onProgressChanged: (Int) -> Unit,
    onTrackerBlocked: () -> Unit,
    onDownloadStarted: (ActiveDownload) -> Unit,
    onError: (BrowserError?) -> Unit,
    onLoadUrl: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    // ── Stable WebView reference created ONCE ──────────────────────────────────
    val webView = remember {
        WebView(context).apply {
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
        }
    }

    // ── Configure ALL settings SYNCHRONOUSLY — fires before first composition ─
    val settings = remember { webView.settings }
    settings.javaScriptEnabled = true
    settings.domStorageEnabled = true
    settings.databaseEnabled = true
    settings.useWideViewPort = true
    settings.loadWithOverviewMode = true
    settings.setSupportZoom(true)
    settings.builtInZoomControls = true
    settings.displayZoomControls = false
    settings.mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
    settings.allowContentAccess = true
    settings.allowFileAccess = true
    settings.mediaPlaybackRequiresUserGesture = false
    settings.cacheMode = WebSettings.LOAD_DEFAULT
    settings.javaScriptCanOpenWindowsAutomatically = true
    CookieManager.getInstance().setAcceptCookie(true)
    CookieManager.getInstance().setAcceptThirdPartyCookies(webView, true)

    // ── User agent: update only when mode changes ──────────────────────────────
    val currentDesktopMode = remember { mutableStateOf(tab.isDesktopMode) }
    if (tab.isDesktopMode != currentDesktopMode.value) {
        currentDesktopMode.value = tab.isDesktopMode
        settings.userAgentString = if (tab.isDesktopMode) DESKTOP_UA else ""
    }

    // ── WebViewClient — set ONCE when WebView first created ─────────────────────
    remember {
        webView.webViewClient = object : WebViewClient() {
            override fun shouldInterceptRequest(
                view: WebView?, request: WebResourceRequest?
            ): WebResourceResponse? {
                val reqUrl = request?.url?.toString()
                Log.d(TAG, "REQ: ${request?.url} main=${request?.isForMainFrame}")
                if (adBlockerEnabled && SecurityEngine.shouldBlockUrl(reqUrl)) {
                    Log.d(TAG, "BLOCKED: $reqUrl")
                    onTrackerBlocked()
                    return SecurityEngine.createEmptyBlockedResponse()
                }
                return super.shouldInterceptRequest(view, request)
            }

            override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                super.onPageStarted(view, url, favicon)
                Log.d(TAG, "onPageStarted: $url")
                onError(null)
                url?.let { onPageStarted(it) }
            }

            override fun onPageFinished(view: WebView?, url: String?) {
                super.onPageFinished(view, url)
                Log.d(TAG, "onPageFinished: $url title=${view?.title}")
                url?.let { onPageFinished(it, view?.title) }
            }

            override fun onReceivedError(
                view: WebView?, request: WebResourceRequest?,
                error: WebResourceError?
            ) {
                super.onReceivedError(view, request, error)
                val code = error?.errorCode ?: -1
                val desc = error?.description?.toString() ?: "unknown"
                Log.e(TAG, "ERROR $code: $desc url=${request?.url}")
                if (request?.isForMainFrame == true) {
                    view?.stopLoading()
                    onError(BrowserError("ERROR", code, desc, request?.url?.toString() ?: ""))
                }
            }

            override fun onReceivedHttpError(
                view: WebView?, request: WebResourceRequest?,
                errorResponse: WebResourceResponse?
            ) {
                super.onReceivedHttpError(view, request, errorResponse)
                val code = errorResponse?.statusCode ?: -1
                Log.e(TAG, "HTTP $code: ${errorResponse?.reasonPhrase} url=${request?.url}")
                if (request?.isForMainFrame == true) {
                    onError(BrowserError("HTTP", code, "HTTP ${errorResponse?.reasonPhrase}", request?.url?.toString() ?: ""))
                }
            }

            override fun shouldOverrideUrlLoading(
                view: WebView?, request: WebResourceRequest?
            ): Boolean = false
        }

        webView.webChromeClient = object : WebChromeClient() {
            override fun onProgressChanged(view: WebView?, newProgress: Int) {
                super.onProgressChanged(view, newProgress)
                Log.d(TAG, "progress=$newProgress url=${view?.url}")
                onProgressChanged(newProgress)
            }

            override fun onReceivedTitle(view: WebView?, title: String?) {
                super.onReceivedTitle(view, title)
                val url = view?.url ?: ""
                Log.d(TAG, "title=$title url=$url")
                onPageFinished(url, title)
            }

            override fun onConsoleMessage(msg: ConsoleMessage?): Boolean {
                Log.d(TAG, "console: ${msg?.message()}")
                return true
            }
        }

        webView.setDownloadListener { url, ua, cd, mt, _ ->
            Log.d(TAG, "download: $url")
            DownloadHelper.startSystemDownload(
                context = context,
                url = url, userAgent = ua,
                contentDisposition = cd, mimeType = mt,
                scope = kotlinx.coroutines.CoroutineScope(kotlinx.coroutines.Dispatchers.Main),
                onDownloadStarted = onDownloadStarted
            )
        }
    }

    // ── Back button ────────────────────────────────────────────────────────────
    BackHandler(enabled = webView.canGoBack()) {
        webView.goBack()
    }

    // ── Navigation: fires when navigationRequest changes ────────────────────────
    DisposableEffect(navigationRequest) {
        Log.d(TAG, "DisposableEffect navRequest=$navigationRequest")
        if (!navigationRequest.isNullOrBlank()) {
            var url = navigationRequest
            if (forceHttps && url.startsWith("http://")) {
                url = "https://" + url.substring(7)
            }
            Log.d(TAG, "loadUrl: $url webView.url=${webView.url}")
            onLoadUrl(url)
            webView.loadUrl(url)      // ← THE ACTUAL LOAD
            onNavigationHandled()
        }
        onDispose { }
    }

    // ── Render ─────────────────────────────────────────────────────────────────
    Box(modifier = modifier.fillMaxSize()) {
        AndroidView(
            factory = { webView },
            modifier = Modifier.fillMaxSize()
        )
    }
}
