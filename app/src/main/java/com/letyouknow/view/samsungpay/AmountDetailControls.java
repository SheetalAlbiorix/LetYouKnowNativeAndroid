package com.letyouknow.view.samsungpay;

import android.content.Context;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import com.letyouknow.R;
import com.letyouknow.databinding.CustomAmountDetailsBinding;
import com.samsung.android.sdk.samsungpay.v2.payment.sheet.AmountBoxControl;
import com.samsung.android.sdk.samsungpay.v2.payment.sheet.AmountConstants;
import com.samsung.android.sdk.samsungpay.v2.payment.sheet.CustomSheet;

import java.util.Locale;

public class AmountDetailControls implements TextWatcher {

    private static final String TAG = "AmountDetailControls";
    private static final String AMOUNT_CONTROL_ID = "amountControlId";
    private static final String PRODUCT_ITEM_ID = "productItemId";
    private static final String PRODUCT_TAX_ID = "productTaxId";
    private static final String PRODUCT_SHIPPING_ID = "productShippingId";
    private static final String PRODUCT_FUEL_ID = "productFuelId";
    private static final String DECIMAL_VALUE_ZERO = "00";
    private final Context mContext;
    private final OrderDetailsListener mOrderDetailsListener;
    private final CustomAmountDetailsBinding mBinding;
    private double mDiscountedProductAmount = 1000;
    private double mTaxAmount = 50;
    private double mShippingAmount = 10;
    private double mAddedShippingAmount = 0;
    private double mAddedBillingAmount = 0;
    private double mProductAmount = 1000;

    AmountDetailControls(Context context, CustomAmountDetailsBinding binding, OrderDetailsListener orderDetailsListener) {
        mContext = context;
        mOrderDetailsListener = orderDetailsListener;
        mBinding = binding;
        init();
    }

    private void setProductAmount(double productAmount) {
        this.mProductAmount = productAmount;
    }

    void setAddedShippingAmount(double amount) {
        mAddedShippingAmount = amount;
    }

    private void init() {
        mBinding.amountViewMoreCheckBox.setOnCheckedChangeListener(((buttonView, isChecked) ->
                mBinding.amountDetailOthers.setVisibility(isChecked ? View.VISIBLE : View.GONE)));

        mDiscountedProductAmount = 1000;
        mTaxAmount = 50;
        mShippingAmount = 10;
        mAddedShippingAmount = 0;
        mAddedBillingAmount = 0;
        mProductAmount = 1000;

        mBinding.price.setText("1000");
        mBinding.priceDec.setText(DECIMAL_VALUE_ZERO);
        mBinding.tax.setText("50");
        mBinding.taxDec.setText(DECIMAL_VALUE_ZERO);
        mBinding.shippingCost.setText("10");
        mBinding.shippingCostDec.setText(DECIMAL_VALUE_ZERO);
        mBinding.totalAmountValue.setText(String.format(Locale.US, "%.2f", getTotalAmount()));

        mBinding.price.addTextChangedListener(this);
        mBinding.priceDec.addTextChangedListener(this);
        mBinding.tax.addTextChangedListener(this);
        mBinding.priceDec.addTextChangedListener(this);
        mBinding.shippingCost.addTextChangedListener(this);
        mBinding.shippingCostDec.addTextChangedListener(this);
        mBinding.tax.addTextChangedListener(this);
        mBinding.taxDec.addTextChangedListener(this);
    }

    private double getTotalAmount() {
        mProductAmount = Double.parseDouble(getPriceValue(mBinding.price, mBinding.priceDec));
        mTaxAmount = Double.parseDouble(getPriceValue(mBinding.tax, mBinding.taxDec));
        mShippingAmount = Double.parseDouble(getPriceValue(mBinding.shippingCost, mBinding.shippingCostDec));

        return mProductAmount + mTaxAmount + mAddedBillingAmount + mShippingAmount + mAddedShippingAmount;
    }

    private String getPriceValue(EditText price, EditText priceDec) {
        String left = "0";
        if (!TextUtils.isEmpty(price.getText())) {
            left = price.getText().toString();
        }

        String right = DECIMAL_VALUE_ZERO;
        if (!TextUtils.isEmpty(priceDec.getText())) {
            right = priceDec.getText().toString();
        }
        return left + "." + right;
    }

    private boolean isValidAmount() {
        if (TextUtils.isEmpty(getPriceValue(mBinding.price, mBinding.priceDec)) ||
                TextUtils.isEmpty(getPriceValue(mBinding.shippingCost, mBinding.shippingCostDec)) ||
                TextUtils.isEmpty(getPriceValue(mBinding.tax, mBinding.priceDec))) {
            mOrderDetailsListener.setButtonState(false);
            return false;
        }
        mOrderDetailsListener.setButtonState(true);
        return true;
    }

    void updateAndCheckAmountValidation() {
        if (isValidAmount()) {
            // Set original Product Price
            setProductAmount(Double.parseDouble(getPriceValue(mBinding.price, mBinding.priceDec)));
            mDiscountedProductAmount = mProductAmount;
            mBinding.totalAmountValue.setText(String.format(Locale.US, "%.2f", getTotalAmount()));
        }
    }

    void updateAmount() {
        mDiscountedProductAmount = mProductAmount;
        mBinding.totalAmountValue.setText(String.format(Locale.US, "%.2f", getTotalAmount()));
    }

    private String getAmountFormat() {
        String selectedString = mBinding.formatAmount.getSelectedItem().toString();
        switch (selectedString) {
            case "FORMAT_TOTAL_PRICE_ONLY":
                selectedString = AmountConstants.FORMAT_TOTAL_PRICE_ONLY;
                break;
            case "FORMAT_TOTAL_ESTIMATED_AMOUNT":
                selectedString = AmountConstants.FORMAT_TOTAL_ESTIMATED_AMOUNT;
                break;
            case "FORMAT_TOTAL_ESTIMATED_CHARGE":
                selectedString = AmountConstants.FORMAT_TOTAL_ESTIMATED_CHARGE;
                break;
            case "FORMAT_TOTAL_ESTIMATED_FARE":
                selectedString = AmountConstants.FORMAT_TOTAL_ESTIMATED_FARE;
                break;
            case "FORMAT_TOTAL_FREE_TEXT_ONLY":
                selectedString = AmountConstants.FORMAT_TOTAL_FREE_TEXT_ONLY;
                break;
            case "FORMAT_TOTAL_AMOUNT_PENDING":
                selectedString = AmountConstants.FORMAT_TOTAL_AMOUNT_PENDING;
                break;
            case "FORMAT_TOTAL_AMOUNT_PENDING_TEXT_ONLY":
                selectedString = AmountConstants.FORMAT_TOTAL_AMOUNT_PENDING_TEXT_ONLY;
                break;
            case "FORMAT_TOTAL_PENDING":
                selectedString = AmountConstants.FORMAT_TOTAL_PENDING;
                break;
            case "FORMAT_TOTAL_PENDING_TEXT_ONLY":
                selectedString = AmountConstants.FORMAT_TOTAL_PENDING_TEXT_ONLY;
                break;
            default:
                Log.e("SampleApp", "Wrong Amount Format!");
        }
        return selectedString;
    }

    AmountBoxControl makeAmountControl() {
        AmountBoxControl amountBoxControl
                = new AmountBoxControl(AMOUNT_CONTROL_ID, mBinding.currency.getSelectedItem().toString());

        amountBoxControl.addItem(PRODUCT_ITEM_ID, mContext.getString(R.string.amount_control_name_item), mDiscountedProductAmount, "");
        amountBoxControl.addItem(PRODUCT_TAX_ID, mContext.getString(R.string.amount_control_name_tax), mTaxAmount + mAddedBillingAmount, "");
        amountBoxControl.addItem(PRODUCT_SHIPPING_ID, mContext.getString(R.string.amount_control_name_shipping), mShippingAmount + mAddedShippingAmount, "");
        amountBoxControl.setAmountTotal(getTotalAmount(), getAmountFormat());

        return amountBoxControl;
    }

    CustomSheet updateAmountControl(CustomSheet sheet) {
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
        updateAndCheckAmountValidation();
    }
}
