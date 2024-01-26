package com.kimoo.android.Model;

public class Yer {
    private String dbisim;
    private String isim;
    private String sehir;
    private String acik_mi;
    private String aciklama;
    private String calisma_saati;
    private String calisma_saatleri;
    private String konum_long;
    private String konum_latit;
    private String mekan_turu;
    private String kriter;
    private int ziyaretciSayisi;

    public Yer(String dbisim, String isim, String konum_long, String konum_latit, String mekan_turu, String acik_mi, String calisma_saatleri, String kriter, String aciklama, String calisma_saati, String sehir) {
        this.dbisim = dbisim;
        this.isim = isim;
        this.konum_long = konum_long;
        this.konum_latit = konum_latit;
        this.mekan_turu = mekan_turu;
        this.kriter = kriter;
        this.acik_mi = acik_mi;
        this.calisma_saatleri = calisma_saatleri;
        this.calisma_saati = calisma_saati;
        this.aciklama = aciklama;
        this.sehir = sehir;
    }

    public Yer() {
    }
    public int getZiyaretciSayisi() {
        return ziyaretciSayisi;
    }

    public void setZiyaretciSayisi(int ziyaretciSayisi) {
        this.ziyaretciSayisi = ziyaretciSayisi;
    }
    public String getSehir() {
        return sehir;
    }

    public void setSehir(String sehir) {
        this.sehir = sehir;
    }

    public String getAciklama() {
        return aciklama;
    }

    public void setAciklama(String aciklama) {
        this.aciklama = aciklama;
    }

    public String getCalisma_saati() {
        return calisma_saati;
    }

    public void setCalisma_saati(String calisma_saati) {
        this.calisma_saati = calisma_saati;
    }

    public String getCalisma_saatleri() {
        return calisma_saatleri;
    }

    public void setCalisma_saatleri(String calisma_saatleri) {
        this.calisma_saatleri = calisma_saatleri;
    }

    public String getAcik_mi() {
        return acik_mi;
    }

    public void setAcik_mi(String acik_mi) {
        this.acik_mi = acik_mi;
    }

    public String getKriter() {
        return kriter;
    }

    public void setKriter(String kriter) {
        this.kriter = kriter;
    }

    public String getDbisim() {
        return dbisim;
    }

    public void setDbisim(String dbisim) {
        this.dbisim = dbisim;
    }

    public String getKonum_long() {
        return konum_long;
    }

    public void setKonum_long(String konum_long) {
        this.konum_long = konum_long;
    }

    public String getKonum_latit() {
        return konum_latit;
    }

    public void setKonum_latit(String konum_latit) {
        this.konum_latit = konum_latit;
    }

    public String getMekan_turu() {
        return mekan_turu;
    }

    public void setMekan_turu(String mekan_turu) {
        this.mekan_turu = mekan_turu;
    }

    public String getIsim() {
        return isim;
    }

    public void setIsim(String isim) {
        this.isim = isim;
    }

}
