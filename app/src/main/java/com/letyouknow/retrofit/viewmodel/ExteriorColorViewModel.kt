package com.letyouknow.retrofit.viewmodel

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.letyouknow.model.ExteriorColorData
import com.letyouknow.retrofit.repository.ExteriorColorRepository

class ExteriorColorViewModel : ViewModel() {
    var liveData: MutableLiveData<ArrayList<ExteriorColorData>>? = null

    fun getExteriorColor(
        context: Context,
        productId: String?,
        yearId: String?,
        makeId: String?,
        modelId: String?,
        trimId: String?
    ): LiveData<ArrayList<ExteriorColorData>>? {
        liveData = ExteriorColorRepository.getExteriorColorCall(
            context,
            productId,
            yearId,
            makeId,
            modelId,
            trimId
        )
        return liveData
    }
}