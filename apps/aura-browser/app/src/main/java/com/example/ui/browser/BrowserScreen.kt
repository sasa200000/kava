package com.example.ui.browser

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection
import com.example.data.model.AppLanguage
import com.example.ui.edgelighting.EdgeLightingOverlay

@Composable
fun BrowserScreen(
    viewModel: BrowserViewModel,
    modifier: Modifier = Modifier
) {
    val tabs by viewModel.tabs.collectAsState()
    val currentTabIndex by viewModel.currentTabIndex.collectAsState()
    val currentTab = viewModel.currentTab
    val searchEngine by viewModel.searchEngine.collectAsState()
    val edgeLightingConfig by viewModel.edgeLightingConfig.collectAsState()
    val language by viewModel.appLanguage.collectAsState()
    val totalBlocked by viewModel.totalTrackersBlocked.collectAsState()
    val adBlockerEnabled by viewModel.adBlockerEnabled.collectAsState()
    val antiPhishingEnabled by viewModel.antiPhishingEnabled.collectAsState()
    val forceHttps by viewModel.forceHttps.collectAsState()
    val activeDownloads by viewModel.activeDownloads.collectAsState()
    val bookmarks by viewModel.bookmarks.collectAsState()
    val history by viewModel.history.collectAsState()
    val savedDownloads by viewModel.savedDownloads.collectAsState()
    val navigationRequest by viewModel.navigationRequest.collectAsState()

    // Sheet states
    val showTabsSheet by viewModel.showTabsSheet.collectAsState()
    val showBookmarksHistorySheet by viewModel.showBookmarksHistorySheet.collectAsState()
    val showDownloadsSheet by viewModel.showDownloadsSheet.collectAsState()
    val showSecurityShieldSheet by viewModel.showSecurityShieldSheet.collectAsState()
    val showEdgeLightingSheet by viewModel.showEdgeLightingSheet.collectAsState()
    val showSettingsSheet by viewModel.showSettingsSheet.collectAsState()

    val layoutDirection = if (language == AppLanguage.FA) LayoutDirection.Rtl else LayoutDirection.Ltr

    CompositionLocalProvider(LocalLayoutDirection provides layoutDirection) {
        Box(
            modifier = modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
        ) {
            Scaffold(
                modifier = Modifier
                    .fillMaxSize()
                    .statusBarsPadding()
                    .imePadding(),
                topBar = {
                    BrowserTopBar(
                        tab = currentTab,
                        totalTabs = tabs.size,
                        language = language,
                        onNavigate = { viewModel.requestNavigate(it) },
                        onReload = { viewModel.requestNavigate(currentTab.url) },
                        onOpenTabs = { viewModel.setShowTabsSheet(true) },
                        onOpenSecurityShield = { viewModel.setShowSecurityShieldSheet(true) },
                        onToggleBookmark = { viewModel.toggleBookmarkCurrentPage() },
                        onOpenDownloads = { viewModel.setShowDownloadsSheet(true) },
                        onOpenSettings = { viewModel.setShowSettingsSheet(true) },
                        onToggleDesktop = { viewModel.toggleDesktopMode() }
                    )
                },
                bottomBar = {
                    BrowserBottomBar(
                        canGoBack = currentTab.canGoBack,
                        canGoForward = currentTab.canGoForward,
                        edgeLightingEnabled = edgeLightingConfig.enabled,
                        onBack = { /* handled by back handler */ },
                        onForward = { /* can forward */ },
                        onHome = { viewModel.requestNavigate("about:home") },
                        onOpenBookmarksHistory = { viewModel.setShowBookmarksHistorySheet(true) },
                        onOpenEdgeLighting = { viewModel.setShowEdgeLightingSheet(true) },
                        onOpenSecurity = { viewModel.setShowSecurityShieldSheet(true) }
                    )
                },
                containerColor = MaterialTheme.colorScheme.background
            ) { innerPadding ->
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                ) {
                    if (currentTab.url == "about:home") {
                        BrowserHomePage(
                            searchEngine = searchEngine,
                            language = language,
                            totalBlocked = totalBlocked,
                            onNavigate = { viewModel.requestNavigate(it) },
                            onOpenSecurity = { viewModel.setShowSecurityShieldSheet(true) },
                            onOpenEdgeLighting = { viewModel.setShowEdgeLightingSheet(true) },
                            onOpenDownloads = { viewModel.setShowDownloadsSheet(true) }
                        )
                    } else {
                        BrowserWebView(
                            tab = currentTab,
                            navigationRequest = navigationRequest,
                            adBlockerEnabled = adBlockerEnabled,
                            forceHttps = forceHttps,
                            onNavigationHandled = { viewModel.clearNavigationRequest() },
                            onPageStarted = { viewModel.onPageStarted(it) },
                            onPageFinished = { url, title -> viewModel.onPageFinished(url, title) },
                            onProgressChanged = { viewModel.onProgressChanged(it) },
                            onTrackerBlocked = { viewModel.onTrackerBlocked() },
                            onDownloadStarted = { viewModel.addActiveDownload(it) }
                        )
                    }
                }
            }

            // High-Performance Multi-Pattern Edge Lighting Overlay on top of screen
            EdgeLightingOverlay(
                config = edgeLightingConfig,
                isPageLoading = currentTab.isLoading,
                isDownloading = activeDownloads.isNotEmpty()
            )

            // Modal Sheets
            if (showTabsSheet) {
                TabsSheet(
                    tabs = tabs,
                    currentTabIndex = currentTabIndex,
                    language = language,
                    onSelectTab = { viewModel.switchTab(it) },
                    onCloseTab = { viewModel.closeTab(it) },
                    onNewTab = { viewModel.newTab(isIncognito = it) },
                    onDismiss = { viewModel.setShowTabsSheet(false) }
                )
            }

            if (showEdgeLightingSheet) {
                EdgeLightingSheet(
                    config = edgeLightingConfig,
                    language = language,
                    onConfigChange = { viewModel.updateEdgeLightingConfig(it) },
                    onDismiss = { viewModel.setShowEdgeLightingSheet(false) }
                )
            }

            if (showSecurityShieldSheet) {
                SecurityShieldSheet(
                    tab = currentTab,
                    totalBlocked = totalBlocked,
                    adBlockerEnabled = adBlockerEnabled,
                    antiPhishingEnabled = antiPhishingEnabled,
                    forceHttps = forceHttps,
                    language = language,
                    onToggleAdBlocker = { viewModel.toggleAdBlocker(it) },
                    onToggleAntiPhishing = { viewModel.toggleAntiPhishing(it) },
                    onToggleForceHttps = { viewModel.toggleForceHttps(it) },
                    onClearData = { viewModel.clearAllBrowserData() },
                    onDismiss = { viewModel.setShowSecurityShieldSheet(false) }
                )
            }

            if (showDownloadsSheet) {
                DownloadsSheet(
                    activeDownloads = activeDownloads,
                    savedDownloads = savedDownloads,
                    language = language,
                    onDismiss = { viewModel.setShowDownloadsSheet(false) }
                )
            }

            if (showBookmarksHistorySheet) {
                BookmarksHistorySheet(
                    bookmarks = bookmarks,
                    history = history,
                    language = language,
                    onNavigate = { viewModel.requestNavigate(it) },
                    onDeleteBookmark = { viewModel.removeBookmark(it) },
                    onDeleteHistoryItem = { viewModel.removeHistoryItem(it) },
                    onClearAllHistory = { viewModel.clearAllHistory() },
                    onDismiss = { viewModel.setShowBookmarksHistorySheet(false) }
                )
            }

            if (showSettingsSheet) {
                SettingsSheet(
                    currentEngine = searchEngine,
                    currentLanguage = language,
                    onSelectEngine = { viewModel.setSearchEngine(it) },
                    onSelectLanguage = { viewModel.setAppLanguage(it) },
                    onOpenEdgeLighting = { viewModel.setShowEdgeLightingSheet(true) },
                    onOpenSecurityShield = { viewModel.setShowSecurityShieldSheet(true) },
                    onDismiss = { viewModel.setShowSettingsSheet(false) }
                )
            }
        }
    }
}
