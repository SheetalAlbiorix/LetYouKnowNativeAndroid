package com.letyouknow.view.home.dealsummery

import android.app.Activity
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.google.gson.Gson
import com.letyouknow.R
import com.letyouknow.base.BaseActivity
import com.letyouknow.databinding.ActivityDealSummeryBinding
import com.letyouknow.model.FindLCDDealGuestData
import com.letyouknow.retrofit.ApiConstant
import com.letyouknow.retrofit.viewmodel.ImageIdViewModel
import com.letyouknow.retrofit.viewmodel.ImageUrlViewModel
import com.letyouknow.utils.AppGlobal.Companion.loadImageUrl
import com.letyouknow.utils.AppGlobal.Companion.setSpinnerTextColor
import com.letyouknow.utils.AppGlobal.Companion.setSpinnerTextColorPos
import com.letyouknow.view.home.dealsummery.delasummreystep2.DealSummeryStep2Activity
import com.letyouknow.view.home.dealsummery.gallery360view.Gallery360TabActivity
import com.pionymessenger.utils.Constant
import com.pionymessenger.utils.Constant.Companion.ARG_LCD_DEAL_GUEST
import com.pionymessenger.utils.Constant.Companion.ARG_TYPE_VIEW
import com.pionymessenger.utils.Constant.Companion.makeLinks
import kotlinx.android.synthetic.main.activity_deal_summery.*
import kotlinx.android.synthetic.main.layout_deal_summery.*
import kotlinx.android.synthetic.main.layout_toolbar_blue.*
import kotlinx.android.synthetic.main.layout_toolbar_blue.toolbar
import org.jetbrains.anko.startActivity

class DealSummeryActivity : BaseActivity(), View.OnClickListener,
    AdapterView.OnItemSelectedListener {
    private var arLoan = arrayListOf("LOAN", "CARD")
    private var arImageUrl: ArrayList<String> = ArrayList()

    private lateinit var imageIdViewModel: ImageIdViewModel
    private lateinit var imageUrlViewModel: ImageUrlViewModel


    private lateinit var binding: ActivityDealSummeryBinding
    private lateinit var dataLCDDeal: FindLCDDealGuestData

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_deal_summery)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_deal_summery)
//        window.decorView.layoutDirection = View.LAYOUT_DIRECTION_RTL
        init()
    }

    private fun init() {
        imageIdViewModel = ViewModelProvider(this).get(ImageIdViewModel::class.java)
        imageUrlViewModel = ViewModelProvider(this).get(ImageUrlViewModel::class.java)

        if (intent.hasExtra(ARG_LCD_DEAL_GUEST)) {
            dataLCDDeal = Gson().fromJson(
                intent.getStringExtra(ARG_LCD_DEAL_GUEST),
                FindLCDDealGuestData::class.java
            )
            callImageIdAPI()
            binding.lcdDealData = dataLCDDeal

        }
        txtTerms.text =
            resources.getString(R.string.i_have_read_accept, resources.getString(R.string.app_name))
        setLoan()
        setInfoLink()
        setPrivacyPolicyLink()


        btnProceedDeal.setOnClickListener(this)
        ivBackDeal.setOnClickListener(this)
        ivEdit.setOnClickListener(this)
        ivForwardDeal.setOnClickListener(this)
        llGallery.setOnClickListener(this)
        ll360.setOnClickListener(this)
//        MainActivity.getInstance().setVisibleEditImg
        backButton()
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

    override fun getViewActivity(): Activity? {
        return this
    }

    override fun onNetworkStateChange(isConnect: Boolean) {
    }

    private fun setLoan() {
        val adapterLoan = ArrayAdapter<String?>(
            this,
            android.R.layout.simple_spinner_item,
            arLoan as List<String?>
        )
        adapterLoan.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spLoan.adapter = adapterLoan
        setSpinnerTextColor(spLoan, this)
        spLoan.onItemSelectedListener = this
    }

    private fun setInfoLink() {
        txtInformation.makeLinks(
            Pair("information here", View.OnClickListener {
                Toast.makeText(this, "Information Clicked", Toast.LENGTH_SHORT)
                    .show()
            })
        )
    }

    private fun setPrivacyPolicyLink() {

        txtTerms.makeLinks(
            Pair("Terms and Conditions", View.OnClickListener {
                Toast.makeText(this, "Terms of Service Clicked", Toast.LENGTH_SHORT)
                    .show()
            }),
            Pair("Privacy Policy", View.OnClickListener {
                Toast.makeText(this, "Privacy Policy Clicked", Toast.LENGTH_SHORT)
                    .show()
            })
        )
    }

    private fun callImageIdAPI() {
        if (Constant.isOnline(this)) {
            Constant.showLoader(this)
            val request = HashMap<String, Any>()
            dataLCDDeal.run {
                request[ApiConstant.vehicleYearID] = yearId!!
                request[ApiConstant.vehicleMakeID] = makeId!!
                request[ApiConstant.vehicleModelID] = modelId!!
                request[ApiConstant.vehicleTrimID] = trimId!!
            }

            imageIdViewModel.imageIdCall(this, request)!!
                .observe(this, Observer { data ->
                    Constant.dismissLoader()

                    callImageUrlAPI(data)
                }
                )

        } else {
            Toast.makeText(this, Constant.noInternet, Toast.LENGTH_SHORT).show()
        }
    }

    private fun callImageUrlAPI(ImageId: String) {
        if (Constant.isOnline(this)) {
            Constant.showLoader(this)

            val request = HashMap<String, Any>()

            request[ApiConstant.ImageId] = ImageId!!
            request[ApiConstant.ImageProduct] = "Splash"

            imageUrlViewModel.imageUrlCall(this, request)!!
                .observe(this, Observer { data ->
                    Constant.dismissLoader()
                    if (data.isNotEmpty()) {

                        arImageUrl = data
                        loadImageUrl(this, ivMain, arImageUrl[0])
                        loadImageUrl(this, ivBg360, arImageUrl[0])
                        loadImageUrl(this, ivBgGallery, arImageUrl[0])
                    }
                }
                )

        } else {
            Toast.makeText(this, Constant.noInternet, Toast.LENGTH_SHORT).show()
        }
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.btnProceedDeal -> {
                startActivity<DealSummeryStep2Activity>()
                finish()
                //loadFragment(DealSummeryStep2Activity())
            }
            R.id.ivBackDeal -> {
                onBackPressed()
            }
            R.id.ivEdit -> {
                onBackPressed()
            }
            R.id.ivForwardDeal -> {
                startActivity<DealSummeryStep2Activity>()
                finish()
            }
            R.id.llGallery -> {
                startActivity<Gallery360TabActivity>(ARG_TYPE_VIEW to 0)
            }
            R.id.ll360 -> {
                startActivity<Gallery360TabActivity>(ARG_TYPE_VIEW to 2)
            }
        }
    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {

        when (parent?.id) {
            R.id.spLoan -> {
                setSpinnerTextColorPos(position, spLoan, this)
            }
        }
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {

    }


    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

}