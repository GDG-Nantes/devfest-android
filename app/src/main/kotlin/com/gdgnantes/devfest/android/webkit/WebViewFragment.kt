package com.gdgnantes.devfest.android.webkit

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebView
import com.gdgnantes.devfest.android.R
import com.gdgnantes.devfest.android.features.base.app.BaseFragment
import com.gdgnantes.devfest.android.view.removeFromParent

open class WebViewFragment : BaseFragment() {

    private var _webview: WebView? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val webView = WebView(activity)
        webView.id = R.id.web_view
        return webView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        ensureWebView()
    }

    override fun onResume() {
        super.onResume()
        _webview!!.onResume()
    }

    override fun onPause() {
        _webview!!.onPause()
        super.onPause()
    }

    override fun onDestroyView() {
        _webview!!.removeFromParent()
        _webview!!.destroy()
        _webview = null
        super.onDestroyView()
    }

    val webView: WebView
        get() {
            ensureWebView()
            return _webview!!
        }

    private fun ensureWebView() {
        if (_webview != null) {
            return
        }
        val root = view ?: throw IllegalStateException("Content view not yet created")

        val rawWebView = root.findViewById<View>(R.id.web_view)
        when (rawWebView) {
            null -> throw RuntimeException("Your content must have a WebView whose id attribute is R.id.web_view'")
            !is WebView -> throw RuntimeException("Content has view with id attribute 'R.id.web_view' that is not a WebView")
        }
        _webview = rawWebView as WebView?
    }
}
