package com.letyouknow.retrofit.viewmodel

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.letyouknow.model.UserProfileData
import com.letyouknow.retrofit.repository.UserProfileRepository

class UserProfileViewModel : ViewModel() {
    var liveData: MutableLiveData<UserProfileData>? = null

    fun userProfileCall(
        context: Context
    ): LiveData<UserProfileData>? {
        liveData = UserProfileRepository.userProfileApiCall(
            context
        )
        return liveData
    }
}