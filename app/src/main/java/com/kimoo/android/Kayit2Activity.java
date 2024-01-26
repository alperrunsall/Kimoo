package com.kimoo.android;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.provider.Settings;
import android.text.InputFilter;
import android.text.Spanned;
import android.text.method.ScrollingMovementMethod;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
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
import com.theartofdev.edmodo.cropper.CropImageView;
import com.kimoo.android.extra.TasarimRenginiGetir;
import com.kimoo.android.extra.YardimSayfasi;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Random;

import de.hdodenhof.circleimageview.CircleImageView;

public class Kayit2Activity extends AppCompatActivity {

    FirebaseDatabase firebaseDatabase;
    DatabaseReference myRef;
    private FirebaseAuth mAuth;
    DatePickerDialog dpd;
    private FirebaseUser user;
    BluetoothAdapter myAdapter;
    CircleImageView pp;
    private ImageView yardim;
    Button yass,cinsiyett,devamBTN,sehir;
    public String cinsiyet;
    private int ortaRenk;
    private boolean dialogAktifMi = false;
    EditText ad;
    private TextView sartlar,sozlesme,sss;
    private String sozlesmeYazisi, sartlarYazisi, sssYazisi, uaLink;
    long ksayisi, dogumTarihi;
    private LinearLayout background;
    private Rect rect;
    private int yas,maxHarf = 0, minHarf = 4, sehirNo;
    private StorageReference mStorageRef;
    String olusanId = "",ppurl = "", uid;
    FirebaseUser fuser;
    Dialog ppYukleDialog;
    private boolean devam,ppyuklendimi,uname, bilgilendirmeyiActiMi = false;
    StringBuilder randomStringBuilder;
    private Uri resimURL;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kayit2);
        StatuBarAyarla();
        fuser = FirebaseAuth.getInstance().getCurrentUser();
        mAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        myRef = firebaseDatabase.getReference("usersF");
        myAdapter = BluetoothAdapter.getDefaultAdapter();
        yass = findViewById(R.id.kayit_yas);
        cinsiyett = findViewById(R.id.kayit_cinsiyet);
        ad = findViewById(R.id.kayit_ad);
        yardim = findViewById(R.id.fotoyardim);
        background = findViewById(R.id.background);

        sartlar = findViewById(R.id.sartlar);
        sozlesme = findViewById(R.id.sozlesme);
        sss = findViewById(R.id.sss);
        sehir = findViewById(R.id.kayit_sehir);
        pp = findViewById(R.id.kayit_pp);
        devamBTN = findViewById(R.id.button4);
        cinsiyet = "0";
        Intent gelenDegerBT = getIntent();
        String bt = gelenDegerBT.getStringExtra("birthday");
        mStorageRef = FirebaseStorage.getInstance().getReference(fuser.getUid());
        olusanId = getUID();
        if (bt != null)
            yass.setText(bt);
        else {
            yass.setText("");
        }
        myAdapter = BluetoothAdapter.getDefaultAdapter();


        FirebaseDatabase.getInstance().getReference("Sistem").child("yazilar").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                sozlesmeYazisi = dataSnapshot.child("gizlilik_sozlesmesi").getValue(String.class);
                sartlarYazisi = dataSnapshot.child("kullanim_sartlari").getValue(String.class);
                sssYazisi = dataSnapshot.child("sss").getValue(String.class);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        sozlesme.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (sozlesmeYazisi != null){
                    Uri uri = Uri.parse(sozlesmeYazisi);
                    Toast.makeText(Kayit2Activity.this, "Bağlantı Açılıyor...", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                    startActivity(intent);
                }
                else
                    Toast.makeText(Kayit2Activity.this, "İnternet bağlantınız yavaş lütfen tekrar deneyin.", Toast.LENGTH_SHORT).show();
            }
        });
        sartlar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (sartlarYazisi != null){
                    Uri uri = Uri.parse(sartlarYazisi);
                    Toast.makeText(Kayit2Activity.this, "Bağlantı Açılıyor...", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                    startActivity(intent);
                }
                else
                    Toast.makeText(Kayit2Activity.this, "İnternet bağlantınız yavaş lütfen tekrar deneyin.", Toast.LENGTH_SHORT).show();
            }
        });

        sss.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (sssYazisi != null){
                    Uri uri = Uri.parse(sssYazisi);
                    Toast.makeText(Kayit2Activity.this, "Bağlantı Açılıyor...", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                    startActivity(intent);
                }
                else
                    Toast.makeText(Kayit2Activity.this, "İnternet bağlantınız yavaş lütfen tekrar deneyin.", Toast.LENGTH_SHORT).show();
            }
        });

        if(internetVarmi()) {
            yass.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final Calendar c = Calendar.getInstance();
                    int day = c.get(Calendar.DAY_OF_MONTH);
                    int month = c.get(Calendar.MONTH);
                    int year = c.get(Calendar.YEAR) - getResources().getInteger(R.integer.alt_yas_siniri);

                    dpd = new DatePickerDialog(Kayit2Activity.this,R.style.DialogTheme, new DatePickerDialog.OnDateSetListener() {
                        @Override
                        public void onDateSet(DatePicker view, int myear, int mmonth, int dayOfMonth) {
                            if(getAge(myear, mmonth, dayOfMonth) > 12)
                                yass.setText("" + getAge(myear, mmonth, dayOfMonth));
                            else
                                yass.setText("");
                        }
                    }, year, month, day);
                    dpd.show();
                }
            });
            sehir.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final String[] sehirler = {"Adana", "Adıyaman", "Afyon", "Ağrı", "Amasya", "Ankara", "Antalya", "Artvin",
                            "Aydın", "Balıkesir", "Bilecik", "Bingöl", "Bitlis", "Bolu", "Burdur", "Bursa", "Çanakkale",
                            "Çankırı", "Çorum", "Denizli", "Diyarbakır", "Edirne", "Elazığ", "Erzincan", "Erzurum ", "Eskişehir",
                            "Gaziantep", "Giresun", "Gümüşhane", "Hakkari", "Hatay", "Isparta", "Mersin", "İstanbul", "İzmir",
                            "Kars", "Kastamonu", "Kayseri", "Kırklareli", "Kırşehir", "Kocaeli", "Konya", "Kütahya ", "Malatya",
                            "Manisa", "Kahramanmaraş", "Mardin", "Muğla", "Muş", "Nevşehir", "Niğde", "Ordu", "Rize", "Sakarya",
                            "Samsun", "Siirt", "Sinop", "Sivas", "Tekirdağ", "Tokat", "Trabzon  ", "Tunceli", "Şanlıurfa", "Uşak",
                            "Van", "Yozgat", "Zonguldak", "Aksaray", "Bayburt ", "Karaman", "Kırıkkale", "Batman", "Şırnak",
                            "Bartın", "Ardahan", "Iğdır", "Yalova", "Karabük ", "Kilis", "Osmaniye ", "Düzce"};
                    final AlertDialog.Builder alert = new AlertDialog.Builder(Kayit2Activity.this);
                    alert.setTitle("Şehrinizi Seçiniz");
                    alert.setSingleChoiceItems(sehirler, -1, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int i) {
                            sehirNo = i + 1;
                            sehir.setText(sehirler[i]);
                            dialog.dismiss();

                            /*DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Yerler");
                            reference.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    if (dataSnapshot.hasChild("" + (i + 1))) {
                                        sehirNo = i + 1;
                                        sehir.setText(sehirler[i]);
                                        dialog.dismiss();
                                    }
                                    else {
                                        Dialog dialog2 = new Dialog(Kayit2Activity.this);
                                        dialog2.setContentView(R.layout.dialog_dizayn_sozlesme);
                                        dialog2.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                                        dialog2.setCancelable(false);

                                        TextView baslik = dialog2.findViewById(R.id.baslik);
                                        TextView aciklama = dialog2.findViewById(R.id.aciklama);
                                        aciklama.setMovementMethod(new ScrollingMovementMethod());
                                        baslik.setText("Üzgünüz");
                                        aciklama.setText("Şuanda sadece Eskişehir ili içerisinde Kimoo'nun popülerleşmesini hedeflediğimizden, ne yazık ki belirttiğiniz şehirde uygulamayı şimdilik kullanamazsınız. Geri dönüp Eskişehiri seçerseniz, uygulamayı sadece bir arayüz olarak kullanabilirsiniz diğer kullanıcılarla etkileşime geçemezsiniz. Lütfen şehrinize yapacağımız çalışmalarımızın bitmesini bekleyin ve bizlere desteğinizi sürdürmek isterseniz uygulamayı silmemeyi tercih edebilirsiniz :) Güncellemelerimiz geldikçe uygulamaya tekrar göz atmanızı tavsiye ederiz.");
                                        Button buton = dialog2.findViewById(R.id.buton);
                                        buton.setText("TAMAM");
                                        buton.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                dialog2.dismiss();
                                            }
                                        });
                                        dialog2.show();
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });*/
                        }
                    });
                    alert.show();
                }
            });
            cinsiyett.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final CharSequence[] gender = {"Erkek", "Kadın"};
                    final AlertDialog.Builder alert = new AlertDialog.Builder(Kayit2Activity.this);
                    alert.setTitle("Cinsiyetinizi Seçiniz");
                    alert.setSingleChoiceItems(gender, -1, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            if (gender[which] == "Erkek") {
                                cinsiyet = "1";
                                cinsiyett.setText("Erkek");
                                dialog.dismiss();
                            } else if (gender[which] == "Kadın") {
                                cinsiyet = "2";
                                cinsiyett.setText("Kadın");
                                dialog.dismiss();
                            }
                        }
                    });
                    alert.show();
                }
            });
            myRef = FirebaseDatabase.getInstance().getReference("usersF");

        /*username.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                UsernameKontrol(s);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });*/


            DatabaseReference dref = FirebaseDatabase.getInstance().getReference("usersF");
            dref.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    ksayisi = dataSnapshot.getChildrenCount();
                    if (ksayisi <= 7000000) {
                        maxHarf = 0;
                    }
                    if (ksayisi > 7000000 && ksayisi <= 70000000) {
                        maxHarf = 1;
                    }
                    if (ksayisi > 70000000 && ksayisi <= 600000000) {
                        maxHarf = 2;
                    }
                    if (ksayisi > 600000000 && ksayisi <= 4000000000L) {
                        maxHarf = 3;
                    }
                    if (ksayisi > 4000000000L) {
                        maxHarf = 4;
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

            ppYukleDialog = new Dialog(Kayit2Activity.this);
            ppYukleDialog.setContentView(R.layout.dialog_dizayn2);
            ppYukleDialog.setCancelable(false);
            ppYukleDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

            TextView baslik = ppYukleDialog.findViewById(R.id.baslik);
            TextView aciklama = ppYukleDialog.findViewById(R.id.aciklama);
            LinearLayout lay1 = ppYukleDialog.findViewById(R.id.lay1);
            ProgressBar pbar = ppYukleDialog.findViewById(R.id.pbar);
            aciklama.setMovementMethod(new ScrollingMovementMethod());
            Button buton = ppYukleDialog.findViewById(R.id.buton);
            baslik.setText("Profil Fotoğrafı");
            buton.setText("YÜKLE");
            aciklama.setText("Lütfen bir profil fotoğrafı yükleyin. Profil fotoğrafınız size ait olmalıdır.");
            buton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    FotoEkle();
                    ppYukleDialog.dismiss();
                }
            });
            ppYukleDialog.show();

            pp.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    if (event.getAction() == MotionEvent.ACTION_DOWN) {
                        rect = new Rect(v.getLeft(), v.getTop(), v.getRight(), v.getBottom());
                        v.animate()
                                .scaleX(1 - 0.05f)
                                .scaleY(1 - 0.05f)
                                .setDuration(100)
                                .start();
                    }
                    if (event.getAction() == MotionEvent.ACTION_UP) {
                        if (rect.contains((int) event.getX(), (int) event.getY())) {
                            v.animate()
                                    .scaleX(1)
                                    .scaleY(1)
                                    .setDuration(100)
                                    .start();
                            FotoEkle();
                        }
                    }
                    if (event.getAction() == MotionEvent.ACTION_MOVE) {
                        if (!rect.contains((int) event.getX(), (int) event.getY())) {
                            v.animate()
                                    .scaleX(1)
                                    .scaleY(1)
                                    .setDuration(100)
                                    .start();
                        } else if (event.getAction() == MotionEvent.ACTION_UP) {
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
            yardim.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    if (event.getAction() == MotionEvent.ACTION_DOWN) {
                        rect = new Rect(v.getLeft(), v.getTop(), v.getRight(), v.getBottom());
                        v.animate()
                                .scaleX(1 - 0.05f)
                                .scaleY(1 - 0.05f)
                                .setDuration(100)
                                .start();
                    }
                    if (event.getAction() == MotionEvent.ACTION_UP) {
                        if (rect.contains((int) event.getX(), (int) event.getY())) {
                            v.animate()
                                    .scaleX(1)
                                    .scaleY(1)
                                    .setDuration(100)
                                    .start();
                            bilgilendirmeyiActiMi = true;
                            YardimSayfasi yardimSayfasi = new YardimSayfasi(Kayit2Activity.this);
                            View sheetView = getLayoutInflater().inflate(R.layout.yardim_menu, null);
                            yardimSayfasi.setContentView(sheetView);
                            yardimSayfasi.show();
                        }
                    }
                    if (event.getAction() == MotionEvent.ACTION_MOVE) {
                        if (!rect.contains((int) event.getX(), (int) event.getY())) {
                            v.animate()
                                    .scaleX(1)
                                    .scaleY(1)
                                    .setDuration(100)
                                    .start();
                        } else if (event.getAction() == MotionEvent.ACTION_UP) {
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
            devamBTN.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(final View view, MotionEvent motionEvent) {
                    int action = motionEvent.getAction();
                    if (action == MotionEvent.ACTION_DOWN) {
                        startScaleAnimation(view);
                        return true;
                    } else if (action == MotionEvent.ACTION_UP) {
                        cancelScaleAnimation(view,1);
                        return true;
                    }
                    return false;
                }
            });
        }
    }

    private void DialogOlustur(String baslikYazisi, String icerik){
        Dialog dialog = new Dialog(Kayit2Activity.this);
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
                                    if (internetVarmi())
                                        Randomla();
                                    else
                                        Toast.makeText(Kayit2Activity.this, "İnternet bağlantınız yok", Toast.LENGTH_SHORT).show();
                                }
                                else if(i == 2){

                                }
                                else if(i == 3){

                                }
                                else if(i == 4){

                                }
                            }
                        });
                    }
                });
            }
        });

    }
    public void TasarimDegistir(String tasDegeri) {
        GradientDrawable gradient = (GradientDrawable) getResources().getDrawable(R.drawable.gradient);
        gradient.setOrientation(GradientDrawable.Orientation.LEFT_RIGHT);

        GradientDrawable gradientYumusak = new GradientDrawable();
        gradientYumusak.setOrientation(GradientDrawable.Orientation.LEFT_RIGHT);
        gradientYumusak.setCornerRadius(70);

        GradientDrawable gradientYumusak2 = new GradientDrawable();
        gradientYumusak2.setOrientation(GradientDrawable.Orientation.LEFT_RIGHT);
        gradientYumusak2.setCornerRadius(60);

        int renk1 = 0,renk2 = 0,t1start = 0,t2start = 0,t1end = 0,t2end = 0,orta = 0;

        renk1 = TasarimRenginiGetir.RengiGetir(Kayit2Activity.this,"renk1",tasDegeri);
        renk2 = TasarimRenginiGetir.RengiGetir(Kayit2Activity.this,"renk2",tasDegeri);
        t1start = TasarimRenginiGetir.RengiGetir(Kayit2Activity.this,"t1start",tasDegeri);
        t2start = TasarimRenginiGetir.RengiGetir(Kayit2Activity.this,"t2start",tasDegeri);
        t1end = TasarimRenginiGetir.RengiGetir(Kayit2Activity.this,"t1end",tasDegeri);
        t2end = TasarimRenginiGetir.RengiGetir(Kayit2Activity.this,"t2end",tasDegeri);
        orta = TasarimRenginiGetir.RengiGetir(Kayit2Activity.this,"orta",tasDegeri);

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
        gradientYumusak2.setColors(new int[]{
                t1end,
                orta,
                t2start,
        });
        GradientDrawable gradientEt1 = new GradientDrawable();
        gradientEt1.setStroke(3,orta);
        gradientEt1.setOrientation(GradientDrawable.Orientation.LEFT_RIGHT);
        gradientEt1.setCornerRadius(100);
        GradientDrawable gradientEt2 = new GradientDrawable();
        gradientEt2.setStroke(3,orta);
        gradientEt2.setOrientation(GradientDrawable.Orientation.LEFT_RIGHT);
        gradientEt2.setCornerRadius(100);
        GradientDrawable gradientEt3 = new GradientDrawable();
        gradientEt3.setStroke(3,orta);
        gradientEt3.setOrientation(GradientDrawable.Orientation.LEFT_RIGHT);
        gradientEt3.setCornerRadius(100);
        GradientDrawable gradientEt4 = new GradientDrawable();
        gradientEt4.setStroke(3,orta);
        gradientEt4.setOrientation(GradientDrawable.Orientation.LEFT_RIGHT);
        gradientEt4.setCornerRadius(100);
        ad.setBackground(gradientEt1);
        sehir.setBackground(gradientEt2);
        cinsiyett.setBackground(gradientEt3);
        yass.setBackground(gradientEt4);

        ortaRenk = orta;
        yardim.setColorFilter(orta);
        devamBTN.setBackground(gradientYumusak);
        background.setBackground(gradient);
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

    private boolean internetVarmi(){
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED)
            return true;
        else {
            Toast.makeText(Kayit2Activity.this, "İnternet bağlantınız yok.", Toast.LENGTH_SHORT).show();
            return false;
        }
    }
    void FotoEkle(){

        Intent pickIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        pickIntent.setType("image/*");
        startActivityForResult(pickIntent, 99);

        /*CropImage.activity()
                .setGuidelines(CropImageView.Guidelines.ON)
                .setMinCropWindowSize(500, 500)
                .setAspectRatio(1, 1)
                .setActivityTitle("Resmi Kırp")
                .setCropMenuCropButtonTitle("Kırp")
                .setBorderLineColor(getResources().getColor(R.color.beyaz))
                .start(Kayit2Activity.this);*/
    }

    /*private void UsernameKontrol(CharSequence s) {
        if(s.toString().length() > 5){
            Query usernameQuery = FirebaseDatabase.getInstance().getReference().child("usersF").orderByChild("usernamef").equalTo(username.getText().toString().trim());
            usernameQuery.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if(dataSnapshot.getChildrenCount() > 0){
                        username.setError("Bu kullanıcı adı zaten var!");
                        devam = false;
                    }else{
                        username.setError(null);
                        devam = true;
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    devam = false;
                }
            });
        }else{
            username.setError("Kullanıcı adınız en az 6 karakterden oluşmalı.");
            devam = false;
        }
    }*/
    void Randomla(){
        if (bilgilendirmeyiActiMi) {
            if (!uname) {
                Random generator = new Random();
                randomStringBuilder = new StringBuilder();
                int randomLength = generator.nextInt(maxHarf + 1) + minHarf;
                char tempChar = 0;
                for (int i = 0; i < randomLength; i++) {
                    tempChar = (char) (generator.nextInt(63 + 1) + 33);
                    randomStringBuilder.append(tempChar);
                }
                RandomUNAME();
            }
        }
        else{
            Toast.makeText(Kayit2Activity.this, "Önce uyarılara göz atmalısınız", Toast.LENGTH_SHORT).show();
            YardimSayfasi yardimSayfasi = new YardimSayfasi(Kayit2Activity.this);
            View sheetView = getLayoutInflater().inflate(R.layout.yardim_menu, null);
            yardimSayfasi.setContentView(sheetView);
            yardimSayfasi.show();
            bilgilendirmeyiActiMi = true;
        }
    }
    public void RandomUNAME() {
        if(!uname) {
            Query usernameQuery = FirebaseDatabase.getInstance().getReference("usersF").orderByChild("usernamef").equalTo(randomStringBuilder.toString());
            usernameQuery.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.getChildrenCount() > 0) {
                        devam = false;
                        Randomla();
                    } else {
                        uid = randomStringBuilder.toString().trim().toLowerCase();
                        devam = true;
                        Kontrol();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    devam = false;
                    Randomla();
                }
            });
        }
    }
    private void Kontrol(){
        if(devam) {
            if (!yass.getText().equals("") && !cinsiyett.getText().equals("") && !ad.getText().toString().trim().equals("") && !sehir.getText().toString().trim().equals("")) {
                if(Integer.valueOf(yass.getText().toString()) > getResources().getInteger(R.integer.ust_yas_siniri)){
                    final AlertDialog.Builder alert = new AlertDialog.Builder(Kayit2Activity.this);
                    alert.setMessage("Yaşınız " + yass.getText() + " onaylıyor musunuz?");
                    alert.setPositiveButton("Evet", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
                            myRef.child(fuser.getUid()).child("ban_durumu").child("durum").setValue("var");
                            myRef.child(fuser.getUid()).child("ban_durumu").child("sebep").setValue("yaş");
                            myRef.child(fuser.getUid()).child("ban_durumu").child("tarih").setValue(ServerValue.TIMESTAMP);
                            myRef.child(fuser.getUid()).child("ban_durumu").child("sure").setValue(1);
                            myRef.child(fuser.getUid()).child("ban_durumu").child("banlayan").setValue("sistem");
                            myRef.child(fuser.getUid()).child("ban_durumu").child("aciklama").setValue("");
                            myRef.child(fuser.getUid()).child("suan").setValue(ServerValue.TIMESTAMP);
                            myRef.child(fuser.getUid()).child("ad").setValue(ad.getText().toString().toLowerCase());
                            myRef.child(fuser.getUid()).child("sehir").setValue(""+sehirNo);
                            myRef.child(fuser.getUid()).child("dg").setValue(yass.getText().toString());
                            myRef.child(fuser.getUid()).child("cinsiyet").setValue(cinsiyett.getText().toString());
                            myRef.child(fuser.getUid()).child("email").setValue(fuser.getEmail());
                            myRef.child(fuser.getUid()).child("kayit_tarihi").setValue(ServerValue.TIMESTAMP);
                            myRef.child(fuser.getUid()).child("uid").setValue(fuser.getUid());
                            ref.child("uid_ban").child(fuser.getUid()).setValue(ServerValue.TIMESTAMP);
                            //ref.child(fuser.getUid()).child("device_token").setValue(DeviceToken);
                            //myRef.child(fuser.getUid()).child("bulduklarim").child("zoEbbpAxvwOTKS1tv7iTtsftRUs2").child("kac_kez_gordum").setValue("1");
                            if(!uname) {
                                myRef.child(fuser.getUid()).child("usernamef").setValue(uid);
                                uname = true;
                            }
                            DatabaseReference banRef = FirebaseDatabase.getInstance().getReference("tel_ban");
                            banRef.child(Settings.Secure.getString(getApplicationContext().getContentResolver(), Settings.Secure.ANDROID_ID)).setValue(fuser.getUid()).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(@NonNull Void unused) {
                                    Toast.makeText(Kayit2Activity.this, "Telefonunuz ve Hesabınız, Kimoo'dan yaş sebebi ile süresiz olarak uzaklaştırıldı.", Toast.LENGTH_LONG).show();
                                    startActivity(new Intent(Kayit2Activity.this, GirisActivity.class));
                                }
                            });

                        }
                    }).setNegativeButton("Hayır", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                        }
                    });
                    alert.show();
                }
                else{
                    if(Integer.valueOf(yass.getText().toString()) > 17) {
                        Kontrol2();
                    }
                    else{
                        Dialog dialog2 = new Dialog(Kayit2Activity.this);
                        dialog2.setContentView(R.layout.dialog_dizayn5);
                        dialog2.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

                        LinearLayout lay1 = dialog2.findViewById(R.id.lay1);
                        LinearLayout lay2 = dialog2.findViewById(R.id.lay2);
                        ProgressBar pbar = dialog2.findViewById(R.id.pbar);
                        pbar.getIndeterminateDrawable().setColorFilter(ortaRenk,android.graphics.PorterDuff.Mode.MULTIPLY);
                        TextView baslik = dialog2.findViewById(R.id.baslik);
                        TextView aciklama = dialog2.findViewById(R.id.aciklama);
                        aciklama.setMovementMethod(new ScrollingMovementMethod());
                        Button buton = dialog2.findViewById(R.id.buton);
                        Button buton2 = dialog2.findViewById(R.id.buton2);
                        baslik.setText("UYARI");
                        aciklama.setText("Henüz 18 yaşında değilsiniz, uygulamaya hala kayıt olmak istiyorsanız ebeveyninize danışmanızı tavsiye ederiz. Sizin yaşınız: " + Integer.valueOf(yass.getText().toString()) + "\n Uygulamada etkileşime geçebileceğiniz yaş aralığı: "+getResources().getInteger(R.integer.alt_yas_siniri)+"-" + (Integer.valueOf(yass.getText().toString())+5) + "\n Devam etmek istiyor musunuz?");
                        buton.setText("HAYIR");
                        buton2.setText("EVET");
                        buton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                finishAffinity();
                            }
                        });
                        buton2.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Kontrol2();
                                dialog2.dismiss();
                            }
                        });
                        dialog2.show();
                    }
                }
            } else {
                Toast.makeText(this, "Boş alanları doldurunuz.", Toast.LENGTH_SHORT).show();
            }
        }else{
            Toast.makeText(this, "Kullanıcı adınızı kontrol ediniz!", Toast.LENGTH_SHORT).show();
        }
    }
    private void Kontrol2(){
        if (!ad.getText().toString().trim().equals("") && ad.getText() != null) {
            if (ad.getText().toString().trim().length() > 3) {
                if (ad.getText().toString().trim().length() < 12 && ad.getText().toString().trim().length() > 3) {
                    String hata = "";
                    int varDegeri = 0;
                    int sesli = 0;
                    int sessiz = 0;
                    String birOncekiHarf = "";
                    List<String> sesliHarfler = Arrays.asList("a", "e", "ı", "i", "o", "ö", "u", "ü");
                    List<String> sessizHarfler = Arrays.asList("f", "r", "t", "y", "p", "ğ", "s", "d", "g", "h", "j", "k", "l", "ş", "z", "c", "v", "b", "n", "m", "ç");

                    for (int i = 0; i < ad.getText().toString().length(); i++) {
                        char harf = ad.getText().toString().toLowerCase().charAt(i);
                        String ad = String.valueOf(harf);

                        if (!birOncekiHarf.equals("m") && !birOncekiHarf.equals("r") && ad.equals(birOncekiHarf)) {
                            varDegeri++;
                            hata = "İsminizde 2 tane ard arda gelen aynı harf var. Lütfen sadece 1 tanesini yazın.";
                        }

                        if (i == 0 && ad.equals("ğ")) {
                            varDegeri++;
                            hata = "İsminiz Ğ harfi ile başlayamaz.";
                        }

                        if (sesliHarfler.contains(ad)) {
                            sesli++;
                            sessiz = 0;
                            if (sesli >= 3) {
                                varDegeri++; // yanyana 3 sesli harfin olduğu anlamına gelir
                                hata = "İsminizde çok fazla arka arkaya gelen sesli harf var.";
                            }
                        } else {
                            sessiz++;
                            sesli = 0;
                            if (sessiz >= 3) {
                                varDegeri++; // yanyana 3 sessiz harfin olduğu anlamına gelir
                                hata = "İsminizde çok fazla arka arkaya sessiz harf var.";
                            }
                        }

                        if (ad.matches("\\p{Punct}") || ad.matches("\\d+(?:\\.\\d+)?")) {
                            varDegeri++;
                            hata = "İsminizde noktalama işareti veya rakam bulunamaz.";
                        }
                        if (ad.equals("q") || ad.equals("x") || ad.equals("w")) {
                            varDegeri++;
                            hata = "Eğer isminizde Türkçe karakterlerin dışında bir harf varsa bu harfi değiştirmelisiniz. Örnek: Q-K, W-V, X-Z";
                        }

                        birOncekiHarf = ad; // 2 tane aynı harf arka arkaya yazılamaz

                    }
                    if (varDegeri > 0) {
                        Toast.makeText(this, hata, Toast.LENGTH_LONG).show();
                    } else {
                        devamBTN.setText("Kayıt tamamlanıyor...");
                        //ResimKontrol(resimURL);
                        ResimYukle(false, "");
                    }

                } else
                    Toast.makeText(this, "İsim çok uzun. Birden fazla isminiz varsa lütfen bir tanesini yazınız.", Toast.LENGTH_SHORT).show();
            } else
                Toast.makeText(this, "İsim çok kısa.", Toast.LENGTH_SHORT).show();
        }
    }
    private void ResimKontrol(Uri uri){
        InputImage image = null;
        try {
            image = InputImage.fromFilePath(Kayit2Activity.this, uri);
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        TextRecognizer recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS);
        recognizer.process(image).addOnSuccessListener(new OnSuccessListener<Text>() {
            @Override
            public void onSuccess(@NonNull Text text) {
                if(text.getText().contains("@") || text.getText().contains("/") || text.getText().contains("_") || text.getText().toLowerCase().contains("tw")
                        || text.getText().toLowerCase().contains("ins") || text.getText().toLowerCase().contains("yt") || text.getText().toLowerCase().contains("twitter")
                        || text.getText().toLowerCase().contains("instagram") || text.getText().toLowerCase().contains("youtube") || text.getText().toLowerCase().contains("tiktok")){
                    Toast.makeText(Kayit2Activity.this, "Fotoğrafınızda link/adres/kullanıcı adı olabilecek harfler algılandı. Lütfen başka fotoğraf ekleyiniz.", Toast.LENGTH_LONG).show();
                }
                else if(text.getText().length() > 5){
                    ResimYukle(true,text.getText().toLowerCase());
                }
                else{
                    ResimYukle(false,text.getText().toLowerCase());
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                //Toast.makeText(ProfilActivity.this, ""+e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
            }
        });

       /* FirebaseVisionImage image = FirebaseVisionImage.fromBitmap(bitmap);
        FirebaseVisionTextRecognizer textRecognizer = FirebaseVision.getInstance().getCloudTextRecognizer();
        textRecognizer.processImage(image).addOnSuccessListener(new OnSuccessListener<FirebaseVisionText>() {
            @Override
            public void onSuccess(FirebaseVisionText firebaseVisionText) {
                Toast.makeText(ProfilActivity.this, "tamam", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(ProfilActivity.this, "hata " + e.getLocalizedMessage(), Toast.LENGTH_LONG).show();

            }
        });*/

    }
    private void SaibeEkle(Dialog dialog, Uri uri, String yazi) {
        final DatabaseReference[] sRef = {FirebaseDatabase.getInstance().getReference("SaibeliFotolar")};
        sRef[0].addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    if (dataSnapshot.hasChild(fuser.getUid())) {
                        DataSnapshot asilData = dataSnapshot.child(fuser.getUid());
                        long a = dataSnapshot.child(fuser.getUid()).getChildrenCount();
                        asilData.child(String.valueOf(a)).child("url").getRef().setValue(uri.toString());
                        asilData.child(String.valueOf(a)).child("zaman").getRef().setValue(ServerValue.TIMESTAMP);
                        asilData.child(String.valueOf(a)).child("saibe_sebebi").getRef().setValue("yazı algılandı");
                        asilData.child(String.valueOf(a)).child("delil").getRef().setValue(yazi);
                    } else {
                        sRef[0].child(fuser.getUid()).child("0").child("url").setValue(uri.toString());
                        sRef[0].child(fuser.getUid()).child("0").child("zaman").setValue(ServerValue.TIMESTAMP);
                        sRef[0].child(fuser.getUid()).child("0").child("saibe_sebebi").setValue("yazı algılandı");
                        sRef[0].child(fuser.getUid()).child("0").child("delil").setValue(yazi);
                    }
                    dialog.dismiss();
                    ppurl = uri.toString();
                    ppyuklendimi = true;
                    SiradakiActivity();
                } else {
                    sRef[0].child(fuser.getUid()).child("0").child("url").setValue(uri.toString());
                    sRef[0].child(fuser.getUid()).child("0").child("zaman").setValue(ServerValue.TIMESTAMP);
                    sRef[0].child(fuser.getUid()).child("0").child("saibe_sebebi").setValue("yazı algılandı");
                    sRef[0].child(fuser.getUid()).child("0").child("delil").setValue(yazi);
                    dialog.dismiss();
                    ppurl = uri.toString();
                    ppyuklendimi = true;
                    SiradakiActivity();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
    private void SiradakiActivity() {
        if(ppyuklendimi) {
            if (!dialogAktifMi) {
                Dialog dialog = new Dialog(Kayit2Activity.this);
                dialog.setContentView(R.layout.dialog_dizayn3);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.setCancelable(false);
                TextView baslik = dialog.findViewById(R.id.baslik);
                TextView aciklama = dialog.findViewById(R.id.aciklama);
                Button buton = dialog.findViewById(R.id.buton);
                LinearLayout lay1 = dialog.findViewById(R.id.lay1);
                LinearLayout lay2 = dialog.findViewById(R.id.lay2);
                ProgressBar pbar = dialog.findViewById(R.id.pbar);
                EditText sorununcevabi = dialog.findViewById(R.id.editText);
                sorununcevabi.setFilters(new InputFilter[]{
                        new InputFilter() {
                            @Override
                            public CharSequence filter(CharSequence cs, int start,
                                                       int end, Spanned spanned, int dStart, int dEnd) {
                                if (cs.toString().contains(" ")) {
                                    return cs.toString().trim();
                                }
                                if (cs.toString().matches("[a-zA-Z]+")) {
                                    if (cs.toString().length() < 31)
                                        return cs;
                                }
                                if (cs.toString().length() < 31)
                                    return cs;
                                else
                                    return cs.toString().substring(0, 29);
                            }
                        }
                });
                sorununcevabi.setHint("Güvenlik Kelimesi");
                baslik.setText("Güvenlik Sorusu");
                aciklama.setText("Lütfen kendinize unutmayacağınız bir kelime belirleyin. Bu kelimeyi hesabınızla ilgili önemli bir değişiklik yapmak isterseniz siz olduğunuzdan emin olmak amacıyla soracağız. \n(*Bu kelimeyi sonradan değiştiremezsiniz, lütfen unutmayacağınız bir kelime belirleyin.)\n(*Büyük-küçük harflere duyarlı değildir.) \n(*Bu kelime 7-30 arasında bir karakter sayısından oluşmalı.)");
                aciklama.setMovementMethod(new ScrollingMovementMethod());
                buton.setText("TAMAM");
                buton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (sorununcevabi.getText().toString().length() > 6) {
                            lay1.setVisibility(View.GONE);
                            lay2.setVisibility(View.GONE);
                            pbar.setVisibility(View.VISIBLE);
                            //myRef.child(fuser.getUid()).child("usernamef").setValue(username.getText().toString().trim().toLowerCase());
                            String andId = Settings.Secure.getString(getApplicationContext().getContentResolver(), Settings.Secure.ANDROID_ID);
                            myRef.child(fuser.getUid()).child("guvenlik").child("uyari_suresi").getRef().setValue(0);
                            myRef.child(fuser.getUid()).child("guvenlik").child("uyari_zamani").getRef().setValue(0);
                            myRef.child(fuser.getUid()).child("guvenlik").child("uyari").getRef().setValue(0);
                            myRef.child(fuser.getUid()).child("guvenlik").child("sifre_degistimi").getRef().setValue(0);
                            myRef.child(fuser.getUid()).child("guvenlik").child("guvenlik_kelimesi").setValue(sorununcevabi.getText().toString());

                            myRef.child(fuser.getUid()).child("ad").setValue(ad.getText().toString().toLowerCase());
                            myRef.child(fuser.getUid()).child("begeni_bildirim_durumu").setValue("yok");
                            myRef.child(fuser.getUid()).child("begeni_sayisi").setValue("0");
                            myRef.child(fuser.getUid()).child("bildirimler").child("mesaj").setValue("gelsin");
                            myRef.child(fuser.getUid()).child("bildirimler").child("ziyaret").setValue("gelsin");
                            myRef.child(fuser.getUid()).child("bildirimler").child("begeni").setValue("gelsin");
                            myRef.child(fuser.getUid()).child("bt_adi").setValue("");
                            myRef.child(fuser.getUid()).child("bulduklarim_bildirim_durumu").setValue("yok");
                            myRef.child(fuser.getUid()).child("cinsiyet").setValue(cinsiyett.getText().toString());
                            //myRef.child(fuser.getUid()).child("device_token").setValue(DeviceToken);
                            myRef.child(fuser.getUid()).child("dg").setValue(yass.getText().toString());
                            myRef.child(fuser.getUid()).child("dogum_tarihi").setValue(dogumTarihi);
                            myRef.child(fuser.getUid()).child("ev_sistemi").child("sistem").setValue("pasif");
                            myRef.child(fuser.getUid()).child("ev_sistemi").child("evde_mi").setValue("hayir");
                            myRef.child(fuser.getUid()).child("favori_sayim").setValue(3);
                            myRef.child(fuser.getUid()).child("foto_kilitleri").child("2").setValue(0);
                            myRef.child(fuser.getUid()).child("foto_kilitleri").child("3").setValue(0);
                            myRef.child(fuser.getUid()).child("foto_kilitleri").child("4").setValue(0);
                            myRef.child(fuser.getUid()).child("foto_kilitleri").child("5").setValue(0);
                            myRef.child(fuser.getUid()).child("foto_kilitleri").child("6").setValue(0);
                            myRef.child(fuser.getUid()).child("fotograflarim").child("pp").setValue(ppurl);
                            myRef.child(fuser.getUid()).child("gidecegim_yerler").child("asil").child("isim").setValue("");
                            myRef.child(fuser.getUid()).child("gidecegim_yerler").child("1").child("isim").setValue("");
                            myRef.child(fuser.getUid()).child("gidecegim_yerler").child("2").child("isim").setValue("");
                            myRef.child(fuser.getUid()).child("gidecegim_yerler").child("3").child("isim").setValue("");
                            myRef.child(fuser.getUid()).child("gidecegim_yerler").child("asil").child("dbisim").setValue("");
                            myRef.child(fuser.getUid()).child("gidecegim_yerler").child("1").child("dbisim").setValue("");
                            myRef.child(fuser.getUid()).child("gidecegim_yerler").child("2").child("dbisim").setValue("");
                            myRef.child(fuser.getUid()).child("gidecegim_yerler").child("3").child("dbisim").setValue("");
                            myRef.child(fuser.getUid()).child("giris_ilkkez").setValue("evet");
                            myRef.child(fuser.getUid()).child("gizlilik_ad").setValue("0");
                            myRef.child(fuser.getUid()).child("gizlilik_foto").setValue("0");
                            myRef.child(fuser.getUid()).child("gizlilik_mekan").setValue("3");
                            myRef.child(fuser.getUid()).child("gun").setValue(0);
                            myRef.child(fuser.getUid()).child("hesap_durumu").child("durum").setValue(0);
                            myRef.child(fuser.getUid()).child("hesap_durumu").child("zaman").setValue(0);
                            myRef.child(fuser.getUid()).child("idler").child("id-1").setValue(olusanId);
                            myRef.child(fuser.getUid()).child("id").setValue(olusanId);
                            myRef.child(fuser.getUid()).child("kapak").setValue("hayir");
                            myRef.child(fuser.getUid()).child("kayit_tarihi").setValue(ServerValue.TIMESTAMP);
                            myRef.child(fuser.getUid()).child("kime_yaziyor").setValue("");
                            myRef.child(fuser.getUid()).child("mesaj_bildirim_durumu").setValue("yok");
                            myRef.child(fuser.getUid()).child("kp").setValue(1000);
                            myRef.child(fuser.getUid()).child("ozel_kadi").setValue(0);
                            myRef.child(fuser.getUid()).child("premium").setValue("0");
                            myRef.child(fuser.getUid()).child("referansim").setValue("");
                            myRef.child(fuser.getUid()).child("son_id_degisimi").setValue(ServerValue.TIMESTAMP);
                            myRef.child(fuser.getUid()).child("sehir").setValue("" + sehirNo);
                            myRef.child(fuser.getUid()).child("tas_arayuz").setValue("1");
                            myRef.child(fuser.getUid()).child("tas_arayuz_sahibim").setValue(",1");
                            myRef.child(fuser.getUid()).child("tas_mesaj").setValue("1");
                            myRef.child(fuser.getUid()).child("tas_mesaj_sahibim").setValue(",1");
                            myRef.child(fuser.getUid()).child("tas_profil").setValue("1");
                            myRef.child(fuser.getUid()).child("tas_profil_sahibim").setValue(",1");
                            myRef.child(fuser.getUid()).child("uid").setValue(fuser.getUid());

                            DatabaseReference ref = FirebaseDatabase.getInstance().getReference("ppler");
                            ref.child(fuser.getUid()).child("pp_url").setValue(ppurl);

                            //myRef.child(fuser.getUid()).child("bulduklarim").child("zoEbbpAxvwOTKS1tv7iTtsftRUs2").child("kac_kez_gordum").setValue("1");
                            if (!uname) {
                                myRef.child(fuser.getUid()).child("usernamef").setValue(uid).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(@NonNull Void unused) {
                                        dialogAktifMi = false;
                                        Intent git = new Intent(Kayit2Activity.this, MainActivity.class);
                                        startActivity(git);
                                        finish();
                                        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                                        devam = false;
                                        dialog.dismiss();
                                    }
                                });
                                uname = true;
                            }
                        } else {
                            sorununcevabi.setError("Kelime en az 7 karakterden oluşmalı.");
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
            } else {
                Toast.makeText(this, "Fotoğrafınız yok.", Toast.LENGTH_SHORT).show();
                FotoEkle();
            }
        }
    }
    private int getAge(int year, int month, int day){//yaş hesaplama
        Calendar dob = Calendar.getInstance();
        Calendar today = Calendar.getInstance();

        dob.set(year, month, day);

        int yas = today.get(Calendar.YEAR) - dob.get(Calendar.YEAR);
        if (today.get(Calendar.DAY_OF_YEAR)+1 < dob.get(Calendar.DAY_OF_YEAR)){
            yas--;
        }
        dogumTarihi = dob.getTimeInMillis();
        return yas;
    }

    protected void onResume() {
        super.onResume();
        TasarimDegistir("5");

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finishAffinity();
    }

    private void ResmiKirp(Uri resultUri){
        CropImage.activity(resultUri)
                .setAspectRatio(1, 1)
                .setActivityTitle("Resmi Kırp")
                .setCropMenuCropButtonTitle("Kırp")
                .setOutputCompressQuality(70)
                .setBorderLineColor(getResources().getColor(R.color.beyaz))
                .start(this);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                Uri uri = result.getUri();
                //YuklemeIslemi(resultUri,false,"");
                if (uri != null) {
                    resimURL = uri;
                    pp.setImageURI(resimURL);
                    if (ppYukleDialog != null)
                        if (ppYukleDialog.isShowing())
                            ppYukleDialog.dismiss();
                }
                else
                    Toast.makeText(Kayit2Activity.this, "Bir hata oluştu", Toast.LENGTH_SHORT).show();
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
                Toast.makeText(Kayit2Activity.this, ""+error, Toast.LENGTH_SHORT).show();
            }
        }
        //Toast.makeText(ProfilActivity.this, " "+ requestCode + " \n"+ resultCode + " \n"+data.getData(), Toast.LENGTH_LONG).show();
        if(requestCode == 99){
            if(data != null) {
                if (resultCode == RESULT_OK) {
                    Uri resultUri = data.getData();
                    if(resultUri != null)
                        ResmiKirp(resultUri);
                    else
                        Toast.makeText(Kayit2Activity.this, "Bir hata oluştu", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(Kayit2Activity.this, "" + data.getDataString(), Toast.LENGTH_SHORT).show();
                }
            }
        }
    }
    private void ResimYukle(boolean saibe, String yazi){

        Dialog dialog = new Dialog(Kayit2Activity.this);
        dialog.setContentView(R.layout.dialog_dizayn_loading);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setCancelable(false);
        TextView baslik = dialog.findViewById(R.id.baslik);
        TextView aciklama = dialog.findViewById(R.id.aciklama);
        ProgressBar pbar = dialog.findViewById(R.id.pbar);
        baslik.setText("Yükleniyor...");
        aciklama.setText("Fotoğrafınız yükleniyor, birkaç saniye sürebilir");

        /*if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            pbar.setProgressTintList(ColorStateList.valueOf(ortaRenk));
        }*/
        dialog.show();

            UploadTask uploadTask = mStorageRef.child("foto_pp.jpg").putFile(resimURL);
            uploadTask.addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                    StorageReference sref = FirebaseStorage.getInstance().getReference(fuser.getUid());
                    sref.child("foto_pp.jpg").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(final Uri uri) {
                            if(saibe){
                                SaibeEkle(dialog,uri,yazi);
                            }
                            else{
                                ppurl = uri.toString();
                                ppyuklendimi = true;
                                SiradakiActivity();
                            }
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            ppyuklendimi = false;
                        }
                    });

                    dialog.dismiss();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    devamBTN.setText("Devam Et");
                    Toast.makeText(Kayit2Activity.this, "Yükleme Başarısız!", Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                }
            });


    }
    public static String getUID() {
        int DIGITS = 5;
        StringBuilder sb = new StringBuilder(DIGITS);
        for(int i = 0;i < DIGITS;i++) {
            sb.append((char) (Math.random() * 10 + '0'));
        }
        return sb.toString();
    }
}
