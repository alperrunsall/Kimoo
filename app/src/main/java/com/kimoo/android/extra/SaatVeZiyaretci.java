package com.kimoo.android.extra;

public class SaatVeZiyaretci {
    String saat;
    int ziyaretci;

    public SaatVeZiyaretci(String saat, int ziyaretci) {
        this.saat = saat;
        this.ziyaretci = ziyaretci;
    }

    public String getSaat() {
        return saat;
    }

    public void setSaat(String saat) {
        this.saat = saat;
    }

    public int getZiyaretci() {
        return ziyaretci;
    }

    public void setZiyaretci(int ziyaretci) {
        this.ziyaretci = ziyaretci;
    }
}
