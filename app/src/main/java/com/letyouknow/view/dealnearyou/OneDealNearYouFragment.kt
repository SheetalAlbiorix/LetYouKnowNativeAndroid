package com.letyouknow.view.dealnearyou

import android.app.Dialog
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.*
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.google.gson.Gson
import com.letyouknow.R
import com.letyouknow.base.BaseFragment
import com.letyouknow.databinding.FragmentOneDealNearYouBinding
import com.letyouknow.model.*
import com.letyouknow.retrofit.viewmodel.*
import com.letyouknow.utils.AppGlobal
import com.letyouknow.utils.AppGlobal.Companion.setSpinnerTextColor
import com.letyouknow.view.dashboard.MainActivity
import com.letyouknow.view.home.dealsummery.DealSummeryFragment
import com.letyouknow.view.spinneradapter.*
import com.pionymessenger.utils.Constant
import kotlinx.android.synthetic.main.dialog_vehicle_packages.*
import kotlinx.android.synthetic.main.fragment_one_deal_near_you.*

class OneDealNearYouFragment : BaseFragment(), View.OnClickListener,
    AdapterView.OnItemSelectedListener {
    private var isValidSpinner = false
    private var isValidZipCode = false

    private lateinit var vehicleYearModel: VehicleYearViewModel
    private lateinit var vehicleMakeModel: VehicleMakeViewModel
    private lateinit var vehicleModelModel: VehicleModelViewModel
    private lateinit var vehicleTrimModel: VehicleTrimViewModel
    private lateinit var exteriorColorModel: ExteriorColorViewModel
    private lateinit var interiorColorModel: InteriorColorViewModel
    private lateinit var zipCodeModel: VehicleZipCodeViewModel
    private lateinit var packagesModel: VehiclePackagesViewModel

    private lateinit var adapterYear: YearSpinnerAdapter
    private lateinit var adapterMake: MakeSpinnerAdapter
    private lateinit var adapterModel: ModelSpinnerAdapter
    private lateinit var adapterTrim: TrimsSpinnerAdapter
    private lateinit var adapterExterior: ExteriorSpinnerAdapter
    private lateinit var adapterInterior: InteriorSpinnerAdapter
    private lateinit var adapterPackages: PackagesAdapter


    private var productId = "3"
    private var yearId = ""
    private var makeId = ""
    private var modelId = ""
    private var trimId = ""
    private var extColorId = ""
    private var intColorId = ""
    private var radiusId = ""

    private var arPackages = arrayListOf("PACKAGES")
    private var arOptionalAccessories = arrayListOf("OPTIONAL & ACCESSORIES")

    private lateinit var binding: FragmentOneDealNearYouBinding
    private var upDownData = UpDownData()
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
        binding.upDownData = upDownData
        vehicleYearModel = ViewModelProvider(this).get(VehicleYearViewModel::class.java)
        vehicleMakeModel = ViewModelProvider(this).get(VehicleMakeViewModel::class.java)
        vehicleModelModel = ViewModelProvider(this).get(VehicleModelViewModel::class.java)
        vehicleTrimModel = ViewModelProvider(this).get(VehicleTrimViewModel::class.java)
        exteriorColorModel = ViewModelProvider(this).get(ExteriorColorViewModel::class.java)
        interiorColorModel = ViewModelProvider(this).get(InteriorColorViewModel::class.java)
        zipCodeModel = ViewModelProvider(this).get(VehicleZipCodeViewModel::class.java)
        packagesModel = ViewModelProvider(this).get(VehiclePackagesViewModel::class.java)

        setYear()
        setMake()
        setModel()
        setTrim()
        setExteriorColor()
        setInteriorColor()
        setPackages()
        setOptions()

        btnSearch.setOnClickListener(this)
        MainActivity.getInstance().setVisibleEditImg(false)
        MainActivity.getInstance().setVisibleLogoutImg(false)


        onChangeZipCode()
    }

    private fun onChangeZipCode() {
        edtZipCode.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val str = s.toString()
                if (str.length == 5) {
                    callVehicleZipCodeAPI(str)
                } else if (str.length < 5) {
                    tvErrorZipCode.visibility = View.GONE
                    edtZipCode.setBackgroundResource(R.drawable.bg_edittext_dark)
                }
            }

            override fun afterTextChanged(s: Editable?) {

            }

        })
    }

    private fun setYear() {
        val arData = ArrayList<VehicleYearData>()
        val yearData = VehicleYearData()
        yearData.year = "YEAR"
        arData.add(0, yearData)
        adapterYear = YearSpinnerAdapter(requireActivity(), arData)
        spYear.adapter = adapterYear
        AppGlobal.setSpinnerLayoutPos(0, spYear, requireActivity())

    }

    private fun setMake() {
        val arData = ArrayList<VehicleMakeData>()
        val makeData = VehicleMakeData()
        makeData.make = "MAKE"
        arData.add(0, makeData)
        adapterMake = MakeSpinnerAdapter(requireActivity(), arData)
        spMake.adapter = adapterMake
        AppGlobal.setSpinnerLayoutPos(0, spMake, requireActivity())
    }

    private fun setModel() {
        val arData = ArrayList<VehicleModelData>()
        val modelData = VehicleModelData()
        modelData.model = "MODEL"
        arData.add(0, modelData)
        adapterModel = ModelSpinnerAdapter(requireActivity(), arData)
        spModel.adapter = adapterModel
        AppGlobal.setSpinnerLayoutPos(0, spModel, requireActivity())

    }

    private fun setTrim() {
        val arData = ArrayList<VehicleTrimData>()
        val trimData = VehicleTrimData()
        trimData.trim = "TRIM"
        arData.add(0, trimData)
        adapterTrim = TrimsSpinnerAdapter(requireActivity(), arData)
        spTrim.adapter = adapterTrim
        AppGlobal.setSpinnerLayoutPos(0, spTrim, requireActivity())
    }

    private fun setExteriorColor() {
        val arData = ArrayList<ExteriorColorData>()
        val trimData = ExteriorColorData()
        trimData.exteriorColor = "EXTERIOR COLOR"
        arData.add(0, trimData)
        adapterExterior = ExteriorSpinnerAdapter(requireActivity(), arData)
        spExteriorColor.adapter = adapterExterior
        AppGlobal.setSpinnerLayoutPos(0, spExteriorColor, requireActivity())
    }

    private fun setInteriorColor() {
        val arData = ArrayList<InteriorColorData>()
        val interiorData = InteriorColorData()
        interiorData.interiorColor = "INTERIOR COLOR"
        arData.add(0, interiorData)
        adapterInterior = InteriorSpinnerAdapter(requireActivity(), arData)
        spInteriorColor.adapter = adapterInterior
        AppGlobal.setSpinnerLayoutPos(0, spInteriorColor, requireActivity())
    }

    private fun setPackages() {
        tvPackages.text = "PACKAGES"
        tvPackages.setOnClickListener(this)
    }

    private fun setOptions() {
        val adapterOptions = ArrayAdapter<String?>(
            requireActivity(),
            android.R.layout.simple_spinner_item,
            arOptionalAccessories as List<String?>
        )
        adapterOptions.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spOptionalAccessories.adapter = adapterOptions
        setSpinnerTextColor(spOptionalAccessories, requireActivity())
    }

    private fun callVehicleZipCodeAPI(zipCode: String?) {
        if (Constant.isOnline(requireActivity())) {
            Constant.showLoader(requireActivity())
            zipCodeModel.getZipCode(requireActivity(), zipCode)!!
                .observe(requireActivity(), Observer { data ->
                    Constant.dismissLoader()
                    Log.e("ZipCode Data", Gson().toJson(data))
                    if (!data) {
                        edtZipCode.setBackgroundResource(R.drawable.bg_edittext_dark_error)
                        tvErrorZipCode.visibility = View.VISIBLE
                        setYear()
                        setMake()
                        setModel()
                        setTrim()
                        setExteriorColor()
                        setInteriorColor()
                        setPackages()
                        setOptions()
                    } else {
                        callVehicleYearAPI()
                    }
                    isValidZipCode = data
                    if (isValidZipCode && isValidSpinner) {
                        btnSearch.isEnabled = true
                    }
                }
                )
        } else {
            Toast.makeText(requireActivity(), Constant.noInternet, Toast.LENGTH_SHORT).show()
        }
    }

    private fun callVehicleYearAPI() {
        if (Constant.isOnline(requireActivity())) {
            Constant.showLoader(requireActivity())
            vehicleYearModel.getYear(requireActivity(), productId)!!
                .observe(requireActivity(), Observer { data ->
                    Constant.dismissLoader()
                    Log.e("Year Data", Gson().toJson(data))
                    if (data != null || data?.size!! > 0) {
                        val yearData = VehicleYearData()
                        yearData.year = "YEAR"
                        data.add(0, yearData)
                        adapterYear = YearSpinnerAdapter(requireActivity(), data)
                        spYear.adapter = adapterYear
                        spYear.onItemSelectedListener = this
                    } else {
                        val arData = ArrayList<VehicleYearData>()
                        val yearData = VehicleYearData()
                        yearData.year = "YEAR"
                        arData.add(0, yearData)
                        adapterYear = YearSpinnerAdapter(requireActivity(), arData)
                        spYear.adapter = adapterYear
                        spYear.onItemSelectedListener = this

                    }

                }
                )
        } else {
            Toast.makeText(requireActivity(), Constant.noInternet, Toast.LENGTH_SHORT).show()
        }
    }

    private fun callVehicleMakeAPI() {
        if (Constant.isOnline(requireActivity())) {
            Constant.showLoader(requireActivity())
            vehicleMakeModel.getMake(requireActivity(), productId, yearId)!!
                .observe(requireActivity(), Observer { data ->
                    Constant.dismissLoader()
                    Log.e("Make Data", Gson().toJson(data))
                    if (data != null || data?.size!! > 0) {
                        val makeData = VehicleMakeData()
                        makeData.make = "MAKE"
                        data.add(0, makeData)
                        adapterMake = MakeSpinnerAdapter(requireActivity(), data)
                        spMake.adapter = adapterMake
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
                }
                )
        } else {
            Toast.makeText(requireActivity(), Constant.noInternet, Toast.LENGTH_SHORT).show()
        }
    }

    private fun callVehicleModelAPI() {
        if (Constant.isOnline(requireActivity())) {
            Constant.showLoader(requireActivity())
            vehicleModelModel.getModel(requireActivity(), productId, yearId, makeId)!!
                .observe(requireActivity(), Observer { data ->
                    Constant.dismissLoader()
                    Log.e("Make Data", Gson().toJson(data))
                    if (data != null || data?.size!! > 0) {
                        val modelData = VehicleModelData()
                        modelData.model = "MODEL"
                        data.add(0, modelData)
                        adapterModel = ModelSpinnerAdapter(requireActivity(), data)
                        spModel.adapter = adapterModel
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
                }
                )
        } else {
            Toast.makeText(requireActivity(), Constant.noInternet, Toast.LENGTH_SHORT).show()
        }
    }

    private fun callVehicleTrimAPI() {
        if (Constant.isOnline(requireActivity())) {
            Constant.showLoader(requireActivity())
            vehicleTrimModel.getTrim(requireActivity(), productId, yearId, makeId, modelId)!!
                .observe(requireActivity(), Observer { data ->
                    Constant.dismissLoader()
                    Log.e("Make Data", Gson().toJson(data))
                    if (data != null || data?.size!! > 0) {
                        val trimData = VehicleTrimData()
                        trimData.trim = "TRIM"
                        data.add(0, trimData)
                        adapterTrim = TrimsSpinnerAdapter(requireActivity(), data)
                        spTrim.adapter = adapterTrim
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
                }
                )
        } else {
            Toast.makeText(requireActivity(), Constant.noInternet, Toast.LENGTH_SHORT).show()
        }
    }

    private fun callExteriorColorAPI() {
        if (Constant.isOnline(requireActivity())) {
            Constant.showLoader(requireActivity())
            exteriorColorModel.getExteriorColor(
                requireActivity(),
                productId,
                yearId,
                makeId,
                modelId,
                trimId
            )!!
                .observe(requireActivity(), Observer { data ->
                    Constant.dismissLoader()
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
                }
                )
        } else {
            Toast.makeText(requireActivity(), Constant.noInternet, Toast.LENGTH_SHORT).show()
        }
    }

    private fun callInteriorColorAPI() {
        if (Constant.isOnline(requireActivity())) {
            Constant.showLoader(requireActivity())
            interiorColorModel.getInteriorColor(
                requireActivity(),
                productId,
                yearId,
                makeId,
                modelId,
                trimId,
                extColorId
            )!!
                .observe(requireActivity(), Observer { data ->
                    Constant.dismissLoader()
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
                intColorId
            )!!
                .observe(requireActivity(), Observer { data ->
                    Constant.dismissLoader()
                    Log.e("Make Data", Gson().toJson(data))
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
                }
                )
        } else {
            Toast.makeText(requireActivity(), Constant.noInternet, Toast.LENGTH_SHORT).show()
        }
    }

    private fun loadFragment(fragment: Fragment, title: String) {
        val transaction = activity?.supportFragmentManager?.beginTransaction()
        transaction?.replace(R.id.flContainer, fragment)
//        transaction?.addToBackStack(null)
        transaction?.commit()
        MainActivity.getInstance().setTitle(title)
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.btnSearch -> {
                loadFragment(DealSummeryFragment(), getString(R.string.one_deal_near_you))
            }
            R.id.tvPackages -> {
                loadFragment(DealSummeryFragment(), getString(R.string.one_deal_near_you))
            }
            R.id.llPackages -> {
                val pos = v.tag as Int

                if (lastSelectPackage != -1) {
                    val data = adapterPackages.getItem(lastSelectPackage)
                    data.isSelect = false
                    adapterPackages.update(lastSelectPackage, data)
                }

                val data = adapterPackages.getItem(pos)
                data.isSelect = true
                adapterPackages.update(pos, data)

                lastSelectPackage = pos
                tvPackages.text = data.packageName
            }
        }
    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        when (parent?.id) {
            R.id.spYear -> {
                val data = adapterYear.getItem(position) as VehicleYearData
                yearId = data.vehicleYearID!!
                if (data.year != "YEAR") {
                    callVehicleMakeAPI()
                    setModel()
                    setTrim()
                    setExteriorColor()
                    setInteriorColor()
                }
                AppGlobal.setSpinnerLayoutPos(position, spYear, requireActivity())
                isValidSpinner = false
                btnSearch.isEnabled = false
            }
            R.id.spMake -> {
                val data = adapterMake.getItem(position) as VehicleMakeData
                makeId = data.vehicleMakeID!!
                AppGlobal.setSpinnerLayoutPos(position, spMake, requireActivity())
                if (data.make != "MAKE") {
                    callVehicleModelAPI()
                    setTrim()
                    setExteriorColor()
                    setInteriorColor()
                }
                isValidSpinner = false
                btnSearch.isEnabled = false
            }
            R.id.spModel -> {
                val data = adapterModel.getItem(position) as VehicleModelData
                modelId = data.vehicleModelID!!
                AppGlobal.setSpinnerLayoutPos(position, spModel, requireActivity())
                if (data.model != "MODEL") {
                    callVehicleTrimAPI()
                    setExteriorColor()
                    setInteriorColor()
                }
                isValidSpinner = false
                btnSearch.isEnabled = false
            }
            R.id.spTrim -> {
                val data = adapterTrim.getItem(position) as VehicleTrimData
                trimId = data.vehicleTrimID!!
                AppGlobal.setSpinnerLayoutPos(position, spTrim, requireActivity())
                if (data.trim != "TRIM") {
                    callExteriorColorAPI()
                    setInteriorColor()
                }
                isValidSpinner = false
                btnSearch.isEnabled = false
            }
            R.id.spExteriorColor -> {
                val data = adapterExterior.getItem(position) as ExteriorColorData
//                extColorId = "0"
                extColorId = data.vehicleExteriorColorID!!
                AppGlobal.setSpinnerLayoutPos(position, spExteriorColor, requireActivity())
                if (data.exteriorColor != "EXTERIOR COLOR") {
                    callInteriorColorAPI()
                }
                isValidSpinner = false
                btnSearch.isEnabled = false
            }
            R.id.spInteriorColor -> {
                val data = adapterInterior.getItem(position) as InteriorColorData
                intColorId = data.vehicleInteriorColorID!!
                AppGlobal.setSpinnerLayoutPos(position, spInteriorColor, requireActivity())
                if (data.interiorColor != "INTERIOR COLOR") callVehiclePackagesAPI()
                isValidSpinner = false
                btnSearch.isEnabled = true
            }
        }
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {

    }

    private lateinit var dialogPackage: Dialog
    private var lastSelectPackage = -1
    private fun popupPackages(data: ArrayList<VehiclePackagesData>) {
        dialogPackage = Dialog(requireActivity(), R.style.FullScreenDialog)
        dialogPackage.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialogPackage.setCancelable(true)
        dialogPackage.setCanceledOnTouchOutside(true)
        dialogPackage.setContentView(R.layout.dialog_vehicle_packages)
        dialogPackage.run {
            adapterPackages =
                PackagesAdapter(R.layout.list_item_packages, this@OneDealNearYouFragment)
            rvPackages.adapter = adapterPackages
            adapterPackages.addAll(data)
        }
        setLayoutParam(dialogPackage)
        dialogPackage.show()
    }

    private fun setLayoutParam(dialog: Dialog) {
        val layoutParams: WindowManager.LayoutParams = dialog.window?.attributes!!
        dialog.window?.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.MATCH_PARENT
        )
        dialog.window?.attributes = layoutParams
    }
}