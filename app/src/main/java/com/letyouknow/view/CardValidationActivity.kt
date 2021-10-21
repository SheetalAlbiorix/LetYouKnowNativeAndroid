package com.letyouknow.view

import android.os.Bundle
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.letyouknow.R
import com.letyouknow.utils.CreditCardNumberTextWatcher
import kotlinx.android.synthetic.main.activity_card_validation.*


class CardValidationActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_card_validation)
        init()
    }

    private fun init() {
        val textWatcher: TextWatcher = CreditCardNumberTextWatcher(edtCardNumber, tvErrorCardNumber)
        edtCardNumber.addTextChangedListener(textWatcher)
        setOnChangeCard()

    }

    private fun setOnChangeCard() {
        edtExpiresDate.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(
                p0: CharSequence?,
                start: Int,
                removed: Int,
                added: Int
            ) {
            }

            override fun onTextChanged(p0: CharSequence?, start: Int, removed: Int, added: Int) {
            }

            override fun afterTextChanged(editable: Editable?) {
                val inputLength = edtExpiresDate.text.toString().length
                if (inputLength == 5 && edtExpiresDate.isDateValid) {
                    tvErrorCardDate.visibility = View.GONE
                    edtCVV.requestFocus()
                } else {
                    tvErrorCardDate.visibility = View.VISIBLE
                    tvErrorCardDate.text = getString(R.string.bt_expiration_invalid)
                }
            }
        })
        edtCVV.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(
                p0: CharSequence?,
                start: Int,
                removed: Int,
                added: Int
            ) {
            }

            override fun onTextChanged(p0: CharSequence?, start: Int, removed: Int, added: Int) {
            }

            override fun afterTextChanged(editable: Editable?) {
                val inputLength = edtCVV.text.toString().length
                if (inputLength < 3) {
                    tvErrorCardCVV.visibility = View.VISIBLE
                    tvErrorCardCVV.text = getString(R.string.cvv_must_be_valid_digits)
                } else if (inputLength == 3) {
                    tvErrorCardCVV.visibility = View.GONE
                    edtCardZipCode.requestFocus()
                }
            }
        })
        edtCardZipCode.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(
                p0: CharSequence?,
                start: Int,
                removed: Int,
                added: Int
            ) {
            }

            override fun onTextChanged(p0: CharSequence?, start: Int, removed: Int, added: Int) {

            }

            override fun afterTextChanged(editable: Editable?) {
                val inputLength = edtCardZipCode.text.toString().length
                if (isValidCard()) {
                    when {
                        inputLength == 5 -> {
                            tvErrorCardZip.visibility = View.GONE
                            if (isValidCard()) {

                            }
                        }
                        inputLength < 5 -> {
                            tvErrorCardZip.visibility = View.VISIBLE
                            tvErrorCardZip.text = getString(R.string.zipcode_must_be_valid_digits)
                        }
                        else -> {
                            tvErrorCardZip.visibility = View.GONE
                        }
                    }
                } else {
                    if (!TextUtils.isEmpty(edtCardZipCode.text.toString().trim()))
                        edtCardZipCode.setText("")
                }
            }
        })
    }

    private fun isValidCard(): Boolean {
        when {
            TextUtils.isEmpty(edtCardNumber.text.toString().trim()) -> {
                tvErrorCardNumber.visibility = View.VISIBLE
                tvErrorCardNumber.text = getString(R.string.enter_card_number)
                edtCardNumber.requestFocus()
                return false
            }
            (tvErrorCardNumber.visibility == View.VISIBLE) -> {
                edtCardNumber.requestFocus()
                return false
            }
            TextUtils.isEmpty(edtExpiresDate.text.toString().trim()) -> {
                tvErrorCardDate.visibility = View.VISIBLE
                tvErrorCardDate.text = getString(R.string.bt_expiration_required)
                edtExpiresDate.requestFocus()
                return false
            }
            tvErrorCardDate.visibility == View.VISIBLE -> {
                edtExpiresDate.requestFocus()
                return false
            }
            TextUtils.isEmpty(edtCVV.text.toString().trim()) -> {
                tvErrorCardCVV.visibility = View.VISIBLE
                tvErrorCardCVV.text = getString(R.string.cvv_required)
                edtCVV.requestFocus()
                return false
            }
            tvErrorCardCVV.visibility == View.VISIBLE -> {
                edtCVV.requestFocus()
                return false
            }
            TextUtils.isEmpty(edtCardZipCode.text.toString().trim()) -> {
                tvErrorCardZip.visibility = View.VISIBLE
                tvErrorCardZip.text = getString(R.string.zip_code_required)
                edtCardZipCode.requestFocus()
                return false
            }

        }
        return true
    }
}