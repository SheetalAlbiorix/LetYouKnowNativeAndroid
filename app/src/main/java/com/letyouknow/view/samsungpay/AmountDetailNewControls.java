package com.letyouknow.view.samsungpay;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;

import com.letyouknow.R;
import com.samsung.android.sdk.samsungpay.v2.payment.sheet.AmountBoxControl;
import com.samsung.android.sdk.samsungpay.v2.payment.sheet.AmountConstants;
import com.samsung.android.sdk.samsungpay.v2.payment.sheet.CustomSheet;

public class AmountDetailNewControls implements TextWatcher {

    private static final String TAG = "AmountDetailControls";
    private static final String AMOUNT_CONTROL_ID = "amountControlId";
    private static final String PRODUCT_ITEM_ID = "productItemId";
    private static final String PRODUCT_TAX_ID = "productTaxId";
    private static final String PRODUCT_SHIPPING_ID = "productShippingId";
    private static final String PRODUCT_FUEL_ID = "productFuelId";
    private static final String DECIMAL_VALUE_ZERO = "00";
    private final Context mContext;
    private final OrderDetailsListener mOrderDetailsListener;
    private double mDiscountedProductAmount = 0;
    private double mTaxAmount = 0;
    private double mShippingAmount = 0;
    private double mAddedShippingAmount = 0;
    private double mAddedBillingAmount = 0;
    private double mProductAmount = 0;

    public AmountDetailNewControls(Context context, Double totalAmount, OrderDetailsListener orderDetailsListener) {
        mContext = context;
        mOrderDetailsListener = orderDetailsListener;
        init(totalAmount);
    }

    private void setProductAmount(double productAmount) {
        this.mProductAmount = productAmount;
    }

    public void setAddedShippingAmount(double amount) {
        mAddedShippingAmount = amount;
    }

    private void init(Double totalAmount) {
        mDiscountedProductAmount = 0;
        mTaxAmount = 0;
        mShippingAmount = 0;
        mAddedShippingAmount = 0;
        mAddedBillingAmount = 0;
        mProductAmount = totalAmount;

    }

    private double getTotalAmount() {
//        mProductAmount = Double.parseDouble("1.0");
//        mTaxAmount = Double.parseDouble("1.0");
//        mShippingAmount = Double.parseDouble("1.0");

//        return mProductAmount + mTaxAmount + mAddedBillingAmount + mShippingAmount + mAddedShippingAmount;
        return mProductAmount;
    }

    public void updateAndCheckAmountValidation() {
        setProductAmount(Double.parseDouble("1.0"));
        mDiscountedProductAmount = mProductAmount;

    }

    private String getAmountFormat() {
        String selectedString = AmountConstants.FORMAT_TOTAL_PRICE_ONLY;
        return selectedString;
    }

    public AmountBoxControl makeAmountControl() {
        AmountBoxControl amountBoxControl = new AmountBoxControl(AMOUNT_CONTROL_ID, "USD");

        amountBoxControl.addItem(PRODUCT_ITEM_ID, mContext.getString(R.string.amount_control_name_item), mDiscountedProductAmount, "");
        amountBoxControl.addItem(PRODUCT_TAX_ID, mContext.getString(R.string.amount_control_name_tax), mTaxAmount + mAddedBillingAmount, "");
        amountBoxControl.addItem(PRODUCT_SHIPPING_ID, mContext.getString(R.string.amount_control_name_shipping), mShippingAmount + mAddedShippingAmount, "");
        amountBoxControl.setAmountTotal(getTotalAmount(), getAmountFormat());

        return amountBoxControl;
    }

    public CustomSheet updateAmountControl(CustomSheet sheet) {
        AmountBoxControl amountBoxControl = (AmountBoxControl) sheet.getSheetControl(AMOUNT_CONTROL_ID);
        if (amountBoxControl == null) {
            Log.e(TAG, "updateAmountControl amountBoxControl : null");
            return sheet;
        }
        amountBoxControl.updateValue(PRODUCT_ITEM_ID, mDiscountedProductAmount);
        amountBoxControl.updateValue(PRODUCT_TAX_ID, mTaxAmount + mAddedBillingAmount);
        amountBoxControl.updateValue(PRODUCT_SHIPPING_ID, mShippingAmount + mAddedShippingAmount);
        if (!amountBoxControl.existItem(PRODUCT_FUEL_ID)) {
            amountBoxControl.addItem(3, PRODUCT_FUEL_ID, mContext.getString(R.string.amount_control_name_fuel), 0, mContext.getString(R.string.amount_control_pending));
        } else {
            amountBoxControl.updateValue(PRODUCT_FUEL_ID, 0, mContext.getString(R.string.amount_control_pending));
        }

        amountBoxControl.setAmountTotal(getTotalAmount(), getAmountFormat());
        sheet.updateControl(amountBoxControl);

        return sheet;
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        //
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        //
    }

    @Override
    public void afterTextChanged(Editable s) {
        //updateAndCheckAmountValidation();
    }
}
