package tech.progarden.world;


import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

// https://medium.com/mindorks/custom-array-adapters-made-easy-b6c4930560dd
class ViewAdapterSensorDetail extends ArrayAdapter<ListaVarijabli> {


    TextView opisnotifikacije_tv, dopodaciideal_tv, odpodaciideal_tv, vrednostsenzor_tv, senzortipime_tv, imekulture_tv, vremesenzor_tv;
    TextView dopodaciideal_crv_donj_tv, dopodaciideal_crv_gor_tv;

    Context context;
    private LayoutInflater inflater;
    ArrayList<ListaVarijabli> data;



    public ViewAdapterSensorDetail(Context context, ArrayList listaVarijablis) {
        super(context, 0 , listaVarijablis);
        data = listaVarijablis;
        this.context = context;
    }

    @Override
    public View getView(final int position, View view, ViewGroup viewGroup) {


        View listItem = view;
        listItem = LayoutInflater.from(context).inflate(R.layout.onerowacitvity_sensor,viewGroup,false);

        ListaVarijabli jedanKomad = data.get(position);


        // GET INFORMATIONS
        String opisnotifikacije_str = jedanKomad.getOpisNotifikacije();

        int odpodaciideal_str = jedanKomad.getOdPodaciIdeal();
        int dopodaciideal_str = jedanKomad.getDoPodaciIdeal();
        int odzutoIdeal_str = jedanKomad.getOdZutoIdeal();
        int dozutoIdeal_str = jedanKomad.getDoZutoIdeal();

        float vrednostsenzor_str = jedanKomad.getVrednostSenzor();
        Log.d("testmiki", String.valueOf(vrednostsenzor_str));
        String senzortipime_str = jedanKomad.getSenzorTipIme();
        //String imekulture_str = resultp.get("ImeKulture");
        String vremesenzor_str = jedanKomad.getVremeSenzor();
        int IdSenNotNotifikacija = jedanKomad.getIdSenNotNotifikacija();



        String colortype;
        switch (IdSenNotNotifikacija) {
            case 1:
                colortype = "Color.GREEN";
                colortype = "#52be80";
                break;
            case 2:
                colortype = "Color.YELLOW";
                colortype = "#f1c40f";
                break;
            case 3:
                colortype = "Color.RED";
                colortype = "#cb4335";
                break;
            default:
                colortype = "Color.GRAY";
                colortype = "#aeb6bf";
                break;
        }

        // definisemo polja
        //opisnotifikacije_tv = (TextView) view.findViewById(R.id.opisnotifikacije_tv);
        //opisnotifikacije_tv.setBackgroundColor(Color.RED);
        //opisnotifikacije_tv.setTextColor(colourtype);
        //String color = Integer.parseInt(String.valueOf(R.color.my_color));


        odpodaciideal_tv = (TextView) listItem.findViewById(R.id.odpodaciideal_tv);
        dopodaciideal_tv = (TextView) listItem.findViewById(R.id.dopodaciideal_tv);

        dopodaciideal_crv_donj_tv = (TextView) listItem.findViewById(R.id.dopodaciideal_crv_donj);
        dopodaciideal_crv_gor_tv = (TextView) listItem.findViewById(R.id.dopodaciideal_crv_gor);

        vrednostsenzor_tv = (TextView) listItem.findViewById(R.id.vrednostsenzor_tv);
        vrednostsenzor_tv.setTypeface(null, Typeface.BOLD);
        vrednostsenzor_tv.setTextColor(Color.parseColor(colortype));
        vrednostsenzor_tv.setTextSize(40);

        senzortipime_tv = (TextView) listItem.findViewById(R.id.senzortipime_tv);
        senzortipime_tv.setTypeface(null, Typeface.BOLD);
        senzortipime_tv.setTextSize(20);

        //imekulture_tv = (TextView) listItem.findViewById(R.id.imekulture_tv);
        vremesenzor_tv = (TextView) listItem.findViewById(R.id.vremesenzor_tv);


        TextView tekstodpodaciideal_tv = (TextView) listItem.findViewById(R.id.tekstodpodaciideal_tv);
        tekstodpodaciideal_tv.setTextSize(10);
        tekstodpodaciideal_tv.setText(context.getResources().getString(R.string.idealnavrenost)+" "+senzortipime_str);

        // upucavamo varijable u polja
        //opisnotifikacije_tv.setText(opisnotifikacije_str);
        odpodaciideal_tv.setText(String.valueOf(odpodaciideal_str));
        odpodaciideal_tv.setTextSize(10);
        dopodaciideal_tv.setText(String.valueOf(dopodaciideal_str));
        dopodaciideal_tv.setTextSize(10);

        dopodaciideal_crv_donj_tv.setText(" < "+String.valueOf(odzutoIdeal_str));
        dopodaciideal_crv_donj_tv.setTextSize(10);

        dopodaciideal_crv_gor_tv.setText(String.valueOf(dozutoIdeal_str)+" > ");
        dopodaciideal_crv_gor_tv.setTextSize(10);

        vrednostsenzor_tv.setText(String.valueOf(vrednostsenzor_str));
        senzortipime_tv.setText(senzortipime_str);
        //imekulture_tv.setText(imekulture_str);



        //2018-05-26 20:15:37
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US);
        Date testDate = null;
        String newFormat = "";
        try {
            testDate = sdf.parse(vremesenzor_str);
            SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/YYYY HH:mm", Locale.US); // MMM dd,yyyy hh:mm a
            newFormat = formatter.format(testDate);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        //System.out.println(".....Date..."+newFormat);


        //SimpleDateFormat dateF = new SimpleDateFormat("EEE, d MMM yyyy", vremesenzor_str); // "HH:mm"
        //String date = dateF.format(Calendar.getInstance().getTime());
        vremesenzor_tv.setText("Date Time : "+newFormat);
        vremesenzor_tv.setTextSize(10);

        listItem.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {

                ListaVarijabli naklik = data.get(position);
                int IdSenzorTip_str = naklik.getIdSenzorTip();

                //Toast.makeText(activity.getApplicationContext(), "Broj : "+IdSenzorTip_str, Toast.LENGTH_SHORT).show();
                // MACADRESA
                // userid
                // kulturaId
                // tipsenzora

//                Intent intent = null;
//                //intent = new Intent(activity, PrikazJednogArtikla.class);
//                Bundle bundle = new Bundle();
//                //bundle.putInt("ArtikalId", Integer.parseInt(resultp.get(this.ArtikalId)));
//                bundle.putString("teer", resultp.get(this.IdSenzorTip_str));
//                intent.putExtras(bundle);
//                //activity.startActivity(intent);

            }
        });


        return listItem;
    }
}

