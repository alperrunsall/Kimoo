package com.kimoo.android;

import android.app.Activity;
import android.app.Dialog;
import android.bluetooth.BluetoothAdapter;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.ContextWrapper;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;

import android.provider.Settings;
import android.text.InputFilter;
import android.text.InputType;
import android.text.Spanned;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.kimoo.android.Model.Fiyatlar;
import com.kimoo.android.Model.User;
import com.kimoo.android.extra.TasarimRenginiGetir;

import java.io.File;

public class AyarlarActivity extends AppCompatActivity {

    private Toolbar mToolbar;
    BluetoothAdapter myAdapter;
    public DataSnapshot snapShot;
    private FirebaseAuth mAuth;
    private FirebaseUser fuser;
    private User Kullanici;
    private Fiyatlar fiyatlar;
    private int ortaRenk;
    private boolean butonTiklandiMi = false;
    private TextView baslik1,baslik2,baslik3,baslik4,baslik5;
    private String sssYazisi, gizlilikYazisi, sartlarYazisi;
    private LinearLayout gizlilik_adveyas, gizlilik_fotolar, gizlilik_ziyaret, hesap_mail, hesap_sifre, hesap_engel, hesap_referans, hesap_dondur, bluetooth,
            background, hesap_cik, hesap_sil, diger_alanac,diger_sss, diger_sartlar, diger_gizlilik, diger_kod, diger_gorus, diger_hazirlayanlar;
    private Switch tum_bildirimler, mesaj_bildirim, begeni_bildirim, ziyaret_bildirim;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ayarlar);
        StatuBarAyarla();

        mToolbar = findViewById(R.id.toolbar);
        gizlilik_adveyas = findViewById(R.id.gizlilik_adveyas);
        gizlilik_fotolar = findViewById(R.id.gizlilik_fotolar);
        gizlilik_ziyaret = findViewById(R.id.gizlilik_mekan);
        hesap_mail = findViewById(R.id.hesap_mail);
        hesap_sifre = findViewById(R.id.hesap_sifre);
        hesap_engel = findViewById(R.id.hesap_engel);
        baslik1 = findViewById(R.id.baslik1_yazi);
        baslik2 = findViewById(R.id.baslik2_yazi);
        baslik3 = findViewById(R.id.baslik3_yazi);
        baslik4 = findViewById(R.id.baslik4_yazi);
        baslik5 = findViewById(R.id.baslik5_yazi);
        hesap_cik = findViewById(R.id.hesap_cikis);
        hesap_sil = findViewById(R.id.hesap_sil);
        hesap_dondur = findViewById(R.id.hesap_dondur);
        diger_alanac = findViewById(R.id.diger_alanac);
        diger_sss = findViewById(R.id.diger_sss);
        diger_sartlar = findViewById(R.id.diger_sartlar);
        diger_gizlilik = findViewById(R.id.diger_gizlilik);
        diger_kod = findViewById(R.id.diger_kod);
        diger_gorus = findViewById(R.id.diger_gorus);
        hesap_referans = findViewById(R.id.hesap_referans);
        bluetooth = findViewById(R.id.bluetooth);
        tum_bildirimler = findViewById(R.id.tum_bildirimler);
        mesaj_bildirim = findViewById(R.id.mesaj_bildirimi);
        begeni_bildirim = findViewById(R.id.begeni_bildirimi);
        ziyaret_bildirim = findViewById(R.id.konum_bildirimi);
        diger_hazirlayanlar = findViewById(R.id.diger_hazirlayanlar);
        background = findViewById(R.id.background);
        mAuth = FirebaseAuth.getInstance();
        setSupportActionBar(mToolbar);
        myAdapter = BluetoothAdapter.getDefaultAdapter();

        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(getResources().getDrawable(R.drawable.ic_mesaj_geri2));
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                overridePendingTransition(R.anim.slide_in_left,R.anim.slide_out_right);
            }
        });

        SharedPreferences sharedPreferences = getSharedPreferences("ayarlar", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        fuser = FirebaseAuth.getInstance().getCurrentUser();


        SharedPreferences tas_shared = getSharedPreferences("TasarimBilgileri",MODE_PRIVATE);
        TasarimDegistir(tas_shared.getString("tasarim_arayuz","1"));

        DatabaseReference fiyatRef = FirebaseDatabase.getInstance().getReference("Sistem");
        fiyatRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                fiyatlar = dataSnapshot.child("fiyatlar").getValue(Fiyatlar.class);
                gizlilikYazisi = dataSnapshot.child("yazilar").child("gizlilik_sozlesmesi").getValue(String.class);
                sartlarYazisi = dataSnapshot.child("yazilar").child("kullanim_sartlari").getValue(String.class);
                sssYazisi = dataSnapshot.child("yazilar").child("sss").getValue(String.class);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("usersF").child(fuser.getUid());
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Kullanici = dataSnapshot.getValue(User.class);
                snapShot = dataSnapshot;
                int kacBildirimGelmiyor = 0;
                int kacBildirimGeliyor = 0;
                if(snapShot.child("bildirimler").child("mesaj").getValue(String.class).equals("gelsin")){
                    kacBildirimGeliyor++;
                    mesaj_bildirim.setChecked(true);
                }
                else{
                    kacBildirimGelmiyor++;
                    mesaj_bildirim.setChecked(false);
                }
                if(snapShot.child("bildirimler").child("begeni").getValue(String.class).equals("gelsin")){
                    kacBildirimGeliyor++;
                    begeni_bildirim.setChecked(true);
                }
                else{
                    kacBildirimGelmiyor++;
                    begeni_bildirim.setChecked(false);
                }
                if(snapShot.child("bildirimler").child("ziyaret").getValue(String.class).equals("gelsin")){
                    kacBildirimGeliyor++;
                    ziyaret_bildirim.setChecked(true);
                }
                else{
                    kacBildirimGelmiyor++;
                    ziyaret_bildirim.setChecked(false);
                }
                if (kacBildirimGelmiyor == 3){
                    tum_bildirimler.setChecked(false);
                    mesaj_bildirim.setChecked(false);
                    begeni_bildirim.setChecked(false);
                    ziyaret_bildirim.setChecked(false);
                }
                if (kacBildirimGeliyor == 3){
                    tum_bildirimler.setChecked(true);
                    mesaj_bildirim.setChecked(true);
                    begeni_bildirim.setChecked(true);
                    ziyaret_bildirim.setChecked(true);
                }
                tum_bildirimler.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        if (isChecked){
                            mesaj_bildirim.setChecked(true);
                            //mesaj_bildirim.setEnabled(true);
                            begeni_bildirim.setChecked(true);
                            //begeni_bildirim.setEnabled(true);
                            ziyaret_bildirim.setChecked(true);
                            // ziyaret_bildirim.setEnabled(true);
                            snapShot.child("bildirimler").child("mesaj").getRef().setValue("gelsin");
                            snapShot.child("bildirimler").child("begeni").getRef().setValue("gelsin");
                            snapShot.child("bildirimler").child("ziyaret").getRef().setValue("gelsin");
                        }
                        else{
                            mesaj_bildirim.setChecked(false);
                            //mesaj_bildirim.setEnabled(false);
                            begeni_bildirim.setChecked(false);
                            // begeni_bildirim.setEnabled(false);
                            ziyaret_bildirim.setChecked(false);
                            //ziyaret_bildirim.setEnabled(false);
                            snapShot.child("bildirimler").child("mesaj").getRef().setValue("gelmesin");
                            snapShot.child("bildirimler").child("begeni").getRef().setValue("gelmesin");
                            snapShot.child("bildirimler").child("ziyaret").getRef().setValue("gelmesin");
                        }
                    }
                });
                mesaj_bildirim.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        if(isChecked){
                            snapShot.child("bildirimler").child("mesaj").getRef().setValue("gelsin");
                        }
                        else{
                            snapShot.child("bildirimler").child("mesaj").getRef().setValue("gelmesin");
                        }
                    }
                });
                begeni_bildirim.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        if(isChecked){
                            snapShot.child("bildirimler").child("begeni").getRef().setValue("gelsin");
                        }
                        else{
                            snapShot.child("bildirimler").child("begeni").getRef().setValue("gelmesin");
                        }
                    }
                });
                ziyaret_bildirim.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        if(isChecked){
                            snapShot.child("bildirimler").child("ziyaret").getRef().setValue("gelsin");
                        }
                        else{
                            snapShot.child("bildirimler").child("ziyaret").getRef().setValue("gelmesin");
                        }
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        diger_hazirlayanlar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!butonTiklandiMi) {
                    butonTiklandiMi = true;
                    startActivity(new Intent(AyarlarActivity.this, HazirlayanlarActivity.class));
                }
            }
        });

        gizlilik_adveyas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!butonTiklandiMi) {
                    butonTiklandiMi = true;
                    GizlilikSec(0, Integer.valueOf(snapShot.child("gizlilik_ad").getValue(String.class)));
                }
            }
        });
        gizlilik_fotolar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    if (!butonTiklandiMi) {
                        butonTiklandiMi = true;
                        GizlilikSec(1, Integer.valueOf(snapShot.child("gizlilik_foto").getValue(String.class)));
                    }
            }
        });
        gizlilik_ziyaret.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!butonTiklandiMi) {
                    butonTiklandiMi = true;
                    GizlilikSec(2, Integer.valueOf(snapShot.child("gizlilik_mekan").getValue(String.class)));
                }
            }
        });
        hesap_mail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!butonTiklandiMi) {
                    butonTiklandiMi = true;
                    Intent intent = new Intent(AyarlarActivity.this, AyarDegistirActivity.class);
                    intent.putExtra("islem", "0");
                    startActivity(intent);
                }
            }
        });
        hesap_sifre.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!butonTiklandiMi) {
                    butonTiklandiMi = true;

                    SharedPreferences sharedPreferences = getSharedPreferences("guvenlik", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();

                    final int[] gonderimSayisi = {sharedPreferences.getInt("mail_gonderimi", 0)};
                    long sonGonderimZamani = sharedPreferences.getLong("son_gonderim",0);

                    Dialog dialog2 = new Dialog(AyarlarActivity.this);
                    dialog2.setContentView(R.layout.dialog_dizayn5);
                    dialog2.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                    LinearLayout lay1 = dialog2.findViewById(R.id.lay1);
                    ProgressBar pbar = dialog2.findViewById(R.id.pbar);
                    TextView baslik = dialog2.findViewById(R.id.baslik);
                    TextView aciklama = dialog2.findViewById(R.id.aciklama);
                    aciklama.setMovementMethod(new ScrollingMovementMethod());
                    Button buton = dialog2.findViewById(R.id.buton);
                    Button buton2 = dialog2.findViewById(R.id.buton2);
                    baslik.setText("Mail Gönder");
                    if (gonderimSayisi[0] == 0)
                        aciklama.setText("Şifrenizi sıfırlamanız için mail gönderilsin mi?");
                    else if (gonderimSayisi[0] == 1)
                        aciklama.setText("Şifrenizi sıfırlamanız için tekrar mail gönderilsin mi?");
                    else
                        aciklama.setText("Şifrenizi sıfırlamanız için mail gönderilsin mi?");
                    buton.setText("HAYIR");
                    buton2.setText("EVET");
                    buton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            butonTiklandiMi = false;
                            dialog2.dismiss();
                        }
                    });
                    buton2.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            lay1.setVisibility(View.GONE);
                            pbar.setVisibility(View.VISIBLE);
                            mAuth.sendPasswordResetEmail(mAuth.getCurrentUser().getEmail()).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        butonTiklandiMi = false;
                                        Toast.makeText(AyarlarActivity.this, "Şifrenizi sıfırlayabilmeniz için mail gönderildi. Mail adresinize ulaşamıyorsanız mail değiştir seçeneğine basabilirsiniz.", Toast.LENGTH_LONG).show();
                                        if (gonderimSayisi[0] == 0)
                                            editor.putInt("mail_gonderimi", 1);
                                        else if (gonderimSayisi[0] == 1)
                                            editor.putInt("mail_gonderimi", 2);
                                        else
                                            editor.putInt("mail_gonderimi", 1);
                                        editor.putLong("son_gonderim",System.currentTimeMillis());
                                        editor.commit();
                                        dialog2.dismiss();
                                    }
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    butonTiklandiMi = false;
                                    Toast.makeText(AyarlarActivity.this, "Bir sorun oluştu, mail adresi doğru olmayabilir.", Toast.LENGTH_SHORT).show();
                                    dialog2.dismiss();
                                }
                            });
                        }
                    });
                    dialog2.setOnCancelListener(new DialogInterface.OnCancelListener() {
                        @Override
                        public void onCancel(DialogInterface dialog) {
                            butonTiklandiMi = false;
                        }
                    });
                    if (gonderimSayisi[0] == 2){
                        if (System.currentTimeMillis() > sonGonderimZamani + 86400000){
                            dialog2.show();
                        }
                        else {
                            butonTiklandiMi = false;
                            editor.putLong("son_gonderim",System.currentTimeMillis()-86400000);
                            editor.commit();
                            Toast.makeText(AyarlarActivity.this, "Bugün yeterince mail gönderilmiş lütfen yarın tekrar deneyin.", Toast.LENGTH_LONG).show();
                            dialog2.dismiss();
                        }
                    }
                    else{
                        dialog2.show();
                    }
                }
            }
        });
        hesap_engel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!butonTiklandiMi) {
                    butonTiklandiMi = true;
                    Intent intent = new Intent(AyarlarActivity.this, AyarDegistirActivity.class);
                    intent.putExtra("islem", "1");
                    AyarlarActivity.this.startActivity(intent);
                }
            }
        });
        hesap_referans.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!butonTiklandiMi) {
                    butonTiklandiMi = true;
                    if (snapShot.child("referansim").getValue(String.class).equals("")) {
                        final int[] kullaniciSayisi = {0};
                        Dialog dialog = new Dialog(AyarlarActivity.this);
                        dialog.setContentView(R.layout.dialog_dizayn3);
                        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

                        TextView baslik = dialog.findViewById(R.id.baslik);
                        TextView aciklama = dialog.findViewById(R.id.aciklama);

                        aciklama.setMovementMethod(new ScrollingMovementMethod());
                        Button buton = dialog.findViewById(R.id.buton);
                        EditText referans = dialog.findViewById(R.id.editText);

                        baslik.setText("Referans");
                        aciklama.setText("Kimoo'yu tavsiye eden arkadaşınızın kullanıcını adını giriniz");
                        buton.setText("TAMAM");
                        buton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                butonTiklandiMi = false;
                                if (referans.getText().length() > 4 && !referans.getText().toString().equals(Kullanici.getUsernamef())) {
                                    DatabaseReference ref = FirebaseDatabase.getInstance().getReference("usersF");
                                    ref.addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                            for (DataSnapshot ds : dataSnapshot.getChildren()) {
                                                if (ds.child("usernamef").getValue(String.class).equals(referans.getText().toString().trim().toLowerCase())) {
                                                    ds.child("referanslarim").child(fuser.getUid()).getRef().setValue(ServerValue.TIMESTAMP);
                                                    snapShot.child("referansim").getRef().setValue(ds.getKey()).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                        @Override
                                                        public void onSuccess(@NonNull Void unused) {
                                                            GelirBilgisiEkle(snapShot.getKey(), "ref_", ds.getKey(), fiyatlar.getReferans() / 2, dialog);
                                                            GelirBilgisiEkle(ds.getKey(), "ref_", ds.getKey(), fiyatlar.getReferans(), dialog);
                                                            SnapshotGuncelle();
                                                            dialog.dismiss();
                                                        }
                                                    });
                                                } else {
                                                    kullaniciSayisi[0]++;
                                                }
                                                if (kullaniciSayisi[0] == dataSnapshot.getChildrenCount()) {
                                                    kullaniciSayisi[0] = 0;
                                                    Toast.makeText(AyarlarActivity.this, "Böyle bir kullanıcı yok", Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError databaseError) {

                                            butonTiklandiMi = false;
                                        }
                                    });
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
                    } else {
                        butonTiklandiMi = false;
                        Toast.makeText(AyarlarActivity.this, "Zaten bir referans eklemişsiniz", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
        bluetooth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!butonTiklandiMi) {
                    butonTiklandiMi = true;
                    SharedPreferences sharedPreferences = getSharedPreferences("ayarlar", Context.MODE_PRIVATE);
                    final SharedPreferences.Editor editor = sharedPreferences.edit();

                    Dialog dialog = new Dialog(AyarlarActivity.this);
                    dialog.setContentView(R.layout.dialog_dizayn3);
                    dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

                    TextView baslik = dialog.findViewById(R.id.baslik);
                    TextView aciklama = dialog.findViewById(R.id.aciklama);
                    aciklama.setMovementMethod(new ScrollingMovementMethod());
                    Button buton = dialog.findViewById(R.id.buton);
                    EditText bt_adi = dialog.findViewById(R.id.editText);

                    bt_adi.setText(sharedPreferences.getString("bt_adi", ""));
                    bt_adi.setHint("Bluetooth Adı");
                    baslik.setText("Bluetooth Adı");
                    aciklama.setText("Kimoo bluetooth özelliğini kullanırken bu ismi değiştirir. Kimoo bluetooth kullanmadığı zamanlarda bu isim ne olsun?");
                    buton.setText("TAMAM");
                    buton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            butonTiklandiMi = false;
                            if (bt_adi.getText().toString().trim().length() > 0) {
                                snapShot.child("bt_adi").getRef().setValue(bt_adi.getText().toString()).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(@NonNull Void unused) {
                                        editor.putString("bt_adi", bt_adi.getText().toString());
                                        editor.commit();
                                        Toast.makeText(AyarlarActivity.this, "Bluetooth adı belirlendi", Toast.LENGTH_SHORT).show();
                                        dialog.dismiss();
                                    }
                                });
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
        });
        diger_kod.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!butonTiklandiMi) {
                    butonTiklandiMi = true;
                    OdulVer();
                }
            }
        });
        diger_alanac.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Dialog dialog = new Dialog(AyarlarActivity.this);
                dialog.setContentView(R.layout.dialog_dizayn5);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

                TextView baslik = dialog.findViewById(R.id.baslik);
                TextView aciklama = dialog.findViewById(R.id.aciklama);
                LinearLayout lay1 = dialog.findViewById(R.id.lay1);
                ProgressBar pbar = dialog.findViewById(R.id.pbar);
                pbar.getIndeterminateDrawable().setColorFilter(ortaRenk,android.graphics.PorterDuff.Mode.MULTIPLY);
                aciklama.setMovementMethod(new ScrollingMovementMethod());

                Button buton = dialog.findViewById(R.id.buton);
                Button buton2 = dialog.findViewById(R.id.buton2);
                baslik.setText("Alan Aç");
                aciklama.setText("Diğer kullanıcıların telefonunuzdaki verilerini silmek ister misiniz? (Fotoğraflar vs.)");
                buton.setText("HAYIR");
                buton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });
                buton2.setText("EVET");
                buton2.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ContextWrapper cw = new ContextWrapper(AyarlarActivity.this);
                        File directory = cw.getDir("kullanici_resimleri", MODE_PRIVATE);
                        SharedPreferences spBulduklarim = getSharedPreferences("Bulduklarim",MODE_PRIVATE);
                        SharedPreferences spFavorilerim = getSharedPreferences("Favorilerim",MODE_PRIVATE);
                        SharedPreferences spBulduklarimZaman = getSharedPreferences("BulduklarimZaman",MODE_PRIVATE);
                        SharedPreferences spFavorilerimZaman = getSharedPreferences("FavorilerimZaman",MODE_PRIVATE);
                        SharedPreferences.Editor spBulduklarimEdit = spBulduklarim.edit();
                        SharedPreferences.Editor spFavorilerimEdit = spFavorilerim.edit();
                        SharedPreferences.Editor spBulduklarimZamanEdit = spBulduklarimZaman.edit();
                        SharedPreferences.Editor spFavorilerimZamanEdit = spFavorilerimZaman.edit();

                        int a = 0;
                        for(int i = 0; i < 10; i++){
                            //Toast.makeText(AyarlarActivity.this, ""+spBulduklarim.getString("" + (i+1),"null"), Toast.LENGTH_SHORT).show();
                            spBulduklarimEdit.remove(""+(i+1));
                            spFavorilerimEdit.remove(""+(i+1));
                            spBulduklarimZamanEdit.remove(""+(i+1));
                            spFavorilerimZamanEdit.remove(""+(i+1));
                            spBulduklarimEdit.commit();
                            spFavorilerimEdit.commit();
                            spBulduklarimZamanEdit.commit();
                            spFavorilerimZamanEdit.commit();
                            if (i == 9){
                                directory.delete();
                                dialog.dismiss();
                            }
                                /*for(File file : directory.listFiles()){
                                    //Toast.makeText(AyarlarActivity.this, ""+file.getName(), Toast.LENGTH_LONG).show();
                                    a++;
                                    file.delete();

                                    if (a == directory.listFiles().length)
                                }*/
                        }
                        //dialog.dismiss();

                    }
                });
                dialog.show();
            }
        });
        diger_sss.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!butonTiklandiMi) {
                    butonTiklandiMi = true;
                    if (sssYazisi != null) {
                        //SSSGoster("Sıkça Sorulan Sorular", sssYazisi);
                        Uri uri = Uri.parse(sssYazisi);
                        Toast.makeText(AyarlarActivity.this, "Bağlantı Açılıyor...", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                        startActivity(intent);
                    } else{
                        Toast.makeText(AyarlarActivity.this, "İnternet bağlantınız yavaş lütfen tekrar deneyin.", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
        diger_sartlar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!butonTiklandiMi) {
                    butonTiklandiMi = true;
                    if (sartlarYazisi != null) {
                        //SSSGoster("Sıkça Sorulan Sorular", sssYazisi);
                        Uri uri = Uri.parse(sartlarYazisi);
                        Toast.makeText(AyarlarActivity.this, "Bağlantı Açılıyor...", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                        startActivity(intent);
                    } else {
                        butonTiklandiMi = false;
                        Toast.makeText(AyarlarActivity.this, "İnternet bağlantınız yavaş lütfen tekrar deneyin.", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
        diger_gizlilik.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!butonTiklandiMi) {
                    butonTiklandiMi = true;
                    if (gizlilikYazisi != null) {
                        //SSSGoster("Sıkça Sorulan Sorular", sssYazisi);
                        Uri uri = Uri.parse(gizlilikYazisi);
                        Toast.makeText(AyarlarActivity.this, "Bağlantı Açılıyor...", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                        startActivity(intent);
                    } else {
                        butonTiklandiMi = false;
                        Toast.makeText(AyarlarActivity.this, "İnternet bağlantınız yavaş lütfen tekrar deneyin.", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
        diger_gorus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!butonTiklandiMi) {
                    butonTiklandiMi = true;
                    GorusBildir();
                }
            }
        });
        hesap_cik.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!butonTiklandiMi) {
                    butonTiklandiMi = true;

                    Dialog dialog = new Dialog(AyarlarActivity.this);
                    dialog.setCancelable(true);
                    dialog.setContentView(R.layout.dialog_dizayn5);
                    dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

                    TextView baslik = dialog.findViewById(R.id.baslik);
                    TextView aciklama = dialog.findViewById(R.id.aciklama);
                    LinearLayout lay1 = dialog.findViewById(R.id.lay1);
                    ProgressBar pbar = dialog.findViewById(R.id.pbar);
                    pbar.getIndeterminateDrawable().setColorFilter(ortaRenk, android.graphics.PorterDuff.Mode.MULTIPLY);
                    aciklama.setMovementMethod(new ScrollingMovementMethod());

                    Button buton = dialog.findViewById(R.id.buton);
                    Button buton2 = dialog.findViewById(R.id.buton2);
                    baslik.setText("Çıkış");
                    aciklama.setText("Hesabınızdan çıkmak istiyor musunuz?");
                    buton.setText("HAYIR");
                    buton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            butonTiklandiMi = false;
                            dialog.dismiss();
                        }
                    });
                    buton2.setText("EVET");
                    buton2.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            lay1.setVisibility(View.GONE);
                            pbar.setVisibility(View.VISIBLE);
                            HesaptanCik(false);
                            butonTiklandiMi = false;
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
        hesap_sil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!butonTiklandiMi) {
                    butonTiklandiMi = true;
                    HesabiSil();
                }
            }
        });
        hesap_dondur.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!butonTiklandiMi) {
                    butonTiklandiMi = true;
                    Dondur();
                }
            }
        });
        hesap_sil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!butonTiklandiMi) {
                    butonTiklandiMi = true;
                    HesabiSil();
                }
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        butonTiklandiMi = false;
    }

    @Override
    protected void onStop() {
        super.onStop();
        butonTiklandiMi = false;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
        overridePendingTransition(R.anim.slide_in_left,R.anim.slide_out_right);
    }
    private void GelirBilgisiEkle(String uid, String id, String satinAlinanDeger, int kacKP, Dialog dialog){
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
                                                Toast.makeText(AyarlarActivity.this, "Tebrikler " + kacKP + "KP kazandınız!", Toast.LENGTH_SHORT).show();
                                                dialog.dismiss();
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
                                        Toast.makeText(AyarlarActivity.this, "Tebrikler " + kacKP + "KP kazandınız!", Toast.LENGTH_SHORT).show();
                                        dialog.dismiss();
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

    private void Dondur() {
        Dialog dialog = new Dialog(AyarlarActivity.this);
        dialog.setContentView(R.layout.dialog_dizayn5);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {

                butonTiklandiMi = false;
            }
        });
        TextView baslik = dialog.findViewById(R.id.baslik);
        TextView aciklama = dialog.findViewById(R.id.aciklama);
        LinearLayout lay1 = dialog.findViewById(R.id.lay1);
        ProgressBar pbar = dialog.findViewById(R.id.pbar);
        aciklama.setMovementMethod(new ScrollingMovementMethod());

        Button buton = dialog.findViewById(R.id.buton);
        Button buton2 = dialog.findViewById(R.id.buton2);
        baslik.setText("Hesap Dondurulsun mu?");
        aciklama.setText("Hesabınızı dondurursanız, hesabınızdan çıkış yapılır ve tekar giriş yapana kadar diğer kullanıcılar sizi göremez");
        buton.setText("HAYIR");
        buton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                butonTiklandiMi = false;
                dialog.dismiss();
            }
        });
        buton2.setText("EVET");
        buton2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Dialog dialog2 = new Dialog(AyarlarActivity.this);
                dialog2.setContentView(R.layout.dialog_dizayn3);
                dialog2.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

                TextView baslik = dialog2.findViewById(R.id.baslik);
                TextView aciklama = dialog2.findViewById(R.id.aciklama);
                LinearLayout lay1 = dialog2.findViewById(R.id.lay1);
                LinearLayout lay2 = dialog2.findViewById(R.id.lay2);
                ProgressBar pbar = dialog2.findViewById(R.id.pbar);
                pbar.getIndeterminateDrawable().setColorFilter(ortaRenk,android.graphics.PorterDuff.Mode.MULTIPLY);
                aciklama.setMovementMethod(new ScrollingMovementMethod());
                Button buton = dialog2.findViewById(R.id.buton);
                EditText sifre = dialog2.findViewById(R.id.editText);
                sifre.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD);
                sifre.setHint("Şifre");
                dialog2.setOnCancelListener(new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialog) {

                        butonTiklandiMi = false;
                    }
                });
                baslik.setText("Onay");
                aciklama.setText("Hesabınız dondurulacak, onaylamak için lütfen şifrenizi giriniz");
                buton.setText("TAMAM");
                buton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (sifre.getText().toString().length() > 5) {
                            lay1.setVisibility(View.GONE);
                            lay2.setVisibility(View.GONE);
                            pbar.setVisibility(View.VISIBLE);
                            FirebaseAuth mAuth = FirebaseAuth.getInstance();
                            mAuth.signInWithEmailAndPassword(Kullanici.getEmail(), sifre.getText().toString()).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("usersF").child(fuser.getUid()).child("hesap_durumu");
                                        ref.child("durum").setValue(1);
                                        ref.child("zaman").setValue(ServerValue.TIMESTAMP).addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(@NonNull Void unused) {
                                                butonTiklandiMi = false;
                                                HesaptanCik(false);
                                            }
                                        });
                                    } else {
                                        butonTiklandiMi = false;
                                        Toast.makeText(AyarlarActivity.this, "Şifrenizi kontrol ediniz", Toast.LENGTH_SHORT).show();
                                        lay1.setVisibility(View.VISIBLE);
                                        lay2.setVisibility(View.VISIBLE);
                                        pbar.setVisibility(View.GONE);
                                    }
                                }
                            });
                        }
                    }
                });
                dialog2.show();
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    private void HesabiSil() {
        Dialog dialog = new Dialog(AyarlarActivity.this);
        dialog.setContentView(R.layout.dialog_dizayn5);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {

                butonTiklandiMi = false;
            }
        });
        TextView baslik = dialog.findViewById(R.id.baslik);
        TextView aciklama = dialog.findViewById(R.id.aciklama);
        LinearLayout lay1 = dialog.findViewById(R.id.lay1);
        ProgressBar pbar = dialog.findViewById(R.id.pbar);
        aciklama.setMovementMethod(new ScrollingMovementMethod());

        Button buton = dialog.findViewById(R.id.buton);
        Button buton2 = dialog.findViewById(R.id.buton2);
        baslik.setText("Hesap Silinsin Mi?");
        aciklama.setText("Onaylarsanız hesabınız tamamen silinecek");
        buton.setText("HAYIR");
        buton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                butonTiklandiMi = false;
                dialog.dismiss();
            }
        });
        buton2.setText("EVET");
        buton2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Dialog dialog2 = new Dialog(AyarlarActivity.this);
                dialog2.setContentView(R.layout.dialog_dizayn3);
                dialog2.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

                dialog2.setOnCancelListener(new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialog) {

                        butonTiklandiMi = false;
                    }
                });
                TextView baslik = dialog2.findViewById(R.id.baslik);
                TextView aciklama = dialog2.findViewById(R.id.aciklama);
                LinearLayout lay1 = dialog2.findViewById(R.id.lay1);
                LinearLayout lay2 = dialog2.findViewById(R.id.lay2);
                ProgressBar pbar = dialog2.findViewById(R.id.pbar);
                pbar.getIndeterminateDrawable().setColorFilter(ortaRenk,android.graphics.PorterDuff.Mode.MULTIPLY);
                aciklama.setMovementMethod(new ScrollingMovementMethod());
                Button buton = dialog2.findViewById(R.id.buton);
                EditText sifre = dialog2.findViewById(R.id.editText);
                sifre.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD);
                sifre.setHint("Şifre");

                baslik.setText("Onay");
                aciklama.setText("Hesabınız silinecek, onaylamak için lütfen şifrenizi giriniz");
                buton.setText("TAMAM");
                buton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        butonTiklandiMi = false;
                        if (sifre.getText().toString().length() > 5) {
                            lay1.setVisibility(View.GONE);
                            lay2.setVisibility(View.GONE);
                            pbar.setVisibility(View.VISIBLE);
                            AuthCredential credential = EmailAuthProvider.getCredential(Kullanici.getEmail(), sifre.getText().toString());
                            fuser.reauthenticate(credential).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        dialog2.dismiss();
                                        Dialog dialog = new Dialog(AyarlarActivity.this);
                                        dialog.setContentView(R.layout.dialog_dizayn3);
                                        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

                                        TextView baslik = dialog.findViewById(R.id.baslik);
                                        TextView aciklama = dialog.findViewById(R.id.aciklama);
                                        LinearLayout lay1 = dialog.findViewById(R.id.lay1);
                                        LinearLayout lay2 = dialog.findViewById(R.id.lay2);
                                        ProgressBar pbar = dialog.findViewById(R.id.pbar);
                                        aciklama.setMovementMethod(new ScrollingMovementMethod());
                                        Button buton = dialog.findViewById(R.id.buton);
                                        EditText sorununcevabi = dialog.findViewById(R.id.editText);
                                        sorununcevabi.setFilters(new InputFilter[] {
                                                new InputFilter() {
                                                    @Override
                                                    public CharSequence filter(CharSequence cs, int start,
                                                                               int end, Spanned spanned, int dStart, int dEnd) {
                                                        if(cs.toString().contains(" ")){
                                                            return cs.toString().trim();
                                                        }
                                                        if(cs.toString().matches("[a-zA-Z]+")){
                                                            if (cs.toString().length() < 31)
                                                                return cs;
                                                        }
                                                        if (cs.toString().length() < 31)
                                                            return cs;
                                                        else
                                                            return cs.toString().substring(0,29);

                                                    }
                                                }
                                        });
                                        sorununcevabi.setHint("Güvenlik Kelimesi");
                                        baslik.setText("Güvenlik Sorusu");
                                        aciklama.setText("Lütfen uygulamaya kayıt olurken belirlediğiniz güvenlik kelimenizi yazınız. \n (*Büyük-küçük harflere duyarlı değildir.)");
                                        buton.setText("TAMAM");
                                        buton.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View view) {
                                                lay1.setVisibility(View.GONE);
                                                lay2.setVisibility(View.GONE);
                                                pbar.setVisibility(View.VISIBLE);
                                                DatabaseReference ref = FirebaseDatabase.getInstance().getReference("usersF").child(fuser.getUid());
                                                ref.addListenerForSingleValueEvent(new ValueEventListener() {
                                                    @Override
                                                    public void onDataChange(@NonNull DataSnapshot kelimeSnap) {
                                                        if(sorununcevabi.getText().toString().trim().length() > 6){
                                                            if(kelimeSnap.child("guvenlik").child("uyari").getValue(Integer.class) < 3) {
                                                                if(kelimeSnap.child("guvenlik").child("guvenlik_kelimesi").getValue(String.class).equals(sorununcevabi.getText().toString().toLowerCase().trim())){
                                                                    StorageReference sref = FirebaseStorage.getInstance().getReference(fuser.getUid());
                                                                    sref.child("foto_" + "pp" + ".jpg").delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                        @Override
                                                                        public void onSuccess(@NonNull Void unused) {
                                                                            if (snapShot.hasChild("fotograflarim")) {
                                                                                int childSayisi = 0;
                                                                                for (DataSnapshot ds : snapShot.child("fotograflarim").getChildren()) {
                                                                                    sref.child("foto_" + ds.getKey() + ".jpg").delete();
                                                                                    childSayisi++;
                                                                                    if (childSayisi == (int) snapShot.child("fotograflarim").getChildrenCount()) {
                                                                                        HesaptanCik(true);
                                                                                    }

                                                                                }
                                                                            } else {
                                                                                final int[] userSayisi = {0};
                                                                                DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference("usersF");
                                                                                usersRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                                                                    @Override
                                                                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                                                        for (DataSnapshot ds : dataSnapshot.getChildren()) {
                                                                                            userSayisi[0]++;
                                                                                            if (ds.child("kilidini_actiklarim").hasChild(fuser.getUid()))
                                                                                                ds.child("kilidini_actiklarim").child(fuser.getUid()).getRef().removeValue();

                                                                                            if (ds.hasChild("favorilerim"))
                                                                                                if (ds.child("favorilerim").hasChild(fuser.getUid()))
                                                                                                    ds.child("favorilerim").child(fuser.getUid()).getRef().removeValue();

                                                                                            if (ds.hasChild("bulduklarim"))
                                                                                                if (ds.child("bulduklarim").hasChild(fuser.getUid()))
                                                                                                    ds.child("bulduklarim").child(fuser.getUid()).getRef().removeValue();

                                                                                            if (ds.hasChild("begenenler")) {
                                                                                                if (ds.child("begenenler").hasChild(fuser.getUid())) {
                                                                                                    ds.child("begenenler").child(fuser.getUid()).getRef().removeValue();
                                                                                                    if (userSayisi[0] == dataSnapshot.getChildrenCount())
                                                                                                        HesaptanCik(true);
                                                                                                } else if (userSayisi[0] == dataSnapshot.getChildrenCount())
                                                                                                    HesaptanCik(true);
                                                                                            } else if (userSayisi[0] == dataSnapshot.getChildrenCount())
                                                                                                HesaptanCik(true);


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
                                                                else{
                                                                    kelimeSnap.child("guvenlik").child("uyari_zamani").getRef().setValue(ServerValue.TIMESTAMP);
                                                                    kelimeSnap.child("guvenlik").child("uyari_suresi").getRef().setValue(86400000 * 1);
                                                                    kelimeSnap.child("guvenlik").child("uyari").getRef().setValue(kelimeSnap.child("guvenlik").child("uyari").getValue(Integer.class) + 1).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                        @Override
                                                                        public void onSuccess(@NonNull Void unused) {
                                                                            lay1.setVisibility(View.VISIBLE);
                                                                            lay2.setVisibility(View.VISIBLE);
                                                                            pbar.setVisibility(View.GONE);
                                                                            sorununcevabi.setError("Yanlış!");
                                                                        }
                                                                    });
                                                                }
                                                            }
                                                            else{
                                                                kelimeSnap.child("guvenlik").child("uyari_zamani").getRef().setValue(ServerValue.TIMESTAMP);
                                                                kelimeSnap.child("guvenlik").child("uyari_suresi").getRef().setValue(86400000 * 3); // 3 Gün 86400000
                                                                mAuth.sendPasswordResetEmail(mAuth.getCurrentUser().getEmail()).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                    @Override
                                                                    public void onComplete(@NonNull Task<Void> task) {
                                                                        Toast.makeText(AyarlarActivity.this, "Güvenlik ihlali yaptınız, bu ayarı bir süre değiştiremezsiniz.", Toast.LENGTH_SHORT).show();
                                                                        dialog.dismiss();
                                                                        startActivity(new Intent(AyarlarActivity.this,AyarlarActivity.class));
                                                                    }
                                                                });
                                                            }
                                                        }
                                                        else{
                                                            lay1.setVisibility(View.VISIBLE);
                                                            lay2.setVisibility(View.VISIBLE);
                                                            pbar.setVisibility(View.GONE);
                                                            sorununcevabi.setError("Bu kelime en az 7 karakterden oluşmalı.");
                                                        }
                                                    }

                                                    @Override
                                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                                        lay1.setVisibility(View.VISIBLE);
                                                        lay2.setVisibility(View.VISIBLE);
                                                        pbar.setVisibility(View.GONE);
                                                    }
                                                });
                                            }
                                        });

                                        dialog.show();

                                    }
                                    else {
                                        Toast.makeText(AyarlarActivity.this, "Şifrenizi kontrol ediniz", Toast.LENGTH_SHORT).show();
                                        lay1.setVisibility(View.VISIBLE);
                                        lay2.setVisibility(View.VISIBLE);
                                        pbar.setVisibility(View.GONE);
                                    }
                                }
                            });
                        }
                    }
                });
                dialog2.show();
                dialog.dismiss();
            }
        });
        dialog.show();

    }

    private void HesaptanCik(boolean sil){

        SharedPreferences tas_shared = getSharedPreferences("TasarimBilgileri",MODE_PRIVATE);
        SharedPreferences.Editor editor1 = tas_shared.edit();
        editor1.putString("tasarim_arayuz","sifirlandi");
        editor1.commit();

        SharedPreferences guvenlik_shared = getSharedPreferences("guvenlik",MODE_PRIVATE);
        SharedPreferences.Editor editor2 = guvenlik_shared.edit();
        editor2.putInt("giris_denemesi",0);
        editor2.commit();

        SharedPreferences bulunan_shared = getSharedPreferences("bulunan",MODE_PRIVATE);
        SharedPreferences.Editor editor3 = bulunan_shared.edit();
        editor3.putInt("KalinanYer",0);
        editor3.commit();

        SharedPreferences sp_shared = getSharedPreferences("SP_USER",MODE_PRIVATE);
        SharedPreferences.Editor editor4 = sp_shared.edit();
        editor4.putString("Current_USERID","none");
        editor4.commit();


        ContextWrapper cw = new ContextWrapper(AyarlarActivity.this);
        String dosyaParent = "benim_resimler";
        File directory = cw.getDir(dosyaParent, MODE_PRIVATE);

        String dosyaAdi = "foto_" + "pp" + ".jpg";
        File image = new File(directory, dosyaAdi);
        if (image.exists())
            image.delete();

        File directory2 = cw.getDir("kullanici_resimleri", MODE_PRIVATE);
        SharedPreferences spBulduklarim = getSharedPreferences("Bulduklarim",MODE_PRIVATE);
        SharedPreferences spFavorilerim = getSharedPreferences("Favorilerim",MODE_PRIVATE);
        SharedPreferences spBulduklarimZaman = getSharedPreferences("BulduklarimZaman",MODE_PRIVATE);
        SharedPreferences spFavorilerimZaman = getSharedPreferences("FavorilerimZaman",MODE_PRIVATE);
        SharedPreferences.Editor spBulduklarimEdit = spBulduklarim.edit();
        SharedPreferences.Editor spFavorilerimEdit = spFavorilerim.edit();
        SharedPreferences.Editor spBulduklarimZamanEdit = spBulduklarimZaman.edit();
        SharedPreferences.Editor spFavorilerimZamanEdit = spFavorilerimZaman.edit();

        int a = 0;
        for(int i = 0; i < 10; i++){
            //Toast.makeText(AyarlarActivity.this, ""+spBulduklarim.getString("" + (i+1),"null"), Toast.LENGTH_SHORT).show();
            spBulduklarimEdit.remove(""+(i+1));
            spFavorilerimEdit.remove(""+(i+1));
            spBulduklarimZamanEdit.remove(""+(i+1));
            spFavorilerimZamanEdit.remove(""+(i+1));
            spBulduklarimEdit.commit();
            spFavorilerimEdit.commit();
            spBulduklarimZamanEdit.commit();
            spFavorilerimZamanEdit.commit();
            if (i == 9) {
                /*for (File file : directory2.listFiles()) {
                    file.delete();
                }*/
                directory2.delete();
                for (int b = 0; b < 7; b++) {
                    String dosyaA = "foto_" + b + ".jpg";
                    File image2 = new File(directory, dosyaA);
                    if (image2.exists())
                        image2.delete();
                    if (b == 6) {
                        DatabaseReference digerRef = FirebaseDatabase.getInstance().getReference("Tokenlar");
                        digerRef.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                if (dataSnapshot.child(fuser.getUid()).exists())
                                    dataSnapshot.child(fuser.getUid()).getRef().removeValue();
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
                        if (sil) {
                            DatabaseReference digerRef2 = FirebaseDatabase.getInstance().getReference("SaibeliFotolar");
                            digerRef2.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    if (dataSnapshot.child("SaibeliFotolar").child(fuser.getUid()).exists())
                                        dataSnapshot.child("SaibeliFotolar").child(fuser.getUid()).getRef().removeValue();
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });
                            DatabaseReference ref = FirebaseDatabase.getInstance().getReference("usersF").child(fuser.getUid());
                            ref.removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(@NonNull Void unused) {
                                    fuser.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(@NonNull Void unused) {
                                            FirebaseAuth.getInstance().signOut();
                                            Intent intent = new Intent(AyarlarActivity.this, MainActivity.class);
                                            startActivity(intent);
                                        }
                                    });
                                }
                            });
                        }
                        else {
                            FirebaseAuth.getInstance().signOut();
                            Intent intent = new Intent(AyarlarActivity.this, MainActivity.class);
                            startActivity(intent);
                        }
                    }
                }
            }
        }

    }
    private void GorusBildir() {
        Dialog dialog = new Dialog(AyarlarActivity.this);
        dialog.setContentView(R.layout.dialog_dizayn_gorus_bildir);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setCancelable(true);

        TextView baslik = dialog.findViewById(R.id.baslik);
        EditText gorus = dialog.findViewById(R.id.gorus);
        LinearLayout lay1 = dialog.findViewById(R.id.lay1);
        ProgressBar pbar = dialog.findViewById(R.id.pbar);
        pbar.getIndeterminateDrawable().setColorFilter(ortaRenk,android.graphics.PorterDuff.Mode.MULTIPLY);
        InputFilter filter1 = new InputFilter.LengthFilter(200);
        gorus.setFilters(new InputFilter[] {filter1});
        baslik.setText("Görüş - Hata Bildir");
        Button buton = dialog.findViewById(R.id.buton);
        buton.setText("GÖNDER");
        buton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                butonTiklandiMi = false;
                pbar.setVisibility(View.VISIBLE);
                lay1.setVisibility(View.GONE);
                DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Gorusler");
                ref.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (gorus.getText().toString().length() > 10) {
                            if (dataSnapshot.exists()) {
                                if (dataSnapshot.hasChild(fuser.getUid())) {
                                    ref.child(fuser.getUid()).child("" + (dataSnapshot.child(fuser.getUid()).getChildrenCount() + 1)).child("gorus").setValue(gorus.getText().toString());
                                    ref.child(fuser.getUid()).child("" + (dataSnapshot.child(fuser.getUid()).getChildrenCount() + 1)).child("zaman").setValue(ServerValue.TIMESTAMP).addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(@NonNull Void unused) {
                                            Toast.makeText(AyarlarActivity.this, "Mesajınız bize ulaşmıştır", Toast.LENGTH_SHORT).show();
                                            dialog.dismiss();
                                        }
                                    });
                                } else {
                                    ref.child(fuser.getUid()).child("1").child("gorus").setValue(gorus.getText().toString());
                                    ref.child(fuser.getUid()).child("1").child("zaman").setValue(ServerValue.TIMESTAMP).addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(@NonNull Void unused) {
                                            Toast.makeText(AyarlarActivity.this, "Mesajınız bize ulaşmıştır", Toast.LENGTH_SHORT).show();
                                            dialog.dismiss();
                                        }
                                    });
                                }
                            } else {
                                ref.child(fuser.getUid()).child("1").child("gorus").setValue(gorus.getText().toString());
                                ref.child(fuser.getUid()).child("1").child("zaman").setValue(ServerValue.TIMESTAMP).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(@NonNull Void unused) {
                                        Toast.makeText(AyarlarActivity.this, "Mesajınız bize ulaşmıştır", Toast.LENGTH_SHORT).show();
                                        dialog.dismiss();
                                    }
                                });
                            }
                        }
                        else{
                            pbar.setVisibility(View.GONE);
                            lay1.setVisibility(View.VISIBLE);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

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

    private void OdulVer() {

        DatabaseReference odulRef = FirebaseDatabase.getInstance().getReference("Kodlar");
        odulRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    Dialog dialog = new Dialog(AyarlarActivity.this);
                    dialog.setContentView(R.layout.dialog_dizayn3);
                    dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

                    TextView baslik = dialog.findViewById(R.id.baslik);
                    TextView aciklama = dialog.findViewById(R.id.aciklama);
                    LinearLayout lay1 = dialog.findViewById(R.id.lay1);
                    LinearLayout lay2 = dialog.findViewById(R.id.lay2);
                    ProgressBar pbar = dialog.findViewById(R.id.pbar);
                    pbar.getIndeterminateDrawable().setColorFilter(ortaRenk,android.graphics.PorterDuff.Mode.MULTIPLY);
                    aciklama.setMovementMethod(new ScrollingMovementMethod());
                    Button buton = dialog.findViewById(R.id.buton);
                    EditText kod = dialog.findViewById(R.id.editText);
                    kod.setHint("Kod");
                    baslik.setText("Ödül Kodu");
                    aciklama.setText("Bir kod giriniz");
                    buton.setText("TAMAM");
                    buton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (kod.getText().toString().trim().length() > 7) {
                                lay1.setVisibility(View.GONE);
                                lay2.setVisibility(View.GONE);
                                pbar.setVisibility(View.VISIBLE);
                                if (dataSnapshot.hasChild(kod.getText().toString())) {
                                    DataSnapshot ds = dataSnapshot.child(kod.getText().toString());
                                    int Sayi = ds.child("sayi").getValue(Integer.class);
                                    int ekleyenSayisi = 0;
                                    if (ds.hasChild("ekleyenler"))
                                        ekleyenSayisi = (int) ds.child("ekleyenler").getChildrenCount();
                                    else
                                        ekleyenSayisi = 0;

                                    if (ekleyenSayisi != 0) {
                                        if (!ds.child("ekleyenler").hasChild(fuser.getUid())) {
                                            if (Sayi != 0) {
                                                if (Sayi > ekleyenSayisi)
                                                    OdulOnayi(ds, dialog, lay1, lay2, pbar, Sayi, kod);
                                                else {
                                                    Toast.makeText(AyarlarActivity.this, "Üzgünüz, bu koddan hiç kalmamış", Toast.LENGTH_LONG).show();
                                                    lay1.setVisibility(View.VISIBLE);
                                                    lay2.setVisibility(View.VISIBLE);
                                                    pbar.setVisibility(View.GONE);
                                                }
                                            } else
                                                OdulOnayi(ds, dialog, lay1, lay2, pbar, Sayi, kod);
                                        } else {
                                            Toast.makeText(AyarlarActivity.this, "Bu kodu zaten kullanmışsınız", Toast.LENGTH_LONG).show();
                                            lay1.setVisibility(View.VISIBLE);
                                            lay2.setVisibility(View.VISIBLE);
                                            pbar.setVisibility(View.GONE);
                                        }
                                    } else {
                                        if (Sayi != 0) {
                                            if (Sayi > ekleyenSayisi)
                                                OdulOnayi(ds, dialog, lay1, lay2, pbar, Sayi, kod);
                                            else {
                                                Toast.makeText(AyarlarActivity.this, "Üzgünüz, bu koddan hiç kalmamış", Toast.LENGTH_LONG).show();
                                                lay1.setVisibility(View.VISIBLE);
                                                lay2.setVisibility(View.VISIBLE);
                                                pbar.setVisibility(View.GONE);
                                            }
                                        } else
                                            OdulOnayi(ds, dialog, lay1, lay2, pbar, Sayi, kod);
                                    }
                                } else {
                                    Toast.makeText(AyarlarActivity.this, "Üzgünüz, böyle bir kod bulunamadı", Toast.LENGTH_LONG).show();
                                    lay1.setVisibility(View.VISIBLE);
                                    lay2.setVisibility(View.VISIBLE);
                                    pbar.setVisibility(View.GONE);
                                }
                            }
                            else
                                Toast.makeText(AyarlarActivity.this, "Çok kısa", Toast.LENGTH_LONG).show();
                        }
                    });
                    dialog.show();
                    butonTiklandiMi = false;
                }
                else{
                    butonTiklandiMi = false;
                    Toast.makeText(AyarlarActivity.this, "Üzgünüz, şuan aktif olan hiç bir kod yok :(", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                butonTiklandiMi = false;
            }
        });
    }
    private void OdulOnayi(DataSnapshot ds, Dialog dialog, LinearLayout lay1, LinearLayout lay2, ProgressBar pbar, int Sayi, EditText kod) {
        DatabaseReference kullaniciRef = FirebaseDatabase.getInstance().getReference("usersF").child(fuser.getUid());
        kullaniciRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot kullaniciDs) {
                ds.child("ekleyenler").getRef().child(fuser.getUid()).setValue(ServerValue.TIMESTAMP).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(@NonNull Void unused) {
                        GelirBilgisiEkle(kullaniciDs.getKey(),"kod_",ds.getKey(),ds.child("odul").getValue(Integer.class),dialog);
                    }
                });
                DatabaseReference tekrarRef = FirebaseDatabase.getInstance().getReference("Kodlar").child(kod.getText().toString());
                tekrarRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.child("ekleyenler").getChildrenCount() == dataSnapshot.child("sayi").getValue(Integer.class))
                            dataSnapshot.getRef().removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(@NonNull Void unused) {
                                }
                            });
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
    private void SSSGoster(String baslikYazisi, String icerik){
        Dialog dialog = new Dialog(AyarlarActivity.this);
        dialog.setContentView(R.layout.dialog_dizayn_sozlesme);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setCancelable(false);

        TextView baslik = dialog.findViewById(R.id.baslik);
        TextView aciklama = dialog.findViewById(R.id.aciklama);
        aciklama.setMovementMethod(new ScrollingMovementMethod());
        baslik.setText(baslikYazisi);
        aciklama.setText(icerik);
        Button buton = dialog.findViewById(R.id.buton);
        buton.setText("TAMAM");
        buton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    @Override
    protected void onResume() {
        super.onResume();

        SharedPreferences tas_shared = getSharedPreferences("TasarimBilgileri",MODE_PRIVATE);
        TasarimDegistir(tas_shared.getString("tasarim_arayuz","1"));
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
    public void TasarimDegistir(String tasDegeri) {
        GradientDrawable gradient = (GradientDrawable) getResources().getDrawable(R.drawable.gradient);
        gradient.setOrientation(GradientDrawable.Orientation.LEFT_RIGHT);

        int renk1 = 0,renk2 = 0,t1start = 0,t2start = 0,t1end = 0,t2end = 0,orta = 0;

        renk1 = TasarimRenginiGetir.RengiGetir(AyarlarActivity.this,"renk1",tasDegeri);
        renk2 = TasarimRenginiGetir.RengiGetir(AyarlarActivity.this,"renk2",tasDegeri);
        t1start = TasarimRenginiGetir.RengiGetir(AyarlarActivity.this,"t1start",tasDegeri);
        t2start = TasarimRenginiGetir.RengiGetir(AyarlarActivity.this,"t2start",tasDegeri);
        t1end = TasarimRenginiGetir.RengiGetir(AyarlarActivity.this,"t1end",tasDegeri);
        t2end = TasarimRenginiGetir.RengiGetir(AyarlarActivity.this,"t2end",tasDegeri);
        orta = TasarimRenginiGetir.RengiGetir(AyarlarActivity.this,"orta",tasDegeri);

        ColorStateList buttonStates = new ColorStateList(
                new int[][]{
                        new int[]{-android.R.attr.state_enabled},
                        new int[]{android.R.attr.state_checked},
                        new int[]{}
                },
                new int[]{
                        getResources().getColor(R.color.transKoyuGri),
                        orta,
                        getResources().getColor(R.color.gri4)
                }
        );
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            begeni_bildirim.getThumbDrawable().setTintList(buttonStates);
            mesaj_bildirim.getThumbDrawable().setTintList(buttonStates);
            tum_bildirimler.getThumbDrawable().setTintList(buttonStates);
            ziyaret_bildirim.getThumbDrawable().setTintList(buttonStates);
        }


        gradient.setColors(new int[]{
                renk1,
                orta,
                renk2,
        });

        ortaRenk = orta;
        baslik1.setTextColor(orta);
        baslik2.setTextColor(orta);
        baslik3.setTextColor(orta);
        baslik4.setTextColor(orta);
        baslik5.setTextColor(orta);

        background.setBackground(gradient);
    }
    private void SnapshotGuncelle(){
        final DatabaseReference ref = FirebaseDatabase.getInstance().getReference("usersF").child(fuser.getUid());
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                snapShot = dataSnapshot;
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
    private void GizlilikSec(int hangisi, int checkDegeri){
        final String[] secenekler = {"Benim beğendiklerim","Beni beğenenler","Mesajlaştıklarım","Hiç Kimse","Herkes"};
        final AlertDialog.Builder alert = new AlertDialog.Builder(AyarlarActivity.this);
        if (hangisi == 0) {
            alert.setTitle("Ad ve Yaş - Kimler görebilsin?");

        }
        if (hangisi == 1) {
            alert.setTitle("Fotoğraflar - Kimler görebilsin?");
        }

        if (hangisi == 2) {
            alert.setTitle("Konum Ziyaretlerimi - Kimler görebilsin?");
        }

        alert.setCancelable(true);
        alert.setSingleChoiceItems(secenekler,checkDegeri, new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int i)
            {
                butonTiklandiMi = false;
                if (hangisi == 0)
                    snapShot.child("gizlilik_ad").getRef().setValue("" + i);

                else if (hangisi == 1)
                    snapShot.child("gizlilik_foto").getRef().setValue("" + i);

                else if (hangisi == 2)
                    snapShot.child("gizlilik_mekan").getRef().setValue("" + i);

                SnapshotGuncelle();
                dialog.dismiss();
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
