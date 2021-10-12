package com.letyouknow.view.home.dealsummery.gallery360view.gallery

import android.view.View
import com.letyouknow.utils.AppGlobal
import com.logispeed.ui.base.BaseAdapter
import kotlinx.android.synthetic.main.list_item_gallery.view.*

class GalleryAdapter(layout: Int, val clickListener: View.OnClickListener) :
    BaseAdapter<String>(layout), BaseAdapter.OnBind<String> {

    init {
        setOnBinding(this)
    }

    override fun onBind(view: View, position: Int, data: String) {
        view.run {
            ivGallery.tag = position
            ivGallery.setOnClickListener(clickListener)
            AppGlobal.loadImageUrl(context, ivGallery, data)
        }
    }
}