package com.letyouknow.view.lyk

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import com.letyouknow.R
import com.letyouknow.base.BaseFragment
import com.letyouknow.retrofit.viewmodel.*
import kotlinx.android.synthetic.main.fragment_lyk1.*


class LYK1Fragment : BaseFragment(), View.OnClickListener {

    private var productId = "1"
    private var yearId = ""
    private var makeId = ""
    private var modelId = ""
    private var trimId = ""
    private var extColorId = ""
    private var intColorId = ""

    private var yearStr = ""
    private var makeStr = ""
    private var modelStr = ""
    private var trimStr = ""
    private var extColorStr = ""
    private var intColorStr = ""

    private lateinit var vehicleYearModel: VehicleYearViewModel
    private lateinit var vehicleMakeModel: VehicleMakeViewModel
    private lateinit var vehicleModelModel: VehicleModelViewModel
    private lateinit var vehicleTrimModel: VehicleTrimViewModel
    private lateinit var exteriorColorModel: ExteriorColorViewModel
    private lateinit var interiorColorModel: InteriorColorViewModel
    private lateinit var lyK1ViewModel: LYK1ViewModel

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
        initData()
        initViewModel()
        setOnClickEvent()
    }

    private fun initData() {
        yearStr = getString(R.string.year_new_cars_title)
        makeStr = getString(R.string.make_title)
        modelStr = getString(R.string.model_title)
        trimStr = getString(R.string.trim_title)
        extColorStr = getString(R.string.exterior_color)
        intColorStr = getString(R.string.interior_color)
    }

    private fun initViewModel() {
        lyK1ViewModel = ViewModelProvider(this)[LYK1ViewModel::class.java]
        lyK1ViewModel.init(requireActivity(), this)
    }

    private fun setOnClickEvent() {
        tvYear.setOnClickListener(this)
        tvMake.setOnClickListener(this)
        tvModel.setOnClickListener(this)
        tvTrim.setOnClickListener(this)
        tvExterior.setOnClickListener(this)
        tvInterior.setOnClickListener(this)
        tvPackages.setOnClickListener(this)
        tvOptionalAccessories.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.tvYear -> {
                callVehicleYearAPI(v)
            }
            R.id.tvMake -> {

            }
            R.id.tvModel -> {

            }
            R.id.tvTrim -> {

            }
            R.id.tvExterior -> {

            }
            R.id.tvInterior -> {

            }
            R.id.tvPackages -> {

            }
            R.id.tvOptionalAccessories -> {

            }
            R.id.tvTitleYear -> {
                val pos = v.tag as Int
                val data = lyK1ViewModel.adapterYear.getItem(pos)
                data.let {
                    tvYear.text = it.year
                    yearStr = it.year!!
                    yearId = it.vehicleYearID!!
                }
                lyK1ViewModel.run {
                    isShowYear = false
                    lyK1ViewModel.popupYear.dismiss()
                    lyK1ViewModel.liveDataYear.value?.clear()
                }

            }
        }
    }

    private fun callVehicleYearAPI(view: View?) {
        if (lyK1ViewModel.isShowYear) {
            lyK1ViewModel.liveDataYear.observe(viewLifecycleOwner) {
                if (lyK1ViewModel.liveDataYear.value != null)
                    lyK1ViewModel.showYearPopUp(view, it)
            }
        }
        lyK1ViewModel.getYear(
            productId,
            "",
        )
    }

    private fun isValid(): Boolean {
        when {
            yearStr == getString(R.string.year_new_cars_title) -> {
                tvErrorYear.visibility = View.VISIBLE
                return false
            }
            makeStr == getString(R.string.make_title) -> {
                tvErrorMake.visibility = View.VISIBLE
                return false
            }
            modelStr == getString(R.string.model_title) -> {
                tvErrorModel.visibility = View.VISIBLE
                return false
            }
            trimStr == getString(R.string.trim_title) -> {
                tvErrorTrim.visibility = View.VISIBLE
                return false
            }
            extColorStr == getString(R.string.exterior_color_title) -> {
                tvErrorExterior.visibility = View.VISIBLE
                return false
            }
            intColorStr == getString(R.string.interior_color_title) -> {
                tvErrorInterior.visibility = View.VISIBLE
                return false
            }
            tvPackages.text.toString().trim() == getString(R.string.packages_title) -> {
                tvErrorPackages.visibility = View.VISIBLE
                return false
            }
            tvOptionalAccessories.text.toString()
                .trim() == getString(R.string.options_accessories_title) -> {
                tvErrorOptionsAccessories.visibility = View.VISIBLE
                return false
            }
        }
        return true
    }
}