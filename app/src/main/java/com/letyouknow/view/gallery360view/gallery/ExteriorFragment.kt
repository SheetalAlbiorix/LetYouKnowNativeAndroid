package com.letyouknow.view.gallery360view.gallery

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.google.gson.Gson
import com.letyouknow.R
import com.letyouknow.base.BaseFragment
import com.letyouknow.retrofit.ApiConstant
import com.letyouknow.retrofit.viewmodel.ExteriorViewModel
import com.letyouknow.retrofit.viewmodel.InteriorViewModel
import com.letyouknow.retrofit.viewmodel.RefreshTokenViewModel
import com.letyouknow.view.gallery360view.gallery.zoomimage.ZoomImageActivity
import com.pionymessenger.utils.Constant
import com.pionymessenger.utils.Constant.Companion.ARG_IMAGE_ID
import com.pionymessenger.utils.Constant.Companion.ARG_IMAGE_URL
import kotlinx.android.synthetic.main.fragment_exterior.*
import org.jetbrains.anko.support.v4.startActivity

class ExteriorFragment : BaseFragment(), View.OnClickListener {
    private lateinit var adapterGallery: GalleryAdapter
    lateinit var exteriorViewModel: ExteriorViewModel
    lateinit var interiorViewModel: InteriorViewModel
    private lateinit var tokenModel: RefreshTokenViewModel
    companion object {
        fun newInstance(id: String?): ExteriorFragment {
            val fragment = ExteriorFragment()
            val bundle = Bundle()
            bundle.putString(ARG_IMAGE_ID, id)
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
        tokenModel = ViewModelProvider(this).get(RefreshTokenViewModel::class.java)
        if (arguments?.containsKey(ARG_IMAGE_ID) == true) {
            val imageId = arguments?.getString(ARG_IMAGE_ID)
            exteriorViewModel = ViewModelProvider(this).get(ExteriorViewModel::class.java)
            interiorViewModel = ViewModelProvider(this).get(InteriorViewModel::class.java)
            getExteriorAPI(imageId)
//            callRefreshTokenApi(imageId!!)
        }
    }

    private fun callRefreshTokenApi(imageId: String) {
        if (Constant.isOnline(requireActivity())) {
            Constant.showLoader(requireActivity())
            val request = java.util.HashMap<String, Any>()
            request[ApiConstant.AuthToken] = pref?.getUserData()?.authToken!!
            request[ApiConstant.RefreshToken] = pref?.getUserData()?.refreshToken!!

            tokenModel.refresh(requireActivity(), request)!!.observe(requireActivity(), { data ->
                val userData = pref?.getUserData()
                userData?.authToken = data.auth_token!!
                userData?.refreshToken = data.refresh_token!!
                pref?.setUserData(Gson().toJson(userData))
                getExteriorAPI(imageId)
            }
            )
        } else {
            Toast.makeText(requireActivity(), Constant.noInternet, Toast.LENGTH_SHORT).show()
        }
    }


    val arImages: ArrayList<String> = ArrayList()

    private fun getExteriorAPI(imageId: String?) {
        if (Constant.isOnline(requireContext())) {
            if (!Constant.isInitProgress()) {
                Constant.showLoader(requireActivity())
            } else if (!Constant.progress.isShowing) {
                Constant.showLoader(requireActivity())
            }
            val request = HashMap<String, Any>()
            request[ApiConstant.ImageId] = imageId!!
            request[ApiConstant.ImageProduct] = "Splash"

            exteriorViewModel.getExterior(this.requireContext(), request)!!
                .observe(this.requireActivity(), Observer { data ->
                    arImages.addAll(data)
                    getInteriorAPI(imageId)

                }
                )
        }
    }

    private fun getInteriorAPI(imageId: String?) {
        if (Constant.isOnline(requireContext())) {
            val request = HashMap<String, Any>()
            request[ApiConstant.ImageId] = imageId!!
            request[ApiConstant.ImageProduct] = ApiConstant.stillSet

            interiorViewModel.getInterior(this.requireContext(), request)!!
                .observe(this.requireActivity(), Observer { data ->

                    if (arImages.size > 0)
                        arImages.addAll(arImages.size - 1, data)
                    else
                        arImages.addAll(data)

                    adapterGallery = GalleryAdapter(R.layout.list_item_gallery, this)
                    rvGallery.adapter = adapterGallery
                    adapterGallery.addAll(arImages)
                    Constant.dismissLoader()
                }
                )
        }
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