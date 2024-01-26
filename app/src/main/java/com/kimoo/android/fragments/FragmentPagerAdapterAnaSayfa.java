package com.kimoo.android.fragments;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

public class FragmentPagerAdapterAnaSayfa extends FragmentStatePagerAdapter {
    int counttab;
    public FragmentPagerAdapterAnaSayfa(FragmentManager fm,int counttab) {
        super(fm);
        this.counttab = counttab;
    }

    @Override
    public Fragment getItem(int i) {
        return null;
    }

    @Override
    public int getCount() {
        return counttab;
    }
}
