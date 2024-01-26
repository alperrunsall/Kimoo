package com.kimoo.android;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.kimoo.android.Model.Hazirlayan;
import com.kimoo.android.extra.FavoriAdapter;
import com.kimoo.android.extra.HazirlayanlarAdapter;
import com.kimoo.android.extra.TasarimRenginiGetir;
import com.kimoo.android.fragments.TumGorduklerim;

import java.util.ArrayList;
import java.util.List;

public class HazirlayanlarActivity extends AppCompatActivity {

    private Toolbar mToolbar;
    private FirebaseUser fuser;
    private LinearLayout background;
    private int ortaRenk;
    private ImageView simge;
    private Button ekle;
    private TextView kutuphaneler;
    private List<Hazirlayan> mHazirlayan = new ArrayList<>();
    private List<String> mList = new ArrayList<>();
    private RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hazirlayanlar);
        StatuBarAyarla();

        simge = findViewById(R.id.simge);
        recyclerView = findViewById(R.id.recyclerView);
        mToolbar = findViewById(R.id.toolbar);
        kutuphaneler = findViewById(R.id.kutuphaneler);
        ekle = findViewById(R.id.ekle);
        fuser = FirebaseAuth.getInstance().getCurrentUser();

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(HazirlayanlarActivity.this));

        background = findViewById(R.id.background);

        setSupportActionBar(mToolbar);
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

        kutuphaneler.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(HazirlayanlarActivity.this, "ArthurHub/Android-Image-Cropper\nhdodenhof/CircleImageView\nbumptech/glide\nBaseflow/PhotoView\nsquare/retrofit", Toast.LENGTH_LONG).show();
            }
        });
        SharedPreferences tas_shared = getSharedPreferences("TasarimBilgileri",MODE_PRIVATE);
        TasarimDegistir(tas_shared.getString("tasarim_arayuz","1"));

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Hazirlayanlar");
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    final int[] deger = {0};
                    for(DataSnapshot ds : dataSnapshot.getChildren()){
                        if (ds.getValue(String.class).substring(0,1).equals("1")){
                            if(!mList.contains(ds.getKey())){
                                mList.add(ds.getKey());
                                FirebaseDatabase.getInstance().getReference("usersF").child(ds.getKey()).addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot onunSnap) {
                                        deger[0]++;
                                        Hazirlayan hazirlayan = new Hazirlayan(ds.getValue(String.class).substring(1,ds.getValue(String.class).length()-1),onunSnap.child("usernamef").getValue(String.class),ds.getKey(),Integer.parseInt(ds.getValue(String.class).substring(ds.getValue(String.class).length()-1)));
                                        mHazirlayan.add(hazirlayan);

                                        if (deger[0] == dataSnapshot.getChildrenCount()) {
                                            // Toast.makeText(HazirlayanlarActivity.this, ""+mHazirlayan.size(), Toast.LENGTH_SHORT).show();
                                            HazirlayanlarAdapter hazirlayanlarAdapter = new HazirlayanlarAdapter(HazirlayanlarActivity.this,mHazirlayan);
                                            recyclerView.setAdapter(hazirlayanlarAdapter);
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                    }
                                });
                            }
                        }
                        else {
                            deger[0]++;

                            if (deger[0] == dataSnapshot.getChildrenCount()) {
                                // Toast.makeText(HazirlayanlarActivity.this, ""+mHazirlayan.size(), Toast.LENGTH_SHORT).show();
                                HazirlayanlarAdapter hazirlayanlarAdapter = new HazirlayanlarAdapter(HazirlayanlarActivity.this,mHazirlayan);
                                recyclerView.setAdapter(hazirlayanlarAdapter);
                            }
                        }
                    }
                    if (!dataSnapshot.hasChild(fuser.getUid()))
                        FirebaseDatabase.getInstance().getReference("usersF").child(fuser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                if (dataSnapshot.hasChild("hazirlayanlar_onayi"))
                                    if (dataSnapshot.child("hazirlayanlar_onayi").getValue(String.class).equals("evet")){
                                        Dialog dialog = new Dialog(HazirlayanlarActivity.this);
                                        dialog.setContentView(R.layout.dialog_dizayn3);
                                        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

                                        LinearLayout lay1 = dialog.findViewById(R.id.lay1);
                                        LinearLayout lay2 = dialog.findViewById(R.id.lay2);
                                        TextView baslik = dialog.findViewById(R.id.baslik);
                                        TextView aciklama = dialog.findViewById(R.id.aciklama);
                                        aciklama.setMovementMethod(new ScrollingMovementMethod());
                                        Button buton = dialog.findViewById(R.id.buton);
                                        EditText adSoyad = dialog.findViewById(R.id.editText);
                                        adSoyad.setHint("Ad - Soyad");
                                        baslik.setText("İSMİNİZİ YAZINIZ");
                                        aciklama.setText("Adınızın listede görünmesi için lütfen adınızı yazınız. Sonraki adımda isminize tıklandığında profil sayfanıza yönlendirilmesini ayarlayabilirsiniz. Değişiklilerin uygulanması 1 gün sürebilir.");
                                        buton.setText("TAMAM");
                                        buton.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                lay1.setVisibility(View.GONE);
                                                lay2.setVisibility(View.GONE);
                                                if(!adSoyad.getText().toString().trim().equals("")){
                                                    dialog.dismiss();
                                                    Dialog dialog2 = new Dialog(HazirlayanlarActivity.this);
                                                    dialog2.setContentView(R.layout.dialog_dizayn5);
                                                    dialog2.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

                                                    LinearLayout lay1 = dialog2.findViewById(R.id.lay1);
                                                    LinearLayout lay2 = dialog2.findViewById(R.id.lay2);
                                                    TextView baslik = dialog2.findViewById(R.id.baslik);
                                                    TextView aciklama = dialog2.findViewById(R.id.aciklama);
                                                    aciklama.setMovementMethod(new ScrollingMovementMethod());
                                                    Button buton = dialog2.findViewById(R.id.buton);
                                                    Button buton2 = dialog2.findViewById(R.id.buton2);
                                                    baslik.setText("TEŞEKKÜR");
                                                    aciklama.setText("Teşekkürler kısmındaki isminize tıklandığında Kimoo profil sayfanıza yönlendirilmesini istiyor musunuz?");
                                                    buton.setText("HAYIR");
                                                    buton2.setText("EVET");
                                                    buton.setOnClickListener(new View.OnClickListener() {
                                                        @Override
                                                        public void onClick(View v) {
                                                            FirebaseDatabase.getInstance().getReference("Hazirlayanlar").child(fuser.getUid()).setValue("0"+adSoyad.getText().toString().replaceAll(" ",".B.")+"1").addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                @Override
                                                                public void onSuccess(@NonNull Void unused) {
                                                                    dataSnapshot.child("hazirlayanlar_onayi").getRef().setValue("hayir").addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                        @Override
                                                                        public void onSuccess(@NonNull Void unused) {
                                                                            dialog2.dismiss();
                                                                        }
                                                                    });
                                                                }
                                                            });
                                                        }
                                                    });
                                                    buton2.setOnClickListener(new View.OnClickListener() {
                                                        @Override
                                                        public void onClick(View v) {
                                                            FirebaseDatabase.getInstance().getReference("Hazirlayanlar").child(fuser.getUid()).setValue("0"+adSoyad.getText().toString().replaceAll(" ",".B.")+"0").addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                @Override
                                                                public void onSuccess(@NonNull Void unused) {
                                                                    dataSnapshot.child("hazirlayanlar_onayi").getRef().setValue("hayir").addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                        @Override
                                                                        public void onSuccess(@NonNull Void unused) {
                                                                            dialog2.dismiss();
                                                                        }
                                                                    });
                                                                }
                                                            });
                                                        }
                                                    });
                                                    dialog2.show();
                                                }
                                                else{
                                                    lay1.setVisibility(View.VISIBLE);
                                                    lay2.setVisibility(View.VISIBLE);
                                                    Toast.makeText(HazirlayanlarActivity.this, "Hiçbir şey yazmadınız.", Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                        });
                                        dialog.show();
                                    }
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

        renk1 = TasarimRenginiGetir.RengiGetir(HazirlayanlarActivity.this,"renk1",tasDegeri);
        renk2 = TasarimRenginiGetir.RengiGetir(HazirlayanlarActivity.this,"renk2",tasDegeri);
        t1start = TasarimRenginiGetir.RengiGetir(HazirlayanlarActivity.this,"t1start",tasDegeri);
        t2start = TasarimRenginiGetir.RengiGetir(HazirlayanlarActivity.this,"t2start",tasDegeri);
        t1end = TasarimRenginiGetir.RengiGetir(HazirlayanlarActivity.this,"t1end",tasDegeri);
        t2end = TasarimRenginiGetir.RengiGetir(HazirlayanlarActivity.this,"t2end",tasDegeri);
        orta = TasarimRenginiGetir.RengiGetir(HazirlayanlarActivity.this,"orta",tasDegeri);


        gradient.setColors(new int[]{
                renk1,
                orta,
                renk2,
        });

        ortaRenk = orta;

        simge.setColorFilter(ortaRenk);

        background.setBackground(gradient);
    }
}