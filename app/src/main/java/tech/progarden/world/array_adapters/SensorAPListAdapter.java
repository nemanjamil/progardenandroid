package tech.progarden.world.array_adapters;

import android.content.Context;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.List;

import tech.progarden.world.R;

/**
 * Created by milan on 1/31/2016.
 */
public class SensorAPListAdapter extends ArrayAdapter<ScanResult> {
    private final Context context;
    private final List<ScanResult> objects;

    public SensorAPListAdapter(Context context, List<ScanResult> objects) {
        super(context, R.layout.sensor_ap_list_row_layout, objects);
        this.objects = objects;
        this.context = context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            /*TODO use view recycling for smooth scrolling...*/
        View rowView = inflater.inflate(R.layout.sensor_ap_list_row_layout, parent, false);
        TextView textView = (TextView) rowView.findViewById(R.id.firstLine);
        TextView textView_desc = (TextView) rowView.findViewById(R.id.secondLine);
        ImageView imageView = (ImageView) rowView.findViewById(R.id.icon);
        ProgressBar progressBar = (ProgressBar) rowView.findViewById(R.id.progressBar);

        textView.setText(objects.get(position).SSID);
        textView_desc.setText(objects.get(position).BSSID);
        //imageView.setImageResource(R.mipmap.tomato6);
        progressBar.setMax(5);
        int level = WifiManager.calculateSignalLevel(objects.get(position).level, 5);
        /*TODO change bar color depending on signal strength*/
        if (level >= 4) {

        } else if (level >= 2) {

        } else {

        }
        progressBar.setProgress(level);


        rowView.setBackgroundColor(ContextCompat.getColor(context, R.color.sensor_list_row_bg_color));
        return rowView;
    }
}
