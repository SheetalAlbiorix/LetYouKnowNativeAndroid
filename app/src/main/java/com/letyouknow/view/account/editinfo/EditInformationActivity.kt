package com.letyouknow.view.account.editinfo

import android.app.Activity
import android.os.Bundle
import android.text.InputFilter
import android.widget.ArrayAdapter
import androidx.databinding.DataBindingUtil
import com.letyouknow.R
import com.letyouknow.base.BaseActivity
import com.letyouknow.databinding.ActivityEditInformationBinding
import kotlinx.android.synthetic.main.activity_edit_information.*
import kotlinx.android.synthetic.main.layout_toolbar.toolbar

class EditInformationActivity : BaseActivity() {
    private lateinit var binding: ActivityEditInformationBinding

    private var arState = arrayListOf("State")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_information)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_edit_information)
        init()
    }

    private fun init() {
        backButton()
        setState()
        edtPhoneNumber.filters = arrayOf<InputFilter>(filter, InputFilter.LengthFilter(13))
    }

    private fun setState() {
        val adapterYear = ArrayAdapter<String?>(
            applicationContext,
            android.R.layout.simple_spinner_item,
            arState as List<String?>
        )
        adapterYear.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spState.adapter = adapterYear
    }

    private fun backButton() {
        toolbar.setNavigationIcon(R.drawable.ic_back)
        toolbar.setTitleTextColor(resources.getColor(R.color.black))

        setSupportActionBar(toolbar)
        if (supportActionBar != null) {
            supportActionBar!!.setDisplayHomeAsUpEnabled(true)
            supportActionBar!!.setDisplayShowHomeEnabled(true)
        }
    }

    override fun getViewActivity(): Activity {
        return this
    }

    override fun onNetworkStateChange(isConnect: Boolean) {

    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    var filter = InputFilter { source, start, end, dest, dstart, dend ->
        var source = source

        if (source.length > 0) {
            if (!Character.isDigit(source[0])) return@InputFilter "" else {
                if (source.toString().length > 1) {
                    val number = source.toString()
                    val digits1 = number.toCharArray()
                    val digits2 = number.split("(?<=.)").toTypedArray()
                    source = digits2[digits2.size - 1]
                }
                if (edtPhoneNumber.text.toString().length < 1) {
                    return@InputFilter "($source"
                } else if (edtPhoneNumber.text.toString().length > 1 && edtPhoneNumber.text.toString()
                        .length <= 3
                ) {
                    return@InputFilter source
                } else if (edtPhoneNumber.text.toString().length > 3 && edtPhoneNumber.text.toString()
                        .length <= 5
                ) {
                    val isContain = dest.toString().contains(")")
                    return@InputFilter if (isContain) {
                        source
                    } else {
                        ")$source"
                    }
                } else if (edtPhoneNumber.text.toString().length > 5 && edtPhoneNumber.text.toString()
                        .length <= 7
                ) {
                    return@InputFilter source
                } else if (edtPhoneNumber.text.toString().length > 7) {
                    val isContain = dest.toString().contains("-")
                    return@InputFilter if (isContain) {
                        source
                    } else {
                        "-$source"
                    }
                }
            }
        } else {
        }
        source
    }
}