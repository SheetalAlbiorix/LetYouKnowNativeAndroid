package com.letyouknow.view.home.dealsummery.gallery360view.gallery

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.letyouknow.R
import com.letyouknow.base.BaseFragment
import com.letyouknow.view.home.dealsummery.gallery360view.gallery.zoomimage.ZoomImageActivity
import kotlinx.android.synthetic.main.fragment_gallery.*
import org.jetbrains.anko.support.v4.startActivity

class GalleryFragment : BaseFragment(), View.OnClickListener {
    private lateinit var adapterGallery: GalleryAdapter
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_gallery, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init()
    }

    private fun init() {
        adapterGallery = GalleryAdapter(R.layout.list_item_gallery, this)
        rvGallery.adapter = adapterGallery
        adapterGallery.addAll(arrayListOf("", "", "", "", "", "", "", "", "", "", "", "", "", ""))
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.ivGallery -> {
                startActivity<ZoomImageActivity>()
            }
        }
    }
}