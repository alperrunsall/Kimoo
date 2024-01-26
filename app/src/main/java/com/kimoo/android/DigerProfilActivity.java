package com.kimoo.android;

import android.app.Activity;
import android.app.Dialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.VectorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Build;
import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;

import android.text.method.ScrollingMovementMethod;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.google.android.gms.tasks.OnFailureListener;
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
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.kimoo.android.Model.Fiyatlar;
import com.kimoo.android.Model.User;
import com.kimoo.android.bildirimler.APIService;
import com.kimoo.android.bildirimler.Client;
import com.kimoo.android.bildirimler.Data;
import com.kimoo.android.bildirimler.MyResponse;
import com.kimoo.android.bildirimler.Sender;
import com.kimoo.android.bildirimler.Token;
import com.kimoo.android.extra.ResimIndir;
import com.kimoo.android.extra.TasarimRenginiGetir;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.UUID;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;


public class DigerProfilActivity extends AppCompatActivity {

    Button mesaj_at, begen;
    TextView foto_yok,ad,soyad,yas,kullanici_adi,begeni_sayisi,encokbegenenisim,encokbegenensayisi;
    ImageView foto_1,foto_2,foto_3,foto_4,foto_5,foto_6,favorilere_ekle,favorilere_ekle2,cinsiyet,cinsiyet2,kapak,rozet_view_arka;
    CircleImageView foto_pp,encokbegenenfoto,yas_arka;
    private RelativeLayout encokbegenenkismi,background;
    FirebaseUser fuser;
    User enCokBegenen,user;
    private int ortaRenk;
    private boolean butonTiklandiMi = false;
    String kiminmis,kiminmisUname,begenebilmeDurumu = "",deger,kullaniciAdi;
    int begeniSayisi;
    DataSnapshot dataSnapshotAsil;
    public static boolean begendim = false;
    boolean basildi,ppyuklendimi,saniyeDevam,engelDurumuVarmi = false;
    boolean r1varmi,r2varmi,r3varmi,r4varmi,r5varmi,r6varmi,eklevesil,kapakvarmi;
    long bulunanDeger;
    long childSayisi,zaman,son,suan;
    Thread saniyeSay;
    User Kullanici;
    ProgressBar pbar;
    LinearLayout anaRel;
    private static Fiyatlar fiyatlar;
    ArrayList<String> begeniler = new ArrayList<>(),fotolar = new ArrayList<>(),fotoRefs = new ArrayList<>(),engellediklerim = new ArrayList<>();
    Uri uf1,uf2,uf3,uf4,uf5,uf6,ufpp,resim,ufkapak;
    DatabaseReference begeniRef;
    APIService apiService;
    boolean notify;
    private StorageReference mStorageRef;
    private String sebep;
    ArrayList<ImageView> fotoViewList2;
    private boolean kilitAcikMi;
    private File ppDosyasi;
    private View kaybolacakView1, kaybolacakView3, kaybolacakView4, gorunecekView2, kaybolacakView6;
    private RelativeLayout kaybolacakView2, kaybolacakView5;
    private LinearLayout gorunecekView1;
    private ImageView admin_ic;
    private boolean yonetici;

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
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_diger_profil);
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

        kaybolacakView1 = findViewById(R.id.kaybolacakView1);
        kaybolacakView2 = findViewById(R.id.kaybolacakView2);
        kaybolacakView3 = findViewById(R.id.kaybolacakView3);
        kaybolacakView4 = findViewById(R.id.kaybolacakView4);
        kaybolacakView5 = findViewById(R.id.kaybolacakView5);
        kaybolacakView6 = findViewById(R.id.kaybolacakView6);
        gorunecekView1 = findViewById(R.id.gorunecekView1);
        gorunecekView2 = findViewById(R.id.gorunecekView2);
        admin_ic = findViewById(R.id.admin_ic);

        kiminmis = getIntent().getStringExtra("userid");
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Sistem");
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                fiyatlar = dataSnapshot.child("fiyatlar").getValue(Fiyatlar.class);
                if (dataSnapshot.child("yoneticiler").hasChild(kiminmis)){
                    yonetici = true;
                    kaybolacakView1.setVisibility(View.GONE);
                    kaybolacakView2.setVisibility(View.GONE);
                    kaybolacakView3.setVisibility(View.GONE);
                    kaybolacakView4.setVisibility(View.GONE);
                    kaybolacakView5.setVisibility(View.GONE);
                    kaybolacakView6.setVisibility(View.GONE);
                    gorunecekView1.setVisibility(View.VISIBLE);
                    gorunecekView2.setVisibility(View.VISIBLE);
                    admin_ic.setColorFilter(Color.parseColor("#fff565"));
                }
                if (dataSnapshot.child("yoneticiler").hasChild(fuser.getUid())) {
                    yonetici = true;
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        Toolbar toolbar = findViewById(R.id.pprofiltoolbar);
        toolbar.setTitleTextColor(getResources().getColor(R.color.colorPrimary));
        toolbar.setOverflowIcon(getResources().getDrawable(R.drawable.ic_dots));
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
        apiService = Client.getClient("https://fcm.googleapis.com/").create(APIService.class);

        mesaj_at = findViewById(R.id.mesaj_at_btn);
        begen = findViewById(R.id.begen_btn);
        kapak = findViewById(R.id.kapak);
        favorilere_ekle = findViewById(R.id.dprofil_favori_btn2);
        favorilere_ekle2 = findViewById(R.id.dprofil_favori_btn);
        background = findViewById(R.id.background);
        foto_1 = findViewById(R.id.profil_foto_1);
        foto_2 = findViewById(R.id.profil_foto_2);
        foto_3 = findViewById(R.id.profil_foto_3);
        foto_4 = findViewById(R.id.profil_foto_4);
        foto_5 = findViewById(R.id.profil_foto_5);
        foto_6 = findViewById(R.id.profil_foto_6);
        yas_arka = findViewById(R.id.yas_arka);
        ad = findViewById(R.id.isim_soyisim);
        yas = findViewById(R.id.yas);
        cinsiyet = findViewById(R.id.cinsiyet_arka);
        cinsiyet2 = findViewById(R.id.cinsiyet_arka2);
        foto_pp = findViewById(R.id.profil_pp);
        foto_yok = findViewById(R.id.phic_foto_yok);
        begeni_sayisi = findViewById(R.id.toplambegenisayisi);
        rozet_view_arka = findViewById(R.id.rozet_view_arka);
        begenebilmeDurumu = "begenilemez";
        encokbegenenfoto = findViewById(R.id.encokbegenenfoto);
        encokbegenensayisi = findViewById(R.id.encokbegenensayisi);
        encokbegenenkismi = findViewById(R.id.encokbegenenkismi);
        anaRel = findViewById(R.id.anaLay);
        pbar = findViewById(R.id.pbar);

        fuser = FirebaseAuth.getInstance().getCurrentUser();
        if (kiminmis.equals(fuser.getUid()))
            startActivity(new Intent(DigerProfilActivity.this,ProfilActivity.class));


        /*SharedPreferences tas_shared = getSharedPreferences("TasarimBilgileri",MODE_PRIVATE);
        TasarimDegistir(tas_shared.getString("tasarim_arayuz","1"));*/

        //Resimler varsa yükle.
        mStorageRef = FirebaseStorage.getInstance().getReference(kiminmis);
        //Profil Fotoğrafı
        if(internetVarmi()) {
            foto_1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!butonTiklandiMi) {
                        butonTiklandiMi = true;
                        if (r1varmi) {
                            deger = "1";
                            ResimeNeOlsun();
                        }
                    }

                }
            });
            foto_2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!butonTiklandiMi) {
                        butonTiklandiMi = true;
                        if (r2varmi) {
                            deger = "2";
                            ResimeNeOlsun();
                        }
                    }
                }
            });
            foto_3.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!butonTiklandiMi) {
                        butonTiklandiMi = true;
                        if (r3varmi) {
                            deger = "3";
                            ResimeNeOlsun();
                        }
                    }
                }
            });
            foto_4.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!butonTiklandiMi) {
                        butonTiklandiMi = true;
                        if (r4varmi) {
                            deger = "4";
                            ResimeNeOlsun();
                        }
                    }
                }
            });
            foto_5.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!butonTiklandiMi) {
                        butonTiklandiMi = true;
                        if (r5varmi) {
                            deger = "5";
                            ResimeNeOlsun();
                        }
                    }
                }
            });
            foto_6.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!butonTiklandiMi) {
                        butonTiklandiMi = true;
                        if (r6varmi) {
                            deger = "6";
                            ResimeNeOlsun();
                        }
                    }

                }
            });
            foto_pp.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!butonTiklandiMi) {
                        butonTiklandiMi = true;
                        deger = "pp";
                        ResimeNeOlsun();
                    }
                }
            });
        }
        else{
            Toast.makeText(DigerProfilActivity.this, "İnternet Bağlantınız Yok!", Toast.LENGTH_SHORT).show();
        }
        encokbegenenkismi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(DigerProfilActivity.this, "En çok beğenen.", Toast.LENGTH_SHORT).show();
            }
        });
        //İsim, yaş, cinsiyet gibi bilgileri çağır
        KimleriEngellemisim();
        begen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    if (internetVarmi()) {
                        if (!butonTiklandiMi) {
                            butonTiklandiMi = true;
                            if( (Integer.parseInt(Kullanici.getDg()) - Integer.parseInt(user.getDg())) > -5 && (Integer.parseInt(Kullanici.getDg()) - Integer.parseInt(user.getDg())) < 5) {
                                if (!begenebilmeDurumu.equals("begenilemez")) {
                                    if (!engelDurumuVarmi) {
                                        begen.setText("Beğeniliyor...");
                                        Begen();
                                    }
                                } else {
                                    butonTiklandiMi = false;
                                    Toast.makeText(DigerProfilActivity.this, "Bir kişiyi günde sadece 1 kez beğenebilirsiniz.", Toast.LENGTH_SHORT).show();
                                }
                            }
                            else {
                                butonTiklandiMi = false;
                                Toast.makeText(DigerProfilActivity.this, "Bu kişiyle etkileşime giremezsiniz.", Toast.LENGTH_SHORT).show();
                            }
                        }
                    } else {
                        Toast.makeText(DigerProfilActivity.this, "İnternet Bağlantınız Yok!", Toast.LENGTH_SHORT).show();
                    }
            }
        });

        if(internetVarmi()) {
            favorilere_ekle.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!butonTiklandiMi) {
                        butonTiklandiMi = true;
                        favoriAyarla();
                    }
                }
            });
            favorilere_ekle2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!butonTiklandiMi) {
                        butonTiklandiMi = true;
                        favoriAyarla();
                    }
                }
            });
        }else{
            Toast.makeText(DigerProfilActivity.this, "İnternet Bağlantınız Yok!", Toast.LENGTH_SHORT).show();
        }

        /*final DatabaseReference ref = FirebaseDatabase.getInstance().getReference("usersF").child(kiminmis);
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChild("begenenler")) {
                    if(begendim) {
                        ref.child("begenenler").child(fuser.getUid()).child("suanki_zaman").setValue(ServerValue.TIMESTAMP);
                        zaman = dataSnapshot.child("begenenler").child(fuser.getUid()).child("suanki_zaman").getValue(long.class);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });*/
        /*new Timer().schedule(new TimerTask() {
                @Override
                public void run() {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if(begendim) {
                                final DatabaseReference ref = FirebaseDatabase.getInstance().getReference("usersF").child(kiminmis);
                                ref.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        if (dataSnapshot.hasChild("begenenler")) {
                                            ref.child("begenenler").child(fuser.getUid()).child("suanki_zaman").setValue(ServerValue.TIMESTAMP);
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                    }
                                });
                            }
                        }
                    });
                }
            }, 0, 1000);*/
    }

    private void TumBilgileriCagir() {
        ContextWrapper cw = new ContextWrapper(DigerProfilActivity.this);
        File directory = cw.getDir("kullanici_resimleri", MODE_PRIVATE);
        for(File files : directory.listFiles()){
            if(files.getName().substring(7,files.getName().length()-4).equals(kiminmis)){
                ppDosyasi = files;
                ppyuklendimi = true;
                foto_pp.setImageURI(Uri.parse(ppDosyasi.getAbsolutePath()));
                //Toast.makeText(DigerProfilActivity.this, "Foto var", Toast.LENGTH_SHORT).show();
            }
            //if(dosyaKontrol == directory.listFiles().length)
        }

        DatabaseReference bilgiler = FirebaseDatabase.getInstance().getReference("usersF").child(kiminmis);
        bilgiler.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                TasarimDegistir(dataSnapshot.child("tas_profil").getValue(String.class));
                if(dataSnapshot.hasChild("engellediklerim")){
                    if(!dataSnapshot.child("engellediklerim").hasChild(fuser.getUid())) {
                        if (!engellediklerim.contains(kiminmis))
                            if (dataSnapshot.child("hesap_durumu").child("durum").getValue(Integer.class) == 0)
                                BilgilerCagirilabilir(dataSnapshot);
                            else {
                                Toast.makeText(DigerProfilActivity.this, "Bu profile erişilemiyor", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(DigerProfilActivity.this, TaraActivity.class));
                            }
                    }
                    else {
                        engelDurumuVarmi = true;
                        Toast.makeText(DigerProfilActivity.this, "Bu profile erişilemiyor", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(DigerProfilActivity.this, TaraActivity.class));
                    }
                }
                else
                    if (!engellediklerim.contains(kiminmis))
                        if (dataSnapshot.child("hesap_durumu").child("durum").getValue(Integer.class) == 0)
                            BilgilerCagirilabilir(dataSnapshot);
                        else {
                            Toast.makeText(DigerProfilActivity.this, "Bu profile erişilemiyor", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(DigerProfilActivity.this, TaraActivity.class));
                        }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    private void BilgilerCagirilabilir(DataSnapshot dataSnapshot) {
        user = dataSnapshot.getValue(User.class);
        ufpp = Uri.parse(dataSnapshot.child("fotograflarim").child("pp").getValue(String.class));
        if (dataSnapshot.hasChild("fotograflarim")) {
            if (dataSnapshot.child("fotograflarim").hasChild("kapak"))
                ufkapak = Uri.parse(dataSnapshot.child("fotograflarim").child("kapak").getValue(String.class));
            else
                ufkapak = null;
        }
        else
            ufkapak = null;
        PPGetir();
        if (dataSnapshot.hasChild("begenenler")) {
            if (dataSnapshot.child("begenenler").hasChild(fuser.getUid()) && dataSnapshot.child("begenenler").child(fuser.getUid()).hasChild("son_begenme")) {
                dataSnapshot.child("begenenler").child(fuser.getUid()).child("suanki_zaman").getRef().setValue(ServerValue.TIMESTAMP);
            }
        }

        mesaj_at.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(internetVarmi() && !engelDurumuVarmi) {
                    if (!butonTiklandiMi) {
                        butonTiklandiMi = true;
                        //Toast.makeText(DigerProfilActivity.this, ""+(Integer.parseInt(Kullanici.getDg()) - Integer.parseInt(user.getDg())), Toast.LENGTH_SHORT).show();
                        if (!yonetici) {
                            if ((Integer.parseInt(Kullanici.getDg()) - Integer.parseInt(user.getDg())) > -5 && (Integer.parseInt(Kullanici.getDg()) - Integer.parseInt(user.getDg())) < 5) {
                                if (kilitAcikMi) {
                                    Intent degis = new Intent(DigerProfilActivity.this, MesajActivity.class);
                                    degis.putExtra("userid", kiminmis);
                                    startActivity(degis);
                                } else {
                                    if (dataSnapshotAsil.child("bulduklarim").child(kiminmis).exists()) {
                                        long ilkZaman = dataSnapshotAsil.child("bulduklarim").child(kiminmis).child("ilk_gordugum_zaman").getValue(Long.class);
                                        long suan = dataSnapshotAsil.child("suan").getValue(Long.class);

                                        if (dataSnapshotAsil != null) {
                                            if (suan - ilkZaman > 86400000) {
                                                Dialog dialog = new Dialog(DigerProfilActivity.this);
                                                dialog.setContentView(R.layout.dialog_dizayn2);
                                                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

                                                LinearLayout lay1 = dialog.findViewById(R.id.lay1);
                                                ProgressBar pbar = dialog.findViewById(R.id.pbar);
                                                pbar.getIndeterminateDrawable().setColorFilter(ortaRenk, android.graphics.PorterDuff.Mode.MULTIPLY);
                                                TextView baslik = dialog.findViewById(R.id.baslik);
                                                final TextView[] aciklama = {dialog.findViewById(R.id.aciklama)};
                                                Button buton = dialog.findViewById(R.id.buton);

                                                baslik.setText("MESAJLAŞ");
                                                if (fiyatlar != null) {
                                                    final int[] deger = {0};
                                                    final String[] aciklamaYazi = {""};
                                                    final String[] key = {""};
                                                    DatabaseReference uref = FirebaseDatabase.getInstance().getReference("usersF").child(user.getUid());
                                                    uref.addListenerForSingleValueEvent(new ValueEventListener() {
                                                        @Override
                                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                            butonTiklandiMi = false;
                                                            if (dataSnapshot.hasChild("kilidini_actiklarim")) {
                                                                if (dataSnapshot.child("kilidini_actiklarim").hasChild(fuser.getUid())) {
                                                                    deger[0] = fiyatlar.getKilit_acma() / 2;
                                                                    key[0] = dataSnapshot.child("kilidini_actiklarim").child(fuser.getUid()).child("oda").getValue(String.class);
                                                                    aciklamaYazi[0] = "Bu kullanıcıya cevap verebilmek için kilidi açmalısınız. Kilidi açmak için gerekli miktar " + deger[0] + "KP, sizde olan " + Kullanici.getKp() + "KP";
                                                                } else {
                                                                    deger[0] = fiyatlar.getKilit_acma();
                                                                    aciklamaYazi[0] = "Bu kullanıcıya mesaj atabilmek için kilidi açmalısınız. Kilidi açmak için gerekli miktar " + deger[0] + "KP, sizde olan " + Kullanici.getKp() + "KP";
                                                                }
                                                            } else {
                                                                deger[0] = fiyatlar.getKilit_acma();
                                                                aciklamaYazi[0] = "Bu kullanıcıya mesaj atabilmek için kilidi açmalısınız. Kilidi açmak için gerekli miktar " + deger[0] + "KP, sizde olan " + Kullanici.getKp() + "KP";
                                                            }

                                                            aciklama[0].setText(aciklamaYazi[0]);

                                                            if (deger[0] != 0) {
                                                                if (Kullanici.getKp() >= deger[0]) {
                                                                    buton.setText("Kilidi Aç");
                                                                    buton.setOnClickListener(new View.OnClickListener() {
                                                                        @Override
                                                                        public void onClick(View view) {
                                                                            lay1.setVisibility(View.GONE);
                                                                            pbar.setVisibility(View.VISIBLE);
                                                                            DatabaseReference ref = FirebaseDatabase.getInstance().getReference("usersF").child(Kullanici.getUid());
                                                                            ref.child("harcamalarim").child("kilit_" + user.getUid()).child("onceki_kp").setValue(Kullanici.getKp());
                                                                            ref.child("harcamalarim").child("kilit_" + user.getUid()).child("zaman").setValue(ServerValue.TIMESTAMP);
                                                                            ref.child("kp").setValue(Kullanici.getKp() - deger[0]).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                                @Override
                                                                                public void onSuccess(@NonNull Void unused) {
                                                                                    if (key[0].equals(""))
                                                                                        key[0] = FirebaseDatabase.getInstance().getReference("Mesajlar").push().getKey();
                                                                                    DatabaseReference karsidaki = FirebaseDatabase.getInstance().getReference("usersF").child(user.getUid());
                                                                                    karsidaki.addListenerForSingleValueEvent(new ValueEventListener() {
                                                                                        @Override
                                                                                        public void onDataChange(@NonNull DataSnapshot karsidakiSnapshot) {
                                                                                            if(karsidakiSnapshot.child("kilidini_actiklarim").child(fuser.getUid()).exists())
                                                                                                karsidaki.child("kilidini_actiklarim").child(Kullanici.getUid()).child("durum").setValue("tam");
                                                                                            else
                                                                                                karsidaki.child("kilidini_actiklarim").child(Kullanici.getUid()).child("durum").setValue("yarim");

                                                                                            karsidaki.child("kilidini_actiklarim").child(Kullanici.getUid()).child("oda").setValue(key[0]);
                                                                                            karsidaki.child("kilidini_actiklarim").child(Kullanici.getUid()).child("mesaj").setValue("yok");
                                                                                            ref.child("kilidini_actiklarim").child(user.getUid()).child("durum").setValue("tam");
                                                                                            ref.child("kilidini_actiklarim").child(user.getUid()).child("mesaj").setValue("yok");
                                                                                            ref.child("kilidini_actiklarim").child(user.getUid()).child("oda").setValue(key[0]).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                                                @Override
                                                                                                public void onSuccess(@NonNull Void unused) {
                                                                                                    Toast.makeText(DigerProfilActivity.this, "Kullanıcının kilidini açtınız!", Toast.LENGTH_SHORT).show();
                                                                                                    kilitAcikMi = true;
                                                                                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                                                                                                        Drawable mesajAt = getResources().getDrawable(R.drawable.mesaj_gonder);
                                                                                                        mesajAt.setTint(ortaRenk);
                                                                                                        mesajAt.setBounds(0, 0, mesajAt.getIntrinsicHeight(), mesajAt.getIntrinsicWidth());
                                                                                                        mesaj_at.setCompoundDrawables(mesajAt, null, null, null);
                                                                                                    }
                                                                                                    dialog.dismiss();
                                                                                                }
                                                                                            });
                                                                                        }

                                                                                        @Override
                                                                                        public void onCancelled(@NonNull DatabaseError databaseError) {

                                                                                        }
                                                                                    });

                                                                                }
                                                                            }).addOnFailureListener(new OnFailureListener() {
                                                                                @Override
                                                                                public void onFailure(@NonNull Exception e) {
                                                                                    lay1.setVisibility(View.VISIBLE);
                                                                                    pbar.setVisibility(View.GONE);
                                                                                    Toast.makeText(DigerProfilActivity.this, "Başarısız!", Toast.LENGTH_SHORT).show();
                                                                                }
                                                                            });
                                                                        }
                                                                    });
                                                                } else {
                                                                    buton.setText("Markete Git");
                                                                    buton.setOnClickListener(new View.OnClickListener() {
                                                                        @Override
                                                                        public void onClick(View view) {
                                                                            lay1.setVisibility(View.GONE);
                                                                            pbar.setVisibility(View.VISIBLE);
                                                                            startActivity(new Intent(DigerProfilActivity.this, MarketActivity.class));
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
                                                dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                                                    @Override
                                                    public void onCancel(DialogInterface dialog) {
                                                        butonTiklandiMi = false;
                                                    }
                                                });
                                                dialog.show();
                                            } else {
                                                Toast.makeText(DigerProfilActivity.this, "Bulduğunuz kişilere 1 gün sonra mesaj atabilirsiniz. Kalan Dakika: " + ((86400000 - (suan - ilkZaman)) / 60000), Toast.LENGTH_LONG).show();
                                                butonTiklandiMi = false;
                                            }
                                        }
                                        else {
                                            Toast.makeText(DigerProfilActivity.this, "Tekrar deneyin", Toast.LENGTH_SHORT).show();
                                            butonTiklandiMi = false;
                                        }
                                    }
                                    else {
                                        Dialog dialog = new Dialog(DigerProfilActivity.this);
                                        dialog.setContentView(R.layout.dialog_dizayn2);
                                        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

                                        LinearLayout lay1 = dialog.findViewById(R.id.lay1);
                                        ProgressBar pbar = dialog.findViewById(R.id.pbar);
                                        pbar.getIndeterminateDrawable().setColorFilter(ortaRenk, android.graphics.PorterDuff.Mode.MULTIPLY);
                                        TextView baslik = dialog.findViewById(R.id.baslik);
                                        final TextView[] aciklama = {dialog.findViewById(R.id.aciklama)};
                                        Button buton = dialog.findViewById(R.id.buton);

                                        baslik.setText("MESAJLAŞ");
                                        if (fiyatlar != null) {
                                            final int[] deger = {0};
                                            final String[] aciklamaYazi = {""};
                                            final String[] key = {""};
                                            DatabaseReference uref = FirebaseDatabase.getInstance().getReference("usersF").child(user.getUid());
                                            uref.addListenerForSingleValueEvent(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                    butonTiklandiMi = false;
                                                    if (dataSnapshot.hasChild("kilidini_actiklarim")) {
                                                        if (dataSnapshot.child("kilidini_actiklarim").hasChild(fuser.getUid())) {
                                                            deger[0] = fiyatlar.getKilit_acma() / 2;
                                                            key[0] = dataSnapshot.child("kilidini_actiklarim").child(fuser.getUid()).child("oda").getValue(String.class);
                                                            aciklamaYazi[0] = "Bu kullanıcıya cevap verebilmek için kilidi açmalısınız. Kilidi açmak için gerekli miktar " + deger[0] + "KP, sizde olan " + Kullanici.getKp() + "KP";
                                                        } else {
                                                            deger[0] = fiyatlar.getKilit_acma();
                                                            aciklamaYazi[0] = "Bu kullanıcıya mesaj atabilmek için kilidi açmalısınız. Kilidi açmak için gerekli miktar " + deger[0] + "KP, sizde olan " + Kullanici.getKp() + "KP";
                                                        }
                                                    } else {
                                                        deger[0] = fiyatlar.getKilit_acma();
                                                        aciklamaYazi[0] = "Bu kullanıcıya mesaj atabilmek için kilidi açmalısınız. Kilidi açmak için gerekli miktar " + deger[0] + "KP, sizde olan " + Kullanici.getKp() + "KP";
                                                    }

                                                    aciklama[0].setText(aciklamaYazi[0]);

                                                    if (deger[0] != 0) {
                                                        if (Kullanici.getKp() >= deger[0]) {
                                                            buton.setText("Kilidi Aç");
                                                            buton.setOnClickListener(new View.OnClickListener() {
                                                                @Override
                                                                public void onClick(View view) {
                                                                    lay1.setVisibility(View.GONE);
                                                                    pbar.setVisibility(View.VISIBLE);
                                                                    DatabaseReference ref = FirebaseDatabase.getInstance().getReference("usersF").child(Kullanici.getUid());
                                                                    ref.child("harcamalarim").child("kilit_" + user.getUid()).child("onceki_kp").setValue(Kullanici.getKp());
                                                                    ref.child("harcamalarim").child("kilit_" + user.getUid()).child("zaman").setValue(ServerValue.TIMESTAMP);
                                                                    ref.child("kp").setValue(Kullanici.getKp() - deger[0]).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                        @Override
                                                                        public void onSuccess(@NonNull Void unused) {
                                                                            if (key[0].equals(""))
                                                                                key[0] = FirebaseDatabase.getInstance().getReference("Mesajlar").push().getKey();
                                                                            DatabaseReference karsidaki = FirebaseDatabase.getInstance().getReference("usersF").child(user.getUid());
                                                                            karsidaki.addListenerForSingleValueEvent(new ValueEventListener() {
                                                                                @Override
                                                                                public void onDataChange(@NonNull DataSnapshot karsidakiSnapshot) {
                                                                                    if(karsidakiSnapshot.child("kilidini_actiklarim").child(fuser.getUid()).exists())
                                                                                        karsidaki.child("kilidini_actiklarim").child(Kullanici.getUid()).child("durum").setValue("tam");
                                                                                    else
                                                                                        karsidaki.child("kilidini_actiklarim").child(Kullanici.getUid()).child("durum").setValue("yarim");

                                                                                    karsidaki.child("kilidini_actiklarim").child(Kullanici.getUid()).child("oda").setValue(key[0]);
                                                                                    karsidaki.child("kilidini_actiklarim").child(Kullanici.getUid()).child("mesaj").setValue("yok");
                                                                                    ref.child("kilidini_actiklarim").child(user.getUid()).child("durum").setValue("tam");
                                                                                    ref.child("kilidini_actiklarim").child(user.getUid()).child("mesaj").setValue("yok");
                                                                                    ref.child("kilidini_actiklarim").child(user.getUid()).child("oda").setValue(key[0]).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                                        @Override
                                                                                        public void onSuccess(@NonNull Void unused) {
                                                                                            Toast.makeText(DigerProfilActivity.this, "Kullanıcının kilidini açtınız!", Toast.LENGTH_SHORT).show();
                                                                                            kilitAcikMi = true;
                                                                                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                                                                                                Drawable mesajAt = getResources().getDrawable(R.drawable.mesaj_gonder);
                                                                                                mesajAt.setTint(ortaRenk);
                                                                                                mesajAt.setBounds(0, 0, mesajAt.getIntrinsicHeight(), mesajAt.getIntrinsicWidth());
                                                                                                mesaj_at.setCompoundDrawables(mesajAt, null, null, null);
                                                                                            }
                                                                                            dialog.dismiss();
                                                                                        }
                                                                                    });
                                                                                }

                                                                                @Override
                                                                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                                                                }
                                                                            });
                                                                        }
                                                                    }).addOnFailureListener(new OnFailureListener() {
                                                                        @Override
                                                                        public void onFailure(@NonNull Exception e) {
                                                                            lay1.setVisibility(View.VISIBLE);
                                                                            pbar.setVisibility(View.GONE);
                                                                            Toast.makeText(DigerProfilActivity.this, "Başarısız!", Toast.LENGTH_SHORT).show();
                                                                        }
                                                                    });
                                                                }
                                                            });
                                                        } else {
                                                            buton.setText("Markete Git");
                                                            buton.setOnClickListener(new View.OnClickListener() {
                                                                @Override
                                                                public void onClick(View view) {
                                                                    lay1.setVisibility(View.GONE);
                                                                    pbar.setVisibility(View.VISIBLE);
                                                                    startActivity(new Intent(DigerProfilActivity.this, MarketActivity.class));
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
                                        dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                                            @Override
                                            public void onCancel(DialogInterface dialog) {
                                                butonTiklandiMi = false;
                                            }
                                        });
                                        dialog.show();
                                    }

                                }
                            } else {
                                butonTiklandiMi = false;
                                Toast.makeText(DigerProfilActivity.this, "Bu kişiyle etkileşime giremezsiniz.", Toast.LENGTH_SHORT).show();
                            }
                        }
                        else{
                            if (kilitAcikMi) {
                                Intent degis = new Intent(DigerProfilActivity.this, MesajActivity.class);
                                degis.putExtra("userid", kiminmis);
                                startActivity(degis);
                            }
                            else {

                                Dialog dialog = new Dialog(DigerProfilActivity.this);
                                dialog.setContentView(R.layout.dialog_dizayn2);
                                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

                                LinearLayout lay1 = dialog.findViewById(R.id.lay1);
                                ProgressBar pbar = dialog.findViewById(R.id.pbar);
                                pbar.getIndeterminateDrawable().setColorFilter(ortaRenk, android.graphics.PorterDuff.Mode.MULTIPLY);
                                TextView baslik = dialog.findViewById(R.id.baslik);
                                final TextView[] aciklama = {dialog.findViewById(R.id.aciklama)};
                                Button buton = dialog.findViewById(R.id.buton);

                                baslik.setText("MESAJLAŞ");
                                if (fiyatlar != null) {
                                    final int[] deger = {0};
                                    final String[] aciklamaYazi = {""};
                                    final String[] key = {""};
                                    DatabaseReference uref = FirebaseDatabase.getInstance().getReference("usersF").child(user.getUid());
                                    uref.addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                            butonTiklandiMi = false;
                                            if (dataSnapshot.hasChild("kilidini_actiklarim")) {
                                                if (dataSnapshot.child("kilidini_actiklarim").hasChild(fuser.getUid())) {
                                                    deger[0] = 0;
                                                    key[0] = dataSnapshot.child("kilidini_actiklarim").child(fuser.getUid()).child("oda").getValue(String.class);
                                                    aciklamaYazi[0] = "Bu kullanıcıya cevap verebilmek için kilidi açmalısınız. Kilidi açmak için gerekli miktar " + deger[0] + "KP, sizde olan " + Kullanici.getKp() + "KP";
                                                } else {
                                                    deger[0] = 0;
                                                    aciklamaYazi[0] = "Bu kullanıcıya mesaj atabilmek için kilidi açmalısınız. Kilidi açmak için gerekli miktar " + deger[0] + "KP, sizde olan " + Kullanici.getKp() + "KP";
                                                }
                                            } else {
                                                deger[0] = 0;
                                                aciklamaYazi[0] = "Bu kullanıcıya mesaj atabilmek için kilidi açmalısınız. Kilidi açmak için gerekli miktar " + deger[0] + "KP, sizde olan " + Kullanici.getKp() + "KP";
                                            }

                                            aciklama[0].setText(aciklamaYazi[0]);

                                            if (Kullanici.getKp() >= deger[0]) {
                                                buton.setText("Kilidi Aç");
                                                buton.setOnClickListener(new View.OnClickListener() {
                                                    @Override
                                                    public void onClick(View view) {
                                                        lay1.setVisibility(View.GONE);
                                                        pbar.setVisibility(View.VISIBLE);
                                                        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("usersF").child(Kullanici.getUid());
                                                        ref.child("harcamalarim").child("kilit_" + user.getUid()).child("onceki_kp").setValue(Kullanici.getKp());
                                                        ref.child("harcamalarim").child("kilit_" + user.getUid()).child("zaman").setValue(ServerValue.TIMESTAMP);
                                                        ref.child("kp").setValue(Kullanici.getKp() - deger[0]).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                            @Override
                                                            public void onSuccess(@NonNull Void unused) {
                                                                if (key[0].equals(""))
                                                                    key[0] = FirebaseDatabase.getInstance().getReference("Mesajlar").push().getKey();
                                                                DatabaseReference karsidaki = FirebaseDatabase.getInstance().getReference("usersF").child(user.getUid());
                                                                karsidaki.addListenerForSingleValueEvent(new ValueEventListener() {
                                                                    @Override
                                                                    public void onDataChange(@NonNull DataSnapshot karsidakiSnapshot) {
                                                                        if(karsidakiSnapshot.child("kilidini_actiklarim").child(fuser.getUid()).exists())
                                                                            karsidaki.child("kilidini_actiklarim").child(Kullanici.getUid()).child("durum").setValue("tam");
                                                                        else
                                                                            karsidaki.child("kilidini_actiklarim").child(Kullanici.getUid()).child("durum").setValue("yarim");

                                                                        karsidaki.child("kilidini_actiklarim").child(Kullanici.getUid()).child("oda").setValue(key[0]);
                                                                        karsidaki.child("kilidini_actiklarim").child(Kullanici.getUid()).child("mesaj").setValue("yok");
                                                                        ref.child("kilidini_actiklarim").child(user.getUid()).child("durum").setValue("tam");
                                                                        ref.child("kilidini_actiklarim").child(user.getUid()).child("mesaj").setValue("yok");
                                                                        ref.child("kilidini_actiklarim").child(user.getUid()).child("oda").setValue(key[0]).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                            @Override
                                                                            public void onSuccess(@NonNull Void unused) {
                                                                                Toast.makeText(DigerProfilActivity.this, "Kullanıcının kilidini açtınız!", Toast.LENGTH_SHORT).show();
                                                                                kilitAcikMi = true;
                                                                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                                                                                    Drawable mesajAt = getResources().getDrawable(R.drawable.mesaj_gonder);
                                                                                    mesajAt.setTint(ortaRenk);
                                                                                    mesajAt.setBounds(0, 0, mesajAt.getIntrinsicHeight(), mesajAt.getIntrinsicWidth());
                                                                                    mesaj_at.setCompoundDrawables(mesajAt, null, null, null);
                                                                                }
                                                                                dialog.dismiss();
                                                                            }
                                                                        });
                                                                    }

                                                                    @Override
                                                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                                                    }
                                                                });
                                                            }
                                                        }).addOnFailureListener(new OnFailureListener() {
                                                            @Override
                                                            public void onFailure(@NonNull Exception e) {
                                                                lay1.setVisibility(View.VISIBLE);
                                                                pbar.setVisibility(View.GONE);
                                                                Toast.makeText(DigerProfilActivity.this, "Başarısız!", Toast.LENGTH_SHORT).show();
                                                            }
                                                        });
                                                    }
                                                });
                                            } else {
                                                buton.setText("Markete Git");
                                                buton.setOnClickListener(new View.OnClickListener() {
                                                    @Override
                                                    public void onClick(View view) {
                                                        lay1.setVisibility(View.GONE);
                                                        pbar.setVisibility(View.VISIBLE);
                                                        startActivity(new Intent(DigerProfilActivity.this, MarketActivity.class));
                                                    }
                                                });
                                            }
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError databaseError) {

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
                        }
                    }
                }else{
                    Toast.makeText(DigerProfilActivity.this, "İnternet Bağlantınız Yok!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        if(user.getCinsiyet().equals("Erkek")) {
            cinsiyet.setVisibility(View.VISIBLE);
            cinsiyet2.setVisibility(View.GONE);
            yas_arka.setColorFilter(getResources().getColor(R.color.gradient2));
        }
        else{
            cinsiyet2.setVisibility(View.VISIBLE);
            cinsiyet.setVisibility(View.GONE);
            yas_arka.setColorFilter(getResources().getColor(R.color.favori_arka));
        }
        getSupportActionBar().setHomeAsUpIndicator(getResources().getDrawable(R.drawable.ic_mesaj_geri2));

        DatabaseReference favoriData = FirebaseDatabase.getInstance().getReference("usersF").child(fuser.getUid());
        favoriData.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.child("kisitli_erisim_engeli").child("durum").getValue().equals("var")) {
                    Intent intent = new Intent(DigerProfilActivity.this, MainActivity.class);
                    startActivity(intent);
                }
                dataSnapshotAsil = dataSnapshot;
                Kullanici = dataSnapshot.getValue(User.class);

                if(dataSnapshot.hasChild("kilidini_actiklarim")){
                    if(dataSnapshot.child("kilidini_actiklarim").hasChild(user.getUid())){
                        if (dataSnapshot.child("kilidini_actiklarim").child(user.getUid()).hasChild("durum")){
                            if (dataSnapshot.child("kilidini_actiklarim").child(user.getUid()).child("durum").getValue(String.class).equals("tam")) {
                                kilitAcikMi = true;
                                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                                    Drawable mesajAt = getResources().getDrawable(R.drawable.mesaj_gonder);
                                    mesajAt.setTint(ortaRenk);
                                    mesajAt.setBounds(0, 0, mesajAt.getIntrinsicHeight(), mesajAt.getIntrinsicWidth());
                                    mesaj_at.setCompoundDrawables(mesajAt, null, null, null);
                                }
                            }
                            else{
                                kilitAcikMi = false;
                                Drawable kilit = null;
                                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                                    kilit = (Drawable) getResources().getDrawable(R.drawable.kilit_ic);
                                    kilit.setBounds(0, 0, kilit.getIntrinsicHeight(), kilit.getIntrinsicWidth());
                                    mesaj_at.setCompoundDrawables(kilit, null, null, null);
                                }
                            }
                        }
                    }
                    else{
                        kilitAcikMi = false;
                        Drawable kilit = null;
                        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                            kilit = (Drawable) getResources().getDrawable(R.drawable.kilit_ic);
                            kilit.setBounds(0, 0, kilit.getIntrinsicHeight(), kilit.getIntrinsicWidth());
                            mesaj_at.setCompoundDrawables(kilit, null, null, null);
                        }
                    }
                }
                else{
                    kilitAcikMi = false;
                    Drawable kilit = null;
                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                        kilit = (Drawable) getResources().getDrawable(R.drawable.kilit_ic);
                        kilit.setBounds(0, 0, kilit.getIntrinsicHeight(), kilit.getIntrinsicWidth());
                        mesaj_at.setCompoundDrawables(kilit, null, null, null);
                    }
                }
                if(dataSnapshot.hasChild("favorilerim")){
                    if(dataSnapshot.child("favorilerim").hasChild(kiminmis)){
                        favorilere_ekle.setVisibility(View.VISIBLE);
                        favorilere_ekle2.setVisibility(View.GONE);
                                /*Drawable img = getApplicationContext().getResources().getDrawable( R.drawable.ic_profil_favori_erkek_dolu );
                                img.setBounds( 0, 0, 60, 60 );
                                favorilere_ekle.setTextColor(getResources().getColor(R.color.colorPrimary));
                                favorilere_ekle.setCompoundDrawables( img, null, null, null );*/
                    }else{
                        favorilere_ekle.setVisibility(View.GONE);
                        favorilere_ekle2.setVisibility(View.VISIBLE);
                    }
                }else{
                    favorilere_ekle.setVisibility(View.GONE);
                    favorilere_ekle2.setVisibility(View.VISIBLE);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        begeniRef = FirebaseDatabase.getInstance().getReference("usersF").child(kiminmis);
        begeniRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull final DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChild("begenenler")) {
                    if (dataSnapshot.child("begenenler").hasChild(fuser.getUid())) {
                        begendim = true;
                        final Long ref = (Long) dataSnapshot.child("begenenler").child(fuser.getUid()).child("son_begenme").getValue();
                        final Long ref2 = (Long) dataSnapshot.child("begenenler").child(fuser.getUid()).child("suanki_zaman").getValue();
                        son = ref.longValue();
                        suan = ref2.longValue();
                        if (suan > son && suan - son < 86400000) {
                            zaman = suan - son;
                            saniyeDevam = true;
                            final Thread thread = new Thread() {

                                @Override
                                public void run() {
                                    try {
                                        while (!isInterrupted()) {
                                            if(getApplicationContext() != null) {
                                                runOnUiThread(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        zaman += 1000;
                                                        begen.setText(farkiHesapla());
                                                        if (begen.getText().equals("Bekleyin...")) {
                                                            BastanBaslat();
                                                        }
                                                    }
                                                });
                                            }
                                            Thread.sleep(1000);
                                        }
                                    } catch (InterruptedException e) {
                                    }

                                }
                            };
                            thread.start();
                        } else {
                            begendim = false;
                            begenebilmeDurumu = "begenilebilir";
                            begen.setText("Beğen");
                        }
                        if (dataSnapshot.child("begenenler").child(fuser.getUid()).hasChild("son_begenme") && dataSnapshot.child("begenenler").child(fuser.getUid()).hasChild("suanki_zaman")) {

                        }
                    }
                    else {
                        begendim = false;
                        begenebilmeDurumu = "begenilebilir";
                        begen.setText("Beğen");
                    }
                }
                else{
                    begendim = false;
                    begenebilmeDurumu = "begenilebilir";
                    begen.setText("Beğen");
                }
                if(dataSnapshot != null)
                    if (dataSnapshot.hasChild("rozetlerim")) {
                        if (dataSnapshot.child("rozet").getValue(String.class).equals("1")) {
                            rozet_view_arka.setBackground(getResources().getDrawable(R.drawable.rozet1_arka));
                        }
                        if (dataSnapshot.child("rozet").getValue(String.class).equals("2")) {
                            rozet_view_arka.setBackground(getResources().getDrawable(R.drawable.rozet2_arka));
                        }
                        if (dataSnapshot.child("rozet").getValue(String.class).equals("3")) {
                            rozet_view_arka.setBackground(getResources().getDrawable(R.drawable.rozet3_arka));
                        }
                        if (dataSnapshot.child("rozet").getValue(String.class).equals("4")) {
                            rozet_view_arka.setBackground(getResources().getDrawable(R.drawable.rozet4_arka));
                        }
                        if (dataSnapshot.child("rozet").getValue(String.class).equals("5")) {
                            rozet_view_arka.setBackground(getResources().getDrawable(R.drawable.rozet5_arka));
                        }
                        if (dataSnapshot.child("rozet").getValue(String.class).equals("6")) {
                            rozet_view_arka.setBackground(getResources().getDrawable(R.drawable.rozet6_arka));
                        }
                    }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        if(user != null) {
            BegeniHesapla();
        }
    }

    public void TasarimDegistir(String tasDegeri) {
        int renk1 = 0,renk2 = 0,t1start = 0,t2start = 0,t1end = 0,t2end = 0,orta = 0;

        GradientDrawable gradient = new GradientDrawable();
        gradient.setOrientation(GradientDrawable.Orientation.LEFT_RIGHT);


        renk1 = TasarimRenginiGetir.RengiGetir(DigerProfilActivity.this,"renk1",tasDegeri);
        renk2 = TasarimRenginiGetir.RengiGetir(DigerProfilActivity.this,"renk2",tasDegeri);
        t1start = TasarimRenginiGetir.RengiGetir(DigerProfilActivity.this,"t1start",tasDegeri);
        t2start = TasarimRenginiGetir.RengiGetir(DigerProfilActivity.this,"t2start",tasDegeri);
        t1end = TasarimRenginiGetir.RengiGetir(DigerProfilActivity.this,"t1end",tasDegeri);
        t2end = TasarimRenginiGetir.RengiGetir(DigerProfilActivity.this,"t2end",tasDegeri);
        orta = TasarimRenginiGetir.RengiGetir(DigerProfilActivity.this,"orta",tasDegeri);


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            VectorDrawable kalp = (VectorDrawable) getResources().getDrawable(R.drawable.ic_profil_begen1);
            kalp.setTint(orta);
            kalp.setBounds(0, 0, kalp.getIntrinsicHeight(), kalp.getIntrinsicWidth());
            begen.setCompoundDrawables(kalp, null, null, null);

            if(kilitAcikMi) {
                Drawable mesajAt = getResources().getDrawable(R.drawable.mesaj_gonder);
                mesajAt.setTint(orta);
                mesajAt.setBounds(0, 0, mesajAt.getIntrinsicHeight(), mesajAt.getIntrinsicWidth());
                mesaj_at.setCompoundDrawables(mesajAt, null, null, null);
            }
            else{
                Drawable mesajAt = getResources().getDrawable(R.drawable.kilit_ic);
                mesajAt.setTint(orta);
                mesajAt.setBounds(0, 0, mesajAt.getIntrinsicHeight(), mesajAt.getIntrinsicWidth());
                mesaj_at.setCompoundDrawables(mesajAt, null, null, null);
            }
        }

        gradient.setColors(new int[]{
                renk1,
                orta,
                renk2,
        });

        ortaRenk = orta;
        //mesaj_at.setTextColor(orta);
       // begen.setTextColor(orta);
        background.setBackground(gradient);
        anaRel.setBackground(gradient);

    }
    private void KimleriEngellemisim() {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("usersF").child(fuser.getUid());
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.hasChild("engellediklerim")){
                    for(DataSnapshot ds : dataSnapshot.child("engellediklerim").getChildren()){
                        engellediklerim.add(ds.getKey());
                        if (engellediklerim.size() == dataSnapshot.child("engellediklerim").getChildrenCount()) {
                            if(!engellediklerim.contains(kiminmis))
                                TumBilgileriCagir();
                            else
                                engelDurumuVarmi = true;
                        }
                    }
                }
                else
                    TumBilgileriCagir();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.dprofil_menu,menu);
        return true;
    }
    private void GelirBilgisiEkle(String uid, String id, String satinAlinanDeger, int kacKP){
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
                                ref.child("gelirlerim").child(id + satinAlinanDeger + "_" + kpSayisi).child("onceki_kp").setValue(dataSnapshot.child("kp").getValue(Integer.class));
                                ref.child("gelirlerim").child(id + satinAlinanDeger + "_" + kpSayisi).child("verilen_kp").setValue(kacKP);
                                ref.child("gelirlerim").child(id + satinAlinanDeger + "_" + kpSayisi).child("zaman").setValue(ServerValue.TIMESTAMP).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(@NonNull Void unused) {
                                        dataSnapshot.child("kp").getRef().setValue(dataSnapshot.child("kp").getValue(Integer.class) + kacKP).addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(@NonNull Void unused) {
                                                if (uid.equals(fuser.getUid()))
                                                    Toast.makeText(DigerProfilActivity.this, "Tebrikler " + kacKP + "KP kazandınız!", Toast.LENGTH_SHORT).show();
                                            }
                                        });
                                    }
                                });
                            }
                        }
                    }
                    else{
                        ref.child("gelirlerim").child(id+satinAlinanDeger+"_1").child("onceki_kp").setValue(dataSnapshot.child("kp").getValue(Integer.class));
                        ref.child("gelirlerim").child(id + satinAlinanDeger+"_1").child("verilen_kp").setValue(kacKP);
                        ref.child("gelirlerim").child(id+satinAlinanDeger+"_1").child("zaman").setValue(ServerValue.TIMESTAMP).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(@NonNull Void unused) {
                                dataSnapshot.child("kp").getRef().setValue(dataSnapshot.child("kp").getValue(Integer.class) + kacKP).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(@NonNull Void unused) {
                                        if (uid.equals(fuser.getUid()))
                                            Toast.makeText(DigerProfilActivity.this, "Tebrikler " + kacKP + "KP kazandınız!", Toast.LENGTH_SHORT).show();
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
    public boolean onOptionsItemSelected(MenuItem item) {
        ClipboardManager clipboard = (ClipboardManager) this.getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip;
        switch (item.getItemId()){
            case R.id.referansim_yap:
                if(internetVarmi() && !engelDurumuVarmi) {
                    final DatabaseReference ref = FirebaseDatabase.getInstance().getReference("usersF").child(fuser.getUid());
                    ref.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if (dataSnapshot.child("referansim").getValue(String.class).equals("")) {
                                DatabaseReference reference = FirebaseDatabase.getInstance().getReference("usersF").child(kiminmis);
                                reference.child("referanslarim").child(fuser.getUid()).setValue(ServerValue.TIMESTAMP);
                                ref.child("referansim").setValue(kiminmis).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        GelirBilgisiEkle(fuser.getUid(),"ref_",fuser.getUid(),fiyatlar.getReferans()/2);
                                        GelirBilgisiEkle(kiminmis,"ref_",kiminmis,fiyatlar.getReferans());
                                    }
                                });
                            } else {
                                Toast.makeText(DigerProfilActivity.this, "Zaten bir referansınız var.", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                }
                return true;
            case R.id.sikayet_et:
                if(internetVarmi()) {
                    DatabaseReference sikayetRef = FirebaseDatabase.getInstance().getReference("ProfilSikayet");
                    sikayetRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if (dataSnapshot.exists()) {
                                if (dataSnapshot.hasChild(kiminmis)) {
                                    if (dataSnapshot.child(kiminmis).child("sikayet_edenler").hasChild(fuser.getUid()))
                                        Toast.makeText(DigerProfilActivity.this, "Zaten şikayet etmişsiniz.", Toast.LENGTH_SHORT).show();
                                    else
                                        profilSikayetEdilsiMi();
                                }
                                else
                                    profilSikayetEdilsiMi();
                            }
                            else
                                profilSikayetEdilsiMi();
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                }
                return true;
            case R.id.kullanici_adi:
                if (kullaniciAdi != null) {
                    clip = ClipData.newPlainText("Kullanıcı adı kopyalandı", kullaniciAdi);
                    clipboard.setPrimaryClip(clip);
                    Toast.makeText(this, "Kullanıcı adı kopyalandı", Toast.LENGTH_SHORT).show();
                }else
                    Toast.makeText(this, "Bir sorun oluştu.", Toast.LENGTH_SHORT).show();
                return true;
            case R.id.engelle:
                if(internetVarmi())
                    engellensinMi();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    public void profilSikayetEdilsiMi(){
        AlertDialog.Builder builder = new AlertDialog.Builder(DigerProfilActivity.this);
        builder.setCancelable(true);

        View view = getLayoutInflater().inflate(R.layout.dialog_dizayn,null);
        String[] secenekler = {};
        if(Kullanici.getCinsiyet().equals("Kadın"))
            secenekler = new String[]{ "40 Yaşından Büyük", "Sahte Bilgiler Kullanıyor","Diğer"}; // "Beni Takip Ediyor",
        if(Kullanici.getCinsiyet().equals("Erkek"))
            secenekler = new String[]{"40 Yaşından Büyük", "Sahte Bilgiler Kullanıyor","Diğer"};
        builder.setView(view);

        EditText sikayet = view.findViewById(R.id.dialog_edittext);
        TextView baslik = view.findViewById(R.id.dialog_baslik);
        TextView bilgi = view.findViewById(R.id.dialog_bilgi);
        bilgi.setText("");
        baslik.setVisibility(View.GONE);
        sikayet.setVisibility(View.GONE);
        builder.setTitle("Şikayet Et");
        builder.setItems(secenekler, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //if(!Kullanici.getCinsiyet().equals("Kadın"))
                which ++;
                if(which == 0){
                    takipEdiyorSikayetEdilsinMi();
                }else if(which == 1){
                    yanlisYasSikayetEdilsinMi();
                }else if(which == 2){
                    sahteBilgilerSikayetEdilsinMi();
                }else if(which == 3){
                    digerSikayetEdilsinMi();
                }
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public void takipEdiyorSikayetEdilsinMi(){
        AlertDialog.Builder builder = new AlertDialog.Builder(DigerProfilActivity.this);
        builder.setCancelable(true);
        builder.setTitle("Şikayet Et");
        builder.setMessage("Eğer bu kişi sizi takip ediyorsa lütfen güvenliğinizden emin olun. Eğer şikayet ederseniz bu kişi 1 saat uygulamayı kullanamayacaktır.");
        builder.setPositiveButton("Evet",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("ProfilSikayet").child(kiminmis);
                        ref.child("sikayet_edenler").child(fuser.getUid()).child("zaman").setValue(ServerValue.TIMESTAMP);
                        ref.child("sikayet_edenler").child(fuser.getUid()).child("sebep").setValue("TAKİP");

                        DatabaseReference ref2 = FirebaseDatabase.getInstance().getReference("usersF").child(kiminmis);
                        ref2.child("kisitli_erisim_engeli").child("durum").getRef().setValue("var");
                        ref2.child("kisitli_erisim_engeli").child("zaman").getRef().setValue(ServerValue.TIMESTAMP);
                        ref2.child("kisitli_erisim_engeli").child("sure").getRef().setValue(3600000).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(@NonNull Void aVoid) {
                                Toast.makeText(DigerProfilActivity.this, "Şikayetiniz bize ulaşmıştır.", Toast.LENGTH_SHORT).show();
                                dialog.dismiss();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(DigerProfilActivity.this, ""+e.toString(), Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                });
        builder.setNegativeButton("Hayır", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }
    public void yanlisYasSikayetEdilsinMi(){

        Dialog dialog = new Dialog(DigerProfilActivity.this);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.dialog_dizayn5);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        LinearLayout lay1 = dialog.findViewById(R.id.lay1);
        ProgressBar pbar = dialog.findViewById(R.id.pbar);
        pbar.getIndeterminateDrawable().setColorFilter(ortaRenk,android.graphics.PorterDuff.Mode.MULTIPLY);
        TextView baslik = dialog.findViewById(R.id.baslik);
        TextView aciklama = dialog.findViewById(R.id.aciklama);
        aciklama.setMovementMethod(new ScrollingMovementMethod());
        Button buton = dialog.findViewById(R.id.buton); // Hayır
        Button buton2 = dialog.findViewById(R.id.buton2); // Evet

        baslik.setText("Şikayet Et");
        aciklama.setText("Bu kişinin yaşı 40'dan büyük mü?");
        buton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        buton2.setOnClickListener(new View.OnClickListener() { // EVet
            @Override
            public void onClick(View v) {
                lay1.setVisibility(View.GONE);
                pbar.setVisibility(View.VISIBLE);
                lay1.setVisibility(View.GONE);
                pbar.setVisibility(View.VISIBLE);
                DatabaseReference ref = FirebaseDatabase.getInstance().getReference("ProfilSikayet").child(kiminmis);
                ref.child("sikayet_edenler").child(fuser.getUid()).child("zaman").setValue(ServerValue.TIMESTAMP);
                ref.child("sikayet_edenler").child(fuser.getUid()).child("sebep").setValue("YAŞ");
                Toast.makeText(DigerProfilActivity.this, "Şikayetiniz bize ulaşmıştır.", Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            }
        });
        dialog.show();
    }
    public void sahteBilgilerSikayetEdilsinMi(){

        Dialog dialog = new Dialog(DigerProfilActivity.this);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.dialog_dizayn5);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        LinearLayout lay1 = dialog.findViewById(R.id.lay1);
        ProgressBar pbar = dialog.findViewById(R.id.pbar);
        pbar.getIndeterminateDrawable().setColorFilter(ortaRenk,android.graphics.PorterDuff.Mode.MULTIPLY);
        TextView baslik = dialog.findViewById(R.id.baslik);
        TextView aciklama = dialog.findViewById(R.id.aciklama);
        aciklama.setMovementMethod(new ScrollingMovementMethod());
        Button buton = dialog.findViewById(R.id.buton); // Hayır
        Button buton2 = dialog.findViewById(R.id.buton2); // Evet

        baslik.setText("Şikayet Et");
        aciklama.setText("Bu kişi sahte bilgiler mi kullanıyor?");
        buton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        buton2.setOnClickListener(new View.OnClickListener() { // EVet
            @Override
            public void onClick(View v) {
                lay1.setVisibility(View.GONE);
                pbar.setVisibility(View.VISIBLE);
                DatabaseReference ref = FirebaseDatabase.getInstance().getReference("ProfilSikayet").child(kiminmis);
                ref.child("sikayet_edenler").child(fuser.getUid()).child("zaman").setValue(ServerValue.TIMESTAMP);
                ref.child("sikayet_edenler").child(fuser.getUid()).child("sebep").setValue("SAHTE");
                Toast.makeText(DigerProfilActivity.this, "Şikayetiniz bize ulaşmıştır.", Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            }
        });
        dialog.show();
    }
    public void digerSikayetEdilsinMi(){

        Dialog dialog = new Dialog(DigerProfilActivity.this);
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
        EditText sikayet = dialog.findViewById(R.id.editText);
        sikayet.setHint("Sebep");
        baslik.setText("Şikayet Et");
        aciklama.setText("Bu kişiyi şikayet etmek için bir sebep giriniz.");
        buton.setText("GÖNDER");
        buton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                lay1.setVisibility(View.GONE);
                lay2.setVisibility(View.GONE);
                pbar.setVisibility(View.VISIBLE);
                if(!sikayet.getText().toString().trim().equals("")){
                    DatabaseReference ref = FirebaseDatabase.getInstance().getReference("ProfilSikayet").child(kiminmis);
                    ref.child("sikayet_edenler").child(fuser.getUid()).child("zaman").setValue(ServerValue.TIMESTAMP);
                    ref.child("sikayet_edenler").child(fuser.getUid()).child("sebep").setValue(sikayet.getText().toString());
                    Toast.makeText(DigerProfilActivity.this, "Şikayetiniz bize ulaşmıştır.", Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                }else{
                    lay1.setVisibility(View.VISIBLE);
                    lay2.setVisibility(View.VISIBLE);
                    pbar.setVisibility(View.GONE);
                    Toast.makeText(DigerProfilActivity.this, "Hiçbir şey yazmadınız.", Toast.LENGTH_SHORT).show();
                }
            }
        });
        dialog.show();
    }
    public void engellensinMi(){
        Dialog dialog = new Dialog(DigerProfilActivity.this);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.dialog_dizayn5);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        LinearLayout lay1 = dialog.findViewById(R.id.lay1);
        ProgressBar pbar = dialog.findViewById(R.id.pbar);
        pbar.getIndeterminateDrawable().setColorFilter(ortaRenk,android.graphics.PorterDuff.Mode.MULTIPLY);
        TextView baslik = dialog.findViewById(R.id.baslik);
        TextView aciklama = dialog.findViewById(R.id.aciklama);
        aciklama.setMovementMethod(new ScrollingMovementMethod());
        Button buton = dialog.findViewById(R.id.buton); // Hayır
        Button buton2 = dialog.findViewById(R.id.buton2); // Evet

        baslik.setText("Engelle");
        aciklama.setText("Bu kişiyi engellemek istiyor musunuz? \n(Ayarlardan engeli tekrar kaldırabilirsiniz.)");
        buton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        buton2.setOnClickListener(new View.OnClickListener() { // EVet
            @Override
            public void onClick(View v) {
                lay1.setVisibility(View.GONE);
                pbar.setVisibility(View.VISIBLE);
                DatabaseReference ref = FirebaseDatabase.getInstance().getReference("usersF").child(fuser.getUid());
                ref.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        dataSnapshot.child("engellediklerim").child(kiminmis).getRef().setValue(ServerValue.TIMESTAMP);

                        if(dataSnapshot.child("kilidini_actiklarim").hasChild(kiminmis))
                            dataSnapshot.child("kilidini_actiklarim").child(kiminmis).getRef().removeValue();

                        if(dataSnapshot.hasChild("favorilerim"))
                            if (dataSnapshot.child("favorilerim").hasChild(kiminmis))
                                dataSnapshot.child("favorilerim").child(kiminmis).getRef().removeValue();

                        if(dataSnapshot.hasChild("bulduklarim"))
                            if (dataSnapshot.child("bulduklarim").hasChild(kiminmis))
                                dataSnapshot.child("bulduklarim").child(kiminmis).getRef().removeValue();

                        if(dataSnapshot.hasChild("begenenler"))
                            if (dataSnapshot.child("begenenler").hasChild(kiminmis))
                                dataSnapshot.child("begenenler").child(kiminmis).getRef().removeValue();



                        DatabaseReference onunref = FirebaseDatabase.getInstance().getReference("usersF").child(kiminmis);
                        onunref.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                if(dataSnapshot.child("kilidini_actiklarim").hasChild(fuser.getUid()))
                                    dataSnapshot.child("kilidini_actiklarim").child(fuser.getUid()).getRef().removeValue();

                                if(dataSnapshot.hasChild("favorilerim"))
                                    if (dataSnapshot.child("favorilerim").hasChild(fuser.getUid()))
                                        dataSnapshot.child("favorilerim").child(fuser.getUid()).getRef().removeValue();

                                if(dataSnapshot.hasChild("bulduklarim"))
                                    if (dataSnapshot.child("bulduklarim").hasChild(fuser.getUid()))
                                        dataSnapshot.child("bulduklarim").child(fuser.getUid()).getRef().removeValue();

                                if(dataSnapshot.hasChild("begenenler"))
                                    if (dataSnapshot.child("begenenler").hasChild(fuser.getUid()))
                                        dataSnapshot.child("begenenler").child(fuser.getUid()).getRef().removeValue();


                                ContextWrapper cw = new ContextWrapper(DigerProfilActivity.this);
                                File directory = cw.getDir("kullanici_resimleri", MODE_PRIVATE);

                                for(File files : directory.listFiles()){
                                    if(files.getName().substring(7,files.getName().length()-4).equals(kiminmis)){
                                        files.delete();
                                        SharedPreferences spFavorilerim = getSharedPreferences("Favorilerim",MODE_PRIVATE);
                                        SharedPreferences spBulduklarim = getSharedPreferences("Bulduklarim",MODE_PRIVATE);
                                        for (int i = 0; i < 10; i++){
                                            if (spFavorilerim.getString("" + i, "null").equals(kiminmis)) {
                                                SharedPreferences.Editor editorFav = spFavorilerim.edit();
                                                editorFav.remove("" + i);
                                                editorFav.commit();
                                            }
                                            if (spBulduklarim.getString("" + i, "null").equals(kiminmis)) {
                                                SharedPreferences.Editor editorBul = spBulduklarim.edit();
                                                editorBul.remove("" + i);
                                                editorBul.commit();
                                            }
                                        }
                                    }
                                }

                                Intent geriDon = new Intent(DigerProfilActivity.this,TaraActivity.class);
                                startActivity(geriDon);
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }
        });
        dialog.show();
    }
    private void BastanBaslat(){
        begen.setText("..:..:..");
        zaman = 0;
        saniyeDevam = false;
        if(saniyeSay != null)
            saniyeSay.interrupt();
        Intent intent = getIntent();
        finish();
        startActivity(intent);
    }
    private String AdiSansurle(User user){
        String kesilmisIsim = user.getAd().substring(1);
        StringBuilder yeniisim = new StringBuilder(kesilmisIsim);
        for(int i = 0; i < kesilmisIsim.length(); i++){
            yeniisim.setCharAt(i,'*');
        }
        String sonHal = user.getAd().substring(0,1).toUpperCase() + yeniisim.toString() ;
        return sonHal;
    }
    private String YasSansurle(User user){
        return "**";
    }
    private String CinSansurle(){
        return "*";
    }

    private void PPGetir() {
        if(internetVarmi()) {
            if (ppyuklendimi){
                if(!ppDosyasi.getName().substring(2,7).equals(ufpp.toString().substring(ufpp.toString().length() - 9,ufpp.toString().length() - 4))){
                    ppDosyasi.delete();
                    Glide.with(DigerProfilActivity.this)
                            .asBitmap()
                            .load(ufpp)
                            .into(foto_pp);
                    new ResimIndir(DigerProfilActivity.this, ufpp.toString(), "kullanici_resimleri", "pp"+ufpp.toString().substring(ufpp.toString().length() - 9,ufpp.toString().length() - 4) + kiminmis +".jpg");
                    //Toast.makeText(DigerProfilActivity.this, ppDosyasi.getName().substring(0,5) + "\n" + ufpp.toString().substring(ufpp.toString().length() - 9,ufpp.toString().length() - 4), Toast.LENGTH_LONG).show();
                }
            }
            else{
                Glide.with(DigerProfilActivity.this)
                        .asBitmap()
                        .load(ufpp)
                        .into(foto_pp);
                String kayitAdi = "pp" + ufpp.toString().substring(ufpp.toString().length() - 9,ufpp.toString().length() - 4) + kiminmis +".jpg";
                new ResimIndir(DigerProfilActivity.this, ufpp.toString(), "kullanici_resimleri", kayitAdi);
                //Toast.makeText(DigerProfilActivity.this, "var " + kayitAdi, Toast.LENGTH_SHORT).show();
            }

            if(ufkapak != null){
                Glide.with(DigerProfilActivity.this)
                        .asBitmap()
                        .load(ufkapak)
                        .into(kapak);
            }

            pbar.setVisibility(View.GONE);
            anaRel.setVisibility(View.VISIBLE);
            FotolariGetir();
        }else{
            FotolariGetir();
        }
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent tarayaDon = new Intent(DigerProfilActivity.this,TaraActivity.class);
        startActivity(tarayaDon);
        finish();
        overridePendingTransition(R.anim.slide_in_left,R.anim.slide_out_right);
    }

    private boolean internetVarmi(){
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED)
            return true;
        else
            return false;
    }

    private String farkiHesapla() {
        long yirmidortSaat = 86400000;
        long asilzaman = yirmidortSaat - zaman;
        long saat = asilzaman / 3600000;
        long dakika = (asilzaman / 60000) - ((asilzaman / 3600000) * 60);
        long saniye = (asilzaman / 1000) - ((asilzaman / 60000) * 60);


        if(saniye > -1) {
            begenebilmeDurumu = "begenilemez";
            if(String.valueOf(saat).length() < 2 && String.valueOf(dakika).length() < 2 && String.valueOf(saniye).length() < 2){
                return "0"+saat + ":0" + dakika + ":0" + saniye;
            }
            else if(String.valueOf(saat).length() < 2 && String.valueOf(dakika).length() < 2 && String.valueOf(saniye).length() == 2){
                return "0"+saat + ":0" + dakika + ":" + saniye;

            }
            else if(String.valueOf(saat).length() == 2 && String.valueOf(dakika).length() < 2 && String.valueOf(saniye).length() == 2){
                return ""+saat + ":0" + dakika + ":" + saniye;
            }
            else if(String.valueOf(saat).length() < 2 && String.valueOf(dakika).length() == 2 && String.valueOf(saniye).length() == 2){
                return "0"+saat + ":" + dakika + ":" + saniye;
            }
            else if(String.valueOf(saat).length() == 2 && String.valueOf(dakika).length() == 2 && String.valueOf(saniye).length() < 2){
                return ""+saat + ":" + dakika + ":0" + saniye;
            }
            else if(String.valueOf(saat).length() < 2 && String.valueOf(dakika).length() == 2 && String.valueOf(saniye).length() < 2){
                return "0"+saat + ":" + dakika + ":0" + saniye;
            }
            else if(String.valueOf(saat).length() == 2 && String.valueOf(dakika).length() < 2 && String.valueOf(saniye).length() < 2){
                return ""+saat + ":0" + dakika + ":0" + saniye;
            }
            else{
                return ""+saat + ":" + dakika + ":" + saniye;
            }
        }
        else {

            return "Bekleyin...";
        }


    }

    private void FotolariGetir() {
        fotolar.clear();
        fotolar = new ArrayList<>();
        fotoRefs.clear();
        fotoRefs = new ArrayList<>();

        ArrayList<ImageView> fotoViewList = new ArrayList<ImageView>();
        ImageView[] fotoView = new ImageView[fotoViewList.size()];
        fotoViewList.add(foto_1);
        fotoViewList.add(foto_2);
        fotoViewList.add(foto_3);
        fotoViewList.add(foto_4);
        fotoViewList.add(foto_5);
        fotoViewList.add(foto_6);
        fotoView = fotoViewList.toArray(fotoView);

        if (internetVarmi()) {
            DatabaseReference ref = FirebaseDatabase.getInstance().getReference("usersF").child(kiminmis);
            ref.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    user = dataSnapshot.getValue(User.class);

                    if (user.getGizlilik_foto().equals("0")) {
                        if (dataSnapshot.hasChild("begendiklerim")) {
                            if (dataSnapshot.child("begendiklerim").hasChild(fuser.getUid())) {
                                for (DataSnapshot ds : dataSnapshot.child("fotograflarim").getChildren()) {
                                    if(!ds.getKey().equals("kapak") && !ds.getKey().equals("pp")) {
                                        Glide.with(DigerProfilActivity.this)
                                                .asBitmap()
                                                .load(ds.getValue(String.class))
                                                .into(fotoViewList.get(Integer.valueOf(ds.getKey()) - 1));
                                        if(Integer.valueOf(ds.getKey()) == 1) {
                                            r1varmi = true;
                                            uf1 = Uri.parse(ds.getValue(String.class));
                                        }
                                        if(Integer.valueOf(ds.getKey()) == 2){
                                            r2varmi = true;
                                            uf2 = Uri.parse(ds.getValue(String.class));
                                        }
                                        if(Integer.valueOf(ds.getKey()) == 3){
                                            r3varmi = true;
                                            uf3 = Uri.parse(ds.getValue(String.class));
                                        }
                                        if(Integer.valueOf(ds.getKey()) == 4){
                                            r4varmi = true;
                                            uf4 = Uri.parse(ds.getValue(String.class));
                                        }
                                        if(Integer.valueOf(ds.getKey()) == 5){
                                            r5varmi = true;
                                            uf5 = Uri.parse(ds.getValue(String.class));
                                        }
                                        if(Integer.valueOf(ds.getKey()) == 6){
                                            r6varmi = true;
                                            uf6 = Uri.parse(ds.getValue(String.class));
                                        }
                                    }
                                    else{
                                        /*ufkapak = Uri.parse(ds.getValue(String.class));
                                        Glide.with(DigerProfilActivity.this)
                                                .asBitmap()
                                                .load(ds.getValue(String.class))
                                                .into(kapak);*/
                                    }
                                }
                                foto_yok.setVisibility(View.GONE);
                                //foto_yok.setVisibility(View.VISIBLE);
                                // FotoSayisi();
                            } else {
                                //Toast.makeText(DigerProfilActivity.this, "Bu kullanıcının fotoğrafları gizlenmiş.", Toast.LENGTH_SHORT).show();
                                // FotoSayisi();
                            }
                        } else {
                            //Toast.makeText(DigerProfilActivity.this, "Bu kullanıcının fotoğrafları gizlenmiş.", Toast.LENGTH_SHORT).show();
                            //FotoSayisi();
                        }
                    }
                    else if (user.getGizlilik_foto().equals("1")) {
                        if (dataSnapshot.hasChild("begenenler")) {
                            if (dataSnapshot.child("begenenler").hasChild(fuser.getUid())) {
                                if (dataSnapshot.hasChild("fotograflarim")) {
                                    for (DataSnapshot ds : dataSnapshot.child("fotograflarim").getChildren()) {
                                        if(!ds.getKey().equals("kapak") && !ds.getKey().equals("pp")) {
                                            Glide.with(DigerProfilActivity.this)
                                                    .asBitmap()
                                                    .load(ds.getValue(String.class))
                                                    .into(fotoViewList.get(Integer.valueOf(ds.getKey()) - 1));
                                            if(Integer.valueOf(ds.getKey()) == 1) {
                                                r1varmi = true;
                                                uf1 = Uri.parse(ds.getValue(String.class));
                                            }
                                            if(Integer.valueOf(ds.getKey()) == 2){
                                                r2varmi = true;
                                                uf2 = Uri.parse(ds.getValue(String.class));
                                            }
                                            if(Integer.valueOf(ds.getKey()) == 3){
                                                r3varmi = true;
                                                uf3 = Uri.parse(ds.getValue(String.class));
                                            }
                                            if(Integer.valueOf(ds.getKey()) == 4){
                                                r4varmi = true;
                                                uf4 = Uri.parse(ds.getValue(String.class));
                                            }
                                            if(Integer.valueOf(ds.getKey()) == 5){
                                                r5varmi = true;
                                                uf5 = Uri.parse(ds.getValue(String.class));
                                            }
                                            if(Integer.valueOf(ds.getKey()) == 6){
                                                r6varmi = true;
                                                uf6 = Uri.parse(ds.getValue(String.class));
                                            }
                                        }
                                        else{
                                            /*ufkapak = Uri.parse(ds.getValue(String.class));
                                            Glide.with(DigerProfilActivity.this)
                                                    .asBitmap()
                                                    .load(ds.getValue(String.class))
                                                    .into(kapak);*/
                                        }
                                    }
                                    foto_yok.setVisibility(View.GONE);
                                }
                                //foto_yok.setVisibility(View.VISIBLE);
                                // FotoSayisi();
                            } else {
                                //Toast.makeText(DigerProfilActivity.this, "Bu kullanıcının fotoğrafları gizlenmiş.", Toast.LENGTH_SHORT).show();
                                // FotoSayisi();
                            }
                        } else {
                            //Toast.makeText(DigerProfilActivity.this, "Bu kullanıcının fotoğrafları gizlenmiş.", Toast.LENGTH_SHORT).show();
                            //FotoSayisi();
                        }
                    }
                    else if (user.getGizlilik_foto().equals("2")) {
                        if (dataSnapshot.hasChild("mesajlastiklarim")) {
                            if (dataSnapshot.child("mesajlastiklarim").hasChild(fuser.getUid())) {
                                if (dataSnapshot.hasChild("fotograflarim")) {
                                    for (DataSnapshot ds : dataSnapshot.child("fotograflarim").getChildren()) {
                                        if(!ds.getKey().equals("kapak") && !ds.getKey().equals("pp")) {
                                            Glide.with(DigerProfilActivity.this)
                                                    .asBitmap()
                                                    .load(ds.getValue(String.class))
                                                    .into(fotoViewList.get(Integer.valueOf(ds.getKey()) - 1));
                                            if(Integer.valueOf(ds.getKey()) == 1) {
                                                r1varmi = true;
                                                uf1 = Uri.parse(ds.getValue(String.class));
                                            }
                                            if(Integer.valueOf(ds.getKey()) == 2){
                                                r2varmi = true;
                                                uf2 = Uri.parse(ds.getValue(String.class));
                                            }
                                            if(Integer.valueOf(ds.getKey()) == 3){
                                                r3varmi = true;
                                                uf3 = Uri.parse(ds.getValue(String.class));
                                            }
                                            if(Integer.valueOf(ds.getKey()) == 4){
                                                r4varmi = true;
                                                uf4 = Uri.parse(ds.getValue(String.class));
                                            }
                                            if(Integer.valueOf(ds.getKey()) == 5){
                                                r5varmi = true;
                                                uf5 = Uri.parse(ds.getValue(String.class));
                                            }
                                            if(Integer.valueOf(ds.getKey()) == 6){
                                                r6varmi = true;
                                                uf6 = Uri.parse(ds.getValue(String.class));
                                            }
                                        }else{
                                            /*ufkapak = Uri.parse(ds.getValue(String.class));
                                            Glide.with(DigerProfilActivity.this)
                                                    .asBitmap()
                                                    .load(ds.getValue(String.class))
                                                    .into(kapak);*/
                                        }
                                    }
                                    foto_yok.setVisibility(View.GONE);
                                }
                                //foto_yok.setVisibility(View.VISIBLE);
                                // FotoSayisi();
                            } else {
                               // Toast.makeText(DigerProfilActivity.this, "Bu kullanıcının fotoğrafları gizlenmiş.", Toast.LENGTH_SHORT).show();
                                // FotoSayisi();
                            }
                        } else {
                           // Toast.makeText(DigerProfilActivity.this, "Bu kullanıcının fotoğrafları gizlenmiş.", Toast.LENGTH_SHORT).show();
                            //FotoSayisi();
                        }
                    }
                    else if (user.getGizlilik_foto().equals("3")) {
                        //Toast.makeText(DigerProfilActivity.this, "Bu kullanıcının fotoğrafları gizlenmiş.", Toast.LENGTH_SHORT).show();
                    }
                    else if (user.getGizlilik_foto().equals("4")) {
                        if (dataSnapshot.hasChild("fotograflarim")) {
                            for (DataSnapshot ds : dataSnapshot.child("fotograflarim").getChildren()) {
                                if(!ds.getKey().equals("kapak") && !ds.getKey().equals("pp")) {
                                    Glide.with(DigerProfilActivity.this)
                                            .asBitmap()
                                            .load(ds.getValue(String.class))
                                            .into(fotoViewList.get(Integer.valueOf(ds.getKey()) - 1));
                                    if(Integer.valueOf(ds.getKey()) == 1) {
                                        r1varmi = true;
                                        uf1 = Uri.parse(ds.getValue(String.class));
                                    }
                                    if(Integer.valueOf(ds.getKey()) == 2){
                                        r2varmi = true;
                                        uf2 = Uri.parse(ds.getValue(String.class));
                                    }
                                    if(Integer.valueOf(ds.getKey()) == 3){
                                        r3varmi = true;
                                        uf3 = Uri.parse(ds.getValue(String.class));
                                    }
                                    if(Integer.valueOf(ds.getKey()) == 4){
                                        r4varmi = true;
                                        uf4 = Uri.parse(ds.getValue(String.class));
                                    }
                                    if(Integer.valueOf(ds.getKey()) == 5){
                                        r5varmi = true;
                                        uf5 = Uri.parse(ds.getValue(String.class));
                                    }
                                    if(Integer.valueOf(ds.getKey()) == 6){
                                        r6varmi = true;
                                        uf6 = Uri.parse(ds.getValue(String.class));
                                    }
                                }
                                else{
                                    /*ufkapak = Uri.parse(ds.getValue(String.class));
                                    Glide.with(DigerProfilActivity.this)
                                            .asBitmap()
                                            .load(ds.getValue(String.class))
                                            .into(kapak);*/
                                }
                            }
                            foto_yok.setVisibility(View.GONE);
                        }
                        //foto_yok.setVisibility(View.VISIBLE);
                        //FotoSayisi();
                    }

                    kullaniciAdi = user.getUsernamef();

                    if (user.getGizlilik_ad().equals("0")) {
                        if (dataSnapshot.hasChild("begendiklerim")) {
                            if (dataSnapshot.child("begendiklerim").hasChild(fuser.getUid())) {
                                yas.setText(user.getDg());
                                ad.setText(user.getAd().substring(0, 1).toUpperCase() + user.getAd().substring(1));
                            } else {
                                ad.setText(AdiSansurle(user));
                                yas.setText(YasSansurle(user));
                            }
                        } else {
                            ad.setText(AdiSansurle(user));
                            yas.setText(YasSansurle(user));
                        }
                    }
                    else if (user.getGizlilik_ad().equals("1")) {
                        if (dataSnapshot.hasChild("begenenler")) {
                            if (dataSnapshot.child("begenenler").hasChild(fuser.getUid())) {
                                yas.setText(user.getDg());
                                ad.setText(user.getAd().substring(0, 1).toUpperCase() + user.getAd().substring(1));
                            } else {
                                ad.setText(AdiSansurle(user));
                                yas.setText(YasSansurle(user));
                            }
                        } else {
                            ad.setText(AdiSansurle(user));
                            yas.setText(YasSansurle(user));
                        }
                    }
                    else if (user.getGizlilik_ad().equals("2")) {
                        if (dataSnapshot.hasChild("mesajlastiklarim")) {
                            if (dataSnapshot.child("mesajlastiklarim").hasChild(fuser.getUid())) {
                                yas.setText(user.getDg());
                                ad.setText(user.getAd().substring(0, 1).toUpperCase() + user.getAd().substring(1));
                            }
                            else {
                                ad.setText(AdiSansurle(user));
                                yas.setText(YasSansurle(user));
                            }
                        }
                        else {
                            ad.setText(AdiSansurle(user));
                            yas.setText(YasSansurle(user));
                        }
                    }
                    else if (user.getGizlilik_ad().equals("3")) {
                        ad.setText(AdiSansurle(user));
                        yas.setText(YasSansurle(user));
                    }
                    else if (user.getGizlilik_ad().equals("4")) {
                        ad.setText(user.getAd().substring(0, 1).toUpperCase() + user.getAd().substring(1));
                        yas.setText(user.getDg());
                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
            DatabaseReference ref2 = FirebaseDatabase.getInstance().getReference("usersF").child(fuser.getUid());
            ref2.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {


                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(saniyeSay != null)
            saniyeSay.interrupt();
        saniyeDevam = false;
        ppyuklendimi = false;
    }

    private void ResimeNeOlsun() {
        AlertDialog.Builder builder = new AlertDialog.Builder(DigerProfilActivity.this);
        builder.setCancelable(true);
        String[] secenekler = {"Resmi Görüntüle", "Resmi Şikayet Et"};


        builder.setItems(secenekler, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                butonTiklandiMi = false;
                if(which == 0){
                    if (deger.equals("1")) {
                        Intent intent = new Intent(DigerProfilActivity.this, ResimActivity.class);
                        intent.putExtra("userid", user.getUid());
                        intent.putExtra("resimURL", uf1.toString());
                        intent.putExtra("resimBenimMi", "hayir");
                        startActivity(intent);
                    } else if (deger.equals("2")) {
                        Intent intent = new Intent(DigerProfilActivity.this, ResimActivity.class);
                        intent.putExtra("userid", user.getUid());
                        intent.putExtra("resimURL", uf2.toString());
                        intent.putExtra("resimBenimMi", "hayir");
                        startActivity(intent);
                    } else if (deger.equals("3")) {
                        Intent intent = new Intent(DigerProfilActivity.this, ResimActivity.class);
                        intent.putExtra("userid", user.getUid());
                        intent.putExtra("resimURL", uf3.toString());
                        intent.putExtra("resimBenimMi", "hayir");
                        startActivity(intent);
                    } else if (deger.equals("4")) {
                        Intent intent = new Intent(DigerProfilActivity.this, ResimActivity.class);
                        intent.putExtra("userid", user.getUid());
                        intent.putExtra("resimURL", uf4.toString());
                        intent.putExtra("resimBenimMi", "hayir");
                        startActivity(intent);
                    } else if (deger.equals("5")) {
                        Intent intent = new Intent(DigerProfilActivity.this, ResimActivity.class);
                        intent.putExtra("userid", user.getUid());
                        intent.putExtra("resimURL", uf5.toString());
                        intent.putExtra("resimBenimMi", "hayir");
                        startActivity(intent);
                    } else if (deger.equals("6")) {
                        Intent intent = new Intent(DigerProfilActivity.this, ResimActivity.class);
                        intent.putExtra("userid", user.getUid());
                        intent.putExtra("resimURL", uf6.toString());
                        intent.putExtra("resimBenimMi", "hayir");
                        startActivity(intent);
                    } else if (deger.equals("pp")) {
                        Intent intent = new Intent(DigerProfilActivity.this, ResimActivity.class);
                        intent.putExtra("userid", user.getUid());
                        intent.putExtra("resimURL", ufpp.toString());
                        intent.putExtra("resimBenimMi", "hayir");
                        startActivity(intent);
                    }
                }
                else if(which == 1) {
                    if(deger.equals("pp"))
                        SikayetEdilsinMi(ufpp.toString());
                    else if(deger.equals("1"))
                        SikayetEdilsinMi(uf1.toString());
                    else if(deger.equals("2"))
                        SikayetEdilsinMi(uf2.toString());
                    else if(deger.equals("3"))
                        SikayetEdilsinMi(uf3.toString());
                    else if(deger.equals("4"))
                        SikayetEdilsinMi(uf4.toString());
                    else if(deger.equals("5"))
                        SikayetEdilsinMi(uf5.toString());
                    else if(deger.equals("6"))
                        SikayetEdilsinMi(uf6.toString());

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

    private void SikayetEdilsinMi(final String resimURL){

        Dialog dialog = new Dialog(DigerProfilActivity.this);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.dialog_dizayn5);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        LinearLayout lay1 = dialog.findViewById(R.id.lay1);
        ProgressBar pbar = dialog.findViewById(R.id.pbar);
        pbar.getIndeterminateDrawable().setColorFilter(ortaRenk,android.graphics.PorterDuff.Mode.MULTIPLY);
        TextView baslik = dialog.findViewById(R.id.baslik);
        TextView aciklama = dialog.findViewById(R.id.aciklama);
        aciklama.setMovementMethod(new ScrollingMovementMethod());
        Button buton = dialog.findViewById(R.id.buton); // Hayır
        Button buton2 = dialog.findViewById(R.id.buton2); // Evet

        baslik.setText("Şikayet edilsin mi?");
        aciklama.setText("Bu resim uygunsuz mu?");
        buton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        buton2.setOnClickListener(new View.OnClickListener() { // EVet
            @Override
            public void onClick(View v) {
                lay1.setVisibility(View.GONE);
                pbar.setVisibility(View.VISIBLE);
                DatabaseReference sikayetRef = FirebaseDatabase.getInstance().getReference("ResimSikayet").child(UUID.randomUUID().toString());
                sikayetRef.child("sikayet_eden").setValue(fuser.getUid());
                sikayetRef.child("sikayet_edilen").setValue(kiminmis);
                sikayetRef.child("resimURL").setValue(resimURL);
                Toast.makeText(DigerProfilActivity.this, "Bize bildirdiğiniz için teşekkür ederiz.", Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            }
        });
        dialog.show();
    }
    private void BegeniHesapla() {
        begeniler = new ArrayList<>();
        begeniSayisi = 0;
        bulunanDeger = 0;
        final DatabaseReference ref = FirebaseDatabase.getInstance().getReference("usersF").child(kiminmis);
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.hasChild("begenenler")){
                    childSayisi = dataSnapshot.child("begenenler").getChildrenCount();
                    for(DataSnapshot ds : dataSnapshot.child("begenenler").getChildren()){
                        if(bulunanDeger < childSayisi) {
                            String begeni = String.valueOf(ds.child("kac_kez_begendim").getValue(Long.class));
                            begeniler.add(begeni);
                            begeniSayisi += Integer.parseInt(begeniler.get(Integer.parseInt(String.valueOf(bulunanDeger))));
                            if(begeniler.size() == childSayisi) {
                                Collections.sort(begeniler, new Comparator<String>() {
                                    @Override
                                    public int compare(String o1, String o2) {
                                        return Integer.parseInt(o2) - Integer.parseInt(o1);
                                    }
                                });

                                EnCokKimBegenmis();
                            }
                            ref.child("begeni_sayisi").setValue(String.valueOf(begeniSayisi));
                            begeni_sayisi.setText(""+begeniSayisi);
                            bulunanDeger++;
                        }
                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        if(begendim) {
            /*new Timer().schedule(new TimerTask() {
                @Override
                public void run() {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            final DatabaseReference ref = FirebaseDatabase.getInstance().getReference("usersF").child(kiminmis);
                            ref.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    if (dataSnapshot.hasChild("begenenler")) {
                                        ref.child("begenenler").child(fuser.getUid()).child("suanki_zaman").setValue(ServerValue.TIMESTAMP);
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });
                        }
                    });
                }
            }, 0, 1000);*/
        }
    }
    private void EnCokKimBegenmis() {
        if(begeniler.size() > 1) {
            if (!begeniler.get(0).equals(begeniler.get(1))) {
                DatabaseReference ref = FirebaseDatabase.getInstance().getReference("usersF").child(kiminmis).child("begenenler");
                ref.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot ds : dataSnapshot.getChildren()) {
                            if (String.valueOf(ds.child("kac_kez_begendim").getValue(Long.class)).equals(begeniler.get(0))) {
                                DatabaseReference aref = FirebaseDatabase.getInstance().getReference("usersF").child(ds.getKey());
                                aref.addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        enCokBegenen = dataSnapshot.getValue(User.class);
                                        encokbegenenkismi.setVisibility(View.VISIBLE);
                                        encokbegenensayisi.setText("x" + begeniler.get(0));
                                        //encokbegenenisim.setText(enCokBegenen.getUsernamef());
                                        if(dataSnapshot.hasChild("engellediklerim")){
                                            if(!dataSnapshot.child("engellediklerim").hasChild(fuser.getUid())){
                                                if(!engellediklerim.contains(enCokBegenen.getUid())){
                                                    Glide.with(getApplicationContext())
                                                            .asBitmap()
                                                            .load(Uri.parse(dataSnapshot.child("fotograflarim").child("pp").getValue(String.class)))
                                                            .into(encokbegenenfoto);
                                                }
                                            }
                                        }else{
                                            if(!engellediklerim.contains(enCokBegenen.getUid())) {
                                                Glide.with(getApplicationContext())
                                                        .asBitmap()
                                                        .load(Uri.parse(dataSnapshot.child("fotograflarim").child("pp").getValue(String.class)))
                                                        .into(encokbegenenfoto);
                                            }
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                    }
                                });

                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            } else {
                encokbegenenkismi.setVisibility(View.GONE);
            }
        }else{
            DatabaseReference ref = FirebaseDatabase.getInstance().getReference("usersF").child(kiminmis).child("begenenler");
            ref.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for (DataSnapshot ds : dataSnapshot.getChildren()) {
                        if(begeniler.size() >= 1)
                            if (String.valueOf(ds.child("kac_kez_begendim").getValue(Long.class)).equals(begeniler.get(0))) {
                                DatabaseReference aref = FirebaseDatabase.getInstance().getReference("usersF").child(ds.getKey());
                                aref.addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        enCokBegenen = dataSnapshot.getValue(User.class);
                                        encokbegenenkismi.setVisibility(View.VISIBLE);
                                        encokbegenensayisi.setText("x" + begeniler.get(0));
                                        Glide.with(getApplicationContext())
                                                .asBitmap()
                                                .load(Uri.parse(dataSnapshot.child("fotograflarim").child("pp").getValue(String.class)))
                                                .into(encokbegenenfoto);
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {

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
    }
    @Override
    protected void onResume() {
        super.onResume();

        FirebaseDatabase.getInstance().getReference("usersF").child(kiminmis).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                TasarimDegistir(dataSnapshot.child("tas_profil").getValue(String.class));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        /*SharedPreferences tas_shared = getSharedPreferences("TasarimBilgileri",MODE_PRIVATE);
        TasarimDegistir(tas_shared.getString("tasarim_arayuz","1"));*/
    }

    private void Begen() {
        notify = true;
        final FirebaseUser fuser = FirebaseAuth.getInstance().getCurrentUser();
        final DatabaseReference begenData = FirebaseDatabase.getInstance().getReference("usersF").child(kiminmis);
        final DatabaseReference benimData = FirebaseDatabase.getInstance().getReference("usersF").child(fuser.getUid());

        FirebaseDatabase.getInstance().getReference("usersF").child(kiminmis).child("begeni_bildirim_durumu").setValue("var");
        begenData.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.hasChild("son_begenenler")){
                    if(dataSnapshot.child("son_begenenler").hasChild(fuser.getUid())){
                        int begeni = Integer.parseInt(dataSnapshot.child("son_begenenler").child(fuser.getUid()).getValue(String.class));
                        dataSnapshot.child("son_begenenler").child(fuser.getUid()).getRef().setValue(""+(begeni+1));
                    }
                    else
                        dataSnapshot.child("son_begenenler").child(fuser.getUid()).getRef().setValue("1");
                }
                else
                    dataSnapshot.child("son_begenenler").child(fuser.getUid()).getRef().setValue("1");

                if(dataSnapshot.child("bildirimler").child("begeni").getValue().equals("gelsin")){
                    sendNotification(kiminmis,Kullanici.getAd(),"1");
                }
                if(dataSnapshot.hasChild("begenenler")){
                    if (dataSnapshot.child("begenenler").hasChild(fuser.getUid())) {
                        if(!dataSnapshot.child("begenenler").child(fuser.getUid()).hasChild("kac_kez_begendim")){
                            benimData.child("begendiklerim").child(kiminmis).setValue("");
                            dataSnapshot.child("begenenler").child(fuser.getUid()).child("kac_kez_begendim").getRef().setValue(1);
                            dataSnapshot.child("begenenler").child(fuser.getUid()).child("son_begenme").getRef().setValue(ServerValue.TIMESTAMP);
                            dataSnapshot.child("begenenler").child(fuser.getUid()).child("suanki_zaman").getRef().setValue(ServerValue.TIMESTAMP).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    begendim = true;
                                    TumBilgileriCagir();
                                    /*Intent intent = getIntent();
                                    finish();
                                    startActivity(intent);*/
                                }
                            });

                        }else{
                            int yeniDeger = dataSnapshot.child("begenenler").child(fuser.getUid()).child("kac_kez_begendim").getValue(Integer.class);
                            yeniDeger += 1;
                            dataSnapshot.child("begenenler").child(fuser.getUid()).child("kac_kez_begendim").getRef().setValue(yeniDeger);
                            benimData.child("begendiklerim").child(kiminmis).setValue("");
                            dataSnapshot.child("begenenler").child(fuser.getUid()).child("son_begenme").getRef().setValue(ServerValue.TIMESTAMP);
                            dataSnapshot.child("begenenler").child(fuser.getUid()).child("suanki_zaman").getRef().setValue(ServerValue.TIMESTAMP).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    begendim = true;
                                    TumBilgileriCagir();
                                    /*Intent intent = getIntent();
                                    finish();
                                    startActivity(intent);*/
                                }
                            });

                        }
                    }
                    else{
                        dataSnapshot.child("begenenler").child(fuser.getUid()).child("son_begenme").getRef().setValue(ServerValue.TIMESTAMP);
                        benimData.child("begendiklerim").child(kiminmis).setValue("");
                        dataSnapshot.child("begenenler").child(fuser.getUid()).child("kac_kez_begendim").getRef().setValue(1);
                        dataSnapshot.child("begenenler").child(fuser.getUid()).child("suanki_zaman").getRef().setValue(ServerValue.TIMESTAMP).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                begendim = true;
                                TumBilgileriCagir();
                                /*Intent intent = getIntent();
                                finish();
                                startActivity(intent);*/
                            }
                        });

                    }
                }
                else{
                    dataSnapshot.child("begenenler").child(fuser.getUid()).child("son_begenme").getRef().setValue(ServerValue.TIMESTAMP);
                    benimData.child("begendiklerim").child(kiminmis).setValue("");
                    dataSnapshot.child("begenenler").child(fuser.getUid()).child("kac_kez_begendim").getRef().setValue(1);
                    dataSnapshot.child("begenenler").child(fuser.getUid()).child("suanki_zaman").getRef().setValue(ServerValue.TIMESTAMP).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            begendim = true;
                            TumBilgileriCagir();
                            /*Intent intent = getIntent();
                            finish();
                            startActivity(intent);*/
                        }
                    });

                }
                butonTiklandiMi = false;
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }

    private void sendNotification(final String kiminmis, final String ad, String s) {
        DatabaseReference tokens = FirebaseDatabase.getInstance().getReference("Tokenlar");
        Query query = tokens.orderByKey().equalTo(kiminmis);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot sn : dataSnapshot.getChildren()){
                    Token token = sn.getValue(Token.class);
                    Data data = new Data(fuser.getUid(), "Sizi beğenenin kim olduğunu görmek için tıklayın.", "Biri sizi beğendi!",kiminmis,R.drawable.kimoo_bildirim);

                    Sender sender = new Sender(data,token.getToken());
                    apiService.sendNotification(sender)
                            .enqueue(new Callback<MyResponse>() {
                                @Override
                                public void onResponse(Call<MyResponse> call, retrofit2.Response<MyResponse> response) {
                                    //Toast.makeText(DigerProfilActivity.this, "Başarısız! " + response.message(), Toast.LENGTH_SHORT).show();
                                }

                                @Override
                                public void onFailure(Call<MyResponse> call, Throwable t) {

                                }
                            });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void favoriAyarla() {
        FirebaseUser fuser = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference favoriData = FirebaseDatabase.getInstance().getReference("usersF").child(fuser.getUid());
        favoriData.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Kullanici = dataSnapshot.getValue(User.class);
                if (dataSnapshot.hasChild("favorilerim")) {
                    if (dataSnapshot.child("favorilerim").hasChild(kiminmis)) {
                        dataSnapshot.child("favorilerim").child(kiminmis).getRef().removeValue();

                        SharedPreferences spFavorilerim = getSharedPreferences("Favorilerim",MODE_PRIVATE);
                        for (int i = 0; i < 10; i++){
                            if (spFavorilerim.getString("" + i, "null").equals(kiminmis)) {
                                SharedPreferences.Editor editorFav = spFavorilerim.edit();
                                editorFav.remove("" + i);
                                editorFav.commit();
                            }
                        }
                    }
                    else {
                        if(dataSnapshot.child("favorilerim").getChildrenCount() < Kullanici.getFavori_sayim()) {
                            dataSnapshot.child("favorilerim").child(kiminmis).getRef().setValue(ServerValue.TIMESTAMP);
                        }
                        else{
                            Dialog dialog = new Dialog(DigerProfilActivity.this);
                            dialog.setContentView(R.layout.dialog_dizayn2);
                            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

                            TextView baslik = dialog.findViewById(R.id.baslik);
                            TextView aciklama = dialog.findViewById(R.id.aciklama);
                            Button buton = dialog.findViewById(R.id.buton);
                            LinearLayout lay1 = dialog.findViewById(R.id.lay1);
                            ProgressBar pbar = dialog.findViewById(R.id.pbar);
                            baslik.setText("Favori Sayısı Arttır");
                            if(fiyatlar != null) {
                                aciklama.setText("Kısıtlı sayıda kişiyi favorilerinize ekleyebilirsiniz. Bu sayıyı arttırmak için gerekli miktar "+fiyatlar.getFavori_ekleme()+", sizde olan " + Kullanici.getKp() + "KP");
                                if (Kullanici.getKp() >= fiyatlar.getFavori_ekleme()) {
                                    buton.setText("+1 Arttır");
                                    buton.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            lay1.setVisibility(View.GONE);
                                            pbar.setVisibility(View.VISIBLE);
                                            DatabaseReference ref = FirebaseDatabase.getInstance().getReference("usersF").child(Kullanici.getUid());
                                            ref.child("harcamalarim").child("favsayisi_"+Kullanici.getFavori_sayim()).child("onceki_kp").setValue(Kullanici.getKp());
                                            ref.child("harcamalarim").child("favsayisi_"+Kullanici.getFavori_sayim()).child("zaman").setValue(ServerValue.TIMESTAMP);
                                            ref.child("kp").setValue(Kullanici.getKp() - fiyatlar.getFavori_ekleme()).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(@NonNull Void unused) {
                                                    ref.child("favori_sayim").setValue(Kullanici.getFavori_sayim() + 1).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                        @Override
                                                        public void onSuccess(@NonNull Void unused) {
                                                            Kullanici.setKp(Kullanici.getKp() - fiyatlar.getFavori_ekleme());
                                                            Kullanici.setFavori_sayim(Kullanici.getFavori_sayim() + 1);
                                                            dataSnapshot.child("favorilerim").child(kiminmis).getRef().setValue(ServerValue.TIMESTAMP).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                @Override
                                                                public void onSuccess(@NonNull Void unused) {
                                                                    //Toast.makeText(DigerProfilActivity.this, "Favori listenize eklediniz.", Toast.LENGTH_SHORT).show();
                                                                    dialog.dismiss();
                                                                }
                                                            });
                                                        }
                                                    });
                                                }
                                            }).addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    lay1.setVisibility(View.VISIBLE);
                                                    pbar.setVisibility(View.GONE);
                                                    Toast.makeText(DigerProfilActivity.this, "Başarısız!", Toast.LENGTH_SHORT).show();
                                                }
                                            });
                                        }
                                    });
                                } else {
                                    buton.setText("Markete Git");
                                    buton.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            lay1.setVisibility(View.GONE);
                                            pbar.setVisibility(View.VISIBLE);
                                            startActivity(new Intent(DigerProfilActivity.this, MarketActivity.class));
                                        }
                                    });
                                }
                            }
                            dialog.show();
                        }
                    }
                } else {
                    dataSnapshot.child("favorilerim").child(kiminmis).getRef().setValue(ServerValue.TIMESTAMP);
                    Toast.makeText(DigerProfilActivity.this, "Favori listenize eklediniz.", Toast.LENGTH_SHORT).show();
                }
                butonTiklandiMi = false;
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                butonTiklandiMi = false;
            }
        });
    }


}
