package tech.progarden.world.wifi;

import android.content.Context;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import tech.progarden.world.R;

public class CustomNetworkAdapter extends BaseAdapter {

    private Context mContext;
    private List<ScanResult> listaList = new ArrayList<>();


    public CustomNetworkAdapter(Context applicationContext, ArrayList<ScanResult> listaWifiNetWorks) {
        mContext = applicationContext;
        listaList = listaWifiNetWorks;
    }

    @Override
    public int getCount() {
        return listaList.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        View listItem = convertView;
        if(listItem == null)
            listItem = LayoutInflater.from(mContext).inflate(R.layout.sensor_ap_list_row_layout,parent,false);

        //LayoutInflater inflater = LayoutInflater.from(mContext);
        //convertView = inflater.inflate(R.layout.sensor_ap_list_row_layout, parent, false);


        final ScanResult resultp = listaList.get(position);

        TextView firstLine = (TextView) listItem.findViewById(R.id.firstLine);
        firstLine.setText(resultp.SSID);

        TextView secondLine = (TextView) listItem.findViewById(R.id.secondLine);
        secondLine.setText(resultp.BSSID);

        ProgressBar progressBar = (ProgressBar) listItem.findViewById(R.id.progressBar);
        //progressBar.getProgressDrawable().setColorFilter(Integer.parseInt("0xFF444444"), PorterDuff.Mode.SRC_IN);

        progressBar.setMax(5);
        int level = WifiManager.calculateSignalLevel(resultp.level, 5);
        progressBar.setProgress(level);

        /*listItem.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                //Log.d("testmiki bsid",resultp.BSSID);
                String ssid_s = resultp.SSID;
                String bsid_s = resultp.BSSID;


            }
        });
*/
        return listItem;
    }


}
