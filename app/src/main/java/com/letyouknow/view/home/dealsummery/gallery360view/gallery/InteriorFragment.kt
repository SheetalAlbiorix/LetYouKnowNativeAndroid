package com.letyouknow.view.home.dealsummery.gallery360view.gallery

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.letyouknow.R
import com.letyouknow.base.BaseFragment
import com.letyouknow.retrofit.ApiConstant
import com.letyouknow.retrofit.viewmodel.InteriorViewModel
import com.letyouknow.view.home.dealsummery.gallery360view.gallery.zoomimage.ZoomImageActivity
import com.pionymessenger.utils.Constant
import kotlinx.android.synthetic.main.fragment_exterior.*
import org.jetbrains.anko.support.v4.startActivity

class InteriorFragment : BaseFragment(), View.OnClickListener {
    private lateinit var adapterGallery: GalleryAdapter
    lateinit var interiorViewModel: InteriorViewModel

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
        interiorViewModel = ViewModelProvider(this).get(InteriorViewModel::class.java)
        getInteriorAPI();
        /*   adapterGallery = GalleryAdapter(R.layout.list_item_gallery, this)
           rvGallery.adapter = adapterGallery
           adapterGallery.addAll(arrayListOf("", "", "", "", "", "", "", "", "", "", "", "", "", ""))*/
    }

    private fun getInteriorAPI() {

        val request = HashMap<String, Any>()
        request[ApiConstant.ImageId] = "13655"
        request[ApiConstant.ImageProduct] = "Interior360"

        interiorViewModel.getInterior(this.requireContext(), request)!!
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
                startActivity<ZoomImageActivity>()
            }
        }
    }
}