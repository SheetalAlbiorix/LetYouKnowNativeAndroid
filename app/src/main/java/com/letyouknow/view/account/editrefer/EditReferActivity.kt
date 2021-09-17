package com.letyouknow.view.account.editrefer

import android.app.Activity
import android.os.Bundle
import android.view.View
import androidx.databinding.DataBindingUtil
import com.letyouknow.R
import com.letyouknow.base.BaseActivity
import com.letyouknow.databinding.ActivityEditReferBinding
import com.letyouknow.model.ReferStepData
import com.letyouknow.view.account.editrefer.referstep2.ReferStep2Activity
import com.letyouknow.view.privacypolicy.PrivacyPolicyTermsCondActivity
import com.pionymessenger.utils.Constant
import com.pionymessenger.utils.Constant.Companion.makeLinks
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
                "Click On the button below and enter the required basic information.",
                "Introduce your friends and family to wonderful services provided by LetYouKnow"
            )
        )
        arRefer.add(
            ReferStepData(
                "They join LetYouKnow!",
                "They like it and complete a transaction with us!"
            )
        )
        arRefer.add(
            ReferStepData(
                "We Pay you!",
                "you can choose between a $100 LetYouKnow credit(to be used on future LetYouKnow purchases), or we will send you a $25 check."
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