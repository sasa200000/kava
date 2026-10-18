package com.example.ui.browser

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.view.ViewGroup
import android.webkit.CookieManager
import android.webkit.WebChromeClient
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import com.example.data.model.ActiveDownload
import com.example.data.model.BrowserTab
import com.example.downloader.DownloadHelper
import com.example.security.SecurityEngine
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

private const val DESKTOP_USER_AGENT =
    "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/128.0.0.0 Safari/537.36"

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
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    // Create WebView and keep stable reference
    val webView = remember {
        WebView(context).apply {
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
        }
    }

    // Configure WebSettings — full Chrome-like support for ALL sites
    LaunchedEffect(tab.isDesktopMode) {
        val settings = webView.settings

        // Core rendering
        settings.javaScriptEnabled = true
        settings.domStorageEnabled = true
        settings.databaseEnabled = true

        // Viewport — proper responsive rendering
        settings.useWideViewPort = true
        settings.loadWithOverviewMode = true

        // Zoom controls
        settings.setSupportZoom(true)
        settings.builtInZoomControls = true
        settings.displayZoomControls = false

        // Allow mixed content (HTTP resources on HTTPS pages) — like Chrome
        settings.mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW

        // Allow content/file access
        settings.allowContentAccess = true
        settings.allowFileAccess = true

        // Media playback
        settings.mediaPlaybackRequiresUserGesture = false

        // Cache
        settings.cacheMode = WebSettings.LOAD_DEFAULT

        // Popups
        settings.javaScriptCanOpenWindowsAutomatically = true

        // Third-party cookies
        CookieManager.getInstance().setAcceptCookie(true)
        CookieManager.getInstance().setAcceptThirdPartyCookies(webView, true)

        // User agent
        if (tab.isDesktopMode) {
            settings.userAgentString = DESKTOP_USER_AGENT
        } else {
            settings.userAgentString = null // Use default mobile UA
        }
    }

    // Handle system back button
    BackHandler(enabled = webView.canGoBack()) {
        webView.goBack()
    }

    // React to navigation requests (from address bar or home page shortcuts)
    LaunchedEffect(navigationRequest) {
        if (!navigationRequest.isNullOrBlank()) {
            var urlToLoad = navigationRequest

            // Force HTTPS for http:// URLs
            if (forceHttps && urlToLoad.startsWith("http://")) {
                urlToLoad = urlToLoad.replaceFirst("http://", "https://")
            }

            webView.loadUrl(urlToLoad)
            onNavigationHandled()
        }
    }

    DisposableEffect(webView) {
        // WebViewClient — handles page lifecycle + ad blocking
        webView.webViewClient = object : WebViewClient() {
            override fun shouldInterceptRequest(
                view: WebView?,
                request: WebResourceRequest?
            ): WebResourceResponse? {
                val reqUrl = request?.url?.toString()
                // Block known trackers if adBlocker is enabled
                if (adBlockerEnabled && SecurityEngine.shouldBlockUrl(reqUrl)) {
                    scope.launch(Dispatchers.Main) {
                        onTrackerBlocked()
                    }
                    return SecurityEngine.createEmptyBlockedResponse()
                }
                return super.shouldInterceptRequest(view, request)
            }

            override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                super.onPageStarted(view, url, favicon)
                url?.let { onPageStarted(it) }
            }

            override fun onPageFinished(view: WebView?, url: String?) {
                super.onPageFinished(view, url)
                url?.let { onPageFinished(it, view?.title) }
            }

            override fun shouldOverrideUrlLoading(
                view: WebView?,
                request: WebResourceRequest?
            ): Boolean {
                // Handle deep links / intent URLs
                return false
            }
        }

        // WebChromeClient — progress bar + title
        webView.webChromeClient = object : WebChromeClient() {
            override fun onProgressChanged(view: WebView?, newProgress: Int) {
                super.onProgressChanged(view, newProgress)
                onProgressChanged(newProgress)
            }

            override fun onReceivedTitle(view: WebView?, title: String?) {
                super.onReceivedTitle(view, title)
                val currentUrl = view?.url ?: ""
                onPageFinished(currentUrl, title)
            }

            override fun onReceivedIcon(view: WebView?, icon: Bitmap?) {
                super.onReceivedIcon(view, icon)
            }
        }

        // File downloads via system DownloadManager
        webView.setDownloadListener { url, userAgent, contentDisposition, mimeType, _ ->
            DownloadHelper.startSystemDownload(
                context = context,
                url = url,
                userAgent = userAgent,
                contentDisposition = contentDisposition,
                mimeType = mimeType,
                scope = scope,
                onDownloadStarted = onDownloadStarted
            )
        }

        onDispose {
            // Clean up WebView on dispose
        }
    }

    Box(modifier = modifier.fillMaxSize()) {
        AndroidView(
            factory = { webView },
            modifier = Modifier.fillMaxSize()
        )
    }
}
