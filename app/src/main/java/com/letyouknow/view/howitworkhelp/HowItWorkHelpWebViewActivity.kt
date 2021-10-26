package com.letyouknow.view.howitworkhelp

import android.app.Activity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.webkit.*
import androidx.databinding.DataBindingUtil
import com.letyouknow.R
import com.letyouknow.base.BaseActivity
import com.letyouknow.databinding.ActivityHowItWorkHelpWebViewBinding
import com.pionymessenger.utils.Constant.Companion.ARG_TITLE
import com.pionymessenger.utils.Constant.Companion.ARG_WEB_URL
import kotlinx.android.synthetic.main.activity_how_it_work_help_web_view.*
import kotlinx.android.synthetic.main.layout_toolbar_blue.*

class HowItWorkHelpWebViewActivity : BaseActivity(), View.OnClickListener {

    private lateinit var binding: ActivityHowItWorkHelpWebViewBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_how_it_work_help_web_view)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_how_it_work_help_web_view)
        init()
    }

    private fun init() {
        if (intent.hasExtra(ARG_TITLE) && intent.hasExtra(ARG_WEB_URL)) {
            val url = intent.getStringExtra(ARG_WEB_URL)
            val title = intent.getStringExtra(ARG_TITLE)
            binding.title = title
            webView.settings.loadWithOverviewMode = true
            webView.settings.useWideViewPort = true
            webView.settings.pluginState = WebSettings.PluginState.ON
            webView.settings.domStorageEnabled = true
            webView.settings.javaScriptEnabled = true
            webView.webViewClient = (object : WebViewClient() {
                override fun shouldOverrideUrlLoading(view: WebView?, url: String?): Boolean {
                    view?.loadUrl(url!!)
                    return true
                }

                override fun onReceivedError(
                    view: WebView?,
                    request: WebResourceRequest?,
                    error: WebResourceError?
                ) {
                    super.onReceivedError(view, request, error)
                    Log.e("WebView Error", error.toString())
                }
            })

            webView.loadUrl(url!!)
        }
        ivBack.setOnClickListener(this)
    }

    override fun getViewActivity(): Activity? {
        return this
    }

    override fun onNetworkStateChange(isConnect: Boolean) {

    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.ivBack -> {
                onBackPressed()
            }
        }
    }
}