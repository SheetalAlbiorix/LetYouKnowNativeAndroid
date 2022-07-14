package com.letyouknow.view.samsungpay;

import android.view.View;
import android.widget.AdapterView;
import android.widget.Spinner;

import androidx.appcompat.widget.AppCompatTextView;

import com.letyouknow.databinding.CustomAddressControlsTitleBinding;
import com.samsung.android.sdk.samsungpay.v2.payment.CustomSheetPaymentInfo.AddressInPaymentSheet;

class RequestAddressOptions {
    private final AddressRequestListener mAddressRequestListener;

    // Address Display on the Payment Sheet, Default value from Merchant
    private AddressInPaymentSheet mAddressRequestType = AddressInPaymentSheet.DO_NOT_SHOW;

    RequestAddressOptions(CustomAddressControlsTitleBinding binding, AddressRequestListener listener) {
        mAddressRequestListener = listener;
        Spinner requestType = binding.requestAddress;

        requestType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String displayText = ((AppCompatTextView) view).getText().toString();
                switch (displayText) {
                    case "Only Billing Address (SPay)":
                        mAddressRequestType = AddressInPaymentSheet.NEED_BILLING_SPAY;
                        break;
                    case "Only Shipping Address (SPay)":
                        mAddressRequestType = AddressInPaymentSheet.NEED_SHIPPING_SPAY;
                        break;
                    case "Only Shipping Address (Merchant)":
                        mAddressRequestType = AddressInPaymentSheet.SEND_SHIPPING;
                        break;
                    case "Billing (SPay), Shipping (Merchant)":
                        mAddressRequestType = AddressInPaymentSheet.NEED_BILLING_SEND_SHIPPING;
                        break;
                    case "Billing and Shipping Addresses (SPay)":
                        mAddressRequestType = AddressInPaymentSheet.NEED_BILLING_AND_SHIPPING;
                        break;
                    case "No Billing/Shipping Address":
                    default:
                        mAddressRequestType = AddressInPaymentSheet.DO_NOT_SHOW;
                }
                mAddressRequestListener.onChange(mAddressRequestType);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                //
            }
        });
    }

    AddressInPaymentSheet getRequestAddressType() {
        return mAddressRequestType;
    }
}