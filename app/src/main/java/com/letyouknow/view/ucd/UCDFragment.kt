package com.letyouknow.view.ucd

import android.app.Dialog
import android.os.Bundle
import android.os.Handler
import android.text.*
import android.util.Log
import android.view.*
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.AdapterView
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.google.gson.Gson
import com.letyouknow.R
import com.letyouknow.base.BaseFragment
import com.letyouknow.databinding.FragmentUcdBinding
import com.letyouknow.model.*
import com.letyouknow.retrofit.ApiConstant
import com.letyouknow.retrofit.viewmodel.*
import com.letyouknow.utils.AppGlobal
import com.letyouknow.utils.AppGlobal.Companion.isEmpty
import com.letyouknow.utils.AppGlobal.Companion.setNoData
import com.letyouknow.utils.AppGlobal.Companion.setSpinnerLayoutPos
import com.letyouknow.utils.Constant
import com.letyouknow.utils.Constant.Companion.ARG_RADIUS
import com.letyouknow.utils.Constant.Companion.ARG_YEAR_MAKE_MODEL
import com.letyouknow.utils.Constant.Companion.ARG_ZIPCODE
import com.letyouknow.view.dashboard.MainActivity
import com.letyouknow.view.spinneradapter.*
import kotlinx.android.synthetic.main.dialog_mobile_no.*
import kotlinx.android.synthetic.main.fragment_ucd.*
import org.jetbrains.anko.support.v4.startActivity
import java.text.SimpleDateFormat
import java.util.*


class UCDFragment : BaseFragment(), View.OnClickListener, AdapterView.OnItemSelectedListener {
    private var arRadius = arrayListOf(
        "SEARCH RADIUS",
        "25 mi",
        "50 mi",
        "75 mi",
        "100 mi",
        "150 mi",
        "200 mi",
        "250 mi",
        "300 mi",
        "350 mi",
        "500 mi",
        "1000 mi",
        "ALL"
    )

    private lateinit var binding: FragmentUcdBinding
    private var upDownData = UpDownData()

    private var isValidZipCode = false

    private lateinit var promotionViewModel: PromotionViewModel
    private lateinit var vehicleYearModel: VehicleYearViewModel
    private lateinit var vehicleMakeModel: VehicleMakeViewModel
    private lateinit var vehicleModelModel: VehicleModelViewModel
    private lateinit var vehicleTrimModel: VehicleTrimViewModel
    private lateinit var exteriorColorModel: ExteriorColorViewModel
    private lateinit var interiorColorModel: InteriorColorViewModel
    private lateinit var zipCodeModel: VehicleZipCodeViewModel
    private lateinit var findUCDDealGuestViewModel: FindUCDDealViewModel
    private lateinit var socialMobileViewModel: SocialMobileViewModel
    private lateinit var priceRangeViewModel: PriceRangeViewModel

    private lateinit var adapterYear: YearSpinnerAdapter
    private lateinit var adapterMake: MakeSpinnerAdapter
    private lateinit var adapterModel: ModelSpinnerAdapter
    private lateinit var adapterTrim: TrimsSpinnerAdapter
    private lateinit var adapterExterior: ExteriorSpinnerAdapter
    private lateinit var adapterInterior: InteriorSpinnerAdapter
    private lateinit var adapterRadius: RadiusSpinnerAdapter
    private lateinit var adapterPriceRange: PriceRangeSpinnerAdapter

    private var productId = "3"
    private var ucdPriceId = ""
    private var yearId = ""
    private var makeId = ""
    private var modelId = ""
    private var trimId = ""

    private var lowerBorder = "ANY PRICE"
    private var upperBorder = ""

    private var yearStr = "YEAR - NEW CARS"
    private var makeStr = "MAKE"
    private var modelStr = "MODEL"
    private var trimStr = "TRIM"
    private var extColorStr = "EXTERIOR COLOR"
    private var intColorStr = "INTERIOR COLOR"
    private var radiusId = "SEARCH RADIUS"

    private var extColorId = "0"
    private var intColorId = "0"

    private lateinit var animBlink: Animation
    private lateinit var animSlideRightToLeft: Animation
    private lateinit var animSlideLeftToRight: Animation
    private lateinit var tokenModel: RefreshTokenViewModel


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_ucd,
            container,
            false
        )

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init()
    }

    private fun init() {
        try {
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

            binding.upDownData = upDownData

            promotionViewModel = ViewModelProvider(this)[PromotionViewModel::class.java]
            vehicleYearModel = ViewModelProvider(this)[VehicleYearViewModel::class.java]
            vehicleMakeModel = ViewModelProvider(this)[VehicleMakeViewModel::class.java]
            vehicleModelModel = ViewModelProvider(this)[VehicleModelViewModel::class.java]
            vehicleTrimModel = ViewModelProvider(this)[VehicleTrimViewModel::class.java]
            exteriorColorModel = ViewModelProvider(this)[ExteriorColorViewModel::class.java]
            interiorColorModel = ViewModelProvider(this)[InteriorColorViewModel::class.java]
            zipCodeModel = ViewModelProvider(this)[VehicleZipCodeViewModel::class.java]
            findUCDDealGuestViewModel =
                ViewModelProvider(this)[FindUCDDealViewModel::class.java]
            tokenModel = ViewModelProvider(this)[RefreshTokenViewModel::class.java]
            socialMobileViewModel = ViewModelProvider(this)[SocialMobileViewModel::class.java]
            priceRangeViewModel = ViewModelProvider(this)[PriceRangeViewModel::class.java]

            tvPriceRange.setOnClickListener(this)
//            tvYear.setOnClickListener(this)
            btnProceedDeal.setOnClickListener(this)
            tvPromo.setOnClickListener(this)
            ivClosePromo.setOnClickListener(this)
            MainActivity.getInstance().setVisibleEditImg(false)
            MainActivity.getInstance().setVisibleLogoutImg(false)
            setTimerPrefData()
            edtZipCode.inputType = InputType.TYPE_CLASS_NUMBER
            callPromotionAPI()
        } catch (e: Exception) {

        }
    }


    private fun onChangeZipCode() {
        try {
            edtZipCode.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) {

                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    val str = s.toString()
                    if (str.length == 5) {
                        callVehicleZipCodeAPI(str)
                    } else if (str.length < 5) {
                        isValidZipCode = false
                        prefSearchDealData.isZipCode = isValidZipCode!!
                        tvErrorZipCode.visibility = View.GONE
                        edtZipCode.setBackgroundResource(R.drawable.bg_edittext_dark)
                    }
                    prefSearchDealData.zipCode = edtZipCode.text.toString().trim()
                }

                override fun afterTextChanged(s: Editable?) {

                }

            })
        } catch (e: Exception) {

        }
    }

    private fun setPriceRange() {
        try {
            val arData = ArrayList<PriceRangeData>()
            val priceRangeData = PriceRangeData()
            priceRangeData.ucdPriceRangeID = "0"
            priceRangeData.lowerBorder = "ANY PRICE"
            arData.add(0, priceRangeData)
            adapterPriceRange = PriceRangeSpinnerAdapter(requireActivity(), arData)
            spPriceRange.adapter = adapterPriceRange
            setSpinnerLayoutPos(0, spPriceRange, requireActivity())
        } catch (e: Exception) {

        }
    }

    private fun setYear() {
        try {
//            spYear.isEnabled = false
            val arData = ArrayList<VehicleYearData>()
            val yearData = VehicleYearData()
            yearData.year = "YEAR - NEW CARS"
            arData.add(0, yearData)
            adapterYear = YearSpinnerAdapter(requireActivity(), arData)
            spYear.adapter = adapterYear
            setSpinnerLayoutPos(0, spYear, requireActivity())
        } catch (e: Exception) {

        }
    }

    private fun setMake() {
        try {
            spMake.isEnabled = false
            val arData = ArrayList<VehicleMakeData>()
            val makeData = VehicleMakeData()
            if (prefSearchDealData.lowerBorder == "ANY PRICE")
                makeData.make = "MAKE"
            else {
                makeData.make = "ANY"
                makeData.vehicleMakeID = "0"
            }
            arData.add(0, makeData)
            adapterMake = MakeSpinnerAdapter(requireActivity(), arData)
            spMake.adapter = adapterMake
            setSpinnerLayoutPos(0, spMake, requireActivity())
        } catch (e: Exception) {

        }
    }

    private fun setModel() {
        try {
            spModel.isEnabled = false
            val arData = ArrayList<VehicleModelData>()
            val modelData = VehicleModelData()
            if (prefSearchDealData.lowerBorder == "ANY PRICE")
                modelData.model = "MODEL"
            else {
                modelData.model = "ANY"
                modelData.vehicleModelID = "0"
            }
            arData.add(0, modelData)
            adapterModel = ModelSpinnerAdapter(requireActivity(), arData)
            spModel.adapter = adapterModel
            setSpinnerLayoutPos(0, spModel, requireActivity())
        } catch (e: Exception) {

        }
    }

    private fun setTrim() {
        try {
            spTrim.isEnabled = false
            val arData = ArrayList<VehicleTrimData>()
            val trimData = VehicleTrimData()
            if (prefSearchDealData.lowerBorder == "ANY PRICE")
                trimData.trim = "TRIM"
            else {
                trimData.trim = "ANY"
                trimData.vehicleTrimID = "0"
            }
            arData.add(0, trimData)
            adapterTrim = TrimsSpinnerAdapter(requireActivity(), arData)
            spTrim.adapter = adapterTrim
            setSpinnerLayoutPos(0, spTrim, requireActivity())
        } catch (e: Exception) {

        }
    }

    private fun setExteriorColor() {
        try {
            spExteriorColor.isEnabled = false
            val arData = ArrayList<ExteriorColorData>()
            val extData = ExteriorColorData()
            if (prefSearchDealData.lowerBorder == "ANY PRICE")
                extData.exteriorColor = "EXTERIOR COLOR"
            else {
                extData.exteriorColor = "ANY"
                extData.vehicleExteriorColorID = "0"
            }
            arData.add(0, extData)
            adapterExterior = ExteriorSpinnerAdapter(requireActivity(), arData)
            spExteriorColor.adapter = adapterExterior
            setSpinnerLayoutPos(0, spExteriorColor, requireActivity())
        } catch (e: Exception) {

        }
    }

    private fun setInteriorColor() {
        try {
            spInteriorColor.isEnabled = false
            val arData = ArrayList<InteriorColorData>()
            val interiorData = InteriorColorData()
            if (prefSearchDealData.lowerBorder == "ANY PRICE")
                interiorData.interiorColor = "INTERIOR COLOR"
            else {
                interiorData.interiorColor = "ANY"
                interiorData.vehicleInteriorColorID = "0"
            }
            arData.add(0, interiorData)
            adapterInterior = InteriorSpinnerAdapter(requireActivity(), arData)
            spInteriorColor.adapter = adapterInterior
            setSpinnerLayoutPos(0, spInteriorColor, requireActivity())
        } catch (e: Exception) {

        }
    }

    /* private fun setRadius() {
         val arData = ArrayList<String>()
         arData.add(0, "SEARCH RADIUS")
         adapterRadius = RadiusSpinnerAdapter(requireActivity(), arData)
         spRadius.adapter = adapterRadius
         setSpinnerLayoutPos(0, spRadius, requireActivity())
     }
 */
    private fun callSearchFindDealAPI() {
        val dataYear = YearModelMakeData()
        dataYear.vehicleYearID = yearId
        dataYear.vehicleMakeID = makeId
        dataYear.vehicleModelID = modelId
        dataYear.vehicleTrimID = trimId
        dataYear.vehicleExtColorID = extColorId
        dataYear.vehicleIntColorID = intColorId
        dataYear.vehicleYearStr = yearStr
        dataYear.vehicleMakeStr = makeStr
        dataYear.vehicleModelStr = modelStr
        dataYear.vehicleTrimStr = trimStr
        dataYear.vehicleExtColorStr = extColorStr
        dataYear.vehicleIntColorStr = intColorStr
        dataYear.radius = radiusId
        dataYear.zipCode = edtZipCode.text.toString().trim()
        dataYear.LowPrice = lowerBorder
        dataYear.HighPrice = upperBorder
//                    Log.e("Find UCD",Gson().toJson(data))
        /*  startActivity<UCDStep1Activity>(
              ARG_YEAR_MAKE_MODEL to Gson().toJson(dataYear),
              ARG_RADIUS to radiusId,
              ARG_ZIPCODE to edtZipCode.text.toString().trim()
          )*/
        startActivity<UCDDealListStep1NewActivity>(
            ARG_YEAR_MAKE_MODEL to Gson().toJson(dataYear),
            ARG_RADIUS to radiusId,
            ARG_ZIPCODE to edtZipCode.text.toString().trim()
        )
    }


    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.tvPriceRange -> {
                tvPriceRange.visibility = View.GONE
                spPriceRange.visibility = View.VISIBLE
                callPriceRangeAPI()
                spPriceRange.performClick()

            }
            /* R.id.tvYear -> {
                 tvYear.visibility = View.GONE
                 spYear.visibility = View.VISIBLE
                 callVehicleYearAPI()
                 spYear.performClick()
             }*/
            R.id.btnProceedDeal -> {
                if (isValid()) {
                    if (pref?.getUserData()?.isSocial!!) {
                        if (!pref?.isUpdateSocialMobile()!!) {
                            dialogPhoneNo(true)
                        } else {
                            callSearchFindDealAPI()
                        }
                    } else {
                        callSearchFindDealAPI()
                    }

//                    callRefreshTokenApi()
                }
            }
            R.id.tvPromo -> {
                tvPromo.clearAnimation()
                tvPromo.visibility = View.GONE
                llPromoOffer.visibility = View.VISIBLE
                llPromoOffer.startAnimation(animSlideRightToLeft)
            }
            R.id.ivClosePromo -> {
                llPromoOffer.startAnimation(animSlideLeftToRight)
                Handler().postDelayed({
                    tvPromo.startAnimation(animBlink)
                    tvPromo.visibility = View.VISIBLE
                    llPromoOffer.visibility = View.GONE
                }, 400)
            }
        }
    }

    private fun callVehicleZipCodeAPI(zipCode: String?) {
        if (Constant.isOnline(requireActivity())) {
            if (!isCallingYear)
                Constant.showLoader(requireActivity())

            zipCodeModel.getZipCode(requireActivity(), zipCode)!!
                .observe(requireActivity(), Observer { data ->
                    Constant.dismissLoader()
                    try {
                        Log.e("ZipCode Data", Gson().toJson(data))
                        if (!data) {
                            /* Toast.makeText(
                                 requireActivity(),
                                 getString(R.string.invalid_zip_code),
                                 Toast.LENGTH_SHORT
                             ).show()*/
                            edtZipCode.setBackgroundResource(R.drawable.bg_edittext_dark_error)
                            tvErrorZipCode.visibility = View.VISIBLE
                        } else {
                            tvErrorZipCode.visibility = View.GONE
                        }
                        isValidZipCode = data
                        prefSearchDealData.isZipCode = isValidZipCode!!
                        prefSearchDealData.isUCDSelZipCode = true
                        setPrefZipCodeData()
                        setLCDPrefData()
                        setPrefLYKLCDData()
                    } catch (e: Exception) {
                    }
                }
                )
        } else {
            Toast.makeText(requireActivity(), Constant.noInternet, Toast.LENGTH_SHORT).show()
        }
    }

    var isCallingYear = false
    private fun callPriceRangeAPI() {
        try {
            isCallingYear = true
            if (Constant.isOnline(requireActivity())) {
                if (Constant.isInitProgress() && !Constant.progress.isShowing) {
                    Constant.dismissLoader()
                }
                if (!Constant.isInitProgress()) {
                    Constant.showLoader(requireActivity())
                } else if (Constant.isInitProgress() && !Constant.progress.isShowing) {
                    Constant.showLoader(requireActivity())
                }
                priceRangeViewModel.getPriceRange(requireActivity())!!
                    .observe(requireActivity(), Observer { data ->
                        if (isEmpty(prefSearchDealData.yearId))
                            Constant.dismissLoader()
                        isCallingYear = false
                        Log.e("PriceRange Data", Gson().toJson(data))
                        try {
                            if (data != null || data?.size!! > 0) {
                                val priceRangeData = PriceRangeData()
                                priceRangeData.lowerBorder = "ANY PRICE"
                                priceRangeData.ucdPriceRangeID = "0"
                                data.add(0, priceRangeData)
                                adapterPriceRange =
                                    PriceRangeSpinnerAdapter(requireActivity(), data)
                                spPriceRange.adapter = adapterPriceRange
                                for (i in 0 until data.size) {
                                    if (!AppGlobal.isEmpty(prefSearchDealData.ucdPriceRangeID) && prefSearchDealData.ucdPriceRangeID == data[i].ucdPriceRangeID) {
                                        spPriceRange.setSelection(i, true)
                                        AppGlobal.setSpinnerLayoutPos(
                                            i,
                                            spPriceRange,
                                            requireActivity()
                                        )
//                                        callVehicleYearAPI()
                                    }
                                }
                                callVehicleYearAPI()
                                spPriceRange.onItemSelectedListener = this
                            } else {
                                val arData = ArrayList<PriceRangeData>()
                                val priceRangeData = PriceRangeData()
                                priceRangeData.lowerBorder = "ANY PRICE"
                                priceRangeData.ucdPriceRangeID = "0"
                                arData.add(priceRangeData)
                                adapterPriceRange =
                                    PriceRangeSpinnerAdapter(requireActivity(), arData)
                                spPriceRange.adapter = adapterPriceRange
                                spPriceRange.onItemSelectedListener = this
                            }
                        } catch (e: Exception) {

                        }
                    }
                    )
            } else {
                Toast.makeText(requireActivity(), Constant.noInternet, Toast.LENGTH_SHORT).show()
            }
        } catch (e: Exception) {

        }
    }

    private fun callVehicleYearAPI() {
        try {
//            isCallingYear = true
//            spYear.isEnabled = true
            if (Constant.isOnline(requireActivity())) {
                if (Constant.isInitProgress() && !Constant.progress.isShowing) {
                    Constant.dismissLoader()
                }
                if (!Constant.isInitProgress()) {
                    Constant.showLoader(requireActivity())
                } else if (Constant.isInitProgress() && !Constant.progress.isShowing) {
                    Constant.showLoader(requireActivity())
                }
                vehicleYearModel.getYear(
                    requireActivity(),
                    productId,
                    "",
                    3,
                    lowerBorder,
                    upperBorder
                )!!
                    .observe(requireActivity(), Observer { data ->
                        if (isEmpty(prefSearchDealData.makeId))
                            Constant.dismissLoader()
//                        isCallingYear = false
                        Log.e("Year Data", Gson().toJson(data))
                        try {
                            if (!data.isNullOrEmpty()) {
                                val yearData = VehicleYearData()
                                if (lowerBorder == "ANY PRICE")
                                    yearData.year = "YEAR - NEW CARS"
                                else {
                                    yearData.year = "ANY"
                                    yearData.vehicleYearID = "0"
                                }
                                data.add(0, yearData)
                                adapterYear = YearSpinnerAdapter(requireActivity(), data)
                                spYear.adapter = adapterYear
                                for (i in 0 until data.size) {
                                    if (!AppGlobal.isEmpty(prefSearchDealData.yearId) && prefSearchDealData.yearId == data[i].vehicleYearID) {
                                        spYear.setSelection(i, true)
                                        AppGlobal.setSpinnerLayoutPos(i, spYear, requireActivity())
                                        if (prefSearchDealData.yearStr != "ANY")
                                            callVehicleMakeAPI()

                                    }
                                }
                                spYear.onItemSelectedListener = this
                            } else {
                                val arData = ArrayList<VehicleYearData>()
                                val yearData = VehicleYearData()
                                if (lowerBorder == "ANY PRICE")
                                    yearData.year = "YEAR - NEW CARS"
                                else {
                                    yearData.year = "ANY"
                                    yearData.vehicleYearID = "0"
                                }
                                arData.add(0, yearData)
                                adapterYear = YearSpinnerAdapter(requireActivity(), arData)
                                spYear.adapter = adapterYear
                                spYear.onItemSelectedListener = this
                                setNoData(requireActivity(), spYear)
                                setClearData()
                            }
                        } catch (e: Exception) {
                        }
                    }
                    )
            } else {
                Toast.makeText(requireActivity(), Constant.noInternet, Toast.LENGTH_SHORT).show()
            }
        } catch (e: Exception) {
        }
    }

    private fun callVehicleMakeAPI() {
        try {
            spMake.isEnabled =
                (prefSearchDealData.yearStr != "ANY" && prefSearchDealData.yearStr != "YEAR - NEW CARS")

            if (Constant.isOnline(requireActivity())) {
                if (isEmpty(prefSearchDealData.makeId)) {
                    if (!Constant.isInitProgress()) {
                        Constant.showLoader(requireActivity())
                    } else if (Constant.isInitProgress() && !Constant.progress.isShowing) {
                        Constant.showLoader(requireActivity())
                    }
                }
                vehicleMakeModel.getMake(
                    requireActivity(), productId, yearId, "", 3,
                    lowerBorder,
                    upperBorder
                )!!
                    .observe(requireActivity(), Observer { data ->
                        if (isEmpty(prefSearchDealData.modelId))
                            Constant.dismissLoader()
                        try {
                            Log.e("Make Data", Gson().toJson(data))
                            if (!data.isNullOrEmpty()) {
                                val makeData = VehicleMakeData()
                                if (lowerBorder == "ANY PRICE")
                                    makeData.make = "MAKE"
                                else {
                                    makeData.make = "ANY"
                                    makeData.vehicleMakeID = "0"
                                }
                                data.add(0, makeData)
                                adapterMake = MakeSpinnerAdapter(requireActivity(), data)
                                spMake.adapter = adapterMake
                                for (i in 0 until data.size) {
                                    if (!AppGlobal.isEmpty(prefSearchDealData.makeId) && prefSearchDealData.makeId == data[i].vehicleMakeID) {
                                        spMake.setSelection(i, true)
                                        if (prefSearchDealData.makeStr != "ANY")
                                            callVehicleModelAPI()
                                        AppGlobal.setSpinnerLayoutPos(i, spMake, requireActivity())
                                    }
                                }
                                spMake.onItemSelectedListener = this
                            } else {
                                val arData = ArrayList<VehicleMakeData>()
                                val makeData = VehicleMakeData()
                                if (lowerBorder == "ANY PRICE")
                                    makeData.make = "MAKE"
                                else {
                                    makeData.make = "ANY"
                                    makeData.vehicleMakeID = "0"
                                }
                                arData.add(0, makeData)
                                adapterMake = MakeSpinnerAdapter(requireActivity(), arData)
                                spMake.adapter = adapterMake
                                spMake.onItemSelectedListener = this
                                setNoData(requireActivity(), spMake)
                                setClearData()
                            }
                        } catch (e: Exception) {

                        }
                    }
                    )
            } else {
                Toast.makeText(requireActivity(), Constant.noInternet, Toast.LENGTH_SHORT).show()
            }
        } catch (e: Exception) {

        }
    }

    private fun callVehicleModelAPI() {
        try {
            spModel.isEnabled =
                (prefSearchDealData.makeStr != "ANY" && prefSearchDealData.makeStr != "MAKE")

            if (Constant.isOnline(requireActivity())) {
                if (isEmpty(prefSearchDealData.modelId)) {
                    if (!Constant.isInitProgress()) {
                        Constant.showLoader(requireActivity())
                    } else if (Constant.isInitProgress() && !Constant.progress.isShowing) {
                        Constant.showLoader(requireActivity())
                    }
                }
                vehicleModelModel.getModel(
                    requireActivity(), productId, yearId, makeId, "", 3,
                    lowerBorder,
                    upperBorder
                )!!
                    .observe(requireActivity(), Observer { data ->
                        if (isEmpty(prefSearchDealData.trimId))
                            Constant.dismissLoader()
                        try {
                            Log.e("Make Data", Gson().toJson(data))
                            if (!data.isNullOrEmpty()) {
                                val modelData = VehicleModelData()
                                if (lowerBorder == "ANY PRICE")
                                    modelData.model = "MODEL"
                                else {
                                    modelData.model = "ANY"
                                    modelData.vehicleModelID = "0"
                                }
                                data.add(0, modelData)
                                adapterModel = ModelSpinnerAdapter(requireActivity(), data)
                                spModel.adapter = adapterModel
                                for (i in 0 until data.size) {
                                    if (!AppGlobal.isEmpty(prefSearchDealData.modelId) && prefSearchDealData.modelId == data[i].vehicleModelID) {
                                        spModel.setSelection(i, true)
                                        if (prefSearchDealData.modelStr != "ANY")
                                            callVehicleTrimAPI()
                                        AppGlobal.setSpinnerLayoutPos(i, spModel, requireActivity())
                                    }
                                }
                                spModel.onItemSelectedListener = this
                            } else {
                                val arData = ArrayList<VehicleModelData>()
                                val modelData = VehicleModelData()
                                if (lowerBorder == "ANY PRICE")
                                    modelData.model = "MODEL"
                                else {
                                    modelData.model = "ANY"
                                    modelData.vehicleModelID = "0"
                                }
                                arData.add(0, modelData)
                                adapterModel = ModelSpinnerAdapter(requireActivity(), arData)
                                spModel.adapter = adapterModel
                                spModel.onItemSelectedListener = this
                                setNoData(requireActivity(), spModel)
                                setClearData()
                            }
                        } catch (e: Exception) {

                        }
                    }
                    )
            } else {
                Toast.makeText(requireActivity(), Constant.noInternet, Toast.LENGTH_SHORT).show()
            }
        } catch (e: Exception) {

        }
    }

    private fun callVehicleTrimAPI() {
        try {
            spTrim.isEnabled =
                (prefSearchDealData.modelStr != "ANY" && prefSearchDealData.modelStr != "MODEL")
            if (Constant.isOnline(requireActivity())) {
                if (isEmpty(prefSearchDealData.trimId)) {
                    if (!Constant.isInitProgress()) {
                        Constant.showLoader(requireActivity())
                    } else if (Constant.isInitProgress() && !Constant.progress.isShowing) {
                        Constant.showLoader(requireActivity())
                    }
                }
                vehicleTrimModel.getTrim(
                    requireActivity(),
                    productId,
                    yearId,
                    makeId,
                    modelId,
                    "", 3,
                    lowerBorder,
                    upperBorder
                )!!
                    .observe(requireActivity(), Observer { data ->
                        if (isEmpty(prefSearchDealData.extColorId))
                            Constant.dismissLoader()
                        Log.e("Make Data", Gson().toJson(data))
                        try {
                            if (!data.isNullOrEmpty()) {
                                val trimData = VehicleTrimData()
                                if (lowerBorder == "ANY PRICE")
                                    trimData.trim = "TRIM"
                                else {
                                    trimData.trim = "ANY"
                                    trimData.vehicleTrimID = "0"
                                }
                                data.add(0, trimData)
                                adapterTrim = TrimsSpinnerAdapter(requireActivity(), data)
                                spTrim.adapter = adapterTrim
                                for (i in 0 until data.size) {
                                    if (!AppGlobal.isEmpty(prefSearchDealData.trimId) && prefSearchDealData.trimId == data[i].vehicleTrimID) {
                                        spTrim.setSelection(i, true)
                                        if (prefSearchDealData.trimStr != "ANY")
                                            callExteriorColorAPI()
                                        AppGlobal.setSpinnerLayoutPos(i, spTrim, requireActivity())
                                    }
                                }
                                spTrim.onItemSelectedListener = this
                            } else {
                                val arData = ArrayList<VehicleTrimData>()
                                val trimData = VehicleTrimData()
                                if (lowerBorder == "ANY PRICE")
                                    trimData.trim = "TRIM"
                                else {
                                    trimData.trim = "ANY"
                                    trimData.vehicleTrimID = "0"
                                }
                                arData.add(0, trimData)
                                adapterTrim = TrimsSpinnerAdapter(requireActivity(), arData)
                                spTrim.adapter = adapterTrim
                                spTrim.onItemSelectedListener = this
                                setNoData(requireActivity(), spTrim)
                                setClearData()
                            }
                        } catch (e: Exception) {

                        }
                    }
                    )
            } else {
                Toast.makeText(requireActivity(), Constant.noInternet, Toast.LENGTH_SHORT).show()
            }
        } catch (e: Exception) {

        }
    }

    private fun callExteriorColorAPI() {
        try {
            spExteriorColor.isEnabled =
                (prefSearchDealData.trimStr != "ANY" && prefSearchDealData.trimStr != "TRIM")
            if (Constant.isOnline(requireActivity())) {
                if (isEmpty(prefSearchDealData.extColorId)) {
                    if (!Constant.isInitProgress()) {
                        Constant.showLoader(requireActivity())
                    } else if (Constant.isInitProgress() && !Constant.progress.isShowing) {
                        Constant.showLoader(requireActivity())
                    }
                }
                exteriorColorModel.getExteriorColor(
                    requireActivity(),
                    productId,
                    yearId,
                    makeId,
                    modelId,
                    trimId, "", 3,
                    lowerBorder,
                    upperBorder
                )!!
                    .observe(requireActivity(), Observer { data ->
                        if (isEmpty(prefSearchDealData.intColorId))
                            Constant.dismissLoader()
                        try {
                            Log.e("Exterior Data", Gson().toJson(data))
                            if (data != null || data?.size!! > 0) {
                                if (lowerBorder == "ANY PRICE") {
                                    val exteriorColorData = ExteriorColorData()
                                    exteriorColorData.exteriorColor = "EXTERIOR COLOR"
                                    data.add(0, exteriorColorData)
                                    val exteriorColorData1 = ExteriorColorData()
                                    exteriorColorData1.vehicleExteriorColorID = "0"
                                    exteriorColorData1.exteriorColor = "ANY"
                                    data.add(1, exteriorColorData1)
                                } else {
                                    val exteriorColorData = ExteriorColorData()
                                    exteriorColorData.vehicleExteriorColorID = "0"
                                    exteriorColorData.exteriorColor = "ANY"
                                    data.add(0, exteriorColorData)
                                }

                                adapterExterior = ExteriorSpinnerAdapter(requireActivity(), data)
                                spExteriorColor.adapter = adapterExterior
                                for (i in 0 until data.size) {
                                    if (!AppGlobal.isEmpty(prefSearchDealData.extColorId) && prefSearchDealData.extColorId == data[i].vehicleExteriorColorID) {
                                        spExteriorColor.setSelection(i, true)
                                        if (prefSearchDealData.extColorStr != "ANY" || prefSearchDealData.lowerBorder == "ANY PRICE")
                                            callInteriorColorAPI()
                                        AppGlobal.setSpinnerLayoutPos(
                                            i,
                                            spExteriorColor,
                                            requireActivity()
                                        )
                                    }
                                }
                                spExteriorColor.onItemSelectedListener = this
                            } else {
                                val arData = ArrayList<ExteriorColorData>()
                                if (lowerBorder == "ANY PRICE") {
                                    val exteriorColorData = ExteriorColorData()
                                    exteriorColorData.exteriorColor = "EXTERIOR COLOR"
                                    arData.add(0, exteriorColorData)
                                    val exteriorColorData1 = ExteriorColorData()
                                    exteriorColorData1.vehicleExteriorColorID = "0"
                                    exteriorColorData1.exteriorColor = "ANY"
                                    arData.add(1, exteriorColorData1)
                                } else {
                                    val exteriorColorData = ExteriorColorData()
                                    exteriorColorData.vehicleExteriorColorID = "0"
                                    exteriorColorData.exteriorColor = "ANY"
                                    arData.add(0, exteriorColorData)
                                }

                                adapterExterior = ExteriorSpinnerAdapter(requireActivity(), arData)
                                spExteriorColor.adapter = adapterExterior
                                spExteriorColor.onItemSelectedListener = this
                            }
                        } catch (e: Exception) {

                        }
                    }
                    )
            } else {
                Toast.makeText(requireActivity(), Constant.noInternet, Toast.LENGTH_SHORT).show()
            }
        } catch (e: Exception) {

        }
    }

    private fun callInteriorColorAPI() {
        try {
            spInteriorColor.isEnabled =
                ((prefSearchDealData.extColorStr != "ANY" || prefSearchDealData.lowerBorder == "ANY PRICE") && prefSearchDealData.extColorStr != "EXTERIOR COLOR")
//            spInteriorColor.isEnabled = true
            if (Constant.isOnline(requireActivity())) {
                if (isEmpty(prefSearchDealData.intColorId)) {
                    if (!Constant.isInitProgress()) {
                        Constant.showLoader(requireActivity())
                    } else if (Constant.isInitProgress() && !Constant.progress.isShowing) {
                        Constant.showLoader(requireActivity())
                    }
                }
                interiorColorModel.getInteriorColor(
                    requireActivity(),
                    productId,
                    yearId,
                    makeId,
                    modelId,
                    trimId,
                    extColorId, "", 3,
                    lowerBorder,
                    upperBorder
                )!!
                    .observe(requireActivity(), Observer { data ->
                        Constant.dismissLoader()
                        try {
                            Log.e("Make Data", Gson().toJson(data))
                            if (data != null || data?.size!! > 0) {
                                if (lowerBorder == "ANY PRICE") {
                                    val interiorColorData = InteriorColorData()
                                    interiorColorData.interiorColor = "INTERIOR COLOR"
                                    data.add(0, interiorColorData)
                                    val interiorColorData1 = InteriorColorData()
                                    interiorColorData1.vehicleInteriorColorID = "0"
                                    interiorColorData1.interiorColor = "ANY"
                                    data.add(1, interiorColorData1)
                                } else {
                                    val interiorColorData1 = InteriorColorData()
                                    interiorColorData1.vehicleInteriorColorID = "0"
                                    interiorColorData1.interiorColor = "ANY"
                                    data.add(0, interiorColorData1)
                                }
                                adapterInterior = InteriorSpinnerAdapter(requireActivity(), data)
                                spInteriorColor.adapter = adapterInterior
                                for (i in 0 until data.size) {
                                    if (!AppGlobal.isEmpty(prefSearchDealData.intColorId) && prefSearchDealData.intColorId == data[i].vehicleInteriorColorID) {
                                        spInteriorColor.setSelection(i, true)
                                        callRadiusAPI()
                                        setSpinnerLayoutPos(
                                            i,
                                            spInteriorColor,
                                            requireActivity()
                                        )
                                    }
                                }
                                spInteriorColor.onItemSelectedListener = this
                            } else {
                                val arData = ArrayList<InteriorColorData>()

                                if (lowerBorder == "ANY PRICE") {
                                    val interiorColorData = InteriorColorData()
                                    interiorColorData.interiorColor = "INTERIOR COLOR"
                                    arData.add(0, interiorColorData)
                                    val interiorColorData1 = InteriorColorData()
                                    interiorColorData1.vehicleInteriorColorID = "0"
                                    interiorColorData1.interiorColor = "ANY"
                                    arData.add(1, interiorColorData1)
                                } else {
                                    val interiorColorData1 = InteriorColorData()
                                    interiorColorData1.vehicleInteriorColorID = "0"
                                    interiorColorData1.interiorColor = "ANY"
                                    arData.add(0, interiorColorData1)
                                }
                                adapterInterior = InteriorSpinnerAdapter(requireActivity(), arData)
                                spInteriorColor.adapter = adapterInterior
                                spInteriorColor.onItemSelectedListener = this
                            }
                        } catch (e: Exception) {

                        }
                    }
                    )
            } else {
                Toast.makeText(requireActivity(), Constant.noInternet, Toast.LENGTH_SHORT).show()
            }
        } catch (e: Exception) {

        }
    }

    private fun callRadiusAPI() {
        try {
            adapterRadius = RadiusSpinnerAdapter(requireActivity(), arRadius)
            spRadius.adapter = adapterRadius
            for (i in 0 until arRadius.size) {
                if (!AppGlobal.isEmpty(radiusId) && radiusId == arRadius[i]) {
                    spRadius.setSelection(i, true)
                    setSpinnerLayoutPos(
                        i,
                        spRadius,
                        requireActivity()
                    )
                }
            }
            spRadius.onItemSelectedListener = this
        } catch (e: Exception) {

        }
//        setSpinnerLayoutPos(0, spRadius, requireActivity())
    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        when (parent?.id) {
            R.id.spPriceRange -> {
                val data = adapterPriceRange.getItem(position) as PriceRangeData
                ucdPriceId = data.ucdPriceRangeID!!
                lowerBorder = if (TextUtils.isEmpty(data.lowerBorder)) "" else data.lowerBorder!!
                upperBorder = if (TextUtils.isEmpty(data.upperBorder)) "" else data.upperBorder!!

//                if (data.lowerBorder != "ANY PRICE") {
                prefSearchDealData.ucdPriceRangeID = data.ucdPriceRangeID
                prefSearchDealData.lowerBorder =
                    if (TextUtils.isEmpty(data.lowerBorder)) "" else data.lowerBorder
                prefSearchDealData.upperBorder =
                    if (TextUtils.isEmpty(data.upperBorder)) "" else data.upperBorder
                prefSearchDealData.yearId = ""
                prefSearchDealData.yearStr = ""
                prefSearchDealData.makeId = ""
                prefSearchDealData.modelId = ""
                prefSearchDealData.trimId = ""
                prefSearchDealData.extColorId = ""
                prefSearchDealData.intColorId = ""
                prefSearchDealData.makeStr = ""
                prefSearchDealData.modelStr = ""
                prefSearchDealData.trimStr = ""
                prefSearchDealData.extColorStr = ""
                prefSearchDealData.intColorStr = ""
                Constant.dismissLoader()
                setPrefData()
                setErrorVisibleGone()
                callVehicleYearAPI()
                setMake()
                setModel()
                setTrim()
                setExteriorColor()
                setInteriorColor()
//                    setRadius()
//                }
                setSpinnerLayoutPos(position, spPriceRange, requireActivity())
            }
            R.id.spYear -> {
                val data = adapterYear.getItem(position) as VehicleYearData
                yearId = data.vehicleYearID!!
                yearStr = data.year!!
                if (data.year != "YEAR - NEW CARS") {
                    prefSearchDealData.yearId = data.vehicleYearID
                    prefSearchDealData.yearStr = data.year
                    prefSearchDealData.makeId = ""
                    prefSearchDealData.modelId = ""
                    prefSearchDealData.trimId = ""
                    prefSearchDealData.extColorId = ""
                    prefSearchDealData.intColorId = ""
                    prefSearchDealData.makeStr = ""
                    prefSearchDealData.modelStr = ""
                    prefSearchDealData.trimStr = ""
                    prefSearchDealData.extColorStr = ""
                    prefSearchDealData.intColorStr = ""
                    Constant.dismissLoader()
                    setPrefData()
                    setErrorVisibleGone()
                    if (prefSearchDealData.yearStr != "ANY")
                        callVehicleMakeAPI()
                    else
                        setMake()
                    setModel()
                    setTrim()
                    setExteriorColor()
                    setInteriorColor()
                    setPrefLYKLCDData()
//                    setRadius()
                }
                setSpinnerLayoutPos(position, spYear, requireActivity())
            }
            R.id.spMake -> {
                val data = adapterMake.getItem(position) as VehicleMakeData
                makeId = data.vehicleMakeID!!
                makeStr = data.make!!
                setSpinnerLayoutPos(position, spMake, requireActivity())
                if (data.make != "MAKE") {
                    prefSearchDealData.makeId = data.vehicleMakeID
                    prefSearchDealData.makeStr = data.make
                    prefSearchDealData.modelId = ""
                    prefSearchDealData.trimId = ""
                    prefSearchDealData.extColorId = ""
                    prefSearchDealData.intColorId = ""
                    prefSearchDealData.modelStr = ""
                    prefSearchDealData.trimStr = ""
                    prefSearchDealData.extColorStr = ""
                    prefSearchDealData.intColorStr = ""
                    Constant.dismissLoader()
                    setPrefData()
                    setErrorVisibleGone()
                    if (prefSearchDealData.makeStr != "ANY")
                        callVehicleModelAPI()
                    else
                        setModel()
                    setTrim()
                    setExteriorColor()
                    setInteriorColor()
                    setPrefLYKLCDData()
//                    setRadius()
                }
            }
            R.id.spModel -> {
                val data = adapterModel.getItem(position) as VehicleModelData
                modelId = data.vehicleModelID!!
                modelStr = data.model!!
                setSpinnerLayoutPos(position, spModel, requireActivity())
                if (data.model != "MODEL") {
                    prefSearchDealData.modelId = data.vehicleModelID
                    prefSearchDealData.modelStr = data.model
                    prefSearchDealData.trimId = ""
                    prefSearchDealData.extColorId = ""
                    prefSearchDealData.intColorId = ""
                    prefSearchDealData.trimStr = ""
                    prefSearchDealData.extColorStr = ""
                    prefSearchDealData.intColorStr = ""
                    Constant.dismissLoader()
                    setPrefData()
                    setErrorVisibleGone()
                    if (prefSearchDealData.modelStr != "ANY")
                        callVehicleTrimAPI()
                    else
                        setTrim()
                    setExteriorColor()
                    setInteriorColor()
                    setPrefLYKLCDData()
//                    setRadius()
                }
            }
            R.id.spTrim -> {
                val data = adapterTrim.getItem(position) as VehicleTrimData
                trimId = data.vehicleTrimID!!
                trimStr = data.trim!!
                setSpinnerLayoutPos(position, spTrim, requireActivity())
                if (data.trim != "TRIM") {
                    prefSearchDealData.trimId = data.vehicleTrimID
                    prefSearchDealData.trimStr = data.trim
                    prefSearchDealData.extColorId = ""
                    prefSearchDealData.intColorId = ""
                    prefSearchDealData.extColorStr = ""
                    prefSearchDealData.intColorStr = ""
                    Constant.dismissLoader()
                    setPrefData()
                    setErrorVisibleGone()
                    if (prefSearchDealData.trimStr != "ANY")
                        callExteriorColorAPI()
                    else
                        setExteriorColor()
                    setInteriorColor()
                    setPrefLYKLCDData()
//                    setRadius()
                }
            }
            R.id.spExteriorColor -> {
                val data = adapterExterior.getItem(position) as ExteriorColorData
//                extColorId = "0"
                extColorId = data.vehicleExteriorColorID!!
                extColorStr = data.exteriorColor!!
                setSpinnerLayoutPos(position, spExteriorColor, requireActivity())
                if (data.exteriorColor != "EXTERIOR COLOR") {
                    prefSearchDealData.extColorId = data.vehicleExteriorColorID
                    prefSearchDealData.extColorStr = data.exteriorColor
                    prefSearchDealData.intColorId = ""
                    prefSearchDealData.intColorStr = ""
                    Constant.dismissLoader()
                    setPrefData()
                    setErrorVisibleGone()
                    if (prefSearchDealData.extColorStr != "ANY" || prefSearchDealData.lowerBorder == "ANY PRICE")
                        callInteriorColorAPI()
                    else
                        setInteriorColor()

                    setPrefLYKLCDData()
//                    setRadius()
                }
            }
            R.id.spInteriorColor -> {
                val data = adapterInterior.getItem(position) as InteriorColorData
                intColorId = data.vehicleInteriorColorID!!
                intColorStr = data.interiorColor!!
                setSpinnerLayoutPos(position, spInteriorColor, requireActivity())
                if (data.interiorColor != "INTERIOR COLOR") {
                    prefSearchDealData.intColorId = data.vehicleInteriorColorID
                    prefSearchDealData.intColorStr = data.interiorColor
                    Constant.dismissLoader()
                    setPrefData()
                    setErrorVisibleGone()
                    callRadiusAPI()
                }
                setPrefLYKLCDData()

            }
            R.id.spRadius -> {
                val data = adapterRadius.getItem(position) as String
                radiusId = data
                setSpinnerLayoutPos(position, spRadius, requireActivity())
                if (data != "SEARCH RADIUS") {
                    prefSearchDealData.searchRadius = data
                    setPrefData()
                    setErrorVisibleGone()
                }
                setPrefLYKLCDData()
            }
        }
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {

    }

    private fun callRefreshTokenApi() {
        if (Constant.isOnline(requireActivity())) {
            Constant.showLoader(requireActivity())
            val request = java.util.HashMap<String, Any>()
            request[ApiConstant.AuthToken] = pref?.getUserData()?.authToken!!
            request[ApiConstant.RefreshToken] = pref?.getUserData()?.refreshToken!!

            tokenModel.refresh(requireActivity(), request)!!.observe(this, { data ->
                Constant.dismissLoader()
                val userData = pref?.getUserData()
                userData?.authToken = data.auth_token!!
                userData?.refreshToken = data.refresh_token!!
                pref?.setUserData(Gson().toJson(userData))
                callSearchFindDealAPI()
            }
            )
        } else {
            Toast.makeText(requireActivity(), Constant.noInternet, Toast.LENGTH_SHORT).show()
        }
    }

    private fun isValid(): Boolean {
        when {
            yearStr == "YEAR - NEW CARS" -> {
                tvErrorYear.visibility = View.VISIBLE
                return false
            }
            makeStr == "MAKE" -> {
                tvErrorMake.visibility = View.VISIBLE
                return false
            }
            modelStr == "MODEL" -> {
                tvErrorModel.visibility = View.VISIBLE
                return false
            }
            trimStr == "TRIM" -> {
                tvErrorTrim.visibility = View.VISIBLE
                return false
            }
            extColorStr == "EXTERIOR COLOR" -> {
                tvErrorExterior.visibility = View.VISIBLE
                return false
            }
            intColorStr == "INTERIOR COLOR" -> {
                tvErrorInterior.visibility = View.VISIBLE
                return false
            }
            TextUtils.isEmpty(edtZipCode.text.toString().trim()) -> {
                tvErrorZipCode.text = getString(R.string.enter_zipcode)
                tvErrorZipCode.visibility = View.VISIBLE
                return false
            }
            !isValidZipCode -> {
                tvErrorZipCode.text = getString(R.string.invalid_zip_code)
                tvErrorZipCode.visibility = View.VISIBLE
                return false
            }
            radiusId == "SEARCH RADIUS" -> {
                tvErrorRadius.visibility = View.VISIBLE
                return false
            }
        }
        return true
    }

    private fun setErrorVisibleGone() {
        tvErrorYear.visibility = View.GONE
        tvErrorMake.visibility = View.GONE
        tvErrorModel.visibility = View.GONE
        tvErrorTrim.visibility = View.GONE
        tvErrorExterior.visibility = View.GONE
        tvErrorInterior.visibility = View.GONE
        tvErrorRadius.visibility = View.GONE
    }

    override fun onDestroy() {
        if (Constant.isInitProgress() && Constant.progress.isShowing)
            Constant.dismissLoader()
        super.onDestroy()
    }

    override fun onPause() {
        if (Constant.isInitProgress() && Constant.progress.isShowing)
            Constant.dismissLoader()
        super.onPause()
    }

    private lateinit var prefSearchDealData: PrefSearchDealData
    private lateinit var handler: Handler
    override fun onResume() {
        super.onResume()
        if (Constant.isInitProgress() && Constant.progress.isShowing)
            Constant.dismissLoader()
        startHandler()
    }

    private fun setPrefData() {
        prefSearchDealData.isUCDSel = true
        pref?.setSearchDealData(Gson().toJson(prefSearchDealData))
        setCurrentTime()
    }

    private fun setPrefZipCodeData() {
        prefSearchDealData.isUCDSelZipCode = true
        pref?.setSearchDealData(Gson().toJson(prefSearchDealData))
        setCurrentTime()
    }

    private fun setInitPrefData() {
        prefSearchDealData.isUCDSel = false
        pref?.setSearchDealData(Gson().toJson(prefSearchDealData))
        setCurrentTime()
    }

    private fun setCurrentTime() {
        val df = SimpleDateFormat("yyyy MM d, HH:mm:ss a")
        val date = df.format(Calendar.getInstance().time)
        pref?.setSearchDealTime(date)
//        Log.e("Submit Date", date)
        startHandler()
    }

    private fun startHandler() {
        if (!AppGlobal.isEmpty(pref?.getSearchDealTime())) {
//            Log.e("DAte Time", AppGlobal.stringToDate(pref?.getSearchDealTime())?.toString()!!)
            handler = Handler()
            handler.postDelayed(runnable, 1000)
        } else {
            if (tvPriceRange.visibility == View.GONE) {
//            if (tvYear.visibility == View.GONE) {
                yearStr = "YEAR - NEW CARS"
                makeStr = "MAKE"
                modelStr = "MODEL"
                trimStr = "TRIM"
                extColorStr = "EXTERIOR COLOR"
                intColorStr = "INTERIOR COLOR"
                radiusId = "SEARCH RADIUS"
//                tvYear.visibility = View.VISIBLE
//                spYear.visibility = View.GONE
                tvPriceRange.visibility = View.VISIBLE
                spPriceRange.visibility = View.GONE
            }
        }

    }

    private var runnable = object : Runnable {
        override fun run() {
            try {
                val date = Calendar.getInstance().time
                val lastDate = AppGlobal.stringToDate(pref?.getSearchDealTime())

                val diff: Long = date.time - (lastDate?.time ?: 0)
                print(diff)

                val seconds = diff / 1000
                val minutes = seconds / 60
                if (minutes >= 30) {
                    handler.removeCallbacks(this)
                    pref?.setSearchDealData(Gson().toJson(PrefSearchDealData()))
                    pref?.setSearchDealTime("")
                    setTimerPrefData()
                    if (tvPriceRange.visibility == View.GONE) {
//                    if (tvYear.visibility == View.GONE) {
                        lowerBorder = "ANY PRICE"
                        upperBorder = ""
                        yearStr = "YEAR - NEW CARS"
                        makeStr = "MAKE"
                        modelStr = "MODEL"
                        trimStr = "TRIM"
                        extColorStr = "EXTERIOR COLOR"
                        intColorStr = "INTERIOR COLOR"
                        radiusId = "SEARCH RADIUS"
//                        tvYear.visibility = View.VISIBLE
//                        spYear.visibility = View.GONE
                        tvPriceRange.visibility = View.VISIBLE
                        spPriceRange.visibility = View.GONE
                    }
                } else {
                    handler.postDelayed(this, 1000)
                }
            } catch (e: Exception) {

            }
        }
    }

    private fun setTimerPrefData() {
        try {
            prefSearchDealData = pref?.getSearchDealData()!!
            setPriceRange()
            setYear()
            setMake()
            setModel()
            setTrim()
            setExteriorColor()
            setInteriorColor()
            onChangeZipCode()

            ucdPriceId = prefSearchDealData.ucdPriceRangeID!!
            yearId = prefSearchDealData.yearId!!
            makeId = prefSearchDealData.makeId!!
            modelId = prefSearchDealData.modelId!!
            trimId = prefSearchDealData.trimId!!
            extColorId = prefSearchDealData.extColorId!!
            intColorId = prefSearchDealData.intColorId!!
            radiusId = prefSearchDealData.searchRadius!!
            lowerBorder = prefSearchDealData.lowerBorder!!
            upperBorder = prefSearchDealData.upperBorder!!
            yearStr = prefSearchDealData.yearStr!!
            makeStr = prefSearchDealData.makeStr!!
            modelStr = prefSearchDealData.modelStr!!
            trimStr = prefSearchDealData.trimStr!!
            extColorStr = prefSearchDealData.extColorStr!!
            intColorStr = prefSearchDealData.intColorStr!!
            if (TextUtils.isEmpty(ucdPriceId)) {
                lowerBorder = "ANY PRICE"
                upperBorder = ""
                yearStr = "YEAR - NEW CARS"
                makeStr = "MAKE"
                modelStr = "MODEL"
                trimStr = "TRIM"
                extColorStr = "EXTERIOR COLOR"
                intColorStr = "INTERIOR COLOR"
                radiusId = "SEARCH RADIUS"
                /*tvYear.visibility = View.VISIBLE
                spYear.visibility = View.GONE*/
                callPriceRangeAPI()
                tvPriceRange.visibility = View.VISIBLE
                spPriceRange.visibility = View.GONE
            } else {
                tvPriceRange.visibility = View.GONE
                spPriceRange.visibility = View.VISIBLE
                /* tvYear.visibility = View.GONE
                 spYear.visibility = View.VISIBLE*/
                if (!isCallingYear)
                    callPriceRangeAPI()
//                    callVehicleYearAPI()
            }
            edtZipCode.setText(prefSearchDealData.zipCode)
            if (prefSearchDealData.zipCode?.length!! >= 1) {
                val str = prefSearchDealData.zipCode.toString()
                if (str.length == 5) {
                    callVehicleZipCodeAPI(str)
                } else if (str.length < 5) {
                    isValidZipCode = false
                    prefSearchDealData.isZipCode = isValidZipCode!!
                    tvErrorZipCode.visibility = View.GONE
                    edtZipCode.setBackgroundResource(R.drawable.bg_edittext_dark)
                }
                prefSearchDealData.zipCode = edtZipCode.text.toString().trim()
                setInitPrefData()
            }

            callRadiusAPI()
        } catch (e: Exception) {

        }
    }

    private lateinit var dialogMobileNo: Dialog
    private fun dialogPhoneNo(isCancel: Boolean) {
        dialogMobileNo = Dialog(requireActivity(), R.style.FullScreenDialog)
        dialogMobileNo.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialogMobileNo.setCancelable(true)
        dialogMobileNo.setContentView(R.layout.dialog_mobile_no)
        Constant.onTextChange(
            requireActivity(),
            dialogMobileNo.edtPhoneNumber,
            dialogMobileNo.tvErrorPhoneNo
        )
        dialogMobileNo.edtPhoneNumber.filters =
            arrayOf<InputFilter>(filterSocMob, InputFilter.LengthFilter(13))
        if (isCancel) {
            dialogMobileNo.ivSocClose.visibility = View.VISIBLE
        }
        dialogMobileNo.run {
            btnDialogSave.setOnClickListener {
                if (TextUtils.isEmpty(edtPhoneNumber.text.toString().trim())) {
                    Constant.setErrorBorder(edtPhoneNumber, tvErrorPhoneNo)
                    tvErrorPhoneNo.text = getString(R.string.enter_phonenumber)
                } else if (edtPhoneNumber.text.toString().trim().length != 13) {
                    Constant.setErrorBorder(edtPhoneNumber, tvErrorPhoneNo)
                    tvErrorPhoneNo.text = getString(R.string.enter_valid_phone_number)
                } else {
                    tvErrorPhoneNo.visibility = View.GONE
                    callSocialMobileAPI(edtPhoneNumber.text.toString().trim())
                    dismiss()
                }
            }
            ivSocClose.setOnClickListener {
                dismiss()
            }
        }
        setLayoutParam(dialogMobileNo)
        dialogMobileNo.show()
    }

    private fun callSocialMobileAPI(phoneNo: String) {
        val data: LoginData = pref?.getUserData()!!
        if (Constant.isOnline(requireActivity())) {
            if (!Constant.isInitProgress()) {
                Constant.showLoader(requireActivity())
            } else if (Constant.isInitProgress() && !Constant.progress.isShowing) {
                Constant.showLoader(requireActivity())
            }
            val request = HashMap<String, Any>()
            request[ApiConstant.FirstNameSoc] = data.firstName!!
            request[ApiConstant.LastNameSoc] = data.lastName!!
            request[ApiConstant.UserNameSoc] = data.userName!!
            request[ApiConstant.EmailSoc] = data.userName!!
            request[ApiConstant.PhoneNumberSoc] = phoneNo

            socialMobileViewModel.getSocialMobile(requireActivity(), request)!!
                .observe(this, Observer { dataSocial ->
                    Constant.dismissLoader()
//                    data.authToken = dataSocial.authToken
//                    data.refreshToken = dataSocial.refreshToken
                    data.message = dataSocial.message
                    if (data.buyerId != 0) {
                        pref?.setLogin(true)
                        data.isSocial = true
                        pref?.setUserData(Gson().toJson(data))
                        pref?.updateSocialMobile(true)
                        callSearchFindDealAPI()
                    } else {
                        Toast.makeText(
                            requireActivity(),
                            resources.getString(R.string.login_failed),
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
                )
        } else {
            Toast.makeText(requireActivity(), Constant.noInternet, Toast.LENGTH_SHORT).show()
        }
    }

    private var filterSocMob = InputFilter { source, start, end, dest, dstart, dend ->
        dialogMobileNo.run {
            var source = source
            if (source.length > 0) {
                if (!Character.isDigit(source[0])) return@InputFilter "" else {
                    if (source.toString().length > 1) {
                        val number = source.toString()
                        val digits1 = number.toCharArray()
                        val digits2 = number.split("(?<=.)").toTypedArray()
                        source = digits2[digits2.size - 1]
                    }
                    if (edtPhoneNumber.text.toString().isEmpty()) {
                        return@InputFilter "($source"
                    } else if (edtPhoneNumber.text.toString().length > 1 && edtPhoneNumber.text.toString()
                            .length <= 3
                    ) {
                        return@InputFilter source
                    } else if (edtPhoneNumber.text.toString().length > 3 && edtPhoneNumber.text.toString()
                            .length <= 5
                    ) {
                        val isContain = dest.toString().contains(")")
                        return@InputFilter if (isContain) {
                            source
                        } else {
                            ")$source"
                        }
                    } else if (edtPhoneNumber.text.toString().length > 5 && edtPhoneNumber.text.toString()
                            .length <= 7
                    ) {
                        return@InputFilter source
                    } else if (edtPhoneNumber.text.toString().length > 7) {
                        val isContain = dest.toString().contains("-")
                        return@InputFilter if (isContain) {
                            source
                        } else {
                            "-$source"
                        }
                    }
                }
            } else {
            }
            source
        }
    }

    private fun setLayoutParam(dialog: Dialog) {
        val layoutParams: WindowManager.LayoutParams = dialog.window?.attributes!!
        dialog.window?.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.MATCH_PARENT
        )
        dialog.window?.attributes = layoutParams
    }

    private fun callPromotionAPI() {
        if (Constant.isOnline(requireActivity())) {
            if (!Constant.isInitProgress()) {
                Constant.showLoader(requireActivity())
            } else if (Constant.isInitProgress() && !Constant.progress.isShowing) {
                Constant.showLoader(requireActivity())
            }
            promotionViewModel.getPromoCode(requireActivity())!!
                .observe(requireActivity(), Observer { data ->
                    try {
                        if (TextUtils.isEmpty(data.promotionCode) || data.discount == 0.0 || TextUtils.isEmpty(
                                data.endDate
                            )
                        ) {
                            tvPromo.visibility = View.GONE
                            llPromoOffer.visibility = View.GONE
                        } else {
                            tvPromo.visibility = View.VISIBLE
                            binding.promoData = data
                        }
                    } catch (e: Exception) {

                    }
                }
                )
        } else {
            Toast.makeText(requireActivity(), Constant.noInternet, Toast.LENGTH_SHORT).show()
        }
    }

    private fun setClearData() {
//        tvYear.visibility = View.GONE
        spYear.visibility = View.VISIBLE
        setYear()
        setMake()
        setModel()
        setTrim()
        prefSearchDealData = PrefSearchDealData()
        pref?.setSearchDealData(Gson().toJson(prefSearchDealData))
    }


    private fun setLCDPrefData() {
        val prefUCD = pref?.getSearchDealData()
        val lcdData = pref?.getOneDealNearYouData()
        val prefLYK = pref?.getSubmitPriceData()
        if (!lcdData?.isLCD!! && !prefUCD?.isUCDSel!! && prefUCD.isUCDSelZipCode!!) {
            if (isValidZipCode) {
                lcdData.zipCode = prefUCD.zipCode
                lcdData.isZipCode = true
                lcdData.yearId = prefUCD.yearId
                lcdData.yearStr = prefUCD.yearStr
                lcdData.makeId = prefUCD.makeId
                lcdData.makeStr = prefUCD.makeStr
                lcdData.modelId = prefUCD.modelId
                lcdData.modelStr = prefUCD.modelStr
                lcdData.trimId = prefUCD.trimId
                lcdData.trimStr = prefUCD.trimStr
                lcdData.extColorId = prefUCD.extColorId
                lcdData.extColorStr = prefUCD.extColorStr
                lcdData.intColorId = prefUCD.intColorId
                lcdData.intColorStr = prefUCD.intColorStr
                lcdData.packagesData = prefLYK?.packagesData
                lcdData.optionsData = prefLYK?.optionsData
                setLCDPrefData(lcdData!!)
            }
        } else {
            if (!lcdData?.isLCD!!) {
                setLCDPrefData(PrefOneDealNearYouData())
            }
        }
    }

    private fun setLCDPrefData(lcdData: PrefOneDealNearYouData) {
        pref?.setOneDealNearYouData(Gson().toJson(lcdData))
        setLCDCurrentTime()
    }

    private fun setLCDCurrentTime() {
        val df = SimpleDateFormat("yyyy MM d, HH:mm:ss a")
        val date = df.format(Calendar.getInstance().time)
        pref?.setOneDealNearYou(date)
    }

    private fun setPrefLYKLCDData() {
        setPrefUCDtoLCD()
        setPrefUCDtoLYK()
    }

    private fun setPrefUCDtoLCD() {
        val prefUCD = pref?.getSearchDealData()
        val prefLCD = pref?.getOneDealNearYouData()
        val prefLYK = pref?.getSubmitPriceData()

        if (!prefLYK?.isLYK!! && !prefLCD?.isLCD!!) {
            if (isValidZipCode) {
                prefLCD.zipCode = prefUCD?.zipCode
                prefLCD.isZipCode = true
                prefLCD.yearId = prefUCD?.yearId
                prefLCD.yearStr = prefUCD?.yearStr
                prefLCD.makeId = prefUCD?.makeId
                prefLCD.makeStr = prefUCD?.makeStr
                prefLCD.modelId = prefUCD?.modelId
                prefLCD.modelStr = prefUCD?.modelStr
                prefLCD.trimId = prefUCD?.trimId
                prefLCD.trimStr = prefUCD?.trimStr
                prefLCD.extColorId = prefUCD?.extColorId
                prefLCD.extColorStr = prefUCD?.extColorStr
                prefLCD.intColorId = prefUCD?.intColorId
                prefLCD.intColorStr = prefUCD?.intColorStr
                setLCDPrefData(prefLCD)
            }
        }
    }

    private fun setPrefUCDtoLYK() {
        val prefUCD = pref?.getSearchDealData()
        val prefLCD = pref?.getOneDealNearYouData()
        val prefLYK = pref?.getSubmitPriceData()

        if (!prefLYK?.isLYK!! && !prefLCD?.isLCD!!) {
            prefLYK.zipCode = prefUCD?.zipCode
            prefLYK.yearId = prefUCD?.yearId
            prefLYK.yearStr = prefUCD?.yearStr
            prefLYK.makeId = prefUCD?.makeId
            prefLYK.makeStr = prefUCD?.makeStr
            prefLYK.modelId = prefUCD?.modelId
            prefLYK.modelStr = prefUCD?.modelStr
            prefLYK.trimId = prefUCD?.trimId
            prefLYK.trimStr = prefUCD?.trimStr
            prefLYK.extColorId = prefUCD?.extColorId
            prefLYK.extColorStr = prefUCD?.extColorStr
            prefLYK.intColorId = prefUCD?.intColorId
            prefLYK.intColorStr = prefUCD?.intColorStr
            prefLYK.radius = prefUCD?.searchRadius
            setUCDtoLYKPrefData(prefLYK)
        }
    }

    private fun setUCDtoLYKPrefData(lykData: PrefSubmitPriceData) {
        pref?.setSubmitPriceData(Gson().toJson(lykData))
        setLYKCurrentTime()
    }

    private fun setLYKCurrentTime() {
        val df = SimpleDateFormat("yyyy MM d, HH:mm:ss a")
        val date = df.format(Calendar.getInstance().time)
        pref?.setSubmitPriceTime(date)
    }
}