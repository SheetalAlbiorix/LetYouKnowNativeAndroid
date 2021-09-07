package com.letyouknow.view.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import com.letyouknow.R
import com.letyouknow.base.BaseFragment
import com.letyouknow.view.home.dealsummery.DealSummeryFragment
import kotlinx.android.synthetic.main.fragment_home.*


class HomeFragment : BaseFragment(), View.OnClickListener {
    private var arYear = arrayListOf("Year")
    private var arMake = arrayListOf("Make")
    private var arModel = arrayListOf("Model")
    private var arTrim = arrayListOf("Trim")
    private var arOptionalPackages = arrayListOf("OptionalPackages")
    private var arExteriorColor = arrayListOf("ExteriorColor")
    private var arInteriorColor = arrayListOf("InteriorColor")
    private var arDealer = arrayListOf("Dealer Installed Accessories")

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init()
    }

    private fun init() {
        setYear()
        setMake()
        setModel()
        setTrim()
        setOptionalPackages()
        setExteriorColor()
        setInteriorColor()
        setDealer()
        btnProceedDeal.setOnClickListener(this)
    }

    private fun setYear() {
        val adapterYear = ArrayAdapter<String?>(
            activity!!,
            android.R.layout.simple_spinner_item,
            arYear as List<String?>
        )
        adapterYear.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spYear.adapter = adapterYear
    }

    private fun setMake() {
        val adapterMake = ArrayAdapter<String?>(
            activity!!,
            android.R.layout.simple_spinner_item,
            arMake as List<String?>
        )
        adapterMake.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spMake.adapter = adapterMake
    }

    private fun setModel() {
        val adapterModel = ArrayAdapter<String?>(
            activity!!,
            android.R.layout.simple_spinner_item,
            arModel as List<String?>
        )
        adapterModel.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spModel.adapter = adapterModel
    }

    private fun setTrim() {
        val adapterTrim = ArrayAdapter<String?>(
            activity!!,
            android.R.layout.simple_spinner_item,
            arTrim as List<String?>
        )
        adapterTrim.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spTrim.adapter = adapterTrim
    }

    private fun setOptionalPackages() {
        val adapterOptionalPackages = ArrayAdapter<String?>(
            activity!!,
            android.R.layout.simple_spinner_item,
            arOptionalPackages as List<String?>
        )
        adapterOptionalPackages.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spOptionalPackages.adapter = adapterOptionalPackages
    }

    private fun setExteriorColor() {
        val adapterExteriorColor = ArrayAdapter<String?>(
            activity!!,
            android.R.layout.simple_spinner_item,
            arExteriorColor as List<String?>
        )
        adapterExteriorColor.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spExteriorColor.adapter = adapterExteriorColor
    }

    private fun setInteriorColor() {
        val adapterInteriorColor = ArrayAdapter<String?>(
            activity!!,
            android.R.layout.simple_spinner_item,
            arInteriorColor as List<String?>
        )
        adapterInteriorColor.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spInteriorColor.adapter = adapterInteriorColor
    }

    private fun setDealer() {
        val adapterDealer = ArrayAdapter<String?>(
            activity!!,
            android.R.layout.simple_spinner_item,
            arDealer as List<String?>
        )
        adapterDealer.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spDealer.adapter = adapterDealer
    }

    private fun loadFragment(fragment: Fragment) {
        val transaction = activity!!.supportFragmentManager.beginTransaction()
        transaction.replace(R.id.flContainer, fragment)
        transaction.addToBackStack(null)
        transaction.commit()
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.btnProceedDeal -> {
                loadFragment(DealSummeryFragment())
            }
        }
    }
}