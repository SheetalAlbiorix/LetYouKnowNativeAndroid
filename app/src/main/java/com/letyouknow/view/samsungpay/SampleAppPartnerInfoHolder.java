package com.letyouknow.view.samsungpay;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;

import com.letyouknow.R;
import com.samsung.android.sdk.samsungpay.v2.PartnerInfo;
import com.samsung.android.sdk.samsungpay.v2.SamsungPay;
import com.samsung.android.sdk.samsungpay.v2.SpaySdk;
import com.samsung.android.sdk.samsungpay.v2.payment.PaymentManager;

import java.lang.ref.WeakReference;

public class SampleAppPartnerInfoHolder {
    private static WeakReference<SampleAppPartnerInfoHolder> sInstance;
    private Context mContext;
    private PartnerInfo mPartnerInfo;
    private int mSpayNotReadyReason;

    SampleAppPartnerInfoHolder(Context context) {
        mContext = context;
        Bundle bundle = new Bundle();
        bundle.putString(SamsungPay.PARTNER_SERVICE_TYPE, SpaySdk.ServiceType.INAPP_PAYMENT.toString());
        mPartnerInfo = new PartnerInfo(this.mContext.getString(R.string.samsung_pay_service_id), bundle);
        sInstance = new WeakReference<>(this);
    }

    public static SampleAppPartnerInfoHolder getInstance() {
        return sInstance.get();
    }

    public PartnerInfo getPartnerInfo() {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(mContext);
        boolean isTestModeEnabled = sharedPref.getBoolean(mContext.getString(R.string.pref_key_test_mode), false);
        if (isTestModeEnabled) {
            mPartnerInfo.getData().putBoolean(PaymentManager.EXTRA_KEY_TEST_MODE, true);
        }
        return mPartnerInfo;
    }

    String getSampleAppName() {
        return mContext.getString(R.string.app_name);
    }

    int getSpayNotReadyStatus() {
        return mSpayNotReadyReason;
    }

    void setSpayNotReadyStatus(int reason) {
        mSpayNotReadyReason = reason;
    }
}