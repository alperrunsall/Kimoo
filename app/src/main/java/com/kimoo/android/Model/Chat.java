package com.kimoo.android.Model;

public class Chat {

    private String gonderici;
    private String alici;
    private String mesajim;
    private Long zaman;
    private String durum;
    private String gormek_istemeyen1;
    private String gormek_istemeyen2;
    private boolean goruldumu;

    public Chat(String gonderici,String alici,String mesajim,boolean goruldumu,Long zaman,String durum,String gormek_istemeyen1,String gormek_istemeyen2){
        this.gonderici = gonderici;
        this.alici = alici;
        this.mesajim = mesajim;
        this.goruldumu = goruldumu;
        this.zaman = zaman;
        this.durum = durum;
        this.gormek_istemeyen1 = gormek_istemeyen1;
        this.gormek_istemeyen2 = gormek_istemeyen2;
    }
    public Chat(){}

    public String getGonderici() {
        return gonderici;
    }

    public void setGonderici(String gonderici) {
        this.gonderici = gonderici;
    }

    public String getAlici() {
        return alici;
    }

    public void setAlici(String alici) {
        this.alici = alici;
    }

    public String getMesajim() {
        return mesajim;
    }

    public void setMesajim(String mesajim) {
        this.mesajim = mesajim;
    }

    public boolean isGoruldumu() {
        return goruldumu;
    }
    public Long getZaman() {
        return zaman;
    }
    public void setZaman(Long zaman) {
        this.zaman = zaman;
    }
    public void setGoruldumu(boolean goruldumu) {
        this.goruldumu = goruldumu;
    }

    public String getDurum() {
        return durum;
    }

    public String getGormek_istemeyen1() {
        return gormek_istemeyen1;
    }

    public void setGormek_istemeyen1(String gormek_istemeyen1) {
        this.gormek_istemeyen1 = gormek_istemeyen1;
    }

    public String getGormek_istemeyen2() {
        return gormek_istemeyen2;
    }

    public void setGormek_istemeyen2(String gormek_istemeyen2) {
        this.gormek_istemeyen2 = gormek_istemeyen2;
    }

    public void setDurum(String durum) {
        this.durum = durum;
    }
}
