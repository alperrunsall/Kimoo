package com.kimoo.android.extra;

import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import com.kimoo.android.fragments.SuanBulunanlar;

public class NotificationReceiver2 extends BroadcastReceiver {
    static BluetoothAdapter myAdapter;
    @Override
    public void onReceive(Context context, Intent intent) {
        Intent intentt2 = new Intent(context, ArkadaBul.class);
        myAdapter = BluetoothAdapter.getDefaultAdapter();
        myAdapter.cancelDiscovery();
        ArkadaBul.myTimer.cancel();
        ArkadaBul.bulma = false;
        ArkadaBul.deaktif = true;
        //SuanBulunanlar.taramaAktifMi = false;
        context.stopService(intentt2);

    }
}
