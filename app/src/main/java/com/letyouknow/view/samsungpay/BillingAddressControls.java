package com.letyouknow.view.samsungpay;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;

import androidx.appcompat.widget.AppCompatTextView;

import com.letyouknow.R;
import com.letyouknow.databinding.CustomBillingAddressControlBinding;
import com.samsung.android.sdk.samsungpay.v2.SpaySdk;
import com.samsung.android.sdk.samsungpay.v2.payment.CustomSheetPaymentInfo;
import com.samsung.android.sdk.samsungpay.v2.payment.PaymentManager;
import com.samsung.android.sdk.samsungpay.v2.payment.sheet.AddressControl;
import com.samsung.android.sdk.samsungpay.v2.payment.sheet.SheetItemType;
import com.samsung.android.sdk.samsungpay.v2.payment.sheet.SheetUpdatedListener;

class BillingAddressControls {
    static final String BILLING_ADDRESS_ID = "billingAddressControlId";

    private final Context mContext;
    CustomBillingAddressControlBinding mBinding;
    private boolean mNeedCustomErrorMessage;

    BillingAddressControls(Context context, CustomBillingAddressControlBinding binding) {
        mContext = context;
        mBinding = binding;
        initView();
    }

    private void initView() {
        mBinding.returnBillingAddressErrorType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String displayText = ((AppCompatTextView) view).getText().toString();
                if (TextUtils.equals(displayText, mContext.getResources().getString(R.string.custom_error_message_list_item))) {
                    mBinding.customErrorMessageForBilling.setVisibility(View.VISIBLE);
                } else {
                    mBinding.customErrorMessageForBilling.setVisibility(View.GONE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                //
            }
        });
    }

    boolean needBillingControl() {
        return mBinding.billingAddressCheckBox.isChecked();
    }

    AddressControl makeBillingAddress(SheetUpdatedListener billingListener) {
        AddressControl billingAddressControl = new AddressControl(BILLING_ADDRESS_ID, SheetItemType.BILLING_ADDRESS);
        billingAddressControl.setAddressTitle(mContext.getString(R.string.billing_address));
        billingAddressControl.setSheetUpdatedListener(billingListener);

        return billingAddressControl;
    }

    int validateBillingAddress(CustomSheetPaymentInfo.Address address) {
        mNeedCustomErrorMessage = false;
        if (address == null) {
            return PaymentManager.ERROR_BILLING_ADDRESS_INVALID;
        }

        int ret;
        String displayText = mBinding.returnBillingAddressErrorType.getSelectedItem().toString();
        switch (displayText) {
            case "ERROR_BILLING_ADDRESS_INVALID":
                ret = PaymentManager.ERROR_BILLING_ADDRESS_INVALID;
                break;
            case "ERROR_BILLING_ADDRESS_NOT_EXIST":
                ret = PaymentManager.ERROR_BILLING_ADDRESS_NOT_EXIST;
                break;
            case "CUSTOM_ERROR_MESSAGE":
                mNeedCustomErrorMessage = true;
                ret = SpaySdk.ERROR_NONE;
                break;
            case "ERROR_NONE":
            default:
                ret = SpaySdk.ERROR_NONE;
        }
        return ret;
    }

    void updateBillingLayoutVisibility(CustomSheetPaymentInfo.AddressInPaymentSheet type) {
        switch (type) {
            case NEED_BILLING_SPAY:
            case NEED_BILLING_AND_SHIPPING:
            case NEED_BILLING_SEND_SHIPPING:
                mBinding.getRoot().setVisibility(View.VISIBLE);
                mBinding.billingAddressCheckBox.setChecked(true);
                break;
            case DO_NOT_SHOW:
            case SEND_SHIPPING:
            case NEED_SHIPPING_SPAY:
            default:
                mBinding.getRoot().setVisibility(View.GONE);
                mBinding.billingAddressCheckBox.setChecked(false);
        }
    }

    boolean needCustomErrorMessage() {
        return mNeedCustomErrorMessage;
    }

    String getCustomErrorMessage() {
        return mBinding.customErrorMessageForBilling.getText().toString();
    }
}