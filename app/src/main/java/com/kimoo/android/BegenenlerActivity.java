package com.kimoo.android;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

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

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.kimoo.android.Model.User;
import com.kimoo.android.extra.FavoriAdapter;
import com.kimoo.android.extra.FavoriAdapter2;
import com.kimoo.android.extra.TasarimRenginiGetir;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class BegenenlerActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener{
    static String kiminmis;
    static EditText ara;
    static RecyclerView recyclerView;
    public static boolean begenilerde = false;
    ImageView kart,liste;
    TextView topBegSay,enCokBegSay;
    private static FavoriAdapter favoriAdapter;
    private static FavoriAdapter2 favoriAdapter2;
    static RelativeLayout ustKisim,encokbegenenkismi;
    static ProgressBar pbar;
    User enCokBegenen;
    static Context mContext;
    static Activity activity;
    private List<String> aramaSirasindaKaldirilanlar = new ArrayList<>(),aramaSirasindaEkliOlanlar = new ArrayList<>(),  kullanicilar;
    private static List<User> mUsers = new ArrayList<>(),mUsers2 = new ArrayList<>();
    int arananHarflerinSayisi;
    private DataSnapshot dataSnapshotAsil;
    private static List<User> CinList,YasList,PremList,araSonuc;
    static String aranacakDeger;
    private static long gorduklerimSayisi;
    SharedPreferences sharedPref;
    private static int gorunumDeger,engelSayim;
    ArrayList<String> begeniler,engellediklerim = new ArrayList<>();
    int begeniSayisi;
    LinearLayout background;
    private SwipeRefreshLayout swipeRefreshLayout;
    long bulunanDeger,childSayisi;
    CircleImageView encokbegenenresmi;
    public static FirebaseUser fuser;
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
        setContentView(R.layout.activity_begenenler);
        activity = this;
        TaraActivity.tarada = false;
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
        mContext = getApplicationContext();
        kiminmis = getIntent().getStringExtra("userid");

        recyclerView = findViewById(R.id.recylerView);
        ara = findViewById(R.id.mesaj_ara_begenenler);
        pbar = findViewById(R.id.progress);
        pbar.getIndeterminateDrawable().setColorFilter(ortaRenk,android.graphics.PorterDuff.Mode.MULTIPLY);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        ustKisim = findViewById(R.id.ust_kisim);
        encokbegenenkismi = findViewById(R.id.encokbegenenkismi);
        topBegSay = findViewById(R.id.toplambegenisayisi);
        enCokBegSay = findViewById(R.id.encokbegenensayisi);
        encokbegenenresmi = findViewById(R.id.encokbegenenpp);
        kart = findViewById(R.id.kart_gorunumu);
        liste = findViewById(R.id.liste_gorunumu);
        Toolbar toolbar = findViewById(R.id.toolbar);
        background = findViewById(R.id.background);
        swipeRefreshLayout = findViewById(R.id.swipe_layout);

        swipeRefreshLayout.setOnRefreshListener(this);
        swipeRefreshLayout.setColorSchemeResources(android.R.color.black);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(getResources().getDrawable(R.drawable.ic_mesaj_geri2));
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(BegenenlerActivity.this,ProfilActivity.class);
                startActivity(intent);
                finish();
                overridePendingTransition(R.anim.slide_in_right,R.anim.slide_out_left);
            }
        });

        SharedPreferences tas_shared = getSharedPreferences("TasarimBilgileri",MODE_PRIVATE);
        TasarimDegistir(tas_shared.getString("tasarim_arayuz","1"));

        fuser = FirebaseAuth.getInstance().getCurrentUser();
        SharedPreferences sharedPreferences = getSharedPreferences("gorunumler",Context.MODE_PRIVATE);
        final SharedPreferences.Editor editor = sharedPreferences.edit();

        liste.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (gorunumDeger != 0) {
                    liste.setColorFilter(ortaRenk);
                    kart.setColorFilter(getResources().getColor(R.color.gri2));
                    editor.putInt("gorunum_begenenler", 0);
                    editor.commit();

                    gorunumDeger = 0;

                    recyclerView.setLayoutManager(new LinearLayoutManager(getAppContext()));
                    recyclerView.setAdapter(favoriAdapter);
                }
            }
        });
        kart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (gorunumDeger != 1) {
                    editor.putInt("gorunum_begenenler", 1);
                    editor.commit();
                    liste.setColorFilter(getResources().getColor(R.color.gri2));
                    kart.setColorFilter(ortaRenk);

                    gorunumDeger = 1;

                    recyclerView.setLayoutManager(new GridLayoutManager(getAppContext(), 3));
                    recyclerView.setAdapter(favoriAdapter2);
                }
            }
        });
        /*filtrele.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AltMenu altMenu = new AltMenu(BegenenlerActivity.this);
                View sheetView = getLayoutInflater().inflate(R.layout.filtre_menu, null);
                altMenu.setContentView(sheetView);
                altMenu.show();
            }
        });
        sirala.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AltMenu2 altMenu = new AltMenu2(BegenenlerActivity.this);
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
        DatabaseReference sistemRef = FirebaseDatabase.getInstance().getReference("Sistem");
        sistemRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.child("versiyon_zorunlu").getValue(Integer.class) <= BuildConfig.VERSION_CODE) {
                    if (dataSnapshot.child("sistem_durumu").child("durum").getValue(Integer.class) == 0) {
                        BegeniHesapla();
                        BulunanlariGetir();
                        GorunumKontrol();
                    } else if (dataSnapshot.child("sistem_durumu").child("durum").getValue(Integer.class) == 1) {
                        startActivity(new Intent(BegenenlerActivity.this, MainActivity.class));
                    }
                }
                else{
                    Toast.makeText(BegenenlerActivity.this, "Eski bir versiyon kullanıyorsunuz, lütfen uygulamayı güncelleyiniz.", Toast.LENGTH_LONG).show();
                    finishAffinity();
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
        Intent tarayaDon = new Intent(BegenenlerActivity.this,ProfilActivity.class);
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

        favoriAdapter = new FavoriAdapter(getAppContext(), BegenenlerActivity.this, mUsers);
        favoriAdapter2 = new FavoriAdapter2(getAppContext(),BegenenlerActivity.this, mUsers);

        SharedPreferences sharedPreferences = getSharedPreferences("gorunumler",Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        if(sharedPreferences.getInt("gorunum_begenenler",0) == 0){
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

    private void mesajAra(final String s){
        if(mUsers2.size() > 0) {
            if (s.length() > 0) {
                for(int i = 0; i < mUsers2.size(); i++){
                    AdKontrol(s,mUsers2.get(i));
                }

            } else {
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

    private void BegeniHesapla() {
        begeniler = new ArrayList<>();
        final List<String> begenenler = new ArrayList<>();
        begeniSayisi = 0;
        bulunanDeger = 0;
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("usersF").child(fuser.getUid());
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.hasChild("begenenler")){
                    childSayisi = dataSnapshot.child("begenenler").getChildrenCount();
                    for(DataSnapshot ds : dataSnapshot.child("begenenler").getChildren()){
                        begenenler.add(ds.getKey());
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
                            topBegSay.setText(""+begeniSayisi);
                            bulunanDeger++;
                        }
                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
    private void EnCokKimBegenmis() {
        if(begeniler.size() > 1) {
            if (!begeniler.get(0).equals(begeniler.get(1))) {
                DatabaseReference ref = FirebaseDatabase.getInstance().getReference("usersF").child(fuser.getUid()).child("begenenler");
                ref.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot ds : dataSnapshot.getChildren()) {
                            if (String.valueOf(ds.child("kac_kez_begendim").getValue(Long.class)).equals(begeniler.get(0))) {
                                DatabaseReference aref = FirebaseDatabase.getInstance().getReference("usersF").child(ds.getKey());
                                aref.addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshotU) {
                                        if (dataSnapshotU.exists()) {
                                            enCokBegenen = dataSnapshotU.getValue(User.class);
                                            encokbegenenkismi.setVisibility(View.VISIBLE);
                                            enCokBegSay.setText("x" + begeniler.get(0));
                                            Glide.with(getApplicationContext())
                                                    .asBitmap()
                                                    .load(Uri.parse(dataSnapshotU.child("fotograflarim").child("pp").getValue(String.class)))
                                                    .into(encokbegenenresmi);
                                        }
                                        else
                                            dataSnapshot.child(ds.getKey()).getRef().removeValue();
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
                encokbegenenkismi.setVisibility(View.INVISIBLE);
            }
        }else{
            DatabaseReference ref = FirebaseDatabase.getInstance().getReference("usersF").child(fuser.getUid()).child("begenenler");
            ref.addValueEventListener(new ValueEventListener() {
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
                                    enCokBegSay.setText("x" + begeniler.get(0));
                                    Glide.with(getApplicationContext())
                                            .asBitmap()
                                            .load(Uri.parse(dataSnapshot.child("fotograflarim").child("pp").getValue(String.class)))
                                            .into(encokbegenenresmi);
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

    private void PPSilinmisMi(DataSnapshot dataSnapshot) {
        if (dataSnapshot.child("fotograflarim").child("pp").getValue(String.class).equals("")) {

            Dialog dialog = new Dialog(BegenenlerActivity.this);
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
                    Intent intent = new Intent(BegenenlerActivity.this, ProfilActivity.class);
                    intent.putExtra("foto_yukle","pp");
                    startActivity(intent);
                }
            });
            dialog.show();
        }
    }

    private void BulunanlariGetir() {
        FirebaseUser fuser = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("usersF").child(fuser.getUid());
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                PPSilinmisMi(dataSnapshot);
                if(dataSnapshot.child("kisitli_erisim_engeli").child("durum").getValue().equals("var")) {
                    Intent intent = new Intent(getAppContext(), MainActivity.class);
                    getAppContext().startActivity(intent);
                }
                if(dataSnapshot.hasChild("begenenler")){
                    gorduklerimSayisi = dataSnapshot.child("begenenler").getChildrenCount();
                    for(final DataSnapshot ds : dataSnapshot.child("begenenler").getChildren()){
                        DatabaseReference dref = FirebaseDatabase.getInstance().getReference("usersF").child(ds.getKey());
                        dref.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot2) {
                                if(dataSnapshot2.child("ban_durumu").child("durum").getValue().equals("yok")){
                                    if(dataSnapshot2.child("hesap_durumu").child("durum").getValue(Integer.class) == 0) {
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
                                        }
                                    }else{
                                        ustKisim.setVisibility(View.VISIBLE);
                                        pbar.setVisibility(View.GONE);
                                    }
                                }
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
    protected void onResume() {
        super.onResume();
        begenilerde = true;
        TaraActivity.tarada = false;

        SharedPreferences tas_shared = getSharedPreferences("TasarimBilgileri",MODE_PRIVATE);
        TasarimDegistir(tas_shared.getString("tasarim_arayuz","1"));
    }
    public void TasarimDegistir(String tasDegeri) {
        GradientDrawable gradient = (GradientDrawable) getResources().getDrawable(R.drawable.gradient);
        gradient.setOrientation(GradientDrawable.Orientation.LEFT_RIGHT);

        int renk1 = 0,renk2 = 0,t1start = 0,t2start = 0,t1end = 0,t2end = 0,orta = 0;

        renk1 = TasarimRenginiGetir.RengiGetir(BegenenlerActivity.this,"renk1",tasDegeri);
        renk2 = TasarimRenginiGetir.RengiGetir(BegenenlerActivity.this,"renk2",tasDegeri);
        t1start = TasarimRenginiGetir.RengiGetir(BegenenlerActivity.this,"t1start",tasDegeri);
        t2start = TasarimRenginiGetir.RengiGetir(BegenenlerActivity.this,"t2start",tasDegeri);
        t1end = TasarimRenginiGetir.RengiGetir(BegenenlerActivity.this,"t1end",tasDegeri);
        t2end = TasarimRenginiGetir.RengiGetir(BegenenlerActivity.this,"t2end",tasDegeri);
        orta = TasarimRenginiGetir.RengiGetir(BegenenlerActivity.this,"orta",tasDegeri);

        gradient.setColors(new int[]{
                renk1,
                orta,
                renk2,
        });

        ortaRenk = orta;
        background.setBackground(gradient);
    }

    @Override
    protected void onPause() {
        super.onPause();
        begenilerde = false;
    }
    public static Context getAppContext(){
        return mContext;
    }


    @Override
    public void onRefresh() {
        swipeRefreshLayout.setRefreshing(true);
        GorunumKontrol();
    }
}
