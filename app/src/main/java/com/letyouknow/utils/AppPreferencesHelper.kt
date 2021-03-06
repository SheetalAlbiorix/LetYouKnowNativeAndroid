package com.logispeed.data.prefs

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.letyouknow.model.*
import java.lang.reflect.Type

class AppPreferencesHelper(context: Context, prefFileName: String) {

    companion object {
        val KEY_IS_LOGIN = "IS_LOGIN"
        val KEY_USER_DATA = "KEY_USER_DATA"
        val KEY_REMEMBER_DATA = "KEY_REMEMBER_DATA"
        val KEY_CARD_LIST = "KEY_CARD_LIST"
        val KEY_IS_SOCIAL_MOBILE = "KEY_IS_SOCIAL_MOBILE"
        val KEY_IS_PAYMENT = "KEY_IS_PAYMENT"
        val KEY_IS_BIOMATRIC = "KEY_IS_BIOMATRIC"
        val KEY_IS_BID = "KEY_IS_BID"
        val KEY_IS_LCD = "KEY_IS_LCD"
        val KEY_SUBMIT_PRICE_TIME = "KEY_SUBMIT_PRICE_TIME"
        val KEY_SUBMIT_PRICE = "KEY_SUBMIT_PRICE"
        val KEY_ONE_DEAL_NEAR_TIME = "KEY_ONE_DEAL_NEAR_TIME"
        val KEY_ONE_DEAL_NEAR = "KEY_ONE_DEAL_NEAR"
        val KEY_SEARCH_DEAL = "KEY_SEARCH_DEAL"
        val KEY_SEARCH_DEAL_TIME = "KEY_SEARCH_DEAL_TIME"
        val KEY_RADIUS = "KEY_RADIUS"
        val KEY_PAYMENT_URL = "KEY_PAYMENT_URL"
        val KEY_FIREBASE_TOKEN = "KEY_FIREBASE_TOKEN"
    }

    private var sharedpreferences: SharedPreferences =
        context.getSharedPreferences(prefFileName, Context.MODE_PRIVATE)
    private var prefs: SharedPreferences.Editor = sharedpreferences.edit()


    fun setLogin(isLogin: Boolean) {
        prefs.putBoolean(KEY_IS_LOGIN, isLogin).apply()
    }

    fun setLogOutData() {
        setLogin(false)
        val remData = isRememberData()
        val loginData = getUserData()
        val firebaseToken = getFirebaseToken()
        prefs.clear().apply()
        setBioMetric(true)
        setFirebaseToken(firebaseToken)
        setUserData(Gson().toJson(loginData))
        setRememberData(Gson().toJson(remData))
    }

    fun isLogin(): Boolean {
        return sharedpreferences.getBoolean(KEY_IS_LOGIN, false)
    }

    fun setPaymentToken(isPayment: Boolean) {
        prefs.putBoolean(KEY_IS_PAYMENT, isPayment).apply()
    }

    fun isPayment(): Boolean {
        return sharedpreferences.getBoolean(KEY_IS_PAYMENT, false)
    }

    fun setCardList(cardList: String) {
        prefs.putString(KEY_CARD_LIST, cardList).apply()
    }

    fun getCardList(): ArrayList<CardListData> {
        return if (sharedpreferences.getString(
                KEY_CARD_LIST,
                ""
            ) == "" || sharedpreferences.getString(
                KEY_CARD_LIST,
                ""
            ) == null
        )
            ArrayList()
        else {
            val gson = Gson()
            val type: Type = object : TypeToken<ArrayList<CardListData>?>() {}.type
            return gson.fromJson(sharedpreferences.getString(KEY_CARD_LIST, ""), type)
        }

    }

    fun setUserData(user: String) {
        prefs.putString(KEY_USER_DATA, user).apply()
    }

    fun getUserData(): LoginData {
        return if (sharedpreferences.getString(
                KEY_USER_DATA,
                ""
            ) == "" || sharedpreferences.getString(
                KEY_USER_DATA,
                ""
            ) == null
        )
            LoginData()
        else {
            val gson = Gson()
            return gson.fromJson(
                sharedpreferences.getString(KEY_USER_DATA, ""),
                LoginData::class.java
            )
        }

    }


    fun setRememberData(user: String) {
        prefs.putString(KEY_REMEMBER_DATA, user).apply()
    }

    fun isRememberData(): RememberMeData {
        return if (sharedpreferences.getString(
                KEY_REMEMBER_DATA,
                ""
            ) == "" || sharedpreferences.getString(
                KEY_REMEMBER_DATA,
                ""
            ) == null
        )
            RememberMeData()
        else {
            val gson = Gson()
            return gson.fromJson(
                sharedpreferences.getString(KEY_REMEMBER_DATA, ""),
                RememberMeData::class.java
            )
        }

    }

    fun setSubmitPriceData(user: String) {
        prefs.putString(KEY_SUBMIT_PRICE, user).apply()
    }

    fun getSubmitPriceData(): PrefSubmitPriceData {
        return if (sharedpreferences.getString(
                KEY_SUBMIT_PRICE,
                ""
            ) == "" || sharedpreferences.getString(
                KEY_SUBMIT_PRICE,
                ""
            ) == null
        )
            PrefSubmitPriceData()
        else {
            val gson = Gson()
            return gson.fromJson(
                sharedpreferences.getString(KEY_SUBMIT_PRICE, ""),
                PrefSubmitPriceData::class.java
            )
        }
    }

    fun setSubmitPriceTime(time: String) {
        prefs.putString(KEY_SUBMIT_PRICE_TIME, time).apply()
    }

    fun getSubmitPriceTime(): String {
        return sharedpreferences.getString(KEY_SUBMIT_PRICE_TIME, "")!!
    }

    fun setOneDealNearYouData(user: String) {
        prefs.putString(KEY_ONE_DEAL_NEAR, user).apply()
    }

    fun getOneDealNearYouData(): PrefOneDealNearYouData {
        return if (sharedpreferences.getString(
                KEY_ONE_DEAL_NEAR,
                ""
            ) == "" || sharedpreferences.getString(
                KEY_ONE_DEAL_NEAR,
                ""
            ) == null
        )
            PrefOneDealNearYouData()
        else {
            val gson = Gson()
            return gson.fromJson(
                sharedpreferences.getString(KEY_ONE_DEAL_NEAR, ""),
                PrefOneDealNearYouData::class.java
            )
        }
    }

    fun setOneDealNearYou(time: String) {
        prefs.putString(KEY_ONE_DEAL_NEAR_TIME, time).apply()
    }

    fun getOneDealNearYou(): String {
        return sharedpreferences.getString(KEY_ONE_DEAL_NEAR_TIME, "")!!
    }

    fun setSearchDealData(user: String) {
        prefs.putString(KEY_SEARCH_DEAL, user).apply()
    }

    fun getSearchDealData(): PrefSearchDealData {
        return if (sharedpreferences.getString(
                KEY_SEARCH_DEAL,
                ""
            ) == "" || sharedpreferences.getString(
                KEY_SEARCH_DEAL,
                ""
            ) == null
        )
            PrefSearchDealData()
        else {
            val gson = Gson()
            return gson.fromJson(
                sharedpreferences.getString(KEY_SEARCH_DEAL, ""),
                PrefSearchDealData::class.java
            )
        }
    }

    fun setSearchDealTime(time: String) {
        prefs.putString(KEY_SEARCH_DEAL_TIME, time).apply()
    }

    fun getSearchDealTime(): String {
        return sharedpreferences.getString(KEY_SEARCH_DEAL_TIME, "")!!
    }

    fun isBioMetric(): Boolean {
        return sharedpreferences.getBoolean(KEY_IS_BIOMATRIC, false)
    }

    fun setBioMetric(isBio: Boolean) {
        prefs.putBoolean(KEY_IS_BIOMATRIC, isBio).apply()
    }

    fun setRadius(radius: String) {
        prefs.putString(KEY_RADIUS, radius).apply()
    }

    fun getRadius(): String {
        return sharedpreferences.getString(KEY_RADIUS, "")!!
    }

    fun isBid(): Boolean {
        return sharedpreferences.getBoolean(KEY_IS_BID, false)
    }

    fun setBid(isBid: Boolean) {
        prefs.putBoolean(KEY_IS_BID, isBid).apply()
    }

    fun isLCD(): Boolean {
        return sharedpreferences.getBoolean(KEY_IS_LCD, false)
    }

    fun setLCD(isLCD: Boolean) {
        prefs.putBoolean(KEY_IS_LCD, isLCD).apply()
    }

    fun setPaymentUrl(url: String) {
        prefs.putString(KEY_PAYMENT_URL, url).apply()
    }

    fun getPaymentUrl(): String {
        return sharedpreferences.getString(KEY_PAYMENT_URL, "")!!
    }

    fun updateSocialMobile(isPayment: Boolean) {
        prefs.putBoolean(KEY_IS_SOCIAL_MOBILE, isPayment).apply()
    }

    fun isUpdateSocialMobile(): Boolean {
        return sharedpreferences.getBoolean(KEY_IS_SOCIAL_MOBILE, false)
    }

    fun setFirebaseToken(url: String?) {
        prefs.putString(KEY_FIREBASE_TOKEN, url).apply()
    }

    fun getFirebaseToken(): String? {
        return sharedpreferences.getString(KEY_FIREBASE_TOKEN, "")!!
    }


}