package com.kimoo.android.fragments;

import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.GradientDrawable;
import android.media.RingtoneManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.provider.Settings;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.ScrollingMovementMethod;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

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
import com.kimoo.android.MainActivity;
import com.kimoo.android.Model.User;
import com.kimoo.android.R;
import com.kimoo.android.TaraActivity;
import com.kimoo.android.extra.ArkadaBul;
import com.kimoo.android.extra.ArkadaBul2;
import com.kimoo.android.extra.FavoriAdapter;
import com.kimoo.android.extra.FavoriAdapter2;
import com.kimoo.android.extra.TasarimRenginiGetir;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class SuanBulunanlar extends AppCompatActivity{
    Button tara;
    public static String bulunabilmeZamaniYazi = "";
    static RecyclerView recyclerView,tarananlar;
    ArrayList<BluetoothDevice> eslesmisCihazlar = new ArrayList<>();
    static BluetoothAdapter myAdapter;
    LinearLayout background;
    //static RelativeLayout ustKisim;
    //static RelativeLayout altKisim;
    private static FavoriAdapter favoriAdapter;
    private static FavoriAdapter2 favoriAdapter2;
    ArrayAdapter<String> arrayAdapter;
    ImageView kart,liste;
    EditText ara;
    private StorageReference mStorageRef;
    ArrayList myDeviceList;
    static Activity activity ;
    private static ArrayList<String> mresimisimleri = new ArrayList<String>();
    private static ArrayList<String> mresimler = new ArrayList<String>();
    RelativeLayout rel, yeniKisiVar;
    private List<String> aramaSirasindaKaldirilanlar = new ArrayList<String>(),aramaSirasindaEkliOlanlar = new ArrayList<String>(),  kullanicilar = new ArrayList<String>(),tumBulduklarim = new ArrayList<String>();
    private static List<User> mUsers = new ArrayList<User>(),mUsers2 = new ArrayList<User>();
    FirebaseDatabase firebaseDatabase;
    DatabaseReference myRef;
    private FirebaseAuth mAuth;
    static Context mContext;
    private FirebaseUser user;
    static String aranacakDeger;
    BluetoothDevice device;
    RelativeLayout bulunabilme,bulma,taraniyor_pbar;
    Intent serviceIntent;
    static final int BULUNUYOR_KONTROL_REQUEST = 7;
    TextView kisiSayisi,bulunabilme2,bulma2, yeniKacKisiVar,taraniyor_yazi;
    ProgressBar pbar;
    private static int gorunumDeger;
    private NotificationManagerCompat notificationManagerCompat;
    int gelendeger, arananHarflerinSayisi, yeniKisiSayisi, bulunmusOlabilir;
    FirebaseUser fuser;
    SharedPreferences sharedPref;
    private DataSnapshot dataSnapshotAsil;
    public static DataSnapshot userSnapshot;
    public static boolean taramaAktifMi;
    private int ortaRenk;
    private boolean iptalEdiliyorMu, evSistemi = false;;
    private boolean tarayaTikladimMi;
    private User Kullanici;

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
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_suan_bulduklarim);
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
        ArkadaBul.tariyor = true;
        mContext = SuanBulunanlar.this;
        firebaseDatabase = FirebaseDatabase.getInstance();
        myRef = firebaseDatabase.getReference("usersF");
        mAuth = FirebaseAuth.getInstance();
        fuser = mAuth.getCurrentUser();
        kart = findViewById(R.id.kart_gorunumu);
        liste = findViewById(R.id.liste_gorunumu);
        background = findViewById(R.id.background);
        ara = findViewById(R.id.mesaj_ara_bulunanlar);
        yeniKisiVar = findViewById(R.id.yeni_kisi_var);
        yeniKacKisiVar = findViewById(R.id.yeni_kac_kisi_var);
        taraniyor_pbar = findViewById(R.id.taraniyor_pbar);
        pbar = findViewById(R.id.pbar);
        taraniyor_yazi = findViewById(R.id.taraniyor_yazi);
        //ustKisim = findViewById(R.id.gorduklerim_ust_kisim);
        //altKisim = findViewById(R.id.rel3);
        myAdapter = BluetoothAdapter.getDefaultAdapter();
        mStorageRef = FirebaseStorage.getInstance().getReference();
        tara = findViewById(R.id.taraa);
        recyclerView = findViewById(R.id.recylerView);
        bulunabilme = findViewById(R.id.bulunabilme);
        bulma = findViewById(R.id.bulma_ark);
        bulunabilme2 = findViewById(R.id.bulunabilme2);
        bulma2 = findViewById(R.id.bulma_ark2);
        kisiSayisi = findViewById(R.id.kisi_sayisi);
        tarananlar = findViewById(R.id.recylerViewArka);
        rel = findViewById(R.id.rel);
        notificationManagerCompat = NotificationManagerCompat.from(SuanBulunanlar.this);
        /*filtrele.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AltMenu altMenu = new AltMenu(SuanBulunanlar.this);
                View sheetView = getLayoutInflater().inflate(R.layout.filtre_menu, null);
                altMenu.setContentView(sheetView);
                altMenu.show();
            }
        });
        sirala.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AltMenu2 altMenu = new AltMenu2(SuanBulunanlar.this);
                View sheetView = getLayoutInflater().inflate(R.layout.sirala_menu, null);
                altMenu.setContentView(sheetView);
                altMenu.show();
            }
        });*/

        Set<BluetoothDevice> pdevice = myAdapter.getBondedDevices();
        for(BluetoothDevice device : pdevice){
            eslesmisCihazlar.add(device);
        }

        SharedPreferences tas_shared = getSharedPreferences("TasarimBilgileri",MODE_PRIVATE);
        TasarimDegistir(tas_shared.getString("tasarim_arayuz","1"));

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(getResources().getDrawable(R.drawable.ic_mesaj_geri2));
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SuanBulunanlar.this,TaraActivity.class);
                startActivity(intent);
                finish();
                overridePendingTransition(R.anim.slide_in_down,R.anim.slide_out_top);
            }
        });
        sharedPref = this.getPreferences(Context.MODE_PRIVATE);
        final SharedPreferences.Editor editorr = sharedPref.edit();

        if(bulunabilme2.getText().equals("Bulunabilme(00:00)"))
            yaziAnimOynat(true);
        else
            yaziAnimOynat(false);

        Thread thread = new Thread() {
            @Override
            public void run() {
                try {
                    while (!isInterrupted()) {
                        if(getApplicationContext() != null) {
                            runOnUiThread(new Runnable() {
                                @SuppressLint("MissingPermission")
                                @Override
                                public void run() {
                                    if(ArkadaBul.bulma)
                                        taramaAktifMi = true;

                                    if(taramaAktifMi){
                                        if (!iptalEdiliyorMu) {
                                            /*IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
                                            registerReceiver(receiver, filter);*/
                                            myAdapter.startDiscovery();
                                        }
                                        else
                                            myAdapter.cancelDiscovery();

                                        taraniyor_pbar.setVisibility(View.VISIBLE);
                                        if (myAdapter.isDiscovering()) {
                                            if (!ArkadaBul.bulma) {
                                                if (!iptalEdiliyorMu) {
                                                    bulma2.setText("Bulunuyor");
                                                    tara.setText("İptal Et");
                                                }
                                            }
                                            else {
                                                bulma2.setText("A. Bulunuyor");
                                                tara.setText("Arkaplanda");
                                            }
                                        }
                                    }
                                    else{
                                        if (!myAdapter.isDiscovering()) {
                                            taraniyor_pbar.setVisibility(View.GONE);
                                            if (iptalEdiliyorMu) {
                                                bulma2.setText("Bulunmuyor");
                                                tara.setText("Tara");
                                                iptalEdiliyorMu = false;
                                            }
                                        }
                                    }
                                        
                                    if(!bulunabilmeZamaniYazi.equals("")) {
                                        bulunabilme2.setText(bulunabilmeZamaniYazi);
                                    }
                                    else {
                                        bulunabilme2.setText("Bulunabilme(00:00)");
                                    }


                                    kisiSayisi.setText("Kişiler("+mUsers.size()+")");
                                }
                            });
                        }
                        Thread.sleep(250);
                    }
                } catch (InterruptedException e) {
                }

            }
        };
        thread.start();
        myDeviceList = new ArrayList();
        arrayAdapter = new ArrayAdapter<String>(SuanBulunanlar.this, android.R.layout.simple_list_item_1, myDeviceList);
        serviceIntent = new Intent(SuanBulunanlar.this, ArkadaBul2.class);

        //Üstteki liste
        liste.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (gorunumDeger != 0) {
                    liste.setColorFilter(ortaRenk);
                    kart.setColorFilter(getResources().getColor(R.color.gri2));
                    editorr.putInt("gorunum_suan_bulunanlar", 0);
                    editorr.commit();

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
                    editorr.putInt("gorunum_suan_bulunanlar", 1);
                    editorr.commit();
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
                arananHarflerinSayisi = count;
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                arananHarflerinSayisi = count;
                mesajAra(s.toString().toLowerCase().replaceAll(System.getProperty("line.separator"), ""));
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        if (internetVarmi()) {
            yeniKisiVar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ara.setText("");
                    yeniKisiSayisi = 0;
                    yeniKisiVar.setVisibility(View.GONE);
                }
            });

            final DatabaseReference reference = FirebaseDatabase.getInstance().getReference("usersF");
            reference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    userSnapshot = dataSnapshot;
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

            DatabaseReference ref = FirebaseDatabase.getInstance().getReference("usersF").child(fuser.getUid());
            ref.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    dataSnapshotAsil = dataSnapshot;
                    if (dataSnapshot.child("kisitli_erisim_engeli").child("durum").getValue().equals("var")) {
                        Intent intent = new Intent(SuanBulunanlar.this, MainActivity.class);
                        startActivity(intent);
                    }

                    if (dataSnapshot.hasChild("bulduklarim"))
                        for(DataSnapshot ds : dataSnapshot.child("bulduklarim").getChildren()){
                            if (!tumBulduklarim.contains(ds.getKey()))
                                tumBulduklarim.add(ds.getKey());
                        }
                    Kullanici = dataSnapshot.getValue(User.class);
                    /*if (!myAdapter.getName().equals("kim_" + us.getUid() + us.getId())) {
                        //myAdapter.setName("kim_" + us.getUsernamef() + us.getId());
                        SharedPreferences sharedPreferences = getSharedPreferences("son_ad", Context.MODE_PRIVATE);
                        final SharedPreferences.Editor editorr = sharedPreferences.edit();
                        editorr.putString("son_ka", myAdapter.getName());
                    }*/
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
            tara.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(final View view, MotionEvent motionEvent) {
                    int action = motionEvent.getAction();
                    if (action == MotionEvent.ACTION_DOWN) {
                            if (!ArkadaBul.bulma) {
                                if (!taramaAktifMi)
                                    tara.setText("Başlatılıyor");
                                else {
                                    iptalEdiliyorMu = true;
                                    tara.setText("Durduruluyor");
                                }
                            startScaleAnimation(view);
                        }
                        return true;
                    } else if (action == MotionEvent.ACTION_UP) {
                        if(!tarayaTikladimMi) {
                            tarayaTikladimMi = true;
                            cancelScaleAnimation(view, 1);
                        }

                        return true;
                    }
                    return false;
                }
            });

            if (getIntent().getStringExtra("bulunabilme") != null)
            if (getIntent().getStringExtra("bulunabilme").equals("ac"))
                IzinKontrolBulunma();

            bulunabilme.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (isNotificationChannelEnabled(SuanBulunanlar.this,"arkadabulunma"))
                        IzinKontrolBulunma();
                    else
                        openChannelSettings("arkadabulunma");
                }
            });
            bulma.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (isNotificationChannelEnabled(SuanBulunanlar.this,"arkadabulma")) {
                        if (ArkadaBul.bulma == false) {
                            if (!taramaAktifMi) {
                                Dialog dialog = new Dialog(SuanBulunanlar.this);
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
                                        dialog.dismiss();
                                    }
                                });
                                buton2.setOnClickListener(new View.OnClickListener() { // EVet
                                    @Override
                                    public void onClick(View v) {
                                        IzinKontrol2();
                                        dialog.dismiss();
                                    }
                                });
                                dialog.show();
                            } else {
                                Toast.makeText(SuanBulunanlar.this, "Zaten anlık olarak tarama yapıyorsunuz.", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(SuanBulunanlar.this, "Zaten arkaplanda tarama yapıyorsunuz.", Toast.LENGTH_SHORT).show();
                        }
                    }
                    else{
                        openChannelSettings("arkadabulma");
                    }

                }
            });

            String bulma = getIntent().getStringExtra("bulma");
            if (bulma != null)
                if (bulma.equals("aktif")) {
                    IzinKontrol3();
                }
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
            linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
            recyclerView.setLayoutManager(linearLayoutManager);
            GorunumKontrol();
        }
        else
            startActivity(new Intent(SuanBulunanlar.this,MainActivity.class));
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
                                if(i == 0){
                                }
                                if(i == 1){
                                    tarayaTikladimMi = false;
                                    if(internetVarmi()) {
                                        if (ArkadaBul.bulma == false) {
                                            if (!taramaAktifMi) {
                                                FirebaseDatabase.getInstance().getReference("usersF").addListenerForSingleValueEvent(new ValueEventListener() {
                                                    @Override
                                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                        userSnapshot = dataSnapshot;
                                                        IzinKontrol();
                                                    }

                                                    @Override
                                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                                    }
                                                });
                                            }
                                            else {
                                                if(receiver.isOrderedBroadcast())
                                                    unregisterReceiver(receiver);
                                                taramaAktifMi = false;
                                                myAdapter.cancelDiscovery();
                                                Intent intent = new Intent(SuanBulunanlar.this, SuanBulunanlar.class);
                                                intent.putExtra("bulma", "pasif");
                                            }
                                        } else {
                                            Toast.makeText(SuanBulunanlar.this, "Önce bildirimlerinizden ''Arkaplanda Tarama'' işlemini kapatmalısınız.", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                    else
                                        tara.setText("İnternet Yok!");
                                }

                            }
                        });
                    }
                });
            }
        });

    }

    public void onBackPressed() {
        super.onBackPressed();
        Intent tarayaDon = new Intent(SuanBulunanlar.this,TaraActivity.class);
        startActivity(tarayaDon);
        finish();
        overridePendingTransition(R.anim.slide_in_down,R.anim.slide_out_top);
    }

    private boolean internetVarmi(){
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED)
            return true;
        else
            return false;
    }

    public void IzinKontrol3(){
        if (myAdapter.isEnabled()) {
            if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED)
            {
                taramaAktifMi = true;
                IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
                if(!receiver.isOrderedBroadcast()) {
                    registerReceiver(receiver, filter);
                    myAdapter.startDiscovery();
                }
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
                yeniKisiSayisi = 0;
                yeniKisiVar.setVisibility(View.GONE);

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


    private void GorunumKontrol() {
        kullanicilar = new ArrayList<>();
        mUsers = new ArrayList<>();
        mUsers2 = new ArrayList<>();

        favoriAdapter = new FavoriAdapter(getAppContext(),activity, mUsers);
        favoriAdapter2 = new FavoriAdapter2(getAppContext(),activity, mUsers);

        if(sharedPref.getInt("gorunum_suan_bulunanlar",0) == 0){
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
       // initRecyclerView();
    }
    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.slide_in_down,R.anim.slide_out_top);
    }
    private void yaziAnimOynat(boolean durum) {
        if(durum) {
            final Animation animation = new AlphaAnimation(1, (float) 0.5);
            animation.setDuration(400);
            animation.setInterpolator(new LinearInterpolator());
            animation.setRepeatCount(Animation.INFINITE);
            animation.setRepeatMode(Animation.REVERSE);

            bulunabilme.startAnimation(animation);
        }else{
            bulunabilme.clearAnimation();
        }
    }

    private void BulunabilmeSuresiBelirle() {

        final boolean[] checked = {false};

        Dialog dialog = new Dialog(SuanBulunanlar.this);
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
                        TaraActivity.ortaRenk,
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
                BulunabilmeSuresiniDegistir(5 * 60, checked[0]);
                dialog.dismiss();
            }
        });
        buton2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BulunabilmeSuresiniDegistir(15 * 60, checked[0]);
                dialog.dismiss();
            }
        });
        buton3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BulunabilmeSuresiniDegistir(30 * 60, checked[0]);
                dialog.dismiss();
            }
        });
        buton4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BulunabilmeSuresiniDegistir(60 * 60, checked[0]);
                dialog.dismiss();
            }
        });

        dialog.show();

    }

    private void BulunabilmeSuresiniDegistir(final int i, final boolean checked) {
        gelendeger = i;
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("usersF").child(fuser.getUid());
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User auser = dataSnapshot.getValue(User.class);
                if(checked) {
                    dataSnapshot.child("ev_sistemi").child("sistem").getRef().setValue("aktif");
                    dataSnapshot.child("ev_sistemi").child("evde_mi").getRef().setValue("hayir");
                }
                FirebaseDatabase.getInstance().getReference("usersF").child(fuser.getUid()).child("suan_bulanlar").removeValue();
                myAdapter.setName("kim_" + dataSnapshot.child("usernamef").getValue(String.class) + dataSnapshot.child("id").getValue(String.class));
                Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
                intent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, i);
                startActivityForResult(intent,BULUNUYOR_KONTROL_REQUEST);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    public void TasarimDegistir(String tasDegeri) {
        GradientDrawable gradient = (GradientDrawable) getResources().getDrawable(R.drawable.gradient);
        gradient.setOrientation(GradientDrawable.Orientation.LEFT_RIGHT);
        GradientDrawable gradientYumusak = new GradientDrawable();
        gradientYumusak.setOrientation(GradientDrawable.Orientation.LEFT_RIGHT);
        gradientYumusak.setCornerRadius(50);

        int renk1 = 0,renk2 = 0,t1start = 0,t2start = 0,t1end = 0,t2end = 0,orta = 0;

        renk1 = TasarimRenginiGetir.RengiGetir(SuanBulunanlar.this,"renk1",tasDegeri);
        renk2 = TasarimRenginiGetir.RengiGetir(SuanBulunanlar.this,"renk2",tasDegeri);
        t1start = TasarimRenginiGetir.RengiGetir(SuanBulunanlar.this,"t1start",tasDegeri);
        t2start = TasarimRenginiGetir.RengiGetir(SuanBulunanlar.this,"t2start",tasDegeri);
        t1end = TasarimRenginiGetir.RengiGetir(SuanBulunanlar.this,"t1end",tasDegeri);
        t2end = TasarimRenginiGetir.RengiGetir(SuanBulunanlar.this,"t2end",tasDegeri);
        orta = TasarimRenginiGetir.RengiGetir(SuanBulunanlar.this,"orta",tasDegeri);

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
        taraniyor_yazi.setTextColor(orta);
        tara.setBackground(gradientYumusak);
        background.setBackground(gradient);
    }

    @Override
    protected void onResume() {
        super.onResume();
        ArkadaBul.tariyor = true;

        SharedPreferences tas_shared = getSharedPreferences("TasarimBilgileri",MODE_PRIVATE);
        TasarimDegistir(tas_shared.getString("tasarim_arayuz","1"));

        final DatabaseReference reference = FirebaseDatabase.getInstance().getReference("usersF");
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                userSnapshot = dataSnapshot;
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public static Context getAppContext(){
        return mContext;
    }
    public void IzinKontrol(){
        if (myAdapter.isEnabled()) {
            if (ContextCompat.checkSelfPermission(SuanBulunanlar.this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_SCAN) == PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED)
            {
                taramaAktifMi = true;
                IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
                registerReceiver(receiver, filter);
                myAdapter.startDiscovery();
                /*FirebaseDatabase.getInstance().getReference("usersF").child("Xvvnx72043furvOyTbwSs24XV2x2").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        BenOnuEngellemismiyim(dataSnapshot.getValue(User.class));
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });*/

            } else {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.BLUETOOTH_SCAN,
                            Manifest.permission.POST_NOTIFICATIONS},1);
                }

            }

        } else {
            Intent eintent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(eintent,3);
        }

    }
    public static String getUID() {
        int DIGITS = 5;
        StringBuilder sb = new StringBuilder(DIGITS);
        for(int i = 0;i < DIGITS;i++) {
            sb.append((char) (Math.random() * 10 + '0'));
        }
        return sb.toString();
    }
    private void IzinKontrol2() {
        final String olusanId = getUID();

        if (myAdapter.isEnabled()) {
            SharedPreferences sharedPreferences = getSharedPreferences("ayarlar",Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            if(sharedPreferences.getString("bt_adi","") == null){
                editor.putString("bt_adi",myAdapter.getName().toString());
                editor.commit();
            }
            DatabaseReference ref = FirebaseDatabase.getInstance().getReference("usersF");
            ref.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    User auser = dataSnapshot.child(fuser.getUid()).getValue(User.class);
                    dataSnapshot.child(fuser.getUid()).child("id").getRef().setValue(olusanId);
                    //myAdapter.setName("kim_" + auser.getUsernamef() + olusanId);
                    ArkadaBul.deaktif = false;
                    ArkadaBul.bulma = true;
                    taramaAktifMi = true;
                    Intent intent = new Intent(SuanBulunanlar.this, ArkadaBul.class);
                    startService(intent);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        } else {
            Intent eintent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(eintent,4);
        }
    }
    public void IzinKontrolBulunma(){

        if (myAdapter.isEnabled()) {
            if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_SCAN) == PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED)
            {
                BulunabilmeSuresiBelirle();
            } else {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.BLUETOOTH_SCAN,
                            Manifest.permission.POST_NOTIFICATIONS},1);
                }
            }

        } else {
            Intent eintent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(eintent,2);
        }

    }

    private void KimlerleAyniYereGidiyorum() {
        final int[] gidenlerSayisi = {0};
        List<String> gidecegimYerler = new ArrayList<String>();
        gidecegimYerler.add("1");
        gidecegimYerler.add("2");
        gidecegimYerler.add("3");
        gidecegimYerler.add("asil");
        DatabaseReference sistemRef = FirebaseDatabase.getInstance().getReference("Sistem");
        sistemRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                int gidilenYerOnayiIcinGerekliKisiSayisi = dataSnapshot.child("gidilenYerOnayiIcinGerekliKisiSayisi").getValue(Integer.class);
                for(int i = 0; i < gidecegimYerler.size(); i ++){
                    String gidecegimYerinDBIsmi = dataSnapshotAsil.child("gidecegim_yerler").child(gidecegimYerler.get(i)).child("dbisim").getValue(String.class);
                    String gidecegimYerinIsmi = dataSnapshotAsil.child("gidecegim_yerler").child(gidecegimYerler.get(i)).child("isim").getValue(String.class);
                    String gidecegimYereGitmismiyim = dataSnapshotAsil.child("gidecegim_yerler").child(gidecegimYerler.get(i)).child("gittimMi").getValue(String.class);
                    if (!gidecegimYerinDBIsmi.equals("") && gidecegimYereGitmismiyim.equals("hayir")) {
                        long zaman = dataSnapshotAsil.child("gidecegim_yerler").child(gidecegimYerler.get(i)).child("zaman").getValue(Long.class);
                        for (DataSnapshot ds : dataSnapshotAsil.child("bulduklarim").getChildren()){
                            if(ds.child("son_gordugum_zaman").getValue(Long.class) > zaman){
                                DatabaseReference ref = FirebaseDatabase.getInstance().getReference("usersF").child(ds.getKey());
                                int finalI = i;
                                ref.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        if(dataSnapshot.child("gidecegim_yerler").child("1").child("dbisim").getValue().equals(gidecegimYerinDBIsmi))
                                            gidenlerSayisi[0]++;
                                        if(dataSnapshot.child("gidecegim_yerler").child("2").child("dbisim").getValue().equals(gidecegimYerinDBIsmi))
                                            gidenlerSayisi[0]++;
                                        if(dataSnapshot.child("gidecegim_yerler").child("3").child("dbisim").getValue().equals(gidecegimYerinDBIsmi))
                                            gidenlerSayisi[0]++;
                                        if(dataSnapshot.child("gidecegim_yerler").child("asil").child("dbisim").getValue().equals(gidecegimYerinDBIsmi))
                                            gidenlerSayisi[0]++;

                                        if(gidenlerSayisi[0] >= gidilenYerOnayiIcinGerekliKisiSayisi){
                                            dataSnapshotAsil.child("gidecegim_yerler").child(gidecegimYerler.get(finalI)).child("gittimMi").getRef().setValue("evet").addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(@NonNull Void unused) {
                                                    // Ödülü ver
                                                    dataSnapshotAsil.child("kp").getRef().setValue(dataSnapshotAsil.child("kp").getValue(Integer.class) + TaraActivity.fiyatlar.getZiyaret_gorevi());
                                                    // bildirim gönder
                                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                                        KonumaUlasildiBildirimiGonderOreo(gidecegimYerinIsmi);
                                                    } else {
                                                        KonumaUlasildiBildirimiGonder(gidecegimYerinIsmi);
                                                    }
                                                    DatabaseReference ref = FirebaseDatabase.getInstance().getReference("usersF").child(fuser.getUid());
                                                    ref.addListenerForSingleValueEvent(new ValueEventListener() {
                                                        @Override
                                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                            dataSnapshotAsil = dataSnapshot;
                                                            Kullanici = dataSnapshot.getValue(User.class);
                                                        }

                                                        @Override
                                                        public void onCancelled(@NonNull DatabaseError databaseError) {

                                                        }
                                                    });
                                                   // dataSnapshotAsil.child("gidecegim_yerler").child(gidecegimYerler.get(finalI)).child("gittimMi").getRef().setValue("evet");
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
    private void KonumaUlasildiBildirimiGonder(String gidecegimYerinIsmi) {
        Uri defaultSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        Intent intent = new Intent(SuanBulunanlar.this, TaraActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this,5,intent,PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext())
                .setSmallIcon(R.drawable.kimoo_bildirim)
                .setContentText("Gitmek istediğiniz konuma ulaştınız!" )
                .setContentTitle("" + gidecegimYerinIsmi + "'a geldiniz.")
                .setAutoCancel(true)
                .setVibrate(new long[] { 1000, 1000, 1000, 1000, 1000 })
                .setSound(defaultSound)
                .setContentIntent(pendingIntent);
        NotificationManager noti = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        noti.notify(4,builder.build());



    }
    @RequiresApi(api = Build.VERSION_CODES.O)
    private void KonumaUlasildiBildirimiGonderOreo(String gidecegimyer) {
        Intent intent = new Intent(SuanBulunanlar.this, TaraActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(),5,intent,PendingIntent.FLAG_UPDATE_CURRENT);
        Notification notification = new Notification.Builder(getApplicationContext(), "gidilenyerler")
                .setContentTitle("" + gidecegimyer + "'a geldiniz.")
                .setContentIntent(pendingIntent)
                .setContentText("Gitmek istediğiniz konuma ulaştınız!" )
                .setSmallIcon(R.drawable.kimoo_bildirim)
                .setAutoCancel(true)
                .build();
        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        manager.notify(5, notification);
    }
    @Override
    protected void onPause() {
        super.onPause();

        if(receiver.isOrderedBroadcast())
            unregisterReceiver(receiver);
        taramaAktifMi = false;
        myAdapter.cancelDiscovery();
        Intent intent = new Intent(SuanBulunanlar.this, SuanBulunanlar.class);
        intent.putExtra("bulma", "pasif");
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == 2)
            IzinKontrolBulunma();
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
        if(requestCode == 8){
            if(resultCode != 0) {
                IzinKontrolBulunma();
            }
        }
        if(requestCode == BULUNUYOR_KONTROL_REQUEST)
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
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 1: {

                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    IzinKontrol();

                } else {
                    Toast.makeText(SuanBulunanlar.this, "Eğer izin vermezseniz diğer Kimoo kullanıcıları sizi bulamaz.", Toast.LENGTH_LONG).show();
                }
                return;
            }
            case 2: {

                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    IzinKontrolBulunma();

                } else {
                    Toast.makeText(SuanBulunanlar.this, "Eğer izin vermezseniz diğer Kimoo kullanıcıları sizi bulamaz.", Toast.LENGTH_LONG).show();
                }
                return;
            }
        }
    }

    private void startServicee(int kalanSure){
        /*;*/
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
    private final BroadcastReceiver receiver = new BroadcastReceiver() {
        public void onReceive(final Context context, Intent intent) {
            String action = intent.getAction();
            if(!ArkadaBul.bulma) {
                if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                    device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                    //Toast.makeText(SuanBulunanlar.this, "" + device.getName(), Toast.LENGTH_SHORT).show();
                    if (device != null) {
                        /*for(int i = 0; i < eslesmisCihazlar.size(); i ++){
                            if(eslesmisCihazlar.get(i).getAddress().equals(device.getAddress())) {
                                device = eslesmisCihazlar.get(i);
                            }
                        }*/
                        //Toast.makeText(SuanBulunanlar.this, "" + device.getName(), Toast.LENGTH_SHORT).show();
                        if (device.getName() != null && !device.getName().trim().equals(""))
                            if(device.getName().length() > 12) {
                                if (device.getName().substring(0, 4).equals("kim_")) {
                                    String k_uid = device.getName().substring(4, device.getName().trim().length() - 5);
                                    String k_id = device.getName().substring(device.getName().trim().length() - 5);
                                    if(!kullanicilar.contains(k_uid)) {
                                        bulunmusOlabilir++;
                                        //taraniyor_yazi.setText("" + bulunmusOlabilir + " Kişi bulunmuş olabilir");
                                        Query userQuery = FirebaseDatabase.getInstance().getReference("usersF").orderByChild("usernamef").equalTo(k_uid);
                                        userQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(DataSnapshot userSS) {
                                                for (DataSnapshot dataSnapshot: userSS.getChildren()) {
                                                    final User user = dataSnapshot.getValue(User.class);
                                                    if (dataSnapshot.child("ban_durumu").child("durum").getValue().equals("yok")) { // kullanıcın ban durumu yoksa
                                                        if (user.getId().equals(k_id)) { // Kullanıcının id'si uygunsa
                                                            int yasSiniri = 0;
                                                            if(Integer.parseInt(Kullanici.getDg()) < 18)
                                                                yasSiniri = 2;
                                                            else
                                                                yasSiniri = 5;
                                                            if( (Integer.parseInt(Kullanici.getDg()) - Integer.parseInt(user.getDg())) > -yasSiniri && (Integer.parseInt(Kullanici.getDg()) - Integer.parseInt(user.getDg())) < yasSiniri) {

                                                                if (tumBulduklarim.size() > 0){
                                                                    if (!tumBulduklarim.contains(dataSnapshot.getKey())) {
                                                                        if (dataSnapshot.child("ev_sistemi").child("sistem").getValue(String.class).equals("aktif")) {
                                                                            if (!dataSnapshot.child("ev_sistemi").child("beni_bulanlar").hasChild(fuser.getUid()))
                                                                                dataSnapshot.child("ev_sistemi").child("beni_bulanlar").child(fuser.getUid()).getRef().setValue(ServerValue.TIMESTAMP);
                                                                        }
                                                                        else {
                                                                            // YENİ KULLANICI BULUNDU

                                                                            if (!tumBulduklarim.contains(user.getUid()))
                                                                                tumBulduklarim.add(user.getUid());
                                                                            if (!kullanicilar.contains(user.getUsernamef())) {
                                                                                kullanicilar.add(user.getUsernamef());

                                                                        /*if(bulunmusOlabilir > 0){
                                                                            bulunmusOlabilir--;
                                                                            if (bulunmusOlabilir == 0)
                                                                                taraniyor_yazi.setText("Kullanıcılar bulunuyor...");
                                                                            else
                                                                                taraniyor_yazi.setText("" + bulunmusOlabilir + " Kişi bulunmuş olabilir");
                                                                        }
                                                                        else
                                                                            taraniyor_yazi.setText("Kullanıcılar bulunuyor...");*/

                                                                                BenOnuEngellemismiyim(user);
                                                                                dataSnapshotAsil.child("bulduklarim").child(user.getUid()).child("kac_kez_gordum").getRef().setValue("1");
                                                                                dataSnapshotAsil.child("bulduklarim").child(user.getUid()).child("ilk_gordugum_zaman").getRef().setValue(ServerValue.TIMESTAMP);
                                                                                dataSnapshotAsil.child("bulduklarim").child(user.getUid()).child("son_gordugum_zaman").getRef().setValue(ServerValue.TIMESTAMP);
                                                                            }

                                                                        }
                                                                    }
                                                                    else {
                                                                    }
                                                                }
                                                                else{
                                                                    if (dataSnapshot.child("ev_sistemi").child("sistem").getValue(String.class).equals("aktif")) {
                                                                        if (!dataSnapshot.child("ev_sistemi").child("beni_bulanlar").hasChild(fuser.getUid()))
                                                                            dataSnapshot.child("ev_sistemi").child("beni_bulanlar").child(fuser.getUid()).getRef().setValue(ServerValue.TIMESTAMP);
                                                                    }
                                                                    else {
                                                                        // YENİ KULLANICI BULUNDU

                                                                        if (!tumBulduklarim.contains(user.getUid()))
                                                                            tumBulduklarim.add(user.getUid());
                                                                        if (!kullanicilar.contains(user.getUsernamef())) {
                                                                            kullanicilar.add(user.getUsernamef());

                                                                        /*if(bulunmusOlabilir > 0){
                                                                            bulunmusOlabilir--;
                                                                            if (bulunmusOlabilir == 0)
                                                                                taraniyor_yazi.setText("Kullanıcılar bulunuyor...");
                                                                            else
                                                                                taraniyor_yazi.setText("" + bulunmusOlabilir + " Kişi bulunmuş olabilir");
                                                                        }
                                                                        else
                                                                            taraniyor_yazi.setText("Kullanıcılar bulunuyor...");*/

                                                                            BenOnuEngellemismiyim(user);
                                                                            dataSnapshotAsil.child("bulduklarim").child(user.getUid()).child("kac_kez_gordum").getRef().setValue("1");
                                                                            dataSnapshotAsil.child("bulduklarim").child(user.getUid()).child("ilk_gordugum_zaman").getRef().setValue(ServerValue.TIMESTAMP);
                                                                            dataSnapshotAsil.child("bulduklarim").child(user.getUid()).child("son_gordugum_zaman").getRef().setValue(ServerValue.TIMESTAMP);
                                                                        }

                                                                    }
                                                                }
                                                                for (DataSnapshot ds2 : dataSnapshot.child("gidecegim_yerler").getChildren()) {
                                                                    if (!ds2.child("dbisim").getValue(String.class).equals("")) {
                                                                        for (DataSnapshot ds3 : dataSnapshotAsil.child("gidecegim_yerler").getChildren()) {
                                                                            if (!ds3.child("dbisim").getValue(String.class).equals("")) {
                                                                                if (ds3.child("dbisim").getValue(String.class).equals(ds2.child("dbisim").getValue(String.class))) {
                                                                                    if (ds3.child("gittimMi").getValue(String.class).equals("hayir")) {
                                                                                        KimlerleAyniYereGidiyorum();
                                                                                    }
                                                                                }
                                                                            }
                                                                        }
                                                                    }
                                                                }
                                                            }
                                                            else{
                                                                /*if(bulunmusOlabilir > 0){
                                                                    bulunmusOlabilir--;
                                                                    if (bulunmusOlabilir == 0)
                                                                        taraniyor_yazi.setText("Kullanıcılar bulunuyor...");
                                                                    else
                                                                        taraniyor_yazi.setText("" + bulunmusOlabilir + " Kişi bulunmuş olabilir");
                                                                }
                                                                else
                                                                    taraniyor_yazi.setText("Kullanıcılar bulunuyor...");*/

                                                            }
                                                            // Gideceğim yere gidiyor mu kontrolü

                                                        }
                                                        else{
                                                            /*if(bulunmusOlabilir > 0){
                                                                bulunmusOlabilir--;
                                                                if (bulunmusOlabilir == 0)
                                                                    taraniyor_yazi.setText("Kullanıcılar bulunuyor...");
                                                                else
                                                                    taraniyor_yazi.setText("" + bulunmusOlabilir + " Kişi bulunmuş olabilir");
                                                            }
                                                            else
                                                                taraniyor_yazi.setText("Kullanıcılar bulunuyor...");*/

                                                        }
                                                    } else {
                                                        // Banlı kullanıcı bulundu

                                                        /*if(bulunmusOlabilir > 0){
                                                            bulunmusOlabilir--;
                                                            if (bulunmusOlabilir == 0)
                                                                taraniyor_yazi.setText("Kullanıcılar bulunuyor...");
                                                            else
                                                                taraniyor_yazi.setText("" + bulunmusOlabilir + " Kişi bulunmuş olabilir");
                                                        }
                                                        else
                                                            taraniyor_yazi.setText("Kullanıcılar bulunuyor...");*/

                                                    }
                                                }
                                            }

                                            @Override
                                            public void onCancelled(DatabaseError databaseError) {

                                            }
                                        });
                                    }
                                }
                            }



                    }
                }
            }
        }
    };
    private void BenOnuEngellemismiyim(final User user) {
        if(dataSnapshotAsil.hasChild("engellediklerim")){
            if(dataSnapshotAsil.child("engellediklerim").hasChild(user.getUid())){

            }
            else
                OBeniEngellemismi(user);

        }
        else
            OBeniEngellemismi(user);
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
    private void OBeniEngellemismi(final User user) {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("usersF").child(user.getUid());
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(!dataSnapshot.child("engellediklerim").child(fuser.getUid()).exists()){
                    mUsers2.add(mUsers2.size(),user);

                    if (arananHarflerinSayisi == 0) {
                        if (!aramaSirasindaEkliOlanlar.contains(user.getUid()))
                            aramaSirasindaEkliOlanlar.add(user.getUid());
                        mUsers.add(mUsers.size(), user);
                        favoriAdapter.notifyDataSetChanged();
                        favoriAdapter2.notifyDataSetChanged();
                    }
                    else{
                        yeniKisiSayisi++;
                        yeniKisiVar.setVisibility(View.VISIBLE);
                        yeniKacKisiVar.setText("+"+yeniKisiSayisi+" Kişi");
                        if(!aramaSirasindaKaldirilanlar.contains(user.getUid()))
                            aramaSirasindaKaldirilanlar.add(user.getUid());
                    }
                    DatabaseReference uref = FirebaseDatabase.getInstance().getReference("usersF").child(user.getUid());
                    uref.child("suan_bulanlar").child(fuser.getUid()).setValue(ServerValue.TIMESTAMP);
                    uref.child("bulanlar").child(fuser.getUid()).setValue(ServerValue.TIMESTAMP);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(receiver.isOrderedBroadcast())
            unregisterReceiver(receiver);
        taramaAktifMi = false;
        myAdapter.cancelDiscovery();
        ArkadaBul.tariyor = false;

        Intent intent = new Intent(SuanBulunanlar.this, SuanBulunanlar.class);
        intent.putExtra("bulma","pasif");
    }
}
