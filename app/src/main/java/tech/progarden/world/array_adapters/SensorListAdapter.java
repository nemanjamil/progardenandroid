package tech.progarden.world.array_adapters;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.ContextCompat;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import tech.progarden.world.MainTenance;
import tech.progarden.world.R;

/**
 * Created by milan on 1/27/2016.
 */
public class SensorListAdapter extends ArrayAdapter<JSONObject> {
    private final Context context;
    private final JSONObject[] values;
    private SparseBooleanArray mSelectedItemsIds;



    public SensorListAdapter(Context context, JSONObject[] values) {
        super(context, R.layout.sensor_list_row_layout, values);
        this.context = context;
        this.values = values;
        mSelectedItemsIds = new SparseBooleanArray();
        // 0 nameValuePairs 1 key SenzorSifra
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {


        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            /*TODO use view recycling for smooth scrolling...*/
        View rowView = inflater.inflate(R.layout.sensor_list_row_layout, parent, false);
        TextView textView = (TextView) rowView.findViewById(R.id.firstLine);
        TextView textView_desc = (TextView) rowView.findViewById(R.id.secondLine);
        ImageView imageView = (ImageView) rowView.findViewById(R.id.icon);
        ImageView imgMainTenance = (ImageView) rowView.findViewById(R.id.imgMainTenance);

        imgMainTenance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(new Intent(getContext(), MainTenance.class));
                try {
                    intent.putExtra("SensorMAC", values[position].getString("SenzorSifra"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                context.startActivity(intent);

                //context.startActivity(new Intent(getContext(), MainTenance.class));
            }
        });

        try {
            textView.setText(values[position].getString("SenzorNaziv"));
            textView_desc.setText(values[position].getString("SenzorSifra"));
            /*TODO figure out better way to pair name with icon */
            if (values[position].getString("ImeKulture").equals("Paradajz"))
                imageView.setImageResource(R.drawable.paradajz);
            if (values[position].getString("ImeKulture").equals("Paprika"))
                imageView.setImageResource(R.drawable.paprika);
            if (values[position].getString("ImeKulture").equals("Boranija"))
                imageView.setImageResource(R.drawable.boranija);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        rowView.setBackgroundColor(mSelectedItemsIds.get(position) ? ContextCompat.getColor(context, R.color.sensor_list_row_sel_color)
                : ContextCompat.getColor(context, R.color.sensor_list_row_bg_color));
        return rowView;
    }

    public void toggleSelection(int position) {
        selectView(position, !mSelectedItemsIds.get(position));
    }

    public void removeSelection() {
        mSelectedItemsIds = new SparseBooleanArray();
        notifyDataSetChanged();
    }

    public void selectView(int position, boolean value) {
        if (value)
            mSelectedItemsIds.put(position, value);
        else
            mSelectedItemsIds.delete(position);

        notifyDataSetChanged();
    }

    public int getSelectedCount() {
        return mSelectedItemsIds.size();
    }

    public String getSensorMAC(int position) {
        String mac = "";
        try {
            mac = values[position].getString("SenzorSifra");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return mac;
    }

    public String getSenzorId(int position) {
        String id = "";
        try {
            id = values[position].getString("IdListaSenzora");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return id;
    }

    public SparseBooleanArray getSelectedIds() {
        return mSelectedItemsIds;
    }
}

