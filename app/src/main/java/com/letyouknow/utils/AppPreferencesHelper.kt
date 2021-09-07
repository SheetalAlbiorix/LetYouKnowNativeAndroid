package com.logispeed.data.prefs

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.letyouknow.model.CardListData
import java.lang.reflect.Type

class AppPreferencesHelper(context: Context, prefFileName: String) {

    companion object {
        val KEY_CARD_LIST = "KEY_CARD_LIST"
    }

    private var sharedpreferences: SharedPreferences =
        context.getSharedPreferences(prefFileName, Context.MODE_PRIVATE)
    private var prefs: SharedPreferences.Editor = sharedpreferences.edit()


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
            val type: Type = object : TypeToken<ArrayList<CardListData>?>() {}.getType()
            return gson.fromJson(sharedpreferences.getString(KEY_CARD_LIST, ""), type)
        }

    }

}