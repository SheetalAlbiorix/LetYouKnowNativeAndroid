package com.letyouknow.view.account

import android.app.Dialog
import android.os.Bundle
import android.text.TextUtils
import android.view.*
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.google.gson.Gson
import com.letyouknow.R
import com.letyouknow.base.BaseFragment
import com.letyouknow.databinding.FragmentAccount1Binding
import com.letyouknow.model.UserProfileData
import com.letyouknow.retrofit.ApiConstant
import com.letyouknow.retrofit.viewmodel.EditUserProfileViewModel
import com.letyouknow.retrofit.viewmodel.NotificationOptionsViewModel
import com.letyouknow.retrofit.viewmodel.SavingsToDateViewModel
import com.letyouknow.retrofit.viewmodel.UserProfileViewModel
import com.letyouknow.utils.AppGlobal
import com.letyouknow.view.account.editinfo.EditInformationActivity
import com.letyouknow.view.account.editlogin.EditLoginActivity
import com.letyouknow.view.account.editnotification.EditNotificationActivity
import com.letyouknow.view.account.editrefer.EditReferActivity
import com.letyouknow.view.dashboard.MainActivity
import com.pionymessenger.utils.Constant
import kotlinx.android.synthetic.main.dialog_edit_info.*
import kotlinx.android.synthetic.main.fragment_account1.*
import org.jetbrains.anko.support.v4.startActivity

class AccountFragment : BaseFragment(), View.OnClickListener, AdapterView.OnItemSelectedListener {

    private lateinit var savingsToDateViewModel: SavingsToDateViewModel
    private lateinit var notificationOptionsViewModel: NotificationOptionsViewModel
    private lateinit var userProfileViewModel: UserProfileViewModel
    private lateinit var editUserProfileViewModel: EditUserProfileViewModel
    private lateinit var binding: FragmentAccount1Binding
    private lateinit var userData: UserProfileData
    private var state = ""

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_account1,
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
        savingsToDateViewModel = ViewModelProvider(this).get(SavingsToDateViewModel::class.java)
        notificationOptionsViewModel =
            ViewModelProvider(this).get(NotificationOptionsViewModel::class.java)
        userProfileViewModel = ViewModelProvider(this).get(UserProfileViewModel::class.java)
        editUserProfileViewModel = ViewModelProvider(this).get(EditUserProfileViewModel::class.java)

        tvEditLogin.setOnClickListener(this)
        tvEditInfo.setOnClickListener(this)
        MainActivity.getInstance().setVisibleEditImg(false)
        MainActivity.getInstance().setVisibleLogoutImg(true)

        callUserProfileAPI()
    }


    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.ivEditInfo -> {
                startActivity<EditInformationActivity>()
            }
            R.id.ivEditLogin -> {
                startActivity<EditLoginActivity>()
            }
            R.id.ivEditNotification -> {
                startActivity<EditNotificationActivity>()
            }
            R.id.ivRefer -> {
                startActivity<EditReferActivity>()
            }
            R.id.tvEditInfo -> {
                popupEditInfo()
            }
        }
    }

    private lateinit var dialogEditInfo: Dialog
    private fun popupEditInfo() {
        dialogEditInfo = Dialog(requireActivity(), R.style.FullScreenDialog)
        dialogEditInfo.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialogEditInfo.setCancelable(true)
        dialogEditInfo.setCanceledOnTouchOutside(true)
        dialogEditInfo.setContentView(R.layout.dialog_edit_info)
        setEditInfoData()
        dialogEditInfo.btnDialogSave.setOnClickListener {
            if (isValid()) {
                callEditUserProfileAPI()
            }
        }
        setLayoutParam(dialogEditInfo)
        dialogEditInfo.show()
    }

    private fun setEditInfoData() {
        dialogEditInfo.run {
            edtFirstName.setText(userData.firstName)
            edtLastName.setText(userData.lastName)
            edtMiddleName.setText(userData.middleName)
            edtEmail.setText(userData.email)
            edtConfirmEmail.setText(userData.email)
            edtUserName.setText(userData.userName)
            edtPhoneNumber.setText(userData.phoneNumber)
            edtAddress1.setText(userData.address1)
            edtAddress2.setText(userData.address2)
            edtCity.setText(userData.city)
            edtZipCode.setText(userData.zipcode)
            setState()
        }
    }

    private fun setClearEditInfoData() {
        dialogEditInfo.run {
            edtFirstName.setText("")
            edtLastName.setText("")
            edtMiddleName.setText("")
            edtEmail.setText("")
            edtConfirmEmail.setText("")
            edtUserName.setText("")
            edtPhoneNumber.setText("")
            edtAddress1.setText("")
            edtAddress2.setText("")
            edtCity.setText("")
            edtZipCode.setText("")
        }
    }

    private lateinit var adapterState: ArrayAdapter<String?>
    private fun setState() {
        adapterState = ArrayAdapter<String?>(
            requireActivity(),
            android.R.layout.simple_spinner_item,
            AppGlobal.arState as List<String?>
        )
        adapterState.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        dialogEditInfo.spState.adapter = adapterState
        dialogEditInfo.spState.onItemSelectedListener = this
        for (i in 0 until AppGlobal.arState.size) {
            if (AppGlobal.arState[i] == userData.state) {
                dialogEditInfo.spState.setSelection(i)
            }
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


    private fun callSavingsToDateAPI() {
        if (Constant.isOnline(requireActivity())) {
            Constant.showLoader(requireActivity())
            savingsToDateViewModel.savingsToDateCall(requireActivity())!!
                .observe(requireActivity(), Observer { data ->
                    Constant.dismissLoader()
                    tvSavingsDate.text = "$$data"
                    callNotificationOptionsAPI()
                }
                )
        } else {
            Toast.makeText(requireActivity(), Constant.noInternet, Toast.LENGTH_SHORT).show()
        }
    }


    private fun callNotificationOptionsAPI() {
        if (Constant.isOnline(requireActivity())) {
            Constant.showLoader(requireActivity())
            notificationOptionsViewModel.notificationCall(requireActivity())!!
                .observe(requireActivity(), Observer { data ->
                    Constant.dismissLoader()

                }
                )
        } else {
            Toast.makeText(requireActivity(), Constant.noInternet, Toast.LENGTH_SHORT).show()
        }
    }

    private fun callUserProfileAPI() {
        if (Constant.isOnline(requireActivity())) {
            Constant.showLoader(requireActivity())
            userProfileViewModel.userProfileCall(requireActivity())!!
                .observe(requireActivity(), Observer { data ->
                    Constant.dismissLoader()
                    binding.userData = data
                    userData = data
                    callSavingsToDateAPI()
                }
                )

        } else {
            Toast.makeText(requireActivity(), Constant.noInternet, Toast.LENGTH_SHORT).show()
        }
    }

    private fun callEditUserProfileAPI() {
        if (Constant.isOnline(requireActivity())) {
            Constant.showLoader(requireActivity())
            val map: HashMap<String, String> = HashMap()
            map[ApiConstant.middleName] = dialogEditInfo.edtMiddleName.text.toString().trim()
            map[ApiConstant.firstName] = dialogEditInfo.edtFirstName.text.toString().trim()
            map[ApiConstant.lastName] = dialogEditInfo.edtLastName.text.toString().trim()
            map[ApiConstant.email] = dialogEditInfo.edtEmail.text.toString().trim()
            map[ApiConstant.confirmEmail] = dialogEditInfo.edtConfirmEmail.text.toString().trim()
            map[ApiConstant.userName] = dialogEditInfo.edtUserName.text.toString().trim()
            map[ApiConstant.phoneNumber] = dialogEditInfo.edtPhoneNumber.text.toString().trim()
            map[ApiConstant.address1] = dialogEditInfo.edtAddress1.text.toString().trim()
            map[ApiConstant.address2] = dialogEditInfo.edtAddress2.text.toString().trim()
            map[ApiConstant.city] = dialogEditInfo.edtCity.text.toString().trim()
            map[ApiConstant.state] = state
            map[ApiConstant.zipcode] = dialogEditInfo.edtZipCode.text.toString().trim()
            editUserProfileViewModel.editUserCall(requireActivity(), map)!!
                .observe(requireActivity(), Observer { data ->
                    Constant.dismissLoader()
                    val userData = pref?.getUserData()
                    userData?.firstName = dialogEditInfo.edtFirstName.text.toString().trim()
                    userData?.lastName = dialogEditInfo.edtLastName.text.toString().trim()
                    userData?.authToken = data.authToken
                    userData?.refreshToken = data.refreshToken
                    pref?.setUserData(Gson().toJson(userData))
                    setClearEditInfoData()
                }
                )

        } else {
            Toast.makeText(requireActivity(), Constant.noInternet, Toast.LENGTH_SHORT).show()
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

    private fun isValid(): Boolean {
        dialogEditInfo.run {
            when {
                TextUtils.isEmpty(edtFirstName.text.toString().trim()) -> {
                    Constant.setErrorBorder(edtFirstName, tvErrorFirstName)
                    return false
                }
                TextUtils.isEmpty(edtLastName.text.toString().trim()) -> {
                    Constant.setErrorBorder(edtLastName, tvErrorLastName)
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
                TextUtils.isEmpty(edtConfirmEmail.text.toString().trim()) -> {
                    tvErrorConfirmEmailAddress.text = getString(R.string.enter_email_address_vali)
                    Constant.setErrorBorder(edtConfirmEmail, tvErrorConfirmEmailAddress)
                    return false
                }
                !Constant.emailValidator(edtConfirmEmail.text.toString().trim()) -> {
                    tvErrorConfirmEmailAddress.text = getString(R.string.enter_valid_email)
                    Constant.setErrorBorder(edtEmail, tvErrorConfirmEmailAddress)
                    return false
                }
                (edtEmail.text.toString().trim() != edtConfirmEmail.text.toString().trim()) -> {
                    tvErrorConfirmEmailAddress.text = getString(R.string.did_n_t_match_email)
                    Constant.setErrorBorder(edtEmail, tvErrorConfirmEmailAddress)
                    return false
                }
                TextUtils.isEmpty(edtUserName.text.toString().trim()) -> {
                    Constant.setErrorBorder(edtUserName, tvErrorUserName)
                    return false
                }
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
                TextUtils.isEmpty(edtAddress1.text.toString().trim()) -> {
                    Constant.setErrorBorder(edtAddress1, tvErrorAddress1)
                    return false
                }
                TextUtils.isEmpty(edtCity.text.toString().trim()) -> {
                    Constant.setErrorBorder(edtCity, tvErrorCity)
                    return false
                }

                TextUtils.isEmpty(edtZipCode.text.toString().trim()) -> {
                    Constant.setErrorBorder(edtZipCode, tvErrorZipCode)
                    return false
                }


                /*    TextUtils.isEmpty(edtPassword.text.toString().trim()) -> {
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
                    }*/
                else -> return true
            }
        }
    }


}


