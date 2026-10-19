package com.example

import android.annotation.SuppressLint
import android.app.DownloadManager
import android.content.Context
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.view.ViewGroup
import android.webkit.CookieManager
import android.webkit.WebChromeClient
import android.webkit.WebResourceRequest
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.DesktopWindows
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material.icons.filled.Wallet
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Switch
import androidx.compose.material3.TextButton
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.viewinterop.AndroidView
import coil.compose.AsyncImage
import java.net.URLEncoder
import kotlin.random.Random

// ─────────────────────────── THEMES ───────────────────────────
data class AppTheme(
    val id: String,
    val name: String,
    val bg: Long,
    val surface: Long,
    val accent: Long,
    val text: Long = 0xFFFFFFFF
)

val THEMES = listOf(
    AppTheme("neon_black", "سیاه نئون",   0xFF0D1117, 0xFF161B22, 0xFF00E5FF),
    AppTheme("pink",      "صورتی",         0xFF1A0A12, 0xFF2D1424, 0xFFFF4FA0),
    AppTheme("yellow",    "زرد",           0xFF1A1400, 0xFF2E2600, 0xFFFFD600),
    AppTheme("green",     "سبز فسفری",     0xFF06120A, 0xFF0E2014, 0xFF00FF6A),
    AppTheme("orange",    "نارنجی",         0xFF1F0F00, 0xFF33200A, 0xFFFF7A1A),
    AppTheme("violet",    "بنفش",           0xFF14061F, 0xFF20102E, 0xFFB26BFF),
    AppTheme("cyan",      "آبی فیروزه‌ای",   0xFF04121A, 0xFF0A1E29, 0xFF00D8FF),
)

val WALLPAPERS = listOf(
    "https://picsum.photos/seed/aura1/1080/600",
    "https://picsum.photos/seed/aura7/1080/600",
    "https://picsum.photos/seed/aura13/1080/600",
    "https://picsum.photos/seed/aura21/1080/600",
    "https://picsum.photos/seed/aura42/1080/600",
    "https://picsum.photos/seed/aura99/1080/600",
    "https://picsum.photos/seed/aura2024/1080/600",
    "https://picsum.photos/seed/aura777/1080/600",
)

// ─────────────────────────── MAIN ACTIVITY ───────────────────────────
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent { AuraApp() }
    }
}

@OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)
@Composable
fun AuraApp() {
    var themeIdx by remember { mutableIntStateOf(0) }
    var showSettings by remember { mutableStateOf(false) }
    var showWallet by remember { mutableStateOf(false) }
    var showHomePage by remember { mutableStateOf(true) }
    var canGoBack by remember { mutableStateOf(false) }
    var canGoForward by remember { mutableStateOf(false) }
    var showLogs by remember { mutableStateOf(false) }
    var desktopMode by remember { mutableStateOf(false) }
    var incognito by remember { mutableStateOf(false) }

    val theme = THEMES[themeIdx]
    val cs = darkColorScheme(
        primary = Color(theme.accent),
        background = Color(theme.bg),
        surface = Color(theme.surface),
        onBackground = Color(theme.text),
        onSurface = Color(theme.text)
    )

    MaterialTheme(colorScheme = cs) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(theme.bg))
        ) {
            // Top bar
            TopBar(
                theme=theme,
                showHomePage=showHomePage,
                onHomeClick={ showHomePage = true },
                onSettingsClick={ showSettings = true },
                onLogsClick={ showLogs = true }
            )

            // Content
            Box(modifier = Modifier.weight(1f)) {
                if (showHomePage) {
                    HomePage(
                        theme=theme,
                        onUrlClick={ url ->
                            showHomePage = false
                            BrowserState.pendingUrl = url
                        }
                    )
                } else {
                    BrowserContent(
                        theme=theme,
                        canGoBack=canGoBack,
                        canGoForward=canGoForward,
                        desktopMode=desktopMode,
                        incognito=incognito,
                        onHomeClick={ showHomePage = true },
                        onNavigationChanged={ cb, cf -> canGoBack=cb; canGoForward=cf }
                    )
                }
            }

            // Bottom nav
            BottomNav(
                theme=theme,
                showHomePage=showHomePage,
                canGoBack=canGoBack,
                canGoForward=canGoForward,
                onHomeToggle={ showHomePage = !showHomePage },
                onWalletClick={ showWallet = true },
                onBack={ if(BrowserState.webView?.canGoBack()==true) BrowserState.webView?.goBack() },
                onForward={ if(BrowserState.webView?.canGoForward()==true) BrowserState.webView?.goForward() },
                onReload={ BrowserState.webView?.reload() }
            )
        }

        // Settings sheet
        if (showSettings) {
            ModalBottomSheet(
                onDismissRequest={ showSettings=false },
                sheetState=rememberModalBottomSheetState(skipPartiallyExpanded=true),
                containerColor=Color(theme.surface)
            ) {
                SettingsSheet(
                    themeIdx=themeIdx,
                    onThemeChange={ themeIdx=it },
                    desktopMode=desktopMode,
                    onDesktopModeChange={ desktopMode=it },
                    incognito=incognito,
                    onIncognitoChange={ incognito=it },
                    onClose={ showSettings=false }
                )
            }
        }

        // Wallet sheet
        if (showWallet) {
            ModalBottomSheet(
                onDismissRequest={ showWallet=false },
                sheetState=rememberModalBottomSheetState(skipPartiallyExpanded=true),
                containerColor=Color(theme.surface)
            ) {
                WalletSheet(theme=theme, onClose={ showWallet=false })
            }
        }

        if (showLogs) {
            ModalBottomSheet(
                onDismissRequest={ showLogs=false },
                sheetState=rememberModalBottomSheetState(skipPartiallyExpanded=true),
                containerColor=Color(theme.surface)
            ) {
                LogViewerSheet(theme=theme, onClose={ showLogs=false })
            }
        }
    }
}


// ─────────────────────────── BROWSER SINGLETON STATE ───────────────────────────
// Holds webView instance across onBackPressed

object BrowserState {
    var webView: WebView? = null
    var webViewClientReady = false
    var pendingUrl: String? = null
    fun loadUrl(url:String) { webView?.loadUrl(url) }
    fun reload() { webView?.reload() }
    fun goBack() { webView?.goBack() }
    fun goForward() { webView?.goForward() }

    // ── گزارش زنده (Diagnostics) ──
    val logs = mutableListOf<String>()
    fun log(msg: String) {
        val ts = java.text.SimpleDateFormat("HH:mm:ss", java.util.Locale.US).format(java.util.Date())
        synchronized(logs) {
            logs.add(0, "[$ts] $msg")
            if (logs.size > 200) logs.removeAt(logs.size - 1)
        }
        android.util.Log.d("AURA_DIAG", msg)
    }
}

@SuppressLint("SetJavaScriptEnabled")
@Composable
fun BrowserContent(
    theme: AppTheme,
    canGoBack: Boolean,
    canGoForward: Boolean,
    desktopMode: Boolean,
    incognito: Boolean,
    onHomeClick: () -> Unit,
    onNavigationChanged: (Boolean, Boolean) -> Unit
) {
    val context = LocalContext.current
    val webView = remember {
        BrowserState.webView ?: WebView(context).apply {
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
        }.also { BrowserState.webView = it }
    }

    // Config only once
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
    settings.allowFileAccess = true
    settings.allowContentAccess = true
    CookieManager.getInstance().setAcceptCookie(true)
    CookieManager.getInstance().setAcceptThirdPartyCookies(webView, true)

    // Desktop mode / incognito apply live
    LaunchedEffect(desktopMode, incognito) {
        BrowserState.log("SETTINGS: desktop=$desktopMode incognito=$incognito")
        if (desktopMode) {
            settings.userAgentString = "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/128.0.0.0 Safari/537.36"
        } else {
            settings.userAgentString = null
        }
        CookieManager.getInstance().setAcceptCookie(!incognito)
        if (incognito) {
            webView.clearCache(true)
            webView.clearHistory()
            CookieManager.getInstance().removeAllCookies(null)
        }
        if (webView.url?.startsWith("http") == true) webView.reload()
    }

    var url by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    var navBack by remember { mutableStateOf(false) }
    var navForward by remember { mutableStateOf(false) }
    var input by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        if (!BrowserState.webViewClientReady) {
            BrowserState.webViewClientReady = true
            BrowserState.webView?.let { wv ->
                wv.webViewClient = object : WebViewClient() {
                    override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest?): Boolean {
                        BrowserState.log("NAV: shouldOverride? ${request?.url} mainFrame=${request?.isForMainFrame}")
                        return false
                    }
                    override fun onPageStarted(view: WebView?, u: String?, favicon: android.graphics.Bitmap?) {
                        super.onPageStarted(view, u, favicon)
                        BrowserState.log("NAV_START: $u")
                        url = u ?: ""; isLoading = true
                    }
                    override fun onPageFinished(view: WebView?, u: String?) {
                        super.onPageFinished(view, u)
                        BrowserState.log("NAV_END: $u")
    url = u ?: ""; isLoading = false
                        navBack = view?.canGoBack() ?: false
                        navForward = view?.canGoForward() ?: false
                        onNavigationChanged(navBack, navForward)
                    }
                    override fun onReceivedError(view: WebView?, errorCode: Int, description: String?, failingUrl: String?) {
                        super.onReceivedError(view, errorCode, description, failingUrl)
                        BrowserState.log("ERR $errorCode: $description [$failingUrl]")
                    }
                    override fun onReceivedHttpError(view: WebView?, request: WebResourceRequest?, errorResponse: android.webkit.WebResourceResponse?) {
                        super.onReceivedHttpError(view, request, errorResponse)
                        if (request?.isForMainFrame == true)
                            BrowserState.log("HTTP_ERR ${errorResponse?.statusCode}: ${request.url}")
                    }
                }
                wv.webChromeClient = object : WebChromeClient() {
                    override fun onProgressChanged(view: WebView?, p: Int) {
                        super.onProgressChanged(view, p)
                        isLoading = p < 100
                    }
                }
                // Download everything
                wv.setDownloadListener { u, ua, _, _, _ ->
                    BrowserState.log("DOWNLOAD: $u [UA: ${ua?.take(30)}]")
                    try {
                        val fileName = u.substringAfterLast('/').takeIf { it.contains('.') }
                            ?: (System.currentTimeMillis().toString() + ".bin")
                        val dm = context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
                        val request = DownloadManager.Request(Uri.parse(u)).apply {
                            setTitle(fileName)
                            setDescription("Aura Browser")
                            setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
                            setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, fileName)
                            setAllowedOverMetered(true)
                            setAllowedOverRoaming(true)
                            setMimeType(null)
                            setShowRunningNotification(true)
                            addRequestHeader("User-Agent", ua ?: "")
                        }
                        dm.enqueue(request)
                        Toast.makeText(context, "❄ دانلود شد: $fileName", Toast.LENGTH_LONG).show()
                    } catch (e: Exception) {
                        Toast.makeText(context, "خطا: " + e.message, Toast.LENGTH_LONG).show()
                    }
                }
            }
        }

        // First load — handle pending URL or home page redirect
        val pUrl = BrowserState.pendingUrl
        BrowserState.log("LAUNCH: pendingUrl=$pUrl currentUrl=${webView.url}")
        if (pUrl != null) {
            webView.loadUrl(pUrl)
            BrowserState.pendingUrl = null
        } else if (webView.url == null || webView.url == "about:blank") {
            BrowserState.log("LAUNCH: loading duckduckgo")
            webView.loadUrl("https://duckduckgo.com")
        }
    }

    fun load(input: String) {
        var t = input.trim()
        if (t.isBlank()) return
        if (!t.startsWith("http")) t = "https://" + t
        BrowserState.webView?.loadUrl(t)
        url = t
    }

    Column(modifier=Modifier.fillMaxSize().background(Color(theme.bg))) {
        // Address bar
        Row(
            modifier=Modifier.fillMaxWidth().statusBarsPadding().padding(horizontal=8.dp, vertical=4.dp),
            verticalAlignment=Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value=input,
                onValueChange={input=it},
                modifier=Modifier.weight(1f),
                placeholder={ Text("جستجو یا آدرس", color=Color(0xFF8B949E), fontSize=13.sp) },
                singleLine=true,
                shape=RoundedCornerShape(24.dp),
                colors=OutlinedTextFieldDefaults.colors(
                    focusedBorderColor=Color(theme.accent),
                    unfocusedBorderColor=Color(0xFF30363D),
                    focusedTextColor=Color(theme.text),
                    unfocusedTextColor=Color(theme.text),
                    cursorColor=Color(0xFF00E5FF)
                ),
                keyboardOptions=KeyboardOptions(imeAction=ImeAction.Go),
                keyboardActions=KeyboardActions(onGo={ load(input) })
            )
            TextButton(onClick={ load(input) }) {
                Text("GO", color=Color(theme.accent), fontWeight=FontWeight.Bold)
            }
        }
        if (isLoading) LinearProgressIndicator(
            modifier=Modifier.fillMaxWidth(),
            color=Color(theme.accent)
        )

        // WebView
        Box(modifier=Modifier.weight(1f)) {
            AndroidView(factory={ webView }, modifier=Modifier.fillMaxSize())
        }
    }
}



// ─────────────────────────── TOP BAR ───────────────────────────
@Composable
fun TopBar(theme: AppTheme, showHomePage: Boolean, onHomeClick: () -> Unit, onSettingsClick: () -> Unit, onLogsClick: () -> Unit) {
    Row(
        modifier=Modifier.fillMaxWidth().background(Color(theme.surface)).statusBarsPadding().padding(horizontal=8.dp, vertical=8.dp),
        verticalAlignment=Alignment.CenterVertically
    ) {
        Text("AURA", color=Color(theme.accent), fontWeight=FontWeight.Black, fontSize=20.sp)
        Spacer(Modifier.weight(1f))
        if (showHomePage) {
            IconButton(onClick=onHomeClick) { Icon(Icons.Default.Refresh, "Browser", tint=Color(theme.text)) }
        } else {
            IconButton(onClick=onHomeClick) { Icon(Icons.Default.Home, "Home", tint=Color(theme.accent)) }
        }
        IconButton(onClick=onSettingsClick) { Icon(Icons.Default.Settings, "Settings", tint=Color(theme.text)) }
        IconButton(onClick=onLogsClick) { Text("🛠", fontSize=18.sp, color=Color(theme.accent)) }
    }
}

// ─────────────────────────── BOTTOM NAV ───────────────────────────
@Composable
fun BottomNav(
    theme: AppTheme,
    showHomePage: Boolean,
    canGoBack: Boolean,
    canGoForward: Boolean,
    onHomeToggle: () -> Unit,
    onWalletClick: () -> Unit,
    onBack: () -> Unit,
    onForward: () -> Unit,
    onReload: () -> Unit
) {
    Row(
        modifier=Modifier.fillMaxWidth().background(Color(theme.surface)).navigationBarsPadding().padding(vertical=8.dp),
        horizontalArrangement=Arrangement.SpaceEvenly
    ) {
        IconButton(onClick=onHomeToggle) {
            Icon(Icons.Default.Home, "Home", tint=if(showHomePage) Color(theme.accent) else Color.White)
        }
        IconButton(onClick=onBack) {
            Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back", tint=if(canGoBack) Color.White else Color(0xFF444444))
        }
        IconButton(onClick=onForward) {
            Icon(Icons.AutoMirrored.Filled.ArrowForward, "Forward", tint=if(canGoForward) Color.White else Color(0xFF444444))
        }
        IconButton(onClick=onReload) {
            Icon(Icons.Default.Refresh, "Refresh", tint=Color.White)
        }
        IconButton(onClick=onWalletClick) {
            Icon(Icons.Default.Wallet, "Wallet", tint=Color(theme.accent))
        }
    }
}

// ─────────────────────────── HOME PAGE ───────────────────────────
@Composable
fun HomePage(theme: AppTheme, onUrlClick: (String) -> Unit) {
    var wallpaperIdx by remember { mutableIntStateOf(Random.nextInt(WALLPAPERS.size)) }
    var searchInput by remember { mutableStateOf("") }

    Column(
        modifier=Modifier.fillMaxSize()
            .background(Color(theme.bg))
            .verticalScroll(rememberScrollState()),
        horizontalAlignment=Alignment.CenterHorizontally
    ) {
        // Full-screen wallpaper
        Box(modifier=Modifier.fillMaxWidth().height(400.dp)) {
            AsyncImage(
                model=WALLPAPERS[wallpaperIdx],
                contentDescription=null,
                modifier=Modifier.fillMaxSize(),
                contentScale=ContentScale.Crop
            )
            Box(modifier=Modifier.fillMaxSize().background(
                Brush.verticalGradient(
                    colors=listOf(Color.Transparent, Color.Black.copy(alpha=0.7f), Color(theme.bg)),
                    startY=0f, endY=1200f
                )
            ))
            Column(
                modifier=Modifier.align(Alignment.TopCenter).padding(top=48.dp),
                horizontalAlignment=Alignment.CenterHorizontally
            ) {
                Text("Aura Browser", fontSize=36.sp, fontWeight=FontWeight.Black, color=Color.White)
                Text("مرورگر ضد تحریم", fontSize=15.sp, color=Color(0xFFB0B8C1))
            }
        }

        // Working search bar
        OutlinedTextField(
            value=searchInput,
            onValueChange={searchInput=it},
            modifier=Modifier.fillMaxWidth().padding(horizontal=16.dp, vertical=12.dp),
            placeholder={ Text("جستجو یا آدرس سایت...", color=Color(0xFF8B949E)) },
            leadingIcon={ Icon(Icons.Default.Add, null, tint=Color(theme.accent)) },
            singleLine=true,
            shape=RoundedCornerShape(28.dp),
            colors=OutlinedTextFieldDefaults.colors(
                focusedBorderColor=Color(theme.accent),
                unfocusedBorderColor=Color(0xFF30363D),
                focusedTextColor=Color.White,
                unfocusedTextColor=Color.White,
                focusedContainerColor=Color(theme.surface),
                unfocusedContainerColor=Color(theme.surface)
            ),
            keyboardOptions=KeyboardOptions(imeAction=ImeAction.Search),
            keyboardActions=KeyboardActions(onSearch={
                if (searchInput.isNotBlank()) {
                    val url = if (searchInput.trim().startsWith("http")) searchInput.trim()
                    else if (searchInput.trim().contains(".")) "https://" + searchInput.trim()
                    else "https://duckduckgo.com/?q=" + URLEncoder.encode(searchInput.trim(), "UTF-8")
                    onUrlClick(url)
                }
            })
        )

        // Shuffle wallpaper button
        TextButton(onClick={ wallpaperIdx = Random.nextInt(WALLPAPERS.size) }) {
            Text("🖼 تصویر جدید  |  AURA", color=Color(theme.accent), fontSize=13.sp)
        }

        // Quick shortcuts grid
        val shortcuts = listOf(
            "Google" to "https://www.google.com",
            "YouTube" to "https://www.youtube.com",
            "GitHub" to "https://github.com",
            "Telegram" to "https://web.telegram.org",
            "ChatGPT" to "https://chat.openai.com",
            "Wikipedia" to "https://wikipedia.org",
            "Reddit" to "https://reddit.com",
            "X / Twitter" to "https://x.com",
            "Instagram" to "https://www.instagram.com",
            "Amazon" to "https://www.amazon.com",
            "Netflix" to "https://www.netflix.com",
            "Spotify" to "https://open.spotify.com"
        )

        // 3-column grid
        Column(
            modifier=Modifier.fillMaxWidth().padding(horizontal=12.dp),
            verticalArrangement=Arrangement.spacedBy(8.dp)
        ) {
            shortcuts.chunked(3).forEach { row ->
                Row(
                    modifier=Modifier.fillMaxWidth(),
                    horizontalArrangement=Arrangement.spacedBy(8.dp)
                ) {
                    row.forEach { (name, url) ->
                        Card(
                            onClick={ onUrlClick(url) },
                            modifier=Modifier.weight(1f),
                            colors=CardDefaults.cardColors(
                                containerColor=Color(theme.surface)
                            )
                        ) {
                            Column(
                                modifier=Modifier.padding(vertical=16.dp, horizontal=8.dp).fillMaxWidth(),
                                horizontalAlignment=Alignment.CenterHorizontally
                            ) {
                                Text(name, color=Color(theme.accent), fontWeight=FontWeight.Bold, fontSize=13.sp, textAlign=TextAlign.Center)
                            }
                        }
                    }
                    // Fill remaining slots in row with spacers
                    repeat(3 - row.size) {
                        Spacer(Modifier.weight(1f))
                    }
                }
            }
        }

        Spacer(Modifier.height(24.dp))
    }
}

// ─────────────────────────── SETTINGS SHEET ───────────────────────────
@Composable
fun SettingsSheet(
    themeIdx: Int,
    onThemeChange: (Int) -> Unit,
    desktopMode: Boolean,
    onDesktopModeChange: (Boolean) -> Unit,
    incognito: Boolean,
    onIncognitoChange: (Boolean) -> Unit,
    onClose: () -> Unit
) {
    val theme = THEMES[themeIdx]

    Column(
        modifier=Modifier.fillMaxWidth().padding(16.dp).verticalScroll(rememberScrollState()),
        verticalArrangement=Arrangement.spacedBy(12.dp)
    ) {
        Text("تنظیمات", fontSize=22.sp, fontWeight=FontWeight.Bold, color=Color(theme.accent))

        // Themes
        Text("تم رنگی", fontWeight=FontWeight.Bold, color=Color.White)
        LazyRow(horizontalArrangement=Arrangement.spacedBy(8.dp)) {
            items(THEMES.size) { i ->
                val t = THEMES[i]
                Box(
                    modifier=Modifier
                        .size(56.dp)
                        .clip(CircleShape)
                        .background(Color(t.bg))
                        .border(3.dp, if(i==themeIdx) Color(t.accent) else Color.Transparent, CircleShape)
                        .clickable { onThemeChange(i) },
                    contentAlignment=Alignment.Center
                ) {
                    Text("●", color=Color(t.accent), fontSize=20.sp)
                }
            }
        }

        // Toggles
        Row(verticalAlignment=Alignment.CenterVertically, modifier=Modifier.fillMaxWidth()) {
            Icon(Icons.Default.DesktopWindows, null, tint=Color(theme.accent))
            Spacer(Modifier.width(8.dp))
            Text("حالت دسکتاپ (Desktop Mode)", color=Color.White, modifier=Modifier.weight(1f))
            Switch(checked=desktopMode, onCheckedChange={ onDesktopModeChange(it) })
        }

        Row(verticalAlignment=Alignment.CenterVertically, modifier=Modifier.fillMaxWidth()) {
            Icon(Icons.Default.Shield, null, tint=Color(theme.accent))
            Spacer(Modifier.width(8.dp))
            Text("مرور خصوصی (Incognito)", color=Color.White, modifier=Modifier.weight(1f))
            Switch(checked=incognito, onCheckedChange={ onIncognitoChange(it) })
        }

        Button(
            onClick=onClose,
            modifier=Modifier.fillMaxWidth(),
            colors=ButtonDefaults.buttonColors(containerColor=Color(theme.accent))
        ) { Text("بستن", color=Color.Black) }
        Spacer(Modifier.height(24.dp))
    }
}

// ─────────────────────────── LOG VIEWER SHEET ───────────────────────────
@Composable
fun LogViewerSheet(theme: AppTheme, onClose: () -> Unit) {
    var logLines by remember { mutableStateOf<List<String>>(emptyList()) }
    LaunchedEffect(Unit) {
        while (true) {
            logLines = synchronized(BrowserState.logs) { BrowserState.logs.toList() }
            kotlinx.coroutines.delay(1000)
        }
    }
    Column(
        modifier=Modifier.fillMaxWidth().padding(16.dp).verticalScroll(rememberScrollState()),
        verticalArrangement=Arrangement.spacedBy(4.dp)
    ) {
        Row(verticalAlignment=Alignment.CenterVertically) {
            Text("🛠 گزارشات", fontSize=20.sp, fontWeight=FontWeight.Bold, color=Color(theme.accent), modifier=Modifier.weight(1f))
            Button(onClick={
                synchronized(BrowserState.logs){ BrowserState.logs.clear() }
                logLines = emptyList()
            }, colors=ButtonDefaults.buttonColors(containerColor=Color(0xFF30363D))) {
                Text("پاک کن", color=Color.White, fontSize=12.sp)
            }
        }
        Text("نسخه: 1.0.4 | تاریخ: ${java.text.SimpleDateFormat("yyyy-MM-dd HH:mm", java.util.Locale.US).format(java.util.Date())}",
             color=Color(0xFF8B949E), fontSize=11.sp)
        Spacer(Modifier.height(8.dp))
        if (logLines.isEmpty()) {
            Text("هنوز گزارشی نیست. یک سایت باز کنید.", color=Color(0xFF8B949E))
        } else {
            logLines.forEach { line ->
                Text(line, fontSize=10.sp, color=Color(0xFFE0E0E0),
                     fontFamily=FontFamily.Monospace,
                     modifier=Modifier.fillMaxWidth())
            }
        }
        Spacer(Modifier.height(8.dp))
        Button(
            onClick=onClose,
            modifier=Modifier.fillMaxWidth(),
            colors=ButtonDefaults.buttonColors(containerColor=Color(theme.accent))
        ) { Text("بستن", color=Color.Black) }
        Spacer(Modifier.height(24.dp))
    }
}

// ─────────────────────────── WALLET SHEET ───────────────────────────
@Composable
fun WalletSheet(theme: AppTheme, onClose: () -> Unit) {
        var btcPrice by remember { mutableStateOf("در حال دریافت...") }
    var prices by remember { mutableStateOf("در حال دریافت...") }
    LaunchedEffect(Unit) {
        try {
            val client = okhttp3.OkHttpClient()
            val req = okhttp3.Request.Builder()
                .url("https://api.coingecko.com/api/v3/simple/price?ids=bitcoin,ethereum,usdt,sol,binancecoin,xrp,cardano,dogecoin,tron,polkadot,matic-network,litecoin,shiba-inu,pepe,ton-hunter&vs_currencies=usd&include_24hr_change=true")
                .build()
            client.newCall(req).enqueue(object : okhttp3.Callback {
                override fun onFailure(call: okhttp3.Call, e: java.io.IOException) {
                    btcPrice = "خطا در اتصال"
                }
                override fun onResponse(call: okhttp3.Call, response: okhttp3.Response) {
                    val body = response.body?.string() ?: "{}"
                    btcPrice = body
                    try {
                        val json = org.json.JSONObject(body)
                        val sb = StringBuilder()
                        json.keys().forEach { k ->
                            val v = json.getJSONObject(k)
                            sb.append(k.uppercase()).append(": $").append(v.getString("usd"))
                                .append("  (").append(v.optDouble("usd_24h_change", 0.0)).append("%)\n")
                        }
                        prices = sb.toString()
                    } catch (e: Exception) { }
                }
            })
        } catch(e: Exception) {
            btcPrice = "خطا: " + e.message
        }
    }

    Column(
        modifier=Modifier.fillMaxWidth().padding(16.dp).verticalScroll(rememberScrollState()),
        verticalArrangement=Arrangement.spacedBy(12.dp)
    ) {
        Text("کیف پول", fontSize=22.sp, fontWeight=FontWeight.Bold, color=Color(theme.accent))
        Text("مدیریت دارایی‌های دیجیتال", fontSize=13.sp, color=Color(0xFF8B949E))

        // Balance card
        Card(colors=CardDefaults.cardColors(containerColor=Color(theme.accent).copy(alpha=0.1f))) {
            Column(Modifier.padding(16.dp).fillMaxWidth()) {
                Text("موجودی کل", color=Color(0xFF8B949E), fontSize=12.sp)
                Text("0.00 USD", fontSize=28.sp, fontWeight=FontWeight.Bold, color=Color.White)
            }
        }

        Text("قیمت لحظه‌ای (USD):", color=Color.White, fontWeight=FontWeight.Bold)
        Text(prices, color=Color(theme.accent), fontSize=13.sp)

        // Token list
        val tokens = listOf(
            "BTC" to "bitcoin", "ETH" to "ethereum", "USDT" to "tether",
            "SOL" to "solana", "XRP" to "ripple", "ADA" to "cardano",
            "DOGE" to "dogecoin", "BNB" to "binancecoin", "TRX" to "tron",
            "DOT" to "polkadot", "LTC" to "litecoin", "TON" to "ton-hunter",
            "SHIB" to "shiba-inu", "PEPE" to "pepe"
        )
        tokens.forEach { (ticker, cgId) ->
            Row(
                modifier=Modifier.fillMaxWidth().padding(vertical=8.dp),
                verticalAlignment=Alignment.CenterVertically
            ) {
                Text(ticker, color=Color(theme.accent), fontWeight=FontWeight.Bold, modifier=Modifier.width(56.dp))
                Spacer(Modifier.width(8.dp))
                Text(cgId.replaceFirstChar{ it.uppercase() }, color=Color(0xFF8B949E), fontSize=11.sp)
                Spacer(Modifier.weight(1f))
                Button(
                    onClick={},
                    colors=ButtonDefaults.buttonColors(containerColor=Color(theme.accent)),
                    contentPadding=androidx.compose.foundation.layout.PaddingValues(horizontal=12.dp, vertical=4.dp)
                ) { Text("دریافت", color=Color.Black, fontSize=11.sp) }
                Spacer(Modifier.width(6.dp))
                Button(
                    onClick={},
                    colors=ButtonDefaults.buttonColors(containerColor=Color(0xFF30363D)),
                    contentPadding=androidx.compose.foundation.layout.PaddingValues(horizontal=12.dp, vertical=4.dp)
                ) { Text("ارسال", color=Color.White, fontSize=11.sp) }
            }
        }

        Button(
            onClick=onClose,
            modifier=Modifier.fillMaxWidth(),
            colors=ButtonDefaults.buttonColors(containerColor=Color(theme.accent))
        ) { Text("بستن", color=Color.Black) }
        Spacer(Modifier.height(24.dp))
    }
}
