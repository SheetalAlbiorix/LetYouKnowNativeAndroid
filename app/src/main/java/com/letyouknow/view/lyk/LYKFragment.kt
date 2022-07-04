package com.letyouknow.view.lyk

import android.app.Dialog
import android.os.Bundle
import android.os.Handler
import android.text.InputFilter
import android.text.TextUtils
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
import com.google.gson.JsonArray
import com.google.gson.reflect.TypeToken
import com.letyouknow.R
import com.letyouknow.base.BaseFragment
import com.letyouknow.databinding.FragmentLykBinding
import com.letyouknow.model.*
import com.letyouknow.retrofit.ApiConstant
import com.letyouknow.retrofit.viewmodel.*
import com.letyouknow.utils.AppGlobal
import com.letyouknow.utils.AppGlobal.Companion.alertError
import com.letyouknow.utils.AppGlobal.Companion.isEmpty
import com.letyouknow.utils.AppGlobal.Companion.setNoData
import com.letyouknow.utils.AppGlobal.Companion.stringToDate
import com.letyouknow.utils.Constant
import com.letyouknow.utils.Constant.Companion.ARG_YEAR_MAKE_MODEL
import com.letyouknow.view.dashboard.MainActivity
import com.letyouknow.view.lyk.summary.LYKStep1Activity
import com.letyouknow.view.spinneradapter.*
import kotlinx.android.synthetic.main.dialog_mobile_no.*
import kotlinx.android.synthetic.main.dialog_vehicle_options.*
import kotlinx.android.synthetic.main.dialog_vehicle_packages.*
import kotlinx.android.synthetic.main.fragment_lyk.*
import org.jetbrains.anko.support.v4.startActivity
import java.lang.reflect.Type
import java.text.SimpleDateFormat
import java.util.*


class LYKFragment : BaseFragment(), View.OnClickListener,
    AdapterView.OnItemSelectedListener {

    private lateinit var promotionViewModel: PromotionViewModel
    private lateinit var vehicleYearModel: VehicleYearViewModel
    private lateinit var vehicleMakeModel: VehicleMakeViewModel
    private lateinit var vehicleModelModel: VehicleModelViewModel
    private lateinit var vehicleTrimModel: VehicleTrimViewModel
    private lateinit var exteriorColorModel: ExteriorColorViewModel
    private lateinit var interiorColorModel: InteriorColorViewModel
    private lateinit var zipCodeModel: VehicleZipCodeViewModel
    private lateinit var packagesModel: VehiclePackagesViewModel
    private lateinit var checkedPackageModel: CheckedPackageInventoryViewModel
    private lateinit var packagesOptional: VehicleOptionalViewModel
    private lateinit var checkedAccessoriesModel: CheckedAccessoriesInventoryViewModel
    private lateinit var minMSRPViewModel: MinMSRPViewModel
    private lateinit var socialMobileViewModel: SocialMobileViewModel

    private lateinit var adapterYear: YearSpinnerAdapter
    private lateinit var adapterMake: MakeSpinnerAdapter
    private lateinit var adapterModel: ModelSpinnerAdapter
    private lateinit var adapterTrim: TrimsSpinnerAdapter
    private lateinit var adapterExterior: ExteriorSpinnerAdapter
    private lateinit var adapterInterior: InteriorSpinnerAdapter
    private lateinit var adapterPackages: PackagesAdapter
    private lateinit var adapterOptions: OptionsAdapter


    private var productId = "1"
    private var yearId = ""
    private var makeId = ""
    private var modelId = ""
    private var trimId = ""
    private var extColorId = ""
    private var intColorId = ""

    private var yearStr = "YEAR - NEW CARS"
    private var makeStr = "MAKE"
    private var modelStr = "MODEL"
    private var trimStr = "TRIM"
    private var extColorStr = "EXTERIOR COLOR"
    private var intColorStr = "INTERIOR COLOR"

    private lateinit var animBlink: Animation
    private lateinit var animSlideRightToLeft: Animation
    private lateinit var animSlideLeftToRight: Animation


    private lateinit var binding: FragmentLykBinding
    private var upDownData = UpDownData()

    private lateinit var prefSubmitPriceData: PrefSubmitPriceData
    private lateinit var handler: Handler

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_lyk,
            container,
            false
        )

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init()
    }

    override fun onResume() {
        if (Constant.isInitProgress() && Constant.progress.isShowing)
            Constant.dismissLoader()
        startHandler()
        super.onResume()
    }

    private fun startHandler() {
        if (!isEmpty(pref?.getSubmitPriceTime())) {
//            Log.e("DAte Time", stringToDate(pref?.getSubmitPriceTime())?.toString()!!)
            handler = Handler()
            handler.postDelayed(runnable, 1000)
        } else {
            if (tvYear.visibility == View.GONE) {
                yearStr = "YEAR - NEW CARS"
                makeStr = "MAKE"
                modelStr = "MODEL"
                trimStr = "TRIM"
                extColorStr = "EXTERIOR COLOR"
                intColorStr = "INTERIOR COLOR"
                tvYear.visibility = View.VISIBLE
                spYear.visibility = View.GONE
            }
        }

    }

    private var runnable = object : Runnable {
        override fun run() {
            try {
                val date = Calendar.getInstance().time
                val lastDate = stringToDate(pref?.getSubmitPriceTime())

                val diff: Long = date.time - (lastDate?.time ?: 0)
                print(diff)

                val seconds = diff / 1000
                val minutes = seconds / 60
                if (minutes >= 30) {
                    handler.removeCallbacks(this)
                    pref?.setSubmitPriceData(Gson().toJson(PrefSubmitPriceData()))
                    pref?.setSubmitPriceTime("")
                    setTimerPrefData()
                    if (tvYear.visibility == View.GONE) {
                        tvYear.visibility = View.VISIBLE
                        spYear.visibility = View.GONE
                    }
                } else {
                    handler.postDelayed(this, 1000)
                }
            } catch (e: Exception) {
            }
        }
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
            packagesModel = ViewModelProvider(this)[VehiclePackagesViewModel::class.java]
            packagesOptional = ViewModelProvider(this)[VehicleOptionalViewModel::class.java]
            checkedPackageModel =
                ViewModelProvider(this)[CheckedPackageInventoryViewModel::class.java]
            checkedAccessoriesModel =
                ViewModelProvider(this)[CheckedAccessoriesInventoryViewModel::class.java]
            minMSRPViewModel =
                ViewModelProvider(this)[MinMSRPViewModel::class.java]
            socialMobileViewModel = ViewModelProvider(this)[SocialMobileViewModel::class.java]
            setTimerPrefData()

            tvYear.setOnClickListener(this)
            btnSearch.setOnClickListener(this)
            MainActivity.getInstance().setVisibleEditImg(false)
            MainActivity.getInstance().setVisibleLogoutImg(false)
            ivClosePromo.setOnClickListener(this)
            tvPromo.setOnClickListener(this)
            callPromotionAPI()
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
            AppGlobal.setSpinnerLayoutPos(0, spYear, requireActivity())
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
            AppGlobal.setSpinnerLayoutPos(0, spMake, requireActivity())
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
            AppGlobal.setSpinnerLayoutPos(0, spModel, requireActivity())
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
            AppGlobal.setSpinnerLayoutPos(0, spTrim, requireActivity())
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
            AppGlobal.setSpinnerLayoutPos(0, spExteriorColor, requireActivity())
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
            AppGlobal.setSpinnerLayoutPos(0, spInteriorColor, requireActivity())
        } catch (e: Exception) {

        }
    }

    private fun setPackages(isEnable: Boolean) {
        try {
            tvPackages.text = "PACKAGES"
            if (isEnable) {
                tvPackages.isEnabled = true
                tvPackages.setOnClickListener(this)
            } else {
                tvPackages.isEnabled = false
                tvPackages.setOnClickListener(null)
            }
        } catch (e: Exception) {

        }
    }

    private fun setOptions(isEnable: Boolean) {
        try {
            tvOptionalAccessories.text = "OPTIONS & ACCESSORIES"
            if (isEnable) {
                tvOptionalAccessories.isEnabled = true
                tvOptionalAccessories.setOnClickListener(this)
            } else {
                tvOptionalAccessories.isEnabled = false
                tvOptionalAccessories.setOnClickListener(null)
            }
        } catch (e: Exception) {

        }
    }

    var isCallingYear = false

    private fun callVehicleYearAPI() {
        try {
            isCallingYear = true
            if (Constant.progress.isShowing)
                Constant.dismissLoader()
            if (Constant.isOnline(requireActivity())) {
                if (!Constant.isInitProgress()) {
                    Constant.showLoader(requireActivity())
                } else if (Constant.isInitProgress() && !Constant.progress.isShowing) {
                    Constant.showLoader(requireActivity())
                }
                vehicleYearModel.getYear(
                    requireActivity(),
                    productId,
                    ""
                )!!
                    .observe(requireActivity(), Observer { data ->
                        if (isEmpty(prefSubmitPriceData.makeId!!))
                            Constant.dismissLoader()
                        isCallingYear = false
                        try {
                            Log.e("Year Data", Gson().toJson(data))
                            if (!data.isNullOrEmpty()) {
                                val yearData = VehicleYearData()
                                yearData.year = "YEAR - NEW CARS"
                                data.add(0, yearData)
                                adapterYear = YearSpinnerAdapter(requireActivity(), data)
                                spYear.adapter = adapterYear
                                for (i in 0 until data.size) {
                                    if (!isEmpty(prefSubmitPriceData.yearId) && prefSubmitPriceData.yearId == data[i].vehicleYearID) {
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
        spMake.isEnabled = true
        if (Constant.isOnline(requireActivity())) {
//            if (isEmpty(prefSubmitPriceData.makeId!!)) {
            if (!Constant.isInitProgress()) {
                Constant.showLoader(requireActivity())
            } else if (Constant.isInitProgress() && !Constant.progress.isShowing) {
                Constant.showLoader(requireActivity())
            }
//            }
            vehicleMakeModel.getMake(
                requireActivity(),
                productId,
                prefSubmitPriceData.yearId,
                ""
            )!!
                .observe(requireActivity(), Observer { data ->
                    if (isEmpty(prefSubmitPriceData.modelId!!))
                        Constant.dismissLoader()
                    //   Log.e("Make Data", Gson().toJson(data))
                    try {
                        if (!data.isNullOrEmpty()) {
                            val makeData = VehicleMakeData()
                            makeData.make = "MAKE"
                            data.add(0, makeData)
                            adapterMake = MakeSpinnerAdapter(requireActivity(), data)
                            spMake.adapter = adapterMake

                            for (i in 0 until data.size) {
                                if (!isEmpty(prefSubmitPriceData.makeId) && prefSubmitPriceData.makeId == data[i].vehicleMakeID) {
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
    }

    private fun callVehicleModelAPI() {
        spModel.isEnabled = true
        if (Constant.isOnline(requireActivity())) {
//            if (isEmpty(prefSubmitPriceData.modelId!!)) {
            if (!Constant.isInitProgress()) {
                Constant.showLoader(requireActivity())
            } else if (Constant.isInitProgress() && !Constant.progress.isShowing) {
                Constant.showLoader(requireActivity())
            }
//            }
            vehicleModelModel.getModel(
                requireActivity(),
                productId,
                prefSubmitPriceData.yearId,
                prefSubmitPriceData.makeId,
                ""
            )!!
                .observe(requireActivity(), Observer { data ->
                    if (isEmpty(prefSubmitPriceData.trimId!!))
                        Constant.dismissLoader()
                    //  Log.e("MODEL Data", Gson().toJson(data))
                    try {
                        if (!data.isNullOrEmpty()) {
                            val modelData = VehicleModelData()
                            modelData.model = "MODEL"
                            data.add(0, modelData)
                            adapterModel = ModelSpinnerAdapter(requireActivity(), data)
                            spModel.adapter = adapterModel
                            for (i in 0 until data.size) {
                                if (!isEmpty(prefSubmitPriceData.modelId) && prefSubmitPriceData.modelId == data[i].vehicleModelID) {
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
    }

    private fun callVehicleTrimAPI() {
        spTrim.isEnabled = true
        if (Constant.isOnline(requireActivity())) {
//            if (isEmpty(prefSubmitPriceData.trimId!!)) {
            if (!Constant.isInitProgress()) {
                Constant.showLoader(requireActivity())
            } else if (Constant.isInitProgress() && !Constant.progress.isShowing) {
                Constant.showLoader(requireActivity())
            }
//            }
            vehicleTrimModel.getTrim(
                requireActivity(),
                productId,
                prefSubmitPriceData.yearId,
                prefSubmitPriceData.makeId,
                prefSubmitPriceData.modelId,
                ""
            )!!
                .observe(requireActivity(), Observer { data ->
                    if (isEmpty(prefSubmitPriceData.extColorId!!))
                        Constant.dismissLoader()
                    // Log.e("TRIM Data", Gson().toJson(data))
                    try {
                        if (!data.isNullOrEmpty()) {
                            val trimData = VehicleTrimData()
                            trimData.trim = "TRIM"
                            data.add(0, trimData)
                            adapterTrim = TrimsSpinnerAdapter(requireActivity(), data)
                            spTrim.adapter = adapterTrim
                            for (i in 0 until data.size) {
                                if (!isEmpty(prefSubmitPriceData.trimId) && prefSubmitPriceData.trimId == data[i].vehicleTrimID) {
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
    }

    private fun callExteriorColorAPI() {
        spExteriorColor.isEnabled = true
        if (Constant.isOnline(requireActivity())) {
//            if (isEmpty(prefSubmitPriceData.extColorId!!)) {
            if (!Constant.isInitProgress()) {
                Constant.showLoader(requireActivity())
            } else if (Constant.isInitProgress() && !Constant.progress.isShowing) {
                Constant.showLoader(requireActivity())
            }
//            }
            exteriorColorModel.getExteriorColor(
                requireActivity(),
                productId,
                prefSubmitPriceData.yearId,
                prefSubmitPriceData.makeId,
                prefSubmitPriceData.modelId,
                prefSubmitPriceData.trimId,
                ""
            )!!
                .observe(requireActivity(), Observer { data ->
                    if (isEmpty(prefSubmitPriceData.intColorId!!))
                        Constant.dismissLoader()
                    //  Log.e("EXTERIOR COLOR Data", Gson().toJson(data))
                    try {
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
                                if (!isEmpty(prefSubmitPriceData.extColorId) && prefSubmitPriceData.extColorId == data[i].vehicleExteriorColorID) {
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
    }

    private fun callInteriorColorAPI() {
        spInteriorColor.isEnabled = true
        if (Constant.isOnline(requireActivity())) {
//            if (isEmpty(prefSubmitPriceData.intColorId!!)) {
            if (!Constant.isInitProgress()) {
                Constant.showLoader(requireActivity())
            } else if (Constant.isInitProgress() && !Constant.progress.isShowing) {
                Constant.showLoader(requireActivity())
            }
//            }
            interiorColorModel.getInteriorColor(
                requireActivity(),
                productId,
                prefSubmitPriceData.yearId,
                prefSubmitPriceData.makeId,
                prefSubmitPriceData.modelId,
                prefSubmitPriceData.trimId,
                prefSubmitPriceData.extColorId,
                ""
            )!!
                .observe(requireActivity(), Observer { data ->
                    Constant.dismissLoader()
                    //  Log.e("INTERIOR Data", Gson().toJson(data))
                    try {
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
                                if (!isEmpty(prefSubmitPriceData.intColorId) && prefSubmitPriceData.intColorId == data[i].vehicleInteriorColorID) {
                                    spInteriorColor.setSelection(i, true)
                                    AppGlobal.setSpinnerLayoutPos(
                                        i,
                                        spInteriorColor,
                                        requireActivity()
                                    )
                                    if (!prefSubmitPriceData.packagesData.isNullOrEmpty()) {
                                        popupPackages(prefSubmitPriceData.packagesData!!)
                                        setPackages(true)
                                        var packagesStr = ""
                                        var isFirst = true
                                        for (i in 0 until adapterPackages.itemCount) {
                                            if (adapterPackages.getItem(i).isSelect == true || adapterPackages.getItem(
                                                    i
                                                ).isOtherSelect == true
                                            ) {
                                                if (isFirst) {
                                                    packagesStr =
                                                        adapterPackages.getItem(i).packageName.toString()
                                                    isFirst = false
                                                } else {
                                                    packagesStr =
                                                        packagesStr + ", " + adapterPackages.getItem(
                                                            i
                                                        ).packageName.toString()
                                                }
                                            }
                                        }
                                        if (packagesStr.length > 0) {
                                            tvPackages.text = packagesStr
                                            tvErrorPackages.visibility = View.GONE
//                                            setOptions(true)
//                                            callOptionalAccessoriesAPI()
                                        } else {
                                            tvPackages.text = "PACKAGES"
                                            setOptions(false)
                                        }

                                        if (!prefSubmitPriceData.optionsData.isNullOrEmpty()) {
                                            popupOptions(prefSubmitPriceData.optionsData!!)
                                            setOptions(true)
                                            var optionsStr = ""
                                            var isFirst = true
                                            for (i in 0 until adapterOptions.itemCount) {
                                                if (adapterOptions.getItem(i).isSelect == true || adapterOptions.getItem(
                                                        i
                                                    ).isOtherSelect == true
                                                ) {
                                                    if (isFirst) {
                                                        optionsStr =
                                                            adapterOptions.getItem(i).accessory.toString()
                                                        isFirst = false
                                                    } else {
                                                        optionsStr =
                                                            optionsStr + ", " + adapterOptions.getItem(
                                                                i
                                                            ).accessory.toString()
                                                    }
                                                }
                                            }
                                            if (!TextUtils.isEmpty(optionsStr)) {
                                                tvErrorOptionsAccessories.visibility = View.GONE
                                                tvOptionalAccessories.text = optionsStr
                                            } else {
                                                tvOptionalAccessories.text = "OPTIONS & ACCESSORIES"
                                            }

                                        } else {
                                            callOptionalAccessoriesAPI()
                                            setOptions(true)
                                        }

                                    } else {
                                        callVehiclePackagesAPI()
                                        setPackages(true)
                                    }

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
    }

    private fun callVehiclePackagesAPI() {
        if (Constant.isOnline(requireActivity())) {
            if (!Constant.isInitProgress()) {
                Constant.showLoader(requireActivity())
            } else if (Constant.isInitProgress() && !Constant.progress.isShowing) {
                Constant.showLoader(requireActivity())
            }
            packagesModel.getPackages(
                requireActivity(),
                productId,
                prefSubmitPriceData.yearId,
                prefSubmitPriceData.makeId,
                prefSubmitPriceData.modelId,
                prefSubmitPriceData.trimId,
                prefSubmitPriceData.extColorId,
                prefSubmitPriceData.intColorId, ""
            )!!
                .observe(requireActivity(), Observer { data ->
                    Constant.dismissLoader()
                    // Log.e("Packages Data", Gson().toJson(data))
                    try {
                        if (data != null || data?.size!! > 0) {
                            val packagesData = VehiclePackagesData()
                            packagesData.vehiclePackageID = "0"
                            packagesData.packageName = "ANY"
                            data.add(0, packagesData)
                            popupPackages(data)

                        } else {
                            val arData = ArrayList<VehiclePackagesData>()
                            val packagesData = VehiclePackagesData()
                            packagesData.vehiclePackageID = "0"
                            packagesData.packageName = "ANY"
                            arData.add(0, packagesData)
                            popupPackages(arData)
                        }
                    } catch (e: Exception) {
                    }
                }
                )
        } else {
            Toast.makeText(requireActivity(), Constant.noInternet, Toast.LENGTH_SHORT).show()
        }
    }

    private fun callOptionalAccessoriesAPI() {
        if (Constant.isOnline(requireActivity())) {
            if (!Constant.isInitProgress()) {
                Constant.showLoader(requireActivity())
            } else if (Constant.isInitProgress() && !Constant.progress.isShowing) {
                Constant.showLoader(requireActivity())
            }
            val jsonArray = JsonArray()
            for (i in 0 until adapterPackages.itemCount) {
                if (adapterPackages.getItem(i).isSelect!! || adapterPackages.getItem(i).isOtherSelect!!) {
                    jsonArray.add(adapterPackages.getItem(i).vehiclePackageID)
                }
            }
            val request = HashMap<String, Any>()
            request[ApiConstant.packageList] = jsonArray
            request[ApiConstant.productId] = productId
            request[ApiConstant.yearId] = prefSubmitPriceData.yearId!!
            request[ApiConstant.makeId] = prefSubmitPriceData.makeId!!
            request[ApiConstant.modelId] = prefSubmitPriceData.modelId!!
            request[ApiConstant.trimId] = prefSubmitPriceData.trimId!!
            request[ApiConstant.exteriorColorId] = prefSubmitPriceData.extColorId!!
            request[ApiConstant.interiorColorId] = prefSubmitPriceData.intColorId!!
            request[ApiConstant.zipCode] = ""

            packagesOptional.getOptional(
                requireActivity(),
                request
            )!!
                .observe(requireActivity(), Observer { data ->
                    Constant.dismissLoader()
                    //   Log.e("Options Data", Gson().toJson(data))
                    try {
                        if (data != null || data?.size!! > 0) {
                            val accessoriesData = VehicleAccessoriesData()
                            accessoriesData.dealerAccessoryID = "0"
                            accessoriesData.accessory = "ANY"
                            data.add(0, accessoriesData)
                            popupOptions(data)
                        } else {
                            val arData = ArrayList<VehicleAccessoriesData>()
                            val accessoriesData = VehicleAccessoriesData()
                            accessoriesData.dealerAccessoryID = "0"
                            accessoriesData.accessory = "ANY"
                            arData.add(0, accessoriesData)
                            popupOptions(arData)
                        }
                    } catch (e: Exception) {
                    }
                }
                )
        } else {
            Toast.makeText(requireActivity(), Constant.noInternet, Toast.LENGTH_SHORT).show()
        }
    }

    var hasMatchPackage = false
    private fun callCheckedPackageAPI() {
        if (Constant.isOnline(requireActivity())) {
            if (!Constant.isInitProgress()) {
                Constant.showLoader(requireActivity())
            } else if (Constant.isInitProgress() && !Constant.progress.isShowing) {
                Constant.showLoader(requireActivity())
            }
            val jsonArray = JsonArray()
            for (i in 0 until adapterPackages.itemCount) {
                if (adapterPackages.getItem(i).isSelect!! || adapterPackages.getItem(i).isOtherSelect!!) {
                    jsonArray.add(adapterPackages.getItem(i).vehiclePackageID)
                }
            }
            val request = HashMap<String, Any>()
            request[ApiConstant.checkedList] = jsonArray
            request[ApiConstant.productId] = productId
            request[ApiConstant.yearId] = prefSubmitPriceData.yearId!!
            request[ApiConstant.makeId] = prefSubmitPriceData.makeId!!
            request[ApiConstant.modelId] = prefSubmitPriceData.modelId!!
            request[ApiConstant.trimId] = prefSubmitPriceData.trimId!!
            request[ApiConstant.exteriorColorId] = prefSubmitPriceData.extColorId!!
            request[ApiConstant.interiorColorId] = prefSubmitPriceData.intColorId!!
            request[ApiConstant.zipCode] = ""

            checkedPackageModel.checkedPackage(requireActivity(), request)!!
                .observe(this, Observer { data ->
                    hasMatchPackage = data.hasMatch!!
                    Constant.dismissLoader()
                    if (data.status == 1) {
                        if (::dialogPackage.isInitialized)
                            dialogPackage.dismiss()
                        alertError(requireActivity(), getString(R.string.market_hot_msg))
                        setPackages(true)
                        prefSubmitPriceData = pref?.getSubmitPriceData()!!
                        prefSubmitPriceData.packagesData = ArrayList()
                        pref?.setSubmitPriceData(Gson().toJson(prefSubmitPriceData))
                        setCurrentTime()
                    } else {
                        if (data.status == 0 || data.status == 3) {
                            showHighlightedDialog()
                        }
                        if (!data.autoCheckList.isNullOrEmpty()) {
                            for (i in 0 until data.autoCheckList.size) {
                                for (j in 0 until adapterPackages.itemCount) {
                                    if (adapterPackages.getItem(j).vehiclePackageID == data.autoCheckList[i]) {
                                        val dataCheck = adapterPackages.getItem(j)
                                        dataCheck.isGray = false
                                        dataCheck.isOtherSelect = true
                                        adapterPackages.update(j, dataCheck)
                                    }
                                }
                            }
                        }
                        if (!data.grayOutList.isNullOrEmpty()) {
                            for (i in 0 until data.grayOutList.size) {
                                for (j in 0 until adapterPackages.itemCount) {
                                    if (adapterPackages.getItem(j).vehiclePackageID == data.grayOutList[i]) {
                                        val dataGray = adapterPackages.getItem(j)
                                        dataGray.isGray = true
                                        adapterPackages.update(j, dataGray)
                                    }
                                }
                            }
                        }
                    }
                }
                )

        } else {
            Toast.makeText(requireActivity(), Constant.noInternet, Toast.LENGTH_SHORT).show()
        }
    }

    private var hasMatchOptions = false
    private fun callCheckedAccessoriesAPI() {
        if (Constant.isOnline(requireActivity())) {
            if (!Constant.isInitProgress()) {
                Constant.showLoader(requireActivity())
            } else if (Constant.isInitProgress() && !Constant.progress.isShowing) {
                Constant.showLoader(requireActivity())
            }
            val jsonArray = JsonArray()
            for (i in 0 until adapterOptions.itemCount) {
                if (adapterOptions.getItem(i).isSelect!! || adapterOptions.getItem(i).isOtherSelect!!) {
                    jsonArray.add(adapterOptions.getItem(i).dealerAccessoryID)
                }
            }
            val jsonArrayPackage = JsonArray()
            for (i in 0 until adapterPackages.itemCount) {
                if (adapterPackages.getItem(i).isSelect!! || adapterPackages.getItem(i).isOtherSelect!!) {
                    jsonArrayPackage.add(adapterPackages.getItem(i).vehiclePackageID)
                }
            }
            val request = HashMap<String, Any>()
            request[ApiConstant.packageList] = jsonArrayPackage
            request[ApiConstant.checkedList] = jsonArray
            request[ApiConstant.productId] = productId
            request[ApiConstant.yearId] = prefSubmitPriceData.yearId!!
            request[ApiConstant.makeId] = prefSubmitPriceData.makeId!!
            request[ApiConstant.modelId] = prefSubmitPriceData.modelId!!
            request[ApiConstant.trimId] = prefSubmitPriceData.trimId!!
            request[ApiConstant.exteriorColorId] = prefSubmitPriceData.extColorId!!
            request[ApiConstant.interiorColorId] = prefSubmitPriceData.intColorId!!
            request[ApiConstant.zipCode] = ""

            checkedAccessoriesModel.checkedAccessories(requireActivity(), request)!!
                .observe(this, Observer { data ->
                    Constant.dismissLoader()
                    hasMatchOptions = data.hasMatch!!
                    if (data.status == 1) {
                        if (::dialogOptions.isInitialized)
                            dialogOptions.dismiss()
                        alertError(requireActivity(), getString(R.string.market_hot_msg))
                        setOptions(true)
                        prefSubmitPriceData = pref?.getSubmitPriceData()!!
                        prefSubmitPriceData.optionsData = ArrayList()
                        pref?.setSubmitPriceData(Gson().toJson(prefSubmitPriceData))
                        setCurrentTime()
                    } else {
                        if (data.status == 0 || data.status == 3) {
                            showHighlightedDialog()
                        }
                        if (!data.autoCheckList.isNullOrEmpty()) {
                            for (i in 0 until data.autoCheckList.size) {
                                for (j in 0 until adapterOptions.itemCount) {
                                    if (adapterOptions.getItem(j).dealerAccessoryID == data.autoCheckList[i]) {
                                        val dataCheck = adapterOptions.getItem(j)
                                        dataCheck.isGray = false
                                        dataCheck.isOtherSelect = true
                                        adapterOptions.update(j, dataCheck)
                                    }
                                }
                            }
                        }
                        if (!data.grayOutList.isNullOrEmpty()) {
                            for (i in 0 until data.grayOutList.size) {
                                for (j in 0 until adapterOptions.itemCount) {
                                    if (adapterOptions.getItem(j).dealerAccessoryID == data.grayOutList[i]) {
                                        val dataGray = adapterOptions.getItem(j)
                                        dataGray.isGray = true
                                        adapterOptions.update(j, dataGray)
                                    }
                                }
                            }
                        }
                    }
                }
                )

        } else {
            Toast.makeText(requireActivity(), Constant.noInternet, Toast.LENGTH_SHORT).show()
        }
    }

    private fun callMinMSRPAPI() {
        if (Constant.isOnline(requireActivity())) {
            Constant.showLoader(requireActivity())
            val jsonArray = JsonArray()
            for (i in 0 until adapterOptions.itemCount) {
                if (adapterOptions.getItem(i).isSelect!! || adapterOptions.getItem(i).isOtherSelect!!) {
                    jsonArray.add(adapterOptions.getItem(i).dealerAccessoryID)
                }
            }
            val jsonArrayPackage = JsonArray()

            for (i in 0 until adapterPackages.itemCount) {
                if (adapterPackages.getItem(i).isSelect!! || adapterPackages.getItem(i).isOtherSelect!!) {
                    jsonArrayPackage.add(adapterPackages.getItem(i).vehiclePackageID)
                }
            }
            val request = HashMap<String, Any>()
            request[ApiConstant.packageList] = jsonArrayPackage
            request[ApiConstant.checkedList] = jsonArray
            request[ApiConstant.yearId] = yearId
            request[ApiConstant.makeId] = makeId
            request[ApiConstant.modelId] = modelId
            request[ApiConstant.trimId] = trimId
            request[ApiConstant.exteriorColorId] = extColorId
            request[ApiConstant.interiorColorId] = intColorId

            //  Log.e("RequestMin", Gson().toJson(request))

            minMSRPViewModel.minMSRPCall(requireActivity(), request)!!
                .observe(this, Observer { dataMSRP ->
                    Constant.dismissLoader()
                    val arOptions: ArrayList<VehicleAccessoriesData> = ArrayList()
                    for (i in 0 until adapterOptions.itemCount) {
                        if (adapterOptions.getItem(i).isSelect!! || adapterOptions.getItem(i).isOtherSelect!!) {
                            arOptions.add(adapterOptions.getItem(i))
                        }
                    }
                    val arPackage: ArrayList<VehiclePackagesData> = ArrayList()

                    for (i in 0 until adapterPackages.itemCount) {
                        if (adapterPackages.getItem(i).isSelect!! || adapterPackages.getItem(i).isOtherSelect!!) {
                            arPackage.add(adapterPackages.getItem(i))
                        }
                    }
                    val data = YearModelMakeData()
                    data.vehicleYearID = yearId
                    data.vehicleMakeID = makeId
                    data.vehicleModelID = modelId
                    data.vehicleTrimID = trimId
                    data.vehicleExtColorID = extColorId
                    data.vehicleIntColorID = intColorId
                    data.vehicleYearStr = yearStr
                    data.vehicleMakeStr = makeStr
                    data.vehicleModelStr = modelStr
                    data.vehicleTrimStr = trimStr
                    data.vehicleExtColorStr = extColorStr
                    data.vehicleIntColorStr = intColorStr
                    data.arPackages = arPackage
                    data.arOptions = arOptions
                    data.msrp = dataMSRP.toFloat()
                    pref?.setSubmitPriceData(Gson().toJson(pref?.getSubmitPriceData()))
                    startActivity<LYKStep1Activity>(
                        ARG_YEAR_MAKE_MODEL to Gson().toJson(
                            data
                        )
                    )
                }
                )
        } else {
            Toast.makeText(requireActivity(), Constant.noInternet, Toast.LENGTH_SHORT).show()
        }
    }

    private var arSelectPackage: ArrayList<VehiclePackagesData> = ArrayList()
    private var selectPackageStr = ""

    private var arSelectOption: ArrayList<VehicleAccessoriesData> = ArrayList()
    private var selectOptionStr = ""


    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.tvYear -> {
                tvYear.visibility = View.GONE
                spYear.visibility = View.VISIBLE
                callVehicleYearAPI()
                spYear.performClick()
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
                    llPromoOffer.visibility = View.GONE
                    tvPromo.startAnimation(animBlink)
                    tvPromo.visibility = View.VISIBLE
                }, 400)
            }
            R.id.btnSearch -> {
                if (isValid()) {
                    if (pref?.getUserData()?.isSocial!!) {
                        if (!pref?.isUpdateSocialMobile()!!) {
                            dialogPhoneNo(true)
                        } else {
                            callMinMSRPAPI()
                        }
                    } else {
                        callMinMSRPAPI()
                    }
                }
            }
            R.id.tvPackages -> {
                if (::adapterPackages.isInitialized) {
                    arSelectPackage = ArrayList()
                    for (i in 0 until adapterPackages.itemCount) {
                        arSelectPackage.add(adapterPackages.getItem(i))
                    }
                    selectPackageStr = Gson().toJson(arSelectPackage)
                    dialogPackage.show()
                } else {
                    callVehiclePackagesAPI()
                    try {
                        arSelectPackage = ArrayList()
                        for (i in 0 until adapterPackages.itemCount) {
                            arSelectPackage.add(adapterPackages.getItem(i))
                        }
                        selectPackageStr = Gson().toJson(arSelectPackage)
                        dialogPackage.show()
                    } catch (e: Exception) {

                    }
                }
            }
            R.id.tvApplyPackage -> {
                var packagesStr = ""
                var isFirst = true
                for (i in 0 until adapterPackages.itemCount) {
                    if (adapterPackages.getItem(i).isSelect == true || adapterPackages.getItem(i).isOtherSelect == true) {
                        if (isFirst) {
                            packagesStr = adapterPackages.getItem(i).packageName.toString()
                            isFirst = false
                        } else {
                            packagesStr =
                                packagesStr + ", " + adapterPackages.getItem(i).packageName.toString()
                        }
                    }
                }
                if (packagesStr.length > 0) {
                    val ignoreInventory = adapterPackages.getItem(0).isSelect == true
                    if (!hasMatchPackage && !ignoreInventory) {
                        showOtherInventoryEmptyDialog()
                    } else {
                        tvPackages.text = packagesStr
                        tvErrorPackages.visibility = View.GONE
                        setOptions(true)
                        callOptionalAccessoriesAPI()
                        dialogPackage.dismiss()
                    }
                } else {

                    showApplyEmptyDialog()
                }
                prefSubmitPriceData = pref?.getSubmitPriceData()!!
                prefSubmitPriceData.packagesData = adapterPackages.getAll()
                Constant.dismissLoader()
                prefSubmitPriceData.optionsData = ArrayList()
                pref?.setSubmitPriceData(Gson().toJson(prefSubmitPriceData))
                setCurrentTime()
                setRadius()

            }
            R.id.tvCancelPackage -> {
                val gson = Gson()
                val type: Type = object : TypeToken<ArrayList<VehiclePackagesData>?>() {}.type
                arSelectPackage = gson.fromJson(selectPackageStr, type)
                for (i in 0 until arSelectPackage.size) {
                    adapterPackages.update(i, arSelectPackage[i])
                }
                dialogPackage.dismiss()
            }
            R.id.llPackages -> {
                //  Log.e("select1", selectPackageStr)
                val pos = v.tag as Int

                val data = adapterPackages.getItem(pos)
                data.isSelect = !data.isSelect!!
                adapterPackages.update(pos, data)
                if (data.isSelect!! && pos == 0) {
                    for (i in 1 until adapterPackages.itemCount) {
                        val dataPackage = adapterPackages.getItem(i)
                        dataPackage.isSelect = false
                        dataPackage.isOtherSelect = false
                        dataPackage.isGray = false
                        adapterPackages.update(i, dataPackage)
                    }
                } else {
                    for (i in 1 until adapterPackages.itemCount) {
                        val dataPackage = adapterPackages.getItem(i)
//                        dataPackage.isOtherSelect = false
//                        dataPackage.isGray = false
                        adapterPackages.update(i, dataPackage)
                    }
                    val data0 = adapterPackages.getItem(0)
                    data0.isSelect = false
                    adapterPackages.update(0, data0)
                    if (!data0.isGray!!)
                        callCheckedPackageAPI()
                }

                //Log.e("clickupdate", selectPackageStr)

            }
            R.id.tvResetPackage -> {
                for (i in 0 until adapterPackages.itemCount) {
                    val data = adapterPackages.getItem(i)
                    data.isSelect = false
                    data.isGray = false
                    data.isOtherSelect = false
                    adapterPackages.update(i, data)
                }
            }
            R.id.tvApplyOption -> {
                var optionsStr = ""
                var isFirst = true
                for (i in 0 until adapterOptions.itemCount) {
                    if (adapterOptions.getItem(i).isSelect == true || adapterOptions.getItem(i).isOtherSelect == true) {
                        if (isFirst) {
                            optionsStr = adapterOptions.getItem(i).accessory.toString()
                            isFirst = false
                        } else {
                            optionsStr =
                                optionsStr + ", " + adapterOptions.getItem(i).accessory.toString()
                        }
                    }
                }
                if (!TextUtils.isEmpty(optionsStr)) {
                    val ignoreInventory = adapterOptions.getItem(0).isSelect == true
                    if (!hasMatchOptions && !ignoreInventory) {
                        showOtherInventoryEmptyDialog()
                    } else {
                        tvErrorOptionsAccessories.visibility = View.GONE
                        tvOptionalAccessories.text = optionsStr
                        dialogOptions.dismiss()
                    }
                } else {
                    showApplyEmptyDialog()
//                    tvOptionalAccessories.text = "OPTIONS & ACCESSORIES"
                }
                prefSubmitPriceData = pref?.getSubmitPriceData()!!
                prefSubmitPriceData.optionsData = adapterOptions.getAll()
                pref?.setSubmitPriceData(Gson().toJson(prefSubmitPriceData))
                setCurrentTime()
                setRadius()
            }
            R.id.tvResetOption -> {
                for (i in 0 until adapterOptions.itemCount) {
                    val data = adapterOptions.getItem(i)
                    data.isSelect = false
                    data.isGray = false
                    data.isOtherSelect = false
                    adapterOptions.update(i, data)
                }
            }
            R.id.tvCancelOption -> {
                val gson = Gson()
                val type: Type = object : TypeToken<ArrayList<VehicleAccessoriesData>?>() {}.type
                arSelectOption = gson.fromJson(selectOptionStr, type)
                for (i in 0 until arSelectOption.size) {
                    adapterOptions.update(i, arSelectOption[i])
                }
                dialogOptions.dismiss()
            }
            R.id.tvOptionalAccessories -> {
                if (::adapterOptions.isInitialized) {
                    try {
                        arSelectOption = ArrayList()
                        for (i in 0 until adapterOptions.itemCount) {
                            arSelectOption.add(adapterOptions.getItem(i))
                        }
                        selectOptionStr = Gson().toJson(arSelectOption)
                        dialogOptions.show()
                    } catch (e: Exception) {

                    }
                } else {
                    callOptionalAccessoriesAPI()
                    try {
                        arSelectOption = ArrayList()
                        for (i in 0 until adapterOptions.itemCount) {
                            arSelectOption.add(adapterOptions.getItem(i))
                        }
                        selectOptionStr = Gson().toJson(arSelectOption)
                        dialogOptions.show()
                    } catch (e: Exception) {

                    }
                }
            }
            R.id.llOptions -> {
                // Log.e("select1", selectOptionStr)
                val pos = v.tag as Int
                val data = adapterOptions.getItem(pos)
                data.isSelect = !data.isSelect!!
                adapterOptions.update(pos, data)
                if (data.isSelect!! && pos == 0) {
                    for (i in 1 until adapterOptions.itemCount) {
                        val dataOptions = adapterOptions.getItem(i)
                        dataOptions.isSelect = false
                        dataOptions.isOtherSelect = false
                        dataOptions.isGray = false
                        adapterOptions.update(i, dataOptions)
                    }
                } else {
                    for (i in 1 until adapterOptions.itemCount) {
                        val dataOptions = adapterOptions.getItem(i)
//                        dataOptions.isOtherSelect = false
//                        dataOptions.isGray = false
                        adapterOptions.update(i, dataOptions)
                    }
                    val data0 = adapterOptions.getItem(0)
                    data0.isSelect = false
                    adapterOptions.update(0, data0)
                    if (!data0.isGray!!)
                        callCheckedAccessoriesAPI()
                }
                Log.e("clickupdate", selectPackageStr)
            }
        }
    }

    private fun setCurrentTime() {
        val df = SimpleDateFormat("yyyy MM d, HH:mm:ss a")
        val date = df.format(Calendar.getInstance().time)
        pref?.setSubmitPriceTime(date)
//        Log.e("Submit Date", date)
        startHandler()
    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        when (parent?.id) {
            R.id.spYear -> {
                val data = adapterYear.getItem(position) as VehicleYearData
                yearId = data.vehicleYearID!!
                yearStr = data.year!!

                AppGlobal.setSpinnerLayoutPos(position, spYear, requireActivity())
                if (data.year != "YEAR - NEW CARS") {
                    prefSubmitPriceData = pref?.getSubmitPriceData()!!
                    prefSubmitPriceData.yearId = data.vehicleYearID
                    prefSubmitPriceData.yearStr = data.year!!
                    prefSubmitPriceData.makeId = ""
                    prefSubmitPriceData.modelId = ""
                    prefSubmitPriceData.trimId = ""
                    prefSubmitPriceData.extColorId = ""
                    prefSubmitPriceData.intColorId = ""
                    prefSubmitPriceData.makeStr = ""
                    prefSubmitPriceData.modelStr = ""
                    prefSubmitPriceData.trimStr = ""
                    prefSubmitPriceData.extColorStr = ""
                    prefSubmitPriceData.intColorStr = ""
                    prefSubmitPriceData.packagesData = ArrayList()
                    prefSubmitPriceData.optionsData = ArrayList()
                    Constant.dismissLoader()
                    pref?.setSubmitPriceData(Gson().toJson(prefSubmitPriceData))
                    setCurrentTime()
                    setErrorVisibleGone()
                    callVehicleMakeAPI()
                    setModel()
                    setTrim()
                    setExteriorColor()
                    setInteriorColor()
                    setPackages(false)
                    setOptions(false)
                    setRadius()
                    setUCDPrefData()
                }
            }
            R.id.spMake -> {
                val data = adapterMake.getItem(position) as VehicleMakeData
                makeId = data.vehicleMakeID!!
                makeStr = data.make!!
                AppGlobal.setSpinnerLayoutPos(position, spMake, requireActivity())
                if (data.make != "MAKE") {
                    prefSubmitPriceData = pref?.getSubmitPriceData()!!
                    prefSubmitPriceData.makeId = data.vehicleMakeID!!
                    prefSubmitPriceData.makeStr = data.make!!
                    prefSubmitPriceData.modelId = ""
                    prefSubmitPriceData.trimId = ""
                    prefSubmitPriceData.extColorId = ""
                    prefSubmitPriceData.intColorId = ""
                    prefSubmitPriceData.modelStr = ""
                    prefSubmitPriceData.trimStr = ""
                    prefSubmitPriceData.extColorStr = ""
                    prefSubmitPriceData.intColorStr = ""
                    prefSubmitPriceData.packagesData = ArrayList()
                    prefSubmitPriceData.optionsData = ArrayList()
                    Constant.dismissLoader()
                    pref?.setSubmitPriceData(Gson().toJson(prefSubmitPriceData))
                    setCurrentTime()
                    setErrorVisibleGone()
                    callVehicleModelAPI()
                    setTrim()
                    setExteriorColor()
                    setInteriorColor()
                    setPackages(false)
                    setOptions(false)
                    setRadius()
                    setUCDPrefData()
                }
            }
            R.id.spModel -> {
                val data = adapterModel.getItem(position) as VehicleModelData
                modelId = data.vehicleModelID!!
                modelStr = data.model!!
                AppGlobal.setSpinnerLayoutPos(position, spModel, requireActivity())
                if (data.model != "MODEL") {
                    prefSubmitPriceData.modelId = data.vehicleModelID!!
                    prefSubmitPriceData.modelStr = data.model!!
                    prefSubmitPriceData.trimId = ""
                    prefSubmitPriceData.extColorId = ""
                    prefSubmitPriceData.intColorId = ""
                    prefSubmitPriceData.trimStr = ""
                    prefSubmitPriceData.extColorStr = ""
                    prefSubmitPriceData.intColorStr = ""
                    prefSubmitPriceData.packagesData = ArrayList()
                    prefSubmitPriceData.optionsData = ArrayList()
                    Constant.dismissLoader()
                    pref?.setSubmitPriceData(Gson().toJson(prefSubmitPriceData))
                    setCurrentTime()
                    setErrorVisibleGone()
                    callVehicleTrimAPI()
                    setExteriorColor()
                    setInteriorColor()
                    setPackages(false)
                    setOptions(false)
                    setRadius()
                    setUCDPrefData()
                }

            }
            R.id.spTrim -> {
                val data = adapterTrim.getItem(position) as VehicleTrimData
                trimId = data.vehicleTrimID!!
                trimStr = data.trim!!
                AppGlobal.setSpinnerLayoutPos(position, spTrim, requireActivity())
                if (data.trim != "TRIM") {
                    prefSubmitPriceData = pref?.getSubmitPriceData()!!
                    prefSubmitPriceData.trimId = data.vehicleTrimID!!
                    prefSubmitPriceData.trimStr = data.trim!!
                    prefSubmitPriceData.extColorId = ""
                    prefSubmitPriceData.intColorId = ""
                    prefSubmitPriceData.extColorStr = ""
                    prefSubmitPriceData.intColorStr = ""
                    prefSubmitPriceData.packagesData = ArrayList()
                    prefSubmitPriceData.optionsData = ArrayList()
                    Constant.dismissLoader()
                    pref?.setSubmitPriceData(Gson().toJson(prefSubmitPriceData))
                    setCurrentTime()
                    setErrorVisibleGone()
                    callExteriorColorAPI()
                    setInteriorColor()
                    setPackages(false)
                    setOptions(false)
                    setRadius()
                    setUCDPrefData()
                }

            }
            R.id.spExteriorColor -> {
                val data = adapterExterior.getItem(position) as ExteriorColorData
//                extColorId = "0"
                extColorId = data.vehicleExteriorColorID!!
                extColorStr = data.exteriorColor!!
                AppGlobal.setSpinnerLayoutPos(position, spExteriorColor, requireActivity())
                if (data.exteriorColor != "EXTERIOR COLOR") {
                    prefSubmitPriceData = pref?.getSubmitPriceData()!!
                    prefSubmitPriceData.extColorId = data.vehicleExteriorColorID!!
                    prefSubmitPriceData.extColorStr = data.exteriorColor!!
                    prefSubmitPriceData.intColorId = ""
                    prefSubmitPriceData.intColorStr = ""
                    prefSubmitPriceData.packagesData = ArrayList()
                    prefSubmitPriceData.optionsData = ArrayList()
                    Constant.dismissLoader()
                    pref?.setSubmitPriceData(Gson().toJson(prefSubmitPriceData))
                    setCurrentTime()
                    setErrorVisibleGone()
                    callInteriorColorAPI()
                    setPackages(false)
                    setOptions(false)
                    setRadius()
                    setUCDPrefData()
                }
            }
            R.id.spInteriorColor -> {
                val data = adapterInterior.getItem(position) as InteriorColorData
                intColorId = data.vehicleInteriorColorID!!
                intColorStr = data.interiorColor!!
                AppGlobal.setSpinnerLayoutPos(position, spInteriorColor, requireActivity())
                if (data.interiorColor != "INTERIOR COLOR") {
                    prefSubmitPriceData = pref?.getSubmitPriceData()!!
                    prefSubmitPriceData.intColorId = data.vehicleInteriorColorID!!
                    prefSubmitPriceData.intColorStr = data.interiorColor!!
                    prefSubmitPriceData.packagesData = ArrayList()
                    prefSubmitPriceData.optionsData = ArrayList()
                    Constant.dismissLoader()
                    pref?.setSubmitPriceData(Gson().toJson(prefSubmitPriceData))
                    setCurrentTime()
                    setErrorVisibleGone()
                    setPackages(true)
                    callVehiclePackagesAPI()
                    setOptions(false)
                    setRadius()
                    setUCDPrefData()
                }

            }
        }
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {

    }

    private lateinit var dialogPackage: Dialog
    private fun popupPackages(data: ArrayList<VehiclePackagesData>) {
        dialogPackage = Dialog(requireActivity(), R.style.FullScreenDialog)
        dialogPackage.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialogPackage.setCancelable(true)
        dialogPackage.setCanceledOnTouchOutside(true)
        dialogPackage.setContentView(R.layout.dialog_vehicle_packages)
        dialogPackage.run {
            adapterPackages =
                PackagesAdapter(R.layout.list_item_packages, this@LYKFragment)
            dialogPackage.rvPackages.adapter = adapterPackages
            adapterPackages.addAll(data)

            tvCancelPackage.setOnClickListener(this@LYKFragment)
            tvResetPackage.setOnClickListener(this@LYKFragment)
            tvApplyPackage.setOnClickListener(this@LYKFragment)
        }
        setLayoutParam(dialogPackage)
    }

    private lateinit var dialogOptions: Dialog
    private fun popupOptions(data: ArrayList<VehicleAccessoriesData>) {
        dialogOptions = Dialog(requireActivity(), R.style.FullScreenDialog)
        dialogOptions.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialogOptions.setCancelable(true)
        dialogOptions.setCanceledOnTouchOutside(true)
        dialogOptions.setContentView(R.layout.dialog_vehicle_options)
        dialogOptions.run {
            adapterOptions =
                OptionsAdapter(R.layout.list_item_options, this@LYKFragment)
            rvOptions.adapter = adapterOptions
            adapterOptions.addAll(data)

            tvCancelOption.setOnClickListener(this@LYKFragment)
            tvResetOption.setOnClickListener(this@LYKFragment)
            tvApplyOption.setOnClickListener(this@LYKFragment)
        }
        setLayoutParam(dialogOptions)
    }

    private fun setLayoutParam(dialog: Dialog) {
        val layoutParams: WindowManager.LayoutParams = dialog.window?.attributes!!
        dialog.window?.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.MATCH_PARENT
        )
        dialog.window?.attributes = layoutParams
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
            tvPackages.text.toString().trim() == "PACKAGES" -> {
                tvErrorPackages.visibility = View.VISIBLE
                return false
            }
            tvOptionalAccessories.text.toString().trim() == "OPTIONS & ACCESSORIES" -> {
                tvErrorOptionsAccessories.visibility = View.VISIBLE
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
        tvErrorPackages.visibility = View.GONE
        tvErrorOptionsAccessories.visibility = View.GONE
    }

    override fun onDestroy() {
        /* if (Constant.isInitProgress() && Constant.progress.isShowing)
             Constant.dismissLoader()*/
        super.onDestroy()
    }

    override fun onPause() {
        if (Constant.isInitProgress() && Constant.progress.isShowing)
            Constant.dismissLoader()
        super.onPause()
    }

    private fun setTimerPrefData() {
        prefSubmitPriceData = pref?.getSubmitPriceData()!!
        setYear()
        setMake()
        setModel()
        setTrim()
        setExteriorColor()
        setInteriorColor()
        setPackages(false)
        setOptions(false)
        yearId = prefSubmitPriceData.yearId!!
        makeId = prefSubmitPriceData.makeId!!
        modelId = prefSubmitPriceData.modelId!!
        trimId = prefSubmitPriceData.trimId!!
        extColorId = prefSubmitPriceData.extColorId!!
        intColorId = prefSubmitPriceData.intColorId!!
        yearStr = prefSubmitPriceData.yearStr!!
        makeStr = prefSubmitPriceData.makeStr!!
        modelStr = prefSubmitPriceData.modelStr!!
        trimStr = prefSubmitPriceData.trimStr!!
        extColorStr = prefSubmitPriceData.extColorStr!!
        intColorStr = prefSubmitPriceData.intColorStr!!

        if (TextUtils.isEmpty(yearId)) {
            yearStr = "YEAR - NEW CARS"
            makeStr = "MAKE"
            modelStr = "MODEL"
            trimStr = "TRIM"
            extColorStr = "EXTERIOR COLOR"
            intColorStr = "INTERIOR COLOR"
            tvYear.visibility = View.VISIBLE
            spYear.visibility = View.GONE
        } else {
            tvYear.visibility = View.GONE
            spYear.visibility = View.VISIBLE
            if (!isCallingYear)
                callVehicleYearAPI()
        }
    }

    private fun setRadius() {
        pref?.setRadius("")
    }

    private fun showApplyEmptyDialog() {
        val dialog = Dialog(requireContext(), R.style.FullScreenDialog)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(true)
        dialog.setContentView(R.layout.dialog_inventory_availability)
        dialog.run {
            Handler().postDelayed({
                dismiss()
            }, 3000)
        }
        setLayoutParam(dialog)
        dialog.show()
    }

    private fun showHighlightedDialog() {
        val dialog = Dialog(requireContext(), R.style.FullScreenDialog)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(true)
        dialog.setContentView(R.layout.dialog_highlight_inventory)
        dialog.run {
            Handler().postDelayed({
                dismiss()
            }, 3000)
        }
        setLayoutParam(dialog)
        dialog.show()
    }

    private fun showOtherInventoryEmptyDialog() {
        val dialog = Dialog(requireContext(), R.style.FullScreenDialog)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(true)
        dialog.setContentView(R.layout.dialog_other_inventory_availability)
        dialog.run {
            Handler().postDelayed({
                dismiss()
            }, 3000)
        }
        setLayoutParam(dialog)
        dialog.show()
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
                        callMinMSRPAPI()
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

    private fun callPromotionAPI() {
        if (Constant.isOnline(requireActivity())) {
            if (!Constant.isInitProgress()) {
                Constant.showLoader(requireActivity())
            } else if (Constant.isInitProgress() && !Constant.progress.isShowing) {
                Constant.showLoader(requireActivity())
            }
            promotionViewModel.getPromoCode(requireActivity())!!
                .observe(
                    requireActivity()
                ) { data ->
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
        } else {
            Toast.makeText(requireActivity(), Constant.noInternet, Toast.LENGTH_SHORT).show()
        }
    }

    private fun setClearData() {
        tvYear.visibility = View.VISIBLE
        spYear.visibility = View.GONE
        setYear()
        setMake()
        setModel()
        setTrim()
        prefSubmitPriceData = PrefSubmitPriceData()
        pref?.setSubmitPriceData(Gson().toJson(prefSubmitPriceData))
        setCurrentTime()
        setTimerPrefData()
    }

    private fun setUCDPrefData() {
        val prefUCD = pref?.getSearchDealData()
        val lykData = pref?.getSubmitPriceData()
        if ((TextUtils.isEmpty(prefUCD?.yearId) || lykData?.yearId == prefUCD?.yearId) && !prefUCD?.isUCDSel!!) {
            prefUCD?.yearId = lykData?.yearId
            prefUCD?.yearStr = lykData?.yearStr
        }
        if ((TextUtils.isEmpty(prefUCD?.makeId) || lykData?.makeId == prefUCD?.makeId) && !prefUCD?.isUCDSel!!) {
            prefUCD?.makeId = lykData?.makeId
            prefUCD?.makeStr = lykData?.makeStr
        }
        if ((TextUtils.isEmpty(prefUCD?.modelId) || lykData?.modelId == prefUCD?.modelId) && !prefUCD?.isUCDSel!!) {
            prefUCD?.modelId = lykData?.modelId
            prefUCD?.modelStr = lykData?.modelStr
        }
        if ((TextUtils.isEmpty(prefUCD?.trimId) || lykData?.trimId == prefUCD?.trimId) && !prefUCD?.isUCDSel!!) {
            prefUCD?.trimId = lykData?.trimId
            prefUCD?.trimStr = lykData?.trimStr
        }
        if ((TextUtils.isEmpty(prefUCD?.extColorId) || lykData?.extColorId == prefUCD?.extColorId) && !prefUCD?.isUCDSel!!) {
            prefUCD?.extColorId = lykData?.extColorId
            prefUCD?.extColorStr = lykData?.extColorStr
        }
        if ((TextUtils.isEmpty(prefUCD?.intColorId) || lykData?.intColorId == prefUCD?.intColorId) && !prefUCD?.isUCDSel!!) {
            prefUCD?.intColorId = lykData?.intColorId
            prefUCD?.intColorStr = lykData?.intColorStr
        }
        setUCDPrefData(prefUCD!!)
    }

    private fun setUCDPrefData(ucdData: PrefSearchDealData) {
        pref?.setSearchDealData(Gson().toJson(ucdData))
        setUCDCurrentTime()
    }

    private fun setUCDCurrentTime() {
        val df = SimpleDateFormat("yyyy MM d, HH:mm:ss a")
        val date = df.format(Calendar.getInstance().time)
        pref?.setSearchDealTime(date)
        startHandler()
    }
}