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
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.GradientDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Build;
import android.os.Bundle;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;

import android.text.Editable;
import android.text.TextWatcher;
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
import com.kimoo.android.Model.Chat;
import com.kimoo.android.Model.Fiyatlar;
import com.kimoo.android.Model.User;
import com.kimoo.android.bildirimler.APIService;
import com.kimoo.android.bildirimler.Client;
import com.kimoo.android.bildirimler.Data;
import com.kimoo.android.bildirimler.MyResponse;
import com.kimoo.android.bildirimler.Sender;
import com.kimoo.android.bildirimler.Token;
import com.kimoo.android.extra.MesajAdapter;
import com.kimoo.android.extra.ResimIndir;
import com.kimoo.android.extra.TasarimRenginiGetir;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MesajActivity extends AppCompatActivity {

    CircleImageView ppMesaj;
    TextView ensonyazisma;
    TextView mesajyok, gorulduMu, baslik,iade_yazi;
    String sebep = "";
    EditText mesaj;
    FirebaseUser fuser;
    private static Fiyatlar fiyatlar;
    private int ortaRenk;
    private Button iade;
    private DatabaseReference reference;
    private RelativeLayout profileGonder,kimeyaziyor,ustRel;
    private View gonderBTN2;
    private LinearLayout bottomLayout,iade_kismi,mesaj_arkasi,background,gonderBTN;
    private ProgressBar pbar, pbar_pp;
    private MesajAdapter mesajAdapter;
    private List<Chat> chats,degisiktarihler,gormekistemiyor,toplamchat;
    private List<String> tarihler,refisimleri, gormekIstemedigimMesajlar;
    private String userid,kullaniciAdi;
    private RecyclerView recyclerView;
    private Toolbar toolbar;
    int bastakiMesajSayisi, suankiMesajSayisi;
    public SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yy");
    public String dateString;
    public static String ozelOda;
    Intent intent;
    private Rect rect;
    APIService apiService;
    private boolean notify, adminMi;
    public static GradientDrawable sagMesajGradient, solMesajGradient;

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent tarayaDon = new Intent(MesajActivity.this,MesajlarimActivity.class);
        startActivity(tarayaDon);
        finish();
        overridePendingTransition(R.anim.slide_in_right,R.anim.slide_out_left);

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
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mesaj);
        if (Build.VERSION.SDK_INT >= 19 && Build.VERSION.SDK_INT < 21) {
            setWindowFlag(this, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, true);
        }
        if (Build.VERSION.SDK_INT >= 19) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        }

        if (Build.VERSION.SDK_INT >= 21) {
            setWindowFlag(this, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, false);
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        }
        intent = getIntent();
        userid = intent.getStringExtra("userid");
        DatabaseReference yazanVarmiRef = FirebaseDatabase.getInstance().getReference("usersF").child(userid);
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Sistem");
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                fiyatlar = dataSnapshot.child("fiyatlar").getValue(Fiyatlar.class);
                if (dataSnapshot.child("yoneticiler").hasChild(userid)){
                    adminMi = true;
                }
                yazanVarmiRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        User us = dataSnapshot.getValue(User.class);

                        ppMesaj.setVisibility(View.VISIBLE);
                        pbar_pp.setVisibility(View.GONE);

                        if(us.getKime_yaziyor().equals(fuser.getUid())){
                            kimeyaziyor.setVisibility(View.VISIBLE);
                        }else{
                            kimeyaziyor.setVisibility(View.GONE);
                        }

                        if(!adminMi) {
                            if (us.getGizlilik_ad().equals("0")) {
                                if (dataSnapshot.hasChild("begendiklerim")) {
                                    if (dataSnapshot.child("begendiklerim").hasChild(fuser.getUid())) {
                                        baslik.setText(us.getAd().substring(0, 1).toUpperCase() + us.getAd().substring(1));
                                    } else {
                                        baslik.setText(AdiSansurle(us));
                                    }
                                } else {
                                    baslik.setText(AdiSansurle(us));
                                }
                            }
                            else if (us.getGizlilik_ad().equals("1")) {
                                if (dataSnapshot.hasChild("begenenler")) {
                                    if (dataSnapshot.child("begenenler").hasChild(fuser.getUid())) {
                                        baslik.setText(us.getAd().substring(0, 1).toUpperCase() + us.getAd().substring(1));
                                    } else {
                                        baslik.setText(AdiSansurle(us));
                                    }
                                } else {
                                    baslik.setText(AdiSansurle(us));
                                }
                            }
                            else if (us.getGizlilik_ad().equals("2")) {
                                if (dataSnapshot.hasChild("mesajlastiklarim")) {
                                    if (dataSnapshot.child("mesajlastiklarim").hasChild(fuser.getUid())) {
                                        baslik.setText(us.getAd().substring(0, 1).toUpperCase() + us.getAd().substring(1));
                                    } else {
                                        baslik.setText(AdiSansurle(us));
                                    }
                                } else {
                                    baslik.setText(AdiSansurle(us));
                                }
                            }
                            else if (us.getGizlilik_ad().equals("3")) {
                                baslik.setText(AdiSansurle(us));
                            }
                            else if (us.getGizlilik_ad().equals("4")) {
                                baslik.setText(us.getAd().substring(0, 1).toUpperCase() + us.getAd().substring(1));
                            }
                        }
                        else{
                            baslik.setText("KİMOO YÖNETİCİSİ");
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
                ContextWrapper cw = new ContextWrapper(MesajActivity.this);

                File directory = cw.getDir("kullanici_resimleri", MODE_PRIVATE);
                File imagepp = null;
                boolean fotoYuklendiMi = false;

                for(File files : directory.listFiles()){
                    if(files.getName().substring(7,files.getName().length()-4).equals(userid)){
                        imagepp = files;
                        fotoYuklendiMi = true;
                        ppMesaj.setImageURI(Uri.parse(imagepp.getAbsolutePath()));
                        //Toast.makeText(mContext, "Foto var", Toast.LENGTH_SHORT).show();
                    }
                    //if(dosyaKontrol == directory.listFiles().length)
                }
                File finalImagepp = imagepp;
                boolean finalFotoYuklendiMi = fotoYuklendiMi;
                if (!adminMi) {
                    yazanVarmiRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            String url = dataSnapshot.child("fotograflarim").child("pp").getValue(String.class);
                            if (!finalFotoYuklendiMi) {
                                Glide.with(MesajActivity.this).asBitmap().load(url).into(ppMesaj);
                                new ResimIndir(MesajActivity.this, url, "kullanici_resimleri", "pp" + url.substring(url.length() - 9, url.length() - 4) + userid + ".jpg");
                            } else {
                                if (!finalImagepp.getName().substring(2, 7).equals(url.substring(url.length() - 9, url.length() - 4))) {
                                    finalImagepp.delete();
                                    Glide.with(MesajActivity.this)
                                            .asBitmap()
                                            .load(url)
                                            .into(ppMesaj);
                                    new ResimIndir(MesajActivity.this, url, "kullanici_resimleri", "pp" + url.substring(url.length() - 9, url.length() - 4) + userid + ".jpg");
                                    //Toast.makeText(mContext, finalImagepp.getName().substring(0, 5) + "\n" + fotoUrl.toString().substring(fotoUrl.toString().length() - 9, fotoUrl.toString().length() - 4), Toast.LENGTH_LONG).show();
                                }
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                }
                else{
                    ppMesaj.setImageDrawable(getResources().getDrawable(R.drawable.kimoo_logo_beyaz));
                }
                OdaAnahtariniGetir();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setOverflowIcon(getResources().getDrawable(R.drawable.ic_dots));
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent tarayaDon = new Intent(MesajActivity.this,MesajlarimActivity.class);
                startActivity(tarayaDon);
                finish();
                overridePendingTransition(R.anim.slide_in_right,R.anim.slide_out_left);
            }
        });
        mesaj_arkasi = findViewById(R.id.mesaj_arkasi);
        mesajyok = findViewById(R.id.mesaj_mesajyok);
        pbar = findViewById(R.id.pbar);
        baslik = findViewById(R.id.baslik);
        pbar_pp = findViewById(R.id.pbar_pp);
        ustRel = findViewById(R.id.ustRel);
        bottomLayout = findViewById(R.id.bottomlayout_mesaj);
        recyclerView = findViewById(R.id.recycler_mesaj);
        iade_kismi = findViewById(R.id.iade_kismi);
        iade = findViewById(R.id.iade);
        iade_yazi = findViewById(R.id.iade_yazi);
        background = findViewById(R.id.background);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);
        gorulduMu = findViewById(R.id.gorulduMu);

        apiService = Client.getClient("https://fcm.googleapis.com/").create(APIService.class);
        bottomLayout.setVisibility(View.VISIBLE);
        ppMesaj = findViewById(R.id.pp_mesaj);
        gonderBTN = findViewById(R.id.gonderBTN_mesaj);
        gonderBTN2 = findViewById(R.id.gonderBTN_mesaj2);
        mesaj = findViewById(R.id.mesaj_mesaj);
        kimeyaziyor = findViewById(R.id.yaziyormu);
        //profileGonder = findViewById(R.id.profile_gonder);
        fuser = FirebaseAuth.getInstance().getCurrentUser();

        SharedPreferences tas_shared = getSharedPreferences("TasarimBilgileri",MODE_PRIVATE);
        TasarimDegistir(tas_shared.getString("tasarim_arayuz","1"));

        currentUserId(userid);
        /*profileGonder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent profileGit = new Intent(MesajActivity.this,DigerProfilActivity.class);
                profileGit.putExtra("userid",userid);
                startActivity(profileGit);
            }
        });*/

        /*DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);

        RelativeLayout.LayoutParams bottomParams = (RelativeLayout.LayoutParams) bottomLayout.getLayoutParams();
        Toast.makeText(MesajActivity.this, ""+displayMetrics.heightPixels + " " + bottomParams.height, Toast.LENGTH_SHORT).show();
        bottomParams.height = displayMetrics.heightPixels/10;*/

        //updateToken(FirebaseInstanceId.getInstance().getToken());
        ppMesaj.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MesajActivity.this,DigerProfilActivity.class);
                intent.putExtra("userid",userid);
                startActivity(intent);
            }
        });
        gonderBTN2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(internetVarmi()) {
                    String msj = mesaj.getText().toString().replace('\n', ' ').trim();
                    if (!msj.equals("")) {
                        if (!ozelOda.equals(""))
                            mesajAt(fuser.getUid(), userid, msj);
                        else
                            OdaAnahtariniGetir();
                    }
                    mesaj.setText("");
                }else{
                    Toast.makeText(MesajActivity.this, "İnternet bağlantınız yok.", Toast.LENGTH_SHORT).show();
                }
            }
        });
        gonderBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(internetVarmi()) {
                    String msj = mesaj.getText().toString().replace('\n', ' ').trim();
                    if (!msj.equals("")) {
                        if (!ozelOda.equals(""))
                            mesajAt(fuser.getUid(), userid, msj);
                        else
                            OdaAnahtariniGetir();
                    }
                    mesaj.setText("");
                }else{
                    Toast.makeText(MesajActivity.this, "İnternet bağlantınız yok.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        mesaj.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.toString().trim().length() == 1) {
                    mesajYaziyor(userid);
                } else if (s.toString().trim().length() == 0) {
                    mesajYaziyor("000");
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }

        });

        getSupportActionBar().setHomeAsUpIndicator(getResources().getDrawable(R.drawable.ic_mesaj_geri2));

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("usersF").child(fuser.getUid());
        ref.child("kime_cevrimici").setValue(userid);
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.child("kisitli_erisim_engeli").child("durum").getValue().equals("var")) {
                    Intent intent = new Intent(MesajActivity.this, MainActivity.class);
                    startActivity(intent);
                }
                kullaniciAdi = dataSnapshot.child("usernamef").getValue(String.class);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }

    private void OdaAnahtariniGetir() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("usersF").child(fuser.getUid());
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                ozelOda = dataSnapshot.child("kilidini_actiklarim").child(userid).child("oda").getValue(String.class);
                readMessages();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void TasarimDegistir(String tasDegeri) {
        ImageView ikon = findViewById(R.id.ikon);
        //ikon.setColorFilter(getResources().getColor(R.color.beyaz));

        GradientDrawable gradient = (GradientDrawable) getResources().getDrawable(R.drawable.gradient);
        GradientDrawable gradient2 = new GradientDrawable();
        GradientDrawable mesajSol = (GradientDrawable) getResources().getDrawable(R.drawable.mesaj_sol);
        GradientDrawable mesajSag = (GradientDrawable) getResources().getDrawable(R.drawable.mesaj_sag);

        gradient.setOrientation(GradientDrawable.Orientation.LEFT_RIGHT);
        gradient2.setOrientation(GradientDrawable.Orientation.LEFT_RIGHT);

        GradientDrawable yaziyorArkasi = new GradientDrawable();
        yaziyorArkasi.setCornerRadii(new float[]{40,40,40,40,0,0,0,0});
        yaziyorArkasi.setOrientation(GradientDrawable.Orientation.LEFT_RIGHT);

        GradientDrawable gradientYumusalk =  (GradientDrawable) getResources().getDrawable(R.drawable.gradient_yumusak_her_yer);

        int renk1 = 0,renk2 = 0,t1start = 0,t2start = 0,t1end = 0,t2end = 0,orta = 0;

        renk1 = TasarimRenginiGetir.RengiGetir(MesajActivity.this,"renk1",tasDegeri);
        renk2 = TasarimRenginiGetir.RengiGetir(MesajActivity.this,"renk2",tasDegeri);
        t1start = TasarimRenginiGetir.RengiGetir(MesajActivity.this,"t1start",tasDegeri);
        t2start = TasarimRenginiGetir.RengiGetir(MesajActivity.this,"t2start",tasDegeri);
        t1end = TasarimRenginiGetir.RengiGetir(MesajActivity.this,"t1end",tasDegeri);
        t2end = TasarimRenginiGetir.RengiGetir(MesajActivity.this,"t2end",tasDegeri);
        orta = TasarimRenginiGetir.RengiGetir(MesajActivity.this,"orta",tasDegeri);

        GradientDrawable kapakEkle = (GradientDrawable) getResources().getDrawable(R.drawable.kapak_ekle);
        kapakEkle.setStroke(1,orta);
        GradientDrawable butonArka = (GradientDrawable) getResources().getDrawable(R.drawable.buton_arka_et_gibi);
        butonArka.setStroke(1,orta);

        gradientYumusalk.setColors(new int[]{
                renk1,
                orta,
                renk2
        });
        ortaRenk = orta;
        /*mesajSol.setColors(new int[]{
                renk1,
                orta,
                renk2
        });
        mesajSag.setColors(new int[]{
                renk1,
                orta,
                renk2
        });*/
        gradient2.setColors(new int[]{
                renk1,
                orta,
                renk2,
        });
        yaziyorArkasi.setColors(new int[]{
                t1end,
                orta,
                t2start
        });
        gradient.setColors(new int[]{
                renk1,
                orta,
                renk2,
        });

        solMesajGradient = mesajSol;
        sagMesajGradient = mesajSag;

        // Başka layouttan textview çekme
        /*LayoutInflater inflater = LayoutInflater.from(MesajActivity.this);
        View sag = inflater.inflate(R.layout.mesaj_item_sag, null);
        View sol = inflater.inflate(R.layout.mesaj_item_sol, null);
        View sol = inflater.inflate(R.layout.mesaj_item_sol, null);

        TextView solTV, sagTV;
        solTV = sol.findViewById(R.id.mesaj_goster);
        sagTV = sag.findViewById(R.id.mesaj_goster);

        solTV.setBackground(mesajSol);
        sagTV.setBackground(mesajSag);*/
        ustRel.setBackground(gradient2);
        kimeyaziyor.setBackground(yaziyorArkasi);
        background.setBackground(gradient);
        mesaj_arkasi.setBackground(gradientYumusalk);
    }
    private void currentUserId(String Id){
        SharedPreferences sp = getSharedPreferences("SP_USER",MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString("Current_USERID",Id);
        editor.apply();
    }

    private boolean internetVarmi(){
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED)
            return true;
        else {
            Toast.makeText(this, "İnternet bağlantınız yok.", Toast.LENGTH_SHORT).show();
            return false;
        }
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

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.slide_in_right,R.anim.slide_out_left);
    }
    public static String onikiliksistem() {
        Date date = new Date();
        String strDateFormat = "hh:mm a";
        DateFormat dateFormat = new SimpleDateFormat(strDateFormat);
        String formattedDate= dateFormat.format(date);
        return formattedDate;
    }
    public static String yirmidortlukluksistem() {
        Calendar cal = Calendar.getInstance();
        Date date=cal.getTime();
        DateFormat dateFormat = new SimpleDateFormat("HH:mm");
        String formattedDate=dateFormat.format(date);
        return formattedDate;
    }
    private void updateToken(String token){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Tokenlar");
        Token token1 = new Token(token);
        reference.child(fuser.getUid()).setValue(token1);
    }
    public void MesajdanGeri(View view){
        Intent geri = new Intent(MesajActivity.this,MesajlarimActivity.class);
        startActivity(geri);
    }
    private void mesajYaziyor(String olay){
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("usersF").child(fuser.getUid());
        ref.child("kime_yaziyor").setValue(olay);
    }
    private void mesajAt(final String gonderici, final String alici, final String mesajim){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Mesajlar").child(ozelOda);
        notify = true;

        String key = reference.push().getKey();
        final HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("gonderici",gonderici);
        hashMap.put("alici",alici);
        hashMap.put("mesajim",mesajim);
        hashMap.put("goruldumu",false);
        hashMap.put("gormek_istemeyen1","");
        hashMap.put("gormek_istemeyen2","");
        hashMap.put("zaman", ServerValue.TIMESTAMP);
        hashMap.put("durum", "silinmedi");
        reference.child(key).setValue(hashMap);
        //reference.child("taraf1").setValue(gonderici);
        //reference.child("taraf2").setValue(alici);
        reference.child("son_mesaj").setValue(key);



        final String msg = mesajim;
        final DatabaseReference digerRef = FirebaseDatabase.getInstance().getReference("usersF").child(userid);
        reference = FirebaseDatabase.getInstance().getReference("usersF").child(fuser.getUid());
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                final User user = dataSnapshot.getValue(User.class);

                if(dataSnapshot.child("kilidini_actiklarim").hasChild(userid))
                    if(dataSnapshot.child("kilidini_actiklarim").child(userid).hasChild("mesaj"))
                        if(dataSnapshot.child("kilidini_actiklarim").child(userid).child("mesaj").getValue(String.class).equals("yok"))
                            dataSnapshot.child("kilidini_actiklarim").child(userid).child("mesaj").getRef().setValue("var");

                if(notify) {
                    digerRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if(dataSnapshot.child("kilidini_actiklarim").hasChild(fuser.getUid())) {
                                if (dataSnapshot.child("kilidini_actiklarim").child(fuser.getUid()).child("mesaj").getValue(String.class).equals("yok"))
                                    dataSnapshot.child("kilidini_actiklarim").child(fuser.getUid()).child("mesaj").getRef().setValue("var");

                                if (dataSnapshot.child("bildirimler").child("mesaj").getValue().equals("gelsin")) {
                                    sendNotification(alici, user.getAd(), msg);
                                    notify = false;
                                }
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                    FirebaseDatabase.getInstance().getReference("usersF").child(userid).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if (!dataSnapshot.child("kime_cevrimici").getValue(String.class).equals(fuser.getUid()))
                                dataSnapshot.child("mesaj_bildirim_durumu").getRef().setValue("var");
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void sendNotification(final String kiminmis, final String ad, final String s) {
        DatabaseReference tokens = FirebaseDatabase.getInstance().getReference("Tokenlar");
        Query query = tokens.orderByKey().equalTo(kiminmis);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                    Token token = snapshot.getValue(Token.class);
                    Data data = new Data(fuser.getUid(), "Mesajınızı görüntülemek için tıklayın.", "Yeni bir mesajınız var!",kiminmis,R.drawable.kimoo_bildirim);

                    Sender sender = new Sender(data,token.getToken());
                    apiService.sendNotification(sender).enqueue(new Callback<MyResponse>() {
                        @Override
                        public void onResponse(Call<MyResponse> call, Response<MyResponse> response) {
                            if(response.code() == 200){
                                if(response.body().success != 1){
                                    //Toast.makeText(MesajActivity.this, "Failed!", Toast.LENGTH_SHORT).show();
                                }
                            }
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

    private void readMessages(){
        final int[] karsiTarafinAttigiMesajSayisi = {0};
        final int[] benimAttigimMesajSayisi = {0};
        int bendenSilinenler = 0;

        chats = new ArrayList<>();
        degisiktarihler = new ArrayList<>();
        tarihler = new ArrayList<>();
        refisimleri = new ArrayList<>();
        gormekIstemedigimMesajlar = new ArrayList<>();
        List<String> childlar = new ArrayList<>();

        mesajAdapter = new MesajAdapter(getApplicationContext(),MesajActivity.this,chats,degisiktarihler,refisimleri);
        recyclerView.setAdapter(mesajAdapter);

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Mesajlar").child(ozelOda);

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                int kontrolSayisi = 0;
                //Chat chat = dataSnapshot.child(dataSnapshot.child("son_mesaj").getValue(String.class)).getValue(Chat.class);
                for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                    if (snapshot.getChildrenCount() > 0) {
                        Chat chat = snapshot.getValue(Chat.class);
                        if (!chat.getGormek_istemeyen1().equals(fuser.getUid()) && !chat.getGormek_istemeyen2().equals(fuser.getUid())) {
                            dateString = formatter.format(new Date(Long.parseLong(String.valueOf(chat.getZaman()))));
                            if (!tarihler.contains(dateString)) {
                                tarihler.add(dateString);
                                degisiktarihler.add(chat);
                            }
                            if (chat.getAlici().equals(fuser.getUid()))
                                karsiTarafinAttigiMesajSayisi[0]++;
                            else
                                benimAttigimMesajSayisi[0]++;


                            if (karsiTarafinAttigiMesajSayisi[0] > 0 && benimAttigimMesajSayisi[0] > 0) {
                                DatabaseReference ref = FirebaseDatabase.getInstance().getReference("usersF").child(chat.getAlici());
                                ref.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        if (dataSnapshot.hasChild("mesajlastiklarim")) {
                                            if (!dataSnapshot.child("mesajlastiklarim").hasChild(chat.getGonderici())) {
                                                dataSnapshot.child("mesajlastiklarim").child(chat.getGonderici()).getRef().setValue(0);
                                            }
                                        } else {
                                            dataSnapshot.child("mesajlastiklarim").child(chat.getGonderici()).getRef().setValue(0);
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                    }
                                });
                                DatabaseReference ref2 = FirebaseDatabase.getInstance().getReference("usersF").child(chat.getGonderici());
                                ref2.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        if (dataSnapshot.hasChild("mesajlastiklarim")) {
                                            if (!dataSnapshot.child("mesajlastiklarim").hasChild(chat.getAlici())) {
                                                dataSnapshot.child("mesajlastiklarim").child(chat.getAlici()).getRef().setValue(0);
                                            }
                                        } else {
                                            dataSnapshot.child("mesajlastiklarim").child(chat.getAlici()).getRef().setValue(0);
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                    }
                                });
                            }

                            if(chat.getDurum().equals("silinmedi")) {
                                if (!refisimleri.contains(snapshot.getKey())) {
                                    refisimleri.add(snapshot.getKey());
                                    chats.add(chat);
                                    //mesajAdapter.notifyItemChanged(chats.size()-2);
                                    mesajAdapter.notifyItemInserted(chats.size());
                                    //mesajAdapter.notifyDataSetChanged();
                                    recyclerView.smoothScrollToPosition(chats.size() - 1);
                                }
                                else{
                                    // mesajAdapter.notifyItemChanged(refisimleri.indexOf(snapshot.getKey()));
                                }
                            }
                            else{
                                    /*if (refisimleri.contains(snapshot.getKey())) {
                                        chats.get(refisimleri.indexOf(snapshot.getKey())).setDurum("silindi");
                                        mesajAdapter.notifyItemChanged(refisimleri.indexOf(snapshot.getKey()));
                                    }*/
                                if (!refisimleri.contains(snapshot.getKey())) {
                                    refisimleri.add(snapshot.getKey());
                                    chats.add(chat);
                                    //mesajAdapter.notifyItemChanged(chats.size()-2);
                                    mesajAdapter.notifyItemInserted(chats.size());
                                    //mesajAdapter.notifyDataSetChanged();
                                    recyclerView.smoothScrollToPosition(chats.size() - 1);
                                }else{
                                    if(chats.get(refisimleri.indexOf(snapshot.getKey())).getDurum().equals("silinmedi")) {
                                        chats.get(refisimleri.indexOf(snapshot.getKey())).setDurum("silindi");
                                        mesajAdapter.notifyItemChanged(refisimleri.indexOf(snapshot.getKey()));
                                    }
                                }
                            }
                            kontrolSayisi++;
                        }
                        else {
                            if(chat.getGonderici().equals(fuser.getUid()))
                                benimAttigimMesajSayisi[0]++;
                            else
                                karsiTarafinAttigiMesajSayisi[0]++;

                            if(!gormekIstemedigimMesajlar.contains(snapshot.getKey()))
                                gormekIstemedigimMesajlar.add(snapshot.getKey());

                            if (refisimleri.contains(snapshot.getKey())) {
                                chats.remove(refisimleri.indexOf(snapshot.getKey()));
                                mesajAdapter.notifyItemRemoved(refisimleri.indexOf(snapshot.getKey()));
                                refisimleri.remove(refisimleri.indexOf(snapshot.getKey()));
                                for(int i = 0; i < chats.size(); i ++){
                                    mesajAdapter.notifyItemChanged(i);
                                }
                            }
                            kontrolSayisi++;
                        }
                        if(kontrolSayisi == (dataSnapshot.getChildrenCount()-1)) {
                            if(fiyatlar != null) {
                                if (benimAttigimMesajSayisi[0] == 0 || karsiTarafinAttigiMesajSayisi[0] == 0) {
                                    if (benimAttigimMesajSayisi[0] != 0)
                                        iade_yazi.setText("Bu kullanıcıya mesaj atabilmek için kilidi açtınız. Bu kişi size cevap verene kadar kilit açma işlemini iptal edebilirsiniz. Ancak sadece 250KP iade edilir.");
                                    else
                                        iade_yazi.setText("Bu kullanıcıya cevap verebilmek için için kilidi açtınız. Bu kişiye cevap vermek istemezseniz kilit açma işlemini iptal edebilirsiniz. Mesajı okuduğunuzu görmeyecek.  250KP iade edilir.");
                                    if (!adminMi)
                                    iade_kismi.setVisibility(View.VISIBLE);
                                    iade.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            DatabaseReference ref = FirebaseDatabase.getInstance().getReference("usersF").child(fuser.getUid());
                                            ref.addListenerForSingleValueEvent(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(@NonNull DataSnapshot fusSnap) {
                                                    User Kullanici = fusSnap.getValue(User.class);
                                                    Kullanici.setKp(Kullanici.getKp() + fiyatlar.getKilit_acma() / 2);
                                                    fusSnap.child("kp").getRef().setValue(Kullanici.getKp()).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                        @Override
                                                        public void onSuccess(@NonNull Void unused) {
                                                            if (benimAttigimMesajSayisi[0] != 0) {
                                                                DatabaseReference reference1 = FirebaseDatabase.getInstance().getReference("usersF").child(chat.getAlici()).child("kilidini_actiklarim").child(fuser.getUid());
                                                                reference1.removeValue();
                                                                DatabaseReference reference2 = FirebaseDatabase.getInstance().getReference("Mesajlar").child(fusSnap.child("kilidini_actiklarim").child(chat.getAlici()).child("oda").getValue(String.class));
                                                                reference2.removeValue();
                                                                fusSnap.child("kilidini_actiklarim").child(chat.getAlici()).getRef().removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                    @Override
                                                                    public void onSuccess(@NonNull Void unused) {
                                                                        Toast.makeText(MesajActivity.this, "Bu kişiyi tekrar kilitlediniz!", Toast.LENGTH_SHORT).show();
                                                                        startActivity(new Intent(MesajActivity.this, MesajlarimActivity.class));
                                                                    /*snapshot.getRef().removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                        @Override
                                                                        public void onSuccess(@NonNull Void unused) {
                                                                        }
                                                                    });*/
                                                                    }
                                                                });
                                                                fusSnap.child("harcamalarim").child("iade_" + chat.getAlici()).getRef().setValue(ServerValue.TIMESTAMP);
                                                            } else {
                                                                fusSnap.child("kilidini_actiklarim").child(chat.getGonderici()).getRef().child("durum").setValue("yok").addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                    @Override
                                                                    public void onSuccess(@NonNull Void unused) {
                                                                        Toast.makeText(MesajActivity.this, "Bu kişiyi tekrar kilitlediniz!", Toast.LENGTH_SHORT).show();
                                                                        startActivity(new Intent(MesajActivity.this, MesajlarimActivity.class));
                                                                    /*snapshot.getRef().removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                        @Override
                                                                        public void onSuccess(@NonNull Void unused) {
                                                                        }
                                                                    });*/
                                                                    }
                                                                });
                                                                fusSnap.child("harcamalarim").child("iade_" + chat.getGonderici()).getRef().setValue(ServerValue.TIMESTAMP);
                                                            }

                                                        }
                                                    });
                                                }

                                                @Override
                                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                                }
                                            });
                                        }
                                    });
                                }
                                else if (benimAttigimMesajSayisi[0] != 0 && karsiTarafinAttigiMesajSayisi[0] != 0) {
                                    iade_kismi.setVisibility(View.GONE);

                                    if (!adminMi) {
                                        if (dataSnapshot.hasChild("son_mesaj")) {
                                            Chat sonChat = dataSnapshot.child(dataSnapshot.child("son_mesaj").getValue(String.class)).getValue(Chat.class);
                                            if (sonChat.getGonderici().equals(fuser.getUid())) {
                                                if (karsiTarafinAttigiMesajSayisi[0] > 0) {
                                                    gorulduMu.setVisibility(View.VISIBLE);
                                                    if (sonChat.isGoruldumu())
                                                        gorulduMu.setText("Görüldü");
                                                    else
                                                        gorulduMu.setText("Görülmedi");
                                                }
                                            } else
                                                gorulduMu.setVisibility(View.INVISIBLE);
                                        } else {
                                            gorulduMu.setVisibility(View.INVISIBLE);
                                        }
                                    }
                                    else
                                        gorulduMu.setVisibility(View.INVISIBLE);
                                }
                            }
                        }

                        if (chats.size() + gormekIstemedigimMesajlar.size() == dataSnapshot.getChildrenCount()-3){
                            if(chats.size() > 0){
                                mesajyok.setVisibility(View.GONE);
                                recyclerView.setVisibility(View.VISIBLE);
                            }else{
                                mesajyok.setVisibility(View.VISIBLE);
                                recyclerView.setVisibility(View.INVISIBLE);
                            }
                        }

                        //recyclerView.smoothScrollToPosition(chats.size()-1);

                        if(chats.size() > 0){
                            mesajyok.setVisibility(View.GONE);
                            recyclerView.setVisibility(View.VISIBLE);
                        }else{
                            mesajyok.setVisibility(View.VISIBLE);
                            recyclerView.setVisibility(View.INVISIBLE);
                        }

                    }
                }
                if (!dataSnapshot.exists()){
                    iade_yazi.setText("Bu kullanıcıya mesaj atabilmek için kilidi açtınız. Bu kişi size cevap verene kadar kilit açma işlemini iptal edebilirsiniz. Ancak sadece 250KP iade edilir.");
                    if (!adminMi)
                        iade_kismi.setVisibility(View.VISIBLE);
                    iade.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            DatabaseReference ref = FirebaseDatabase.getInstance().getReference("usersF").child(fuser.getUid());
                            ref.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot fusSnap) {
                                    User Kullanici = fusSnap.getValue(User.class);
                                    Kullanici.setKp(Kullanici.getKp() + fiyatlar.getKilit_acma() / 2);
                                    fusSnap.child("kp").getRef().setValue(Kullanici.getKp()).addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(@NonNull Void unused) {
                                            DatabaseReference reference1 = FirebaseDatabase.getInstance().getReference("usersF").child(userid).child("kilidini_actiklarim").child(fuser.getUid());
                                            reference1.removeValue();

                                            DatabaseReference reference2 = FirebaseDatabase.getInstance().getReference("Mesajlar").child(fusSnap.child("kilidini_actiklarim").child(userid).child("oda").getValue(String.class));
                                            reference2.removeValue();

                                            fusSnap.child("kilidini_actiklarim").child(userid).getRef().removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(@NonNull Void unused) {
                                                    Toast.makeText(MesajActivity.this, "Bu kişiyi tekrar kilitlediniz!", Toast.LENGTH_SHORT).show();
                                                    startActivity(new Intent(MesajActivity.this, MesajlarimActivity.class));
                                                }
                                            });
                                            fusSnap.child("harcamalarim").child("iade_" + userid).getRef().setValue(ServerValue.TIMESTAMP);
                                        }
                                    });
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

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
    }

    @Override
    protected void onPause() {
        super.onPause();
        mesajYaziyor("000");
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("usersF").child(fuser.getUid());
        ref.child("kime_cevrimici").setValue("");
        if(fuser != null)
            currentUserId("none");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mesajYaziyor("000");
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("usersF").child(fuser.getUid());
        ref.child("kime_cevrimici").setValue("");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.mesajlasmadaki_menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        ClipboardManager clipboard = (ClipboardManager) this.getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip;
        switch (item.getItemId()){
            case R.id.mesajlari_sil_item:
                if(internetVarmi())
                    tumMesajlarSilinsinMi();
                return true;
            case R.id.kullanici_adi:
                if(internetVarmi()){
                    if (kullaniciAdi != null) {
                        clip = ClipData.newPlainText("Kullanıcı adı kopyalandı", kullaniciAdi);
                        clipboard.setPrimaryClip(clip);
                        Toast.makeText(this, "Kullanıcı adı kopyalandı", Toast.LENGTH_SHORT).show();
                    }else
                        Toast.makeText(this, "Bir sorun oluştu.", Toast.LENGTH_SHORT).show();
                }
                return true;
            case R.id.referansim_yap:
                if(internetVarmi()) {
                    final DatabaseReference ref = FirebaseDatabase.getInstance().getReference("usersF").child(fuser.getUid());
                    ref.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if (dataSnapshot.child("referansim").getValue(String.class).equals("")) {
                                DatabaseReference reference = FirebaseDatabase.getInstance().getReference("usersF").child(userid);
                                reference.child("referanslarim").child(fuser.getUid()).setValue(ServerValue.TIMESTAMP);
                                ref.child("referansim").setValue(userid).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        GelirBilgisiEkle(fuser.getUid(),"ref_",fuser.getUid(),fiyatlar.getReferans()/2);
                                        GelirBilgisiEkle(userid,"ref_",userid,fiyatlar.getReferans());
                                    }
                                });
                            } else {
                                Toast.makeText(MesajActivity.this, "Zaten bir referansınız var.", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                }
                return true;
            case R.id.profile_git_item:
                if(internetVarmi()) {
                    Intent profilegit = new Intent(MesajActivity.this, DigerProfilActivity.class);
                    profilegit.putExtra("userid", userid);
                    startActivity(profilegit);
                }
                return true;
            case R.id.sikayet_et_item:
                if(internetVarmi()) {
                    DatabaseReference sikayetRef = FirebaseDatabase.getInstance().getReference("MesajSikayet");
                    sikayetRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if (dataSnapshot.exists()) {
                                if (dataSnapshot.hasChild(userid)) {
                                    sebep = dataSnapshot.child(userid).child("sebep").getValue(String.class);
                                    if (dataSnapshot.child(userid).child("sikayet_edenler").hasChild(fuser.getUid()))
                                        Toast.makeText(MesajActivity.this, "Zaten şikayet etmişsiniz.", Toast.LENGTH_SHORT).show();
                                    else
                                        mesajlasmaSikayetEdilsiMi();
                                }
                                else
                                    mesajlasmaSikayetEdilsiMi();
                            } else {
                                mesajlasmaSikayetEdilsiMi();
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                }
                return true;
            case R.id.engellet_item:
                if(internetVarmi())
                    engellensinMi();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
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
                                ref.child("gelirlerim").child(id + satinAlinanDeger + "_" + kpSayisi).child("zaman").setValue(ServerValue.TIMESTAMP).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(@NonNull Void unused) {
                                        dataSnapshot.child("kp").getRef().setValue(dataSnapshot.child("kp").getValue(Integer.class) + kacKP).addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(@NonNull Void unused) {
                                                Toast.makeText(MesajActivity.this, "Tebrikler " + kacKP + "KP kazandınız!", Toast.LENGTH_SHORT).show();
                                            }
                                        });
                                    }
                                });
                            }
                        }
                    }
                    else{
                        ref.child("gelirlerim").child(id+satinAlinanDeger+"_1").child("onceki_kp").setValue(dataSnapshot.child("kp").getValue(Integer.class));
                        ref.child("gelirlerim").child(id+satinAlinanDeger+"_1").child("zaman").setValue(ServerValue.TIMESTAMP).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(@NonNull Void unused) {
                                dataSnapshot.child("kp").getRef().setValue(dataSnapshot.child("kp").getValue(Integer.class) + kacKP).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(@NonNull Void unused) {
                                        Toast.makeText(MesajActivity.this, "Tebrikler " + kacKP + "KP kazandınız!", Toast.LENGTH_SHORT).show();
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
    protected void onResume() {
        super.onResume();
        mesaj.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.toString().trim().length() == 1) {
                    mesajYaziyor(userid);
                } else if (s.toString().trim().length() == 0) {
                    mesajYaziyor("000");
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }

        });
        if(fuser != null)
            currentUserId(userid);
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("usersF").child(fuser.getUid());
        ref.child("kime_cevrimici").setValue(userid);
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.child("kisitli_erisim_engeli").child("durum").getValue().equals("var")) {
                    Intent intent = new Intent(MesajActivity.this, MainActivity.class);
                    startActivity(intent);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        // Toast.makeText(this, ""+getApplicationContext().toString(), Toast.LENGTH_SHORT).show();
    }

    public void tumMesajlarSilinsinMi(){
        AlertDialog.Builder builder = new AlertDialog.Builder(MesajActivity.this);
        builder.setCancelable(true);
        builder.setTitle("Tüm mesajlar Silinsin Mi?");
       // builder.setMessage("Tüm mesajları silerseniz, bu kişiyi daha önce bulmadıysanız veya favori listenizde değilse bir daha karşı taraf mesaj atmadığı sürece bu kişiyle mesajlaşamazsın.");
        builder.setPositiveButton("Evet",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Mesajlar").child(ozelOda);
                        ref.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                for(DataSnapshot ss : dataSnapshot.getChildren()){
                                    if (ss.getChildrenCount() > 0) {
                                        Chat chat = ss.getValue(Chat.class);
                                        if (chat.getAlici().equals(fuser.getUid()) && chat.getGonderici().equals(userid) ||
                                                chat.getAlici().equals(userid) && chat.getGonderici().equals(fuser.getUid())) {
                                            if (!chat.getGormek_istemeyen1().equals(fuser.getUid()) && !chat.getGormek_istemeyen2().equals(fuser.getUid())) {
                                                if (chat.getGormek_istemeyen1().equals("")) {
                                                    ss.getRef().child("gormek_istemeyen1").setValue(fuser.getUid());
                                                } else if (chat.getGormek_istemeyen2().equals("")) {
                                                    ss.getRef().child("gormek_istemeyen2").setValue(fuser.getUid());
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

    public void mesajlasmaSikayetEdilsiMi(){
        AlertDialog.Builder builder = new AlertDialog.Builder(MesajActivity.this);
        builder.setCancelable(true);

        View view = getLayoutInflater().inflate(R.layout.dialog_dizayn,null);
        String[] secenekler = {"Argo", "Taciz", "Tehdit","Diğer"};
        builder.setView(view);

        EditText sikayet = view.findViewById(R.id.dialog_edittext);
        TextView baslik = view.findViewById(R.id.dialog_baslik);
        TextView bilgi = view.findViewById(R.id.dialog_bilgi);
        bilgi.setText("Sohbetinizde yer alan uygunsuzluğu seçiniz.");
        baslik.setVisibility(View.GONE);
        sikayet.setVisibility(View.GONE);
        builder.setTitle("Şikayet Et");
        builder.setItems(secenekler, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if(which == 0){
                    tumMesajArgoSikayetEdilsinMi();
                }else if(which == 1){
                    tumMesajTacizSikayetEdilsinMi();
                }else if(which == 2){
                    tumMesajTehditSikayetEdilsinMi();
                }else if(which == 3){
                    tumMesajDigerSikayetEdilsinMi();
                }
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }
    public void tumMesajArgoSikayetEdilsinMi(){
        Dialog dialog = new Dialog(MesajActivity.this);
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
        aciklama.setText("Sohbette argo var mı?");
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
                DatabaseReference ref = FirebaseDatabase.getInstance().getReference("MesajSikayet").child(userid);
                ref.child("mesaj").setValue("");
                ref.child("sebep").setValue(sebep + " ARGO");
                ref.child("sikayet_edenler").child(fuser.getUid()).setValue(ServerValue.TIMESTAMP);
                Toast.makeText(MesajActivity.this, "Şikayetiniz bize ulaşmıştır.", Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            }
        });
        dialog.show();

    }
    public void tumMesajTacizSikayetEdilsinMi(){
        Dialog dialog = new Dialog(MesajActivity.this);
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
        aciklama.setText("Sohbette taciz var mı?");
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
                DatabaseReference ref = FirebaseDatabase.getInstance().getReference("MesajSikayet").child(userid);
                ref.child("mesaj").setValue("");
                ref.child("sebep").setValue(sebep + " TACIZ");
                ref.child("sikayet_edenler").child(fuser.getUid()).setValue(ServerValue.TIMESTAMP);
                Toast.makeText(MesajActivity.this, "Şikayetiniz bize ulaşmıştır.", Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            }
        });
        dialog.show();
    }
    public void tumMesajTehditSikayetEdilsinMi(){
        Dialog dialog = new Dialog(MesajActivity.this);
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
        aciklama.setText("Sohbette tehdit var mı?");
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
                DatabaseReference ref = FirebaseDatabase.getInstance().getReference("MesajSikayet").child(userid);
                ref.child("mesaj").setValue("");
                ref.child("sebep").setValue(sebep + " TEHDIT");
                ref.child("sikayet_edenler").child(fuser.getUid()).setValue(ServerValue.TIMESTAMP);
                Toast.makeText(MesajActivity.this, "Şikayetiniz bize ulaşmıştır.", Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            }
        });
        dialog.show();
    }
    public void tumMesajDigerSikayetEdilsinMi(){

        Dialog dialog = new Dialog(MesajActivity.this);
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
        aciklama.setText("Bu mesajlaşmada uygunsuz gördüğünüz şeyi yazınız.");
        buton.setText("GÖNDER");
        buton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                lay1.setVisibility(View.GONE);
                lay2.setVisibility(View.GONE);
                pbar.setVisibility(View.VISIBLE);
                if(!sikayet.getText().toString().trim().equals("")){
                    if(!sikayet.getText().toString().trim().equals("")){
                        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("MesajSikayet").child(userid);
                        ref.child("mesaj").setValue("");
                        ref.child("sebep").setValue(sebep + " " +sikayet.getText().toString());
                        ref.child("sikayet_edenler").child(fuser.getUid()).setValue(ServerValue.TIMESTAMP);
                        Toast.makeText(MesajActivity.this, "Şikayetiniz bize ulaşmıştır.", Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                    }else{
                        lay1.setVisibility(View.VISIBLE);
                        lay2.setVisibility(View.VISIBLE);
                        pbar.setVisibility(View.GONE);
                        Toast.makeText(MesajActivity.this, "Hiçbir şey yazmadınız.", Toast.LENGTH_SHORT).show();
                    }
                }else{
                    lay1.setVisibility(View.VISIBLE);
                    lay2.setVisibility(View.VISIBLE);
                    pbar.setVisibility(View.GONE);
                    Toast.makeText(MesajActivity.this, "Hiçbir şey yazmadınız.", Toast.LENGTH_SHORT).show();
                }
            }
        });
        dialog.show();
    }

    public void engellensinMi(){

        Dialog dialog = new Dialog(MesajActivity.this);
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

        baslik.setText("Engellensin Mi?");
        aciklama.setText("Engelledikten sonra tekrar engeli kaldırabilirsiniz.");
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
                        dataSnapshot.child("engellediklerim").child(userid).getRef().setValue(ServerValue.TIMESTAMP);

                        if(dataSnapshot.child("kilidini_actiklarim").hasChild(userid))
                            dataSnapshot.child("kilidini_actiklarim").child(userid).getRef().removeValue();

                        if(dataSnapshot.hasChild("favorilerim"))
                            if (dataSnapshot.child("favorilerim").hasChild(userid))
                                dataSnapshot.child("favorilerim").child(userid).getRef().removeValue();

                        if(dataSnapshot.hasChild("bulduklarim"))
                            if (dataSnapshot.child("bulduklarim").hasChild(userid))
                                dataSnapshot.child("bulduklarim").child(userid).getRef().removeValue();

                        if(dataSnapshot.hasChild("begenenler"))
                            if (dataSnapshot.child("begenenler").hasChild(userid))
                                dataSnapshot.child("begenenler").child(userid).getRef().removeValue();



                        DatabaseReference onunref = FirebaseDatabase.getInstance().getReference("usersF").child(userid);
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


                                ContextWrapper cw = new ContextWrapper(MesajActivity.this);
                                File directory = cw.getDir("kullanici_resimleri", MODE_PRIVATE);

                                for(File files : directory.listFiles()){
                                    if(files.getName().substring(7,files.getName().length()-4).equals(userid)){
                                        files.delete();
                                        SharedPreferences spFavorilerim = getSharedPreferences("Favorilerim",MODE_PRIVATE);
                                        SharedPreferences spBulduklarim = getSharedPreferences("Bulduklarim",MODE_PRIVATE);
                                        for (int i = 0; i < 10; i++){
                                            if (spFavorilerim.getString("" + i, "null").equals(userid)) {
                                                SharedPreferences.Editor editorFav = spFavorilerim.edit();
                                                editorFav.remove("" + i);
                                                editorFav.commit();
                                            }
                                            if (spBulduklarim.getString("" + i, "null").equals(userid)) {
                                                SharedPreferences.Editor editorBul = spBulduklarim.edit();
                                                editorBul.remove("" + i);
                                                editorBul.commit();
                                            }
                                        }
                                    }
                                }

                                Intent geriDon = new Intent(MesajActivity.this,MesajlarimActivity.class);
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
}
