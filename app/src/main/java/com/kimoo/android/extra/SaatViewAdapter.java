package com.kimoo.android.extra;

import android.app.Activity;
import android.content.Context;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.kimoo.android.R;
import com.kimoo.android.TaraActivity;

import java.util.List;

public class SaatViewAdapter extends RecyclerView.Adapter<SaatViewAdapter.ViewHolder> {

    private Context mContext;
    private List<SaatVeZiyaretci> ziyaretciler;
    private int ziyaretciSayisiMax;
    private Activity activity;

    public SaatViewAdapter(Context mContext,Activity activity, List<SaatVeZiyaretci> ziyaretciler, int ziyaretciSayisiMax) {
        this.mContext = mContext;
        this.activity = activity;
        this.ziyaretciler = ziyaretciler;
        this.ziyaretciSayisiMax = ziyaretciSayisiMax;
    }

    @NonNull
    @Override
    public SaatViewAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.saat_adapter_view,viewGroup,false);
        return new SaatViewAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder viewHolder, int i) {
        final SaatVeZiyaretci ziyaretci = ziyaretciler.get(i);
        int ziyaretciSayisi = ziyaretci.getZiyaretci();
        viewHolder.saatTV.setText(ziyaretci.saat);
        viewHolder.sayiTV.setText(""+ziyaretciSayisi);

        LinearLayout.LayoutParams altParam = new LinearLayout.LayoutParams(viewHolder.temel.getLayoutParams());
        LinearLayout.LayoutParams ustParam = new LinearLayout.LayoutParams(viewHolder.ustKisim.getLayoutParams());
        int altDeger = 0;
        int ustDeger = 0;
        if(ziyaretciSayisi != 0) {
            altDeger = (ziyaretciSayisi * 100) / ziyaretciSayisiMax;
            ustDeger = 100 - ((ziyaretciSayisi * 100 ) / ziyaretciSayisiMax);
        }
        else {
            altDeger = 1;
            ustDeger = 100;
        }

        altParam.weight = altDeger;
        ustParam.weight = ustDeger;

        viewHolder.temel.setLayoutParams(altParam);
        viewHolder.ustKisim.setLayoutParams(ustParam);
        viewHolder.temel.setBackgroundColor(TaraActivity.ortaRenk);
    }

    @Override
    public int getItemCount() {
        return ziyaretciler.size();
    }
    public class ViewHolder extends RecyclerView.ViewHolder{

        public YatayTextView saatTV;
        public TextView sayiTV, kisiSayisi;
        public ImageView giden1,giden2,giden3;
        public RelativeLayout temel,ustKisim;
        public View kuculenMavi;

        public ViewHolder(View itemView) {
            super(itemView);

            saatTV = itemView.findViewById(R.id.saat);
            sayiTV = itemView.findViewById(R.id.sayi);
            ustKisim = itemView.findViewById(R.id.ustKisim);
            temel = itemView.findViewById(R.id.temel);

        }
    }
}
