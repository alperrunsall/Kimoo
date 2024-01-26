package com.kimoo.android.extra;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.kimoo.android.AyarDegistirActivity;
import com.kimoo.android.Model.User;
import com.kimoo.android.R;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class EngellenenlerAdapter extends RecyclerView.Adapter<EngellenenlerAdapter.ViewHolder>  {
    private Context mContext;
    private List<User> mUser;
    int spaceCount = 0;

    public EngellenenlerAdapter(Context mContext, List<User> mUser) {
        this.mContext = mContext;
        this.mUser = mUser;
    }
    @NonNull
    @Override
    public EngellenenlerAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.engellediklerim_item,viewGroup,false);
        return new EngellenenlerAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final EngellenenlerAdapter.ViewHolder viewHolder, int i) {
        final User user = mUser.get(i);
        final FirebaseUser fuser = FirebaseAuth.getInstance().getCurrentUser();
        viewHolder.isim.setText(AdiSansurle(user));
        //Glide.with(mContext).asBitmap().load(user.getPp_url()).into(viewHolder.foto);
        viewHolder.btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatabaseReference ref = FirebaseDatabase.getInstance().getReference("usersF").child(fuser.getUid()).child("engellediklerim");
                ref.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        dataSnapshot.child(user.getUid()).getRef().removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toast.makeText(mContext, "Engeli kaldırdınız.", Toast.LENGTH_SHORT).show();
                                viewHolder.itemView.setVisibility(View.GONE);
                            }
                        });
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }
        });
        viewHolder.btn.setBackground(AyarDegistirActivity.gradientDrawable);
    }

    @Override
    public int getItemCount() {
        return mUser.size();
    }
    private String AdiSansurle(User user){
        /*for (char c : user.getAd().toCharArray()) {
            if (c == ' ') {
                spaceCount++;
            }
        }
        user.getAd().indexOf(' ');
        */
        String kesilmisIsim = user.getAd().substring(1);
        StringBuilder yeniisim = new StringBuilder(kesilmisIsim);
        for(int i = 0; i < kesilmisIsim.length(); i++){
            yeniisim.setCharAt(i,'*');
        }

        String sonHal = user.getAd().substring(0,1).toUpperCase() + yeniisim.toString();
        return sonHal;
    }
    private String YasSansurle(User user){
        return "**";
    }
    public class ViewHolder extends RecyclerView.ViewHolder{

        public TextView isim;
        public CircleImageView foto;
        public Button btn;

        public ViewHolder(View itemView) {
            super(itemView);

            isim = itemView.findViewById(R.id.isim);
            foto = itemView.findViewById(R.id.foto);
            btn = itemView.findViewById(R.id.engel_kaldir);
        }
    }
}
