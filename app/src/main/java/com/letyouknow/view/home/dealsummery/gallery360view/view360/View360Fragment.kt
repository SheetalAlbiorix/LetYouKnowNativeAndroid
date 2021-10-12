package com.letyouknow.view.home.dealsummery.gallery360view.view360

import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.google.vr.sdk.widgets.pano.VrPanoramaView
import com.letyouknow.R
import com.letyouknow.base.BaseFragment
import kotlinx.android.synthetic.main.fragment_360_view.*

class View360Fragment : BaseFragment() {
    val option = VrPanoramaView.Options().also {
        it.inputType = VrPanoramaView.Options.TYPE_MONO
    }

    //    val DEMO_PANORAMA_LINK = "https://image.shutterstock.com/image-photo/tbilisi-georgia-may-6-2021-260nw-1985063774.jpg"
    val DEMO_PANORAMA_LINK1 = "http://reznik.lt/wp-content/uploads/2017/09/preview3000.jpg"
    val DEMO_PANORAMA_LINK =
        "https://dbhdyzvm8lm25.cloudfront.net/interior_eq_4000/MY2021/14904_ineq_4000.png"

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
        View360()
        interiorView()
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

        Log.d("", imagesTag360)
        web_view.loadDataWithBaseURL(
            "",
            imagesTag360, "text/html", "UTF-8", null
        )
        web_view.scrollBarStyle = View.SCROLLBARS_INSIDE_OVERLAY
        web_view.isScrollbarFadingEnabled = true
    }

    private fun interiorView() {
        Glide.with(this).asBitmap().load(DEMO_PANORAMA_LINK).into(object : CustomTarget<Bitmap>() {
            override fun onLoadCleared(placeholder: Drawable?) {}
            override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                // "option": Declared at the step 3
                vrPanoramaView.loadImageFromBitmap(resource, option)
            }
        })
    }
}