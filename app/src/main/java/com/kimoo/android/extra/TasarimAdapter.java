package com.kimoo.android.extra;

import static android.content.Context.MODE_PRIVATE;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.drawable.GradientDrawable;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.kimoo.android.ProfilActivity;
import com.kimoo.android.R;

import java.util.List;

public class TasarimAdapter extends RecyclerView.Adapter<TasarimAdapter.ViewHolder> {

    private Context mContext;
    private List<Integer> mDeger;
    private int tasarimModu;
    private List<String> mIsim;
    private List<GradientDrawable> mGradientDrawable;

    public TasarimAdapter(Context mContext, List<Integer> mDeger, List<GradientDrawable> mGradientDrawable, int tasarimModu, List<String> mIsim) {
        this.mContext = mContext;
        this.mDeger = mDeger;
        this.tasarimModu = tasarimModu;
        this.mGradientDrawable = mGradientDrawable;
        this.mIsim = mIsim;
    }

    @NonNull
    @Override
    public TasarimAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.tasarim_satinalma_view,viewGroup,false);
        return new TasarimAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final TasarimAdapter.ViewHolder viewHolder, @SuppressLint("RecyclerView") int i) {
        final int deger = mDeger.get(i);
        final GradientDrawable gradientDrawable = mGradientDrawable.get(i);

        viewHolder.background.setBackground(gradientDrawable);
        if (tasarimModu == 0) {// Profil

            if(ProfilActivity.TasarimDegeriProfil.equals(mIsim.get(i)))
                viewHolder.tik.setVisibility(View.VISIBLE);
            else
                viewHolder.tik.setVisibility(View.GONE);

            if(ProfilActivity.TasarimProfilSahipOlduklarim.contains(","+mIsim.get(i))) {
                viewHolder.degerTv.setVisibility(View.GONE);
                viewHolder.para_ic.setVisibility(View.GONE);
            }
            else {
                viewHolder.degerTv.setVisibility(View.VISIBLE);
                viewHolder.para_ic.setVisibility(View.VISIBLE);
            }
        }
        else if(tasarimModu == 1){// Mesaj

            if(ProfilActivity.TasarimDegeriMesaj.equals(mIsim.get(i)))
                viewHolder.tik.setVisibility(View.VISIBLE);
            else
                viewHolder.tik.setVisibility(View.GONE);

            if(ProfilActivity.TasarimMesajSahipOlduklarim.contains(","+mIsim.get(i))) {
                viewHolder.degerTv.setVisibility(View.GONE);
                viewHolder.para_ic.setVisibility(View.GONE);
            }
            else {
                viewHolder.degerTv.setVisibility(View.VISIBLE);
                viewHolder.para_ic.setVisibility(View.VISIBLE);
            }
        }
        else{ // Arayüz

            if(ProfilActivity.TasarimDegeriArayuz.equals(mIsim.get(i)))
                viewHolder.tik.setVisibility(View.VISIBLE);
            else
                viewHolder.tik.setVisibility(View.GONE);

            if(ProfilActivity.TasarimArayuzSahipOlduklarim.contains(","+mIsim.get(i))) {
                viewHolder.degerTv.setVisibility(View.GONE);
                viewHolder.para_ic.setVisibility(View.GONE);
            }
            else {
                viewHolder.degerTv.setVisibility(View.VISIBLE);
                viewHolder.para_ic.setVisibility(View.VISIBLE);
            }
        }

        if(!TasarimSecenekleri.onizlemeTasDegeri.equals(mIsim.get(i)))
            viewHolder.market_ic.setVisibility(View.GONE);
        else
            if (viewHolder.tik.getVisibility() != View.VISIBLE)
                viewHolder.market_ic.setVisibility(View.VISIBLE);
        if(deger == 1)
            viewHolder.degerTv.setText(""+ProfilActivity.fiyatlar.getTasarim_seviye1());
        else if(deger == 2)
            viewHolder.degerTv.setText(""+ProfilActivity.fiyatlar.getTasarim_seviye2());
        else if(deger == 3)
            viewHolder.degerTv.setText(""+ProfilActivity.fiyatlar.getTasarim_seviye3());


        viewHolder.arkaplan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (viewHolder.market_ic.getVisibility() == View.GONE) {
                    if (viewHolder.degerTv.getVisibility() == View.GONE) { // Bunun anlamı = bu tasarıma sahip olduğumuz.
                        ProfilActivity.TasarimDegistir("" + mIsim.get(i), tasarimModu, true);
                        if (tasarimModu == 2) {
                            SharedPreferences tas_shared = mContext.getSharedPreferences("TasarimBilgileri",MODE_PRIVATE);
                            SharedPreferences.Editor tas_seditor = tas_shared.edit();
                            tas_seditor.putString("tasarim_arayuz", mIsim.get(i));
                            tas_seditor.commit();
                        }
                    } else {
                        TasarimSecenekleri.onizlemeTasDegeri = mIsim.get(i);
                        TasarimSecenekleri.OnizlemeTasarimiGuncelle();
                        //ProfilActivity.VeritabanindaSatinAlmaYadaTasarimDegistirme(true,(i+1),tasarimModu,deger);
                    }
                }else{

                    ProfilActivity.VeritabanindaSatinAlmaYadaTasarimDegistirme(true,""+(i+1),tasarimModu,deger);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mDeger.size();
    }
    public class ViewHolder extends RecyclerView.ViewHolder{

        public TextView degerTv;
        public RelativeLayout background;
        public LinearLayout arkaplan;
        public ImageView tik, para_ic, market_ic;

        public ViewHolder(View itemView) {
            super(itemView);

            degerTv = itemView.findViewById(R.id.tasarim_degeri);
            background = itemView.findViewById(R.id.tasarim_background);
            arkaplan = itemView.findViewById(R.id.arkaplan);
            tik = itemView.findViewById(R.id.tik);
            para_ic = itemView.findViewById(R.id.para_ic);
            market_ic = itemView.findViewById(R.id.satinAl);

        }
    }
}
