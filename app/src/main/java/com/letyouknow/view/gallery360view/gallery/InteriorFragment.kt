package com.letyouknow.view.gallery360view.gallery

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
import com.letyouknow.utils.Constant
import com.letyouknow.utils.Constant.Companion.ARG_IMAGE_URL
import com.letyouknow.view.gallery360view.gallery.zoomimage.ZoomImageActivity
import kotlinx.android.synthetic.main.fragment_exterior.*
import org.jetbrains.anko.support.v4.startActivity

class InteriorFragment : BaseFragment(), View.OnClickListener {
    private lateinit var adapterGallery: GalleryAdapter
    lateinit var interiorViewModel: InteriorViewModel

    companion object {
        fun newInstance(id: String?): InteriorFragment {
            val fragment = InteriorFragment()
            val bundle = Bundle()
            bundle.putString(Constant.ARG_IMAGE_ID, id)
            fragment.arguments = bundle
            return fragment
        }
    }

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
        if (arguments?.containsKey(Constant.ARG_IMAGE_ID) == true) {
            val imageId = arguments?.getString(Constant.ARG_IMAGE_ID)
            interiorViewModel = ViewModelProvider(this).get(InteriorViewModel::class.java)
            getInteriorAPI(imageId)
        }
        /*   adapterGallery = GalleryAdapter(R.layout.list_item_gallery, this)
           rvGallery.adapter = adapterGallery
           adapterGallery.addAll(arrayListOf("", "", "", "", "", "", "", "", "", "", "", "", "", ""))*/
    }

    private fun getInteriorAPI(imageId: String?) {

        val request = HashMap<String, Any>()
        request[ApiConstant.ImageId] = imageId!!
        request[ApiConstant.ImageProduct] = ApiConstant.stillSet

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
                val pos = v.tag as Int
                val data = adapterGallery.getItem(pos)
                startActivity<ZoomImageActivity>(ARG_IMAGE_URL to data)
            }
        }
    }
}