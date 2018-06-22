package tech.progarden.world.array_adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.List;

import tech.progarden.world.R;
import tech.progarden.world.web_requests.kulture.Kulture;

/**
 * Created by brajan on 10/5/2017.
 */

public class KultureAdapter extends ArrayAdapter<Kulture> {

    private static class ViewHolder {
        private ImageView imgKulura;
        private TextView txtKulturaNaziv;
    }

    public KultureAdapter(@NonNull Context context, List<Kulture> object) {
        super(context, 0, object);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        ViewHolder viewHolder = new ViewHolder();

        if (convertView == null) {
            convertView = LayoutInflater.from(this.getContext()).inflate(R.layout.item_kulture, parent, false);
            SetKultureFields(viewHolder, convertView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        SetKultureValues(viewHolder, position);

        return convertView;
    }

    private void SetKultureValues(ViewHolder viewHolder, int position) {
        Kulture item = getItem(position);

        Glide.with(getContext()).load(item.slikaKulture).into(viewHolder.imgKulura);

        viewHolder.txtKulturaNaziv.setText(item.ImeKulture);
    }

    private void SetKultureFields(ViewHolder viewHolder, View convertView) {
        viewHolder.txtKulturaNaziv = (TextView) convertView.findViewById(R.id.txtKulturaNaziv);
        viewHolder.imgKulura = (ImageView) convertView.findViewById(R.id.imgKulura);
    }
}
