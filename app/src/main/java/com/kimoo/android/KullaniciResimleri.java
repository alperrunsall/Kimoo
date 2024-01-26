package com.kimoo.android;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

public class KullaniciResimleri extends PagerAdapter {
    private Context mContext;
    private ArrayList<String> resimler = new ArrayList<>();

    KullaniciResimleri(Context context,ArrayList<String> resim){
        mContext = context;
        resimler = resim;
    }

    @Override
    public int getCount() {
        return resimler.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object o) {
        return view == o;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        ImageView view = new ImageView(mContext);
        view.setScaleType(ImageView.ScaleType.CENTER_CROP);
        Glide.with(mContext)
                .asBitmap()
                .load(resimler.get(position))
                .into(view);
        container.addView(view,0);
        return view;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
       container.removeView((ImageView) object);
    }
}
