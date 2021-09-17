package com.letyouknow.view.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.letyouknow.R
import com.letyouknow.base.BaseFragment
import com.letyouknow.databinding.FragmentHomeBinding
import com.letyouknow.model.UpDownData
import com.letyouknow.utils.AppGlobal.Companion.setTextColor
import com.letyouknow.view.dashboard.MainActivity
import com.letyouknow.view.home.dealsummery.DealSummeryFragment
import kotlinx.android.synthetic.main.fragment_home.*


class HomeFragment : BaseFragment(), View.OnClickListener {
    private var arYear = arrayListOf("YEAR")
    private var arMake = arrayListOf("MAKE")
    private var arModel = arrayListOf("MODEL")
    private var arTrim = arrayListOf("TRIM")
    private var arExteriorColor = arrayListOf("EXTERIOR COLOR")
    private var arInteriorColor = arrayListOf("INTERIOR COLOR")
    private var arRadius = arrayListOf("RADIUS ")

    private lateinit var binding: FragmentHomeBinding
    private var upDownData = UpDownData()
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
        binding.upDownData = upDownData
        setYear()
        setMake()
        setModel()
        setTrim()
        setExteriorColor()
        setInteriorColor()
        setRadius()
        btnProceedDeal.setOnClickListener(this)
        MainActivity.getInstance().setVisibleEditImg(false)
    }

    private fun setYear() {
        val adapterYear = ArrayAdapter<String?>(
            activity!!,
            android.R.layout.simple_spinner_item,
            arYear as List<String?>
        )
        adapterYear.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spYear.adapter = adapterYear
        setTextColor(spYear, requireActivity())

    }

    private fun setMake() {
        val adapterMake = ArrayAdapter<String?>(
            activity!!,
            android.R.layout.simple_spinner_item,
            arMake as List<String?>
        )
        adapterMake.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spMake.adapter = adapterMake
        setTextColor(spMake, requireActivity())
    }

    private fun setModel() {
        val adapterModel = ArrayAdapter<String?>(
            activity!!,
            android.R.layout.simple_spinner_item,
            arModel as List<String?>
        )
        adapterModel.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spModel.adapter = adapterModel
        setTextColor(spModel, requireActivity())
    }

    private fun setTrim() {
        val adapterTrim = ArrayAdapter<String?>(
            activity!!,
            android.R.layout.simple_spinner_item,
            arTrim as List<String?>
        )
        adapterTrim.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spTrim.adapter = adapterTrim
        setTextColor(spTrim, requireActivity())
    }



    private fun setExteriorColor() {
        val adapterExteriorColor = ArrayAdapter<String?>(
            activity!!,
            android.R.layout.simple_spinner_item,
            arExteriorColor as List<String?>
        )
        adapterExteriorColor.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spExteriorColor.adapter = adapterExteriorColor
        setTextColor(spExteriorColor, requireActivity())
    }

    private fun setInteriorColor() {
        val adapterInteriorColor = ArrayAdapter<String?>(
            activity!!,
            android.R.layout.simple_spinner_item,
            arInteriorColor as List<String?>
        )
        adapterInteriorColor.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spInteriorColor.adapter = adapterInteriorColor
        setTextColor(spInteriorColor, requireActivity())
    }

    private fun setRadius() {
        val adapterRadius = ArrayAdapter<String?>(
            activity!!,
            android.R.layout.simple_spinner_item,
            arRadius as List<String?>
        )
        adapterRadius.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spRadius.adapter = adapterRadius
        setTextColor(spRadius, requireActivity())
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
                loadFragment(DealSummeryFragment(), getString(R.string.lighting_car_deals))
            }

        }
    }
}