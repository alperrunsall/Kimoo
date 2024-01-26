package com.kimoo.android.extra;

import static android.content.Context.MODE_PRIVATE;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
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

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import android.net.Uri;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import com.kimoo.android.MarketActivity;
import com.kimoo.android.MesajActivity;
import com.kimoo.android.MesajlarimActivity;
import com.kimoo.android.Model.Chat;
import com.kimoo.android.Model.Fiyatlar;
import com.kimoo.android.Model.User;
import com.kimoo.android.R;
import com.kimoo.android.bildirimler.Data;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class MesajlarimListView extends RecyclerView.Adapter<MesajlarimListView.ViewHolder>{

    private Context mContext;
    private Activity mActivity;
    private List<MesajlarimItemKullaniciVeOda> mItem;
    private FirebaseUser fuser;
    private boolean dialogOlustuMu;
    private static Fiyatlar fiyatlar;
    private User Kullanici;
    private static DatabaseReference ref1, ref2;
    private static ValueEventListener ref1Listener, ref2Listener;

    public MesajlarimListView(Context mContext, List<MesajlarimItemKullaniciVeOda> mItem, Activity mActivity) {
        this.mContext = mContext;
        this.mItem = mItem;
        this.mActivity = mActivity;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.mesajlarim_gorunumu_liste,viewGroup,false);
        return new MesajlarimListView.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder viewHolder, int i) {
        final MesajlarimItemKullaniciVeOda item = mItem.get(i);
        User user = item.getUser();
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
                            Glide.with(mContext).asBitmap().load(fotoUrl).into(viewHolder.resim);
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
                    //Toast.makeText(mActivity, "pp" + fotoUrl.substring(fotoUrl.length() - 9,fotoUrl.length() - 4) + user.getUid() +".jpg", Toast.LENGTH_LONG).show();
                    new ResimIndir(mContext, fotoUrl, "kullanici_resimleri", "pp" + fotoUrl.substring(fotoUrl.length() - 9, fotoUrl.length() - 4) + user.getUid() + ".jpg");

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
                    viewHolder.okundumu.setVisibility(View.GONE);
                    viewHolder.tarih.setVisibility(View.GONE);
                    viewHolder.son_mesaj.setTextSize(14);
                    viewHolder.son_mesaj.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
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
                            }
                            else {
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



        final ObjectAnimator[] scaleDownX = new ObjectAnimator[1];
        final ObjectAnimator[] scaleDownY = new ObjectAnimator[1];

        viewHolder.resim.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent profilegit = new Intent(mContext, DigerProfilActivity.class);
                profilegit.putExtra("userid", user.getUid());
                mContext.startActivity(profilegit);
            }
        });
        viewHolder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
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
        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatabaseReference reference = FirebaseDatabase.getInstance().getReference("usersF").child(fuser.getUid());
                reference.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        Kullanici = dataSnapshot.getValue(User.class);
                        if (!adminMi[0]) {
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
        List<String> mesajlar = new ArrayList<>();
        ref1 = FirebaseDatabase.getInstance().getReference("Mesajlar").child(item.getOdaNo());
        ref1Listener = ref1.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                final String[] sonMesaj = new String[1];
                final Chat[] chat = new Chat[1];
                final int[] deger = {0};
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
                                if(chat[0] == null) {
                                    chat[0] = dataSnapshot.child(dataSnapshot.child("son_mesaj").getValue(String.class)).getValue(Chat.class);
                                    if (!mesajlar.contains(dataSnapshot.child(dataSnapshot.child("son_mesaj").getValue(String.class)).getKey()))
                                        chat[0] = dataSnapshot.child(mesajlar.get(mesajlar.size() - 1)).getValue(Chat.class);
                                    //dateString = formatter.format(new Date(Long.parseLong(String.valueOf(chat[0].getZaman()))));
                                    TarihiYaz(viewHolder, chat[0].getZaman());

                                    if (!chat[0].getGormek_istemeyen2().equals(fuser.getUid()) && !chat[0].getGormek_istemeyen1().equals(fuser.getUid())) {
                                        sonMesaj[0] = chat[0].getMesajim();
                                        if (!chat[0].getDurum().equals("silindi")) {
                                            if (chat[0].getAlici().equals(item.getUser().getUid())) {
                                                if (chat[0].isGoruldumu()) {
                                                    viewHolder.son_mesaj.setTypeface(Typeface.defaultFromStyle(Typeface.ITALIC));
                                                    //viewHolder.okundumu.setTypeface(Typeface.defaultFromStyle(Typeface.NORMAL));
                                                    viewHolder.okundumu.setVisibility(View.VISIBLE);
                                                    viewHolder.okundumu.setText("Okudu");
                                                    //altKisim.setBackground(gradientDrawable);
                                                    viewHolder.son_mesaj_silindi.setVisibility(View.GONE);
                                                } else {
                                                    viewHolder.son_mesaj.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD_ITALIC));
                                                    //viewHolder.okundumu.setTypeface(Typeface.defaultFromStyle(Typeface.NORMAL));
                                                    //altKisim.setBackground(gradientDrawable);
                                                    viewHolder.okundumu.setVisibility(View.VISIBLE);
                                                    viewHolder.okundumu.setText("Okumadı");
                                                    viewHolder.son_mesaj_silindi.setVisibility(View.GONE);
                                                }
                                            } else {
                                                if (chat[0].isGoruldumu()) {
                                                    viewHolder.son_mesaj.setTypeface(Typeface.defaultFromStyle(Typeface.ITALIC));
                                                    //viewHolder.okundumu.setTypeface(Typeface.defaultFromStyle(Typeface.NORMAL));
                                                    //altKisim.setBackground(gradientDrawable);
                                                    viewHolder.okundumu.setVisibility(View.VISIBLE);
                                                    viewHolder.okundumu.setText("Okudun");
                                                    viewHolder.son_mesaj_silindi.setVisibility(View.GONE);

                                                } else {
                                                    viewHolder.son_mesaj.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD_ITALIC));
                                                    //altKisim.setBackgroundResource(R.drawable.yamuk_kare_kirmizi2);
                                                    viewHolder.okundumu.setVisibility(View.VISIBLE);
                                                    viewHolder.okundumu.setText("Okumadın");
                                                    viewHolder.itemView.setScaleX(1f);
                                                    viewHolder.itemView.setScaleY(1f);
                                                    if (scaleDownY[0] == null) {
                                                        scaleDownX[0] = ObjectAnimator.ofFloat(viewHolder.itemView, "scaleX", 0.95f);
                                                        scaleDownY[0] = ObjectAnimator.ofFloat(viewHolder.itemView, "scaleY", 0.95f);
                                                        scaleDownX[0].setDuration(1000);
                                                        scaleDownY[0].setDuration(1000);
                                                        scaleDownX[0].start();
                                                        scaleDownY[0].start();
                                                        scaleDownX[0].addListener(new AnimatorListenerAdapter() {
                                                            @Override
                                                            public void onAnimationEnd(Animator animation) {
                                                                super.onAnimationEnd(animation);
                                                                ObjectAnimator scaleDownX2 = ObjectAnimator.ofFloat(viewHolder.itemView, "scaleX", 1f);
                                                                ObjectAnimator scaleDownY2 = ObjectAnimator.ofFloat(viewHolder.itemView, "scaleY", 1f);
                                                                scaleDownX2.setDuration(1000);
                                                                scaleDownY2.setDuration(1000);
                                                                scaleDownX2.start();
                                                                scaleDownY2.start();
                                                                scaleDownX2.addListener(new AnimatorListenerAdapter() {
                                                                    @Override
                                                                    public void onAnimationEnd(Animator animation) {
                                                                        super.onAnimationEnd(animation);
                                                                        scaleDownX[0].start();
                                                                        scaleDownY[0].start();
                                                                    }
                                                                });
                                                            }
                                                        });
                                                    }
                                                    //if (viewHolder.okundumu.getText().equals("Okumadın"))
                                                    viewHolder.son_mesaj_silindi.setVisibility(View.GONE);
                                                }
                                            }
                                        }
                                        else {
                                            sonMesaj[0] = "";
                                            viewHolder.son_mesaj.setTypeface(Typeface.defaultFromStyle(Typeface.ITALIC));
                                            //viewHolder.okundumu.setTypeface(Typeface.defaultFromStyle(Typeface.NORMAL));
                                            viewHolder.okundumu.setText("Mesaj Yok");
                                            viewHolder.okundumu.setVisibility(View.INVISIBLE);
                                            //altKisim.setBackground(gradientDrawable);
                                            viewHolder.son_mesaj.setVisibility(View.GONE);
                                            viewHolder.son_mesaj_silindi.setVisibility(View.VISIBLE);
                                        }
                                    } else {
                                        sonMesaj[0] = "BU MESAJI SİLDİNİZ";
                                    }
                                    switch (sonMesaj[0]) {
                                        case "default":
                                            viewHolder.son_mesaj.setText("");
                                            break;

                                        default:
                                            if (sonMesaj[0].length() < 96) {
                                                viewHolder.son_mesaj.setText("''" + sonMesaj[0] + "''");
                                                viewHolder.son_mesaj.setTypeface(Typeface.defaultFromStyle(Typeface.ITALIC));
                                            } else {
                                                viewHolder.son_mesaj.setText("''" + sonMesaj[0].substring(0, 96) + "...''");
                                                viewHolder.son_mesaj.setTypeface(Typeface.defaultFromStyle(Typeface.ITALIC));
                                            }
                                            break;
                                    }
                                    sonMesaj[0] = "default";
                                }

                            }
                            else{ // Tüm mesajları görmek istememiş

                                viewHolder.son_mesaj.setTypeface(Typeface.defaultFromStyle(Typeface.ITALIC));
                                //viewHolder.okundumu.setTypeface(Typeface.defaultFromStyle(Typeface.NORMAL));
                                viewHolder.okundumu.setVisibility(View.VISIBLE);
                                viewHolder.okundumu.setText("Tüm mesajları silmişsiniz");
                                //altKisim.setBackground(gradientDrawable);
                                viewHolder.son_mesaj_silindi.setVisibility(View.GONE);
                                viewHolder.son_mesaj.setVisibility(View.GONE);
                            }
                            yaziyormuKontrol(item.getUser().getUid(), viewHolder.okundumu);

                        }
                    }
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void TarihiYaz(ViewHolder viewHolder, Long mesajinZamani) {

        if ( ( (System.currentTimeMillis() / 86400000) - (mesajinZamani / 86400000) ) == 0) {
            long kacSaatOnce = (System.currentTimeMillis() / 3600000) - (mesajinZamani / 3600000);
            long kacDakikaOnce = (System.currentTimeMillis() / 60000) - (mesajinZamani / 60000);
            long kacSaniyeOnce = (System.currentTimeMillis() / 1000) - (mesajinZamani / 1000);

            if (kacSaatOnce < 0)
                viewHolder.tarih.setText("Sistem saatiniz hatalı");
            else if(kacSaatOnce == 0) {
                if (kacDakikaOnce < 0) {
                    viewHolder.tarih.setText("Az Önce" );
                }
                else if (kacDakikaOnce == 0) {
                    if (kacSaniyeOnce >= 0)
                        viewHolder.tarih.setText("" + kacSaniyeOnce + " SANİYE ÖNCE");
                    else
                        viewHolder.tarih.setText("Az Önce" );
                }
                else
                    viewHolder.tarih.setText("" + kacDakikaOnce + " DAKİKA ÖNCE");
            }
            else
                viewHolder.tarih.setText("" + kacSaatOnce + " SAAT ÖNCE");
        }
        else if( ( (System.currentTimeMillis() / 86400000) - (mesajinZamani / 86400000) ) == 1)
            viewHolder.tarih.setText("DÜN");
        else {
            long kacGunOnce = (System.currentTimeMillis() / 86400000) - (mesajinZamani / 86400000);
            if (kacGunOnce < 1)
                viewHolder.tarih.setText("Sistem saatiniz hatalı");
            else if (kacGunOnce > 1 && kacGunOnce <= 29)
                viewHolder.tarih.setText("" + kacGunOnce + " GÜN ÖNCE");
            else if (kacGunOnce > 29 && kacGunOnce <= 59)
                viewHolder.tarih.setText("1 AY ÖNCE");
            else if (kacGunOnce > 59 && kacGunOnce <= 89)
                viewHolder.tarih.setText("2 AY ÖNCE");
            else if (kacGunOnce > 89 && kacGunOnce <= 119)
                viewHolder.tarih.setText("3 AY ÖNCE");
            else if (kacGunOnce > 119 && kacGunOnce <= 149)
                viewHolder.tarih.setText("4 AY ÖNCE");
            else if (kacGunOnce > 149 && kacGunOnce <= 179)
                viewHolder.tarih.setText("5 AY ÖNCE");
            else if (kacGunOnce > 179 && kacGunOnce <= 209)
                viewHolder.tarih.setText("6 AY ÖNCE");
            else if (kacGunOnce > 209 && kacGunOnce <= 239)
                viewHolder.tarih.setText("7 AY ÖNCE");
            else if (kacGunOnce > 239 && kacGunOnce <= 269)
                viewHolder.tarih.setText("8 AY ÖNCE");
            else if (kacGunOnce > 269 && kacGunOnce <= 299)
                viewHolder.tarih.setText("9 AY ÖNCE");
            else if (kacGunOnce > 299 && kacGunOnce <= 329)
                viewHolder.tarih.setText("10 AY ÖNCE");
            else if (kacGunOnce > 329 && kacGunOnce <= 359)
                viewHolder.tarih.setText("11 AY ÖNCE");
            else if (kacGunOnce > 359 && kacGunOnce <= 547)
                viewHolder.tarih.setText("1 YIL ÖNCE");
            else if (kacGunOnce > 547 && kacGunOnce <= 730)
                viewHolder.tarih.setText("1.5 YIL ÖNCE");
            else if (kacGunOnce > 730 && kacGunOnce <= 1000)
                viewHolder.tarih.setText("2 YIL ÖNCE");
            else if (kacGunOnce > 1000)
                viewHolder.tarih.setText("UZUN ZAMAN ÖNCE");
        }
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


                                                                        DatabaseReference ref2 = FirebaseDatabase.getInstance().getReference("usersF").child(user.getUid());
                                                                        ref2.addListenerForSingleValueEvent(new ValueEventListener() {
                                                                            @Override
                                                                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                                                if(user.getGizlilik_ad().equals("0")) {
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
                                                                                    }
                                                                                    else if (user.getGizlilik_ad().equals("1")) {
                                                                                        if (dataSnapshot.hasChild("begenenler")) {
                                                                                            if (dataSnapshot.child("begenenler").hasChild(fuser.getUid())) {
                                                                                                viewHolder.username.setText(user.getAd().substring(0, 1).toUpperCase() + user.getAd().substring(1));
                                                                                            } else {
                                                                                                viewHolder.username.setText(AdiSansurle(user));
                                                                                            }
                                                                                        } else {
                                                                                            viewHolder.username.setText(AdiSansurle(user));
                                                                                        }
                                                                                    }
                                                                                    else if (user.getGizlilik_ad().equals("2")) {
                                                                                        if (dataSnapshot.hasChild("mesajlastiklarim")) {
                                                                                            if (dataSnapshot.child("mesajlastiklarim").hasChild(fuser.getUid())) {
                                                                                                viewHolder.username.setText(user.getAd().substring(0, 1).toUpperCase() + user.getAd().substring(1));
                                                                                            } else {
                                                                                                viewHolder.username.setText(AdiSansurle(user));
                                                                                            }
                                                                                        } else {
                                                                                            viewHolder.username.setText(AdiSansurle(user));
                                                                                        }
                                                                                    }
                                                                                    else if (user.getGizlilik_ad().equals("3")) {
                                                                                        viewHolder.username.setText(AdiSansurle(user));
                                                                                    }
                                                                                    else if (user.getGizlilik_ad().equals("4")) {
                                                                                        viewHolder.username.setText(user.getAd().substring(0, 1).toUpperCase() + user.getAd().substring(1));
                                                                                    }
                                                                                }

                                                                            }
                                                                            @Override
                                                                            public void onCancelled(@NonNull DatabaseError databaseError) {
                                                                            }
                                                                        });

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
    }

    private void yaziyormuKontrol(final String userId, final TextView yaziyormu) {
        final String yaziyorEski = yaziyormu.getText().toString();
        ref2 = FirebaseDatabase.getInstance().getReference("usersF").child(userId);
        ref2Listener = ref2.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    User us = dataSnapshot.getValue(User.class);
                    if(us.getUid().equals(userId)){
                        if(us.getKime_yaziyor().equals(fuser.getUid())){
                            yaziyormu.setText("Yazıyor...");
                        }else{
                            if(!yaziyorEski.equals(""))
                                yaziyormu.setText(yaziyorEski);
                        }
                    }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    @Override
    public int getItemCount() {
        return mItem.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        private TextView username;
        private ImageView resim;
        private TextView son_mesaj,okundumu,tarih;
        private TextView son_mesaj_silindi;
        private ProgressBar pbar;
        private RelativeLayout alt_kisim, kilitRel, anaLay;
        private ViewHolder(View itemView) {
            super(itemView);
            username = itemView.findViewById(R.id.kullanici_adi);
            alt_kisim = itemView.findViewById(R.id.arkaplan);
            resim = itemView.findViewById(R.id.profile_image);
            son_mesaj = itemView.findViewById(R.id.mesaj);
            anaLay = itemView.findViewById(R.id.anaLay);
            pbar = itemView.findViewById(R.id.pbar);
            son_mesaj_silindi = itemView.findViewById(R.id.mesajsilindi);
            okundumu = itemView.findViewById(R.id.yazi_durumu);
            tarih = itemView.findViewById(R.id.tarih);
            kilitRel = itemView.findViewById(R.id.kilitRel);
        }
    }

    public void TasarimDegistir(String tasDegeri, ViewHolder viewHolder) {
        GradientDrawable gradientBackground =  new GradientDrawable();

        gradientBackground.setOrientation(GradientDrawable.Orientation.LEFT_RIGHT);

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

    private String AdiSansurle(User user){
        String kesilmisIsim = user.getAd().substring(1);
        StringBuilder yeniisim = new StringBuilder(kesilmisIsim);
        for(int i = 0; i < kesilmisIsim.length(); i++){
            yeniisim.setCharAt(i,'*');
        }
        String sonHal = user.getAd().substring(0,1).toUpperCase() + yeniisim.toString();
        return sonHal;
    }
    private String YasSansurle(User user){
        return "**";
    }
    private String CinSansurle(){
        return "*";
    }



}
