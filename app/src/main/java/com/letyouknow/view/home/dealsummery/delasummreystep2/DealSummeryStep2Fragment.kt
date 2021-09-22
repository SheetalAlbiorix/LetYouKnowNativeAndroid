package com.letyouknow.view.home.dealsummery.delasummreystep2

import android.os.Bundle
import android.os.CountDownTimer
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.google.gson.Gson
import com.letyouknow.R
import com.letyouknow.base.BaseFragment
import com.letyouknow.databinding.FragmentDealSummeryStep2Binding
import com.letyouknow.model.CardListData
import com.letyouknow.model.LightDealBindData
import com.letyouknow.utils.CreditCardNumberTextWatcher
import com.letyouknow.utils.CreditCardType
import com.letyouknow.view.dashboard.MainActivity
import com.letyouknow.view.signup.CardListAdapter
import kotlinx.android.synthetic.main.fragment_deal_summery_step2.*
import kotlinx.android.synthetic.main.layout_deal_summery_step2.*

class DealSummeryStep2Fragment : BaseFragment(), View.OnClickListener {
    lateinit var binding: FragmentDealSummeryStep2Binding
    private lateinit var adapterCardList: CardListAdapter
    private var selectCardPos = -1
    private var selectPaymentType = 0
    private var arCardList: ArrayList<CardListData> = ArrayList()
    private var lightBindData: LightDealBindData = LightDealBindData()
    private lateinit var cTimer: CountDownTimer
    private var seconds = 10

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_deal_summery_step2,
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
        val textWatcher: TextWatcher = CreditCardNumberTextWatcher(edtCardNumber)
        edtCardNumber.addTextChangedListener(textWatcher)

        initCardAdapter()
        llDebitCreditCard.setOnClickListener(this)
        llPayPal.setOnClickListener(this)
        llBankAccount.setOnClickListener(this)
        tvHavePromoCode.setOnClickListener(this)
        tvAddMore.setOnClickListener(this)
        btnSave.setOnClickListener(this)
        ivBackDeal.setOnClickListener(this)
        btnProceedDeal.setOnClickListener(this)
        tvAddMin.setOnClickListener(this)

        setOnChange()
        startTimer()
    }

    private fun startTimer() {
        cTimer = object : CountDownTimer(((seconds * 1000)).toLong(), 1000) {
            override fun onTick(millisUntilFinished: Long) {
                seconds -= 1
                tvTimer.text = (String.format("%02d", seconds / 60)
                        + ":" + String.format("%02d", seconds % 60))
                if (seconds == 0) {
                    cancelTimer()
                    return
                }
            }

            override fun onFinish() {

            }
        }.start()
    }

    private fun cancelTimer() {
        if (cTimer != null)
            cTimer.cancel()
    }

    override fun onDestroy() {
        cancelTimer()
        super.onDestroy()
    }

    override fun onPause() {
        super.onPause()
        cancelTimer()
    }

    override fun onStop() {
        super.onStop()
        cancelTimer()
    }

    private fun setOnChange() {
        edtExpiresDate.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

            override fun afterTextChanged(s: Editable?) {
                val inputLength = edtExpiresDate.text.toString().length
                if (inputLength == 2) {
                    edtExpiresDate.setText(edtExpiresDate.text.toString().trim() + "/")
                    edtExpiresDate.setSelection(edtExpiresDate.text.toString().length)
                }
                if (inputLength == 5) {
                    edtCVV.requestFocus()
                }
            }
        })
    }

    private fun initCardAdapter() {
        adapterCardList = CardListAdapter(R.layout.list_item_card, this)
        rvCard.adapter = adapterCardList

        if (pref?.getCardList()!!.size != 0) {
            llCardList.visibility = View.VISIBLE
            arCardList = pref?.getCardList()!!
            adapterCardList.addAll(arCardList)
            llCardViewDetail.visibility = View.GONE
            for (i in 0 until arCardList.size) {
                if (arCardList[i].isSelect!!) {
                    selectCardPos = i
                }
            }
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
                lightBindData.selectPaymentType = selectPaymentType
                binding.lightDealBindData = lightBindData
            }
            R.id.llPayPal -> {
                selectPaymentType = 2
                lightBindData.selectPaymentType = selectPaymentType
                binding.lightDealBindData = lightBindData
            }
            R.id.llBankAccount -> {
                selectPaymentType = 3
                lightBindData.selectPaymentType = selectPaymentType
                binding.lightDealBindData = lightBindData
            }
            R.id.tvHavePromoCode -> {
                lightBindData.isShowPromo = !lightBindData.isShowPromo!!
                binding.lightDealBindData = lightBindData
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
            R.id.ivBackDeal -> {
                MainActivity.getInstance().onBackPressed()
            }
            R.id.btnProceedDeal -> {
                MainActivity.getInstance().onBackPressed()
            }
            R.id.tvAddMin -> {
                seconds += 120;
                //seconds = ((60 * ((2 + minutes) + (second / 60))).toDouble())
                tvAddMin.visibility = View.GONE
                cancelTimer()
                startTimer()
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