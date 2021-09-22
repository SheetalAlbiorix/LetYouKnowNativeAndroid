package com.logispeed.data.prefs

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.letyouknow.model.CardListData
import com.letyouknow.model.LoginData
import java.lang.reflect.Type

class AppPreferencesHelper(context: Context, prefFileName: String) {

    companion object {
        val KEY_IS_LOGIN = "IS_LOGIN"
        val KEY_USER_DATA = "KEY_USER_DATA"
        val KEY_CARD_LIST = "KEY_CARD_LIST"
    }

    private var sharedpreferences: SharedPreferences =
        context.getSharedPreferences(prefFileName, Context.MODE_PRIVATE)
    private var prefs: SharedPreferences.Editor = sharedpreferences.edit()


    fun setLogin(isLogin: Boolean) {
        prefs.putBoolean(KEY_IS_LOGIN, isLogin).apply()
    }

    fun isLogin(): Boolean {
        return sharedpreferences.getBoolean(KEY_IS_LOGIN, false)
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

}