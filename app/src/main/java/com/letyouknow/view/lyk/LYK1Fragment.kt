package com.letyouknow.view.lyk

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
import com.google.gson.Gson
import com.letyouknow.R
import com.letyouknow.base.BaseFragment
import com.letyouknow.retrofit.viewmodel.LYK1ViewModel
import com.letyouknow.utils.Constant
import kotlinx.android.synthetic.main.fragment_lyk1.*
import java.text.NumberFormat
import java.util.*


class LYK1Fragment : BaseFragment(), View.OnClickListener {

    private lateinit var lyKViewModel: LYK1ViewModel
    private lateinit var animBlink: Animation
    private lateinit var animSlideRightToLeft: Animation
    private lateinit var animSlideLeftToRight: Animation


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_lyk1, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init()
    }

    private fun init() {
        initViewModel()
        lyKViewModel.setTimerPrefData()
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
        lyKViewModel.liveDataPromotion?.observe(requireActivity()) { data ->
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
        lyKViewModel.callPromotionAPI()
    }

    private fun initHandler() {
        if (::lyKViewModel.isInitialized) {
            lyKViewModel.startHandler()
        }
    }

    private fun initData() {
        lyKViewModel.run {
            if (!TextUtils.isEmpty(yearId)) {
                tvYear.text = yearStr
                lyKViewModel.isEnableField(true, true, false, false, false, false, false, false)
            }
            if (!TextUtils.isEmpty(makeId)) {
                tvMake.text = makeStr
                lyKViewModel.isEnableField(true, true, true, false, false, false, false, false)
            }
            if (!TextUtils.isEmpty(modelId)) {
                tvModel.text = modelStr
                lyKViewModel.isEnableField(true, true, true, true, false, false, false, false)
            }
            if (!TextUtils.isEmpty(trimId)) {
                tvTrim.text = trimStr
                lyKViewModel.isEnableField(true, true, true, true, true, false, false, false)
            }
            if (!TextUtils.isEmpty(extColorId)) {
                tvExterior.text = extColorStr
                lyKViewModel.isEnableField(true, true, true, true, true, true, false, false)
            }
            if (!TextUtils.isEmpty(intColorId)) {
                tvInterior.text = intColorStr
                lyKViewModel.isEnableField(true, true, true, true, true, true, true, false)
            }
            if (!prefSubmitPriceData.packagesData.isNullOrEmpty()) {
                lyKViewModel.isEnableField(true, true, true, true, true, true, true, true)
                var isFirst = true
                var packagesStr = ""
                for (i in 0 until prefSubmitPriceData.packagesData!!.size) {
                    if (prefSubmitPriceData.packagesData!![i].isSelect == true || prefSubmitPriceData.packagesData!![i].isOtherSelect == true
                    ) {
                        if (isFirst) {
                            packagesStr =
                                prefSubmitPriceData.packagesData!![i].packageName.toString()
                            isFirst = false
                        } else {
                            packagesStr =
                                packagesStr + ", " + prefSubmitPriceData.packagesData!![i].packageName.toString()
                        }
                    }
                }
                tvPackages.text = packagesStr
                tvOptionsAccessories.isEnabled = true
            } else {
            }

            if (!prefSubmitPriceData.optionsData.isNullOrEmpty()) {
                var optionsStr = ""
                var isFirst = true
                for (i in 0 until prefSubmitPriceData.optionsData!!.size) {
                    if (prefSubmitPriceData.optionsData!![i].isSelect == true || prefSubmitPriceData.optionsData!![i].isOtherSelect == true) {
                        if (isFirst) {
                            optionsStr = prefSubmitPriceData.optionsData!![i].accessory.toString()
                            isFirst = false
                        } else {
                            optionsStr =
                                optionsStr + ", " + prefSubmitPriceData.optionsData!![i].accessory.toString()
                        }
                    }
                }
                tvOptions.text = optionsStr
            }

        }
    }

    private fun initViewModel() {
        lyKViewModel = ViewModelProvider(this)[LYK1ViewModel::class.java]
        lyKViewModel.init(
            requireActivity(), this, tvYear,
            tvMake,
            tvModel,
            tvTrim,
            tvExterior,
            tvInterior,
            tvPackages,
            tvOptionsAccessories
        )
    }

    private fun setOnClickEvent() {
        tvYear.setOnClickListener(this)
        tvMake.setOnClickListener(this)
        tvModel.setOnClickListener(this)
        tvTrim.setOnClickListener(this)
        tvExterior.setOnClickListener(this)
        tvInterior.setOnClickListener(this)
        tvPackages.setOnClickListener(this)
        tvOptionsAccessories.setOnClickListener(this)
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
            R.id.tvPackages -> {
                callPackageAPI()
            }
            R.id.tvOptionsAccessories -> {
                callOptionsAccessoriesAPI()
            }
            R.id.tvTitleYear -> {
                val pos = v.tag as Int
                val data = lyKViewModel.adapterYear.getItem(pos)
                data.let {
                    tvYear.text = it.year
                    lyKViewModel.yearStr = it.year!!
                    lyKViewModel.yearId = it.vehicleYearID!!
                    if (lyKViewModel.yearStr != getString(R.string.year_new_cars_title)) {
                        visibleField(tvErrorYear)
                        lyKViewModel.isEnableField(
                            true,
                            true,
                            false,
                            false,
                            false,
                            false,
                            false,
                            false
                        )
                        lyKViewModel.isReInitField(
                            true,
                            false,
                            false,
                            false,
                            false,
                            false,
                            false,
                            false
                        )
                    }
                }
                lyKViewModel.run {

                    lyKViewModel.popupYear.dismiss()
                }

            }
            R.id.tvTitleMake -> {
                val pos = v.tag as Int
                val data = lyKViewModel.adapterMake.getItem(pos)
                data.let {
                    tvMake.text = it.make
                    lyKViewModel.makeStr = it.make!!
                    lyKViewModel.makeId = it.vehicleMakeID!!
                    if (lyKViewModel.makeStr != getString(R.string.make_title)) {
                        visibleField(tvErrorMake)
                        lyKViewModel.isEnableField(
                            true,
                            true,
                            true,
                            false,
                            false,
                            false,
                            false,
                            false
                        )
                        lyKViewModel.isReInitField(
                            true,
                            true,
                            false,
                            false,
                            false,
                            false,
                            false,
                            false
                        )
                    }
                }
                lyKViewModel.run {
                    lyKViewModel.popupMake.dismiss()
                }

            }
            R.id.tvTitleModel -> {
                val pos = v.tag as Int
                val data = lyKViewModel.adapterModel.getItem(pos)
                data.let {
                    tvModel.text = it.model
                    lyKViewModel.modelStr = it.model!!
                    lyKViewModel.modelId = it.vehicleModelID!!
                    if (lyKViewModel.modelStr != getString(R.string.model_title)) {
                        visibleField(tvErrorModel)
                        lyKViewModel.isEnableField(
                            true,
                            true,
                            true,
                            true,
                            false,
                            false,
                            false,
                            false
                        )
                        lyKViewModel.isReInitField(
                            true,
                            true,
                            true,
                            false,
                            false,
                            false,
                            false,
                            false
                        )
                    }
                }
                lyKViewModel.run {
                    lyKViewModel.popupModel.dismiss()
                }

            }
            R.id.tvTitleTrim -> {
                val pos = v.tag as Int
                val data = lyKViewModel.adapterTrim.getItem(pos)
                data.let {
                    tvTrim.text = it.trim
                    lyKViewModel.trimStr = it.trim!!
                    lyKViewModel.trimId = it.vehicleTrimID!!
                    if (lyKViewModel.trimStr != getString(R.string.trim_title)) {
                        visibleField(tvErrorTrim)
                        lyKViewModel.isEnableField(
                            true,
                            true,
                            true,
                            true,
                            true,
                            false,
                            false,
                            false
                        )
                        lyKViewModel.isReInitField(
                            true,
                            true,
                            true,
                            true,
                            false,
                            false,
                            false,
                            false
                        )
                    }
                }
                lyKViewModel.run {
                    lyKViewModel.popupTrim.dismiss()
                }

            }
            R.id.tvTitleExterior -> {
                val pos = v.tag as Int
                val data = lyKViewModel.adapterExterior.getItem(pos)
                data.let {
                    tvExterior.text = it.exteriorColor
                    lyKViewModel.extColorStr = it.exteriorColor!!
                    lyKViewModel.extColorId = it.vehicleExteriorColorID!!
                    if (lyKViewModel.extColorStr != getString(R.string.exterior_color_title)) {
                        visibleField(tvErrorExterior)
                        lyKViewModel.isEnableField(true, true, true, true, true, true, false, false)
                        lyKViewModel.isReInitField(
                            true,
                            true,
                            true,
                            true,
                            true,
                            false,
                            false,
                            false
                        )
                    }
                }
                lyKViewModel.run {
                    lyKViewModel.popupExterior.dismiss()
                }

            }
            R.id.tvTitleInterior -> {
                val pos = v.tag as Int
                val data = lyKViewModel.adapterInterior.getItem(pos)
                data.let {
                    tvInterior.text = it.interiorColor
                    lyKViewModel.intColorStr = it.interiorColor!!
                    lyKViewModel.intColorId = it.vehicleInteriorColorID!!
                    if (lyKViewModel.intColorStr != getString(R.string.interior_color_title)) {
                        visibleField(tvErrorInterior)
                        lyKViewModel.isEnableField(true, true, true, true, true, true, true, false)
                        lyKViewModel.isReInitField(true, true, true, true, true, true, false, false)
                    }
                }
                lyKViewModel.run {
                    lyKViewModel.popupInterior.dismiss()
                }
            }
            R.id.tvApplyPackage -> {
                var packagesStr = ""
                var isFirst = true
                for (i in 0 until lyKViewModel.adapterPackages.itemCount) {
                    if (lyKViewModel.adapterPackages.getItem(i).isSelect == true || lyKViewModel.adapterPackages.getItem(
                            i
                        ).isOtherSelect == true
                    ) {
                        if (isFirst) {
                            packagesStr =
                                lyKViewModel.adapterPackages.getItem(i).packageName.toString()
                            isFirst = false
                        } else {
                            packagesStr =
                                packagesStr + ", " + lyKViewModel.adapterPackages.getItem(i).packageName.toString()
                        }
                    }
                }
                if (packagesStr.length > 0) {
                    val ignoreInventory = lyKViewModel.adapterPackages.getItem(0).isSelect == true
                    if (!lyKViewModel.hasMatchPackage && !ignoreInventory) {
                        lyKViewModel.showOtherInventoryEmptyDialog()
                    } else {
                        tvPackages.text = packagesStr
                        tvErrorPackages.visibility = View.GONE
                        tvOptionsAccessories.isEnabled = true
                        tvOptionsAccessories.text = getString(R.string.options_accessories_title)
                        lyKViewModel.dialogPackage.dismiss()
                        lyKViewModel.prefSubmitPriceData.optionsData = ArrayList()
                        lyKViewModel.setLYKPrefData()
                    }
                } else {
                    lyKViewModel.showApplyEmptyDialog()
                }
                lyKViewModel.prefSubmitPriceData = pref?.getSubmitPriceData()!!
                lyKViewModel.prefSubmitPriceData.packagesData =
                    lyKViewModel.adapterPackages.getAll()
                Constant.dismissLoader()
                lyKViewModel.prefSubmitPriceData.optionsData = ArrayList()
                lyKViewModel.setLYKPrefData()
                lyKViewModel.setRadius()
            }
            R.id.tvCancelPackage -> {
                Log.e("cancelPKG", Gson().toJson(lyKViewModel.prefSubmitPriceData.packagesData))
                if (!lyKViewModel.prefSubmitPriceData.packagesData.isNullOrEmpty()) {
                    for (i in 0 until lyKViewModel.prefSubmitPriceData.packagesData!!.size) {
                        lyKViewModel.adapterPackages.update(
                            i,
                            lyKViewModel.prefSubmitPriceData.packagesData!![i]
                        )
                    }
                }
                lyKViewModel.dialogPackage.dismiss()
            }
            R.id.llPackages -> {
                //  Log.e("select1", selectPackageStr)
                val pos = v.tag as Int

                val data = lyKViewModel.adapterPackages.getItem(pos)
                data.isSelect = !data.isSelect!!
                lyKViewModel.adapterPackages.update(pos, data)
                if (data.isSelect!! && pos == 0) {
                    for (i in 1 until lyKViewModel.adapterPackages.itemCount) {
                        val dataPackage = lyKViewModel.adapterPackages.getItem(i)
                        dataPackage.isSelect = false
                        dataPackage.isOtherSelect = false
                        dataPackage.isGray = false
                        lyKViewModel.adapterPackages.update(i, dataPackage)
                    }
                } else {
                    for (i in 1 until lyKViewModel.adapterPackages.itemCount) {
                        val dataPackage = lyKViewModel.adapterPackages.getItem(i)
//                        dataPackage.isOtherSelect = false
//                        dataPackage.isGray = false
                        lyKViewModel.adapterPackages.update(i, dataPackage)
                    }
                    val data0 = lyKViewModel.adapterPackages.getItem(0)
                    data0.isSelect = false
                    lyKViewModel.adapterPackages.update(0, data0)
                    if (!data0.isGray!!)
                        lyKViewModel.callCheckedPackageAPI()
                }
            }
            R.id.tvResetPackage -> {
                lyKViewModel.setLYKPrefData()
                for (i in 0 until lyKViewModel.adapterPackages.itemCount) {
                    val data = lyKViewModel.adapterPackages.getItem(i)
                    data.isSelect = false
                    data.isGray = false
                    data.isOtherSelect = false
                    lyKViewModel.adapterPackages.update(i, data)
                }
                lyKViewModel.prefSubmitPriceData = pref?.getSubmitPriceData()!!
            }
            R.id.tvApplyOption -> {
                var optionsStr = ""
                var isFirst = true
                for (i in 0 until lyKViewModel.adapterOptions.itemCount) {
                    if (lyKViewModel.adapterOptions.getItem(i).isSelect == true || lyKViewModel.adapterOptions.getItem(
                            i
                        ).isOtherSelect == true
                    ) {
                        if (isFirst) {
                            optionsStr = lyKViewModel.adapterOptions.getItem(i).accessory.toString()
                            isFirst = false
                        } else {
                            optionsStr =
                                optionsStr + ", " + lyKViewModel.adapterOptions.getItem(i).accessory.toString()
                        }
                    }
                }
                if (!TextUtils.isEmpty(optionsStr)) {
                    val ignoreInventory = lyKViewModel.adapterOptions.getItem(0).isSelect == true
                    if (!lyKViewModel.hasMatchOptions && !ignoreInventory) {
                        lyKViewModel.showOtherInventoryEmptyDialog()
                    } else {
                        tvErrorOptionsAccessories.visibility = View.GONE
                        tvOptionsAccessories.text = optionsStr
                        lyKViewModel.dialogOptions.dismiss()
                    }
                } else {
                    lyKViewModel.showApplyEmptyDialog()
//                    tvOptionalAccessories.text = "OPTIONS & ACCESSORIES"
                }
                lyKViewModel.prefSubmitPriceData = pref?.getSubmitPriceData()!!
                lyKViewModel.prefSubmitPriceData.optionsData = lyKViewModel.adapterOptions.getAll()
                lyKViewModel.setLYKPrefData()
                lyKViewModel.setRadius()
            }
            R.id.tvResetOption -> {
                lyKViewModel.setLYKPrefData()
                for (i in 0 until lyKViewModel.adapterOptions.itemCount) {
                    val data = lyKViewModel.adapterOptions.getItem(i)
                    data.isSelect = false
                    data.isGray = false
                    data.isOtherSelect = false
                    lyKViewModel.adapterOptions.update(i, data)
                }

                lyKViewModel.prefSubmitPriceData = pref?.getSubmitPriceData()!!
            }
            R.id.tvCancelOption -> {
                for (i in 0 until lyKViewModel.prefSubmitPriceData.optionsData?.size!!) {
                    lyKViewModel.adapterOptions.update(
                        i,
                        lyKViewModel.prefSubmitPriceData.optionsData!![i]
                    )
                }
                lyKViewModel.dialogOptions.dismiss()
            }
            R.id.llOptions -> {
                // Log.e("select1", selectOptionStr)
                val pos = v.tag as Int
                val data = lyKViewModel.adapterOptions.getItem(pos)
                data.isSelect = !data.isSelect!!
                lyKViewModel.adapterOptions.update(pos, data)
                if (data.isSelect!! && pos == 0) {
                    for (i in 1 until lyKViewModel.adapterOptions.itemCount) {
                        val dataOptions = lyKViewModel.adapterOptions.getItem(i)
                        dataOptions.isSelect = false
                        dataOptions.isOtherSelect = false
                        dataOptions.isGray = false
                        lyKViewModel.adapterOptions.update(i, dataOptions)
                    }
                } else {
                    for (i in 1 until lyKViewModel.adapterOptions.itemCount) {
                        val dataOptions = lyKViewModel.adapterOptions.getItem(i)
//                        dataOptions.isOtherSelect = false
//                        dataOptions.isGray = false
                        lyKViewModel.adapterOptions.update(i, dataOptions)
                    }
                    val data0 = lyKViewModel.adapterOptions.getItem(0)
                    data0.isSelect = false
                    lyKViewModel.adapterOptions.update(0, data0)
                    if (!data0.isGray!!)
                        lyKViewModel.callCheckedOptionsAPI()
                }
            }
            R.id.btnSearch -> {
                if (isValid()) {
                    Log.e("data", "ISValid")
                    lyKViewModel.clickSearchBtn()
                }
            }
        }
    }

    private fun callVehicleYearAPI(view: View?) {
        lyKViewModel.getYear(
            view,
            ""
        )
    }

    private fun callVehicleMakeAPI(view: View?) {

        lyKViewModel.getMake(
            view,
            "",
            lyKViewModel.yearId
        )
    }

    private fun callVehicleModelAPI(view: View?) {
        lyKViewModel.getModel(
            view
        )
    }

    private fun callVehicleTrimAPI(view: View?) {
        lyKViewModel.getTrim(
            view
        )
    }

    private fun callVehicleExteriorAPI(view: View?) {
        lyKViewModel.getExteriorColor(
            view
        )
    }

    private fun callVehicleInteriorAPI(view: View?) {
        lyKViewModel.getInteriorColor(
            view
        )
    }

    private fun callPackageAPI() {
        lyKViewModel.getPackages()
    }

    private fun callOptionsAccessoriesAPI() {
        lyKViewModel.getOptionsAccessories()
    }


    private fun isValid(): Boolean {
        when {
            TextUtils.isEmpty(lyKViewModel.yearStr) || lyKViewModel.yearStr == getString(R.string.year_new_cars_title) -> {
                tvErrorYear.visibility = View.VISIBLE
                return false
            }
            TextUtils.isEmpty(lyKViewModel.makeStr) || lyKViewModel.makeStr == getString(R.string.make_title) -> {
                tvErrorMake.visibility = View.VISIBLE
                return false
            }
            TextUtils.isEmpty(lyKViewModel.modelStr) || lyKViewModel.modelStr == getString(R.string.model_title) -> {
                tvErrorModel.visibility = View.VISIBLE
                return false
            }
            TextUtils.isEmpty(lyKViewModel.trimStr) || lyKViewModel.trimStr == getString(R.string.trim_title) -> {
                tvErrorTrim.visibility = View.VISIBLE
                return false
            }
            TextUtils.isEmpty(lyKViewModel.extColorStr) || lyKViewModel.extColorStr == getString(R.string.exterior_color_title) -> {
                tvErrorExterior.visibility = View.VISIBLE
                return false
            }
            TextUtils.isEmpty(lyKViewModel.intColorStr) || lyKViewModel.intColorStr == getString(R.string.interior_color_title) -> {
                tvErrorInterior.visibility = View.VISIBLE
                return false
            }
            tvPackages.text.toString().trim() == getString(R.string.packages_title) -> {
                tvErrorPackages.visibility = View.VISIBLE
                return false
            }
            tvOptionsAccessories.text.toString()
                .trim() == getString(R.string.options_accessories_title) -> {
                tvErrorOptionsAccessories.visibility = View.VISIBLE
                return false
            }
        }
        return true
    }

    private fun visibleField(tvError: TextView) {
        tvError.visibility = View.GONE
    }
}