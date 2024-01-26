package com.kimoo.android;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.GradientDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.text.InputType;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.kimoo.android.Model.User;
import com.kimoo.android.extra.TasarimRenginiGetir;

import java.text.SimpleDateFormat;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference myRef;
    private FirebaseUser fuser;
    private BluetoothAdapter myAdapter;
    private ProgressBar pbar;
    private ImageView image;
    private Button buton;
    private TextView versiyon;
    private DataSnapshot asilDataSnapshot;
    private boolean devam = true;
    private boolean connected = false,sonuclandi;
    private String olusanId = "", android_id;
    private DatabaseReference ref;
    Timer timer = new Timer();
    private String andId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.e("asd","asdadsad");
        StatuBarAyarla();
        android_id = Settings.Secure.getString(getApplicationContext().getContentResolver(),
                Settings.Secure.ANDROID_ID);
        sonuclandi = false;
        //FirebaseApp.initializeApp(this);
        fuser = FirebaseAuth.getInstance().getCurrentUser();
        olusanId = getUID();
        image = findViewById(R.id.isim);
        myAdapter = BluetoothAdapter.getDefaultAdapter();
        pbar = findViewById(R.id.pbar);
        buton = findViewById(R.id.buton);
        versiyon = findViewById(R.id.versiyon);
        versiyon.setText(BuildConfig.VERSION_NAME);

        buton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseAuth.getInstance().signOut();
                Intent i = new Intent(MainActivity.this, KayitActivity.class);
                startActivity(i);
            }
        });
        SharedPreferences tas_shared = getSharedPreferences("TasarimBilgileri",MODE_PRIVATE);
        TasarimDegistir(tas_shared.getString("tasarim_arayuz","1"));

        final boolean[] bitti = {false};

        andId = Settings.Secure.getString(getApplicationContext().getContentResolver(), Settings.Secure.ANDROID_ID);

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.BLUETOOTH_CONNECT) == PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.BLUETOOTH_SCAN) == PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED) {
            Basla();
        }
        else{
            requestPermissions(new String[]{Manifest.permission.BLUETOOTH_CONNECT, Manifest.permission.BLUETOOTH_SCAN, Manifest.permission.POST_NOTIFICATIONS, Manifest.permission.WRITE_EXTERNAL_STORAGE},1);
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            KanallariOlustur();
        }
        //scaleView(image,1f,1.3f);

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1)
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Basla();

            } else {
                Toast.makeText(MainActivity.this, "Eğer izin vermezseniz diğer Kimoo'yu kullanamazsınız.", Toast.LENGTH_LONG).show();
            }
    }

    private void Basla(){
        if(internetVarmi()) {
            if (fuser != null) {
                DatabaseReference sistemRef = FirebaseDatabase.getInstance().getReference("Sistem");
                sistemRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if(dataSnapshot.child("sistem_durumu").child("durum").getValue(Integer.class) == 0){
                            if (dataSnapshot.child("versiyon_zorunlu").getValue(Integer.class) <= BuildConfig.VERSION_CODE) {

                                ref = FirebaseDatabase.getInstance().getReference("tel_ban");
                                ref.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        if (dataSnapshot.child(android_id).exists()) {
                                            buton.setVisibility(View.GONE);
                                            BanKontrol(dataSnapshot.child(android_id).getValue(String.class));
                                        } else {
                                            BanKontrol("");
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                    }
                                });
                            }
                            else {
                                Toast.makeText(MainActivity.this, "Eski bir versiyon kullanıyorsunuz, lütfen uygulamayı güncelleyiniz.", Toast.LENGTH_LONG).show();
                                finishAffinity();
                            }
                        }
                        else if (dataSnapshot.child("sistem_durumu").child("durum").getValue(Integer.class) == 1)
                            Toast.makeText(MainActivity.this, ""+dataSnapshot.child("sistem_durumu").child("durum_aciklamasi").getValue(String.class), Toast.LENGTH_LONG).show();


                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }
            else {
                Intent i = new Intent(MainActivity.this, GirisActivity.class);
                startActivity(i);
            }
        }
        else{
            if (fuser != null) {
                Intent intent = new Intent(MainActivity.this, BaglantiYok.class);
                startActivity(intent);
                sonuclandi = true;
            }
            else {
                Toast.makeText(MainActivity.this, "İnternet bağlantınız yok!", Toast.LENGTH_SHORT).show();
                TimerTask t = new TimerTask() {
                    @Override
                    public void run() {
                        if (internetVarmi())
                            startActivity(new Intent(MainActivity.this, GirisActivity.class));
                    }
                };
                timer.scheduleAtFixedRate(t,0,1000);
            }
        }
    }
    public void TasarimDegistir(String tasDegeri) {
        GradientDrawable gradientYumusak = new GradientDrawable();

        gradientYumusak.setOrientation(GradientDrawable.Orientation.LEFT_RIGHT);
        gradientYumusak.setCornerRadius(50);

        int renk1 = 0,renk2 = 0,t1start = 0,t2start = 0,t1end = 0,t2end = 0,orta = 0;

        renk1 = TasarimRenginiGetir.RengiGetir(MainActivity.this,"renk1",tasDegeri);
        renk2 = TasarimRenginiGetir.RengiGetir(MainActivity.this,"renk2",tasDegeri);
        t1start = TasarimRenginiGetir.RengiGetir(MainActivity.this,"t1start",tasDegeri);
        t2start = TasarimRenginiGetir.RengiGetir(MainActivity.this,"t2start",tasDegeri);
        t1end = TasarimRenginiGetir.RengiGetir(MainActivity.this,"t1end",tasDegeri);
        t2end = TasarimRenginiGetir.RengiGetir(MainActivity.this,"t2end",tasDegeri);
        orta = TasarimRenginiGetir.RengiGetir(MainActivity.this,"orta",tasDegeri);
        if (!tasDegeri.equals("sifirlandi") && !tasDegeri.equals("0")){
            versiyon.setTextColor(orta);
            image.setColorFilter(orta);
            pbar.getIndeterminateDrawable().setColorFilter(orta,android.graphics.PorterDuff.Mode.MULTIPLY);
        }
        else{
            versiyon.setTextColor(getResources().getColor(R.color.tas1orta));
            image.setColorFilter(getResources().getColor(R.color.tas1orta));
            pbar.getIndeterminateDrawable().setColorFilter(getResources().getColor(R.color.tas1orta),android.graphics.PorterDuff.Mode.MULTIPLY);
        }

    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void KanallariOlustur() {
        NotificationChannel channel = new NotificationChannel("gidilenyerler", "Gidilen Yerler", NotificationManager.IMPORTANCE_DEFAULT);
        channel.enableVibration(true);

        NotificationChannel notificationChannel = new NotificationChannel("arkadabulma", "Arkada Bulma", NotificationManager.IMPORTANCE_LOW);
        NotificationChannel notificationChannel2 = new NotificationChannel("arkadabulunma", "Arkada Bulunma", NotificationManager.IMPORTANCE_LOW);
        notificationChannel.setVibrationPattern(null);

        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        manager.createNotificationChannel(channel);
        manager.createNotificationChannel(notificationChannel);
        manager.createNotificationChannel(notificationChannel2);

    }
    private void StatuBarAyarla() {
        if (Build.VERSION.SDK_INT >= 19 && Build.VERSION.SDK_INT < 21) {
            setWindowFlag(this, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, true);
        }
        if (Build.VERSION.SDK_INT >= 19) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        }

        if (Build.VERSION.SDK_INT >= 21) {
            setWindowFlag(this, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, false);
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        }
    }
    public static void setWindowFlag(Activity activity, final int bits, boolean on) {
        Window win = activity.getWindow();
        WindowManager.LayoutParams winParams = win.getAttributes();
        if (on) {
            winParams.flags |= bits;
        } else {
            winParams.flags &= ~bits;
        }
        win.setAttributes(winParams);
    }
    private boolean internetVarmi(){
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED)
            return true;
        else {
            return false;
        }
    }
    private void BanKontrol(String deger) {
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yy");
        if(deger.equals("")) {
            DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("usersF").child(fuser.getUid());
            userRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        dataSnapshot.child("suan").getRef().setValue(ServerValue.TIMESTAMP).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(@NonNull Void unused) {
                                if (dataSnapshot.child("ban_durumu").exists()) {
                                    if (dataSnapshot.child("ban_durumu").child("durum").getValue(String.class).equals("var")) {
                                        buton.setVisibility(View.VISIBLE);
                                        Kontrol("ban");
                                    } else {
                                        buton.setVisibility(View.GONE);
                                        Kontrol("kisit");
                                    }
                                }
                                else {
                                    Toast.makeText(MainActivity.this, "Hesabınız silinmiş", Toast.LENGTH_SHORT).show();
                                    FirebaseAuth.getInstance().signOut();
                                    startActivity(new Intent(MainActivity.this, GirisActivity.class));
                                }
                            }
                        });
                    }
                    else {
                        FirebaseAuth.getInstance().signOut();
                        startActivity(new Intent(MainActivity.this, MainActivity.class));
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
        else{
            DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("usersF");
            userRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if(dataSnapshot.hasChild(deger)){
                        Toast.makeText(MainActivity.this, "Telefonunuz ve Hesabınız, Kimoo'dan " + dataSnapshot.child(deger).child("ban_durumu").child("sebep").getValue() + " sebebi ile süresiz olarak uzaklaştırıldı.", Toast.LENGTH_LONG).show();
                        FirebaseAuth.getInstance().signOut();
                    }else{
                        Toast.makeText(MainActivity.this, "Hesabınız silinmiştir. Bu telefonda başka hesap oluşturamazsınız.", Toast.LENGTH_SHORT).show();
                        FirebaseAuth.getInstance().signOut();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
    }

    public void scaleView(View v, float startScale, float endScale) {
        Animation anim = new ScaleAnimation(startScale, endScale, // Start and end values for the X axis scaling
                startScale, endScale, // Start and end values for the Y axis scaling
                Animation.RELATIVE_TO_SELF, 0.5f, // Pivot point of X scaling
                Animation.RELATIVE_TO_SELF, 0.5f); // Pivot point of Y scaling
        anim.setFillAfter(true); // Needed to keep the result of the animation
        anim.setDuration(10000);
        v.startAnimation(anim);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 3){
            if(resultCode != 0) {
                //Kontrol();
            }
        }
    }

    private void DevamEt(){
        if (fuser != null) {
            final ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
            if (!sonuclandi) {
                Timer myTimer = new Timer();
                myTimer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        if (!sonuclandi && fuser != null) {
                            if (internetVarmi()) {
                                myRef = FirebaseDatabase.getInstance().getReference("usersF");
                                myRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        if (!dataSnapshot.child(fuser.getUid()).hasChild("usernamef")) {
                                            Intent git = new Intent(getApplicationContext(), Kayit2Activity.class);
                                            Toast.makeText(getApplicationContext(), "Bilgilerinizi tamamlayınız.", Toast.LENGTH_SHORT).show();
                                            startActivity(git);
                                            sonuclandi = true;
                                        } else {
                                            IdDegisimKontrol();
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {
                                        // Failed to read value
                                    }
                                });
                            } else {
                                Intent intent = new Intent(MainActivity.this, BaglantiYok.class);
                                startActivity(intent);
                                sonuclandi = true;
                            }
                        }
                    }
                }, 0, 1000);
            }
        }
    }

    private void Kontrol(String deger) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("usersF").child(fuser.getUid());
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                long suan = dataSnapshot.child("suan").getValue(long.class);
                long zaman = dataSnapshot.child("ban_durumu").child("tarih").getValue(long.class);
                long sure = dataSnapshot.child("ban_durumu").child("sure").getValue(long.class);
                if(deger.equals("kisit")) {
                    if (dataSnapshot.child("kisitli_erisim_engeli").child("durum").getValue().equals("yok")) {
                        DevamEt();
                    } else {
                        long zaman2 = dataSnapshot.child("kisitli_erisim_engeli").child("zaman").getValue(long.class);
                        long sure2 = dataSnapshot.child("kisitli_erisim_engeli").child("sure").getValue(long.class);
                        if (sure2 != 1 && (suan - zaman2) > sure2) {
                            dataSnapshot.child("kisitli_erisim_engeli").child("durum").getRef().setValue("yok");
                            dataSnapshot.child("kisitli_erisim_engeli").child("zaman").getRef().setValue(0);
                            dataSnapshot.child("kisitli_erisim_engeli").child("sure").getRef().setValue(0).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(@NonNull Void aVoid) {
                                    DevamEt();
                                }
                            });
                        } else {
                            Toast.makeText(MainActivity.this, "Şuan erişim izniniz yok, lütfen kısa bir süre sonra tekrar deneyiniz.", Toast.LENGTH_LONG).show();
                        }
                    }
                }
                else if(deger.equals("ban")){
                    if (sure != 1 && (suan - zaman) > sure) {
                        buton.setVisibility(View.GONE);
                        dataSnapshot.child("ban_durumu").child("uyari_sayisi").getRef().setValue(2);
                        dataSnapshot.child("ban_durumu").child("durum").getRef().setValue("yok");
                        dataSnapshot.child("ban_durumu").child("sure").getRef().setValue(0);
                        dataSnapshot.child("ban_durumu").child("tarih").getRef().setValue(0).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(@NonNull Void aVoid) {
                                DevamEt();
                            }
                        });
                    }
                    else if (sure == 1){
                        Toast.makeText(MainActivity.this, "Kimoo'dan " + dataSnapshot.child("ban_durumu").child("sebep").getValue() + " sebebi ile süresiz olarak uzaklaştırıldınız.", Toast.LENGTH_LONG).show();
                        FirebaseAuth.getInstance().signOut();
                    }
                    else{
                        Toast.makeText(MainActivity.this, "Kimoo'dan " + dataSnapshot.child("ban_durumu").child("sebep").getValue() + " sebebi ile " + ((sure - (suan - zaman))/3600000) + " saat uzaklaştırıldınız.", Toast.LENGTH_LONG).show();
                        //FirebaseAuth.getInstance().signOut();
                    }
                }
                else{
                    if(suan > ((suan - zaman) + (86400000 * 30))){
                        if (dataSnapshot.child("ban_durumu").child("uyari_sayisi").getValue(Integer.class) > 0) {
                            dataSnapshot.child("ban_durumu").child("tarih").getRef().setValue(ServerValue.TIMESTAMP);
                            dataSnapshot.child("ban_durumu").child("uyari_sayisi").getRef().setValue(dataSnapshot.child("ban_durumu").child("uyari_sayisi").getValue(Integer.class) - 1);
                            DevamEt();
                        }
                        else {
                            dataSnapshot.child("ban_durumu").child("tarih").getRef().setValue(ServerValue.TIMESTAMP);
                            dataSnapshot.child("ban_durumu").child("uyari_sayisi").getRef().setValue(0);
                            DevamEt();
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void IdDegisimKontrol(){
        // Eğer son id 1 gün içinde değişmişse yada değişmemişse
        DatabaseReference idRef = FirebaseDatabase.getInstance().getReference("usersF").child(fuser.getUid());
        idRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull final DataSnapshot dataSnapshot) {
                asilDataSnapshot= dataSnapshot;
                final User fuser = dataSnapshot.getValue(User.class);
                final int idlerCount = (int) dataSnapshot.child("idler").getChildrenCount();
                dataSnapshot.getRef().child("suan").setValue(ServerValue.TIMESTAMP).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        final Long ref = (Long) dataSnapshot.child("son_id_degisimi").getValue();
                        final Long ref2 = (Long) dataSnapshot.child("suan").getValue();
                        long son =  ref.longValue() + 43200000;
                        long suan = ref2.longValue();
                        if (suan < son) {
                            Devam();
                            //Toast.makeText(MainActivity.this, "Sıkıntı yok devam!" + farkiHesapla(son,suan), Toast.LENGTH_LONG).show();
                        } else {
                            SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences("son_ad", Context.MODE_PRIVATE);
                            final SharedPreferences.Editor editorr = sharedPreferences.edit();
                            if(idlerCount == 1){
                                dataSnapshot.child("idler").getRef().child("id-2").setValue(olusanId);
                                dataSnapshot.child("son_id_degisimi").getRef().setValue(ref2);
                                dataSnapshot.child("id").getRef().setValue(olusanId);
                                editorr.putString("son_ka","kim_" + fuser.getUsernamef() + olusanId);
                                editorr.commit();
                                Devam();
                            }
                            else if(idlerCount == 2){
                                dataSnapshot.child("idler").getRef().child("id-3").setValue(olusanId);
                                dataSnapshot.child("son_id_degisimi").getRef().setValue(ref2);
                                dataSnapshot.child("id").getRef().setValue(olusanId);
                                editorr.putString("son_ka","kim_" + fuser.getUsernamef() + olusanId);
                                editorr.commit();
                                Devam();
                            }
                            else if(idlerCount == 3){
                                dataSnapshot.child("idler").getRef().child("id-4").setValue(olusanId);
                                dataSnapshot.child("son_id_degisimi").getRef().setValue(ref2);
                                dataSnapshot.child("id").getRef().setValue(olusanId);
                                editorr.putString("son_ka","kim_" + fuser.getUsernamef() + olusanId);
                                editorr.commit();
                                Devam();
                            }
                            else if(idlerCount == 4){
                                dataSnapshot.child("idler").getRef().child("id-5").setValue(olusanId);
                                dataSnapshot.child("son_id_degisimi").getRef().setValue(ref2);
                                dataSnapshot.child("id").getRef().setValue(olusanId);
                                editorr.putString("son_ka","kim_" + fuser.getUsernamef() + olusanId);
                                editorr.commit();
                                Devam();
                            }
                            else if(idlerCount == 5){
                                dataSnapshot.child("idler").child("id-1").getRef().setValue(dataSnapshot.child("idler").child("id-2").getValue());
                                dataSnapshot.child("idler").child("id-2").getRef().setValue(dataSnapshot.child("idler").child("id-3").getValue());
                                dataSnapshot.child("idler").child("id-3").getRef().setValue(dataSnapshot.child("idler").child("id-4").getValue());
                                dataSnapshot.child("idler").child("id-4").getRef().setValue(dataSnapshot.child("idler").child("id-5").getValue());
                                dataSnapshot.child("idler").getRef().child("id-5").setValue(olusanId);
                                dataSnapshot.child("son_id_degisimi").getRef().setValue(ref2);
                                dataSnapshot.child("id").getRef().setValue(olusanId);
                                editorr.putString("son_ka","kim_" + fuser.getUsernamef() + olusanId);
                                editorr.commit();
                                Devam();
                            }
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Kontrol("");
                    }
                });

                // Bu bulunabilme açıldığında yazılacak
                                                        /*int idlerCount = (int) dataSnapshot.child("idler").getChildrenCount();
                                                        for(DataSnapshot ds : dataSnapshot.child("idler").getChildren()){
                                                            myAdapter.setName(ds.child("id-"+idlerCount).getValue().toString());
                                                        }*/
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        /**/
    }
    private void Devam(){
        if(asilDataSnapshot != null){
            if (asilDataSnapshot.child("hesap_durumu").child("durum").getValue(Integer.class) != 0) {
                asilDataSnapshot.child("hesap_durumu").child("durum").getRef().setValue(0);
                asilDataSnapshot.child("hesap_durumu").child("zaman").getRef().setValue(0).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(@NonNull Void unused) {if (asilDataSnapshot.child("guvenlik").child("uyari_suresi").getValue(Long.class) != 0) {
                        if ((asilDataSnapshot.child("guvenlik").child("uyari_suresi").getValue(Long.class) + asilDataSnapshot.child("guvenlik").child("uyari_zamani").getValue(Long.class))
                                < asilDataSnapshot.child("suan").getValue(Long.class)) {
                            asilDataSnapshot.child("guvenlik").child("uyari_suresi").getRef().setValue(0);
                            asilDataSnapshot.child("guvenlik").child("uyari_zamani").getRef().setValue(0);
                            asilDataSnapshot.child("guvenlik").child("uyari").getRef().setValue(0).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(@NonNull Void unused) {
                                    Intent i = new Intent(MainActivity.this, TaraActivity.class);
                                    startActivity(i);
                                    sonuclandi = true;
                                }
                            });
                        } else {
                            Intent i = new Intent(MainActivity.this, TaraActivity.class);
                            startActivity(i);
                            sonuclandi = true;
                        }
                    } else {
                        Intent i = new Intent(MainActivity.this, TaraActivity.class);
                        startActivity(i);
                        sonuclandi = true;
                    }
                    }

                });
            }else{
                if (asilDataSnapshot.child("guvenlik").child("uyari_suresi").getValue(Long.class) != 0) {
                if ((asilDataSnapshot.child("guvenlik").child("uyari_suresi").getValue(Long.class) + asilDataSnapshot.child("guvenlik").child("uyari_zamani").getValue(Long.class))
                        < asilDataSnapshot.child("suan").getValue(Long.class)) {
                    asilDataSnapshot.child("guvenlik").child("uyari_suresi").getRef().setValue(0);
                    asilDataSnapshot.child("guvenlik").child("uyari_zamani").getRef().setValue(0);
                    asilDataSnapshot.child("guvenlik").child("uyari").getRef().setValue(0).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(@NonNull Void unused) {
                            Intent i = new Intent(MainActivity.this, TaraActivity.class);
                            startActivity(i);
                            sonuclandi = true;
                        }
                    });
                } else {
                    Intent i = new Intent(MainActivity.this, TaraActivity.class);
                    startActivity(i);
                    sonuclandi = true;
                }
            } else {
                Intent i = new Intent(MainActivity.this, TaraActivity.class);
                startActivity(i);
                sonuclandi = true;
            }
            }
        }
        else {
            Intent i = new Intent(MainActivity.this, TaraActivity.class);
            startActivity(i);
            sonuclandi = true;
        }
    }

    private void GuvenlikKontrol() {
        SharedPreferences sharedPreferences = getSharedPreferences("guvenlik", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        if(sharedPreferences.getInt("giris_denemesi",0) > 2){
            Dialog dialog = new Dialog(MainActivity.this);
            dialog.setContentView(R.layout.dialog_dizayn3);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialog.setCancelable(false);

            TextView baslik = dialog.findViewById(R.id.baslik);
            TextView aciklama = dialog.findViewById(R.id.aciklama);
            aciklama.setMovementMethod(new ScrollingMovementMethod());
            Button buton = dialog.findViewById(R.id.buton);
            EditText kelime = dialog.findViewById(R.id.editText);
            kelime.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD);
            kelime.setHint("Güvenlik Kelimesi");
            baslik.setText("Güvenlik Sorusu");
            aciklama.setText("Lütfen uygulamaya kayıt olurken belirlediğiniz güvenlik kelimenizi yazınız. \n (*Büyük-küçük harflere duyarlı değildir.)");
            buton.setText("TAMAM");
            buton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(kelime.getText().toString().trim().length() > 6){
                        if(asilDataSnapshot.child("guvenlik").child("uyari").getValue(Integer.class) < 3) {
                            if(asilDataSnapshot.child("guvenlik").child("guvenlik_kelimesi").getValue(String.class).equals(kelime.getText().toString().toLowerCase().trim())){
                                asilDataSnapshot.child("guvenlik").child("uyari_suresi").getRef().setValue(0);
                                asilDataSnapshot.child("guvenlik").child("uyari_zamani").getRef().setValue(0);
                                asilDataSnapshot.child("guvenlik").child("uyari").getRef().setValue(0).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(@NonNull Void unused) {
                                        editor.putInt("giris_denemesi",0);
                                        editor.commit();
                                        Intent i = new Intent(MainActivity.this, TaraActivity.class);
                                        startActivity(i);
                                        sonuclandi = true;
                                    }
                                });
                            }
                            else{
                                asilDataSnapshot.child("guvenlik").child("uyari_zamani").getRef().setValue(ServerValue.TIMESTAMP);
                                asilDataSnapshot.child("guvenlik").child("uyari_suresi").getRef().setValue(86400000 * 1);
                                asilDataSnapshot.child("guvenlik").child("uyari").getRef().setValue(asilDataSnapshot.child("guvenlik").child("uyari").getValue(Integer.class) + 1).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(@NonNull Void unused) {
                                        kelime.setError("Yanlış!");
                                    }
                                });
                            }
                        }else{
                            asilDataSnapshot.child("guvenlik").child("uyari_zamani").getRef().setValue(ServerValue.TIMESTAMP);
                            asilDataSnapshot.child("guvenlik").child("uyari_suresi").getRef().setValue(86400000 * 3).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(@NonNull Void unused) {
                                    Toast.makeText(MainActivity.this, "Güvenlik ihlali yaptınız, bir süre hesabınıza erişemezsiniz.", Toast.LENGTH_SHORT).show();
                                    FirebaseAuth.getInstance().signOut();
                                }
                            });
                        }
                    }else{
                        kelime.setError("Bu kelime en az 7 karakterden oluşmalı.");
                    }
                }
            });
            dialog.show();
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        sonuclandi = false;
        //Kontrol();
        SharedPreferences tas_shared = getSharedPreferences("TasarimBilgileri",MODE_PRIVATE);
        TasarimDegistir(tas_shared.getString("tasarim_arayuz","1"));
        Basla();
    }

    @Override
    protected void onPause() {
        super.onPause();
        sonuclandi = true;
        timer.cancel();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        sonuclandi = true;
        timer.cancel();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
        System.exit(0);
    }

    public static String getUID() {
        int DIGITS = 5;
        StringBuilder sb = new StringBuilder(DIGITS);
        for(int i = 0;i < DIGITS;i++) {
            sb.append((char) (Math.random() * 10 + '0'));
        }
        return sb.toString();
    }

}
