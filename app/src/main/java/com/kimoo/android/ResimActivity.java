package com.kimoo.android;

import android.app.Activity;
import android.content.ContextWrapper;
import android.content.Intent;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;

import com.bumptech.glide.Glide;
import com.github.chrisbanes.photoview.PhotoView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.kimoo.android.extra.ResimIndir;

import java.io.File;

public class ResimActivity extends AppCompatActivity {

    PhotoView resim;
    Button isim;
    FirebaseUser fuser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_resim);
        StatuBarAyarla();
        resim = findViewById(R.id.resim_fotograf);
        isim = findViewById(R.id.resim_isim_btn);

        fuser = FirebaseAuth.getInstance().getCurrentUser();
        String uri = getIntent().getStringExtra("resimURL");
        String deger = getIntent().getStringExtra("resimDeger");
        String benimMi = getIntent().getStringExtra("resimBenimMi");
        final String kiminmis = getIntent().getStringExtra("userid");



        if(benimMi.equals("evet")){
            ContextWrapper cw = new ContextWrapper(getApplicationContext());
            File directory = cw.getDir("benim_resimler", MODE_PRIVATE);
            File image = new File(directory, "foto_" + deger + ".jpg");
            if(image.exists()){
                resim.setImageURI(Uri.parse(image.getAbsolutePath()));
            }
            else {
                if(uri.length() > 5) {
                    Glide.with(ResimActivity.this).asBitmap().load(uri).into(resim);
                    new ResimIndir(getApplicationContext(), uri, "benim_resimler", "foto_" + deger + ".jpg");
                }
            }
        }else{
            Glide.with(ResimActivity.this)
                    .asBitmap()
                    .load(uri)
                    .into(resim);
        }

        /**/
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("usersF").child(kiminmis);
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                isim.setText(dataSnapshot.child("ad").getValue(String.class));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        isim.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(kiminmis.equals(fuser.getUid())){
                    Intent intent = new Intent(ResimActivity.this,ProfilActivity.class);
                    startActivity(intent);
                }else{
                    Intent intent = new Intent(ResimActivity.this,DigerProfilActivity.class);
                    intent.putExtra("userid",kiminmis);
                    startActivity(intent);
                }

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
}
