package com.kimoo.android.extra;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;
import com.kimoo.android.DestekActivity;
import com.kimoo.android.DigerProfilActivity;
import com.kimoo.android.Model.Hazirlayan;
import com.kimoo.android.Model.User;
import com.kimoo.android.Model.Yer;
import com.kimoo.android.R;

import java.util.List;

public class HazirlayanlarAdapter extends RecyclerView.Adapter<HazirlayanlarAdapter.ViewHolder> {

    private Context mContext;
    private List<Hazirlayan> mList;

    public HazirlayanlarAdapter(Context mContext, List<Hazirlayan> mList) {
        this.mContext = mContext;
        this.mList = mList;
    }

    @NonNull
    @Override
    public HazirlayanlarAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.hazirlayanlar_view,parent,false);
        return new HazirlayanlarAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HazirlayanlarAdapter.ViewHolder holder, int i) {
        Hazirlayan hazirlayan = mList.get(i);
        FirebaseUser fuser = FirebaseAuth.getInstance().getCurrentUser();
        holder.k_adi.setText("@"+hazirlayan.getK_adi());
        holder.isim.setText(hazirlayan.getIsim());
        holder.buton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!hazirlayan.getUid().equals(fuser.getUid())) {
                    if (hazirlayan.getTiklanacakMi() == 0) {
                        Intent profilegit = new Intent(mContext, DigerProfilActivity.class);
                        profilegit.putExtra("userid", hazirlayan.getUid());
                        mContext.startActivity(profilegit);
                    }
                }
                else{
                    Dialog dialog = new Dialog(mContext);
                    dialog.setContentView(R.layout.dialog_dizayn3);
                    dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

                    LinearLayout lay1 = dialog.findViewById(R.id.lay1);
                    LinearLayout lay2 = dialog.findViewById(R.id.lay2);
                    TextView baslik = dialog.findViewById(R.id.baslik);
                    TextView aciklama = dialog.findViewById(R.id.aciklama);
                    aciklama.setMovementMethod(new ScrollingMovementMethod());
                    Button buton = dialog.findViewById(R.id.buton);
                    EditText adSoyad = dialog.findViewById(R.id.editText);
                    adSoyad.setHint("Ad - Soyad");
                    baslik.setText("BU KİŞİ SİZSİNİZ");
                    aciklama.setText("Listedeki adınızı değiştirmek isterseniz aşağıya yazabilirsiniz. Sonraki adımda isminize tıklandığında profil sayfanıza yönlendirilmesini ayarlayabilirsiniz. Değişiklilerin uygulanması 1 gün sürebilir.");
                    buton.setText("TAMAM");
                    buton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            lay1.setVisibility(View.GONE);
                            lay2.setVisibility(View.GONE);
                            if(!adSoyad.getText().toString().trim().equals("")){
                                dialog.dismiss();
                                Dialog dialog2 = new Dialog(mContext);
                                dialog2.setContentView(R.layout.dialog_dizayn5);
                                dialog2.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

                                LinearLayout lay1 = dialog2.findViewById(R.id.lay1);
                                LinearLayout lay2 = dialog2.findViewById(R.id.lay2);
                                TextView baslik = dialog2.findViewById(R.id.baslik);
                                TextView aciklama = dialog2.findViewById(R.id.aciklama);
                                aciklama.setMovementMethod(new ScrollingMovementMethod());
                                Button buton = dialog2.findViewById(R.id.buton);
                                Button buton2 = dialog2.findViewById(R.id.buton2);
                                baslik.setText("TEŞEKKÜR");
                                aciklama.setText("Teşekkürler kısmındaki isminize tıklandığında Kimoo profil sayfanıza yönlendirilmesini istiyor musunuz?");
                                buton.setText("HAYIR");
                                buton2.setText("EVET");
                                buton.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        FirebaseDatabase.getInstance().getReference("Hazirlayanlar").child(fuser.getUid()).setValue("0"+adSoyad.getText().toString().replaceAll(" ",".B.")+"1").addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(@NonNull Void unused) {
                                                dialog2.dismiss();
                                            }
                                        });
                                    }
                                });
                                buton2.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        FirebaseDatabase.getInstance().getReference("Hazirlayanlar").child(fuser.getUid()).setValue("0"+adSoyad.getText().toString().replaceAll(" ",".B.")+"0").addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(@NonNull Void unused) {
                                                dialog2.dismiss();
                                            }
                                        });
                                    }
                                });
                                dialog2.show();
                            }
                            else{
                                lay1.setVisibility(View.VISIBLE);
                                lay2.setVisibility(View.VISIBLE);
                                Toast.makeText(mContext, "Hiçbir şey yazmadınız.", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                    dialog.show();
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        public TextView isim, k_adi;
        public LinearLayout buton;

        public ViewHolder(View itemView) {
            super(itemView);

            isim = itemView.findViewById(R.id.isim);
            k_adi = itemView.findViewById(R.id.k_adi);
            buton = itemView.findViewById(R.id.buton);

        }
    }
}
