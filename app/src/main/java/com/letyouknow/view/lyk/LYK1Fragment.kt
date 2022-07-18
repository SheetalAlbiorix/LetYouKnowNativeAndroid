package com.letyouknow.view.lyk

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.Observer
import com.google.gson.Gson
import com.letyouknow.R
import com.letyouknow.base.BaseFragment
import com.letyouknow.retrofit.viewmodel.*
import com.letyouknow.utils.Constant
import kotlinx.android.synthetic.main.fragment_lyk1.*

class LYK1Fragment : BaseFragment(), View.OnClickListener {

    private var productId = "1"
    private var yearId = ""
    private var makeId = ""
    private var modelId = ""
    private var trimId = ""
    private var extColorId = ""
    private var intColorId = ""

    private var yearStr = getString(R.string.year_new_cars_title)
    private var makeStr = getString(R.string.make_title)
    private var modelStr = getString(R.string.model_title)
    private var trimStr = getString(R.string.trim_title)
    private var extColorStr = getString(R.string.exterior_color)
    private var intColorStr = getString(R.string.interior_color)

    private lateinit var vehicleYearModel: VehicleYearViewModel
    private lateinit var vehicleMakeModel: VehicleMakeViewModel
    private lateinit var vehicleModelModel: VehicleModelViewModel
    private lateinit var vehicleTrimModel: VehicleTrimViewModel
    private lateinit var exteriorColorModel: ExteriorColorViewModel
    private lateinit var interiorColorModel: InteriorColorViewModel

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
        setOnClickEvent()
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
                callVehicleYearAPI()
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

    private fun callVehicleYearAPI() {
        try {

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
                        Constant.dismissLoader()
                        try {
                            Log.e("Year Data", Gson().toJson(data))
                            if (!data.isNullOrEmpty()) {

                            } else {

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
}