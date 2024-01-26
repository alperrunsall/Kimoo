package com.kimoo.android.extra;

import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.os.Bundle;
import androidx.annotation.NonNull;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.kimoo.android.R;
import com.kimoo.android.TaraActivity;

import java.util.Timer;
import java.util.TimerTask;

public class GorevBilgi extends BottomSheetDialog {

    public GorevBilgi(@NonNull Context context) {
        super(context);
    }
    public static String odulYazi, infoYazi;
    private RelativeLayout bir, iki, uc, dort, bes;
    private ImageView bir_arka, iki_arka, uc_arka, dort_arka, bes_arka;
    public static long suan, sure, zaman;
    private long kalanZaman;
    private String
            bilgi1 = "Tüm görevler 1 kez yapılabilir. Görevlerdeki istenen miktarlar, görev yapıldıkça güncellenir.",
            bilgi2 = "Beğen görevlerinde, daha önce beğenmediğiniz birini beğenmelisiniz.",
            bilgi3 = "Mesajlaş görevlerinde, mesaj attığınız kişi size yanıt vermeli ve şuanki mesajlaştığınız kişi sayınızı arttırmalısınız.",
            bilgi4 = "Favorilere ekle görevinde favori kişi sayınızı arttırmalısınız.",
            bilgi5 = "Bul görevlerinde, daha önce bulmadığınız birini bulmalısınız.";
    TextView odul,gorevBilgi,gorevBilgi2,gorevBilgi3,gorevBilgi4,gorevBilgi5, zamanYazi;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        odul = findViewById(R.id.odul);
        gorevBilgi = findViewById(R.id.odul_bilgi);
        gorevBilgi5 = findViewById(R.id.odul_bilgi5);
        gorevBilgi2 = findViewById(R.id.odul_bilgi2);
        gorevBilgi3 = findViewById(R.id.odul_bilgi3);
        gorevBilgi4 = findViewById(R.id.odul_bilgi4);
        bir = findViewById(R.id.bir);
        iki = findViewById(R.id.iki);
        uc = findViewById(R.id.uc);
        dort = findViewById(R.id.dort);
        bes = findViewById(R.id.bes);

        bir_arka = findViewById(R.id.bir_arka);
        iki_arka = findViewById(R.id.iki_arka);
        uc_arka = findViewById(R.id.uc_arka);
        dort_arka = findViewById(R.id.dort_arka);
        bes_arka = findViewById(R.id.bes_arka);

        bir_arka.setColorFilter(TaraActivity.ortaRenk);
        iki_arka.setColorFilter(TaraActivity.ortaRenk);
        uc_arka.setColorFilter(TaraActivity.ortaRenk);
        dort_arka.setColorFilter(TaraActivity.ortaRenk);
        bes_arka.setColorFilter(TaraActivity.ortaRenk);

        zamanYazi = findViewById(R.id.zaman);

        if(sure != 0)
            kalanZaman = sure - (suan - zaman);
        else
            zamanYazi.setText("Süre : SINIRSIZ");

        if (sure != 0 && kalanZaman > 5000) {
            Timer T=new Timer();
            T.scheduleAtFixedRate(new TimerTask() {
                @Override
                public void run() {
                    getActivity().runOnUiThread(new Runnable()
                    {
                        @Override
                        public void run()
                        {
                            if(kalanZaman > 1000) {
                                //zamanYazi.setText(""+ (suan - zaman));
                                zamanYazi.setText("Süre : " + farkiHesapla(kalanZaman));
                                kalanZaman -= 1000;
                            }else{
                                zamanYazi.setText("Süre bitti !");
                                T.cancel();
                            }
                        }
                    });
                }
            }, 1000, 1000);

        }

        if(infoYazi.equals("")){
            gorevBilgi.setText(bilgi1);
            gorevBilgi.setVisibility(View.VISIBLE);
            bir.setVisibility(View.VISIBLE);
        }
        if(infoYazi.contains("1")){
            gorevBilgi.setText(bilgi1);
            gorevBilgi2.setText(bilgi5);

            gorevBilgi.setVisibility(View.VISIBLE);
            gorevBilgi2.setVisibility(View.VISIBLE);
            bir.setVisibility(View.VISIBLE);
            iki.setVisibility(View.VISIBLE);
        }
        if(infoYazi.contains("2")){
            gorevBilgi.setText(bilgi1);
            if(gorevBilgi2.getVisibility() == View.VISIBLE){
                gorevBilgi3.setText(bilgi3);
                gorevBilgi4.setText(bilgi4);

                dort.setVisibility(View.VISIBLE);
                gorevBilgi4.setVisibility(View.VISIBLE);
            }else{
                gorevBilgi2.setText(bilgi3);
                gorevBilgi3.setText(bilgi4);
            }

            gorevBilgi.setVisibility(View.VISIBLE);
            gorevBilgi2.setVisibility(View.VISIBLE);
            gorevBilgi3.setVisibility(View.VISIBLE);
            bir.setVisibility(View.VISIBLE);
            iki.setVisibility(View.VISIBLE);
            uc.setVisibility(View.VISIBLE);
        }
        if(infoYazi.contains("3")){
            gorevBilgi.setText(bilgi1);

            gorevBilgi.setVisibility(View.VISIBLE);
            bir.setVisibility(View.VISIBLE);
        }

        if(infoYazi.contains("4")){
            gorevBilgi.setText(bilgi1);

            if(gorevBilgi2.getVisibility() == View.VISIBLE) {
                if(gorevBilgi3.getVisibility() == View.VISIBLE) {
                    if(gorevBilgi4.getVisibility() == View.VISIBLE) {
                        gorevBilgi5.setText(bilgi2);

                        gorevBilgi4.setVisibility(View.VISIBLE);
                        dort.setVisibility(View.VISIBLE);
                        gorevBilgi3.setVisibility(View.VISIBLE);
                        uc.setVisibility(View.VISIBLE);
                        gorevBilgi5.setVisibility(View.VISIBLE);
                        bes.setVisibility(View.VISIBLE);
                    }
                    else{
                        gorevBilgi4.setText(bilgi2);

                        gorevBilgi4.setVisibility(View.VISIBLE);
                        dort.setVisibility(View.VISIBLE);
                        gorevBilgi3.setVisibility(View.VISIBLE);
                        uc.setVisibility(View.VISIBLE);
                    }
                }else{
                    gorevBilgi3.setText(bilgi2);

                    gorevBilgi3.setVisibility(View.VISIBLE);
                    uc.setVisibility(View.VISIBLE);
                }
            }else{
                gorevBilgi2.setText(bilgi2);
            }

            gorevBilgi.setVisibility(View.VISIBLE);
            gorevBilgi2.setVisibility(View.VISIBLE);
            bir.setVisibility(View.VISIBLE);
            iki.setVisibility(View.VISIBLE);

        }
        if(infoYazi.contains("5")){
            gorevBilgi.setText(bilgi1);

            gorevBilgi.setVisibility(View.VISIBLE);
            bir.setVisibility(View.VISIBLE);
        }





        odul.setText(odulYazi);
    }
    public Activity getActivity() {
        Context context = getContext();
        while (context instanceof ContextWrapper) {
            if (context instanceof Activity) {
                return (Activity)context;
            }
            context = ((ContextWrapper)context).getBaseContext();
        }
        return null;
    }

    private String farkiHesapla(Long kalanZaman)
    {
        long yirmidortSaat = 86400000;
        long asilzaman = yirmidortSaat - kalanZaman;
        long saat = kalanZaman / 3600000; // asilZaman olursa 24 saatten çıkartır
        long dakika = (kalanZaman / 60000) - ((kalanZaman / 3600000) * 60);
        long saniye = (kalanZaman / 1000) - ((kalanZaman / 60000) * 60);


        if(saniye > -1) {
            if(String.valueOf(saat).length() < 2 && String.valueOf(dakika).length() < 2 && String.valueOf(saniye).length() < 2){
                return "0"+saat + ":0" + dakika + ":0" + saniye;
            }
            else if(String.valueOf(saat).length() < 2 && String.valueOf(dakika).length() < 2 && String.valueOf(saniye).length() == 2){
                return "0"+saat + ":0" + dakika + ":" + saniye;

            }
            else if(String.valueOf(saat).length() == 2 && String.valueOf(dakika).length() < 2 && String.valueOf(saniye).length() == 2){
                return ""+saat + ":0" + dakika + ":" + saniye;
            }
            else if(String.valueOf(saat).length() < 2 && String.valueOf(dakika).length() == 2 && String.valueOf(saniye).length() == 2){
                return "0"+saat + ":" + dakika + ":" + saniye;
            }
            else if(String.valueOf(saat).length() == 2 && String.valueOf(dakika).length() == 2 && String.valueOf(saniye).length() < 2){
                return ""+saat + ":" + dakika + ":0" + saniye;
            }
            else if(String.valueOf(saat).length() < 2 && String.valueOf(dakika).length() == 2 && String.valueOf(saniye).length() < 2){
                return "0"+saat + ":" + dakika + ":0" + saniye;
            }
            else if(String.valueOf(saat).length() == 2 && String.valueOf(dakika).length() < 2 && String.valueOf(saniye).length() < 2){
                return ""+saat + ":0" + dakika + ":0" + saniye;
            }
            else{
                return ""+saat + ":" + dakika + ":" + saniye;
            }
        }
        else {

            return "Bekleyin...";
        }


    }
}
