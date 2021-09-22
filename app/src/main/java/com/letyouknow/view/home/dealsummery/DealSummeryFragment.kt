package com.letyouknow.view.home.dealsummery

import android.content.res.ColorStateList
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.core.widget.ImageViewCompat
import androidx.fragment.app.Fragment
import com.letyouknow.R
import com.letyouknow.base.BaseFragment
import com.letyouknow.utils.AppGlobal.Companion.setSpinnerTextColor
import com.letyouknow.utils.AppGlobal.Companion.setSpinnerTextColorPos
import com.letyouknow.view.dashboard.MainActivity
import com.letyouknow.view.home.dealsummery.delasummreystep2.DealSummeryStep2Fragment
import com.pionymessenger.utils.Constant.Companion.makeLinks
import kotlinx.android.synthetic.main.fragment_deal_summery.*
import kotlinx.android.synthetic.main.layout_deal_summery.*

class DealSummeryFragment : BaseFragment(), View.OnClickListener,
    AdapterView.OnItemSelectedListener {
    private var arLoan = arrayListOf("LOAN", "CARD")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_deal_summery, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init()
    }

    private fun init() {
        txtTerms.text =
            resources.getString(R.string.i_have_read_accept, resources.getString(R.string.app_name))
        setLoan()
        setInfoLink()
        setPrivacyPolicyLink()

        ImageViewCompat.setImageTintList(
            ivInteriorColor,
            ColorStateList.valueOf(resources.getColor(R.color.textLightGrey))
        )

        btnProceedDeal.setOnClickListener(this)
        ivBackDeal.setOnClickListener(this)
        ivForwardDeal.setOnClickListener(this)
        MainActivity.getInstance().setVisibleEditImg(true)
    }

    private fun setLoan() {
        val adapterLoan = ArrayAdapter<String?>(
            requireActivity(),
            android.R.layout.simple_spinner_item,
            arLoan as List<String?>
        )
        adapterLoan.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spLoan.adapter = adapterLoan
        setSpinnerTextColor(spLoan, requireActivity())
        spLoan.onItemSelectedListener = this
    }

    private fun setInfoLink() {
        txtInformation.makeLinks(
            Pair("information here", View.OnClickListener {
                Toast.makeText(requireActivity(), "Information Clicked", Toast.LENGTH_SHORT)
                    .show()
            })
        )
    }

    private fun setPrivacyPolicyLink() {

        txtTerms.makeLinks(
            Pair("Terms and Conditions", View.OnClickListener {
                Toast.makeText(requireActivity(), "Terms of Service Clicked", Toast.LENGTH_SHORT)
                    .show()
            }),
            Pair("Privacy Policy", View.OnClickListener {
                Toast.makeText(requireActivity(), "Privacy Policy Clicked", Toast.LENGTH_SHORT)
                    .show()
            })
        )
    }

    private fun loadFragment(fragment: Fragment) {
        val transaction = activity?.supportFragmentManager?.beginTransaction()
        transaction?.replace(R.id.flContainer, fragment)
//        transaction?.addToBackStack(null)
        transaction?.commit()
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.btnProceedDeal -> {
                loadFragment(DealSummeryStep2Fragment())
            }
            R.id.ivBackDeal -> {
                requireActivity().onBackPressed()
            }
            R.id.ivForwardDeal -> {
                loadFragment(DealSummeryStep2Fragment())
            }
        }
    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {

        when (parent?.id) {
            R.id.spLoan -> {
                setSpinnerTextColorPos(position, spLoan, requireActivity())
            }
        }
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {

    }

}