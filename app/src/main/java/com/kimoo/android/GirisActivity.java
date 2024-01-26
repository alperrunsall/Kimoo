package com.kimoo.android;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Paint;
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

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.kimoo.android.extra.TasarimRenginiGetir;

public class GirisActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;

    private EditText sifre,mail;
    private Button giris,hesabinYokmu;
    private TextView sifrenimiunuttun, sartlar,sozlesme,sss;
    private String teldekiMail = "";
    private LinearLayout background;
    private String sozlesmeYazisi, sartlarYazisi, sssYazisi, uaLink;
    DataSnapshot datas;
    int deger = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_giris);
        StatuBarAyarla();
        mAuth = FirebaseAuth.getInstance();
        sifre = findViewById(R.id.girisSifre);
        mail = findViewById(R.id.girisMail);
        giris = findViewById(R.id.girisGirisYapBTN);
        background = findViewById(R.id.background);
        hesabinYokmu = findViewById(R.id.button2);
        sartlar = findViewById(R.id.sartlar);
        sozlesme = findViewById(R.id.sozlesme);
        sss = findViewById(R.id.sss);
        sifrenimiunuttun = findViewById(R.id.sifreniMiUnuttun);

        sifrenimiunuttun.setPaintFlags(sifrenimiunuttun.getPaintFlags() |   Paint.UNDERLINE_TEXT_FLAG);
        final String android_id = Settings.Secure.getString(getApplicationContext().getContentResolver(),
                Settings.Secure.ANDROID_ID);


        sifrenimiunuttun.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences sharedPreferences = getSharedPreferences("guvenlik", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();

                final int[] gonderimSayisi = {sharedPreferences.getInt("mail_gonderimi", 0)};
                long sonGonderimZamani = sharedPreferences.getLong("son_gonderim",0);
                if(mail.getText().toString().trim().equals("")) {
                    Toast.makeText(GirisActivity.this, "Mail adresinizi yazmalısınız.", Toast.LENGTH_SHORT).show();
                }
                else{
                    Dialog dialog2 = new Dialog(GirisActivity.this);
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
                            dialog2.dismiss();
                        }
                    });
                    buton2.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            lay1.setVisibility(View.GONE);
                            pbar.setVisibility(View.VISIBLE);
                            mAuth.sendPasswordResetEmail(mail.getText().toString()).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Toast.makeText(GirisActivity.this, "Şifre sıfırlama için e-mail gönderildi. Biraz gecikme yaşanabilir.", Toast.LENGTH_LONG).show();
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
                                    Toast.makeText(GirisActivity.this, "Bir sorun oluştu, mail adresi doğru olmayabilir.", Toast.LENGTH_SHORT).show();
                                    dialog2.dismiss();
                                }
                            });
                        }
                    });
                    if (gonderimSayisi[0] == 2){
                        if (System.currentTimeMillis() > sonGonderimZamani + 86400000){
                            dialog2.show();
                        }
                        else {
                            editor.putLong("son_gonderim",System.currentTimeMillis()-86400000);
                            editor.commit();
                            Toast.makeText(GirisActivity.this, "Bugün yeterince mail gönderilmiş lütfen yarın tekrar deneyin.", Toast.LENGTH_LONG).show();
                            dialog2.dismiss();
                        }
                    }
                    else{
                        dialog2.show();
                    }
                }
            }
        });

        /*DatabaseReference sistemRef = FirebaseDatabase.getInstance().getReference("Sistem");
        sistemRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.child("sistem_durumu").child("durum").getValue(Integer.class) == 0){

                }
                else if (dataSnapshot.child("sistem_durumu").child("durum").getValue(Integer.class) == 1)
                    Toast.makeText(GirisActivity.this, ""+dataSnapshot.child("sistem_durumu").child("durum_aciklamasi").getValue(String.class), Toast.LENGTH_LONG).show();

                sozlesmeYazisi = dataSnapshot.child("yazilar").child("gizlilik_sozlesmesi").getValue(String.class);
                sartlarYazisi = dataSnapshot.child("yazilar").child("kullanim_sartlari").getValue(String.class);
                sssYazisi = dataSnapshot.child("yazilar").child("sss").getValue(String.class);
                uaLink = dataSnapshot.child("yazilar").child("ua_link").getValue(String.class);


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });*/
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
                    Toast.makeText(GirisActivity.this, "Bağlantı Açılıyor...", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                    startActivity(intent);
                }
                else
                    Toast.makeText(GirisActivity.this, "İnternet bağlantınız yavaş lütfen tekrar deneyin.", Toast.LENGTH_SHORT).show();
            }
        });
        sartlar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (sartlarYazisi != null){
                    Uri uri = Uri.parse(sartlarYazisi);
                    Toast.makeText(GirisActivity.this, "Bağlantı Açılıyor...", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                    startActivity(intent);
                }
                else
                    Toast.makeText(GirisActivity.this, "İnternet bağlantınız yavaş lütfen tekrar deneyin.", Toast.LENGTH_SHORT).show();
            }
        });

        sss.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (sssYazisi != null){
                    Uri uri = Uri.parse(sssYazisi);
                    Toast.makeText(GirisActivity.this, "Bağlantı Açılıyor...", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                    startActivity(intent);
                }
                else
                    Toast.makeText(GirisActivity.this, "İnternet bağlantınız yavaş lütfen tekrar deneyin.", Toast.LENGTH_SHORT).show();
            }
        });

        giris.setOnTouchListener(new View.OnTouchListener() {
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
        hesabinYokmu.setOnTouchListener(new View.OnTouchListener() {
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

        deger = 2; // Eğer mail kontrol edilecekse bu silinmeli
        // BURAYA BAK TEKRAR
        /*Query usernameQuery = FirebaseDatabase.getInstance().getReference("usersF").orderByChild("and_id").equalTo(android_id);
        usernameQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.getChildrenCount() > 0){
                    deger = 1;
                        for (DataSnapshot ds : dataSnapshot.getChildren()) {
                            datas = ds;
                            int nerede = ds.child("email").getValue().toString().indexOf("@");
                            teldekiMail = ds.child("email").getValue().toString();
                            String mailKisim1 = ds.child("email").getValue().toString().substring(0, nerede);
                            String mailKisim2 = ds.child("email").getValue().toString().substring(nerede);
                            if(mailKisim1.length() > 8) {
                                String mailKesilmis1 = mailKisim1.substring(2, mailKisim1.length() - 2);
                                StringBuilder yeniKisim = new StringBuilder(mailKesilmis1);
                                for (int i = 0; i < mailKesilmis1.length(); i++) {
                                    yeniKisim.setCharAt(i, '*');
                                }
                                String mailAsilKisim1 = mailKisim1.replace(mailKesilmis1, yeniKisim);
                                StringBuilder sonHal = new StringBuilder(mailAsilKisim1 + mailKisim2);
                                mail.setHint(sonHal.toString());
                            }else{
                                String mailKesilmis1 = mailKisim1.substring(1, mailKisim1.length() - 1);
                                StringBuilder yeniKisim = new StringBuilder(mailKesilmis1);
                                for (int i = 0; i < mailKesilmis1.length(); i++) {
                                    yeniKisim.setCharAt(i, '*');
                                }
                                String mailAsilKisim1 = mailKisim1.replace(mailKesilmis1, yeniKisim);
                                StringBuilder sonHal = new StringBuilder(mailAsilKisim1 + mailKisim2);
                                mail.setHint(sonHal.toString());
                            }
                        }

                }else{
                    deger = 2;
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                deger = 0;
            }
        });*/
        TasarimDegistir("5");
    }

    @Override
    protected void onResume() {
        super.onResume();
        TasarimDegistir("5");
    }

    private void DialogOlustur(String baslikYazisi, String icerik){
        Dialog dialog = new Dialog(GirisActivity.this);
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
                                        GirisGirisYap(mail,sifre,"");
                                    else
                                        Toast.makeText(GirisActivity.this, "İnternet bağlantınız yok", Toast.LENGTH_SHORT).show();
                                }
                                else if(i == 2){
                                    GirisHesabinYokMu();
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

    private boolean internetVarmi(){
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED)
            return true;
        else {
            return false;
        }
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

        GradientDrawable gradientYumusak = new GradientDrawable();
        gradientYumusak.setOrientation(GradientDrawable.Orientation.LEFT_RIGHT);
        gradientYumusak.setCornerRadius(70);

        GradientDrawable gradientYumusak2 = new GradientDrawable();
        gradientYumusak2.setOrientation(GradientDrawable.Orientation.LEFT_RIGHT);
        gradientYumusak2.setCornerRadius(60);

        int renk1 = 0,renk2 = 0,t1start = 0,t2start = 0,t1end = 0,t2end = 0,orta = 0;

        renk1 = TasarimRenginiGetir.RengiGetir(GirisActivity.this,"renk1",tasDegeri);
        renk2 = TasarimRenginiGetir.RengiGetir(GirisActivity.this,"renk2",tasDegeri);
        t1start = TasarimRenginiGetir.RengiGetir(GirisActivity.this,"t1start",tasDegeri);
        t2start = TasarimRenginiGetir.RengiGetir(GirisActivity.this,"t2start",tasDegeri);
        t1end = TasarimRenginiGetir.RengiGetir(GirisActivity.this,"t1end",tasDegeri);
        t2end = TasarimRenginiGetir.RengiGetir(GirisActivity.this,"t2end",tasDegeri);
        orta = TasarimRenginiGetir.RengiGetir(GirisActivity.this,"orta",tasDegeri);

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
        giris.setBackground(gradientYumusak);
        hesabinYokmu.setBackground(gradientYumusak2);
        background.setBackground(gradient);
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        System.exit(0);
    }

    public void GirisGirisYap(EditText mailTV, EditText sifreTV, String mesaj){

        Dialog dialog = new Dialog(GirisActivity.this);
        dialog.setContentView(R.layout.dialog_dizayn_loading);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setCancelable(false);
        TextView baslik = dialog.findViewById(R.id.baslik);
        TextView aciklama = dialog.findViewById(R.id.aciklama);
        ProgressBar pbar = dialog.findViewById(R.id.pbar);
        baslik.setText("Giriş Yapılıyor");
        aciklama.setText("Lütfen bekleyin...");

        dialog.show();
        if(deger != 0) {
            if (!mailTV.getText().toString().trim().equals("") && !sifreTV.getText().toString().trim().equals("")) {
                mAuth.signInWithEmailAndPassword(mailTV.getText().toString(), sifreTV.getText().toString())
                        .addOnCompleteListener(GirisActivity.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {

                                    if(mailTV.getText().toString().equals(teldekiMail)){

                                    }
                                    else{
                                        String android_id = Settings.Secure.getString(getApplicationContext().getContentResolver(),
                                                Settings.Secure.ANDROID_ID);
                                        FirebaseUser fuser = FirebaseAuth.getInstance().getCurrentUser();
                                        if (fuser != null){
                                            FirebaseDatabase.getInstance().getReference("usersF").child(fuser.getUid()).child("and_id").getRef().setValue(android_id).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(@NonNull Void unused) {
                                                    SharedPreferences sharedPreferences = getSharedPreferences("guvenlik", Context.MODE_PRIVATE);
                                                    SharedPreferences.Editor editor = sharedPreferences.edit();
                                                    editor.putInt("giris_denemesi", 0);
                                                    editor.commit();

                                                    Intent i = new Intent(GirisActivity.this, MainActivity.class);
                                                    startActivity(i);
                                                    finish();
                                                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                                                    dialog.dismiss();
                                                }
                                            });
                                        }
                                        else {
                                            Query usernameQuery = FirebaseDatabase.getInstance().getReference("usersF").orderByChild("email").equalTo(mailTV.getText().toString());
                                            usernameQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                    for (DataSnapshot ds : dataSnapshot.getChildren()) {
                                                        ds.child("and_id").getRef().setValue(android_id).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                            @Override
                                                            public void onSuccess(@NonNull Void unused) {
                                                                SharedPreferences sharedPreferences = getSharedPreferences("guvenlik", Context.MODE_PRIVATE);
                                                                SharedPreferences.Editor editor = sharedPreferences.edit();
                                                                editor.putInt("giris_denemesi", 0);
                                                                editor.commit();

                                                                Intent i = new Intent(GirisActivity.this, MainActivity.class);
                                                                startActivity(i);
                                                                finish();
                                                                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                                                                dialog.dismiss();
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
                                    if (!mesaj.equals(""))
                                        Toast.makeText(GirisActivity.this, ""+mesaj, Toast.LENGTH_LONG).show();

                                }
                            }
                        }).addOnFailureListener(GirisActivity.this, new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        dialog.dismiss();

                        switch (e.getMessage()){
                            case "The password is invalid or the user does not have a password.":
                                SharedPreferences sharedPreferences = getSharedPreferences("guvenlik", Context.MODE_PRIVATE);
                                SharedPreferences.Editor editor = sharedPreferences.edit();
                                editor.putInt("giris_denemesi",sharedPreferences.getInt("giris_denemesi",0) + 1);
                                editor.commit();
                                if(sharedPreferences.getInt("giris_denemesi",0) > 2)
                                    mAuth.sendPasswordResetEmail(mailTV.getText().toString().trim()).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                        }
                                    });
                                Toast.makeText(GirisActivity.this, "Mail adresinizi ve şifrenizi kontrol ediniz.", Toast.LENGTH_SHORT).show();
                                break;
                            case "There is no user record corresponding to this identifier. The user may have been deleted.":
                                Toast.makeText(GirisActivity.this, "Böyle bir kullanıcı bulunamadı.", Toast.LENGTH_SHORT).show();
                                break;
                            default:
                                Toast.makeText(GirisActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                break;
                        }
                    }
                });
            } else {
                dialog.dismiss();
                Toast.makeText(this, "Lütfen boş alanları doldurunuz.", Toast.LENGTH_SHORT).show();
            }
        }else{
            dialog.dismiss();
            Toast.makeText(this, "Lütfen bir süre bekleyin. İnternet bağlantınız olmayabilir.", Toast.LENGTH_SHORT).show();
        }
    }

    public void GirisHesabinYokMu(){
        if(deger == 0){
            //Buraya sonra bak
            Intent i = new Intent(GirisActivity.this, KayitActivity.class);
            startActivity(i);
            finish();
            overridePendingTransition(R.anim.slide_in_right,R.anim.slide_out_left);
            Toast.makeText(GirisActivity.this, "Lütfen bir süre bekleyin. İnternet bağlantınız olmayabilir.", Toast.LENGTH_SHORT).show();
        }
        else if(deger == 1){
            Toast.makeText(GirisActivity.this, "Zaten bu telefonda kullandığınız bir hesap var. Bu telefonda başka bir hesaba giriş yapabilirsiniz, ancak yeni bir hesap oluşturamazsınız.", Toast.LENGTH_LONG).show();
        }
        else if(deger == 2){
            Intent i = new Intent(GirisActivity.this, KayitActivity.class);
            startActivity(i);
            finish();
            overridePendingTransition(R.anim.slide_in_right,R.anim.slide_out_left);
        }


    }
}
