package com.letyouknow.retrofit.viewmodel

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.letyouknow.retrofit.repository.VehicleZipCodeRepository

class VehicleZipCodeViewModel : ViewModel() {
    var liveData: MutableLiveData<Boolean>? = null

    fun getZipCode(
        context: Context,
        zipCode: String?
    ): LiveData<Boolean>? {
        liveData = VehicleZipCodeRepository.getVehicleZipCodeApiCall(context, zipCode)
        return liveData
    }
}