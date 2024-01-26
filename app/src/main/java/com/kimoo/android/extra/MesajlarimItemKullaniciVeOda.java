package com.kimoo.android.extra;

import com.kimoo.android.Model.User;

public class MesajlarimItemKullaniciVeOda {

    private User user;
    private String odaNo;
    private Long zaman;

    public MesajlarimItemKullaniciVeOda(User user, String odaNo,Long zaman) {
        this.user = user;
        this.odaNo = odaNo;
        this.zaman = zaman;
    }

    public User getUser() {
        return user;
    }

    public Long getZaman() {
        return zaman;
    }

    public void setZaman(Long zaman) {
        this.zaman = zaman;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getOdaNo() {
        return odaNo;
    }

    public void setOdaNo(String odaNo) {
        this.odaNo = odaNo;
    }
}
