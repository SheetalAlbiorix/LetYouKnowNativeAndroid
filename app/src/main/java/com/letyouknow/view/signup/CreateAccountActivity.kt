package com.letyouknow.view.signup


import android.app.Activity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import com.google.gson.Gson
import com.letyouknow.R
import com.letyouknow.base.BaseActivity
import com.letyouknow.databinding.ActivityCreateAccountBinding
import com.letyouknow.model.CardListData
import com.letyouknow.view.dashboard.MainActivity
import com.pionymessenger.utils.Constant.Companion.makeLinks
import kotlinx.android.synthetic.main.activity_create_account.*
import kotlinx.android.synthetic.main.layout_toolbar.toolbar
import org.jetbrains.anko.sdk27.coroutines.onCheckedChange
import org.jetbrains.anko.startActivity

class CreateAccountActivity : BaseActivity(), View.OnClickListener {
    private lateinit var adapterCardList: CardListAdapter
    private var selectCardPos = -1
    private var selectPaymentType = 0
    private var arCardList: ArrayList<CardListData> = ArrayList()
    lateinit var binding: ActivityCreateAccountBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_account)

        binding =
            DataBindingUtil.setContentView(this, R.layout.activity_create_account)
        binding.setSelectPaymentType(selectPaymentType)
        toolbar.setNavigationIcon(R.drawable.ic_back)
        toolbar.setTitleTextColor(resources.getColor(R.color.black))

        setSupportActionBar(toolbar)
        if (supportActionBar != null) {
            supportActionBar!!.setDisplayHomeAsUpEnabled(true);
            supportActionBar!!.setDisplayShowHomeEnabled(true);
        }

        init()
    }

    private fun init() {
        llDebitCreditCard.setOnClickListener(this)
        llPayPal.setOnClickListener(this)
        llBankAccount.setOnClickListener(this)
        tvAddMore.setOnClickListener(this)
        btnSave.setOnClickListener(this)
        btnCreateAccount.setOnClickListener(this)

        initCardAdapter()

        edtExpiresDate.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

            override fun afterTextChanged(s: Editable?) {
                /* val inputlength = edtCardNumber.getText().toString().length

                 if (inputlength == 4 ||
                     inputlength == 9 || inputlength == 14){

                     edtCardNumber.setText(edtCardNumber.text.toString() + " ");

                     var pos = edtCardNumber.text.toString().length
                     edtCardNumber.setSelection(pos);

                 }*/
                val inputlength = edtExpiresDate.text.toString().length
                if (inputlength == 2) {
                    edtExpiresDate.setText(edtExpiresDate.text.toString().trim() + "/")
                    edtExpiresDate.setSelection(edtExpiresDate.text.toString().length)
                }
                if (inputlength == 5) {
                    edtCVV.requestFocus()
                }
            }

        })

        txtTerms.makeLinks(
            Pair("Terms and Conditions", View.OnClickListener {
                Toast.makeText(applicationContext, "Terms of Service Clicked", Toast.LENGTH_SHORT)
                    .show()
            }),
            Pair("Privacy Policy", View.OnClickListener {
                Toast.makeText(applicationContext, "Privacy Policy Clicked", Toast.LENGTH_SHORT)
                    .show()
            })
        )

        chkIsPayment.onCheckedChange { buttonView, isChecked ->
            if (isChecked)
                llPaymentOptions.visibility = View.VISIBLE
            else
                llPaymentOptions.visibility = View.GONE
        }
    }

    private fun initCardAdapter() {
        adapterCardList = CardListAdapter(R.layout.list_item_card, this)
        rvCard.adapter = adapterCardList

        if (pref?.getCardList()!!.size != 0) {
            llCardList.visibility = View.VISIBLE
            arCardList = pref?.getCardList()!!
            adapterCardList.addAll(arCardList)
            llCardViewDetail.visibility = View.GONE
        } else {
            llCardList.visibility = View.GONE
            llCardViewDetail.visibility = View.VISIBLE
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    override fun getViewActivity(): Activity {
        return this
    }

    override fun onNetworkStateChange(isConnect: Boolean) {
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.cardMain -> {
                val pos = v.tag as Int
                if (selectCardPos != -1) {
                    val data = adapterCardList.getItem(selectCardPos)
                    data.isSelect = false
                    adapterCardList.update(selectCardPos, data)
                }

                val data = adapterCardList.getItem(pos)
                data.isSelect = true
                adapterCardList.update(pos, data)

                selectCardPos = pos;
            }
            R.id.llDebitCreditCard -> {
                selectPaymentType = 1
                binding.selectPaymentType = selectPaymentType
            }
            R.id.llPayPal -> {
                selectPaymentType = 2
                binding.selectPaymentType = selectPaymentType
            }
            R.id.llBankAccount -> {
                selectPaymentType = 3
                binding.selectPaymentType = selectPaymentType
            }
            R.id.tvAddMore -> {
                llCardViewDetail.visibility = View.VISIBLE
            }
            R.id.btnCreateAccount -> {
                startActivity<MainActivity>()
            }
            R.id.btnSave -> {
                arCardList.add(
                    CardListData(
                        edtCardNumber.mCurrentDrawableResId,
                        edtCardNumber.text.toString().trim(),
                        edtCardHolder.text.toString().trim(),
                        edtExpiresDate.text.toString().trim(),
                        edtCVV.text.toString().trim(),
                        false
                    )
                )
                adapterCardList.addAll(arCardList)
                pref?.setCardList(Gson().toJson(arCardList))
                llCardList.visibility = View.VISIBLE
                setClearData()
            }
        }
    }

    private fun setClearData() {
        edtCardNumber.setText("")
        edtCardHolder.setText("")
        edtExpiresDate.setText("")
        edtCVV.setText("")
        llCardViewDetail.visibility = View.GONE
    }


}