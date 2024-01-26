package com.kimoo.android;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.billingclient.api.AcknowledgePurchaseParams;
import com.android.billingclient.api.AcknowledgePurchaseResponseListener;
import com.android.billingclient.api.BillingClient;
import com.android.billingclient.api.BillingClientStateListener;
import com.android.billingclient.api.BillingFlowParams;
import com.android.billingclient.api.BillingResult;
import com.android.billingclient.api.ConsumeParams;
import com.android.billingclient.api.ConsumeResponseListener;
import com.android.billingclient.api.Purchase;
import com.android.billingclient.api.PurchasesResponseListener;
import com.android.billingclient.api.PurchasesUpdatedListener;
import com.android.billingclient.api.SkuDetails;
import com.android.billingclient.api.SkuDetailsParams;
import com.android.billingclient.api.SkuDetailsResponseListener;
import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.OnUserEarnedRewardListener;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.gms.ads.rewarded.RewardItem;
import com.google.android.gms.ads.rewarded.RewardedAd;
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.kimoo.android.bildirimler.Data;
import com.kimoo.android.extra.PreBilgiBottomSheet;
import com.kimoo.android.extra.TasarimRenginiGetir;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class MarketActivity extends AppCompatActivity {
    private static final String AD_UNIT_ID = "ca-app-pub-1691490115613299/8327497916";
    private static final String APP_ID = "ca-app-pub-1691490115613299~6850764715";
    private Button premiumBilgi,butonOmur,buton0,buton1,buton2,buton3,buton4;
    private RelativeLayout background;
    private BillingClient billingClient;
    private RewardedAd mRewardedAd;
    private FirebaseUser fuser;
    private TextView sureyazi;
    private int satinAlinanDeger;
    private DatabaseReference reference;
    private DataSnapshot asilSnapShot;
    private int ortaRenk;
    private String kosulYazisi = null;
    private boolean butonTiklandiMi = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_market);

        StatuBarAyarla();

        fuser = FirebaseAuth.getInstance().getCurrentUser();

        premiumBilgi = findViewById(R.id.butonInfo);
        butonOmur = findViewById(R.id.butonOmur);
        butonOmur.setClickable(false);
        butonOmur.setEnabled(false);
        buton0 = findViewById(R.id.buton0);
        buton1 = findViewById(R.id.buton1);
        buton2 = findViewById(R.id.buton2);
        buton3 = findViewById(R.id.buton3);
        buton4 = findViewById(R.id.buton4);
        sureyazi = findViewById(R.id.sureyazi);
        background = findViewById(R.id.background);

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitleTextColor(getResources().getColor(R.color.colorPrimary));
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setElevation(0);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


        reference = FirebaseDatabase.getInstance().getReference("usersF").child(fuser.getUid());
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                asilSnapShot = dataSnapshot;
                FirebaseDatabase.getInstance().getReference("Sistem").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot sisDs) {
                        kosulYazisi = sisDs.child("yazilar").child("kullanim_sartlari").getValue(String.class);
                        Long hedefZaman = sisDs.child("tekliflerin_suresi").getValue(Long.class);
                        Long suan = dataSnapshot.child("suan").getValue(Long.class);
                        final long[] asilzaman = {hedefZaman - suan};
                        long gun = asilzaman[0] / 86400000;
                        if(asilzaman[0] > 0) {
                            Timer myTimer = new Timer();
                            myTimer.schedule(new TimerTask() {
                                @Override
                                public void run() {
                                    runOnUiThread(new Runnable(){

                                        @Override
                                        public void run(){
                                            asilzaman[0] -= 1000;
                                            long saat = (asilzaman[0] / 3600000) - ((asilzaman[0] / 86400000) * 24);
                                            long dakika = (asilzaman[0] / 60000) - ((asilzaman[0] / 3600000) * 60);
                                            long saniye = (asilzaman[0] / 1000) - ((asilzaman[0] / 60000) * 60);

                                            sureyazi.setText("Tekliflerin geçerlilik süresi: " + gun + " : " + saat + " : " + dakika + " : " + saniye);
                                        }
                                    });
                                }

                            }, 0, 1000);
                        }
                        else
                            sureyazi.setText("Çok yakında...");
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
                VideoReklamiYukle();
            }
        });

        SharedPreferences tas_shared = getSharedPreferences("TasarimBilgileri",MODE_PRIVATE);
        TasarimDegistir(tas_shared.getString("tasarim_arayuz","1"));

        premiumBilgi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!butonTiklandiMi) {
                    butonTiklandiMi = true;
                    PreBilgiBottomSheet altMenu = new PreBilgiBottomSheet(MarketActivity.this);
                    View sheetView = getLayoutInflater().inflate(R.layout.uyelik__hakkinda, null);
                    TextView bilgi = sheetView.findViewById(R.id.bilgi);
                    bilgi.setMovementMethod(new ScrollingMovementMethod());
                    altMenu.setContentView(sheetView);
                    altMenu.show();
                }
            }
        });
        buton0.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!butonTiklandiMi) {
                    butonTiklandiMi = true;
                    VideoReklamiGoster();
                }
            }
        });
        billingClient = BillingClient.newBuilder(MarketActivity.this)
                .enablePendingPurchases()
                .setListener(new PurchasesUpdatedListener() {
                    @Override
                    public void onPurchasesUpdated(@NonNull BillingResult billingResult, @Nullable List<Purchase> list) {
                        if(list != null)
                        for(Purchase purchase : list) {
                            if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK) {
                                if (purchase.getPurchaseState() == Purchase.PurchaseState.PURCHASED && purchase != null && !purchase.isAcknowledged()) {
                                    SatisiDogrula(purchase);
                                    reference.addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                            if (satinAlinanDeger != 99) {

                                                // GelirBilgisiEkle(dataSnapshot.getKey(),"kp_",satinAlinanDeger,satinAlinanDeger);

                                            } else {
                                                butonOmur.setClickable(false);
                                                butonOmur.setEnabled(false);
                                                butonOmur.setText("Satın Aldınız");
                                                butonOmur.setOnClickListener(new View.OnClickListener() {
                                                    @Override
                                                    public void onClick(View v) {
                                                        Toast.makeText(MarketActivity.this, "Zaten satın aldınız", Toast.LENGTH_SHORT).show();
                                                    }
                                                });
                                                dataSnapshot.child("premium").getRef().setValue("99").addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(@NonNull Void unused) {
                                                        Toast.makeText(MarketActivity.this, "Ömür boyu premium üyelik satın aldınız!", Toast.LENGTH_SHORT).show();
                                                    }
                                                });
                                            }
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError databaseError) {

                                        }
                                    });
                                } else if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.USER_CANCELED) {
                                    Toast.makeText(MarketActivity.this, "İşlem başarısız!", Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(MarketActivity.this, "Bir hata oluştu!", Toast.LENGTH_SHORT).show();
                                }
                            }
                        }
                    }
                }).build();
        odemeSistemineBaglan();
    }

    private void snapshotGuncelle(){
        reference = FirebaseDatabase.getInstance().getReference("usersF").child(fuser.getUid());
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                asilSnapShot = dataSnapshot;
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        billingClient.endConnection();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        billingClient.endConnection();
    }

    @Override
    protected void onPause() {
        super.onPause();
        butonTiklandiMi = false;
    }

    @Override
    protected void onStop() {
        super.onStop();
        butonTiklandiMi = false;
    }

    private void GelirBilgisiEkle(String uid, String id, int sad, int kacKP){
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("usersF").child(uid);
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    int deger = 0;
                    int kpSayisi = 0;
                    if (dataSnapshot.hasChild("gelirlerim")) {
                        for (DataSnapshot ds : dataSnapshot.child("gelirlerim").getChildren()) {
                            deger++;
                            if (ds.getKey().substring(0, id.length()).equals(id)) {
                                kpSayisi++;
                            }
                            if (deger == dataSnapshot.child("gelirlerim").getChildrenCount()) {
                                ref.child("gelirlerim").child(id + sad + "_" + kpSayisi).child("onceki_kp").setValue(dataSnapshot.child("kp").getValue(Integer.class));
                                ref.child("gelirlerim").child(id + sad + "_" + kpSayisi).child("verilen_kp").setValue(kacKP);
                                ref.child("gelirlerim").child(id + sad + "_" + kpSayisi).child("zaman").setValue(ServerValue.TIMESTAMP).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(@NonNull Void unused) {
                                        dataSnapshot.child("kp").getRef().setValue(dataSnapshot.child("kp").getValue(Integer.class) + kacKP).addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(@NonNull Void unused) {
                                                Toast.makeText(MarketActivity.this, "Tebrikler " + kacKP + "KP satın aldınız!", Toast.LENGTH_SHORT).show();
                                            }
                                        });
                                    }
                                });
                            }
                        }
                    }
                    else{
                        ref.child("gelirlerim").child(id+sad+"_0").child("onceki_kp").setValue(dataSnapshot.child("kp").getValue(Integer.class));
                        ref.child("gelirlerim").child(id +sad+"_0").child("verilen_kp").setValue(kacKP);
                        ref.child("gelirlerim").child(id+sad+"_0").child("zaman").setValue(ServerValue.TIMESTAMP).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(@NonNull Void unused) {
                                dataSnapshot.child("kp").getRef().setValue(dataSnapshot.child("kp").getValue(Integer.class) + kacKP).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(@NonNull Void unused) {
                                        Toast.makeText(MarketActivity.this, "Tebrikler " + kacKP + "KP satın aldınız!", Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                        });
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
    private void VideoReklamiYukle() {

        AdRequest adRequest = new AdRequest.Builder().build();

        RewardedAd.load(this, AD_UNIT_ID,
                adRequest, new RewardedAdLoadCallback() {
                    @Override
                    public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                        mRewardedAd = null;
                    }

                    @Override
                    public void onAdLoaded(@NonNull RewardedAd rewardedAd) {
                        mRewardedAd = rewardedAd;
                        buton0.setText("Reklam İzle");
                        mRewardedAd.setFullScreenContentCallback(new FullScreenContentCallback() {
                            @Override
                            public void onAdShowedFullScreenContent() {
                                mRewardedAd = null;
                            }

                            @Override
                            public void onAdFailedToShowFullScreenContent(AdError adError) {

                            }

                            @Override
                            public void onAdDismissedFullScreenContent() {
                                VideoReklamiYukle();
                            }
                        });
                    }
                });
    }

    private void VideoReklamiGoster(){
        if (mRewardedAd != null) {
            Activity activityContext = MarketActivity.this;
            mRewardedAd.show(activityContext, new OnUserEarnedRewardListener() {
                @Override
                public void onUserEarnedReward(@NonNull RewardItem rewardItem) {
                    int odulMiktari = rewardItem.getAmount();
                    String odulTipi = rewardItem.getType();
                    reference.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            dataSnapshot.child("kp").getRef().setValue(dataSnapshot.child("kp").getValue(Integer.class) + odulMiktari).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(@NonNull Void unused) {
                                    Toast.makeText(MarketActivity.this, "" + odulMiktari + "KP kazandınız!", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                }
            });
            butonTiklandiMi = false;
        }
        else {
            buton0.setText("Yükleniyor...");
            Toast.makeText(MarketActivity.this, "Reklam henüz yüklenmedi", Toast.LENGTH_SHORT).show();
            butonTiklandiMi = false;
        }
    }

    private void odemeSistemineBaglan() {
        billingClient.startConnection(new BillingClientStateListener() {
            @Override
            public void onBillingSetupFinished(BillingResult billingResult) {
                if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK) {
                    itemleriGetir("2000kp");
                    itemleriGetir("5000kp");
                    itemleriGetir("10000kp");
                    itemleriGetir("20000kp");
                    itemleriGetir("omur_boyu_premium");
                }
            }

            @Override
            public void onBillingServiceDisconnected() {
                odemeSistemineBaglan();
            }
        });
    }

    private void SatisiDogrula(Purchase purchase){
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Siparisler");
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChild(fuser.getUid())) {
                    DatabaseReference sonRef = dataSnapshot.child(fuser.getUid()).getRef().child("" + (dataSnapshot.child(fuser.getUid()).getChildrenCount() + 1));
                    sonRef.child("token").setValue(purchase.getPurchaseToken());
                    sonRef.child("zaman").setValue(purchase.getPurchaseTime());
                    sonRef.child("onceki_kp").setValue(asilSnapShot.child("kp").getValue(Integer.class));
                    sonRef.child("siparisNo").setValue(purchase.getOrderId());
                    sonRef.child("item").setValue(satinAlinanDeger);
                } else {
                    DatabaseReference sonRef = dataSnapshot.child(fuser.getUid()).getRef().child("1");
                    sonRef.child("token").setValue(purchase.getPurchaseToken());
                    sonRef.child("onceki_kp").setValue(asilSnapShot.child("kp").getValue(Integer.class));
                    sonRef.child("zaman").setValue(purchase.getPurchaseTime());
                    sonRef.child("siparisNo").setValue(purchase.getOrderId());
                    sonRef.child("item").setValue(satinAlinanDeger);
                }
                if (satinAlinanDeger != 99) {
                    ConsumeParams consumeParams = ConsumeParams.newBuilder().setPurchaseToken(purchase.getPurchaseToken()).build();
                    billingClient.consumeAsync(consumeParams, new ConsumeResponseListener() {
                        @Override
                        public void onConsumeResponse(@NonNull BillingResult billingResult, @NonNull String s) {
                            if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK) {
                            }
                        }
                    });
                    GelirBilgisiEkle(fuser.getUid(), "kp_",satinAlinanDeger,satinAlinanDeger);
                }
                snapshotGuncelle();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        /*String requestUrl = "https://us-central1-favdeneme-a7144.cloudfunctions.net/satinAlimlariDogrula?" +
                "token=" + purchase.getPurchaseToken() + "&" +
                "zaman=" + purchase.getPurchaseTime() + "&" +
                "siparisNo=" + purchase.getOrderId();

        StringRequest stringRequest = new StringRequest(
                Request.Method.POST,
                requestUrl,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try{
                            JSONObject purchaseInfoFromServer = new JSONObject(response);
                            if(purchaseInfoFromServer.getBoolean("gecerliMi")){
                                ConsumeParams consumeParams = ConsumeParams.newBuilder().setPurchaseToken(purchase.getPurchaseToken()).build();
                                billingClient.consumeAsync(consumeParams, new ConsumeResponseListener() {
                                    @Override
                                    public void onConsumeResponse(@NonNull BillingResult billingResult, @NonNull String s) {
                                        if(billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK){
                                            Toast.makeText(MarketActivity.this, "ONAYLANDI", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                            }
                        }
                        catch (Exception err){

                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                }
        );
        Volley.newRequestQueue(this).add(stringRequest);*/
    }
    private void itemleriGetir(String id) {
        List<String> skuList = new ArrayList<>();
        skuList.add(id);

        SkuDetailsParams params = SkuDetailsParams
                .newBuilder()
                .setSkusList(skuList)
                .setType(BillingClient.SkuType.INAPP)
                .build();

        billingClient.querySkuDetailsAsync(params, new SkuDetailsResponseListener() {
            @Override
            public void onSkuDetailsResponse(@NonNull BillingResult billingResult, @Nullable List<SkuDetails> list) {
                if(billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK && list != null){
                    SkuDetails sku = list.get(0);

                    if(sku.getSku().equals("2000kp")){
                        buton1.setText(sku.getPrice());
                        buton1.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (!butonTiklandiMi) {
                                    butonTiklandiMi = true;
                                    satinAlinanDeger = 2000;
                                    DialogUyari(sku);
                                }
                            }
                        });
                    }
                    if(sku.getSku().equals("5000kp")){
                        buton2.setText(sku.getPrice());
                        buton2.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (!butonTiklandiMi) {
                                    butonTiklandiMi = true;
                                    satinAlinanDeger = 5000;
                                    DialogUyari(sku);
                                }
                            }
                        });
                    }
                    if(sku.getSku().equals("10000kp")){
                        buton3.setText(sku.getPrice());
                        buton3.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (!butonTiklandiMi) {
                                    butonTiklandiMi = true;
                                    satinAlinanDeger = 10000;
                                    DialogUyari(sku);
                                }
                            }
                        });
                    }
                    if(sku.getSku().equals("20000kp")){
                        buton4.setText(sku.getPrice());
                        buton4.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (!butonTiklandiMi) {
                                    butonTiklandiMi = true;
                                    satinAlinanDeger = 20000;
                                    DialogUyari(sku);
                                }
                            }
                        });
                    }
                    if(sku.getSku().equals("omur_boyu_premium")){
                        reference.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                if(!dataSnapshot.child("premium").getValue(String.class).equals("99")){
                                    butonOmur.setClickable(true);
                                    butonOmur.setEnabled(true);
                                    butonOmur.setText(sku.getPrice());
                                    butonOmur.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            if (!butonTiklandiMi) {
                                                butonTiklandiMi = true;
                                                satinAlinanDeger = 99;
                                                DialogUyari(sku);
                                            }
                                        }
                                    });
                                }
                                else{
                                    butonOmur.setText("Satın Aldınız");
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
                    }
                }
                else{
                    Toast.makeText(MarketActivity.this, "Bir problem oluştu", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void DialogUyari(SkuDetails sku) {
        Dialog dialog = new Dialog(MarketActivity.this);
        dialog.setContentView(R.layout.dialog_dizayn2);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        TextView baslik = dialog.findViewById(R.id.baslik);
        TextView aciklama = dialog.findViewById(R.id.aciklama);

        LinearLayout lay1 = dialog.findViewById(R.id.lay1);
        ProgressBar pbar = dialog.findViewById(R.id.pbar);
        pbar.getIndeterminateDrawable().setColorFilter(ortaRenk,android.graphics.PorterDuff.Mode.MULTIPLY);

        aciklama.setMovementMethod(new ScrollingMovementMethod());
        Button buton = dialog.findViewById(R.id.buton);
        baslik.setText("Satın Almadan Önce");
        aciklama.setText("Ücretli bir içerik almak üzeresiniz. Bu içeriği satın almadan önce lütfen kullanım koşullarımızı tekrar okuyunuz. Kurallarımıza uymadığınız takdirde aldığınız ücretli içeriğe rağmen hesabınızın kapatılabileceğini veya farklı yaptırımlara maruz kalabileceğinizi unutmayın. Bu koşullarda hiçbir geri ödeme talep edemezsiniz. Bu ürünü daha sonra google play üzerinden iade etmek isterseniz aldığınız içeriği kullanmış olsanız bile hesabınızdan geri alınacağını unutmayın. (Kullanım koşullarını okumak için bu metne tıklayınız)");
        aciklama.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                butonTiklandiMi = false;
                if (kosulYazisi != null){
                    Uri uri = Uri.parse(kosulYazisi);
                    Toast.makeText(MarketActivity.this, "Bağlantı Açılıyor...", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                    startActivity(intent);
                }
                else
                    Toast.makeText(MarketActivity.this, "İnternet bağlantınız yavaş lütfen tekrar deneyin.", Toast.LENGTH_SHORT).show();
            }
        });
        buton.setText("Devam");
        buton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                butonTiklandiMi = false;
                billingClient.launchBillingFlow(MarketActivity.this, BillingFlowParams.newBuilder().setSkuDetails(sku).build());
                dialog.dismiss();
            }
        });
        dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                butonTiklandiMi = false;
            }
        });
        dialog.show();
    }

    @Override
    protected void onResume() {
        super.onResume();

        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
                VideoReklamiYukle();
            }
        });

        SharedPreferences tas_shared = getSharedPreferences("TasarimBilgileri",MODE_PRIVATE);
        TasarimDegistir(tas_shared.getString("tasarim_arayuz","1"));

        billingClient.queryPurchasesAsync(BillingClient.SkuType.INAPP, new PurchasesResponseListener() {
            @Override
            public void onQueryPurchasesResponse(@NonNull BillingResult billingResult, @NonNull List<Purchase> list) {
                if(billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK){
                    for (Purchase purchase : list){
                        if(purchase.getPurchaseState() == Purchase.PurchaseState.PURCHASED && !purchase.isAcknowledged()){
                            //SatisiDogrula(purchase);
                        }
                    }
                }
            }
        });

    }
    public void TasarimDegistir(String tasDegeri) {
        GradientDrawable gradient = (GradientDrawable) getResources().getDrawable(R.drawable.gradient);
        gradient.setOrientation(GradientDrawable.Orientation.LEFT_RIGHT);

        GradientDrawable gradientYumusak1 = new GradientDrawable();
        gradientYumusak1.setOrientation(GradientDrawable.Orientation.LEFT_RIGHT);
        gradientYumusak1.setCornerRadius(50);
        GradientDrawable gradientYumusak2 = new GradientDrawable();
        gradientYumusak2.setOrientation(GradientDrawable.Orientation.LEFT_RIGHT);
        gradientYumusak2.setCornerRadius(50);
        GradientDrawable gradientYumusak3 = new GradientDrawable();
        gradientYumusak3.setOrientation(GradientDrawable.Orientation.LEFT_RIGHT);
        gradientYumusak3.setCornerRadius(50);
        GradientDrawable gradientYumusak4 = new GradientDrawable();
        gradientYumusak4.setOrientation(GradientDrawable.Orientation.LEFT_RIGHT);
        gradientYumusak4.setCornerRadius(50);
        GradientDrawable gradientYumusak5 = new GradientDrawable();
        gradientYumusak5.setOrientation(GradientDrawable.Orientation.LEFT_RIGHT);
        gradientYumusak5.setCornerRadius(50);
        GradientDrawable gradientYumusak6 = new GradientDrawable();
        gradientYumusak6.setOrientation(GradientDrawable.Orientation.LEFT_RIGHT);
        gradientYumusak6.setCornerRadius(50);
        GradientDrawable gradientYumusak7 = new GradientDrawable();
        gradientYumusak7.setOrientation(GradientDrawable.Orientation.LEFT_RIGHT);
        gradientYumusak7.setCornerRadius(50);

        int renk1 = 0,renk2 = 0,t1start = 0,t2start = 0,t1end = 0,t2end = 0,orta = 0;

        renk1 = TasarimRenginiGetir.RengiGetir(MarketActivity.this,"renk1",tasDegeri);
        renk2 = TasarimRenginiGetir.RengiGetir(MarketActivity.this,"renk2",tasDegeri);
        t1start = TasarimRenginiGetir.RengiGetir(MarketActivity.this,"t1start",tasDegeri);
        t2start = TasarimRenginiGetir.RengiGetir(MarketActivity.this,"t2start",tasDegeri);
        t1end = TasarimRenginiGetir.RengiGetir(MarketActivity.this,"t1end",tasDegeri);
        t2end = TasarimRenginiGetir.RengiGetir(MarketActivity.this,"t2end",tasDegeri);
        orta = TasarimRenginiGetir.RengiGetir(MarketActivity.this,"orta",tasDegeri);

        gradient.setColors(new int[]{
                renk1,
                orta,
                renk2,
        });
        gradientYumusak1.setColors(new int[]{
                t1start,
                orta,
                t2end,
        });
        gradientYumusak2.setColors(new int[]{
                t1start,
                orta,
                t2end,
        });
        gradientYumusak3.setColors(new int[]{
                t1start,
                orta,
                t2end,
        });
        gradientYumusak4.setColors(new int[]{
                t1start,
                orta,
                t2end,
        });
        gradientYumusak5.setColors(new int[]{
                t1start,
                orta,
                t2end,
        });
        gradientYumusak6.setColors(new int[]{
                t1start,
                orta,
                t2end,
        });
        gradientYumusak7.setColors(new int[]{
                t1start,
                orta,
                t2end,
        });

        ortaRenk = orta;
        buton0.setBackground(gradientYumusak1);
        buton1.setBackground(gradientYumusak2);
        buton2.setBackground(gradientYumusak3);
        buton3.setBackground(gradientYumusak4);
        buton4.setBackground(gradientYumusak5);
        butonOmur.setBackground(gradientYumusak6);
        premiumBilgi.setBackground(gradientYumusak7);
        background.setBackground(gradient);
    }

    private void StatuBarAyarla() {
        if (Build.VERSION.SDK_INT >= 19 && Build.VERSION.SDK_INT < 21) {
            setWindowFlag(this, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, true);
        }
        if (Build.VERSION.SDK_INT >= 19) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        }

        if (Build.VERSION.SDK_INT >= 21) {
            setWindowFlag(this, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, false);
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        }
    }

    public static void setWindowFlag(Activity activity, final int bits, boolean on) {
        Window win = activity.getWindow();
        WindowManager.LayoutParams winParams = win.getAttributes();
        if (on) {
            winParams.flags |= bits;
        } else {
            winParams.flags &= ~bits;
        }
        win.setAttributes(winParams);
    }

}