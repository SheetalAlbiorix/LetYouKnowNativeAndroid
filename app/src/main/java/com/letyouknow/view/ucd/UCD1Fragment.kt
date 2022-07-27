package com.letyouknow.view.ucd

import android.os.Bundle
import android.os.Handler
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.TextView
import androidx.lifecycle.ViewModelProvider
import com.letyouknow.R
import com.letyouknow.base.BaseFragment
import com.letyouknow.retrofit.viewmodel.UCDViewModel
import kotlinx.android.synthetic.main.fragment_ucd1.*
import java.text.NumberFormat
import java.util.*


class UCD1Fragment : BaseFragment(), View.OnClickListener {

    private lateinit var ucdViewModel: UCDViewModel
    private lateinit var animBlink: Animation
    private lateinit var animSlideRightToLeft: Animation
    private lateinit var animSlideLeftToRight: Animation


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_ucd1, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init()
    }

    private fun init() {
        initViewModel()
        ucdViewModel.setTimerPrefData()
        initData()
        setOnClickEvent()
        initHandler()
        setHeaderPromoCodeData()
        setAnimPromo()
    }

    private fun setAnimPromo() {
        animBlink = AnimationUtils.loadAnimation(
            requireActivity(),
            R.anim.anim_blink
        )
        animSlideRightToLeft = AnimationUtils.loadAnimation(
            requireActivity(),
            R.anim.anim_slide_in_right
        )
        animSlideLeftToRight = AnimationUtils.loadAnimation(
            requireActivity(),
            R.anim.anim_slide_in_left
        )
        tvPromo.startAnimation(animBlink)
    }

    private fun setHeaderPromoCodeData() {
        ucdViewModel.liveDataPromotion?.observe(requireActivity()) { data ->
            try {
                if (TextUtils.isEmpty(data.promotionCode) || data.discount == 0.0 || TextUtils.isEmpty(
                        data.endDate
                    )
                ) {
                    tvPromo.visibility = View.GONE
                    llPromoOffer.visibility = View.GONE
                } else {
                    tvPromo.visibility = View.VISIBLE
                    tvEnterPromo.text = resources.getString(
                        R.string.enter_promo_save_more, data.promotionCode,
                        NumberFormat.getCurrencyInstance(Locale.US).format(data.discount)
                    )
                    tvOfferExpire.text = resources.getString(
                        R.string.offer_expires_and_has_no_cash_value,
                        data.endDate
                    )
                    if (TextUtils.isEmpty(data.promotionCode)) {
                        llPromoHeader.visibility = View.GONE
                    }
                }
            } catch (e: Exception) {

            }
        }
        ucdViewModel.callPromotionAPI()
    }

    private fun initHandler() {
        if (::ucdViewModel.isInitialized) {
            ucdViewModel.startHandler()
        }
    }

    private fun initData() {
        ucdViewModel.run {
            if (!TextUtils.isEmpty(yearId)) {
                tvYear.text = yearStr
                ucdViewModel.isEnableField(true, true, false, false, false, false)
            }
            if (!TextUtils.isEmpty(makeId)) {
                tvMake.text = makeStr
                ucdViewModel.isEnableField(true, true, true, false, false, false)
            }
            if (!TextUtils.isEmpty(modelId)) {
                tvModel.text = modelStr
                ucdViewModel.isEnableField(true, true, true, true, false, false)
            }
            if (!TextUtils.isEmpty(trimId)) {
                tvTrim.text = trimStr
                ucdViewModel.isEnableField(true, true, true, true, true, false)
            }
            if (!TextUtils.isEmpty(extColorId)) {
                tvExterior.text = extColorStr
                ucdViewModel.isEnableField(true, true, true, true, true, true)
            }
            if (!TextUtils.isEmpty(intColorId)) {
                tvInterior.text = intColorStr
                ucdViewModel.isEnableField(true, true, true, true, true, true)
            }
        }
    }

    private fun initViewModel() {
        ucdViewModel = ViewModelProvider(this)[UCDViewModel::class.java]
        ucdViewModel.init(
            requireActivity(), this, tvYear,
            tvMake,
            tvModel,
            tvTrim,
            tvExterior,
            tvInterior
        )
    }

    private fun setOnClickEvent() {
        tvYear.setOnClickListener(this)
        tvMake.setOnClickListener(this)
        tvModel.setOnClickListener(this)
        tvTrim.setOnClickListener(this)
        tvExterior.setOnClickListener(this)
        tvInterior.setOnClickListener(this)
        btnSearch.setOnClickListener(this)
        tvPromo.setOnClickListener(this)
        ivClosePromo.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.tvPromo -> {
                tvPromo.clearAnimation()
                tvPromo.visibility = View.GONE
                llPromoOffer.visibility = View.VISIBLE
                llPromoOffer.startAnimation(animSlideRightToLeft)
            }
            R.id.ivClosePromo -> {
                llPromoOffer.startAnimation(animSlideLeftToRight)
                Handler().postDelayed({
                    llPromoOffer.visibility = View.GONE
                    tvPromo.startAnimation(animBlink)
                    tvPromo.visibility = View.VISIBLE
                }, 400)
            }
            R.id.tvYear -> {
                callVehicleYearAPI(v)
            }
            R.id.tvMake -> {
                callVehicleMakeAPI(v)
            }
            R.id.tvModel -> {
                callVehicleModelAPI(v)
            }
            R.id.tvTrim -> {
                callVehicleTrimAPI(v)
            }
            R.id.tvExterior -> {
                callVehicleExteriorAPI(v)
            }
            R.id.tvInterior -> {
                callVehicleInteriorAPI(v)
            }
            R.id.tvTitleYear -> {
                val pos = v.tag as Int
                val data = ucdViewModel.adapterYear.getItem(pos)
                data.let {
                    tvYear.text = it.year
                    ucdViewModel.yearStr = it.year!!
                    ucdViewModel.yearId = it.vehicleYearID!!
                    if (ucdViewModel.yearStr != getString(R.string.year_new_cars_title)) {
                        visibleField(tvErrorYear)
                        ucdViewModel.isEnableField(true, true, false, false, false, false)
                        ucdViewModel.isReInitField(true, false, false, false, false, false)
                    }
                }
                ucdViewModel.run {

                    ucdViewModel.popupYear.dismiss()
                }

            }
            R.id.tvTitleMake -> {
                val pos = v.tag as Int
                val data = ucdViewModel.adapterMake.getItem(pos)
                data.let {
                    tvMake.text = it.make
                    ucdViewModel.makeStr = it.make!!
                    ucdViewModel.makeId = it.vehicleMakeID!!
                    if (ucdViewModel.makeStr != getString(R.string.make_title)) {
                        visibleField(tvErrorMake)
                        ucdViewModel.isEnableField(true, true, true, false, false, false)
                        ucdViewModel.isReInitField(true, true, false, false, false, false)
                    }
                }
                ucdViewModel.run {
                    ucdViewModel.popupMake.dismiss()
                }

            }
            R.id.tvTitleModel -> {
                val pos = v.tag as Int
                val data = ucdViewModel.adapterModel.getItem(pos)
                data.let {
                    tvModel.text = it.model
                    ucdViewModel.modelStr = it.model!!
                    ucdViewModel.modelId = it.vehicleModelID!!
                    if (ucdViewModel.modelStr != getString(R.string.model_title)) {
                        visibleField(tvErrorModel)
                        ucdViewModel.isEnableField(true, true, true, true, false, false)
                        ucdViewModel.isReInitField(true, true, true, false, false, false)
                    }
                }
                ucdViewModel.run {
                    ucdViewModel.popupModel.dismiss()
                }

            }
            R.id.tvTitleTrim -> {
                val pos = v.tag as Int
                val data = ucdViewModel.adapterTrim.getItem(pos)
                data.let {
                    tvTrim.text = it.trim
                    ucdViewModel.trimStr = it.trim!!
                    ucdViewModel.trimId = it.vehicleTrimID!!
                    if (ucdViewModel.trimStr != getString(R.string.trim_title)) {
                        visibleField(tvErrorTrim)
                        ucdViewModel.isEnableField(true, true, true, true, true, false)
                        ucdViewModel.isReInitField(true, true, true, true, false, false)
                    }
                }
                ucdViewModel.run {
                    ucdViewModel.popupTrim.dismiss()
                }

            }
            R.id.tvTitleExterior -> {
                val pos = v.tag as Int
                val data = ucdViewModel.adapterExterior.getItem(pos)
                data.let {
                    tvExterior.text = it.exteriorColor
                    ucdViewModel.extColorStr = it.exteriorColor!!
                    ucdViewModel.extColorId = it.vehicleExteriorColorID!!
                    if (ucdViewModel.extColorStr != getString(R.string.exterior_color_title)) {
                        visibleField(tvErrorExterior)
                        ucdViewModel.isEnableField(true, true, true, true, true, true)
                        ucdViewModel.isReInitField(true, true, true, true, true, false)
                    }
                }
                ucdViewModel.run {
                    ucdViewModel.popupExterior.dismiss()
                }

            }
            R.id.tvTitleInterior -> {
                val pos = v.tag as Int
                val data = ucdViewModel.adapterInterior.getItem(pos)
                data.let {
                    tvInterior.text = it.interiorColor
                    ucdViewModel.intColorStr = it.interiorColor!!
                    ucdViewModel.intColorId = it.vehicleInteriorColorID!!
                    if (ucdViewModel.intColorStr != getString(R.string.interior_color_title)) {
                        visibleField(tvErrorInterior)
                        ucdViewModel.isEnableField(true, true, true, true, true, true)
                        ucdViewModel.isReInitField(true, true, true, true, true, true)
                    }
                }
                ucdViewModel.run {
                    ucdViewModel.popupInterior.dismiss()
                }
            }
            R.id.btnSearch -> {
                if (isValid()) {
                    Log.e("data", "ISValid")
                    ucdViewModel.clickSearchBtn()
                }
            }
        }
    }

    private fun callVehicleYearAPI(view: View?) {
        ucdViewModel.getYear(
            view,
            ""
        )
    }

    private fun callVehicleMakeAPI(view: View?) {

        ucdViewModel.getMake(
            view,
            "",
            ucdViewModel.yearId
        )
    }

    private fun callVehicleModelAPI(view: View?) {
        ucdViewModel.getModel(
            view
        )
    }

    private fun callVehicleTrimAPI(view: View?) {
        ucdViewModel.getTrim(
            view
        )
    }

    private fun callVehicleExteriorAPI(view: View?) {
        ucdViewModel.getExteriorColor(
            view
        )
    }

    private fun callVehicleInteriorAPI(view: View?) {
        ucdViewModel.getInteriorColor(
            view
        )
    }

    private fun isValid(): Boolean {
        when {
            TextUtils.isEmpty(ucdViewModel.yearStr) || ucdViewModel.yearStr == getString(R.string.year_new_cars_title) -> {
                tvErrorYear.visibility = View.VISIBLE
                return false
            }
            TextUtils.isEmpty(ucdViewModel.makeStr) || ucdViewModel.makeStr == getString(R.string.make_title) -> {
                tvErrorMake.visibility = View.VISIBLE
                return false
            }
            TextUtils.isEmpty(ucdViewModel.modelStr) || ucdViewModel.modelStr == getString(R.string.model_title) -> {
                tvErrorModel.visibility = View.VISIBLE
                return false
            }
            TextUtils.isEmpty(ucdViewModel.trimStr) || ucdViewModel.trimStr == getString(R.string.trim_title) -> {
                tvErrorTrim.visibility = View.VISIBLE
                return false
            }
            TextUtils.isEmpty(ucdViewModel.extColorStr) || ucdViewModel.extColorStr == getString(R.string.exterior_color_title) -> {
                tvErrorExterior.visibility = View.VISIBLE
                return false
            }
            TextUtils.isEmpty(ucdViewModel.intColorStr) || ucdViewModel.intColorStr == getString(R.string.interior_color_title) -> {
                tvErrorInterior.visibility = View.VISIBLE
                return false
            }
        }
        return true
    }

    private fun visibleField(tvError: TextView) {
        tvError.visibility = View.GONE
    }
}