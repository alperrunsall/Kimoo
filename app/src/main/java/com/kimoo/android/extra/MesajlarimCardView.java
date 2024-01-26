package com.kimoo.android.extra;

import static android.content.Context.MODE_PRIVATE;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.GradientDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import android.net.Uri;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
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
import com.kimoo.android.Kayit2Activity;
import com.kimoo.android.MarketActivity;
import com.kimoo.android.MesajActivity;
import com.kimoo.android.MesajlarimActivity;
import com.kimoo.android.Model.Chat;
import com.kimoo.android.Model.Fiyatlar;
import com.kimoo.android.Model.User;
import com.kimoo.android.R;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class MesajlarimCardView extends RecyclerView.Adapter<MesajlarimCardView.ViewHolder>{

    private Context mContext;
    private Activity mActivity;
    private List<MesajlarimItemKullaniciVeOda> mItem;
    private static Fiyatlar fiyatlar;
    private FirebaseUser fuser;
    private User Kullanici;
    private boolean dialogOlustuMu;
    String sonMesaj;
    private static DatabaseReference ref1, ref2, ref3;
    private static ValueEventListener ref1Listener, ref2Listener, ref3Listener;

    public MesajlarimCardView(Context mContext, List<MesajlarimItemKullaniciVeOda> mItem, Activity mActivity) {
        this.mContext = mContext;
        this.mItem = mItem;
        this.mActivity = mActivity;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.mesajlarim_gorunumu_cardview,viewGroup,false);
        return new MesajlarimCardView.ViewHolder(view);
    }

    private boolean internetVarmi(){
        ConnectivityManager connectivityManager = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED)
            return true;
        else
            return false;
    }
    @Override
    public void onBindViewHolder(@NonNull final ViewHolder viewHolder, int i) {
        final MesajlarimItemKullaniciVeOda item = mItem.get(i);
        User user = item.getUser();

        final boolean[] oynasinMi = new boolean[1];

        TasarimDegistir(user.getTas_profil(),viewHolder);

        fuser = FirebaseAuth.getInstance().getCurrentUser();

        ContextWrapper cw = new ContextWrapper(mContext);

        File directory = cw.getDir("kullanici_resimleri", MODE_PRIVATE);
        File imagepp = null;
        boolean fotoYuklendiMi = false;

        for(File files : directory.listFiles()){
            if(files.getName().substring(7,files.getName().length()-4).equals(user.getUid())){
                imagepp = files;
                fotoYuklendiMi = true;
                viewHolder.resim.setImageURI(Uri.parse(imagepp.getAbsolutePath()));
                //Toast.makeText(mContext, "Foto var", Toast.LENGTH_SHORT).show();
            }
            //if(dosyaKontrol == directory.listFiles().length)
        }


        File finalImagepp = imagepp;
        boolean finalFotoYuklendiMi = fotoYuklendiMi;


        FirebaseDatabase.getInstance().getReference("usersF").child(user.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String fotoUrl = dataSnapshot.child("fotograflarim").child("pp").getValue(String.class);
                viewHolder.anaLay.setVisibility(View.VISIBLE);
                viewHolder.pbar.setVisibility(View.INVISIBLE);

                if (finalFotoYuklendiMi){
                    if(finalImagepp != null) {
                        if (!finalImagepp.getName().substring(2, 7).equals(fotoUrl.substring(fotoUrl.length() - 9, fotoUrl.length() - 4))) {
                            finalImagepp.delete();
                            Glide.with(mContext)
                                    .asBitmap()
                                    .load(fotoUrl)
                                    .into(viewHolder.resim);
                            new ResimIndir(mContext, fotoUrl, "kullanici_resimleri", "pp" + fotoUrl.substring(fotoUrl.length() - 9, fotoUrl.length() - 4) + user.getUid() + ".jpg");
                            //Toast.makeText(mContext, finalImagepp.getName().substring(0, 5) + "\n" + fotoUrl.toString().substring(fotoUrl.toString().length() - 9, fotoUrl.toString().length() - 4), Toast.LENGTH_LONG).show();
                        }
                    }
                }
                else{
                    Glide.with(mContext)
                            .asBitmap()
                            .load(fotoUrl)
                            .into(viewHolder.resim);
                    String kayitAdi = "pp" + fotoUrl.substring(fotoUrl.length() - 9,fotoUrl.length() - 4) + user.getUid() +".jpg";
                    new ResimIndir(mContext, fotoUrl, "kullanici_resimleri", kayitAdi);
                    //Toast.makeText(mContext, "iindirdim " + kayitAdi, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("usersF").child(fuser.getUid());
        final boolean[] adminMi = {false};

        DatabaseReference reference2 = FirebaseDatabase.getInstance().getReference("Sistem");
        reference2.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                fiyatlar = dataSnapshot.child("fiyatlar").getValue(Fiyatlar.class);
                if (dataSnapshot.child("yoneticiler").hasChild(user.getUid())){
                    adminMi[0] = true;
                    viewHolder.resim.setImageDrawable(mContext.getResources().getDrawable(R.drawable.kimoo_logo_beyaz));
                    viewHolder.username.setText("KİMOO YÖNETİCİSİ");
                    viewHolder.anaLay.setBackgroundColor(Color.BLACK);
                }
                reference.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        Kullanici = dataSnapshot.getValue(User.class);
                        if (!adminMi[0]) {
                            if (dataSnapshot.hasChild("kilidini_actiklarim")) {
                                if (dataSnapshot.child("kilidini_actiklarim").hasChild(user.getUid())) {
                                    if (dataSnapshot.child("kilidini_actiklarim").child(user.getUid()).child("durum").getValue(String.class).equals("tam")) {
                                        viewHolder.kilitRel.setVisibility(View.GONE);
                                        viewHolder.son_mesaj.setVisibility(View.VISIBLE);


                                        if (user.getGizlilik_ad().equals("0")) {
                                            if (dataSnapshot.hasChild("begendiklerim")) {
                                                if (dataSnapshot.child("begendiklerim").hasChild(fuser.getUid())) {
                                                    viewHolder.username.setText(user.getAd().substring(0, 1).toUpperCase() + user.getAd().substring(1));
                                                } else {
                                                    viewHolder.username.setText(AdiSansurle(user));
                                                }
                                            } else {
                                                viewHolder.username.setText(AdiSansurle(user));
                                            }
                                        } else if (user.getGizlilik_ad().equals("1")) {
                                            if (dataSnapshot.hasChild("begenenler")) {
                                                if (dataSnapshot.child("begenenler").hasChild(fuser.getUid())) {
                                                    viewHolder.username.setText(user.getAd().substring(0, 1).toUpperCase() + user.getAd().substring(1));
                                                } else {
                                                    viewHolder.username.setText(AdiSansurle(user));
                                                }
                                            } else {
                                                viewHolder.username.setText(AdiSansurle(user));
                                            }
                                        } else if (user.getGizlilik_ad().equals("2")) {
                                            if (dataSnapshot.hasChild("mesajlastiklarim")) {
                                                if (dataSnapshot.child("mesajlastiklarim").hasChild(fuser.getUid())) {
                                                    viewHolder.username.setText(user.getAd().substring(0, 1).toUpperCase() + user.getAd().substring(1));
                                                } else {
                                                    viewHolder.username.setText(AdiSansurle(user));
                                                }
                                            } else {
                                                viewHolder.username.setText(AdiSansurle(user));
                                            }
                                        } else if (user.getGizlilik_ad().equals("3")) {
                                            viewHolder.username.setText(AdiSansurle(user));
                                        } else if (user.getGizlilik_ad().equals("4")) {
                                            viewHolder.username.setText(user.getAd().substring(0, 1).toUpperCase() + user.getAd().substring(1));
                                        }

                                    } else {
                                        viewHolder.kilitRel.setVisibility(View.VISIBLE);
                                        viewHolder.son_mesaj.setVisibility(View.GONE);
                                    }


                                } else {
                                    viewHolder.kilitRel.setVisibility(View.VISIBLE);
                                    viewHolder.son_mesaj.setVisibility(View.GONE);
                                }
                            } else {
                                viewHolder.kilitRel.setVisibility(View.VISIBLE);
                                viewHolder.son_mesaj.setVisibility(View.GONE);
                            }
                        }
                        else{
                            viewHolder.kilitRel.setVisibility(View.GONE);
                            viewHolder.son_mesaj.setVisibility(View.VISIBLE);
                        }
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

        sonMesajKontrol(user.getUid(), viewHolder.son_mesaj,viewHolder,item.getOdaNo());

        ref1 = FirebaseDatabase.getInstance().getReference("usersF").child(user.getUid()).child("kime_yaziyor");
        ref1Listener = ref1.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.getValue(String.class).equals(fuser.getUid())){
                    oynasinMi[0] = true;
                    RotateAnimation rotate = new RotateAnimation(0, 2, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
                    rotate.setDuration(150);
                    rotate.setInterpolator(new LinearInterpolator());
                    rotate.setAnimationListener(new Animation.AnimationListener() {
                        @Override
                        public void onAnimationStart(Animation animation) {

                        }

                        @Override
                        public void onAnimationEnd(Animation animation) {
                            if(oynasinMi[0]) {
                                RotateAnimation rotate2 = new RotateAnimation(2, -2, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
                                rotate2.setDuration(300);
                                rotate2.setInterpolator(new LinearInterpolator());
                                rotate2.setAnimationListener(new Animation.AnimationListener() {
                                    @Override
                                    public void onAnimationStart(Animation animation) {

                                    }

                                    @Override
                                    public void onAnimationEnd(Animation animation) {
                                        if(oynasinMi[0]) {
                                            RotateAnimation rotate3 = new RotateAnimation(-2, -0, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
                                            rotate3.setDuration(150);
                                            rotate3.setInterpolator(new LinearInterpolator());
                                            rotate3.setAnimationListener(new Animation.AnimationListener() {
                                                @Override
                                                public void onAnimationStart(Animation animation) {

                                                }

                                                @Override
                                                public void onAnimationEnd(Animation animation) {
                                                    if(oynasinMi[0]) {
                                                        viewHolder.itemView.startAnimation(rotate);
                                                    }
                                                    else {
                                                        rotate.cancel();
                                                        rotate2.cancel();
                                                        rotate3.cancel();
                                                        viewHolder.itemView.clearAnimation();
                                                    }

                                                }

                                                @Override
                                                public void onAnimationRepeat(Animation animation) {

                                                }
                                            });
                                            viewHolder.itemView.startAnimation(rotate3);
                                        }
                                        else {
                                            rotate.cancel();
                                            rotate2.cancel();
                                            viewHolder.itemView.clearAnimation();
                                        }

                                    }

                                    @Override
                                    public void onAnimationRepeat(Animation animation) {

                                    }
                                });
                                viewHolder.itemView.startAnimation(rotate2);
                            }
                            else {
                                rotate.cancel();
                                viewHolder.itemView.clearAnimation();
                            }
                        }

                        @Override
                        public void onAnimationRepeat(Animation animation) {

                        }
                    });
                    viewHolder.itemView.startAnimation(rotate);

                }
                else {
                    oynasinMi[0] = false;
                    viewHolder.itemView.clearAnimation();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        viewHolder.resim.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent profilegit = new Intent(mContext, DigerProfilActivity.class);
                profilegit.putExtra("userid", user.getUid());
                mContext.startActivity(profilegit);
            }
        });
        viewHolder.alt_kisim.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Dialog dialog2 = new Dialog(mActivity);
                dialog2.setContentView(R.layout.dialog_dizayn5);
                dialog2.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

                LinearLayout lay1 = dialog2.findViewById(R.id.lay1);
                ProgressBar pbar = dialog2.findViewById(R.id.pbar);
                pbar.getIndeterminateDrawable().setColorFilter(MesajlarimActivity.ortaRenk,android.graphics.PorterDuff.Mode.MULTIPLY);
                TextView baslik = dialog2.findViewById(R.id.baslik);
                TextView aciklama = dialog2.findViewById(R.id.aciklama);
                aciklama.setMovementMethod(new ScrollingMovementMethod());
                Button buton = dialog2.findViewById(R.id.buton);
                Button buton2 = dialog2.findViewById(R.id.buton2);
                baslik.setText("Tüm mesajlar silinsin Mi?");
                aciklama.setText("Mesajlar sadece sizden silinir.");
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
                        final int[] a = {0};
                        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Mesajlar").child(item.getOdaNo());
                        ref.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                for(DataSnapshot ss : dataSnapshot.getChildren()){
                                    a[0]++;
                                    if (ss.getChildrenCount() > 0) {
                                        Chat chat = ss.getValue(Chat.class);
                                        if (chat.getAlici().equals(fuser.getUid()) && chat.getGonderici().equals(item.getUser().getUid()) ||
                                                chat.getAlici().equals(item.getUser().getUid()) && chat.getGonderici().equals(fuser.getUid())) {
                                            if (!chat.getGormek_istemeyen1().equals(fuser.getUid()) && !chat.getGormek_istemeyen2().equals(fuser.getUid())) {
                                                if (chat.getGormek_istemeyen1().equals("")) {
                                                    ss.getRef().child("gormek_istemeyen1").setValue(fuser.getUid());
                                                } else if (chat.getGormek_istemeyen2().equals("")) {
                                                    ss.getRef().child("gormek_istemeyen2").setValue(fuser.getUid());
                                                }
                                            }
                                            if (a[0] == dataSnapshot.getChildrenCount()-1)
                                                dialog2.dismiss();
                                        }
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
                    }
                });
                dialog2.show();
                return false;
            }
        });
        viewHolder.alt_kisim.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                reference.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        Kullanici = dataSnapshot.getValue(User.class);
                        if (adminMi[0]) {
                            if (dataSnapshot.hasChild("kilidini_actiklarim")) {
                                if (dataSnapshot.child("kilidini_actiklarim").hasChild(user.getUid())) {
                                    if (dataSnapshot.child("kilidini_actiklarim").child(user.getUid()).child("durum").getValue(String.class).equals("tam")) {
                                        Intent alper = new Intent(mContext, MesajActivity.class);
                                        alper.putExtra("userid", user.getUid());
                                        alper.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                        mContext.startActivity(alper);
                                        mActivity.finish();
                                        mActivity.overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
                                    }
                                    else {
                                        DialogOlustur(user, viewHolder);
                                    }
                                }
                                else {
                                    DialogOlustur(user, viewHolder);
                                }
                            }
                            else {
                                DialogOlustur(user, viewHolder);
                            }
                        }
                        else{
                            Intent alper = new Intent(mContext, MesajActivity.class);
                            alper.putExtra("userid", user.getUid());
                            alper.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            mContext.startActivity(alper);
                            mActivity.finish();
                            mActivity.overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

            }
        });

    }

    private void DialogOlustur(User user, ViewHolder viewHolder) {
        if (!dialogOlustuMu) {
            dialogOlustuMu = true;
            Dialog dialog = new Dialog(mActivity);
            dialog.setContentView(R.layout.dialog_dizayn2);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

            LinearLayout lay1 = dialog.findViewById(R.id.lay1);
            ProgressBar pbar = dialog.findViewById(R.id.pbar);
            TextView baslik = dialog.findViewById(R.id.baslik);
            TextView aciklama = dialog.findViewById(R.id.aciklama);
            Button buton = dialog.findViewById(R.id.buton);

            baslik.setText("MESAJLAŞ");

            if (fiyatlar != null) {

                final int[] deger = {0};
                final String[] aciklamaYazi = {""};
                DatabaseReference uref = FirebaseDatabase.getInstance().getReference("usersF").child(user.getUid());
                uref.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.hasChild("kilidini_actiklarim")) {
                            if (dataSnapshot.child("kilidini_actiklarim").hasChild(fuser.getUid())) {
                                deger[0] = fiyatlar.getKilit_acma() / 2;
                                aciklamaYazi[0] = "Bu kullanıcıya cevap verebilmek için kilidi açmalısınız. Kilidi açmak için gerekli miktar " + deger[0] + "KP, sizde olan " + Kullanici.getKp() + "KP";
                            } else {
                                deger[0] = fiyatlar.getKilit_acma();
                                aciklamaYazi[0] = "Bu kullanıcıya mesaj atabilmek için kilidi açmalısınız. Kilidi açmak için gerekli miktar " + deger[0] + "KP, sizde olan " + Kullanici.getKp() + "KP";
                            }
                        } else {
                            deger[0] = fiyatlar.getKilit_acma();
                            aciklamaYazi[0] = "Bu kullanıcıya mesaj atabilmek için kilidi açmalısınız. Kilidi açmak için gerekli miktar " + deger[0] + "KP, sizde olan " + Kullanici.getKp() + "KP";
                        }
                        aciklama.setText(aciklamaYazi[0]);
                        if (deger[0] != 0) {
                            if (Kullanici.getKp() >= deger[0]) {
                                buton.setText("Kilidi Aç");
                                buton.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        lay1.setVisibility(View.GONE);
                                        pbar.setVisibility(View.VISIBLE);
                                        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("usersF").child(Kullanici.getUid());
                                        ref.child("kp").setValue(Kullanici.getKp() - deger[0]).addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(@NonNull Void unused) {
                                                DatabaseReference reference = FirebaseDatabase.getInstance().getReference("usersF").child(user.getUid());
                                                reference.addListenerForSingleValueEvent(new ValueEventListener() {
                                                    @Override
                                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                        ref.child("kilidini_actiklarim").child(user.getUid()).child("mesaj").setValue("var");
                                                        ref.child("kilidini_actiklarim").child(user.getUid()).child("durum").setValue("tam");
                                                        ref.child("kilidini_actiklarim").child(user.getUid()).child("oda").setValue(dataSnapshot.child("kilidini_actiklarim").child(Kullanici.getUid()).child("oda").getValue(String.class))
                                                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                    @Override
                                                                    public void onSuccess(@NonNull Void unused) {
                                                                        ref.child("harcamalarim").child("kilit_" + user.getUid()).setValue(ServerValue.TIMESTAMP);
                                                                        Toast.makeText(mActivity, "Kullanıcının kilidini açtınız!", Toast.LENGTH_SHORT).show();
                                                                        viewHolder.kilitRel.setVisibility(View.GONE);
                                                                        viewHolder.son_mesaj.setVisibility(View.VISIBLE);

                                                                        if (user.getGizlilik_ad().equals("0")) {
                                                                            if (dataSnapshot.hasChild("begenenler")) {
                                                                                if (dataSnapshot.child("begenenler").hasChild(user.getUid())) {
                                                                                    viewHolder.username.setText(user.getAd().substring(0, 1).toUpperCase() + user.getAd().substring(1));
                                                                                } else {
                                                                                    viewHolder.username.setText(AdiSansurle(user));
                                                                                }
                                                                            } else {
                                                                                viewHolder.username.setText(AdiSansurle(user));
                                                                            }
                                                                        } else if (user.getGizlilik_ad().equals("1")) {
                                                                            ref2 = FirebaseDatabase.getInstance().getReference("usersF").child(user.getUid());
                                                                            ref2Listener = ref2.addValueEventListener(new ValueEventListener() {
                                                                                @Override
                                                                                public void onDataChange(@NonNull DataSnapshot dataSnapshot2) {
                                                                                    if (dataSnapshot2.hasChild("begenenler")) {
                                                                                        if (dataSnapshot2.child("begenenler").hasChild(fuser.getUid())) {
                                                                                            viewHolder.username.setText(user.getAd().substring(0, 1).toUpperCase() + user.getAd().substring(1));
                                                                                        } else {
                                                                                            viewHolder.username.setText(AdiSansurle(user));
                                                                                        }
                                                                                    } else {
                                                                                        viewHolder.username.setText(AdiSansurle(user));
                                                                                    }
                                                                                }

                                                                                @Override
                                                                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                                                                }
                                                                            });
                                                                        } else if (user.getGizlilik_ad().equals("2")) {
                                                                            viewHolder.username.setText(user.getAd().substring(0, 1).toUpperCase() + user.getAd().substring(1));
                                                                        }
                                                                        dialogOlustuMu = false;
                                                                        dialog.dismiss();
                                                                    }
                                                                });
                                                    }

                                                    @Override
                                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                                    }
                                                });
                                            }
                                        }).addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                lay1.setVisibility(View.VISIBLE);
                                                pbar.setVisibility(View.GONE);
                                                Toast.makeText(mActivity, "Başarısız!", Toast.LENGTH_SHORT).show();
                                            }
                                        });
                                    }
                                });
                            } else {
                                buton.setText("Markete Git");
                                buton.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        lay1.setVisibility(View.GONE);
                                        pbar.setVisibility(View.VISIBLE);
                                        dialogOlustuMu = false;
                                        mActivity.startActivity(new Intent(mActivity, MarketActivity.class));
                                    }
                                });
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

            }
            dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                @Override
                public void onCancel(DialogInterface dialog) {
                    dialogOlustuMu = false;
                }
            });
            dialog.show();
        }
    }

    public static void TumListenerlariDurdur(){
        if (ref1 != null && ref1 != null) {
            ref1.removeEventListener(ref1Listener);
        }
        if (ref2 != null && ref2 != null) {
            ref2.removeEventListener(ref2Listener);
        }
        if (ref3 != null && ref3 != null) {
            ref3.removeEventListener(ref3Listener);
        }
    }

    @Override
    public int getItemCount() {
        return mItem.size();
    }
    private void sonMesajKontrol(final String userid, final TextView sonmsj , final ViewHolder viewHolder,String odaNo){
        sonMesaj = "default";
        final int[] deger = {0};
        List<String> mesajlar = new ArrayList<>();
        ref3 = FirebaseDatabase.getInstance().getReference("Mesajlar").child(odaNo);
        ref3Listener = ref3.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot ds : dataSnapshot.getChildren()){
                    if(ds.getChildrenCount() > 0) {
                        Chat chat2 = ds.getValue(Chat.class);
                        if (!mesajlar.contains(ds.getKey())) {
                            deger[0]++;
                            if(!chat2.getGormek_istemeyen1().equals(fuser.getUid()) && !chat2.getGormek_istemeyen2().equals(fuser.getUid())) {
                                mesajlar.add(ds.getKey());
                            }
                        }

                        if (deger[0] == dataSnapshot.getChildrenCount()-1){
                            if(mesajlar.size() > 0){
                                Chat chat = dataSnapshot.child(dataSnapshot.child("son_mesaj").getValue(String.class)).getValue(Chat.class);
                                if (!mesajlar.contains(dataSnapshot.child(dataSnapshot.child("son_mesaj").getValue(String.class)).getKey()))
                                    chat = dataSnapshot.child(mesajlar.get(mesajlar.size()-1)).getValue(Chat.class);

                                if (!chat.getGormek_istemeyen2().equals(fuser.getUid()) && !chat.getGormek_istemeyen1().equals(fuser.getUid())) {
                                    sonMesaj = chat.getMesajim();
                                    if(!chat.getDurum().equals("silindi")) {
                                        if (chat.getAlici().equals(userid)) {
                                            if (chat.isGoruldumu()) {
                                                viewHolder.yuvarlak.setVisibility(View.GONE);
                                                viewHolder.okundu.setVisibility(View.VISIBLE);
                                                viewHolder.son_mesaj.setTextColor(mContext.getResources().getColor(R.color.beyaz));
                                                //altKisim.setBackground(gradientDrawable);
                                                viewHolder.son_mesaj_silindi.setVisibility(View.GONE);
                                            } else {
                                                viewHolder.yuvarlak.setVisibility(View.GONE);
                                                viewHolder.okundu.setVisibility(View.GONE);
                                                viewHolder.son_mesaj.setTextColor(mContext.getResources().getColor(R.color.beyaz));
                                                //altKisim.setBackground(gradientDrawable);
                                                viewHolder.son_mesaj_silindi.setVisibility(View.GONE);
                                            }
                                        } else {
                                            if (chat.isGoruldumu()) {
                                                viewHolder.yuvarlak.setVisibility(View.GONE);
                                                viewHolder.okundu.setVisibility(View.GONE);
                                                viewHolder.son_mesaj.setTextColor(mContext.getResources().getColor(R.color.beyaz));
                                                viewHolder.son_mesaj_silindi.setVisibility(View.GONE);
                                                //altKisim.setBackground(gradientDrawable);
                                            } else {
                                                viewHolder.yuvarlak.setVisibility(View.VISIBLE);
                                                viewHolder.okundu.setVisibility(View.GONE);
                                                viewHolder.son_mesaj.setTextColor(mContext.getResources().getColor(R.color.beyaz));
                                                viewHolder.son_mesaj_silindi.setVisibility(View.GONE);
                                                //altKisim.setBackgroundResource(R.drawable.yamuk_kare_krimizi);
                                            }
                                        }
                                    }else{
                                        viewHolder.son_mesaj.setTypeface(Typeface.defaultFromStyle(Typeface.ITALIC));
                                        viewHolder.okundu.setVisibility(View.INVISIBLE);
                                        //altKisim.setBackground(gradientDrawable);
                                        viewHolder.son_mesaj.setVisibility(View.GONE);
                                        viewHolder.son_mesaj_silindi.setVisibility(View.VISIBLE);
                                    }
                                }
                                else{
                                    sonMesaj = "BU MESAJI SİLDİNİZ";
                                }

                                switch (sonMesaj){
                                    case "default":
                                        sonmsj.setText("");
                                        break;

                                    default:
                                        if(sonMesaj.length() < 40){
                                            sonmsj.setText(sonMesaj);
                                        }else{
                                            sonmsj.setText(sonMesaj.substring(0,14) + "...");
                                        }
                                        break;
                                }
                                sonMesaj = "default";
                            }
                            else{ // Tüm mesajları görmek istememiş
                                viewHolder.son_mesaj.setTypeface(Typeface.defaultFromStyle(Typeface.ITALIC));
                                //altKisim.setBackground(gradientDrawable);
                                viewHolder.son_mesaj_silindi.setVisibility(View.GONE);
                                viewHolder.son_mesaj.setVisibility(View.GONE);
                                viewHolder.son_mesaj.setText("Tüm mesajları silmişsiniz");
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

    public void TasarimDegistir(String tasDegeri, ViewHolder viewHolder) {
        GradientDrawable gradientBackground =  new GradientDrawable();
        GradientDrawable gradientBackground2 =  new GradientDrawable();

        gradientBackground.setOrientation(GradientDrawable.Orientation.LEFT_RIGHT);
        //gradientBackground.setCornerRadii(new float[]{50,50,50,50,0,0,0,0});
        gradientBackground.setCornerRadius(50);
        gradientBackground2.setOrientation(GradientDrawable.Orientation.LEFT_RIGHT);
        //gradientBackground.setCornerRadii(new float[]{50,50,50,50,0,0,0,0});
        gradientBackground2.setCornerRadius(50);

        int renk1 = 0,renk2 = 0,t1start = 0,t2start = 0,t1end = 0,t2end = 0,orta = 0;

        renk1 = TasarimRenginiGetir.RengiGetir(mContext,"renk1",tasDegeri);
        renk2 = TasarimRenginiGetir.RengiGetir(mContext,"renk2",tasDegeri);
        t1start = TasarimRenginiGetir.RengiGetir(mContext,"t1start",tasDegeri);
        t2start = TasarimRenginiGetir.RengiGetir(mContext,"t2start",tasDegeri);
        t1end = TasarimRenginiGetir.RengiGetir(mContext,"t1end",tasDegeri);
        t2end = TasarimRenginiGetir.RengiGetir(mContext,"t2end",tasDegeri);
        orta = TasarimRenginiGetir.RengiGetir(mContext,"orta",tasDegeri);
        gradientBackground.setColors(new int[]{
                renk1,
                orta,
                renk2
        });

        viewHolder.alt_kisim.setBackground(gradientBackground);
    }
    public class ViewHolder extends RecyclerView.ViewHolder{

        public TextView username;
        public ImageView resim,yuvarlak,okundu;
        public TextView son_mesaj;
        public TextView son_mesaj_silindi;
        private ProgressBar pbar;
        private RelativeLayout alt_kisim, kilitRel, anaLay;
        public ViewHolder(View itemView) {
            super(itemView);
            alt_kisim = itemView.findViewById(R.id.rel_alt_kisim);
            username = itemView.findViewById(R.id.kullanici_adi);
            resim = itemView.findViewById(R.id.resim);
            son_mesaj = itemView.findViewById(R.id.son_mesaj);
            yuvarlak = itemView.findViewById(R.id.yuvarlak);
            anaLay = itemView.findViewById(R.id.anaLay);
            pbar = itemView.findViewById(R.id.pbar);
            son_mesaj_silindi = itemView.findViewById(R.id.son_mesaj_silindi);
            okundu = itemView.findViewById(R.id.okundu);
            kilitRel = itemView.findViewById(R.id.kilitRel);
        }
    }

    private String AdiSansurle(User user){
        String kesilmisIsim = user.getAd().substring(1);
        StringBuilder yeniisim = new StringBuilder(kesilmisIsim);
        for(int i = 0; i < kesilmisIsim.length(); i++){
            yeniisim.setCharAt(i,'*');
        }
        String sonHal = user.getAd().substring(0,1).toUpperCase() + yeniisim;
        return sonHal;
    }
    private String YasSansurle(User user){
        return "**";
    }
    private String CinSansurle(){
        return "*";
    }



}
