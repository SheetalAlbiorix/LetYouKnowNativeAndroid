package com.letyouknow.retrofit.viewmodel

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.letyouknow.model.VehicleYearData
import com.letyouknow.retrofit.repository.VehicleYearRepository

class VehicleYearViewModel : ViewModel() {
    var liveData: MutableLiveData<ArrayList<VehicleYearData>>? = null

    fun getYear(
        context: Context,
        productId: String?
    ): LiveData<ArrayList<VehicleYearData>>? {
        liveData = VehicleYearRepository.getVehicleYearApiCall(context, productId)
        return liveData
    }
}