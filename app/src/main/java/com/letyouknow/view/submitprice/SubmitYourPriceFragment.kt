package com.letyouknow.view.submitprice

import android.app.Dialog
import android.os.Bundle
import android.os.Handler
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
import com.letyouknow.databinding.FragmentSubmitYourPriceBinding
import com.letyouknow.model.*
import com.letyouknow.retrofit.ApiConstant
import com.letyouknow.retrofit.viewmodel.*
import com.letyouknow.utils.AppGlobal
import com.letyouknow.utils.AppGlobal.Companion.isEmpty
import com.letyouknow.utils.AppGlobal.Companion.stringToDate
import com.letyouknow.view.dashboard.MainActivity
import com.letyouknow.view.spinneradapter.*
import com.letyouknow.view.submitprice.summary.SubmitPriceDealSummaryActivity
import com.pionymessenger.utils.Constant
import com.pionymessenger.utils.Constant.Companion.ARG_YEAR_MAKE_MODEL
import kotlinx.android.synthetic.main.dialog_vehicle_options.*
import kotlinx.android.synthetic.main.dialog_vehicle_packages.*
import kotlinx.android.synthetic.main.fragment_submit_your_price.*
import org.jetbrains.anko.support.v4.startActivity
import java.lang.reflect.Type
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap


class SubmitYourPriceFragment : BaseFragment(), View.OnClickListener,
    AdapterView.OnItemSelectedListener {

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


    private lateinit var binding: FragmentSubmitYourPriceBinding
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
            R.layout.fragment_submit_your_price,
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
        super.onResume()
        startHandler()
    }

    private fun startHandler() {
        if (!isEmpty(pref?.getSubmitPriceTime())) {
            Log.e("DAte Time", stringToDate(pref?.getSubmitPriceTime())?.toString()!!)
            handler = Handler()
            handler.postDelayed(runnable, 1000)
        }

    }

    private var runnable = object : Runnable {
        override fun run() {
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
            } else {
                handler.postDelayed(this, 1000)
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
            vehicleYearModel = ViewModelProvider(this).get(VehicleYearViewModel::class.java)
            vehicleMakeModel = ViewModelProvider(this).get(VehicleMakeViewModel::class.java)
            vehicleModelModel = ViewModelProvider(this).get(VehicleModelViewModel::class.java)
            vehicleTrimModel = ViewModelProvider(this).get(VehicleTrimViewModel::class.java)
            exteriorColorModel = ViewModelProvider(this).get(ExteriorColorViewModel::class.java)
            interiorColorModel = ViewModelProvider(this).get(InteriorColorViewModel::class.java)
            zipCodeModel = ViewModelProvider(this).get(VehicleZipCodeViewModel::class.java)
            packagesModel = ViewModelProvider(this).get(VehiclePackagesViewModel::class.java)
            packagesOptional = ViewModelProvider(this).get(VehicleOptionalViewModel::class.java)
            checkedPackageModel =
                ViewModelProvider(this).get(CheckedPackageInventoryViewModel::class.java)
            checkedAccessoriesModel =
                ViewModelProvider(this).get(CheckedAccessoriesInventoryViewModel::class.java)
            minMSRPViewModel =
                ViewModelProvider(this).get(MinMSRPViewModel::class.java)
            setTimerPrefData()

            btnSearch.setOnClickListener(this)
            MainActivity.getInstance().setVisibleEditImg(false)
            MainActivity.getInstance().setVisibleLogoutImg(false)
            ivClosePromo.setOnClickListener(this)
            tvPromo.setOnClickListener(this)
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
            if (Constant.isOnline(requireActivity())) {

                Constant.showLoader(requireActivity())
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
                            if (data != null || data?.size!! > 0) {
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
            if (isEmpty(prefSubmitPriceData.makeId!!))
                Constant.showLoader(requireActivity())
            vehicleMakeModel.getMake(
                requireActivity(),
                productId,
                yearId,
                ""
            )!!
                .observe(requireActivity(), Observer { data ->
                    if (isEmpty(prefSubmitPriceData.modelId!!))
                        Constant.dismissLoader()
                    Log.e("Make Data", Gson().toJson(data))
                    try {
                        if (data != null || data?.size!! > 0) {
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
            if (isEmpty(prefSubmitPriceData.modelId!!))
                Constant.showLoader(requireActivity())
            vehicleModelModel.getModel(
                requireActivity(),
                productId,
                yearId,
                makeId,
                ""
            )!!
                .observe(requireActivity(), Observer { data ->
                    if (isEmpty(prefSubmitPriceData.trimId!!))
                        Constant.dismissLoader()
                    Log.e("MODEL Data", Gson().toJson(data))
                    try {
                        if (data != null || data?.size!! > 0) {
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
            if (isEmpty(prefSubmitPriceData.trimId!!))
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
                    if (isEmpty(prefSubmitPriceData.extColorId!!))
                        Constant.dismissLoader()
                    Log.e("TRIM Data", Gson().toJson(data))
                    try {
                        if (data != null || data?.size!! > 0) {
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
            if (isEmpty(prefSubmitPriceData.extColorId!!))
                Constant.showLoader(requireActivity())
            exteriorColorModel.getExteriorColor(
                requireActivity(),
                productId,
                yearId,
                makeId,
                modelId,
                trimId,
                ""
            )!!
                .observe(requireActivity(), Observer { data ->
                    if (isEmpty(prefSubmitPriceData.intColorId!!))
                        Constant.dismissLoader()
                    Log.e("EXTERIOR COLOR Data", Gson().toJson(data))
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
            if (isEmpty(prefSubmitPriceData.intColorId!!))
                Constant.showLoader(requireActivity())
            interiorColorModel.getInteriorColor(
                requireActivity(),
                productId,
                yearId,
                makeId,
                modelId,
                trimId,
                extColorId,
                ""
            )!!
                .observe(requireActivity(), Observer { data ->
                    Constant.dismissLoader()
                    Log.e("INTERIOR Data", Gson().toJson(data))
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
            Constant.showLoader(requireActivity())
            packagesModel.getPackages(
                requireActivity(),
                productId,
                yearId,
                makeId,
                modelId,
                trimId,
                extColorId,
                intColorId, ""
            )!!
                .observe(requireActivity(), Observer { data ->
                    Constant.dismissLoader()
                    Log.e("Packages Data", Gson().toJson(data))
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
            Constant.showLoader(requireActivity())
            val jsonArray = JsonArray()
            for (i in 0 until adapterPackages.itemCount) {
                if (adapterPackages.getItem(i).isSelect!!) {
                    jsonArray.add(adapterPackages.getItem(i).vehiclePackageID)
                }
            }
            val request = HashMap<String, Any>()
            request[ApiConstant.packageList] = jsonArray
            request[ApiConstant.productId] = productId
            request[ApiConstant.yearId] = yearId
            request[ApiConstant.makeId] = makeId
            request[ApiConstant.modelId] = modelId
            request[ApiConstant.trimId] = trimId
            request[ApiConstant.exteriorColorId] = extColorId
            request[ApiConstant.interiorColorId] = intColorId
            request[ApiConstant.zipCode] = ""

            packagesOptional.getOptional(
                requireActivity(),
                request
            )!!
                .observe(requireActivity(), Observer { data ->
                    Constant.dismissLoader()
                    Log.e("Options Data", Gson().toJson(data))
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

    private fun callCheckedPackageAPI() {
        if (Constant.isOnline(requireActivity())) {
            Constant.showLoader(requireActivity())
            val jsonArray = JsonArray()
            for (i in 0 until adapterPackages.itemCount) {
                if (adapterPackages.getItem(i).isSelect!!) {
                    jsonArray.add(adapterPackages.getItem(i).vehiclePackageID)
                }
            }
            val request = HashMap<String, Any>()
            request[ApiConstant.checkedList] = jsonArray
            request[ApiConstant.productId] = productId
            request[ApiConstant.yearId] = yearId
            request[ApiConstant.makeId] = makeId
            request[ApiConstant.modelId] = modelId
            request[ApiConstant.trimId] = trimId
            request[ApiConstant.exteriorColorId] = extColorId
            request[ApiConstant.interiorColorId] = intColorId
            request[ApiConstant.zipCode] = ""

            checkedPackageModel.checkedPackage(requireActivity(), request)!!
                .observe(this, Observer { data ->
                    Constant.dismissLoader()
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
                )

        } else {
            Toast.makeText(requireActivity(), Constant.noInternet, Toast.LENGTH_SHORT).show()
        }
    }

    private fun callCheckedAccessoriesAPI() {
        if (Constant.isOnline(requireActivity())) {
            Constant.showLoader(requireActivity())
            val jsonArray = JsonArray()
            for (i in 0 until adapterOptions.itemCount) {
                if (adapterOptions.getItem(i).isSelect!!) {
                    jsonArray.add(adapterOptions.getItem(i).dealerAccessoryID)
                }
            }
            val jsonArrayPackage = JsonArray()
            for (i in 0 until adapterPackages.itemCount) {
                if (adapterPackages.getItem(i).isSelect!!) {
                    jsonArrayPackage.add(adapterPackages.getItem(i).vehiclePackageID)
                }
            }
            val request = HashMap<String, Any>()
            request[ApiConstant.packageList] = jsonArrayPackage
            request[ApiConstant.checkedList] = jsonArray
            request[ApiConstant.productId] = productId
            request[ApiConstant.yearId] = yearId
            request[ApiConstant.makeId] = makeId
            request[ApiConstant.modelId] = modelId
            request[ApiConstant.trimId] = trimId
            request[ApiConstant.exteriorColorId] = extColorId
            request[ApiConstant.interiorColorId] = intColorId
            request[ApiConstant.zipCode] = ""

            checkedAccessoriesModel.checkedAccessories(requireActivity(), request)!!
                .observe(this, Observer { data ->
                    Constant.dismissLoader()
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
                if (adapterOptions.getItem(i).isSelect!!) {
                    jsonArray.add(adapterOptions.getItem(i).dealerAccessoryID)
                }
            }
            val jsonArrayPackage = JsonArray()

            for (i in 0 until adapterPackages.itemCount) {
                if (adapterPackages.getItem(i).isSelect!!) {
                    jsonArrayPackage.add(adapterPackages.getItem(i).vehiclePackageID)
                }
            }
            val request = HashMap<String, Any>()
            request[ApiConstant.packageList] = jsonArrayPackage
            request[ApiConstant.checkedList] = jsonArray
            request[ApiConstant.productId] = productId
            request[ApiConstant.yearId] = yearId
            request[ApiConstant.makeId] = makeId
            request[ApiConstant.modelId] = modelId
            request[ApiConstant.trimId] = trimId
            request[ApiConstant.exteriorColorId] = extColorId
            request[ApiConstant.interiorColorId] = intColorId

            minMSRPViewModel.minMSRPCall(requireActivity(), request)!!
                .observe(this, Observer { data ->
                    Constant.dismissLoader()

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
            R.id.btnSearch -> {
                if (isValid()) {
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

                    startActivity<SubmitPriceDealSummaryActivity>(
                        ARG_YEAR_MAKE_MODEL to Gson().toJson(
                            data
                        )
                    )
                }
//                callMinMSRPAPI()
            }
            R.id.tvPackages -> {
                arSelectPackage = ArrayList()
                for (i in 0 until adapterPackages.itemCount) {
                    arSelectPackage.add(adapterPackages.getItem(i))
                }
                selectPackageStr = Gson().toJson(arSelectPackage)
                dialogPackage.show()
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
                    tvPackages.text = packagesStr
                    tvErrorPackages.visibility = View.GONE
                    setOptions(true)
                    callOptionalAccessoriesAPI()
                } else {
                    tvPackages.text = "PACKAGES"
                    setOptions(false)
                }
                prefSubmitPriceData.packagesData = adapterPackages.getAll()
                Constant.dismissLoader()
                prefSubmitPriceData.optionsData = ArrayList()
                pref?.setSubmitPriceData(Gson().toJson(prefSubmitPriceData))
                setCurrentTime()
                dialogPackage.dismiss()
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
                Log.e("select1", selectPackageStr)
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
                        dataPackage.isOtherSelect = false
                        dataPackage.isGray = false
                        adapterPackages.update(i, dataPackage)
                    }
                    val data0 = adapterPackages.getItem(0)
                    data0.isSelect = false
                    adapterPackages.update(0, data0)
                    if (!data0.isGray!!)
                        callCheckedPackageAPI()
                }


                Log.e("clickupdate", selectPackageStr)

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
                    tvErrorOptionsAccessories.visibility = View.GONE
                    tvOptionalAccessories.text = optionsStr
                } else {
                    tvOptionalAccessories.text = "OPTIONS & ACCESSORIES"
                }
                prefSubmitPriceData.optionsData = adapterOptions.getAll()
                pref?.setSubmitPriceData(Gson().toJson(prefSubmitPriceData))
                setCurrentTime()
                dialogOptions.dismiss()
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
                arSelectOption = ArrayList()
                for (i in 0 until adapterOptions.itemCount) {
                    arSelectOption.add(adapterOptions.getItem(i))
                }
                selectOptionStr = Gson().toJson(arSelectOption)
                dialogOptions.show()
            }
            R.id.llOptions -> {
                Log.e("select1", selectOptionStr)
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
                        dataOptions.isOtherSelect = false
                        dataOptions.isGray = false
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
        Log.e("Submit Date", date)
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
                    prefSubmitPriceData.yearId = data.vehicleYearID!!
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
                }
            }
            R.id.spMake -> {
                val data = adapterMake.getItem(position) as VehicleMakeData
                makeId = data.vehicleMakeID!!
                makeStr = data.make!!
                AppGlobal.setSpinnerLayoutPos(position, spMake, requireActivity())
                if (data.make != "MAKE") {
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
                }

            }
            R.id.spTrim -> {
                val data = adapterTrim.getItem(position) as VehicleTrimData
                trimId = data.vehicleTrimID!!
                trimStr = data.trim!!
                AppGlobal.setSpinnerLayoutPos(position, spTrim, requireActivity())
                if (data.trim != "TRIM") {
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
                }

            }
            R.id.spExteriorColor -> {
                val data = adapterExterior.getItem(position) as ExteriorColorData
//                extColorId = "0"
                extColorId = data.vehicleExteriorColorID!!
                extColorStr = data.exteriorColor!!
                AppGlobal.setSpinnerLayoutPos(position, spExteriorColor, requireActivity())
                if (data.exteriorColor != "EXTERIOR COLOR") {
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
                }

            }
            R.id.spInteriorColor -> {
                val data = adapterInterior.getItem(position) as InteriorColorData
                intColorId = data.vehicleInteriorColorID!!
                intColorStr = data.interiorColor!!
                AppGlobal.setSpinnerLayoutPos(position, spInteriorColor, requireActivity())
                if (data.interiorColor != "INTERIOR COLOR") {
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
                PackagesAdapter(R.layout.list_item_packages, this@SubmitYourPriceFragment)
            dialogPackage.rvPackages.adapter = adapterPackages
            adapterPackages.addAll(data)

            tvCancelPackage.setOnClickListener(this@SubmitYourPriceFragment)
            tvResetPackage.setOnClickListener(this@SubmitYourPriceFragment)
            tvApplyPackage.setOnClickListener(this@SubmitYourPriceFragment)
        }
        setLayoutParam(dialogPackage)

        /*dialogPackage = LayoutInflater.from(requireActivity())
            .inflate(R.layout.dialog_vehicle_packages, null, false)
        popUpPackage = PopupWindow(
            dialogPackage,
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT,
            false
        )
        popUpPackage.isTouchable = true
        popUpPackage.isFocusable = true
        popUpPackage.isOutsideTouchable = true
//        dialogPackage.run {
        adapterPackages =
            PackagesAdapter(R.layout.list_item_packages, this@OneDealNearYouFragment)
        dialogPackage.rvPackages.adapter = adapterPackages
        adapterPackages.addAll(data)
//        }*/

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
                OptionsAdapter(R.layout.list_item_options, this@SubmitYourPriceFragment)
            rvOptions.adapter = adapterOptions
            adapterOptions.addAll(data)

            tvCancelOption.setOnClickListener(this@SubmitYourPriceFragment)
            tvResetOption.setOnClickListener(this@SubmitYourPriceFragment)
            tvApplyOption.setOnClickListener(this@SubmitYourPriceFragment)
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
        if (Constant.progress.isShowing)
            Constant.dismissLoader()
        super.onDestroy()
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

        if (!isCallingYear)
            callVehicleYearAPI()
    }



}