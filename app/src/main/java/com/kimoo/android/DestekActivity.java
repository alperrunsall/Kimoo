package com.kimoo.android;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.ScrollingMovementMethod;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.billingclient.api.BillingClient;
import com.android.billingclient.api.BillingClientStateListener;
import com.android.billingclient.api.BillingFlowParams;
import com.android.billingclient.api.BillingResult;
import com.android.billingclient.api.ConsumeParams;
import com.android.billingclient.api.ConsumeResponseListener;
import com.android.billingclient.api.Purchase;
import com.android.billingclient.api.PurchasesResponseListener;
import com.android.billingclient.api.PurchasesUpdatedListener;
import com.android.billingclient.api.SkuDetails;
import com.android.billingclient.api.SkuDetailsParams;
import com.android.billingclient.api.SkuDetailsResponseListener;
import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnSuccessListener;
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

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class DestekActivity extends AppCompatActivity {

    private Button gpOy,insTakip,insPaylas,wpPaylas,ytTakip,webZiyaret,maneviDestek,maddiDestek,
        buton1,buton2,buton3,buton4,buton5,buton6;
    private TextView buton_basligi_1,buton_basligi_2,buton_basligi_3,buton_basligi_4,buton_basligi_5,buton_basligi_6,
        destek1_aciklama,destek2_aciklama,destek3_aciklama,destek4_aciklama,destek5_aciklama,destek6_aciklama;
    private LinearLayout ss,yaziLayout,maneviLayout,button_back;
    private ScrollView maddiLayout;
    private User Kullanici;
    private FirebaseUser fuser;
    private CircleImageView pp;
    private BillingClient billingClient;
    private TextView isim, mesaj, anaBaslik, yazi2;
    private ImageView gp_ic, ins_ic, ins_ic2, yt_ic, wp_ic, web_ic;
    private int carpanDegeri = 1, tiklansinMi = 0;
    RelativeLayout background;
    private String satinAlinanDeger;
    private DatabaseReference reference;
    private int ortaRenk;
    private DataSnapshot sistemSnapShot, asilSnapShot;
    private String kosulYazisi = null;
    private String AdSoyad;
    private boolean butonTiklandiMi = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_destek);

        StatuBarAyarla();

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitleTextColor(getResources().getColor(R.color.colorPrimary));
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setElevation(0);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        insTakip = findViewById(R.id.insTakip);
        insPaylas = findViewById(R.id.insPaylas);
        wpPaylas = findViewById(R.id.wpPaylas);
        ytTakip = findViewById(R.id.ytTakip);
        yazi2 = findViewById(R.id.yazi2);
        gp_ic = findViewById(R.id.gp_ic);
        gpOy = findViewById(R.id.gpOy);
        webZiyaret = findViewById(R.id.webZiyaret);
        maneviDestek = findViewById(R.id.manevi);
        maddiDestek = findViewById(R.id.maddi);
        yaziLayout = findViewById(R.id.yaziLayout);
        maneviLayout = findViewById(R.id.maneviLayout);
        maddiLayout = findViewById(R.id.maddiLayout);
        buton_basligi_1 = findViewById(R.id.buton_basligi_1);
        buton_basligi_2 = findViewById(R.id.buton_basligi_2);
        buton_basligi_3 = findViewById(R.id.buton_basligi_3);
        buton_basligi_4 = findViewById(R.id.buton_basligi_4);
        buton_basligi_5 = findViewById(R.id.buton_basligi_5);
        buton_basligi_6 = findViewById(R.id.buton_basligi_6);
        destek1_aciklama = findViewById(R.id.destek1_aciklama);
        destek2_aciklama = findViewById(R.id.destek2_aciklama);
        destek3_aciklama = findViewById(R.id.destek3_aciklama);
        destek4_aciklama = findViewById(R.id.destek4_aciklama);
        destek5_aciklama = findViewById(R.id.destek5_aciklama);
        destek6_aciklama = findViewById(R.id.destek6_aciklama);
        button_back = findViewById(R.id.button_back);
        ins_ic = findViewById(R.id.ins_ic);
        ins_ic2 = findViewById(R.id.ins_ic2);
        yt_ic = findViewById(R.id.yt_ic);
        web_ic = findViewById(R.id.web_ic);
        wp_ic = findViewById(R.id.wp_ic);
        //anaBaslik = findViewById(R.id.ana_baslik);
        buton1 = findViewById(R.id.buton1);
        buton2 = findViewById(R.id.buton2);
        buton3 = findViewById(R.id.buton3);
        buton4 = findViewById(R.id.buton4);
        buton5 = findViewById(R.id.buton5);
        buton6 = findViewById(R.id.buton6);
        isim = findViewById(R.id.isim);
        pp = findViewById(R.id.pp);
        ss = findViewById(R.id.ozelSs);
        ss.setDrawingCacheEnabled(true);
        background = findViewById(R.id.background);

        mesaj = findViewById(R.id.mesaj);
        mesaj.setMovementMethod(new ScrollingMovementMethod());

        SharedPreferences tas_shared = getSharedPreferences("TasarimBilgileri",MODE_PRIVATE);
        TasarimDegistir(tas_shared.getString("tasarim_arayuz","1"));

        fuser = FirebaseAuth.getInstance().getCurrentUser();

        reference = FirebaseDatabase.getInstance().getReference("usersF").child(fuser.getUid());

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("usersF").child(fuser.getUid());
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                asilSnapShot = dataSnapshot;
                Kullanici = dataSnapshot.getValue(User.class);
                Glide.with(DestekActivity.this)
                        .asBitmap()
                        .load(dataSnapshot.child("fotograflarim").child("pp").getValue(String.class))
                        .into(pp);
                isim.setText("Selam ben "+Kullanici.getAd().substring(0,1).toUpperCase()+Kullanici.getAd().substring(1).toLowerCase()+",");
                AciklamalariDuzenle();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        DatabaseReference sisref = FirebaseDatabase.getInstance().getReference("Sistem");
        sisref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                kosulYazisi = dataSnapshot.child("yazilar").child("kullanim_sartlari").getValue(String.class);
                sistemSnapShot = dataSnapshot;
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        yazi2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip;
                clip = ClipData.newPlainText("Link", "play.google.com/store/apps/details?id=com.kimoo.android");
                clipboard.setPrimaryClip(clip);
                Toast.makeText(DestekActivity.this, "Link kopyalandı", Toast.LENGTH_SHORT).show();
            }
        });

        gpOy.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(final View view, MotionEvent motionEvent) {
                int action = motionEvent.getAction();
                if (action == MotionEvent.ACTION_DOWN) {
                    startScaleAnimation(view);
                    return true;
                } else if (action == MotionEvent.ACTION_UP) {
                    cancelScaleAnimation(view,12);
                    return true;
                }
                return false;
            }
        });
        insTakip.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(final View view, MotionEvent motionEvent) {
                int action = motionEvent.getAction();
                if (action == MotionEvent.ACTION_DOWN) {
                    startScaleAnimation(view);
                    return true;
                } else if (action == MotionEvent.ACTION_UP) {
                    cancelScaleAnimation(view,7);
                    return true;
                }
                return false;
            }
        });
        insPaylas.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(final View view, MotionEvent motionEvent) {
                int action = motionEvent.getAction();
                if (action == MotionEvent.ACTION_DOWN) {
                    startScaleAnimation(view);
                    return true;
                } else if (action == MotionEvent.ACTION_UP) {
                    cancelScaleAnimation(view,8);
                    return true;
                }
                return false;
            }
        });
        wpPaylas.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(final View view, MotionEvent motionEvent) {
                int action = motionEvent.getAction();
                if (action == MotionEvent.ACTION_DOWN) {
                    startScaleAnimation(view);
                    return true;
                } else if (action == MotionEvent.ACTION_UP) {
                    cancelScaleAnimation(view,9);
                    return true;
                }
                return false;
            }
        });
        ytTakip.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(final View view, MotionEvent motionEvent) {
                int action = motionEvent.getAction();
                if (action == MotionEvent.ACTION_DOWN) {
                    startScaleAnimation(view);
                    return true;
                } else if (action == MotionEvent.ACTION_UP) {
                    cancelScaleAnimation(view,10);
                    return true;
                }
                return false;
            }
        });
        webZiyaret.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(final View view, MotionEvent motionEvent) {
                int action = motionEvent.getAction();
                if (action == MotionEvent.ACTION_DOWN) {
                    startScaleAnimation(view);
                    return true;
                } else if (action == MotionEvent.ACTION_UP) {
                    cancelScaleAnimation(view,11);
                    return true;
                }
                return false;
            }
        });


        maneviDestek.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(maddiDestek.getText().equals("geri gel"))
                    carpanDegeri = 2;
                else
                    carpanDegeri = 1;
                if (!maneviDestek.getText().equals("geri gel")) {
                   // anaBaslik.setText("BİZİ TAKİP EDİN VE PAYLAŞIN");
                    if (maneviLayout.getVisibility() == View.INVISIBLE)
                        SolaMiSagaMi(maneviLayout,"sola",0,maneviDestek,"paylaş");
                    if (maddiLayout.getVisibility() == View.INVISIBLE)
                        SolaMiSagaMi(maddiLayout,"saga",0,maddiDestek,"destek");

                    SolaMiSagaMi(maneviLayout,"saga",200,maneviDestek,"paylaş");
                    SolaMiSagaMi(yaziLayout,"saga",200,maneviDestek,"geri gel");
                    SolaMiSagaMi(maddiLayout,"saga",200,maddiDestek,"destek");
                    //barAnim(yaziLayout);
                    maneviDestek.setClickable(false);
                }
                else{
                    //anaBaslik.setText("BÜYÜMEMİZİ İSTER MİSİNİZ");
                    SolaMiSagaMi(maddiLayout,"sola",200,maddiDestek,"destek");
                    SolaMiSagaMi(maneviLayout,"sola",200,maneviDestek,"paylaş");
                    SolaMiSagaMi(yaziLayout,"sola",200,maneviDestek,"paylaş");
                    maneviDestek.setClickable(false);
                }
            }
        });
        maddiDestek.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(maneviDestek.getText().equals("geri gel"))
                    carpanDegeri = 2;
                else
                    carpanDegeri = 1;
                if (!maddiDestek.getText().equals("geri gel")) {
                   // anaBaslik.setText("destek SAĞLAMAK İSTER MİSİNİZ");
                    if (maddiLayout.getVisibility() == View.INVISIBLE)
                        SolaMiSagaMi(maddiLayout,"saga",0,maddiDestek,"destek");
                    if (maneviLayout.getVisibility() == View.INVISIBLE)
                        SolaMiSagaMi(maneviLayout,"sola",0,maneviDestek,"paylaş");

                    SolaMiSagaMi(maddiLayout,"sola",200,maddiDestek,"destek");
                    SolaMiSagaMi(yaziLayout,"sola",200,maddiDestek,"geri gel");
                    SolaMiSagaMi(maneviLayout,"sola",200,maneviDestek,"paylaş");
                    //barAnim(yaziLayout);
                    maddiDestek.setClickable(false);
                }
                else{
                   // anaBaslik.setText("BÜYÜMEMİZİ İSTER MİSİNİZ");
                    SolaMiSagaMi(maneviLayout,"saga",200,maneviDestek,"paylaş");
                    SolaMiSagaMi(maddiLayout,"saga",200,maddiDestek,"destek");
                    SolaMiSagaMi(yaziLayout,"saga",200,maddiDestek,"destek");
                    maddiDestek.setClickable(false);
                }
            }
        });

        billingClient = BillingClient.newBuilder(DestekActivity.this)
                .enablePendingPurchases()
                .setListener(new PurchasesUpdatedListener() {
                    @Override
                    public void onPurchasesUpdated(@NonNull BillingResult billingResult, @Nullable List<Purchase> list) {
                        butonTiklandiMi = false;
                        if(billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK && list != null){
                            if(list != null)
                            for(Purchase purchase : list) {
                                if(purchase.getPurchaseState() == Purchase.PurchaseState.PURCHASED && purchase != null && !purchase.isAcknowledged()){
                                    SatisiDogrula(purchase);
                                    if(satinAlinanDeger.equals("destekci1")){
                                        asilSnapShot.child("tas_arayuz_sahibim").getRef().setValue(asilSnapShot.child("tas_arayuz_sahibim").getValue(String.class)+",o1");
                                        asilSnapShot.child("tas_mesaj_sahibim").getRef().setValue(asilSnapShot.child("tas_mesaj_sahibim").getValue(String.class)+",o1");
                                        asilSnapShot.child("tas_profil_sahibim").getRef().setValue(asilSnapShot.child("tas_profil_sahibim").getValue(String.class)+",o1");
                                        if(asilSnapShot.hasChild("rozetlerim"))
                                            asilSnapShot.child("rozetlerim").getRef().setValue(asilSnapShot.child("rozetlerim").getValue(String.class) + ",1");
                                        else
                                            asilSnapShot.child("rozetlerim").getRef().setValue(",1");

                                        asilSnapShot.child("rozet").getRef().setValue("1").addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(@NonNull Void unused) {
                                                Toast.makeText(DestekActivity.this, "Desteğiniz için teşekkür ederiz!", Toast.LENGTH_SHORT).show();
                                            }
                                        });
                                    }
                                    else if(satinAlinanDeger.equals("destekci2")){
                                        asilSnapShot.child("tas_arayuz_sahibim").getRef().setValue(asilSnapShot.child("tas_arayuz_sahibim").getValue(String.class)+",o2");
                                        asilSnapShot.child("tas_mesaj_sahibim").getRef().setValue(asilSnapShot.child("tas_mesaj_sahibim").getValue(String.class)+",o2");
                                        asilSnapShot.child("tas_profil_sahibim").getRef().setValue(asilSnapShot.child("tas_profil_sahibim").getValue(String.class)+",o2");
                                        if(asilSnapShot.hasChild("rozetlerim"))
                                            asilSnapShot.child("rozetlerim").getRef().setValue(asilSnapShot.child("rozetlerim").getValue(String.class) + ",2");
                                        else
                                            asilSnapShot.child("rozetlerim").getRef().setValue(",2");

                                        asilSnapShot.child("rozet").getRef().setValue("2").addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(@NonNull Void unused) {
                                                Toast.makeText(DestekActivity.this, "Desteğiniz için teşekkür ederiz!", Toast.LENGTH_SHORT).show();
                                            }
                                        });
                                    }
                                    else if(satinAlinanDeger.equals("destekci3")){
                                        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Hazirlayanlar");
                                        ref.child(fuser.getUid()).setValue("0"+AdSoyad+tiklansinMi);

                                        asilSnapShot.child("tas_arayuz_sahibim").getRef().setValue(asilSnapShot.child("tas_arayuz_sahibim").getValue(String.class)+",o3");
                                        asilSnapShot.child("tas_mesaj_sahibim").getRef().setValue(asilSnapShot.child("tas_mesaj_sahibim").getValue(String.class)+",o3");
                                        asilSnapShot.child("tas_profil_sahibim").getRef().setValue(asilSnapShot.child("tas_profil_sahibim").getValue(String.class)+",o3");
                                        if(asilSnapShot.hasChild("rozetlerim"))
                                            asilSnapShot.child("rozetlerim").getRef().setValue(asilSnapShot.child("rozetlerim").getValue(String.class) + ",3");
                                        else
                                            asilSnapShot.child("rozetlerim").getRef().setValue(",3");

                                        asilSnapShot.child("rozet").getRef().setValue("3").addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(@NonNull Void unused) {
                                                Toast.makeText(DestekActivity.this, "Desteğiniz için teşekkür ederiz!", Toast.LENGTH_SHORT).show();
                                            }
                                        });
                                    }
                                    else if(satinAlinanDeger.equals("destekci4")){
                                        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Hazirlayanlar");
                                        ref.child(fuser.getUid()).setValue("0"+AdSoyad+tiklansinMi);
                                        asilSnapShot.child("tas_arayuz_sahibim").getRef().setValue(asilSnapShot.child("tas_arayuz_sahibim").getValue(String.class)+",o4");
                                        asilSnapShot.child("tas_mesaj_sahibim").getRef().setValue(asilSnapShot.child("tas_mesaj_sahibim").getValue(String.class)+",o4");
                                        asilSnapShot.child("tas_profil_sahibim").getRef().setValue(asilSnapShot.child("tas_profil_sahibim").getValue(String.class)+",o4");
                                        if(asilSnapShot.hasChild("rozetlerim"))
                                            asilSnapShot.child("rozetlerim").getRef().setValue(asilSnapShot.child("rozetlerim").getValue(String.class) + ",4");
                                        else
                                            asilSnapShot.child("rozetlerim").getRef().setValue(",4");

                                        asilSnapShot.child("rozet").getRef().setValue("4").addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(@NonNull Void unused) {
                                                Toast.makeText(DestekActivity.this, "Desteğiniz için teşekkür ederiz!", Toast.LENGTH_SHORT).show();
                                            }
                                        });
                                    }
                                    else if(satinAlinanDeger.equals("destekci5")){
                                        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Hazirlayanlar");
                                        ref.child(fuser.getUid()).setValue("0"+AdSoyad+tiklansinMi);
                                        asilSnapShot.child("tas_arayuz_sahibim").getRef().setValue(asilSnapShot.child("tas_arayuz_sahibim").getValue(String.class)+",o5");
                                        asilSnapShot.child("tas_mesaj_sahibim").getRef().setValue(asilSnapShot.child("tas_mesaj_sahibim").getValue(String.class)+",o5");
                                        asilSnapShot.child("tas_profil_sahibim").getRef().setValue(asilSnapShot.child("tas_profil_sahibim").getValue(String.class)+",o5");
                                        if(asilSnapShot.hasChild("rozetlerim"))
                                            asilSnapShot.child("rozetlerim").getRef().setValue(asilSnapShot.child("rozetlerim").getValue(String.class) + ",5");
                                        else
                                            asilSnapShot.child("rozetlerim").getRef().setValue(",5");

                                        asilSnapShot.child("rozet").getRef().setValue("5").addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(@NonNull Void unused) {
                                                Toast.makeText(DestekActivity.this, "Desteğiniz için teşekkür ederiz!", Toast.LENGTH_SHORT).show();
                                            }
                                        });
                                    }
                                    else if(satinAlinanDeger.equals("destekci6")){
                                        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Hazirlayanlar");
                                        ref.child(fuser.getUid()).setValue("0"+AdSoyad+tiklansinMi);
                                        asilSnapShot.child("tas_arayuz_sahibim").getRef().setValue(asilSnapShot.child("tas_arayuz_sahibim").getValue(String.class)+",o6");
                                        asilSnapShot.child("tas_mesaj_sahibim").getRef().setValue(asilSnapShot.child("tas_mesaj_sahibim").getValue(String.class)+",o6");
                                        asilSnapShot.child("tas_profil_sahibim").getRef().setValue(asilSnapShot.child("tas_profil_sahibim").getValue(String.class)+",o6");
                                        if(asilSnapShot.hasChild("rozetlerim"))
                                            asilSnapShot.child("rozetlerim").getRef().setValue(asilSnapShot.child("rozetlerim").getValue(String.class) + ",6");
                                        else
                                            asilSnapShot.child("rozetlerim").getRef().setValue(",6");

                                        asilSnapShot.child("rozet").getRef().setValue("6").addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(@NonNull Void unused) {
                                                Toast.makeText(DestekActivity.this, "Desteğiniz için teşekkür ederiz!", Toast.LENGTH_SHORT).show();
                                            }
                                        });
                                    }
                                    AciklamalariDuzenle();
                                }
                                else if(billingResult.getResponseCode() == BillingClient.BillingResponseCode.USER_CANCELED){
                                    Toast.makeText(DestekActivity.this, "İşlem başarısız!", Toast.LENGTH_SHORT).show();
                                }
                                else{
                                    Toast.makeText(DestekActivity.this, "Bir hata oluştu!", Toast.LENGTH_SHORT).show();
                                }
                            }
                        }
                    }
                }).build();
        odemeSistemineBaglan();
    }

    private void GelirBilgisiEkle(String uid, String id, int sad, int kacKP){
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("usersF").child(uid);
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    int deger = 0;
                    int kpSayisi = 0;
                    if (dataSnapshot.hasChild("gelirlerim")) {
                        for (DataSnapshot ds : dataSnapshot.child("gelirlerim").getChildren()) {
                            deger++;
                            if (ds.getKey().substring(0, id.length()).equals(id)) {
                                kpSayisi++;
                            }
                            if (deger == dataSnapshot.child("gelirlerim").getChildrenCount()) {
                                ref.child("gelirlerim").child(id + sad + "_" + kpSayisi).child("onceki_kp").setValue(dataSnapshot.child("kp").getValue(Integer.class));
                                ref.child("gelirlerim").child(id + sad + "_" + kpSayisi).child("verilen_kp").setValue(kacKP);
                                ref.child("gelirlerim").child(id + sad + "_" + kpSayisi).child("zaman").setValue(ServerValue.TIMESTAMP).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(@NonNull Void unused) {
                                        dataSnapshot.child("kp").getRef().setValue(dataSnapshot.child("kp").getValue(Integer.class) + kacKP).addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(@NonNull Void unused) {
                                                Toast.makeText(DestekActivity.this, "Tebrikler " + kacKP + "KP kazandınız!", Toast.LENGTH_SHORT).show();
                                            }
                                        });
                                    }
                                });
                            }
                        }
                    }
                    else{
                        ref.child("gelirlerim").child(id+sad+"_0").child("onceki_kp").setValue(dataSnapshot.child("kp").getValue(Integer.class));
                        ref.child("gelirlerim").child(id +sad+"_0").child("verilen_kp").setValue(kacKP);
                        ref.child("gelirlerim").child(id+sad+"_0").child("zaman").setValue(ServerValue.TIMESTAMP).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(@NonNull Void unused) {
                                dataSnapshot.child("kp").getRef().setValue(dataSnapshot.child("kp").getValue(Integer.class) + kacKP).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(@NonNull Void unused) {
                                        Toast.makeText(DestekActivity.this, "Tebrikler " + kacKP + "KP kazandınız!", Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                        });
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        billingClient.endConnection();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        billingClient.endConnection();
    }

    public void adSoyadDialog(SkuDetails sku){
        Dialog dialog = new Dialog(DestekActivity.this);
        dialog.setContentView(R.layout.dialog_dizayn3);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        LinearLayout lay1 = dialog.findViewById(R.id.lay1);
        LinearLayout lay2 = dialog.findViewById(R.id.lay2);
        ProgressBar pbar = dialog.findViewById(R.id.pbar);
        pbar.getIndeterminateDrawable().setColorFilter(ortaRenk,android.graphics.PorterDuff.Mode.MULTIPLY);
        TextView baslik = dialog.findViewById(R.id.baslik);
        TextView aciklama = dialog.findViewById(R.id.aciklama);
        aciklama.setMovementMethod(new ScrollingMovementMethod());
        Button buton = dialog.findViewById(R.id.buton);
        EditText adSoyad = dialog.findViewById(R.id.editText);
        adSoyad.setHint("Ad - Soyad");
        baslik.setText("TEŞEKKÜR");
        aciklama.setText("Size teşekkür etmek için ayarlar sayfasında bulunan teşekkürler kısmında isminize yer vermek istiyoruz. Lütfen ad ve soyad bilginizi giriniz, ödeme yaptıktan sonra 1 gün içinde teşekkürler kısmını kontrol edebilirsiniz. Ayrıca kullanıcı adınız da teşekkür kısmında görünecektir, isterseniz profil kısmından kullanıcı adınızı değiştirebilirsiniz.");
        buton.setText("TAMAM");
        buton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                lay1.setVisibility(View.GONE);
                lay2.setVisibility(View.GONE);
                pbar.setVisibility(View.VISIBLE);
                if(!adSoyad.getText().toString().trim().equals("")){
                    dialog.dismiss();
                    Dialog dialog2 = new Dialog(DestekActivity.this);
                    dialog2.setContentView(R.layout.dialog_dizayn5);
                    dialog2.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

                    LinearLayout lay1 = dialog2.findViewById(R.id.lay1);
                    LinearLayout lay2 = dialog2.findViewById(R.id.lay2);
                    ProgressBar pbar = dialog2.findViewById(R.id.pbar);
                    pbar.getIndeterminateDrawable().setColorFilter(ortaRenk,android.graphics.PorterDuff.Mode.MULTIPLY);
                    TextView baslik = dialog2.findViewById(R.id.baslik);
                    TextView aciklama = dialog2.findViewById(R.id.aciklama);
                    aciklama.setMovementMethod(new ScrollingMovementMethod());
                    Button buton = dialog2.findViewById(R.id.buton);
                    Button buton2 = dialog2.findViewById(R.id.buton2);
                    baslik.setText("TEŞEKKÜR");
                    aciklama.setText("Teşekkürler kısmındaki isminize tıklandığında Kimoo profil sayfanıza yönlendirilmesini istiyor musunuz?");
                    buton.setText("HAYIR");
                    buton2.setText("EVET");
                    buton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            tiklansinMi = 1;
                            AdSoyad = adSoyad.getText().toString().replaceAll(" ",".B.");
                            DialogUyari(sku);
                            dialog2.dismiss();
                        }
                    });
                    buton2.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            tiklansinMi = 0;
                            AdSoyad = adSoyad.getText().toString().replaceAll(" ",".B.");
                            DialogUyari(sku);
                            dialog2.dismiss();
                        }
                    });
                    dialog2.show();
                }
                else{
                    lay1.setVisibility(View.VISIBLE);
                    lay2.setVisibility(View.VISIBLE);
                    pbar.setVisibility(View.GONE);
                    Toast.makeText(DestekActivity.this, "Hiçbir şey yazmadınız.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Hazirlayanlar");
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    if(dataSnapshot.hasChild(fuser.getUid())) {
                        Toast.makeText(DestekActivity.this, "Zaten teşekkür listesinde varsınız yine de tekrar destek olabilirsiniz. Eğer listedeki adınızı sonradan değiştirmek isterseniz listede isminize tıklayabilirsiniz.", Toast.LENGTH_LONG).show();
                        DialogUyari(sku);
                    }
                    else
                        dialog.show();
                }
                else
                    dialog.show();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
    private void AciklamalariDuzenle() {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("usersF").child(fuser.getUid());
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                asilSnapShot = dataSnapshot;
                Kullanici = dataSnapshot.getValue(User.class);
                if(asilSnapShot.hasChild("rozetlerim")) {
                    if (asilSnapShot.child("rozetlerim").getValue(String.class).contains(",1"))
                        destek1_aciklama.setText("- 40.000KP");
                    if (asilSnapShot.child("rozetlerim").getValue(String.class).contains(",2"))
                        destek2_aciklama.setText("- 100.000KP");
                    if (asilSnapShot.child("rozetlerim").getValue(String.class).contains(",3"))
                        destek3_aciklama.setText("- 225.000KP");
                    if (asilSnapShot.child("rozetlerim").getValue(String.class).contains(",4"))
                        destek4_aciklama.setText("- 500.000KP");
                    if (asilSnapShot.child("rozetlerim").getValue(String.class).contains(",5"))
                        destek5_aciklama.setText("- 1.000.000KP");
                    if (asilSnapShot.child("rozetlerim").getValue(String.class).contains(",6"))
                        destek6_aciklama.setText("- 3.000.000KP");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void odemeSistemineBaglan() {
        billingClient.startConnection(new BillingClientStateListener() {
            @Override
            public void onBillingSetupFinished(BillingResult billingResult) {
                if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK) {
                    itemleriGetir("destekci1");
                    itemleriGetir("destekci2");
                    itemleriGetir("destekci3");
                    itemleriGetir("destekci4");
                    itemleriGetir("destekci5");
                    itemleriGetir("destekci6");
                }
            }

            @Override
            public void onBillingServiceDisconnected() {
                odemeSistemineBaglan();
            }
        });
    }

    private void SatisiDogrula(Purchase purchase){
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Siparisler");
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChild(fuser.getUid())) {
                    DatabaseReference sonRef = dataSnapshot.child(fuser.getUid()).getRef().child("" + (dataSnapshot.child(fuser.getUid()).getChildrenCount() + 1));
                    sonRef.child("token").setValue(purchase.getPurchaseToken());
                    sonRef.child("zaman").setValue(purchase.getPurchaseTime());
                    sonRef.child("onceki_kp").setValue(asilSnapShot.child("kp").getValue(Integer.class));
                    sonRef.child("siparisNo").setValue(purchase.getOrderId());
                    sonRef.child("item").setValue(satinAlinanDeger);
                } else {
                    DatabaseReference sonRef = dataSnapshot.child(fuser.getUid()).getRef().child("1");
                    sonRef.child("token").setValue(purchase.getPurchaseToken());
                    sonRef.child("zaman").setValue(purchase.getPurchaseTime());
                    sonRef.child("onceki_kp").setValue(asilSnapShot.child("kp").getValue(Integer.class));
                    sonRef.child("siparisNo").setValue(purchase.getOrderId());
                    sonRef.child("item").setValue(satinAlinanDeger);
                }
                ConsumeParams consumeParams = ConsumeParams.newBuilder().setPurchaseToken(purchase.getPurchaseToken()).build();
                billingClient.consumeAsync(consumeParams, new ConsumeResponseListener() {
                    @Override
                    public void onConsumeResponse(@NonNull BillingResult billingResult, @NonNull String s) {
                        if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK) {

                        }
                    }
                });
                if (satinAlinanDeger.equals("destekci1_kp")) {
                    GelirBilgisiEkle(fuser.getUid(),"kp_",40000,40000);
                }
                else if (satinAlinanDeger.equals("destekci2_kp")) {
                    GelirBilgisiEkle(fuser.getUid(),"kp_",100000,100000);
                }
                else if (satinAlinanDeger.equals("destekci3_kp")) {
                    GelirBilgisiEkle(fuser.getUid(),"kp_",225000,225000);
                }
                else if (satinAlinanDeger.equals("destekci4_kp")) {
                    GelirBilgisiEkle(fuser.getUid(),"kp_",500000,500000);
                }
                else if (satinAlinanDeger.equals("destekci5_kp")) {
                    GelirBilgisiEkle(fuser.getUid(),"kp_",1000000,1000000);
                }
                else if (satinAlinanDeger.equals("destekci6_kp")) {
                    GelirBilgisiEkle(fuser.getUid(),"kp_",3000000,3000000);
                }
                snapshotGuncelle();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
    private void snapshotGuncelle(){
        reference = FirebaseDatabase.getInstance().getReference("usersF").child(fuser.getUid());
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                asilSnapShot = dataSnapshot;
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
    private void itemleriGetir(String id) {
        List<String> skuList = new ArrayList<>();
        skuList.add(id);

        SkuDetailsParams params = SkuDetailsParams
                .newBuilder()
                .setSkusList(skuList)
                .setType(BillingClient.SkuType.INAPP)
                .build();

        billingClient.querySkuDetailsAsync(params, new SkuDetailsResponseListener() {
            @Override
            public void onSkuDetailsResponse(@NonNull BillingResult billingResult, @Nullable List<SkuDetails> list) {
                if(billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK && list != null){
                    SkuDetails sku = list.get(0);

                    if(sku.getSku().equals("destekci1")){
                        buton1.setText(sku.getPrice());
                        buton1.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (!butonTiklandiMi) {
                                    butonTiklandiMi = true;
                                    if (asilSnapShot.hasChild("rozetlerim")) {
                                        if (asilSnapShot.child("rozetlerim").getValue(String.class).contains(",1"))
                                            satinAlinanDeger = "destekci1_kp";
                                        else
                                            satinAlinanDeger = "destekci1";
                                    } else
                                        satinAlinanDeger = "destekci1";


                                    DialogUyari(sku);
                                }
                            }
                        });
                    }
                    if(sku.getSku().equals("destekci2")){
                        buton2.setText(sku.getPrice());
                        buton2.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (!butonTiklandiMi) {
                                    butonTiklandiMi = true;
                                    if (asilSnapShot.hasChild("rozetlerim")) {
                                        if (asilSnapShot.child("rozetlerim").getValue(String.class).contains(",2"))
                                            satinAlinanDeger = "destekci2_kp";
                                        else
                                            satinAlinanDeger = "destekci2";
                                    } else
                                        satinAlinanDeger = "destekci2";
                                    DialogUyari(sku);
                                }
                            }
                        });
                    }
                    if(sku.getSku().equals("destekci3")){
                        buton3.setText(sku.getPrice());
                        buton3.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (!butonTiklandiMi) {
                                    butonTiklandiMi = true;
                                    if (asilSnapShot.hasChild("rozetlerim")) {
                                        if (asilSnapShot.child("rozetlerim").getValue(String.class).contains(",3"))
                                            satinAlinanDeger = "destekci3_kp";
                                        else
                                            satinAlinanDeger = "destekci3";
                                    } else
                                        satinAlinanDeger = "destekci3";
                                    adSoyadDialog(sku);
                                }
                            }
                        });
                    }
                    if(sku.getSku().equals("destekci4")){
                        buton4.setText(sku.getPrice());
                        buton4.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (!butonTiklandiMi) {
                                    butonTiklandiMi = true;
                                    if (asilSnapShot.hasChild("rozetlerim")) {
                                        if (asilSnapShot.child("rozetlerim").getValue(String.class).contains(",4"))
                                            satinAlinanDeger = "destekci4_kp";
                                        else
                                            satinAlinanDeger = "destekci4";
                                    } else
                                        satinAlinanDeger = "destekci4";
                                    adSoyadDialog(sku);
                                }
                            }
                        });
                    }
                    if(sku.getSku().equals("destekci5")){
                        buton5.setText(sku.getPrice());
                        buton5.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (!butonTiklandiMi) {
                                    butonTiklandiMi = true;
                                    if (asilSnapShot.hasChild("rozetlerim")) {
                                        if (asilSnapShot.child("rozetlerim").getValue(String.class).contains(",5"))
                                            satinAlinanDeger = "destekci5_kp";
                                        else
                                            satinAlinanDeger = "destekci5";
                                    } else
                                        satinAlinanDeger = "destekci5";
                                    adSoyadDialog(sku);
                                }
                            }
                        });
                    }
                    if(sku.getSku().equals("destekci6")){
                        buton6.setText(sku.getPrice());
                        buton6.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (!butonTiklandiMi) {
                                    butonTiklandiMi = true;
                                    if (asilSnapShot.hasChild("rozetlerim")) {
                                        if (asilSnapShot.child("rozetlerim").getValue(String.class).contains(",6"))
                                            satinAlinanDeger = "destekci6_kp";
                                        else
                                            satinAlinanDeger = "destekci6";
                                    } else
                                        satinAlinanDeger = "destekci6";
                                    adSoyadDialog(sku);
                                }
                            }
                        });
                    }

                }
                else{
                    Toast.makeText(DestekActivity.this, "Bir problem oluştu", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    private void DialogUyari(SkuDetails sku) {
        Dialog dialog = new Dialog(DestekActivity.this);
        dialog.setContentView(R.layout.dialog_dizayn2);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        TextView baslik = dialog.findViewById(R.id.baslik);
        TextView aciklama = dialog.findViewById(R.id.aciklama);

        LinearLayout lay1 = dialog.findViewById(R.id.lay1);
        ProgressBar pbar = dialog.findViewById(R.id.pbar);
        pbar.getIndeterminateDrawable().setColorFilter(ortaRenk,android.graphics.PorterDuff.Mode.MULTIPLY);

        aciklama.setMovementMethod(new ScrollingMovementMethod());
        Button buton = dialog.findViewById(R.id.buton);
        baslik.setText("Satın Almadan Önce");
        aciklama.setText("Ücretli bir içerik almak üzeresiniz. Bu içeriği satın almadan önce lütfen kullanım koşullarımızı tekrar okuyunuz. Kurallarımıza uymadığınız takdirde aldığınız ücretli içeriğe rağmen hesabınızın kapatılabileceğini veya farklı yaptırımlara maruz kalabileceğinizi unutmayın. Bu koşullarda hiçbir geri ödeme talep edemezsiniz. Bu ürünü daha sonra google play üzerinden iade etmek isterseniz aldığınız içeriği kullanmış olsanız bile hesabınızdan geri alınacağını unutmayın. (Kullanım koşullarını okumak için bu metne tıklayınız)");
        aciklama.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (kosulYazisi != null){
                    Uri uri = Uri.parse(kosulYazisi);
                    Toast.makeText(DestekActivity.this, "Bağlantı Açılıyor...", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                    startActivity(intent);
                }
                else
                    Toast.makeText(DestekActivity.this, "İnternet bağlantınız yavaş lütfen tekrar deneyin.", Toast.LENGTH_SHORT).show();
            }
        });
        buton.setText("Devam");
        buton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!butonTiklandiMi) {
                    butonTiklandiMi = true;
                    billingClient.launchBillingFlow(DestekActivity.this, BillingFlowParams.newBuilder().setSkuDetails(sku).build());
                    dialog.dismiss();
                }
            }
        });
        dialog.show();
        butonTiklandiMi = false;
    }
    private void startScaleAnimation(final View view) {
        view.setScaleX(1f);
        view.setScaleY(1f);
        ObjectAnimator scaleDownX = ObjectAnimator.ofFloat(view, "scaleX", 0.9f);
        ObjectAnimator scaleDownY = ObjectAnimator.ofFloat(view, "scaleY", 0.9f);
        scaleDownX.setDuration(50);
        scaleDownY.setDuration(50);
        scaleDownX.start();
        scaleDownY.start();
    }
    private void cancelScaleAnimation(final View view, final int i) {
        ObjectAnimator scaleDownX2 = ObjectAnimator.ofFloat(view, "scaleX", 0.9f);
        final ObjectAnimator scaleDownY2 = ObjectAnimator.ofFloat(view, "scaleY", 0.9f);
        scaleDownX2.setDuration(100);
        scaleDownY2.setDuration(100);
        scaleDownX2.start();
        scaleDownY2.start();
        scaleDownY2.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                ObjectAnimator scaleDownX = ObjectAnimator.ofFloat(view, "scaleX", 1f);
                final ObjectAnimator scaleDownY = ObjectAnimator.ofFloat(view, "scaleY", 1f);
                scaleDownX.setDuration(300);
                scaleDownY.setDuration(300);
                scaleDownX.start();
                scaleDownY.start();
                scaleDownX.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                        scaleDownY.addListener(new AnimatorListenerAdapter() {
                            @Override
                            public void onAnimationEnd(Animator animation) {
                                super.onAnimationEnd(animation);
                                if (!butonTiklandiMi) {
                                    butonTiklandiMi = true;
                                    if (i == 7) {
                                        if (sistemSnapShot != null) {
                                            if (!sistemSnapShot.child("ins_adres").getValue(String.class).trim().equals("")) {
                                                Uri uri = Uri.parse(sistemSnapShot.child("ins_adres").getValue(String.class));
                                                Toast.makeText(DestekActivity.this, "Bağlantı Açılıyor...", Toast.LENGTH_SHORT).show();
                                                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                                                startActivity(intent);
                                            } else {
                                                Toast.makeText(DestekActivity.this, "Yakında...", Toast.LENGTH_SHORT).show();
                                            }
                                            butonTiklandiMi = false;
                                        }
                                    }
                                    else if (i == 8) {
                                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                            if (ActivityCompat.checkSelfPermission(DestekActivity.this, Manifest.permission.READ_MEDIA_IMAGES) == PackageManager.PERMISSION_GRANTED) {
                                                if (Kullanici != null) {
                                                    File file = saveBitMap(DestekActivity.this, ss);
                                                    if (file != null)
                                                        shareStory(file);

                                                }
                                                butonTiklandiMi = false;
                                            }
                                            else {
                                                //requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},99);
                                                ActivityCompat.requestPermissions(DestekActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_MEDIA_IMAGES}, 99);
                                                butonTiklandiMi = false;
                                                Toast.makeText(DestekActivity.this, "Izin vermelisin: " + (ActivityCompat.checkSelfPermission(DestekActivity.this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) + "  " +
                                                        (ActivityCompat.checkSelfPermission(DestekActivity.this, Manifest.permission.READ_MEDIA_IMAGES) == PackageManager.PERMISSION_GRANTED) , Toast.LENGTH_LONG).show();
                                            }
                                        }
                                        else
                                            butonTiklandiMi = false;
                                    }
                                    else if (i == 9) {
                                        Intent whatsappIntent = new Intent(Intent.ACTION_SEND);
                                        whatsappIntent.setType("text/plain");
                                        whatsappIntent.setPackage("com.whatsapp");
                                        whatsappIntent.putExtra(Intent.EXTRA_TEXT, "Merhaba dostum! Sokakta, AVM'de, kafede ya da dışarıda her hangi bir yerde gezerken karşımdaki kişilerin kim olduğunu görebildiğim bu uygulamayı keşfettim ve üye oldum! Sen de Kimoo'yu kullanmak istersen, hemen indirebilirsin : play.google.com/store/apps/details?id=com.kimoo.android İyi günler :)");
                                        try {
                                            startActivity(whatsappIntent);
                                        } catch (ActivityNotFoundException ex) {
                                            Toast.makeText(DestekActivity.this, "Whatsapp yüklü değil!", Toast.LENGTH_SHORT).show();
                                        }
                                        butonTiklandiMi = false;
                                    }
                                    else if (i == 10) {
                                        if (sistemSnapShot != null) {
                                            if (!sistemSnapShot.child("yt_adres").getValue(String.class).trim().equals("")) {
                                                Uri uri = Uri.parse(sistemSnapShot.child("yt_adres").getValue(String.class));
                                                Toast.makeText(DestekActivity.this, "Bağlantı Açılıyor...", Toast.LENGTH_SHORT).show();
                                                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                                                startActivity(intent);
                                            } else {
                                                Toast.makeText(DestekActivity.this, "Yakında...", Toast.LENGTH_SHORT).show();
                                            }
                                            butonTiklandiMi = false;
                                        }
                                    }
                                    else if (i == 11) {
                                        if (sistemSnapShot != null) {
                                            if (!sistemSnapShot.child("web_adres").getValue(String.class).trim().equals("")) {
                                                Uri uri = Uri.parse(sistemSnapShot.child("web_adres").getValue(String.class));
                                                Toast.makeText(DestekActivity.this, "Bağlantı Açılıyor...", Toast.LENGTH_SHORT).show();
                                                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                                                startActivity(intent);
                                            } else {
                                                Toast.makeText(DestekActivity.this, "Yakında...", Toast.LENGTH_SHORT).show();
                                            }
                                            butonTiklandiMi = false;
                                        }
                                    }
                                    else if (i == 12) {
                                        Uri uri = Uri.parse("https://play.google.com/store/apps/details?id=com.kimoo.android");
                                        Toast.makeText(DestekActivity.this, "Bağlantı Açılıyor...", Toast.LENGTH_SHORT).show();
                                        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                                        startActivity(intent);
                                        butonTiklandiMi = false;
                                    }
                                }
                            }
                        });
                    }
                });
            }
        });

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
            if(requestCode == 99){
                File file = saveBitMap(DestekActivity.this, ss);
                if (file != null)
                    shareStory(file);
                butonTiklandiMi = false;
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        SharedPreferences tas_shared = getSharedPreferences("TasarimBilgileri",MODE_PRIVATE);
        TasarimDegistir(tas_shared.getString("tasarim_arayuz","1"));

        billingClient.queryPurchasesAsync(BillingClient.SkuType.INAPP, new PurchasesResponseListener() {
            @Override
            public void onQueryPurchasesResponse(@NonNull BillingResult billingResult, @NonNull List<Purchase> list) {
                if(billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK){
                    for (Purchase purchase : list){
                        if(purchase.getPurchaseState() == Purchase.PurchaseState.PURCHASED && !purchase.isAcknowledged()){
                            //SatisiDogrula(purchase);
                        }
                    }
                }
            }
        });
    }
    public void TasarimDegistir(String tasDegeri) {
        GradientDrawable gradient = (GradientDrawable) getResources().getDrawable(R.drawable.gradient);
        GradientDrawable gradient2 = new GradientDrawable();
        GradientDrawable gradient3 = new GradientDrawable();
        GradientDrawable gradientYumusak = new GradientDrawable();
        GradientDrawable gradientYumusak2 = new GradientDrawable();

        gradient.setOrientation(GradientDrawable.Orientation.LEFT_RIGHT);
        gradient2.setOrientation(GradientDrawable.Orientation.LEFT_RIGHT);
        gradient3.setOrientation(GradientDrawable.Orientation.LEFT_RIGHT);
        gradientYumusak.setOrientation(GradientDrawable.Orientation.LEFT_RIGHT);
        gradientYumusak.setCornerRadius(50);
        gradientYumusak2.setOrientation(GradientDrawable.Orientation.LEFT_RIGHT);
        gradientYumusak2.setCornerRadius(50);

        int renk1 = 0,renk2 = 0,t1start = 0,t2start = 0,t1end = 0,t2end = 0,orta = 0;

        renk1 = TasarimRenginiGetir.RengiGetir(DestekActivity.this,"renk1",tasDegeri);
        renk2 = TasarimRenginiGetir.RengiGetir(DestekActivity.this,"renk2",tasDegeri);
        t1start = TasarimRenginiGetir.RengiGetir(DestekActivity.this,"t1start",tasDegeri);
        t2start = TasarimRenginiGetir.RengiGetir(DestekActivity.this,"t2start",tasDegeri);
        t1end = TasarimRenginiGetir.RengiGetir(DestekActivity.this,"t1end",tasDegeri);
        t2end = TasarimRenginiGetir.RengiGetir(DestekActivity.this,"t2end",tasDegeri);
        orta = TasarimRenginiGetir.RengiGetir(DestekActivity.this,"orta",tasDegeri);

        gradient.setColors(new int[]{
                renk1,
                orta,
                renk2,
        });
        gradient2.setColors(new int[]{
                renk1,
                orta,
                renk2,
        });
        gradient3.setColors(new int[]{
                renk1,
                orta,
                renk2,
        });


        gradientYumusak.setColors(new int[]{
                t1end,
                orta,
                t2start,
        });
        gradientYumusak2.setColors(new int[]{
                t1end,
                orta,
                t2start,
        });
        ortaRenk = orta;

        buton_basligi_1.setTextColor(orta);
        buton_basligi_2.setTextColor(orta);
        buton_basligi_3.setTextColor(orta);
        buton_basligi_4.setTextColor(orta);
        buton_basligi_5.setTextColor(orta);
        buton_basligi_6.setTextColor(orta);

        buton1.setBackground(gradientYumusak);
        buton2.setBackground(gradientYumusak);
        buton3.setBackground(gradientYumusak);
        buton4.setBackground(gradientYumusak);
        buton5.setBackground(gradientYumusak);
        buton6.setBackground(gradientYumusak);

        gpOy.setBackground(gradientYumusak2);
        insTakip.setBackground(gradientYumusak2);
        insPaylas.setBackground(gradientYumusak2);
        wpPaylas.setBackground(gradientYumusak2);
        ytTakip.setBackground(gradientYumusak2);
        webZiyaret.setBackground(gradientYumusak2);

        gp_ic.setColorFilter(orta);
        ins_ic.setColorFilter(orta);
        ins_ic2.setColorFilter(orta);
        wp_ic.setColorFilter(orta);
        yt_ic.setColorFilter(orta);
        web_ic.setColorFilter(orta);

        ss.setBackground(gradient2);
        background.setBackground(gradient3);
        button_back.setBackground(gradient);
    }
    private void SolaMiSagaMi(final View view, final String deger, final int hiz, final Button buton, final String butonAdi) {
        ObjectAnimator scaleDownX = null;
        if(deger.equals("sola"))
            scaleDownX = ObjectAnimator.ofFloat(view, "translationX", view.getTranslationX() - (view.getWidth() * carpanDegeri));
        else
            scaleDownX = ObjectAnimator.ofFloat(view, "translationX", view.getTranslationX() + (view.getWidth() * carpanDegeri));
        scaleDownX.setDuration(hiz);
        scaleDownX.start();
        buton.setText(butonAdi);
        scaleDownX.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                view.setVisibility(View.VISIBLE);
                buton.setClickable(true);
                //barAnim(view);
                //Toast.makeText(DestekActivity.this, "geldi", Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void barAnim(final View view) {
        ObjectAnimator scaleDownX = ObjectAnimator.ofFloat(view, "translationX", view.getTranslationX() + view.getWidth());
        scaleDownX.setDuration(500);
        scaleDownX.start();
        scaleDownX.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                maneviDestek.setText("Geri Gel");
                maneviDestek.setClickable(true);
                //Toast.makeText(DestekActivity.this, "geldi", Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void barAnimNormal(final View view) {
        ObjectAnimator scaleDownX = ObjectAnimator.ofFloat(view, "translationX", view.getTranslationX() - view.getWidth());
        scaleDownX.setDuration(500);
        scaleDownX.start();
        scaleDownX.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);

            }
        });
    }




    private void shareStory(File file){
        Uri backgroundAssetUri = FileProvider.getUriForFile(this, "com.kimoo.android.provider", file);
        Toast.makeText(this, "Hikaye hazırlanıyor...", Toast.LENGTH_SHORT).show();
        Intent storiesIntent = new Intent("com.instagram.share.ADD_TO_STORY");
        storiesIntent.setDataAndType(backgroundAssetUri,  "image/*");
        storiesIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        storiesIntent.setPackage("com.instagram.android");
        this.grantUriPermission(
                "com.instagram.android", backgroundAssetUri, Intent.FLAG_GRANT_READ_URI_PERMISSION);
        butonTiklandiMi = false;
        this.startActivity(storiesIntent);
    }
    private File saveBitMap(Context context, View drawView){
        File pictureFileDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),"Kimoo");
        if (!pictureFileDir.exists()) {
            return null;
        }
        String filename = pictureFileDir.getPath() +File.separator+ "destekss"+".jpg";
        File pictureFile = new File(filename);
        Bitmap bitmap =getBitmapFromView(drawView);
        try {
            pictureFile.createNewFile();
            FileOutputStream oStream = new FileOutputStream(pictureFile);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, oStream);
            oStream.flush();
            oStream.close();
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(DestekActivity.this, "Bir hata oluştu.", Toast.LENGTH_SHORT).show();
        }
        scanGallery( context,pictureFile.getAbsolutePath());
        return pictureFile;
    }
    private Bitmap getBitmapFromView(View view) {
        //Define a bitmap with the same size as the view
        Bitmap returnedBitmap = Bitmap.createBitmap(view.getWidth(), view.getHeight(),Bitmap.Config.ARGB_8888);
        //Bind a canvas to it
        Canvas canvas = new Canvas(returnedBitmap);
        //Get the view's background
        Drawable bgDrawable =view.getBackground();
        if (bgDrawable!=null) {
            //has background drawable, then draw it on the canvas
            bgDrawable.draw(canvas);
        }   else{
            //does not have background drawable, then draw white background on the canvas
            canvas.drawColor(Color.WHITE);
        }
        // draw the view on the canvas
        view.draw(canvas);
        //return the bitmap
        return returnedBitmap;
    }
    private void scanGallery(Context cntx, String path) {
        try {
            MediaScannerConnection.scanFile(cntx, new String[] { path },null, new MediaScannerConnection.OnScanCompletedListener() {
                public void onScanCompleted(String path, Uri uri) {
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
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
}