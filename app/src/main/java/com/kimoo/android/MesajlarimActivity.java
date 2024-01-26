package com.kimoo.android;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.os.Bundle;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.ScrollingMovementMethod;
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

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.kimoo.android.Model.Chat;
import com.kimoo.android.Model.User;
import com.kimoo.android.extra.MesajlarimCardView;
import com.kimoo.android.extra.MesajlarimItemKullaniciVeOda;
import com.kimoo.android.extra.MesajlarimItemOdaVeZaman;
import com.kimoo.android.extra.MesajlarimListView;
import com.kimoo.android.extra.TasarimRenginiGetir;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MesajlarimActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener {
    private RecyclerView recyclerView;
    FirebaseUser fuser;
    MesajlarimCardView mesajlarimCardView;
    MesajlarimListView mesajlarimListView;
    SwipeRefreshLayout swipeRefreshLayout;
    List<String> mOdalar,mOdalar2,kaldirilanlar = new ArrayList<>(), kacKisiVar = new ArrayList<>();
    RelativeLayout yeniMesajVar,pbar;
    TextView yeniKacMesajVar;
    List<MesajlarimItemKullaniciVeOda> mesajlarimItemKullaniciVeOdaList, mesajlarimItemKullaniciVeOdaList2;
    List<MesajlarimItemOdaVeZaman> mesajlarimItemOdaVeZamanList;
    EditText ara;
    LinearLayout mesajinyok,background;
    static String aranacakDeger;
    boolean olustu;
    ImageView kart,listee;
    private static int gorunumDeger;
    SharedPreferences sharedPref;
    public static int yeniMesajSayisi,arananHarflerinSayisi;
    private boolean refreshleniyorMu;
    public static int ortaRenk;
    private DatabaseReference mesRef, digerRef, benRef;
    private ValueEventListener mesRefListener, digerRefListener;
    private ChildEventListener benRefListener;

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
        setContentView(R.layout.activity_mesajlarim);

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


        pbar = findViewById(R.id.pbar);
        ara = findViewById(R.id.ara);
        recyclerView = findViewById(R.id.recylerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        fuser = FirebaseAuth.getInstance().getCurrentUser();
        mesRef = FirebaseDatabase.getInstance().getReference("Mesajlar");
        benRef = FirebaseDatabase.getInstance().getReference("usersF").child(fuser.getUid()).child("kilidini_actiklarim");

        currentUserId("mesajlarim");
        kart = findViewById(R.id.kart_gorunumu);
        listee = findViewById(R.id.liste_gorunumu);
        mesajinyok = findViewById(R.id.mesaj_yok);
        background = findViewById(R.id.background);
        yeniMesajVar = findViewById(R.id.yeni_mesaj_var);
        yeniKacMesajVar = findViewById(R.id.yeni_kac_mesaj_var);
        swipeRefreshLayout = findViewById(R.id.swipe_layout);

        swipeRefreshLayout.setOnRefreshListener(this);
        swipeRefreshLayout.setColorSchemeResources(android.R.color.holo_orange_dark);

        sharedPref = this.getPreferences(Context.MODE_PRIVATE);

        SharedPreferences tas_shared = getSharedPreferences("TasarimBilgileri",MODE_PRIVATE);
        TasarimDegistir(tas_shared.getString("tasarim_arayuz","1"));

        final SharedPreferences.Editor editorr = sharedPref.edit();

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(getResources().getDrawable(R.drawable.ic_mesaj_geri2));
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent tarayaDon = new Intent(MesajlarimActivity.this,TaraActivity.class);
                startActivity(tarayaDon);
                overridePendingTransition(R.anim.slide_in_right,R.anim.slide_out_left);
                finish();
            }
        });

        ara.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                arananHarflerinSayisi = count;
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                arananHarflerinSayisi = count;
                if (mesajlarimItemKullaniciVeOdaList != null)
                    mesajAra(s.toString().toLowerCase());
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });


        listee.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (gorunumDeger != 0) {
                    editorr.putInt("gorunum_mesajlarim", 0);
                    editorr.commit();
                    gorunumDeger = 0;
                    listee.setColorFilter(ortaRenk);
                    kart.setColorFilter(getResources().getColor(R.color.gri2));

                    //mesajlarimListView = new MesajlarimListView(MesajlarimActivity.this,mesajlarimItemKullaniciVeOdaList, MesajlarimActivity.this);
                    recyclerView.setLayoutManager(new LinearLayoutManager(MesajlarimActivity.this));
                    recyclerView.setAdapter(mesajlarimListView);
                }

            }
        });
        kart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (gorunumDeger != 1) {
                    editorr.putInt("gorunum_mesajlarim", 1);
                    editorr.commit();

                    listee.setColorFilter(getResources().getColor(R.color.gri2));
                    kart.setColorFilter(ortaRenk);
                    gorunumDeger = 1;

                    //mesajlarimCardView = new MesajlarimCardView(MesajlarimActivity.this,mesajlarimItemKullaniciVeOdaList, MesajlarimActivity.this);
                    recyclerView.setLayoutManager(new GridLayoutManager(getApplicationContext(), 3));
                    recyclerView.setAdapter(mesajlarimCardView);
                }

            }
        });

        yeniMesajVar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ara.setText("");
                yeniMesajSayisi = 0;
                yeniKacMesajVar.setVisibility(View.GONE);
            }
        });

        DatabaseReference sistemRef = FirebaseDatabase.getInstance().getReference("Sistem");
        sistemRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.child("versiyon_zorunlu").getValue(Integer.class) <= BuildConfig.VERSION_CODE) {
                    if (dataSnapshot.child("sistem_durumu").child("durum").getValue(Integer.class) == 0) {

                        GorunumKontrol();
                        BildirimKontrol();
                        mesajlarimItemKullaniciVeOdaList = new ArrayList<>();
                        mesajlarimItemKullaniciVeOdaList2 = new ArrayList<>();
                        mesajlarimItemOdaVeZamanList = new ArrayList<>();
                        mOdalar = new ArrayList<>();
                        mOdalar2 = new ArrayList<>();

                        mesajlarimCardView = new MesajlarimCardView(MesajlarimActivity.this, mesajlarimItemKullaniciVeOdaList, MesajlarimActivity.this);
                        mesajlarimListView = new MesajlarimListView(MesajlarimActivity.this, mesajlarimItemKullaniciVeOdaList, MesajlarimActivity.this);
                        if (gorunumDeger == 1) {
                            recyclerView.setLayoutManager(new GridLayoutManager(getApplicationContext(), 3));
                            recyclerView.setAdapter(mesajlarimCardView);
                        } else {
                            recyclerView.setLayoutManager(new LinearLayoutManager(MesajlarimActivity.this));
                            recyclerView.setAdapter(mesajlarimListView);
                        }

                        KimlerleMesajlasmisim();

                    } else if (dataSnapshot.child("sistem_durumu").child("durum").getValue(Integer.class) == 1) {
                        startActivity(new Intent(MesajlarimActivity.this, MainActivity.class));
                    }
                }
                else{
                    Toast.makeText(MesajlarimActivity.this, "Eski bir versiyon kullanıyorsunuz, lütfen uygulamayı güncelleyiniz.", Toast.LENGTH_LONG).show();
                    finishAffinity();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void currentUserId(String Id){
        SharedPreferences sp = getSharedPreferences("SP_USER",MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString("Current_USERID",Id);
        editor.apply();
    }



    private void KimlerleMesajlasmisim() {
        benRef.addChildEventListener(benRefListener = new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot benimSnap, @Nullable String s) {
                if (benimSnap.exists()) {
                    if (benimSnap.getChildrenCount() > 0) {
                        if (benimSnap.hasChild("mesaj") && benimSnap.hasChild("oda") && benimSnap.hasChild("durum"))
                            if (benimSnap.child("mesaj").getValue(String.class).equals("var")) {
                                final int[] mesajSayisi = {0};
                                final int[] olanMesajSayisi = {0};
                                if (!mOdalar.contains(benimSnap.child("oda").getValue(String.class))) {
                                    FirebaseDatabase.getInstance().getReference("Mesajlar").child(benimSnap.child("oda").getValue(String.class)).addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                            for (DataSnapshot ds : dataSnapshot.getChildren()){
                                                if (ds.getChildrenCount() > 0){
                                                    mesajSayisi[0]++;
                                                    Chat chat = ds.getValue(Chat.class);
                                                    if(!chat.getGormek_istemeyen1().equals(fuser.getUid()) && !chat.getGormek_istemeyen2().equals(fuser.getUid())){
                                                        olanMesajSayisi[0]++;
                                                    }
                                                    if (mesajSayisi[0] == dataSnapshot.getChildrenCount()-1)
                                                        if (olanMesajSayisi[0] != 0){
                                                            mOdalar.add(benimSnap.child("oda").getValue(String.class));
                                                            DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("usersF").child(benimSnap.getKey());
                                                            userRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                                                @Override
                                                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                                    User user = dataSnapshot.getValue(User.class);
                                                                    if (dataSnapshot.exists()) {
                                                                        if (dataSnapshot.child("hesap_durumu").child("durum").getValue(Integer.class) == 0) {
                                                                            mesRef.addValueEventListener(mesRefListener = new ValueEventListener() {
                                                                                @Override
                                                                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                                                    if (dataSnapshot.hasChild(benimSnap.child("oda").getValue(String.class))) {
                                                                                        dataSnapshot = dataSnapshot.child(benimSnap.child("oda").getValue(String.class));
                                                                                        if (!mOdalar2.contains(benimSnap.child("oda").getValue(String.class))) {
                                                                                            mOdalar2.add(benimSnap.child("oda").getValue(String.class));

                                                                                            if (arananHarflerinSayisi == 0) {
                                                                                                if (dataSnapshot.hasChild("son_mesaj")) {
                                                                                                    mesajlarimItemKullaniciVeOdaList.add(new MesajlarimItemKullaniciVeOda(user, benimSnap.child("oda").getValue(String.class),
                                                                                                            dataSnapshot.child(dataSnapshot.child("son_mesaj").getValue(String.class)).child("zaman").getValue(Long.class)));
                                                                                                    Collections.sort(mesajlarimItemKullaniciVeOdaList, new Comparator<MesajlarimItemKullaniciVeOda>() {
                                                                                                        @Override
                                                                                                        public int compare(MesajlarimItemKullaniciVeOda o1, MesajlarimItemKullaniciVeOda o2) {
                                                                                                            return (int) (o2.getZaman() - o1.getZaman());
                                                                                                        }
                                                                                                    });
                                                                                                }
                                                                                                //mesajlarimCardView.notifyDataSetChanged();
                                                                                                //mesajlarimListView.notifyDataSetChanged();

                                                                                                mesajlarimCardView = new MesajlarimCardView(MesajlarimActivity.this, mesajlarimItemKullaniciVeOdaList, MesajlarimActivity.this);
                                                                                                mesajlarimListView = new MesajlarimListView(MesajlarimActivity.this, mesajlarimItemKullaniciVeOdaList, MesajlarimActivity.this);

                                                                                                if (gorunumDeger == 1) {
                                                                                                    recyclerView.setLayoutManager(new GridLayoutManager(getApplicationContext(), 3));
                                                                                                    recyclerView.setAdapter(mesajlarimCardView);
                                                                                                } else {
                                                                                                    recyclerView.setLayoutManager(new LinearLayoutManager(MesajlarimActivity.this));
                                                                                                    recyclerView.setAdapter(mesajlarimListView);
                                                                                                }

                                                                                                swipeRefreshLayout.setRefreshing(false);
                                                                                                refreshleniyorMu = false;
                                                                                            } else {
                                                                                                yeniMesajSayisi++;
                                                                                                yeniMesajVar.setVisibility(View.VISIBLE);
                                                                                                yeniKacMesajVar.setText("+" + yeniMesajSayisi + " Mesaj");
                                                                                            }

                                                                                            if (dataSnapshot.hasChild("son_mesaj")) {
                                                                                                mesajlarimItemKullaniciVeOdaList2.add(new MesajlarimItemKullaniciVeOda(user, benimSnap.child("oda").getValue(String.class),
                                                                                                        dataSnapshot.child(dataSnapshot.child("son_mesaj").getValue(String.class)).child("zaman").getValue(Long.class)));
                                                                                            }

                                                                                            Collections.sort(mesajlarimItemKullaniciVeOdaList2, new Comparator<MesajlarimItemKullaniciVeOda>() {
                                                                                                @Override
                                                                                                public int compare(MesajlarimItemKullaniciVeOda o1, MesajlarimItemKullaniciVeOda o2) {
                                                                                                    return (int) (o2.getZaman() - o1.getZaman());
                                                                                                }
                                                                                            });

                                                                                            listee.setEnabled(true);
                                                                                            kart.setEnabled(true);
                                                                                            ara.setEnabled(true);

                                                                                        } else {
                                                                                            for (int i = 0; i < mesajlarimItemKullaniciVeOdaList.size(); i++) {
                                                                                                if (mesajlarimItemKullaniciVeOdaList.get(i).getOdaNo().equals(benimSnap.child("oda").getValue(String.class))) {
                                                                                                    mesajlarimItemKullaniciVeOdaList.get(i).setZaman(dataSnapshot.child(dataSnapshot.child("son_mesaj").getValue(String.class)).child("zaman").getValue(Long.class));
                                                                                                    Collections.sort(mesajlarimItemKullaniciVeOdaList, new Comparator<MesajlarimItemKullaniciVeOda>() {
                                                                                                        @Override
                                                                                                        public int compare(MesajlarimItemKullaniciVeOda o1, MesajlarimItemKullaniciVeOda o2) {
                                                                                                            return (int) (o2.getZaman() - o1.getZaman());
                                                                                                        }
                                                                                                    });

                                                                                                    Collections.sort(mesajlarimItemKullaniciVeOdaList2, new Comparator<MesajlarimItemKullaniciVeOda>() {
                                                                                                        @Override
                                                                                                        public int compare(MesajlarimItemKullaniciVeOda o1, MesajlarimItemKullaniciVeOda o2) {
                                                                                                            return (int) (o2.getZaman() - o1.getZaman());
                                                                                                        }
                                                                                                    });


                                                                                                    //mesajlarimCardView.notifyDataSetChanged();
                                                                                                    //mesajlarimListView.notifyDataSetChanged();

                                                                                                    mesajlarimCardView = new MesajlarimCardView(MesajlarimActivity.this, mesajlarimItemKullaniciVeOdaList, MesajlarimActivity.this);
                                                                                                    mesajlarimListView = new MesajlarimListView(MesajlarimActivity.this, mesajlarimItemKullaniciVeOdaList, MesajlarimActivity.this);

                                                                                                    if (gorunumDeger == 1) {
                                                                                                        recyclerView.setLayoutManager(new GridLayoutManager(getApplicationContext(), 3));
                                                                                                        recyclerView.setAdapter(mesajlarimCardView);
                                                                                                    } else {
                                                                                                        recyclerView.setLayoutManager(new LinearLayoutManager(MesajlarimActivity.this));
                                                                                                        recyclerView.setAdapter(mesajlarimListView);
                                                                                                    }
                                                                                                    swipeRefreshLayout.setRefreshing(false);
                                                                                                    refreshleniyorMu = false;
                                                                                                }
                                                                                            }

                                                                                            if (arananHarflerinSayisi == 0) {

                                                                                            } else {
                                                                                                if (kaldirilanlar.contains(benimSnap.child("oda").getValue(String.class))) {
                                                                                                    yeniMesajSayisi++;
                                                                                                    yeniMesajVar.setVisibility(View.VISIBLE);
                                                                                                    yeniKacMesajVar.setText("+" + yeniMesajSayisi + " Mesaj");
                                                                                                }
                                                                                            }
                                                                                        }
                                                                                    }
                                                                                    else {
                                                                                        mOdalar2.remove(benimSnap.child("oda").getValue(String.class));
                                                                                        for (int i = 0; i < mesajlarimItemKullaniciVeOdaList.size(); i++) {
                                                                                            if (mesajlarimItemKullaniciVeOdaList.get(i).getOdaNo().equals(benimSnap.child("oda").getValue(String.class))) {
                                                                                                mesajlarimItemKullaniciVeOdaList.remove(i);
                                                                                                mesajlarimItemKullaniciVeOdaList2.remove(i);
                                                                                            }
                                                                                        }

                                                                                        Collections.sort(mesajlarimItemKullaniciVeOdaList, new Comparator<MesajlarimItemKullaniciVeOda>() {
                                                                                            @Override
                                                                                            public int compare(MesajlarimItemKullaniciVeOda o1, MesajlarimItemKullaniciVeOda o2) {
                                                                                                return (int) (o2.getZaman() - o1.getZaman());
                                                                                            }
                                                                                        });

                                                                                        Collections.sort(mesajlarimItemKullaniciVeOdaList2, new Comparator<MesajlarimItemKullaniciVeOda>() {
                                                                                            @Override
                                                                                            public int compare(MesajlarimItemKullaniciVeOda o1, MesajlarimItemKullaniciVeOda o2) {
                                                                                                return (int) (o2.getZaman() - o1.getZaman());
                                                                                            }
                                                                                        });
                                                                                        //mesajlarimCardView.notifyDataSetChanged();
                                                                                        //mesajlarimListView.notifyDataSetChanged();
                                                                                        mesajlarimCardView = new MesajlarimCardView(MesajlarimActivity.this, mesajlarimItemKullaniciVeOdaList, MesajlarimActivity.this);
                                                                                        mesajlarimListView = new MesajlarimListView(MesajlarimActivity.this, mesajlarimItemKullaniciVeOdaList, MesajlarimActivity.this);

                                                                                        if (gorunumDeger == 1) {
                                                                                            recyclerView.setLayoutManager(new GridLayoutManager(getApplicationContext(), 3));
                                                                                            recyclerView.setAdapter(mesajlarimCardView);
                                                                                        } else {
                                                                                            recyclerView.setLayoutManager(new LinearLayoutManager(MesajlarimActivity.this));
                                                                                            recyclerView.setAdapter(mesajlarimListView);
                                                                                        }
                                                                                        swipeRefreshLayout.setRefreshing(false);
                                                                                        refreshleniyorMu = false;
                                                                                    }
                                                                                }

                                                                                @Override
                                                                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                                                                }
                                                                            });
                                                                        } else {
                                                                            // Hesabı donuk birisi bulunursa
                                                                        }
                                                                    }
                                                                    else
                                                                        benimSnap.getRef().removeValue();
                                                                }

                                                                @Override
                                                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                                                }
                                                            });
                                                        }
                                                    else{
                                                        swipeRefreshLayout.setRefreshing(false);
                                                        refreshleniyorMu = false;
                                                    }

                                                }
                                            }
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError databaseError) {

                                        }
                                    });


                                }
                                pbar.setVisibility(View.GONE);
                                recyclerView.setVisibility(View.VISIBLE);
                                mesajinyok.setVisibility(View.GONE);
                            } else {
                                pbar.setVisibility(View.GONE);
                            }
                    }
                    else {
                        pbar.setVisibility(View.GONE);
                        mesajinyok.setVisibility(View.VISIBLE);
                    }
                }
                else {
                    pbar.setVisibility(View.GONE);
                    mesajinyok.setVisibility(View.VISIBLE);
                }
            }
            @Override
            public void onChildChanged(@NonNull DataSnapshot benimSnap, @Nullable String s) {
                if(benimSnap.getChildrenCount() > 0) {
                    if (benimSnap.hasChild("mesaj"))
                        if (benimSnap.child("mesaj").getValue(String.class).equals("yok")) {

                        } else {
                            KimlerleMesajlasmisim();
                        }
                }
                else{

                }
            }
            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }
            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                //Toast.makeText(MesajlarimActivity.this, ""+databaseError, Toast.LENGTH_SHORT).show();
            }
        });

        benRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (!dataSnapshot.exists()) {
                    pbar.setVisibility(View.GONE);
                    mesajinyok.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        if (!refreshleniyorMu) {
            if (mesajlarimItemKullaniciVeOdaList.size() == 0) {
                recyclerView.setVisibility(View.GONE);
                listee.setEnabled(false);
                kart.setEnabled(false);
                ara.setEnabled(false);
            } else {
                recyclerView.setVisibility(View.VISIBLE);
                listee.setEnabled(true);
                kart.setEnabled(true);
                ara.setEnabled(true);
            }
        }

    }
    @Override
    protected void onPause() {
        super.onPause();
        olustu = false;
        if(fuser != null)
            currentUserId("none");
        FirebaseDatabase.getInstance().getReference("usersF").child(fuser.getUid()).child("mesaj_bildirim_durumu").setValue("yok");
        //MesajlarimListView.TumListenerlariDurdur();
        //MesajlarimCardView.TumListenerlariDurdur();
        /*if (digerRef != null && digerRef != null) {
            digerRef.removeEventListener(digerRefListener);
        }*/
        /*if (mesRef != null && mesRef != null) {
            mesRef.removeEventListener(mesRefListener);
        }*/
        /*if (benRef != null && benRef != null) {
            benRef.removeEventListener(benRefListener);
        }*/
       // finish();
    }

    @Override
    protected void onStop() {
        super.onStop();
       // MesajlarimListView.TumListenerlariDurdur();
        //MesajlarimCardView.TumListenerlariDurdur();
        /*if (digerRef != null && digerRef != null) {
            digerRef.removeEventListener(digerRefListener);
        }*/
       /* if (mesRef != null && mesRef != null) {
            mesRef.removeEventListener(mesRefListener);
        }*/
        /*if (benRef != null && benRef != null) {
            benRef.removeEventListener(benRefListener);
        }*/
        //finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
       // MesajlarimListView.TumListenerlariDurdur();
       // MesajlarimCardView.TumListenerlariDurdur();
        /*if (digerRef != null && digerRef != null) {
            digerRef.removeEventListener(digerRefListener);
        }*/
        /*if (mesRef != null && mesRef != null) {
            mesRef.removeEventListener(mesRefListener);
        }*/
       /* if (benRef != null && benRef != null) {
            benRef.removeEventListener(benRefListener);
        }*/
       // finish();
    }


    @Override
    protected void onResume() {
        super.onResume();
        ara.setText("");
        arananHarflerinSayisi = 0;
        if(fuser != null)
            currentUserId("mesajlarim");
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("usersF").child(fuser.getUid());
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                PPSilinmisMi(dataSnapshot);
                if(dataSnapshot.child("kisitli_erisim_engeli").child("durum").getValue().equals("var")) {
                    Intent intent = new Intent(MesajlarimActivity.this, MainActivity.class);
                    startActivity(intent);
                }
                SharedPreferences tas_shared = getSharedPreferences("TasarimBilgileri",MODE_PRIVATE);
                SharedPreferences.Editor editor = tas_shared.edit();
                if (!tas_shared.getString("tasarim_arayuz","1").equals("sifirlandi"))
                    TasarimDegistir(tas_shared.getString("tasarim_arayuz","1"));
                else {
                    TasarimDegistir(dataSnapshot.child("tas_arayuz").getValue(String.class));
                    editor.putString("tasarim_arayuz",dataSnapshot.child("tas_arayuz").getValue(String.class));
                    editor.commit();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void PPSilinmisMi(DataSnapshot dataSnapshot) {
        if (dataSnapshot.child("fotograflarim").child("pp").getValue(String.class).equals("")) {

            Dialog dialog = new Dialog(MesajlarimActivity.this);
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
                    Intent intent = new Intent(MesajlarimActivity.this, ProfilActivity.class);
                    intent.putExtra("foto_yukle","pp");
                    startActivity(intent);
                }
            });
            dialog.show();
        }
    }
    public void TasarimDegistir(String tasDegeri) {
        GradientDrawable gradient = (GradientDrawable) getResources().getDrawable(R.drawable.gradient);
        gradient.setOrientation(GradientDrawable.Orientation.LEFT_RIGHT);

        int renk1 = 0,renk2 = 0,t1start = 0,t2start = 0,t1end = 0,t2end = 0,orta = 0;

        renk1 = TasarimRenginiGetir.RengiGetir(MesajlarimActivity.this,"renk1",tasDegeri);
        renk2 = TasarimRenginiGetir.RengiGetir(MesajlarimActivity.this,"renk2",tasDegeri);
        t1start = TasarimRenginiGetir.RengiGetir(MesajlarimActivity.this,"t1start",tasDegeri);
        t2start = TasarimRenginiGetir.RengiGetir(MesajlarimActivity.this,"t2start",tasDegeri);
        t1end = TasarimRenginiGetir.RengiGetir(MesajlarimActivity.this,"t1end",tasDegeri);
        t2end = TasarimRenginiGetir.RengiGetir(MesajlarimActivity.this,"t2end",tasDegeri);
        orta = TasarimRenginiGetir.RengiGetir(MesajlarimActivity.this,"orta",tasDegeri);

        gradient.setColors(new int[]{
                renk1,
                orta,
                renk2,
        });

        ortaRenk = orta;

        background.setBackground(gradient);
    }
    private void GorunumKontrol() {
        if(sharedPref.getInt("gorunum_mesajlarim",0) == 0){
            listee.setColorFilter(ortaRenk);
            kart.setColorFilter(getResources().getColor(R.color.gri2));
            gorunumDeger = 0;

            mesajlarimItemKullaniciVeOdaList = new ArrayList<>();
            mesajlarimItemKullaniciVeOdaList2 = new ArrayList<>();
            mesajlarimItemOdaVeZamanList = new ArrayList<>();
            mOdalar = new ArrayList<>();
            mOdalar2 = new ArrayList<>();

            recyclerView.setVisibility(View.GONE);
            listee.setEnabled(false);
            kart.setEnabled(false);
            ara.setEnabled(false);

            mesajlarimListView = new MesajlarimListView(MesajlarimActivity.this,mesajlarimItemKullaniciVeOdaList, MesajlarimActivity.this);
            recyclerView.setLayoutManager(new LinearLayoutManager(MesajlarimActivity.this));
            recyclerView.setAdapter(mesajlarimListView);
            KimlerleMesajlasmisim();
        }
        else{
            listee.setColorFilter(getResources().getColor(R.color.gri2));
            kart.setColorFilter(ortaRenk);
            gorunumDeger = 1;

            mesajlarimItemKullaniciVeOdaList = new ArrayList<>();
            mesajlarimItemKullaniciVeOdaList2 = new ArrayList<>();
            mesajlarimItemOdaVeZamanList = new ArrayList<>();
            mOdalar = new ArrayList<>();
            mOdalar2 = new ArrayList<>();

            recyclerView.setVisibility(View.GONE);
            listee.setEnabled(false);
            kart.setEnabled(false);
            ara.setEnabled(false);

            mesajlarimCardView = new MesajlarimCardView(MesajlarimActivity.this, mesajlarimItemKullaniciVeOdaList,MesajlarimActivity.this);
            recyclerView.setLayoutManager(new GridLayoutManager(getApplicationContext(), 3));
            recyclerView.setAdapter(mesajlarimCardView);
            KimlerleMesajlasmisim();
        }
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent tarayaDon = new Intent(MesajlarimActivity.this,TaraActivity.class);
        startActivity(tarayaDon);
        finish();
        overridePendingTransition(R.anim.slide_in_right,R.anim.slide_out_left);
    }
    public void MesajlardanGeri(View view){
        Intent geri = new Intent(MesajlarimActivity.this,TaraActivity.class);
        startActivity(geri);
    }
    void AdKontrol(final String s, MesajlarimItemKullaniciVeOda item){

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("usersF").child(item.getUser().getUid());
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (item.getUser().getGizlilik_ad().equals("0")) {
                    if (dataSnapshot.child("begendiklerim").child(fuser.getUid()).exists()) {
                        if(item.getUser().getAd().trim().length() >= s.trim().length()){
                            if(!s.trim().toLowerCase().equals(item.getUser().getAd().trim().substring(0,s.trim().length()))) {
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
                        if(item.getUser().getAd().trim().substring(0,1).length() == s.trim().length()){
                            if(!s.trim().toLowerCase().equals(item.getUser().getAd().trim().substring(0,1))) {
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
                else if (item.getUser().getGizlilik_ad().equals("1")) {
                    if (dataSnapshot.child("begenenler").child(fuser.getUid()).exists()) {
                        if(item.getUser().getAd().trim().length() >= s.trim().length()){
                            if(!s.trim().toLowerCase().equals(item.getUser().getAd().trim().substring(0,s.trim().length()))) {
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
                        if(item.getUser().getAd().trim().substring(0,1).length() >= s.trim().length()){
                            if(!s.trim().toLowerCase().equals(item.getUser().getAd().trim().substring(0,s.trim().length()))) {
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
                else if (item.getUser().getGizlilik_ad().equals("2")) {
                    if (dataSnapshot.child("mesajlastiklarim").child(fuser.getUid()).exists()) {
                        if (item.getUser().getAd().trim().length() >= s.trim().length()) {
                            if (!s.trim().toLowerCase().equals(item.getUser().getAd().trim().substring(0, s.trim().length()))) {
                                Kaldir(item);
                            } else {
                                Ekle(item);
                            }
                        } else {
                            Kaldir(item);
                        }
                    }
                    else{
                        if(item.getUser().getAd().trim().substring(0,1).length() >= s.trim().length()){
                            if(!s.trim().toLowerCase().equals(item.getUser().getAd().trim().substring(0,s.trim().length()))) {
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
                else if (item.getUser().getGizlilik_ad().equals("3")) {
                    if(item.getUser().getAd().trim().substring(0,1).length() >= s.trim().length()){
                        if(!s.trim().toLowerCase().equals(item.getUser().getAd().trim().substring(0,s.trim().length()))) {
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
                    if (item.getUser().getAd().trim().length() >= s.trim().length()) {
                        if (!s.trim().toLowerCase().equals(item.getUser().getAd().trim().substring(0, s.trim().length()))) {
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

    private void Ekle(MesajlarimItemKullaniciVeOda item) {
        if(kaldirilanlar.contains(item.getOdaNo())) {
            for (int i = 0; i < mesajlarimItemKullaniciVeOdaList2.size(); i++) {
                if (mesajlarimItemKullaniciVeOdaList2.get(i).getOdaNo().equals(item.getOdaNo())) {
                    if (mesajlarimItemKullaniciVeOdaList.size() < i) {

                        if (kaldirilanlar.contains(item.getOdaNo()))
                            kaldirilanlar.remove(item.getOdaNo());
                        mesajlarimItemKullaniciVeOdaList.add(mesajlarimItemKullaniciVeOdaList.size(), item);
                        //mesajlarimCardView.notifyItemInserted(mesajlarimItemKullaniciVeOdaList.size());
                        //mesajlarimListView.notifyItemInserted(mesajlarimItemKullaniciVeOdaList.size());
                        //mesajlarimCardView.notifyDataSetChanged();
                        //mesajlarimListView.notifyDataSetChanged();

                        mesajlarimCardView = new MesajlarimCardView(MesajlarimActivity.this, mesajlarimItemKullaniciVeOdaList,MesajlarimActivity.this);
                        mesajlarimListView = new MesajlarimListView(MesajlarimActivity.this, mesajlarimItemKullaniciVeOdaList, MesajlarimActivity.this);

                        if (gorunumDeger == 1) {
                            recyclerView.setLayoutManager(new GridLayoutManager(getApplicationContext(), 3));
                            recyclerView.setAdapter(mesajlarimCardView);
                        } else {
                            recyclerView.setLayoutManager(new LinearLayoutManager(MesajlarimActivity.this));
                            recyclerView.setAdapter(mesajlarimListView);
                        }

                    }
                    else {

                        if (kaldirilanlar.contains(item.getOdaNo()))
                            kaldirilanlar.remove(item.getOdaNo());
                        mesajlarimItemKullaniciVeOdaList.add(i, item);
                        //mesajlarimCardView.notifyDataSetChanged();
                        //mesajlarimListView.notifyDataSetChanged();
                        //mesajlarimCardView.notifyItemInserted(i);
                        //mesajlarimListView.notifyItemInserted(i);

                        mesajlarimCardView = new MesajlarimCardView(MesajlarimActivity.this, mesajlarimItemKullaniciVeOdaList,MesajlarimActivity.this);
                        mesajlarimListView = new MesajlarimListView(MesajlarimActivity.this, mesajlarimItemKullaniciVeOdaList, MesajlarimActivity.this);

                        if (gorunumDeger == 1) {
                            recyclerView.setLayoutManager(new GridLayoutManager(getApplicationContext(), 3));
                            recyclerView.setAdapter(mesajlarimCardView);
                        } else {
                            recyclerView.setLayoutManager(new LinearLayoutManager(MesajlarimActivity.this));
                            recyclerView.setAdapter(mesajlarimListView);
                        }

                    }
                }
            }
        }
    }
    void Kaldir(MesajlarimItemKullaniciVeOda item){
        for(int i = 0; i < mesajlarimItemKullaniciVeOdaList.size(); i ++){
            if(mesajlarimItemKullaniciVeOdaList.get(i).getOdaNo().equals(item.getOdaNo())){
                if (!kaldirilanlar.contains(item.getOdaNo()))
                    kaldirilanlar.add(item.getOdaNo());
                mesajlarimItemKullaniciVeOdaList.remove(i);
                //mesajlarimCardView.notifyDataSetChanged();
                //mesajlarimListView.notifyDataSetChanged();
                //mesajlarimCardView.notifyItemRemoved(i);
                //mesajlarimListView.notifyItemRemoved(i);

                mesajlarimCardView = new MesajlarimCardView(MesajlarimActivity.this, mesajlarimItemKullaniciVeOdaList,MesajlarimActivity.this);
                mesajlarimListView = new MesajlarimListView(MesajlarimActivity.this, mesajlarimItemKullaniciVeOdaList, MesajlarimActivity.this);

                if (gorunumDeger == 1) {
                    recyclerView.setLayoutManager(new GridLayoutManager(getApplicationContext(), 3));
                    recyclerView.setAdapter(mesajlarimCardView);
                } else {
                    recyclerView.setLayoutManager(new LinearLayoutManager(MesajlarimActivity.this));
                    recyclerView.setAdapter(mesajlarimListView);
                }
            }
        }
    }

    private void mesajAra(final String s){
        if(mesajlarimItemKullaniciVeOdaList2.size() > 0) {
            if (s.length() > 0) {
                for(int i = 0; i < mesajlarimItemKullaniciVeOdaList2.size(); i++){
                    AdKontrol(s,mesajlarimItemKullaniciVeOdaList2.get(i));
                }

            } else {
                yeniMesajSayisi = 0;
                yeniMesajVar.setVisibility(View.GONE);

                kaldirilanlar = new ArrayList<>();
                mesajlarimItemKullaniciVeOdaList = new ArrayList<>();
                for(int i = 0; i < mesajlarimItemKullaniciVeOdaList2.size(); i ++){
                    mesajlarimItemKullaniciVeOdaList.add(mesajlarimItemKullaniciVeOdaList2.get(i));
                    if (mesajlarimItemKullaniciVeOdaList.size() == mesajlarimItemKullaniciVeOdaList2.size()){
                        mesajlarimCardView = new MesajlarimCardView(MesajlarimActivity.this, mesajlarimItemKullaniciVeOdaList2,MesajlarimActivity.this);
                        mesajlarimListView = new MesajlarimListView(MesajlarimActivity.this, mesajlarimItemKullaniciVeOdaList2, MesajlarimActivity.this);
                        if (gorunumDeger == 1) {
                            recyclerView.setLayoutManager(new GridLayoutManager(getApplicationContext(), 3));
                            recyclerView.setAdapter(mesajlarimCardView);
                        } else {
                            recyclerView.setLayoutManager(new LinearLayoutManager(MesajlarimActivity.this));
                            recyclerView.setAdapter(mesajlarimListView);
                        }
                    }
                }
                /*for(int i = 0; i < mesajlarimItemKullaniciVeOdaList2.size(); i ++){
                    if(mesajlarimItemKullaniciVeOdaList.size() > i){
                        if (!mesajlarimItemKullaniciVeOdaList.get(i).getOdaNo().equals(mesajlarimItemKullaniciVeOdaList2.get(i).getOdaNo())) {
                            mesajlarimItemKullaniciVeOdaList.add(i, mesajlarimItemKullaniciVeOdaList2.get(i));

                            //mesajlarimCardView.notifyItemInserted(i);
                            //mesajlarimListView.notifyItemInserted(i);
                            if (mesajlarimItemKullaniciVeOdaList.size() == mesajlarimItemKullaniciVeOdaList2.size()){
                                //mesajlarimCardView.notifyDataSetChanged();
                                //mesajlarimListView.notifyDataSetChanged();
                                mesajlarimCardView = new MesajlarimCardView(MesajlarimActivity.this, mesajlarimItemKullaniciVeOdaList,MesajlarimActivity.this);
                                mesajlarimListView = new MesajlarimListView(MesajlarimActivity.this, mesajlarimItemKullaniciVeOdaList, MesajlarimActivity.this);

                                if (gorunumDeger == 1) {
                                    recyclerView.setLayoutManager(new GridLayoutManager(getApplicationContext(), 3));
                                    recyclerView.setAdapter(mesajlarimCardView);
                                } else {
                                    recyclerView.setLayoutManager(new LinearLayoutManager(MesajlarimActivity.this));
                                    recyclerView.setAdapter(mesajlarimListView);
                                }
                            }
                        }
                    }
                    else{
                        mesajlarimItemKullaniciVeOdaList.add(i,mesajlarimItemKullaniciVeOdaList2.get(i));
                        //mesajlarimCardView.notifyItemInserted(i);
                        //mesajlarimListView.notifyItemInserted(i);
                        if (mesajlarimItemKullaniciVeOdaList.size() == mesajlarimItemKullaniciVeOdaList2.size()){
                            //mesajlarimCardView.notifyDataSetChanged();
                            //mesajlarimListView.notifyDataSetChanged();
                            mesajlarimCardView = new MesajlarimCardView(MesajlarimActivity.this, mesajlarimItemKullaniciVeOdaList,MesajlarimActivity.this);
                            mesajlarimListView = new MesajlarimListView(MesajlarimActivity.this, mesajlarimItemKullaniciVeOdaList, MesajlarimActivity.this);

                            if (gorunumDeger == 1) {
                                recyclerView.setLayoutManager(new GridLayoutManager(getApplicationContext(), 3));
                                recyclerView.setAdapter(mesajlarimCardView);
                            } else {
                                recyclerView.setLayoutManager(new LinearLayoutManager(MesajlarimActivity.this));
                                recyclerView.setAdapter(mesajlarimListView);
                            }
                        }
                    }

                }*/
            }
        }else{

        }

    }

    private void BildirimKontrol() {
        FirebaseDatabase.getInstance().getReference("usersF").child(fuser.getUid()).child("mesaj_bildirim_durumu").setValue("yok");
    }

    @Override
    public void onRefresh() {
        refreshleniyorMu = true;

        mesajlarimItemKullaniciVeOdaList = new ArrayList<>();
        mesajlarimItemKullaniciVeOdaList2 = new ArrayList<>();
        mesajlarimItemOdaVeZamanList = new ArrayList<>();
        mOdalar = new ArrayList<>();
        mOdalar2 = new ArrayList<>();

        pbar.setVisibility(View.VISIBLE);
        recyclerView.setVisibility(View.GONE);

        mesajlarimCardView = new MesajlarimCardView(MesajlarimActivity.this, mesajlarimItemKullaniciVeOdaList,MesajlarimActivity.this);
        mesajlarimListView = new MesajlarimListView(MesajlarimActivity.this,mesajlarimItemKullaniciVeOdaList, MesajlarimActivity.this);
        if (gorunumDeger == 1) {
            recyclerView.setLayoutManager(new GridLayoutManager(getApplicationContext(), 3));
            recyclerView.setAdapter(mesajlarimCardView);
        } else {
            recyclerView.setLayoutManager(new LinearLayoutManager(MesajlarimActivity.this));
            recyclerView.setAdapter(mesajlarimListView);
        }
        swipeRefreshLayout.setRefreshing(true);
        KimlerleMesajlasmisim();
    }
}
