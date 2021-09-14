package com.letyouknow.view.account

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.letyouknow.R
import com.letyouknow.base.BaseFragment
import com.letyouknow.view.account.editlogin.EditLoginActivity
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
        ivEditLogin.setOnClickListener(this)
    }


    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.ivEditLogin -> {
                startActivity<EditLoginActivity>()
            }
        }
    }
}