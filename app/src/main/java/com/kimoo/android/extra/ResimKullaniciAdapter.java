package com.kimoo.android.extra;

import static android.content.Context.MODE_PRIVATE;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.kimoo.android.DigerProfilActivity;
import com.kimoo.android.Model.Chat;
import com.kimoo.android.Model.User;
import com.kimoo.android.ProfilActivity;
import com.kimoo.android.R;
import com.kimoo.android.TaraActivity;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class ResimKullaniciAdapter extends RecyclerView.Adapter<ResimKullaniciAdapter.ViewHolder>{
    float dX, dY;
    private Context mContext;
    private List<TariheGoreListeleSadeceResim> mItem;
    private FirebaseUser fuser;
    Chat chat;
    String sonMesaj;
    public ResimKullaniciAdapter(Context mContext, List<TariheGoreListeleSadeceResim> mItem) {
        this.mContext = mContext;
        this.mItem = mItem;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.user_item_sadeceresim,viewGroup,false);
        return new ResimKullaniciAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder viewHolder, int i) {
        final String user = mItem.get(i).getUid();
        /*viewHolder.pbar.setVisibility(View.GONE);
        viewHolder.resim.setVisibility(View.VISIBLE);*/

        ContextWrapper cw = new ContextWrapper(mContext);

        File directory = cw.getDir("kullanici_resimleri", MODE_PRIVATE);
        File imagepp = null;
        boolean fotoYuklendiMi = false;

        for(File files : directory.listFiles()){
            if(files.getName().substring(7,files.getName().length()-4).equals(user)){
                imagepp = files;
                fotoYuklendiMi = true;
                viewHolder.resim.setImageURI(Uri.parse(imagepp.getAbsolutePath()));
                //Toast.makeText(mContext, "Foto var", Toast.LENGTH_SHORT).show();
            }
            //if(dosyaKontrol == directory.listFiles().length)
        }

        File finalImagepp = imagepp;
        boolean finalFotoYuklendiMi = fotoYuklendiMi;

        FirebaseDatabase.getInstance().getReference("usersF").child(user).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.child("fotograflarim").hasChild("pp")) {
                    if (!dataSnapshot.child("fotograflarim").child("pp").getValue(String.class).equals("")) {
                        String fotoUrl = dataSnapshot.child("fotograflarim").child("pp").getValue(String.class);

                        if (finalFotoYuklendiMi){
                            if(finalImagepp != null) {
                                if (!finalImagepp.getName().substring(2, 7).equals(fotoUrl.substring(fotoUrl.length() - 9, fotoUrl.length() - 4))) {
                                    finalImagepp.delete();
                                    Glide.with(mContext)
                                            .asBitmap()
                                            .load(fotoUrl)
                                            .into(viewHolder.resim);
                                    new ResimIndir(mContext, fotoUrl, "kullanici_resimleri", "pp" + fotoUrl.substring(fotoUrl.length() - 9, fotoUrl.length() - 4) + user + ".jpg");
                                    //Toast.makeText(mContext, finalImagepp.getName().substring(0, 5) + "\n" + fotoUrl.toString().substring(fotoUrl.toString().length() - 9, fotoUrl.toString().length() - 4), Toast.LENGTH_LONG).show();
                                }
                            }
                        }
                        else{
                            Glide.with(mContext)
                                    .asBitmap()
                                    .load(fotoUrl)
                                    .into(viewHolder.resim);
                            String kayitAdi = "pp" + fotoUrl.substring(fotoUrl.length() - 9,fotoUrl.length() - 4) + user +".jpg";
                            new ResimIndir(mContext, fotoUrl, "kullanici_resimleri", kayitAdi);
                            //Toast.makeText(mContext, "iindirdim " + kayitAdi, Toast.LENGTH_SHORT).show();
                        }
                    }
                    else
                        viewHolder.resim.setImageDrawable(mContext.getResources().getDrawable(R.drawable.kimoo_logo));
                }
                else
                    viewHolder.resim.setImageDrawable(mContext.getResources().getDrawable(R.drawable.kimoo_logo));

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        viewHolder.resim.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                int action = event.getAction();
                if (action == MotionEvent.ACTION_DOWN) {
                    hafifBuyumeAnim(v);
                    return true;
                } else if (action == MotionEvent.ACTION_UP) {
                    cancelHafifBuyumeAnim(v,1,user);
                    return true;
                }else if(action == MotionEvent.ACTION_CANCEL){
                    cancelHafifBuyumeAnim(v,0,user);
                }
                return false;
            }
        });
    }

    private void hafifBuyumeAnim(final View view){
        ObjectAnimator scaleDownX = ObjectAnimator.ofFloat(view, "scaleX", 0.9f);
        ObjectAnimator scaleDownY = ObjectAnimator.ofFloat(view, "scaleY", 0.9f);
        scaleDownX.setDuration(50);
        scaleDownY.setDuration(50);
        scaleDownX.start();
        scaleDownY.start();
    }
    private void cancelHafifBuyumeAnim(final View view, final int i, final String user){
        ObjectAnimator scaleDownX = ObjectAnimator.ofFloat(view, "scaleX", 1);
        ObjectAnimator scaleDownY = ObjectAnimator.ofFloat(view, "scaleY", 1);
        scaleDownX.setDuration(300);
        scaleDownY.setDuration(300);
        scaleDownX.start();
        scaleDownY.start();
        scaleDownY.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                if(i == 1){
                    Intent intent = new Intent(mContext,DigerProfilActivity.class);
                    intent.putExtra("userid",user);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    mContext.startActivity(intent);
                }
                else if(i == 2){

                }
                else if(i == 3){

                }
                else if(i == 4){

                }
                else if(i == 5){

                }
            }
        });

    }
    @Override
    public int getItemCount() {
        return mItem.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        public CircleImageView resim;
        public ProgressBar pbar;
        public ViewHolder(View itemView) {
            super(itemView);

            pbar = itemView.findViewById(R.id.pbar);
            resim = itemView.findViewById(R.id.resim);
        }
    }
    private void digerDosylariSil(String[] Alternatifler,String dosyaParent){
        ContextWrapper cw = new ContextWrapper(mContext);
        File directory = cw.getDir(dosyaParent, MODE_PRIVATE);
        File[] files = directory.listFiles();
        if(files.length != Alternatifler.length) {
            for (int i = 0; i < files.length; i++) {
                for (int a = 0; a < Alternatifler.length; a++) {
                    if (!files[i].getName().equals(Alternatifler[a] + ".jpg")) {
                        files[i].delete();
                    }
                }
            }
        }
    }
    private void ResmiIndir(Bitmap bitmap,CircleImageView foto,String dosyaAdi,String dosyaParent){
        ContextWrapper wrapper = new ContextWrapper(mContext);
        File file = wrapper.getDir(dosyaParent, MODE_PRIVATE);
        file = new File(file, dosyaAdi);
        try{
            OutputStream stream = null;
            stream = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG,100,stream);
            stream.flush();
            stream.close();
        }catch (IOException e)
        {
            e.printStackTrace();
        }
        Uri savedImageURI = Uri.parse(file.getAbsolutePath());
        foto.setImageURI(savedImageURI);

    }
    public boolean dosyaVarmi(String name,String dosyaParent) {
        ContextWrapper cw = new ContextWrapper(mContext);
        File directory = cw.getDir(dosyaParent, MODE_PRIVATE);
        File image = new File(directory, name);
        return image.exists();
    }
    public void dosyayiGetir(String name, ImageView foto, String dosyaParent) {
        ContextWrapper cw = new ContextWrapper(mContext);
        File directory = cw.getDir(dosyaParent, MODE_PRIVATE);
        File image = new File(directory, name);
        Uri savedImageURI = Uri.parse(image.getAbsolutePath());
        foto.setImageURI(savedImageURI);

    }


}
