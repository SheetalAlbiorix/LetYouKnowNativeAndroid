package com.letyouknow.view.search

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.databinding.DataBindingUtil
import com.letyouknow.R
import com.letyouknow.base.BaseFragment
import com.letyouknow.databinding.FragmentSearchBinding
import com.letyouknow.model.UpDownData
import kotlinx.android.synthetic.main.fragment_search.*

class SearchFragment : BaseFragment(), View.OnClickListener {
    private var arYear = arrayListOf("Year")
    private var arMake = arrayListOf("Make")
    private var arModel = arrayListOf("Model")
    private var arTrim = arrayListOf("Trim")
    private var arSearchRadius = arrayListOf("Search Radius")
    private var arExteriorColor = arrayListOf("ExteriorColor")
    private var arInteriorColor = arrayListOf("InteriorColor")

    private val upDownData = UpDownData()
    private lateinit var binding: FragmentSearchBinding
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_search,
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
        setYear()
        setMake()
        setModel()
        setTrim()
        setSearchRadius()
        setExteriorColor()
        setInteriorColor()
        llCarOptions.setOnClickListener(this)
        llLocation.setOnClickListener(this)
        llColors.setOnClickListener(this)
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

    private fun setSearchRadius() {
        val adapterSearchRadius = ArrayAdapter<String?>(
            activity!!,
            android.R.layout.simple_spinner_item,
            arSearchRadius as List<String?>
        )
        adapterSearchRadius.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spRadius.adapter = adapterSearchRadius
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

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.llCarOptions -> {
                upDownData.isCarOptions = !upDownData.isCarOptions!!
                binding.upDownData = upDownData
            }
            R.id.llLocation -> {
                upDownData.isLocation = !upDownData.isLocation!!
                binding.upDownData = upDownData
            }
            R.id.llColors -> {
                upDownData.isColors = !upDownData.isColors!!
                binding.upDownData = upDownData
            }
        }
    }
}