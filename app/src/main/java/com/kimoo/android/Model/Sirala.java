package com.kimoo.android.Model;

import androidx.annotation.NonNull;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Sirala {

    User deger1;
    int yas;
    int begeni_sayisi;
    String isim;
    String uname;

    public int getYas() {
        return Integer.parseInt(String.valueOf(deger1.getDg()));
    }

    public void setYas(int yas) {
        this.yas = yas;
    }

    public User getDeger1() {
        return deger1;
    }

    public void setDeger1(User deger1) {
        this.deger1 = deger1;
    }

    public int getBegeni_sayisi() {
        FirebaseUser fuser = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("usersF").child(fuser.getUid()).child("begenenler").child(getDeger1().getUid());
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                begeni_sayisi = Integer.parseInt(String.valueOf(dataSnapshot.child("kac_kez_begendim").getValue(Long.class)));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        return begeni_sayisi;
    }

    public void setBegeni_sayisi(int begeni_sayisi) {
        this.begeni_sayisi = begeni_sayisi;
    }

    public String getIsim() {
        return deger1.getAd();
    }

    public void setIsim(String isim) {
        this.isim = isim;
    }

    public String getUname() {
        return deger1.getUsernamef();
    }

    public void setUname(String uname) {
        this.uname = uname;
    }
}
