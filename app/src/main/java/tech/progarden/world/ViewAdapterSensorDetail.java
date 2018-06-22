package tech.progarden.world;


import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

class ViewAdapterSensorDetail extends BaseAdapter {


    TextView opisnotifikacije_tv, dopodaciideal_tv, odpodaciideal_tv, vrednostsenzor_tv, senzortipime_tv, imekulture_tv, vremesenzor_tv;

    private Activity activity;
    private LayoutInflater inflater;
    ArrayList<HashMap<String, String>> data;
    HashMap<String, String> resultp = new HashMap<>();

    public ViewAdapterSensorDetail(Activity activity, ArrayList<HashMap<String, String>> arraylist) {
        this.activity = activity;
        data = arraylist;
    }


    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(final int position, View view, ViewGroup viewGroup) {


        if (inflater == null)
            inflater = (LayoutInflater) activity
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (view == null)
            view = inflater.inflate(R.layout.onerowacitvity_sensor, null);

        resultp = data.get(position);


        // GET INFORMATIONS
        String opisnotifikacije_str = resultp.get("OpisNotifikacije");
        String odpodaciideal_str = resultp.get("OdPodaciIdeal");
        String dopodaciideal_str = resultp.get("DoPodaciIdeal");
        String vrednostsenzor_str = resultp.get("vrednostSenzor");
        String senzortipime_str = resultp.get("senzorTipIme");
        //String imekulture_str = resultp.get("ImeKulture");
        String vremesenzor_str = resultp.get("vremeSenzor");
        int IdSenNotNotifikacija = Integer.parseInt(resultp.get("IdSenNotNotifikacija"));


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


        odpodaciideal_tv = (TextView) view.findViewById(R.id.odpodaciideal_tv);
        dopodaciideal_tv = (TextView) view.findViewById(R.id.dopodaciideal_tv);

        vrednostsenzor_tv = (TextView) view.findViewById(R.id.vrednostsenzor_tv);
        vrednostsenzor_tv.setTypeface(null, Typeface.BOLD);
        vrednostsenzor_tv.setTextColor(Color.parseColor(colortype));
        vrednostsenzor_tv.setTextSize(40);

        senzortipime_tv = (TextView) view.findViewById(R.id.senzortipime_tv);
        senzortipime_tv.setTypeface(null, Typeface.BOLD);
        senzortipime_tv.setTextSize(20);

        //imekulture_tv = (TextView) view.findViewById(R.id.imekulture_tv);
        vremesenzor_tv = (TextView) view.findViewById(R.id.vremesenzor_tv);


        TextView tekstodpodaciideal_tv = (TextView) view.findViewById(R.id.tekstodpodaciideal_tv);
        tekstodpodaciideal_tv.setTextSize(10);
        tekstodpodaciideal_tv.setText(activity.getResources().getString(R.string.idealnavrenost)+" "+senzortipime_str);

        // upucavamo varijable u polja
        //opisnotifikacije_tv.setText(opisnotifikacije_str);
        odpodaciideal_tv.setText(odpodaciideal_str);
        odpodaciideal_tv.setTextSize(10);
        dopodaciideal_tv.setText(dopodaciideal_str);
        dopodaciideal_tv.setTextSize(10);

        vrednostsenzor_tv.setText(vrednostsenzor_str);
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
        vremesenzor_tv.setTextSize(8);

        view.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {

                resultp = data.get(position);
                int IdSenzorTip_str = Integer.parseInt(resultp.get("IdSenzorTip"));

                //Toast.makeText(activity.getApplicationContext(), "Broj : "+IdSenzorTip_str, Toast.LENGTH_SHORT).show();
                // MACADRESA
                // userid
                // kulturaId
                // tipsenzora

               /* Intent intent = null;
                //intent = new Intent(activity, PrikazJednogArtikla.class);
                Bundle bundle = new Bundle();
                //bundle.putInt("ArtikalId", Integer.parseInt(resultp.get(this.ArtikalId)));
                bundle.putString("teer", resultp.get(this.IdSenzorTip_str));
                intent.putExtras(bundle);
                //activity.startActivity(intent);*/

            }
        });


        return view;
    }
}

