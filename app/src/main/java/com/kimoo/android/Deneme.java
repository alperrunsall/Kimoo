package com.kimoo.android;

import static com.kimoo.android.ProfilActivity.Kullanici;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.kimoo.android.Model.User;
import com.kimoo.android.fragments.SuanBulunanlar;

public class Deneme extends AppCompatActivity {

    Button buton;
    BluetoothAdapter myAdapter;
    BluetoothDevice device;
    FirebaseUser fuser;
    private User Kullanici;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_deneme);

        buton = findViewById(R.id.buton);
        fuser = FirebaseAuth.getInstance().getCurrentUser();
        myAdapter = BluetoothAdapter.getDefaultAdapter();

        buton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                IzinKontrol();
            }
        });
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("usersF").child(fuser.getUid());
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Kullanici = dataSnapshot.getValue(User.class);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
    private final BroadcastReceiver receiver = new BroadcastReceiver() {
        public void onReceive(final Context context, Intent intent) {
            String action = intent.getAction();
            buton.setText("Taranıyor...");
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                buton.setText("Cihaz bulundu...");
                    device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                    //Toast.makeText(SuanBulunanlar.this, "" + device.getName(), Toast.LENGTH_SHORT).show();
                    if (device != null) {
                        //Toast.makeText(Deneme.this, "Cihaz var: " + device.getName(), Toast.LENGTH_SHORT).show();
                        if (device.getName() != null && !device.getName().trim().equals(""))
                            if(device.getName().length() > 12) {
                                if (device.getName().substring(0, 4).equals("kim_")) {
                                    Toast.makeText(Deneme.this, "Cihaz kim_: " + device.getName(), Toast.LENGTH_SHORT).show();
                                    String k_uid = device.getName().substring(4, device.getName().trim().length() - 5);
                                    String k_id = device.getName().substring(device.getName().trim().length() - 5);
                                        //taraniyor_yazi.setText("" + bulunmusOlabilir + " Kişi bulunmuş olabilir");
                                        Query userQuery = FirebaseDatabase.getInstance().getReference("usersF").orderByChild("usernamef").equalTo(k_uid);
                                        userQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(DataSnapshot userSS) {
                                                for (DataSnapshot dataSnapshot: userSS.getChildren()) {
                                                    final User user = dataSnapshot.getValue(User.class);
                                                    if (dataSnapshot.child("ban_durumu").child("durum").getValue().equals("yok")) { // kullanıcın ban durumu yoksa
                                                        if (user.getId().equals(k_id)) { // Kullanıcının id'si uygunsa
                                                            if( (Integer.parseInt(Kullanici.getDg()) - Integer.parseInt(user.getDg())) > -6 || (Integer.parseInt(Kullanici.getDg()) - Integer.parseInt(user.getDg())) < 6) {
                                                                if (dataSnapshot.child("ev_sistemi").child("sistem").getValue(String.class).equals("aktif")) {

                                                                    if (!dataSnapshot.child("ev_sistemi").child("beni_bulanlar").hasChild(fuser.getUid()))
                                                                        dataSnapshot.child("ev_sistemi").child("beni_bulanlar").child(fuser.getUid()).getRef().setValue(ServerValue.TIMESTAMP);
                                                                }
                                                                else {
                                                                    Toast.makeText(Deneme.this, "Buldum: " + user.getAd(), Toast.LENGTH_SHORT).show();
                                                                }
                                                            }
                                                            else{
                                                                Toast.makeText(Deneme.this, "Bu senden büyük yada küçük", Toast.LENGTH_SHORT).show();
                                                            }

                                                        }
                                                        else{

                                                        }
                                                    }
                                                    else {
                                                        Toast.makeText(Deneme.this, "Banlı", Toast.LENGTH_SHORT).show();
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
    };
    public void IzinKontrol(){
        if (myAdapter.isEnabled()) {
            if (ContextCompat.checkSelfPermission(Deneme.this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
                registerReceiver(receiver, filter);
                myAdapter.startDiscovery();
            }
            else {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION},1);
                }

            }

        } else {
            Intent eintent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(eintent,3);
        }

    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        if(requestCode == 3){
            if(resultCode != 0) {
                IzinKontrol();
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
                    Toast.makeText(Deneme.this, "Eğer izin vermezseniz diğer Kimoo kullanıcıları sizi bulamaz.", Toast.LENGTH_LONG).show();
                }
                return;
            }
        }
    }
}