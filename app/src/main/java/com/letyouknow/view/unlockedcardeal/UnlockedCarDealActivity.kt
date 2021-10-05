package com.letyouknow.view.unlockedcardeal

import android.app.Activity
import android.os.Bundle
import android.view.View
import androidx.databinding.DataBindingUtil
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.letyouknow.R
import com.letyouknow.base.BaseActivity
import com.letyouknow.databinding.ActivityUnlockedCarDealBinding
import com.letyouknow.model.FindUcdDealGuestData
import com.pionymessenger.utils.Constant.Companion.ARG_RADIUS
import com.pionymessenger.utils.Constant.Companion.ARG_UCD_DEAL
import kotlinx.android.synthetic.main.activity_unlocked_car_deal.*
import kotlinx.android.synthetic.main.layout_toolbar.toolbar

class UnlockedCarDealActivity : BaseActivity(), View.OnClickListener {
    private var arUnlocked: ArrayList<FindUcdDealGuestData> = ArrayList()
    private lateinit var adapterUnlockedCarDeal: UnlockedCarDealAdapter
    private var searchRadius = ""

    private lateinit var binding: ActivityUnlockedCarDealBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_unlocked_car_deal)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_unlocked_car_deal)
        init()
    }

    private fun init() {
        if (intent.hasExtra(ARG_UCD_DEAL) && intent.hasExtra(ARG_RADIUS)) {
            arUnlocked = Gson().fromJson(intent.getStringExtra(ARG_UCD_DEAL),
                object : TypeToken<ArrayList<FindUcdDealGuestData>?>() {}.type)
            searchRadius = intent.getStringExtra(ARG_RADIUS)!!
            binding.searchRadius =
                getString(R.string.search_radius_100_miles, searchRadius.replace("mi", "").trim())
        }
        backButton()
        adapterUnlockedCarDeal = UnlockedCarDealAdapter(R.layout.list_item_unlocked_car, this)
        rvUnlockedCar.adapter = adapterUnlockedCarDeal
        adapterUnlockedCarDeal.addAll(arUnlocked)
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
        TODO("Not yet implemented")
    }

    private var selectPos = -1
    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.cardUnlocked -> {
                val pos = v.tag as Int
                if (selectPos != -1) {
                    val data = adapterUnlockedCarDeal.getItem(selectPos)
                    data.isSelect = false
                    adapterUnlockedCarDeal.update(selectPos, data)
                }

                val data = adapterUnlockedCarDeal.getItem(pos)
                data.isSelect = true
                adapterUnlockedCarDeal.update(pos, data)
                selectPos = pos
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}