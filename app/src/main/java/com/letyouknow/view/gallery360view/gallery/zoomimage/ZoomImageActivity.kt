package com.letyouknow.view.gallery360view.gallery.zoomimage

import android.app.Activity
import android.os.Bundle
import android.view.View
import androidx.databinding.DataBindingUtil
import com.letyouknow.R
import com.letyouknow.base.BaseActivity
import com.letyouknow.databinding.ActivityZoomImageBinding
import com.letyouknow.utils.AppGlobal
import com.pionymessenger.utils.Constant.Companion.ARG_IMAGE_URL
import kotlinx.android.synthetic.main.activity_zoom_image.*
import kotlinx.android.synthetic.main.layout_toolbar.toolbar
import kotlinx.android.synthetic.main.layout_toolbar_blue.*

class ZoomImageActivity : BaseActivity(), View.OnClickListener {
    private lateinit var binding: ActivityZoomImageBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_zoom_image)
        init()
    }

    private fun init() {
        binding = DataBindingUtil.setContentView(this, R.layout.activity_zoom_image)
        binding.title = ""
        if (intent.hasExtra(ARG_IMAGE_URL)) {
//            pager.currentItem =intent.getIntExtra(ARG_TYPE_VIEW,0)
            var data = intent.getStringExtra(ARG_IMAGE_URL)
            AppGlobal.loadImageUrlFitCenter(this.applicationContext, imViewedImage, data.toString())
//            imViewedImage.setZoom(10.0f)
        }
        ivBack.setOnClickListener(this)
//        backButton()
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

    override fun getViewActivity(): Activity {
        return this
    }

    override fun onNetworkStateChange(isConnect: Boolean) {
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.ivBack -> {
                onBackPressed()
            }
        }
    }
}