package com.letyouknow.retrofit.viewmodel

import android.app.Activity
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.letyouknow.model.CurrentReferralProgramData
import com.letyouknow.retrofit.repository.CurrentReferralProgramPostRepository

class CurrentReferralPostViewModel : ViewModel() {
    var loginLiveData: MutableLiveData<CurrentReferralProgramData>? = null

    fun getReferral(
        context: Activity,
        request: HashMap<String, Any>
    ): LiveData<CurrentReferralProgramData>? {
        loginLiveData = CurrentReferralProgramPostRepository.getReferralApiCall(context, request)
        return loginLiveData
    }
}