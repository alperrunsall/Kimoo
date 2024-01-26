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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.kimoo.android.MainActivity;
import com.kimoo.android.Model.User;
import com.kimoo.android.R;
import com.kimoo.android.TaraActivity;
import com.kimoo.android.extra.AltMenu2;
import com.kimoo.android.extra.FavoriAdapter;
import com.kimoo.android.extra.FavoriAdapter2;
import com.kimoo.android.extra.TasarimRenginiGetir;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class Favorilerim extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener {
    private static List<User> mUsers = new ArrayList<>(),aList = new ArrayList<>(),mUsers2 = new ArrayList<>();
    private static List<User> CinList,YasList,PremList;
    private static int ortaRenk;
    private List<String> aramaSirasindaKaldirilanlar = new ArrayList<>(),aramaSirasindaEkliOlanlar = new ArrayList<>(),  kullanicilar;
    static RecyclerView recyclerView;
    static TextView favoriYok;
    static ProgressBar pbar;
    static RelativeLayout ustKisim;
    static FirebaseUser fuser;
    Spinner spin2;
    static Context mContext;
    static Activity activity;
    //FloatingActionButton sirala,filtrele;
    private SwipeRefreshLayout swipeRefreshLayout;
    private static FavoriAdapter favoriAdapter;
    private static FavoriAdapter2 favoriAdapter2;
    private TextView ara;
    public static int spin2deger;
    private static long gorduklerimSayisi;
    static String aranacakDeger;
    ImageView kart,liste;
    static LinearLayout background;
    static int gorunumDeger,engelSayim;
    SharedPreferences sharedPref;
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
        setContentView(R.layout.fragment_favorilerim);
        activity = this;

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

        sharedPref = this.getPreferences(Context.MODE_PRIVATE);
        final SharedPreferences.Editor editor = sharedPref.edit();
        //favoriYok = view.findViewById(R.id.favorinizyok);
        fuser = FirebaseAuth.getInstance().getCurrentUser();
        pbar = findViewById(R.id.progress);

        recyclerView = findViewById(R.id.recylerView);
        recyclerView.setHasFixedSize(true);

        mContext = getApplicationContext();
        ara = findViewById(R.id.mesaj_ara_favoriler);
        //spin2 = findViewById(R.id.spinn2);
        kart = findViewById(R.id.kart_gorunumu);
        background = findViewById(R.id.background);
        liste = findViewById(R.id.liste_gorunumu);
        ustKisim = findViewById(R.id.ust_kisim);
        Toolbar toolbar = findViewById(R.id.toolbar);
        swipeRefreshLayout = findViewById(R.id.swipe_layout);

        SharedPreferences tas_shared = getSharedPreferences("TasarimBilgileri",MODE_PRIVATE);
        TasarimDegistir(tas_shared.getString("tasarim_arayuz","1"));

        swipeRefreshLayout.setOnRefreshListener(this);
        swipeRefreshLayout.setColorSchemeResources(android.R.color.black);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(getResources().getDrawable(R.drawable.ic_mesaj_geri2));
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Favorilerim.this,TaraActivity.class);
                startActivity(intent);
                finish();
                overridePendingTransition(R.anim.slide_in_left,R.anim.slide_out_right);
            }
        });

        /*filtrele.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AltMenu altMenu = new AltMenu(Favorilerim.this);
                View sheetView = getLayoutInflater().inflate(R.layout.filtre_menu, null);
                altMenu.setContentView(sheetView);
                altMenu.show();
            }
        });
        sirala.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AltMenu2 altMenu = new AltMenu2(Favorilerim.this);
                View sheetView = getLayoutInflater().inflate(R.layout.sirala_menu, null);
                altMenu.setContentView(sheetView);
                altMenu.show();
            }
        });*/
        liste.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (gorunumDeger != 0) {
                    liste.setColorFilter(ortaRenk);
                    kart.setColorFilter(getResources().getColor(R.color.gri2));
                    editor.putInt("gorunum_favori", 0);
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
                    editor.putInt("gorunum_favori", 1);
                    editor.commit();
                    liste.setColorFilter(getResources().getColor(R.color.gri2));
                    kart.setColorFilter(ortaRenk);

                    gorunumDeger = 1;

                    recyclerView.setLayoutManager(new GridLayoutManager(getAppContext(), 3));
                    recyclerView.setAdapter(favoriAdapter2);
                }
            }
        });
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
        /*spin2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                spin2deger = position;
                if(userAdapter != null)
                    userAdapter.notifyDataSetChanged();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });*/

        GorunumKontrol();

    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent tarayaDon = new Intent(Favorilerim.this,TaraActivity.class);
        startActivity(tarayaDon);
        finish();
        overridePendingTransition(R.anim.slide_in_left,R.anim.slide_out_right);
    }
    private void GorunumKontrol() {
        kullanicilar = new ArrayList<>();
        mUsers = new ArrayList<>();
        mUsers2 = new ArrayList<>();

        recyclerView.setVisibility(View.VISIBLE);
        ustKisim.setVisibility(View.VISIBLE);
        pbar.setVisibility(View.GONE);

        favoriAdapter = new FavoriAdapter(getAppContext(),activity, mUsers);
        favoriAdapter2 = new FavoriAdapter2(getAppContext(),activity, mUsers);

        if(sharedPref.getInt("gorunum_favori",0) == 0){
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
    public void TasarimDegistir(String tasDegeri) {
        GradientDrawable gradient = (GradientDrawable) activity.getResources().getDrawable(R.drawable.gradient);
        gradient.setOrientation(GradientDrawable.Orientation.LEFT_RIGHT);

        int renk1 = 0,renk2 = 0,t1start = 0,t2start = 0,t1end = 0,t2end = 0,orta = 0;

        renk1 = TasarimRenginiGetir.RengiGetir(activity,"renk1",tasDegeri);
        renk2 = TasarimRenginiGetir.RengiGetir(activity,"renk2",tasDegeri);
        t1start = TasarimRenginiGetir.RengiGetir(activity,"t1start",tasDegeri);
        t2start = TasarimRenginiGetir.RengiGetir(activity,"t2start",tasDegeri);
        t1end = TasarimRenginiGetir.RengiGetir(activity,"t1end",tasDegeri);
        t2end = TasarimRenginiGetir.RengiGetir(activity,"t2end",tasDegeri);
        orta = TasarimRenginiGetir.RengiGetir(activity,"orta",tasDegeri);

        gradient.setColors(new int[]{
                renk1,
                orta,
                renk2,
        });

        ortaRenk = orta;

        background.setBackground(gradient);
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
    @Override
    public void onResume() {
        super.onResume();

        SharedPreferences tas_shared = getSharedPreferences("TasarimBilgileri",MODE_PRIVATE);
        TasarimDegistir(tas_shared.getString("tasarim_arayuz","1"));
    }
    @Override
    public void onPause() {
        super.onPause();
    }
    public void BulunanlariGetir() {

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("usersF").child(fuser.getUid());
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.child("kisitli_erisim_engeli").child("durum").getValue().equals("var")) {
                    Intent intent = new Intent(getAppContext(), MainActivity.class);
                    getAppContext().startActivity(intent);
                }
                if(dataSnapshot.hasChild("favorilerim")){
                    gorduklerimSayisi = dataSnapshot.child("favorilerim").getChildrenCount();
                    for(final DataSnapshot ds : dataSnapshot.child("favorilerim").getChildren()){
                        DatabaseReference dref = FirebaseDatabase.getInstance().getReference("usersF").child(ds.getKey());
                        dref.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot2) {
                                if(dataSnapshot2.exists()) {
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
                                            }
                                        }
                                    }
                                }
                                else
                                    dataSnapshot.child("favorilerim").child(ds.getKey()).getRef().removeValue();

                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });


                    }

                    swipeRefreshLayout.setRefreshing(false);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        //pbar.setVisibility(View.GONE);
    }


    public static void CinFiltre(int secenekCin, int secenekPrem, int secenekYasCok, int secenekYasAz){
        CinList = new ArrayList<>();
        CinList.clear();
        String cin = "";
        if (secenekCin == 0)
            cin = "";
        else if (secenekCin == 1)
            cin = "Erkek";
        else if (secenekCin == 2)
            cin = "Kadın";

        if (mUsers.size() > 0) {
            if (secenekCin != 0) {
                for (int i = 0; i < mUsers.size(); i++) {
                    if (mUsers.get(i).getCinsiyet().equals(cin)) {
                        CinList.add(CinList.size(),mUsers.get(i));
                        YasFiltre(secenekPrem, secenekYasCok, secenekYasAz);
                    }else{
                        if(i == mUsers.size()-1 && CinList.size() == 0)
                            Toast.makeText(mContext, "Kimse bulunamadı.", Toast.LENGTH_SHORT).show();
                    }
                }
            }else{
                YasFiltre(secenekPrem, secenekYasCok, secenekYasAz);
            }
        }else{
            Toast.makeText(mContext, "Kimse bulunamadı.", Toast.LENGTH_SHORT).show();
        }
        //Toast.makeText(mContext, ""+AsilList.size() + " " + mUsers.size(), Toast.LENGTH_SHORT).show();

    }

    private static void YasFiltre(int secenekPrem, int secenekYasCok, int secenekYasAz) {
        YasList = new ArrayList<>();
        ArrayList<User> burdakiList = new ArrayList<>();
        if(CinList.size() > 0){
            for(int i = 0; i < CinList.size(); i++){
                burdakiList.add(i,CinList.get(i));
            }
        }else{
            for(int i = 0; i < mUsers.size(); i++){
                burdakiList.add(i,mUsers.get(i));
            }
        }
        if (burdakiList.size() > 0) {
            if (secenekYasAz != 0 || secenekYasCok != 0) {
                for(int i = 0; i < burdakiList.size(); i++){
                    if (secenekYasCok != 0) {
                        if (Integer.parseInt(burdakiList.get(i).getDg()) <= secenekYasCok) {
                            YasList.add(YasList.size(), burdakiList.get(i));
                            PremFiltre(secenekPrem);
                        }else{
                            if(i == burdakiList.size()-1 && YasList.size() == 0)
                                Toast.makeText(getAppContext(), "Kimse bulunamadı.", Toast.LENGTH_SHORT).show();
                        }
                    }
                    if (secenekYasAz != 0) {
                        if (Integer.parseInt(burdakiList.get(i).getDg()) >= secenekYasAz) {
                            YasList.add(YasList.size(), burdakiList.get(i));
                            PremFiltre(secenekPrem);
                        }else{
                            if(i == burdakiList.size()-1 && YasList.size() == 0)
                                Toast.makeText(getAppContext(), "Kimse bulunamadı.", Toast.LENGTH_SHORT).show();
                        }
                    }
                    if(secenekYasAz != 0 && secenekYasAz != 0){
                        if (Integer.parseInt(burdakiList.get(i).getDg()) > secenekYasAz && Integer.parseInt(burdakiList.get(i).getDg()) < secenekYasCok) {
                            YasList.add(YasList.size(), burdakiList.get(i));
                            PremFiltre(secenekPrem);
                        }else{
                            if(i == burdakiList.size()-1 && YasList.size() == 0)
                                Toast.makeText(getAppContext(), "Kimse bulunamadı.", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            }
            if(secenekYasAz == 0 && secenekYasCok == 0){
                PremFiltre(secenekPrem);
            }


        }else{
            PremFiltre(secenekPrem);
        }
    }

    private static void PremFiltre(int secenekPrem) {
        ArrayList<User> burdakiList = new ArrayList<>();
        if(YasList.size() > 0){
            for(int i = 0; i < YasList.size(); i++){
                burdakiList.add(i,YasList.get(i));
            }
        }else if(CinList.size() > 0){
            for(int i = 0; i < CinList.size(); i++){
                burdakiList.add(i,CinList.get(i));
            }
        }else{
            for(int i = 0; i < mUsers.size(); i++){
                burdakiList.add(i,mUsers.get(i));

            }
        }
        if (burdakiList.size() > 0) {
            if (secenekPrem != 0) {

            } else {
                if(burdakiList.size() <= mUsers.size()) {
                    Sirala(burdakiList);
                }
            }
        }
        else{
            Sirala(burdakiList);

        }
        HangiListe1();
    }

    static void Sirala(ArrayList<User> sonucList){
        for(int i = 0; i < sonucList.size(); i++){
            sonucList.get(i).getKarsilasma();
        }
        if(AltMenu2.adegAlff == 1){
            Collections.sort(sonucList, new Comparator<User>() {
                @Override
                public int compare(User o1, User o2) {
                    return o1.getAd().compareToIgnoreCase(o2.getAd());
                }
            });
        }else if(AltMenu2.adegAlff == 2){
            Collections.sort(sonucList, new Comparator<User>() {
                @Override
                public int compare(User o1, User o2) {
                    return o2.getAd().compareToIgnoreCase(o1.getAd());
                }
            });
        }
        if (AltMenu2.adegYasf == 1) {
            Collections.sort(sonucList, new Comparator<User>() {
                @Override
                public int compare(User o1, User o2) {
                    return Integer.parseInt(o1.getDg()) - Integer.parseInt(o2.getDg());
                }
            });
        } else if (AltMenu2.adegYasf == 2) {
            Collections.sort(sonucList, new Comparator<User>() {
                @Override
                public int compare(User o1, User o2) {
                    return Integer.parseInt(o2.getDg()) - Integer.parseInt(o1.getDg());
                }
            });
        }
        if (AltMenu2.adegKarf == 1) {
            Collections.sort(sonucList, new Comparator<User>() {
                @Override
                public int compare(User o1, User o2) {
                    return Integer.parseInt(o2.getKarsilasma()) - Integer.parseInt(o1.getKarsilasma());
                }
            });
        }
        if (AltMenu2.adegBegf == 1) {
            Collections.sort(sonucList, new Comparator<User>() {
                @Override
                public int compare(User o1, User o2) {
                    return Integer.parseInt(o1.getBegeni_sayisi()) - Integer.parseInt(o2.getBegeni_sayisi());
                }
            });
        } else if (AltMenu2.adegBegf == 2) {
            Collections.sort(sonucList, new Comparator<User>() {
                @Override
                public int compare(User o1, User o2) {
                    return Integer.parseInt(o2.getBegeni_sayisi()) - Integer.parseInt(o1.getBegeni_sayisi());
                }
            });
        }
    }
    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.slide_in_left,R.anim.slide_out_right);
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
    public static Context getAppContext(){
        return mContext;
    }

    @Override
    public void onRefresh() {
        swipeRefreshLayout.setRefreshing(true);
        GorunumKontrol();
    }
}
