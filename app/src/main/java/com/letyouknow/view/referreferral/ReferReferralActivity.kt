package com.letyouknow.view.referreferral

import android.app.Activity
import android.app.Dialog
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.AdapterView
import android.widget.CompoundButton
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.letyouknow.R
import com.letyouknow.base.BaseActivity
import com.letyouknow.retrofit.ApiConstant
import com.letyouknow.retrofit.viewmodel.CurrentReferralPostViewModel
import com.letyouknow.retrofit.viewmodel.CurrentReferralViewModel
import com.letyouknow.utils.AppGlobal
import com.letyouknow.utils.AppGlobal.Companion.getTimeZoneOffset
import com.letyouknow.utils.Constant
import com.letyouknow.utils.Constant.Companion.LYK100Dollars
import com.letyouknow.utils.Constant.Companion.makeLinks
import com.letyouknow.view.spinneradapter.StateSpinnerAdapter
import kotlinx.android.synthetic.main.activity_refer_referral.*
import kotlinx.android.synthetic.main.dialog_referral_info.*
import kotlinx.android.synthetic.main.layout_toolbar_blue.*


class ReferReferralActivity : BaseActivity(), View.OnClickListener,
    CompoundButton.OnCheckedChangeListener, AdapterView.OnItemSelectedListener {

    private lateinit var adapterState: StateSpinnerAdapter
    private var state = ""
    private lateinit var currentReferralViewModel: CurrentReferralViewModel
    private lateinit var currentReferralPostViewModel: CurrentReferralPostViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_refer_referral)
        init()
    }

    private fun init() {
        currentReferralViewModel = ViewModelProvider(this)[CurrentReferralViewModel::class.java]
        currentReferralPostViewModel =
            ViewModelProvider(this)[CurrentReferralPostViewModel::class.java]
        tvTitleTool.visibility = View.GONE
        ivBack.setOnClickListener(this)
        ivCopy.setOnClickListener(this)
        ivShare.setOnClickListener(this)
        rb100LYK.setOnCheckedChangeListener(this)
        rb25Check.setOnCheckedChangeListener(this)
        setTermsLink()
//        tvLink.text = getString(R.string.link,"aaaaaa")
//        callReferralAPI()
        callReferralPostAPI()
    }

    private fun setTermsLink() {
        txtTerms.makeLinks(
            Pair("Terms and Conditions", View.OnClickListener {
//                startActivity<PrivacyPolicyTermsCondActivity>(ARG_POLICY to TERMS_CONDITIONS_LINK)
                AppGlobal.dialogWebView(this, Constant.TERMS_CONDITIONS_LINK)
            }),

            )
    }

    override fun getViewActivity(): Activity? {
        return this
    }

    override fun onNetworkStateChange(isConnect: Boolean) {

    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.ivBack -> {
                onBackPressed()
            }
            R.id.ivCopy -> {
                copyContent()
            }
            R.id.ivShare -> {
                shareContent()
            }
        }
    }

    private fun copyContent() {
        val clipboard: ClipboardManager = getSystemService(CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText("label", tvLink.text.toString().trim())
        clipboard.setPrimaryClip(clip)
        Toast.makeText(this@ReferReferralActivity, "Copy to Clipboard", Toast.LENGTH_LONG).show()
    }

    private fun shareContent() {
        val sendIntent: Intent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, tvLink.text.toString().trim())
            type = "text/plain"
        }
        val shareIntent = Intent.createChooser(sendIntent, null)
        startActivity(shareIntent)
    }

    private lateinit var dialogReferralInfo: Dialog
    private fun popupReferral() {
        dialogReferralInfo = Dialog(this, R.style.FullScreenDialog)
        dialogReferralInfo.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialogReferralInfo.setCancelable(true)
        dialogReferralInfo.setCanceledOnTouchOutside(true)
        dialogReferralInfo.setContentView(R.layout.dialog_referral_info)
        setState()
        dialogReferralInfo.run {
            tvReferConfirm.setOnClickListener {
                dismiss()
            }
            tvReferCancel.setOnClickListener {
                dismiss()
            }
        }
        setLayoutParam(dialogReferralInfo)
        dialogReferralInfo.show()
    }

    private fun setState() {
        adapterState = StateSpinnerAdapter(
            this,
            AppGlobal.arState
        )
        dialogReferralInfo.spState.adapter = adapterState
        dialogReferralInfo.spState.onItemSelectedListener = this
        for (i in 0 until AppGlobal.arState.size) {
            /*if (AppGlobal.arState[i] == userData?.state) {
                dialogReferralInfo.spState.setSelection(i)
            }*/
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
            request[ApiConstant.ReferralRewardType] = LYK100Dollars
            request[ApiConstant.timeZoneOffset] = getTimeZoneOffset()

            currentReferralViewModel.getReferral(this, request)!!.observe(this, Observer { data ->
                Constant.dismissLoader()

                tvLink.text = getString(R.string.link, data.inviteCode)
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
            request[ApiConstant.ReferralRewardType] = LYK100Dollars
            request[ApiConstant.timeZoneOffset] = getTimeZoneOffset()

            currentReferralPostViewModel.getReferral(this, request)!!
                .observe(this, Observer { data ->
                    Constant.dismissLoader()
                    if (TextUtils.isEmpty(data.inviteCode)) {
                        callReferralAPI()
                    } else {
                        tvLink.text = getString(R.string.link, data.inviteCode)
                    }
                }
                )
        } else {
            Toast.makeText(this, Constant.noInternet, Toast.LENGTH_SHORT).show()
        }
    }


    private fun setLayoutParam(dialog: Dialog) {
        val layoutParams: WindowManager.LayoutParams = dialog.window?.attributes!!
        dialog.window?.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.MATCH_PARENT
        )
        dialog.window?.attributes = layoutParams
    }

    override fun onCheckedChanged(buttonView: CompoundButton?, isChecked: Boolean) {
        when (buttonView?.id) {
            R.id.rb100LYK -> {
                if (isChecked) {

                }
            }
            R.id.rb25Check -> {
                if (isChecked) {
                    popupReferral()
                } else {

                }
            }
        }
    }

    override fun onItemSelected(v: AdapterView<*>?, view: View?, position: Int, id: Long) {
        when (v?.id) {
            R.id.spState -> {
                val data = adapterState.getItem(position) as String
                state = data
            }
        }
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {
    }


}