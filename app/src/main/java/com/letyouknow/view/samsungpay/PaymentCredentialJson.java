package com.letyouknow.view.samsungpay;

import com.google.gson.annotations.SerializedName;

public class PaymentCredentialJson {
    @SerializedName("3DS")
    public CryptoDataJson data;
    public String payment_card_brand;
    public String payment_currency_type;
    public String payment_last4_dpan;
    public String payment_last4_fpan;
    public String combo_debit_credit;
    public String payment_shipping_method;
}
