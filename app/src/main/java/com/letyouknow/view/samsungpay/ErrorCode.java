package com.letyouknow.view.samsungpay;

import android.util.SparseArray;

import com.samsung.android.sdk.samsungpay.v2.SpaySdk;
import com.samsung.android.sdk.samsungpay.v2.card.CardManager;
import com.samsung.android.sdk.samsungpay.v2.payment.PaymentManager;

import java.lang.reflect.Field;

public class ErrorCode {
    private static ErrorCode sInstance;
    private SparseArray<String> mErrorCodeMap = new SparseArray<>();

    private ErrorCode() {
        createErrorCodeMap(SpaySdk.class);
        createErrorCodeMap(PaymentManager.class);
        createErrorCodeMap(CardManager.class);
    }

    public static synchronized ErrorCode getInstance() {
        if (sInstance == null) {
            sInstance = new ErrorCode();
        }
        return sInstance;
    }

    private void createErrorCodeMap(Class c) {
        Field[] fields = c.getDeclaredFields();
        for (Field fld : fields) {
            if (fld.getType() == int.class) {
                try {
                    int v = fld.getInt(null);
                    String name = fld.getName();
                    if (name.startsWith("ERROR_") && v != SpaySdk.ERROR_NONE) {
                        // ERROR_NONE is not an error.
                        mErrorCodeMap.put(v, name);
                    } else if (name.startsWith("SPAY_")) {
                        mErrorCodeMap.put(v, name);
                    }
                } catch (Exception e) {
                    // No need to print the stack trace here.
                }
            }
        }
    }

    public String getErrorCodeName(int i) {
        return mErrorCodeMap.get(i);
    }
}