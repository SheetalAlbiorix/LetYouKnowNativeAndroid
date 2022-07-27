package com.letyouknow.view.lcd

import android.os.Bundle
import android.os.Handler
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
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
import com.letyouknow.retrofit.viewmodel.LCDViewModel
import com.letyouknow.utils.Constant
import kotlinx.android.synthetic.main.fragment_lcd.*
import java.text.NumberFormat
import java.util.*


class LCDFragment : BaseFragment(), View.OnClickListener {

    private lateinit var lcdViewModel: LCDViewModel
    private lateinit var animBlink: Animation
    private lateinit var animSlideRightToLeft: Animation
    private lateinit var animSlideLeftToRight: Animation


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_lcd, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init()
    }

    private fun init() {
        initViewModel()
        lcdViewModel.setTimerPrefData()
        initData()
        setOnClickEvent()
        initHandler()
        setHeaderPromoCodeData()
        setAnimPromo()
        onChangeZipCode()
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
        lcdViewModel.liveDataPromotion?.observe(requireActivity()) { data ->
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
        lcdViewModel.callPromotionAPI()
    }

    private fun initHandler() {
        if (::lcdViewModel.isInitialized) {
            lcdViewModel.startHandler()
        }
    }

    private fun initData() {
        lcdViewModel.run {
            if (!TextUtils.isEmpty(zipCode)) {
                edtZipCode.setText(zipCode)
                isEnableField(
                    true,
                    zipCode.length == 5,
                    false,
                    false,
                    false,
                    false,
                    false,
                    false,
                    false
                )
            }
            if (!TextUtils.isEmpty(yearId)) {
                tvYear.text = yearStr
                isEnableField(true, true, true, false, false, false, false, false, false)
            }
            if (!TextUtils.isEmpty(makeId)) {
                tvMake.text = makeStr
                isEnableField(true, true, true, true, false, false, false, false, false)
            }
            if (!TextUtils.isEmpty(modelId)) {
                tvModel.text = modelStr
                isEnableField(true, true, true, true, true, false, false, false, false)
            }
            if (!TextUtils.isEmpty(trimId)) {
                tvTrim.text = trimStr
                isEnableField(true, true, true, true, true, true, false, false, false)
            }
            if (!TextUtils.isEmpty(extColorId)) {
                tvExterior.text = extColorStr
                isEnableField(true, true, true, true, true, true, true, false, false)
            }
            if (!TextUtils.isEmpty(intColorId)) {
                tvInterior.text = intColorStr
                isEnableField(true, true, true, true, true, true, true, true, false)
            }
            if (!prefLCDData.packagesData.isNullOrEmpty()) {
                isEnableField(true, true, true, true, true, true, true, true, true)
                var isFirst = true
                var packagesStr = ""
                for (i in 0 until prefLCDData.packagesData!!.size) {
                    if (prefLCDData.packagesData!![i].isSelect == true || prefLCDData.packagesData!![i].isOtherSelect == true
                    ) {
                        if (isFirst) {
                            packagesStr =
                                prefLCDData.packagesData!![i].packageName.toString()
                            isFirst = false
                        } else {
                            packagesStr =
                                packagesStr + ", " + prefLCDData.packagesData!![i].packageName.toString()
                        }
                    }
                }
                tvPackages.text = packagesStr
                tvOptionsAccessories.isEnabled = true
            }

            if (!prefLCDData.optionsData.isNullOrEmpty()) {
                var optionsStr = ""
                var isFirst = true
                for (i in 0 until prefLCDData.optionsData!!.size) {
                    if (prefLCDData.optionsData!![i].isSelect == true || prefLCDData.optionsData!![i].isOtherSelect == true) {
                        if (isFirst) {
                            optionsStr = prefLCDData.optionsData!![i].accessory.toString()
                            isFirst = false
                        } else {
                            optionsStr =
                                optionsStr + ", " + prefLCDData.optionsData!![i].accessory.toString()
                        }
                    }
                }
                tvOptions.text = optionsStr
            }
        }
    }

    private fun initViewModel() {
        lcdViewModel = ViewModelProvider(this)[LCDViewModel::class.java]
        lcdViewModel.init(
            requireActivity(), this,
            edtZipCode,
            tvYear,
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
                val data = lcdViewModel.adapterYear.getItem(pos)
                data.let {
                    tvYear.text = it.year
                    lcdViewModel.yearStr = it.year!!
                    lcdViewModel.yearId = it.vehicleYearID!!
                    if (lcdViewModel.yearStr != getString(R.string.year_new_cars_title)) {
                        visibleField(tvErrorYear)
                        isEnableField(true, true, true, false, false, false, false, false, false)
                        isReInitField(true, true, false, false, false, false, false, false, false)
                    }
                }
                lcdViewModel.run {
                    lcdViewModel.popupYear.dismiss()
                }
            }
            R.id.tvTitleMake -> {
                val pos = v.tag as Int
                val data = lcdViewModel.adapterMake.getItem(pos)
                data.let {
                    tvMake.text = it.make
                    lcdViewModel.makeStr = it.make!!
                    lcdViewModel.makeId = it.vehicleMakeID!!
                    if (lcdViewModel.makeStr != getString(R.string.make_title)) {
                        visibleField(tvErrorMake)
                        isEnableField(true, true, true, true, false, false, false, false, false)
                        isReInitField(true, true, true, false, false, false, false, false, false)
                    }
                }
                lcdViewModel.run {
                    lcdViewModel.popupMake.dismiss()
                }

            }
            R.id.tvTitleModel -> {
                val pos = v.tag as Int
                val data = lcdViewModel.adapterModel.getItem(pos)
                data.let {
                    tvModel.text = it.model
                    lcdViewModel.modelStr = it.model!!
                    lcdViewModel.modelId = it.vehicleModelID!!
                    if (lcdViewModel.modelStr != getString(R.string.model_title)) {
                        visibleField(tvErrorModel)
                        isEnableField(true, true, true, true, true, false, false, false, false)
                        isReInitField(true, true, true, true, false, false, false, false, false)
                    }
                }
                lcdViewModel.run {
                    lcdViewModel.popupModel.dismiss()
                }

            }
            R.id.tvTitleTrim -> {
                val pos = v.tag as Int
                val data = lcdViewModel.adapterTrim.getItem(pos)
                data.let {
                    tvTrim.text = it.trim
                    lcdViewModel.trimStr = it.trim!!
                    lcdViewModel.trimId = it.vehicleTrimID!!
                    if (lcdViewModel.trimStr != getString(R.string.trim_title)) {
                        visibleField(tvErrorTrim)
                        isEnableField(true, true, true, true, true, true, false, false, false)
                        isReInitField(true, true, true, true, true, false, false, false, false)
                    }
                }
                lcdViewModel.run {
                    lcdViewModel.popupTrim.dismiss()
                }

            }
            R.id.tvTitleExterior -> {
                val pos = v.tag as Int
                val data = lcdViewModel.adapterExterior.getItem(pos)
                data.let {
                    tvExterior.text = it.exteriorColor
                    lcdViewModel.extColorStr = it.exteriorColor!!
                    lcdViewModel.extColorId = it.vehicleExteriorColorID!!
                    if (lcdViewModel.extColorStr != getString(R.string.exterior_color_title)) {
                        visibleField(tvErrorExterior)
                        isEnableField(true, true, true, true, true, true, true, false, false)
                        isReInitField(true, true, true, true, true, true, false, false, false)
                    }
                }
                lcdViewModel.run {
                    lcdViewModel.popupExterior.dismiss()
                }

            }
            R.id.tvTitleInterior -> {
                val pos = v.tag as Int
                val data = lcdViewModel.adapterInterior.getItem(pos)
                data.let {
                    tvInterior.text = it.interiorColor
                    lcdViewModel.intColorStr = it.interiorColor!!
                    lcdViewModel.intColorId = it.vehicleInteriorColorID!!
                    if (lcdViewModel.intColorStr != getString(R.string.interior_color_title)) {
                        visibleField(tvErrorInterior)
                        isEnableField(true, true, true, true, true, true, true, true, false)
                        isReInitField(true, true, true, true, true, true, true, false, false)
                    }
                }
                lcdViewModel.run {
                    lcdViewModel.popupInterior.dismiss()
                }
            }
            R.id.tvApplyPackage -> {
                var packagesStr = ""
                var isFirst = true
                for (i in 0 until lcdViewModel.adapterPackages.itemCount) {
                    if (lcdViewModel.adapterPackages.getItem(i).isSelect == true || lcdViewModel.adapterPackages.getItem(
                            i
                        ).isOtherSelect == true
                    ) {
                        if (isFirst) {
                            packagesStr =
                                lcdViewModel.adapterPackages.getItem(i).packageName.toString()
                            isFirst = false
                        } else {
                            packagesStr =
                                packagesStr + ", " + lcdViewModel.adapterPackages.getItem(i).packageName.toString()
                        }
                    }
                }
                if (packagesStr.length > 0) {
                    val ignoreInventory = lcdViewModel.adapterPackages.getItem(0).isSelect == true
                    if (!lcdViewModel.hasMatchPackage && !ignoreInventory) {
                        lcdViewModel.showOtherInventoryEmptyDialog()
                    } else {
                        tvPackages.text = packagesStr
                        tvErrorPackages.visibility = View.GONE
                        tvOptionsAccessories.isEnabled = true
                        tvOptionsAccessories.text = getString(R.string.options_accessories_title)
                        lcdViewModel.dialogPackage.dismiss()
                        lcdViewModel.prefLCDData.optionsData = ArrayList()
                        lcdViewModel.setLCDPrefData()
                    }
                } else {
                    lcdViewModel.showApplyEmptyDialog()
                }
                lcdViewModel.prefLCDData = pref?.getOneDealNearYouData()!!
                lcdViewModel.prefLCDData.packagesData =
                    lcdViewModel.adapterPackages.getAll()
                Constant.dismissLoader()
                lcdViewModel.prefLCDData.optionsData = ArrayList()
                lcdViewModel.setLCDPrefData()
                lcdViewModel.setRadius()
            }
            R.id.tvCancelPackage -> {
                Log.e("cancelPKG", Gson().toJson(lcdViewModel.prefLCDData.packagesData))
                if (!lcdViewModel.prefLCDData.packagesData.isNullOrEmpty()) {
                    for (i in 0 until lcdViewModel.prefLCDData.packagesData!!.size) {
                        lcdViewModel.adapterPackages.update(
                            i,
                            lcdViewModel.prefLCDData.packagesData!![i]
                        )
                    }
                }
                lcdViewModel.dialogPackage.dismiss()
            }
            R.id.llPackages -> {
                //  Log.e("select1", selectPackageStr)
                val pos = v.tag as Int

                val data = lcdViewModel.adapterPackages.getItem(pos)
                data.isSelect = !data.isSelect!!
                lcdViewModel.adapterPackages.update(pos, data)
                if (data.isSelect!! && pos == 0) {
                    for (i in 1 until lcdViewModel.adapterPackages.itemCount) {
                        val dataPackage = lcdViewModel.adapterPackages.getItem(i)
                        dataPackage.isSelect = false
                        dataPackage.isOtherSelect = false
                        dataPackage.isGray = false
                        lcdViewModel.adapterPackages.update(i, dataPackage)
                    }
                } else {
                    for (i in 1 until lcdViewModel.adapterPackages.itemCount) {
                        val dataPackage = lcdViewModel.adapterPackages.getItem(i)
//                        dataPackage.isOtherSelect = false
//                        dataPackage.isGray = false
                        lcdViewModel.adapterPackages.update(i, dataPackage)
                    }
                    val data0 = lcdViewModel.adapterPackages.getItem(0)
                    data0.isSelect = false
                    lcdViewModel.adapterPackages.update(0, data0)
                    if (!data0.isGray!!)
                        lcdViewModel.callCheckedPackageAPI()
                }
            }
            R.id.tvResetPackage -> {
                lcdViewModel.setLCDPrefData()
                for (i in 0 until lcdViewModel.adapterPackages.itemCount) {
                    val data = lcdViewModel.adapterPackages.getItem(i)
                    data.isSelect = false
                    data.isGray = false
                    data.isOtherSelect = false
                    lcdViewModel.adapterPackages.update(i, data)
                }
                lcdViewModel.prefLCDData = pref?.getOneDealNearYouData()!!
            }
            R.id.tvApplyOption -> {
                var optionsStr = ""
                var isFirst = true
                for (i in 0 until lcdViewModel.adapterOptions.itemCount) {
                    if (lcdViewModel.adapterOptions.getItem(i).isSelect == true || lcdViewModel.adapterOptions.getItem(
                            i
                        ).isOtherSelect == true
                    ) {
                        if (isFirst) {
                            optionsStr = lcdViewModel.adapterOptions.getItem(i).accessory.toString()
                            isFirst = false
                        } else {
                            optionsStr =
                                optionsStr + ", " + lcdViewModel.adapterOptions.getItem(i).accessory.toString()
                        }
                    }
                }
                if (!TextUtils.isEmpty(optionsStr)) {
                    val ignoreInventory = lcdViewModel.adapterOptions.getItem(0).isSelect == true
                    if (!lcdViewModel.hasMatchOptions && !ignoreInventory) {
                        lcdViewModel.showOtherInventoryEmptyDialog()
                    } else {
                        tvErrorOptionsAccessories.visibility = View.GONE
                        tvOptionsAccessories.text = optionsStr
                        lcdViewModel.dialogOptions.dismiss()
                    }
                } else {
                    lcdViewModel.showApplyEmptyDialog()
//                    tvOptionalAccessories.text = "OPTIONS & ACCESSORIES"
                }
                lcdViewModel.prefLCDData = pref?.getOneDealNearYouData()!!
                lcdViewModel.prefLCDData.optionsData = lcdViewModel.adapterOptions.getAll()
                lcdViewModel.setLCDPrefData()
                lcdViewModel.setRadius()
            }
            R.id.tvResetOption -> {
                lcdViewModel.setLCDPrefData()
                for (i in 0 until lcdViewModel.adapterOptions.itemCount) {
                    val data = lcdViewModel.adapterOptions.getItem(i)
                    data.isSelect = false
                    data.isGray = false
                    data.isOtherSelect = false
                    lcdViewModel.adapterOptions.update(i, data)
                }

                lcdViewModel.prefLCDData = pref?.getOneDealNearYouData()!!
            }
            R.id.tvCancelOption -> {
                for (i in 0 until lcdViewModel.prefLCDData.optionsData?.size!!) {
                    lcdViewModel.adapterOptions.update(
                        i,
                        lcdViewModel.prefLCDData.optionsData!![i]
                    )
                }
                lcdViewModel.dialogOptions.dismiss()
            }
            R.id.llOptions -> {
                // Log.e("select1", selectOptionStr)
                val pos = v.tag as Int
                val data = lcdViewModel.adapterOptions.getItem(pos)
                data.isSelect = !data.isSelect!!
                lcdViewModel.adapterOptions.update(pos, data)
                if (data.isSelect!! && pos == 0) {
                    for (i in 1 until lcdViewModel.adapterOptions.itemCount) {
                        val dataOptions = lcdViewModel.adapterOptions.getItem(i)
                        dataOptions.isSelect = false
                        dataOptions.isOtherSelect = false
                        dataOptions.isGray = false
                        lcdViewModel.adapterOptions.update(i, dataOptions)
                    }
                } else {
                    for (i in 1 until lcdViewModel.adapterOptions.itemCount) {
                        val dataOptions = lcdViewModel.adapterOptions.getItem(i)
//                        dataOptions.isOtherSelect = false
//                        dataOptions.isGray = false
                        lcdViewModel.adapterOptions.update(i, dataOptions)
                    }
                    val data0 = lcdViewModel.adapterOptions.getItem(0)
                    data0.isSelect = false
                    lcdViewModel.adapterOptions.update(0, data0)
                    if (!data0.isGray!!)
                        lcdViewModel.callCheckedOptionsAPI()
                }
            }
            R.id.btnSearch -> {
                if (isValid()) {
                    Log.e("data", "ISValid")
                    lcdViewModel.clickSearchBtn()
                }
            }
        }
    }

    private fun onChangeZipCode() {
        lcdViewModel.liveDataZipCode?.observe(requireActivity()) { data ->
            if (!data) {
                edtZipCode.setBackgroundResource(R.drawable.bg_edittext_dark_error)
                tvErrorZipCode.visibility = View.VISIBLE
                isEnableField(true, false, false, false, false, false, false, false, false)
                isReInitField(false, false, false, false, false, false, false, false, false)
            } else {
                tvErrorZipCode.visibility = View.GONE
                isEnableField(true, true, false, false, false, false, false, false, false)
                isReInitField(true, false, false, false, false, false, false, false, false)
            }
            lcdViewModel.isValidZipCode = data
            lcdViewModel.prefLCDData.isZipCode = lcdViewModel.isValidZipCode!!
//            lcdViewModel.setPrefLykUcdData()
        }
        edtZipCode.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val str = s.toString()
                if (str.length == 5) {
                    if (lcdViewModel.prefLCDData.zipCode != edtZipCode.text.toString().trim()) {
                        isEnableField(true, false, false, false, false, false, false, false, false)
                        isReInitField(true, false, false, false, false, false, false, false, false)
                    }
                    lcdViewModel.callVehicleZipCodeAPI(str)
                } else if (str.length < 5) {
                    lcdViewModel.isValidZipCode = false
                    lcdViewModel.prefLCDData.isZipCode = lcdViewModel.isValidZipCode!!
                    tvErrorZipCode.visibility = View.GONE
                    edtZipCode.setBackgroundResource(R.drawable.bg_edittext_dark)
                    if (str.length == 4) {
                        isEnableField(true, false, false, false, false, false, false, false, false)
                        isReInitField(true, false, false, false, false, false, false, false, false)
                        /* zipCodeField(
                             lcdViewModel.isValidZipCode
                         )*/
                    }
                }
                lcdViewModel.zipCode = edtZipCode.text.toString().trim()
                lcdViewModel.prefLCDData.zipCode = edtZipCode.text.toString().trim()
                lcdViewModel.setLCDPrefData()
//                isEnableField(true, lcdViewModel.isValidZipCode, false, false, false, false, false, false, false)
//                isReInitField(lcdViewModel.isValidZipCode, false, false, false, false, false, false, false, false)
            }

            override fun afterTextChanged(s: Editable?) {

            }
        })
    }

    private fun callVehicleYearAPI(view: View?) {
        lcdViewModel.getYear(
            view
        )
    }

    private fun callVehicleMakeAPI(view: View?) {
        lcdViewModel.getMake(
            view
        )
    }

    private fun callVehicleModelAPI(view: View?) {
        lcdViewModel.getModel(
            view
        )
    }

    private fun callVehicleTrimAPI(view: View?) {
        lcdViewModel.getTrim(
            view
        )
    }

    private fun callVehicleExteriorAPI(view: View?) {
        lcdViewModel.getExteriorColor(
            view
        )
    }

    private fun callVehicleInteriorAPI(view: View?) {
        lcdViewModel.getInteriorColor(
            view
        )
    }

    private fun callPackageAPI() {
        lcdViewModel.getPackages()
    }

    private fun callOptionsAccessoriesAPI() {
        lcdViewModel.getOptionsAccessories()
    }

    private fun isEnableField(
        isZip: Boolean,
        isYear: Boolean,
        isMake: Boolean,
        isModel: Boolean,
        isTrim: Boolean,
        isExtColor: Boolean,
        isIntColor: Boolean,
        isPackage: Boolean,
        isOptions: Boolean
    ) {
        tvYear.isEnabled = isYear
        tvMake.isEnabled = isMake
        tvModel.isEnabled = isModel
        tvTrim.isEnabled = isTrim
        tvExterior.isEnabled = isExtColor
        tvInterior.isEnabled = isIntColor
        tvPackages.isEnabled = isPackage
        tvOptionsAccessories.isEnabled = isOptions
    }

    private fun isReInitField(
        isZip: Boolean,
        isYear: Boolean,
        isMake: Boolean,
        isModel: Boolean,
        isTrim: Boolean,
        isExtColor: Boolean,
        isIntColor: Boolean,
        isPackage: Boolean,
        isOptions: Boolean
    ) {
        if (!isZip) {
            edtZipCode.setText("")
            lcdViewModel.run {
                zipCode = ""
                isValidZipCode = false
                yearId = ""
                yearStr = ""
                makeId = ""
                makeStr = ""
                modelId = ""
                modelStr = ""
                trimId = ""
                trimStr = ""
                extColorId = ""
                extColorStr = ""
                intColorId = ""
                intColorStr = ""
            }
        }
        if (!isYear) {
            tvYear.text = getString(R.string.year_new_cars_title)
            lcdViewModel.run {
                yearId = ""
                yearStr = ""
                makeId = ""
                makeStr = ""
                modelId = ""
                modelStr = ""
                trimId = ""
                trimStr = ""
                extColorId = ""
                extColorStr = ""
                intColorId = ""
                intColorStr = ""
            }
        }
        if (!isMake) {
            tvMake.text = getString(R.string.make_title)
            lcdViewModel.run {
                makeId = ""
                makeStr = ""
                modelId = ""
                modelStr = ""
                trimId = ""
                trimStr = ""
                extColorId = ""
                extColorStr = ""
                intColorId = ""
                intColorStr = ""
            }
        }
        if (!isModel) {
            tvModel.text = getString(R.string.model_title)
            lcdViewModel.run {
                modelId = ""
                modelStr = ""
                trimId = ""
                trimStr = ""
                extColorId = ""
                extColorStr = ""
                intColorId = ""
                intColorStr = ""
            }
        }
        if (!isTrim) {
            tvTrim.text = getString(R.string.trim_title)
            lcdViewModel.run {
                trimId = ""
                trimStr = ""
                extColorId = ""
                extColorStr = ""
                intColorId = ""
                intColorStr = ""
            }
        }
        if (!isExtColor) {
            tvExterior.text = getString(R.string.exterior_color_title)
            lcdViewModel.run {
                extColorId = ""
                extColorStr = ""
                intColorId = ""
                intColorStr = ""
            }
        }
        if (!isIntColor) {
            tvInterior.text =
                getString(R.string.interior_color_title)
            lcdViewModel.run {
                intColorId = ""
                intColorStr = ""
            }
        }
        if (!isPackage) {
            tvPackages.text = getString(R.string.packages_title)
        }
        if (!isOptions) tvOptionsAccessories.text = getString(R.string.options_accessories_title)
        lcdViewModel.run {
            prefLCDData.isZipCode = isValidZipCode
            prefLCDData.zipCode = zipCode
            prefLCDData.yearId = yearId
            prefLCDData.yearId = yearId
            prefLCDData.makeId = makeId
            prefLCDData.modelId = modelId
            prefLCDData.trimId = trimId
            prefLCDData.extColorId = extColorId
            prefLCDData.intColorId = intColorId
            prefLCDData.yearStr = yearStr
            prefLCDData.makeStr = makeStr
            prefLCDData.modelStr = modelStr
            prefLCDData.trimStr = trimStr
            prefLCDData.extColorStr = extColorStr
            prefLCDData.intColorStr = intColorStr
            prefLCDData.packagesData = ArrayList()
            prefLCDData.optionsData = ArrayList()
            setLCDPrefData()
        }
    }

    private fun zipCodeField(
        isZip: Boolean
    ) {
        if (!isZip) {
            lcdViewModel.run {
                zipCode = ""
                isValidZipCode = false
                yearId = ""
                yearStr = ""
                makeId = ""
                makeStr = ""
                modelId = ""
                modelStr = ""
                trimId = ""
                trimStr = ""
                extColorId = ""
                extColorStr = ""
                intColorId = ""
                intColorStr = ""
            }
        }
        lcdViewModel.run {
            prefLCDData.isZipCode = isValidZipCode
            prefLCDData.zipCode = zipCode
            prefLCDData.yearId = yearId
            prefLCDData.yearId = yearId
            prefLCDData.makeId = makeId
            prefLCDData.modelId = modelId
            prefLCDData.trimId = trimId
            prefLCDData.extColorId = extColorId
            prefLCDData.intColorId = intColorId
            prefLCDData.yearStr = yearStr
            prefLCDData.makeStr = makeStr
            prefLCDData.modelStr = modelStr
            prefLCDData.trimStr = trimStr
            prefLCDData.extColorStr = extColorStr
            prefLCDData.intColorStr = intColorStr
            prefLCDData.packagesData = ArrayList()
            prefLCDData.optionsData = ArrayList()
            setLCDPrefData()
        }
    }


    private fun isValid(): Boolean {
        when {
            TextUtils.isEmpty(edtZipCode.text.toString().trim()) -> {
                tvErrorZipCode.text = getString(R.string.enter_zipcode)
                tvErrorZipCode.visibility = View.VISIBLE
                return false
            }
            !lcdViewModel.isValidZipCode -> {
                tvErrorZipCode.text = getString(R.string.invalid_zip_code)
                tvErrorZipCode.visibility = View.VISIBLE
                return false
            }
            TextUtils.isEmpty(lcdViewModel.yearStr) || lcdViewModel.yearStr == getString(R.string.year_new_cars_title) -> {
                tvErrorYear.visibility = View.VISIBLE
                return false
            }
            TextUtils.isEmpty(lcdViewModel.makeStr) || lcdViewModel.makeStr == getString(R.string.make_title) -> {
                tvErrorMake.visibility = View.VISIBLE
                return false
            }
            TextUtils.isEmpty(lcdViewModel.modelStr) || lcdViewModel.modelStr == getString(R.string.model_title) -> {
                tvErrorModel.visibility = View.VISIBLE
                return false
            }
            TextUtils.isEmpty(lcdViewModel.trimStr) || lcdViewModel.trimStr == getString(R.string.trim_title) -> {
                tvErrorTrim.visibility = View.VISIBLE
                return false
            }
            TextUtils.isEmpty(lcdViewModel.extColorStr) || lcdViewModel.extColorStr == getString(R.string.exterior_color_title) -> {
                tvErrorExterior.visibility = View.VISIBLE
                return false
            }
            TextUtils.isEmpty(lcdViewModel.intColorStr) || lcdViewModel.intColorStr == getString(R.string.interior_color_title) -> {
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