package com.kimoo.android.extra;

import android.content.Context;
import android.content.Intent;
import android.os.Build;

import androidx.annotation.RequiresApi;

public class BroadcastReceiver extends android.content.BroadcastReceiver {
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onReceive(Context context, Intent intent) {

        if(intent.getAction().equals(Intent.ACTION_SCREEN_OFF)){
            Intent service = new Intent(context, ArkadaBul.class);
            context.startForegroundService(service);
        }
    }
}
