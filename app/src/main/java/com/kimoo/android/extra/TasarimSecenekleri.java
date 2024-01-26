package com.kimoo.android.extra;

import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.kimoo.android.ProfilActivity;
import com.kimoo.android.R;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class TasarimSecenekleri extends BottomSheetDialog {

    public static Context mContext;
    private static RecyclerView recyclerView;
    private TextView yaziAciklama, baslik, toplam_kp;
    private static int tasModu;
    private static LinearLayout arayuzOnizleme;
    private LinearLayout mesajOnizleme;
    private RelativeLayout profilOnizleme;
    public static TasarimSecenekleri tasarimSecenekleri;
    // Onizleme profil için gerekli olanlar
    private ImageView liste;
    private ImageView kart;
    private ImageView profilResmi2;
    private ImageView rozet;
    private ImageView rozet2;
    private CardView cardCard, cardList;
    private static LinearLayout backgroundList;
    private CircleImageView profilResmi;
    private TextView isim,isim2;
    public static String onizlemeTasDegeri ;
    private static RelativeLayout backgroundCard;
    // Onizleme mesaj için gerekli olanlar
    private TextView mesaj1;
    private static TextView mesaj2;


    public TasarimSecenekleri(@NonNull Context context,int tasModu) {
        super(context);
        mContext = context;
        this.tasModu = tasModu;
        tasarimSecenekleri = this;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        recyclerView = findViewById(R.id.tasarimRV);
        baslik = findViewById(R.id.baslik);
        yaziAciklama = findViewById(R.id.yazi_aciklama);
        toplam_kp = findViewById(R.id.toplam_kp);
        arayuzOnizleme = findViewById(R.id.tasArayuzOnizleme);
        mesajOnizleme = findViewById(R.id.tasMesajOnizleme);
        profilOnizleme = findViewById(R.id.tasProfilOnizleme);
        //Profil için
        kart = findViewById(R.id.kart_gorunumu);
        liste = findViewById(R.id.liste_gorunumu);
        cardCard = findViewById(R.id.cardCard);
        cardList = findViewById(R.id.cardList);
        backgroundList = findViewById(R.id.background);
        backgroundCard = findViewById(R.id.rel_alt_kisim);
        profilResmi = findViewById(R.id.profile_image);
        profilResmi2 = findViewById(R.id.resim);
        isim = findViewById(R.id.isim);
        isim2 = findViewById(R.id.isim2);
        rozet = findViewById(R.id.rozet);
        rozet2 = findViewById(R.id.rozet2);
        //Mesaj için
        mesaj1 = findViewById(R.id.mesaj1);
        mesaj2 = findViewById(R.id.mesaj2);

        recyclerView.setLayoutManager(new GridLayoutManager(mContext,5));
        recyclerView.setHasFixedSize(true);

        kart.setColorFilter(mContext.getResources().getColor(R.color.gri2));
        liste.setColorFilter(ProfilActivity.ortaRenk);

        toplam_kp.setText(""+ ProfilActivity.Kullanici.getKp());
        if(tasModu == 0){
            profilOnizleme.setVisibility(View.VISIBLE);
            baslik.setText("Profil Tasarımını Değiştir");
            yaziAciklama.setText("Bu tasarımı diğer kullanıcılar görebilirler.");
            isim.setText(ProfilActivity.Kullanici.getAd().substring(0,1).toUpperCase() + ProfilActivity.Kullanici.getAd().substring(1).toLowerCase());
            isim2.setText(ProfilActivity.Kullanici.getAd().substring(0,1).toUpperCase() + ProfilActivity.Kullanici.getAd().substring(1).toLowerCase());
            Glide.with(mContext)
                    .asBitmap()
                    .load(ProfilActivity.AsilDataSnapShot.child("fotograflarim").child("pp").getValue(String.class))
                    .into(profilResmi);
            Glide.with(mContext)
                    .asBitmap()
                    .load(ProfilActivity.AsilDataSnapShot.child("fotograflarim").child("pp").getValue(String.class))
                    .into(profilResmi2);
            onizlemeTasDegeri = ProfilActivity.TasarimDegeriProfil;
            OnizlemeTasarimiGuncelle();
            kart.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    liste.setColorFilter(mContext.getResources().getColor(R.color.gri2));
                    kart.setColorFilter(ProfilActivity.ortaRenk);
                    cardCard.setVisibility(View.VISIBLE);
                    cardList.setVisibility(View.GONE);
                }
            });
            liste.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    liste.setColorFilter(ProfilActivity.ortaRenk);
                    kart.setColorFilter(mContext.getResources().getColor(R.color.gri2));
                    cardCard.setVisibility(View.GONE);
                    cardList.setVisibility(View.VISIBLE);
                }
            });
        }
        else if(tasModu == 1){
            onizlemeTasDegeri = ProfilActivity.TasarimDegeriMesaj;
            OnizlemeTasarimiGuncelle();
            mesajOnizleme.setVisibility(View.VISIBLE);
            baslik.setText("Mesaj Tasarımını Değiştir");
            yaziAciklama.setText("Bu tasarımı diğer kullanıcılar görebilirler.");
        }
        else{
            onizlemeTasDegeri = ProfilActivity.TasarimDegeriArayuz;
            OnizlemeTasarimiGuncelle();
            arayuzOnizleme.setVisibility(View.VISIBLE);
            baslik.setText("Arayüz Tasarımını Değiştir");
            yaziAciklama.setText("Bu tasarımı diğer kullanıcılar göremez.");
        }

        if(ProfilActivity.AsilDataSnapShot.hasChild("rozet")){
            if(ProfilActivity.AsilDataSnapShot.child("rozet").getValue(String.class).equals("1")){
                rozet.setVisibility(View.VISIBLE);
                rozet.setImageDrawable(mContext.getResources().getDrawable(R.drawable.rozet1));
                rozet2.setVisibility(View.VISIBLE);
                rozet2.setImageDrawable(mContext.getResources().getDrawable(R.drawable.rozet1));
            }
            if(ProfilActivity.AsilDataSnapShot.child("rozet").getValue(String.class).equals("2")){
                rozet.setVisibility(View.VISIBLE);
                rozet.setImageDrawable(mContext.getResources().getDrawable(R.drawable.rozet2));
                rozet2.setVisibility(View.VISIBLE);
                rozet2.setImageDrawable(mContext.getResources().getDrawable(R.drawable.rozet2));
            }
            if(ProfilActivity.AsilDataSnapShot.child("rozet").getValue(String.class).equals("3")){
                rozet.setVisibility(View.VISIBLE);
                rozet.setImageDrawable(mContext.getResources().getDrawable(R.drawable.rozet3));
                rozet2.setVisibility(View.VISIBLE);
                rozet2.setImageDrawable(mContext.getResources().getDrawable(R.drawable.rozet3));
            }
            if(ProfilActivity.AsilDataSnapShot.child("rozet").getValue(String.class).equals("4")){
                rozet.setVisibility(View.VISIBLE);
                rozet.setImageDrawable(mContext.getResources().getDrawable(R.drawable.rozet4));
                rozet2.setVisibility(View.VISIBLE);
                rozet2.setImageDrawable(mContext.getResources().getDrawable(R.drawable.rozet4));
            }
            if(ProfilActivity.AsilDataSnapShot.child("rozet").getValue(String.class).equals("5")){
                rozet.setVisibility(View.VISIBLE);
                rozet.setImageDrawable(mContext.getResources().getDrawable(R.drawable.rozet5));
                rozet2.setVisibility(View.VISIBLE);
                rozet2.setImageDrawable(mContext.getResources().getDrawable(R.drawable.rozet5));
            }
            if(ProfilActivity.AsilDataSnapShot.child("rozet").getValue(String.class).equals("6")){
                rozet.setVisibility(View.VISIBLE);
                rozet.setImageDrawable(mContext.getResources().getDrawable(R.drawable.rozet6));
                rozet2.setVisibility(View.VISIBLE);
                rozet2.setImageDrawable(mContext.getResources().getDrawable(R.drawable.rozet6));
            }
        }
        else
            rozet.setVisibility(View.GONE);

        Guncelle();
    }

    public static void OnizlemeTasarimiGuncelle() {

        GradientDrawable gradientBackground =  new GradientDrawable();
        gradientBackground.setOrientation(GradientDrawable.Orientation.LEFT_RIGHT);
        GradientDrawable gradientBackground2 =  new GradientDrawable();
        gradientBackground2.setOrientation(GradientDrawable.Orientation.LEFT_RIGHT);
        gradientBackground2.setCornerRadius(50);

        int renk1 = 0,renk2 = 0,t1start = 0,t2start = 0,t1end = 0,t2end = 0,orta = 0;


        renk1 = TasarimRenginiGetir.RengiGetir(mContext,"renk1",onizlemeTasDegeri);
        renk2 = TasarimRenginiGetir.RengiGetir(mContext,"renk2",onizlemeTasDegeri);
        t1start = TasarimRenginiGetir.RengiGetir(mContext,"t1start",onizlemeTasDegeri);
        t2start = TasarimRenginiGetir.RengiGetir(mContext,"t2start",onizlemeTasDegeri);
        t1end = TasarimRenginiGetir.RengiGetir(mContext,"t1end",onizlemeTasDegeri);
        t2end = TasarimRenginiGetir.RengiGetir(mContext,"t2end",onizlemeTasDegeri);
        orta = TasarimRenginiGetir.RengiGetir(mContext,"orta",onizlemeTasDegeri);

        gradientBackground.setColors(new int[]{
                renk1,
                orta,
                renk2
        });
        gradientBackground2.setColors(new int[]{
                renk1,
                orta,
                renk2
        });

        GradientDrawable gradientBackground3 =  new GradientDrawable();
        gradientBackground3.setOrientation(GradientDrawable.Orientation.LEFT_RIGHT);
        gradientBackground3.setCornerRadii(new float[] {50,50,50,50,50,50,0,0});
        gradientBackground3.setColors(new int[]{
                renk1,
                orta,
                renk2
        });
        backgroundCard.setBackground(gradientBackground2);
        backgroundList.setBackground(gradientBackground);
        mesaj2.setBackground(gradientBackground3);
        arayuzOnizleme.setBackground(gradientBackground);
        Guncelle();
    }

    public static void Guncelle(){

        List<GradientDrawable> mGradientDrawable = new ArrayList<>();
        List<Integer> mDeger = new ArrayList<>();
        List<String> mIsim = new ArrayList<>();

        for(int i = 0; i < 15; i ++){
            mIsim.add(""+(i+1));
            mGradientDrawable.add(gradientDrawable(""+(i+1)));
            if ((i + 1) < 5)
                mDeger.add(1);
            else if ((i + 1) < 11)
                mDeger.add(2);
            else
                mDeger.add(3);

            if (i == 14) {
                TasarimAdapter tasarimAdapter = new TasarimAdapter(mContext, mDeger, mGradientDrawable, tasModu,mIsim);
                recyclerView.setAdapter(tasarimAdapter);
            }
        }

        if(ProfilActivity.AsilDataSnapShot != null) {
            if (ProfilActivity.AsilDataSnapShot.child("tas_arayuz_sahibim").getValue(String.class).contains(",o1")) {
                mGradientDrawable.add(gradientDrawable("o1"));
                mDeger.add(10);
                mIsim.add("o1");
            }
            if (ProfilActivity.AsilDataSnapShot.child("tas_arayuz_sahibim").getValue(String.class).contains(",o2")) {
                mGradientDrawable.add(gradientDrawable("o2"));
                mDeger.add(10);
                mIsim.add("o2");
            }
            if (ProfilActivity.AsilDataSnapShot.child("tas_arayuz_sahibim").getValue(String.class).contains(",o3")) {
                mGradientDrawable.add(gradientDrawable("o3"));
                mDeger.add(10);
                mIsim.add("o3");
            }
            if (ProfilActivity.AsilDataSnapShot.child("tas_arayuz_sahibim").getValue(String.class).contains(",o4")) {
                mGradientDrawable.add(gradientDrawable("o4"));
                mDeger.add(10);
                mIsim.add("o4");
            }
            if (ProfilActivity.AsilDataSnapShot.child("tas_arayuz_sahibim").getValue(String.class).contains(",o5")) {
                mGradientDrawable.add(gradientDrawable("o5"));
                mDeger.add(10);
                mIsim.add("o5");
            }
            if (ProfilActivity.AsilDataSnapShot.child("tas_arayuz_sahibim").getValue(String.class).contains(",o6")) {
                mGradientDrawable.add(gradientDrawable("o6"));
                mDeger.add(10);
                mIsim.add("o6");
            }
            TasarimAdapter tasarimAdapter = new TasarimAdapter(mContext, mDeger, mGradientDrawable, tasModu, mIsim);
            recyclerView.setAdapter(tasarimAdapter);
        }
    }
    private static GradientDrawable gradientDrawable(String tasDegeri) {
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

        return gradientBackground;
    }

}
