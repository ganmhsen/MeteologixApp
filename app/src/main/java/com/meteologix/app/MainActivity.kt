package com.meteologix.app

import android.annotation.SuppressLint
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.os.Bundle
import android.view.KeyEvent
import android.view.View
import android.webkit.CookieManager
import android.webkit.WebChromeClient
import android.webkit.WebResourceError
import android.webkit.WebResourceRequest
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.FrameLayout
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.meteologix.app.databinding.ActivityMainBinding

@SuppressLint("SetJavaScriptEnabled")
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private var webView: WebView? = null
    private var progressBar: ProgressBar? = null
    private var swipeRefreshLayout: SwipeRefreshLayout? = null

    private val targetUrl = "https://meteologix.com/sa/model-charts/standard"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        webView = binding.webView
        progressBar = binding.progressBar
        swipeRefreshLayout = binding.swipeRefreshLayout

        setupWebView()
        setupSwipeRefresh()
        loadUrl()
    }

    private fun setupWebView() {
        val settings = webView?.settings ?: return

        // Enable JavaScript
        settings.javaScriptEnabled = true
        settings.domStorageEnabled = true
        settings.databaseEnabled = true
        settings.cacheMode = WebSettings.LOAD_DEFAULT

        // Enable zoom
        settings.builtInZoomControls = true
        settings.displayZoomControls = false
        settings.useWideViewPort = true

        // Enable wide viewport
        settings.loadWithOverviewMode = true

        // Enable mixed content
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            settings.mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
        }

        // Enable third-party cookies
        CookieManager.getInstance().setAcceptThirdPartyCookies(webView!!, true)

        // User agent
        settings.userAgentString = "Mozilla/5.0 (Linux; Android ${Build.VERSION.RELEASE}; ${Build.MODEL}) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Mobile Safari/537.36 MeteologixApp/1.0"

        // Layout algorithm
        settings.layoutAlgorithm = WebSettings.LayoutAlgorithm.TEXT_AUTOSIZING
        settings.textZoom = 100

        // Media playback
        settings.mediaPlaybackRequiresUserGesture = false

        // Hardware acceleration
        webView?.setLayerType(View.LAYER_TYPE_HARDWARE, null)

        // WebViewClient for navigation
        webView?.webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(view: WebView, request: WebResourceRequest): Boolean {
                val url = request.url.toString()
                if (url.startsWith("http://") || url.startsWith("https://")) {
                    view.loadUrl(url)
                    return true
                }
                return super.shouldOverrideUrlLoading(view, request)
            }

            override fun onPageStarted(view: WebView?, url: String?, favicon: android.graphics.Bitmap?) {
                super.onPageStarted(view, url, favicon)
                progressBar?.visibility = View.VISIBLE
                swipeRefreshLayout?.isRefreshing = true
            }

            override fun onPageFinished(view: WebView?, url: String?) {
                super.onPageFinished(view, url)
                progressBar?.visibility = View.GONE
                swipeRefreshLayout?.isRefreshing = false
                
                // Inject custom CSS for better mobile experience
                injectMobileCSS()
            }

            override fun onReceivedError(view: WebView?, request: WebResourceRequest?, error: WebResourceError?) {
                super.onReceivedError(view, request, error)
                progressBar?.visibility = View.GONE
                swipeRefreshLayout?.isRefreshing = false
                showErrorMessage()
            }
        }

        // WebChromeClient for progress and JavaScript dialogs
        webView?.webChromeClient = object : WebChromeClient() {
            override fun onProgressChanged(view: WebView?, newProgress: Int) {
                super.onProgressChanged(view, newProgress)
                progressBar?.progress = newProgress
            }

            override fun onReceivedTitle(view: WebView?, title: String?) {
                super.onReceivedTitle(view, title)
                // Update toolbar title if needed
            }
        }
    }

    private fun setupSwipeRefresh() {
        swipeRefreshLayout?.setOnRefreshListener {
            webView?.reload()
        }
        swipeRefreshLayout?.setColorSchemeResources(
            android.R.color.holo_blue_bright,
            android.R.color.holo_green_light,
            android.R.color.holo_orange_light,
            android.R.color.holo_red_light
        )
    }

    private fun loadUrl() {
        if (isNetworkAvailable()) {
            webView?.loadUrl(targetUrl)
        } else {
            showNoInternetMessage()
        }
    }

    private fun injectMobileCSS() {
        val css = """
            javascript:(function() {
                var style = document.createElement('style');
                style.innerHTML = `
                    /* Mobile optimizations */
                    html, body { 
                        overflow-x: hidden !important; 
                        -webkit-text-size-adjust: 100%;
                        touch-action: manipulation;
                    }
                    /* Hide desktop-only elements */
                    @media (max-width: 768px) {
                        .desktop-only, .hide-on-mobile { display: none !important; }
                        .sidebar, .nav-sidebar { display: none !important; }
                    }
                    /* Improve map interaction */
                    .map-container, #map, canvas { touch-action: pan-x pan-y pinch-zoom !important; }
                    /* Better button sizes */
                    button, .btn, a[role="button"] { min-height: 44px; min-width: 44px; }
                    /* Hide ads if any */
                    .advertisement, .ads, [class*="ad-"], [id*="ad-"] { display: none !important; }
                    /* Improve dropdowns */
                    select, .dropdown-menu { font-size: 16px !important; }
                `;
                document.head.appendChild(style);
            })()
        """.trimIndent()
        webView?.evaluateJavascript(css, null)
    }

    private fun isNetworkAvailable(): Boolean {
        val connectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val network = connectivityManager.activeNetwork ?: return false
            val capabilities = connectivityManager.getNetworkCapabilities(network) ?: return false
            return capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
        } else {
            val networkInfo = connectivityManager.activeNetworkInfo
            return networkInfo != null && networkInfo.isConnected
        }
    }

    private fun showErrorMessage() {
        runOnUiThread {
            Toast.makeText(this, "خطأ في تحميل الصفحة", Toast.LENGTH_SHORT).show()
        }
    }

    private fun showNoInternetMessage() {
        runOnUiThread {
            Toast.makeText(this, "لا يوجد اتصال بالإنترنت", Toast.LENGTH_LONG).show()
            // Show offline page
            val offlineHtml = """
                <!DOCTYPE html>
                <html dir="rtl" lang="ar">
                <head>
                    <meta charset="UTF-8">
                    <meta name="viewport" content="width=device-width, initial-scale=1.0">
                    <style>
                        body { 
                            font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, sans-serif;
                            display: flex; 
                            flex-direction: column; 
                            align-items: center; 
                            justify-content: center; 
                            height: 100vh; 
                            margin: 0; 
                            background: #f5f5f5;
                            padding: 20px;
                            text-align: center;
                        }
                        .icon { font-size: 64px; margin-bottom: 16px; }
                        h1 { color: #333; margin-bottom: 8px; }
                        p { color: #666; margin-bottom: 24px; }
                        button { 
                            background: #2196F3; 
                            color: white; 
                            border: none; 
                            padding: 12px 24px; 
                            border-radius: 4px; 
                            font-size: 16px;
                            cursor: pointer;
                        }
                    </style>
                </head>
                <body>
                    <div class="icon">🌤️</div>
                    <h1>لا يوجد اتصال بالإنترنت</h1>
                    <p>يرجى التحقق من اتصالك بالشبكة والمحاولة مرة أخرى</p>
                    <button onclick="location.reload()">إعادة المحاولة</button>
                </body>
                </html>
            """.trimIndent()
            webView?.loadDataWithBaseURL(null, offlineHtml, "text/html", "UTF-8", null)
        }
    }

    override fun onBackPressed() {
        if (webView?.canGoBack() == true) {
            webView?.goBack()
        } else {
            super.onBackPressed()
        }
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK && webView?.canGoBack() == true) {
            webView?.goBack()
            return true
        }
        return super.onKeyDown(keyCode, event)
    }

    override fun onPause() {
        super.onPause()
        webView?.onPause()
    }

    override fun onResume() {
        super.onResume()
        webView?.onResume()
    }

    override fun onDestroy() {
        webView?.destroy()
        webView = null
        super.onDestroy()
    }
}