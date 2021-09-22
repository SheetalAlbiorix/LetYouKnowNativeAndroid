package com.letyouknow.view.account

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.letyouknow.R
import com.letyouknow.base.BaseFragment
import com.letyouknow.view.account.editinfo.EditInformationActivity
import com.letyouknow.view.account.editlogin.EditLoginActivity
import com.letyouknow.view.account.editnotification.EditNotificationActivity
import com.letyouknow.view.account.editrefer.EditReferActivity
import com.letyouknow.view.dashboard.MainActivity
import kotlinx.android.synthetic.main.fragment_account.*
import org.jetbrains.anko.support.v4.startActivity

class AccountFragment : BaseFragment(), View.OnClickListener {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_account, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init()
    }

    private fun init() {
        ivEditInfo.setOnClickListener(this)
        ivEditLogin.setOnClickListener(this)
        ivEditNotification.setOnClickListener(this)
        ivRefer.setOnClickListener(this)
        MainActivity.getInstance().setVisibleEditImg(false)
        MainActivity.getInstance().setVisibleLogoutImg(true)
    }


    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.ivEditInfo -> {
                startActivity<EditInformationActivity>()
            }
            R.id.ivEditLogin -> {
                startActivity<EditLoginActivity>()
            }
            R.id.ivEditNotification -> {
                startActivity<EditNotificationActivity>()
            }
            R.id.ivRefer -> {
                startActivity<EditReferActivity>()
            }
        }
    }
}