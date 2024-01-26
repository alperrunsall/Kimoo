package com.kimoo.android.extra;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.CountDownTimer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.kimoo.android.DigerProfilActivity;
import com.kimoo.android.Model.User;
import com.kimoo.android.Model.Yer;
import com.kimoo.android.R;
import com.kimoo.android.TaraActivity;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

public class YerAdapter extends RecyclerView.Adapter<YerAdapter.ViewHolder> {

    private Context mContext;
    private List<Yer> mYer;
    private String gidecegimSlot = "";
    private String bosSlot = "";
    private User Kullanici;
    private DatabaseReference ref2;
    private Activity activity;
    public static ArrayList<Integer> seciliSaatDilimleri = new ArrayList<>();
    public static Button secButon;
    private AlertDialog konumDetayAd;
    private boolean gittim;
    private boolean butonTiklandiMi = false;

    public YerAdapter(Context mContext,Activity activity, List<Yer> mYer, User Kullanici) {
        this.mContext = mContext;
        this.mYer = mYer;
        this.Kullanici = Kullanici;
        this.activity = activity;
    }
    @NonNull
    @Override
    public YerAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.konum_view,viewGroup,false);
        return new YerAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final YerAdapter.ViewHolder viewHolder, int i) {
        Yer yer = mYer.get(i);
        final boolean[] gidiyorMuyum = {false};
        final ArrayList<String> arkadaslariminPPsi = new ArrayList<>();
        final String[] tekusername = new String[1];
        final String[] tekuid = new String[1];
        final ArrayList<String> hedefListem = new ArrayList<>();
        final ArrayList<String> gidenSayisi = new ArrayList<>();
        final FirebaseUser fuser = FirebaseAuth.getInstance().getCurrentUser();
        final DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Yerler").child(yer.getSehir()).child(yer.getDbisim());
        ref2 = FirebaseDatabase.getInstance().getReference("usersF").child(fuser.getUid());
        final DatabaseReference ref3 = FirebaseDatabase.getInstance().getReference("usersF");

        final boolean[] gorev = {false};
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot gidenlerSnap) {
                /*if(dataSnapshot.child("gittigi_yer").getValue().equals(yer.getDbisim())){
                    viewHolder.git_icon.setImageDrawable(mContext.getResources().getDrawable(R.drawable.adam_icon));
                }*/

                if(gidenlerSnap.hasChild("gorev"))
                    gorev[0] = true;
                else
                    gorev[0] = false;

                SlotlariGetir(ref2,yer,gorev[0]);
                int gidenSayisiint = 0;
                if(gidenlerSnap.hasChild("gidenler")){
                    boolean varmi = false;
                    for(DataSnapshot gidenlerDs : gidenlerSnap.child("gidenler").getChildren()){
                        gidenSayisiint++;
                        for(DataSnapshot gidenlerDs2 : gidenlerDs.getChildren()){
                            if (!gidenlerDs2.getKey().equals("deneme"))
                            if(!gidenSayisi.contains(gidenlerDs2.getKey()))
                                gidenSayisi.add(gidenlerDs2.getKey());
                            yer.setZiyaretciSayisi(gidenSayisi.size());
                        }
                        if (yer.getIsim().contains(" "))
                            viewHolder.isim.setText(yer.getIsim().substring(0,yer.getIsim().indexOf(" ")) + " " + yer.getIsim().substring(yer.getIsim().indexOf(" "),yer.getIsim().indexOf(" ")+2) + "." +"("+ gidenSayisi.size() + ")");
                        else
                            viewHolder.isim.setText(yer.getIsim()+"("+ gidenSayisi.size() + ")");
                        if(!varmi)
                        if(!gidenlerDs.hasChild(fuser.getUid())){
                            viewHolder.gidiyorum.setBackground(mContext.getResources().getDrawable(R.drawable.anasayfa_tara_btn));
                            viewHolder.git_yazi.setTextColor(TaraActivity.ortaRenk);
                            if(!gorev[0]) {
                                viewHolder.git_yazi.setText("Git");
                            }else{
                                viewHolder.git_yazi.setText("Görev");
                            }
                            gidiyorMuyum[0] = false;
                            gittim = false;
                        }
                        else{
                            if(!gorev[0]) {
                                viewHolder.gidiyorum.setBackground(mContext.getResources().getDrawable(R.drawable.yerler_btn_arka2));
                                viewHolder.git_yazi.setText("Gitme");
                                viewHolder.git_yazi.setTextColor(mContext.getResources().getColor(R.color.beyaz));
                            }else{
                                viewHolder.gidiyorum.setBackground(mContext.getResources().getDrawable(R.drawable.yerler_btn_arka3));
                                viewHolder.git_yazi.setText("İptal");
                                viewHolder.git_yazi.setTextColor(mContext.getResources().getColor(R.color.beyaz));
                            }
                            gittim = false;
                            gidiyorMuyum[0] = true;
                            varmi = true;
                            ref2.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    for(DataSnapshot ds : dataSnapshot.child("gidecegim_yerler").getChildren()){
                                        if(ds.child("dbisim").getValue().equals(yer.getDbisim())){
                                            if(ds.child("gittimMi").getValue().equals("evet")){
                                                viewHolder.gidiyorum.setBackground(mContext.getResources().getDrawable(R.drawable.yerler_btn_arka2));
                                                viewHolder.git_yazi.setText("Gittim!");
                                                gittim = true;
                                            }
                                        }

                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });
                        }

                        if(gidenSayisiint == gidenlerSnap.child("gidenler").getChildrenCount()){
                            /*FirebaseDatabase.getInstance().getReference("usersF").child(fuser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot benimSnap) {
                                    if (benimSnap.hasChild("begendiklerim")) {
                                        for(DataSnapshot keySnap : benimSnap.child("begendiklerim").getChildren()) {
                                            if (gidenSayisi.contains(keySnap.getKey())) {
                                                FirebaseDatabase.getInstance().getReference("usersF").child(keySnap.getKey()).addListenerForSingleValueEvent(new ValueEventListener() {
                                                    @Override
                                                    public void onDataChange(@NonNull DataSnapshot onunSnap) {
                                                        User user = onunSnap.getValue(User.class);
                                                        if (user.getGizlilik_mekan().equals("1")) {
                                                            if (onunSnap.hasChild("begenenler")) {
                                                                for(DataSnapshot ds3 : onunSnap.child("begenenler").getChildren()){
                                                                    if (ds3.getKey().equals(fuser.getUid())){
                                                                        if (!hedefListem.contains(keySnap.getKey()))
                                                                            hedefListem.add(keySnap.getKey());
                                                                    }
                                                                }
                                                            }
                                                        }
                                                        else if (user.getGizlilik_mekan().equals("4")) {
                                                            if (!hedefListem.contains(keySnap.getKey()))
                                                                hedefListem.add(keySnap.getKey());
                                                        }
                                                    }

                                                    @Override
                                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                                    }
                                                });
                                            }
                                        }
                                    }
                                    if (benimSnap.hasChild("begenenenler")) {
                                        for(DataSnapshot keySnap : benimSnap.child("begenenenler").getChildren()) {
                                            if (gidenSayisi.contains(keySnap.getKey())) {
                                                FirebaseDatabase.getInstance().getReference("usersF").child(keySnap.getKey()).addListenerForSingleValueEvent(new ValueEventListener() {
                                                    @Override
                                                    public void onDataChange(@NonNull DataSnapshot onunSnap) {
                                                        User user = onunSnap.getValue(User.class);
                                                        if (user.getGizlilik_mekan().equals("0")) {
                                                            if (onunSnap.hasChild("begendiklerim")) {
                                                                for(DataSnapshot ds3 : onunSnap.child("begendiklerim").getChildren()){
                                                                    if (ds3.getKey().equals(fuser.getUid())){
                                                                        if (!hedefListem.contains(keySnap.getKey()))
                                                                            hedefListem.add(keySnap.getKey());
                                                                    }
                                                                }
                                                            }
                                                        }
                                                        else if (user.getGizlilik_mekan().equals("4")) {
                                                            if (!hedefListem.contains(keySnap.getKey()))
                                                                hedefListem.add(keySnap.getKey());
                                                        }
                                                    }

                                                    @Override
                                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                                    }
                                                });
                                            }
                                        }
                                    }
                                    if (benimSnap.hasChild("mesajlastiklarim")) {
                                        for(DataSnapshot keySnap : benimSnap.child("mesajlastiklarim").getChildren()) {
                                            if (gidenSayisi.contains(keySnap.getKey())) {
                                                FirebaseDatabase.getInstance().getReference("usersF").child(keySnap.getKey()).addListenerForSingleValueEvent(new ValueEventListener() {
                                                    @Override
                                                    public void onDataChange(@NonNull DataSnapshot onunSnap) {
                                                        User user = onunSnap.getValue(User.class);
                                                        if (user.getGizlilik_mekan().equals("2")) {
                                                            if (!hedefListem.contains(keySnap.getKey()))
                                                                hedefListem.add(keySnap.getKey());
                                                        }
                                                        else if (user.getGizlilik_mekan().equals("4")) {
                                                            if (!hedefListem.contains(keySnap.getKey()))
                                                                hedefListem.add(keySnap.getKey());
                                                        }
                                                    }

                                                    @Override
                                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                                    }
                                                });
                                            }
                                        }
                                    }


                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });*/
                            /*if (yer.getDbisim().equals("barlar_sokagi"))
                                Toast.makeText(mContext, ""+gidenSayisi.toString(), Toast.LENGTH_SHORT).show();*/
                            if (gidenSayisi.size() > 0)
                            ref3.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    for (int i = 0; i < gidenSayisi.size(); i++) {
                                        if (arkadaslariminPPsi.size() < 3) {
                                            if (arkadaslariminPPsi.size() == 0) {
                                                tekuid[0] = gidenSayisi.get(0);
                                                tekusername[0] = dataSnapshot.child(gidenSayisi.get(i)).child("usernamef").getValue(String.class);
                                            } else {
                                                /*if (!arkadaslariminPPsi.contains(dataSnapshot.child(gidenSayisi.get(i)).child("fotograflarim").child("pp").getValue(String.class)))
                                                    arkadaslariminPPsi.add(dataSnapshot.child(gidenSayisi.get(i)).child("fotograflarim").child("pp").getValue(String.class));*/
                                            }
                                            if (dataSnapshot.hasChild(gidenSayisi.get(i))) {
                                                User user = dataSnapshot.child(gidenSayisi.get(i)).getValue(User.class);
                                                if (!user.getUid().equals(fuser.getUid())) {
                                                    if (user.getGizlilik_mekan().equals("0")) {
                                                        if (dataSnapshot.hasChild("begendiklerim")) {
                                                            if (dataSnapshot.child("begendiklerim").hasChild(fuser.getUid())) {
                                                                if (!arkadaslariminPPsi.contains(dataSnapshot.child(gidenSayisi.get(i)).child("fotograflarim").child("pp").getValue(String.class)))
                                                                    arkadaslariminPPsi.add(dataSnapshot.child(gidenSayisi.get(i)).child("fotograflarim").child("pp").getValue(String.class));
                                                            } else {
                                                            }
                                                        } else {
                                                        }
                                                    } else if (user.getGizlilik_mekan().equals("1")) {
                                                        if (dataSnapshot.hasChild("begenenler")) {
                                                            if (dataSnapshot.child("begenenler").hasChild(fuser.getUid())) {
                                                                if (!arkadaslariminPPsi.contains(dataSnapshot.child(gidenSayisi.get(i)).child("fotograflarim").child("pp").getValue(String.class)))
                                                                    arkadaslariminPPsi.add(dataSnapshot.child(gidenSayisi.get(i)).child("fotograflarim").child("pp").getValue(String.class));
                                                            } else {
                                                            }
                                                        } else {
                                                        }
                                                    } else if (user.getGizlilik_mekan().equals("2")) {
                                                        if (dataSnapshot.hasChild("mesajlastiklarim")) {
                                                            if (dataSnapshot.child("mesajlastiklarim").hasChild(fuser.getUid())) {
                                                                if (!arkadaslariminPPsi.contains(dataSnapshot.child(gidenSayisi.get(i)).child("fotograflarim").child("pp").getValue(String.class)))
                                                                    arkadaslariminPPsi.add(dataSnapshot.child(gidenSayisi.get(i)).child("fotograflarim").child("pp").getValue(String.class));
                                                            } else {
                                                            }
                                                        } else {
                                                        }
                                                    } else if (user.getGizlilik_mekan().equals("3")) {
                                                    } else if (user.getGizlilik_mekan().equals("4")) {
                                                        if (!arkadaslariminPPsi.contains(dataSnapshot.child(gidenSayisi.get(i)).child("fotograflarim").child("pp").getValue(String.class)))
                                                            arkadaslariminPPsi.add(dataSnapshot.child(gidenSayisi.get(i)).child("fotograflarim").child("pp").getValue(String.class));
                                                    }
                                                }
                                            }

                                        }
                                        if (i == gidenSayisi.size()-1){
                                            if (gidenSayisi.size() > 0 && gidenSayisi.size() <= 3) {
                                                viewHolder.resimler.setVisibility(View.VISIBLE);
                                                viewHolder.ekstra_gelenler.setVisibility(View.INVISIBLE);
                                                if (arkadaslariminPPsi.size() == 3) {
                                                    viewHolder.giden1.setVisibility(View.VISIBLE);
                                                    Glide.with(mContext)
                                                            .asBitmap()
                                                            .load(arkadaslariminPPsi.get(0))
                                                            .into(viewHolder.giden1);
                                                    viewHolder.giden2.setVisibility(View.VISIBLE);
                                                    Glide.with(mContext)
                                                            .asBitmap()
                                                            .load(arkadaslariminPPsi.get(1))
                                                            .into(viewHolder.giden2);
                                                    viewHolder.giden3.setVisibility(View.VISIBLE);
                                                    Glide.with(mContext)
                                                            .asBitmap()
                                                            .load(arkadaslariminPPsi.get(2))
                                                            .into(viewHolder.giden3);
                                                }
                                                else if (arkadaslariminPPsi.size() == 2) {
                                                    viewHolder.giden1.setVisibility(View.VISIBLE);
                                                    Glide.with(mContext)
                                                            .asBitmap()
                                                            .load(arkadaslariminPPsi.get(0))
                                                            .into(viewHolder.giden1);
                                                    viewHolder.giden2.setVisibility(View.VISIBLE);
                                                    viewHolder.giden3.setVisibility(View.GONE);
                                                    Glide.with(mContext)
                                                            .asBitmap()
                                                            .load(arkadaslariminPPsi.get(1))
                                                            .into(viewHolder.giden2);
                                                }
                                                else if (arkadaslariminPPsi.size() == 1) {
                                                    viewHolder.giden3.setVisibility(View.GONE);
                                                    viewHolder.giden2.setVisibility(View.GONE);
                                                    viewHolder.giden1.setVisibility(View.VISIBLE);
                                                    Glide.with(mContext)
                                                            .asBitmap()
                                                            .load(arkadaslariminPPsi.get(0))
                                                            .into(viewHolder.giden1);
                                                }
                                                else {
                                                    viewHolder.resimler.setVisibility(View.INVISIBLE);
                                                }
                                            }
                                            else {
                                                if (arkadaslariminPPsi.size() == 3) {
                                                    viewHolder.giden1.setVisibility(View.VISIBLE);
                                                    Glide.with(mContext)
                                                            .asBitmap()
                                                            .load(arkadaslariminPPsi.get(0))
                                                            .into(viewHolder.giden1);
                                                    viewHolder.giden2.setVisibility(View.VISIBLE);
                                                    Glide.with(mContext)
                                                            .asBitmap()
                                                            .load(arkadaslariminPPsi.get(1))
                                                            .into(viewHolder.giden2);
                                                    viewHolder.giden3.setVisibility(View.VISIBLE);
                                                    Glide.with(mContext)
                                                            .asBitmap()
                                                            .load(arkadaslariminPPsi.get(2))
                                                            .into(viewHolder.giden3);
                                                }
                                                if (arkadaslariminPPsi.size() > 0) {
                                                    viewHolder.resimler.setVisibility(View.VISIBLE);
                                                    viewHolder.ekstra_gelenler.setVisibility(View.VISIBLE);
                                                    viewHolder.ekstra_gelenler.setText("+" + (gidenSayisi.size() - 3) + " kişi");
                                                }
                                                else {
                                                    viewHolder.ekstra_gelenler.setVisibility(View.INVISIBLE);
                                                    viewHolder.resimler.setVisibility(View.INVISIBLE);
                                                }

                                            }
                                        }
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });
                        }
                    }


                }
                else{
                    if(!gorev[0])
                        viewHolder.git_yazi.setText("Git");
                    else
                        viewHolder.git_yazi.setText("Görev");

                    viewHolder.isim.setText(yer.getIsim()+"(0)");
                    viewHolder.gidiyorum.setBackground(mContext.getResources().getDrawable(R.drawable.yerler_btn_arka1));
                    viewHolder.git_yazi.setTextColor(TaraActivity.ortaRenk);
                    gidiyorMuyum[0] = false;
                    gittim = false;
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        viewHolder.gidiyorum.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!gittim) {
                    if (gidiyorMuyum[0]) {
                        ref.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                for (DataSnapshot ds : dataSnapshot.child("gidenler").getChildren()) {
                                    if (ds.hasChild(fuser.getUid()))
                                        ds.child(fuser.getUid()).getRef().removeValue();
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
                        if (!gidecegimSlot.equals("") && gidecegimSlot != null) {
                            ref2.child("gidecegim_yerler").child(gidecegimSlot).child("dbisim").setValue("").addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(@NonNull Void aVoid) {
                                    if (!gorev[0])
                                        viewHolder.git_yazi.setText("Git");
                                    else
                                        viewHolder.git_yazi.setText("Görev");
                                    viewHolder.gidiyorum.setBackground(mContext.getResources().getDrawable(R.drawable.yerler_btn_arka1));
                                    viewHolder.git_yazi.setTextColor(TaraActivity.ortaRenk);
                                    seciliSaatDilimleri = new ArrayList<>();
                                    gidiyorMuyum[0] = false;
                                    SlotlariGetir(ref2, yer, gorev[0]);
                                }
                            });
                        }
                    }
                    else {
                        if (!bosSlot.equals("")) {
                            seciliSaatDilimleri = new ArrayList<>();
                            SaatiSor(yer,gorev[0]);
                        /*ref.child("gidenler").child(fuser.getUid()).setValue("");
                        ref2.child("gidecegim_yerler").child(bosSlot).setValue(yer.getDbisim()).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(@NonNull Void aVoid) {
                                viewHolder.gidiyorum.setBackground(mContext.getResources().getDrawable(R.drawable.yerler_btn_arka2));
                                viewHolder.git_yazi.setText("Gidiyorum");
                                viewHolder.git_yazi.setTextColor(mContext.getResources().getColor(R.color.beyaz));
                                gidiyorMuyum[0] = true;
                                SlotlariGetir(ref2, yer);
                            }
                        });*/
                        } else {
                            Toast.makeText(mContext, "En fazla 3 yere gidebilirsiniz.", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
                else
                    Toast.makeText(activity, "Zaten buraya bugün gitmişsiniz, yarın tekrar gidebilirsiniz.", Toast.LENGTH_SHORT).show();
            }
        });

        viewHolder.konumRel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!butonTiklandiMi) {
                    butonTiklandiMi = true;
                    KonumDetayiniGoster(yer, gorev[0]);
                }
            }
        });
        /*viewHolder.konumuGor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String uri = String.format(Locale.ENGLISH, "geo:"+yer.getKonum_long()+","+yer.getKonum_latit()+"?z=16");
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
                mContext.startActivity(intent);
            }
        });*/
        viewHolder.resimler.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(arkadaslariminPPsi.size() == 1){
                    Intent intent = new Intent(mContext, DigerProfilActivity.class);
                    intent.putExtra("userid", tekuid[0]);
                    intent.putExtra("username", tekusername[0]);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    mContext.startActivity(intent);
                }
                else if (arkadaslariminPPsi.size() > 1){
                    GidenArklariGoster(yer.getIsim(),gidenSayisi);
                }
                else{

                }
            }
        });
    }

    private void GidenArklariGoster(String yer, ArrayList<String> gidenArkadaslarim) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(activity,R.style.CustomAlertDialog);
        LayoutInflater inflater = activity.getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.konum_ayni_yere_giden_arkadaslarim, null);
        dialogBuilder.setView(dialogView);
        dialogBuilder.setCancelable(true);

        TextView mekanisim = dialogView.findViewById(R.id.isim);
        TextView belirtilenSaat = dialogView.findViewById(R.id.calisma_saatleri);
        RecyclerView recyclerView = dialogView.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(activity));
        Button sec = dialogView.findViewById(R.id.saati_sec_btn);
        belirtilenSaat.setText(yer);
        mekanisim.setText(yer);

        ArrayList<User> mUsers = new ArrayList<>();

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("usersF");

        for(int i = 0; i < gidenArkadaslarim.size(); i++){
            ref.child(gidenArkadaslarim.get(i)).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if(dataSnapshot.exists()) {
                        User user = dataSnapshot.getValue(User.class);
                        if (!user.getUid().equals(Kullanici.getUid())) {
                            mUsers.add(user);
                            FavoriAdapter favoriAdapter = new FavoriAdapter(activity, activity, mUsers);
                            recyclerView.setAdapter(favoriAdapter);
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
        final AlertDialog ad = dialogBuilder.show();
        sec.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ad.dismiss();
            }
        });

    }
    private void SaatiSor(Yer yer,boolean gorevMi) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Yerler").child(yer.getSehir()).child(yer.getDbisim()).child("gidenler");
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(Kullanici.getSuan());
        calendar.setTimeZone(TimeZone.getTimeZone("Turkey"));
        int saat = calendar.get(Calendar.HOUR_OF_DAY);

        ArrayList<String> tumSaatler = new ArrayList<>();
        ArrayList<String> saatler = new ArrayList<>();
        ArrayList<String> gecmisSaatler = new ArrayList<>();
        tumSaatler.add("0");
        tumSaatler.add("08:00 - 10:00"); //1
        tumSaatler.add("10:00 - 12:00"); //2
        tumSaatler.add("12:00 - 14:00"); //3
        tumSaatler.add("14:00 - 16:00"); //4
        tumSaatler.add("16:00 - 18:00"); //5
        tumSaatler.add("18:00 - 20:00"); //6
        tumSaatler.add("20:00 - 22:00"); //7
        tumSaatler.add("22:00 - 00:00"); //8
        tumSaatler.add("00:00 - 02:00"); //9
        tumSaatler.add("02:00 - 04:00"); //10
        tumSaatler.add("04:00 - 06:00"); //11

        ArrayList<String> mSaat = new ArrayList<>();

        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(activity,R.style.CustomAlertDialog);
        LayoutInflater inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View dialogView = inflater.inflate(R.layout.konum_dialog_dizayn_saat_belirle, null);
        dialogBuilder.setView(dialogView);
        dialogBuilder.setCancelable(true);

        Button sec = dialogView.findViewById(R.id.saati_sec_btn);
        Button geri_gel = dialogView.findViewById(R.id.geri_gel);
        TextView mekanisim = dialogView.findViewById(R.id.isim);
        //TextView calismaSaati = dialogView.findViewById(R.id.calisma_saatleri);
        TextView aciklamaTV = dialogView.findViewById(R.id.isletme_aciklamasi);
        ProgressBar pbar = dialogView.findViewById(R.id.pbar);
        RecyclerView recyclerView = dialogView.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new GridLayoutManager(mContext, 2));
        mekanisim.setText(yer.getIsim());
        //calismaSaati.setText("Çalışma saatleri: " + yer.getCalisma_saati());
        aciklamaTV.setText(yer.getAciklama());
        secButon = sec;

        mekanisim.setTextColor(TaraActivity.ortaRenk);

        if(gorevMi)
            Toast.makeText(activity, "Eğer bu konumu ziyaret ederseniz +" + TaraActivity.fiyatlar.getZiyaret_gorevi()+"KP kazanacaksınız!", Toast.LENGTH_SHORT).show();

        if(yer.getCalisma_saatleri().contains("1")) {
            mSaat.add(tumSaatler.get(1));
            saatler.add("1");
            if (saat > 9 || saat < 6)
                gecmisSaatler.add("1");
        }
        if(yer.getCalisma_saatleri().contains("2")) {
            mSaat.add(tumSaatler.get(2));
            saatler.add("2");
            if (saat > 11 || saat < 6)
                gecmisSaatler.add("2");
        }
        if(yer.getCalisma_saatleri().contains("3")) {
            mSaat.add(tumSaatler.get(3));
            saatler.add("3");
            if (saat > 13 || saat < 6)
                gecmisSaatler.add("3");
        }
        if(yer.getCalisma_saatleri().contains("4")) {
            mSaat.add(tumSaatler.get(4));
            saatler.add("4");
            if (saat > 15 || saat < 6)
                gecmisSaatler.add("4");
        }
        if(yer.getCalisma_saatleri().contains("5")) {
            mSaat.add(tumSaatler.get(5));
            saatler.add("5");
            if (saat > 17 || saat < 6)
                gecmisSaatler.add("5");
        }
        if(yer.getCalisma_saatleri().contains("6")) {
            mSaat.add(tumSaatler.get(6));
            saatler.add("6");
            if (saat > 19 || saat < 6)
                gecmisSaatler.add("6");
        }
        if(yer.getCalisma_saatleri().contains("7")) {
            mSaat.add(tumSaatler.get(7));
            saatler.add("7");
            if (saat > 21 || saat < 6)
                gecmisSaatler.add("7");
        }
        if(yer.getCalisma_saatleri().contains("8")) {
            mSaat.add(tumSaatler.get(8));
            saatler.add("8");
            if (saat < 1)
                gecmisSaatler.add("8");
        }
        if(yer.getCalisma_saatleri().contains("9")) {
            mSaat.add(tumSaatler.get(9));
            saatler.add("9");
            if (saat > 1 && saat < 6)
                gecmisSaatler.add("9");
        }
        if(yer.getCalisma_saatleri().contains("10")) {
            mSaat.add(tumSaatler.get(10));
            saatler.add("10");
            if (saat > 3 && saat < 6)
                gecmisSaatler.add("10");
        }
        if(yer.getCalisma_saatleri().contains("11")) {
            mSaat.add(tumSaatler.get(11));
            saatler.add("11");
            if (saat > 5 && saat < 8)
                gecmisSaatler.add("11");
        }



        SaatAdapter saatAdapter = new SaatAdapter(mContext,activity,mSaat,saatler,gecmisSaatler,yer.getDbisim(),yer.getIsim());
        recyclerView.setAdapter(saatAdapter);


        final AlertDialog dialog = dialogBuilder.create();
        dialog.show();

        sec.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(seciliSaatDilimleri.size() > 0){
                    for(int i = 0; i < seciliSaatDilimleri.size(); i++){
                        reference.child(String.valueOf(seciliSaatDilimleri.get(i))).child(Kullanici.getUid()).setValue("").addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(@NonNull Void aVoid) {
                                if (!bosSlot.equals("")) {
                                    ref2.child("gidecegim_yerler").child(bosSlot).child("isim").setValue(yer.getIsim());
                                    ref2.child("gidecegim_yerler").child(bosSlot).child("dbisim").setValue(yer.getDbisim());
                                    ref2.child("gidecegim_yerler").child(bosSlot).child("gittimMi").setValue("hayir");
                                    ref2.child("gidecegim_yerler").child(bosSlot).child("zaman").setValue(ServerValue.TIMESTAMP).addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(@NonNull Void unused) {
                                            seciliSaatDilimleri = new ArrayList<>();
                                            SlotlariGetir(ref2, yer, gorevMi);
                                            if (konumDetayAd != null)
                                                konumDetayAd.dismiss();
                                            dialog.dismiss();
                                        }
                                    });
                                }
                            }
                        });
                    }
                }
                else{
                    reference.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            for(DataSnapshot ds : dataSnapshot.getChildren()){
                                if(ds.hasChild(Kullanici.getUid())) {
                                    ds.child(Kullanici.getUid()).getRef().removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(@NonNull Void unused) {
                                            dialog.dismiss();
                                        }
                                    });
                                }
                                else
                                    dialog.dismiss();
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                }
            }
        });

        geri_gel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                seciliSaatDilimleri = new ArrayList<>();
                dialog.dismiss();
            }
        });
    }
    private void KonumDetayiniGoster(Yer yer, boolean gorevMi) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Yerler").child(yer.getSehir()).child(yer.getDbisim()).child("gidenler");
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(activity,R.style.CustomAlertDialog);
        LayoutInflater inflater = activity.getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.konum_detayli_goruntuleme, null);
        dialogBuilder.setView(dialogView);
        dialogBuilder.setCancelable(true);


        CardView konumBTN, resimlerBTN;
        ArrayList<String> tumSaatler = new ArrayList<>();
        ArrayList<String> saatler = new ArrayList<>();
        ArrayList<SaatVeZiyaretci> ziyaretciler = new ArrayList<>();
        tumSaatler.add("0");
        tumSaatler.add("08:00 - 10:00"); //1
        tumSaatler.add("10:00 - 12:00"); //2
        tumSaatler.add("12:00 - 14:00"); //3
        tumSaatler.add("14:00 - 16:00"); //4
        tumSaatler.add("16:00 - 18:00"); //5
        tumSaatler.add("18:00 - 20:00"); //6
        tumSaatler.add("20:00 - 22:00"); //7
        tumSaatler.add("22:00 - 00:00"); //8
        tumSaatler.add("00:00 - 02:00"); //9
        tumSaatler.add("02:00 - 04:00"); //10
        tumSaatler.add("04:00 - 06:00"); //11

        ArrayList<String> mSaat = new ArrayList<>();
        Button geri_gel = dialogView.findViewById(R.id.geri_gel);
        Button konuma_git = dialogView.findViewById(R.id.konumaGit);
        TextView mekanisim = dialogView.findViewById(R.id.isim);
        TextView ziyaretciSayisi = dialogView.findViewById(R.id.ziyaretci_sayisi);
        TextView calismaSaati = dialogView.findViewById(R.id.calisma_saatleri);
        TextView aciklama = dialogView.findViewById(R.id.isletme_aciklamasi);
        LinearLayout arka1 = dialogView.findViewById(R.id.butonArkasi1);
        LinearLayout arka2 = dialogView.findViewById(R.id.butonArkasi2);
        arka1.setBackground(TaraActivity.gradientNormal);
        arka2.setBackground(TaraActivity.gradientNormal);
        RecyclerView istatistikRV = dialogView.findViewById(R.id.istatistikRV);
        konumBTN = dialogView.findViewById(R.id.konumBTN);
        resimlerBTN = dialogView.findViewById(R.id.resimlerBTN);
        mekanisim.setText(yer.getIsim());
        calismaSaati.setText("Çalışma saatleri: " + yer.getCalisma_saati());
        aciklama.setText(yer.getAciklama());
        ziyaretciSayisi.setText("Ziyaretçi Sayısı: "+ yer.getZiyaretciSayisi());

        mekanisim.setTextColor(TaraActivity.ortaRenk);


        if(yer.getCalisma_saatleri().contains("1")) {
            mSaat.add(tumSaatler.get(1));
            saatler.add("1");
        }
        if(yer.getCalisma_saatleri().contains("2")) {
            mSaat.add(tumSaatler.get(2));
            saatler.add("2");
        }
        if(yer.getCalisma_saatleri().contains("3")) {
            mSaat.add(tumSaatler.get(3));
            saatler.add("3");
        }
        if(yer.getCalisma_saatleri().contains("4")) {
            mSaat.add(tumSaatler.get(4));
            saatler.add("4");
        }
        if(yer.getCalisma_saatleri().contains("5")) {
            mSaat.add(tumSaatler.get(5));
            saatler.add("5");
        }
        if(yer.getCalisma_saatleri().contains("6")) {
            mSaat.add(tumSaatler.get(6));
            saatler.add("6");
        }
        if(yer.getCalisma_saatleri().contains("7")) {
            mSaat.add(tumSaatler.get(7));
            saatler.add("7");
        }
        if(yer.getCalisma_saatleri().contains("8")) {
            mSaat.add(tumSaatler.get(8));
            saatler.add("8");
        }
        if(yer.getCalisma_saatleri().contains("9")) {
            mSaat.add(tumSaatler.get(9));
            saatler.add("9");
        }
        if(yer.getCalisma_saatleri().contains("10")) {
            mSaat.add(tumSaatler.get(10));
            saatler.add("10");
        }
        if(yer.getCalisma_saatleri().contains("11")) {
            mSaat.add(tumSaatler.get(11));
            saatler.add("11");
        }
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                int enCokZiyaretci = -1;
                for(DataSnapshot ds : dataSnapshot.getChildren()){
                    ziyaretciler.add(new SaatVeZiyaretci(mSaat.get(ziyaretciler.size()), (int) (ds.getChildrenCount()-1)));
                    if(enCokZiyaretci == -1)
                        enCokZiyaretci = ziyaretciler.get(ziyaretciler.size()-1).getZiyaretci();
                    else
                    if (enCokZiyaretci < ziyaretciler.get(ziyaretciler.size()-1).getZiyaretci()) {
                        enCokZiyaretci = ziyaretciler.get(ziyaretciler.size()-1).getZiyaretci();
                    }
                    if(ziyaretciler.size() == dataSnapshot.getChildrenCount()) {
                        istatistikRV.setHasFixedSize(true);
                        GridLayoutManager manager = new GridLayoutManager(activity,mSaat.size());
                        istatistikRV.setLayoutManager(manager);
                        SaatViewAdapter saatViewAdapter = new SaatViewAdapter(mContext,activity,ziyaretciler,enCokZiyaretci);
                        istatistikRV.setAdapter(saatViewAdapter);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        final AlertDialog ad = dialogBuilder.show();
        konumDetayAd = ad;
        geri_gel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                seciliSaatDilimleri = new ArrayList<>();
                butonTiklandiMi = false;
                ad.dismiss();
            }
        });
        konuma_git.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                butonTiklandiMi = false;
                final int[] gitmismi = {0};
                final int[] kontrol = {0};
                ref2.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for(DataSnapshot ds : dataSnapshot.child("gidecegim_yerler").getChildren()){
                            if(ds.child("dbisim").getValue().equals(yer.getDbisim())){
                                if(ds.child("gittimMi").getValue().equals("evet")){
                                    gitmismi[0]++;
                                    kontrol[0]++;
                                }
                                else
                                    kontrol[0]++;
                            }
                            else
                                kontrol[0]++;

                            if(kontrol[0] == dataSnapshot.child("gidecegim_yerler").getChildrenCount()){
                                if(gitmismi[0] > 0)
                                    Toast.makeText(mContext, "Zaten buraya bugün gitmişsiniz, yarın tekrar gidebilirsiniz.", Toast.LENGTH_SHORT).show();
                                else
                                    SaatKisminiAC(yer,gorevMi);
                            }

                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }
        });

        konumBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String uri = String.format(Locale.ENGLISH, "geo:"+yer.getKonum_long()+","+yer.getKonum_latit()+"?z=17");
                //String uri = String.format(Locale.ENGLISH, "geo:%f,%f", yer.getKonum_latit(), yer.getKonum_long());
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
                activity.startActivity(intent);
            }
        });
        resimlerBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ResimlerKisminiGetir(yer);
            }
        });
        ad.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                butonTiklandiMi = false;
            }
        });

    }

    private void SaatKisminiAC(Yer yer,boolean gorevMi){
        final int[] sayi = {0};
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Yerler").child(Kullanici.getSehir()).child(yer.getDbisim()).child("gidenler");
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                //viewHolder.kisiSayisi.setText(String.valueOf(dataSnapshot2.child(saatid).getChildrenCount()-1));
                for(DataSnapshot ds : dataSnapshot.getChildren()){
                    if(ds.hasChild(Kullanici.getUid())){
                        seciliSaatDilimleri.add(Integer.valueOf(ds.getKey()));
                        sayi[0]++;
                    }
                    else
                        sayi[0]++;
                    if(sayi[0] == dataSnapshot.getChildrenCount()) {
                        if (seciliSaatDilimleri.size() == 1)
                            SaatiSor(yer,gorevMi);
                        else if (seciliSaatDilimleri.size() > 1)
                            Toast.makeText(mContext, "Zaten buraya gidiyorsunuz.", Toast.LENGTH_SHORT).show();
                        else
                            SaatiSor(yer,gorevMi);
                        /**/
                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void ResimlerKisminiGetir(Yer yer) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(activity,R.style.CustomAlertDialog);
        LayoutInflater inflater = activity.getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.google_resim_arama_konum_icin, null);
        dialogBuilder.setView(dialogView);
        dialogBuilder.setCancelable(true);

        Button geri = dialogView.findViewById(R.id.geri_gel);
        WebView webView = dialogView.findViewById(R.id.webView);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.loadUrl("https://www.google.com/search?tbm=isch&q=" + yer.getIsim());

        final AlertDialog ad = dialogBuilder.show();

        geri.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ad.dismiss();
            }
        });
    }

    private void SlotlariGetir(DatabaseReference ref2, Yer yer,boolean gorevMi) {
        bosSlot = "";
        ref2.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if (dataSnapshot.child("gidecegim_yerler").child("3").child("dbisim").getValue(String.class).equals(yer.getDbisim())){
                    gidecegimSlot = "3";
                }
                else if(dataSnapshot.child("gidecegim_yerler").child("3").child("dbisim").getValue(String.class).equals("")){
                    bosSlot = "3";
                }
                if (dataSnapshot.child("gidecegim_yerler").child("2").child("dbisim").getValue(String.class).equals(yer.getDbisim())){
                    gidecegimSlot = "2";
                }
                else if(dataSnapshot.child("gidecegim_yerler").child("2").child("dbisim").getValue(String.class).equals("")){
                    bosSlot = "2";
                }
                if (dataSnapshot.child("gidecegim_yerler").child("1").child("dbisim").getValue(String.class).equals(yer.getDbisim())){
                    gidecegimSlot = "1";
                }
                else if(dataSnapshot.child("gidecegim_yerler").child("1").child("dbisim").getValue(String.class).equals("")){
                    bosSlot = "1";
                }
                if (dataSnapshot.child("gidecegim_yerler").child("asil").child("dbisim").getValue(String.class).equals(yer.getDbisim())) {
                    gidecegimSlot = "asil";
                }
                if(gorevMi)
                    bosSlot = "asil";
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public int getItemCount() {
        return mYer.size();
    }
    public class ViewHolder extends RecyclerView.ViewHolder{

        public TextView isim, git_yazi, ekstra_gelenler;
        public ImageView giden1,giden2,giden3;
        public RelativeLayout resimler,gidiyorum,konumRel;

        public ViewHolder(View itemView) {
            super(itemView);

            isim = itemView.findViewById(R.id.isim);
            giden1 = itemView.findViewById(R.id.gelenArk1);
            giden2 = itemView.findViewById(R.id.gelenArk2);
            giden3 = itemView.findViewById(R.id.gelenArk3);
            gidiyorum = itemView.findViewById(R.id.git);
            resimler = itemView.findViewById(R.id.resimler);
            git_yazi = itemView.findViewById(R.id.git_text);
            konumRel = itemView.findViewById(R.id.konum);
            ekstra_gelenler = itemView.findViewById(R.id.ekstra_gelenler);

        }
    }
}
