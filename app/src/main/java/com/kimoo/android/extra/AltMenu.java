package com.kimoo.android.extra;

import android.content.Context;
import android.os.Bundle;
import androidx.annotation.NonNull;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Spinner;

import com.kimoo.android.BegenenlerActivity;
import com.kimoo.android.R;
import com.kimoo.android.TaraActivity;
import com.kimoo.android.fragments.Favorilerim;

public class AltMenu extends BottomSheetDialog {
    Spinner spinner;
    EditText yasAz,yasCok;
    CheckBox premiumMu;
    Button filtrele,sifirla,durdur;
    public static int degCin,degYasAz,degYasCok,degPrem,degCinb,degYasAzb,degYasCokb,degPremb,degCinf,degYasAzf,degYasCokf,degPremf,degCina,degYasAza,degYasCoka,degPrema;
    public int secenekCin, secenekYasAz, secenekYasCok, secenekPrem,secenekCinb, secenekYasAzb, secenekYasCokb, secenekPremb,secenekCinf, secenekYasAzf, secenekYasCokf, secenekPremf,secenekCina, secenekYasAza, secenekYasCoka, secenekPrema;

    public AltMenu(@NonNull Context context) {
        super(context);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        spinner = findViewById(R.id.filtrele_cinsiyet_sec);
        yasAz = findViewById(R.id.filtrele_yas_enaz);
        yasCok = findViewById(R.id.filtrele_yas_encok);
        premiumMu = findViewById(R.id.filtrele_premium);
        filtrele = findViewById(R.id.filtrele_filtrele_btn);
        sifirla = findViewById(R.id.filtrele_sifirla_btn);
        durdur = findViewById(R.id.filtrele_iptal_et_btn);

        BilgileriGetir();
        yasAz.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(!s.toString().equals("")){
                    if(TaraActivity.tarada){
                        if (TaraActivity.favorilerde)
                            secenekYasAzf = Integer.parseInt(s.toString());
                        else if(TaraActivity.gorduklerimde)
                            secenekYasAz = Integer.parseInt(s.toString());
                        else
                            secenekYasAza = Integer.parseInt(s.toString());
                    }
                    else
                        secenekYasAzb = Integer.parseInt(s.toString());
                }else{
                    if(TaraActivity.tarada){
                        if (TaraActivity.favorilerde)
                            secenekYasAzf = 0;
                        else if(TaraActivity.gorduklerimde)
                            secenekYasAz = 0;
                        else
                            secenekYasAza = 0;
                    }
                    else
                        secenekYasAzb = 0;
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        yasCok.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(!s.toString().equals("")){
                    if(TaraActivity.tarada){
                        if (TaraActivity.favorilerde)
                            secenekYasCokf = Integer.parseInt(s.toString());
                        else if(TaraActivity.gorduklerimde)
                            secenekYasCok = Integer.parseInt(s.toString());
                        else
                            secenekYasCoka = Integer.parseInt(s.toString());
                    }
                    else
                        secenekYasCokb = Integer.parseInt(s.toString());
                }else{
                    if(TaraActivity.tarada){
                        if (TaraActivity.favorilerde)
                            secenekYasCokf = 0;
                        else if(TaraActivity.gorduklerimde)
                            secenekYasCok = 0;
                        else
                            secenekYasCoka = 0;
                    }
                    else
                        secenekYasCokb = 0;
                }
                if (s.toString().trim().length() == 0) {
                    if(TaraActivity.tarada){
                        if (TaraActivity.favorilerde)
                            secenekYasCokf = 0;
                        else if(TaraActivity.gorduklerimde)
                            secenekYasCok = 0;
                        else
                            secenekYasCoka = 0;
                    }
                    else
                        secenekYasCokb = 0;
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(TaraActivity.tarada){
                    if (TaraActivity.favorilerde)
                        secenekCinf = position;
                    else if(TaraActivity.gorduklerimde)
                        secenekCin = position;
                    else
                        secenekCina = position;
                }
                else
                    secenekCinb = position;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        premiumMu.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    if(TaraActivity.tarada){
                        if (TaraActivity.favorilerde)
                            secenekPremf = 1;
                        else if(TaraActivity.gorduklerimde)
                            secenekPrem = 1;
                        else
                            secenekPrema = 1;
                    }
                    else
                        secenekPremb = 1;
                }else{
                    if(TaraActivity.tarada){
                        if (TaraActivity.favorilerde)
                            secenekPremf = 0;
                        else if(TaraActivity.gorduklerimde)
                            secenekPrem = 0;
                        else
                            secenekPrema = 0;
                    }
                    else
                        secenekPremb = 0;
                }
            }
        });
        sifirla.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(TaraActivity.tarada) {
                    if (TaraActivity.gorduklerimde) {
                        degPrem = 0;
                        degCin = 0;
                        degYasAz = 0;
                        degYasCok = 0;
                    }else if(TaraActivity.favorilerde){
                        degPremf = 0;
                        degCinf = 0;
                        degYasAzf = 0;
                        degYasCokf = 0;
                    }else{
                        degPrema = 0;
                        degCina = 0;
                        degYasAza = 0;
                        degYasCoka = 0;
                    }
                }else{
                    degPremb = 0;
                    degCinb = 0;
                    degYasAzb = 0;
                    degYasCokb = 0;
                }
                BilgileriGetir();
            }
        });
        durdur.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(TaraActivity.tarada) {
                    if (TaraActivity.gorduklerimde) {
                        degPrem = 0;
                        degCin = 0;
                        degYasAz = 0;
                        degYasCok = 0;
                    }else if(TaraActivity.favorilerde){
                        degPremf = 0;
                        degCinf = 0;
                        degYasAzf = 0;
                        degYasCokf = 0;
                    }else{
                        degPrema = 0;
                        degCina = 0;
                        degYasAza = 0;
                        degYasCoka = 0;
                    }
                }else{
                    degPremb = 0;
                    degCinb = 0;
                    degYasAzb = 0;
                    degYasCokb = 0;
                }
                BilgileriGetir();
                if(!TaraActivity.tarada) {
                    //BegenenlerActivity.CinFiltre(degCinb,degPremb,degYasCokb, degYasAzb);
                }else{
                    if(TaraActivity.gorduklerimde) {
                        //TumGorduklerim.CinFiltre(degCin,degPrem,degYasCok, degYasAz);
                    }
                    else if(TaraActivity.favorilerde)
                        Favorilerim.CinFiltre(degCinf,degPremf,degYasCokf, degYasAzf);
                   /* else
                        SuanBulunanlar.CinFiltre(degCina,degPrema,degYasCoka, degYasAza);*/
                }
                dismiss();
            }
        });
        filtrele.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!TaraActivity.tarada){
                   degCinb = secenekCinb;
                   degPremb = secenekPremb;
                   degYasAzb = secenekYasAzb;
                   degYasCokb = secenekYasCokb;
                   //BegenenlerActivity.CinFiltre(degCinb,degPremb,degYasCokb, degYasAzb);
                   dismiss();
                }else{
                    if(TaraActivity.favorilerde){
                        degCinf = secenekCinf;
                        degPremf = secenekPremf;
                        degYasAzf = secenekYasAzf;
                        degYasCokf = secenekYasCokf;
                        Favorilerim.CinFiltre(degCinf,degPremf,degYasCokf, degYasAzf);
                        dismiss();
                    }
                    else if(TaraActivity.gorduklerimde){
                        degCin = secenekCin;
                        degPrem = secenekPrem;
                        degYasAz = secenekYasAz;
                        degYasCok = secenekYasCok;
                        //TumGorduklerim.CinFiltre(degCin,degPrem,degYasCok, degYasAz);

                        dismiss();
                    }else{
                        degCina = secenekCina;
                        degPrema = secenekPrema;
                        degYasAza = secenekYasAza;
                        degYasCoka = secenekYasCoka;
                      //  SuanBulunanlar.CinFiltre(degCina,degPrema,degYasCoka, degYasAza);

                        dismiss();
                    }
                }

            }
        });
    }
    private void BilgileriGetir(){
        if (!BegenenlerActivity.begenilerde) {
            if(TaraActivity.gorduklerimde) {
                secenekYasAz = degYasAz;
                secenekYasCok = degYasCok;
                secenekPrem = degPrem;
                secenekCin = degCin;
                if (secenekYasCok != 0)
                    yasCok.setText("" + secenekYasCok);
                else
                    yasCok.setText("");
                if (secenekYasAz != 0)
                    yasAz.setText("" + secenekYasAz);
                else
                    yasAz.setText("");

                if (secenekPrem == 0)
                    premiumMu.setChecked(false);
                else
                    premiumMu.setChecked(true);
                spinner.setSelection(secenekCin);
            }else if(TaraActivity.favorilerde){
                secenekYasAzf = degYasAzf;
                secenekYasCokf = degYasCokf;
                secenekPremf = degPremf;
                secenekCinf = degCinf;
                if (secenekYasCokf != 0)
                    yasCok.setText("" + secenekYasCokf);
                else
                    yasCok.setText("");
                if (secenekYasAzf != 0)
                    yasAz.setText("" + secenekYasAzf);
                else
                    yasAz.setText("");

                if (secenekPremf == 0)
                    premiumMu.setChecked(false);
                else
                    premiumMu.setChecked(true);
                spinner.setSelection(secenekCinf);
            }else{
                secenekYasAza = degYasAza;
                secenekYasCoka = degYasCoka;
                secenekPrema = degPrema;
                secenekCina = degCina;
                if (secenekYasCoka != 0)
                    yasCok.setText("" + secenekYasCoka);
                else
                    yasCok.setText("");
                if (secenekYasAza != 0)
                    yasAz.setText("" + secenekYasAza);
                else
                    yasAz.setText("");

                if (secenekPrema == 0)
                    premiumMu.setChecked(false);
                else
                    premiumMu.setChecked(true);
                spinner.setSelection(secenekCina);
            }
        }else{
            secenekYasAzb = degYasAzb;
            secenekYasCokb = degYasCokb;
            secenekPremb = degPremb;
            secenekCinb = degCinb;
            if (secenekYasCokb != 0)
                yasCok.setText("" + secenekYasCokb);
            else
                yasCok.setText("");
            if (secenekYasAzb != 0)
                yasAz.setText("" + secenekYasAzb);
            else
                yasAz.setText("");

            if (secenekPremb == 0)
                premiumMu.setChecked(false);
            else
                premiumMu.setChecked(true);
            spinner.setSelection(secenekCinb);
        }
    }
}
