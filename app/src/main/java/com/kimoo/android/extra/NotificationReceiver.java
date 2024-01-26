package com.kimoo.android.extra;

import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;
import com.kimoo.android.fragments.SuanBulunanlar;

public class NotificationReceiver extends BroadcastReceiver {
    static BluetoothAdapter myAdapter;

    @Override
    public void onReceive(Context context, Intent intent) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("ayarlar",Context.MODE_PRIVATE);
        myAdapter = BluetoothAdapter.getDefaultAdapter();
        myAdapter.setName(sharedPreferences.getString("bt_adi", ""));
        Intent intentt = new Intent(context, ArkadaBul2.class);

        FirebaseUser fuser = FirebaseAuth.getInstance().getCurrentUser();
        FirebaseDatabase.getInstance().getReference("usersF").child(fuser.getUid()).child("suan_bulanlar").removeValue();

        SuanBulunanlar.bulunabilmeZamaniYazi = "";
        ArkadaBul2.yazi = 0;
        ArkadaBul2.bulunma = false;
        ArkadaBul2.deaktif = true;
        context.stopService(intentt);

    }
}
