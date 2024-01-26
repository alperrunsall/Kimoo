package com.kimoo.android.extra;

import android.content.Context;
import android.content.Intent;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.google.firebase.auth.FirebaseUser;
import com.kimoo.android.DigerProfilActivity;
import com.kimoo.android.Model.Chat;
import com.kimoo.android.Model.User;
import com.kimoo.android.R;

import java.util.List;

public class ResimKullaniciAdapter2 extends RecyclerView.Adapter<ResimKullaniciAdapter2.ViewHolder>{

    private Context mContext;
    private List<User> mUser;
    private FirebaseUser fuser;
    Chat chat;
    String sonMesaj;
    public ResimKullaniciAdapter2(Context mContext, List<User> mUser) {
        this.mContext = mContext;
        this.mUser = mUser;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.user_item_sadeceresim2,viewGroup,false);
        return new ResimKullaniciAdapter2.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder viewHolder, int i) {
        final User user = mUser.get(i);
        /*Glide.with(mContext)
             .asBitmap()
             .load(user.getPp_url())
             .into(viewHolder.resim);*/

        viewHolder.resim.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        v.animate()
                                .scaleX(1+0.1f)
                                .scaleY(1+0.1f)
                                .setDuration(300)
                                .start();
                        break;
                    case MotionEvent.ACTION_UP:
                        Intent intent = new Intent(mContext,DigerProfilActivity.class);
                        intent.putExtra("userid",user.getUid());
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        mContext.startActivity(intent);
                    case MotionEvent.ACTION_CANCEL:
                        v.animate()
                                .scaleX(1)
                                .scaleY(1)
                                .setDuration(300)
                                .start();
                        break;
                    default:
                        return false;
                }
                return true;
            }
        });
    }

    @Override
    public int getItemCount() {
        return mUser.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        public ImageView resim;
        public ViewHolder(View itemView) {
            super(itemView);

            resim = itemView.findViewById(R.id.resim);
        }
    }



}
