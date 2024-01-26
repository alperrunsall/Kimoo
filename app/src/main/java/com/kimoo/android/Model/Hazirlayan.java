package com.kimoo.android.Model;

public class Hazirlayan {

    private String isim, k_adi, uid;
    private int tiklanacakMi;

    public Hazirlayan(String isim, String k_adi, String uid, int tiklanacakMi) {
        this.isim = isim;
        this.k_adi = k_adi;
        this.uid = uid;
        this.tiklanacakMi = tiklanacakMi;
    }

    public int getTiklanacakMi() {
        return tiklanacakMi;
    }

    public void setTiklanacakMi(int tiklanacakMi) {
        this.tiklanacakMi = tiklanacakMi;
    }

    public String getIsim() {
        return isim;
    }

    public void setIsim(String isim) {
        this.isim = isim;
    }

    public String getK_adi() {
        return k_adi;
    }

    public void setK_adi(String k_adi) {
        this.k_adi = k_adi;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }
}
