package com.letyouknow.retrofit.viewmodel

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.letyouknow.model.NotificationOptionsData
import com.letyouknow.retrofit.repository.NotificationOptionsRepository

class NotificationOptionsViewModel : ViewModel() {
    var liveData: MutableLiveData<NotificationOptionsData>? = null

    fun notificationCall(
        context: Context,
    ): LiveData<NotificationOptionsData>? {
        liveData = NotificationOptionsRepository.notificationOptionsRepositoryApiCall(
            context
        )
        return liveData
    }
}