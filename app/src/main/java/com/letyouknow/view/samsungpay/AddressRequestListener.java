package com.letyouknow.view.samsungpay;

import com.samsung.android.sdk.samsungpay.v2.payment.CustomSheetPaymentInfo.AddressInPaymentSheet;

public interface AddressRequestListener {
    void onChange(AddressInPaymentSheet type);
}

