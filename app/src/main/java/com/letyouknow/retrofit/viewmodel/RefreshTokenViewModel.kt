package com.letyouknow.retrofit.viewmodel

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.letyouknow.model.RefreshTokenData
import com.letyouknow.retrofit.repository.RefreshTokenRepository

class RefreshTokenViewModel : ViewModel() {
    var liveData: MutableLiveData<RefreshTokenData>? = null

    fun refresh(
        context: Context,
        request: HashMap<String, Any>
    ): LiveData<RefreshTokenData>? {
        liveData =
            RefreshTokenRepository.refresh(context, request)
        return liveData
    }
}