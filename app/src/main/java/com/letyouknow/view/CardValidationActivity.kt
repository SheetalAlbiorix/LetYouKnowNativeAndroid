package com.letyouknow.view

import android.os.Bundle
import android.text.Editable
import android.text.InputFilter
import android.text.TextWatcher
import androidx.appcompat.app.AppCompatActivity
import com.letyouknow.R
import com.letyouknow.utils.CreditCardExpiryInputFilter
import com.letyouknow.utils.CreditCardNumberTextWatcher
import kotlinx.android.synthetic.main.activity_card_validation.*

class CardValidationActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_card_validation)
        init()
    }

    private fun init() {
        val textWatcher: TextWatcher = CreditCardNumberTextWatcher(edtCardNumber)
        edtCardNumber.addTextChangedListener(textWatcher)
        edtExpiresDate.filters = arrayOf<InputFilter>(CreditCardExpiryInputFilter())
//        setOnChange()
    }

    private fun setOnChange() {
        edtExpiresDate.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

            override fun afterTextChanged(s: Editable?) {
                val inputLength = edtExpiresDate.text.toString().length
                if (inputLength == 2) {
                    edtExpiresDate.setText(edtExpiresDate.text.toString().trim() + "/")
                    edtExpiresDate.setSelection(edtExpiresDate.text.toString().length)
                }
                if (inputLength == 5) {
                    edtCVV.requestFocus()
                }
            }
        })
    }
}