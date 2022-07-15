package com.letyouknow.view.dropdown

import android.app.Activity
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupWindow
import com.letyouknow.R

class YearDropDown(val context: Activity, val clickListener: View.OnClickListener) {
    private fun showAlertFilter(v: View) {
        val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val view = inflater.inflate(R.layout.popup_dialog, null)

        val popup = PopupWindow(
            view,
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )

        return popup.showAsDropDown(v)
    }
}