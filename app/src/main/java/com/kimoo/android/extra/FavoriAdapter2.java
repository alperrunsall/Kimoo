package com.kimoo.android.extra;

import static android.content.Context.MODE_PRIVATE;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.GradientDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
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
import com.kimoo.android.Model.Fiyatlar;
import com.kimoo.android.Model.User;
import com.kimoo.android.R;
import com.kimoo.android.TaraActivity;

import java.io.File;
import java.util.List;

public class FavoriAdapter2 extends RecyclerView.Adapter<FavoriAdapter2.ViewHolder> {
    private Context mContext;
    private Activity mActivity;
    private FirebaseUser fuser;
    DatabaseReference ref;
    private static Fiyatlar fiyatlar;
    private List<User> mUser;

    public FavoriAdapter2(Context mContext,Activity mActivity, List<User> mUser) {
        this.mContext = mContext;
        this.mUser = mUser;
        this.mActivity = mActivity;
    }
    @NonNull
    @Override
    public FavoriAdapter2.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.favori_item_2,viewGroup,false);
        return new FavoriAdapter2.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final FavoriAdapter2.ViewHolder viewHolder, int i) {
        fuser = FirebaseAuth.getInstance().getCurrentUser();
        final User user = mUser.get(i);

        fuser = FirebaseAuth.getInstance().getCurrentUser();
        ref = FirebaseDatabase.getInstance().getReference("usersF").child(fuser.getUid()).child("bulduklarim").child(user.getUid());

        ContextWrapper cw = new ContextWrapper(mContext);

        File directory = cw.getDir("kullanici_resimleri", MODE_PRIVATE);
        File imagepp = null;
        boolean fotoYuklendiMi = false;

        for(File files : directory.listFiles()){
            if(files.getName().substring(7,files.getName().length()-4).equals(user.getUid())){
                imagepp = files;
                fotoYuklendiMi = true;
                viewHolder.profile_image.setImageURI(Uri.parse(imagepp.getAbsolutePath()));
                //Toast.makeText(mContext, "Foto var", Toast.LENGTH_SHORT).show();
            }
            //if(dosyaKontrol == directory.listFiles().length)
        }


        File finalImagepp = imagepp;
        boolean finalFotoYuklendiMi = fotoYuklendiMi;

        final boolean[] adminMi = {false};
        DatabaseReference userref = FirebaseDatabase.getInstance().getReference("usersF").child(user.getUid());
        userref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {String fotoUrl = dataSnapshot.child("fotograflarim").child("pp").getValue(String.class);

                if (finalFotoYuklendiMi){
                    if(finalImagepp != null) {
                        if (!finalImagepp.getName().substring(2, 7).equals(fotoUrl.substring(fotoUrl.length() - 9, fotoUrl.length() - 4))) {
                            finalImagepp.delete();
                            Glide.with(mContext)
                                    .asBitmap()
                                    .load(fotoUrl)
                                    .into(viewHolder.profile_image);
                            new ResimIndir(mContext, fotoUrl, "kullanici_resimleri", "pp" + fotoUrl.substring(fotoUrl.length() - 9, fotoUrl.length() - 4) + user + ".jpg");
                            //Toast.makeText(mContext, finalImagepp.getName().substring(0, 5) + "\n" + fotoUrl.toString().substring(fotoUrl.toString().length() - 9, fotoUrl.toString().length() - 4), Toast.LENGTH_LONG).show();
                        }
                    }
                }
                else{
                    Glide.with(mContext)
                            .asBitmap()
                            .load(fotoUrl)
                            .into(viewHolder.profile_image);
                    String kayitAdi = "pp" + fotoUrl.substring(fotoUrl.length() - 9,fotoUrl.length() - 4) + user +".jpg";
                    new ResimIndir(mContext, fotoUrl, "kullanici_resimleri", kayitAdi);
                    //Toast.makeText(mContext, "iindirdim " + kayitAdi, Toast.LENGTH_SHORT).show();
                }
                if (!adminMi[0]) {
                    if (dataSnapshot.hasChild("rozetlerim")) {
                        if (dataSnapshot.child("rozet").getValue(String.class).equals("1")) {
                            viewHolder.rozet.setVisibility(View.VISIBLE);
                            viewHolder.rozet.setImageDrawable(mContext.getResources().getDrawable(R.drawable.rozet1));
                            viewHolder.rozet.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Toast.makeText(mContext, "Destekçi 1", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                        if (dataSnapshot.child("rozet").getValue(String.class).equals("2")) {
                            viewHolder.rozet.setVisibility(View.VISIBLE);
                            viewHolder.rozet.setImageDrawable(mContext.getResources().getDrawable(R.drawable.rozet2));
                            viewHolder.rozet.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Toast.makeText(mContext, "Destekçi 2", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                        if (dataSnapshot.child("rozet").getValue(String.class).equals("3")) {
                            viewHolder.rozet.setVisibility(View.VISIBLE);
                            viewHolder.rozet.setImageDrawable(mContext.getResources().getDrawable(R.drawable.rozet3));
                            viewHolder.rozet.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Toast.makeText(mContext, "Destekçi 3", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                        if (dataSnapshot.child("rozet").getValue(String.class).equals("4")) {
                            viewHolder.rozet.setVisibility(View.VISIBLE);
                            viewHolder.rozet.setImageDrawable(mContext.getResources().getDrawable(R.drawable.rozet4));
                            viewHolder.rozet.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Toast.makeText(mContext, "Destekçi 4", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                        if (dataSnapshot.child("rozet").getValue(String.class).equals("5")) {
                            viewHolder.rozet.setVisibility(View.VISIBLE);
                            viewHolder.rozet.setImageDrawable(mContext.getResources().getDrawable(R.drawable.rozet5));
                            viewHolder.rozet.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Toast.makeText(mContext, "Destekçi 5", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                        if (dataSnapshot.child("rozet").getValue(String.class).equals("6")) {
                            viewHolder.rozet.setVisibility(View.VISIBLE);
                            viewHolder.rozet.setImageDrawable(mContext.getResources().getDrawable(R.drawable.rozet6));
                            viewHolder.rozet.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Toast.makeText(mContext, "Destekçi 6", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                        else
                            viewHolder.rozet.setVisibility(View.GONE);
                    }
                    else
                        viewHolder.rozet.setVisibility(View.GONE);
                }
                else{
                    viewHolder.rozet.setVisibility(View.VISIBLE);
                    viewHolder.rozet.setImageDrawable(mContext.getResources().getDrawable(R.drawable.kimoo_logo_beyaz));
                    viewHolder.rozet.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Toast.makeText(mContext, "Yönetici", Toast.LENGTH_SHORT).show();
                        }
                    });
                }

                if (user.getGizlilik_ad().equals("0")) {
                    if (dataSnapshot.hasChild("begendiklerim")) {
                        if (dataSnapshot.child("begendiklerim").hasChild(fuser.getUid())) {
                            viewHolder.isim.setText(user.getAd().substring(0, 1).toUpperCase() + user.getAd().substring(1));
                        } else {
                            viewHolder.isim.setText(AdiSansurle(user));
                        }
                    } else {
                        viewHolder.isim.setText(AdiSansurle(user));
                    }
                }
                else if (user.getGizlilik_ad().equals("1")) {
                    if (dataSnapshot.hasChild("begenenler")) {
                        if (dataSnapshot.child("begenenler").hasChild(fuser.getUid())) {
                            viewHolder.isim.setText(user.getAd().substring(0, 1).toUpperCase() + user.getAd().substring(1));
                        } else {
                            viewHolder.isim.setText(AdiSansurle(user));
                        }
                    } else {
                        viewHolder.isim.setText(AdiSansurle(user));
                    }
                }
                else if (user.getGizlilik_ad().equals("2")) {
                    if (dataSnapshot.hasChild("mesajlastiklarim")) {
                        if (dataSnapshot.child("mesajlastiklarim").hasChild(fuser.getUid())) {
                            viewHolder.isim.setText(user.getAd().substring(0, 1).toUpperCase() + user.getAd().substring(1));
                        } else {
                            viewHolder.isim.setText(AdiSansurle(user));
                        }
                    } else {
                        viewHolder.isim.setText(AdiSansurle(user));
                    }
                }
                else if (user.getGizlilik_ad().equals("3")) {
                    viewHolder.isim.setText(AdiSansurle(user));
                }
                else if (user.getGizlilik_ad().equals("4")) {
                    viewHolder.isim.setText(user.getAd().substring(0, 1).toUpperCase() + user.getAd().substring(1));
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Sistem");
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                fiyatlar = dataSnapshot.child("fiyatlar").getValue(Fiyatlar.class);
                if (dataSnapshot.child("yoneticiler").hasChild(user.getUid())){
                    adminMi[0] = true;
                    viewHolder.rozet.setVisibility(View.VISIBLE);
                    viewHolder.rozet.setImageDrawable(mContext.getResources().getDrawable(R.drawable.kimoo_logo_beyaz));
                    viewHolder.rozet.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Toast.makeText(mContext, "Yönetici", Toast.LENGTH_SHORT).show();
                        }
                    });
                }


                TasarimDegistir(user.getTas_profil(),viewHolder);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        viewHolder.pbar.setVisibility(View.GONE);
        viewHolder.profile_image.setVisibility(View.VISIBLE);

        if(TaraActivity.tarada) {
            if (TaraActivity.favorilerde)
                viewHolder.favori.setVisibility(View.VISIBLE);
            else {
                viewHolder.favori.setVisibility(View.INVISIBLE);

                SharedPreferences sharedPreferences2 = mContext.getSharedPreferences("bulunan",Context.MODE_PRIVATE);
                SharedPreferences.Editor editor2 = sharedPreferences2.edit();
                int neredeKalmisti = sharedPreferences2.getInt("KalinanYer",0);
                FirebaseDatabase.getInstance().getReference("usersF").child(fuser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if(!dataSnapshot.child("bulduklarim").child(user.getUid()).exists()){
                            for (int a = 0; a < neredeKalmisti; a++) {
                                String isim = sharedPreferences2.getString("bulunan_" + i, null);
                                if (isim != null) {
                                    if (isim.substring(4,isim.length() - 5).equals(user.getUsernamef())) {
                                        viewHolder.yeni.setVisibility(View.VISIBLE);
                                        viewHolder.yeni.setText("YENİ");
                                    }
                                    else{
                                        viewHolder.yeni.setVisibility(View.GONE);
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
        else {
            viewHolder.favori.setVisibility(View.GONE);DatabaseReference dref = FirebaseDatabase.getInstance().getReference("usersF").child(fuser.getUid());
            dref.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if(dataSnapshot.child("begeni_bildirim_durumu").getValue(String.class).equals("var")){
                        if(dataSnapshot.hasChild("son_begenenler")) {
                            if(dataSnapshot.child("son_begenenler").hasChild(user.getUid())){
                                if(dataSnapshot.child("son_begenenler").child(user.getUid()).getValue(String.class).equals("1")){
                                    viewHolder.yeniKismi.setVisibility(View.VISIBLE);
                                    viewHolder.yeni.setText("YENİ");
                                    dataSnapshot.child("begeni_bildirim_durumu").getRef().setValue("yok");
                                    dataSnapshot.child("son_begenenler").child(user.getUid()).getRef().removeValue();
                                }else{
                                    viewHolder.yeniKismi.setVisibility(View.VISIBLE);
                                    viewHolder.yeni.setText("YENİ x" + dataSnapshot.child("son_begenenler").child(user.getUid()).getValue(String.class));
                                    dataSnapshot.child("begeni_bildirim_durumu").getRef().setValue("yok");
                                    dataSnapshot.child("son_begenenler").child(user.getUid()).getRef().removeValue();
                                }
                            }
                            else
                                viewHolder.yeniKismi.setVisibility(View.GONE);
                        }
                        else
                            viewHolder.yeniKismi.setVisibility(View.GONE);
                        //FirebaseDatabase.getInstance().getReference("usersF").child(fuser.getUid()).child("son_begenen").setValue("");

                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }

        viewHolder.favori.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseUser fuser = FirebaseAuth.getInstance().getCurrentUser();
                DatabaseReference favoriData = FirebaseDatabase.getInstance().getReference("usersF").child(fuser.getUid());
                favoriData.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        User Kullanici = dataSnapshot.getValue(User.class);
                        if (dataSnapshot.hasChild("favorilerim")) {
                            if (dataSnapshot.child("favorilerim").hasChild(user.getUid())) {
                                dataSnapshot.child("favorilerim").child(user.getUid()).getRef().removeValue();
                                viewHolder.favori.setImageDrawable(mContext.getResources().getDrawable(R.drawable.favoriler_bos));

                                SharedPreferences spFavorilerim = mContext.getSharedPreferences("Favorilerim",MODE_PRIVATE);
                                for (int i = 0; i < 10; i++){
                                    if (spFavorilerim.getString("" + i, "null").equals(user.getUid())) {
                                        SharedPreferences.Editor editorFav = spFavorilerim.edit();
                                        editorFav.remove("" + i);
                                        editorFav.commit();
                                    }
                                }
                            } else {
                                if(dataSnapshot.child("favorilerim").getChildrenCount() < Kullanici.getFavori_sayim()) {
                                    dataSnapshot.child("favorilerim").child(user.getUid()).getRef().setValue(ServerValue.TIMESTAMP);
                                    viewHolder.favori.setImageDrawable(mContext.getResources().getDrawable(R.drawable.favoriler_dolu));
                                }
                                else{
                                    Dialog dialog = new Dialog(mActivity);
                                    dialog.setContentView(R.layout.dialog_dizayn2);
                                    dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

                                    TextView baslik = dialog.findViewById(R.id.baslik);
                                    TextView aciklama = dialog.findViewById(R.id.aciklama);
                                    Button buton = dialog.findViewById(R.id.buton);
                                    baslik.setText("Favori Sayısı Arttır");
                                    if(fiyatlar != null) {
                                        aciklama.setText("Kısıtlı sayıda kişiyi favorilerinize ekleyebilirsiniz. Bu sayıyı arttırmak için gerekli miktar " + fiyatlar.getFavori_ekleme() + ", sizde olan " + Kullanici.getKp() + "KP");
                                        if (Kullanici.getKp() >= fiyatlar.getFavori_ekleme()) {
                                            buton.setText("+1 Arttır");
                                            buton.setOnClickListener(new View.OnClickListener() {
                                                @Override
                                                public void onClick(View view) {
                                                    DatabaseReference ref = FirebaseDatabase.getInstance().getReference("usersF").child(Kullanici.getUid());
                                                    ref.child("harcamalarim").child("favsayisi_"+Kullanici.getFavori_sayim()).child("onceki_kp").setValue(Kullanici.getKp());
                                                    ref.child("harcamalarim").child("favsayisi_"+Kullanici.getFavori_sayim()).child("zaman").setValue(ServerValue.TIMESTAMP);
                                                    ref.child("kp").setValue(Kullanici.getKp() - fiyatlar.getFavori_ekleme()).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                        @Override
                                                        public void onSuccess(@NonNull Void unused) {
                                                            ref.child("favori_sayim").setValue(Kullanici.getFavori_sayim() + 1).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                @Override
                                                                public void onSuccess(@NonNull Void unused) {

                                                                    Kullanici.setKp(Kullanici.getKp() - fiyatlar.getFavori_ekleme());
                                                                    Kullanici.setFavori_sayim(Kullanici.getFavori_sayim() + 1);
                                                                    dataSnapshot.child("favorilerim").child(user.getUid()).getRef().setValue(ServerValue.TIMESTAMP).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                        @Override
                                                                        public void onSuccess(@NonNull Void unused) {
                                                                            viewHolder.favori.setImageDrawable(mContext.getResources().getDrawable(R.drawable.favoriler_dolu));
                                                                            dialog.dismiss();
                                                                        }
                                                                    });
                                                                }
                                                            });
                                                        }
                                                    }).addOnFailureListener(new OnFailureListener() {
                                                        @Override
                                                        public void onFailure(@NonNull Exception e) {
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
                                                    mActivity.startActivity(new Intent(mActivity, MarketActivity.class));
                                                }
                                            });
                                        }
                                    }
                                    dialog.show();
                                }
                            }
                        } else {
                            dataSnapshot.child("favorilerim").child(user.getUid()).getRef().setValue(ServerValue.TIMESTAMP);
                            viewHolder.favori.setImageDrawable(mContext.getResources().getDrawable(R.drawable.favoriler_dolu));
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }
        });
        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent alper = new Intent(mContext, DigerProfilActivity.class);
                alper.putExtra("userid", user.getUid());
                alper.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                mContext.startActivity(alper);
            }
        });
    }

    public void TasarimDegistir(String tasDegeri, ViewHolder viewHolder) {
        GradientDrawable gradientBackground =  new GradientDrawable();

        gradientBackground.setOrientation(GradientDrawable.Orientation.LEFT_RIGHT);
        gradientBackground.setCornerRadius(50);

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

        viewHolder.background.setBackground(gradientBackground);
    }

    private boolean internetVarmi(){
        ConnectivityManager connectivityManager = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED)
            return true;
        else
            return false;
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
    @Override
    public int getItemCount() {
        return mUser.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        public TextView isim,begeniSayisi,yeni;
        public ImageView favori,profile_image,rozet;
        public ProgressBar pbar;
        public RelativeLayout yeniKismi,begeniKismi,background;
        public ViewHolder(View itemView) {
            super(itemView);
            begeniSayisi = itemView.findViewById(R.id.begeni_sayisi);
            begeniKismi = itemView.findViewById(R.id.begenenler_kismi);
            yeniKismi = itemView.findViewById(R.id.yeni_kismi);
            yeni = itemView.findViewById(R.id.yeni);
            isim = itemView.findViewById(R.id.isim);
            background = itemView.findViewById(R.id.rel_alt_kisim);
            favori = itemView.findViewById(R.id.favori);
            pbar = itemView.findViewById(R.id.pbar);
            profile_image = itemView.findViewById(R.id.resim);
            rozet = itemView.findViewById(R.id.rozet);
        }
    }
}
