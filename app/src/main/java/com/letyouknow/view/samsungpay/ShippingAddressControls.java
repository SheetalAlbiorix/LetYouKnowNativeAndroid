package com.letyouknow.view.samsungpay;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.RadioGroup;

import androidx.appcompat.widget.AppCompatTextView;

import com.letyouknow.R;
import com.letyouknow.databinding.CustomShippingAddressControlBinding;
import com.samsung.android.sdk.samsungpay.v2.SpaySdk;
import com.samsung.android.sdk.samsungpay.v2.payment.CustomSheetPaymentInfo;
import com.samsung.android.sdk.samsungpay.v2.payment.PaymentManager;
import com.samsung.android.sdk.samsungpay.v2.payment.sheet.AddressConstants;
import com.samsung.android.sdk.samsungpay.v2.payment.sheet.AddressControl;
import com.samsung.android.sdk.samsungpay.v2.payment.sheet.SheetItemType;
import com.samsung.android.sdk.samsungpay.v2.payment.sheet.SheetUpdatedListener;
import com.samsung.android.sdk.samsungpay.v2.payment.sheet.SpinnerControl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Locale;

class ShippingAddressControls {

    static final String SHIPPING_ADDRESS_ID = "shippingAddressControlId";
    static final String SHIPPING_METHOD_SPINNER_ID = "shippingMethodSpinnerControlId";
    static final String SHIPPING_METHOD_1 = "shipping_method1";
    static final String SHIPPING_METHOD_2 = "shipping_method2";
    static final String SHIPPING_METHOD_3 = "shipping_method3";
    private static final String TAG = "ShippingAddressControls";
    private final Context mContext;
    CustomShippingAddressControlBinding mBinding;
    private ArrayAdapter<String> mCountryAdapter;
    private ShippingMethodListener mShippingMethodChangedListener;
    private boolean mIsCustomErrorMessage;
    private boolean mNeedAllShippingMethodItems;

    ShippingAddressControls(Context context, CustomShippingAddressControlBinding binding) {
        mContext = context;
        mBinding = binding;
        init();
    }

    private void init() {
        mBinding.returnShippingAddressErrorType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String displayText = ((AppCompatTextView) view).getText().toString();
                if (TextUtils.equals(displayText, mContext.getResources().getString(R.string.custom_error_message_list_item))) {
                    mBinding.customErrorMessageForShipping.setVisibility(View.VISIBLE);
                } else {
                    mBinding.customErrorMessageForShipping.setVisibility(View.GONE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                //
            }
        });

        mBinding.shippingMethodGroup.setOnCheckedChangeListener((RadioGroup group, int checkedId) -> {
            int index = group.indexOfChild(group.findViewById(checkedId));
            Log.d(TAG, "index = " + index);
            double extraShippingFee = 0;

            switch (index) {
                case 1:
                    extraShippingFee = 0.1;
                    break;
                case 2:
                    extraShippingFee = 0.2;
                    break;
                default:
                    break;
            }
            mShippingMethodChangedListener.updateAmount(extraShippingFee);
        });

        createCountryList();
        mBinding.country.setSelection(mCountryAdapter.getPosition("USA"));

        updateShippingOptionVisibility(false);
        updateShippingAddressFieldsFromPartner(false);
    }

    boolean needSendShippingControl() {
        return mBinding.shippingAddressCheckBox.isChecked();
    }

    AddressControl makeShippingAddress(SheetUpdatedListener shippingListener) {
        AddressControl shippingAddressControl = new AddressControl(SHIPPING_ADDRESS_ID, SheetItemType.SHIPPING_ADDRESS);

        shippingAddressControl.setAddress(buildShippingAddressInfo());
        shippingAddressControl.setAddressTitle(mContext.getString(R.string.shipping_address));
        shippingAddressControl.setSheetUpdatedListener(shippingListener);

        int displayOptionValue = AddressConstants.DISPLAY_OPTION_ADDRESSEE;

        if (mBinding.displayOptionAddress.isChecked()) {
            displayOptionValue += AddressConstants.DISPLAY_OPTION_ADDRESS;
        }
        if (mBinding.displayOptionPhoneNumber.isChecked()) {
            displayOptionValue += AddressConstants.DISPLAY_OPTION_PHONE_NUMBER;
        }
        if (mBinding.displayOptionEmail.isChecked()) {
            displayOptionValue += AddressConstants.DISPLAY_OPTION_EMAIL;
        }

        shippingAddressControl.setDisplayOption(displayOptionValue);
        return shippingAddressControl;
    }

    private void updateShippingAddressFieldsFromPartner(boolean visible) {
        mBinding.shippingAddressFromPartnerLayout.setVisibility(visible ? View.VISIBLE : View.GONE);
    }

    private void updateShippingOptionVisibility(boolean visible) {
        mBinding.getRoot().setVisibility(visible ? View.VISIBLE : View.GONE);

        if (visible) {
            mBinding.shippingMethodDescription.setVisibility(View.VISIBLE);
            mBinding.shippingMethodDescription.setText(mNeedAllShippingMethodItems ?
                    R.string.provide_all_options : R.string.provide_only_one_option);
        } else {
            mBinding.shippingMethodDescription.setVisibility(View.GONE);
        }
    }

    private void createCountryList() {
        String[] codes = Locale.getISOCountries();
        ArrayList<String> countryList = new ArrayList<>();

        for (String code2Digit : codes) {
            Locale locale = new Locale("", code2Digit);

            countryList.add(locale.getISO3Country());
        }
        Collections.sort(countryList);
        mCountryAdapter = new ArrayAdapter<>(mContext, R.layout.register_myinfo_country_item, countryList);
        mBinding.country.setAdapter(mCountryAdapter);
    }

    private CustomSheetPaymentInfo.Address buildShippingAddressInfo() {

        return new CustomSheetPaymentInfo.Address.Builder()
                .setAddressee(mBinding.name.getText().toString())
                .setAddressLine1(mBinding.addressLine1.getText().toString())
                .setAddressLine2(mBinding.addressLine2.getText().toString())
                .setCity(mBinding.city.getText().toString())
                .setState(mBinding.state.getText().toString())
                .setCountryCode(mBinding.country.getSelectedItem().toString())
                .setPostalCode(mBinding.zip.getText().toString())
                .setPhoneNumber(mBinding.phoneNumber.getText().toString())
                .setEmail(mBinding.email.getText().toString())
                .build();
    }

    void updateShippingAddressLayout(CustomSheetPaymentInfo.AddressInPaymentSheet type) {
        switch (type) {
            case DO_NOT_SHOW:
            case NEED_BILLING_SPAY:
                mBinding.shippingAddressCheckBox.setChecked(false);
                updateShippingOptionVisibility(false);
                updateShippingAddressFieldsFromPartner(false);
                break;
            case SEND_SHIPPING:
            case NEED_BILLING_SEND_SHIPPING:
                mBinding.shippingAddressCheckBox.setChecked(true);
                updateShippingOptionVisibility(true);
                updateShippingAddressFieldsFromPartner(true);
                break;
            case NEED_SHIPPING_SPAY:
            case NEED_BILLING_AND_SHIPPING:
                mBinding.shippingAddressCheckBox.setChecked(true);
                updateShippingOptionVisibility(true);
                updateShippingAddressFieldsFromPartner(false);
                break;
        }
    }

    int validateShippingAddress(CustomSheetPaymentInfo.Address addr) {
        mIsCustomErrorMessage = false;
        if (addr == null) {
            return PaymentManager.ERROR_SHIPPING_ADDRESS_INVALID;
        }
        int ret;
        String displayText = mBinding.returnShippingAddressErrorType.getSelectedItem().toString();
        switch (displayText) {
            case "ERROR_SHIPPING_ADDRESS_INVALID":
                ret = PaymentManager.ERROR_SHIPPING_ADDRESS_INVALID;
                break;
            case "ERROR_SHIPPING_ADDRESS_NOT_EXIST":
                ret = PaymentManager.ERROR_SHIPPING_ADDRESS_NOT_EXIST;
                break;
            case "ERROR_SHIPPING_ADDRESS_UNABLE_TO_SHIP":
                ret = PaymentManager.ERROR_SHIPPING_ADDRESS_UNABLE_TO_SHIP;
                break;
            case "CUSTOM_ERROR_MESSAGE":
                mIsCustomErrorMessage = true;
                ret = SpaySdk.ERROR_NONE;
                break;
            case "ERROR_NONE":
            default:
                ret = SpaySdk.ERROR_NONE;
        }
        return ret;
    }

    boolean needCustomErrorMessage() {
        return mIsCustomErrorMessage;
    }

    String getCustomErrorMessage() {
        return mBinding.customErrorMessageForShipping.getText().toString();
    }

    void setNeedAllShippingMethodItems(boolean needAll) {
        mNeedAllShippingMethodItems = needAll;
    }

    SpinnerControl makeShippingMethodSpinnerControl(SheetUpdatedListener shippingMethodListener) {
        SpinnerControl spinnerControl = new SpinnerControl(SHIPPING_METHOD_SPINNER_ID,
                mContext.getString(R.string.shipping_method), SheetItemType.SHIPPING_METHOD_SPINNER);

        if (mBinding.shippingMethodStandardShippingFree.isChecked()) {
            spinnerControl.addItem(SHIPPING_METHOD_1, mContext.getString(R.string.standard_shipping_free));
            spinnerControl.setSelectedItemId(SHIPPING_METHOD_1);
        } else if (mBinding.shippingMethodTwoDaysShipping.isChecked()) {
            spinnerControl.addItem(SHIPPING_METHOD_2, mContext.getString(R.string.two_days_shipping));
            spinnerControl.setSelectedItemId(SHIPPING_METHOD_2);
        } else if (mBinding.shippingMethodOneDayShipping.isChecked()) {
            spinnerControl.addItem(SHIPPING_METHOD_3, mContext.getString(R.string.one_day_shipping));
            spinnerControl.setSelectedItemId(SHIPPING_METHOD_3);
        }

        if (mNeedAllShippingMethodItems) {
            if (!spinnerControl.existItem(SHIPPING_METHOD_1)) {
                spinnerControl.addItem(SHIPPING_METHOD_1,
                        mContext.getString(R.string.standard_shipping_free));
            }
            if (!spinnerControl.existItem(SHIPPING_METHOD_2)) {
                spinnerControl.addItem(SHIPPING_METHOD_2, mContext.getString(R.string.two_days_shipping));
            }
            if (!spinnerControl.existItem(SHIPPING_METHOD_3)) {
                spinnerControl.addItem(SHIPPING_METHOD_3, mContext.getString(R.string.one_day_shipping));
            }
        }

        spinnerControl.setSheetUpdatedListener(shippingMethodListener);
        return spinnerControl;
    }

    void setShippingMethodChangedListener(ShippingMethodListener listener) {
        mShippingMethodChangedListener = listener;
    }
}