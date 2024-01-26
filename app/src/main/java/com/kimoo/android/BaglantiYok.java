package com.kimoo.android;

import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.app.Dialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.GradientDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.text.TextUtils;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.kimoo.android.extra.ArkadaBul;
import com.kimoo.android.extra.ArkadaBul2;
import com.kimoo.android.extra.TasarimRenginiGetir;

import java.util.Timer;
import java.util.TimerTask;


public class BaglantiYok extends AppCompatActivity {
    private View bul,bulun,bulvebulun;
    private LinearLayout background;
    private CardView bul_card, bulun_card, bulvebulun_card;
    public static final String mBroadcastStringAction = "com.unsaladvance.kimoo.extra.ArkadaBul.string";
    public static final String mBroadcastIntegerAction = "com.unsaladvance.kimoo.extra.ArkadaBul.integer";
    public static final String mBroadcastArrayListAction = "com.unsaladvance.kimoo.extra.ArkadaBul.arraylist";
    String yazi = "", baslikk = "";
    int i= 1;
    private boolean butonTiklandiMi = false;
    int gelendeger;
    Timer timer;
    Intent serviceIntent;
    boolean bulbulun = false,connected = false, bitti = false;
    private IntentFilter mIntentFilter;
    BluetoothAdapter myAdapter;
    TextView text;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_baglanti_yok);
        StatuBarAyarla();
        bitti = false;
        bul = findViewById(R.id.tara);
        bulun = findViewById(R.id.tara2);
        bulvebulun = findViewById(R.id.taraikiside);
        background = findViewById(R.id.background);

        bul_card = findViewById(R.id.tara_card);
        bulun_card = findViewById(R.id.tara2_card);
        bulvebulun_card = findViewById(R.id.taraikiside_card);

        mIntentFilter = new IntentFilter();
        mIntentFilter.addAction(mBroadcastStringAction);
        mIntentFilter.addAction(mBroadcastIntegerAction);
        mIntentFilter.addAction(mBroadcastArrayListAction);
        myAdapter = BluetoothAdapter.getDefaultAdapter();
        serviceIntent = new Intent(getApplicationContext(), ArkadaBul2.class);
        Intent serviceIntent = new Intent(this, BaglantiYok.class);
        startService(serviceIntent);

        SharedPreferences tas_shared = getSharedPreferences("TasarimBilgileri",MODE_PRIVATE);
        TasarimDegistir(tas_shared.getString("tasarim_arayuz","1"));

        /**/

        if(!bitti) {
            Timer timer = new Timer();
            TimerTask t = new TimerTask() {
                @Override
                public void run() {
                    if(!bitti) {
                        final ConnectivityManager connectivityManager = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
                        if (connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                                connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED) {
                            connected = true;
                            Intent intent = new Intent(BaglantiYok.this, MainActivity.class);
                            startActivity(intent);
                            bitti = true;
                        }
                        else if (connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.DISCONNECTED &&
                                connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.DISCONNECTED)
                            connected = false;
                    }
                }
            };
            timer.scheduleAtFixedRate(t,0,1000);
        }

        bulun_card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!butonTiklandiMi) {
                    butonTiklandiMi = true;
                    if (isNotificationChannelEnabled(BaglantiYok.this, "arkadabulunma")) {
                        bulbulun = false;
                        baslikk = "Bulunma Süresini Belirle";
                        yazi = "Diğer Kimoo kullanıcıları tarafından bulunmak istiyorsanız lütfen süre belirleyin.";
                        IzinKontrol2();
                    } else
                        openChannelSettings("arkadabulunma");
                }
            }
        });
        bul_card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!butonTiklandiMi) {
                    butonTiklandiMi = true;
                    if (isNotificationChannelEnabled(BaglantiYok.this, "arkadabulma")) {
                        Dialog dialog = new Dialog(BaglantiYok.this);
                        dialog.setContentView(R.layout.dialog_dizayn5);
                        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

                        TextView baslik = dialog.findViewById(R.id.baslik);
                        TextView aciklama = dialog.findViewById(R.id.aciklama);
                        aciklama.setMovementMethod(new ScrollingMovementMethod());
                        Button buton = dialog.findViewById(R.id.buton); // Hayır
                        Button buton2 = dialog.findViewById(R.id.buton2); // Evet

                        baslik.setText("Arkaplanda Bul");
                        aciklama.setText("Diğer Kimoo kullanıcılarını arkaplanda bulmak ister misiniz? Siz kapatana kadar arkaplanda aramaya devam edecektir.\nBulunan kullanıcıları tespit edebilmeniz için 5 gün içerisinde internete bağlanmanız ve\"Tüm Bulunanlar\" sayfasına girmeniz gerekir.");
                        buton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                butonTiklandiMi = false;
                                dialog.dismiss();
                            }
                        });
                        buton2.setOnClickListener(new View.OnClickListener() { // EVet
                            @Override
                            public void onClick(View v) {
                                bulbulun = false;
                                SharedPreferences sharedPreferences = getSharedPreferences("bulunan", Context.MODE_PRIVATE);
                                ArkadaBul.neredeKalmisti = sharedPreferences.getInt("KalinanYer", 0);
                                IzinKontrol();
                                butonTiklandiMi = false;
                                dialog.dismiss();
                            }
                        });
                        dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                            @Override
                            public void onCancel(DialogInterface dialog) {
                                butonTiklandiMi = false;
                            }
                        });
                        dialog.show();
                    }
                    else {
                        openChannelSettings("arkadabulma");
                    }

                }
            }
        });
        bulvebulun_card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!butonTiklandiMi) {
                    butonTiklandiMi = true;
                    if (isNotificationChannelEnabled(BaglantiYok.this, "arkadabulma")) {
                        bulbulun = true;
                        baslikk = "Bul ve Bulun";
                        yazi = "Bulma işlemi başladı! Diğer bluetooth cihazları tarafından bulunma sürenizi belirleyiniz. Eğer bu süre biterse diğer Kimoo kullanıcıları sizi bulamaz.";
                        IzinKontrol2();
                    } else
                        openChannelSettings("arkadabulma");
                }
            }
        });
        //dikkatCekmeAnim(bulvebulun,1000,0.97f,1f);
        //dikkatCekmeAnim(bulun,1000,0.97f,1f);
        //dikkatCekmeAnim(bul,1000,0.97f,1f);
    }

    @Override
    protected void onStop() {
        super.onStop();
        butonTiklandiMi = false;
    }
    private void openChannelSettings(String channelId) {
        Intent intent = new Intent(Settings.ACTION_CHANNEL_NOTIFICATION_SETTINGS);
        intent.putExtra(Settings.EXTRA_APP_PACKAGE, getPackageName());
        intent.putExtra(Settings.EXTRA_CHANNEL_ID, channelId);
        startActivity(intent);
    }
    private boolean isNotificationChannelEnabled(Context context, @Nullable String channelId){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            if(!TextUtils.isEmpty(channelId)) {
                NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
                NotificationChannel channel = manager.getNotificationChannel(channelId);
                return channel.getImportance() != NotificationManager.IMPORTANCE_NONE;
            }
            return false;
        } else {
            return NotificationManagerCompat.from(context).areNotificationsEnabled();
        }
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
    private void dikkatCekmeAnim(final View view, int hiz, float baslangicBoyut, float boyut) {
        view.setScaleX(baslangicBoyut);
        view.setScaleY(baslangicBoyut);
        ObjectAnimator scaleDownXIlk = ObjectAnimator.ofFloat(view, "scaleX", boyut);
        ObjectAnimator scaleDownYIlk = ObjectAnimator.ofFloat(view, "scaleY", boyut);
        scaleDownXIlk.setDuration(hiz);
        scaleDownYIlk.setDuration(hiz);
        scaleDownXIlk.start();
        scaleDownYIlk.start();
        scaleDownYIlk.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                ObjectAnimator scaleDownX = ObjectAnimator.ofFloat(view, "scaleX", baslangicBoyut);
                ObjectAnimator scaleDownY = ObjectAnimator.ofFloat(view, "scaleY", baslangicBoyut);
                scaleDownX.setDuration(hiz);
                scaleDownY.setDuration(hiz);
                scaleDownX.start();
                scaleDownY.start();
                scaleDownX.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                        scaleDownXIlk.start();
                        scaleDownYIlk.start();
                    }
                });
            }
        });
    }

    private void TasarimDegistir(String tasDegeri) {
        GradientDrawable gradientBackground =  new GradientDrawable();
        gradientBackground.setOrientation(GradientDrawable.Orientation.LEFT_RIGHT);

        GradientDrawable gradientYumusak =  new GradientDrawable();
        gradientYumusak.setOrientation(GradientDrawable.Orientation.TL_BR);
        gradientYumusak.setCornerRadius(50);
        GradientDrawable gradientYumusak2 =  new GradientDrawable();
        gradientYumusak2.setOrientation(GradientDrawable.Orientation.TL_BR);
        gradientYumusak2.setCornerRadius(50);
        GradientDrawable gradientYumusak3 =  new GradientDrawable();
        gradientYumusak3.setOrientation(GradientDrawable.Orientation.TL_BR);
        gradientYumusak3.setCornerRadius(50);

        int renk1 = 0,renk2 = 0,t1start = 0,t2start = 0,t1end = 0,t2end = 0,orta = 0;


        renk1 = TasarimRenginiGetir.RengiGetir(BaglantiYok.this,"renk1",tasDegeri);
        renk2 = TasarimRenginiGetir.RengiGetir(BaglantiYok.this,"renk2",tasDegeri);
        t1start = TasarimRenginiGetir.RengiGetir(BaglantiYok.this,"t1start",tasDegeri);
        t2start = TasarimRenginiGetir.RengiGetir(BaglantiYok.this,"t2start",tasDegeri);
        t1end = TasarimRenginiGetir.RengiGetir(BaglantiYok.this,"t1end",tasDegeri);
        t2end = TasarimRenginiGetir.RengiGetir(BaglantiYok.this,"t2end",tasDegeri);
        orta = TasarimRenginiGetir.RengiGetir(BaglantiYok.this,"orta",tasDegeri);

        gradientBackground.setColors(new int[]{
                renk1,
                orta,
                renk2
        });
        gradientYumusak.setColors(new int[]{
                renk1,
                orta,
                renk2
        });
        gradientYumusak2.setColors(new int[]{
                renk1,
                orta,
                renk2
        });
        gradientYumusak3.setColors(new int[]{
                renk1,
                orta,
                renk2
        });

        bul.setBackground(gradientYumusak);
        bulvebulun.setBackground(gradientYumusak2);
        bulun.setBackground(gradientYumusak3);
        background.setBackground(gradientBackground);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //timer.cancel();
        Thread.interrupted();
        bitti = true;
    }

    private void BulunabilmeSuresiBelirle() {
        AlertDialog.Builder builder = new AlertDialog.Builder(BaglantiYok.this);
        builder.setCancelable(true);

        View view = getLayoutInflater().inflate(R.layout.dialog_dizayn,null);
        String[] secenekler = {"5 Dakika", "15 Dakika", "30 Dakika","1 Saat"};
        builder.setView(view);

        EditText sikayet = view.findViewById(R.id.dialog_edittext);
        TextView baslik = view.findViewById(R.id.dialog_baslik);
        TextView bilgi = view.findViewById(R.id.dialog_bilgi);
        bilgi.setText(yazi);
        baslik.setVisibility(View.GONE);
        sikayet.setVisibility(View.GONE);
        builder.setTitle(baslikk);
        builder.setItems(secenekler, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if(which == 0){
                    BulunabilmeSuresiniDegistir(5 * 60);
                }else if(which == 1){
                    BulunabilmeSuresiniDegistir(15 * 60);
                }else if(which == 2){
                    BulunabilmeSuresiniDegistir(30 * 60);
                }else if(which == 3) {
                    BulunabilmeSuresiniDegistir(60 * 60);
                }
            }
        });

        builder.show();
    }
    private void BulunabilmeSuresiniDegistir(final int i) {
        gelendeger = i;
        SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences("son_ad", Context.MODE_PRIVATE);
        final SharedPreferences.Editor editorr = sharedPreferences.edit();

        myAdapter.setName(sharedPreferences.getString("son_ka",""));
        if(!myAdapter.getName().toString().equals("")) {
            Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
            intent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, i);
            if(!bulbulun)
                startActivityForResult(intent, 7);
            else
                startActivityForResult(intent, 6);
        }else{
            Toast.makeText(this, "Bir hata oluştu. Bu özelliği şuan kullanamayabilirsiniz.", Toast.LENGTH_SHORT).show();
            //BulunabilmeSuresiBelirle();
        }


    }
    public void IzinKontrol(){
        if (myAdapter.isEnabled()) {
            if (ContextCompat.checkSelfPermission(BaglantiYok.this, Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED)
            {
                if(bulbulun) {
                    ArkadaBul.deaktif = false;
                    ArkadaBul.bulma = true;
                    Intent intent = new Intent(BaglantiYok.this, ArkadaBul.class);
                    startService(intent);

                }else{
                    ArkadaBul.deaktif = false;
                    ArkadaBul.bulma = true;
                    Intent intent = new Intent(BaglantiYok.this, ArkadaBul.class);
                    startService(intent);
                }
            } else {
                ActivityCompat.requestPermissions(BaglantiYok.this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION},1);

            }

        } else {
            Intent eintent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(eintent,3);
        }
    }
    public void IzinKontrol2(){
        if (myAdapter.isEnabled()) {
            if (ContextCompat.checkSelfPermission(BaglantiYok.this, Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED)
            {
                BulunabilmeSuresiBelirle();
                if(bulbulun)
                    IzinKontrol();
            } else {
                ActivityCompat.requestPermissions(BaglantiYok.this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION},1);

            }

        } else {
            Intent eintent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(eintent,4);
        }
        butonTiklandiMi = false;
    }
    private void startServicee(int kalanSure){
        if(ArkadaBul2.yazi <= 0) {
            ArkadaBul2.yazi = kalanSure;
            ArkadaBul2.deaktif = false;
            ArkadaBul2.bulunma = true;
            getApplicationContext().startService(serviceIntent);
        }else{
            if (ArkadaBul2.yazi + kalanSure > 3600)
                ArkadaBul2.yazi += 3600;
            else
                ArkadaBul2.yazi += kalanSure;
        }
    }
    private void startServicee2(int kalanSure){
        ArkadaBul2.yazi = kalanSure;
        ArkadaBul2.deaktif = false;
        ArkadaBul2.bulunma = true;
        ArkadaBul.bulma = true;
        getApplicationContext().startService(serviceIntent);
        Intent intent = new Intent(BaglantiYok.this, ArkadaBul.class);
        startService(intent);
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if(requestCode == 3){
            if(resultCode != 0) {
                IzinKontrol();
            }
        }
        if(requestCode == 4){
            if(resultCode != 0) {
                IzinKontrol2();
            }
        }
        if(requestCode == 7){
            if(resultCode != 0) {
                startServicee(gelendeger);
            }
        }
        if(requestCode == 6){
            if(resultCode != 0) {
                startServicee2(gelendeger);
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 1: {

                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    IzinKontrol();

                } else {
                    Toast.makeText(BaglantiYok.this, "Eğer izin vermezseniz diğer Kimoo kullanıcıları sizi bulamaz.", Toast.LENGTH_LONG).show();
                }
                return;
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
    }


    @Override
    protected void onPause() {
        super.onPause();
        butonTiklandiMi = false;
    }
}
