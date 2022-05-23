package com.letyouknow.utils;

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import com.letyouknow.R;


public class MyReceiver extends BroadcastReceiver {
    Dialog dialog;

    @Override
    public void onReceive(final Context context, final Intent intent) {

        String status = NetworkUtil.getConnectivityStatusString(context);

        //   Log.d("network", status);
        if (status.isEmpty() || status.equals(context.getString(R.string.no_internet_is_available)) || status.equals(context.getString(R.string.no_internet_connection))) {
            status = context.getString(R.string.you_are_offline);
            Toast.makeText(context, status, Toast.LENGTH_LONG).show();
//                if (!dialog.isShowing())
//                    dialog.show();
        } else {
//                if (dialog.isShowing()) {
//                    dialog.dismiss();
//                }
        }

    }
}