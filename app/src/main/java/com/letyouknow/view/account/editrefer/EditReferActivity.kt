package com.letyouknow.view.account.editrefer

import android.app.Activity
import android.os.Bundle
import android.view.View
import androidx.databinding.DataBindingUtil
import com.letyouknow.R
import com.letyouknow.base.BaseActivity
import com.letyouknow.databinding.ActivityEditReferBinding
import com.letyouknow.model.ReferStepData
import com.letyouknow.utils.Constant
import com.letyouknow.utils.Constant.Companion.makeLinks
import com.letyouknow.view.account.editrefer.referstep2.ReferStep2Activity
import com.letyouknow.view.privacypolicy.PrivacyPolicyTermsCondActivity
import kotlinx.android.synthetic.main.activity_edit_refer.*
import kotlinx.android.synthetic.main.layout_toolbar.toolbar
import org.jetbrains.anko.startActivity

class EditReferActivity : BaseActivity(), View.OnClickListener {
    private lateinit var binding: ActivityEditReferBinding
    private lateinit var adapterReferStep: ReferStepAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_refer)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_edit_refer)
        init()
    }

    private fun init() {
        backButton()
        setReferStepAdapter()
        btnLetTheKnow.setOnClickListener(this)
        setTermsLink()
    }

    private fun setTermsLink() {
        txtTerms.makeLinks(
            Pair(getString(R.string.friends_and_family), View.OnClickListener {
                startActivity<PrivacyPolicyTermsCondActivity>(Constant.ARG_POLICY to Constant.TERMS_CONDITIONS_LINK)
            }),

            )
    }

    private val arRefer: ArrayList<ReferStepData> = ArrayList()
    private fun setReferStepAdapter() {
        arRefer.add(
            ReferStepData(
                resources.getString(R.string.click_on_button_below),
                resources.getString(R.string.introduce_your_friends)
            )
        )
        arRefer.add(
            ReferStepData(
                resources.getString(R.string.they_join_letYouKnow),
                resources.getString(R.string.they_like_it_complete)
            )
        )
        arRefer.add(
            ReferStepData(
                resources.getString(R.string.we_pay_you),
                resources.getString(R.string.you_can_choose_between)
            )
        )
        adapterReferStep = ReferStepAdapter(R.layout.list_item_refer_step, this)
        rvStep.adapter = adapterReferStep
        adapterReferStep.addAll(arRefer)
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

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.btnLetTheKnow -> {
                startActivity<ReferStep2Activity>()
            }
        }
    }
}