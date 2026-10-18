package com.example.ui.browser

import android.app.Application
import android.net.Uri
import android.webkit.CookieManager
import android.webkit.WebStorage
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.AppDatabase
import com.example.data.local.BookmarkEntity
import com.example.data.local.DownloadEntity
import com.example.data.local.HistoryEntity
import com.example.data.model.ActiveDownload
import com.example.data.model.AppLanguage
import com.example.data.model.BrowserTab
import com.example.data.model.EdgeLightingConfig
import com.example.data.model.SearchEngine
import com.example.security.SecurityEngine
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class BrowserViewModel(application: Application) : AndroidViewModel(application) {

    private val db = AppDatabase.getDatabase(application)
    private val bookmarkDao = db.bookmarkDao()
    private val historyDao = db.historyDao()
    private val downloadDao = db.downloadDao()

    val bookmarks = bookmarkDao.getAllBookmarks()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val history = historyDao.getAllHistory()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val savedDownloads = downloadDao.getAllDownloads()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Tabs
    private val _tabs = MutableStateFlow<List<BrowserTab>>(listOf(
        BrowserTab(url = "about:home", title = "Aura Home")
    ))
    val tabs: StateFlow<List<BrowserTab>> = _tabs.asStateFlow()

    private val _currentTabIndex = MutableStateFlow(0)
    val currentTabIndex: StateFlow<Int> = _currentTabIndex.asStateFlow()

    val currentTab: BrowserTab
        get() = _tabs.value.getOrNull(_currentTabIndex.value) ?: BrowserTab()

    // Config & Settings
    private val _searchEngine = MutableStateFlow(SearchEngine.DUCKDUCKGO)
    val searchEngine: StateFlow<SearchEngine> = _searchEngine.asStateFlow()

    private val _edgeLightingConfig = MutableStateFlow(EdgeLightingConfig())
    val edgeLightingConfig: StateFlow<EdgeLightingConfig> = _edgeLightingConfig.asStateFlow()

    private val _appLanguage = MutableStateFlow(AppLanguage.FA) // Default Persian as requested
    val appLanguage: StateFlow<AppLanguage> = _appLanguage.asStateFlow()

    // Security & Anti-Hack Counters
    private val _totalTrackersBlocked = MutableStateFlow(0)
    val totalTrackersBlocked: StateFlow<Int> = _totalTrackersBlocked.asStateFlow()

    private val _adBlockerEnabled = MutableStateFlow(true)
    val adBlockerEnabled: StateFlow<Boolean> = _adBlockerEnabled.asStateFlow()

    private val _antiPhishingEnabled = MutableStateFlow(true)
    val antiPhishingEnabled: StateFlow<Boolean> = _antiPhishingEnabled.asStateFlow()

    private val _forceHttps = MutableStateFlow(true)
    val forceHttps: StateFlow<Boolean> = _forceHttps.asStateFlow()

    // Active in-memory downloads
    private val _activeDownloads = MutableStateFlow<List<ActiveDownload>>(emptyList())
    val activeDownloads: StateFlow<List<ActiveDownload>> = _activeDownloads.asStateFlow()

    // UI Dialog & Sheet states
    private val _showTabsSheet = MutableStateFlow(false)
    val showTabsSheet: StateFlow<Boolean> = _showTabsSheet.asStateFlow()

    private val _showBookmarksHistorySheet = MutableStateFlow(false)
    val showBookmarksHistorySheet: StateFlow<Boolean> = _showBookmarksHistorySheet.asStateFlow()

    private val _showDownloadsSheet = MutableStateFlow(false)
    val showDownloadsSheet: StateFlow<Boolean> = _showDownloadsSheet.asStateFlow()

    private val _showSecurityShieldSheet = MutableStateFlow(false)
    val showSecurityShieldSheet: StateFlow<Boolean> = _showSecurityShieldSheet.asStateFlow()

    private val _showEdgeLightingSheet = MutableStateFlow(false)
    val showEdgeLightingSheet: StateFlow<Boolean> = _showEdgeLightingSheet.asStateFlow()

    private val _showSettingsSheet = MutableStateFlow(false)
    val showSettingsSheet: StateFlow<Boolean> = _showSettingsSheet.asStateFlow()

    // Tab navigation trigger: notifies webview to load
    private val _navigationRequest = MutableStateFlow<String?>(null)
    val navigationRequest: StateFlow<String?> = _navigationRequest.asStateFlow()

    fun requestNavigate(urlOrQuery: String) {
        val target = formatInputToUrl(urlOrQuery)
        updateCurrentTabUrl(target)
        _navigationRequest.value = target
    }

    fun clearNavigationRequest() {
        _navigationRequest.value = null
    }

    /** Updates the current tab's displayed URL so BrowserScreen shows the WebView. */
    private fun updateCurrentTabUrl(newUrl: String) {
        val idx = _currentTabIndex.value
        val list = _tabs.value.toMutableList()
        if (idx in list.indices) {
            val cur = list[idx]
            list[idx] = cur.copy(url = newUrl)
            _tabs.value = list
        }
    }

    private fun formatInputToUrl(input: String): String {
        val trimmed = input.trim()
        if (trimmed == "about:home" || trimmed == "about:blank") return trimmed

        val isUrl = trimmed.startsWith("http://") ||
                trimmed.startsWith("https://") ||
                (trimmed.contains(".") && !trimmed.contains(" ") && trimmed.length > 3)

        return if (isUrl) {
            if (!trimmed.startsWith("http://") && !trimmed.startsWith("https://")) {
                "https://$trimmed"
            } else {
                trimmed
            }
        } else {
            val template = _searchEngine.value.queryUrl
            String.format(template, Uri.encode(trimmed))
        }
    }

    fun newTab(isIncognito: Boolean = false, initialUrl: String = "about:home") {
        val newTab = BrowserTab(
            url = initialUrl,
            title = if (isIncognito) "Incognito" else "New Tab",
            isIncognito = isIncognito
        )
        val updated = _tabs.value + newTab
        _tabs.value = updated
        _currentTabIndex.value = updated.size - 1
        _showTabsSheet.value = false
        requestNavigate(initialUrl)
    }

    fun closeTab(index: Int) {
        val currentList = _tabs.value.toMutableList()
        if (currentList.size <= 1) {
            // Reset to blank home tab instead of closing last tab
            currentList[0] = BrowserTab(url = "about:home", title = "Aura Home")
            _tabs.value = currentList
            _currentTabIndex.value = 0
            requestNavigate("about:home")
            return
        }

        currentList.removeAt(index)
        _tabs.value = currentList

        var newIndex = _currentTabIndex.value
        if (newIndex >= currentList.size) {
            newIndex = currentList.size - 1
        }
        _currentTabIndex.value = newIndex
        requestNavigate(currentList[newIndex].url)
    }

    fun switchTab(index: Int) {
        if (index in _tabs.value.indices) {
            _currentTabIndex.value = index
            _showTabsSheet.value = false
            requestNavigate(_tabs.value[index].url)
        }
    }

    fun toggleDesktopMode() {
        val idx = _currentTabIndex.value
        val list = _tabs.value.toMutableList()
        if (idx in list.indices) {
            val cur = list[idx]
            list[idx] = cur.copy(isDesktopMode = !cur.isDesktopMode)
            _tabs.value = list
            requestNavigate(cur.url) // reload with desktop UA
        }
    }

    fun onPageStarted(url: String) {
        val idx = _currentTabIndex.value
        val list = _tabs.value.toMutableList()
        if (idx in list.indices) {
            val isHttps = url.startsWith("https://")
            list[idx] = list[idx].copy(
                url = url,
                isLoading = true,
                progress = 10,
                isSecureHttps = isHttps,
                blockedTrackersOnPage = 0
            )
            _tabs.value = list
        }
    }

    fun onPageFinished(url: String, title: String?) {
        val idx = _currentTabIndex.value
        val list = _tabs.value.toMutableList()
        if (idx in list.indices) {
            val cleanTitle = if (title.isNullOrBlank()) SecurityEngine.formatDisplayUrl(url) else title
            list[idx] = list[idx].copy(
                url = url,
                title = cleanTitle,
                isLoading = false,
                progress = 100
            )
            _tabs.value = list

            // Record to history if not incognito
            if (!list[idx].isIncognito && url != "about:home" && url != "about:blank") {
                viewModelScope.launch(Dispatchers.IO) {
                    historyDao.insertHistory(
                        HistoryEntity(title = cleanTitle, url = url)
                    )
                }
            }
        }
    }

    fun onProgressChanged(progress: Int) {
        val idx = _currentTabIndex.value
        val list = _tabs.value.toMutableList()
        if (idx in list.indices) {
            list[idx] = list[idx].copy(
                progress = progress,
                isLoading = progress < 100
            )
            _tabs.value = list
        }
    }

    fun onTrackerBlocked() {
        _totalTrackersBlocked.value += 1
        val idx = _currentTabIndex.value
        val list = _tabs.value.toMutableList()
        if (idx in list.indices) {
            list[idx] = list[idx].copy(
                blockedTrackersOnPage = list[idx].blockedTrackersOnPage + 1
            )
            _tabs.value = list
        }
    }

    fun toggleBookmarkCurrentPage() {
        val tab = currentTab
        if (tab.url == "about:home" || tab.url == "about:blank") return

        viewModelScope.launch(Dispatchers.IO) {
            val exists = bookmarkDao.isBookmarked(tab.url)
            if (exists) {
                bookmarkDao.deleteBookmarkByUrl(tab.url)
            } else {
                bookmarkDao.insertBookmark(
                    BookmarkEntity(title = tab.title, url = tab.url)
                )
            }
        }
    }

    fun removeBookmark(bookmark: BookmarkEntity) {
        viewModelScope.launch(Dispatchers.IO) {
            bookmarkDao.deleteBookmark(bookmark)
        }
    }

    fun removeHistoryItem(item: HistoryEntity) {
        viewModelScope.launch(Dispatchers.IO) {
            historyDao.deleteHistory(item)
        }
    }

    fun clearAllHistory() {
        viewModelScope.launch(Dispatchers.IO) {
            historyDao.clearAllHistory()
        }
    }

    fun clearAllBrowserData() {
        viewModelScope.launch(Dispatchers.IO) {
            historyDao.clearAllHistory()
            try {
                CookieManager.getInstance().removeAllCookies(null)
                CookieManager.getInstance().flush()
                WebStorage.getInstance().deleteAllData()
            } catch (_: Exception) {}
        }
    }

    fun addActiveDownload(download: ActiveDownload) {
        _activeDownloads.value = listOf(download) + _activeDownloads.value
    }

    fun setSearchEngine(engine: SearchEngine) {
        _searchEngine.value = engine
    }

    fun setAppLanguage(lang: AppLanguage) {
        _appLanguage.value = lang
    }

    fun updateEdgeLightingConfig(newConfig: EdgeLightingConfig) {
        _edgeLightingConfig.value = newConfig
    }

    fun toggleAdBlocker(enabled: Boolean) {
        _adBlockerEnabled.value = enabled
    }

    fun toggleAntiPhishing(enabled: Boolean) {
        _antiPhishingEnabled.value = enabled
    }

    fun toggleForceHttps(enabled: Boolean) {
        _forceHttps.value = enabled
    }

    // Sheet visibility toggles
    fun setShowTabsSheet(show: Boolean) { _showTabsSheet.value = show }
    fun setShowBookmarksHistorySheet(show: Boolean) { _showBookmarksHistorySheet.value = show }
    fun setShowDownloadsSheet(show: Boolean) { _showDownloadsSheet.value = show }
    fun setShowSecurityShieldSheet(show: Boolean) { _showSecurityShieldSheet.value = show }
    fun setShowEdgeLightingSheet(show: Boolean) { _showEdgeLightingSheet.value = show }
    fun setShowSettingsSheet(show: Boolean) { _showSettingsSheet.value = show }
}
