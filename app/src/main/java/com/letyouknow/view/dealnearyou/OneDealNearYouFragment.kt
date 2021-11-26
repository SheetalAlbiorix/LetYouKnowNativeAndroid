package com.letyouknow.view.dealnearyou

import android.app.Dialog
import android.os.Bundle
import android.os.Handler
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.util.Log
import android.view.*
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.*
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.google.gson.Gson
import com.google.gson.JsonArray
import com.google.gson.reflect.TypeToken
import com.letyouknow.R
import com.letyouknow.base.BaseFragment
import com.letyouknow.databinding.FragmentOneDealNearYouBinding
import com.letyouknow.model.*
import com.letyouknow.retrofit.ApiConstant
import com.letyouknow.retrofit.viewmodel.*
import com.letyouknow.utils.AppGlobal
import com.letyouknow.utils.AppGlobal.Companion.isEmpty
import com.letyouknow.view.dashboard.MainActivity
import com.letyouknow.view.home.dealsummary.DealSummaryActivity
import com.letyouknow.view.spinneradapter.*
import com.pionymessenger.utils.Constant
import com.pionymessenger.utils.Constant.Companion.ARG_LCD_DEAL_GUEST
import kotlinx.android.synthetic.main.dialog_vehicle_options.*
import kotlinx.android.synthetic.main.dialog_vehicle_packages.*
import kotlinx.android.synthetic.main.fragment_one_deal_near_you.*
import org.jetbrains.anko.support.v4.startActivity
import java.lang.reflect.Type
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

class OneDealNearYouFragment : BaseFragment(), View.OnClickListener,
    AdapterView.OnItemSelectedListener {
    private var isValidZipCode = false

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
    private lateinit var findLCDDealGuestViewModel: FindLCDDealViewModel
    private lateinit var tokenModel: RefreshTokenViewModel

    private lateinit var adapterYear: YearSpinnerAdapter
    private lateinit var adapterMake: MakeSpinnerAdapter
    private lateinit var adapterModel: ModelSpinnerAdapter
    private lateinit var adapterTrim: TrimsSpinnerAdapter
    private lateinit var adapterExterior: ExteriorSpinnerAdapter
    private lateinit var adapterInterior: InteriorSpinnerAdapter
    private lateinit var adapterPackages: PackagesAdapter
    private lateinit var adapterOptions: OptionsAdapter


    private var productId = "2"
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
    private var intColorStr = "INTERIOR COLOR"
    private var extColorStr = "EXTERIOR COLOR"

    private lateinit var animBlink: Animation
    private lateinit var animSlideRightToLeft: Animation
    private lateinit var animSlideLeftToRight: Animation


    private lateinit var binding: FragmentOneDealNearYouBinding
    private var upDownData = UpDownData()

    private lateinit var prefOneDealNearYouData: PrefOneDealNearYouData

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_one_deal_near_you,
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
            packagesModel = ViewModelProvider(this).get(VehiclePackagesViewModel::class.java)
            packagesOptional = ViewModelProvider(this).get(VehicleOptionalViewModel::class.java)
            tokenModel = ViewModelProvider(this).get(RefreshTokenViewModel::class.java)
            checkedPackageModel =
                ViewModelProvider(this).get(CheckedPackageInventoryViewModel::class.java)
            checkedAccessoriesModel =
                ViewModelProvider(this).get(CheckedAccessoriesInventoryViewModel::class.java)
            findLCDDealGuestViewModel =
                ViewModelProvider(this).get(FindLCDDealViewModel::class.java)


            btnSearch.setOnClickListener(this)
            MainActivity.getInstance().setVisibleEditImg(false)
            MainActivity.getInstance().setVisibleLogoutImg(false)
            tvPromo.setOnClickListener(this)
            ivClosePromo.setOnClickListener(this)

            setPrefOneDealNearYouData()
        } catch (e: Exception) {

        }
    }

    override fun onResume() {
        super.onResume()
        startHandler()
    }

    private fun onChangeZipCode() {
        edtZipCode.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val str = s.toString()
                if (str.length == 5) {
                    prefOneDealNearYouData.yearId = ""
                    prefOneDealNearYouData.makeId = ""
                    prefOneDealNearYouData.modelId = ""
                    prefOneDealNearYouData.trimId = ""
                    prefOneDealNearYouData.extColorId = ""
                    prefOneDealNearYouData.intColorId = ""
                    prefOneDealNearYouData.yearStr = ""
                    prefOneDealNearYouData.makeStr = ""
                    prefOneDealNearYouData.modelStr = ""
                    prefOneDealNearYouData.trimStr = ""
                    prefOneDealNearYouData.extColorStr = ""
                    prefOneDealNearYouData.intColorStr = ""
                    prefOneDealNearYouData.packagesData = ArrayList()
                    prefOneDealNearYouData.optionsData = ArrayList()
                    callVehicleZipCodeAPI(str)
                } else if (str.length < 5) {
                    isValidZipCode = false
                    prefOneDealNearYouData.isZipCode = isValidZipCode!!
                    tvErrorZipCode.visibility = View.GONE
                    edtZipCode.setBackgroundResource(R.drawable.bg_edittext_dark)
                }
                prefOneDealNearYouData.zipCode = edtZipCode.text.toString().trim()

                setPrefData()
            }

            override fun afterTextChanged(s: Editable?) {

            }

        })
    }

    private lateinit var handler: Handler
    private fun setPrefData() {
        pref?.setOneDealNearYouData(Gson().toJson(prefOneDealNearYouData))
        setCurrentTime()
    }

    private fun setCurrentTime() {
        val df = SimpleDateFormat("yyyy MM d, HH:mm:ss a")
        val date = df.format(Calendar.getInstance().time)
        pref?.setOneDealNearYou(date)
      //  Log.e("Submit Date", date)
        startHandler()
    }

    private fun startHandler() {
        if (!isEmpty(pref?.getOneDealNearYou())) {
            //  Log.e("Date Time", AppGlobal.stringToDate(pref?.getOneDealNearYou())?.toString()!!)
                handler = Handler()
                handler.postDelayed(runnable, 1000)
        }

    }

    private var runnable = object : Runnable {
        override fun run() {
            val date = Calendar.getInstance().time
            val lastDate = AppGlobal.stringToDate(pref?.getOneDealNearYou())

            val diff: Long = date.time - (lastDate?.time ?: 0)
            print(diff)

            val seconds = diff / 1000
            val minutes = seconds / 60
            if (minutes >= 30) {
                handler.removeCallbacks(this)
                pref?.setOneDealNearYouData(Gson().toJson(PrefOneDealNearYouData()))
                pref?.setOneDealNearYou("")
                setPrefOneDealNearYouData()
            } else {
                handler.postDelayed(this, 1000)
            }
        }

    }

    private fun setYear() {
        try {
            spYear.isEnabled = false
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

    private fun callVehicleZipCodeAPI(zipCode: String?) {
        try {
            if (Constant.isOnline(requireActivity())) {
                Constant.showLoader(requireActivity())
                zipCodeModel.getZipCode(requireActivity(), zipCode)!!
                    .observe(requireActivity(), Observer { data ->
                        Constant.dismissLoader()
                        Log.e("ZipCode Data", Gson().toJson(data))
                        try {
                            if (!data) {
                                edtZipCode.setBackgroundResource(R.drawable.bg_edittext_dark_error)
                                tvErrorZipCode.visibility = View.VISIBLE
                                setYear()
                                setMake()
                                setModel()
                                setTrim()
                                setExteriorColor()
                                setInteriorColor()
                                setPackages(false)
                                setOptions(false)
                            } else {
                                tvErrorZipCode.visibility = View.GONE
                                callVehicleYearAPI()
                            }
                            isValidZipCode = data
                            prefOneDealNearYouData.isZipCode = isValidZipCode!!
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
            spYear.isEnabled = true
            if (Constant.isOnline(requireActivity())) {
                Constant.showLoader(requireActivity())
                vehicleYearModel.getYear(
                    requireActivity(),
                    productId,
                    edtZipCode.text.toString().trim()
                )!!
                    .observe(requireActivity(), Observer { data ->
                        if (isEmpty(prefOneDealNearYouData.makeId))
                            Constant.dismissLoader()
                        try {
                            Log.e("Year Data", Gson().toJson(data))
                            if (data != null || data?.size!! > 0) {
                                val yearData = VehicleYearData()
                                yearData.year = "YEAR - NEW CARS"
                                data.add(0, yearData)
                                adapterYear = YearSpinnerAdapter(requireActivity(), data)
                                spYear.adapter = adapterYear
                                for (i in 0 until data.size) {
                                    if (!isEmpty(yearId) && yearId == data[i].vehicleYearID) {
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
                if (isEmpty(prefOneDealNearYouData.makeId))
                    Constant.showLoader(requireActivity())
                vehicleMakeModel.getMake(
                    requireActivity(),
                    productId,
                    yearId,
                    edtZipCode.text.toString().trim()
                )!!
                    .observe(requireActivity(), Observer { data ->
                        if (isEmpty(prefOneDealNearYouData.modelId))
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
                                    if (!isEmpty(makeId) && makeId == data[i].vehicleMakeID) {
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
                if (isEmpty(prefOneDealNearYouData.modelId))
                    Constant.showLoader(requireActivity())
                vehicleModelModel.getModel(
                    requireActivity(),
                    productId,
                    yearId,
                    makeId,
                    edtZipCode.text.toString().trim()
                )!!
                    .observe(requireActivity(), Observer { data ->
                        if (isEmpty(prefOneDealNearYouData.trimId))
                            Constant.dismissLoader()
                        Log.e("Make Data", Gson().toJson(data))
                        try {
                            if (data != null || data?.size!! > 0) {
                                val modelData = VehicleModelData()
                                modelData.model = "MODEL"
                                data.add(0, modelData)
                                adapterModel = ModelSpinnerAdapter(requireActivity(), data)
                                spModel.adapter = adapterModel
                                for (i in 0 until data.size) {
                                    if (!isEmpty(modelId) && modelId == data[i].vehicleModelID) {
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
                if (isEmpty(prefOneDealNearYouData.trimId))
                    Constant.showLoader(requireActivity())
                vehicleTrimModel.getTrim(
                    requireActivity(),
                    productId,
                    yearId,
                    makeId,
                    modelId,
                    edtZipCode.text.toString().trim()
                )!!
                    .observe(requireActivity(), Observer { data ->
                        if (isEmpty(prefOneDealNearYouData.extColorId))
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
                                    if (!isEmpty(trimId) && trimId == data[i].vehicleTrimID) {
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
                if (isEmpty(prefOneDealNearYouData.extColorId))
                    Constant.showLoader(requireActivity())
                exteriorColorModel.getExteriorColor(
                    requireActivity(),
                    productId,
                    yearId,
                    makeId,
                    modelId,
                    trimId,
                    edtZipCode.text.toString().trim()
                )!!
                    .observe(requireActivity(), Observer { data ->
                        if (isEmpty(prefOneDealNearYouData.intColorId))
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
                                    if (!isEmpty(extColorId) && extColorId == data[i].vehicleExteriorColorID) {
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
                if (isEmpty(prefOneDealNearYouData.intColorId))
                    Constant.showLoader(requireActivity())
                interiorColorModel.getInteriorColor(
                    requireActivity(),
                    productId,
                    yearId,
                    makeId,
                    modelId,
                    trimId,
                    extColorId,
                    edtZipCode.text.toString().trim()
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
                                    if (!isEmpty(intColorId) && intColorId == data[i].vehicleInteriorColorID) {
                                        spInteriorColor.setSelection(i, true)
                                        AppGlobal.setSpinnerLayoutPos(
                                            i,
                                            spInteriorColor,
                                            requireActivity()
                                        )
                                        if (!prefOneDealNearYouData.packagesData.isNullOrEmpty()) {
                                            popupPackages(prefOneDealNearYouData.packagesData!!)
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
                                            if (!prefOneDealNearYouData.optionsData.isNullOrEmpty()) {
                                                popupOptions(prefOneDealNearYouData.optionsData!!)
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
                                                    tvOptionalAccessories.text =
                                                        "OPTIONS & ACCESSORIES"
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
        } catch (e: Exception) {

        }
    }

    private fun callVehiclePackagesAPI() {
        try {
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
                    intColorId, edtZipCode.text.toString().trim()
                )!!
                    .observe(requireActivity(), Observer { data ->
                        Constant.dismissLoader()
                        Log.e("Make Data", Gson().toJson(data))
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
        } catch (e: Exception) {

        }
    }

    private fun callOptionalAccessoriesAPI() {
        try {
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
                request[ApiConstant.zipCode] = edtZipCode.text.toString().trim()

                packagesOptional.getOptional(
                    requireActivity(),
                    request
                )!!
                    .observe(requireActivity(), Observer { data ->
                        Constant.dismissLoader()
                        try {
                            Log.e("Make Data", Gson().toJson(data))
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
        } catch (e: Exception) {

        }
    }

    private fun callCheckedPackageAPI() {
        try {
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
                request[ApiConstant.zipCode] = edtZipCode.text.toString().trim()

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
        } catch (e: Exception) {

        }
    }

    private fun callCheckedAccessoriesAPI() {
        try {
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
                request[ApiConstant.zipCode] = edtZipCode.text.toString().trim()

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
        } catch (e: Exception) {

        }
    }

    private fun callSearchFindDealAPI() {
        try {
            if (Constant.isOnline(requireActivity())) {
                Constant.showLoader(requireActivity())
                val jsonArrayAccessories = JsonArray()
                var accessoriesStr = ""
                var isFirstAcce = true
                val arAccId: ArrayList<String> = ArrayList()
                for (i in 0 until adapterOptions.itemCount) {
                    if (adapterOptions.getItem(i).isSelect!!) {
                        jsonArrayAccessories.add(adapterOptions.getItem(i).dealerAccessoryID)
                        arAccId.add(adapterOptions.getItem(i).dealerAccessoryID!!)
                        if (isFirstAcce) {
                            isFirstAcce = false
                            accessoriesStr = adapterOptions.getItem(i).accessory!!
                        } else
                            accessoriesStr += ",\n" + adapterOptions.getItem(i).accessory!!
                    }
                }
                val jsonArrayPackage = JsonArray()
                var packageStr = ""
                var isFirstPackage = true
                val arPackageId: ArrayList<String> = ArrayList()

                for (i in 0 until adapterPackages.itemCount) {
                    if (adapterPackages.getItem(i).isSelect!!) {
                        jsonArrayPackage.add(adapterPackages.getItem(i).vehiclePackageID)
                        arPackageId.add(adapterPackages.getItem(i).vehiclePackageID!!)
                        if (isFirstPackage) {
                            isFirstPackage = false
                            packageStr = adapterPackages.getItem(i).packageName!!
                        } else {
                            packageStr =
                                packageStr + ",\n" + adapterPackages.getItem(i).packageName!!
                        }
                    }
                }
                val request = HashMap<String, Any>()
                request[ApiConstant.vehicleYearID] = yearId
                request[ApiConstant.vehicleMakeID] = makeId
                request[ApiConstant.vehicleModelID] = modelId
                request[ApiConstant.vehicleTrimID] = trimId
                request[ApiConstant.vehicleExteriorColorID] = extColorId
                request[ApiConstant.vehicleInteriorColorID] = intColorId
                request[ApiConstant.zipCode] = edtZipCode.text.toString().trim()
                request[ApiConstant.dealerAccessoryIDs] = jsonArrayAccessories
                request[ApiConstant.vehiclePackageIDs] = jsonArrayPackage
                Log.e("Request LCDDeal", Gson().toJson(request))
                findLCDDealGuestViewModel.findDeal(requireActivity(), request)!!
                    .observe(this, Observer { data ->
                        Constant.dismissLoader()
                        Log.e("Response", Gson().toJson(data))
                        data.productId = productId
                        data.yearId = yearId
                        data.makeId = makeId
                        data.modelId = modelId
                        data.trimId = trimId
                        data.exteriorColorId = extColorId
                        data.interiorColorId = intColorId
                        data.yearStr = yearStr
                        data.makeStr = makeStr
                        data.modelStr = modelStr
                        data.trimStr = trimStr
                        data.exteriorColorStr = extColorStr
                        data.interiorColorStr = intColorStr
                        data.arPackage = packageStr
                        data.arAccessories = accessoriesStr
                        data.arAccessoriesId = arAccId
                        data.arPackageId = arPackageId
                        startActivity<DealSummaryActivity>(ARG_LCD_DEAL_GUEST to Gson().toJson(data))
                    }
                    )
            } else {
                Toast.makeText(requireActivity(), Constant.noInternet, Toast.LENGTH_SHORT).show()
            }
        } catch (e: Exception) {

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
                // loadFragment(DealSummeryActivity(), getString(R.string.one_deal_near_you))
                if (isValid())
                    callRefreshTokenApi()
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
                tvPackages.text = packagesStr
                if (packagesStr.length > 0) {
                    tvErrorPackages.visibility = View.GONE
                    setOptions(true)
                    callOptionalAccessoriesAPI()
                } else {
                    tvPackages.text = "PACKAGES"
                    setOptions(false)
                }
                prefOneDealNearYouData.packagesData = adapterPackages.getAll()
                prefOneDealNearYouData.optionsData = ArrayList()
                setPrefData()
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
                        dataPackage.isGray = false
                        dataPackage.isOtherSelect = false
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
                prefOneDealNearYouData.optionsData = adapterOptions.getAll()
                setPrefData()
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

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        when (parent?.id) {
            R.id.spYear -> {
                val data = adapterYear.getItem(position) as VehicleYearData
                yearId = data.vehicleYearID!!
                yearStr = data.year!!
                if (data.year != "YEAR - NEW CARS") {
                    prefOneDealNearYouData.yearId = data.vehicleYearID
                    prefOneDealNearYouData.yearStr = data.year
                    prefOneDealNearYouData.makeId = ""
                    prefOneDealNearYouData.modelId = ""
                    prefOneDealNearYouData.trimId = ""
                    prefOneDealNearYouData.extColorId = ""
                    prefOneDealNearYouData.intColorId = ""
                    prefOneDealNearYouData.makeStr = ""
                    prefOneDealNearYouData.modelStr = ""
                    prefOneDealNearYouData.trimStr = ""
                    prefOneDealNearYouData.extColorStr = ""
                    prefOneDealNearYouData.intColorStr = ""
                    prefOneDealNearYouData.packagesData = ArrayList()
                    prefOneDealNearYouData.optionsData = ArrayList()
                    Constant.dismissLoader()
                    setPrefData()
                    setErrorVisibleGone()
                    callVehicleMakeAPI()
                    setModel()
                    setTrim()
                    setExteriorColor()
                    setInteriorColor()
                    setPackages(false)
                    setOptions(false)

                }
                AppGlobal.setSpinnerLayoutPos(position, spYear, requireActivity())
            }
            R.id.spMake -> {
                val data = adapterMake.getItem(position) as VehicleMakeData
                makeId = data.vehicleMakeID!!
                makeStr = data.make!!
                AppGlobal.setSpinnerLayoutPos(position, spMake, requireActivity())
                if (data.make != "MAKE") {
                    prefOneDealNearYouData.makeId = data.vehicleMakeID
                    prefOneDealNearYouData.makeStr = data.make
                    prefOneDealNearYouData.modelId = ""
                    prefOneDealNearYouData.trimId = ""
                    prefOneDealNearYouData.extColorId = ""
                    prefOneDealNearYouData.intColorId = ""
                    prefOneDealNearYouData.modelStr = ""
                    prefOneDealNearYouData.trimStr = ""
                    prefOneDealNearYouData.extColorStr = ""
                    prefOneDealNearYouData.intColorStr = ""
                    prefOneDealNearYouData.packagesData = ArrayList()
                    prefOneDealNearYouData.optionsData = ArrayList()
                    Constant.dismissLoader()
                    setPrefData()
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
                    prefOneDealNearYouData.modelId = data.vehicleModelID
                    prefOneDealNearYouData.modelStr = data.model
                    prefOneDealNearYouData.trimId = ""
                    prefOneDealNearYouData.extColorId = ""
                    prefOneDealNearYouData.intColorId = ""
                    prefOneDealNearYouData.trimStr = ""
                    prefOneDealNearYouData.extColorStr = ""
                    prefOneDealNearYouData.intColorStr = ""
                    prefOneDealNearYouData.packagesData = ArrayList()
                    prefOneDealNearYouData.optionsData = ArrayList()
                    Constant.dismissLoader()
                    setPrefData()
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
                    prefOneDealNearYouData.trimId = data.vehicleTrimID
                    prefOneDealNearYouData.trimStr = data.trim
                    prefOneDealNearYouData.extColorId = ""
                    prefOneDealNearYouData.intColorId = ""
                    prefOneDealNearYouData.extColorStr = ""
                    prefOneDealNearYouData.intColorStr = ""
                    prefOneDealNearYouData.packagesData = ArrayList()
                    prefOneDealNearYouData.optionsData = ArrayList()
                    Constant.dismissLoader()
                    setPrefData()
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
                    prefOneDealNearYouData.extColorId = data.vehicleExteriorColorID
                    prefOneDealNearYouData.extColorStr = data.exteriorColor
                    prefOneDealNearYouData.intColorId = ""
                    prefOneDealNearYouData.intColorStr = ""
                    prefOneDealNearYouData.packagesData = ArrayList()
                    prefOneDealNearYouData.optionsData = ArrayList()
                    Constant.dismissLoader()
                    setPrefData()
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
                    prefOneDealNearYouData.intColorId = data.vehicleInteriorColorID
                    prefOneDealNearYouData.intColorStr = data.interiorColor
                    prefOneDealNearYouData.packagesData = ArrayList()
                    prefOneDealNearYouData.optionsData = ArrayList()
                    Constant.dismissLoader()
                    setPrefData()
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
                PackagesAdapter(R.layout.list_item_packages, this@OneDealNearYouFragment)
            dialogPackage.rvPackages.adapter = adapterPackages
            adapterPackages.addAll(data)

            tvCancelPackage.setOnClickListener(this@OneDealNearYouFragment)
            tvResetPackage.setOnClickListener(this@OneDealNearYouFragment)
            tvApplyPackage.setOnClickListener(this@OneDealNearYouFragment)
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
                OptionsAdapter(R.layout.list_item_options, this@OneDealNearYouFragment)
            rvOptions.adapter = adapterOptions
            adapterOptions.addAll(data)

            tvCancelOption.setOnClickListener(this@OneDealNearYouFragment)
            tvResetOption.setOnClickListener(this@OneDealNearYouFragment)
            tvApplyOption.setOnClickListener(this@OneDealNearYouFragment)
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
        tvErrorModel.visibility = View.GONE
        tvErrorMake.visibility = View.GONE
        tvErrorTrim.visibility = View.GONE
        tvErrorInterior.visibility = View.GONE
        tvErrorExterior.visibility = View.GONE
        tvErrorPackages.visibility = View.GONE
        tvErrorOptionsAccessories.visibility = View.GONE
    }

    /*override fun onPause() {
        Constant.dismissLoader()
        super.onPause()
    }*/
    override fun onDestroy() {
        if (Constant.isInitProgress() && Constant.progress.isShowing)
            Constant.dismissLoader()
        super.onDestroy()
    }

    private fun setPrefOneDealNearYouData() {
        try {
            prefOneDealNearYouData = pref?.getOneDealNearYouData()!!
            setYear()
            setMake()
            setModel()
            setTrim()
            setExteriorColor()
            setInteriorColor()
            setPackages(false)
            setOptions(false)
            if (!isEmpty(prefOneDealNearYouData.zipCode)) edtZipCode.setText(prefOneDealNearYouData.zipCode)
            yearId = prefOneDealNearYouData.yearId!!
            makeId = prefOneDealNearYouData.makeId!!
            modelId = prefOneDealNearYouData.modelId!!
            trimId = prefOneDealNearYouData.trimId!!
            extColorId = prefOneDealNearYouData.extColorId!!
            intColorId = prefOneDealNearYouData.intColorId!!
            yearStr = prefOneDealNearYouData.yearStr!!
            makeStr = prefOneDealNearYouData.makeStr!!
            modelStr = prefOneDealNearYouData.modelStr!!
            trimStr = prefOneDealNearYouData.trimStr!!
            extColorStr = prefOneDealNearYouData.extColorStr!!
            intColorStr = prefOneDealNearYouData.intColorStr!!

            onChangeZipCode()
            if (prefOneDealNearYouData.zipCode?.length!! >= 1) {
                val str = prefOneDealNearYouData.zipCode.toString()
                if (str.length == 5) {
                    callVehicleZipCodeAPI(str)
                } else if (str.length < 5) {
                    isValidZipCode = false
                    prefOneDealNearYouData.isZipCode = isValidZipCode!!
                    tvErrorZipCode.visibility = View.GONE
                    edtZipCode.setBackgroundResource(R.drawable.bg_edittext_dark)
                }
                prefOneDealNearYouData.zipCode = edtZipCode.text.toString().trim()
                setPrefData()
            }
        } catch (e: Exception) {

        }
    }
}