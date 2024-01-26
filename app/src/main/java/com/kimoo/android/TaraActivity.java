package com.kimoo.android;

import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.Shader;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.VectorDrawable;
import android.net.Uri;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.CountDownTimer;
import android.provider.Settings;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicBlur;
import android.text.TextUtils;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.kimoo.android.Model.Fiyatlar;
import com.kimoo.android.Model.User;
import com.kimoo.android.Model.Yer;
import com.kimoo.android.bildirimler.Data;
import com.kimoo.android.bildirimler.Token;
import com.kimoo.android.extra.AppRater;
import com.kimoo.android.extra.ArkadaBul;
import com.kimoo.android.extra.ArkadaBul2;
import com.kimoo.android.extra.GorevBilgi;
import com.kimoo.android.extra.MesajlarimItemKullaniciVeOda;
import com.kimoo.android.extra.ResimIndir;
import com.kimoo.android.extra.ResimKullaniciAdapter;
import com.kimoo.android.extra.TariheGoreListeleSadeceResim;
import com.kimoo.android.extra.TasarimRenginiGetir;
import com.kimoo.android.extra.YeniKisiBuldum;
import com.kimoo.android.extra.YerAdapter;
import com.kimoo.android.fragments.Favorilerim;
import com.kimoo.android.fragments.SuanBulunanlar;
import com.kimoo.android.fragments.TumGorduklerim;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import de.hdodenhof.circleimageview.CircleImageView;


public class TaraActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener {
    User Kullanici;
    Button favoriBTN, bulduklarimBTN, yenibulBTN;
    RecyclerView bulduklarimRV, favorilerimRV, yerlerRV, dialogRV;
    private SwipeRefreshLayout swipeRefreshLayout;
    private ImageView bulma, bulunma, gorunmeyenFoto, tik1, tik2, tik3, tik4, tik5, info, page1, page2, page3, page4, page5, damga, solimg, sagimg, oncekiIcon, sonrakiIcon, ev_ic, mesajlar_arka, ipucu1, ipucu2;
    CircleImageView mesajBildirim, begeniBildirim, profilFotosu, bulduklarimBildirim;
    private RelativeLayout solBTN, sagBTN, gorev1, gorev2, gorev3, gorev4, gorev5, baslikKismi, pbar, profil_rel, mesajlar, profil, animasyonRel, destekBTN, tuto_gec, background,
            gorevler_yazi_kismi, oncekiSayfa, sonrakiSayfa;
    TextView gorevYazi1, gorevYazi2, gorevYazi3, gorevYazi4, gorevYazi5, odul, baslik, favori_sayisi, kp_yazi, animasyonYazi, ipucu, mekan_yok;
    Dialog konumSorDialog;
    private GradientDrawable dialogIcinOzelBackground;
    private CardView gorevRel;
    BluetoothAdapter myAdapter;
    Intent serviceIntent;
    private ResimKullaniciAdapter resimKullaniciAdapterBul, resimKullaniciAdapterFav;
    private DataSnapshot tumUserlarDS;
    private boolean butonTiklandiMi = false, evSistemi = false;
    private GradientDrawable gorev_arka;
    static ProgressBar pbarBul;
    static ProgressBar pbarFav;
    ProgressBar pbarGorev;
    public static Fiyatlar fiyatlar;
    int gelendeger;
    int kacGorevVar;
    int sayRef;
    int sayBeg;
    int sistemGun;
    int KONTROL1;
    int KONTROL2;
    static int favPbarDeger;
    static int bulPbarDeger;
    int degistirDeger = 1, bir, iki, uc, dort, bes, kacGorevTamam, pageDegis = 10, normalGorevSayisi, favoriHedefHangisi, bulunmaHedefHangisi, referansHedefHangisi, mesajlasHedefHangisi, begeniHedefHangisi;
    FirebaseUser fuser;
    public static boolean gorduklerimde = false, favorilerde = false, tarada = false, mesajVarmi;
    long gorduklerimSayisi, gorduklerimSayisi2;
    List<TariheGoreListeleSadeceResim> mUsers, mUsers2;
    OutputStream outputStream;
    private LinearLayout pageRel, normalGorevLay, konuma_gidenlerRel, favoriKismi, bulunanlarKismi, favoriEkle, kp_buton, tuto;
    boolean baglantiVarMi, ilkKezMi;
    private Rect rect;
    private Long sure, zaman;
    Timer myTimer = new Timer();
    private String infoYazi, favoriHedef = null, bulunmaHedef = null, begeniHedef = null, referansHedef = null, mesajlasHedef = null, odulYazi;
    private DataSnapshot genelDS;
    SharedPreferences spReklam, spBulduklarim, spBulduklarimZaman, spFavorilerim, spFavorilerimZaman;
    String kacFavorimVar = null,
            kacKisiBegenmisim = null,
            kacKisiIleMesajlasmisim = null,
            kacReferansimVar = null,
            kacKisiBulmusum = null;
    private String baglanti;
    private Switch evdeMiyim;
    DatabaseReference myRef;
    ValueEventListener myRefListener;
    DataSnapshot dataSnapshotAsil;
    private String tasDegeri;
    public static int ilkRenk;
    public static int sonRenk;
    public static int ortaRenk;
    private List<String> gorevler, bulduklarimdakilerinUid = new ArrayList<>(), favorilerimdekilerinUid = new ArrayList<>();
    private List<DataSnapshot> mGelirler = new ArrayList<>();
    private List<String> gelirler = new ArrayList<>();
    private boolean dialogBul, dialogBulun, dialogFav, favoriyeBastim, bulduklarimaBastim, suanaBastim;
    public static GradientDrawable gradientYumusak, gradientNormal;
    private static int favorilerimSayisi, bulduklarimSayisi;
    private boolean ppYuklendi;
    private boolean dialogAktifMi, sonrakiKuculuyorMu, oncekiKuculuyorMu, reklam;
    int downX, upX, downX2, upX2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tara);
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

        tarada = true;
        mesajlar = findViewById(R.id.ana_mesaj);
        profil = findViewById(R.id.ana_profil);
        bulduklarimRV = findViewById(R.id.bulduklarimi_listele);
        favorilerimRV = findViewById(R.id.favorilerimi_listele);
        mesajBildirim = findViewById(R.id.mesajBildirim);
        begeniBildirim = findViewById(R.id.begeniBildirim);
        swipeRefreshLayout = findViewById(R.id.swipe_layout);

        pbarBul = findViewById(R.id.pbarBulduklarim);
        pbarFav = findViewById(R.id.pbarFavorilerim);
        pbarGorev = findViewById(R.id.pbar_gorev);
        gorunmeyenFoto = findViewById(R.id.gorunmeyenFoto);
        bulduklarimBildirim = findViewById(R.id.bulduklarimBildirim);
        gorevler_yazi_kismi = findViewById(R.id.gorevler_yazi_kismi);
        oncekiSayfa = findViewById(R.id.oncekiSayfa);
        sonrakiSayfa = findViewById(R.id.sonrakiSayfa);
        oncekiIcon = findViewById(R.id.oncekiIcon);
        sonrakiIcon = findViewById(R.id.sonrakiIcon);
        pbar = findViewById(R.id.pbar);
        solimg = findViewById(R.id.solimg);
        sagimg = findViewById(R.id.sagimg);
        solBTN = findViewById(R.id.solOk);
        sagBTN = findViewById(R.id.sagOk);
        bulma = findViewById(R.id.tarama);
        damga = findViewById(R.id.damga);
        pageRel = findViewById(R.id.pageRel);
        odul = findViewById(R.id.odul);
        info = findViewById(R.id.info);
        tik1 = findViewById(R.id.tik1);
        tik2 = findViewById(R.id.tik2);
        tik3 = findViewById(R.id.tik3);
        tik4 = findViewById(R.id.tik4);
        tik5 = findViewById(R.id.tik5);
        tuto = findViewById(R.id.tuto);
        tuto_gec = findViewById(R.id.tuto_gec);
        ipucu = findViewById(R.id.ipucu);
        ipucu1 = findViewById(R.id.ipucu1);
        ipucu2 = findViewById(R.id.ipucu2);
        profil_rel = findViewById(R.id.profil_rel);
        gorevYazi1 = findViewById(R.id.gorevyazi1);
        gorevYazi2 = findViewById(R.id.gorevyazi2);
        gorevYazi3 = findViewById(R.id.gorevyazi3);
        gorevYazi4 = findViewById(R.id.gorevyazi4);
        gorevYazi5 = findViewById(R.id.gorevyazi5);
        destekBTN = findViewById(R.id.destek);
        mekan_yok = findViewById(R.id.mekan_yok);
        gorev1 = findViewById(R.id.gorev1);
        gorev2 = findViewById(R.id.gorev2);
        gorev3 = findViewById(R.id.gorev3);
        gorev4 = findViewById(R.id.gorev4);
        gorev5 = findViewById(R.id.gorev5);
        page1 = findViewById(R.id.page1);
        page2 = findViewById(R.id.page2);
        page3 = findViewById(R.id.page3);
        page4 = findViewById(R.id.page4);
        page5 = findViewById(R.id.page5);
        baslik = findViewById(R.id.baslik);
        ev_ic = findViewById(R.id.ev_ic);
        evdeMiyim = findViewById(R.id.evdeMiyim);
        mesajlar_arka = findViewById(R.id.mesajlar_arka);
        baslikKismi = findViewById(R.id.baslikKismi);
        profilFotosu = findViewById(R.id.profilFotosu);
        bulunma = findViewById(R.id.bulunabilme);
        gorevRel = findViewById(R.id.gorev_kismi);
        favoriBTN = findViewById(R.id.favoriler_btn);
        bulduklarimBTN = findViewById(R.id.bulduklarim_btn);
        yenibulBTN = findViewById(R.id.yenibul_btn);
        background = findViewById(R.id.background);
        myAdapter = BluetoothAdapter.getDefaultAdapter();
        favoriKismi = findViewById(R.id.favoriKismi);
        bulunanlarKismi = findViewById(R.id.bulunanlarKismi);
        favori_sayisi = findViewById(R.id.favori_sayisi);
        favoriEkle = findViewById(R.id.favoriEkle);
        kp_yazi = findViewById(R.id.kp_yazi);
        kp_buton = findViewById(R.id.kp_buton);
        normalGorevLay = findViewById(R.id.normal_gorev);
        bulduklarimRV.setHasFixedSize(true);
        bulduklarimRV.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        favorilerimRV.setHasFixedSize(true);
        favorilerimRV.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        fuser = FirebaseAuth.getInstance().getCurrentUser();
        animasyonYazi = findViewById(R.id.animasyon_yazi);
        animasyonRel = findViewById(R.id.animasyon_rel);
        konuma_gidenlerRel = findViewById(R.id.konuma_gidenler);
        yerlerRV = findViewById(R.id.konumlar_rv);
        yerlerRV.setHasFixedSize(true);
        GridLayoutManager manager = new GridLayoutManager(TaraActivity.this, 2);

        swipeRefreshLayout.setOnRefreshListener(TaraActivity.this);
        swipeRefreshLayout.setColorSchemeResources(android.R.color.black);

        yerlerRV.setLayoutManager(manager);

        SharedPreferences sp = getSharedPreferences("SP_USER", MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString("Current_USERID", fuser.getUid());
        editor.apply();

        SharedPreferences tas_shared = getSharedPreferences("TasarimBilgileri", MODE_PRIVATE);
        tasDegeri = tas_shared.getString("tasarim_arayuz", "1");
        TasarimDegistir();

        SharedPreferences sharedPreferences = getSharedPreferences("ayarlar", Context.MODE_PRIVATE);
        SharedPreferences.Editor e = sharedPreferences.edit();
        if (!sharedPreferences.getString("giris", "").equals("ilkdegil")) {
            IlkKez();
            e.putString("giris", "ilkdegil");
            e.commit();
        }
        serviceIntent = new Intent(getApplicationContext(), ArkadaBul2.class);
        //updateToken(FirebaseInstanceId.getInstance().getToken());
        final FirebaseUser fuser = FirebaseAuth.getInstance().getCurrentUser();
        //final String DeviceToken = FirebaseInstanceId.getInstance().getToken();
        myRef = FirebaseDatabase.getInstance().getReference("usersF").child(fuser.getUid());
        ev_ic.setColorFilter(getResources().getColor(R.color.gri2));
        final DatabaseReference ref = FirebaseDatabase.getInstance().getReference("usersF");
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                tumUserlarDS = dataSnapshot;
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        /*background.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) gorevler_yazi_kismi.getLayoutParams();
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    downX2 = (int) event.getX();
                    return true;
                }

                else if (event.getAction() == MotionEvent.ACTION_UP) {
                    int fark = downX2 - upX2;
                    upX2 = (int) event.getX();
                    if (fark > 150){

                        Toast.makeText(TaraActivity.this, "sol", Toast.LENGTH_SHORT).show();
                    }
                    else if(fark < -150){

                        Toast.makeText(TaraActivity.this, "sag", Toast.LENGTH_SHORT).show();
                    }

                    layoutParams.rightMargin = 0;
                    layoutParams.leftMargin = -0;

                    return true;

                }
                else if (event.getAction() == MotionEvent.ACTION_MOVE) {
                    upX2 = (int) event.getX();
                    int fark = downX2 - upX2;
                    if(fark < 150 && fark > -150){

                    }
                    else if(fark < -150){
                    }
                    else if (fark > 150) {

                    }

                    return true;

                }
                return false;
            }
        });*/

        gorunmeyenFoto.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) gorevler_yazi_kismi.getLayoutParams();
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    downX = (int) event.getX();
                    return true;
                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    int fark = downX - upX;
                    upX = (int) event.getX();
                    if (fark > 150) {
                        if (degistirDeger < kacGorevVar) {
                            degistirDeger++;
                            pageDegis = 10;
                            GorevleriGetir();
                        } else {
                            degistirDeger = 1;
                            GorevleriGetir();
                        }
                    } else if (fark < -150) {
                        if (degistirDeger > 1) {
                            degistirDeger--;
                            pageDegis = 10;
                            GorevleriGetir();
                        } else {
                            degistirDeger = kacGorevVar;
                            GorevleriGetir();
                        }
                    }
                    sagBTN.setVisibility(View.VISIBLE);
                    solBTN.setVisibility(View.VISIBLE);
                    pageRel.setVisibility(View.VISIBLE);

                    if (sonrakiSayfa.getVisibility() == View.VISIBLE)
                        sonrakiSayfa.setVisibility(View.GONE);
                    if (oncekiSayfa.getVisibility() == View.VISIBLE)
                        oncekiSayfa.setVisibility(View.GONE);

                    layoutParams.rightMargin = 0;
                    layoutParams.leftMargin = -0;

                    return true;

                } else if (event.getAction() == MotionEvent.ACTION_MOVE) {
                    upX = (int) event.getX();
                    int fark = downX - upX;
                    if (fark < 150 && fark > -150) {
                        sagBTN.setVisibility(View.GONE);
                        solBTN.setVisibility(View.GONE);
                        pageRel.setVisibility(View.GONE);
                        if (!reklam) {
                            layoutParams.rightMargin = fark;
                            layoutParams.leftMargin = -fark;
                        }
                        if (sonrakiSayfa.getVisibility() == View.VISIBLE) {
                            if (fark < 100) {
                                if (!sonrakiKuculuyorMu) {
                                    sonrakiKuculuyorMu = true;
                                    buyutAnim(sonrakiIcon, 1, 0, false, sonrakiSayfa, 100);
                                }
                            }
                        }
                        if (oncekiSayfa.getVisibility() == View.VISIBLE) {
                            if (fark > -100) {
                                if (!oncekiKuculuyorMu) {
                                    oncekiKuculuyorMu = true;
                                    buyutAnim(oncekiIcon, 1, 0, false, oncekiSayfa, 100);
                                }
                            }
                        }
                    } else if (fark < -150) {
                        if (sonrakiSayfa.getVisibility() == View.GONE) {
                            buyutAnim(sonrakiIcon, 0, 1, true, sonrakiSayfa, 300);
                        }
                    } else if (fark > 150) {
                        if (oncekiSayfa.getVisibility() == View.GONE) {
                            buyutAnim(oncekiIcon, 0, 1, true, oncekiSayfa, 300);
                        }
                    }
                    if (!reklam)
                        gorevler_yazi_kismi.setLayoutParams(layoutParams);

                    return true;

                }
                return false;
            }
        });
        favoriEkle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!dialogFav) {
                    dialogFav = true;
                    Dialog dialog = new Dialog(TaraActivity.this);
                    dialog.setContentView(R.layout.dialog_dizayn2);
                    dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

                    TextView baslik = dialog.findViewById(R.id.baslik);
                    TextView aciklama = dialog.findViewById(R.id.aciklama);

                    LinearLayout lay1 = dialog.findViewById(R.id.lay1);
                    ProgressBar pbar = dialog.findViewById(R.id.pbar);
                    pbar.getIndeterminateDrawable().setColorFilter(ortaRenk, android.graphics.PorterDuff.Mode.MULTIPLY);

                    aciklama.setMovementMethod(new ScrollingMovementMethod());
                    Button buton = dialog.findViewById(R.id.buton);
                    baslik.setText("Favori Sayısı Arttır");
                    aciklama.setText("Kısıtlı sayıda kişiyi favorilerinize ekleyebilirsiniz. Bu sayıyı arttırmak için gerekli miktar " + fiyatlar.getFavori_ekleme() + ", sizde olan " + Kullanici.getKp() + "KP");
                    if (Kullanici.getKp() >= fiyatlar.getFavori_ekleme()) {
                        buton.setText("+1 Arttır");
                        buton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                lay1.setVisibility(View.GONE);
                                pbar.setVisibility(View.VISIBLE);
                                DatabaseReference ref = FirebaseDatabase.getInstance().getReference("usersF").child(Kullanici.getUid());
                                ref.child("harcamalarim").child("favsayisi_" + Kullanici.getFavori_sayim()).child("onceki_kp").setValue(Kullanici.getKp());
                                ref.child("harcamalarim").child("favsayisi_" + Kullanici.getFavori_sayim()).child("zaman").setValue(ServerValue.TIMESTAMP);
                                ref.child("kp").setValue(Kullanici.getKp() - fiyatlar.getFavori_ekleme()).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(@NonNull Void unused) {
                                        ref.child("favori_sayim").setValue(Kullanici.getFavori_sayim() + 1).addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(@NonNull Void unused) {
                                                Kullanici.setKp(Kullanici.getKp() - fiyatlar.getFavori_ekleme());
                                                Kullanici.setFavori_sayim(Kullanici.getFavori_sayim() + 1);
                                                //Toast.makeText(TaraActivity.this, "Yeni favorilerinize ekleyebileceğiniz kişi sayısı " + Kullanici.getFavori_sayim(), Toast.LENGTH_SHORT).show();
                                                favori_sayisi.setText("" + Kullanici.getFavori_sayim() + "/" + mUsers2.size());
                                                kp_yazi.setText("" + Kullanici.getKp());
                                                dialogFav = false;
                                                dialog.dismiss();
                                            }
                                        });
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        lay1.setVisibility(View.VISIBLE);
                                        pbar.setVisibility(View.GONE);
                                        Toast.makeText(TaraActivity.this, "Başarısız!", Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                        });
                    } else {
                        buton.setText("Markete Git");
                        buton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                if (!butonTiklandiMi) {
                                    butonTiklandiMi = true;
                                    lay1.setVisibility(View.GONE);
                                    pbar.setVisibility(View.VISIBLE);
                                    dialogFav = false;
                                    startActivity(new Intent(TaraActivity.this, MarketActivity.class));
                                }
                            }
                        });
                    }
                    dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                        @Override
                        public void onCancel(DialogInterface dialog) {
                            dialogFav = false;
                        }
                    });
                    dialog.show();
                }
            }
        });
        kp_buton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatabaseReference ref = FirebaseDatabase.getInstance().getReference("usersF").child(fuser.getUid());
                ref.child("suan").setValue(ServerValue.TIMESTAMP).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            startActivity(new Intent(TaraActivity.this, MarketActivity.class));
                        } else {
                            Toast.makeText(TaraActivity.this, "İnternet bağlantınız olmayabilir", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
        favoriBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!favoriyeBastim) {
                    favoriyeBastim = true;
                    favorilerde = true;
                    tarada = true;
                    gorduklerimde = false;
                    Intent intent = new Intent(TaraActivity.this, Favorilerim.class);
                    finish();
                    startActivity(intent);
                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                }

            }
        });
        destekBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(TaraActivity.this, DestekActivity.class));
            }
        });
        bulduklarimBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!bulduklarimaBastim) {
                    bulduklarimaBastim = true;
                    favorilerde = false;
                    tarada = true;
                    gorduklerimde = true;
                    Intent intent = new Intent(TaraActivity.this, TumGorduklerim.class);
                    finish();
                    startActivity(intent);
                    overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);

                }
            }
        });
        evdeMiyim.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    myRef.child("ev_sistemi").child("sistem").setValue("pasif");
                    myRef.child("ev_sistemi").child("evde_mi").setValue("evet");
                    if (dataSnapshotAsil != null)
                        if (dataSnapshotAsil.child("ev_sistemi").hasChild("beni_bulanlar"))
                            for (DataSnapshot ds : dataSnapshotAsil.child("ev_sistemi").child("beni_bulanlar").getChildren()) {
                                DatabaseReference ref = FirebaseDatabase.getInstance().getReference("usersF").child(ds.getKey());
                                ref.child("bulduklarim_bildirim_durumu").setValue("var");
                                YeniKisiBuldum.YeniKisiBuldum(ref, fuser.getUid(), ds.getValue(long.class));
                                ds.getRef().removeValue();
                            }

                    ev_ic.setColorFilter(getResources().getColor(R.color.gri2));
                    evdeMiyim.setEnabled(false);
                }
            }
        });
        yenibulBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!suanaBastim) {
                    suanaBastim = true;
                    favorilerde = false;
                    tarada = true;
                    gorduklerimde = false;
                    Intent intent = new Intent(TaraActivity.this, SuanBulunanlar.class);
                    finish();
                    startActivity(intent);
                    overridePendingTransition(R.anim.slide_in_top, R.anim.slide_out_down);
                }
            }
        });
        odul.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!butonTiklandiMi) {
                    butonTiklandiMi = true;
                    GorevBilgi altMenu = new GorevBilgi(TaraActivity.this);
                    GorevBilgi.odulYazi = odulYazi;
                    GorevBilgi.infoYazi = infoYazi;
                    GorevBilgi.sure = sure;
                    GorevBilgi.zaman = zaman;
                    GorevBilgi.suan = Kullanici.getSuan();
                    View sheetView = getLayoutInflater().inflate(R.layout.gorev_info, null);
                    altMenu.setContentView(sheetView);
                    altMenu.show();
                    if (altMenu.isShowing())
                        butonTiklandiMi = false;
                }
            }
        });
        info.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!butonTiklandiMi) {
                    butonTiklandiMi = true;
                    GorevBilgi altMenu = new GorevBilgi(TaraActivity.this);
                    GorevBilgi.odulYazi = odulYazi;
                    GorevBilgi.infoYazi = infoYazi;
                    GorevBilgi.sure = sure;
                    GorevBilgi.zaman = zaman;
                    GorevBilgi.suan = Kullanici.getSuan();
                    View sheetView = getLayoutInflater().inflate(R.layout.gorev_info, null);
                    altMenu.setContentView(sheetView);
                    altMenu.show();
                    if (altMenu.isShowing())
                        butonTiklandiMi = false;
                }
            }
        });
        bulma.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(final View view, MotionEvent motionEvent) {
                int action = motionEvent.getAction();
                if (action == MotionEvent.ACTION_DOWN) {
                    rect = new Rect(view.getLeft(), view.getTop(), view.getRight(), view.getBottom());
                    startScaleAnimation(view);
                    return true;
                } else if (action == MotionEvent.ACTION_UP) {
                    cancelScaleAnimation(view, 3);
                    return true;
                }
                return false;
            }
        });
        bulunma.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(final View view, MotionEvent motionEvent) {
                int action = motionEvent.getAction();
                if (action == MotionEvent.ACTION_DOWN) {
                    rect = new Rect(view.getLeft(), view.getTop(), view.getRight(), view.getBottom());
                    startScaleAnimation(view);
                    return true;
                } else if (action == MotionEvent.ACTION_UP) {
                    cancelScaleAnimation(view, 4);
                    return true;
                }
                return false;
            }
        });
        solBTN.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(final View view, MotionEvent motionEvent) {
                int action = motionEvent.getAction();
                if (action == MotionEvent.ACTION_DOWN) {
                    rect = new Rect(view.getLeft(), view.getTop(), view.getRight(), view.getBottom());
                    startScaleAnimation2(view);
                    return true;
                } else if (action == MotionEvent.ACTION_UP) {
                    cancelScaleAnimation2(view, 2);
                    return true;
                }
                return false;
            }
        });
        sagBTN.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(final View view, MotionEvent motionEvent) {
                int action = motionEvent.getAction();
                if (action == MotionEvent.ACTION_DOWN) {
                    rect = new Rect(view.getLeft(), view.getTop(), view.getRight(), view.getBottom());
                    startScaleAnimation2(view);
                    return true;
                } else if (action == MotionEvent.ACTION_UP) {
                    cancelScaleAnimation2(view, 1);
                    return true;
                }
                return false;
            }
        });
        mesajlar.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(final View view, MotionEvent motionEvent) {
                int action = motionEvent.getAction();
                if (action == MotionEvent.ACTION_DOWN) {
                    rect = new Rect(view.getLeft(), view.getTop(), view.getRight(), view.getBottom());
                    startScaleAnimation(view);
                    return true;
                } else if (action == MotionEvent.ACTION_UP) {
                    cancelScaleAnimation(view, 2);
                    return true;
                }
                return false;
            }
        });
        profil.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(final View view, MotionEvent motionEvent) {
                int action = motionEvent.getAction();
                if (action == MotionEvent.ACTION_DOWN) {
                    rect = new Rect(view.getLeft(), view.getTop(), view.getRight(), view.getBottom());
                    startScaleAnimation(view);
                    return true;
                } else if (action == MotionEvent.ACTION_UP) {
                    cancelScaleAnimation(view, 1);
                    return true;
                }
                return false;
            }
        });
        final int[] tutoDegeri = {0};
        tuto_gec.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (tutoDegeri[0] == 0) {
                    ipucu1.setBackgroundColor(getResources().getColor(R.color.transSiyah));
                    ipucu2.setImageDrawable(null);
                    ipucu.setText("Eğer bir kullanıcı tarafından bulunmak isterseniz kullanıcıların olabileceği yerlerde bu butona basarak ne kadar süre boyunca bulunabileceğinizi belirleyebilirsiniz.");
                    tutoDegeri[0]++;
                } else {
                    tuto.setVisibility(View.GONE);

                }
            }
        });
        spReklam = getSharedPreferences("Gorevler", MODE_PRIVATE);
        spBulduklarim = getSharedPreferences("Bulduklarim", MODE_PRIVATE);
        spFavorilerim = getSharedPreferences("Favorilerim", MODE_PRIVATE);
        spBulduklarimZaman = getSharedPreferences("BulduklarimZaman", MODE_PRIVATE);
        spFavorilerimZaman = getSharedPreferences("FavorilerimZaman", MODE_PRIVATE);

        gorevRel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (baglantiVarMi && baglanti != null) {
                    Uri uri = Uri.parse(baglanti);
                    Toast.makeText(TaraActivity.this, "Bağlantı Açılıyor...", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                    startActivity(intent);
                }
            }
        });

        //VerileriCagir();

    }

    public void buyutAnim(View v, float startScale, float endScale, boolean aktifEt, View aktifEdilecekView, int hiz) {
        if (aktifEt)
            aktifEdilecekView.setVisibility(View.VISIBLE);

        Animation anim = new ScaleAnimation(
                1f, 1f,
                startScale, endScale,
                Animation.RELATIVE_TO_SELF, 0f,
                Animation.RELATIVE_TO_SELF, 1f);
        anim.setFillAfter(true);
        anim.setDuration(hiz);
        anim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                if (!aktifEt)
                    aktifEdilecekView.setVisibility(View.GONE);
                oncekiKuculuyorMu = false;
                sonrakiKuculuyorMu = false;
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        v.startAnimation(anim);
    }

    public Bitmap blurBitmap(Bitmap bitmap) {

        //Let's create an empty bitmap with the same size of the bitmap we want to blur
        Bitmap outBitmap = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);

        //Instantiate a new Renderscript
        RenderScript rs = RenderScript.create(getApplicationContext());

        //Create an Intrinsic Blur Script using the Renderscript
        ScriptIntrinsicBlur blurScript = ScriptIntrinsicBlur.create(rs, Element.U8_4(rs));

        //Create the Allocations (in/out) with the Renderscript and the in/out bitmaps
        Allocation allIn = Allocation.createFromBitmap(rs, bitmap);
        Allocation allOut = Allocation.createFromBitmap(rs, outBitmap);

        //Set the radius of the blur
        blurScript.setRadius(25.f);

        //Perform the Renderscript
        blurScript.setInput(allIn);
        blurScript.forEach(allOut);

        //Copy the final bitmap created by the out Allocation to the outBitmap
        allOut.copyTo(outBitmap);

        //recycle the original bitmap
        bitmap.recycle();

        //After finishing everything, we destroy the Renderscript.
        rs.destroy();

        return outBitmap;


    }

    private void dikkatCekmeAnim(final View view, int hiz, float boyut) {
        view.setScaleX(1f);
        view.setScaleY(1f);
        ObjectAnimator scaleDownX = ObjectAnimator.ofFloat(view, "scaleX", boyut);
        ObjectAnimator scaleDownY = ObjectAnimator.ofFloat(view, "scaleY", boyut);
        scaleDownX.setDuration(hiz);
        scaleDownY.setDuration(hiz);
        scaleDownX.start();
        scaleDownY.start();
        scaleDownY.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                ObjectAnimator scaleDownX = ObjectAnimator.ofFloat(view, "scaleX", 1f);
                ObjectAnimator scaleDownY = ObjectAnimator.ofFloat(view, "scaleY", 1f);
                scaleDownX.setDuration(hiz);
                scaleDownY.setDuration(hiz);
                scaleDownX.start();
                scaleDownY.start();
                scaleDownX.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                        ObjectAnimator scaleDownX = ObjectAnimator.ofFloat(view, "scaleX", boyut);
                        ObjectAnimator scaleDownY = ObjectAnimator.ofFloat(view, "scaleY", boyut);
                        scaleDownX.setDuration(hiz);
                        scaleDownY.setDuration(hiz);
                        scaleDownX.start();
                        scaleDownY.start();
                        scaleDownX.addListener(new AnimatorListenerAdapter() {
                            @Override
                            public void onAnimationEnd(Animator animation) {
                                super.onAnimationEnd(animation);
                                ObjectAnimator scaleDownX = ObjectAnimator.ofFloat(view, "scaleX", 1f);
                                ObjectAnimator scaleDownY = ObjectAnimator.ofFloat(view, "scaleY", 1f);
                                scaleDownX.setDuration(hiz);
                                scaleDownY.setDuration(hiz);
                                scaleDownX.start();
                                scaleDownY.start();
                                scaleDownX.addListener(new AnimatorListenerAdapter() {
                                    @Override
                                    public void onAnimationEnd(Animator animation) {
                                        super.onAnimationEnd(animation);

                                    }
                                });
                            }
                        });
                    }
                });
            }
        });
    }

    private void startScaleAnimation2(final View view) {
        view.setScaleX(1f);
        view.setScaleY(1f);
        ObjectAnimator scaleDownX = ObjectAnimator.ofFloat(view, "scaleX", 1.5f);
        ObjectAnimator scaleDownY = ObjectAnimator.ofFloat(view, "scaleY", 1.5f);
        scaleDownX.setDuration(50);
        scaleDownY.setDuration(50);
        scaleDownX.start();
        scaleDownY.start();
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

    private void cancelScaleAnimation2(final View view, final int i) {
        ObjectAnimator scaleDownX2 = ObjectAnimator.ofFloat(view, "scaleX", 1.5f);
        final ObjectAnimator scaleDownY2 = ObjectAnimator.ofFloat(view, "scaleY", 1.5f);
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
                                if (i == 0) {
                                }
                                if (i == 1) {
                                    if (genelDS != null) {
                                        damga.setVisibility(View.GONE);
                                        //myTimer.cancel();
                                        if (degistirDeger < kacGorevVar) {
                                            degistirDeger++;
                                            pageDegis = 10;
                                            GorevleriGetir();
                                        } else {
                                            degistirDeger = 1;
                                            GorevleriGetir();
                                        }

                                    }
                                } else if (i == 2) {
                                    if (genelDS != null) {
                                        damga.setVisibility(View.GONE);
                                        //myTimer.cancel();
                                        if (degistirDeger > 1) {
                                            degistirDeger--;
                                            pageDegis = 10;
                                            GorevleriGetir();
                                        } else {
                                            degistirDeger = kacGorevVar;
                                            GorevleriGetir();
                                        }
                                    }
                                }

                            }
                        });
                    }
                });
            }
        });

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
                                if (i == 0) {
                                }
                                if (i == 1) {
                                    Intent intent = new Intent(TaraActivity.this, ProfilActivity.class);
                                    startActivity(intent);
                                    finish();
                                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                                } else if (i == 2) {
                                    Intent intent = new Intent(TaraActivity.this, MesajlarimActivity.class);
                                    startActivity(intent);
                                    finish();
                                    overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
                                } else if (i == 3) {
                                    if (!dialogBul) {
                                        if (ArkadaBul.bulma == false) {
                                            if (SuanBulunanlar.taramaAktifMi == false) {
                                                dialogBul = true;
                                                Dialog dialog = new Dialog(TaraActivity.this);
                                                dialog.setContentView(R.layout.dialog_dizayn4);
                                                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

                                                TextView baslik = dialog.findViewById(R.id.baslik);
                                                TextView aciklama = dialog.findViewById(R.id.aciklama);
                                                aciklama.setMovementMethod(new ScrollingMovementMethod());
                                                Button buton2 = dialog.findViewById(R.id.buton2); // Evet butonu
                                                Button buton3 = dialog.findViewById(R.id.buton3); // diğer

                                                buton2.setOnClickListener(new View.OnClickListener() {
                                                    @Override
                                                    public void onClick(View v) {
                                                        if (isNotificationChannelEnabled(TaraActivity.this, "arkadabulma")) {
                                                            IzinKontrol2();
                                                            dialogBul = false;
                                                            dialog.dismiss();
                                                        } else {
                                                            //Toast.makeText(TaraActivity.this, "Zaten arkaplanda tarama yapıyorsunuz", Toast.LENGTH_SHORT).show();
                                                            dialogBul = false;
                                                        }
                                                    }
                                                });
                                                buton3.setOnClickListener(new View.OnClickListener() {
                                                    @Override
                                                    public void onClick(View v) {
                                                        SuanBulunanlar.userSnapshot = tumUserlarDS;
                                                        dialogBul = false;
                                                        Intent intent = new Intent(getApplicationContext(), SuanBulunanlar.class);
                                                        intent.putExtra("bulma", "aktif");
                                                        dialog.dismiss();
                                                        startActivity(intent);
                                                    }
                                                });

                                                dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                                                    @Override
                                                    public void onCancel(DialogInterface dialog) {
                                                        dialogBul = false;
                                                    }
                                                });
                                                dialog.show();
                                            } else {
                                                dialogBul = false;
                                                Intent intent = new Intent(getApplicationContext(), SuanBulunanlar.class);
                                                startActivity(intent);
                                            }
                                        } else
                                            Toast.makeText(TaraActivity.this, "Zaten tarama yapıyorsunuz", Toast.LENGTH_SHORT).show();
                                    } else {
                                        openChannelSettings("arkadabulma");
                                        dialogBul = false;
                                    }
                                } else if (i == 4) {
                                    if (isNotificationChannelEnabled(TaraActivity.this, "arkadabulunma")) {
                                        IzinKontrol();
                                    } else
                                        openChannelSettings("arkadabulunma");
                                } else if (i == 5) { // Sağ oK
                                    if (genelDS != null) {
                                        damga.setVisibility(View.GONE);
                                        //myTimer.cancel();
                                        if (degistirDeger < kacGorevVar) {
                                            degistirDeger++;
                                            pageDegis = 10;
                                            GorevleriGetir();
                                        } else {
                                            degistirDeger = 1;
                                            GorevleriGetir();
                                        }

                                    }
                                } else if (i == 6) { // Sol Ok
                                    if (genelDS != null) {
                                        damga.setVisibility(View.GONE);
                                        //myTimer.cancel();
                                        if (degistirDeger > 1) {
                                            degistirDeger--;
                                            pageDegis = 10;
                                            GorevleriGetir();
                                        } else {
                                            degistirDeger = kacGorevVar;
                                            GorevleriGetir();
                                        }
                                    }
                                }
                            }
                        });
                    }
                });
            }
        });

    }

    private void openChannelSettings(String channelId) {
        Intent intent = new Intent(Settings.ACTION_CHANNEL_NOTIFICATION_SETTINGS);
        intent.putExtra(Settings.EXTRA_APP_PACKAGE, getPackageName());
        intent.putExtra(Settings.EXTRA_CHANNEL_ID, channelId);
        startActivity(intent);
    }

    private boolean isNotificationChannelEnabled(Context context, @Nullable String channelId) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            if (!TextUtils.isEmpty(channelId)) {
                NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
                NotificationChannel channel = manager.getNotificationChannel(channelId);
                return channel.getImportance() != NotificationManager.IMPORTANCE_NONE;
            }
            return false;
        } else {
            return NotificationManagerCompat.from(context).areNotificationsEnabled();
        }
    }

    public static String getUID() {
        int DIGITS = 5;
        StringBuilder sb = new StringBuilder(DIGITS);
        for (int i = 0; i < DIGITS; i++) {
            sb.append((char) (Math.random() * 10 + '0'));
        }
        return sb.toString();
    }

    @SuppressLint("MissingPermission")
    private void IzinKontrol2() {
        final String olusanId = getUID();

        if (myAdapter.isEnabled()) {
            // scan
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) == PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_SCAN) == PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED) {
                ArkadaBul.deaktif = false;
                ArkadaBul.bulma = true;
                Intent intent = new Intent(TaraActivity.this, ArkadaBul.class);
                startService(intent);
                SharedPreferences sharedPreferences = getSharedPreferences("ayarlar", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
            }
            else{
                requestPermissions(new String[]{Manifest.permission.BLUETOOTH_CONNECT, Manifest.permission.BLUETOOTH_SCAN, Manifest.permission.POST_NOTIFICATIONS},3);
            }


        } else {
            Intent eintent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(eintent, 4);
        }
    }

    private void VerileriCagir() {
        bulduklarimdakilerinUid = new ArrayList<>();
        favorilerimdekilerinUid = new ArrayList<>();

        mUsers = new ArrayList<>();
        mUsers2 = new ArrayList<>();

        for (int i = 0; i < 10; i++) {
            if (!spBulduklarim.getString("" + (i + 1), "null").equals("null")) {
                if (!bulduklarimdakilerinUid.contains(spBulduklarim.getString("" + (i + 1), "null"))) {
                    //Toast.makeText(TaraActivity.this, ""+spBulduklarim.getString("" + (i+1),"null"), Toast.LENGTH_SHORT).show();
                    bulduklarimdakilerinUid.add(spBulduklarim.getString("" + (i + 1), "null"));
                    TariheGoreListeleSadeceResim item = new TariheGoreListeleSadeceResim(spBulduklarim.getString("" + (i + 1), "null"), spBulduklarimZaman.getLong("" + (i + 1), 0));
                    mUsers.add(mUsers.size(), item);

                    Collections.sort(mUsers, new Comparator<TariheGoreListeleSadeceResim>() {
                        @Override
                        public int compare(TariheGoreListeleSadeceResim o1, TariheGoreListeleSadeceResim o2) {
                            return (int) (o2.getZaman() - o1.getZaman());
                        }
                    });

                    Sonuc("bulduklarim");
                }
            }

            if (!spFavorilerim.getString("" + (i + 1), "null").equals("null")) {
                //Toast.makeText(TaraActivity.this, ""+spFavorilerim.getString("" + (i+1), "null"), Toast.LENGTH_SHORT).show();
                if (!favorilerimdekilerinUid.contains(spFavorilerim.getString("" + (i + 1), "null"))) {
                    favorilerimdekilerinUid.add(spFavorilerim.getString("" + (i + 1), "null"));
                    TariheGoreListeleSadeceResim item = new TariheGoreListeleSadeceResim(spFavorilerim.getString("" + (i + 1), "null"), spFavorilerimZaman.getLong("" + (i + 1), 0));
                    mUsers2.add(mUsers2.size(), item);

                    Collections.sort(mUsers2, new Comparator<TariheGoreListeleSadeceResim>() {
                        @Override
                        public int compare(TariheGoreListeleSadeceResim o1, TariheGoreListeleSadeceResim o2) {
                            return (int) (o2.getZaman() - o1.getZaman());
                        }
                    });

                    Sonuc("favorilerim");
                }
            }
        }
        ContextWrapper cw = new ContextWrapper(TaraActivity.this);
        File directory = cw.getDir("benim_resimler", MODE_PRIVATE);

        File image = new File(directory, "foto_pp.jpg");
        if (image.exists()) {
            profilFotosu.setImageURI(Uri.parse(image.getAbsolutePath()));
            ppYuklendi = true;
        } else
            ppYuklendi = false;
        AppRater.app_launched(TaraActivity.this);
        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                dataSnapshotAsil = dataSnapshot;
                Kullanici = dataSnapshotAsil.getValue(User.class);
                kp_yazi.setText("" + Kullanici.getKp());
                PPSilinmisMi();
                OzelHaberimVarmi();


                DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Sistem");
                reference.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataS) {
                        if (dataS.child("versiyon_zorunlu").getValue(Integer.class) <= BuildConfig.VERSION_CODE) {
                            if (!ilkKezMi) {
                                HaberVarmi(dataS);
                            } else {

                            }

                            fiyatlar = dataS.child("fiyatlar").getValue(Fiyatlar.class);
                            sistemGun = dataS.child("gun").getValue(Integer.class);

                            if (sistemGun != 0)
                                if (Kullanici.getGun() != sistemGun) {
                                    if (fiyatlar != null) {
                                        Kullanici.setGun(sistemGun);
                                        dataSnapshotAsil.child("kp").getRef().setValue(Kullanici.getKp() + fiyatlar.getGunluk_odul()).addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(@NonNull Void unused) {

                                                DatabaseReference ref = FirebaseDatabase.getInstance().getReference("usersF").child(fuser.getUid());
                                                ref.child("gelirlerim").child("gunluk_" + sistemGun).child("onceki_kp").setValue(Kullanici.getKp());
                                                ref.child("gelirlerim").child("gunluk_" + sistemGun).child("verilen_kp").setValue(fiyatlar.getGunluk_odul());
                                                ref.child("gelirlerim").child("gunluk_" + sistemGun).child("zaman").setValue(ServerValue.TIMESTAMP);

                                                Kullanici.setKp(Kullanici.getKp() + fiyatlar.getGunluk_odul());
                                                dataSnapshotAsil.child("gun").getRef().setValue(sistemGun).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(@NonNull Void unused) {
                                                        kp_yazi.setText("" + Kullanici.getKp());
                                                        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                                            @Override
                                                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                                dataSnapshotAsil = dataSnapshot;
                                                                GelirleriKontrolEt();
                                                                if (!ilkKezMi)
                                                                    KonumaDavet();
                                                            }

                                                            @Override
                                                            public void onCancelled(@NonNull DatabaseError databaseError) {

                                                            }
                                                        });
                                                    }
                                                });
                                            }
                                        });

                                        long a = ((dataSnapshotAsil.child("suan").getValue(long.class) / 1000) - (dataSnapshotAsil.child("dogum_tarihi").getValue(long.class) / 1000)) / 31556926;
                                        long suankiYas = Long.valueOf(dataSnapshotAsil.child("dg").getValue(String.class));
                                        if (a > suankiYas) {
                                            Toast.makeText(TaraActivity.this, "Doğum günün kutlu olsun!", Toast.LENGTH_SHORT).show();
                                            dataSnapshotAsil.child("dg").getRef().setValue("" + (suankiYas + 1));
                                            if ((suankiYas + 1) > getResources().getInteger(R.integer.ust_yas_siniri)) {
                                                dataSnapshotAsil.child("ban_durumu").child("durum").getRef().setValue("var");
                                                dataSnapshotAsil.child("ban_durumu").child("sebep").getRef().setValue("yaş");
                                                dataSnapshotAsil.child("ban_durumu").child("tarih").getRef().setValue(ServerValue.TIMESTAMP);
                                                dataSnapshotAsil.child("ban_durumu").child("sure").getRef().setValue(1);
                                                dataSnapshotAsil.child("ban_durumu").child("banlayan").getRef().setValue("sistem");
                                                dataSnapshotAsil.child("ban_durumu").child("aciklama").getRef().setValue("").addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(@NonNull Void unused) {
                                                        Toast.makeText(TaraActivity.this, "Doğum günün kutlu olsun ancak " + getResources().getInteger(R.integer.ust_yas_siniri) + " yaşını doldurduğun için artık Kimoo'yu kullanamazsın :(", Toast.LENGTH_LONG).show();
                                                        startActivity(new Intent(TaraActivity.this, MainActivity.class));
                                                    }
                                                });

                                            }
                                        }
                                    }
                                } else
                                    GelirleriKontrolEt();

                            if (dataSnapshot.child("kisitli_erisim_engeli").child("durum").getValue().equals("var")) {
                                Intent intent = new Intent(TaraActivity.this, MainActivity.class);
                                startActivity(intent);
                            }
                            GorevSayaci();
                            GorevleriGetir();
                            if (dataSnapshot.child("giris_ilkkez").getValue().equals("evet")) {
                                ilkKezMi = true;
                                IlkKez();
                            }
                            if (dataSnapshot.hasChild("bulduklarim")) {
                                gorduklerimSayisi = dataSnapshot.child("bulduklarim").getChildrenCount();
                                EngelKontrol("bulduklarim");
                            } else
                                pbarBul.setVisibility(View.GONE);
                            if (dataSnapshot.hasChild("favorilerim")) {
                                gorduklerimSayisi2 = dataSnapshot.child("favorilerim").getChildrenCount();
                                EngelKontrol("favorilerim");

                            } else {
                                pbarFav.setVisibility(View.GONE);
                                favori_sayisi.setText("" + Kullanici.getFavori_sayim() + "/0");
                            }
                            for (String uid : bulduklarimdakilerinUid) {
                                if (!dataSnapshot.child("bulduklarim").child(uid).exists()) {
                                    BuUidOlanVerileriSil(uid, "bulduklarim");
                                }
                            }
                            for (String uid : favorilerimdekilerinUid) {
                                if (!dataSnapshot.child("favorilerim").child(uid).exists()) {
                                    BuUidOlanVerileriSil(uid, "favorilerim");
                                }
                            }

                            final String url = dataSnapshot.child("fotograflarim").child("pp").getValue().toString();

                            ContextWrapper cw = new ContextWrapper(TaraActivity.this);

                            SharedPreferences fotopref = getSharedPreferences("Fotolar", MODE_PRIVATE);
                            SharedPreferences.Editor editor = fotopref.edit();

                            if (ppYuklendi) {
                                if (!url.trim().equals("")) {
                                    if (!fotopref.getString("foto_pp", "").equals(url.substring(url.length() - 3))) {
                                        editor.putString("foto_pp", url.substring(url.length() - 3));
                                        Glide.with(TaraActivity.this).asBitmap().load(url).into(profilFotosu);
                                        new ResimIndir(getApplicationContext(), url, "benim_resimler", "foto_pp.jpg");
                                        editor.putString("foto_pp", url.substring(url.length() - 3));
                                        editor.commit();
                                        //Toast.makeText(TaraActivity.this, "indiriyom", Toast.LENGTH_SHORT).show();
                                    } else
                                        profilFotosu.setImageURI(Uri.parse(image.getAbsolutePath()));
                                } else
                                    image.delete();
                            } else {
                                if (!url.trim().equals("")) {
                                    Glide.with(TaraActivity.this).asBitmap().load(url).into(profilFotosu);
                                    new ResimIndir(getApplicationContext(), url, "benim_resimler", "foto_pp.jpg");
                                }
                            }

                            if (dataSnapshotAsil.child("ev_sistemi").child("sistem").getValue(String.class).equals("pasif")) {
                                ev_ic.setColorFilter(getResources().getColor(R.color.gri2));
                                evdeMiyim.setEnabled(false);
                            } else {
                                ev_ic.setColorFilter(ortaRenk);
                                evdeMiyim.setEnabled(true);
                            }
                            swipeRefreshLayout.setRefreshing(false);
                        } else {
                            Toast.makeText(TaraActivity.this, "Eski bir versiyon kullanıyorsunuz, lütfen uygulamayı güncelleyiniz.", Toast.LENGTH_LONG).show();
                            finishAffinity();
                        }
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
        BildirimKontrol();
    }

    private void KonumaDavet() {
        konumSorDialog = new Dialog(TaraActivity.this);
        konumSorDialog.setContentView(R.layout.dialog_recyclerview);
        konumSorDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        konumSorDialog.setCancelable(false);

        TextView baslik = konumSorDialog.findViewById(R.id.baslik);
        TextView aciklama = konumSorDialog.findViewById(R.id.aciklama);
        LinearLayout background = konumSorDialog.findViewById(R.id.background);
        LinearLayout lay2 = konumSorDialog.findViewById(R.id.lay2);
        ProgressBar pbar = konumSorDialog.findViewById(R.id.pbar);
        background.setBackground(dialogIcinOzelBackground);
        dialogRV = konumSorDialog.findViewById(R.id.recyclerView);
        dialogRV.setHasFixedSize(true);
        dialogRV.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        Button tamam = konumSorDialog.findViewById(R.id.buton);
        tamam.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pbar.setVisibility(View.VISIBLE);
                lay2.setVisibility(View.GONE);
                int yerler = 0;
                int kontrol = 0;
                for (DataSnapshot ds : dataSnapshotAsil.child("gidecegim_yerler").getChildren()) {
                    kontrol++;
                    if (!ds.child("dbisim").getValue(String.class).equals("")) {
                        yerler = 1;
                    }
                    if (kontrol == 4)
                        if (yerler == 1) {
                            dataSnapshotAsil.child("gelirlerim").child("konum_" + sistemGun).child("zaman").getRef().setValue(ServerValue.TIMESTAMP);
                            dataSnapshotAsil.child("gelirlerim").child("konum_" + sistemGun).child("onceki_kp").getRef().setValue(dataSnapshotAsil.child("kp").getValue(Integer.class));
                            dataSnapshotAsil.child("gelirlerim").child("konum_" + sistemGun).child("verilen_kp").getRef().setValue(500);
                            dataSnapshotAsil.child("kp").getRef().setValue(dataSnapshotAsil.child("kp").getValue(Integer.class) + 500).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(@NonNull Void unused) {
                                    Toast.makeText(TaraActivity.this, "500KP kazandınız!", Toast.LENGTH_SHORT).show();
                                    konumSorDialog.dismiss();
                                }
                            });
                        } else
                            konumSorDialog.dismiss();
                }
            }
        });
        baslik.setText("***** 500 KP KAZANIN *****");
        aciklama.setMovementMethod(new ScrollingMovementMethod());
        aciklama.setText("Şehrinizin popüler mekanlarına bugün gidecekleri aşağıdan görebilir ve onlarla aynı yere gitmeyi tercih edebilirsiniz. Sizin nereye gittiğinizi diğer kullanıcıların görebilmesini isterseniz gizlilik kısmından ziyaret ayarlarınızı değiştirebilirsiniz. Gideceğiniz konumu belirttiğinizde 500KP kazanacaksınız!");

        YerleriListele(true);
        konumSorDialog.show();

    }

    private void GorevSayaci() {
        pageDegis = 10;
        myTimer = new Timer();
        TimerTask t = new TimerTask() {
            @Override
            public void run() {
                if (kacGorevVar > 1) {
                    pageDegis--;
                    if (pageDegis < 0) {
                        if (genelDS != null) {
                            pageDegis = 10;
                            if (degistirDeger < kacGorevVar) {
                                degistirDeger++;
                                GorevleriGetir();
                            } else {
                                degistirDeger = 1;
                                GorevleriGetir();
                            }
                        }
                    }
                }
            }
        };
        //myTimer.scheduleAtFixedRate(t,0,1000);
    }

    private void PPSilinmisMi() {
        if (dataSnapshotAsil.child("fotograflarim").child("pp").getValue(String.class).equals("")) {

            Dialog dialog = new Dialog(TaraActivity.this);
            dialog.setContentView(R.layout.dialog_dizayn2);
            dialog.setCancelable(false);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

            TextView baslik = dialog.findViewById(R.id.baslik);
            TextView aciklama = dialog.findViewById(R.id.aciklama);
            LinearLayout lay1 = dialog.findViewById(R.id.lay1);
            ProgressBar pbar = dialog.findViewById(R.id.pbar);
            aciklama.setMovementMethod(new ScrollingMovementMethod());
            Button buton = dialog.findViewById(R.id.buton);
            baslik.setText("Profil Fotoğrafı");
            buton.setText("TAMAM");
            aciklama.setText("Profil fotoğrafınız kaldırılmış, uygulamayı kullanmaya devam edebilmek için lütfen bir profil fotoğrafı ekleyin.");
            buton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(TaraActivity.this, ProfilActivity.class);
                    intent.putExtra("foto_yukle", "pp");
                    startActivity(intent);
                }
            });
            dialog.show();
        }
    }

    private void OzelHaberimVarmi() {
        for (DataSnapshot ds : dataSnapshotAsil.child("haberlerim").getChildren()) {

            Dialog dialog = new Dialog(TaraActivity.this);
            dialog.setContentView(R.layout.dialog_dizayn2);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

            TextView baslik = dialog.findViewById(R.id.baslik);
            TextView aciklama = dialog.findViewById(R.id.aciklama);
            LinearLayout lay1 = dialog.findViewById(R.id.lay1);
            ProgressBar pbar = dialog.findViewById(R.id.pbar);
            aciklama.setMovementMethod(new ScrollingMovementMethod());
            Button buton = dialog.findViewById(R.id.buton);
            buton.setText("TAMAM");

            baslik.setText(ds.child("baslik").getValue(String.class));
            String icerikYazi = "";
            if (ds.child("icerik").getValue(String.class).equals("Sebep:1"))
                icerikYazi = getResources().getString(R.string.uyari_pp_bas) + " " + getResources().getString(R.string.pp_sebep1) + " " + getResources().getString(R.string.uyari_pp_son);
            else if (ds.child("icerik").getValue(String.class).equals("Sebep:2"))
                icerikYazi = getResources().getString(R.string.uyari_pp_bas) + " " + getResources().getString(R.string.pp_sebep2) + " " + getResources().getString(R.string.uyari_pp_son);
            else if (ds.child("icerik").getValue(String.class).equals("Sebep:3"))
                icerikYazi = getResources().getString(R.string.uyari_pp_bas) + " " + getResources().getString(R.string.pp_sebep3) + " " + getResources().getString(R.string.uyari_pp_son);
            else if (ds.child("icerik").getValue(String.class).contains("SebepYazi:"))
                icerikYazi = getResources().getString(R.string.uyari_pp_bas) + " " + ds.child("icerik").getValue(String.class).substring(10) + " " + getResources().getString(R.string.uyari_pp_son);
            else
                icerikYazi = ds.child("icerik").getValue(String.class);

            aciklama.setText(icerikYazi);

            buton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    lay1.setVisibility(View.GONE);
                    pbar.setVisibility(View.VISIBLE);
                    if (ds.child("buton").getValue(String.class).equals("dismiss")) {
                        ds.getRef().removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(@NonNull Void unused) {
                                dialog.dismiss();
                            }
                        });
                    } else if (ds.child("buton").getValue(String.class).substring(0, 9).equals("activity:")) {
                        String activity = ds.child("buton").getValue(String.class).substring(9);
                        if (activity.equals("Mesajlar"))
                            startActivity(new Intent(TaraActivity.this, MesajlarimActivity.class));
                        else if (activity.equals("Profil"))
                            startActivity(new Intent(TaraActivity.this, ProfilActivity.class));
                        else if (activity.equals("Tüm"))
                            startActivity(new Intent(TaraActivity.this, TumGorduklerim.class));
                        else if (activity.equals("Favori"))
                            startActivity(new Intent(TaraActivity.this, Favorilerim.class));
                        else if (activity.equals("Begenenler"))
                            startActivity(new Intent(TaraActivity.this, BegenenlerActivity.class));
                        else if (activity.equals("Suan"))
                            startActivity(new Intent(TaraActivity.this, SuanBulunanlar.class));
                        else if (activity.equals("Ayar"))
                            startActivity(new Intent(TaraActivity.this, AyarlarActivity.class));
                        else if (activity.substring(0, 4).equals("link"))
                            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(activity.substring(4))));

                        dialog.dismiss();
                    } else {
                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(ds.child("buton").getValue(String.class)));
                        startActivity(intent);
                        dialog.dismiss();
                    }
                }
            });

            dialog.show();
        }
    }

    private void GelirleriKontrolEt() {
        int deger = 0;
        if (dataSnapshotAsil.hasChild("gelirlerim"))
            for (DataSnapshot ds : dataSnapshotAsil.child("gelirlerim").getChildren()) {
                deger++;
                if (!ds.hasChild("goster")) {
                    if (!gelirler.contains(ds.getKey()))
                        mGelirler.add(ds);
                }

                if (deger == dataSnapshotAsil.child("gelirlerim").getChildrenCount()) {
                    if (mGelirler.size() > 0) {
                        AnimOynat();
                    }
                }
            }

    }

    private void AnimOynat() {
        animasyonRel.setVisibility(View.VISIBLE);
        animasyonYazi.setText("+" + mGelirler.get(0).child("verilen_kp").getValue());
        AlphaAnimation fadeIn = new AlphaAnimation(0.0f, 1.0f);
        fadeIn.setDuration(500);
        fadeIn.setFillAfter(true);
        animasyonRel.startAnimation(fadeIn);
        fadeIn.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                AlphaAnimation fadeOut = new AlphaAnimation(1.0f, 0.0f);
                fadeOut.setDuration(400);
                fadeOut.setFillAfter(true);
                fadeOut.setStartOffset(1500 + fadeIn.getStartOffset());
                animasyonRel.startAnimation(fadeOut);
                fadeOut.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        animasyonRel.setVisibility(View.GONE);
                        mGelirler.get(0).getRef().child("goster").setValue(ServerValue.TIMESTAMP).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(@NonNull Void unused) {
                                mGelirler.remove(0);
                                if (mGelirler.size() > 0)
                                    AnimOynat();
                            }
                        });
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {

                    }
                });
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }

    private void HaberVarmi(DataSnapshot dataSnapshot) {
        SharedPreferences sharedPreferences = getSharedPreferences("sistem", Context.MODE_PRIVATE);
        final SharedPreferences.Editor editor = sharedPreferences.edit();
        /*editor.remove("2");
        editor.commit();*/
        for (DataSnapshot ds : dataSnapshot.child("haberler").getChildren()) {
            if (!sharedPreferences.getString(ds.getKey(), "").equals(ds.getKey())) {
                editor.putString(ds.getKey(), ds.getKey());
                editor.commit();

                Dialog dialog = new Dialog(TaraActivity.this);
                dialog.setContentView(R.layout.dialog_dizayn2);
                dialog.setCancelable(false);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

                TextView baslik = dialog.findViewById(R.id.baslik);
                TextView aciklama = dialog.findViewById(R.id.aciklama);
                aciklama.setMovementMethod(new ScrollingMovementMethod());
                Button buton = dialog.findViewById(R.id.buton);
                buton.setText("TAMAM");

                baslik.setText(ds.child("baslik").getValue(String.class));
                aciklama.setText(ds.child("icerik").getValue(String.class));

                buton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (ds.child("buton").getValue(String.class).equals("dismiss")) {
                            dialog.dismiss();
                        } else if (ds.child("buton").getValue(String.class).substring(0, 9).equals("activity:")) {
                            String activity = ds.child("buton").getValue(String.class).substring(9);
                            if (activity.equals("Mesajlar"))
                                startActivity(new Intent(TaraActivity.this, MesajlarimActivity.class));
                            else if (activity.equals("Profil"))
                                startActivity(new Intent(TaraActivity.this, ProfilActivity.class));
                            else if (activity.equals("Tüm"))
                                startActivity(new Intent(TaraActivity.this, TumGorduklerim.class));
                            else if (activity.equals("Favori"))
                                startActivity(new Intent(TaraActivity.this, Favorilerim.class));
                            else if (activity.equals("Begenenler"))
                                startActivity(new Intent(TaraActivity.this, BegenenlerActivity.class));
                            else if (activity.equals("Suan"))
                                startActivity(new Intent(TaraActivity.this, SuanBulunanlar.class));
                            else if (activity.equals("Ayar"))
                                startActivity(new Intent(TaraActivity.this, AyarlarActivity.class));
                            dialog.dismiss();
                        } else {
                            if (!ds.child("buton").getValue(String.class).equals("")) {
                                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(ds.child("buton").getValue(String.class)));
                                startActivity(intent);
                            }
                            dialog.dismiss();
                        }
                    }
                });

                dialog.show();
            }

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
                int gidilenYerOnayiIcinGerekliKisiSayisi = dataSnapshot.child("gidilenYerOnayiIcinGerekliKisiSayisi").getValue(Integer.class);

                for (int i = 0; i < gidecegimYerler.size(); i++) {
                    String gidecegimYerinIsmi = dataSnapshotAsil.child("gidecegim_yerler").child(gidecegimYerler.get(i)).child("isim").getValue(String.class);
                    if (!gidecegimYerinIsmi.equals("")) {
                        long zaman = dataSnapshotAsil.child("gidecegim_yerler").child(gidecegimYerler.get(i)).child("zaman").getValue(Long.class);
                        for (DataSnapshot ds : dataSnapshotAsil.child("bulduklarim").getChildren()) {
                            if (ds.child("son_gordugum_zaman").getValue(Long.class) > zaman) {
                                DatabaseReference ref = FirebaseDatabase.getInstance().getReference("usersF").child(ds.getKey());
                                int finalI = i;
                                ref.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        if (dataSnapshot.child("gidecegim_yerler").child("1").child("isim").getValue().equals(gidecegimYerinIsmi))
                                            gidenlerSayisi[0]++;
                                        if (dataSnapshot.child("gidecegim_yerler").child("2").child("isim").getValue().equals(gidecegimYerinIsmi))
                                            gidenlerSayisi[0]++;
                                        if (dataSnapshot.child("gidecegim_yerler").child("3").child("isim").getValue().equals(gidecegimYerinIsmi))
                                            gidenlerSayisi[0]++;
                                        if (dataSnapshot.child("gidecegim_yerler").child("asil").child("isim").getValue().equals(gidecegimYerinIsmi))
                                            gidenlerSayisi[0]++;
                                        if (gidenlerSayisi[0] >= gidilenYerOnayiIcinGerekliKisiSayisi) {
                                            dataSnapshotAsil.child("gidecegim_yerler").child(gidecegimYerler.get(finalI)).child("gittimMi").getRef().setValue("evet").addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(@NonNull Void unused) {

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

    @SuppressLint("MissingPermission")
    private void IlkKez() {
        SharedPreferences sharedPreferences = getSharedPreferences("ayarlar", Context.MODE_PRIVATE);
        final SharedPreferences.Editor editor = sharedPreferences.edit();

        final AlertDialog.Builder builder = new AlertDialog.Builder(TaraActivity.this);
        builder.setCancelable(false);

        View view = getLayoutInflater().inflate(R.layout.dialog_dizayn, null);
        builder.setView(view);
        if (!dialogAktifMi) {
            Dialog dialog = new Dialog(TaraActivity.this);
            dialog.setContentView(R.layout.dialog_dizayn3);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialog.setCancelable(false);

            TextView baslik = dialog.findViewById(R.id.baslik);
            TextView aciklama = dialog.findViewById(R.id.aciklama);
            aciklama.setMovementMethod(new ScrollingMovementMethod());
            Button buton = dialog.findViewById(R.id.buton);
            EditText bt_adi = dialog.findViewById(R.id.editText);
            bt_adi.setText(myAdapter.getName());
            baslik.setText("Bluetooth Adı");
            aciklama.setText("Kimoo bluetooth özelliğini kullanırken bu ismi değiştirir. Bluetooth kullanılmadığı zamanlarda bu isim ne olsun?  \n(Bunu sonra ayarlardan değiştirebilirsiniz.)");
            buton.setText("TAMAM");
            buton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (bt_adi.getText().toString().trim().length() > 0) {
                        dataSnapshotAsil.child("bt_adi").getRef().setValue(bt_adi.getText().toString());
                        dataSnapshotAsil.child("giris_ilkkez").getRef().setValue("hayir").addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                dialogAktifMi = false;
                                editor.putString("bt_adi", bt_adi.getText().toString());
                                editor.commit();
                                //Toast.makeText(TaraActivity.this, bt_adi.getText().toString() + " ismini belirlediniz.", Toast.LENGTH_SHORT).show();
                                tuto.setVisibility(View.VISIBLE);
                                dialog.dismiss();
                            }
                        });
                    }
                }
            });

            dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                @Override
                public void onCancel(DialogInterface dialog) {
                    dialogAktifMi = false;
                }
            });
            dialog.show();
            dialogAktifMi = true;
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        butonTiklandiMi = false;
    }

    private void BildirimKontrol() {
        myRefListener = myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                dataSnapshotAsil = dataSnapshot;
                SharedPreferences sharedPreferences2 = getSharedPreferences("bulunan",Context.MODE_PRIVATE);
                SharedPreferences.Editor editor2 = sharedPreferences2.edit();

                if(fuser != null && dataSnapshotAsil.hasChild("bulduklarim_bildirim_durumu")) {
                    if (dataSnapshotAsil.child("mesaj_bildirim_durumu").getValue(String.class).equals("var"))
                        mesajBildirim.setVisibility(View.VISIBLE);
                    else
                        mesajBildirim.setVisibility(View.GONE);

                    if (dataSnapshotAsil.child("begeni_bildirim_durumu").getValue(String.class).equals("var"))
                        begeniBildirim.setVisibility(View.VISIBLE);
                    else
                        begeniBildirim.setVisibility(View.GONE);

                    if (dataSnapshotAsil.child("bulduklarim_bildirim_durumu").getValue(String.class).equals("var")) {
                        bulduklarimBildirim.setVisibility(View.VISIBLE);
                        bulduklarimBildirim.setImageDrawable(getResources().getDrawable(R.drawable.yuvarlak_kirmizi));
                    }
                    else {
                        if (sharedPreferences2.getInt("KalinanYer",0) == 0)
                            bulduklarimBildirim.setVisibility(View.GONE);
                        else {
                            bulduklarimBildirim.setVisibility(View.VISIBLE);
                            bulduklarimBildirim.setImageDrawable(getResources().getDrawable(R.drawable.yuvarlak_turuncu));
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void EngelKontrol(String child) {
        for (final DataSnapshot ds : dataSnapshotAsil.child(child).getChildren()) {
            DatabaseReference onunRef = FirebaseDatabase.getInstance().getReference("usersF").child(ds.getKey());
            onunRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    String user = dataSnapshot.getKey();
                    if(dataSnapshot.exists()) {
                        if(!dataSnapshot.child("engellediklerim").child(fuser.getUid()).exists()) {
                            if (dataSnapshot.child("ban_durumu").child("durum").getValue(String.class).equals("yok")) {
                                if (dataSnapshot.child("hesap_durumu").child("durum").getValue(Integer.class) == 0) {
                                    if (child.equals("favorilerim")) {
                                        if (!favorilerimdekilerinUid.contains(user)) {
                                            favorilerimdekilerinUid.add(user);
                                            TariheGoreListeleSadeceResim item = new TariheGoreListeleSadeceResim(user, ds.getValue(Long.class));
                                            mUsers2.add(mUsers2.size(), item);

                                            ArrayList<String> favoridekiler = new ArrayList<>();
                                            for (int i = 0; i < 10; i++) {
                                                if (!spFavorilerim.getString("" + (i + 1), "null").equals("null"))
                                                    favoridekiler.add(spFavorilerim.getString("" + (i + 1), "null"));
                                                if (i == 9) {
                                                    if (favoridekiler.size() < 10) {
                                                        if (favoridekiler.size() == 0 || favoridekiler == null) {
                                                            SharedPreferences.Editor editorFavZaman = spFavorilerimZaman.edit();
                                                            editorFavZaman.putLong("1", ds.getValue(Long.class));
                                                            editorFavZaman.commit();

                                                            SharedPreferences.Editor editorFav = spFavorilerim.edit();
                                                            editorFav.putString("1", user);
                                                            editorFav.commit();
                                                        } else {
                                                            int index = 0;
                                                            if (spFavorilerim.getString("10", "null").equals("null"))
                                                                index = 10;
                                                            if (spFavorilerim.getString("9", "null").equals("null"))
                                                                index = 9;
                                                            if (spFavorilerim.getString("8", "null").equals("null"))
                                                                index = 8;
                                                            if (spFavorilerim.getString("7", "null").equals("null"))
                                                                index = 7;
                                                            if (spFavorilerim.getString("6", "null").equals("null"))
                                                                index = 6;
                                                            if (spFavorilerim.getString("5", "null").equals("null"))
                                                                index = 5;
                                                            if (spFavorilerim.getString("4", "null").equals("null"))
                                                                index = 4;
                                                            if (spFavorilerim.getString("3", "null").equals("null"))
                                                                index = 3;
                                                            if (spFavorilerim.getString("2", "null").equals("null"))
                                                                index = 2;
                                                            if (spFavorilerim.getString("1", "null").equals("null"))
                                                                index = 1;

                                                            SharedPreferences.Editor editorFavZaman = spFavorilerimZaman.edit();
                                                            editorFavZaman.putLong("" + index, ds.getValue(Long.class));
                                                            editorFavZaman.commit();

                                                            SharedPreferences.Editor editorFav = spFavorilerim.edit();
                                                            editorFav.putString("" + index, user);
                                                            editorFav.commit();
                                                        }
                                                    }
                                                    else {
                                                        SharedPreferences.Editor editorFavZaman = spFavorilerimZaman.edit();
                                                        editorFavZaman.putLong("1", ds.getValue(Long.class));
                                                        editorFavZaman.commit();

                                                        SharedPreferences.Editor editorFav = spFavorilerim.edit();
                                                        editorFav.putString("1", user);
                                                        editorFav.commit();
                                                    }
                                                    Collections.sort(mUsers2, new Comparator<TariheGoreListeleSadeceResim>() {
                                                        @Override
                                                        public int compare(TariheGoreListeleSadeceResim o1, TariheGoreListeleSadeceResim o2) {
                                                            return (int) (o2.getZaman() - o1.getZaman());
                                                        }
                                                    });
                                                    Sonuc(child);
                                                }
                                            }
                                        }
                                        else {
                                            SharedPreferences.Editor editorFavZaman = spFavorilerimZaman.edit();
                                            for (int i = 0; i < 10; i++) {
                                                if (spFavorilerim.getString("" + i, "null").equals(user)) {
                                                    if (spFavorilerimZaman.getLong("" + i, 0) != ds.getValue(Long.class)) {
                                                        editorFavZaman.putLong("" + i, ds.getValue(Long.class));
                                                        editorFavZaman.commit();
                                                        for (int a = 0; a < mUsers2.size(); a++) {
                                                            if (mUsers2.get(a).getUid().equals(user)) {
                                                                TariheGoreListeleSadeceResim item = new TariheGoreListeleSadeceResim(user, ds.getValue(Long.class));
                                                                mUsers2.set(a, item);
                                                                Collections.sort(mUsers2, new Comparator<TariheGoreListeleSadeceResim>() {
                                                                    @Override
                                                                    public int compare(TariheGoreListeleSadeceResim o1, TariheGoreListeleSadeceResim o2) {
                                                                        return (int) (o2.getZaman() - o1.getZaman());
                                                                    }
                                                                });
                                                                Sonuc(child);
                                                            }
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                    else {
                                        if (!bulduklarimdakilerinUid.contains(user)) {
                                            bulduklarimdakilerinUid.add(user);
                                            TariheGoreListeleSadeceResim item = new TariheGoreListeleSadeceResim(user, ds.child("son_gordugum_zaman").getValue(Long.class));
                                            mUsers.add(mUsers.size(), item);

                                            ArrayList<String> bulduklarim = new ArrayList<>();
                                            for (int i = 0; i < 10; i++) {
                                                if (!spBulduklarim.getString("" + (i + 1), "null").equals("null"))
                                                    bulduklarim.add(spBulduklarim.getString("" + (i + 1), "null"));
                                                if (i == 9) {
                                                    if (bulduklarim.size() < 10) {
                                                        if (bulduklarim.size() == 0 || bulduklarim == null) {
                                                            SharedPreferences.Editor editorBulZaman = spBulduklarimZaman.edit();
                                                            editorBulZaman.putLong("1", ds.child("son_gordugum_zaman").getValue(Long.class));
                                                            editorBulZaman.commit();

                                                            SharedPreferences.Editor editorBul = spBulduklarim.edit();
                                                            editorBul.putString("1", user);
                                                            editorBul.commit();
                                                        }
                                                        else {
                                                            int index = 0;
                                                            if (spBulduklarim.getString("10", "null").equals("null"))
                                                                index = 10;
                                                            if (spBulduklarim.getString("9", "null").equals("null"))
                                                                index = 9;
                                                            if (spBulduklarim.getString("8", "null").equals("null"))
                                                                index = 8;
                                                            if (spBulduklarim.getString("7", "null").equals("null"))
                                                                index = 7;
                                                            if (spBulduklarim.getString("6", "null").equals("null"))
                                                                index = 6;
                                                            if (spBulduklarim.getString("5", "null").equals("null"))
                                                                index = 5;
                                                            if (spBulduklarim.getString("4", "null").equals("null"))
                                                                index = 4;
                                                            if (spBulduklarim.getString("3", "null").equals("null"))
                                                                index = 3;
                                                            if (spBulduklarim.getString("2", "null").equals("null"))
                                                                index = 2;
                                                            if (spBulduklarim.getString("1", "null").equals("null"))
                                                                index = 1;
                                                            SharedPreferences.Editor editorBulZaman = spBulduklarimZaman.edit();
                                                            editorBulZaman.putLong("" + index, ds.child("son_gordugum_zaman").getValue(Long.class));
                                                            editorBulZaman.commit();
                                                            //Toast.makeText(TaraActivity.this, "yine indiriyo", Toast.LENGTH_SHORT).show();
                                                            SharedPreferences.Editor editorBul = spBulduklarim.edit();
                                                            editorBul.putString("" + index, user);
                                                            editorBul.commit();
                                                        }
                                                    } else {
                                                        SharedPreferences.Editor editorBulZaman = spBulduklarimZaman.edit();
                                                        editorBulZaman.putLong("1", ds.child("son_gordugum_zaman").getValue(Long.class));
                                                        editorBulZaman.commit();

                                                        SharedPreferences.Editor editorBul = spBulduklarim.edit();
                                                        editorBul.putString("1", user);
                                                        editorBul.commit();
                                                    }
                                                    Collections.sort(mUsers, new Comparator<TariheGoreListeleSadeceResim>() {
                                                        @Override
                                                        public int compare(TariheGoreListeleSadeceResim o1, TariheGoreListeleSadeceResim o2) {
                                                            return (int) (o2.getZaman() - o1.getZaman());
                                                        }
                                                    });
                                                    Sonuc(child);
                                                }
                                            }
                                        } else {
                                            SharedPreferences.Editor editorBulZaman = spBulduklarimZaman.edit();
                                            for (int i = 0; i < 10; i++) {
                                                if (spBulduklarim.getString("" + (i + 1), "null").equals(user)) {
                                                    if (spBulduklarimZaman.getLong("" + (i + 1), 0) != ds.child("son_gordugum_zaman").getValue(Long.class)) {
                                                        editorBulZaman.putLong("" + (i + 1), ds.child("son_gordugum_zaman").getValue(Long.class));
                                                        editorBulZaman.commit();
                                                        for (int a = 0; a < mUsers.size(); a++) {
                                                            if (mUsers.get(a).getUid().equals(user)) {
                                                                TariheGoreListeleSadeceResim item = new TariheGoreListeleSadeceResim(user, ds.child("son_gordugum_zaman").getValue(Long.class));
                                                                mUsers.set(a, item);
                                                                Collections.sort(mUsers, new Comparator<TariheGoreListeleSadeceResim>() {
                                                                    @Override
                                                                    public int compare(TariheGoreListeleSadeceResim o1, TariheGoreListeleSadeceResim o2) {
                                                                        return (int) (o2.getZaman() - o1.getZaman());
                                                                    }
                                                                });
                                                                Sonuc(child);
                                                            }
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                    favori_sayisi.setText("" + Kullanici.getFavori_sayim() + "/" + mUsers2.size());
                                } else {
                                    BuUidOlanVerileriSil(user,child);
                                    /*for (int i = 0; i < 10; i++) {
                                        if (spFavorilerim.getString("" + (i + 1), "null").equals(user)) {
                                            //Toast.makeText(TaraActivity.this, "a"+(i+1), Toast.LENGTH_SHORT).show();
                                            SharedPreferences.Editor editorFav = spFavorilerim.edit();
                                            editorFav.remove("" + (i + 1));
                                            editorFav.commit();
                                        }
                                        if (spBulduklarim.getString("" + (i + 1), "null").equals(user)) {
                                            //Toast.makeText(TaraActivity.this, "b"+(i+1), Toast.LENGTH_SHORT).show();
                                            SharedPreferences.Editor editorBul = spBulduklarim.edit();
                                            editorBul.remove("" + (i + 1));
                                            editorBul.commit();
                                        }
                                    }*/
                                }
                            }
                            else {
                                BuUidOlanVerileriSil(user,child);
                                /*ContextWrapper cw = new ContextWrapper(TaraActivity.this);
                                File directory = cw.getDir("kullanici_resimleri", MODE_PRIVATE);

                                for (File files : directory.listFiles()) {
                                    if (files.getName().substring(7, files.getName().length() - 4).equals(user)) {
                                        files.delete();
                                        for (int i = 0; i < 10; i++) {
                                            if (spFavorilerim.getString("" + (i + 1), "null").equals(user)) {
                                                SharedPreferences.Editor editorFav = spFavorilerim.edit();
                                                editorFav.remove("" + (i + 1));
                                                editorFav.commit();
                                            }
                                            if (spBulduklarim.getString("" + (i + 1), "null").equals(user)) {
                                                SharedPreferences.Editor editorBul = spBulduklarim.edit();
                                                editorBul.remove("" + (i + 1));
                                                editorBul.commit();
                                            }
                                        }
                                    }
                                }*/
                            }
                        }
                        else{
                            BuUidOlanVerileriSil(user,child);
                            /*ContextWrapper cw = new ContextWrapper(TaraActivity.this);
                            File directory = cw.getDir("kullanici_resimleri", MODE_PRIVATE);

                            for(File files : directory.listFiles()){
                                if(files.getName().substring(7,files.getName().length()-4).equals(user)){
                                    files.delete();
                                    for (int i = 0; i < 10; i++){
                                        if (spFavorilerim.getString("" + (i+1), "null").equals(user)) {
                                            SharedPreferences.Editor editorFav = spFavorilerim.edit();
                                            editorFav.remove("" + (i+1));
                                            editorFav.commit();
                                        }
                                        if (spBulduklarim.getString("" + (i+1), "null").equals(user)) {
                                            SharedPreferences.Editor editorBul = spBulduklarim.edit();
                                            editorBul.remove("" + (i+1));
                                            editorBul.commit();
                                        }
                                        if(i == 9){
                                            for(int a = 0; a < 10; a++) {
                                                if (mUsers.size() > a)
                                                if (mUsers.get(a).getUid().equals(user)) {
                                                    mUsers.remove(a);
                                                }
                                                if (mUsers2.size() > a)
                                                if (mUsers2.get(a).getUid().equals(user)) {
                                                    mUsers2.remove(a);
                                                }
                                                if (a == 9)
                                                    Sonuc(child);
                                            }
                                        }
                                    }
                                }
                            }*/
                        }
                    }
                    else
                        dataSnapshotAsil.child(child).child(ds.getKey()).getRef().removeValue();


                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });


        }
    }

    private void BuUidOlanVerileriSil(String uid, String child) {
        ContextWrapper cw = new ContextWrapper(TaraActivity.this);
        File directory = cw.getDir("kullanici_resimleri", MODE_PRIVATE);

        for(File files : directory.listFiles()){
            if(files.getName().substring(7,files.getName().length()-4).equals(uid)){
                files.delete();
            }
        }
        for (int i = 0; i < 10; i++){
            if(child.equals("favorilerim")) {
                if (spFavorilerim.getString("" + (i + 1), "null").equals(uid)) {
                    SharedPreferences.Editor editorFav = spFavorilerim.edit();
                    editorFav.remove("" + (i + 1));
                    editorFav.commit();

                    SharedPreferences.Editor editorFavZaman = spFavorilerimZaman.edit();
                    editorFavZaman.remove("" + (i + 1));
                    editorFavZaman.commit();
                    if(i == 9){
                        for(int a = 0; a < 10; a++) {
                            if (mUsers2.size() > a)
                                if (mUsers2.get(a).getUid().equals(uid)) {
                                    mUsers2.remove(a);
                                }
                            if (a == 9)
                                Sonuc(child);
                        }
                    }
                }
            }
            else {
                if (spBulduklarim.getString("" + (i + 1), "null").equals(uid)) {
                    SharedPreferences.Editor editorBul = spBulduklarim.edit();
                    editorBul.remove("" + (i + 1));
                    editorBul.commit();

                    SharedPreferences.Editor editorBulZaman = spBulduklarimZaman.edit();
                    editorBulZaman.remove("" + (i + 1));
                    editorBulZaman.commit();
                    if(i == 9){
                        for(int a = 0; a < 10; a++) {
                            if (mUsers.size() > a)
                                if (mUsers.get(a).getUid().equals(uid)) {
                                    mUsers.remove(a);
                                }
                            if (a == 9)
                                Sonuc(child);
                        }
                    }
                }
            }
        }
    }

    private void Sonuc(String child){
        if(child.equals("favorilerim")) {
            Collections.sort(mUsers2, new Comparator<TariheGoreListeleSadeceResim>() {
                @Override
                public int compare(TariheGoreListeleSadeceResim o1, TariheGoreListeleSadeceResim o2) {
                    return (int) (o2.getZaman() - o1.getZaman());
                }
            });
            resimKullaniciAdapterFav = new ResimKullaniciAdapter(TaraActivity.this,mUsers2);
            favorilerimRV.setAdapter(resimKullaniciAdapterFav);
            favoriKismi.setVisibility(View.VISIBLE);
        }
        else {
            Collections.sort(mUsers, new Comparator<TariheGoreListeleSadeceResim>() {
                @Override
                public int compare(TariheGoreListeleSadeceResim o1, TariheGoreListeleSadeceResim o2) {
                    return (int) (o2.getZaman() - o1.getZaman());
                }
            });
            resimKullaniciAdapterBul = new ResimKullaniciAdapter(TaraActivity.this,mUsers);
            bulduklarimRV.setAdapter(resimKullaniciAdapterBul);
            bulunanlarKismi.setVisibility(View.VISIBLE);
        }
    }


    private void GorevleriGetir() {
        final DatabaseReference gorevRef = FirebaseDatabase.getInstance().getReference("Gorevler");
        gorevRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull final DataSnapshot dataSnapshot) {
                genelDS = dataSnapshot;
                gorevler = new ArrayList<>();
                kacGorevVar = (int) dataSnapshot.getChildrenCount();
                for(DataSnapshot ds : dataSnapshot.getChildren()){
                    gorevler.add(ds.getKey());
                    if(ds.hasChild("tamamlayanlar"))
                        if(ds.child("tamamlayanlar").hasChild(fuser.getUid())) {
                            kacGorevVar--;
                            gorevler.remove(ds.getKey());
                        }
                }
                AktifVePasifOlacaklar();
                GorevleriAta();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    private void GorevleriAta() {
        pbar.setVisibility(View.VISIBLE);
        SayfaDegistir();
        kacKisiBegenmisim = null;
        kacKisiBulmusum = null;
        kacKisiIleMesajlasmisim = null;
        kacReferansimVar = null;
        kacFavorimVar = null;
        DataSnapshot ds = genelDS.child(gorevler.get(degistirDeger-1));
        if (ds.child("yazilar").getValue().equals("var")) {
            mekan_yok.setVisibility(View.GONE);
            reklam = false;
            baslikKismi.setBackground(gorev_arka);
            if(ds.hasChild("tamamlayanlar")) {
                if(!ds.child("tamamlayanlar").hasChild(fuser.getUid())) {
                    normalGorevLay.setVisibility(View.VISIBLE);
                    konuma_gidenlerRel.setVisibility(View.GONE);
                    info.setVisibility(View.VISIBLE);
                    odul.setVisibility(View.VISIBLE);
                    baglantiVarMi = false;
                    baglanti = null;
                    pageRel.setVisibility(View.VISIBLE);
                    baslik.setText("Profil Görevleri");
                    pbar.setVisibility(View.GONE);
                    gorunmeyenFoto.setColorFilter(null);
                    gorunmeyenFoto.setBackground(null);
                    gorunmeyenFoto.setImageDrawable(null);
                    infoYazi = ds.child("bilgi").getValue(String.class);
                    odulYazi = ds.child("odul").getValue(String.class);
                    sure = ds.child("sure").getValue(Long.class);
                    zaman = ds.child("zaman").getValue(Long.class);
                    odul.setText(odulYazi);
                    GorevIlerlemesiIcin(ds.child("gorev-id").getValue(String.class), ds);
                    DeaktifNormalGorev(ds);
                }
            }
            else{
                normalGorevLay.setVisibility(View.VISIBLE);
                konuma_gidenlerRel.setVisibility(View.GONE);
                info.setVisibility(View.VISIBLE);
                odul.setVisibility(View.VISIBLE);
                baglantiVarMi = false;
                baglanti = null;
                pageRel.setVisibility(View.VISIBLE);
                baslik.setText("Profil Görevleri");
                pbar.setVisibility(View.GONE);
                gorunmeyenFoto.setColorFilter(null);
                gorunmeyenFoto.setBackground(null);
                gorunmeyenFoto.setImageDrawable(null);
                infoYazi = ds.child("bilgi").getValue(String.class);
                odulYazi = ds.child("odul").getValue(String.class);
                sure = ds.child("sure").getValue(Long.class);
                zaman = ds.child("zaman").getValue(Long.class);
                odul.setText(odulYazi);
                GorevIlerlemesiIcin(ds.child("gorev-id").getValue(String.class), ds);
                DeaktifNormalGorev(ds);
            }
        }
        else if(ds.child("yazilar").getValue().equals("yok")){
            mekan_yok.setVisibility(View.GONE);
            reklam = true;
            normalGorevLay.setVisibility(View.GONE);
            konuma_gidenlerRel.setVisibility(View.INVISIBLE);
            info.setVisibility(View.INVISIBLE);
            odul.setVisibility(View.INVISIBLE);
            //pageRel.setVisibility(View.INVISIBLE);
            gorunmeyenFoto.setColorFilter(null);
            gorunmeyenFoto.setBackground(null);
            gorunmeyenFoto.setImageDrawable(null);

            ContextWrapper cw = new ContextWrapper(TaraActivity.this);
            File directory = cw.getDir("gorev_resimler", MODE_PRIVATE);

            SharedPreferences gorevpref = getSharedPreferences("GFotolar", MODE_PRIVATE);
            SharedPreferences.Editor gorevprefE = gorevpref.edit();

            File image = new File(directory, ds.getKey()+".jpg");
            if (image.exists()) {
                gorunmeyenFoto.setImageURI(Uri.parse(image.getAbsolutePath()));
                pbar.setVisibility(View.GONE);
                if(!gorevpref.getString(ds.getKey(),"null").equals(ds.child("resimurl").getValue(String.class).substring(ds.child("resimurl").getValue(String.class).length() - 3))){
                    new ResimIndir(getApplicationContext(), ds.child("resimurl").getValue(String.class), "gorev_resimler", ds.getKey()+".jpg");
                    Glide.with(getApplicationContext())
                            .asBitmap()
                            .load(ds.child("resimurl").getValue(String.class))
                            .listener(new RequestListener<Bitmap>() {
                                @Override
                                public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Bitmap> target, boolean isFirstResource) {
                                    return false;
                                }

                                @Override
                                public boolean onResourceReady(Bitmap resource, Object model, Target<Bitmap> target, DataSource dataSource, boolean isFirstResource) {
                                    pbar.setVisibility(View.GONE);
                                    return false;
                                }
                            }).into(gorunmeyenFoto);
                    gorevprefE.putString(ds.getKey(),ds.child("resimurl").getValue(String.class).substring(ds.child("resimurl").getValue(String.class).length() - 3));
                    gorevprefE.commit();
                }
            }
            else{
                new ResimIndir(getApplicationContext(), ds.child("resimurl").getValue(String.class), "gorev_resimler", ds.getKey()+".jpg");
                Glide.with(getApplicationContext())
                        .asBitmap()
                        .load(ds.child("resimurl").getValue(String.class))
                        .listener(new RequestListener<Bitmap>() {
                            @Override
                            public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Bitmap> target, boolean isFirstResource) {
                                return false;
                            }

                            @Override
                            public boolean onResourceReady(Bitmap resource, Object model, Target<Bitmap> target, DataSource dataSource, boolean isFirstResource) {
                                pbar.setVisibility(View.GONE);
                                return false;
                            }
                        }).into(gorunmeyenFoto);
                gorevprefE.putString(ds.getKey(),ds.child("resimurl").getValue(String.class).substring(ds.child("resimurl").getValue(String.class).length() - 3));
                gorevprefE.commit();
            }

            baglantiVarMi = true;
            baglanti = ds.child("baglantiurl").getValue(String.class);
            baslik.setText(ds.child("baslik").getValue().toString().toUpperCase());

            int color = Color.parseColor(ds.child("baslikrengi").getValue(String.class));

            GradientDrawable drawable = new GradientDrawable();
            drawable.setCornerRadii(new float[]{40,40,40,40,0,0,0,0});
            drawable.setOrientation(GradientDrawable.Orientation.LEFT_RIGHT);
            drawable.setColors(new int[]{
                    color,
                    color,
            });
            baslikKismi.setBackground(drawable);
        }
        else if(ds.child("yazilar").getValue().equals("yerler")){
            reklam = false;
            baslikKismi.setBackground(gorev_arka);
            normalGorevLay.setVisibility(View.GONE);
            konuma_gidenlerRel.setVisibility(View.VISIBLE);
            info.setVisibility(View.INVISIBLE);
            odul.setVisibility(View.INVISIBLE);
            baglantiVarMi = false;
            baglanti = null;
            pageRel.setVisibility(View.VISIBLE);
            baslik.setText("Konumlar");
            pbar.setVisibility(View.GONE);
            gorunmeyenFoto.setColorFilter(null);
            gorunmeyenFoto.setBackground(null);
            gorunmeyenFoto.setImageDrawable(null);
            //pageRel.setVisibility(View.INVISIBLE);
            YerleriListele(false);
        }
        TasarimDegistir();
    }

    public void TasarimDegistir() {

        SharedPreferences tas_shared = getSharedPreferences("TasarimBilgileri",MODE_PRIVATE);
        SharedPreferences.Editor editor = tas_shared.edit();
        if (!tas_shared.getString("tasarim_arayuz","1").equals("sifirlandi"))
            tasDegeri = tas_shared.getString("tasarim_arayuz","1");

        GradientDrawable gradientArka = new GradientDrawable();
        GradientDrawable gradient = new GradientDrawable();
        GradientDrawable gradient2 = (GradientDrawable) getResources().getDrawable(R.drawable.gradient2);
        GradientDrawable gradientYumusalk =  (GradientDrawable) getResources().getDrawable(R.drawable.gradient_yumusak_her_yer);
        GradientDrawable gradientGorevIcin =  new GradientDrawable();
        GradientDrawable mesajlarArkasi =  (GradientDrawable) getResources().getDrawable(R.drawable.ovalgradient);
        gorev_arka =  new GradientDrawable();

        gorev_arka.setCornerRadii(new float[]{40,40,40,40,0,0,0,0});
        gorev_arka.setOrientation(GradientDrawable.Orientation.LEFT_RIGHT);
        //gorev_arka.setStroke(1,Color.WHITE);

        gradientArka.setOrientation(GradientDrawable.Orientation.LEFT_RIGHT);
        gradient.setOrientation(GradientDrawable.Orientation.LEFT_RIGHT);
        gradientGorevIcin.setOrientation(GradientDrawable.Orientation.LEFT_RIGHT);
        dialogIcinOzelBackground = new GradientDrawable();
        dialogIcinOzelBackground.setOrientation(GradientDrawable.Orientation.LEFT_RIGHT);
        dialogIcinOzelBackground.setCornerRadius(70);

        int renk1 = 0,renk2 = 0,t1start = 0,t2start = 0,t1end = 0,t2end = 0,orta = 0;


        renk1 = TasarimRenginiGetir.RengiGetir(TaraActivity.this,"renk1",tasDegeri);
        renk2 = TasarimRenginiGetir.RengiGetir(TaraActivity.this,"renk2",tasDegeri);
        t1start = TasarimRenginiGetir.RengiGetir(TaraActivity.this,"t1start",tasDegeri);
        t2start = TasarimRenginiGetir.RengiGetir(TaraActivity.this,"t2start",tasDegeri);
        t1end = TasarimRenginiGetir.RengiGetir(TaraActivity.this,"t1end",tasDegeri);
        t2end = TasarimRenginiGetir.RengiGetir(TaraActivity.this,"t2end",tasDegeri);
        orta = TasarimRenginiGetir.RengiGetir(TaraActivity.this,"orta",tasDegeri);

        ColorStateList buttonStates = new ColorStateList(
                new int[][]{
                        new int[]{-android.R.attr.state_enabled},
                        new int[]{android.R.attr.state_checked},
                        new int[]{}
                },
                new int[]{
                        getResources().getColor(R.color.gri3),
                        ortaRenk,
                        getResources().getColor(R.color.gri4)
                }
        );
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            evdeMiyim.getThumbDrawable().setTintList(buttonStates);
        }

        ilkRenk = renk1;
        sonRenk = renk2;
        ortaRenk = orta;


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            VectorDrawable kalp = (VectorDrawable) getDrawable(R.drawable.ic_profil_begen1);
            kalp.setTint(orta);
            kalp.setBounds(0, 0, kalp.getIntrinsicHeight(), kalp.getIntrinsicWidth());
            VectorDrawable cancel = (VectorDrawable) getDrawable(R.drawable.ic_cancel);
            cancel.setTint(orta);
            cancel.setBounds(0, 0, cancel.getIntrinsicHeight(), cancel.getIntrinsicWidth());
            VectorDrawable save = (VectorDrawable) getDrawable(R.drawable.ic_save);
            save.setTint(orta);
            save.setBounds(0, 0, save.getIntrinsicHeight(), save.getIntrinsicWidth());
            VectorDrawable duzenle = (VectorDrawable) getDrawable(R.drawable.ic_duzenle);
            duzenle.setTint(orta);
            duzenle.setBounds(0, 0, duzenle.getIntrinsicHeight(), duzenle.getIntrinsicWidth());
        }

        GradientDrawable kapakEkle = (GradientDrawable) getResources().getDrawable(R.drawable.kapak_ekle);
        kapakEkle.setStroke(1,orta);
        GradientDrawable butonArka = (GradientDrawable) getResources().getDrawable(R.drawable.buton_arka_et_gibi);
        butonArka.setStroke(1,orta);


        dialogIcinOzelBackground.setColors(new int[]{
                renk1,
                orta,
                renk2,
        });
        gradientArka.setColors(new int[]{
                renk1,
                orta,
                renk2,
        });
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
        gradientYumusalk.setColors(new int[]{
                renk1,
                orta,
                renk2,
        });
        gradientGorevIcin.setColors(new int[]{
                renk1,
                orta,
                renk2,
        });
        gorev_arka.setColors(new int[]{
                t1end,
                orta,
                t2start,
        });
        mesajlarArkasi.setColors(new int[]{
                renk1,
                t1end,
        });


        BitmapBelirle(bulma,getResources().getDrawable(R.drawable.bulma_efekt),t1end, orta, t2start);
        BitmapBelirle(bulunma,getResources().getDrawable(R.drawable.bulunma_efekt),t1end, orta, t2start);
        gradientNormal = gradient;
        gradientYumusak = gradientYumusalk;
        mesajlar_arka.setImageDrawable(mesajlarArkasi);
        background.setBackground(gradientArka);
        gorunmeyenFoto.setBackground(gradientGorevIcin);
        //baslikKismi.setBackground(gorev_arka);
    }

    public static Bitmap drawableToBitmap (Drawable drawable) {
        Bitmap bitmap = null;

        if (drawable instanceof BitmapDrawable) {
            BitmapDrawable bitmapDrawable = (BitmapDrawable) drawable;
            if(bitmapDrawable.getBitmap() != null) {
                return bitmapDrawable.getBitmap();
            }
        }

        if(drawable.getIntrinsicWidth() <= 0 || drawable.getIntrinsicHeight() <= 0) {
            bitmap = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888); // Single color bitmap will be created of 1x1 pixel
        } else {
            bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        }

        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);
        return bitmap;
    }
    public void BitmapBelirle(ImageView resim, Drawable drawable, int renk1, int orta, int renk2){

        Bitmap newBitmap = addGradient(drawableToBitmap(drawable),renk1,orta,renk2);
        resim.setImageDrawable(new BitmapDrawable(getResources(), newBitmap));
    }

    public Bitmap addGradient(Bitmap originalBitmap, int renk1, int orta, int renk2) {
        int width = originalBitmap.getWidth();
        int height = originalBitmap.getHeight();
        Bitmap updatedBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(updatedBitmap);

        canvas.drawBitmap(originalBitmap, 0, 0, null);

        Paint paint = new Paint();
        float[] positions = null;
        int[] colors = {
                renk1,
                orta,
                renk2
        };
        LinearGradient shader = new LinearGradient(0, 0, width, 0, colors, positions, Shader.TileMode.CLAMP);
        paint.setShader(shader);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawRect(0, 0, width, height, paint);

        return updatedBitmap;
    }

    private Bitmap createDynamicGradient(Drawable drawable) {
        Bitmap mutableBitmap = Bitmap.createBitmap(100, 100, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(mutableBitmap);
        drawable.setBounds(0, 0, 100, 100);
        drawable.draw(canvas);

        return mutableBitmap;
    }
    private void YerleriListele(boolean dialogMu) { // Kullanıcılar ilk girdiğinde nereye gideceğini soran dialog
        List<Yer> mYer = new ArrayList<>();
        if(Kullanici != null) {
            YerAdapter yerAdapter = new YerAdapter(getApplicationContext(), TaraActivity.this, mYer, Kullanici);
            DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Yerler").child(Kullanici.getSehir());
            ref.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if(dataSnapshot.exists()) {
                        for (DataSnapshot ds : dataSnapshot.getChildren()) {
                            Yer yer = ds.getValue(Yer.class);
                            if (yer.getAcik_mi().equals("acik"))
                                if (!yer.getKriter().equals("+18")) {
                                    if (!mYer.contains(yer)) {
                                        if(!yer.getDbisim().equals("espark") && !yer.getDbisim().equals("kanatli"))
                                            mYer.add(yer);
                                        else
                                            mYer.add(0,yer);
                                    }
                                }
                                else{
                                    if (Integer.valueOf(Kullanici.getDg()) >= 18){
                                        if (!mYer.contains(yer)) {
                                            mYer.add(yer);
                                        }
                                    }
                                }
                                if (!dialogMu)
                                    yerlerRV.setAdapter(yerAdapter);
                                else
                                    dialogRV.setAdapter(yerAdapter);
                        }
                    }
                    else {
                        if(!dialogMu) {
                            mekan_yok.setVisibility(View.VISIBLE);
                            yerlerRV.setVisibility(View.GONE);
                        }
                        else{
                            konumSorDialog.dismiss();
                            Dialog dialog = new Dialog(TaraActivity.this);
                            dialog.setContentView(R.layout.dialog_dizayn3);
                            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                            dialog.setCancelable(true);

                            TextView baslik = dialog.findViewById(R.id.baslik);
                            TextView aciklama = dialog.findViewById(R.id.aciklama);
                            aciklama.setMovementMethod(new ScrollingMovementMethod());
                            LinearLayout lay1 = dialog.findViewById(R.id.lay1);
                            LinearLayout lay2 = dialog.findViewById(R.id.lay2);
                            ProgressBar pbar = dialog.findViewById(R.id.pbar);
                            Button buton = dialog.findViewById(R.id.buton);
                            EditText et = dialog.findViewById(R.id.editText);
                            et.setHint("Mekan tavsiyesi...");
                            baslik.setText("Konum Tavsiyesi");
                            aciklama.setText("Henüz şehrinize özel hiçbir mekan eklenmemiş. Bize şehrinizdeki popüler mekanlardan birini önerir misiniz?");
                            buton.setText("TAMAM");
                            buton.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    lay1.setVisibility(View.GONE);
                                    lay2.setVisibility(View.GONE);
                                    pbar.setVisibility(View.VISIBLE);
                                    if (et.getText().toString().trim().length() > 0) {
                                        FirebaseDatabase.getInstance().getReference("KonumTavsiyeleri").child(Kullanici.getSehir()).child(Kullanici.getUid()).setValue(et.getText().toString()).addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(@NonNull Void unused) {
                                                Toast.makeText(TaraActivity.this, "Tavsiyeniz için teşekkür ederiz :)", Toast.LENGTH_SHORT).show();
                                                dialog.dismiss();
                                            }
                                        });
                                    }
                                    else
                                        dialog.dismiss();
                                }
                            });

                            dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                                @Override
                                public void onCancel(DialogInterface dialog) {
                                    dialogAktifMi = false;
                                }
                            });
                            dialog.show();
                        }
                        //Toast.makeText(TaraActivity.this, "Şehrinize özel hiç mekan eklenmemiş.", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }

    }

    private ImageView HangiPage(){
        if(degistirDeger == 1){
            if(kacGorevVar == 2)
                return page2;
            else if(kacGorevVar == 3)
                return page2;
            else if(kacGorevVar == 4)
                return page1;
            else
                return page1;
        }
        else if(degistirDeger == 2){
            if(kacGorevVar == 2)
                return page3;
            else if(kacGorevVar == 3)
                return page3;
            else if(kacGorevVar == 4)
                return page2;
            else
                return page2;
        }
        else if(degistirDeger == 3){
            if(kacGorevVar == 3)
                return page4;
            else if(kacGorevVar == 4)
                return page3;
            else
                return page3;
        }
        else if(degistirDeger == 4){
            if(kacGorevVar == 4)
                return page4;
            else
                return page4;
        }
        else {
            if(kacGorevVar == 5)
                return page5;
            else
                return page5;
        }
    }

    private void SayfaDegistir() {
        HangiPage().getLayoutParams().width = 5;
        HangiPage().requestLayout();
        if(!page1.equals(HangiPage())){
            page1.getLayoutParams().width = 0;
            page1.requestLayout();
        }
        if(!page2.equals(HangiPage())){
            page2.getLayoutParams().width = 0;
            page2.requestLayout();
        }
        if(!page3.equals(HangiPage())){
            page3.getLayoutParams().width = 0;
            page3.requestLayout();
        }
        if(!page4.equals(HangiPage())){
            page4.getLayoutParams().width = 0;
            page4.requestLayout();
        }
        if(!page5.equals(HangiPage())){
            page5.getLayoutParams().width = 0;
            page5.requestLayout();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        myTimer.cancel();
    }

    private void GorevIlerlemesiIcin(final String id, final DataSnapshot ds) {
        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(int i = 1; i < ds.child("gorevler").getChildrenCount()+1; i ++) {
                    if (ds.child("gorevler").child(String.valueOf(i)).getValue(String.class).substring(0, 1).equals("5")) {
                        if (dataSnapshot.hasChild("favorilerim"))
                            kacFavorimVar = String.valueOf(dataSnapshot.child("favorilerim").getChildrenCount());
                        else {
                            favori_sayisi.setText(""+Kullanici.getFavori_sayim()+"/0");
                            if(kacFavorimVar == null || kacFavorimVar.equals("0")){
                                kacFavorimVar = "0";
                            }
                        }
                    }
                    if (ds.child("gorevler").child(String.valueOf(i)).getValue(String.class).substring(0, 1).equals("1")) {
                        if (dataSnapshot.hasChild("bulduklarim"))
                            kacKisiBulmusum = String.valueOf(dataSnapshot.child("bulduklarim").getChildrenCount());
                        else {
                            if(kacKisiBulmusum == null || kacKisiBulmusum.equals("0")){
                                kacKisiBulmusum = "0";
                            }
                        }
                    }
                }
                if(dataSnapshot.hasChild(id)){
                    if(dataSnapshot.child(id).child("tamamlandimi").getValue(String.class).equals("hayir")) {
                        TumBilgileriKontrolEt(id, false, ds);
                        damga.setVisibility(View.GONE);
                    }
                    else{
                        pbar.setVisibility(View.GONE);
                        GorevBitmis();
                    }
                }
                else{
                    damga.setVisibility(View.GONE);
                    TumBilgileriKontrolEt(id,true,ds);
                    dataSnapshot.getRef().child(id).child("tamamlandimi").setValue("hayir");
                    for(int i = 1; i < ds.child("gorevler").getChildrenCount()+1; i ++){
                        if(ds.child("gorevler").child(String.valueOf(i)).getValue(String.class).substring(0,1).equals("5")){
                            dataSnapshot.getRef().child(id).child("kacFavorimVar").setValue(kacFavorimVar);
                        }
                        if(ds.child("gorevler").child(String.valueOf(i)).getValue(String.class).substring(0,1).equals("1")){
                            dataSnapshot.getRef().child(id).child("kacKisiBulmusum").setValue(kacKisiBulmusum);
                        }
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }
    private void GorevBitmis() {
        damga.setVisibility(View.VISIBLE);
        tik1.setVisibility(View.VISIBLE);
        tik2.setVisibility(View.VISIBLE);
        tik3.setVisibility(View.VISIBLE);
        tik4.setVisibility(View.VISIBLE);
        tik5.setVisibility(View.VISIBLE);
        tik1.setImageDrawable(getResources().getDrawable(R.drawable.tik));
        tik2.setImageDrawable(getResources().getDrawable(R.drawable.tik));
        tik3.setImageDrawable(getResources().getDrawable(R.drawable.tik));
        tik4.setImageDrawable(getResources().getDrawable(R.drawable.tik));
        tik5.setImageDrawable(getResources().getDrawable(R.drawable.tik));
    }
    private void TumBilgileriKontrolEt(final String id,final boolean kaydet, final DataSnapshot dss) {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("usersF").child(fuser.getUid());
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                sayRef = 0;
                sayBeg = 0;
                for(int i = 1; i < dss.child("gorevler").getChildrenCount()+1; i ++){
                    if(dss.child("gorevler").child(String.valueOf(i)).getValue(String.class).substring(0,1).equals("5")) {
                        bes = Integer.parseInt(dss.child("gorevler").child(String.valueOf(i)).getValue(String.class).substring(1));
                        GorevYapildiMi(id,dss);
                    }
                    if(dss.child("gorevler").child(String.valueOf(i)).getValue(String.class).substring(0,1).equals("1")) {
                        bir = Integer.parseInt(dss.child("gorevler").child(String.valueOf(i)).getValue(String.class).substring(1));
                        GorevYapildiMi(id,dss);
                    }
                    if(dss.child("gorevler").child(String.valueOf(i)).getValue(String.class).substring(0,1).equals("3")){
                        uc = Integer.parseInt(dss.child("gorevler").child(String.valueOf(i)).getValue(String.class).substring(1));

                        sayRef = (int) dataSnapshot.child("referanslarim").getChildrenCount();
                        kacReferansimVar = ""+ sayRef;
                        if(kaydet)
                            myRef.child(id).child("kacReferansimVar").setValue(kacReferansimVar);
                        else
                            GorevYapildiMi(id,dss);
                    }
                    if(dss.child("gorevler").child(String.valueOf(i)).getValue(String.class).substring(0,1).equals("4")){
                        dort = Integer.parseInt(dss.child("gorevler").child(String.valueOf(i)).getValue(String.class).substring(1));
                        if(dataSnapshot.hasChild("begendiklerim")){
                            sayBeg = (int) dataSnapshot.child("begendiklerim").getChildrenCount();
                            kacKisiBegenmisim = "" + sayBeg;
                            if(kaydet)
                                myRef.child(id).child("kacKisiBegenmisim").setValue(kacKisiBegenmisim);
                            else
                                GorevYapildiMi(id,dss);
                        }else{
                            if(kaydet)
                                myRef.child(id).child("kacKisiBegenmisim").setValue("0");
                        }
                    }
                    if(dss.child("gorevler").child(String.valueOf(i)).getValue(String.class).substring(0,1).equals("2")){
                        iki = Integer.parseInt(dss.child("gorevler").child(String.valueOf(i)).getValue(String.class).substring(1));
                        if(dataSnapshot.hasChild("mesajlastiklarim")){
                            kacKisiIleMesajlasmisim = ""+dataSnapshot.child("mesajlastiklarim").getChildrenCount();;
                            if(kaydet)
                                myRef.child(id).child("kacKisiIleMesajlasmisim").setValue(kacKisiIleMesajlasmisim);
                            else
                                GorevYapildiMi(id,dss);
                        }
                        else{
                            kacKisiIleMesajlasmisim = "0";
                            if(kaydet)
                                myRef.child(id).child("kacKisiIleMesajlasmisim").setValue(kacKisiIleMesajlasmisim);
                            else
                                GorevYapildiMi(id,dss);
                        }
                    }
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
    private TextView Hangisi(int deger){
        if(deger == 1){
            if(bulunmaHedefHangisi == 1)
                return gorevYazi1;
            else if(bulunmaHedefHangisi == 2)
                return gorevYazi2;
            else if(bulunmaHedefHangisi == 3)
                return gorevYazi3;
            else if(bulunmaHedefHangisi == 4)
                return gorevYazi4;
            else
                return gorevYazi5;
        }
        else if(deger == 2){
            if(mesajlasHedefHangisi == 1)
                return gorevYazi1;
            else if(mesajlasHedefHangisi == 2)
                return gorevYazi2;
            else if(mesajlasHedefHangisi == 3)
                return gorevYazi3;
            else if(mesajlasHedefHangisi == 4)
                return gorevYazi4;
            else
                return gorevYazi5;
        }
        else if(deger == 3){
            if(referansHedefHangisi == 1)
                return gorevYazi1;
            else if(referansHedefHangisi == 2)
                return gorevYazi2;
            else if(referansHedefHangisi == 3)
                return gorevYazi3;
            else if(referansHedefHangisi == 4)
                return gorevYazi4;
            else
                return gorevYazi5;
        }
        else if(deger == 4){
            if(begeniHedefHangisi == 1)
                return gorevYazi1;
            else if(begeniHedefHangisi == 2)
                return gorevYazi2;
            else if(begeniHedefHangisi == 3)
                return gorevYazi3;
            else if(begeniHedefHangisi == 4)
                return gorevYazi4;
            else
                return gorevYazi5;
        }
        else {
            if(favoriHedefHangisi == 1)
                return gorevYazi1;
            else if(favoriHedefHangisi == 2)
                return gorevYazi2;
            else if(favoriHedefHangisi == 3)
                return gorevYazi3;
            else if(favoriHedefHangisi == 4)
                return gorevYazi4;
            else
                return gorevYazi5;
        }
    }
    private ImageView TikHangisi(int deger){
        if(deger == 1){
            if(bulunmaHedefHangisi == 1)
                return tik1;
            else if(bulunmaHedefHangisi == 2)
                return tik2;
            else if(bulunmaHedefHangisi == 3)
                return tik3;
            else if(bulunmaHedefHangisi == 4)
                return tik4;
            else
                return tik5;
        }
        else if(deger == 2){
            if(mesajlasHedefHangisi == 1)
                return tik1;
            else if(mesajlasHedefHangisi == 2)
                return tik2;
            else if(mesajlasHedefHangisi == 3)
                return tik3;
            else if(mesajlasHedefHangisi == 4)
                return tik4;
            else
                return tik5;
        }
        else if(deger == 3){
            if(referansHedefHangisi == 1)
                return tik1;
            else if(referansHedefHangisi == 2)
                return tik2;
            else if(referansHedefHangisi == 3)
                return tik3;
            else if(referansHedefHangisi == 4)
                return tik4;
            else
                return tik5;
        }
        else if(deger == 4){
            if(begeniHedefHangisi == 1)
                return tik1;
            else if(begeniHedefHangisi == 2)
                return tik2;
            else if(begeniHedefHangisi == 3)
                return tik3;
            else if(begeniHedefHangisi == 4)
                return tik4;
            else
                return tik5;
        }
        else {
            if(favoriHedefHangisi == 1)
                return tik1;
            else if(favoriHedefHangisi == 2)
                return tik2;
            else if(favoriHedefHangisi == 3)
                return tik3;
            else if(favoriHedefHangisi == 4)
                return tik4;
            else
                return tik5;
        }
    }
    private void GorevYapildiMi(final String id,final DataSnapshot dss) {

        ArrayList<String> liste = new ArrayList<>();
        if(kacKisiBulmusum != null) {
            if (dataSnapshotAsil.child(id).hasChild("kacKisiBulmusum")) {
                int deger = Integer.parseInt(bulunmaHedef) - (Integer.parseInt(kacKisiBulmusum) - Integer.parseInt(dataSnapshotAsil.child(id).child("kacKisiBulmusum").getValue(String.class)));
                pbar.setVisibility(View.GONE);
                if (deger <= 0) {
                    if (!TikHangisi(1).getDrawable().getConstantState().equals(getResources().getDrawable(R.drawable.tik).getConstantState())) {
                        Hangisi(1).setText("" + bulunmaHedef + " Kişi bul");
                        Hangisi(1).setTextColor(getResources().getColor(R.color.transBeyaz));
                        if (!liste.contains("kacKisiBulmusum"))
                            liste.add("kacKisiBulmusum");
                        TikHangisi(1).setImageDrawable(getResources().getDrawable(R.drawable.tik));
                    } else if (!liste.contains("kacKisiBulmusum"))
                        liste.add("kacKisiBulmusum");
                } else {
                    Hangisi(1).setText("" + deger + " Kişi bul");
                    Hangisi(1).setTextColor(getResources().getColor(R.color.beyaz));
                }
            }
        }
        if(kacKisiIleMesajlasmisim != null) {
            if (dataSnapshotAsil.child(id).hasChild("kacKisiIleMesajlasmisim")) {
                int deger = Integer.parseInt(mesajlasHedef) - (Integer.parseInt(kacKisiIleMesajlasmisim) - Integer.parseInt(dataSnapshotAsil.child(id).child("kacKisiIleMesajlasmisim").getValue(String.class)));
                pbar.setVisibility(View.GONE);
                if (deger <= 0) {
                    if (!TikHangisi(2).getDrawable().getConstantState().equals(getResources().getDrawable(R.drawable.tik).getConstantState())) {
                        Hangisi(2).setText(mesajlasHedef + " Kişi ile mesajlaş");
                        Hangisi(2).setTextColor(getResources().getColor(R.color.transBeyaz));
                        if (!liste.contains("kacKisiIleMesajlasmisim"))
                            liste.add("kacKisiIleMesajlasmisim");
                        TikHangisi(2).setImageDrawable(getResources().getDrawable(R.drawable.tik));
                    } else if (!liste.contains("kacKisiIleMesajlasmisim"))
                        liste.add("kacKisiIleMesajlasmisim");
                } else {
                    Hangisi(2).setText("" + deger + " Kişi ile mesajlaş");
                    Hangisi(2).setTextColor(getResources().getColor(R.color.beyaz));
                }
            }
        }
        if(kacReferansimVar != null) {
            if (dataSnapshotAsil.child(id).hasChild("kacReferansimVar")) {
                int deger = Integer.parseInt(referansHedef) - (Integer.parseInt(kacReferansimVar) - Integer.parseInt(dataSnapshotAsil.child(id).child("kacReferansimVar").getValue(String.class)));
                pbar.setVisibility(View.GONE);
                if (deger <= 0) {
                    if (!TikHangisi(3).getDrawable().getConstantState().equals(getResources().getDrawable(R.drawable.tik).getConstantState())) {
                        Hangisi(3).setText(referansHedef + " Kişiye referans ol");
                        Hangisi(3).setTextColor(getResources().getColor(R.color.transBeyaz));
                        if (!liste.contains("kacReferansimVar"))
                            liste.add("kacReferansimVar");
                        TikHangisi(3).setImageDrawable(getResources().getDrawable(R.drawable.tik));
                    } else if (!liste.contains("kacReferansimVar"))
                        liste.add("kacReferansimVar");
                } else {
                    Hangisi(3).setText("" + deger + " Kişiye referans ol");
                    Hangisi(3).setTextColor(getResources().getColor(R.color.beyaz));
                }
            }
        }
        if(kacKisiBegenmisim != null) {
            if (dataSnapshotAsil.child(id).hasChild("kacKisiBegenmisim")) {
                int deger = Integer.parseInt(begeniHedef) - (Integer.parseInt(kacKisiBegenmisim) - Integer.parseInt(dataSnapshotAsil.child(id).child("kacKisiBegenmisim").getValue(String.class)));
                pbar.setVisibility(View.GONE);
                if (deger <= 0) {
                    if (!TikHangisi(4).getDrawable().getConstantState().equals(getResources().getDrawable(R.drawable.tik).getConstantState())) {
                        Hangisi(4).setText(begeniHedef + " Kişiyi beğen");
                        Hangisi(4).setTextColor(getResources().getColor(R.color.transBeyaz));
                        if (!liste.contains("kacKisiBegenmisim"))
                            liste.add("kacKisiBegenmisim");
                        TikHangisi(4).setImageDrawable(getResources().getDrawable(R.drawable.tik));
                    } else if (!liste.contains("kacKisiBegenmisim"))
                        liste.add("kacKisiBegenmisim");
                } else {
                    Hangisi(4).setText("" + deger + " Kişiyi beğen");
                    Hangisi(4).setTextColor(getResources().getColor(R.color.beyaz));
                }
            }
        }
        if(kacFavorimVar != null) {
            if (dataSnapshotAsil.child(id).hasChild("kacFavorimVar")) {
                int deger = Integer.parseInt(favoriHedef) - (Integer.parseInt(kacFavorimVar) - Integer.parseInt(dataSnapshotAsil.child(id).child("kacFavorimVar").getValue(String.class)));
                pbar.setVisibility(View.GONE);
                if (deger <= 0) {
                    if (!TikHangisi(5).getDrawable().getConstantState().equals(getResources().getDrawable(R.drawable.tik).getConstantState())) {
                        Hangisi(5).setText(favoriHedef + " Kişiyi favorilerine ekle");
                        Hangisi(5).setTextColor(getResources().getColor(R.color.transBeyaz));
                        if (!liste.contains("kacFavorimVar"))
                            liste.add("kacFavorimVar");
                        TikHangisi(5).setImageDrawable(getResources().getDrawable(R.drawable.tik));
                    } else if (!liste.contains("kacFavorimVar"))
                        liste.add("kacFavorimVar");
                } else {
                    Hangisi(5).setText("" + deger + " Kişiyi favorilerine ekle");
                    Hangisi(5).setTextColor(getResources().getColor(R.color.beyaz));
                }
            }
        }
        if(liste.size() == (int) dss.child("gorevler").getChildrenCount()) {
            OdulVer(id,dss);
        }

    }
    private void GelirBilgisiEkle(String uid, String id, int kacKP){
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
                            if(ds.getKey().length() >= id.length()) {
                                if (ds.getKey().substring(0, id.length()).equals(id)) {
                                    kpSayisi++;
                                }
                                if (deger == dataSnapshot.child("gelirlerim").getChildrenCount()) {
                                    ref.child("gelirlerim").child(id + kpSayisi).child("onceki_kp").setValue(dataSnapshot.child("kp").getValue(Integer.class));
                                    ref.child("gelirlerim").child(id + kpSayisi).child("verilen_kp").setValue(kacKP);
                                    ref.child("gelirlerim").child(id + kpSayisi).child("zaman").setValue(ServerValue.TIMESTAMP).addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(@NonNull Void unused) {
                                            dataSnapshot.child("kp").getRef().setValue(dataSnapshot.child("kp").getValue(Integer.class) + kacKP).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(@NonNull Void unused) {
                                                    Kullanici.setKp(Kullanici.getKp() + kacKP);
                                                    kp_yazi.setText("" + Kullanici.getKp());
                                                    dataSnapshot.child(id).getRef().removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                                        @Override
                                                        public void onSuccess(@NonNull Void unused) {
                                                            GorevBitmis();
                                                        }
                                                    });
                                                    Toast.makeText(TaraActivity.this, "Tebrikler " + kacKP + "KP kazandınız!", Toast.LENGTH_SHORT).show();
                                                }
                                            });
                                        }
                                    });
                                }
                            }
                        }
                    }
                    else{
                        ref.child("gelirlerim").child(id+"_1").child("onceki_kp").setValue(dataSnapshot.child("kp").getValue(Integer.class));
                        ref.child("gelirlerim").child(id +"_1").child("verilen_kp").setValue(kacKP);
                        ref.child("gelirlerim").child(id+"_1").child("zaman").setValue(ServerValue.TIMESTAMP).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(@NonNull Void unused) {
                                dataSnapshot.child("kp").getRef().setValue(dataSnapshot.child("kp").getValue(Integer.class) + kacKP).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(@NonNull Void unused) {
                                        Kullanici.setKp(Kullanici.getKp() + kacKP);
                                        kp_yazi.setText(""+Kullanici.getKp());
                                        dataSnapshot.child(id).getRef().removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(@NonNull Void unused) {
                                                GorevBitmis();
                                            }
                                        });Toast.makeText(TaraActivity.this, "Tebrikler " + kacKP + "KP kazandınız!", Toast.LENGTH_SHORT).show();
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
    private void OdulVer(String id, final DataSnapshot dss) {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("usersF").child(fuser.getUid());
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dss.child("odul").getValue(String.class).equals("Ödül: Kapak Resmi ve 500KP")) {
                    if(!dataSnapshot.child(id).child("tamamlandimi").getValue().equals("evet")) {
                        dataSnapshot.child(id).child("tamamlandimi").getRef().setValue("evet");
                        dss.child("tamamlayanlar").child(fuser.getUid()).getRef().setValue(ServerValue.TIMESTAMP);
                        dataSnapshot.child("kapak").getRef().setValue("evet").addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                GelirBilgisiEkle(fuser.getUid(),"gorev_"+id+"_",500);
                            }
                        });
                    }
                }
                if (!id.equals("kapak_gorevi")) {
                    if (id.equals("tesekkur")) {
                        if (!dataSnapshot.child(id).child("tamamlandimi").getValue().equals("evet")) {
                            dataSnapshot.child(id).child("tamamlandimi").getRef().setValue("evet");
                            dss.child("tamamlayanlar").child(fuser.getUid()).getRef().setValue(ServerValue.TIMESTAMP);
                            dataSnapshot.child("hazirlayanlar_onayi").getRef().setValue("evet").addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Toast.makeText(TaraActivity.this, "Görevi tamamladınız, lütfen ayarlar sayfasında bulunan teşekkürler kısmına göz atın.", Toast.LENGTH_LONG).show();
                                    dataSnapshot.child(id).getRef().removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(@NonNull Void unused) {
                                            GorevBitmis();
                                        }
                                    });
                                }
                            });
                        }
                    }
                    else{
                        if(dss.child("odul").getValue(String.class).substring(0,6).equals("Ödül: ")){
                            if(!dataSnapshot.child(id).child("tamamlandimi").getValue().equals("evet")) {
                                dataSnapshot.child(id).child("tamamlandimi").getRef().setValue("evet").addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        dss.child("tamamlayanlar").child(fuser.getUid()).getRef().setValue(ServerValue.TIMESTAMP);
                                        GelirBilgisiEkle(fuser.getUid(),"gorev_"+id+"_",Integer.valueOf(dss.child("odul").getValue(String.class).substring(6, (Integer.valueOf(dss.child("odul").getValue(String.class).length() - 2)))));
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
    private void DeaktifNormalGorev(DataSnapshot ds) {
        normalGorevSayisi = (int) ds.child("gorevler").getChildrenCount();
        if(normalGorevSayisi == 1){
            gorev1.setVisibility(View.VISIBLE);
            gorev2.setVisibility(View.INVISIBLE);
            gorev3.setVisibility(View.INVISIBLE);
            gorev4.setVisibility(View.INVISIBLE);
            gorev5.setVisibility(View.INVISIBLE);
            gorevYazi1.setText(DegeriStringeDonustur(ds.child("gorevler").child("1").getValue(String.class),1));
        }
        if(normalGorevSayisi == 2){
            gorev1.setVisibility(View.VISIBLE);
            gorev2.setVisibility(View.VISIBLE);
            gorev3.setVisibility(View.INVISIBLE);
            gorev4.setVisibility(View.INVISIBLE);
            gorev5.setVisibility(View.INVISIBLE);
            gorevYazi1.setText(DegeriStringeDonustur(ds.child("gorevler").child("1").getValue(String.class),1));
            gorevYazi2.setText(DegeriStringeDonustur(ds.child("gorevler").child("2").getValue(String.class),2));
        }
        if(normalGorevSayisi == 3){
            gorev1.setVisibility(View.VISIBLE);
            gorev2.setVisibility(View.VISIBLE);
            gorev3.setVisibility(View.VISIBLE);
            gorev4.setVisibility(View.INVISIBLE);
            gorev5.setVisibility(View.INVISIBLE);
            gorevYazi1.setText(DegeriStringeDonustur(ds.child("gorevler").child("1").getValue(String.class),1));
            gorevYazi2.setText(DegeriStringeDonustur(ds.child("gorevler").child("2").getValue(String.class),2));
            gorevYazi3.setText(DegeriStringeDonustur(ds.child("gorevler").child("3").getValue(String.class),3));

        }
        if(normalGorevSayisi == 4){
            gorev1.setVisibility(View.VISIBLE);
            gorev2.setVisibility(View.VISIBLE);
            gorev3.setVisibility(View.VISIBLE);
            gorev4.setVisibility(View.VISIBLE);
            gorev5.setVisibility(View.INVISIBLE);
            gorevYazi1.setText(DegeriStringeDonustur(ds.child("gorevler").child("1").getValue(String.class),1));
            gorevYazi2.setText(DegeriStringeDonustur(ds.child("gorevler").child("2").getValue(String.class),2));
            gorevYazi3.setText(DegeriStringeDonustur(ds.child("gorevler").child("3").getValue(String.class),3));
            gorevYazi4.setText(DegeriStringeDonustur(ds.child("gorevler").child("4").getValue(String.class),4));
        }
        if(normalGorevSayisi == 5){
            gorev1.setVisibility(View.VISIBLE);
            gorev2.setVisibility(View.VISIBLE);
            gorev3.setVisibility(View.VISIBLE);
            gorev4.setVisibility(View.VISIBLE);
            gorev5.setVisibility(View.VISIBLE);
            gorevYazi1.setText(DegeriStringeDonustur(ds.child("gorevler").child("1").getValue(String.class),1));
            gorevYazi2.setText(DegeriStringeDonustur(ds.child("gorevler").child("2").getValue(String.class),2));
            gorevYazi3.setText(DegeriStringeDonustur(ds.child("gorevler").child("3").getValue(String.class),3));
            gorevYazi4.setText(DegeriStringeDonustur(ds.child("gorevler").child("4").getValue(String.class),4));
            gorevYazi5.setText(DegeriStringeDonustur(ds.child("gorevler").child("5").getValue(String.class),5));
        }
        tik1.setVisibility(View.VISIBLE);
        tik2.setVisibility(View.VISIBLE);
        tik3.setVisibility(View.VISIBLE);
        tik4.setVisibility(View.VISIBLE);
        tik5.setVisibility(View.VISIBLE);
        tik1.setImageDrawable(getResources().getDrawable(R.drawable.carpi));
        tik2.setImageDrawable(getResources().getDrawable(R.drawable.carpi));
        tik3.setImageDrawable(getResources().getDrawable(R.drawable.carpi));
        tik4.setImageDrawable(getResources().getDrawable(R.drawable.carpi));
        tik5.setImageDrawable(getResources().getDrawable(R.drawable.carpi));
    }
    private String DegeriStringeDonustur(String deger, int hangisi) {
        String ikinciHane = deger.substring(1);
        if(deger.substring(0,1).equals("1")){
            bulunmaHedef = deger.substring(1);
            bulunmaHedefHangisi = hangisi;
            return ikinciHane + " Kişi bul";
        }
        else if(deger.substring(0,1).equals("2")){
            mesajlasHedef = deger.substring(1);
            mesajlasHedefHangisi = hangisi;
            return ikinciHane + " Kişi ile mesajlaş";
        }
        else if(deger.substring(0,1).equals("3")){
            referansHedef = deger.substring(1);
            referansHedefHangisi = hangisi;
            return ikinciHane + " Kişiye referans ol";
        }
        else if(deger.substring(0,1).equals("4")){
            begeniHedef = deger.substring(1);
            begeniHedefHangisi = hangisi;
            return ikinciHane + " Kişiyi beğen";
        }
        else if(deger.substring(0,1).equals("5")){
            favoriHedef = deger.substring(1);
            favoriHedefHangisi = hangisi;
            return ikinciHane + " Kişiyi favorilerine ekle";
        }
        else{
            return "Bir hata oluştu, lütfen tekrar deneyin";
        }
    }
    private void AktifVePasifOlacaklar() {
        if(kacGorevVar == 0){
            solBTN.setVisibility(View.INVISIBLE);
            sagBTN.setVisibility(View.INVISIBLE);
            page1.setVisibility(View.INVISIBLE);
            page2.setVisibility(View.INVISIBLE);
            page3.setVisibility(View.INVISIBLE);
            page4.setVisibility(View.INVISIBLE);
            page5.setVisibility(View.INVISIBLE);
        }
        else if(kacGorevVar == 1){
            solBTN.setVisibility(View.INVISIBLE);
            sagBTN.setVisibility(View.INVISIBLE);
            page1.setVisibility(View.INVISIBLE);
            page2.setVisibility(View.INVISIBLE);
            page3.setVisibility(View.INVISIBLE);
            page4.setVisibility(View.INVISIBLE);
            page5.setVisibility(View.INVISIBLE);
        }
        else if(kacGorevVar == 2){
            solBTN.setVisibility(View.VISIBLE);
            sagBTN.setVisibility(View.VISIBLE);
            page1.setVisibility(View.INVISIBLE);
            page2.setVisibility(View.VISIBLE);
            page3.setVisibility(View.VISIBLE);
            page4.setVisibility(View.INVISIBLE);
            page5.setVisibility(View.INVISIBLE);
            /*int horizontal = (int) TypedValue.applyDimension(
                    TypedValue.COMPLEX_UNIT_DIP, 25, getResources()
                            .getDisplayMetrics());
            int verticle = (int) TypedValue.applyDimension(
                    TypedValue.COMPLEX_UNIT_DIP, 6, getResources()
                            .getDisplayMetrics());

            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) pageRel.getLayoutParams();
            params.setMargins(horizontal,verticle,horizontal,verticle);
            pageRel.setLayoutParams(params);*/
        }
        else if(kacGorevVar == 3){
            solBTN.setVisibility(View.VISIBLE);
            sagBTN.setVisibility(View.VISIBLE);
            page1.setVisibility(View.INVISIBLE);
            page2.setVisibility(View.VISIBLE);
            page3.setVisibility(View.VISIBLE);
            page4.setVisibility(View.VISIBLE);
            page5.setVisibility(View.INVISIBLE);

        }
        else if(kacGorevVar == 4){
            solBTN.setVisibility(View.VISIBLE);
            sagBTN.setVisibility(View.VISIBLE);
            page1.setVisibility(View.VISIBLE);
            page2.setVisibility(View.VISIBLE);
            page3.setVisibility(View.VISIBLE);
            page4.setVisibility(View.VISIBLE);
            page5.setVisibility(View.INVISIBLE);

        }
        else if(kacGorevVar == 5){
            solBTN.setVisibility(View.VISIBLE);
            sagBTN.setVisibility(View.VISIBLE);
            page1.setVisibility(View.VISIBLE);
            page2.setVisibility(View.VISIBLE);
            page3.setVisibility(View.VISIBLE);
            page4.setVisibility(View.VISIBLE);
            page5.setVisibility(View.VISIBLE);

        }
    }

    public void updateToken(String token){
        DatabaseReference tref = FirebaseDatabase.getInstance().getReference("Tokenlar");
        Token mtoken = new Token(token);
        tref.child(fuser.getUid()).setValue(mtoken);

    }

    private void BulunabilmeSuresiBelirle() {

        if (!dialogBulun) {
            dialogBulun = true;

            Dialog dialog = new Dialog(TaraActivity.this);
            dialog.setContentView(R.layout.dialog_dizayn6);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

            TextView baslik = dialog.findViewById(R.id.baslik);
            TextView aciklama = dialog.findViewById(R.id.aciklama);
            Button buton1 = dialog.findViewById(R.id.buton1);
            Button buton2 = dialog.findViewById(R.id.buton2);
            Button buton3 = dialog.findViewById(R.id.buton3);
            Button buton4 = dialog.findViewById(R.id.buton4);
            Switch switch1 = dialog.findViewById(R.id.switch_1);

            ColorStateList buttonStates = new ColorStateList(
                    new int[][]{
                            new int[]{-android.R.attr.state_enabled},
                            new int[]{android.R.attr.state_checked},
                            new int[]{}
                    },
                    new int[]{
                            getResources().getColor(R.color.transKoyuGri),
                            ortaRenk,
                            getResources().getColor(R.color.gri4)
                    }
            );
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                switch1.getThumbDrawable().setTintList(buttonStates);
            }

            aciklama.setText("Diğer bluetooth cihazları tarafından bulunma sürenizi belirleyiniz. Eğer bu süre biterse diğer Kimoo kullanıcıları sizi bulamaz.");
            switch1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    evSistemi = isChecked;
                    if (isChecked)
                        aciklama.setText("Bulunabilme süreniz açık olduğu süre boyunca diğer kullanıcılar sizi bulabilir ancak siz ana sayfada yer alan ''Eve geldim'' seçeneğini işaretlemeden sizi göremez.");
                    else
                        aciklama.setText("Diğer bluetooth cihazları tarafından bulunma sürenizi belirleyiniz. Eğer bu süre biterse diğer Kimoo kullanıcıları sizi bulamaz.");
                }
            });
            buton1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    BulunabilmeSuresiniDegistir(5 * 60);
                    evdeMiyim.setChecked(false);
                    dialogBulun = false;
                    dialog.dismiss();
                }
            });
            buton2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    BulunabilmeSuresiniDegistir(15 * 60);
                    evdeMiyim.setChecked(false);
                    dialogBulun = false;
                    dialog.dismiss();
                }
            });
            buton3.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    BulunabilmeSuresiniDegistir(30 * 60);
                    evdeMiyim.setChecked(false);
                    dialogBulun = false;
                    dialog.dismiss();
                }
            });
            buton4.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    BulunabilmeSuresiniDegistir(60 * 60);
                    evdeMiyim.setChecked(false);
                    dialogBulun = false;
                    dialog.dismiss();
                }
            });

            dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                @Override
                public void onCancel(DialogInterface dialog) {
                    dialogBulun = false;
                }
            });
            dialog.show();
        }

    }

    private void BulunabilmeSuresiniDegistir(final int i) {
        gelendeger = i;
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("usersF").child(fuser.getUid());
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @SuppressLint("MissingPermission")
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User auser = dataSnapshot.getValue(User.class);
                FirebaseDatabase.getInstance().getReference("usersF").child(fuser.getUid()).child("suan_bulanlar").removeValue();
                myAdapter.setName("kim_" + dataSnapshot.child("usernamef").getValue(String.class) + dataSnapshot.child("id").getValue(String.class));
                Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
                intent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, i);
                startActivityForResult(intent,7);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }


    public void IzinKontrol(){

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) == PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_SCAN) == PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED) {
            if (myAdapter.isEnabled()) {
                if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                        == PackageManager.PERMISSION_GRANTED)
                {
                    BulunabilmeSuresiBelirle();
                } else {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION},1);
                    }
                }

            } else {
                Intent eintent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(eintent,3);
            }
        }
        else{

            requestPermissions(new String[]{Manifest.permission.BLUETOOTH_CONNECT, Manifest.permission.BLUETOOTH_SCAN, Manifest.permission.POST_NOTIFICATIONS},2);
        }

    }
    private void startServicee(int kalanSure){
        if(ArkadaBul2.yazi <= 0) {
            ArkadaBul2.yazi = kalanSure;
            ArkadaBul2.deaktif = false;
            ArkadaBul2.bulunma = true;
            startService(serviceIntent);
        }else{
            if (ArkadaBul2.yazi + kalanSure > 3600)
                ArkadaBul2.yazi += 3600;
            else
                ArkadaBul2.yazi += kalanSure;

        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if(requestCode == 7)
            if(resultCode != 0){
                startServicee(gelendeger);
                DatabaseReference ref = FirebaseDatabase.getInstance().getReference("usersF").child(fuser.getUid());
                if(evSistemi) {
                    ref.child("ev_sistemi").child("sistem").getRef().setValue("aktif");
                    ref.child("ev_sistemi").child("evde_mi").getRef().setValue("hayir");
                }
                else{
                    ref.child("ev_sistemi").child("sistem").getRef().setValue("pasif");
                    ref.child("ev_sistemi").child("evde_mi").getRef().setValue("evet");
                }
            }

        if(requestCode == 4)
            if(resultCode != 0)
                IzinKontrol2();

        if(requestCode == 3)
            if (resultCode != 0)
                IzinKontrol();
        super.onActivityResult(requestCode, resultCode, data);
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1)
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                IzinKontrol();

            } else {
                Toast.makeText(TaraActivity.this, "Eğer izin vermezseniz diğer Kimoo kullanıcıları sizi bulamaz.", Toast.LENGTH_LONG).show();
            }
        if (requestCode == 2)
            IzinKontrol();
        if (requestCode == 3) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                IzinKontrol2();

            } else {
                Toast.makeText(TaraActivity.this, "Eğer izin vermezseniz diğer Kimoo kullanıcılarını bulamazsınız.", Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        //finish();
        finishAffinity();
        //System.exit(0);
    }


    @Override
    protected void onResume() {
        super.onResume();
        tarada = true;

        SharedPreferences tas_shared = getSharedPreferences("TasarimBilgileri",MODE_PRIVATE);
        SharedPreferences.Editor editor = tas_shared.edit();
        if (!tas_shared.getString("tasarim_arayuz","1").equals("sifirlandi"))
            tasDegeri = tas_shared.getString("tasarim_arayuz","1");

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("usersF").child(fuser.getUid());
        ref.child("suan").setValue(ServerValue.TIMESTAMP);
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                dataSnapshotAsil = dataSnapshot;
                if (tas_shared.getString("tasarim_arayuz","1").equals("sifirlandi") || !tas_shared.getString("tasarim_arayuz","1").equals(dataSnapshotAsil.child("tas_arayuz").getValue(String.class))){
                    tasDegeri = dataSnapshotAsil.child("tas_arayuz").getValue(String.class);
                    editor.putString("tasarim_arayuz",tasDegeri);
                    editor.commit();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        //GorevleriGetir();
        VerileriCagir();
        //startActivity(new Intent(TaraActivity.this,Deneme.class));

        new CountDownTimer(2000, 1000) {
            public void onTick(long millisUntilFinished) {

                // 2 Saniye sayacak ve her saniye kontrol edecek
            }

            public void onFinish() {
                dikkatCekmeAnim(destekBTN,200,0.93f);
            }

        }.start();

    }

    @Override
    protected void onPause() {
        super.onPause();
        kacGorevVar = 0;
        degistirDeger = 1;
        favoriyeBastim = false;
        suanaBastim = false;
        bulduklarimaBastim = false;
        myTimer.cancel();
    }

    @Override
    public void onRefresh() {
        swipeRefreshLayout.setRefreshing(true);
        GorevleriGetir();
        VerileriCagir();
    }
}
