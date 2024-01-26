package com.kimoo.android.extra;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import android.graphics.drawable.GradientDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.kimoo.android.MesajActivity;
import com.kimoo.android.Model.Chat;
import com.kimoo.android.Model.User;
import com.kimoo.android.R;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

public class MesajAdapter extends RecyclerView.Adapter<MesajAdapter.ViewHolder> {

    public int MESAJ_TIP_SAG = 0;
    public int MESAJ_TIP_SOL = 1;
    private Context mContext;
    private List<Chat> mChat,degisiktarihler;
    public boolean zamanGorunurMu = false, solMu;
    public SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yy");
    public String dateString;
    public Activity activity;
    public List<String> refisimleri;
    FirebaseUser fuser;
    User Kullanici = null;
    private GradientDrawable gradientDrawable;

    public MesajAdapter(Context mContext,Activity activity, List<Chat> mChat,List<Chat> degisiktarihler, List<String> refisimleri){
        this.mContext = mContext;
        this.mChat = mChat;
        this.degisiktarihler = degisiktarihler;
        this.activity = activity;
        this.refisimleri = refisimleri;
    }


    @NonNull
    @Override
    public MesajAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        if(viewType == MESAJ_TIP_SAG){
            View view = LayoutInflater.from(mContext).inflate(R.layout.mesaj_item_sag,viewGroup,false);
            solMu = false;
            return new MesajAdapter.ViewHolder(view);
        }else{
            View view = LayoutInflater.from(mContext).inflate(R.layout.mesaj_item_sol,viewGroup,false);
            solMu = true;
            return new MesajAdapter.ViewHolder(view);
        }
    }
    private boolean internetVarmi(){
        ConnectivityManager connectivityManager = (ConnectivityManager) activity.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED)
            return true;
        else {
            Toast.makeText(mContext, "İnternet bağlantınız yok.", Toast.LENGTH_SHORT).show();
            return false;
        }
    }

    private void TarihiYaz(TextView tarih, Long mesajinZamani) {

        if ( ( (System.currentTimeMillis() / 86400000) - (mesajinZamani / 86400000) ) == 0) {
            tarih.setText("BUGÜN");
        }
        else if( ( (System.currentTimeMillis() / 86400000) - (mesajinZamani / 86400000) ) == 1)
            tarih.setText("DÜN");
        else {

            String gununIsmi = new SimpleDateFormat("EEEE").format(new Date(mesajinZamani));
            String tarihDate = new SimpleDateFormat("dd/MM/yyyy").format(new Date(mesajinZamani));
            if (gununIsmi.equals("Monday"))
                gununIsmi = "PAZARTESİ";
            else if (gununIsmi.equals("Tuesday"))
                gununIsmi = "SALI";
            else if (gununIsmi.equals("Wednesday"))
                gununIsmi = "ÇARŞAMBA";
            else if (gununIsmi.equals("Thursday"))
                gununIsmi = "PERŞEMBE";
            else if (gununIsmi.equals("Friday"))
                gununIsmi = "CUMA";
            else if (gununIsmi.equals("Saturday"))
                gununIsmi = "CUMARTESİ";
            else if (gununIsmi.equals("Sunday"))
                gununIsmi = "PAZAR";

            long kacGunOnce = (System.currentTimeMillis() / 86400000) - (mesajinZamani / 86400000);
            if (kacGunOnce < 1)
                tarih.setText("Sistem saatiniz hatalı");
            else if (kacGunOnce > 1 && kacGunOnce <= 7)
                tarih.setText(gununIsmi);
            else
                tarih.setText(tarihDate);
        }
    }
    @Override
    public void onBindViewHolder(@NonNull final MesajAdapter.ViewHolder viewHolder, @SuppressLint("RecyclerView") int i) {
        final Chat chat = mChat.get(i);
        viewHolder.show_msj.setText(chat.getMesajim());
        fuser = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("usersF").child(fuser.getUid());
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Kullanici = dataSnapshot.getValue(User.class);

                TasarimDegistir(Kullanici.getTas_arayuz(),viewHolder,0,true);
                if(chat.getGonderici().equals(Kullanici.getUid())) {

                    TasarimDegistir(Kullanici.getTas_mesaj(),viewHolder,1,false);
                }
                else{
                    DatabaseReference ref2 = FirebaseDatabase.getInstance().getReference("usersF").child(chat.getGonderici());
                    ref2.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            User user = dataSnapshot.getValue(User.class);

                            TasarimDegistir(user.getTas_mesaj(),viewHolder,0,false);
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });



        //Mesaj hangi gün atılmış. Kalıplar halinde.
        if(degisiktarihler.contains(chat)){
            TarihiYaz(viewHolder.hangiGun,chat.getZaman());
            viewHolder.hangiGun.setVisibility(View.VISIBLE);
        }
        else{
            viewHolder.hangiGun.setVisibility(View.GONE);
        }
        // -------------------------------------------------

        if(chat.getDurum().equals("silindi")) {
            viewHolder.show_msj.setVisibility(View.GONE);
            viewHolder.silindi_msj.setVisibility(View.VISIBLE);
        }
        else{
            viewHolder.show_msj.setVisibility(View.VISIBLE);
            viewHolder.silindi_msj.setVisibility(View.GONE);
        }
        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar calendar = Calendar.getInstance();
                calendar.setTimeInMillis(chat.getZaman());
                calendar.setTimeZone(TimeZone.getTimeZone("Turkey"));
                String dateString = calendar.get(Calendar.HOUR_OF_DAY) + ":" + calendar.get(Calendar.MINUTE);

                viewHolder.zaman.setText(dateString);
                if (!zamanGorunurMu) {
                    viewHolder.zaman.setVisibility(View.VISIBLE);
                    zamanGorunurMu = true;
                } else {
                    viewHolder.zaman.setVisibility(View.GONE);
                    zamanGorunurMu = false;
                }
            }
        });
        viewHolder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if(internetVarmi()) {
                    if (chat.getDurum().equals("silinmedi")) { // eğer mesaj silinmediyse
                        if (fuser.getUid().equals(chat.getGonderici())) { //eğer kendi mesajını seçtiysen
                            viewHolder.builder.setItems(viewHolder.secenekler, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    if (which == 0) {
                                        viewHolder.clip = ClipData.newPlainText("Mesaj Kopyalandı", chat.getMesajim());
                                        viewHolder.clipboard.setPrimaryClip(viewHolder.clip);
                                        Toast.makeText(mContext, "Mesaj Kopyalandı", Toast.LENGTH_SHORT).show();
                                    }
                                    if (which == 1) {
                                        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Mesajlar").child(MesajActivity.ozelOda).child(refisimleri.get(i));
                                        ref.addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                Chat chat1 = dataSnapshot.getValue(Chat.class);
                                                if (chat1.getGormek_istemeyen1().equals("")) {
                                                    dataSnapshot.getRef().child("gormek_istemeyen1").setValue(fuser.getUid());
                                                } else if (chat1.getGormek_istemeyen2().equals("")) {
                                                    dataSnapshot.getRef().child("gormek_istemeyen2").setValue(fuser.getUid());
                                                }
                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError databaseError) {

                                            }
                                        });
                                    }
                                    if (which == 2) {
                                        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Mesajlar").child(MesajActivity.ozelOda).child(refisimleri.get(i));
                                        ref.child("durum").setValue("silindi");
                                    }
                                }
                            });
                        } else { // eğer diğeri'nin mesajını seçtiysen.
                            viewHolder.builder.setItems(viewHolder.secenekler2, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    if (which == 0) {
                                        viewHolder.clip = ClipData.newPlainText("Mesaj Kopyalandı", chat.getMesajim());
                                        viewHolder.clipboard.setPrimaryClip(viewHolder.clip);
                                        Toast.makeText(mContext, "Mesaj Kopyalandı", Toast.LENGTH_SHORT).show();
                                    }
                                    if (which == 1) {
                                        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Mesajlar").child(MesajActivity.ozelOda).child(refisimleri.get(i));
                                        ref.addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                Chat chat1 = dataSnapshot.getValue(Chat.class);
                                                if (chat1.getGormek_istemeyen1().equals("")) {
                                                    dataSnapshot.getRef().child("gormek_istemeyen1").setValue(fuser.getUid());
                                                } else if (chat1.getGormek_istemeyen2().equals("")) {
                                                    dataSnapshot.getRef().child("gormek_istemeyen2").setValue(fuser.getUid());
                                                }
                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError databaseError) {

                                            }
                                        });
                                    }
                                }
                            });
                        }
                        AlertDialog dialog = viewHolder.builder.create();
                        dialog.show();

                    }
                }
                return false;
            }
        });
        /*if(i+1 == mChat.size()){
            if(chat.getGonderici().equals(fuser.getUid())) {
                viewHolder.goruldu.setVisibility(View.VISIBLE);
                if (chat.isGoruldumu()) {
                    viewHolder.goruldu.setText("Görüldü");
                } else {
                    viewHolder.goruldu.setText("Görülmedi");
                }
            }
        }else{
            viewHolder.goruldu.setVisibility(View.GONE);
        }*/



       OkunduYap(refisimleri.get(i),chat.getGonderici());
    }

    private void OkunduYap(String ref, String mesajiAtan) {
        if(!mesajiAtan.equals(fuser.getUid())){
            DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Mesajlar").child(MesajActivity.ozelOda).child(ref);
            reference.child("goruldumu").setValue(true);
        }
    }

    public void TasarimDegistir(String tasDegeri, ViewHolder viewHolder, int benimMi,boolean arayuzMu) {
        GradientDrawable gradientBackground =  new GradientDrawable();
        GradientDrawable tarihBackground =  new GradientDrawable();

        tarihBackground.setOrientation(GradientDrawable.Orientation.LEFT_RIGHT);
        tarihBackground.setCornerRadius(50);

        gradientBackground.setOrientation(GradientDrawable.Orientation.LEFT_RIGHT);
        if(!arayuzMu) {
            if (benimMi == 0)
                gradientBackground.setCornerRadii(new float[]{50, 50, 50, 50, 50, 50, 0, 0});
            else
                gradientBackground.setCornerRadii(new float[]{50, 50, 50, 50, 0, 0, 50, 50});
        }
        else
            gradientBackground.setCornerRadii(new float[]{50, 50, 50, 50, 50, 50, 50, 50});
        int renk1 = 0,renk2 = 0,t1start = 0,t2start = 0,t1end = 0,t2end = 0,orta = 0;

        renk1 = TasarimRenginiGetir.RengiGetir(mContext,"renk1",tasDegeri);
        renk2 = TasarimRenginiGetir.RengiGetir(mContext,"renk2",tasDegeri);
        t1start = TasarimRenginiGetir.RengiGetir(mContext,"t1start",tasDegeri);
        t2start = TasarimRenginiGetir.RengiGetir(mContext,"t2start",tasDegeri);
        t1end = TasarimRenginiGetir.RengiGetir(mContext,"t1end",tasDegeri);
        t2end = TasarimRenginiGetir.RengiGetir(mContext,"t2end",tasDegeri);
        orta = TasarimRenginiGetir.RengiGetir(mContext,"orta",tasDegeri);

        gradientBackground.setColors(new int[]{
                renk1,
                orta,
                renk2
        });
        tarihBackground.setColors(new int[]{
                renk1,
                orta,
                renk2
        });

        gradientDrawable = gradientBackground;

        if (arayuzMu)
            viewHolder.hangiGun.setBackground(tarihBackground);
        else {
            viewHolder.show_msj.setBackground(gradientDrawable);
        }
    }
    @Override
    public int getItemCount() {
        return mChat.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        public TextView show_msj;
        public TextView goruldu;
        public TextView zaman;
        public TextView hangiGun;
        public TextView silindi_msj;
        ClipboardManager clipboard = (ClipboardManager) mContext.getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip;
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        String[] secenekler = {"Mesajı Kopyala", "Mesajı Benden Sil", "Mesajı Herkesten Sil"};
        String[] secenekler2 = {"Mesajı Kopyala","Mesajı Benden Sil"};


        public ViewHolder(View itemView){
            super(itemView);

            silindi_msj = itemView.findViewById(R.id.bu_mesaj_silindi);
            show_msj = itemView.findViewById(R.id.mesaj_goster);
            goruldu = itemView.findViewById(R.id.goruldu);
            zaman = itemView.findViewById(R.id.mesajinsaati);
            hangiGun = itemView.findViewById(R.id.ne_zaman_atilmis);
        }
    }

    @Override
    public int getItemViewType(int position){
        fuser = FirebaseAuth.getInstance().getCurrentUser();
        if(mChat.get(position).getAlici().equals(fuser.getUid())){
            return MESAJ_TIP_SOL;
        }else{
            return MESAJ_TIP_SAG;
        }
    }
    public static String ayOnceTarih() {
        Date date = new Date();
        String strDateFormat = "MM/dd/yy";
        DateFormat dateFormat = new SimpleDateFormat(strDateFormat);
        String formattedDate= dateFormat.format(date);
        return formattedDate;
    }
    public static String bugunMu() {
        Calendar cal = Calendar.getInstance();
        cal.setTimeZone(TimeZone.getTimeZone("Turkey"));
        Date date=cal.getTime();
        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yy");
        String formattedDate=dateFormat.format(date);
        return formattedDate;
    }
    public static String dunMu() {
        Calendar cal = Calendar.getInstance();
        cal.setTimeZone(TimeZone.getTimeZone("Turkey"));
        Date date=cal.getTime();
        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yy");
        DateFormat dateFormat1 = new SimpleDateFormat("dd");
        String formattedDate=dateFormat.format(date);
        String formattedDate2=dateFormat1.format(date);
        return formattedDate2;
    }
}
