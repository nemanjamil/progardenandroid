package tech.progarden.world.web_requests;

import android.app.Activity;
import android.content.Context;
import android.util.SparseBooleanArray;
import android.widget.ListAdapter;
import android.widget.ListView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import tech.progarden.world.R;
import tech.progarden.world.app.AppConfig;
import tech.progarden.world.app.AppController;
import tech.progarden.world.array_adapters.SensorListAdapter;
import tech.progarden.world.callback_interfaces.WebRequestCallbackInterface;
import tech.progarden.world.dialogs.ProgressDialogCustom;

/**
 * Created by milan on 2/11/2016.
 */

public class UpdateSensorRequest {
    private Context context;
    private ProgressDialogCustom progressDialog;
    private WebRequestCallbackInterface webRequestCallbackInterface;
    private SensorListAdapter customArrayAdapter;
    private JSONObject[] jsonObjects;
    private ListView listView;

    public UpdateSensorRequest(Activity context) {
        this.context = context;
        progressDialog = new ProgressDialogCustom(context);
        progressDialog.setCancelable(false);
        webRequestCallbackInterface = null;
        listView = (ListView) context.findViewById(R.id.listView);
    }

    public void setCallbackListener(WebRequestCallbackInterface listener) {
        this.webRequestCallbackInterface = listener;
    }

    /**
     * function to pull sensor list form web server
     */
    private void updateSensor(final String uid, final String mac, final String kind) {
        // Tag used to cancel the request
        String tag_string_req = "req_update_sensor";
        progressDialog.showDialog(context.getString(R.string.progress_update_sensor));

        String url = String.format(AppConfig.URL_UPDATE_SENSOR_GET, uid, mac, kind);

        StringRequest strReq = new StringRequest(Request.Method.GET,
                url, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                progressDialog.hideDialog();
                if (response != null) {
                    AppConfig.logDebug("RESPONSE", "Nije null");
                    AppConfig.logDebug("RESPONSE", response);
                } else {
                    AppConfig.logDebug("RESPONSE", "NULL RESPONSE");
                }

                try {

                    JSONObject jObj = new JSONObject(response);

                    boolean success = jObj.getBoolean("success");
                    jsonObjects = new JSONObject[1];
                    jsonObjects[0] = jObj;

                    if (success) {
                        webRequestCallbackInterface.webRequestSuccess(true, jsonObjects);

                    } else {

                        webRequestCallbackInterface.webRequestSuccess(false, jsonObjects);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                progressDialog.hideDialog();
                webRequestCallbackInterface.webRequestError(error.getMessage());
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                // Post params
                Map<String, String> params = new HashMap<String, String>();
                params.put("action", "izmeniPodatkeSenzorId");
                params.put("id", uid);
                params.put("string", mac);
                params.put("br", kind);
                return params;
            }

        };

        strReq.setRetryPolicy(new DefaultRetryPolicy(AppConfig.DEFAULT_TIMEOUT_MS, AppConfig.DEFAULT_MAX_RETRIES, AppConfig.DEFAULT_BACKOFF_MULT));
        // Adding request to  queue
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);

    }

    public void updateSensorListRequest(final String uid, final String kind) {

        //Loop through sensor list and delete all selected sensors
        ListAdapter la = listView.getAdapter();
        SensorListAdapter sla = (SensorListAdapter) la;
        SparseBooleanArray selectedIds = sla.getSelectedIds();
        int listCount = listView.getCount();
        AppConfig.logInfo("listCount", String.valueOf(listCount));
        for (int i = 0; i < listCount; i++) {
            if (selectedIds.get(i)) {
                updateSensor(uid, sla.getSensorMAC(i), kind);
            }
        }
    }
}
