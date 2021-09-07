package com.letyouknow

import android.app.Application
import com.logispeed.data.prefs.AppPreferencesHelper

class LetYouKnowApp : Application() {
    private var appPreferencesHelper: AppPreferencesHelper? = null

    companion object {
        private var mInstance: LetYouKnowApp? = null

        @Synchronized
        fun getInstance(): LetYouKnowApp? {
            return mInstance

        }
    }

    override fun onCreate() {
        super.onCreate()
        mInstance = this
//        Fabric.with(this, Crashlytics())

        appPreferencesHelper = AppPreferencesHelper(this, getString(R.string.app_name))
//        FirebaseApp.initializeApp(this)

        //Generate 11 digit key for sms retrive
//        Log.i("AppApplication","appSignatures = ${AppSignatureHelper(this).getAppSignatures()}")
    }

    fun getAppPreferencesHelper(): AppPreferencesHelper? {
        return appPreferencesHelper
    }
}