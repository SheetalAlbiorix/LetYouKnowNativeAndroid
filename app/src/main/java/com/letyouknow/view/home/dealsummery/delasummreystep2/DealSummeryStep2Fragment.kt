package com.letyouknow.view.home.dealsummery.delasummreystep2

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.google.gson.Gson
import com.letyouknow.R
import com.letyouknow.base.BaseFragment
import com.letyouknow.databinding.FragmentDealSummeryStep2Binding
import com.letyouknow.model.CardListData
import com.letyouknow.utils.CreditCardType
import com.letyouknow.view.signup.CardListAdapter
import kotlinx.android.synthetic.main.layout_deal_summery_step2.*

class DealSummeryStep2Fragment : BaseFragment(), View.OnClickListener {
    lateinit var binding: FragmentDealSummeryStep2Binding
    private lateinit var adapterCardList: CardListAdapter
    private var selectCardPos = -1
    private var selectPaymentType = 0
    private var arCardList: ArrayList<CardListData> = ArrayList()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_deal_summery_step2,
            container,
            false
        )
        binding.selectPaymentType = selectPaymentType
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init()

    }

    private fun init() {
        initCardAdapter()
        llDebitCreditCard.setOnClickListener(this)
        llPayPal.setOnClickListener(this)
        llBankAccount.setOnClickListener(this)
        tvAddMore.setOnClickListener(this)
        btnSave.setOnClickListener(this)
    }

    private fun initCardAdapter() {
        adapterCardList = CardListAdapter(R.layout.list_item_card, this)
        rvCard.adapter = adapterCardList

        if (pref?.getCardList()!!.size != 0) {
            llCardList.visibility = View.VISIBLE
            arCardList = pref?.getCardList()!!
            adapterCardList.addAll(arCardList)
            llCardViewDetail.visibility = View.GONE
        } else {
            llCardList.visibility = View.GONE
            llCardViewDetail.visibility = View.VISIBLE
        }
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.cardMain -> {
                val pos = v.tag as Int
                if (selectCardPos != -1) {
                    val data = adapterCardList.getItem(selectCardPos)
                    data.isSelect = false
                    adapterCardList.update(selectCardPos, data)
                }

                val data = adapterCardList.getItem(pos)
                data.isSelect = true
                adapterCardList.update(pos, data)

                selectCardPos = pos;
            }
            R.id.llDebitCreditCard -> {
                selectPaymentType = 1
                binding.selectPaymentType = selectPaymentType
            }
            R.id.llPayPal -> {
                selectPaymentType = 2
                binding.selectPaymentType = selectPaymentType
            }
            R.id.llBankAccount -> {
                selectPaymentType = 3
                binding.selectPaymentType = selectPaymentType
            }
            R.id.tvAddMore -> {
                llCardViewDetail.visibility = View.VISIBLE
            }
            R.id.btnSave -> {
                arCardList.add(
                    CardListData(
                        getDetectedCreditCardImage(),
                        edtCardNumber.text.toString().trim(),
                        edtCardHolder.text.toString().trim(),
                        edtExpiresDate.text.toString().trim(),
                        edtCVV.text.toString().trim(),
                        false
                    )
                )
                adapterCardList.addAll(arCardList)
                pref?.setCardList(Gson().toJson(arCardList))
                llCardList.visibility = View.VISIBLE
                setClearData()
            }
        }
    }

    private fun setClearData() {
        edtCardNumber.setText("")
        edtCardHolder.setText("")
        edtExpiresDate.setText("")
        edtCVV.setText("")
        llCardViewDetail.visibility = View.GONE
    }

    private fun getDetectedCreditCardImage(): String {
        val type: CreditCardType = CreditCardType.detect(edtCardNumber.text.toString().trim())
        return if (type != null) {
            type.imageResourceName
        } else {
            "ic_camera"
        }
    }
}