package com.letyouknow.base

import android.app.Activity
import android.os.Bundle
import androidx.fragment.app.Fragment
import cn.pedant.SweetAlert.SweetAlertDialog
import com.letyouknow.R
import com.letyouknow.utils.AppGlobal

open class BaseFragment : Fragment(), BaseView {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun showProgress() {
        AppGlobal.showProgressDialog(getViewActivity(), "")
    }

    override fun hideProgress() {
        AppGlobal.hideProgressDialog()
    }

    fun showAlertBox(message: String?, clickListener: SweetAlertDialog.OnSweetClickListener?) {
        SweetAlertDialog(getViewActivity(), SweetAlertDialog.SUCCESS_TYPE)
            .setTitleText(resources.getString(R.string.app_name))
            .setContentText(message)
            .setConfirmText("ok")
            .setConfirmClickListener(clickListener)
            .show()
        // Utility.showSnackBar(findViewById(android.R.id.content), message, Snackbar.LENGTH_LONG, false);
    }

    override fun showToast(message: String?) {
        SweetAlertDialog(getViewActivity(), SweetAlertDialog.SUCCESS_TYPE)
            .setTitleText(resources.getString(R.string.app_name))
            .setContentText(message)
            .setConfirmText("ok")
            .setConfirmClickListener(object : SweetAlertDialog.OnSweetClickListener {
                override fun onClick(sDialog: SweetAlertDialog?) {
                    sDialog?.dismiss()
                }

            }).show()
    }

    override fun showError(message: String?) {
        SweetAlertDialog(getViewActivity(), SweetAlertDialog.ERROR_TYPE)
            .setTitleText(resources.getString(R.string.app_name))
            .setContentText(message)
            .setConfirmText("ok")
            .show()
    }

    override fun getViewActivity(): Activity? {
        return activity
    }

    override fun onNetworkStateChange(isConnect: Boolean) {
    }

}