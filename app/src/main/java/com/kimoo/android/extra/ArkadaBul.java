package com.kimoo.android.extra;

import android.app.Notification;
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
import android.media.RingtoneManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.IBinder;
import android.os.SystemClock;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.kimoo.android.MainActivity;
import com.kimoo.android.Model.Fiyatlar;
import com.kimoo.android.Model.User;
import com.kimoo.android.R;
import com.kimoo.android.TaraActivity;
import com.kimoo.android.fragments.SuanBulunanlar;
import com.kimoo.android.fragments.TumGorduklerim;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;


public class ArkadaBul extends Service {
    static BluetoothDevice device;
    static NotificationCompat.Builder notification;
    static ArrayList<String> cihazlar = new ArrayList<>();
    static BluetoothAdapter myAdapter;
    public static int neredeKalmisti,yazi = 0;
    public static boolean deaktif = false,bulma = false,tariyor, internetYeniGeldi;
    boolean connected;
    FirebaseUser fuser;
    public static Timer myTimer = new Timer();
    private String anaYazi,yazit1,yazit2,yazib1,yazib2,name2,name3;
    private DataSnapshot dataSnapshotAsil;
    private ArrayList<String> asilBulunanlar = new ArrayList<>(), tumBulduklarim = new ArrayList<>();

    @Override
    public void onCreate() {
        super.onCreate();
        super.onCreate();
        SharedPreferences sharedPreferences = getSharedPreferences("bulunan",Context.MODE_PRIVATE);
        myAdapter = BluetoothAdapter.getDefaultAdapter();
        neredeKalmisti = sharedPreferences.getInt("KalinanYer",0);
        cihazlar = new ArrayList<>();
        tumBulduklarim = new ArrayList<>();
        myTimer = new Timer();
        asilBulunanlar = new ArrayList<>();
        fuser = FirebaseAuth.getInstance().getCurrentUser();
        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED) {
            DatabaseReference ref = FirebaseDatabase.getInstance().getReference("usersF").child(fuser.getUid());
            ref.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    dataSnapshotAsil = dataSnapshot;
                    if (dataSnapshot.hasChild("bulduklarim")) {
                        int i = 0;
                        for (DataSnapshot ds : dataSnapshot.child("bulduklarim").getChildren()) {
                            i++;
                            if (!tumBulduklarim.contains(ds.getKey()))
                                tumBulduklarim.add(ds.getKey());
                            if (i == dataSnapshot.child("bulduklarim").getChildrenCount())
                                if (!receiverr.isOrderedBroadcast()) {
                                    registerReceiver(receiverr, filter);
                                    myAdapter.startDiscovery();
                                }
                        }
                    }
                    else{
                        if (!receiverr.isOrderedBroadcast()) {
                            registerReceiver(receiverr, filter);
                            myAdapter.startDiscovery();
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }

    }

    @Override
    public int onStartCommand(final Intent intent, int flags, int startId) {
        if (bulma) {
            cihazlar = new ArrayList<>();
            tumBulduklarim = new ArrayList<>();
            myTimer = new Timer();
            asilBulunanlar = new ArrayList<>();
            IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
            if (!connected)
            if (!receiverr.isOrderedBroadcast()) {
                registerReceiver(receiverr, filter);
                myAdapter.startDiscovery();
            }

            Intent broadcastIntent = new Intent(getApplicationContext(), NotificationReceiver2.class);
            Intent contentIntent = new Intent(getApplicationContext(), TumGorduklerim.class);
            contentIntent.putExtra("bulmayikapat","evet");
            final PendingIntent contentPintent = PendingIntent.getActivity(getApplicationContext(), 1, contentIntent, PendingIntent.FLAG_MUTABLE);
            final PendingIntent pintent = PendingIntent.getBroadcast(getApplicationContext(), 0, broadcastIntent, PendingIntent.FLAG_MUTABLE);

            notification = new NotificationCompat.Builder(getApplicationContext(), "arkadabulma");
            notification
                    .setContentIntent(contentPintent)
                    .setAutoCancel(false)
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setPriority(NotificationCompat.PRIORITY_LOW)
                    .addAction(R.mipmap.ic_launcher, "KAPAT", pintent);




            //final int[] sayi = {0};
            myTimer = new Timer();
            TimerTask t = new TimerTask() {
                @Override
                public void run() {
                    if (bulma) {
                        notification.setContentTitle("Kullanıcılar Bulunuyor... ");
                        //notification.setContentText("Henüz hiç kullanıcı bulunamadı");
                        startForeground(4, notification.build());

                        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
                        if (connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                                connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED) {
                            connected = true;
                            internetYeniGeldi = true;
                        }
                        else
                            connected = false;

                        if (myAdapter.isEnabled()) {
                            myAdapter.startDiscovery();
                            if (myAdapter.isDiscovering()) {
                                if (connected) {
                                   if(internetYeniGeldi)
                                       if (dataSnapshotAsil != null)
                                            KullaniciKontrol();
                                       else{
                                           if (connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                                                   connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED) {
                                               DatabaseReference ref = FirebaseDatabase.getInstance().getReference("usersF").child(fuser.getUid());
                                               ref.addListenerForSingleValueEvent(new ValueEventListener() {
                                                   @Override
                                                   public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                       dataSnapshotAsil = dataSnapshot;
                                                       if (dataSnapshot.hasChild("bulduklarim")) {
                                                           int i = 0;
                                                           for (DataSnapshot ds : dataSnapshot.child("bulduklarim").getChildren()) {
                                                               i++;
                                                               if (!tumBulduklarim.contains(ds.getKey()))
                                                                   tumBulduklarim.add(ds.getKey());
                                                               if (i == dataSnapshot.child("bulduklarim").getChildrenCount())
                                                                   KullaniciKontrol();
                                                           }
                                                       }
                                                       else{
                                                           KullaniciKontrol();
                                                       }
                                                   }

                                                   @Override
                                                   public void onCancelled(@NonNull DatabaseError databaseError) {

                                                   }
                                               });
                                           }
                                       }
                                }
                                else {
                                    notification.setContentTitle("Kullanıcılar Bulunuyor... ");
                                    if (cihazlar.size() != 0) {
                                        notification.setContentText("" + cihazlar.size() + " Adet kullanıcı bulunmuş olabilir");
                                        //startForeground(4, notification.build());
                                    } else {
                                        notification.setContentText("Henüz hiç kullanıcı bulunamadı");
                                        //startForeground(4, notification.build());
                                    }
                                }
                            }
                            else
                                myAdapter.startDiscovery();
                        }
                        else {
                            notification.setContentTitle("Kullanıcılar Bulunamıyor! ")
                                    .setContentText("Bluetooth Kapalı");
                            //startForeground(4, notification.build());
                        }
                        SystemClock.sleep(1000);
                    }
                }
            };
            myTimer.scheduleAtFixedRate(t,0,1000);
        }
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(receiverr);
    }

    private void BenOnuEngellemismiyim(final String user) {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("usersF").child(fuser.getUid());
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.hasChild("engellediklerim")){
                    if(dataSnapshot.child("engellediklerim").hasChild(user)){
                        if(bulma) {
                            notification.setContentText("Henüz hiç kullanıcı bulunamadı");
                            //startForeground(4, notification.build());
                        }
                    }
                    else
                        OBeniEngellemismi(user);

                }
                else
                    OBeniEngellemismi(user);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
    private void OBeniEngellemismi(final String user) {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("usersF").child(user);
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(!dataSnapshot.child("engellediklerim").child(fuser.getUid()).exists()){
                    if(dataSnapshot.child("ev_sistemi").child("sistem").getValue(String.class).equals("aktif")){
                        if(!dataSnapshot.child("ev_sistemi").child("beni_bulanlar").hasChild(fuser.getUid()))
                            dataSnapshot.child("ev_sistemi").child("beni_bulanlar").child(fuser.getUid()).getRef().setValue(ServerValue.TIMESTAMP);
                        dataSnapshot.child("suan_bulanlar").child(fuser.getUid()).getRef().setValue(ServerValue.TIMESTAMP);
                    }
                    else{
                        if(!asilBulunanlar.contains(user))
                            asilBulunanlar.add(user);
                        VeriTabaninaIsle(user);
                        if(bulma) {
                            if (cihazlar.size() != 0) {
                                notification.setContentText("" + asilBulunanlar.size() + " Kullanıcı Bulundu!");
                                FirebaseDatabase.getInstance().getReference("usersF").child(fuser.getUid()).child("bulduklarim_bildirim_durumu").setValue("var");
                                //startForeground(4, notification.build());
                            }else{
                                notification.setContentText("Henüz hiç kullanıcı bulunamadı");
                                //startForeground(4, notification.build());
                            }
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
    private void VeriTabaninaIsle(String user) {
        DatabaseReference uref = FirebaseDatabase.getInstance().getReference("usersF").child(user);
        uref.child("suan_bulanlar").child(fuser.getUid()).setValue(ServerValue.TIMESTAMP);
        uref.child("bulanlar").child(fuser.getUid()).setValue(ServerValue.TIMESTAMP);

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("usersF").child(fuser.getUid());
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.child("bulduklarim").child(user).exists()) {
                    Date date = new Date();
                    Long sonBirSaat = date.getTime() - 3600000;
                    if(dataSnapshot.child("bulduklarim").child(user).child("son_gordugum_zaman").getValue(Long.class) < sonBirSaat){
                        // Eğer burası olmazsa her saniye son görülme zamanını yeniliyor
                        dataSnapshot.child("bulduklarim").child(user).child("son_gordugum_zaman").getRef().setValue(ServerValue.TIMESTAMP).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(@NonNull Void unused) {
                                KimlerleAyniYereGidiyorum();
                            }
                        });
                    }
                }
                else {
                    dataSnapshot.child("bulduklarim").child(user).child("kac_kez_gordum").getRef().setValue("0");
                    dataSnapshot.child("bulduklarim").child(user).child("ilk_gordugum_zaman").getRef().setValue(ServerValue.TIMESTAMP);
                    dataSnapshot.child("bulduklarim").child(user).child("son_gordugum_zaman").getRef().setValue(ServerValue.TIMESTAMP).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(@NonNull Void unused) {
                            KimlerleAyniYereGidiyorum();
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
    private void KimlerleAyniYereGidiyorum() {
        final int[] gidenlerSayisi = {0};
        List<String> gidecegimYerler = new ArrayList<>();
        gidecegimYerler.add("1");
        gidecegimYerler.add("2");
        gidecegimYerler.add("3");
        gidecegimYerler.add("asil");
        DatabaseReference sistemRef = FirebaseDatabase.getInstance().getReference("Sistem");
        sistemRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                boolean gorev = false;
                int gidilenYerOnayiIcinGerekliKisiSayisi = dataSnapshot.child("gidilenYerOnayiIcinGerekliKisiSayisi").getValue(Integer.class);
                for(int i = 0; i < gidecegimYerler.size(); i ++){
                    if(gidecegimYerler.get(i).equals("asil"))
                        gorev = true;
                    else
                        gorev = false;
                    String gidecegimYerinDBIsmi = dataSnapshotAsil.child("gidecegim_yerler").child(gidecegimYerler.get(i)).child("dbisim").getValue(String.class);
                    String gidecegimYerinIsmi = dataSnapshotAsil.child("gidecegim_yerler").child(gidecegimYerler.get(i)).child("isim").getValue(String.class);
                    String gidecegimYereGitmismiyim = dataSnapshotAsil.child("gidecegim_yerler").child(gidecegimYerler.get(i)).child("gittimMi").getValue(String.class);
                    if (!gidecegimYerinDBIsmi.equals("") && gidecegimYereGitmismiyim.equals("hayir")) {
                        long zaman = dataSnapshotAsil.child("gidecegim_yerler").child(gidecegimYerler.get(i)).child("zaman").getValue(Long.class);
                        for (DataSnapshot ds : dataSnapshotAsil.child("bulduklarim").getChildren()){
                            if(ds.child("son_gordugum_zaman").getValue(Long.class) > zaman){
                                DatabaseReference ref = FirebaseDatabase.getInstance().getReference("usersF").child(ds.getKey());
                                int finalI = i;
                                boolean finalGorev = gorev;
                                ref.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        if(dataSnapshot.child("gidecegim_yerler").child("1").child("dbisim").getValue().equals(gidecegimYerinDBIsmi))
                                            gidenlerSayisi[0]++;
                                        if(dataSnapshot.child("gidecegim_yerler").child("2").child("dbisim").getValue().equals(gidecegimYerinDBIsmi))
                                            gidenlerSayisi[0]++;
                                        if(dataSnapshot.child("gidecegim_yerler").child("3").child("dbisim").getValue().equals(gidecegimYerinDBIsmi))
                                            gidenlerSayisi[0]++;
                                        if(dataSnapshot.child("gidecegim_yerler").child("asil").child("dbisim").getValue().equals(gidecegimYerinDBIsmi)) {
                                            gidenlerSayisi[0]++;
                                        }
                                        if(gidenlerSayisi[0] >= gidilenYerOnayiIcinGerekliKisiSayisi){
                                            dataSnapshotAsil.child("gidecegim_yerler").child(gidecegimYerler.get(finalI)).child("gittimMi").getRef().setValue("evet").addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(@NonNull Void unused) {
                                                    if(finalGorev) {
                                                        final Fiyatlar[] fiyatlar = new Fiyatlar[1];
                                                        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Sistem").child("fiyatlar");
                                                        reference.addListenerForSingleValueEvent(new ValueEventListener() {
                                                            @Override
                                                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                                fiyatlar[0] = dataSnapshot.getValue(Fiyatlar.class);
                                                                dataSnapshotAsil.child("kp").getRef().setValue(dataSnapshotAsil.child("kp").getValue(Integer.class) + fiyatlar[0].getZiyaret_gorevi()).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                    @Override
                                                                    public void onSuccess(@NonNull Void unused) {
                                                                        if (dataSnapshotAsil.child("bildirimler").child("ziyaret").getValue(String.class).equals("gelsin")) {
                                                                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                                                                KonumaUlasildiBildirimiGonderOreo(gidecegimYerinIsmi);
                                                                            } else {
                                                                                KonumaUlasildiBildirimiGonder(gidecegimYerinIsmi);
                                                                            }

                                                                            DatabaseReference ref = FirebaseDatabase.getInstance().getReference("usersF").child(fuser.getUid());
                                                                            ref.addListenerForSingleValueEvent(new ValueEventListener() {
                                                                                @Override
                                                                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                                                    dataSnapshotAsil = dataSnapshot;
                                                                                }

                                                                                @Override
                                                                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                                                                }
                                                                            });

                                                                        }
                                                                    }
                                                                });
                                                            }

                                                            @Override
                                                            public void onCancelled(@NonNull DatabaseError databaseError) {

                                                            }
                                                        });

                                                    }
                                                    // dataSnapshotAsil.child("gidecegim_yerler").child(gidecegimYerler.get(finalI)).child("gittimMi").getRef().setValue("evet");
                                                }
                                            });
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                    }
                                });
                            }
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
    private void KonumaUlasildiBildirimiGonder(String gidecegimYerinIsmi) {
        Uri defaultSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        Intent intent = new Intent(this, TaraActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this,5,intent,PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext())
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentText("Gitmek istediğiniz konuma ulaştınız!" )
                .setContentTitle("" + gidecegimYerinIsmi + "'a geldiniz.")
                .setAutoCancel(true)
                .setContentIntent(pendingIntent)
                .setVibrate(new long[] { 1000, 1000, 1000, 1000, 1000 })
                .setSound(defaultSound);
        NotificationManager noti = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        noti.notify(4,builder.build());



    }
    @RequiresApi(api = Build.VERSION_CODES.O)
    private void KonumaUlasildiBildirimiGonderOreo(String gidecegimyer) {
        Intent intent = new Intent(this, TaraActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(),5,intent,PendingIntent.FLAG_UPDATE_CURRENT);
        Notification notification = new Notification.Builder(getApplicationContext(), "gidilenyerler")
                .setContentTitle("" + gidecegimyer + "'a geldiniz.")
                .setContentIntent(pendingIntent)
                .setContentText("Gitmek istediğiniz konuma ulaştınız!" )
                .setSmallIcon(R.mipmap.ic_launcher)
                .setAutoCancel(true)
                .build();
        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        manager.notify(5, notification);
    }

    private void KullaniciKontrol() {
        if(cihazlar.size() > 0) {
            for(int i = 0; i < cihazlar.size(); i++) {
                String k_uid = cihazlar.get(i).substring(4, cihazlar.get(i).length() - 5);
                String k_id = cihazlar.get(i).substring(cihazlar.get(i).length() - 5);

                Query userQuery = FirebaseDatabase.getInstance().getReference("usersF").orderByChild("usernamef").equalTo(k_uid);
                userQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot userSS) {
                        for (DataSnapshot dataSnapshot : userSS.getChildren()) {
                            if (dataSnapshot.child("id").getValue(String.class).equals(k_id)) {
                                int yasSiniri = 0;
                                if(Integer.parseInt(dataSnapshotAsil.child("dg").getValue(String.class)) < 18)
                                    yasSiniri = 2;
                                else
                                    yasSiniri = 5;
                                if ((Integer.parseInt(dataSnapshotAsil.child("dg").getValue(String.class)) - Integer.parseInt(dataSnapshot.child("dg").getValue(String.class))) > -yasSiniri &&
                                        (Integer.parseInt(dataSnapshotAsil.child("dg").getValue(String.class)) - Integer.parseInt(dataSnapshot.child("dg").getValue(String.class))) < yasSiniri) {
                                    if (!asilBulunanlar.contains(dataSnapshot.getKey())) {
                                        if (tumBulduklarim.size() > 0) {
                                            if (!tumBulduklarim.contains(dataSnapshot.getKey())) {
                                                if (dataSnapshot.child("ban_durumu").child("durum").getValue().equals("yok")) {
                                                    BenOnuEngellemismiyim(dataSnapshot.getKey());
                                                }
                                                tumBulduklarim.add(dataSnapshot.getKey());
                                            }
                                            else
                                                notification.setContentText("Henüz hiç kullanıcı bulunamadı");
                                        }
                                        else{
                                            BenOnuEngellemismiyim(dataSnapshot.getKey());
                                            tumBulduklarim.add(dataSnapshot.getKey());
                                        }
                                    }
                                }
                            }
                        }
                        internetYeniGeldi = false;
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }
        }else{
            if(bulma) {
                notification.setContentText("Henüz hiç kullanıcı bulunamadı");
                //startForeground(4, notification.build());
            }/*else if(bulma && bulunma){
                yazib1 = "İnternet Var!";
                yazit1 = "Kullanıcı bulunamadı.";
            }*/
        }
    }

    public final BroadcastReceiver receiverr = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            SharedPreferences sharedPreferences = context.getSharedPreferences("bulunan",Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                if (!device.equals(null) && device != null) {
                    if(device.getName() != null && !device.getName().trim().equals(""))
                        if(device.getName().length() > 12){
                            if(device.getName().substring(0,4).equals("kim_")){
                                if(!cihazlar.contains(device.getName())) {
                                    cihazlar.add(device.getName());
                                    internetYeniGeldi = true;
                                    ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
                                    if (connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                                            connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED) {
                                        connected = true;
                                    }
                                    else
                                        connected = false;

                                    if(!tariyor) {
                                        if (neredeKalmisti > 0) {
                                            for (int i = 0; i < neredeKalmisti; i++) {
                                                String isim = sharedPreferences.getString("bulunan_" + i, null);
                                                if (isim != null) {
                                                    if (!isim.substring(isim.length() - 5).equals(device.getName().substring(device.getName().length() - 5))) {
                                                        neredeKalmisti++;
                                                        editor.putString("bulunan_" + neredeKalmisti, device.getName());
                                                        editor.putInt("KalinanYer", neredeKalmisti);
                                                        editor.commit();
                                                    }
                                                }
                                            }
                                        }
                                        else{
                                            neredeKalmisti++;
                                            editor.putString("bulunan_" + neredeKalmisti, device.getName());
                                            editor.putInt("KalinanYer", neredeKalmisti);
                                            editor.commit();
                                        }
                                    }
                                }
                            }
                        }
                }
            }

        }
    };

   /* private String DogruAdiGetir() {
        SharedPreferences sharedPreferences = getSharedPreferences("preferences",Context.MODE_PRIVATE);
        if(sharedPreferences.getString("bulunan_1","default").equals("default")){
            return "bulunan_1";
        }else{
            deger = 1;
            while (!sharedPreferences.getString("bulunan_"+deger,"default").equals("default")){
                deger++;
            }
            return "bulunan_"+deger;
        }
    }*/


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
