package com.kimoo.android.extra;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

public class YeniKisiBuldum {
    public static void YeniKisiBuldum(DatabaseReference ref, String uid, Object bulunanZaman) {
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChild("bulduklarim")) {
                    if (dataSnapshot.child("bulduklarim").hasChild(uid)) {
                        String deger = dataSnapshot.child("bulduklarim").child(uid).child("kac_kez_gordum").getValue(String.class);
                        int sayi = Integer.parseInt(deger);
                        sayi += 1;
                        dataSnapshot.child("bulduklarim").child(uid).child("kac_kez_gordum").getRef().setValue(String.valueOf(sayi));
                        dataSnapshot.child("bulduklarim").child(uid).child("son_gordugum_zaman").getRef().setValue(bulunanZaman);
                    } else {
                        dataSnapshot.child("bulduklarim").child(uid).child("kac_kez_gordum").getRef().setValue("1");
                        dataSnapshot.child("bulduklarim").child(uid).child("ilk_gordugum_zaman").getRef().setValue(bulunanZaman);
                        dataSnapshot.child("bulduklarim").child(uid).child("son_gordugum_zaman").getRef().setValue(bulunanZaman);
                    }
                }
                else {
                    dataSnapshot.child("bulduklarim").child(uid).child("kac_kez_gordum").getRef().setValue("1");
                    dataSnapshot.child("bulduklarim").child(uid).child("ilk_gordugum_zaman").getRef().setValue(bulunanZaman);
                    dataSnapshot.child("bulduklarim").child(uid).child("son_gordugum_zaman").getRef().setValue(bulunanZaman);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
