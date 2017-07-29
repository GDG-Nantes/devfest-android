package com.gdgnantes.devfest.android.features.licenses

import android.os.Bundle
import android.view.View
import com.gdgnantes.devfest.android.features.base.webkit.WebViewFragment

class LicensesFragment : WebViewFragment() {

    companion object {
        fun newInstance() = LicensesFragment()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        webView.loadUrl("file:///android_asset/licenses.html")
    }

}
