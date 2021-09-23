package com.letyouknow.view.home

import android.os.Bundle
import android.text.Editable
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
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.google.gson.Gson
import com.letyouknow.R
import com.letyouknow.base.BaseFragment
import com.letyouknow.databinding.FragmentHomeBinding
import com.letyouknow.model.*
import com.letyouknow.retrofit.viewmodel.*
import com.letyouknow.utils.AppGlobal.Companion.setSpinnerLayoutPos
import com.letyouknow.view.dashboard.MainActivity
import com.letyouknow.view.home.dealsummery.DealSummeryFragment
import com.letyouknow.view.spinneradapter.*
import com.pionymessenger.utils.Constant
import kotlinx.android.synthetic.main.fragment_home.*


class HomeFragment : BaseFragment(), View.OnClickListener, AdapterView.OnItemSelectedListener {
    private var arRadius = arrayListOf(
        "RADIUS",
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

    private var isValidSpinner = false
    private var isValidZipCode = false

    private lateinit var vehicleYearModel: VehicleYearViewModel
    private lateinit var vehicleMakeModel: VehicleMakeViewModel
    private lateinit var vehicleModelModel: VehicleModelViewModel
    private lateinit var vehicleTrimModel: VehicleTrimViewModel
    private lateinit var exteriorColorModel: ExteriorColorViewModel
    private lateinit var interiorColorModel: InteriorColorViewModel
    private lateinit var zipCodeModel: VehicleZipCodeViewModel

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
    private var extColorId = ""
    private var intColorId = ""
    private var radiusId = ""

    private lateinit var animBlink: Animation
    private lateinit var animSlideRightToLeft: Animation
    private lateinit var animSlideLeftToRight: Animation


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

        setYear()
        setMake()
        setModel()
        setTrim()
        setExteriorColor()
        setInteriorColor()
        setRadius()
        btnProceedDeal.setOnClickListener(this)
        tvPromo.setOnClickListener(this)
        ivClosePromo.setOnClickListener(this)
        MainActivity.getInstance().setVisibleEditImg(false)
        MainActivity.getInstance().setVisibleLogoutImg(false)
        callVehicleYearAPI()
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
        setSpinnerLayoutPos(0, spYear, requireActivity())

    }

    private fun setMake() {
        val arData = ArrayList<VehicleMakeData>()
        val makeData = VehicleMakeData()
        makeData.make = "MAKE"
        arData.add(0, makeData)
        adapterMake = MakeSpinnerAdapter(requireActivity(), arData)
        spMake.adapter = adapterMake
        setSpinnerLayoutPos(0, spMake, requireActivity())
    }

    private fun setModel() {
        val arData = ArrayList<VehicleModelData>()
        val modelData = VehicleModelData()
        modelData.model = "MODEL"
        arData.add(0, modelData)
        adapterModel = ModelSpinnerAdapter(requireActivity(), arData)
        spModel.adapter = adapterModel
        setSpinnerLayoutPos(0, spModel, requireActivity())

    }

    private fun setTrim() {
        val arData = ArrayList<VehicleTrimData>()
        val trimData = VehicleTrimData()
        trimData.trim = "TRIM"
        arData.add(0, trimData)
        adapterTrim = TrimsSpinnerAdapter(requireActivity(), arData)
        spTrim.adapter = adapterTrim
        setSpinnerLayoutPos(0, spTrim, requireActivity())
    }

    private fun setExteriorColor() {
        val arData = ArrayList<ExteriorColorData>()
        val trimData = ExteriorColorData()
        trimData.exteriorColor = "EXTERIOR COLOR"
        arData.add(0, trimData)
        adapterExterior = ExteriorSpinnerAdapter(requireActivity(), arData)
        spExteriorColor.adapter = adapterExterior
        setSpinnerLayoutPos(0, spExteriorColor, requireActivity())
    }

    private fun setInteriorColor() {
        val arData = ArrayList<InteriorColorData>()
        val interiorData = InteriorColorData()
        interiorData.interiorColor = "INTERIOR COLOR"
        arData.add(0, interiorData)
        adapterInterior = InteriorSpinnerAdapter(requireActivity(), arData)
        spInteriorColor.adapter = adapterInterior
        setSpinnerLayoutPos(0, spInteriorColor, requireActivity())
    }

    private fun setRadius() {
        val arData = ArrayList<String>()
        arData.add(0, "RADIUS")
        adapterRadius = RadiusSpinnerAdapter(requireActivity(), arData)
        spRadius.adapter = adapterRadius
        setSpinnerLayoutPos(0, spRadius, requireActivity())
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
            R.id.btnProceedDeal -> {
                loadFragment(DealSummeryFragment(), getString(R.string.search_deals_title))
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
            Constant.showLoader(requireActivity())
            zipCodeModel.getZipCode(requireActivity(), zipCode)!!
                .observe(requireActivity(), Observer { data ->
                    Constant.dismissLoader()
                    Log.e("ZipCode Data", Gson().toJson(data))
                    if (!data) {
                        /* Toast.makeText(
                             requireActivity(),
                             getString(R.string.invalid_zip_code),
                             Toast.LENGTH_SHORT
                         ).show()*/
                        edtZipCode.setBackgroundResource(R.drawable.bg_edittext_dark_error)
                        tvErrorZipCode.visibility = View.VISIBLE
                    }
                    isValidZipCode = data
                    if (isValidZipCode && isValidSpinner) {
                        btnProceedDeal.isEnabled = true
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

    private fun callRadiusAPI() {
        adapterRadius = RadiusSpinnerAdapter(requireActivity(), arRadius)
        spRadius.adapter = adapterRadius
        spRadius.onItemSelectedListener = this
//        setSpinnerLayoutPos(0, spRadius, requireActivity())
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
                    setRadius()
                }
                setSpinnerLayoutPos(position, spYear, requireActivity())
                isValidSpinner = false
                btnProceedDeal.isEnabled = false
            }
            R.id.spMake -> {
                val data = adapterMake.getItem(position) as VehicleMakeData
                makeId = data.vehicleMakeID!!
                setSpinnerLayoutPos(position, spMake, requireActivity())
                if (data.make != "MAKE") {
                    callVehicleModelAPI()
                    setTrim()
                    setExteriorColor()
                    setInteriorColor()
                    setRadius()
                }
                isValidSpinner = false
                btnProceedDeal.isEnabled = false
            }
            R.id.spModel -> {
                val data = adapterModel.getItem(position) as VehicleModelData
                modelId = data.vehicleModelID!!
                setSpinnerLayoutPos(position, spModel, requireActivity())
                if (data.model != "MODEL") {
                    callVehicleTrimAPI()
                    setExteriorColor()
                    setInteriorColor()
                    setRadius()
                }
                isValidSpinner = false
                btnProceedDeal.isEnabled = false
            }
            R.id.spTrim -> {
                val data = adapterTrim.getItem(position) as VehicleTrimData
                trimId = data.vehicleTrimID!!
                setSpinnerLayoutPos(position, spTrim, requireActivity())
                if (data.trim != "TRIM") {
                    callExteriorColorAPI()
                    setInteriorColor()
                    setRadius()
                }
                isValidSpinner = false
                btnProceedDeal.isEnabled = false
            }
            R.id.spExteriorColor -> {
                val data = adapterExterior.getItem(position) as ExteriorColorData
//                extColorId = "0"
                extColorId = data.vehicleExteriorColorID!!
                setSpinnerLayoutPos(position, spExteriorColor, requireActivity())
                if (data.exteriorColor != "EXTERIOR COLOR") {
                    callInteriorColorAPI()
                    setRadius()
                }
                isValidSpinner = false
                btnProceedDeal.isEnabled = false
            }
            R.id.spInteriorColor -> {
                val data = adapterInterior.getItem(position) as InteriorColorData
                intColorId = data.vehicleInteriorColorID!!
                setSpinnerLayoutPos(position, spInteriorColor, requireActivity())
                if (data.interiorColor != "INTERIOR COLOR") callRadiusAPI()
                isValidSpinner = false
                btnProceedDeal.isEnabled = false
            }
            R.id.spRadius -> {
                val data = adapterRadius.getItem(position) as String
                radiusId = data
                setSpinnerLayoutPos(position, spRadius, requireActivity())
                isValidSpinner = data != "RADIUS"

                if (isValidZipCode && isValidSpinner) {
                    btnProceedDeal.isEnabled = true
                }
            }
        }
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {

    }

}