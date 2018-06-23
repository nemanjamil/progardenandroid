package tech.progarden.world.web_requests;

import android.app.Activity;
import android.content.Context;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
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
import tech.progarden.world.app.RequestQueueSingleton;
import tech.progarden.world.array_adapters.SensorListAdapter;
import tech.progarden.world.callback_interfaces.WebRequestCallbackInterface;
import tech.progarden.world.dialogs.ProgressDialogCustom;

/**
 * Created by milan on 1/30/2016.
 */
public class AddSensorRequest {
    private Context context;
    private ProgressDialogCustom progressDialog;
    private WebRequestCallbackInterface webRequestCallbackInterface;
    private SensorListAdapter customArrayAdapter;
    private JSONObject[] jsonObjects;

    //ListView listView;

    public AddSensorRequest(Activity context) {
        this.context = context;
        progressDialog = new ProgressDialogCustom(context);
        progressDialog.setCancelable(false);
        webRequestCallbackInterface = null;
        //listView = (ListView) context.findViewById(R.id.listView);
    }

    public void setCallbackListener(WebRequestCallbackInterface listener) {
        this.webRequestCallbackInterface = listener;
    }

    /**
     * function to pull sensor list form web server
     */
    public void addSensor(final String uid, final String mac, final String kind) {
        // Tag used to cancel the request
        String tag_string_req = "req_add_sensor";
        progressDialog.showDialog(context.getString(R.string.progress_add_sensor_list));

        String url = String.format(AppConfig.URL_ADD_SENSOR_GET, uid, mac, kind);

        StringRequest strReq = new StringRequest(Request.Method.POST,
                AppConfig.URL_ADD_SENSOR_POST, new Response.Listener<String>() {

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
                        String errorMsg = jObj.getString("error_msg");
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
                //params.put("action", "dodajSenzorId");
                params.put("id", uid);
                params.put("string", mac);
                //params.put("br", kind);
                return params;
            }

        };

        //strReq.setRetryPolicy(new DefaultRetryPolicy(AppConfig.DEFAULT_TIMEOUT_MS, AppConfig.DEFAULT_MAX_RETRIES, AppConfig.DEFAULT_BACKOFF_MULT));
        //AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
        RequestQueue requestQueue = RequestQueueSingleton.getInstance(context.getApplicationContext()).getRequestQueue();
        strReq.setTag("AddSensorRequest");
        requestQueue.add(strReq);

    }
}
