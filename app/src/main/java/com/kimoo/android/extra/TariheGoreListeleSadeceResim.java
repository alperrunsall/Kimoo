package com.kimoo.android.extra;

public class TariheGoreListeleSadeceResim {

    public String uid;
    public Long zaman;

    public TariheGoreListeleSadeceResim() {
    }

    public TariheGoreListeleSadeceResim(String uid, Long zaman) {
        this.uid = uid;
        this.zaman = zaman;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public Long getZaman() {
        return zaman;
    }

    public void setZaman(Long zaman) {
        this.zaman = zaman;
    }
}
