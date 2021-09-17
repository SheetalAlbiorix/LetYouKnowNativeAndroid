package com.letyouknow.view.account.editrefer.referstep2

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.ClipboardManager
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.share.Sharer
import com.facebook.share.model.ShareLinkContent
import com.facebook.share.widget.ShareDialog
import com.letyouknow.R
import com.letyouknow.base.BaseActivity
import com.letyouknow.databinding.ActivityReferStep2Binding
import com.letyouknow.view.privacypolicy.PrivacyPolicyTermsCondActivity
import com.pionymessenger.utils.Constant
import com.pionymessenger.utils.Constant.Companion.makeLinks
import kotlinx.android.synthetic.main.activity_refer_step2.*
import kotlinx.android.synthetic.main.layout_toolbar.toolbar
import org.jetbrains.anko.startActivity

class ReferStep2Activity : BaseActivity(), View.OnClickListener {
    private lateinit var binding: ActivityReferStep2Binding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_refer_step2)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_refer_step2)
        init()
    }

    private fun init() {
        tvCopy.setOnClickListener(this)
        llFBShareLink.setOnClickListener(this)
        llShareSMS.setOnClickListener(this)
        llShareEmail.setOnClickListener(this)
        llShareTwitter.setOnClickListener(this)
        backButton()
        setTermsLink()
        initFaceBookShare()
    }

    private fun setTermsLink() {
        txtRewards.makeLinks(
            Pair("Terms and Conditions", View.OnClickListener {
                startActivity<PrivacyPolicyTermsCondActivity>(Constant.ARG_POLICY to Constant.TERMS_CONDITIONS_LINK)
            })
        )
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
            R.id.tvCopy -> {
                val clipboardManager =
                    getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                clipboardManager.text = tvLink.text
                Toast.makeText(this, "Copied to Clipboard", Toast.LENGTH_SHORT).show()
            }
            R.id.llFBShareLink -> {
                shareLinkShare()
            }
            R.id.llShareSMS -> {
                composeSmsMessage()
            }
            R.id.llShareTwitter -> {
                shareTwitter()
            }
            R.id.llShareEmail -> {
                shareEmail()
            }
        }
    }

    private fun shareTwitter() {
        val shareIntent = Intent(Intent.ACTION_SEND)
        shareIntent.type = "text/plain"
        shareIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        shareIntent.setClassName(
            "com.twitter.android",
            "com.twitter.composer.ComposerShareActivity"
        )
        startActivity(shareIntent)
    }

    //Share Via Email
    private fun shareEmail() {
        val emailIntent = Intent(Intent.ACTION_VIEW)
        emailIntent.type = "vnd.android.cursor.dir/email"
        val to = arrayOf("sejal.albiorix@gmail.com")
        emailIntent.putExtra(Intent.EXTRA_EMAIL, to)
        emailIntent.putExtra(Intent.EXTRA_TEXT, "https://www.lyk.com/user1/referral")
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.app_name))
        startActivity(Intent.createChooser(emailIntent, "Send mail..."))
    }

    //Share Via SMS
    private fun composeSmsMessage() {
        val intent = Intent(android.content.Intent.ACTION_VIEW)
        val body = "https://www.lyk.com/user1/referral"
//        intent.type = "text/plain"
        intent.data = Uri.parse("smsto:1234567890")
        intent.type = "vnd.android-dir/mms-sms"
        intent.putExtra("address", 1234567890)
        intent.putExtra("sms_body", body)
        startActivity(intent)
    }

    //share Facebook
    private lateinit var callbackManager: CallbackManager
    private lateinit var shareDialog: ShareDialog

    private fun initFaceBookShare() {
        callbackManager = CallbackManager.Factory.create()
        shareDialog = ShareDialog(this)
        shareDialog.registerCallback(callbackManager, callback)
        shareLinkShare()
    }

    private fun shareLinkShare() {
        fb_share_button.performClick()
        val content = ShareLinkContent.Builder()
            .setContentTitle("Tutorialwing - Free programming tutorials")
//            .setImageUrl(Uri.parse("https://scontent-sin6-1.xx.fbcdn.net/t31.0-8/13403381_247495578953089_8113745370016563192_o.png"))
//            .setContentDescription("Tutorialwing is an online platform for free programming tutorials. These tutorials are designed for beginners as well as experienced programmers.")
            .setContentUrl(Uri.parse("https://www.lyk.com/user1/referral"))
            .setQuote("Learn and share your knowledge")
            .build()
        fb_share_button.shareContent = content
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        callbackManager.onActivityResult(requestCode, resultCode, data)
    }

    private val callback = object : FacebookCallback<Sharer.Result> {
        override fun onSuccess(result: Sharer.Result?) {
            Log.v(TAG, "Successfully posted")
        }

        override fun onCancel() {
            Log.v(TAG, "Sharing cancelled")
        }

        override fun onError(error: FacebookException?) {
            Log.v(TAG, error?.message!!)
        }

    }
}