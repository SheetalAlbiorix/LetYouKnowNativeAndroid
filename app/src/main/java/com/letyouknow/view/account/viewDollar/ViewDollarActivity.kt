package com.letyouknow.view.account.viewDollar

import android.app.Activity
import android.os.Bundle
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.view.View
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.letyouknow.R
import com.letyouknow.base.BaseActivity
import com.letyouknow.databinding.ActivityViewDollarBinding
import com.letyouknow.model.ReferralProgramDTO
import com.letyouknow.retrofit.ApiConstant
import com.letyouknow.retrofit.viewmodel.CurrentReferralPostViewModel
import com.letyouknow.retrofit.viewmodel.CurrentReferralViewModel
import com.letyouknow.utils.AppGlobal
import com.letyouknow.utils.Constant
import com.letyouknow.view.dashboard.MainActivity
import com.letyouknow.view.transaction_history.TransactionCodeDetailActivity
import kotlinx.android.synthetic.main.activity_view_dollar.*
import kotlinx.android.synthetic.main.layout_toolbar_blue.*
import org.jetbrains.anko.clearTask
import org.jetbrains.anko.intentFor
import org.jetbrains.anko.newTask
import org.jetbrains.anko.startActivity
import java.text.NumberFormat
import java.util.*

class ViewDollarActivity : BaseActivity(), View.OnClickListener {
    private lateinit var binding: ActivityViewDollarBinding
    private lateinit var viewActivityAdapter: ViewActivityAdapter
    private lateinit var currentReferralViewModel: CurrentReferralViewModel
    private lateinit var currentReferralPostViewModel: CurrentReferralPostViewModel
    private var arActivity: ArrayList<ReferralProgramDTO> = ArrayList()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_dollar)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_view_dollar)
        init()
    }

    private fun init() {
//        backButton()
        currentReferralViewModel = ViewModelProvider(this)[CurrentReferralViewModel::class.java]
        currentReferralPostViewModel =
            ViewModelProvider(this)[CurrentReferralPostViewModel::class.java]
        ivBack.setOnClickListener(this)
        btnFindYourCar.setOnClickListener(this)
        callReferralPostAPI()
        setActivityAdapter()
        setFilterData()
    }

    private fun setFilterData() {
        edtFilter.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val str = s.toString()
                if (str.length > 0) {
                    val arData: ArrayList<ReferralProgramDTO> = ArrayList()
                    for (i in 0 until arActivity.size) {
                        val data = arActivity[i]

                        if ((!TextUtils.isEmpty(data.transactionCode) && (data.transactionCode?.toLowerCase())!!.contains(
                                str.toLowerCase()
                            )) ||
                            ((data.value.toString()).contains(str) ||
                                    (data.timeStampFormatted.toString()
                                        .toLowerCase()).contains(str.toLowerCase()) ||
                                    (data.userIdentifier?.toLowerCase())!!.contains(str.toLowerCase()) ||
                                    (data.referralProgramType?.toLowerCase())!!.contains(str.toLowerCase()))
                        ) {
                            arData.add(data)
                        }

                    }

                    if (arData.size > 0) {
                        viewActivityAdapter.addAll(arData)
                    } else {
                        viewActivityAdapter.addAll(ArrayList())
                    }

                } else {
                    viewActivityAdapter.addAll(arActivity)
                }
            }

            override fun afterTextChanged(s: Editable?) {
            }

        })
    }

    private fun setActivityAdapter() {
        viewActivityAdapter = ViewActivityAdapter(R.layout.list_item_dollar_activity, this)
        rvActivity.adapter = viewActivityAdapter
    }


    override fun getViewActivity(): Activity {
        return this
    }

    override fun onNetworkStateChange(isConnect: Boolean) {

    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.btnFindYourCar -> {
                startActivity(
                    intentFor<MainActivity>().clearTask().newTask()
                )
            }
            R.id.ivBack -> {
                onBackPressed()
            }
            R.id.llActivity -> {
                val pos = v.tag as Int
                val data = viewActivityAdapter.getItem(pos)
                if (data.type == "Debit") {
                    startActivity<TransactionCodeDetailActivity>(Constant.ARG_TRANSACTION_CODE to data.transactionCode)
                }
            }
        }
    }

    private fun callReferralAPI() {
        if (Constant.isOnline(this)) {
            if (!Constant.isInitProgress()) {
                Constant.showLoader(this)
            } else if (Constant.isInitProgress() && !Constant.progress.isShowing) {
                Constant.showLoader(this)
            }
            val request = HashMap<String, Any>()
            request[ApiConstant.ReferralRewardType] = Constant.LYK100Dollars
            request[ApiConstant.timeZoneOffset] = AppGlobal.getTimeZoneOffset()

            currentReferralViewModel.getReferral(this, request)!!.observe(this, { data ->
                Constant.dismissLoader()

                tvBalance.text =
                    NumberFormat.getCurrencyInstance(Locale.US).format(data.balance)
                tvEarned.text =
                    NumberFormat.getCurrencyInstance(Locale.US).format(data.totalEarned)
                val arData: ArrayList<ReferralProgramDTO> = ArrayList()

                if (!data.referralProgramTransactionHistoryDTO!!.isNullOrEmpty()) {

                    for (i in 0 until data.referralProgramTransactionHistoryDTO!!.size) {
                        val dataReferral = data.referralProgramTransactionHistoryDTO[i]
                        if (dataReferral.referralProgramType == "Check25") {
                            dataReferral.referralType = "Mailed"
                        } else {
                            dataReferral.referralType = "Credited"
                        }

                        if (dataReferral.userIdentifier == "System LetYouKnow") {
                            dataReferral.identifier = "LetYouKnow"
                        } else {
                            dataReferral.identifier = dataReferral.userIdentifier
                        }

                        arData.add(dataReferral)
                    }
                }
//                arActivity = arData
                arActivity = data.referralProgramTransactionHistoryDTO!!
                viewActivityAdapter.addAll(arActivity)

            }
            )
        } else {
            Toast.makeText(this, Constant.noInternet, Toast.LENGTH_SHORT).show()
        }
    }

    private fun callReferralPostAPI() {
        if (Constant.isOnline(this)) {
            if (!Constant.isInitProgress()) {
                Constant.showLoader(this)
            } else if (Constant.isInitProgress() && !Constant.progress.isShowing) {
                Constant.showLoader(this)
            }
            val request = HashMap<String, Any>()
            request[ApiConstant.ReferralRewardType] = Constant.LYK100Dollars
            request[ApiConstant.timeZoneOffset] = AppGlobal.getTimeZoneOffset()

            currentReferralPostViewModel.getReferral(this, request)!!
                .observe(this, Observer { data ->
                    if (TextUtils.isEmpty(data.inviteCode)) {
                        callReferralAPI()
                    } else {
                        Constant.dismissLoader()
                        tvBalance.text =
                            NumberFormat.getCurrencyInstance(Locale.US).format(data.balance)
                        tvEarned.text =
                            NumberFormat.getCurrencyInstance(Locale.US).format(data.totalEarned)
                        val arData: ArrayList<ReferralProgramDTO> = ArrayList()

                        if (!data.referralProgramTransactionHistoryDTO!!.isNullOrEmpty()) {

                            for (i in 0 until data.referralProgramTransactionHistoryDTO!!.size) {
                                val dataReferral = data.referralProgramTransactionHistoryDTO[i]
                                if (dataReferral.referralProgramType == "Check25") {
                                    dataReferral.referralType = "Mailed"
                                } else {
                                    dataReferral.referralType = "Credited"
                                }

                                if (dataReferral.userIdentifier == "System LetYouKnow") {
                                    dataReferral.identifier = "LetYouKnow"
                                } else {
                                    dataReferral.identifier = dataReferral.userIdentifier
                                }

                                arData.add(dataReferral)
                            }
                        }
//                arActivity = arData
                        arActivity = data.referralProgramTransactionHistoryDTO!!
                        viewActivityAdapter.addAll(arActivity)
                    }
                }
                )
        } else {
            Toast.makeText(this, Constant.noInternet, Toast.LENGTH_SHORT).show()
        }
    }
}