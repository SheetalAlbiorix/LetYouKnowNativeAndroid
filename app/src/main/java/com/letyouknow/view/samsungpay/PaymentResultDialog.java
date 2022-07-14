package com.letyouknow.view.samsungpay;

import android.app.Activity;
import android.app.AlertDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;

import androidx.databinding.DataBindingUtil;

import com.google.gson.Gson;
import com.letyouknow.R;
import com.letyouknow.databinding.AlertPaymentResultBinding;
import com.samsung.android.sdk.samsungpay.v2.SpaySdk;
import com.samsung.android.sdk.samsungpay.v2.payment.CustomSheetPaymentInfo;

import java.lang.ref.WeakReference;

public class PaymentResultDialog {

    private static final String TAG = "PaymentResultDialog";
    private final WeakReference<Activity> mActivityRef;
    AlertPaymentResultBinding viewBinding;
    private AlertDialog mAlertDialog = null;

    public PaymentResultDialog(Activity activity) {
        this.mActivityRef = new WeakReference<>(activity);
        viewBinding = DataBindingUtil.inflate(mActivityRef.get().getLayoutInflater(),
                R.layout.alert_payment_result, null, false);
    }

    public void onSuccessDialog(CustomSheetPaymentInfo paymentInfo, String paymentCredential, Bundle extraPaymentData) {
        try {
            if (mActivityRef == null) {
                return;
            }
            View alertView = setContentTextView(paymentInfo, paymentCredential, extraPaymentData);
            showAlertDialog(alertView);
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }

    private void showAlertDialog(View alertView) {
        if (mAlertDialog != null && mAlertDialog.isShowing()) {
            mAlertDialog.dismiss();
            mAlertDialog = null;
        }
        if (mActivityRef.get() == null || mActivityRef.get().isFinishing() || mActivityRef.get().isDestroyed()) {
            return;
        }

        new AlertDialog.Builder(mActivityRef.get())
                .setPositiveButton(mActivityRef.get().getString(android.R.string.ok), (dialog, which) -> dialog.dismiss())
                .setCancelable(true)
                .setView(alertView)
                .show();
    }

    private String getComboTypeText(String comboType) {
        String combo = mActivityRef.get().getString(R.string.not_requested);
        if (!TextUtils.isEmpty(comboType)) {
            switch (comboType) {
                case "C":
                    combo = mActivityRef.get().getString(R.string.payment_combo_credit);
                    break;
                case "D":
                    combo = mActivityRef.get().getString(R.string.payment_combo_debit);
                    break;
                default:
                    combo = mActivityRef.get().getString(R.string.payment_combo_invalid_type);
            }
        }
        Log.d(TAG, "getComboTypeText = " + combo);
        return combo;
    }

    private View setContentTextView(CustomSheetPaymentInfo paymentInfo, String paymentCredential, Bundle extraPaymentData) {

        PaymentCredentialJson paymentData = new Gson().fromJson(paymentCredential, PaymentCredentialJson.class);
        viewBinding.cardBrand.setText(paymentData.payment_card_brand);
        viewBinding.currencyType.setText(paymentData.payment_currency_type);
        viewBinding.last4dpan.setText(paymentData.payment_last4_dpan);
        viewBinding.last4fpan.setText(paymentData.payment_last4_fpan);
        viewBinding.comboCardType.setText(getComboTypeText(paymentData.combo_debit_credit));
        if (extraPaymentData != null &&
                extraPaymentData.containsKey(SpaySdk.EXTRA_CPF_HOLDER_NAME)) {
            viewBinding.cpfInfoContainer.setVisibility(View.VISIBLE);
            viewBinding.cpfHolderName.setText(extraPaymentData.getString(SpaySdk.EXTRA_CPF_HOLDER_NAME));
            viewBinding.cpfNumber.setText(extraPaymentData.getString(SpaySdk.EXTRA_CPF_NUMBER));
        }

        if (TextUtils.isEmpty(paymentData.payment_shipping_method)) {
            viewBinding.shippingMethod.setText(R.string.not_requested);
            viewBinding.addressContainer.setVisibility(View.GONE);
        } else {
            viewBinding.shippingMethod.setText(paymentData.payment_shipping_method);
            viewBinding.addressContainer.setVisibility(View.VISIBLE);
            viewBinding.name.setText(paymentInfo.getPaymentShippingAddress().getAddressee());
            viewBinding.addressLine1.setText(paymentInfo.getPaymentShippingAddress().getAddressLine1());
            viewBinding.addressLine2.setText(paymentInfo.getPaymentShippingAddress().getAddressLine2());
            viewBinding.city.setText(paymentInfo.getPaymentShippingAddress().getCity());
            viewBinding.state.setText(paymentInfo.getPaymentShippingAddress().getState());
            viewBinding.zip.setText(paymentInfo.getPaymentShippingAddress().getPostalCode());
            viewBinding.countryCode.setText(paymentInfo.getPaymentShippingAddress().getCountryCode());
            viewBinding.email.setText(paymentInfo.getPaymentShippingAddress().getEmail()); // Different
            viewBinding.phoneNumber.setText(paymentInfo.getPaymentShippingAddress().getPhoneNumber()); // Different
        }
        return viewBinding.getRoot();
    }
}