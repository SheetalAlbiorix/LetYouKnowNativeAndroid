package com.letyouknow.view.signup


import android.app.Activity
import android.app.Dialog
import android.os.Bundle
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.google.gson.Gson
import com.letyouknow.R
import com.letyouknow.base.BaseActivity
import com.letyouknow.databinding.ActivitySignUpBinding
import com.letyouknow.model.CardListData
import com.letyouknow.retrofit.ApiConstant
import com.letyouknow.retrofit.viewmodel.SignUpViewModel
import com.letyouknow.view.privacypolicy.PrivacyPolicyTermsCondActivity
import com.pionymessenger.utils.Constant
import com.pionymessenger.utils.Constant.Companion.makeLinks
import com.pionymessenger.utils.Constant.Companion.onTextChange
import kotlinx.android.synthetic.main.activity_sign_up.*
import kotlinx.android.synthetic.main.layout_toolbar.toolbar
import org.jetbrains.anko.sdk27.coroutines.onCheckedChange
import org.jetbrains.anko.startActivity

class SignUpActivity : BaseActivity(), View.OnClickListener {
    private lateinit var adapterCardList: CardListAdapter
    private var selectCardPos = -1
    private var selectPaymentType = 0
    private var arCardList: ArrayList<CardListData> = ArrayList()
    lateinit var binding: ActivitySignUpBinding
    lateinit var signupViewModel: SignUpViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)

        binding =
            DataBindingUtil.setContentView(this, R.layout.activity_sign_up)
        binding.setSelectPaymentType(selectPaymentType)
        toolbar.setNavigationIcon(R.drawable.ic_back)
        toolbar.setTitleTextColor(resources.getColor(R.color.black))

        setSupportActionBar(toolbar)
        if (supportActionBar != null) {
            supportActionBar!!.setDisplayHomeAsUpEnabled(true);
            supportActionBar!!.setDisplayShowHomeEnabled(true);
        }
        init()
    }

    private fun init() {
        signupViewModel = ViewModelProvider(this).get(SignUpViewModel::class.java)
        llDebitCreditCard.setOnClickListener(this)
        llPayPal.setOnClickListener(this)
        llBankAccount.setOnClickListener(this)
        tvAddMore.setOnClickListener(this)
        btnSave.setOnClickListener(this)
        btnCreateAccount.setOnClickListener(this)
        ivPasswordInfo.setOnClickListener(this)

        initCardAdapter()
        setOnChange()
        setLink()
    }

    private fun setLink() {
        txtTerms.makeLinks(
            Pair("Terms and Conditions", View.OnClickListener {
                startActivity<PrivacyPolicyTermsCondActivity>(Constant.ARG_POLICY to Constant.TERMS_CONDITIONS_LINK)
            }),
            Pair("Privacy Policy", View.OnClickListener {
                startActivity<PrivacyPolicyTermsCondActivity>(Constant.ARG_POLICY to Constant.PRIVACY_POLICY_LINK)
            })
        )

        chkIsPayment.onCheckedChange { buttonView, isChecked ->
            if (isChecked)
                llPaymentOptions.visibility = View.VISIBLE
            else
                llPaymentOptions.visibility = View.GONE
        }
    }

    private fun setOnChange() {
        onTextChange(this, edtFirstName, tvErrorFirstName)
        onTextChange(this, edtLastName, tvErrorLastName)
        onTextChange(this, edtAddress1, tvErrorAddress1)
        onTextChange(this, edtAddress2, tvErrorAddress2)
        onTextChange(this, edtCity, tvErrorCity)
        onTextChange(this, edtState, tvErrorState)
        onTextChange(this, edtPhoneNumber, tvErrorPhoneNo)
        onTextChange(this, edtEmail, tvErrorEmailAddress)
        onTextChange(this, edtPassword, tvErrorPassword)
        onTextChange(this, edtConfirmPassword, tvErrorConfirmPassword)

        edtExpiresDate.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

            override fun afterTextChanged(s: Editable?) {
                val inputlength = edtExpiresDate.text.toString().length
                if (inputlength == 2) {
                    edtExpiresDate.setText(edtExpiresDate.text.toString().trim() + "/")
                    edtExpiresDate.setSelection(edtExpiresDate.text.toString().length)
                }
                if (inputlength == 5) {
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
        } else {
            llCardList.visibility = View.GONE
            llCardViewDetail.visibility = View.VISIBLE
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    override fun getViewActivity(): Activity {
        return this
    }

    override fun onNetworkStateChange(isConnect: Boolean) {
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
            R.id.ivPasswordInfo -> {
                popupPassword()
            }
            R.id.btnCreateAccount -> {
                setErrorVisible()
                if (isValid()) {
                    if (Constant.isOnline(this)) {
                        Constant.showLoader(this)
                        val request = HashMap<String, String>()
                        request[ApiConstant.middleName] = ""
                        request[ApiConstant.firstName] = edtFirstName.text.toString().trim()
                        request[ApiConstant.lastName] = edtLastName.text.toString().trim()
                        request[ApiConstant.email] = edtEmail.text.toString().trim()
                        request[ApiConstant.userName] = edtEmail.text.toString().trim()
                        request[ApiConstant.phoneNumber] = edtPhoneNumber.text.toString().trim()
                        request[ApiConstant.password] = edtPassword.text.toString().trim()

                        signupViewModel.createAccount(this, request)!!
                            .observe(this, Observer { signUpVo ->
                                Constant.dismissLoader()
                                Toast.makeText(this, signUpVo.message, Toast.LENGTH_SHORT).show()
//                                startActivity<MainActivity>()
                                finish()
                            }
                            )
                    } else {
                        Toast.makeText(this, Constant.noInternet, Toast.LENGTH_SHORT).show()
                    }
                }
            }
            R.id.btnSave -> {
                arCardList.add(
                    CardListData(
                        edtCardNumber.mCurrentDrawableResId,
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

    private fun setErrorVisible() {
        tvErrorFirstName.visibility = View.GONE
        tvErrorLastName.visibility = View.GONE
        tvErrorAddress1.visibility = View.GONE
        tvErrorAddress2.visibility = View.GONE
        tvErrorCity.visibility = View.GONE
        tvErrorState.visibility = View.GONE
        tvErrorPhoneNo.visibility = View.GONE
        tvErrorEmailAddress.visibility = View.GONE
        tvErrorPassword.visibility = View.GONE
        tvErrorConfirmPassword.visibility = View.GONE
    }

    private fun setClearData() {
        edtCardNumber.setText("")
        edtCardHolder.setText("")
        edtExpiresDate.setText("")
        edtCVV.setText("")
        llCardViewDetail.visibility = View.GONE
    }

    private fun popupPassword() {
        val dialog = Dialog(this, R.style.FullScreenDialog)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(true)
        dialog.setContentView(R.layout.dialog_password_hint)
        setLayoutParam(dialog)
        dialog.show()
    }

    private fun setLayoutParam(dialog: Dialog) {
        val layoutParams: WindowManager.LayoutParams = dialog.window?.attributes!!
        dialog.window?.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.MATCH_PARENT
        )
        dialog.window?.attributes = layoutParams
    }

    private fun isValid(): Boolean {
        when {
            TextUtils.isEmpty(edtFirstName.text.toString().trim()) -> {
                Constant.setErrorBorder(edtFirstName, tvErrorFirstName)
                return false
            }
            TextUtils.isEmpty(edtLastName.text.toString().trim()) -> {
                Constant.setErrorBorder(edtLastName, tvErrorLastName)
                return false
            }
            TextUtils.isEmpty(edtAddress1.text.toString().trim()) -> {
                Constant.setErrorBorder(edtAddress1, tvErrorAddress1)
                return false
            }
            TextUtils.isEmpty(edtAddress2.text.toString().trim()) -> {
                Constant.setErrorBorder(edtAddress2, tvErrorAddress2)
                return false
            }
            TextUtils.isEmpty(edtCity.text.toString().trim()) -> {
                Constant.setErrorBorder(edtCity, tvErrorCity)
                return false
            }
            TextUtils.isEmpty(edtState.text.toString().trim()) -> {
                Constant.setErrorBorder(edtState, tvErrorState)
                return false
            }
            TextUtils.isEmpty(edtZipCode.text.toString().trim()) -> {
                Constant.setErrorBorder(edtZipCode, tvErrorZipCode)
                return false
            }
            TextUtils.isEmpty(edtPhoneNumber.text.toString().trim()) -> {
                Constant.setErrorBorder(edtPhoneNumber, tvErrorPhoneNo)
                return false
            }
            TextUtils.isEmpty(edtEmail.text.toString().trim()) -> {
                tvErrorEmailAddress.text = getString(R.string.enter_email_address_vali)
                Constant.setErrorBorder(edtEmail, tvErrorEmailAddress)
                return false
            }
            !Constant.emailValidator(edtEmail.text.toString().trim()) -> {
                tvErrorEmailAddress.text = getString(R.string.enter_valid_email)
                Constant.setErrorBorder(edtEmail, tvErrorEmailAddress)
                return false
            }
            TextUtils.isEmpty(edtPassword.text.toString().trim()) -> {
                tvErrorPassword.text = getString(R.string.enter_password)
                Constant.setErrorBorder(edtPassword, tvErrorPassword)
                return false
            }
            !Constant.passwordValidator(edtPassword.text.toString().trim()) -> {
                tvErrorPassword.text = getString(R.string.enter_valid_password)
                Constant.setErrorBorder(edtPassword, tvErrorPassword)
                return false
            }
            TextUtils.isEmpty(edtConfirmPassword.text.toString().trim()) -> {
                tvErrorConfirmPassword.text = getString(R.string.enter_confirm_password)
                Constant.setErrorBorder(edtConfirmPassword, tvErrorConfirmPassword)
                return false
            }
            (edtPassword.text.toString().trim() != edtConfirmPassword.text.toString().trim()) -> {
                tvErrorConfirmPassword.text = getString(R.string.did_n_t_match_password)
                Constant.setErrorBorder(edtConfirmPassword, tvErrorConfirmPassword)
                return false
            }
            else -> return true
        }
    }

}