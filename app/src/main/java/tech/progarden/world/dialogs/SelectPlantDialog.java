package tech.progarden.world.dialogs;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import tech.progarden.world.R;

/**
 * Created by 1 on 2/11/2016.
 */
public class SelectPlantDialog extends DialogFragment {
    SharedPreferences pref;
    OnDialogDataPass dataPasser;


    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.pick_plant_title);
        CharSequence[] plants;

        pref = PreferenceManager.getDefaultSharedPreferences(getActivity().getApplicationContext());
        String plants_str = pref.getString(getString(R.string.KEY_PLANTS), "");

        try {

            JSONArray jsonArray = new JSONArray(plants_str);

            plants = new CharSequence[jsonArray.length()];
            final String[] plantIds = new String[jsonArray.length()];
            JSONObject jObjPlant;
            for (int i = 0; i < jsonArray.length(); i++) {
                jObjPlant = jsonArray.getJSONObject(i);
                plants[i] = jObjPlant.getString("ImeKulture");
                plantIds[i] = String.valueOf(jObjPlant.getInt("IdKulture"));
            }

            builder.setItems(plants, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    // The 'which' argument contains the index position
                    // of the selected item
                    dataPasser.onDialogDataPass(plantIds[which]);

                }
            });

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return builder.create();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        dataPasser = (OnDialogDataPass) activity;
    }


    public interface OnDialogDataPass {
        void onDialogDataPass(String data);
    }
}
