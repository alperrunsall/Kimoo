package com.kimoo.android.extra;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.IBinder;
import android.os.SystemClock;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import android.os.VibrationEffect;
import android.os.Vibrator;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.kimoo.android.MainActivity;
import com.kimoo.android.R;
import com.kimoo.android.fragments.SuanBulunanlar;

import java.util.ArrayList;


public class ArkadaBul2 extends Service {
    static BluetoothDevice device;
    static NotificationCompat.Builder notification,notification2,notification3;
    static ArrayList<String> cihazlar = new ArrayList<>(), bulunanlar = new ArrayList<>();
    static BluetoothAdapter myAdapter;
    public static NotificationManager notificationManager;
    public static int i = 0,neredeKalmisti,yazi = 0;
    public static boolean deaktif = false,bulunma = false;
    boolean connected;
    public static Thread thread;
    private long bulanlarinSayisi = 0;
    Runnable runnable;
    private FirebaseUser fuser;
    private String anaYazi,yazit1,yazit2,yazib1,yazib2,name2,name3;

    @Override
    public void onCreate() {
        super.onCreate();
        SharedPreferences sharedPreferences = getSharedPreferences("bulunan",Context.MODE_PRIVATE);
        myAdapter = BluetoothAdapter.getDefaultAdapter();
        neredeKalmisti = sharedPreferences.getInt("KalinanYer",0);
        notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences("ayarlar", Context.MODE_PRIVATE);
        myAdapter = BluetoothAdapter.getDefaultAdapter();
        myAdapter.setName(sharedPreferences.getString("bt_adi", ""));
    }

    @Override
    public int onStartCommand(final Intent intent, int flags, int startId) {
        fuser = FirebaseAuth.getInstance().getCurrentUser();
        if(!deaktif) {
            if (bulunma) {
                DatabaseReference ref = FirebaseDatabase.getInstance().getReference("usersF").child(fuser.getUid()).child("suan_bulanlar");
                ref.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if(dataSnapshot.exists()){
                            bulanlarinSayisi = dataSnapshot.getChildrenCount();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

                IntentFilter intentFilter = new IntentFilter(BluetoothAdapter.ACTION_SCAN_MODE_CHANGED);
                registerReceiver(mBroadcastReceiver,intentFilter);

                Intent broadcastIntent = new Intent(getApplicationContext(), NotificationReceiver.class);
                Intent intent1 = new Intent(getApplicationContext(), SuanBulunanlar.class);
                intent1.putExtra("bulunabilme","ac");
                final PendingIntent pintent = PendingIntent.getBroadcast(getApplicationContext(), 0, broadcastIntent, PendingIntent.FLAG_IMMUTABLE);
                final PendingIntent pintent2 = PendingIntent.getActivity(getApplicationContext(), 1, intent1, PendingIntent.FLAG_IMMUTABLE);

                notification2 = new NotificationCompat.Builder(this,"arkadabulunma");
                notification2
                        .setContentIntent(pintent2)
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .addAction(R.mipmap.ic_launcher, "KAPAT", pintent)
                        .setPriority(NotificationCompat.PRIORITY_LOW);
                notificationManager.notify(5,notification2.build());
                runnable = new Runnable() {
                    @Override
                    public void run() {
                        while (yazi > 0 && !deaktif){
                            if(bulunma) {
                                SuanBulunanlar.bulunabilmeZamaniYazi = "" + Yazi();
                                if (internetVarmi())
                                    notification2.setContentText(SuanBulunanlar.bulunabilmeZamaniYazi)
                                            .setContentTitle(bulanlarinSayisi + " Kişi sizi buldu");
                                else
                                    notification2.setContentText(SuanBulunanlar.bulunabilmeZamaniYazi)
                                            .setContentTitle("İnternet bağlantınız yok");

                                notificationManager.notify(5,notification2.build());
                                startForeground(5, notification2.build());
                                SystemClock.sleep(1000);
                            }
                            else {
                                stopForeground(false);
                            }
                        }
                        if(yazi <= 0 && !deaktif){
                            SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences("ayarlar", Context.MODE_PRIVATE);
                            myAdapter = BluetoothAdapter.getDefaultAdapter();
                            myAdapter.setName(sharedPreferences.getString("bt_adi", ""));
                            anaYazi = "Bulunabilme(00:00)";
                            SuanBulunanlar.bulunabilmeZamaniYazi = anaYazi;
                            if (internetVarmi())
                                notification2.setContentText(anaYazi)
                                        .setContentTitle(bulanlarinSayisi + " Kişi sizi buldu");
                            else
                                notification2.setContentText(anaYazi)
                                        .setContentTitle("İnternet bağlantınız yok");

                            FirebaseDatabase.getInstance().getReference("usersF").child(fuser.getUid()).child("suan_bulanlar").removeValue();
                            notificationManager.notify(5, notification2.build());
                            //startForeground(4, notification2.build());

                            stopForeground(false);
                        }
                    }
                };
                thread = new Thread(runnable);
                thread.start();

            }
        }
        return START_STICKY;
    }
    private final BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();

            if(action.equals(BluetoothAdapter.ACTION_SCAN_MODE_CHANGED)){
                int mode = intent.getIntExtra(BluetoothAdapter.EXTRA_SCAN_MODE, BluetoothAdapter.ERROR);

                if (mode != BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE) {
                    Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        v.vibrate(VibrationEffect.createOneShot(500, VibrationEffect.DEFAULT_AMPLITUDE));
                    } else {
                        //deprecated in API 26
                        v.vibrate(500);
                    }
                    SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences("ayarlar", Context.MODE_PRIVATE);
                    myAdapter = BluetoothAdapter.getDefaultAdapter();
                    myAdapter.setName(sharedPreferences.getString("bt_adi", ""));
                    anaYazi = "Bulunabilme(00:00)";
                    SuanBulunanlar.bulunabilmeZamaniYazi = anaYazi;

                    if (internetVarmi())
                        notification2.setContentText("Bulunabilirliğiniz durdu!")
                                .setContentTitle(bulanlarinSayisi + " Kişi seni buldu");
                    else
                        notification2.setContentText("Bulunabilirliğiniz durdu!")
                                .setContentTitle("İnternet bağlantınız yok");

                    FirebaseDatabase.getInstance().getReference("usersF").child(fuser.getUid()).child("suan_bulanlar").removeValue();

                    notificationManager.notify(5, notification2.build());
                    //startForeground(4, notification2.build());

                    yazi = 0;
                    bulunma = false;
                    deaktif = true;

                    stopForeground(false);
                    thread.interrupt();
                    unregisterReceiver(mBroadcastReceiver);
                }

            }

        }
    };

    private boolean internetVarmi(){
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED)
            return true;
        else {
            return false;
        }
    }

    private String Yazi(){
        yazi -= 1;
        return "Bulunabilme(" + yazi / 60 + ":" + (yazi - ((yazi / 60) * 60)) + ")";
    }




    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
