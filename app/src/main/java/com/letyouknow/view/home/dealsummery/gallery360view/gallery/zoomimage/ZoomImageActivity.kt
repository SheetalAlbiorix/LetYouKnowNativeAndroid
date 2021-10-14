package com.letyouknow.view.home.dealsummery.gallery360view.gallery.zoomimage

import android.app.Activity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import com.letyouknow.R
import com.letyouknow.base.BaseActivity
import com.letyouknow.databinding.ActivityZoomImageBinding
import com.letyouknow.utils.AppGlobal
import com.pionymessenger.utils.Constant
import kotlinx.android.synthetic.main.activity_zoom_image.*
import kotlinx.android.synthetic.main.layout_toolbar.*

class ZoomImageActivity : BaseActivity() {
    private lateinit var binding: ActivityZoomImageBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_zoom_image)
        init()
    }

    private fun init() {
        binding = DataBindingUtil.setContentView(this, R.layout.activity_zoom_image)
        if (intent.hasExtra(Constant.ARG_IMAGE_URL)) {
//            pager.currentItem =intent.getIntExtra(ARG_TYPE_VIEW,0)
            var data = intent.getStringExtra(Constant.ARG_IMAGE_URL)
            AppGlobal.loadImageUrl(this.applicationContext, imViewedImage, data.toString())
        }
        backButton()
    }

    private fun backButton() {
        toolbar.setNavigationIcon(R.drawable.ic_back)
        toolbar.setTitleTextColor(resources.getColor(R.color.black))

        setSupportActionBar(toolbar)
        if (supportActionBar != null) {
            supportActionBar!!.setDisplayHomeAsUpEnabled(true)
            supportActionBar!!.setDisplayShowHomeEnabled(true)
        }
    }

    override fun getViewActivity(): Activity? {
        return this
    }

    override fun onNetworkStateChange(isConnect: Boolean) {
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}