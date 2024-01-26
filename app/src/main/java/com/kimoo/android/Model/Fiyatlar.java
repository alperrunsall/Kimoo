package com.kimoo.android.Model;

public class Fiyatlar {

    private int favori_ekleme, foto_kilit, kilit_acma, kullanici_adi, reklam_izleme, tasarim_seviye1, tasarim_seviye2, tasarim_seviye3, ziyaret_gorevi, gunluk_odul, referans;

    public Fiyatlar() {
    }

    public Fiyatlar(int favori_ekleme, int foto_kilit, int kilit_acma, int kullanici_adi, int reklam_izleme,
                    int tasarim_seviye1, int tasarim_seviye2, int tasarim_seviye3, int ziyaret_gorevi, int gunluk_odul, int referans) {

        this.favori_ekleme = favori_ekleme;
        this.foto_kilit = foto_kilit;
        this.kilit_acma = kilit_acma;
        this.kullanici_adi = kullanici_adi;
        this.reklam_izleme = reklam_izleme;
        this.tasarim_seviye1 = tasarim_seviye1;
        this.tasarim_seviye2 = tasarim_seviye2;
        this.tasarim_seviye3 = tasarim_seviye3;
        this.ziyaret_gorevi = ziyaret_gorevi;
        this.gunluk_odul = gunluk_odul;
        this.referans = referans;
    }

    public int getReferans() {
        return referans;
    }

    public void setReferans(int referans) {
        this.referans = referans;
    }

    public int getZiyaret_gorevi() {
        return ziyaret_gorevi;
    }

    public void setZiyaret_gorevi(int ziyaret_gorevi) {
        this.ziyaret_gorevi = ziyaret_gorevi;
    }

    public int getGunluk_odul() {
        return gunluk_odul;
    }

    public void setGunluk_odul(int gunluk_odul) {
        this.gunluk_odul = gunluk_odul;
    }

    public int getFavori_ekleme() {
        return favori_ekleme;
    }

    public void setFavori_ekleme(int favori_ekleme) {
        this.favori_ekleme = favori_ekleme;
    }

    public int getFoto_kilit() {
        return foto_kilit;
    }

    public void setFoto_kilit(int foto_kilit) {
        this.foto_kilit = foto_kilit;
    }

    public int getKilit_acma() {
        return kilit_acma;
    }

    public void setKilit_acma(int kilit_acma) {
        this.kilit_acma = kilit_acma;
    }

    public int getKullanici_adi() {
        return kullanici_adi;
    }

    public void setKullanici_adi(int kullanici_adi) {
        this.kullanici_adi = kullanici_adi;
    }

    public int getReklam_izleme() {
        return reklam_izleme;
    }

    public void setReklam_izleme(int reklam_izleme) {
        this.reklam_izleme = reklam_izleme;
    }

    public int getTasarim_seviye1() {
        return tasarim_seviye1;
    }

    public void setTasarim_seviye1(int tasarim_seviye1) {
        this.tasarim_seviye1 = tasarim_seviye1;
    }

    public int getTasarim_seviye2() {
        return tasarim_seviye2;
    }

    public void setTasarim_seviye2(int tasarim_seviye2) {
        this.tasarim_seviye2 = tasarim_seviye2;
    }

    public int getTasarim_seviye3() {
        return tasarim_seviye3;
    }

    public void setTasarim_seviye3(int tasarim_seviye3) {
        this.tasarim_seviye3 = tasarim_seviye3;
    }
}

