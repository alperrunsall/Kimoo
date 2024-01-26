package com.kimoo.android;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.ScrollingMovementMethod;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.kimoo.android.extra.TasarimRenginiGetir;

public class KayitActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private FirebaseUser user;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference myRef;
    private LinearLayout background;
    private EditText sifre,mail;
    private Button kayit,hesabinVarmi;
    private TextView sartlar,sozlesme,sss;
    private String sozlesmeYazisi, sartlarYazisi, sssYazisi;
    private boolean sifredeHataVarMi = true;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kayit);
        StatuBarAyarla();
        firebaseDatabase = FirebaseDatabase.getInstance();
        myRef = firebaseDatabase.getReference();
        mAuth = FirebaseAuth.getInstance();
        sifre = findViewById(R.id.kayitSifre);
        mail = findViewById(R.id.kayitMail);
        sartlar = findViewById(R.id.sartlar);
        sozlesme = findViewById(R.id.sozlesme);
        sss = findViewById(R.id.sss);
        hesabinVarmi = findViewById(R.id.button2);
        kayit = findViewById(R.id.kayitKayitOlBTN);
        background = findViewById(R.id.background);

        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        kayit.setOnTouchListener(new View.OnTouchListener() {
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
        hesabinVarmi.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(final View view, MotionEvent motionEvent) {
                int action = motionEvent.getAction();
                if (action == MotionEvent.ACTION_DOWN) {
                    startScaleAnimation(view);
                    return true;
                } else if (action == MotionEvent.ACTION_UP) {
                    cancelScaleAnimation(view,2);
                    return true;
                }
                return false;
            }
        });
        sifre.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.toString().trim().length() < 8){
                    sifre.setError("Şifreniz en az 8 karakterden oluşmalı");
                    sifredeHataVarMi = true;
                }
                else{
                    int kontrolSayisi = 0;
                    int rakamSayisi = 0;
                    int harfSayisi = 0;
                    int boslukSayisi = 0;
                    for(char c : s.toString().toCharArray()){
                        kontrolSayisi++;
                        if(Character.isDigit(c))
                            rakamSayisi++;
                        else
                            harfSayisi++;

                        if (kontrolSayisi == s.toString().length()){
                            if (rakamSayisi > 0 && harfSayisi > 0) {
                                sifredeHataVarMi = false;

                            }
                            else {
                                sifredeHataVarMi = true;
                                sifre.setError("Şifrenizde en az 1 harf ve 1 rakam bulunmalıdır");
                            }
                        }
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

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
                    Toast.makeText(KayitActivity.this, "Bağlantı Açılıyor...", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                    startActivity(intent);
                }
                else
                    Toast.makeText(KayitActivity.this, "İnternet bağlantınız yavaş lütfen tekrar deneyin.", Toast.LENGTH_SHORT).show();
            }
        });
        sartlar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (sartlarYazisi != null){
                    Uri uri = Uri.parse(sartlarYazisi);
                    Toast.makeText(KayitActivity.this, "Bağlantı Açılıyor...", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                    startActivity(intent);
                }
                else
                    Toast.makeText(KayitActivity.this, "İnternet bağlantınız yavaş lütfen tekrar deneyin.", Toast.LENGTH_SHORT).show();
            }
        });

        sss.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (sssYazisi != null){
                    Uri uri = Uri.parse(sssYazisi);
                    Toast.makeText(KayitActivity.this, "Bağlantı Açılıyor...", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                    startActivity(intent);
                }
                else
                    Toast.makeText(KayitActivity.this, "İnternet bağlantınız yavaş lütfen tekrar deneyin.", Toast.LENGTH_SHORT).show();
            }
        });
        TasarimDegistir("5");
        SSSGoster("Bilgilendirme Metni", getResources().getString(R.string.aydinlatma_metni));
    }
    private void SSSGoster(String baslikYazisi, String icerik){
        Dialog dialog = new Dialog(KayitActivity.this);
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

        TasarimDegistir("5");
    }

    private void DialogOlustur(String baslikYazisi, String icerik){
        Dialog dialog = new Dialog(KayitActivity.this);
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
                                        KayitKayitOl();
                                    else
                                        Toast.makeText(KayitActivity.this, "İnternet bağlantınız yok", Toast.LENGTH_SHORT).show();
                                }
                                else if(i == 2){
                                    KayitHesabinVarMi();
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

        renk1 = TasarimRenginiGetir.RengiGetir(KayitActivity.this,"renk1",tasDegeri);
        renk2 = TasarimRenginiGetir.RengiGetir(KayitActivity.this,"renk2",tasDegeri);
        t1start = TasarimRenginiGetir.RengiGetir(KayitActivity.this,"t1start",tasDegeri);
        t2start = TasarimRenginiGetir.RengiGetir(KayitActivity.this,"t2start",tasDegeri);
        t1end = TasarimRenginiGetir.RengiGetir(KayitActivity.this,"t1end",tasDegeri);
        t2end = TasarimRenginiGetir.RengiGetir(KayitActivity.this,"t2end",tasDegeri);
        orta = TasarimRenginiGetir.RengiGetir(KayitActivity.this,"orta",tasDegeri);

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
        sifre.setBackground(gradientEt1);
        GradientDrawable gradientEt2 = new GradientDrawable();
        gradientEt2.setStroke(3,orta);
        gradientEt2.setOrientation(GradientDrawable.Orientation.LEFT_RIGHT);
        gradientEt2.setCornerRadius(100);
        mail.setBackground(gradientEt1);

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            Drawable kilit_ic = getResources().getDrawable(R.drawable.kilit_ic);
            kilit_ic.setTint(orta);
            kilit_ic.setBounds(0, 0, kilit_ic.getIntrinsicHeight(), kilit_ic.getIntrinsicWidth());
            sifre.setCompoundDrawables(kilit_ic, null, null, null);
            Drawable mail_ic = getResources().getDrawable(R.drawable.ic_mail);
            mail_ic.setTint(orta);
            mail_ic.setBounds(0, 0, mail_ic.getIntrinsicHeight(), mail_ic.getIntrinsicWidth());
            mail.setCompoundDrawables(mail_ic, null, null, null);
        }
        kayit.setBackground(gradientYumusak);
        hesabinVarmi.setBackground(gradientYumusak2);
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
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent i = new Intent(KayitActivity.this, GirisActivity.class);
        startActivity(i);
        finish();
        overridePendingTransition(R.anim.slide_in_left,R.anim.slide_out_right);
    }

    public void KayitKayitOl(){
        if(internetVarmi()) {
            final String android_id = Settings.Secure.getString(getApplicationContext().getContentResolver(),
                    Settings.Secure.ANDROID_ID);
            Dialog dialog = new Dialog(KayitActivity.this);
            dialog.setContentView(R.layout.dialog_dizayn_loading);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialog.setCancelable(false);
            TextView baslik = dialog.findViewById(R.id.baslik);
            TextView aciklama = dialog.findViewById(R.id.aciklama);
            ProgressBar pbar = dialog.findViewById(R.id.pbar);
            baslik.setText("Kayıt Oluyorsunuz");
            aciklama.setText("Mail adresiniz kontrol ediliyor, biraz bekleyin...");

        /*if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            pbar.setProgressTintList(ColorStateList.valueOf(ortaRenk));
        }*/
            dialog.show();

            final DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("usersF").getRef();
            if (!mail.getText().toString().trim().equals("") && !sifre.getText().toString().trim().equals("")) {
                if (!sifredeHataVarMi) {
                    mAuth.createUserWithEmailAndPassword(mail.getText().toString(), sifre.getText().toString())
                            .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                                @Override
                                public void onSuccess(AuthResult authResult) {
                                    user = mAuth.getCurrentUser();

                                    ref.child(user.getUid()).child("ban_durumu").child("durum").setValue("yok");
                                    ref.child(user.getUid()).child("ban_durumu").child("sebep").setValue("");
                                    ref.child(user.getUid()).child("ban_durumu").child("tarih").setValue(0);
                                    ref.child(user.getUid()).child("ban_durumu").child("sure").setValue(0);
                                    ref.child(user.getUid()).child("ban_durumu").child("banlayan").setValue("");
                                    ref.child(user.getUid()).child("ban_durumu").child("aciklama").setValue("");
                                    ref.child(user.getUid()).child("ban_durumu").child("kac_kez").setValue(0);
                                    ref.child(user.getUid()).child("ban_durumu").child("uyari_sayisi").setValue(0);
                                    ref.child(user.getUid()).child("email").setValue(user.getEmail());
                                    ref.child(user.getUid()).child("kisitli_erisim_engeli").child("zaman").setValue(0);
                                    ref.child(user.getUid()).child("kisitli_erisim_engeli").child("sure").setValue(0);
                                    ref.child(user.getUid()).child("kisitli_erisim_engeli").child("durum").setValue("yok");
                                    ref.child(user.getUid()).child("and_id").setValue(android_id).addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            Intent i = new Intent(KayitActivity.this, Kayit2Activity.class);
                                            dialog.dismiss();
                                            startActivity(i);
                                            finish();
                                            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                                        }
                                    });
                                }
                            }).addOnFailureListener(KayitActivity.this, new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            //Toast.makeText(KayitActivity.this, e.toString(), Toast.LENGTH_LONG).show();
                            switch (e.getMessage()){
                                case "The email address is badly formatted.":
                                    Toast.makeText(KayitActivity.this, "Lütfen gerçek bir e-mail yazınız.", Toast.LENGTH_SHORT).show();
                                    break;
                                case "The email address is already in use by another account.":
                                    Toast.makeText(KayitActivity.this, "Bu mail adresi zaten kullanılıyor.", Toast.LENGTH_SHORT).show();
                                    break;
                                default:
                                    Toast.makeText(KayitActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                    break;
                            }

                            dialog.dismiss();
                        }
                    });
                }
                else {
                    dialog.dismiss();
                    Toast.makeText(this, "Şifrenizde hata var", Toast.LENGTH_SHORT).show();
                }
            }
            else {
                dialog.dismiss();
                Toast.makeText(this, "Lütfen boş alanları doldurunuz", Toast.LENGTH_SHORT).show();
            }
        }
    }
    public void KayitHesabinVarMi() {
        Intent i = new Intent(KayitActivity.this, GirisActivity.class);
        startActivity(i);
        finish();
        overridePendingTransition(R.anim.slide_in_left,R.anim.slide_out_right);
    }
    private boolean internetVarmi(){
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED)
            return true;
        else {
            Toast.makeText(KayitActivity.this, "İnternet bağlantınız yok.", Toast.LENGTH_SHORT).show();
            return false;
        }
    }

}
