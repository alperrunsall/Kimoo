package com.kimoo.android.extra;

public class MesajlarimItemOdaVeZaman {

    private String odaNo;
    private Long zaman;

    public MesajlarimItemOdaVeZaman(String odaNo, Long zaman) {
        this.odaNo = odaNo;
        this.zaman = zaman;
    }

    public String getOdaNo() {
        return odaNo;
    }

    public void setOdaNo(String odaNo) {
        this.odaNo = odaNo;
    }

    public Long getZaman() {
        return zaman;
    }

    public void setZaman(Long zaman) {
        this.zaman = zaman;
    }
}
