package com.letyouknow.view.home.dealsummary.gallery360view.view360

import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.google.vr.sdk.widgets.pano.VrPanoramaView
import com.letyouknow.R
import com.letyouknow.base.BaseFragment
import com.letyouknow.retrofit.ApiConstant
import com.letyouknow.retrofit.viewmodel.InteriorViewModel
import com.pionymessenger.utils.Constant
import kotlinx.android.synthetic.main.fragment_360_view.*

class View360Fragment : BaseFragment() {
    lateinit var interiorViewModel: InteriorViewModel

    val option = VrPanoramaView.Options().also {
        it.inputType = VrPanoramaView.Options.TYPE_MONO
    }

    //    val DEMO_PANORAMA_LINK = "https://image.shutterstock.com/image-photo/tbilisi-georgia-may-6-2021-260nw-1985063774.jpg"

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_360_view, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init()
    }

    private fun init() {
        interiorViewModel = ViewModelProvider(this).get(InteriorViewModel::class.java)
        getInteriorAPI();
    }

    private fun View360(list: ArrayList<String>) {
        var imagesTag360 = ""
        print(list.size)
        for (i in 0 until list.size) {
            imagesTag360 = imagesTag360 + "<img src=\"" + list[i] + "\"/>"
        }

        web_view.loadDataWithBaseURL(
            "",
            imagesTag360, "text/html", "UTF-8", null
        )
        web_view.scrollBarStyle = View.SCROLLBARS_INSIDE_OVERLAY
        web_view.isScrollbarFadingEnabled = true
    }

    private fun interiorView(url: String) {
        Glide.with(this).asBitmap().load(url).into(object : CustomTarget<Bitmap>() {
            override fun onLoadCleared(placeholder: Drawable?) {}
            override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                // "option": Declared at the step 3
                vrPanoramaView.loadImageFromBitmap(resource, option)

            }
        })
        getInteriorViewAPI();
    }

    private fun getInteriorAPI() {
        val request = HashMap<String, Any>()
        request[ApiConstant.ImageId] = "14904"
        request[ApiConstant.ImageProduct] = ApiConstant.interior360Pano

        interiorViewModel.getInterior(this.requireContext(), request)!!
            .observe(this.requireActivity(), Observer { loginVo ->
                Constant.dismissLoader()
                interiorView(loginVo[0])
            }
            )

    }

    private fun getInteriorViewAPI() {

        val request = HashMap<String, Any>()
        request[ApiConstant.ImageId] = "13655"
        request[ApiConstant.ImageProduct] = ApiConstant.exterior360

        interiorViewModel.getInterior(this.requireContext(), request)!!
            .observe(this.requireActivity(), Observer { loginVo ->
                Constant.dismissLoader()
                print("data display " + loginVo.size.toString())
                View360(loginVo)
            }
            )

    }
}