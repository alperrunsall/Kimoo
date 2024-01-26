package com.kimoo.android;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.os.Bundle;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;

import android.provider.Settings;
import android.text.InputFilter;
import android.text.Spanned;
import android.text.method.ScrollingMovementMethod;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.kimoo.android.Model.User;
import com.kimoo.android.extra.EngellenenlerAdapter;
import com.kimoo.android.extra.TasarimRenginiGetir;

import java.util.ArrayList;
import java.util.List;

public class AyarDegistirActivity extends AppCompatActivity {
    private Toolbar mToolbar;
    String gelenDeg;
    RecyclerView recyclerView;
    RelativeLayout rel1,rel2;
    EditText suan,yeni;
    FirebaseAuth mAuth;
    List<User> mUser = new ArrayList<>();
    Button degisBTN;
    FirebaseUser fuser;
    TextView baslik;
    DataSnapshot AsilDataSnapshot;
    private LinearLayout background;
    public static GradientDrawable gradientDrawable;
    User user;
    int uyari_degeri;
    EngellenenlerAdapter engellenenlerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ayar_degistir);
        StatuBarAyarla();
        fuser = FirebaseAuth.getInstance().getCurrentUser();
        gelenDeg = getIntent().getStringExtra("islem");
        mToolbar = findViewById(R.id.toolbar);
        rel1 = findViewById(R.id.rel1);
        rel2 = findViewById(R.id.rel2);
        suan = findViewById(R.id.eski_mail);
        yeni = findViewById(R.id.yeni_mail);
        baslik = findViewById(R.id.baslik);
        degisBTN = findViewById(R.id.degistir_btn);
        background = findViewById(R.id.background);
        recyclerView = findViewById(R.id.recylerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        SharedPreferences tas_shared = getSharedPreferences("TasarimBilgileri",MODE_PRIVATE);
        TasarimDegistir(tas_shared.getString("tasarim_arayuz","1"));

        mAuth = FirebaseAuth.getInstance();
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(getResources().getDrawable(R.drawable.ic_mesaj_geri2));
        if(gelenDeg.equals("0")) {
            rel1.setVisibility(View.VISIBLE);
            rel2.setVisibility(View.GONE);
            baslik.setText("Email Değiştir");
        }
        else {
            rel1.setVisibility(View.GONE);
            rel2.setVisibility(View.VISIBLE);
            baslik.setText("Engellediklerim");
        }


        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        if(gelenDeg.equals("1")) {
            DatabaseReference ref = FirebaseDatabase.getInstance().getReference("usersF").child(fuser.getUid());
            ref.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    AsilDataSnapshot = dataSnapshot;
                    if (dataSnapshot.hasChild("engellediklerim")) {
                        for (final DataSnapshot ds : dataSnapshot.child("engellediklerim").getChildren()) {
                            DatabaseReference reff = FirebaseDatabase.getInstance().getReference("usersF");
                            reff.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    for (DataSnapshot dss : dataSnapshot.getChildren()) {
                                        if (dss.getKey().equals(ds.getKey())) {
                                            User user = dss.getValue(User.class);
                                            if (!mUser.contains(user)) {
                                                mUser.add(user);
                                            }
                                            engellenenlerAdapter = new EngellenenlerAdapter(AyarDegistirActivity.this, mUser);
                                            recyclerView.setAdapter(engellenenlerAdapter);
                                        }
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });
                        }
                    } else {
                        Toast.makeText(AyarDegistirActivity.this, "Kimseyi engellememişsiniz.", Toast.LENGTH_SHORT).show();
                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }else{
            DatabaseReference ref = FirebaseDatabase.getInstance().getReference("usersF").child(fuser.getUid());
            ref.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    AsilDataSnapshot = dataSnapshot;
                    user = dataSnapshot.getValue(User.class);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

            degisBTN.setOnTouchListener(new View.OnTouchListener() {
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

    @Override
    protected void onResume() {
        super.onResume();

        SharedPreferences tas_shared = getSharedPreferences("TasarimBilgileri",MODE_PRIVATE);
        TasarimDegistir(tas_shared.getString("tasarim_arayuz","1"));
    }


    public void TasarimDegistir(String tasDegeri) {
        GradientDrawable gradient = (GradientDrawable) getResources().getDrawable(R.drawable.gradient);
        gradient.setOrientation(GradientDrawable.Orientation.LEFT_RIGHT);

        GradientDrawable gradientYumusak = new GradientDrawable();
        gradientYumusak.setOrientation(GradientDrawable.Orientation.LEFT_RIGHT);
        gradientYumusak.setCornerRadius(50);

        GradientDrawable gradientStroke = new GradientDrawable();
        gradientStroke.setOrientation(GradientDrawable.Orientation.LEFT_RIGHT);
        gradientStroke.setCornerRadius(50);

        int renk1 = 0,renk2 = 0,t1start = 0,t2start = 0,t1end = 0,t2end = 0,orta = 0;

        renk1 = TasarimRenginiGetir.RengiGetir(AyarDegistirActivity.this,"renk1",tasDegeri);
        renk2 = TasarimRenginiGetir.RengiGetir(AyarDegistirActivity.this,"renk2",tasDegeri);
        t1start = TasarimRenginiGetir.RengiGetir(AyarDegistirActivity.this,"t1start",tasDegeri);
        t2start = TasarimRenginiGetir.RengiGetir(AyarDegistirActivity.this,"t2start",tasDegeri);
        t1end = TasarimRenginiGetir.RengiGetir(AyarDegistirActivity.this,"t1end",tasDegeri);
        t2end = TasarimRenginiGetir.RengiGetir(AyarDegistirActivity.this,"t2end",tasDegeri);
        orta = TasarimRenginiGetir.RengiGetir(AyarDegistirActivity.this,"orta",tasDegeri);



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
        gradientDrawable = gradientYumusak;

        gradientStroke.setStroke(2,renk2);

        //yeni.setBackground(gradientStroke);
        //suan.setBackground(gradientStroke);

        degisBTN.setBackground(gradientYumusak);
        background.setBackground(gradient);
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
                                    if(yeni.getText().toString().toUpperCase().trim().length() > 5 && !suan.getText().toString().toUpperCase().trim().equals("")) {
                                        String andId = Settings.Secure.getString(getApplicationContext().getContentResolver(), Settings.Secure.ANDROID_ID);
                                        if(AsilDataSnapshot.child("guvenlik").child("uyari").getValue(Integer.class) < 3) {
                                            GuvenlikSorusunuSor();
                                        }else{
                                            Toast.makeText(AyarDegistirActivity.this, "Güvenlik ihlali yaptınız, bu ayarı bir süre değiştiremezsiniz.", Toast.LENGTH_SHORT).show();
                                        }
                                        /**/
                                    }else{
                                        Toast.makeText(AyarDegistirActivity.this, "Lütfen boşlukları düzgün doldurunuz.", Toast.LENGTH_SHORT).show();
                                    }
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
    private void GuvenlikSorusunuSor() {
        Dialog dialog = new Dialog(AyarDegistirActivity.this);
        dialog.setContentView(R.layout.dialog_dizayn3);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        TextView baslik = dialog.findViewById(R.id.baslik);
        TextView aciklama = dialog.findViewById(R.id.aciklama);
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
                String andId = Settings.Secure.getString(getApplicationContext().getContentResolver(), Settings.Secure.ANDROID_ID);
                DatabaseReference ref = FirebaseDatabase.getInstance().getReference("usersF").child(fuser.getUid());
                ref.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        AsilDataSnapshot = dataSnapshot;
                        if(sorununcevabi.getText().toString().trim().length() > 6){
                            if(AsilDataSnapshot.child("guvenlik").child("uyari").getValue(Integer.class) < 3) {
                                if(AsilDataSnapshot.child("guvenlik").child("guvenlik_kelimesi").getValue(String.class).equals(sorununcevabi.getText().toString().toLowerCase().trim())){
                                    AuthCredential credential = EmailAuthProvider.getCredential(fuser.getEmail(), yeni.getText().toString().toLowerCase().trim());
                                    fuser.reauthenticate(credential)
                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if (task.isSuccessful()) {
                                                        fuser.updateEmail(suan.getText().toString().toLowerCase().trim())
                                                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                    @Override
                                                                    public void onComplete(@NonNull Task<Void> task) {
                                                                        if (task.isSuccessful()) {
                                                                            AsilDataSnapshot.child("guvenlik").child("uyari_suresi").getRef().setValue(0);
                                                                            AsilDataSnapshot.child("guvenlik").child("uyari_zamani").getRef().setValue(0);
                                                                            AsilDataSnapshot.child("guvenlik").child("uyari").getRef().setValue(0).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                                @Override
                                                                                public void onSuccess(@NonNull Void unused) {
                                                                                    dialog.dismiss();
                                                                                    startActivity(new Intent(AyarDegistirActivity.this,AyarlarActivity.class));
                                                                                    Toast.makeText(AyarDegistirActivity.this, "Mail adresiniz değiştirildi.", Toast.LENGTH_SHORT).show();
                                                                                }
                                                                            });
                                                                        } else {
                                                                            Toast.makeText(AyarDegistirActivity.this, "Bir hata oluştu.", Toast.LENGTH_SHORT).show();
                                                                        }
                                                                    }
                                                                });
                                                    } else {
                                                        Toast.makeText(AyarDegistirActivity.this, "Şifren yanlış.", Toast.LENGTH_SHORT).show();
                                                    }
                                                }
                                            });
                                }
                                else{
                                    AsilDataSnapshot.child("guvenlik").child("uyari_zamani").getRef().setValue(ServerValue.TIMESTAMP);
                                    AsilDataSnapshot.child("guvenlik").child("uyari_suresi").getRef().setValue(86400000 * 1);
                                    AsilDataSnapshot.child("guvenlik").child("uyari").getRef().setValue(AsilDataSnapshot.child("guvenlik").child("uyari").getValue(Integer.class) + 1).addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(@NonNull Void unused) {
                                            sorununcevabi.setError("Yanlış!");
                                        }
                                    });
                                }
                            }else{
                                AsilDataSnapshot.child("guvenlik").child("uyari_zamani").getRef().setValue(ServerValue.TIMESTAMP);
                                AsilDataSnapshot.child("guvenlik").child("uyari_suresi").getRef().setValue(86400000 * 3); // 3 Gün 86400000
                                mAuth.sendPasswordResetEmail(mAuth.getCurrentUser().getEmail()).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        Toast.makeText(AyarDegistirActivity.this, "Güvenlik ihlali yaptınız, bu ayarı bir süre değiştiremezsiniz.", Toast.LENGTH_SHORT).show();
                                        dialog.dismiss();
                                        startActivity(new Intent(AyarDegistirActivity.this,AyarlarActivity.class));
                                    }
                                });
                            }
                        }else{
                            sorununcevabi.setError("Bu kelime en az 7 karakterden oluşmalı.");
                        }
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
