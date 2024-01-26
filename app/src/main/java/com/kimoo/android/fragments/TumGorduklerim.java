package com.kimoo.android.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

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
import com.kimoo.android.Model.User;
import com.kimoo.android.R;
import com.kimoo.android.TaraActivity;
import com.kimoo.android.extra.ArkadaBul;
import com.kimoo.android.extra.FavoriAdapter;
import com.kimoo.android.extra.FavoriAdapter2;
import com.kimoo.android.extra.TasarimRenginiGetir;

import java.util.ArrayList;
import java.util.List;

public class TumGorduklerim extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener{
    private static List<User> mUsers = new ArrayList<>(),aList = new ArrayList<>(),mUsers2 = new ArrayList<>(),mArkaplandaBulunanlar = new ArrayList<>();
    private static List<User> CinList,YasList,PremList,araSonuc;
    private List<String> aramaSirasindaKaldirilanlar = new ArrayList<>(),aramaSirasindaEkliOlanlar = new ArrayList<>(),  kullanicilar,arkaplandaBulunanlar = new ArrayList<>();
    static RecyclerView recyclerView,recyclerViewArka;
    static ProgressBar pbar;
    static RelativeLayout ustKisim;
    ImageView kart,liste;
    static Context mContext;
    static Activity mActivity;
    private User Kullanici;
    private static FavoriAdapter favoriAdapter;
    private static FavoriAdapter2 favoriAdapter2;
    private TextView ara, baslik, bulanSayisi;
    private SwipeRefreshLayout swipeRefreshLayout;
    private Button arkaplanBTN;
    public static int spin2deger;
    private static long gorduklerimSayisi;
    static String aranacakDeger;
    static LinearLayout background;
    public static boolean arkaMi = false;
    private static int gorunumDeger,engelSayim;
    static FirebaseUser fuser;
    SharedPreferences sharedPreferences,sharedPreferences2;
    SharedPreferences.Editor editor2;
    int kaldigimYer;
    private int ortaRenk;

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
        setContentView(R.layout.fragment_tum_gorduklerim);
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
        sharedPreferences2 = getSharedPreferences("bulunan",Context.MODE_PRIVATE);
        editor2 = sharedPreferences2.edit();

        fuser = FirebaseAuth.getInstance().getCurrentUser();
        pbar = findViewById(R.id.progress);
        pbar.getIndeterminateDrawable().setColorFilter(ortaRenk,android.graphics.PorterDuff.Mode.MULTIPLY);
        recyclerView = findViewById(R.id.recylerView);
        baslik = findViewById(R.id.baslik);
        bulanSayisi = findViewById(R.id.bulan_sayisi);
        kart = findViewById(R.id.kart_gorunumu);
        liste = findViewById(R.id.liste_gorunumu);
        recyclerViewArka = findViewById(R.id.recylerViewArka);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        recyclerViewArka.setHasFixedSize(true);
        recyclerViewArka.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        background = findViewById(R.id.background);
        arkaplanBTN = findViewById(R.id.arkaplanda_bulunanlar_btn);
        mContext = getApplicationContext();
        mActivity = TumGorduklerim.this;
        ara = findViewById(R.id.mesaj_ara_gorduklerim);
        ustKisim = findViewById(R.id.ust_kisim);
        swipeRefreshLayout = findViewById(R.id.swipe_layout);

        SharedPreferences tas_shared = getSharedPreferences("TasarimBilgileri",MODE_PRIVATE);
        TasarimDegistir(tas_shared.getString("tasarim_arayuz","1"));

        swipeRefreshLayout.setOnRefreshListener(this);
        swipeRefreshLayout.setColorSchemeResources(android.R.color.black);
        ArkaplanDedektor();
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(getResources().getDrawable(R.drawable.ic_mesaj_geri2));
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(TumGorduklerim.this,TaraActivity.class);
                startActivity(intent);
                finish();
                overridePendingTransition(R.anim.slide_in_right,R.anim.slide_out_left);
            }
        });

        if (getIntent().getStringExtra("bulmayikapat") != null)
        if (getIntent().getStringExtra("bulmayikapat").equals("evet"))
            ArkadaBul.bulma = false;

        FirebaseDatabase.getInstance().getReference("usersF").child(fuser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Kullanici = dataSnapshot.getValue(User.class);
                if (dataSnapshot.child("bulanlar").exists())
                    bulanSayisi.setText("Sizi bulan toplam kişi sayısı: "+dataSnapshot.child("bulanlar").getChildrenCount());
                else
                    bulanSayisi.setText("Sizi bulan toplam kişi sayısı: 0");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
            /*filtrele.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AltMenu altMenu = new AltMenu(TumGorduklerim.this);
                    View sheetView = getLayoutInflater().inflate(R.layout.filtre_menu, null);
                    altMenu.setContentView(sheetView);
                    altMenu.show();
                }
            });
            sirala.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AltMenu2 altMenu = new AltMenu2(TumGorduklerim.this);
                    View sheetView = getLayoutInflater().inflate(R.layout.sirala_menu, null);
                    altMenu.setContentView(sheetView);
                    altMenu.show();
                }
            });*/
            ara.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    mesajAra(s.toString().toLowerCase().replaceAll(System.getProperty("line.separator"), ""));

                }

                @Override
                public void afterTextChanged(Editable s) {
                }
            });
        liste.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (gorunumDeger != 0) {
                    liste.setColorFilter(ortaRenk);
                    kart.setColorFilter(getResources().getColor(R.color.gri2));
                    editor2.putInt("gorunum_bulunanlar", 0);
                    editor2.commit();

                    gorunumDeger = 0;


                    if (!arkaMi) {
                        favoriAdapter = new FavoriAdapter(TumGorduklerim.this, mActivity, mUsers);
                        recyclerView.setLayoutManager(new LinearLayoutManager(getAppContext()));
                        recyclerView.setAdapter(favoriAdapter);
                    } else {
                        favoriAdapter = new FavoriAdapter(TumGorduklerim.this, mActivity, mArkaplandaBulunanlar);
                        recyclerViewArka.setLayoutManager(new LinearLayoutManager(TumGorduklerim.this));
                        recyclerViewArka.setAdapter(favoriAdapter);
                    }
                }
            }
        });
        kart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (gorunumDeger != 1) {
                    editor2.putInt("gorunum_bulunanlar", 1);
                    editor2.commit();
                    liste.setColorFilter(getResources().getColor(R.color.gri2));
                    kart.setColorFilter(ortaRenk);

                    gorunumDeger = 1;

                    if (!arkaMi) {
                        favoriAdapter2 = new FavoriAdapter2(TumGorduklerim.this, mActivity, mUsers);
                        recyclerView.setLayoutManager(new GridLayoutManager(getAppContext(), 3));
                        recyclerView.setAdapter(favoriAdapter2);
                    } else {
                        favoriAdapter2 = new FavoriAdapter2(TumGorduklerim.this, mActivity, mArkaplandaBulunanlar);
                        recyclerViewArka.setLayoutManager(new GridLayoutManager(getAppContext(), 3));
                        recyclerViewArka.setAdapter(favoriAdapter2);
                    }
                }
            }
        });
        kaldigimYer = sharedPreferences2.getInt("KalinanYer",0);
        arkaplanBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                arkaMi = !arkaMi;
                editor2.putInt("KalinanYer", 0).commit();
                /*if (kaldigimYer == mArkaplandaBulunanlar.size() || mArkaplandaBulunanlar.size() == 0) {
                }*/
                ArkaplanDedektor();
            }
        });

        Intent intentt2 = new Intent(getAppContext(), ArkadaBul.class);
        //ArkadaBul.deaktif = true;
        //getAppContext().stopService(intentt2);
        arkaplandaBulunanlar = new ArrayList<>();
        if(kaldigimYer != 0){
            FirebaseDatabase.getInstance().getReference("usersF").child(fuser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot benimSnap) {
                    for(int i = 1; i < kaldigimYer+1; i++){
                        if(i != kaldigimYer+1) {
                            String kullanici = sharedPreferences2.getString("bulunan_" + i, null);
                            if(kullanici != null)
                                if(kullanici.length() > 12) {
                                    String k_uid = kullanici.substring(4, kullanici.length() - 5);
                                    String k_id = kullanici.substring(kullanici.length() - 5);

                                    Query userQuery = FirebaseDatabase.getInstance().getReference("usersF").orderByChild("usernamef").equalTo(k_uid);
                                    userQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot userSS) {
                                            for (DataSnapshot dataSnapshot : userSS.getChildren()) {
                                                if(!benimSnap.child("bulduklarim").child(dataSnapshot.getKey()).exists()) {
                                                    User user = dataSnapshot.getValue(User.class);
                                                    int yasSiniri = 0;
                                                    if(Integer.parseInt(Kullanici.getDg()) < 18)
                                                        yasSiniri = 2;
                                                    else
                                                        yasSiniri = 5;
                                                    if ((Integer.parseInt(Kullanici.getDg()) - Integer.parseInt(user.getDg())) > -yasSiniri && (Integer.parseInt(Kullanici.getDg()) - Integer.parseInt(user.getDg())) < yasSiniri) {
                                                        arkaplanBTN.setVisibility(View.VISIBLE);
                                                        arkaplanBTN.setEnabled(true);
                                                        arkaplanBTN.setClickable(true);
                                                        if (user.getId().equals(k_id)) {
                                                            if (!arkaplandaBulunanlar.contains(user.getUid())) {
                                                                arkaplandaBulunanlar.add(user.getUid());
                                                                mArkaplandaBulunanlar.add(user);
                                                                BenOnuEngellemismiyim(mArkaplandaBulunanlar, user);
                                                            }
                                                        } else {
                                                            IdKontrol(user, kullanici);
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

                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }else{
            arkaMi = false;
            ArkaplanDedektor();
        }
        baslik.setText("Tüm Bulduklarım");
        GorunumKontrol();
    }

    private void BenOnuEngellemismiyim(final List<User> ustListe, final User user) {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("usersF").child(fuser.getUid());
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.hasChild("engellediklerim")){
                    if(dataSnapshot.child("engellediklerim").hasChild(user.getUid())){
                        ustListe.remove(user);
                        ListeHazir(user);
                    }
                    else
                        OBeniEngellemismi(ustListe,user);

                }
                else
                    OBeniEngellemismi(ustListe,user);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void OBeniEngellemismi(final List<User> ustListe, final User user) {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("usersF").child(user.getUid());
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.hasChild("engellediklerim")){
                    if(dataSnapshot.child("engellediklerim").hasChild(fuser.getUid())){
                        ustListe.remove(user);
                        ListeHazir(user);
                    }
                    else
                        ListeHazir(user);
                }
                else
                    ListeHazir(user);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void ListeHazir(final User user) {
        DatabaseReference bulduklarimRef = FirebaseDatabase.getInstance().getReference("usersF").child(fuser.getUid());
        bulduklarimRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChild("bulduklarim")) {
                    if (dataSnapshot.child("bulduklarim").hasChild(user.getUid())) {
                        String deger = dataSnapshot.child("bulduklarim").child(user.getUid()).child("kac_kez_gordum").getValue(String.class);
                        int sayi = Integer.parseInt(deger);
                        sayi += 1;
                        dataSnapshot.child("bulduklarim").child(user.getUid()).child("kac_kez_gordum").getRef().setValue(String.valueOf(sayi));
                        dataSnapshot.child("bulduklarim").child(user.getUid()).child("son_gordugum_zaman").getRef().setValue(ServerValue.TIMESTAMP);

                    } else {
                        dataSnapshot.child("bulduklarim").child(user.getUid()).child("kac_kez_gordum").getRef().setValue("1");
                        dataSnapshot.child("bulduklarim").child(user.getUid()).child("ilk_gordugum_zaman").getRef().setValue(ServerValue.TIMESTAMP);
                        dataSnapshot.child("bulduklarim").child(user.getUid()).child("son_gordugum_zaman").getRef().setValue(ServerValue.TIMESTAMP);
                    }
                } else {
                    dataSnapshot.child("bulduklarim").child(user.getUid()).child("kac_kez_gordum").getRef().setValue("1");
                    dataSnapshot.child("bulduklarim").child(user.getUid()).child("ilk_gordugum_zaman").getRef().setValue(ServerValue.TIMESTAMP);
                    dataSnapshot.child("bulduklarim").child(user.getUid()).child("son_gordugum_zaman").getRef().setValue(ServerValue.TIMESTAMP);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void IdKontrol(final User user, final String kullanici){
        DatabaseReference idRef = FirebaseDatabase.getInstance().getReference("usersF").child(user.getUid());
        idRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.child("idler").child("id-1").getValue(String.class).equals(kullanici.substring(kullanici.length() - 5))){
                    if (!arkaplandaBulunanlar.contains(user.getUid())) {
                        arkaplandaBulunanlar.add(user.getUid());
                        mArkaplandaBulunanlar.add(user);
                        favoriAdapter = new FavoriAdapter(getAppContext(),mActivity, mArkaplandaBulunanlar);
                        recyclerViewArka.setAdapter(favoriAdapter);
                        DatabaseReference bulduklarimRef = FirebaseDatabase.getInstance().getReference("usersF").child(fuser.getUid());
                        bulduklarimRef.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                if (dataSnapshot.hasChild("bulduklarim")) {
                                    if (dataSnapshot.child("bulduklarim").hasChild(user.getUid())) {
                                        String deger = dataSnapshot.child("bulduklarim").child(user.getUid()).child("kac_kez_gordum").getValue(String.class);
                                        int sayi = Integer.parseInt(deger);
                                        sayi += 1;
                                        dataSnapshot.child("bulduklarim").child(user.getUid()).child("kac_kez_gordum").getRef().setValue(String.valueOf(sayi));
                                        dataSnapshot.child("bulduklarim").child(user.getUid()).child("ilk_gordugum_zaman").getRef().setValue(ServerValue.TIMESTAMP);
                                        dataSnapshot.child("bulduklarim").child(user.getUid()).child("son_gordugum_zaman").getRef().setValue(ServerValue.TIMESTAMP);

                                    } else {
                                        dataSnapshot.child("bulduklarim").child(user.getUid()).child("kac_kez_gordum").getRef().setValue("1");
                                        dataSnapshot.child("bulduklarim").child(user.getUid()).child("ilk_gordugum_zaman").getRef().setValue(ServerValue.TIMESTAMP);
                                        dataSnapshot.child("bulduklarim").child(user.getUid()).child("son_gordugum_zaman").getRef().setValue(ServerValue.TIMESTAMP);
                                    }
                                } else {
                                    dataSnapshot.child("bulduklarim").child(user.getUid()).child("kac_kez_gordum").getRef().setValue("1");
                                    dataSnapshot.child("bulduklarim").child(user.getUid()).child("ilk_gordugum_zaman").getRef().setValue(ServerValue.TIMESTAMP);
                                    dataSnapshot.child("bulduklarim").child(user.getUid()).child("son_gordugum_zaman").getRef().setValue(ServerValue.TIMESTAMP);
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
                    }
                }
                if(dataSnapshot.child("idler").hasChild("id-2")){
                    if(dataSnapshot.child("idler").child("id-2").getValue(String.class).equals(kullanici.substring(kullanici.length() - 5))){
                        if (!arkaplandaBulunanlar.contains(user.getUid())) {
                            arkaplandaBulunanlar.add(user.getUid());
                            mArkaplandaBulunanlar.add(user);
                            favoriAdapter = new FavoriAdapter(getAppContext(),mActivity, mArkaplandaBulunanlar);
                            recyclerViewArka.setAdapter(favoriAdapter);
                            FirebaseUser fuser = FirebaseAuth.getInstance().getCurrentUser();
                            DatabaseReference bulduklarimRef = FirebaseDatabase.getInstance().getReference("usersF").child(fuser.getUid());
                            bulduklarimRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    if (dataSnapshot.hasChild("bulduklarim")) {
                                        if (dataSnapshot.child("bulduklarim").hasChild(user.getUid())) {
                                            String deger = dataSnapshot.child("bulduklarim").child(user.getUid()).child("kac_kez_gordum").getValue(String.class);
                                            int sayi = Integer.parseInt(deger);
                                            sayi += 1;
                                            dataSnapshot.child("bulduklarim").child(user.getUid()).child("kac_kez_gordum").getRef().setValue(String.valueOf(sayi));
                                            dataSnapshot.child("bulduklarim").child(user.getUid()).child("ilk_gordugum_zaman").getRef().setValue(ServerValue.TIMESTAMP);
                                            dataSnapshot.child("bulduklarim").child(user.getUid()).child("son_gordugum_zaman").getRef().setValue(ServerValue.TIMESTAMP);

                                        } else {
                                            dataSnapshot.child("bulduklarim").child(user.getUid()).child("kac_kez_gordum").getRef().setValue("1");
                                            dataSnapshot.child("bulduklarim").child(user.getUid()).child("ilk_gordugum_zaman").getRef().setValue(ServerValue.TIMESTAMP);
                                            dataSnapshot.child("bulduklarim").child(user.getUid()).child("son_gordugum_zaman").getRef().setValue(ServerValue.TIMESTAMP);
                                        }
                                    } else {
                                        dataSnapshot.child("bulduklarim").child(user.getUid()).child("kac_kez_gordum").getRef().setValue("1");
                                        dataSnapshot.child("bulduklarim").child(user.getUid()).child("ilk_gordugum_zaman").getRef().setValue(ServerValue.TIMESTAMP);
                                        dataSnapshot.child("bulduklarim").child(user.getUid()).child("son_gordugum_zaman").getRef().setValue(ServerValue.TIMESTAMP);
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });
                        }
                    }
                }
                if(dataSnapshot.child("idler").hasChild("id-3")){
                    if(dataSnapshot.child("idler").child("id-3").getValue(String.class).equals(kullanici.substring(kullanici.length() - 5))){
                        if (!arkaplandaBulunanlar.contains(user.getUid())) {
                            arkaplandaBulunanlar.add(user.getUid());
                            mArkaplandaBulunanlar.add(user);
                            favoriAdapter = new FavoriAdapter(getAppContext(),mActivity, mArkaplandaBulunanlar);
                            recyclerViewArka.setAdapter(favoriAdapter);
                            FirebaseUser fuser = FirebaseAuth.getInstance().getCurrentUser();
                            DatabaseReference bulduklarimRef = FirebaseDatabase.getInstance().getReference("usersF").child(fuser.getUid());
                            bulduklarimRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    if (dataSnapshot.hasChild("bulduklarim")) {
                                        if (dataSnapshot.child("bulduklarim").hasChild(user.getUid())) {
                                            String deger = dataSnapshot.child("bulduklarim").child(user.getUid()).child("kac_kez_gordum").getValue(String.class);
                                            int sayi = Integer.parseInt(deger);
                                            sayi += 1;
                                            dataSnapshot.child("bulduklarim").child(user.getUid()).child("kac_kez_gordum").getRef().setValue(String.valueOf(sayi));
                                            dataSnapshot.child("bulduklarim").child(user.getUid()).child("ilk_gordugum_zaman").getRef().setValue(ServerValue.TIMESTAMP);
                                            dataSnapshot.child("bulduklarim").child(user.getUid()).child("son_gordugum_zaman").getRef().setValue(ServerValue.TIMESTAMP);

                                        } else {
                                            dataSnapshot.child("bulduklarim").child(user.getUid()).child("kac_kez_gordum").getRef().setValue("1");
                                            dataSnapshot.child("bulduklarim").child(user.getUid()).child("ilk_gordugum_zaman").getRef().setValue(ServerValue.TIMESTAMP);
                                            dataSnapshot.child("bulduklarim").child(user.getUid()).child("son_gordugum_zaman").getRef().setValue(ServerValue.TIMESTAMP);
                                        }
                                    } else {
                                        dataSnapshot.child("bulduklarim").child(user.getUid()).child("kac_kez_gordum").getRef().setValue("1");
                                        dataSnapshot.child("bulduklarim").child(user.getUid()).child("ilk_gordugum_zaman").getRef().setValue(ServerValue.TIMESTAMP);
                                        dataSnapshot.child("bulduklarim").child(user.getUid()).child("son_gordugum_zaman").getRef().setValue(ServerValue.TIMESTAMP);
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });
                        }
                    }
                }
                if(dataSnapshot.child("idler").hasChild("id-4")){
                    if(dataSnapshot.child("idler").child("id-4").getValue(String.class).equals(kullanici.substring(kullanici.length() - 5))){
                        if (!arkaplandaBulunanlar.contains(user.getUid())) {
                            arkaplandaBulunanlar.add(user.getUid());
                            mArkaplandaBulunanlar.add(user);
                            favoriAdapter = new FavoriAdapter(getAppContext(),mActivity, mArkaplandaBulunanlar);
                            recyclerViewArka.setAdapter(favoriAdapter);
                            FirebaseUser fuser = FirebaseAuth.getInstance().getCurrentUser();
                            DatabaseReference bulduklarimRef = FirebaseDatabase.getInstance().getReference("usersF").child(fuser.getUid());
                            bulduklarimRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    if (dataSnapshot.hasChild("bulduklarim")) {
                                        if (dataSnapshot.child("bulduklarim").hasChild(user.getUid())) {
                                            String deger = dataSnapshot.child("bulduklarim").child(user.getUid()).child("kac_kez_gordum").getValue(String.class);
                                            int sayi = Integer.parseInt(deger);
                                            sayi += 1;
                                            dataSnapshot.child("bulduklarim").child(user.getUid()).child("kac_kez_gordum").getRef().setValue(String.valueOf(sayi));
                                            dataSnapshot.child("bulduklarim").child(user.getUid()).child("ilk_gordugum_zaman").getRef().setValue(ServerValue.TIMESTAMP);
                                            dataSnapshot.child("bulduklarim").child(user.getUid()).child("son_gordugum_zaman").getRef().setValue(ServerValue.TIMESTAMP);

                                        } else {
                                            dataSnapshot.child("bulduklarim").child(user.getUid()).child("kac_kez_gordum").getRef().setValue("1");
                                            dataSnapshot.child("bulduklarim").child(user.getUid()).child("ilk_gordugum_zaman").getRef().setValue(ServerValue.TIMESTAMP);
                                            dataSnapshot.child("bulduklarim").child(user.getUid()).child("son_gordugum_zaman").getRef().setValue(ServerValue.TIMESTAMP);
                                        }
                                    } else {
                                        dataSnapshot.child("bulduklarim").child(user.getUid()).child("kac_kez_gordum").getRef().setValue("1");
                                        dataSnapshot.child("bulduklarim").child(user.getUid()).child("ilk_gordugum_zaman").getRef().setValue(ServerValue.TIMESTAMP);
                                        dataSnapshot.child("bulduklarim").child(user.getUid()).child("son_gordugum_zaman").getRef().setValue(ServerValue.TIMESTAMP);
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });
                        }
                    }
                }
                if(dataSnapshot.child("idler").hasChild("id-5")){
                    if(dataSnapshot.child("idler").child("id-5").getValue(String.class).equals(kullanici.substring(kullanici.length() - 5))){
                        if (!arkaplandaBulunanlar.contains(user.getUid())) {
                            arkaplandaBulunanlar.add(user.getUid());
                            mArkaplandaBulunanlar.add(user);
                            favoriAdapter = new FavoriAdapter(getAppContext(),mActivity, mArkaplandaBulunanlar);
                            recyclerViewArka.setAdapter(favoriAdapter);
                            FirebaseUser fuser = FirebaseAuth.getInstance().getCurrentUser();
                            DatabaseReference bulduklarimRef = FirebaseDatabase.getInstance().getReference("usersF").child(fuser.getUid());
                            bulduklarimRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    if (dataSnapshot.hasChild("bulduklarim")) {
                                        if (dataSnapshot.child("bulduklarim").hasChild(user.getUid())) {
                                            String deger = dataSnapshot.child("bulduklarim").child(user.getUid()).child("kac_kez_gordum").getValue(String.class);
                                            int sayi = Integer.parseInt(deger);
                                            sayi += 1;
                                            dataSnapshot.child("bulduklarim").child(user.getUid()).child("kac_kez_gordum").getRef().setValue(String.valueOf(sayi));
                                            dataSnapshot.child("bulduklarim").child(user.getUid()).child("ilk_gordugum_zaman").getRef().setValue(ServerValue.TIMESTAMP);
                                            dataSnapshot.child("bulduklarim").child(user.getUid()).child("son_gordugum_zaman").getRef().setValue(ServerValue.TIMESTAMP);

                                        } else {
                                            dataSnapshot.child("bulduklarim").child(user.getUid()).child("kac_kez_gordum").getRef().setValue("1");
                                            dataSnapshot.child("bulduklarim").child(user.getUid()).child("ilk_gordugum_zaman").getRef().setValue(ServerValue.TIMESTAMP);
                                            dataSnapshot.child("bulduklarim").child(user.getUid()).child("son_gordugum_zaman").getRef().setValue(ServerValue.TIMESTAMP);
                                        }
                                    } else {
                                        dataSnapshot.child("bulduklarim").child(user.getUid()).child("kac_kez_gordum").getRef().setValue("1");
                                        dataSnapshot.child("bulduklarim").child(user.getUid()).child("ilk_gordugum_zaman").getRef().setValue(ServerValue.TIMESTAMP);
                                        dataSnapshot.child("bulduklarim").child(user.getUid()).child("son_gordugum_zaman").getRef().setValue(ServerValue.TIMESTAMP);
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

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent tarayaDon = new Intent(TumGorduklerim.this,TaraActivity.class);
        startActivity(tarayaDon);
        finish();
        overridePendingTransition(R.anim.slide_in_right,R.anim.slide_out_left);
    }
    private void GorunumKontrol() {
        kullanicilar = new ArrayList<>();
        mUsers = new ArrayList<>();
        mUsers2 = new ArrayList<>();

        recyclerView.setVisibility(View.VISIBLE);
        ustKisim.setVisibility(View.VISIBLE);
        pbar.setVisibility(View.GONE);

        favoriAdapter = new FavoriAdapter(getAppContext(),TumGorduklerim.this, mUsers);
        favoriAdapter2 = new FavoriAdapter2(getAppContext(),TumGorduklerim.this, mUsers);

        if(sharedPreferences2.getInt("gorunum_bulunanlar",0) == 0){
            liste.setColorFilter(ortaRenk);
            kart.setColorFilter(getResources().getColor(R.color.gri2));
            gorunumDeger = 0;

            recyclerView.setLayoutManager(new LinearLayoutManager(getAppContext()));
            recyclerView.setAdapter(favoriAdapter);

        }else{
            liste.setColorFilter(getResources().getColor(R.color.gri2));
            kart.setColorFilter(ortaRenk);
            gorunumDeger = 1;

            recyclerView.setLayoutManager(new GridLayoutManager(getAppContext(),3));
            recyclerView.setAdapter(favoriAdapter2);
        }

        BulunanlariGetir();
    }
    private void ArkaplanDedektor(){
        ara.setText("");
        if(arkaMi){
            arkaplanBTN.setText("TÜM BULUNANLAR");
            arkaplanBTN.setClickable(true);
            recyclerViewArka.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.INVISIBLE);
            baslik.setText("Arkaplanda Bulduklarım");
            //sirala.hide();
            //filtrele.hide();
            if (gorunumDeger == 0) {
                favoriAdapter = new FavoriAdapter(TumGorduklerim.this, mActivity, mArkaplandaBulunanlar);
                recyclerViewArka.setLayoutManager(new LinearLayoutManager(TumGorduklerim.this));
                recyclerViewArka.setAdapter(favoriAdapter);
            }
            else {
                favoriAdapter2 = new FavoriAdapter2(TumGorduklerim.this, mActivity, mArkaplandaBulunanlar);
                recyclerViewArka.setLayoutManager(new GridLayoutManager(getAppContext(),3));
                recyclerViewArka.setAdapter(favoriAdapter2);
            }
        }else{
            liste.setClickable(true);
            kart.setClickable(true);
            //arkaplanBTN.setClickable(false);
            arkaplanBTN.setText("ARKAPLANDA BULUNANLAR");
            recyclerViewArka.setVisibility(View.INVISIBLE);
            recyclerView.setVisibility(View.VISIBLE);
            baslik.setText("Tüm Bulduklarım");
            //sirala.show();
            //filtrele.show();
            if (gorunumDeger == 0) {
                favoriAdapter = new FavoriAdapter(TumGorduklerim.this, mActivity, mUsers);
                recyclerView.setLayoutManager(new LinearLayoutManager(TumGorduklerim.this));
                recyclerView.setAdapter(favoriAdapter);
            }
            else {
                favoriAdapter2 = new FavoriAdapter2(TumGorduklerim.this, mActivity, mUsers);
                recyclerView.setLayoutManager(new GridLayoutManager(getAppContext(),3));
                recyclerView.setAdapter(favoriAdapter2);
            }
        }

    }
    public static Context getAppContext(){
        return mContext;
    }
    public static Activity getAppActivty(){
        return mActivity;
    }

    void AdKontrol(final String s, User item){
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("usersF").child(item.getUid());
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (item.getGizlilik_ad().equals("0")) {
                    if (dataSnapshot.child("begendiklerim").child(fuser.getUid()).exists()) {
                            if(item.getAd().trim().length() >= s.trim().length()){
                                if(!s.trim().toLowerCase().equals(item.getAd().trim().substring(0,s.trim().length()))) {
                                    Kaldir(item);
                                }
                                else {
                                    Ekle(item);
                                }
                            }
                            else {
                                Kaldir(item);
                            }
                    }
                    else {
                        if(item.getAd().trim().substring(0,1).length() == s.trim().length()){
                            if(!s.trim().toLowerCase().equals(item.getAd().trim().substring(0,1))) {
                                Kaldir(item);
                            }
                            else {
                                Ekle(item);
                            }
                        }
                        else {
                            Kaldir(item);
                        }
                    }
                }
                else if (item.getGizlilik_ad().equals("1")) {
                    if (dataSnapshot.child("begenenler").child(fuser.getUid()).exists()) {
                            if(item.getAd().trim().length() >= s.trim().length()){
                                if(!s.trim().toLowerCase().equals(item.getAd().trim().substring(0,s.trim().length()))) {
                                    Kaldir(item);
                                }
                                else {
                                    Ekle(item);
                                }
                            }
                            else {
                                Kaldir(item);
                            }
                    }
                    else {
                        if(item.getAd().trim().substring(0,1).length() >= s.trim().length()){
                            if(!s.trim().toLowerCase().equals(item.getAd().trim().substring(0,s.trim().length()))) {
                                Kaldir(item);
                            }
                            else {
                                Ekle(item);
                            }
                        }
                        else {
                            Kaldir(item);
                        }
                    }
                }
                else if (item.getGizlilik_ad().equals("2")) {
                    if (dataSnapshot.child("mesajlastiklarim").child(fuser.getUid()).exists()) {
                        if (item.getAd().trim().length() >= s.trim().length()) {
                            if (!s.trim().toLowerCase().equals(item.getAd().trim().substring(0, s.trim().length()))) {
                                Kaldir(item);
                            } else {
                                Ekle(item);
                            }
                        } else {
                            Kaldir(item);
                        }
                    }
                    else{
                        if(item.getAd().trim().substring(0,1).length() >= s.trim().length()){
                            if(!s.trim().toLowerCase().equals(item.getAd().trim().substring(0,s.trim().length()))) {
                                Kaldir(item);
                            }
                            else {
                                Ekle(item);
                            }
                        }
                        else {
                            Kaldir(item);
                        }
                    }
                }
                else if (item.getGizlilik_ad().equals("3")) {
                    if(item.getAd().trim().substring(0,1).length() >= s.trim().length()){
                        if(!s.trim().toLowerCase().equals(item.getAd().trim().substring(0,s.trim().length()))) {
                            Kaldir(item);
                        }
                        else {
                            Ekle(item);
                        }
                    }
                    else {
                        Kaldir(item);
                    }
                }
                else{
                    if (item.getAd().trim().length() >= s.trim().length()) {
                        if (!s.trim().toLowerCase().equals(item.getAd().trim().substring(0, s.trim().length()))) {
                            Kaldir(item);
                        } else {
                            Ekle(item);
                        }
                    } else {
                        Kaldir(item);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void UsernameKontrol(String s, User user) {
        if(user.getUsernamef().trim().length() >= s.trim().length()){
            if(s.trim().equals(user.getUsernamef().trim().substring(0,s.trim().length()))) {
                aranacakDeger = user.getUsernamef().trim();
              //  Ara(user);
            }

        }else{
            //Toast.makeText(mContext, "Hata2", Toast.LENGTH_SHORT).show();
        }

    }


    private void Ekle(User item) {
        if(aramaSirasindaKaldirilanlar.contains(item.getUid())) {
            for (int i = 0; i < mUsers2.size(); i++) {
                if (mUsers2.get(i).getUid().equals(item.getUid())) {
                    if (mUsers.size() < i) {
                        mUsers.add(mUsers.size(), item);
                        if (!aramaSirasindaEkliOlanlar.contains(item.getUid()))
                            aramaSirasindaEkliOlanlar.add(item.getUid());
                        aramaSirasindaKaldirilanlar.remove(item.getUid());

                        favoriAdapter.notifyItemInserted(mUsers.size());
                        favoriAdapter2.notifyItemInserted(mUsers.size());
                    } else {
                        mUsers.add(i, item);
                        if (!aramaSirasindaEkliOlanlar.contains(item.getUid()))
                            aramaSirasindaEkliOlanlar.add(item.getUid());
                        aramaSirasindaKaldirilanlar.remove(item.getUid());

                        favoriAdapter.notifyItemInserted(i);
                        favoriAdapter2.notifyItemInserted(i);
                    }
                }
            }
        }
    }
    void Kaldir(User item){
        for(int i = 0; i < mUsers.size(); i ++){
            if(mUsers.get(i).getUid().equals(item.getUid())){
                if (!aramaSirasindaKaldirilanlar.contains(item.getUid()))
                    aramaSirasindaKaldirilanlar.add(item.getUid());
                if (aramaSirasindaEkliOlanlar.contains(item.getUid()))
                    aramaSirasindaEkliOlanlar.remove(item.getUid());
                mUsers.remove(i);
                favoriAdapter.notifyItemRemoved(i);
                favoriAdapter2.notifyItemRemoved(i);
            }
        }
    }
    private void mesajAra(final String s){
        if(mUsers2.size() > 0) {
            if (s.length() > 0) {
                for(int i = 0; i < mUsers2.size(); i++){
                    AdKontrol(s,mUsers2.get(i));
                }

            }
            else {
                aramaSirasindaKaldirilanlar = new ArrayList<>();
                for(int i = 0; i < mUsers2.size(); i ++){
                    if(!aramaSirasindaEkliOlanlar.contains(mUsers2.get(i).getUid())){
                        aramaSirasindaEkliOlanlar.add(mUsers2.get(i).getUid());
                        mUsers.add(i, mUsers2.get(i));
                        favoriAdapter.notifyItemInserted(i);
                        favoriAdapter2.notifyItemInserted(i);
                    }
                    /*if(mUsers.size() > i){
                        if (!mUsers.get(i).getUid().equals(mUsers2.get(i).getUid())) {
                            mUsers.add(i, mUsers2.get(i));
                            favoriAdapter.notifyItemInserted(i);
                            favoriAdapter2.notifyItemInserted(i);
                        }
                    }
                    else{
                        mUsers.add(i,mUsers2.get(i));
                        favoriAdapter.notifyItemInserted(i);
                        favoriAdapter2.notifyItemInserted(i);
                    }*/

                }
            }
        }else{

        }

    }

    @Override
    public void onResume() {
        super.onResume();

        SharedPreferences tas_shared = getSharedPreferences("TasarimBilgileri",MODE_PRIVATE);
        TasarimDegistir(tas_shared.getString("tasarim_arayuz","1"));
    }

    @Override
    public void onPause() {
        super.onPause();
        fuser = FirebaseAuth.getInstance().getCurrentUser();
        FirebaseDatabase.getInstance().getReference("usersF").child(fuser.getUid()).child("bulduklarim_bildirim_durumu").setValue("yok");
    }

    private void BulunanlariGetir() {
        FirebaseUser fuser = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("usersF").child(fuser.getUid());
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.child("kisitli_erisim_engeli").child("durum").getValue().equals("var")) {
                    Intent intent = new Intent(getAppContext(), MainActivity.class);
                    getAppContext().startActivity(intent);
                }
                if(dataSnapshot.hasChild("bulduklarim")){
                    gorduklerimSayisi = dataSnapshot.child("bulduklarim").getChildrenCount();
                    for(final DataSnapshot ds : dataSnapshot.child("bulduklarim").getChildren()){
                        DatabaseReference dref = FirebaseDatabase.getInstance().getReference("usersF").child(ds.getKey());
                        dref.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot2) {
                                if (dataSnapshot2.exists()) {
                                    if (dataSnapshot2.child("ban_durumu").child("durum").getValue().equals("yok")) {
                                        if (dataSnapshot2.child("hesap_durumu").child("durum").getValue(Integer.class) == 0) {
                                            User user = dataSnapshot2.getValue(User.class);
                                            if (!kullanicilar.contains(user.getUid())) {
                                                kullanicilar.add(user.getUid());
                                                if (!aramaSirasindaEkliOlanlar.contains(user.getUid()))
                                                    aramaSirasindaEkliOlanlar.add(user.getUid());

                                                mUsers.add(mUsers.size(), user);
                                                mUsers2.add(mUsers2.size(), user);
                                                favoriAdapter.notifyDataSetChanged();
                                                favoriAdapter2.notifyDataSetChanged();

                                                ustKisim.setVisibility(View.VISIBLE);
                                                pbar.setVisibility(View.GONE);
                                                recyclerView.setVisibility(View.VISIBLE);
                                            }
                                        }
                                    }
                                }
                                else
                                    dataSnapshot.child("bulduklarim").child(ds.getKey()).getRef().removeValue();
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });


                    }

                    swipeRefreshLayout.setRefreshing(false);
                }else{
                    recyclerView.setVisibility(View.GONE);
                    //bulunanYok.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }


    @Override
    protected void onStop() {
        super.onStop();
        editor2.putInt("KalinanYer", 0).commit();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        FirebaseDatabase.getInstance().getReference("usersF").child(fuser.getUid()).child("bulduklarim_bildirim_durumu").setValue("yok");
    }
    public void TasarimDegistir(String tasDegeri) {
        GradientDrawable gradient = (GradientDrawable) mActivity.getResources().getDrawable(R.drawable.gradient);
        gradient.setOrientation(GradientDrawable.Orientation.LEFT_RIGHT);

        GradientDrawable gradientYumusak = new GradientDrawable();
        gradientYumusak.setOrientation(GradientDrawable.Orientation.LEFT_RIGHT);
        gradientYumusak.setCornerRadius(50);

        int renk1 = 0,renk2 = 0,t1start = 0,t2start = 0,t1end = 0,t2end = 0,orta = 0;

        renk1 = TasarimRenginiGetir.RengiGetir(mActivity,"renk1",tasDegeri);
        renk2 = TasarimRenginiGetir.RengiGetir(mActivity,"renk2",tasDegeri);
        t1start = TasarimRenginiGetir.RengiGetir(mActivity,"t1start",tasDegeri);
        t2start = TasarimRenginiGetir.RengiGetir(mActivity,"t2start",tasDegeri);
        t1end = TasarimRenginiGetir.RengiGetir(mActivity,"t1end",tasDegeri);
        t2end = TasarimRenginiGetir.RengiGetir(mActivity,"t2end",tasDegeri);
        orta = TasarimRenginiGetir.RengiGetir(mActivity,"orta",tasDegeri);

        gradient.setColors(new int[]{
                renk1,
                orta,
                renk2,
        });
        gradientYumusak.setColors(new int[]{
                t1end,
                orta,
                t2start,
        });

        ortaRenk = orta;
        arkaplanBTN.setBackground(gradientYumusak);
        background.setBackground(gradient);
    }
    private static void HangiListe1() {
        aList = new ArrayList<>();
        if(PremList != null){
            if(PremList.size() > 0) {
                for (int i = 0; i < PremList.size(); i++) {
                    aList.add(i, PremList.get(i));
                }
            }else{
                HangiListe2();
            }
        }else{
            HangiListe2();
        }
    }
    private static void HangiListe2() {
        if(YasList != null){
            if(YasList.size() > 0) {
                for (int i = 0; i < YasList.size(); i++) {
                    aList.add(i, YasList.get(i));
                }
            }else{
                HangiListe3();
            }
        }else{
            HangiListe3();
        }
    }
    private static void HangiListe3() {
        if(CinList != null){
            if(CinList.size() > 0) {
                for (int i = 0; i < CinList.size(); i++) {
                    aList.add(i, CinList.get(i));
                }
            }else{
                HangiListe4();
            }
        }else{
            HangiListe4();
        }
    }
    private static void HangiListe4() {
        for (int i = 0; i < mUsers.size(); i++) {
            aList.add(i, mUsers.get(i));
        }
    }

    @Override
    public void onRefresh() {
        swipeRefreshLayout.setRefreshing(true);
        GorunumKontrol();
    }
}
