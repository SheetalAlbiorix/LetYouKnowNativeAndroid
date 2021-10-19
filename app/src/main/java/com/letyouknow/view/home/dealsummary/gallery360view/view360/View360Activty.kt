package com.letyouknow.view.home.dealsummary.gallery360view.view360

import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.google.vr.sdk.widgets.pano.VrPanoramaView
import com.letyouknow.R
import kotlinx.android.synthetic.main.activity_view360_activty.*

class View360Activty : AppCompatActivity() {
    val option = VrPanoramaView.Options().also {
        it.inputType = VrPanoramaView.Options.TYPE_MONO
    }
    val DEMO_PANORAMA_LINK = "http://reznik.lt/wp-content/uploads/2017/09/preview3000.jpg"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view360_activty)
        interiorView()
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