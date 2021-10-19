package com.letyouknow.view.home.dealsummary.gallery360view.gallery

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.letyouknow.R
import com.letyouknow.base.BaseFragment
import com.letyouknow.retrofit.ApiConstant
import com.letyouknow.retrofit.viewmodel.ExteriorViewModel
import com.letyouknow.view.home.dealsummary.gallery360view.gallery.zoomimage.ZoomImageActivity
import com.pionymessenger.utils.Constant
import com.pionymessenger.utils.Constant.Companion.ARG_IMAGE_URL
import kotlinx.android.synthetic.main.fragment_exterior.*
import org.jetbrains.anko.support.v4.startActivity

class ExteriorFragment : BaseFragment(), View.OnClickListener {
    private lateinit var adapterGallery: GalleryAdapter
    lateinit var exteriorViewModel: ExteriorViewModel
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_exterior, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init()
    }

    private fun init() {
        exteriorViewModel = ViewModelProvider(this).get(ExteriorViewModel::class.java)
        getExteriorAPI();
    }

    private fun getExteriorAPI() {

        val request = HashMap<String, Any>()
        request[ApiConstant.ImageId] = "14170"
        request[ApiConstant.ImageProduct] = "MultiAngle"

        exteriorViewModel.getExterior(this.requireContext(), request)!!
            .observe(this.requireActivity(), Observer { loginVo ->
                Constant.dismissLoader()

                adapterGallery = GalleryAdapter(R.layout.list_item_gallery, this)
                rvGallery.adapter = adapterGallery
                adapterGallery.addAll(loginVo)
            }
            )

    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.ivGallery -> {
                val pos = v.tag as Int
                var data = adapterGallery.getItem(pos).toString()
                startActivity<ZoomImageActivity>(ARG_IMAGE_URL to data)

            }
        }
    }
}