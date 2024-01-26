package com.kimoo.android.Model;

import androidx.annotation.NonNull;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Calendar;
import java.util.TimeZone;

public class User {


    private String ad;
    private String kime_yaziyor;
    private String sehir;
    private String uid;
    private String usernamef;
    private String arama;
    private String id;
    private String cinsiyet;
    private String dg;
    private String email;
    private String gittigi_yer;
    private String begeni_sayisi;
    private String karsilasma;
    private String and_id;
    private String tas_arayuz;
    private String tas_arayuz_sahibim;
    private String tas_profil;
    private int kp;
    private int gun;
    private int favori_sayim;
    private int ozel_kadi;
    private String tas_profil_sahibim;
    private String tas_mesaj;
    private String tas_mesaj_sahibim;
    private String beni_begenme_sayisi;
    private String gizlilik_ad;
    private String gizlilik_foto;
    private String gizlilik_mekan;
    private Long suan;
    private FirebaseUser fuser;
    private DatabaseReference ref;
    public User(String ad,String arama,Long suan,String id,String kime_yaziyor,String email, String sehir,
                String uid,String usernamef,String cinsiyet,String dg,String begeni_sayisi,String gittigi_yer,String and_id,String
                gizlilik_ad,String gizlilik_foto,String gizlilik_mekan,String tas_arayuz_sahibim,String tas_mesaj_sahibim,String tas_profil_sahibim,
                String tas_arayuz, String tas_mesaj, String tas_profil, int gun, int kp, int ozel_kadi, int favori_sayim) {
        this.ad = ad;
        this.kime_yaziyor = kime_yaziyor;
        this.sehir = sehir;
        this.gittigi_yer = gittigi_yer;
        this.uid = uid;
        this.usernamef = usernamef;
        this.arama = arama;
        this.id = id;
        this.and_id = and_id;
        this.cinsiyet = cinsiyet;
        this.dg = dg;
        this.email = email;
        this.begeni_sayisi = begeni_sayisi;
        this.gizlilik_ad = gizlilik_ad;
        this.gizlilik_foto = gizlilik_foto;
        this.gizlilik_mekan = gizlilik_mekan;
        this.suan = suan;
        this.favori_sayim = favori_sayim;
        this.tas_mesaj = tas_mesaj;
        this.tas_profil = tas_profil;
        this.tas_arayuz = tas_arayuz;
        this.tas_mesaj_sahibim = tas_mesaj_sahibim;
        this.tas_profil_sahibim = tas_profil_sahibim;
        this.tas_arayuz_sahibim = tas_arayuz_sahibim;
        this.kp = kp;
        this.ozel_kadi = ozel_kadi;
        this.gun = gun;

        fuser = FirebaseAuth.getInstance().getCurrentUser();
        ref = FirebaseDatabase.getInstance().getReference("usersF").child(fuser.getUid()).child("bulduklarim").child(getUid());

    }

    public User(){

    }

    public String getGizlilik_mekan() {
        return gizlilik_mekan;
    }

    public void setGizlilik_mekan(String gizlilik_mekan) {
        this.gizlilik_mekan = gizlilik_mekan;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public int getGun() {
        return gun;
    }

    public void setGun(int gun) {
        this.gun = gun;
    }

    public int getOzel_kadi() {
        return ozel_kadi;
    }

    public int getFavori_sayim() {
        return favori_sayim;
    }

    public void setFavori_sayim(int favori_sayim) {
        this.favori_sayim = favori_sayim;
    }

    public void setOzel_kadi(int ozel_kadi) {
        this.ozel_kadi = ozel_kadi;
    }

    public int getKp() {
        return kp;
    }

    public void setKp(int kp) {
        this.kp = kp;
    }

    public String getTas_arayuz() {
        return tas_arayuz;
    }

    public void setTas_arayuz(String tas_arayuz) {
        this.tas_arayuz = tas_arayuz;
    }

    public String getTas_arayuz_sahibim() {
        return tas_arayuz_sahibim;
    }

    public void setTas_arayuz_sahibim(String tas_arayuz_sahibim) {
        this.tas_arayuz_sahibim = tas_arayuz_sahibim;
    }

    public String getTas_profil() {
        return tas_profil;
    }

    public void setTas_profil(String tas_profil) {
        this.tas_profil = tas_profil;
    }

    public String getTas_profil_sahibim() {
        return tas_profil_sahibim;
    }

    public void setTas_profil_sahibim(String tas_profil_sahibim) {
        this.tas_profil_sahibim = tas_profil_sahibim;
    }

    public String getTas_mesaj() {
        return tas_mesaj;
    }

    public void setTas_mesaj(String tas_mesaj) {
        this.tas_mesaj = tas_mesaj;
    }

    public String getTas_mesaj_sahibim() {
        return tas_mesaj_sahibim;
    }

    public void setTas_mesaj_sahibim(String tas_mesaj_sahibim) {
        this.tas_mesaj_sahibim = tas_mesaj_sahibim;
    }

    public Long getSuan() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(suan);
        calendar.setTimeZone(TimeZone.getTimeZone("Turkey"));
        long saat = calendar.get(Calendar.HOUR_OF_DAY);
        return calendar.getTimeInMillis();
    }

    public void setSuan(Long suan) {
        this.suan = suan;
    }


    public String getGizlilik_ad() {
        return gizlilik_ad;
    }

    public void setGizlilik_ad(String gizlilik_ad) {
        this.gizlilik_ad = gizlilik_ad;
    }

    public String getGizlilik_foto() {
        return gizlilik_foto;
    }

    public void setGizlilik_foto(String gizlilik_foto) {
        this.gizlilik_foto = gizlilik_foto;
    }

    public String getAnd_id() {
        return and_id;
    }

    public void setAnd_id(String and_id) {
        this.and_id = and_id;
    }

    public String getGittigi_yer() {
        return gittigi_yer;
    }

    public void setGittigi_yer(String gittigi_yer) {
        this.gittigi_yer = gittigi_yer;
    }

    public String getAd() {
        return ad;
    }

    public void setAd(String ad) {
        this.ad = ad;
    }



    public String getKime_yaziyor() {
        return kime_yaziyor;
    }

    public void setKime_yaziyor(String kime_yaziyor) {
        this.kime_yaziyor = kime_yaziyor;
    }

    public String getSehir() {
        return sehir;
    }

    public void setSehir(String sehir) {
        this.sehir = sehir;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getUsernamef() {
        return usernamef;
    }

    public void setUsernamef(String usernamef) {
        this.usernamef = usernamef;
    }

    public String getArama() {
        return arama;
    }

    public void setArama(String arama) {
        this.arama = arama;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCinsiyet() {
        return cinsiyet;
    }

    public void setCinsiyet(String cinsiyet) {
        this.cinsiyet = cinsiyet;
    }

    public String getDg() {
        return dg;
    }

    public void setDg(String dg) {
        this.dg = dg;
    }
    public String getKarsilasma() {
        return karsilasma;
    }
    public String getBegeni_sayisi() {
        return begeni_sayisi;
    }

    public void setBegeni_sayisi(String begeni_sayisi) {
        this.begeni_sayisi = begeni_sayisi;
    }
    public void Karsilasma(){
        fuser = FirebaseAuth.getInstance().getCurrentUser();
        ref = FirebaseDatabase.getInstance().getReference("usersF").child(fuser.getUid()).child("bulduklarim").child(getUid());

        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                karsilasma = String.valueOf(dataSnapshot.child("kac_kez_gordum").getValue());

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void setKarsilasma(String karsilasma) {
        this.karsilasma = karsilasma;
    }

    public String getBeni_begenme_sayisi() {
        final FirebaseUser fuser = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("usersF").child(fuser.getUid()).child("begenenler").child(getUid());
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                beni_begenme_sayisi = String.valueOf(dataSnapshot.child("kac_kez_begendim").getValue(Long.class));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        return beni_begenme_sayisi;
    }
}
