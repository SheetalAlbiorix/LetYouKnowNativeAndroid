package com.letyouknow.base

import android.app.Activity

interface BaseView {
    fun showProgress()

    fun hideProgress()

    fun showToast(message: String?)

    fun showError(message: String?)

    fun getViewActivity(): Activity?

    fun onNetworkStateChange(isConnect: Boolean)
}