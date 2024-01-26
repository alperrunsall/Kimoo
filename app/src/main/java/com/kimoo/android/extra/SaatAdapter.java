package com.kimoo.android.extra;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.CountDownTimer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.kimoo.android.DigerProfilActivity;
import com.kimoo.android.Model.User;
import com.kimoo.android.R;

import java.util.ArrayList;
import java.util.List;

public class SaatAdapter extends RecyclerView.Adapter<SaatAdapter.ViewHolder> {

    private Context mContext;
    private List<String> mSaat, saatler, gecmisSaatler;
    private User Kullanici = null;
    private String mekan,mekanAdi;
    private DatabaseReference ref;
    private Activity activity;
    public static int yuklenenFoto;

    public SaatAdapter(Context mContext,Activity activity, List<String> mSaat, List<String> saatler, List<String> gecmisSaatler,String mekan, String mekanAdi) {
        this.mContext = mContext;
        this.mSaat = mSaat;
        this.mekan = mekan;
        this.saatler = saatler;
        this.mekanAdi = mekanAdi;
        this.activity = activity;
        this.gecmisSaatler = gecmisSaatler;
    }

    @NonNull
    @Override
    public SaatAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.saat_view,viewGroup,false);
        return new SaatAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final SaatAdapter.ViewHolder viewHolder, int i) {
        final String saat = mSaat.get(i);
        final String saatid = saatler.get(i);
        viewHolder.saatTV.setText(saat);
        final String[] tekusername = new String[1];
        final String[] tekuid = new String[1];
        int selectSayisi = 0;
        final ArrayList<String> arkadaslariminPPsi = new ArrayList<>();
        final ArrayList<String> gidenSayisi = new ArrayList<>();
        final ArrayList<String> hedefListem = new ArrayList<>();
        final DatabaseReference ref3 = FirebaseDatabase.getInstance().getReference("usersF");

        final FirebaseUser fuser = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("usersF").child(fuser.getUid());

        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot benimSnap) {
                Kullanici = benimSnap.getValue(User.class);
                ref = FirebaseDatabase.getInstance().getReference("Yerler").child(Kullanici.getSehir()).child(mekan).child("gidenler").child(saatid);
                ref.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot gidenlerSnap) {
                        int gidenSayisiint = 0;
                        for(DataSnapshot gidenlerDs : gidenlerSnap.getChildren()){
                                gidenSayisiint++;

                            if (!gidenlerDs.getKey().equals("deneme"))
                            if(!gidenSayisi.contains(gidenlerDs.getKey()))
                                gidenSayisi.add(gidenlerDs.getKey());

                            if(gidenSayisiint == gidenlerSnap.getChildrenCount()){
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

                        /*if(begendiklerim.size() > 0) {

                            if(gidenArkadaslarim.size() > 0) {
                                ref3.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        for (int i = 0; i < gidenArkadaslarim.size(); i++) {
                                            if (arkadaslariminPPsi.size() < 3) {
                                                if(arkadaslariminPPsi.size() == 0){
                                                    arkadaslariminPPsi.add(dataSnapshot.child(gidenArkadaslarim.get(i)).child("pp_url").getValue(String.class));
                                                    tekuid[0] = gidenArkadaslarim.get(0);
                                                    tekusername[0] = dataSnapshot.child(gidenArkadaslarim.get(i)).child("usernamef").getValue(String.class);
                                                }
                                                else {
                                                    if(!arkadaslariminPPsi.contains(dataSnapshot.child(gidenArkadaslarim.get(i)).child("pp_url").getValue(String.class)))
                                                        arkadaslariminPPsi.add(dataSnapshot.child(gidenArkadaslarim.get(i)).child("pp_url").getValue(String.class));
                                                }

                                            }
                                        }
                                        if (gidenArkadaslarim.size() <= 3) {
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
                                                yuklenenFoto++;
                                            }
                                            else if (arkadaslariminPPsi.size() == 2) {
                                                viewHolder.giden1.setVisibility(View.VISIBLE);
                                                Glide.with(mContext)
                                                        .asBitmap()
                                                        .load(arkadaslariminPPsi.get(0))
                                                        .into(viewHolder.giden1);
                                                viewHolder.giden2.setVisibility(View.VISIBLE);
                                                viewHolder.giden3.setVisibility(View.INVISIBLE);
                                                Glide.with(mContext)
                                                        .asBitmap()
                                                        .load(arkadaslariminPPsi.get(1))
                                                        .into(viewHolder.giden2);
                                                yuklenenFoto++;
                                            }
                                            else if (arkadaslariminPPsi.size() == 1) {
                                                viewHolder.giden3.setVisibility(View.GONE);
                                                viewHolder.giden2.setVisibility(View.GONE);
                                                viewHolder.giden1.setVisibility(View.VISIBLE);
                                                Glide.with(mContext)
                                                        .asBitmap()
                                                        .load(arkadaslariminPPsi.get(0))
                                                        .into(viewHolder.giden1);
                                                yuklenenFoto++;
                                            }else{
                                                yuklenenFoto++;
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
                                            viewHolder.resimler.setVisibility(View.VISIBLE);
                                            yuklenenFoto++;
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                    }
                                });
                            }
                            else{
                                viewHolder.giden1.setVisibility(View.GONE);
                                viewHolder.giden2.setVisibility(View.GONE);
                                viewHolder.giden3.setVisibility(View.GONE);
                            }

                        }
                        else{
                            viewHolder.giden1.setVisibility(View.GONE);
                            viewHolder.giden2.setVisibility(View.GONE);
                            viewHolder.giden3.setVisibility(View.GONE);
                        }*/
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        for(int a = 0; a < YerAdapter.seciliSaatDilimleri.size(); a++){
            if(YerAdapter.seciliSaatDilimleri.get(a) == Integer.valueOf(saatid))
                hafifKuculmeAnim(viewHolder.kuculenMavi,0);
        }
        if(YerAdapter.seciliSaatDilimleri.size() == 0)
            YerAdapter.secButon.setText("Gitme");
        else if(YerAdapter.seciliSaatDilimleri.size() == 1)
            YerAdapter.secButon.setText("Saati Seç");
        else if(YerAdapter.seciliSaatDilimleri.size() == 2)
            YerAdapter.secButon.setText("Saatleri Seç");

        if(!gecmisSaatler.contains(saatler.get(i))) {
            viewHolder.kuculenMavi.setBackground(activity.getResources().getDrawable(R.drawable.gradient_yumusak_her_yer));
            viewHolder.saatArka.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (YerAdapter.seciliSaatDilimleri.size() == 0) {
                        YerAdapter.seciliSaatDilimleri.add(Integer.valueOf(saatid));
                        hafifKuculmeAnim(viewHolder.kuculenMavi, 1);
                        YerAdapter.secButon.setText("Saati Seç");
                    } else if (YerAdapter.seciliSaatDilimleri.size() == 1) {
                        if (YerAdapter.seciliSaatDilimleri.get(0) == Integer.valueOf(saatid) + 1 || YerAdapter.seciliSaatDilimleri.get(0) == Integer.valueOf(saatid) - 1) {
                            YerAdapter.seciliSaatDilimleri.add(Integer.valueOf(saatid));
                            YerAdapter.secButon.setText("Saatleri Seç");
                            hafifKuculmeAnim(viewHolder.kuculenMavi, 1);
                        } else if (YerAdapter.seciliSaatDilimleri.get(0) == Integer.valueOf(saatid)) {
                            cancelHafifBuyumeAnim(viewHolder.kuculenMavi, 1);
                            YerAdapter.seciliSaatDilimleri.remove(0);
                            YerAdapter.secButon.setText("Gitme");
                            //viewHolder.saatArka.setBackground(mContext.getResources().getDrawable(R.drawable.arka_mavi));
                        } else {
                            Toast.makeText(mContext, "Seçitiğiniz saat dilimine yakın bir seçenek seçmelisiniz.", Toast.LENGTH_SHORT).show();
                        }

                    } else if (YerAdapter.seciliSaatDilimleri.size() == 2) {
                        int iDegil = 0;
                        for (int a = 0; a < YerAdapter.seciliSaatDilimleri.size(); a++) {
                            if (YerAdapter.seciliSaatDilimleri.get(a) == Integer.valueOf(saatid)) {
                                YerAdapter.seciliSaatDilimleri.remove(a);
                                YerAdapter.secButon.setText("Saati Seç");
                                cancelHafifBuyumeAnim(viewHolder.kuculenMavi, 1);
                                // viewHolder.saatArka.setBackground(mContext.getResources().getDrawable(R.drawable.arka_mavi));
                            } else {
                                iDegil++;
                                if (iDegil == 2)
                                    Toast.makeText(mContext, "En fazla 2 farklı saat dilimi seçebilirsiniz.", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }


                }
            });
        }
        else {
            viewHolder.kuculenMavi.setBackground(activity.getResources().getDrawable(R.drawable.arka_siyah));
            //viewHolder.saatTV.setText("Bu saat geçti.");
        }

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
                    GidenArklariGoster(saat,gidenSayisi);
                }
                else{

                }
            }
        });
    }

    private void GidenArklariGoster(String saat, ArrayList<String> gidenArkadaslarim) {
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
        belirtilenSaat.setText(saat);
        mekanisim.setText(mekanAdi);

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

    private void hafifKuculmeAnim(final View view, final int i){
        ObjectAnimator scaleDownX = ObjectAnimator.ofFloat(view, "scaleX", 0.9f);
        ObjectAnimator scaleDownY = ObjectAnimator.ofFloat(view, "scaleY", 0.8f);
        scaleDownX.setDuration(50);
        scaleDownY.setDuration(50);
        scaleDownX.start();
        scaleDownY.start();
        scaleDownY.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                if(i == 1){

                }
            }
        });
    }
    private void cancelHafifBuyumeAnim(final View view, final int i){
        ObjectAnimator scaleDownX = ObjectAnimator.ofFloat(view, "scaleX", 1);
        ObjectAnimator scaleDownY = ObjectAnimator.ofFloat(view, "scaleY", 1);
        scaleDownX.setDuration(300);
        scaleDownY.setDuration(300);
        scaleDownX.start();
        scaleDownY.start();
        scaleDownY.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                if(i == 1){

                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mSaat.size();
    }
    public class ViewHolder extends RecyclerView.ViewHolder{

        public TextView saatTV, ekstra_gelenler, kisiSayisi;
        public ImageView giden1,giden2,giden3;
        public RelativeLayout resimler,saatArka;
        public View kuculenMavi;

        public ViewHolder(View itemView) {
            super(itemView);

            saatTV = itemView.findViewById(R.id.saat);
            giden1 = itemView.findViewById(R.id.gelenArk1);
            giden2 = itemView.findViewById(R.id.gelenArk2);
            giden3 = itemView.findViewById(R.id.gelenArk3);
            saatArka = itemView.findViewById(R.id.saat_arka);
            resimler = itemView.findViewById(R.id.resimler);
            //kisiSayisi = itemView.findViewById(R.id.kisi_sayisi);
            kuculenMavi = itemView.findViewById(R.id.kuculen_mavi);
            ekstra_gelenler = itemView.findViewById(R.id.ekstra_gelenler);

        }
    }
}
