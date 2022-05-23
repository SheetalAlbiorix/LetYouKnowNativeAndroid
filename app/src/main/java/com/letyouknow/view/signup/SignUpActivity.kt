package com.letyouknow.view.signup


import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.text.*
import android.util.Log
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.facebook.AccessToken
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FacebookAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.gson.Gson
import com.letyouknow.R
import com.letyouknow.base.BaseActivity
import com.letyouknow.databinding.ActivitySignUpBinding
import com.letyouknow.model.CardListData
import com.letyouknow.retrofit.ApiConstant
import com.letyouknow.retrofit.viewmodel.*
import com.letyouknow.utils.AppGlobal
import com.letyouknow.utils.Constant
import com.letyouknow.utils.Constant.Companion.makeLinks
import com.letyouknow.utils.Constant.Companion.onTextChange
import com.letyouknow.utils.CreditCardType
import com.letyouknow.view.dashboard.MainActivity
import kotlinx.android.synthetic.main.activity_sign_up.*
import kotlinx.android.synthetic.main.dialog_password_hint.*
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
    lateinit var socialMobileViewModel: SocialMobileViewModel

    lateinit var fbLoginViewModel: FacebookLoginViewModel
    lateinit var googleLoginViewModel: GoogleLoginViewModel
    lateinit var pushTokenViewModel: AddPushTokenViewModel
    private var arState = arrayListOf("State")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)

        binding =
            DataBindingUtil.setContentView(this, R.layout.activity_sign_up)
        binding.selectPaymentType = selectPaymentType
        toolbar.setNavigationIcon(R.drawable.ic_back)
        toolbar.setTitleTextColor(resources.getColor(R.color.black))

        setSupportActionBar(toolbar)
        if (supportActionBar != null) {
            supportActionBar!!.setDisplayHomeAsUpEnabled(true)
            supportActionBar!!.setDisplayShowHomeEnabled(true)
        }
        init()
    }

    private fun init() {
        /* val textWatcher: TextWatcher = CreditCardNumberTextWatcher(edtCardNumber)
         edtCardNumber.addTextChangedListener(textWatcher)
 */
        pushTokenViewModel = ViewModelProvider(this)[AddPushTokenViewModel::class.java]
        signupViewModel = ViewModelProvider(this)[SignUpViewModel::class.java]
        fbLoginViewModel = ViewModelProvider(this)[FacebookLoginViewModel::class.java]
        googleLoginViewModel = ViewModelProvider(this)[GoogleLoginViewModel::class.java]
        socialMobileViewModel = ViewModelProvider(this)[SocialMobileViewModel::class.java]
        llDebitCreditCard.setOnClickListener(this)
        llPayPal.setOnClickListener(this)
        llBankAccount.setOnClickListener(this)
        tvAddMore.setOnClickListener(this)
        btnSave.setOnClickListener(this)
        btnCreateAccount.setOnClickListener(this)
        ivPasswordInfo.setOnClickListener(this)
        ivGoogle.setOnClickListener(this)

        initCardAdapter()
        setOnChange()
        setLink()

        //Google & facebook Sign In
        firebaseAuth()
        googleInit()
        facebookInit()
        setState()
        edtZipCode.inputType = InputType.TYPE_CLASS_NUMBER
        edtPhoneNumber.filters = arrayOf<InputFilter>(filter, InputFilter.LengthFilter(13))
    }

    private fun setState() {
        val adapterYear = ArrayAdapter<String?>(
            applicationContext,
            android.R.layout.simple_spinner_item,
            arState as List<String?>
        )
        adapterYear.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spState.adapter = adapterYear
    }

    private fun setLink() {
        txtTerms.makeLinks(
            Pair("Terms and Conditions", View.OnClickListener {
//                startActivity<PrivacyPolicyTermsCondActivity>(Constant.ARG_POLICY to Constant.TERMS_CONDITIONS_LINK)
                AppGlobal.dialogWebView(this, Constant.TERMS_CONDITIONS_LINK)
            }),
            Pair("Privacy Policy", View.OnClickListener {
//                startActivity<PrivacyPolicyTermsCondActivity>(Constant.ARG_POLICY to Constant.PRIVACY_POLICY_LINK)
                AppGlobal.dialogWebView(this, Constant.PRIVACY_POLICY_LINK)
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
        Constant.onTextChangeFirstName(this, edtFirstName, tvErrorFirstName)
        Constant.onTextChangeLastName(this, edtLastName, tvErrorLastName)
        onTextChange(this, edtAddress1, tvErrorAddress1)
        onTextChange(this, edtAddress2, tvErrorAddress2)
        Constant.onTextChangeCity(this, edtCity, tvErrorCity)
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
                        if (!Constant.isInitProgress()) {
                            Constant.showLoader(this)
                        } else if (Constant.isInitProgress() && !Constant.progress.isShowing) {
                            Constant.showLoader(this)
                        }
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
            R.id.ivGoogle -> {
                googleSignIn()
            }
        }
    }

    private fun getDetectedCreditCardImage(): String {
        val type: CreditCardType = CreditCardType.detect(edtCardNumber.text.toString().trim())
        return if (type != null) {
            type.imageResourceName
        } else {
            "ic_camera"
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
        dialog.setCanceledOnTouchOutside(true)
        dialog.setContentView(R.layout.dialog_password_hint)
        dialog.run {
            ivClose.setOnClickListener {
                dismiss()
            }
        }
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
                tvErrorFirstName.text = getString(R.string.first_name_required)
                return false
            }
            (Constant.firstNameValidator(edtFirstName.text.toString().trim())) -> {
                Constant.setErrorBorder(edtFirstName, tvErrorFirstName)
                tvErrorFirstName.text = getString(R.string.enter_valid_first_name)
                return false
            }
            TextUtils.isEmpty(edtLastName.text.toString().trim()) -> {
                Constant.setErrorBorder(edtLastName, tvErrorLastName)
                tvErrorLastName.text = getString(R.string.last_name_required)
                return false
            }
            (Constant.lastNameValidator(edtLastName.text.toString().trim())) -> {
                Constant.setErrorBorder(edtLastName, tvErrorLastName)
                tvErrorLastName.text = getString(R.string.enter_valid_last_name)
                return false
            }
            /* TextUtils.isEmpty(edtAddress1.text.toString().trim()) -> {
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

             TextUtils.isEmpty(edtZipCode.text.toString().trim()) -> {
                 Constant.setErrorBorder(edtZipCode, tvErrorZipCode)
                 return false
             }*/
            TextUtils.isEmpty(edtPhoneNumber.text.toString().trim()) -> {
                Constant.setErrorBorder(edtPhoneNumber, tvErrorPhoneNo)
                tvErrorPhoneNo.text = getString(R.string.enter_phonenumber)
                return false
            }
            (edtPhoneNumber.text.toString().length != 13) -> {
                Constant.setErrorBorder(edtPhoneNumber, tvErrorPhoneNo)
                tvErrorPhoneNo.text = getString(R.string.enter_valid_phone_number)
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

    //Google

    private lateinit var mGoogleSignInClient: GoogleSignInClient
    private lateinit var auth: FirebaseAuth
    private lateinit var callbackManager: CallbackManager
    private val GOOGLE_SIGN_IN = 101

    private fun firebaseAuth() {
        auth = FirebaseAuth.getInstance();
    }

    private fun googleInit() {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.google_web_client_id)).requestEmail()
            .build()
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso)
    }

    private fun googleSignIn() {
        val signInIntent: Intent = mGoogleSignInClient.getSignInIntent()
        startActivityForResult(signInIntent, GOOGLE_SIGN_IN)
    }

    //Facebook
    private fun facebookInit() {
        ivFacebook.setBackgroundResource(R.drawable.ic_facebook)
        ivFacebook.text = ""
        ivFacebook.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null)
        callbackManager = CallbackManager.Factory.create()
        ivFacebook.setReadPermissions("email", "public_profile")

        ivFacebook.registerCallback(callbackManager, object : FacebookCallback<LoginResult> {
            override fun onSuccess(loginResult: LoginResult) {
                Log.d(TAG, "facebook:onSuccess:$loginResult")
                handleFacebookAccessToken(loginResult.accessToken)
            }

            override fun onCancel() {
                Log.d(TAG, "facebook:onCancel")
            }

            override fun onError(error: FacebookException) {
                Log.d(TAG, "facebook:onError", error)
            }
        })
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        callbackManager.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == GOOGLE_SIGN_IN) {
                val task: Task<GoogleSignInAccount> =
                    GoogleSignIn.getSignedInAccountFromIntent(data)
                try {
                    // Google Sign In was successful, authenticate with Firebase
                    val account: GoogleSignInAccount = task.getResult(ApiException::class.java)
                    Log.d(TAG, "firebaseAuthWithGoogle:" + account.id)
                    account.idToken?.let { firebaseAuthWithGoogle(it) }
                } catch (e: ApiException) {
                    // Google Sign In failed, update UI appropriately
                    Log.w(TAG, "Google sign in failed", e)
                }
            }


        }
    }

    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(TAG, "signInWithCredential:success")
                    val user = auth.currentUser
                    Log.e("UserName : ", user?.displayName!!)
                    /*Toast.makeText(applicationContext, "Login Successfully", Toast.LENGTH_SHORT)
                        .show()*/
                    // updateUI(user)
                    callGoogleLoginAPI(idToken)
                    googleSignOut()
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w(TAG, "signInWithCredential:failure", task.exception)
                    //  updateUI(null)
                }
            }
    }

    private fun handleFacebookAccessToken(token: AccessToken) {
        Log.d(TAG, "handleFacebookAccessToken:$token")
        val credential = FacebookAuthProvider.getCredential(token.token)
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(TAG, "signInWithCredential:success")
                    val user = auth.currentUser
                    Log.e("UserName FB : ", user?.displayName!!)
//                    Toast.makeText(applicationContext, "Login Successfully", Toast.LENGTH_SHORT)
//                        .show()
                    callFBLoginAPI(token.token)
                    facebookSignOut()
                    //updateUI(user)
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w(TAG, "signInWithCredential:failure", task.exception)
                    Toast.makeText(
                        baseContext, "Authentication failed.",
                        Toast.LENGTH_SHORT
                    ).show()
                    facebookSignOut()
                    //updateUI(null)
                }
            }

    }

    private fun googleSignOut() {
        mGoogleSignInClient.signOut()
        auth.signOut()
    }

    private fun facebookSignOut() {
        LoginManager.getInstance().logOut()
        auth.signOut()
    }

    var filter = InputFilter { source, start, end, dest, dstart, dend ->
        var source = source

        if (source.length > 0) {
            if (!Character.isDigit(source[0])) return@InputFilter "" else {
                if (source.toString().length > 1) {
                    val number = source.toString()
                    val digits1 = number.toCharArray()
                    val digits2 = number.split("(?<=.)").toTypedArray()
                    source = digits2[digits2.size - 1]
                }
                if (edtPhoneNumber.text.toString().length < 1) {
                    return@InputFilter "($source"
                } else if (edtPhoneNumber.text.toString().length > 1 && edtPhoneNumber.text.toString()
                        .length <= 3
                ) {
                    return@InputFilter source
                } else if (edtPhoneNumber.text.toString().length > 3 && edtPhoneNumber.text.toString()
                        .length <= 5
                ) {
                    val isContain = dest.toString().contains(")")
                    return@InputFilter if (isContain) {
                        source
                    } else {
                        ")$source"
                    }
                } else if (edtPhoneNumber.text.toString().length > 5 && edtPhoneNumber.text.toString()
                        .length <= 7
                ) {
                    return@InputFilter source
                } else if (edtPhoneNumber.text.toString().length > 7) {
                    val isContain = dest.toString().contains("-")
                    return@InputFilter if (isContain) {
                        source
                    } else {
                        "-$source"
                    }
                }
            }
        } else {
        }
        source
    }

    private fun callFBLoginAPI(accessToken: String) {
        if (Constant.isOnline(this)) {
            if (!Constant.isInitProgress()) {
                Constant.showLoader(this)
            } else if (Constant.isInitProgress() && !Constant.progress.isShowing) {
                Constant.showLoader(this)
            }
            val request = HashMap<String, Any>()
            request[ApiConstant.AccessToken] = accessToken

            fbLoginViewModel.getLogin(this, request)!!.observe(this, Observer { data ->
                Constant.dismissLoader()
                if (data.buyerId != 0) {
                    pref?.setLogin(true)
                    data.isSocial = true
                    pref?.setUserData(Gson().toJson(data))
                    callPushTokenAPI()
                } else {
                    Toast.makeText(
                        this,
                        resources.getString(R.string.login_failed),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
            )
        } else {
            Toast.makeText(this, Constant.noInternet, Toast.LENGTH_SHORT).show()
        }
    }

    private fun callGoogleLoginAPI(accessToken: String) {
        if (Constant.isOnline(this)) {
            if (!Constant.isInitProgress()) {
                Constant.showLoader(this)
            } else if (Constant.isInitProgress() && !Constant.progress.isShowing) {
                Constant.showLoader(this)
            }
            val request = HashMap<String, Any>()
            request[ApiConstant.Token] = accessToken

            googleLoginViewModel.getLogin(this, request)!!.observe(this, Observer { data ->
                Constant.dismissLoader()
                if (data.buyerId != 0) {
                    pref?.setLogin(true)
                    data.isSocial = true
                    pref?.setUserData(Gson().toJson(data))
                    callPushTokenAPI()
                } else {
                    Toast.makeText(
                        this,
                        resources.getString(R.string.login_failed),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
            )
        } else {
            Toast.makeText(this, Constant.noInternet, Toast.LENGTH_SHORT).show()
        }
    }

    private fun callPushTokenAPI() {
        if (Constant.isOnline(this)) {
            if (!Constant.isInitProgress()) {
                Constant.showLoader(this)
            } else if (Constant.isInitProgress() && !Constant.progress.isShowing) {
                Constant.showLoader(this)
            }
            val request = HashMap<String, Any>()
            request[ApiConstant.Token] = pref?.getFirebaseToken()!!
            request[ApiConstant.UserProfileId] = pref?.getUserData()?.buyerId!!.toString()
            request[ApiConstant.DeviceType] = "Android"

            pushTokenViewModel.pushToken(this, request)!!.observe(this, Observer { data ->
                Constant.dismissLoader()
                Log.e("token Resp", data.message!!)
                startActivity<MainActivity>()
                finish()
            }
            )
        } else {
            Toast.makeText(this, Constant.noInternet, Toast.LENGTH_SHORT).show()
        }
    }

}