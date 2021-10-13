package com.letyouknow.view.home.dealsummery.gallery360view.view360

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

    private fun View360() {
        var imagesTag360 = ""
        for (i in 1 until 36) {
            if (i < 10) {
                imagesTag360 =
                    imagesTag360 + "<img src=\"https://dbhdyzvm8lm25.cloudfront.net/exterior_036_spinframes_0640/MY2020/13655/13655_sp0640_00" + i + ".jpg\"/>"
            } else {
                imagesTag360 =
                    imagesTag360 + "<img src=\"https://dbhdyzvm8lm25.cloudfront.net/exterior_036_spinframes_0640/MY2020/13655/13655_sp0640_0" + i + ".jpg\"/>"
            }
            //imagesTag360 = imagesTag360 + "<img src=\"file:///android_asset/images/image1_" + i + ".jpg\"/>"
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
    }

    private fun getInteriorAPI() {

        val request = HashMap<String, Any>()
        request[ApiConstant.ImageId] = "14904"
        request[ApiConstant.ImageProduct] = "Interior360Pano"

        interiorViewModel.getInterior(this.requireContext(), request)!!
            .observe(this.requireActivity(), Observer { loginVo ->
                Constant.dismissLoader()
                View360()
                interiorView(loginVo[0])
            }
            )

    }
}