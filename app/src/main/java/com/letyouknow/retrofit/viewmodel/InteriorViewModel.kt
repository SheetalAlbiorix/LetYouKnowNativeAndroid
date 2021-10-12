package com.letyouknow.retrofit.viewmodel

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.letyouknow.retrofit.repository.InteriorRepository

class InteriorViewModel : ViewModel() {
    var interiorLiveData: MutableLiveData<ArrayList<String>>? = null

    fun getInterior(
        context: Context,
        request: HashMap<String, Any>
    ): LiveData<ArrayList<String>>? {
        interiorLiveData = InteriorRepository.interiorApiCall(context, request)
        return interiorLiveData
    }
}