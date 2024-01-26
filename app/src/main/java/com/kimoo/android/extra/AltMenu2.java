package com.kimoo.android.extra;

import android.content.Context;
import android.os.Bundle;
import androidx.annotation.NonNull;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Spinner;
import android.widget.TextView;

import com.kimoo.android.BegenenlerActivity;
import com.kimoo.android.R;
import com.kimoo.android.TaraActivity;
import com.kimoo.android.fragments.Favorilerim;

public class AltMenu2 extends BottomSheetDialog {
    Spinner spinAlfa,spinYas,spinBegeni;
    CheckBox karilastiklarimEnOnce;
    public static int adegAlf,adegYas,adegBeg,adegKar,adegAlfb,adegYasb,adegBegb,adegKarb,adegAlff,adegYasf,adegBegf,adegKarf,adegAlfa,adegYasa,adegBega,adegKara;
    public int degAlf,degYas,degBeg,degKar,degAlfb,degYasb,degBegb,degKarb,degAlff,degYasf,degBegf,degKarf,degAlfa,degYasa,degBega,degKara;
    Button sirala;
    TextView yazi,yazi3;

    public AltMenu2(@NonNull Context context) {
        super(context);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        spinAlfa = findViewById(R.id.sirala_alfa);
        spinYas = findViewById(R.id.sirala_yas);
        spinBegeni = findViewById(R.id.sirala_begeni);
        sirala = findViewById(R.id.sirala_sirala_btn);
        yazi = findViewById(R.id.sirala_yazi4);
        yazi3 = findViewById(R.id.sirala_yazi3);
        karilastiklarimEnOnce = findViewById(R.id.sirala_karsilasilanlar);

        if(TaraActivity.gorduklerimde){
            yazi.setVisibility(View.VISIBLE);
            karilastiklarimEnOnce.setVisibility(View.VISIBLE);
        }else{
            yazi.setVisibility(View.GONE);
            karilastiklarimEnOnce.setVisibility(View.GONE);
        }
        if(TaraActivity.tarada){
            yazi3.setText("Beğeni Sayısı");
            ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getContext(), R.array.spinn_begeni, android.R.layout.simple_spinner_item);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinBegeni.setAdapter(adapter);
        }else{
            yazi3.setText("Beğenme Sayısı");
            ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getContext(), R.array.spinn_begenme, android.R.layout.simple_spinner_item);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinBegeni.setAdapter(adapter);
        }
        BilgileriGetir();
        karilastiklarimEnOnce.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked) {
                    degKar = 1;
                    degYas = 0;
                    degBeg = 0;
                    degAlf = 0;
                    DigeriniIptalEt();
                }
                else {
                    if(TaraActivity.gorduklerimde)
                        degKar = 0;
                }
            }
        });
        spinAlfa.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(TaraActivity.tarada) {
                    if(TaraActivity.gorduklerimde)
                        degAlf = position;
                    else if(TaraActivity.favorilerde)
                        degAlff = position;
                    else
                        degAlfa = position;
                }
                else
                    degAlfb = position;
                if(position != 0){
                    if(TaraActivity.tarada) {
                        if(TaraActivity.gorduklerimde) {
                            degYas = 0;
                            degBeg = 0;
                            degKar = 0;
                        }else if(TaraActivity.favorilerde){
                            degYasf = 0;
                            degBegf = 0;
                            degKarf = 0;
                        }else{
                            degYasa = 0;
                            degBega = 0;
                            degKara = 0;
                        }
                    }else {
                        degYasb = 0;
                        degBegb = 0;
                        degKarb = 0;
                    }
                    DigeriniIptalEt();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        spinBegeni.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(TaraActivity.tarada) {
                    if(TaraActivity.gorduklerimde)
                        degBeg = position;
                    else if(TaraActivity.favorilerde)
                        degBegf = position;
                    else
                        degBega = position;
                }
                else
                    degBegb = position;
                if(position != 0){
                    if(TaraActivity.tarada) {
                        if (TaraActivity.gorduklerimde) {
                            degAlf = 0;
                            degYas = 0;
                            degKar = 0;
                        }else if(TaraActivity.favorilerde){
                            degAlff = 0;
                            degYasf = 0;
                            degKarf = 0;
                        }else{
                            degAlfa = 0;
                            degYasa = 0;
                            degKara = 0;
                        }
                    }else {
                        degAlfb = 0;
                        degYasb = 0;
                        degKarb = 0;
                    }
                    DigeriniIptalEt();
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        spinYas.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(TaraActivity.tarada) {
                    if(TaraActivity.gorduklerimde)
                        degYas = position;
                    else if(TaraActivity.favorilerde)
                        degYasf = position;
                    else
                        degYasa = position;
                }
                else
                    degYasb = position;
                if(position != 0){
                    if(TaraActivity.tarada) {
                        if (TaraActivity.gorduklerimde) {
                            degAlf = 0;
                            degBeg = 0;
                            degKar = 0;
                        }else if(TaraActivity.favorilerde){
                            degAlff = 0;
                            degBegf = 0;
                            degKarf = 0;
                        }else{
                            degAlfa = 0;
                            degBega = 0;
                            degKara = 0;
                        }
                    }else {
                        degAlfb = 0;
                        degBegb = 0;
                        degKarb = 0;
                    }
                    DigeriniIptalEt();
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        sirala.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!TaraActivity.tarada) {
                    adegAlfb = degAlfb;
                    adegBegb = degBegb;
                    adegKarb = degKarb;
                    adegYasb = degYasb;
                    //BegenenlerActivity.CinFiltre(AltMenu.degCinb, AltMenu.degPremb, AltMenu.degYasCokb, AltMenu.degYasAzb);
                }else {
                    if(TaraActivity.gorduklerimde) {

                        adegAlf = degAlf;
                        adegBeg = degBeg;
                        adegKar = degKar;
                        adegYas = degYas;
                        //TumGorduklerim.CinFiltre(AltMenu.degCin, AltMenu.degPrem, AltMenu.degYasCok, AltMenu.degYasAz);
                    }
                    else if(TaraActivity.favorilerde){
                        adegAlff = degAlff;
                        adegBegf = degBegf;
                        adegKarf = degKarf;
                        adegYasf = degYasf;
                        Favorilerim.CinFiltre(AltMenu.degCinf, AltMenu.degPremf, AltMenu.degYasCokf, AltMenu.degYasAzf);
                    }else{
                        adegAlfa = degAlfa;
                        adegBega = degBega;
                        adegKara = degKara;
                        adegYasa = degYasa;
                       // SuanBulunanlar.CinFiltre(AltMenu.degCina, AltMenu.degPrema, AltMenu.degYasCoka, AltMenu.degYasAza);
                    }
                }
                dismiss();
            }
        });
    }

    private void DigeriniIptalEt() {
        if (!BegenenlerActivity.begenilerde) {
            if (TaraActivity.gorduklerimde) {
                spinAlfa.setSelection(degAlf);
                if (degYas != 0)
                    spinYas.setSelection(degYas);
                else
                    spinYas.setSelection(0);


                if (degBeg != 0)
                    spinBegeni.setSelection(degBeg);
                else
                    spinBegeni.setSelection(0);

                if(degKar == 0 && degKar == 2)
                    karilastiklarimEnOnce.setChecked(false);
                else if(degKar == 1)
                    karilastiklarimEnOnce.setChecked(true);

            }else if(TaraActivity.favorilerde){
                spinAlfa.setSelection(degAlff);
                if (degYasf != 0)
                    spinYas.setSelection(degYasf);
                else
                    spinYas.setSelection(0);


                if (degBegf != 0)
                    spinBegeni.setSelection(degBegf);
                else
                    spinBegeni.setSelection(0);

            }else{
                spinAlfa.setSelection(degAlfa);
                if (degYasa != 0)
                    spinYas.setSelection(degYasa);
                else
                    spinYas.setSelection(0);


                if (degBega != 0)
                    spinBegeni.setSelection(degBega);
                else
                    spinBegeni.setSelection(0);


            }
        }else{
            spinAlfa.setSelection(degAlfb);
            if (degYasb != 0)
                spinYas.setSelection(degYasb);
            else
                spinYas.setSelection(0);


            if (degBegb != 0)
                spinBegeni.setSelection(degBegb);
            else
                spinBegeni.setSelection(0);

        }
    }

    private void BilgileriGetir() {

        if (!BegenenlerActivity.begenilerde) {
            if(TaraActivity.gorduklerimde) {
                degAlf = adegAlf;
                degBeg = adegBeg;
                degKar = adegKar;
                degYas = adegYas;
                if(degKar == 0){
                    karilastiklarimEnOnce.setChecked(false);
                }else{
                    karilastiklarimEnOnce.setChecked(true);
                }
            }else if(TaraActivity.favorilerde){
                degAlff = adegAlff;
                degBegf = adegBegf;
                degKarf = adegKarf;
                degYasf = adegYasf;
            }else{
                degAlfa = adegAlfa;
                degBega = adegBega;
                degKara = adegKara;
                degYasa = adegYasa;
            }
        } else {
            degAlfb = adegAlfb;
            degBegb = adegBegb;
            degKarb = adegKarb;
            degYasb = adegYasb;

        }
        DigeriniIptalEt();
    }
}
