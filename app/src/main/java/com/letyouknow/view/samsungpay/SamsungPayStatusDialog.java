package com.letyouknow.view.samsungpay;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.widget.TextView;

import androidx.fragment.app.FragmentActivity;

import com.letyouknow.R;
import com.samsung.android.sdk.samsungpay.v2.SamsungPay;

public class SamsungPayStatusDialog {

    private static SamsungPayStatusDialog mSamsungPayStatusDialog = null;
    private AlertDialog mAlertDialog = null;

    private SamsungPayStatusDialog() {
    }

    public static SamsungPayStatusDialog getInstance() {
        if (mSamsungPayStatusDialog == null) {
            mSamsungPayStatusDialog = new SamsungPayStatusDialog();
        }
        return mSamsungPayStatusDialog;
    }

    public void showSamsungPayStatusErrorDialog(FragmentActivity fragmentActivity, final int error,
                                                DialogInterface.OnClickListener listener) {
        if (mAlertDialog != null && mAlertDialog.isShowing()) {
            mAlertDialog.dismiss();
            mAlertDialog = null;
        }

        if (fragmentActivity != null && !fragmentActivity.isFinishing() && !fragmentActivity.isDestroyed()) {
            AlertDialog.Builder builder = new AlertDialog.Builder(fragmentActivity);
            mAlertDialog = builder.setTitle(getErrorDialogTitle(fragmentActivity, error))
                    .setMessage(getErrorDialogMessage(error))
                    .setPositiveButton(fragmentActivity.getString(android.R.string.ok), listener)
                    .setNegativeButton(fragmentActivity.getString(android.R.string.cancel), listener)
                    .setCancelable(false)
                    .show();
            TextView textView = mAlertDialog.findViewById(android.R.id.message);
            textView.setTextSize(12);
        }
    }

    private String getErrorDialogTitle(Context mContext, int error) {
        switch (error) {
            case SamsungPay.ERROR_SPAY_APP_NEED_TO_UPDATE:
                return mContext.getString(R.string.go_to_samsung_pay_update_popup_title);
            case SamsungPay.ERROR_SPAY_SETUP_NOT_COMPLETED:
                return mContext.getString(R.string.go_to_samsung_pay_start_popup_title);
            default:
                return mContext.getString(R.string.error);
        }
    }

    private String getErrorDialogMessage(int error) {
        return ErrorCode.getInstance().getErrorCodeName(error) + " : " + error;
    }
}