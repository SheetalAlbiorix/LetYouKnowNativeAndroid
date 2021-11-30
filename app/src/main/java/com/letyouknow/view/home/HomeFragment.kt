package com.letyouknow.view.home

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
import android.widget.AdapterView
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.google.gson.Gson
import com.letyouknow.R
import com.letyouknow.base.BaseFragment
import com.letyouknow.databinding.FragmentHomeBinding
import com.letyouknow.model.*
import com.letyouknow.retrofit.ApiConstant
import com.letyouknow.retrofit.viewmodel.*
import com.letyouknow.utils.AppGlobal
import com.letyouknow.utils.AppGlobal.Companion.isEmpty
import com.letyouknow.utils.AppGlobal.Companion.setSpinnerLayoutPos
import com.letyouknow.view.dashboard.MainActivity
import com.letyouknow.view.spinneradapter.*
import com.letyouknow.view.unlockedcardeal.UnlockedCarDealActivity
import com.pionymessenger.utils.Constant
import com.pionymessenger.utils.Constant.Companion.ARG_RADIUS
import com.pionymessenger.utils.Constant.Companion.ARG_YEAR_MAKE_MODEL
import com.pionymessenger.utils.Constant.Companion.ARG_ZIPCODE
import kotlinx.android.synthetic.main.fragment_home.*
import org.jetbrains.anko.support.v4.startActivity
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap


class HomeFragment : BaseFragment(), View.OnClickListener, AdapterView.OnItemSelectedListener {
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

    private lateinit var binding: FragmentHomeBinding
    private var upDownData = UpDownData()

    private var isValidZipCode = false

    private lateinit var vehicleYearModel: VehicleYearViewModel
    private lateinit var vehicleMakeModel: VehicleMakeViewModel
    private lateinit var vehicleModelModel: VehicleModelViewModel
    private lateinit var vehicleTrimModel: VehicleTrimViewModel
    private lateinit var exteriorColorModel: ExteriorColorViewModel
    private lateinit var interiorColorModel: InteriorColorViewModel
    private lateinit var zipCodeModel: VehicleZipCodeViewModel
    private lateinit var findUCDDealGuestViewModel: FindUCDDealViewModel

    private lateinit var adapterYear: YearSpinnerAdapter
    private lateinit var adapterMake: MakeSpinnerAdapter
    private lateinit var adapterModel: ModelSpinnerAdapter
    private lateinit var adapterTrim: TrimsSpinnerAdapter
    private lateinit var adapterExterior: ExteriorSpinnerAdapter
    private lateinit var adapterInterior: InteriorSpinnerAdapter
    private lateinit var adapterRadius: RadiusSpinnerAdapter

    private var productId = "3"
    private var yearId = ""
    private var makeId = ""
    private var modelId = ""
    private var trimId = ""

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
            R.layout.fragment_home,
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

            vehicleYearModel = ViewModelProvider(this).get(VehicleYearViewModel::class.java)
            vehicleMakeModel = ViewModelProvider(this).get(VehicleMakeViewModel::class.java)
            vehicleModelModel = ViewModelProvider(this).get(VehicleModelViewModel::class.java)
            vehicleTrimModel = ViewModelProvider(this).get(VehicleTrimViewModel::class.java)
            exteriorColorModel = ViewModelProvider(this).get(ExteriorColorViewModel::class.java)
            interiorColorModel = ViewModelProvider(this).get(InteriorColorViewModel::class.java)
            zipCodeModel = ViewModelProvider(this).get(VehicleZipCodeViewModel::class.java)
            findUCDDealGuestViewModel =
                ViewModelProvider(this).get(FindUCDDealViewModel::class.java)
            tokenModel = ViewModelProvider(this).get(RefreshTokenViewModel::class.java)


            btnProceedDeal.setOnClickListener(this)
            tvPromo.setOnClickListener(this)
            ivClosePromo.setOnClickListener(this)
            MainActivity.getInstance().setVisibleEditImg(false)
            MainActivity.getInstance().setVisibleLogoutImg(false)
            setTimerPrefData()
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

    private fun setYear() {
        try {
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
            makeData.make = "MAKE"
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
            modelData.model = "MODEL"
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
            trimData.trim = "TRIM"
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
            val trimData = ExteriorColorData()
            trimData.exteriorColor = "EXTERIOR COLOR"
            arData.add(0, trimData)
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
            interiorData.interiorColor = "INTERIOR COLOR"
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

        if (Constant.isOnline(requireActivity())) {
            Constant.showLoader(requireActivity())

            val request = HashMap<String, Any>()
            request[ApiConstant.vehicleYearID] = yearId
            request[ApiConstant.vehicleMakeID] = makeId
            request[ApiConstant.vehicleModelID] = modelId
            request[ApiConstant.vehicleTrimID] = trimId
            request[ApiConstant.vehicleExteriorColorID] = extColorId
            request[ApiConstant.vehicleInteriorColorID] = intColorId
            request[ApiConstant.zipCode] = edtZipCode.text.toString().trim()
            request[ApiConstant.searchRadius] =
                if (radiusId == "ALL") "6000" else radiusId.replace("mi", "").trim()
            Log.e("Request Find Deal", Gson().toJson(request))
            findUCDDealGuestViewModel.findDeal(requireActivity(), request)!!
                .observe(this, Observer { data ->
                    Constant.dismissLoader()
                    Log.e("Response", Gson().toJson(data))
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
                    startActivity<UnlockedCarDealActivity>(
                        Constant.ARG_UCD_DEAL to Gson().toJson(
                            data
                        ),
                        ARG_YEAR_MAKE_MODEL to Gson().toJson(dataYear),
                        ARG_RADIUS to radiusId,
                        ARG_ZIPCODE to edtZipCode.text.toString().trim()
                    )
                }
                )

        } else {
            Toast.makeText(requireActivity(), Constant.noInternet, Toast.LENGTH_SHORT).show()
        }
    }



    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.btnProceedDeal -> {
                if (isValid()) {
                    callRefreshTokenApi()
                }
            }
            R.id.tvPromo -> {
                tvPromo.clearAnimation()
                tvPromo.visibility = View.GONE
                llPromoOffer.visibility = View.VISIBLE
                llPromoOffer.startAnimation(animSlideRightToLeft)
            }
            R.id.ivClosePromo -> {
                tvPromo.startAnimation(animBlink)
                tvPromo.visibility = View.VISIBLE
                llPromoOffer.visibility = View.GONE
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
                        setPrefData()
                    } catch (e: Exception) {
                    }
                }
                )
        } else {
            Toast.makeText(requireActivity(), Constant.noInternet, Toast.LENGTH_SHORT).show()
        }
    }

    var isCallingYear = false
    private fun callVehicleYearAPI() {
        try {
            isCallingYear = true
            if (Constant.isOnline(requireActivity())) {
                Constant.showLoader(requireActivity())
                vehicleYearModel.getYear(requireActivity(), productId, "")!!
                    .observe(requireActivity(), Observer { data ->
                        if (isEmpty(prefSearchDealData.makeId))
                            Constant.dismissLoader()
                        isCallingYear = false
                        Log.e("Year Data", Gson().toJson(data))
                        try {
                            if (data != null || data?.size!! > 0) {
                                val yearData = VehicleYearData()
                                yearData.year = "YEAR - NEW CARS"
                                data.add(0, yearData)
                                adapterYear = YearSpinnerAdapter(requireActivity(), data)
                                spYear.adapter = adapterYear
                                for (i in 0 until data.size) {
                                    if (!AppGlobal.isEmpty(prefSearchDealData.yearId) && prefSearchDealData.yearId == data[i].vehicleYearID) {
                                        spYear.setSelection(i, true)
                                        AppGlobal.setSpinnerLayoutPos(i, spYear, requireActivity())
                                        callVehicleMakeAPI()
                                    }
                                }
                                spYear.onItemSelectedListener = this
                            } else {
                                val arData = ArrayList<VehicleYearData>()
                                val yearData = VehicleYearData()
                                yearData.year = "YEAR - NEW CARS"
                                arData.add(0, yearData)
                                adapterYear = YearSpinnerAdapter(requireActivity(), arData)
                                spYear.adapter = adapterYear
                                spYear.onItemSelectedListener = this
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
            spMake.isEnabled = true
            if (Constant.isOnline(requireActivity())) {
                if (isEmpty(prefSearchDealData.makeId))
                    Constant.showLoader(requireActivity())
                vehicleMakeModel.getMake(requireActivity(), productId, yearId, "")!!
                    .observe(requireActivity(), Observer { data ->
                        if (isEmpty(prefSearchDealData.modelId))
                            Constant.dismissLoader()
                        try {
                            Log.e("Make Data", Gson().toJson(data))
                            if (data != null || data?.size!! > 0) {
                                val makeData = VehicleMakeData()
                                makeData.make = "MAKE"
                                data.add(0, makeData)
                                adapterMake = MakeSpinnerAdapter(requireActivity(), data)
                                spMake.adapter = adapterMake
                                for (i in 0 until data.size) {
                                    if (!AppGlobal.isEmpty(prefSearchDealData.makeId) && prefSearchDealData.makeId == data[i].vehicleMakeID) {
                                        spMake.setSelection(i, true)
                                        callVehicleModelAPI()
                                        AppGlobal.setSpinnerLayoutPos(i, spMake, requireActivity())
                                    }
                                }
                                spMake.onItemSelectedListener = this
                            } else {
                                val arData = ArrayList<VehicleMakeData>()
                                val makeData = VehicleMakeData()
                                makeData.make = "MAKE"
                                arData.add(0, makeData)
                                adapterMake = MakeSpinnerAdapter(requireActivity(), arData)
                                spMake.adapter = adapterMake
                                spMake.onItemSelectedListener = this
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
            spModel.isEnabled = true
            if (Constant.isOnline(requireActivity())) {
                if (isEmpty(prefSearchDealData.modelId))
                    Constant.showLoader(requireActivity())
                vehicleModelModel.getModel(requireActivity(), productId, yearId, makeId, "")!!
                    .observe(requireActivity(), Observer { data ->
                        if (isEmpty(prefSearchDealData.trimId))
                            Constant.dismissLoader()
                        try {
                            Log.e("Make Data", Gson().toJson(data))
                            if (data != null || data?.size!! > 0) {
                                val modelData = VehicleModelData()
                                modelData.model = "MODEL"
                                data.add(0, modelData)
                                adapterModel = ModelSpinnerAdapter(requireActivity(), data)
                                spModel.adapter = adapterModel
                                for (i in 0 until data.size) {
                                    if (!AppGlobal.isEmpty(prefSearchDealData.modelId) && prefSearchDealData.modelId == data[i].vehicleModelID) {
                                        spModel.setSelection(i, true)
                                        callVehicleTrimAPI()
                                        AppGlobal.setSpinnerLayoutPos(i, spModel, requireActivity())
                                    }
                                }
                                spModel.onItemSelectedListener = this
                            } else {
                                val arData = ArrayList<VehicleModelData>()
                                val modelData = VehicleModelData()
                                modelData.model = "MODEL"
                                arData.add(0, modelData)
                                adapterModel = ModelSpinnerAdapter(requireActivity(), arData)
                                spModel.adapter = adapterModel
                                spModel.onItemSelectedListener = this
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
            spTrim.isEnabled = true
            if (Constant.isOnline(requireActivity())) {
                if (isEmpty(prefSearchDealData.trimId))
                    Constant.showLoader(requireActivity())
                vehicleTrimModel.getTrim(
                    requireActivity(),
                    productId,
                    yearId,
                    makeId,
                    modelId,
                    ""
                )!!
                    .observe(requireActivity(), Observer { data ->
                        if (isEmpty(prefSearchDealData.extColorId))
                            Constant.dismissLoader()
                        Log.e("Make Data", Gson().toJson(data))
                        try {
                            if (data != null || data?.size!! > 0) {
                                val trimData = VehicleTrimData()
                                trimData.trim = "TRIM"
                                data.add(0, trimData)
                                adapterTrim = TrimsSpinnerAdapter(requireActivity(), data)
                                spTrim.adapter = adapterTrim
                                for (i in 0 until data.size) {
                                    if (!AppGlobal.isEmpty(prefSearchDealData.trimId) && prefSearchDealData.trimId == data[i].vehicleTrimID) {
                                        spTrim.setSelection(i, true)
                                        callExteriorColorAPI()
                                        AppGlobal.setSpinnerLayoutPos(i, spTrim, requireActivity())
                                    }
                                }
                                spTrim.onItemSelectedListener = this
                            } else {
                                val arData = ArrayList<VehicleTrimData>()
                                val trimData = VehicleTrimData()
                                trimData.trim = "TRIM"
                                arData.add(0, trimData)
                                adapterTrim = TrimsSpinnerAdapter(requireActivity(), arData)
                                spTrim.adapter = adapterTrim
                                spTrim.onItemSelectedListener = this
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
            spExteriorColor.isEnabled = true
            if (Constant.isOnline(requireActivity())) {
                if (isEmpty(prefSearchDealData.extColorId))
                    Constant.showLoader(requireActivity())
                exteriorColorModel.getExteriorColor(
                    requireActivity(),
                    productId,
                    yearId,
                    makeId,
                    modelId,
                    trimId, ""
                )!!
                    .observe(requireActivity(), Observer { data ->
                        if (isEmpty(prefSearchDealData.intColorId))
                            Constant.dismissLoader()
                        try {
                            Log.e("Make Data", Gson().toJson(data))
                            if (data != null || data?.size!! > 0) {
                                val exteriorColorData = ExteriorColorData()
                                exteriorColorData.exteriorColor = "EXTERIOR COLOR"
                                data.add(0, exteriorColorData)
                                val exteriorColorData1 = ExteriorColorData()
                                exteriorColorData1.vehicleExteriorColorID = "0"
                                exteriorColorData1.exteriorColor = "ANY"
                                data.add(1, exteriorColorData1)
                                adapterExterior = ExteriorSpinnerAdapter(requireActivity(), data)
                                spExteriorColor.adapter = adapterExterior
                                for (i in 0 until data.size) {
                                    if (!AppGlobal.isEmpty(prefSearchDealData.extColorId) && prefSearchDealData.extColorId == data[i].vehicleExteriorColorID) {
                                        spExteriorColor.setSelection(i, true)
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
                                val exteriorColorData = ExteriorColorData()
                                exteriorColorData.exteriorColor = "EXTERIOR COLOR"
                                arData.add(0, exteriorColorData)
                                val exteriorColorData1 = ExteriorColorData()
                                exteriorColorData1.vehicleExteriorColorID = "0"
                                exteriorColorData1.exteriorColor = "ANY"
                                arData.add(1, exteriorColorData1)
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
            spInteriorColor.isEnabled = true
            if (Constant.isOnline(requireActivity())) {
                if (isEmpty(prefSearchDealData.intColorId))
                    Constant.showLoader(requireActivity())
                interiorColorModel.getInteriorColor(
                    requireActivity(),
                    productId,
                    yearId,
                    makeId,
                    modelId,
                    trimId,
                    extColorId, ""
                )!!
                    .observe(requireActivity(), Observer { data ->
                        Constant.dismissLoader()
                        try {
                            Log.e("Make Data", Gson().toJson(data))
                            if (data != null || data?.size!! > 0) {
                                val interiorColorData = InteriorColorData()
                                interiorColorData.interiorColor = "INTERIOR COLOR"
                                data.add(0, interiorColorData)
                                val interiorColorData1 = InteriorColorData()
                                interiorColorData1.vehicleInteriorColorID = "0"
                                interiorColorData1.interiorColor = "ANY"
                                data.add(1, interiorColorData1)
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
                                val interiorColorData = InteriorColorData()
                                interiorColorData.interiorColor = "INTERIOR COLOR"
                                arData.add(0, interiorColorData)
                                val interiorColorData1 = InteriorColorData()
                                interiorColorData1.vehicleInteriorColorID = "0"
                                interiorColorData1.interiorColor = "ANY"
                                arData.add(1, interiorColorData1)
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
                    callVehicleMakeAPI()
                    setModel()
                    setTrim()
                    setExteriorColor()
                    setInteriorColor()
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
                    callVehicleModelAPI()
                    setTrim()
                    setExteriorColor()
                    setInteriorColor()
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
                    callVehicleTrimAPI()
                    setExteriorColor()
                    setInteriorColor()
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
                    callExteriorColorAPI()
                    setInteriorColor()
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
                    callInteriorColorAPI()
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

    private lateinit var prefSearchDealData: PrefSearchDealData
    private lateinit var handler: Handler
    override fun onResume() {
        super.onResume()
        startHandler()
    }

    private fun setPrefData() {
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
        }

    }

    private var runnable = object : Runnable {
        override fun run() {
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
            } else {
                handler.postDelayed(this, 1000)
            }
        }

    }

    private fun setTimerPrefData() {
        try {
            prefSearchDealData = pref?.getSearchDealData()!!
            setYear()
            setMake()
            setModel()
            setTrim()
            setExteriorColor()
            setInteriorColor()
            callRadiusAPI()
            onChangeZipCode()

            yearId = prefSearchDealData.yearId!!
            makeId = prefSearchDealData.makeId!!
            modelId = prefSearchDealData.modelId!!
            trimId = prefSearchDealData.trimId!!
            extColorId = prefSearchDealData.extColorId!!
            intColorId = prefSearchDealData.intColorId!!
            radiusId = prefSearchDealData.searchRadius!!
            yearStr = prefSearchDealData.yearStr!!
            makeStr = prefSearchDealData.makeStr!!
            modelStr = prefSearchDealData.modelStr!!
            trimStr = prefSearchDealData.trimStr!!
            extColorStr = prefSearchDealData.extColorStr!!
            intColorStr = prefSearchDealData.intColorStr!!

            if (!isCallingYear)
                callVehicleYearAPI()
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
                setPrefData()
            }
        } catch (e: Exception) {

        }
    }
}