package com.kimoo.android;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.VectorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.text.Text;
import com.google.mlkit.vision.text.TextRecognition;
import com.google.mlkit.vision.text.TextRecognizer;
import com.google.mlkit.vision.text.latin.TextRecognizerOptions;
import com.theartofdev.edmodo.cropper.CropImage;
import com.kimoo.android.Model.Fiyatlar;
import com.kimoo.android.Model.User;
import com.kimoo.android.extra.ResimIndir;
import com.kimoo.android.extra.TasarimRenginiGetir;
import com.kimoo.android.extra.TasarimSecenekleri;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfilActivity extends AppCompatActivity {
    private static GradientDrawable kapakEkleGradient;
    public static int ortaRenk;
    private boolean butonTiklandiMi = false;
    CardView kapak_sec_animasyon_icin;
    TextView foto_yok,ad,yas,kullanici_adi,begeni_sayisi,encokbegenenisim,encokbegenensayisi;
    public static EditText adTV;
    ImageView ayarlar, foto_1, hedefImage;
    ImageView foto_2;
    ImageView foto_3;
    ImageView foto_4;
    ImageView foto_5;
    ImageView foto_6;
    ImageView encokbegenenkalpicon;
    ImageView cinsiyet;
    ImageView cinsiyet2;
    ImageView kapak, rozet,rozet_view_arka;
    LinearLayout rozetLay, anaLay;
    ProgressBar pbar;
    static ImageView arti_ic;
    static ImageView kilit2;
    static ImageView kilit3;
    static ImageView kilit4;
    static ImageView kilit5;
    static ImageView kilit6;
    public static ImageView kapak_sec;
    public static Button profili_duzenle,profili_kaydet,begenenler,sehir_degis,diptal,usnameButton;
    RelativeLayout foto_layout;
    RelativeLayout edittext_layout;
    RelativeLayout encokbegenenkismi;
    RelativeLayout begenenlerRel;
    RelativeLayout foto1Arka;
    RelativeLayout foto2Arka;
    RelativeLayout foto3Arka;
    RelativeLayout foto4Arka;
    RelativeLayout foto5Arka;
    RelativeLayout foto6Arka;
    static RelativeLayout background;
    public static LinearLayout tas_profil;
    public static LinearLayout tas_arayuz;
    public static LinearLayout tas_mesaj;
    static LinearLayout background2;
    static View tas_profil_view;
    static View tas_arayuz_view;
    static View tas_mesaj_view;
    CircleImageView foto_pp,encokbegenenfoto,yas_arka,begeniBildirim;
    RelativeLayout alttakiLay;
    FirebaseUser fuser;
    User enCokBegenen;
    public static int foto2kilidi,foto3kilidi,foto4kilidi,foto5kilidi,foto6kilidi;
    public static User Kullanici;
    public static String TasarimDegeriProfil, TasarimDegeriArayuz, TasarimDegeriMesaj;
    public static String TasarimProfilSahipOlduklarim, TasarimArayuzSahipOlduklarim, TasarimMesajSahipOlduklarim;

    String dogum_gunu,deger,kapakUri;
    int begeniSayisi,anaDeger;
    long bulunanDeger;
    long childSayisi;
    boolean r1varmi,r2varmi,r3varmi,r4varmi,r5varmi,r6varmi,eklevesil,ppyuklendimi,kapakVarmi,kapakDegistirebilirMi;
    String uf1,uf2,uf3,uf4,uf5,uf6,ufkapak,ufpp;
    ArrayList<String> begeniler = new ArrayList<>(),fotolar = new ArrayList<>();
    Rect rect;
    public static Fiyatlar fiyatlar;
    private StorageReference mStorageRef;
    private int sehirNo;
    ArrayList<ImageView> fotoViewList;
    ArrayList<Boolean>rBoolList;
    private static Context mContext;
    private static Activity mActivity;
    private int degisiklikSayisi;
    private String yeniKullaniciAdi = "";
    private List<ImageView> ekleViewList;
    public static DataSnapshot AsilDataSnapShot;
    private String rozetlerim;
    private byte[] bytepp;
    private String yuklenecekResimUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = getApplicationContext();
        mActivity = ProfilActivity.this;
        setContentView(R.layout.activity_profil);
        StatuBarAyarla();

        Toolbar toolbar = findViewById(R.id.pprofiltoolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setElevation(0);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               // finish();
            }
        });
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Sistem").child("fiyatlar");
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                fiyatlar = dataSnapshot.getValue(Fiyatlar.class);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        begeniBildirim = findViewById(R.id.begeniBildirim);
        profili_duzenle = (Button) findViewById(R.id.profil_duzenle_btn);
        profili_kaydet = findViewById(R.id.profil_kaydet_btn);
        begenenler = findViewById(R.id.profil_begenenler_btn);
        diptal = findViewById(R.id.profil_diptal_btn);
        ayarlar = findViewById(R.id.ayarlar);
        sehir_degis = findViewById(R.id.sehir_degis);
        //sehir_degis.setTextColor(TaraActivity.ortaRenk);
        usnameButton = findViewById(R.id.profil_uname_degis_ET);

        /*sehir_degis.getBackground().setColorFilter(TaraActivity.ortaRenk,
                PorterDuff.Mode.SRC_ATOP);
        usnameButton.getBackground().setColorFilter(TaraActivity.ortaRenk,
                PorterDuff.Mode.SRC_ATOP);*/

        //usnameButton.setTextColor(TaraActivity.ortaRenk);
        //Relative Layoutlar
        begenenlerRel = findViewById(R.id.begenenler_rel);
        kapak = findViewById(R.id.kapak);
        edittext_layout = findViewById(R.id.editText_layout);
        foto_layout = findViewById(R.id.foto_layout);
        //EditTextler
        adTV = findViewById(R.id.profil_ad_degis_ET);
        cinsiyet = findViewById(R.id.cinsiyet_arka);;
        cinsiyet2 = findViewById(R.id.cinsiyet_arka2);
        yas_arka = findViewById(R.id.yas_arka);
        //encokbegenensayisi = findViewById(R.id.encokbegenisayisi);
        //encokbegenenkismi = findViewById(R.id.encokbegenenkismi);
        //encokbegenenkalpicon = findViewById(R.id.encokkalpicon);
        kapak_sec = findViewById(R.id.kapak_sec);
        foto_1 = findViewById(R.id.profil_foto_1);
        foto_2 = findViewById(R.id.profil_foto_2);
        foto_3 = findViewById(R.id.profil_foto_3);
        foto_4 = findViewById(R.id.profil_foto_4);
        foto_5 = findViewById(R.id.profil_foto_5);
        foto_6 = findViewById(R.id.profil_foto_6);
        rozet = findViewById(R.id.rozet_view);
        rozetLay = findViewById(R.id.tas_rozet);
        rozet_view_arka = findViewById(R.id.rozet_view_arka);
        anaLay = findViewById(R.id.anaLay);
        pbar = findViewById(R.id.pbar);

        arti_ic = findViewById(R.id.arti_ic_1);
        kilit2 = findViewById(R.id.kilit2);
        kilit3 = findViewById(R.id.kilit3);
        kilit4 = findViewById(R.id.kilit4);
        kilit5 = findViewById(R.id.kilit5);
        kilit6 = findViewById(R.id.kilit6);
        foto1Arka = findViewById(R.id.foto1Arka);
        foto2Arka = findViewById(R.id.foto2Arka);
        foto3Arka = findViewById(R.id.foto3Arka);
        foto4Arka = findViewById(R.id.foto4Arka);
        foto5Arka = findViewById(R.id.foto5Arka);
        foto6Arka = findViewById(R.id.foto6Arka);
        kullanici_adi = findViewById(R.id.profil_kullaniciadi);
        ad = findViewById(R.id.isim_soyisim);
        yas = findViewById(R.id.yas);
        foto_pp = findViewById(R.id.profil_pp);
        foto_yok = findViewById(R.id.phic_foto_yok);
        tas_arayuz = findViewById(R.id.tas_arayuz);
        tas_arayuz_view = findViewById(R.id.tas_arayuz_view);
        tas_profil = findViewById(R.id.tas_profil);
        tas_profil_view = findViewById(R.id.tas_profil_view);
        tas_mesaj = findViewById(R.id.tas_mesaj);
        tas_mesaj_view = findViewById(R.id.tas_mesaj_view);
        background = findViewById(R.id.background);
        background2 = findViewById(R.id.background2);

        kapak_sec_animasyon_icin = findViewById(R.id.kapak_sec_animasyon_icin);

        //alttakiLay = findViewById(R.id.prel3);
        //begeni_sayisi = findViewById(R.id.profil_begeni_sayisi);
        fuser = FirebaseAuth.getInstance().getCurrentUser();
        BildirimKontrol();

        /*ayarlar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ProfilActivity.this,AyarlarActivity.class);
                startActivity(intent);
            }
        });*/
        mStorageRef = FirebaseStorage.getInstance().getReference(fuser.getUid());
        /*encokbegenenkismi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(enCokBegenen != null) {
                    Intent intent = new Intent(ProfilActivity.this, DigerProfilActivity.class);
                    intent.putExtra("userid", enCokBegenen.getUid());
                    startActivity(intent);
                }
            }
        });*/

        SharedPreferences tas_shared = getSharedPreferences("TasarimBilgileri",MODE_PRIVATE);
        TasarimDegistir(tas_shared.getString("tasarim_arayuz","1"), 2,false);
        if (getIntent().getStringExtra("foto_yukle") != null)
        if (getIntent().getStringExtra("foto_yukle").equals("pp")) {
            deger = "pp";
            FotoEkle();
        }

        ayarlar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!butonTiklandiMi) {
                    butonTiklandiMi = true;
                    startActivity(new Intent(ProfilActivity.this, AyarlarActivity.class));
                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                }
            }
        });
        foto_1.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                v = (View) foto1Arka;
                if(event.getAction() == MotionEvent.ACTION_DOWN){
                    rect = new Rect(v.getLeft(), v.getTop(), v.getRight(), v.getBottom());
                    v.animate()
                            .scaleX(1-0.05f)
                            .scaleY(1-0.05f)
                            .setDuration(100)
                            .start();
                }
                if(event.getAction() == MotionEvent.ACTION_UP){
                    if(rect.contains((int)event.getX(), (int)event.getY())){
                        v.animate()
                                .scaleX(1)
                                .scaleY(1)
                                .setDuration(100)
                                .start();
                        if (internetVarmi()) {
                            if (!butonTiklandiMi) {
                                butonTiklandiMi = true;
                                if (r1varmi) {
                                    deger = "1";
                                    ResimeNeOlsun();
                                } else {
                                    deger = "1";
                                    ResimeEkle();
                                }
                            }
                        }else
                            Toast.makeText(ProfilActivity.this, "İnternet Bağlantınız Yok!", Toast.LENGTH_SHORT).show();
                    }
                }
                if(event.getAction() == MotionEvent.ACTION_MOVE){
                    if(!rect.contains((int)event.getX(), (int)event.getY())){
                        v.animate()
                                .scaleX(1)
                                .scaleY(1)
                                .setDuration(100)
                                .start();
                    }else if(event.getAction() == MotionEvent.ACTION_UP){
                        v.animate()
                                .scaleX(1)
                                .scaleY(1)
                                .setDuration(100)
                                .start();
                    }
                }
                return true;
            }
        });
        foto_2.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                v = foto2Arka;
                if(event.getAction() == MotionEvent.ACTION_DOWN){
                    rect = new Rect(v.getLeft(), v.getTop(), v.getRight(), v.getBottom());
                    v.animate()
                            .scaleX(1-0.05f)
                            .scaleY(1-0.05f)
                            .setDuration(100)
                            .start();
                }
                if(event.getAction() == MotionEvent.ACTION_UP){
                        v.animate()
                                .scaleX(1)
                                .scaleY(1)
                                .setDuration(100)
                                .start();
                        if (internetVarmi()) {
                            if (!butonTiklandiMi) {
                                butonTiklandiMi = true;
                                if (foto2kilidi == 1) {
                                    if (r2varmi) {
                                        deger = "2";
                                        ResimeNeOlsun();
                                    } else {
                                        deger = "2";
                                        eklevesil = false;
                                        FotoEkle();
                                    }
                                } else {
                                    KilitKaldirmakIcinDialogOlustur("2");
                                }
                            }
                        }else
                            Toast.makeText(ProfilActivity.this, "İnternet Bağlantınız Yok!", Toast.LENGTH_SHORT).show();
                }
                if(event.getAction() == MotionEvent.ACTION_MOVE){
                    if(!rect.contains((int)event.getX(), (int)event.getY())){
                        v.animate()
                                .scaleX(1)
                                .scaleY(1)
                                .setDuration(100)
                                .start();
                    }else if(event.getAction() == MotionEvent.ACTION_UP){
                        v.animate()
                                .scaleX(1)
                                .scaleY(1)
                                .setDuration(100)
                                .start();
                    }
                }
                return true;
            }
        });
        foto_3.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                v = foto3Arka;
                if(event.getAction() == MotionEvent.ACTION_DOWN){
                    rect = new Rect(v.getLeft(), v.getTop(), v.getRight(), v.getBottom());
                    v.animate()
                            .scaleX(1-0.05f)
                            .scaleY(1-0.05f)
                            .setDuration(100)
                            .start();
                }
                if(event.getAction() == MotionEvent.ACTION_UP){
                        v.animate()
                                .scaleX(1)
                                .scaleY(1)
                                .setDuration(100)
                                .start();
                        if (internetVarmi()) {
                            if (!butonTiklandiMi) {
                                butonTiklandiMi = true;
                                if (foto3kilidi == 1) {
                                    if (r3varmi) {
                                        deger = "3";
                                        ResimeNeOlsun();
                                    } else {
                                        deger = "3";
                                        eklevesil = false;
                                        FotoEkle();
                                    }
                                } else {
                                    KilitKaldirmakIcinDialogOlustur("3");
                                }
                            }
                        }else
                            Toast.makeText(ProfilActivity.this, "İnternet Bağlantınız Yok!", Toast.LENGTH_SHORT).show();
                }
                if(event.getAction() == MotionEvent.ACTION_MOVE){
                    if(!rect.contains((int)event.getX(), (int)event.getY())){
                        v.animate()
                                .scaleX(1)
                                .scaleY(1)
                                .setDuration(100)
                                .start();
                    }else if(event.getAction() == MotionEvent.ACTION_UP){
                        v.animate()
                                .scaleX(1)
                                .scaleY(1)
                                .setDuration(100)
                                .start();
                    }
                }
                return true;
            }
        });
        foto_4.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                v = foto4Arka;
                if(event.getAction() == MotionEvent.ACTION_DOWN){
                    rect = new Rect(v.getLeft(), v.getTop(), v.getRight(), v.getBottom());
                    v.animate()
                            .scaleX(1-0.05f)
                            .scaleY(1-0.05f)
                            .setDuration(100)
                            .start();
                }
                if(event.getAction() == MotionEvent.ACTION_UP){
                        v.animate()
                                .scaleX(1)
                                .scaleY(1)
                                .setDuration(100)
                                .start();
                        if (internetVarmi()) {
                            if (!butonTiklandiMi) {
                                butonTiklandiMi = true;
                                if (foto4kilidi == 1) {
                                    if (r4varmi) {
                                        deger = "4";
                                        ResimeNeOlsun();
                                    } else {
                                        deger = "4";
                                        eklevesil = false;
                                        FotoEkle();
                                    }
                                } else {
                                    KilitKaldirmakIcinDialogOlustur("4");
                                }
                            }
                        }else
                            Toast.makeText(ProfilActivity.this, "İnternet Bağlantınız Yok!", Toast.LENGTH_SHORT).show();
                }
                if(event.getAction() == MotionEvent.ACTION_MOVE){
                    if(!rect.contains((int)event.getX(), (int)event.getY())){
                        v.animate()
                                .scaleX(1)
                                .scaleY(1)
                                .setDuration(100)
                                .start();
                    }else if(event.getAction() == MotionEvent.ACTION_UP){
                        v.animate()
                                .scaleX(1)
                                .scaleY(1)
                                .setDuration(100)
                                .start();
                    }
                }
                return true;
            }
        });
        foto_5.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                v = foto5Arka;
                if(event.getAction() == MotionEvent.ACTION_DOWN){
                    rect = new Rect(v.getLeft(), v.getTop(), v.getRight(), v.getBottom());
                    v.animate()
                            .scaleX(1-0.05f)
                            .scaleY(1-0.05f)
                            .setDuration(100)
                            .start();
                }
                if(event.getAction() == MotionEvent.ACTION_UP){
                        v.animate()
                                .scaleX(1)
                                .scaleY(1)
                                .setDuration(100)
                                .start();
                        if (internetVarmi()) {
                            if (!butonTiklandiMi) {
                                butonTiklandiMi = true;
                                if (foto5kilidi == 1) {
                                    if (r5varmi) {
                                        deger = "5";
                                        ResimeNeOlsun();
                                    } else {
                                        deger = "5";
                                        eklevesil = false;
                                        FotoEkle();
                                    }
                                } else {
                                    KilitKaldirmakIcinDialogOlustur("5");
                                }
                            }
                        }else
                            Toast.makeText(ProfilActivity.this, "İnternet Bağlantınız Yok!", Toast.LENGTH_SHORT).show();
                }
                if(event.getAction() == MotionEvent.ACTION_MOVE){
                    if(!rect.contains((int)event.getX(), (int)event.getY())){
                        v.animate()
                                .scaleX(1)
                                .scaleY(1)
                                .setDuration(100)
                                .start();
                    }else if(event.getAction() == MotionEvent.ACTION_UP){
                        v.animate()
                                .scaleX(1)
                                .scaleY(1)
                                .setDuration(100)
                                .start();
                    }
                }
                return true;
            }
        });
        foto_6.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                v = foto6Arka;
                if(event.getAction() == MotionEvent.ACTION_DOWN){
                    rect = new Rect(v.getLeft(), v.getTop(), v.getRight(), v.getBottom());
                    v.animate()
                            .scaleX(1-0.05f)
                            .scaleY(1-0.05f)
                            .setDuration(100)
                            .start();
                }
                if(event.getAction() == MotionEvent.ACTION_UP){
                        v.animate()
                                .scaleX(1)
                                .scaleY(1)
                                .setDuration(100)
                                .start();
                        if (internetVarmi()) {
                            if (!butonTiklandiMi) {
                                butonTiklandiMi = true;
                                if (foto6kilidi == 1) {
                                    if (r6varmi) {
                                        deger = "6";
                                        ResimeNeOlsun();
                                    } else {
                                        deger = "6";
                                        eklevesil = false;
                                        FotoEkle();
                                    }
                                } else {
                                    KilitKaldirmakIcinDialogOlustur("6");
                                }
                            }
                        }else
                            Toast.makeText(ProfilActivity.this, "İnternet Bağlantınız Yok!", Toast.LENGTH_SHORT).show();
                }
                if(event.getAction() == MotionEvent.ACTION_MOVE){
                    if(!rect.contains((int)event.getX(), (int)event.getY())){
                        v.animate()
                                .scaleX(1)
                                .scaleY(1)
                                .setDuration(100)
                                .start();
                    }else if(event.getAction() == MotionEvent.ACTION_UP){
                        v.animate()
                                .scaleX(1)
                                .scaleY(1)
                                .setDuration(100)
                                .start();
                    }
                }
                return true;
            }
        });

        rozetLay.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(final View view, MotionEvent motionEvent) {
                int action = motionEvent.getAction();
                if (action == MotionEvent.ACTION_DOWN) {
                    rect = new Rect(view.getLeft(), view.getTop(), view.getRight(), view.getBottom());
                    startScaleAnimation(view);
                    return true;
                } else if (action == MotionEvent.ACTION_UP) {
                    if (!butonTiklandiMi) {
                        butonTiklandiMi = true;
                        cancelScaleAnimation(view, 3);
                    }
                    return true;
                }
                return false;
            }
        });

        tas_profil.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(final View view, MotionEvent motionEvent) {
                int action = motionEvent.getAction();
                if (action == MotionEvent.ACTION_DOWN) {
                    rect = new Rect(view.getLeft(), view.getTop(), view.getRight(), view.getBottom());
                    startScaleAnimation(view);
                    return true;
                } else if (action == MotionEvent.ACTION_UP) {
                    if (!butonTiklandiMi) {
                        butonTiklandiMi = true;
                        cancelScaleAnimation(view, 0);
                    }
                    return true;
                }
                return false;
            }
        });
        tas_mesaj.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(final View view, MotionEvent motionEvent) {
                int action = motionEvent.getAction();
                if (action == MotionEvent.ACTION_DOWN) {
                    rect = new Rect(view.getLeft(), view.getTop(), view.getRight(), view.getBottom());
                    startScaleAnimation(view);
                    return true;
                } else if (action == MotionEvent.ACTION_UP) {
                    if (!butonTiklandiMi) {
                        butonTiklandiMi = true;
                        cancelScaleAnimation(view, 1);
                    }
                    return true;
                }
                return false;
            }
        });
        tas_arayuz.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(final View view, MotionEvent motionEvent) {
                int action = motionEvent.getAction();
                if (action == MotionEvent.ACTION_DOWN) {
                    rect = new Rect(view.getLeft(), view.getTop(), view.getRight(), view.getBottom());
                    startScaleAnimation(view);
                    return true;
                } else if (action == MotionEvent.ACTION_UP) {
                    if (!butonTiklandiMi) {
                        butonTiklandiMi = true;
                        cancelScaleAnimation(view, 2);
                    }
                    return true;
                }
                return false;
            }
        });

        foto_pp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    if (internetVarmi()) {
                        if (!butonTiklandiMi) {
                            butonTiklandiMi = true;
                        deger = "pp";
                        ResimeNeOlsun();
                        }
                    } else {
                        Toast.makeText(ProfilActivity.this, "İnternet Bağlantınız Yok!", Toast.LENGTH_SHORT).show();

                    }
            }
        });
        begenenler.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    if (internetVarmi()) {
                        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("usersF").child(fuser.getUid());
                        ref.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                if (dataSnapshot.hasChild("begenenler")) {
                                    if (dataSnapshot.child("begenenler").getChildrenCount() > 0) {
                                        final int[] bansizSayi = {0};
                                        final int[] normalSayi = {0};
                                        for (DataSnapshot dss : dataSnapshot.child("begenenler").getChildren()) {
                                            DatabaseReference reference = FirebaseDatabase.getInstance().getReference("usersF").child(dss.getKey());
                                            reference.addListenerForSingleValueEvent(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(@NonNull DataSnapshot dataSnapshot2) {
                                                    if (dataSnapshot2.child("ban_durumu").child("durum").getValue().equals("yok")) {
                                                        bansizSayi[0]++;
                                                        normalSayi[0]++;
                                                    } else {
                                                        bansizSayi[0]--;
                                                        normalSayi[0]++;
                                                    }
                                                    if (normalSayi[0] == dataSnapshot.child("begenenler").getChildrenCount()) {
                                                        if ((bansizSayi[0] + dataSnapshot.child("begenenler").getChildrenCount()) != 0) {
                                                            Intent intent = new Intent(ProfilActivity.this, BegenenlerActivity.class);
                                                            intent.putExtra("userid", fuser.getUid());
                                                            startActivity(intent);
                                                            // finish();
                                                            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
                                                        } else {
                                                            Toast.makeText(ProfilActivity.this, "Henüz kimse sizi beğenmemiş :( ", Toast.LENGTH_SHORT).show();
                                                        }
                                                    }

                                                }

                                                @Override
                                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                                }
                                            });
                                        }
                                    }
                                } else {
                                    Toast.makeText(ProfilActivity.this, "Henüz kimse sizi beğenmemiş :( ", Toast.LENGTH_SHORT).show();
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
                    }
                    else {
                        Toast.makeText(ProfilActivity.this, "İnternet Bağlantınız Yok!", Toast.LENGTH_SHORT).show();
                        butonTiklandiMi = false;
                    }
            }
        });
        if(fuser != null)
            BilgileriIste();
        final boolean[] degistiMi = {false};
        adTV.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (editable.toString().trim().length() == 1) {
                    if(!degistiMi[0]) {
                        degisiklikSayisi++;
                        DegisiklikVarmi();
                        degistiMi[0] = true;
                    }
                } else if (editable.toString().trim().length() == 0) {
                    degistiMi[0] = false;
                    degisiklikSayisi--;
                    DegisiklikVarmi();
                }
            }
        });
        diptal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                kapakVarmi = false;
                adTV.setText("");
                sehir_degis.setText("Şehir");
                usnameButton.setText("Kullanıcı Adı: "+Kullanici.getUsernamef());
                begenenlerRel.setVisibility(View.VISIBLE);
                profili_kaydet.setVisibility(View.GONE);
                profili_kaydet.clearAnimation();
                profili_duzenle.setVisibility(View.VISIBLE);
                foto_layout.setVisibility(View.VISIBLE);
                profili_kaydet.clearAnimation();
                edittext_layout.setVisibility(View.GONE);
                diptal.setVisibility(View.GONE);
                /*Intent intent = new Intent(ProfilActivity.this,ProfilActivity.class);
                startActivity(intent);*/
            }
        });

        usnameButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (!butonTiklandiMi) {
                    butonTiklandiMi = true;
                    if (Kullanici.getOzel_kadi() == 0) {
                        Dialog dialog = new Dialog(mActivity);
                        dialog.setContentView(R.layout.dialog_dizayn2);
                        LinearLayout lay1 = dialog.findViewById(R.id.lay1);
                        ProgressBar pbar = dialog.findViewById(R.id.pbar);
                        pbar.getIndeterminateDrawable().setColorFilter(ortaRenk, android.graphics.PorterDuff.Mode.MULTIPLY);
                        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

                        TextView baslik = dialog.findViewById(R.id.baslik);
                        TextView aciklama = dialog.findViewById(R.id.aciklama);
                        Button buton = dialog.findViewById(R.id.buton);
                        if (Kullanici.getKp() >= fiyatlar.getKullanici_adi()) {
                            baslik.setText("Özelleştirilebilir Kullanıcı Adı");
                            aciklama.setText("Bu özelliği kullanabilmek için " + fiyatlar.getKullanici_adi() + " gerekmektedir. Sizde olan " + Kullanici.getKp() + "KP. Satın almak istiyor musunuz?");
                            buton.setText("Satın Al");
                            buton.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    lay1.setVisibility(View.GONE);
                                    pbar.setVisibility(View.VISIBLE);
                                    DatabaseReference ref = FirebaseDatabase.getInstance().getReference("usersF").child(Kullanici.getUid());
                                    ref.child("harcamalarim").child("kullanici adi").setValue(ServerValue.TIMESTAMP);
                                    ref.child("harcamalarim").child("kullanici adi").child("onceki_kp").setValue(Kullanici.getKp());
                                    ref.child("harcamalarim").child("kullanici adi").child("zaman").setValue(ServerValue.TIMESTAMP);
                                    ref.child("kp").setValue(Kullanici.getKp() - fiyatlar.getKullanici_adi()).addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(@NonNull Void unused) {
                                            ref.child("ozel_kadi").setValue(1).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(@NonNull Void unused) {
                                                    Toast.makeText(ProfilActivity.this, "Özelleştirilebilir Kullanıcı Adı aldınız!", Toast.LENGTH_SHORT).show();
                                                    Kullanici.setKp(Kullanici.getKp() - fiyatlar.getKullanici_adi());
                                                    Kullanici.setOzel_kadi(1);
                                                    butonTiklandiMi = false;
                                                    dialog.dismiss();
                                                }
                                            });
                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            lay1.setVisibility(View.VISIBLE);
                                            pbar.setVisibility(View.GONE);
                                            butonTiklandiMi = false;
                                            Toast.makeText(mActivity, "Başarısız!", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                }
                            });
                        }
                        else {
                            baslik.setText("Özelleştirilebilir Kullanıcı Adı");
                            aciklama.setText("Bu özelliği kullanabilmek için " + fiyatlar.getKullanici_adi() + " gerekmektedir. Sizde olan " + Kullanici.getKp() + "KP. Yetersiz KP!");
                            buton.setText("Markete Git");
                            buton.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    butonTiklandiMi = false;
                                    mActivity.startActivity(new Intent(mActivity, MarketActivity.class));
                                    dialog.dismiss();
                                }
                            });
                        }
                        dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                            @Override
                            public void onCancel(DialogInterface dialog) {
                                butonTiklandiMi = false;
                            }
                        });
                        dialog.show();
                    }
                    else {

                        Dialog dialog = new Dialog(mActivity);
                        dialog.setContentView(R.layout.dialog_dizayn3);
                        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

                        TextView baslik = dialog.findViewById(R.id.baslik);
                        TextView aciklama = dialog.findViewById(R.id.aciklama);
                        LinearLayout lay1 = dialog.findViewById(R.id.lay1);
                        ProgressBar pbar = dialog.findViewById(R.id.pbar);
                        pbar.getIndeterminateDrawable().setColorFilter(ortaRenk, android.graphics.PorterDuff.Mode.MULTIPLY);
                        Button buton = dialog.findViewById(R.id.buton);
                        EditText uname = dialog.findViewById(R.id.editText);
                        baslik.setText("Kullanıcı Adı Değiştir");
                        aciklama.setText("Kullanıcı adınızın kullanılabilir olduğunu anlamak için değişiklikleri kaydetmeniz gerekir");
                        buton.setText("Değiştir");
                        buton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                butonTiklandiMi = false;
                                if (uname.getText().toString().trim().length() > 5) {
                                    lay1.setVisibility(View.GONE);
                                    pbar.setVisibility(View.VISIBLE);
                                    usnameButton.setText("Kullanıcı Adı: " + uname.getText().toString().trim().toLowerCase());
                                    yeniKullaniciAdi = uname.getText().toString().trim().toLowerCase();
                                    degisiklikSayisi++;
                                    DegisiklikVarmi();
                                    dialog.dismiss();
                                } else {
                                    uname.setError("Kullanıcı adınız en az 6 karakterden oluşmalı");
                                }
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
                }
            }
        });
        profili_kaydet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(internetVarmi())
                    DegisiklikleriKontrolEt();
                else
                    Toast.makeText(ProfilActivity.this, "İnternet Bağlantınız Yok!", Toast.LENGTH_SHORT).show();
            }
        });
        sehir_degis.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!butonTiklandiMi) {
                    butonTiklandiMi = true;
                    if (internetVarmi())
                        SehirSec();
                    else {
                        Toast.makeText(ProfilActivity.this, "İnternet Bağlantınız Yok!", Toast.LENGTH_SHORT).show();
                        butonTiklandiMi = false;
                    }
                }
            }
        });
        kapak_sec_animasyon_icin.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction() == MotionEvent.ACTION_DOWN){
                    rect = new Rect(v.getLeft(), v.getTop(), v.getRight(), v.getBottom());
                    v.animate()
                            .scaleX(1-0.02f)
                            .scaleY(1-0.02f)
                            .setDuration(100)
                            .start();
                }
                if(event.getAction() == MotionEvent.ACTION_UP){
                    if(rect.contains((int)event.getX(), (int)event.getY())){
                        v.animate()
                                .scaleX(1)
                                .scaleY(1)
                                .setDuration(100)
                                .start();
                        if(internetVarmi())
                            if (!butonTiklandiMi) {
                                butonTiklandiMi = true;
                                if (kapakDegistirebilirMi) {
                                    deger = "kapak";
                                    if (ufkapak != null)
                                        ResimeNeOlsun();
                                    else
                                        FotoEkle();

                                }
                                else{
                                    Toast.makeText(ProfilActivity.this, "Kapak fotoğrafı ekleyebilmek için gerekli görevi tamamlamalısınız", Toast.LENGTH_SHORT).show();
                                    butonTiklandiMi = false;
                                }
                            }
                        else {
                                Toast.makeText(ProfilActivity.this, "İnternet Bağlantınız Yok!", Toast.LENGTH_SHORT).show();
                                butonTiklandiMi = false;
                            }
                    }
                }
                if(event.getAction() == MotionEvent.ACTION_MOVE){
                    if(!rect.contains((int)event.getX(), (int)event.getY())){
                        v.animate()
                                .scaleX(1)
                                .scaleY(1)
                                .setDuration(100)
                                .start();
                    }else if(event.getAction() == MotionEvent.ACTION_UP){
                        v.animate()
                                .scaleX(1)
                                .scaleY(1)
                                .setDuration(100)
                                .start();
                    }
                }
                return true;
            }
        });

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
    private void cancelScaleAnimation(final View view, final int deger) {
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
                                if(deger == 0){
                                    View sheetView = getLayoutInflater().inflate(R.layout.tasarim_degistir_bottomsheet, null);
                                    TasarimSecenekleri altMenu = new TasarimSecenekleri(ProfilActivity.this, 0);
                                    altMenu.setContentView(sheetView);
                                    altMenu.show();
                                    butonTiklandiMi = false;
                                }
                                if(deger == 1){
                                    View sheetView = getLayoutInflater().inflate(R.layout.tasarim_degistir_bottomsheet, null);
                                    TasarimSecenekleri altMenu = new TasarimSecenekleri(ProfilActivity.this, 1);
                                    altMenu.setContentView(sheetView);
                                    altMenu.show();
                                    butonTiklandiMi = false;
                                }
                                if(deger == 2){
                                    View sheetView = getLayoutInflater().inflate(R.layout.tasarim_degistir_bottomsheet, null);
                                    TasarimSecenekleri altMenu = new TasarimSecenekleri(ProfilActivity.this, 2);
                                    altMenu.setContentView(sheetView);
                                    altMenu.show();
                                    butonTiklandiMi = false;
                                }
                                if (deger == 3) {
                                    Dialog dialog = new Dialog(mActivity);
                                    dialog.setContentView(R.layout.dialog_dizayn_rozet_sec);
                                    dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                                    ImageView rozet1 = dialog.findViewById(R.id.rozet1);
                                    ImageView rozet2 = dialog.findViewById(R.id.rozet2);
                                    ImageView rozet3 = dialog.findViewById(R.id.rozet3);
                                    ImageView rozet4 = dialog.findViewById(R.id.rozet4);
                                    ImageView rozet5 = dialog.findViewById(R.id.rozet5);
                                    ImageView rozet6 = dialog.findViewById(R.id.rozet6);
                                    Button buton = dialog.findViewById(R.id.buton);

                                    rozet1.setColorFilter(getResources().getColor(R.color.siyah));
                                    rozet2.setColorFilter(getResources().getColor(R.color.siyah));
                                    rozet3.setColorFilter(getResources().getColor(R.color.siyah));
                                    rozet4.setColorFilter(getResources().getColor(R.color.siyah));
                                    rozet5.setColorFilter(getResources().getColor(R.color.siyah));
                                    rozet6.setColorFilter(getResources().getColor(R.color.siyah));

                                    if(rozetlerim.contains(",1"))
                                        rozet1.setVisibility(View.VISIBLE);
                                    else
                                        rozet1.setVisibility(View.INVISIBLE);
                                    if(rozetlerim.contains(",2"))
                                        rozet2.setVisibility(View.VISIBLE);
                                    else
                                        rozet2.setVisibility(View.INVISIBLE);
                                    if(rozetlerim.contains(",3"))
                                        rozet3.setVisibility(View.VISIBLE);
                                    else
                                        rozet3.setVisibility(View.INVISIBLE);
                                    if(rozetlerim.contains(",4"))
                                        rozet4.setVisibility(View.VISIBLE);
                                    else
                                        rozet4.setVisibility(View.INVISIBLE);
                                    if(rozetlerim.contains(",5"))
                                        rozet5.setVisibility(View.VISIBLE);
                                    else
                                        rozet5.setVisibility(View.INVISIBLE);
                                    if(rozetlerim.contains(",6"))
                                        rozet6.setVisibility(View.VISIBLE);
                                    else
                                        rozet6.setVisibility(View.INVISIBLE);

                                    buton.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            AsilDataSnapShot.child("rozet").getRef().setValue("").addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(@NonNull Void unused) {
                                                    rozet.setImageDrawable(null);
                                                    rozet_view_arka.setBackground(null);
                                                    BilgileriIste();
                                                    butonTiklandiMi = false;
                                                    dialog.dismiss();
                                                }
                                            });
                                        }
                                    });
                                    rozet1.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            AsilDataSnapShot.child("rozet").getRef().setValue("1").addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(@NonNull Void unused) {
                                                    rozet.setImageDrawable(getResources().getDrawable(R.drawable.rozet1));
                                                    rozet_view_arka.setBackground(getResources().getDrawable(R.drawable.rozet1_arka));
                                                    BilgileriIste();
                                                    butonTiklandiMi = false;
                                                    dialog.dismiss();
                                                }
                                            });
                                        }
                                    });
                                    rozet2.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            AsilDataSnapShot.child("rozet").getRef().setValue("2").addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(@NonNull Void unused) {
                                                    rozet.setImageDrawable(getResources().getDrawable(R.drawable.rozet2));
                                                    rozet_view_arka.setBackground(getResources().getDrawable(R.drawable.rozet2_arka));
                                                    BilgileriIste();
                                                    butonTiklandiMi = false;
                                                    dialog.dismiss();
                                                }
                                            });

                                        }
                                    });
                                    rozet3.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            AsilDataSnapShot.child("rozet").getRef().setValue("3").addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(@NonNull Void unused) {
                                                    rozet.setImageDrawable(getResources().getDrawable(R.drawable.rozet3));
                                                    rozet_view_arka.setBackground(getResources().getDrawable(R.drawable.rozet3_arka));
                                                    BilgileriIste();
                                                    butonTiklandiMi = false;
                                                    dialog.dismiss();
                                                }
                                            });

                                        }
                                    });
                                    rozet4.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            AsilDataSnapShot.child("rozet").getRef().setValue("4").addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(@NonNull Void unused) {
                                                    rozet.setImageDrawable(getResources().getDrawable(R.drawable.rozet4));
                                                    rozet_view_arka.setBackground(getResources().getDrawable(R.drawable.rozet4_arka));
                                                    BilgileriIste();
                                                    butonTiklandiMi = false;
                                                    dialog.dismiss();
                                                }
                                            });

                                        }
                                    });
                                    rozet5.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            AsilDataSnapShot.child("rozet").getRef().setValue("5").addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(@NonNull Void unused) {
                                                    rozet.setImageDrawable(getResources().getDrawable(R.drawable.rozet5));
                                                    rozet_view_arka.setBackground(getResources().getDrawable(R.drawable.rozet5_arka));
                                                    BilgileriIste();
                                                    butonTiklandiMi = false;
                                                    dialog.dismiss();
                                                }
                                            });

                                        }
                                    });
                                    rozet6.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            AsilDataSnapShot.child("rozet").getRef().setValue("6").addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(@NonNull Void unused) {
                                                    rozet.setImageDrawable(getResources().getDrawable(R.drawable.rozet6));
                                                    rozetLay.setBackground(getResources().getDrawable(R.drawable.rozet6_arka));
                                                    BilgileriIste();
                                                    butonTiklandiMi = false;
                                                    dialog.dismiss();
                                                }
                                            });
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
                            }
                        });
                    }
                });
            }
        });

    }

    private void KilitKaldirmakIcinDialogOlustur(String s) {
        Dialog dialog = new Dialog(mActivity);
        dialog.setContentView(R.layout.dialog_dizayn2);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        LinearLayout lay1 = dialog.findViewById(R.id.lay1);
        ProgressBar pbar = dialog.findViewById(R.id.pbar);
        pbar.getIndeterminateDrawable().setColorFilter(ortaRenk,android.graphics.PorterDuff.Mode.MULTIPLY);
        TextView baslik = dialog.findViewById(R.id.baslik);
        TextView aciklama = dialog.findViewById(R.id.aciklama);
        Button buton = dialog.findViewById(R.id.buton);
        if( fiyatlar != null){
            if(Kullanici.getKp() >= fiyatlar.getFoto_kilit()){
                baslik.setText("Fotoğraf Kilidi Kaldır");
                aciklama.setText("Kilidi kaldırmak için " + fiyatlar.getFoto_kilit() +" gerekli. Sizde bulunan "+Kullanici.getKp()+"KP");
                buton.setText("Kilidi Kaldır");
                buton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        lay1.setVisibility(View.GONE);
                        pbar.setVisibility(View.VISIBLE);
                        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("usersF").child(Kullanici.getUid());
                        ref.child("harcamalarim").child("foto_kilit_"+s).child("onceki_kp").setValue(Kullanici.getKp());
                        ref.child("harcamalarim").child("foto_kilit_"+s).child("zaman").setValue(ServerValue.TIMESTAMP);
                        ref.child("kp").setValue(Kullanici.getKp()-fiyatlar.getFoto_kilit()).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(@NonNull Void unused) {
                                ref.child("foto_kilitleri").child(s).setValue(1).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(@NonNull Void unused) {
                                        butonTiklandiMi = false;
                                        Toast.makeText(ProfilActivity.this, s+" Numaralı fotoğrafın kilidini açtınız!", Toast.LENGTH_SHORT).show();
                                        Kullanici.setKp(Kullanici.getKp()-fiyatlar.getFoto_kilit());
                                        if(s.equals("2")) {
                                            foto2kilidi = 1;
                                            kilit2.setImageDrawable(getResources().getDrawable(R.drawable.arti_ic));
                                        }
                                        else if(s.equals("3")) {
                                            foto3kilidi = 1;
                                            kilit3.setImageDrawable(getResources().getDrawable(R.drawable.arti_ic));
                                        }
                                        else if(s.equals("4")) {
                                            foto4kilidi = 1;
                                            kilit4.setImageDrawable(getResources().getDrawable(R.drawable.arti_ic));
                                        }
                                        else if(s.equals("5")) {
                                            foto5kilidi = 1;
                                            kilit5.setImageDrawable(getResources().getDrawable(R.drawable.arti_ic));
                                        }
                                        else if(s.equals("6")) {
                                            foto6kilidi = 1;
                                            kilit6.setImageDrawable(getResources().getDrawable(R.drawable.arti_ic));
                                        }

                                        dialog.dismiss();
                                    }
                                });
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                lay1.setVisibility(View.VISIBLE);
                                pbar.setVisibility(View.GONE);
                                butonTiklandiMi = false;
                                Toast.makeText(mActivity, "Başarısız!", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                });
            }
            else{
                butonTiklandiMi = false;
                baslik.setText("Yetersiz KP");
                aciklama.setText("Kilidi kaldırmak için " + fiyatlar.getFoto_kilit() + " gerekli. Sizde bulunan "+Kullanici.getKp()+"KP");
                buton.setText("Markete Git");
                buton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        lay1.setVisibility(View.GONE);
                        pbar.setVisibility(View.VISIBLE);
                        mActivity.startActivity(new Intent(mActivity,MarketActivity.class));
                    }
                });
            }
        }
        dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                butonTiklandiMi = false;
            }
        });
        dialog.show();
    }

    private void DegisiklikVarmi(){
        if(degisiklikSayisi > 4)
            degisiklikSayisi = 4;
        if(degisiklikSayisi < 0)
            degisiklikSayisi = 0;

        //Toast.makeText(ProfilActivity.this, ""+degisiklikSayisi, Toast.LENGTH_SHORT).show();

        if(degisiklikSayisi > 0) {
            final Animation animation = new AlphaAnimation(1, (float) 0.7);
            animation.setDuration(600);
            animation.setInterpolator(new LinearInterpolator());
            animation.setRepeatCount(Animation.INFINITE);
            animation.setRepeatMode(Animation.REVERSE);
            profili_kaydet.setVisibility(View.VISIBLE);
            profili_kaydet.startAnimation(animation);
        }
        else {
            profili_kaydet.setVisibility(View.GONE);
            profili_kaydet.clearAnimation();
        }
    }

    public static void TasarimDegistir(String tasDegeri, int tasModu, boolean ilkdegil) {
        GradientDrawable gradientBackground =  new GradientDrawable();
        gradientBackground.setOrientation(GradientDrawable.Orientation.LEFT_RIGHT);
        GradientDrawable gradientBackground2 =  new GradientDrawable();
        gradientBackground2.setOrientation(GradientDrawable.Orientation.LEFT_RIGHT);

        int renk1 = 0,renk2 = 0,t1start = 0,t2start = 0,t1end = 0,t2end = 0,orta = 0;


        renk1 = TasarimRenginiGetir.RengiGetir(mContext,"renk1",tasDegeri);
        renk2 = TasarimRenginiGetir.RengiGetir(mContext,"renk2",tasDegeri);
        t1start = TasarimRenginiGetir.RengiGetir(mContext,"t1start",tasDegeri);
        t2start = TasarimRenginiGetir.RengiGetir(mContext,"t2start",tasDegeri);
        t1end = TasarimRenginiGetir.RengiGetir(mContext,"t1end",tasDegeri);
        t2end = TasarimRenginiGetir.RengiGetir(mContext,"t2end",tasDegeri);
        orta = TasarimRenginiGetir.RengiGetir(mContext,"orta",tasDegeri);

        gradientBackground.setColors(new int[]{
                renk1,
                orta,
                renk2
        });
        gradientBackground2.setColors(new int[]{
                renk1,
                orta,
                renk2
        });


        if(tasModu == 0){
            tas_profil_view.setBackground(gradientDrawable(tasDegeri));
        }
        else if(tasModu == 1){
            tas_mesaj_view.setBackground(gradientDrawable(tasDegeri));
        }
        else {
            GradientDrawable kapakEkle = (GradientDrawable) mContext.getResources().getDrawable(R.drawable.kapak_ekle);
            kapakEkle.setColors(new int[]{
                    renk1,
                    orta,
                    renk2
            });

            GradientDrawable arka = new GradientDrawable();

            arka.setColors(new int[]{
                    renk1,
                    orta,
                    renk2
            });

            GradientDrawable etGibi = (GradientDrawable) mContext.getResources().getDrawable(R.drawable.buton_arka_et_gibi);
            etGibi.setStroke(3, orta);
            GradientDrawable butonArka = new GradientDrawable();
            butonArka.setCornerRadius(50);
            butonArka.setStroke(3, orta);
            GradientDrawable adArka = new GradientDrawable();
            adArka.setCornerRadius(50);
            adArka.setStroke(3, orta);

            /*tas_profil.setBackground(butonArka);
            tas_mesaj.setBackground(butonArka);
            tas_arayuz.setBackground(butonArka);*/
            /*if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                adTV.setBackgroundTintList(ColorStateList.valueOf(orta));

            }*/
            /*sehir_degis.setBackground(arka);
            sehir_degis.getBackground().setColorFilter(orta,
                    PorterDuff.Mode.SRC_ATOP);
            usnameButton.getBackground().setColorFilter(orta,
                    PorterDuff.Mode.SRC_ATOP);
            //usnameButton.setTextColor(orta);
            kapakEkleGradient = kapakEkle;
            usnameButton.setBackground(arka);
            kapak_sec.setBackground(kapakEkle);*/
            background.setBackground(gradientBackground);
            background2.setBackground(gradientBackground2);
            tas_arayuz_view.setBackground(gradientDrawable(tasDegeri));
        }

        if (tasModu == 2)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            VectorDrawable kalp = (VectorDrawable) mContext.getDrawable(R.drawable.ic_profil_begen1);
            kalp.setTint(orta);
            kalp.setBounds(0, 0, kalp.getIntrinsicHeight(), kalp.getIntrinsicWidth());
            begenenler.setCompoundDrawables(kalp, null, null, null);
            VectorDrawable cancel = (VectorDrawable) mContext.getDrawable(R.drawable.ic_cancel);
            cancel.setTint(orta);
            cancel.setBounds(0, 0, cancel.getIntrinsicHeight(), cancel.getIntrinsicWidth());
            diptal.setCompoundDrawables(cancel, null, null, null);
            VectorDrawable save = (VectorDrawable) mContext.getDrawable(R.drawable.ic_save);
            save.setTint(orta);
            save.setBounds(0, 0, save.getIntrinsicHeight(), save.getIntrinsicWidth());
            profili_kaydet.setCompoundDrawables(save, null, null, null);
            VectorDrawable duzenle = (VectorDrawable) mContext.getDrawable(R.drawable.ic_duzenle);
            duzenle.setTint(orta);
            duzenle.setBounds(0, 0, duzenle.getIntrinsicHeight(), duzenle.getIntrinsicWidth());
            profili_duzenle.setCompoundDrawables(duzenle, null, null, null);
        }
        if(ilkdegil) {
            TasarimSecenekleri.tasarimSecenekleri.cancel();
            TasarimSecenekleri.tasarimSecenekleri.dismiss();
            VeritabanindaSatinAlmaYadaTasarimDegistirme(false, tasDegeri, tasModu, 0);
        }
        ortaRenk = orta;
    }
    private static void SatinAlinsinMi(int deger, String tasarimDegeri, int tasarimModu) {

        SharedPreferences tas_shared = mActivity.getSharedPreferences("TasarimBilgileri",MODE_PRIVATE);
        SharedPreferences.Editor tas_seditor = tas_shared.edit();

        Dialog dialog = new Dialog(mActivity);
        dialog.setContentView(R.layout.dialog_dizayn2);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        LinearLayout lay1 = dialog.findViewById(R.id.lay1);
        ProgressBar pbar = dialog.findViewById(R.id.pbar);
        pbar.getIndeterminateDrawable().setColorFilter(ortaRenk,android.graphics.PorterDuff.Mode.MULTIPLY);
        TextView baslik = dialog.findViewById(R.id.baslik);
        TextView aciklama = dialog.findViewById(R.id.aciklama);
        Button buton = dialog.findViewById(R.id.buton);
        if(fiyatlar != null)
        if(deger == 1)
            deger = fiyatlar.getTasarim_seviye1();
        else if(deger == 2)
            deger = fiyatlar.getTasarim_seviye2();
        else if(deger == 3)
            deger = fiyatlar.getTasarim_seviye3();

        if(deger <= Kullanici.getKp() && deger > 10) {
            aciklama.setText("Bu tasarımın değeri "+deger+"KP satın alınsın mı?");
            baslik.setText("Satın Al");
            buton.setText("Satın Al");
            int finalDeger = deger;
            buton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    lay1.setVisibility(View.GONE);
                    pbar.setVisibility(View.VISIBLE);
                    DatabaseReference ref = FirebaseDatabase.getInstance().getReference("usersF").child(Kullanici.getUid());
                    ref.child("harcamalarim").child("tasmod_"+tasarimModu+"tasdeg_"+tasarimDegeri).child("onceki_kp").setValue(Kullanici.getKp());
                    ref.child("harcamalarim").child("tasmod_"+tasarimModu+"tasdeg_"+tasarimDegeri).child("zaman").setValue(ServerValue.TIMESTAMP);
                    ref.child("kp").setValue(Kullanici.getKp()- finalDeger).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(@NonNull Void unused) {
                            if(tasarimModu == 0){ // Profil
                                ref.child("tas_profil_sahibim").setValue(Kullanici.getTas_profil_sahibim()+","+tasarimDegeri).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(@NonNull Void unused) {
                                        ref.child("tas_profil").setValue(tasarimDegeri).addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(@NonNull Void unused) {
                                                //tas_seditor.putString("tasarim_profil",tasarimDegeri);
                                                TasarimDegistir(tasarimDegeri,tasarimModu,true);
                                                Toast.makeText(mActivity, "Satın alma başarılı", Toast.LENGTH_SHORT).show();
                                                Kullanici.setTas_profil(tasarimDegeri);
                                                TasarimDegeriProfil = tasarimDegeri;
                                                TasarimProfilSahipOlduklarim = TasarimProfilSahipOlduklarim+","+tasarimDegeri;
                                                tas_profil_view.setBackground(gradientDrawable(tasarimDegeri));
                                                TasarimSecenekleri.onizlemeTasDegeri = tasarimDegeri;
                                                TasarimSecenekleri.Guncelle();
                                                dialog.dismiss();
                                            }
                                        });
                                    }
                                });
                            }
                            else if(tasarimModu == 1){ // mesaj
                                ref.child("tas_mesaj_sahibim").setValue(Kullanici.getTas_mesaj_sahibim()+","+tasarimDegeri).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(@NonNull Void unused) {
                                        ref.child("tas_mesaj").setValue(tasarimDegeri).addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(@NonNull Void unused) {
                                                //tas_seditor.putString("tasarim_mesaj",tasarimDegeri);
                                                TasarimDegistir(tasarimDegeri,tasarimModu,true);
                                                Toast.makeText(mActivity, "Satın alma başarılı", Toast.LENGTH_SHORT).show();
                                                Kullanici.setTas_mesaj(tasarimDegeri);
                                                TasarimDegeriMesaj = tasarimDegeri;
                                                TasarimMesajSahipOlduklarim = TasarimMesajSahipOlduklarim+","+tasarimDegeri;
                                                tas_mesaj_view.setBackground(gradientDrawable(tasarimDegeri));
                                                TasarimSecenekleri.onizlemeTasDegeri = tasarimDegeri;
                                                TasarimSecenekleri.Guncelle();
                                                dialog.dismiss();
                                            }
                                        });
                                    }
                                });
                            }
                            else{// arayüz
                                ref.child("tas_arayuz_sahibim").setValue(Kullanici.getTas_arayuz_sahibim()+","+tasarimDegeri).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(@NonNull Void unused) {
                                        ref.child("tas_arayuz").setValue(tasarimDegeri).addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(@NonNull Void unused) {
                                                tas_seditor.putString("tasarim_arayuz",tasarimDegeri);
                                                tas_seditor.commit();
                                                TasarimDegistir(tasarimDegeri,tasarimModu,true);
                                                Toast.makeText(mActivity, "Satın alma başarılı", Toast.LENGTH_SHORT).show();
                                                Kullanici.setTas_arayuz(tasarimDegeri);
                                                TasarimDegeriArayuz = tasarimDegeri;
                                                TasarimArayuzSahipOlduklarim = TasarimArayuzSahipOlduklarim+","+tasarimDegeri;
                                                tas_arayuz_view.setBackground(gradientDrawable(tasarimDegeri));
                                                TasarimSecenekleri.onizlemeTasDegeri = tasarimDegeri;
                                                TasarimSecenekleri.Guncelle();
                                                dialog.dismiss();
                                            }
                                        });
                                    }
                                });
                            }
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            lay1.setVisibility(View.VISIBLE);
                            pbar.setVisibility(View.GONE);
                            Toast.makeText(mActivity, "Başarısız!", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            });

        }
        else{
            buton.setText("Markete Git");
            aciklama.setText("Bu tasarımın değeri "+deger+"KP, sizin sahip olduğunuz "+Kullanici.getKp()+"KP");
            baslik.setText("Yetersiz KP!");
            buton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    lay1.setVisibility(View.GONE);
                    pbar.setVisibility(View.VISIBLE);
                    mActivity.startActivity(new Intent(mActivity,MarketActivity.class));
                }
            });
        }

        dialog.show();
    }

    public static void VeritabanindaSatinAlmaYadaTasarimDegistirme(boolean satinAlmaMi,String tasDegeri, int tasModu,int deger){
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("usersF").child(Kullanici.getUid());
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Kullanici = dataSnapshot.getValue(User.class);
                if(satinAlmaMi){
                    SatinAlinsinMi(deger,tasDegeri,tasModu);
                }
                else {
                    DatabaseReference ref = FirebaseDatabase.getInstance().getReference("usersF").child(Kullanici.getUid());
                    if(tasModu == 0){
                        ref.child("tas_profil").setValue(tasDegeri).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(@NonNull Void unused) {
                                Kullanici.setTas_profil(tasDegeri);
                                TasarimDegeriProfil = tasDegeri;
                                tas_profil_view.setBackground(gradientDrawable(TasarimDegeriProfil));
                                TasarimSecenekleri.onizlemeTasDegeri = tasDegeri;
                                TasarimSecenekleri.Guncelle();
                            }
                        });
                    }
                    else if(tasModu == 1){
                        ref.child("tas_mesaj").setValue(tasDegeri).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(@NonNull Void unused) {
                                Kullanici.setTas_mesaj(tasDegeri);
                                TasarimDegeriMesaj = tasDegeri;
                                tas_mesaj_view.setBackground(gradientDrawable(TasarimDegeriMesaj));
                                TasarimSecenekleri.onizlemeTasDegeri = tasDegeri;
                                TasarimSecenekleri.Guncelle();
                            }
                        });
                    }
                    else {
                        ref.child("tas_arayuz").setValue(tasDegeri).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(@NonNull Void unused) {
                                Kullanici.setTas_arayuz(tasDegeri);
                                TasarimDegeriArayuz = tasDegeri;
                                TasarimSecenekleri.onizlemeTasDegeri = tasDegeri;
                                TasarimSecenekleri.Guncelle();
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

    private static void FotoKilitleriniKontrolEt(DataSnapshot dataSnapshot) {
        if(dataSnapshot.child("foto_kilitleri").child("2").getValue(Integer.class) == 0){
            foto2kilidi = 0;
            kilit2.setImageDrawable(mActivity.getResources().getDrawable(R.drawable.kilit));
        }else{
            foto2kilidi = 1;
            kilit2.setImageDrawable(mActivity.getResources().getDrawable(R.drawable.arti_ic));
        }

        if(dataSnapshot.child("foto_kilitleri").child("3").getValue(Integer.class) == 0){
            foto3kilidi = 0;
            kilit3.setImageDrawable(mActivity.getResources().getDrawable(R.drawable.kilit));
        }else{
            foto3kilidi = 1;
            kilit3.setImageDrawable(mActivity.getResources().getDrawable(R.drawable.arti_ic));
        }

        if(dataSnapshot.child("foto_kilitleri").child("4").getValue(Integer.class) == 0){
            foto4kilidi = 0;
            kilit4.setImageDrawable(mActivity.getResources().getDrawable(R.drawable.kilit));
        }else{
            foto4kilidi = 1;
            kilit4.setImageDrawable(mActivity.getResources().getDrawable(R.drawable.arti_ic));
        }

        if(dataSnapshot.child("foto_kilitleri").child("5").getValue(Integer.class) == 0){
            foto5kilidi = 0;
            kilit5.setImageDrawable(mActivity.getResources().getDrawable(R.drawable.kilit));
        }else{
            foto5kilidi = 1;
            kilit5.setImageDrawable(mActivity.getResources().getDrawable(R.drawable.arti_ic));
        }

        if(dataSnapshot.child("foto_kilitleri").child("6").getValue(Integer.class) == 0){
            foto6kilidi = 0;
            kilit6.setImageDrawable(mActivity.getResources().getDrawable(R.drawable.kilit));
        }else{
            foto6kilidi = 1;
            kilit6.setImageDrawable(mActivity.getResources().getDrawable(R.drawable.arti_ic));
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
    @Override
    protected void onResume() {
        super.onResume();
        SharedPreferences tas_shared = getSharedPreferences("TasarimBilgileri",MODE_PRIVATE);
        TasarimDegistir(tas_shared.getString("tasarim_arayuz","1"), 2,false);
        //BilgileriIste();
    }

    private void KapakEkle(){
        deger = "kapak";
        Intent intentGalley = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intentGalley.setType("image/*");
        startActivityForResult(intentGalley, 5);
    }
    private boolean internetVarmi(){
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED)
            return true;
        else
            return false;
    }

    private void ResimeEkle() {
        eklevesil = false;
        FotoEkle();
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent tarayaDon = new Intent(ProfilActivity.this,TaraActivity.class);
        startActivity(tarayaDon);
       // finish();
        overridePendingTransition(R.anim.slide_in_left,R.anim.slide_out_right);
    }

    private void FotolariGetir() {
        r1varmi = false;
        r2varmi = false;
        r3varmi = false;
        r4varmi = false;
        r5varmi = false;
        r6varmi = false;
        kapakVarmi = false;

        fotolar.clear();
        fotolar = new ArrayList<>();

        fotoViewList = new ArrayList<ImageView>();
        fotoViewList.add(foto_1);
        fotoViewList.add(foto_2);
        fotoViewList.add(foto_3);
        fotoViewList.add(foto_4);
        fotoViewList.add(foto_5);
        fotoViewList.add(foto_6);

        ekleViewList = new ArrayList<ImageView>();
        ekleViewList.add(arti_ic);
        ekleViewList.add(kilit2);
        ekleViewList.add(kilit3);
        ekleViewList.add(kilit4);
        ekleViewList.add(kilit5);
        ekleViewList.add(kilit6);

        if(ufkapak == null){
            kapak.setImageDrawable(null);
            kapak.setBackgroundColor(getResources().getColor(R.color.siyah));
        }
        ContextWrapper cw = new ContextWrapper(ProfilActivity.this);
        File directory = cw.getDir("benim_resimler", MODE_PRIVATE);

        File imagepp = new File(directory, "foto_pp.jpg");
        if(imagepp.exists()){
            foto_pp.setImageURI(Uri.parse(imagepp.getAbsolutePath()));
            ppyuklendimi = true;
            pbar.setVisibility(View.GONE);
            anaLay.setVisibility(View.VISIBLE);
        }
        else{
            ppyuklendimi = false;
        }
        File imagekapak = new File(directory, "foto_kapak.jpg");
        if(imagekapak.exists()){
            kapakVarmi = true;
            kapak.setImageURI(Uri.parse(imagekapak.getAbsolutePath()));
            kapak_sec.setImageURI(Uri.parse(imagekapak.getAbsolutePath()));
        }

        File image1 = new File(directory, "foto_1.jpg");
        if(image1.exists()){
            r1varmi = true;
            foto_1.setImageURI(Uri.parse(image1.getAbsolutePath()));
            arti_ic.setVisibility(View.GONE);
            //Toast.makeText(ProfilActivity.this, "var", Toast.LENGTH_SHORT).show();
        }
        File image2 = new File(directory, "foto_2.jpg");
        if(image2.exists()){
            r2varmi = true;
            foto_2.setImageURI(Uri.parse(image2.getAbsolutePath()));
            kilit2.setVisibility(View.GONE);
        }
        File image3 = new File(directory, "foto_3.jpg");
        if(image3.exists()){
            r3varmi = true;
            foto_3.setImageURI(Uri.parse(image3.getAbsolutePath()));
            kilit3.setVisibility(View.GONE);
        }
        File image4 = new File(directory, "foto_4.jpg");
        if(image4.exists()){
            r4varmi = true;
            foto_4.setImageURI(Uri.parse(image4.getAbsolutePath()));
            kilit4.setVisibility(View.GONE);
        }
        File image5 = new File(directory, "foto_5.jpg");
        if(image5.exists()){
            r5varmi = true;
            foto_5.setImageURI(Uri.parse(image5.getAbsolutePath()));
            kilit5.setVisibility(View.GONE);
        }
        File image6 = new File(directory, "foto_6.jpg");
        if(image6.exists()){
            r6varmi = true;
            foto_6.setImageURI(Uri.parse(image6.getAbsolutePath()));
            kilit6.setVisibility(View.GONE);
        }


        rBoolList = new ArrayList<Boolean>();
        rBoolList.add(r1varmi);
        rBoolList.add(r2varmi);
        rBoolList.add(r3varmi);
        rBoolList.add(r4varmi);
        rBoolList.add(r5varmi);
        rBoolList.add(r6varmi);

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("usersF").child(fuser.getUid());
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.child("kisitli_erisim_engeli").child("durum").getValue().equals("var")) {
                    Intent intent = new Intent(ProfilActivity.this, MainActivity.class);
                    startActivity(intent);
                }
                if (dataSnapshot.hasChild("fotograflarim")) {
                    if (!dataSnapshot.child("fotograflarim").hasChild("kapak")){
                        if (kapakVarmi){
                            ufkapak = null;
                            imagekapak.delete();
                            kapakVarmi = false;
                            kapak.setImageDrawable(null);
                            kapak.setBackgroundColor(getResources().getColor(R.color.siyah));
                            kapak_sec.setImageDrawable(null);
                        }
                    }
                    if (!dataSnapshot.child("fotograflarim").hasChild("1")){
                        if (r1varmi){
                            image1.delete();
                            r1varmi = false;
                            foto_1.setImageDrawable(null);
                            foto_1.setBackgroundColor(getResources().getColor(R.color.arka));
                            arti_ic.setVisibility(View.VISIBLE);
                        }
                    }
                    if (!dataSnapshot.child("fotograflarim").hasChild("2")){
                        if (r2varmi){
                            image2.delete();
                            r2varmi = false;
                            foto_2.setImageDrawable(null);
                            foto_2.setBackgroundColor(getResources().getColor(R.color.arka));
                            kilit2.setVisibility(View.VISIBLE);
                        }
                    }
                    if (!dataSnapshot.child("fotograflarim").hasChild("3")){
                        if (r3varmi){
                            image3.delete();
                            r3varmi = false;
                            foto_3.setImageDrawable(null);
                            foto_3.setBackgroundColor(getResources().getColor(R.color.arka));
                            kilit3.setVisibility(View.VISIBLE);
                        }
                    }
                    if (!dataSnapshot.child("fotograflarim").hasChild("4")){
                        if (r4varmi){
                            image4.delete();
                            r4varmi = false;
                            foto_4.setImageDrawable(null);
                            foto_4.setBackgroundColor(getResources().getColor(R.color.arka));
                            kilit4.setVisibility(View.VISIBLE);
                        }
                    }
                    if (!dataSnapshot.child("fotograflarim").hasChild("5")){
                        if (r5varmi){
                            image5.delete();
                            r5varmi = false;
                            foto_5.setImageDrawable(null);
                            foto_5.setBackgroundColor(getResources().getColor(R.color.arka));
                            kilit5.setVisibility(View.VISIBLE);
                        }
                    }
                    if (!dataSnapshot.child("fotograflarim").hasChild("6")){
                        if (r6varmi){
                            image6.delete();
                            r6varmi = false;
                            foto_6.setImageDrawable(null);
                            foto_6.setBackgroundColor(getResources().getColor(R.color.arka));
                            kilit6.setVisibility(View.VISIBLE);
                        }
                    }
                    for (DataSnapshot ds : dataSnapshot.child("fotograflarim").getChildren()) {
                        SharedPreferences fotopref = getSharedPreferences("Fotolar",MODE_PRIVATE);
                        SharedPreferences.Editor editor = fotopref.edit();
                        if (ds.getKey().equals("pp")){
                            ufpp = ds.getValue(String.class);
                            if (!ppyuklendimi){
                                if(ufpp.length() > 5) {
                                    ppyuklendimi = true;
                                    pbar.setVisibility(View.GONE);
                                    anaLay.setVisibility(View.VISIBLE);
                                    editor.putString("foto_pp", ufpp.substring(ufpp.length() - 3));
                                    editor.commit();
                                    Glide.with(ProfilActivity.this).asBitmap().load(ds.getValue(String.class)).into(foto_pp);
                                    new ResimIndir(getApplicationContext(), ds.getValue(String.class), "benim_resimler", "foto_pp.jpg");
                                }
                            }
                            else {
                                if (!fotopref.getString("foto_pp","").equals(ufpp.substring(ufpp.length()-3))){
                                    editor.putString("foto_pp",ufpp.substring(ufpp.length()-3));
                                    editor.commit();
                                    Glide.with(ProfilActivity.this).asBitmap().load(ds.getValue(String.class)).into(foto_pp);
                                    //new ResimIndir(getApplicationContext(), ds.getValue(String.class), "benim_resimler", "foto_pp.jpg");
                                }
                            }
                        }
                        if(ds.getKey().equals("1")) {
                            FotoAyari(1,ds,editor,fotopref);
                        }
                        if(ds.getKey().equals("2")) {
                            FotoAyari(2,ds,editor,fotopref);
                            /*uf2 = ds.getValue(String.class);
                            if (!r2varmi) {
                                editor.putString("foto_2", uf2.substring(uf2.length() - 3));
                                Glide.with(ProfilActivity.this).asBitmap().load(ds.getValue(String.class)).into(foto_2);
                                new ResimIndir(getApplicationContext(), ds.getValue(String.class), "benim_resimler", "foto_2.jpg");
                                r2varmi = true;
                                kilit2.setVisibility(View.GONE);
                            } else {
                                if (!fotopref.getString("foto_2", "").equals(uf2.substring(uf2.length() - 3))) {
                                    editor.putString("foto_2", uf2.substring(uf2.length() - 3));
                                    Glide.with(ProfilActivity.this).asBitmap().load(ds.getValue(String.class)).into(foto_2);
                                    //new ResimIndir(getApplicationContext(), ds.getValue(String.class), "benim_resimler", "foto_2.jpg");
                                }
                            }*/
                        }
                        if(ds.getKey().equals("3")) {
                            FotoAyari(3,ds,editor,fotopref);
                            /*uf3 = ds.getValue(String.class);
                            if (!r3varmi) {
                                editor.putString("foto_3", uf3.substring(uf3.length() - 3));
                                Glide.with(ProfilActivity.this).asBitmap().load(ds.getValue(String.class)).into(foto_3);
                                new ResimIndir(getApplicationContext(), ds.getValue(String.class), "benim_resimler", "foto_3.jpg");
                                r3varmi = true;
                                kilit3.setVisibility(View.GONE);
                            }
                            else {
                                if (!fotopref.getString("foto_3", "").equals(uf3.substring(uf3.length() - 3))) {
                                    editor.putString("foto_3", uf3.substring(uf3.length() - 3));
                                    Glide.with(ProfilActivity.this).asBitmap().load(ds.getValue(String.class)).into(foto_3);
                                    //new ResimIndir(getApplicationContext(), ds.getValue(String.class), "benim_resimler", "foto_3.jpg");
                                }
                            }*/
                        }
                        if(ds.getKey().equals("4")) {
                            FotoAyari(4,ds,editor,fotopref);
                            /*uf4 = ds.getValue(String.class);
                            if (!r4varmi) {
                                editor.putString("foto_4", uf4.substring(uf4.length() - 3));
                                Glide.with(ProfilActivity.this).asBitmap().load(ds.getValue(String.class)).into(foto_4);
                                new ResimIndir(getApplicationContext(), ds.getValue(String.class), "benim_resimler", "foto_4.jpg");
                                r4varmi = true;
                                kilit4.setVisibility(View.GONE);
                            }
                            else {
                                if (!fotopref.getString("foto_4", "").equals(uf4.substring(uf4.length() - 3))) {
                                    editor.putString("foto_4", uf4.substring(uf4.length() - 3));
                                    Glide.with(ProfilActivity.this).asBitmap().load(ds.getValue(String.class)).into(foto_4);
                                    //new ResimIndir(getApplicationContext(), ds.getValue(String.class), "benim_resimler", "foto_4.jpg");
                                }
                            }*/
                        }
                        if(ds.getKey().equals("5")){
                            FotoAyari(5,ds,editor,fotopref);
                            /*uf5 = ds.getValue(String.class);
                            if(!r5varmi){
                                editor.putString("foto_5", uf5.substring(uf5.length() - 3));
                                Glide.with(ProfilActivity.this).asBitmap().load(ds.getValue(String.class)).into(foto_5);
                                new ResimIndir(getApplicationContext(),  ds.getValue(String.class), "benim_resimler", "foto_5.jpg");
                                r5varmi = true;
                                kilit5.setVisibility(View.GONE);
                            }
                            else {
                                if (!fotopref.getString("foto_5", "").equals(uf5.substring(uf5.length() - 3))) {
                                    editor.putString("foto_5", uf5.substring(uf5.length() - 3));
                                    Glide.with(ProfilActivity.this).asBitmap().load(ds.getValue(String.class)).into(foto_5);
                                    //new ResimIndir(getApplicationContext(), ds.getValue(String.class), "benim_resimler", "foto_5.jpg");
                                }
                            }*/
                        }
                        if(ds.getKey().equals("6")) {
                            FotoAyari(6,ds,editor,fotopref);
                            /*uf6 = ds.getValue(String.class);
                            if (!r6varmi) {
                                editor.putString("foto_6", uf6.substring(uf6.length() - 3));
                                Glide.with(ProfilActivity.this).asBitmap().load(ds.getValue(String.class)).into(foto_6);
                                new ResimIndir(getApplicationContext(), ds.getValue(String.class), "benim_resimler", "foto_6.jpg");
                                r6varmi = true;
                                kilit6.setVisibility(View.GONE);
                            }
                            else {
                                if (!fotopref.getString("foto_6", "").equals(uf6.substring(uf6.length() - 3))) {
                                    editor.putString("foto_6", uf6.substring(uf6.length() - 3));
                                    Glide.with(ProfilActivity.this).asBitmap().load(ds.getValue(String.class)).into(foto_6);
                                    //new ResimIndir(getApplicationContext(), ds.getValue(String.class), "benim_resimler", "foto_6.jpg");
                                }
                            }*/
                        }
                        if(ds.getKey().equals("kapak")) {
                            ufkapak = ds.getValue(String.class);
                            if (!kapakVarmi) {
                                editor.putString("foto_kapak", ufkapak.substring(ufkapak.length() - 3));
                                editor.commit();
                                Glide.with(ProfilActivity.this).asBitmap().load(ds.getValue(String.class)).into(kapak);
                                Glide.with(ProfilActivity.this).asBitmap().load(ds.getValue(String.class)).into(kapak_sec);
                                new ResimIndir(getApplicationContext(), ds.getValue(String.class), "benim_resimler", "foto_kapak.jpg");
                                kapakVarmi = true;
                            }
                            else {
                                if (!fotopref.getString("foto_kapak", "").equals(ufkapak.substring(ufkapak.length() - 3))) {
                                    editor.putString("foto_kapak", ufkapak.substring(ufkapak.length() - 3));
                                    editor.commit();
                                    Glide.with(ProfilActivity.this).asBitmap().load(ds.getValue(String.class)).into(kapak);
                                    Glide.with(ProfilActivity.this).asBitmap().load(ds.getValue(String.class)).into(kapak_sec);
                                    //new ResimIndir(getApplicationContext(), ds.getValue(String.class), "benim_resimler", "foto_kapak.jpg");
                                }
                            }
                        }
                    }
                }
                //FotoSayisi();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        //netYoksaGetir("Profil",fotoView);
    }

    private void FotoAyari(int sayiDegeri, DataSnapshot ds, SharedPreferences.Editor editor, SharedPreferences fotopref){
        mStorageRef.child("foto_"+(sayiDegeri)+".jpg").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(@NonNull Uri uri) {
                String uf = "";
                if (uri.toString().equals(ds.getValue(String.class))){
                    uf = ds.getValue(String.class);
                    if (sayiDegeri == 1)
                        uf1 = uf;
                    else if (sayiDegeri == 2)
                        uf2 = uf;
                    else if (sayiDegeri == 3)
                        uf3 = uf;
                    else if (sayiDegeri == 4)
                        uf4 = uf;
                    else if (sayiDegeri == 5)
                        uf5 = uf;
                    else if (sayiDegeri == 6)
                        uf6 = uf;
                }
                else {
                    uf = uri.toString();
                    if (sayiDegeri == 1)
                        uf1 = uf;
                    else if (sayiDegeri == 2)
                        uf2 = uf;
                    else if (sayiDegeri == 3)
                        uf3 = uf;
                    else if (sayiDegeri == 4)
                        uf4 = uf;
                    else if (sayiDegeri == 5)
                        uf5 = uf;
                    else if (sayiDegeri == 6)
                        uf6 = uf;
                    ds.getRef().setValue(uf);
                }
                //Toast.makeText(ProfilActivity.this, (sayiDegeri-1)+" "+rBoolList.get(sayiDegeri-1), Toast.LENGTH_SHORT).show();
                if (!rBoolList.get(sayiDegeri-1)) {
                    editor.putString("foto_"+(sayiDegeri-1), uf.substring(uf.length() - 3));
                    editor.commit();
                    Glide.with(ProfilActivity.this).asBitmap().load(uf).into(fotoViewList.get(sayiDegeri-1));
                    new ResimIndir(getApplicationContext(), uf, "benim_resimler", "foto_"+(sayiDegeri)+".jpg");
                    //Toast.makeText(ProfilActivity.this, "indiriyom", Toast.LENGTH_SHORT).show();
                    rBoolList.set(sayiDegeri-1,true);
                    ekleViewList.get(sayiDegeri-1).setVisibility(View.GONE);
                } else {
                    if (!fotopref.getString("foto_"+(sayiDegeri-1), "").equals(uf.substring(uf.length() - 3))) {
                        editor.putString("foto_"+(sayiDegeri-1), uf.substring(uf.length() - 3));
                        editor.commit();
                        //Toast.makeText(ProfilActivity.this, "burda", Toast.LENGTH_SHORT).show();
                        Glide.with(ProfilActivity.this).asBitmap().load(uf).into(fotoViewList.get(sayiDegeri-1));
                        //new ResimIndir(getApplicationContext(), ds.getValue(String.class), "benim_resimler", "foto_1.jpg");
                    }
                }
            }
        });
    }


    @Override
    protected void onPause() {
        super.onPause();
        ppyuklendimi = false;
        butonTiklandiMi = false;
    }

    @Override
    protected void onStop() {
        super.onStop();
        butonTiklandiMi = false;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        FotoEkle();
    }

    private void ResmiKirp(Uri resultUri){
        if (!deger.equals("kapak") && !deger.equals("pp") && !deger.equals("") && !String.valueOf(resultUri).equals("")) {
            CropImage.activity(resultUri)
                    .setAspectRatio(300, 480)
                    .setActivityTitle("Resmi Kırp")
                    .setCropMenuCropButtonTitle("Kırp")
                    .setOutputCompressQuality(70)
                    .setBorderLineColor(getResources().getColor(R.color.beyaz))
                    .start(this);
        }
        else if (deger.equals("pp")) {
            anaDeger = 5;
            CropImage.activity(resultUri)
                    .setAspectRatio(1, 1)
                    .setActivityTitle("Resmi Kırp")
                    .setCropMenuCropButtonTitle("Kırp")
                    .setOutputCompressQuality(70)
                    .setBorderLineColor(getResources().getColor(R.color.beyaz))
                    .start(this);
        }
        else if(deger.equals("kapak")){
            anaDeger = 5;
            CropImage.activity(resultUri)
                    .setAspectRatio(375, (int) 227)
                    .setActivityTitle("Resmi Kırp")
                    .setCropMenuCropButtonTitle("Kırp")
                    .setOutputCompressQuality(70)
                    .setBorderLineColor(getResources().getColor(R.color.beyaz))
                    .start(this);
        }
    }
    void FotoEkle(){
        butonTiklandiMi = false;
        Intent pickIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        pickIntent.setType("image/*");
        startActivityForResult(pickIntent, 99);
        /*
        Intent getIntent = new Intent(Intent.ACTION_GET_CONTENT);
        getIntent.setType("image/*");
        Intent chooserIntent = Intent.createChooser(getIntent, "Resim Seçin");
        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Intent[] {pickIntent});*/

    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                Uri resultUri = result.getUri();
                //YuklemeIslemi(resultUri,false,"");
                if (resultUri != null)
                    ResimKontrol(String.valueOf(resultUri));
                else
                    Toast.makeText(ProfilActivity.this, "Bir hata oluştu", Toast.LENGTH_SHORT).show();
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
                Toast.makeText(ProfilActivity.this, ""+error, Toast.LENGTH_SHORT).show();
            }
        }
        //Toast.makeText(ProfilActivity.this, " "+ requestCode + " \n"+ resultCode + " \n"+data.getData(), Toast.LENGTH_LONG).show();
        if(requestCode == 99){
            if(data != null) {
                if (resultCode == RESULT_OK) {
                    Uri resultUri = data.getData();
                    yuklenecekResimUrl = String.valueOf(resultUri);
                    if(resultUri != null)
                        ResmiKirp(resultUri);
                    else
                        Toast.makeText(ProfilActivity.this, "Bir hata oluştu", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(ProfilActivity.this, "" + data.getDataString(), Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    @Override
    public void finish() {
        super.finish();

    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        if (savedInstanceState != null){
            yuklenecekResimUrl = savedInstanceState.getString("url","");
            deger = savedInstanceState.getString("deger","");
            //ResmiKirp(Uri.parse(yuklenecekResimUrl));
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (yuklenecekResimUrl == null) {
            outState.putString("url", yuklenecekResimUrl);
            outState.putString("deger", deger);

        }
    }


    private void ResimKontrol(String uri){
        InputImage image = null;
        try {
            image = InputImage.fromFilePath(ProfilActivity.this, Uri.parse(uri));
        } catch (IOException e) {
            e.printStackTrace();
        }
        TextRecognizer recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS);
        recognizer.process(image).addOnSuccessListener(new OnSuccessListener<Text>() {
            @Override
            public void onSuccess(@NonNull Text text) {
                if(text.getText().contains("@") || text.getText().toLowerCase().contains("twitter") || text.getText().toLowerCase().contains("instagram") ||
                        text.getText().toLowerCase().contains("youtube") || text.getText().toLowerCase().contains("tiktok")){
                    Toast.makeText(ProfilActivity.this, "Fotoğrafınızda link/adres/kullanıcı adı olabilecek harfler algılandı. Lütfen başka fotoğraf ekleyiniz", Toast.LENGTH_LONG).show();
                }
                else if(text.getText().length() > 10){
                    YuklemeIslemi(Uri.parse(uri),true,text.getText().substring(10));
                }
                else{
                    YuklemeIslemi(Uri.parse(uri),false,"");
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                //Toast.makeText(ProfilActivity.this, ""+e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
            }
        });

    }

    private void YuklemeIslemi(Uri uri, boolean saibe, String yazi){

        Dialog dialog = new Dialog(ProfilActivity.this);
        dialog.setContentView(R.layout.dialog_dizayn_loading);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setCancelable(false);
        TextView baslik = dialog.findViewById(R.id.baslik);
        TextView aciklama = dialog.findViewById(R.id.aciklama);
        ProgressBar pbar = dialog.findViewById(R.id.pbar);
        pbar.getIndeterminateDrawable().setColorFilter(ortaRenk,android.graphics.PorterDuff.Mode.MULTIPLY);
        baslik.setText("Yükleniyor...");
        aciklama.setText("Fotoğrafınız yükleniyor, birkaç saniye sürebilir");

        dialog.show();

        ContextWrapper cw = new ContextWrapper(ProfilActivity.this);
        File directory = cw.getDir("benim_resimler", MODE_PRIVATE);
        File image = null;

        image = new File(directory, ("foto_"+deger+".jpg"));

        if (image.exists()) {
            image.delete();
            //Toast.makeText(ProfilActivity.this, "Fotoğrafınız değiştirildi.", Toast.LENGTH_SHORT).show();
        }
        StorageReference referencefpp = mStorageRef.child("foto_" + deger + ".jpg");
        UploadTask uploadTask = referencefpp.putFile(uri);
        uploadTask.addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                StorageReference sref = FirebaseStorage.getInstance().getReference(fuser.getUid());
                sref.child("foto_" + deger + ".jpg").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(final Uri uri) {
                        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("usersF").child(fuser.getUid());
                        if (deger.equals("kapak")) {
                            Glide.with(mActivity).asBitmap().load(uri.toString()).into(kapak_sec);
                            DatabaseReference ref2 = FirebaseDatabase.getInstance().getReference("ppler");
                            ref2.child(fuser.getUid()).child("kapak").setValue(ServerValue.TIMESTAMP);
                        }
                        if (!eklevesil) {
                            ref.child("fotograflarim").child(deger).setValue(uri.toString()).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(@NonNull Void unused) {
                                    if (saibe) {
                                        SaibeEkle(dialog, uri, yazi);
                                    } else {
                                        dialog.dismiss();
                                        deger = "";
                                        FotolariGetir();
                                    }
                                }
                            });

                        }
                        else {
                            ref.child("fotograflarim").addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    String degistirilenFotoUrl = dataSnapshot.child(deger).getValue(String.class);
                                    ref.child("fotograflarim").child(deger).setValue(uri.toString()).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                if (deger.equals("pp")) {
                                                    DatabaseReference ref = FirebaseDatabase.getInstance().getReference("usersF").child(fuser.getUid());
                                                    ref.child("pp_url").setValue(uri.toString());

                                                    DatabaseReference ref2 = FirebaseDatabase.getInstance().getReference("ppler");
                                                    ref2.child(fuser.getUid()).child("pp").setValue(ServerValue.TIMESTAMP);
                                                }
                                                SaibeVarsaSil(saibe,degistirilenFotoUrl,uri,yazi);
                                                dialog.dismiss();
                                                deger = "";
                                                FotolariGetir();

                                            }
                                        }
                                    });
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });

                        }
                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(ProfilActivity.this, "Yükleme Başarısız!", Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            }
        });
    }

    private void SaibeVarsaSil(boolean saibe, String degistirilenFotoUrl, Uri uri, String yazi) {
        DatabaseReference saibeRef = FirebaseDatabase.getInstance().getReference("SaibeliFotolar");
        saibeRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot2) {
                if (dataSnapshot2.exists()) {
                    if (dataSnapshot2.hasChild(fuser.getUid())) {
                        DataSnapshot asilData = dataSnapshot2.child(fuser.getUid());
                        for (DataSnapshot ds : asilData.getChildren()) {
                            if (ds.child("url").getValue(String.class).equals(degistirilenFotoUrl)) {
                                if(saibe){
                                    ds.child("url").getRef().setValue(uri.toString());
                                    ds.child("zaman").getRef().setValue(ServerValue.TIMESTAMP);
                                    ds.child("saibe_sebebi").getRef().setValue("yazı algılandı");
                                    ds.child("delil").getRef().setValue(yazi);
                                }
                                else{
                                    ds.getRef().removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(@NonNull Void aVoid) {
                                        }
                                    });
                                }
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

    private void SaibeEkle(Dialog progressDialog, Uri uri, String yazi) {
        final DatabaseReference[] sRef = {FirebaseDatabase.getInstance().getReference("SaibeliFotolar")};
        sRef[0].addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    if (dataSnapshot.hasChild(fuser.getUid())) {
                        DataSnapshot asilData = dataSnapshot.child(fuser.getUid());
                        asilData.child(bosOlanSaibeDegeri(asilData,0)).child("url").getRef().setValue(uri.toString());
                        asilData.child(bosOlanSaibeDegeri(asilData,0)).child("zaman").getRef().setValue(ServerValue.TIMESTAMP);
                        asilData.child(bosOlanSaibeDegeri(asilData,0)).child("saibe_sebebi").getRef().setValue("yazı algılandı");
                        asilData.child(bosOlanSaibeDegeri(asilData,0)).child("delil").getRef().setValue(yazi);
                    } else {
                        sRef[0].child(fuser.getUid()).child("0").child("url").setValue(uri.toString());
                        sRef[0].child(fuser.getUid()).child("0").child("zaman").setValue(ServerValue.TIMESTAMP);
                        sRef[0].child(fuser.getUid()).child("0").child("saibe_sebebi").setValue("yazı algılandı");
                        sRef[0].child(fuser.getUid()).child("0").child("delil").setValue(yazi);
                    }
                    progressDialog.dismiss();
                    deger = "";
                    FotolariGetir();
                } else {
                    sRef[0].child(fuser.getUid()).child("0").child("url").setValue(uri.toString());
                    sRef[0].child(fuser.getUid()).child("0").child("zaman").setValue(ServerValue.TIMESTAMP);
                    sRef[0].child(fuser.getUid()).child("0").child("saibe_sebebi").setValue("yazı algılandı");
                    sRef[0].child(fuser.getUid()).child("0").child("delil").setValue(yazi);
                    progressDialog.dismiss();
                    deger = "";
                    FotolariGetir();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private String bosOlanSaibeDegeri(DataSnapshot saibeliFotolar, int a) {
        if(saibeliFotolar.hasChild(String.valueOf(a))) {
            a++;
            if(saibeliFotolar.hasChild(String.valueOf(a))) {
                a++;
                if(saibeliFotolar.hasChild(String.valueOf(a))) {
                    a++;
                    if(saibeliFotolar.hasChild(String.valueOf(a))) {
                        a++;
                        if(saibeliFotolar.hasChild(String.valueOf(a))) {

                        }
                        else
                            return String.valueOf(a);
                    }
                    else
                        return String.valueOf(a);
                }
                else
                    return String.valueOf(a);
            }
            else
                return String.valueOf(a);
        }
        else
            return String.valueOf(a);

        return String.valueOf(a);
    }

    private void ResimeNeOlsun() {
        AlertDialog.Builder builder = new AlertDialog.Builder(ProfilActivity.this);
        builder.setCancelable(true);
        String[] secenekler = {"Resmi Görüntüle", "Resmi Değiştir", "Resmi Kaldır"};


        builder.setItems(secenekler, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                butonTiklandiMi = false;
                if(which == 0){
                        if (deger.equals("1")) {
                            Intent intent = new Intent(ProfilActivity.this, ResimActivity.class);
                            intent.putExtra("userid", fuser.getUid());
                            intent.putExtra("resimURL", uf1.toString());
                            intent.putExtra("resimDeger", "1");
                            intent.putExtra("resimBenimMi", "evet");
                            startActivity(intent);
                        } else if (deger.equals("2")) {
                            Intent intent = new Intent(ProfilActivity.this, ResimActivity.class);
                            intent.putExtra("userid", fuser.getUid());
                            intent.putExtra("resimURL", uf2.toString());
                            intent.putExtra("resimDeger", "2");
                            intent.putExtra("resimBenimMi", "evet");
                            startActivity(intent);
                        } else if (deger.equals("3")) {
                            Intent intent = new Intent(ProfilActivity.this, ResimActivity.class);
                            intent.putExtra("userid", fuser.getUid());
                            intent.putExtra("resimURL", uf3.toString());
                            intent.putExtra("resimDeger", "3");
                            intent.putExtra("resimBenimMi", "evet");
                            startActivity(intent);
                        } else if (deger.equals("4")) {
                            Intent intent = new Intent(ProfilActivity.this, ResimActivity.class);
                            intent.putExtra("userid", fuser.getUid());
                            intent.putExtra("resimURL", uf4.toString());
                            intent.putExtra("resimDeger", "4");
                            intent.putExtra("resimBenimMi", "evet");
                            startActivity(intent);
                        } else if (deger.equals("5")) {
                            Intent intent = new Intent(ProfilActivity.this, ResimActivity.class);
                            intent.putExtra("userid", fuser.getUid());
                            intent.putExtra("resimURL", uf5.toString());
                            intent.putExtra("resimDeger", "5");
                            intent.putExtra("resimBenimMi", "evet");
                            startActivity(intent);
                        } else if (deger.equals("6")) {
                            Intent intent = new Intent(ProfilActivity.this, ResimActivity.class);
                            intent.putExtra("userid", fuser.getUid());
                            intent.putExtra("resimURL", uf6.toString());
                            intent.putExtra("resimDeger", "6");
                            intent.putExtra("resimBenimMi", "evet");
                            startActivity(intent);
                        } else if (deger.equals("pp")) {
                            Intent intent = new Intent(ProfilActivity.this, ResimActivity.class);
                            intent.putExtra("userid", fuser.getUid());
                            intent.putExtra("resimURL", ufpp);
                            intent.putExtra("resimDeger", "pp");
                            intent.putExtra("resimBenimMi", "evet");
                            startActivity(intent);
                        }else if (deger.equals("kapak")) {
                            Intent intent = new Intent(ProfilActivity.this, ResimActivity.class);
                            intent.putExtra("userid", fuser.getUid());
                            intent.putExtra("resimURL", ufkapak.toString());
                            intent.putExtra("resimDeger", "kapak");
                            intent.putExtra("resimBenimMi", "evet");
                            startActivity(intent);
                        }
                }
                else if(which == 1){
                    if(!deger.equals("0") && !deger.equals("")){
                        eklevesil = true;
                        FotoEkle();
                    }
                }
                else if(which == 2) {
                    if(!deger.equals("pp")) {
                        Dialog dialog2 = new Dialog(ProfilActivity.this);
                        dialog2.setContentView(R.layout.dialog_dizayn_loading);
                        dialog2.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                        dialog2.setCancelable(false);
                        TextView baslik = dialog2.findViewById(R.id.baslik);
                        TextView aciklama = dialog2.findViewById(R.id.aciklama);
                        ProgressBar pbar = dialog2.findViewById(R.id.pbar);
                        pbar.getIndeterminateDrawable().setColorFilter(ortaRenk,android.graphics.PorterDuff.Mode.MULTIPLY);
                        baslik.setText("Siliniyor...");
                        aciklama.setText("Fotoğrafınız siliniyor, birkaç saniye sürebilir");

                        dialog2.show();

                        String URL = "" + fuser.getUid() + "/foto_" + deger + ".jpg";
                        DatabaseReference ref = null;

                        ref = FirebaseDatabase.getInstance().getReference("usersF").child(fuser.getUid()).child("fotograflarim").child(deger);
                        ref.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                String uri = dataSnapshot.getValue(String.class);
                                SaibeVarsaSil(false,uri,Uri.parse(""),"");
                                dataSnapshot.getRef().removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(@NonNull Void unused) {
                                        StorageReference storageReference = FirebaseStorage.getInstance().getReference(URL);
                                        storageReference.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(@NonNull Void unused) {
                                                dialog2.dismiss();
                                                deger = "";
                                                FotolariGetir();
                                            }
                                        });
                                    }
                                });



                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
                    }else{
                        Toast.makeText(ProfilActivity.this, "Profil resminizi kaldıramazsınız", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        builder.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                butonTiklandiMi = false;
            }
        });
        AlertDialog dialog = builder.create();
        dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                butonTiklandiMi = false;
            }
        });
        dialog.show();
    }

    private static GradientDrawable gradientDrawable(String tasDegeri) {
        GradientDrawable gradientBackground =  new GradientDrawable();

        gradientBackground.setOrientation(GradientDrawable.Orientation.LEFT_RIGHT);
        gradientBackground.setCornerRadius(50);

        int renk1 = 0,renk2 = 0,t1start = 0,t2start = 0,t1end = 0,t2end = 0,orta = 0;


        renk1 = TasarimRenginiGetir.RengiGetir(mContext,"renk1",tasDegeri);
        renk2 = TasarimRenginiGetir.RengiGetir(mContext,"renk2",tasDegeri);
        t1start = TasarimRenginiGetir.RengiGetir(mContext,"t1start",tasDegeri);
        t2start = TasarimRenginiGetir.RengiGetir(mContext,"t2start",tasDegeri);
        t1end = TasarimRenginiGetir.RengiGetir(mContext,"t1end",tasDegeri);
        t2end = TasarimRenginiGetir.RengiGetir(mContext,"t2end",tasDegeri);
        orta = TasarimRenginiGetir.RengiGetir(mContext,"orta",tasDegeri);
        gradientBackground.setColors(new int[]{
                renk1,
                orta,
                renk2
        });

        return gradientBackground;
    }
    private void BilgileriIste() {
        try {
            final SharedPreferences spref = getSharedPreferences("KisiselBilgiler",MODE_PRIVATE);
            if(internetVarmi()) {
                final DatabaseReference bilgiler = FirebaseDatabase.getInstance().getReference("usersF").child(fuser.getUid());
                bilgiler.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull final DataSnapshot dataSnapshot) {
                        AsilDataSnapShot = dataSnapshot;
                        Kullanici = dataSnapshot.getValue(User.class);
                        FotolariGetir();

                        FotoKilitleriniKontrolEt(dataSnapshot);


                        TasarimDegeriArayuz = Kullanici.getTas_arayuz();
                        TasarimDegeriProfil = Kullanici.getTas_profil();
                        TasarimDegeriMesaj = Kullanici.getTas_mesaj();
                        TasarimMesajSahipOlduklarim = Kullanici.getTas_mesaj_sahibim();
                        TasarimProfilSahipOlduklarim = Kullanici.getTas_profil_sahibim();
                        TasarimArayuzSahipOlduklarim = Kullanici.getTas_arayuz_sahibim();
                        tas_profil_view.setBackground(gradientDrawable(TasarimDegeriProfil));
                        tas_arayuz_view.setBackground(gradientDrawable(TasarimDegeriArayuz));
                        tas_mesaj_view.setBackground(gradientDrawable(TasarimDegeriMesaj));


                        if(AsilDataSnapShot != null)
                        if (AsilDataSnapShot.hasChild("rozetlerim")) {
                            rozetlerim = AsilDataSnapShot.child("rozetlerim").getValue(String.class);
                            if (AsilDataSnapShot.child("rozet").getValue(String.class).equals("1")) {
                                rozet.setImageDrawable(getResources().getDrawable(R.drawable.rozet1));
                                rozet_view_arka.setBackground(getResources().getDrawable(R.drawable.rozet1_arka));
                            }
                            if (AsilDataSnapShot.child("rozet").getValue(String.class).equals("2")) {
                                rozet.setImageDrawable(getResources().getDrawable(R.drawable.rozet2));
                                rozet_view_arka.setBackground(getResources().getDrawable(R.drawable.rozet2_arka));
                            }
                            if (AsilDataSnapShot.child("rozet").getValue(String.class).equals("3")) {
                                rozet.setImageDrawable(getResources().getDrawable(R.drawable.rozet3));
                                rozet_view_arka.setBackground(getResources().getDrawable(R.drawable.rozet3_arka));
                            }
                            if (AsilDataSnapShot.child("rozet").getValue(String.class).equals("4")) {
                                rozet.setImageDrawable(getResources().getDrawable(R.drawable.rozet4));
                                rozet_view_arka.setBackground(getResources().getDrawable(R.drawable.rozet4_arka));
                            }
                            if (AsilDataSnapShot.child("rozet").getValue(String.class).equals("5")) {
                                rozet.setImageDrawable(getResources().getDrawable(R.drawable.rozet5));
                                rozet_view_arka.setBackground(getResources().getDrawable(R.drawable.rozet5_arka));
                            }
                            if (AsilDataSnapShot.child("rozet").getValue(String.class).equals("6")) {
                                rozet.setImageDrawable(getResources().getDrawable(R.drawable.rozet6));
                                rozet_view_arka.setBackground(getResources().getDrawable(R.drawable.rozet6_arka));
                            }
                        }


                        SharedPreferences.Editor seditor = spref.edit();
                        if (Kullanici.getCinsiyet().equals("Erkek")) {
                            seditor.putString("Cinsiyet","Erkek");
                            cinsiyet.setVisibility(View.VISIBLE);
                            cinsiyet2.setVisibility(View.GONE);
                            yas_arka.setColorFilter(getResources().getColor(R.color.gradient2));
                        } else {
                            seditor.putString("Cinsiyet","Kadin");
                            cinsiyet2.setVisibility(View.VISIBLE);
                            cinsiyet.setVisibility(View.GONE);
                            yas_arka.setColorFilter(getResources().getColor(R.color.favori_arka));
                        }
                        adTV.setHint("İsim: " + Kullanici.getAd().substring(0,1).toUpperCase() + Kullanici.getAd().substring(1).toLowerCase());
                        usnameButton.setText("Kullanıcı adı: "+Kullanici.getUsernamef());
                        if(dataSnapshot.child("kapak").getValue(String.class).equals("evet"))
                            kapakDegistirebilirMi = true;
                        else
                            kapakDegistirebilirMi = false;
                        //BegeniHesapla();
                        kullanici_adi.setText("@" + Kullanici.getUsernamef());
                        if(!spref.getString("Uname","").equals(Kullanici.getUsernamef()))
                            seditor.putString("Uname",Kullanici.getUsernamef());
                        ad.setText(Kullanici.getAd().substring(0, 1).toUpperCase() + Kullanici.getAd().substring(1).toLowerCase());
                        if(!spref.getString("Ad","").equals(Kullanici.getAd()))
                            seditor.putString("Ad",Kullanici.getAd());
                        yas.setText(Kullanici.getDg());
                        if(!spref.getString("Yas","").equals(Kullanici.getDg()))
                            seditor.putString("Yas",Kullanici.getDg());

                        seditor.commit();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                    }
                });
            }else{
                String cin = spref.getString("Cinsiyet","");
                String Ad = spref.getString("Ad","");
                String Yas = spref.getString("Yas","");
                String uname = spref.getString("Uname","");
                ad.setText(Ad.substring(0, 1).toUpperCase() + Ad.substring(1).toLowerCase());
                yas.setText(Yas);
                kullanici_adi.setText("@" + uname);
                if (cin.equals("Erkek")) {
                    cinsiyet.setVisibility(View.VISIBLE);
                    cinsiyet2.setVisibility(View.GONE);
                    yas_arka.setColorFilter(getResources().getColor(R.color.gradient2));
                }
                else {
                    cinsiyet2.setVisibility(View.VISIBLE);
                    cinsiyet.setVisibility(View.GONE);
                    yas_arka.setColorFilter(getResources().getColor(R.color.favori_arka));
                }
            }
            Toolbar toolbar = findViewById(R.id.pprofiltoolbar);
            getSupportActionBar().setHomeAsUpIndicator(getResources().getDrawable(R.drawable.ic_mesaj_geri2));
            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(ProfilActivity.this,TaraActivity.class);
                    startActivity(intent);
                    //finish();
                    overridePendingTransition(R.anim.slide_in_left,R.anim.slide_out_right);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
    private void BildirimKontrol() {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("usersF").child(fuser.getUid());
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.child("begeni_bildirim_durumu").getValue(String.class).equals("var"))
                    begeniBildirim.setVisibility(View.VISIBLE);
                else
                    begeniBildirim.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
    public void ProfilDuzenle(View view){
        //profili_kaydet.setVisibility(View.VISIBLE);
        edittext_layout.setVisibility(View.VISIBLE);
        diptal.setVisibility(View.VISIBLE);
        foto_layout.setVisibility(View.GONE);
        profili_duzenle.setVisibility(View.GONE);
        begenenlerRel.setVisibility(View.GONE);

        if(AsilDataSnapShot != null)
            if (AsilDataSnapShot.hasChild("rozetlerim"))
                rozetLay.setVisibility(View.VISIBLE);
            else
                rozetLay.setVisibility(View.GONE);


        ContextWrapper cw = new ContextWrapper(ProfilActivity.this);
        String dosyaParent = "benim_resimler";
        String dosyaAdi = "foto_kapak.jpg";
        File directory = cw.getDir(dosyaParent, MODE_PRIVATE);
        File image = new File(directory, dosyaAdi);

        if(kapakVarmi)
        if (image.exists())
            kapak_sec.setImageURI(Uri.parse(image.getAbsolutePath()));
        else {
            Glide.with(ProfilActivity.this).asBitmap().load(ufkapak).into(kapak_sec);
            new ResimIndir(getApplicationContext(), ufkapak, dosyaParent, dosyaAdi);
        }

        kapakVarmi = true;
    }
    public void SehirSec(){
        final String[] sehirler = {"Adana","Adıyaman", "Afyon", "Ağrı", "Amasya", "Ankara", "Antalya", "Artvin",
                "Aydın", "Balıkesir","Bilecik", "Bingöl", "Bitlis", "Bolu", "Burdur", "Bursa", "Çanakkale",
                "Çankırı", "Çorum","Denizli","Diyarbakır", "Edirne", "Elazığ", "Erzincan", "Erzurum ", "Eskişehir",
                "Gaziantep", "Giresun","Gümüşhane", "Hakkari", "Hatay", "Isparta", "Mersin", "İstanbul", "İzmir",
                "Kars", "Kastamonu", "Kayseri","Kırklareli", "Kırşehir", "Kocaeli", "Konya", "Kütahya ", "Malatya",
                "Manisa", "Kahramanmaraş", "Mardin", "Muğla", "Muş", "Nevşehir", "Niğde", "Ordu", "Rize", "Sakarya",
                "Samsun", "Siirt", "Sinop", "Sivas", "Tekirdağ", "Tokat", "Trabzon  ", "Tunceli", "Şanlıurfa", "Uşak",
                "Van", "Yozgat", "Zonguldak", "Aksaray", "Bayburt ", "Karaman", "Kırıkkale", "Batman", "Şırnak",
                "Bartın", "Ardahan", "Iğdır", "Yalova", "Karabük ", "Kilis", "Osmaniye ", "Düzce"};
        final AlertDialog.Builder alert = new AlertDialog.Builder(ProfilActivity.this);
        View view = getLayoutInflater().inflate(R.layout.dialog_dizayn,null);
        alert.setView(view);
        alert.setTitle("Şehrinizi Seçiniz");
        alert.setCancelable(true);
        alert.setSingleChoiceItems(sehirler,Integer.valueOf(Kullanici.getSehir())-1, new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int i)
            {
                butonTiklandiMi = false;
                DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Yerler");
                reference.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.hasChild("" + (i + 1))) {
                            sehirNo = i + 1;
                            sehir_degis.setText(sehirler[i]);
                            if(sehirNo != Integer.valueOf(Kullanici.getSehir())) {
                                degisiklikSayisi++;
                                DegisiklikVarmi();
                            }
                            dialog.dismiss();
                        } else {
                            Toast.makeText(ProfilActivity.this, "Kimoo kullanıcı sayısı şehrinizde çok az olduğundan kullanmanızı tavsiye etmiyoruz. Lütfen şehrinizdeki çalışmalarımızın bitmesini bekleyin", Toast.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }
        });

        alert.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                butonTiklandiMi = false;
            }
        });
        alert.show();
    }
    private String getAge(int year, int month, int day){//yaş hesaplama
        Calendar dob = Calendar.getInstance();
        Calendar today = Calendar.getInstance();

        dob.set(year, month, day);

        int age = today.get(Calendar.YEAR) - dob.get(Calendar.YEAR);

        if (today.get(Calendar.DAY_OF_YEAR) < dob.get(Calendar.DAY_OF_YEAR)){
            age--;
        }
        String ageS = "";
        if(age >= 15)
            ageS = "" + age;
        else
            ageS = "15 Yaşından Küçüksünüz";

        return ageS;
    }

    private void DegisiklikleriKontrolEt() {
        butonTiklandiMi = false;
        if(!adTV.getText().toString().trim().equals("") && adTV.getText() != null){
            if(adTV.getText().toString().trim().length() != 1) {
                if(adTV.getText().toString().trim().length() < 12) {
                    String hata = "";
                    int varDegeri = 0;
                    int sesli = 0;
                    int sessiz = 0;
                    String birOncekiHarf = "";
                    List<String> sesliHarfler = Arrays.asList("a", "e", "ı", "i", "o", "ö", "u", "ü");
                    List<String> sessizHarfler = Arrays.asList("f", "r", "t", "y", "p", "ğ", "s", "d", "g", "h", "j", "k", "l", "ş", "z", "c", "v", "b", "n", "m", "ç");

                    for (int i = 0; i < adTV.getText().toString().length(); i++) {
                        char harf = adTV.getText().toString().toLowerCase().charAt(i);
                        String ad = String.valueOf(harf);

                        if (!birOncekiHarf.equals("m") && !birOncekiHarf.equals("r") &&  ad.equals(birOncekiHarf)) {
                            varDegeri++;
                            hata = "İsminizde 2 tane ard arda gelen aynı harf var. Lütfen sadece 1 tanesini yazın";
                        }

                        if (i == 0 && ad.equals("ğ")) {
                            varDegeri++;
                            hata = "İsminiz Ğ harfi ile başlayamaz";
                        }

                        if (sesliHarfler.contains(ad)) {
                            sesli++;
                            sessiz = 0;
                            if (sesli >= 3) {
                                varDegeri++; // yanyana 3 sesli harfin olduğu anlamına gelir
                                hata = "İsminizde çok fazla arka arkaya gelen sesli harf var";
                            }
                        } else {
                            sessiz++;
                            sesli = 0;
                            if (sessiz >= 3) {
                                varDegeri++; // yanyana 3 sessiz harfin olduğu anlamına gelir
                                hata = "İsminizde çok fazla arka arkaya sessiz harf var";
                            }
                        }

                        if (ad.matches("\\p{Punct}") || ad.matches("\\d+(?:\\.\\d+)?")) {
                            varDegeri++;
                            hata = "İsminizde noktalama işareti veya rakam bulunamaz";
                        }
                        if(ad.equals("q") || ad.equals("x") || ad.equals("w")){
                            varDegeri++;
                            hata = "Eğer isminizde Türkçe karakterlerin dışında bir harf varsa bu harfi değiştirmelisiniz. Örnek: Q-K, W-V, X-Z";
                        }


                        birOncekiHarf = ad; // 2 tane aynı harf arka arkaya yazılamaz
                    }
                    if(varDegeri > 0){
                        Toast.makeText(this, hata, Toast.LENGTH_LONG).show();
                    }
                    else{
                        DigerAyarlariKontrolEt();
                    }
                }
                else
                    Toast.makeText(this, "İsim çok uzun. Birden fazla isminiz varsa lütfen bir tanesini yazınız", Toast.LENGTH_SHORT).show();
            }
            else
                Toast.makeText(this, "İsim çok kısa", Toast.LENGTH_SHORT).show();
        }
        else{
            DigerAyarlariKontrolEt();
        }
    }

    private void DigerAyarlariKontrolEt(){
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("usersF").child(fuser.getUid());

        if(!adTV.getText().toString().trim().equals("") && adTV.getText() != null)
            ref.child("ad").setValue(adTV.getText().toString().trim().toLowerCase());

        if(!sehir_degis.getText().toString().equals("Şehir") && sehir_degis.getText() != null){
            if (sehirNo != 0)
                ref.child("sehir").setValue(""+sehirNo);
        }
        if(!yeniKullaniciAdi.equals("")){
            if(yeniKullaniciAdi.length() > 5){
                Query usernameQuery = FirebaseDatabase.getInstance().getReference("usersF").orderByChild("usernamef").equalTo(yeniKullaniciAdi);
                usernameQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if(dataSnapshot.getChildrenCount() > 0){
                            Toast.makeText(ProfilActivity.this, "Bu kullanıcı adı zaten var!", Toast.LENGTH_SHORT).show();
                        }else{
                            DatabaseReference ref = FirebaseDatabase.getInstance().getReference("usersF").child(fuser.getUid());
                            ref.child("usernamef").setValue(yeniKullaniciAdi).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(@NonNull Void unused) {
                                    FirebaseDatabase.getInstance().getReference("OzelKAdiAlanlar").child(fuser.getUid()).child("yeni_kadi").setValue(yeniKullaniciAdi);
                                    FirebaseDatabase.getInstance().getReference("OzelKAdiAlanlar").child(fuser.getUid()).child("eski_kadi").setValue(Kullanici.getUsernamef());
                                    FirebaseDatabase.getInstance().getReference("OzelKAdiAlanlar").child(fuser.getUid()).child("zaman").setValue(ServerValue.TIMESTAMP).addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(@NonNull Void unused) {
                                            yeniKullaniciAdi = "";
                                            Toast.makeText(ProfilActivity.this, "Değişiklikler kaydedildi", Toast.LENGTH_SHORT).show();
                                            edittext_layout.setVisibility(View.GONE);
                                            foto_layout.setVisibility(View.VISIBLE);
                                            adTV.setText("");
                                            usnameButton.setText("");
                                            sehir_degis.setText("Şehir");
                                            diptal.setVisibility(View.GONE);
                                            profili_duzenle.setVisibility(View.VISIBLE);
                                            begenenlerRel.setVisibility(View.VISIBLE);
                                            profili_kaydet.clearAnimation();
                                            profili_kaydet.setVisibility(View.GONE);
                                            BilgileriIste();
                                        }
                                    });
                                }
                            });
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }else{
                // 6 karakterden fazla olmalı
            }
        }
        else{
            if(adTV.getText().toString().trim().equals("") &&
                    sehir_degis.getText().toString().trim().equals("Şehir")
                    && !kapakVarmi)
                Toast.makeText(ProfilActivity.this, "Bir değişiklik yapılmadı", Toast.LENGTH_SHORT).show();
            else
                Toast.makeText(ProfilActivity.this, "Değişiklikler kaydedildi", Toast.LENGTH_SHORT).show();
            edittext_layout.setVisibility(View.GONE);
            foto_layout.setVisibility(View.VISIBLE);
            adTV.setText("");
            usnameButton.setText("");
            sehir_degis.setText("Şehir");
            profili_duzenle.setVisibility(View.VISIBLE);
            diptal.setVisibility(View.GONE);
            begenenlerRel.setVisibility(View.VISIBLE);
            profili_kaydet.clearAnimation();
            profili_kaydet.setVisibility(View.GONE);
            if(kapakVarmi) {

            }
            else{
                kapak.setImageDrawable(null);
                kapak.setBackgroundColor(getResources().getColor(R.color.siyah));
            }
            BilgileriIste();
        }

    }
}
