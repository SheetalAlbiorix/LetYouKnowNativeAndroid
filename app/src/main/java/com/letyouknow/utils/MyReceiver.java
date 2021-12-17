package com.letyouknow.utils;

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;


public class MyReceiver extends BroadcastReceiver {
    Dialog dialog;

    @Override
    public void onReceive(final Context context, final Intent intent) {

        String status = NetworkUtil.getConnectivityStatusString(context);
      /*  if(dialog != null) {
            dialog = new Dialog(context, android.R.style.Theme_NoTitleBar_Fullscreen);
            dialog.setContentView(R.layout.dialog_network);
            dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
            TextView tvOk = dialog.findViewById(R.id.tvOk);
            tvOk.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dialog.dismiss();
                }
            });
        }*/
        Log.d("network", status);
        if (status.isEmpty() || status.equals("No internet is available") || status.equals("No Internet Connection")) {
            status = "You're offline. Please check your internet connection";
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