package com.letyouknow.view.privacypolicy

import android.app.Activity
import android.os.Bundle
import android.view.View
import android.webkit.WebView
import android.webkit.WebViewClient
import com.letyouknow.R
import com.letyouknow.base.BaseActivity
import com.pionymessenger.utils.Constant.Companion.ARG_POLICY
import kotlinx.android.synthetic.main.activity_privacy_policy_terms_cond.*


class PrivacyPolicyTermsCondActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_privacy_policy_terms_cond)
        init()
    }

    private fun init() {
        if (intent.hasExtra(ARG_POLICY)) {
            webView.webViewClient = MyBrowser()
            webView.getSettings().loadsImagesAutomatically = true
            webView.getSettings().javaScriptEnabled = true
            webView.scrollBarStyle = View.SCROLLBARS_INSIDE_OVERLAY
            webView.loadUrl(intent.getStringExtra(ARG_POLICY)!!)
        }
    }

    override fun getViewActivity(): Activity {
        return this
    }

    override fun onNetworkStateChange(isConnect: Boolean) {
    }
}

private class MyBrowser() : WebViewClient() {
    override fun shouldOverrideUrlLoading(view: WebView, url: String?): Boolean {
        view.loadUrl(url!!)
        return true
    }
}