package com.letyouknow.view.lyk

import android.content.Context
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupWindow
import androidx.lifecycle.ViewModelProvider
import com.letyouknow.R
import com.letyouknow.base.BaseFragment
import com.letyouknow.model.VehicleYearData
import com.letyouknow.retrofit.viewmodel.*
import com.letyouknow.view.dropdown.YearAdapter
import kotlinx.android.synthetic.main.fragment_lyk1.*
import kotlinx.android.synthetic.main.popup_dialog.view.*


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
        }
    }

    private fun callVehicleYearAPI(view: View?) {
        lyK1ViewModel.liveDataYear.observe(viewLifecycleOwner) {
            showAlertFilter(view, it)
        }
        lyK1ViewModel.getYear(
            productId,
            "",
            view
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

    private lateinit var adapterYear: YearAdapter

    private fun showAlertFilter(v: View?, list: MutableList<VehicleYearData>) {
        val inflater =
            requireActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val view = inflater.inflate(R.layout.popup_dialog, v as ViewGroup, true)

        val popup = PopupWindow(
            view,
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )

        adapterYear = YearAdapter(R.layout.list_item_year, this)
        view?.rvPopup?.adapter = adapterYear
        adapterYear.addAll(ArrayList(list))

        return popup.showAsDropDown(v, 100, -10, Gravity.CENTER)
    }
}